package com.example.lab6_23120501037;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity_23120501037 extends AppCompatActivity {
    private EditText etUsername, etPwd;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_23120501037);
        etUsername = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_pwd);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            if ("user".equals(name) && "123456".equals(pwd)) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity_23120501037.class));
                finish();
            } else {
                Toast.makeText(this, "账号密码错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}