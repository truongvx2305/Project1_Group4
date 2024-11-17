package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.Function.Login;

public class Started extends AppCompatActivity {

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.started);

        // Khởi tạo SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            // Nếu đã đăng nhập, chuyển đến màn hình Navigation
            Intent intent = new Intent(Started.this, Navigation.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Nếu chưa đăng nhập, ở lại màn hình Started và thiết lập nút chuyển đến màn hình Login
        Button btnGoToLogin = findViewById(R.id.goToLogin);
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Started.this, Login.class);
            startActivity(intent);
            finish();
        });
    }
}
