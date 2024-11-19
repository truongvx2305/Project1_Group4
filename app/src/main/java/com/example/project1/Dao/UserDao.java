package com.example.project1.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project1.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final SQLiteDatabase db;

    public UserDao(SQLiteDatabase db) {
        this.db = db;
    }

    // 1. Thêm, xóa và cập nhật thông tin người dùng

    // Thêm tài khoản
    public boolean insert(UserModel user) {
        ContentValues values = new ContentValues();
        values.put("Username", user.getUsername());
        values.put("Password", user.getPassword());
        values.put("Image", user.getImage());
        values.put("Name", user.getName());
        values.put("Email", user.getEmail());
        values.put("Phone_Number", user.getPhoneNumber());
        values.put("isAdmin", user.isAdmin() ? 1 : 0);
        values.put("isActive", user.isActive() ? 1 : 0);
        values.put("Security_Lock", user.getSecurityLock()); // Thêm Security Lock

        long result = db.insert("user", null, values);
        if (result != -1) {
            user.setId((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }


    // Cập nhật mật khẩu
    public boolean updatePassword(UserModel user, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("Password", newPassword);
        int result = db.update("user", values, "Username = ?", new String[]{user.getUsername()});
        return result > 0;
    }

    // Cập nhật trạng thái của nhân viên (isActive)
    public boolean updateEmployeeStatus(int userId, boolean isActive) {
        ContentValues values = new ContentValues();
        values.put("isActive", isActive ? 1 : 0);
        int result = db.update("user", values, "ID_User = ?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    public boolean updateSecurityLock(String username, String newLock) {
        ContentValues values = new ContentValues();
        values.put("Security_Lock", newLock);
        int result = db.update("user", values, "Username = ?", new String[]{username});
        return result > 0;
    }

    // Cập nhật thông tin người dùng
    public void updateUserProfile(UserModel user) {
        ContentValues values = new ContentValues();
        if (user.getImage() != null) {
            values.put("Image", user.getImage());
        }
        values.put("Name", user.getName());
        values.put("Email", user.getEmail());
        values.put("Phone_Number", user.getPhoneNumber());
        values.put("isAdmin", user.isAdmin() ? 1 : 0);
        values.put("isActive", user.isActive() ? 1 : 0);

        int result = db.update("user", values, "ID_User = ?", new String[]{String.valueOf(user.getId())});
    }

    // Xóa tài khoản
    public boolean delete(String username) {
        int result = db.delete("user", "Username = ?", new String[]{username});
        return result > 0;
    }

    // 2. Các phương thức kiểm tra dữ liệu

    // Kiểm tra Username
    public boolean checkUsername(String username) {
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE Username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Kiểm tra Password
    public boolean checkPassword(String username, String password) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM user WHERE Username = ? AND Password = ?", new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Kiểm tra Role
    public boolean checkRole(UserModel user) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM user WHERE Username = ? AND isAdmin = ?", new String[]{user.getUsername(), user.isAdmin() ? "1" : "0"});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean checkSecurityLock(String username, String securityLock) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM user WHERE Username = ? AND Security_Lock = ?",
                new String[]{username, securityLock});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Kiểm tra email đã tồn tại
    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        cursor.close();
        return false;
    }

    // Kiểm tra số điện thoại đã tồn tại
    public boolean isPhoneNumberExists(String phoneNumber) {
        String query = "SELECT COUNT(*) FROM user WHERE phone_number = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phoneNumber});
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        cursor.close();
        return false;
    }

    // 3. Các phương thức lấy thông tin người dùng

    // Lấy thông tin người dùng từ database
    public UserModel getProfileByUsername(String username) {
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE Username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_User"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("Image"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
            boolean isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("isAdmin")) == 1;
            boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive")) == 1;
            String securityLock = cursor.getString(cursor.getColumnIndexOrThrow("Security_Lock"));

            cursor.close();
            return new UserModel(id, username, password, image, name, email, phoneNumber, isAdmin, isActive, securityLock);
        }
        cursor.close();
        return null;
    }

    // Lấy tất cả nhân viên (không phải Admin)
    public List<UserModel> getAllEmployees() {
        List<UserModel> employees = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE isAdmin = 0", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_User"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("Image"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
                boolean isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("isAdmin")) == 1;
                boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive")) == 1;
                String securityLock = cursor.getString(cursor.getColumnIndexOrThrow("Security_Lock"));

                UserModel employee = new UserModel(id, username, password, image, name, email, phoneNumber, isAdmin, isActive, securityLock);
                employees.add(employee);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return employees;
    }

    // Lấy ảnh profile của người dùng từ Username
    public byte[] getProfileImage(String username) {
        Cursor cursor = db.rawQuery("SELECT Image FROM user WHERE Username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("Image"));
            cursor.close();
            return image;
        }
        cursor.close();
        return null;
    }

    // Lấy email theo tên người dùng
    public String getEmail(String username) {
        Cursor cursor = db.rawQuery("SELECT Email FROM user WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
            cursor.close();
            return email;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public String getSecurityLock(String username) {
        Cursor cursor = db.rawQuery("SELECT Security_Lock FROM user WHERE Username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            String lock = cursor.getString(cursor.getColumnIndexOrThrow("Security_Lock"));
            cursor.close();
            return lock;
        }
        cursor.close();
        return null;
    }
}
