package com.example.hcd;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GoodActivity extends AppCompatActivity {

    ImageView load;
    TextView pose_name;
    TextView pose_info;
    Long pose_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good);

        load = (ImageView)findViewById(R.id.image_good);
        pose_name = (TextView)findViewById(R.id.text_PoseName);
        pose_info = (TextView)findViewById(R.id.text_PoseInfo);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("rasp_camera_image");

        ///////////////////////////////////////////// 포즈 상태 확인
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("AFPC").document("rasp");

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists())
                {
                    pose_state = documentSnapshot.getLong("state");

                    if (pose_state == 1)
                    {
                        pose_name.setText("Goddess");
                        pose_info.setText("완벽한 자세입니다.");
                    }
                    else if (pose_state == 2)
                    {
                        pose_name.setText("Tree");
                        pose_info.setText("완벽한 자세입니다.");
                    }
                    else if (pose_state == 3)
                    {
                        pose_name.setText("Warrior");
                        pose_info.setText("완벽한 자세입니다.");
                    }
                    else if (pose_state == 4)
                    {
                        pose_name.setText("Downdog");
                        pose_info.setText("완벽한 자세입니다.");
                    }
                    else
                    {
                        Log.w(TAG, "Pose name: "+ pose_name);
                    }
                }
            }
        });

        ///////////////////////////////////////////// 포즈 사진 불러오기
        if (pathReference == null)
        {
            Toast.makeText(GoodActivity.this, "저장소에 사진이 없습니다." ,Toast.LENGTH_SHORT).show();
        }
        else
        {
            StorageReference submitProfile = storageReference.child("rasp_camera_image/pose_take.jpg");

            submitProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(GoodActivity.this).load(uri).into(load);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    // 저장소의 사진 삭제하는 함수
    public void Del()
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference desertRef = storageRef.child("rasp_camera_image/pose_take.jpg");

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "< 사진 삭제 성공 >");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Log.d(TAG, "< 사진 삭제 실패 >", exception);
            }
        });
    }

    ///////////////////////////////// 하단 내비게이션 바
    public void Home(View target)
    {
        // 사진 삭제
        Del();
        Toast.makeText(GoodActivity.this, "사진이 삭제되었습니다." ,Toast.LENGTH_SHORT).show();

        // 홈 화면으로 이동
        Intent intent = new Intent(getApplication(), PoseActivity.class);
        startActivity(intent);
    }

    public void User(View target)
    {
        // 사진 삭제
        Del();
        Toast.makeText(GoodActivity.this, "사진이 삭제되었습니다." ,Toast.LENGTH_SHORT).show();

        // 마이페이지로 이동
        Intent intent = new Intent(getApplication(), UserActivity.class);
        startActivity(intent);
    }
}