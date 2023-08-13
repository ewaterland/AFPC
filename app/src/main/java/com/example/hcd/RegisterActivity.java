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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public String id;

    // 입력 있으면 1, 입력 없으면 0
    int email_active = 0;
    int pw_active = 0;
    int pwc_active = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();

        EditText editText_email = (EditText) findViewById(R.id.editText_EmailAddress);
        EditText editText_password = (EditText) findViewById(R.id.editText_Password);
        EditText editText_password_2 = (EditText) findViewById(R.id.editText_Password_2);

        editText_email.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { email_active = 1; check(); }
                else              { email_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_password.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { pw_active = 1; check(); }
                else              { pw_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editText_password_2.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { pwc_active = 1; check(); }
                else              { pwc_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // 칸이 모두 채워졌는지 확인
    public void check()
    {
        Button btn_register = (Button) findViewById(R.id.btn_signup);

        if (email_active == 1 && pw_active == 1 && pwc_active == 1)
        {
            btn_register.setEnabled(true);
        }
        else
        {
            btn_register.setEnabled(false);
        }
    }

    // Sign in 버튼 누를 경우 로그인 화면으로 이동
    public void Login(View target)
    {
        Intent intent = new Intent(getApplication(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Sign up 버튼 누를 경우 회원가입 시도
    public void Register(View target)
    {
        // 회원 가입을 위한 정보 받기
        String email=((EditText)findViewById(R.id.editText_EmailAddress)).getText().toString();
        String password=((EditText)findViewById(R.id.editText_Password)).getText().toString();
        String passwordCheck=((EditText)findViewById(R.id.editText_Password_2)).getText().toString();

        id = email;

        // 회원 가입을 위한 정보 검토
        if(email.length()>0 && password.length()>0 && passwordCheck.length()>0) // id, password 가 0 초과
        {
            if(password.equals(passwordCheck)) // password 확인이 일치
            {
                if (password.length() >= 6)  // password 길이가 6자리 이상
                {
                    // 회원 가입 요청
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, task -> {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(RegisterActivity.this, "회원가입 성공" ,Toast.LENGTH_SHORT).show();

                                    Map<String, Object> user = new HashMap<>();

                                    user.put("name", "");
                                    user.put("age", "");
                                    user.put("gender", "");
                                    user.put("height", "");
                                    user.put("weight", "");

                                    db.collection("AFPC").document(id).set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>()
                                            {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "< 데이터베이스에 유저 정보 저장 성공 >");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "< 데이터베이스에 유저 정보 저장 실패 >");
                                                }
                                            });

                                    // 회원가입 성공 후 로그인 화면으로 이동
                                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else // password 6자리 미만
                {
                    Toast.makeText(RegisterActivity.this, "비밀번호를 6자리 이상 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            else // password와 password check가 일치하지 않음
            {
                Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다." ,Toast.LENGTH_SHORT).show();
            }
        }
        else // email과 password, password check 셋 중 하나가 입력되지 않음
        {
            Toast.makeText(RegisterActivity.this, "이메일과 비밀번호를 확인해주세요." ,Toast.LENGTH_SHORT).show();
        }



    }
}