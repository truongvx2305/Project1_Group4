package com.example.project1.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "project1_group4.db";
    public static final String userTable = "user";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");

        // Tạo bảng user
        createUserTable(db);

        // Chèn dữ liệu admin mẫu
        insertAdmin(db);
        // Chèn dữ liệu nhân viên mẫu
        insertEmployee(db);
        insertEmployee2(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + userTable);

        // Tạo lại bảng
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    private void createUserTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + userTable + " (" +
                "ID_User INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Username TEXT UNIQUE, " +
                "Password TEXT, " +
                "Image BLOB, " +
                "Name TEXT, " +
                "Email TEXT UNIQUE, " +
                "Phone_Number TEXT UNIQUE, " +
                "isAdmin INTEGER, " +
                "isActive INTEGER, " +
                "Security_Lock TEXT)"); // Thêm Security Lock
    }

    private void insertAdmin(SQLiteDatabase db) {
        ContentValues adminUser = new ContentValues();
        adminUser.put("Username", "Admin");
        adminUser.put("Password", "12345678");
        adminUser.put("Name", "TCDQ");
        adminUser.put("Email", "TCDQ@gmail.com");
        adminUser.put("Phone_Number", "0123456789");
        adminUser.put("isAdmin", 1);
        adminUser.put("isActive", 1);
        adminUser.put("Security_Lock", "12345"); // Thêm Security Lock mặc định

        db.insert(userTable, null, adminUser);
    }

    private void insertEmployee(SQLiteDatabase db) {
        ContentValues employeeUser = new ContentValues();
        employeeUser.put("Username", "Employee001");
        employeeUser.put("Password", "12345678");
        employeeUser.put("Name", "Employee001");
        employeeUser.put("Email", "Employee001@gmail.com");
        employeeUser.put("Phone_Number", "0987654321");
        employeeUser.put("isAdmin", 0); // 0 cho false
        employeeUser.put("isActive", 1); // 1 cho true
        employeeUser.put("Security_Lock", "12345");

        db.insert(userTable, null, employeeUser);
    }

    private void insertEmployee2(SQLiteDatabase db) {
        ContentValues employeeUser2 = new ContentValues();
        employeeUser2.put("Username", "Employee002");
        employeeUser2.put("Password", "12345678");
        employeeUser2.put("Name", "Employee002");
        employeeUser2.put("Email", "Employee002@gmail.com");
        employeeUser2.put("Phone_Number", "0896745231");
        employeeUser2.put("isAdmin", 0); // 0 cho false
        employeeUser2.put("isActive", 1); // 1 cho true
        employeeUser2.put("Security_Lock", "12345");

        db.insert(userTable, null, employeeUser2);
    }
}
