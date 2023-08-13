package com.example.hcd;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    // 입력 있으면 1, 입력 없으면 0
    int name_active = 0;
    int age_active = 0;
    int height_active = 0;
    int weight_active = 0;

    // 정보 입력 받을 공간
    EditText editText_Name;
    EditText editText_Age;
    RadioButton rd_Gender_f;
    RadioButton rd_Gender_m;
    EditText editText_Height;
    EditText editText_Weight;

    // 입력 받은 정보를 저장할 공간
    public String name;
    public String age;
    public String gender;
    public String height;
    public String weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        editText_Name = findViewById(R.id.editText_Name);
        editText_Age = findViewById(R.id.editText_Age);
        rd_Gender_f = findViewById(R.id.rd_gender_f);
        rd_Gender_m = findViewById(R.id.rd_gender_m);
        editText_Height = findViewById(R.id.editText_Height);
        editText_Weight = findViewById(R.id.editText_Weight);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(UserActivity.this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView text_email = findViewById(R.id.text_email);
        text_email.setText(id);

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if (documentSnapshot.exists())
                {
                    editText_Name.setText(documentSnapshot.getString("name"));
                    editText_Age.setText(documentSnapshot.getString("age"));
                    gender = documentSnapshot.getString("gender");
                    editText_Height.setText(documentSnapshot.getString("height"));
                    editText_Weight.setText(documentSnapshot.getString("weight"));
                }
            }
        });

        editText_Name.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { name_active = 1; check(); }
                else              { name_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_Age.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { age_active = 1; check(); }
                else              { age_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_Height.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { height_active = 1; check(); }
                else              { height_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_Weight.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { weight_active = 1; check(); }
                else              { weight_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

    // 칸이 모두 채워졌는지 확인
    public void check()
    {
        Button btn_save = (Button) findViewById(R.id.btn_save);

        if (name_active == 1 && age_active == 1 && height_active == 1 && weight_active == 1)
        {
            btn_save.setEnabled(true);
        }
        else
        {
            btn_save.setEnabled(false);
        }
    }

    // 현재 로그인 된 유저 정보 읽기
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String id = currentUser.getEmail();
    DocumentReference docRef = db.collection("AFPC").document(id);

    public void Save(View target)
    {
        // 입력한 정보 받기
        name = ((EditText) findViewById(R.id.editText_Name)).getText().toString();
        age = ((EditText) findViewById(R.id.editText_Age)).getText().toString();
        height = ((EditText) findViewById(R.id.editText_Height)).getText().toString();
        weight = ((EditText) findViewById(R.id.editText_Weight)).getText().toString();

        if (rd_Gender_f.isChecked())
        {
            gender = "Female";
        }
        else if (rd_Gender_m.isChecked())
        {
            gender = "Male";
        }

        // 입력한 정보 업데이트
        docRef.update("name", name);
        docRef.update("age", age);
        docRef.update("gender", gender);
        docRef.update("height", height);
        docRef.update("weight", weight);

        db.collection("AFPC")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists())
                            {
                                Toast.makeText(UserActivity.this, "저장되었습니다.",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                Intent intent = new Intent(getApplication(), PoseActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(UserActivity.this, "실패하였습니다.",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "No such document");
                            }
                        }
                        else
                        {
                            Toast.makeText(UserActivity.this, "실패하였습니다.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
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

    // Logout 버튼 누를 경우
    public void Logout(View target)
    {
        // 유저 로그아웃
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(UserActivity.this, "로그아웃 되었습니다.",
                Toast.LENGTH_SHORT).show();

        // Login 화면으로 이동
        Intent intent = new Intent(getApplication(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 회원 탈퇴 버튼 누를 경우
    public void withdraw(View target)
    {
        // 유저 삭제
        String email = currentUser.getEmail();
        currentUser.delete();

        // 유저 정보 삭제
        db.collection("AFPC").document(email)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(UserActivity.this, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();

                        // 로그인 화면으로 이동
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        startActivity(intent);
                        finish();

                        Log.d(TAG, "< 유저 정보 삭제 성공 >");
                        Log.d(TAG, "< 회원 탙퇴 > Email: "+ email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "< 유저 정보 삭제 실패 >", e);
                    }
                });
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