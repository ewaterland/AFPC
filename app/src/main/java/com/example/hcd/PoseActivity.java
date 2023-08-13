package com.example.hcd;
import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

public class PoseActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef = db.collection("AFPC").document("rasp");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    StorageReference pathReference = storageReference.child("rasp_camera_image");

    private Button btn_pose1;
    private Button btn_pose2;
    private Button btn_pose3;
    private Button btn_pose4;

    public Long signal = 0L;  // 사용자가 선택한 자세
    private Long state = 0L;  // 라즈베리파이에서 인식한 자세

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(PoseActivity.this);
        mAuth = FirebaseAuth.getInstance();

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists())
                {
                    state = documentSnapshot.getLong("state");
                    Log.w(TAG, "pose_state 데이터 가져오기 성공");
                }
                else
                {
                    Log.w(TAG, "pose_state 데이터 가져오기 실패");
                }
            }
        });

        btn_pose1 = (Button) findViewById(R.id.btn_pose1);
        btn_pose2 = (Button) findViewById(R.id.btn_pose2);
        btn_pose3 = (Button) findViewById(R.id.btn_pose3);
        btn_pose4 = (Button) findViewById(R.id.btn_pose4);

        // 사진 url 다운 통해 저장소에 사진이 있는지 확인
        StorageReference take_pic = storageReference.child("rasp_camera_image/pose_take.jpg");
        take_pic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                btn_pose1.setEnabled(true);
                btn_pose2.setEnabled(true);
                btn_pose3.setEnabled(true);
                btn_pose4.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener(){
            @Override
            public void onFailure(@NonNull Exception e)
            {
                btn_pose1.setEnabled(false);
                btn_pose2.setEnabled(false);
                btn_pose3.setEnabled(false);
                btn_pose4.setEnabled(false);

                Toast.makeText(PoseActivity.this, "저장소에 사진이 없습니다.", Toast.LENGTH_LONG).show();
            }
        });
    } // onCreate

    /////////////////////////////////////////////

    // 라즈베리파이에서 전달받은 State를 통해 포즈 확인
    public void Signal_check()
    {
        if(state == signal)  // 인식한 자세와 사용자가 선택한 자세가 일치하는 경우
        {
            // Good
            Intent intent = new Intent(getApplication(), GoodActivity.class);
            startActivity(intent);
        }
        else if (state != signal)  // 인식한 자세와 사용자가 선택한 자세가 일치하지 않는 경우
        {
            // 사용자가 선택한 자세 저장
            String input_signal = signal.toString();

            // Bad
            Intent intent = new Intent(PoseActivity.this, BadActivity.class);
            intent.putExtra("signal", input_signal);
            startActivity(intent);
        }
    }

    // 포즈 5개의 버튼 동작
    public void Pose1(View target)
    {
        // 포즈 상태 업데이트
        signal = 1L;
        Signal_check();
    }

    public void Pose2(View target)
    {
        // 포즈 상태 업데이트
        signal = 2L;
        Signal_check();
    }

    public void Pose3(View target)
    {
        // 포즈 상태 업데이트
        signal = 3L;
        Signal_check();
    }

    public void Pose4(View target)
    {
        // 포즈 상태 업데이트
        signal = 4L;
        Signal_check();
    }

    ///////////////////////////////// 하단 내비게이션 바
    public void Home(View target)
    {
        Intent intent = new Intent(getApplication(), PoseActivity.class);
        startActivity(intent);
    }
    public void User(View target)
    {
        Intent intent = new Intent(getApplication(), UserActivity.class);
        startActivity(intent);
    }
}