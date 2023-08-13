package com.example.hcd;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BadActivity extends AppCompatActivity {

    ImageView load1, load2;
    TextView pose_name;
    TextView pose_info;
    Long state;
    String pose_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bad);

        load1 = (ImageView)findViewById(R.id.image_bad);
        load2 = (ImageView)findViewById(R.id.image_good);
        pose_name = (TextView)findViewById(R.id.text_PoseName);
        pose_info = (TextView)findViewById(R.id.text_PoseInfo);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("rasp_camera_image");

        ///////////////////////////////////////////// 촬영한 사진이 있을 경우 불러오기
        if (pathReference == null)
        {
            Toast.makeText(BadActivity.this, "저장소에 사진이 없습니다." ,Toast.LENGTH_SHORT).show();
        }
        else
        {
            StorageReference submitProfile1 = storageReference.child("rasp_camera_image/pose_take.jpg");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("AFPC").document("rasp");

            submitProfile1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
            {
                @Override
                public void onSuccess(Uri uri)
                {
                    Glide.with(BadActivity.this).load(uri).into(load1);

                    // 사용자가 선택한 자세 데이터 가져오기
                    Intent intent = getIntent();
                    String signal = intent.getStringExtra("signal");

                    // DocumentSnapshot 객체 생성, 데이터 가져오기
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot)
                        {
                            if (documentSnapshot.exists())
                            {
                                state = documentSnapshot.getLong("state");
                                pose_state = state.toString();

                                if (signal.equals("1"))
                                {
                                    pose_name.setText("Goddess");
                                    pose_info.setText("넓은 발로 서서 무릎을 구부리고 엉덩이를 낮춥니다. 기도 자세를 유지하거나 팔을 뻗습니다.");
                                    StorageReference submitProfile2 = storageReference.child("pose/pose1.jpg");
                                    submitProfile2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(BadActivity.this).load(uri).into(load2);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                }
                                else if (signal.equals("2"))
                                {
                                    pose_name.setText("Tree");
                                    pose_info.setText("발을 모으고 서서 한 발을 반대쪽 다리의 허벅지 안쪽에 놓고 균형을 맞춘 후 손을 심장 위치에서 기도 자세로 유지합니다.");
                                    StorageReference submitProfile2 = storageReference.child("pose/pose2.jpg");
                                    submitProfile2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(BadActivity.this).load(uri).into(load2);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                }
                                else if (signal.equals("3"))
                                {
                                    pose_name.setText("Warrior");
                                    pose_info.setText("한 발을 뒤로 내딛고 앞 무릎을 구부립니다. 뒷 다리를 곧게 펴고 팔을 지면과 평행하게 들어올리며 정면을 바라봅니다.");
                                    StorageReference submitProfile2 = storageReference.child("pose/pose3.jpg");
                                    submitProfile2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(BadActivity.this).load(uri).into(load2);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                }
                                else if (signal.equals("4"))
                                {
                                    pose_name.setText("Downdog");
                                    pose_info.setText("손과 무릎을 바닥에 댄 상태에서 엉덩이를 들어올립니다. 다리를 곧게 펴고 손을 힘껏 밀며 산 모양을 유지합니다.");
                                    StorageReference submitProfile2 = storageReference.child("pose/pose4.jpg");
                                    submitProfile2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(BadActivity.this).load(uri).into(load2);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                }
                                else
                                {
                                    Log.w(TAG, "Pose name: "+ pose_name);
                                }
                            }
                        }
                    });
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
        Toast.makeText(BadActivity.this, "사진이 삭제되었습니다." ,Toast.LENGTH_SHORT).show();

        // 홈 화면으로 이동
        Intent intent = new Intent(getApplication(), PoseActivity.class);
        startActivity(intent);
    }

    public void User(View target)
    {
        // 사진 삭제
        Del();
        Toast.makeText(BadActivity.this, "사진이 삭제되었습니다." ,Toast.LENGTH_SHORT).show();

        // 마이페이지로 이동
        Intent intent = new Intent(getApplication(), UserActivity.class);
        startActivity(intent);
    }
}