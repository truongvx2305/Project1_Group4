package com.example.project1.Function;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.UserDao;
import com.example.project1.R;

public class SecurityCode extends Fragment {
    private String username;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private UserDao userDao;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.security_code, container, false);

        // Khởi tạo các thành phần giao diện
        EditText edtSecurityCode1 = view.findViewById(R.id.edt_securityCode1);
        EditText edtSecurityCode2 = view.findViewById(R.id.edt_SecurityCode2);
        Button btnUpdateSecurity = view.findViewById(R.id.btn_updateSecurity);
        Button btnAddSecurity = view.findViewById(R.id.btn_addSecurity);

        // Khởi tạo UserDao
        dbHelper = new DatabaseHelper(requireContext());
        db = dbHelper.getWritableDatabase();
        userDao = new UserDao(db);

        // Xử lý sự kiện cho nút "Cập nhật mã bảo mật"
        btnUpdateSecurity.setOnClickListener(v -> {
            String securityCode1 = edtSecurityCode1.getText().toString().trim();
            String securityCode2 = edtSecurityCode2.getText().toString().trim();

            // Kiểm tra thông tin nhập
            if (TextUtils.isEmpty(securityCode1)) {
                Toast.makeText(getContext(), "Vui lòng nhập mã bảo mật hiện tại!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(securityCode2)) {
                Toast.makeText(getContext(), "Vui lòng nhập mã bảo mật mới!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mã bảo mật cũ
            if (!userDao.checkSecurityLock(username, securityCode1)) {
                Toast.makeText(getContext(), "Mã bảo mật hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật mã bảo mật mới
            if (userDao.updateSecurityLock(username, securityCode2)) {
                Toast.makeText(getContext(), "Cập nhật mã bảo mật thành công!", Toast.LENGTH_SHORT).show();
                edtSecurityCode1.setText("");
                edtSecurityCode2.setText("");
            } else {
                Toast.makeText(getContext(), "Cập nhật mã bảo mật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện cho nút "Thêm mã bảo mật"
        btnAddSecurity.setOnClickListener(v -> {
            String existingLock = userDao.getSecurityLock(username);

            String securityCode1 = edtSecurityCode1.getText().toString().trim();
            String securityCode2 = edtSecurityCode2.getText().toString().trim();

            // Kiểm tra thông tin nhập
            if (TextUtils.isEmpty(securityCode1) || TextUtils.isEmpty(securityCode2)) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem mã bảo mật đã tồn tại chưa
            if (!TextUtils.isEmpty(existingLock)) {
                Toast.makeText(getContext(), "Bạn đã có mã bảo mật!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!securityCode1.equals(securityCode2)) {
                Toast.makeText(getContext(), "Mã bảo mật mới không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm mã bảo mật mới
            if (userDao.updateSecurityLock(username, securityCode1)) {
                Toast.makeText(getContext(), "Thêm mã bảo mật thành công!", Toast.LENGTH_SHORT).show();
                edtSecurityCode1.setText("");
                edtSecurityCode2.setText("");
            } else {
                Toast.makeText(getContext(), "Thêm mã bảo mật thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
