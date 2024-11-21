package com.example.project1.Function;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.UserDao;
import com.example.project1.Model.UserModel;
import com.example.project1.Navigation;
import com.example.project1.R;
import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {
    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin;
    // private CheckBox remember;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Ánh xạ
        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        // remember = findViewById(R.id.remember);

        // Khởi tạo Database và UserDao
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);

        // Sự kiện khi nhấn nút đăng nhập
        btnLogin.setOnClickListener(v -> loginClick());
    }

    // Hàm xử lý khi nhấn nút đăng nhập
    // Hàm xử lý khi nhấn nút đăng nhập
    private void loginClick() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username.trim())) {
            edtUsername.setError("Vui lòng nhập tên tài khoản!");
            return;
        }
        if (TextUtils.isEmpty(password.trim())) {
            edtPassword.setError("Vui lòng nhập mật khẩu!");
            return;
        }

        // Kiểm tra mật khẩu
        if (userDao.checkPassword(username, password)) {
            UserModel user = userDao.getProfileByUsername(username);

            if (user == null) {
                Toast.makeText(this, "Người dùng không tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!user.isActive()) {
                Toast.makeText(this, "Tài khoản đã hết hạn hợp đồng, không thể đăng nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Xin chào " + username + "!", Toast.LENGTH_SHORT).show();

            // Lưu thông tin vào SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true); // Luôn lưu trạng thái đăng nhập
            editor.putString(KEY_USERNAME, username); // Lưu tên người dùng cho các chức năng khác

            editor.apply();

            // Chuyển đến Navigation
            Intent intent = new Intent(Login.this, Navigation.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
        if (db != null && db.isOpen()) db.close();
    }
}
