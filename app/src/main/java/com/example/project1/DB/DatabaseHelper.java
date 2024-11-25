package com.example.project1.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "project1_group4.db";
    public static final String userTable = "user";
    public static final String customerTable = "customer";
    public static final String discountTable = "discount";
    public static final String productTable = "product";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 18);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bật kiểm tra khóa ngoại
        db.execSQL("PRAGMA foreign_keys=ON;");

        // Tạo bảng user
        createUserTable(db);
        // Tạo bảng customer
        createCustomerTable(db);
        // Tạo bảng discount
        createDiscountTable(db);
        // Tạo bảng sản phẩm
        createProductTable(db);

        // Chèn dữ liệu admin mẫu
        insertAdmin(db);
        // Chèn dữ liệu nhân viên mẫu
        insertEmployee(db);
        insertEmployee2(db);
        // Chèn dữ liệu khách hàng mẫu
        insertCustomer(db);
        insertCustomer2(db);
        // Chèn dữ liệu giảm giá mẫu
        insertDiscount(db);
        // Chèn dữ liệu sản phẩm mẫu
        insertProduct(db);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + userTable);
        db.execSQL("DROP TABLE IF EXISTS " + customerTable);
        db.execSQL("DROP TABLE IF EXISTS " + discountTable);

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
                "Security_Lock TEXT)");
    }

    private void createCustomerTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + customerTable + " (" +
                "ID_Customer INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "Phone_Number TEXT UNIQUE, " +
                "isVIP INTEGER)");
    }

    private void createDiscountTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + discountTable + " (" +
                "ID_Discount INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "Discount_Price REAL, " +
                "Min_Order_Price INTEGER, " + // Giá trị hóa đơn tối thiểu để áp dụng
                "Start_Date TEXT, " +      // Ngày bắt đầu
                "End_Date TEXT, " +        // Ngày kết thúc
                "Quantity INTEGER, " +     // Số lượng
                "isValid INTEGER)");       // Hạn sử dụng
    }

    private void createProductTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + productTable + " (" +
                "ID_Product INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Product_Name TEXT, " +
                "Discount_Price REAL, " +
                "Product_Type TEXT, " +
                "Product_Brand TEXT)");
    }

    private void insertProduct(SQLiteDatabase db) {
        ContentValues product = new ContentValues();
        product.put("Product_Name", "Váy Thu Đông");
        product.put("Discount_Price", 100000);
        product.put("Product_Type", "Váy");
        product.put("Product_Brand", "Arena");
        db.insert(productTable, null, product);

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

        db.insert(userTable, null, employeeUser2);
    }

    private void insertCustomer(SQLiteDatabase db) {
        ContentValues customer = new ContentValues();
        customer.put("Name", "Customer001");
        customer.put("Phone_Number", "0123456789");
        customer.put("isVIP", 1);

        db.insert(customerTable, null, customer);
    }

    private void insertCustomer2(SQLiteDatabase db) {
        ContentValues customer2 = new ContentValues();
        customer2.put("Name", "Customer002");
        customer2.put("Phone_Number", "019999999");
        customer2.put("isVIP", 1);

        db.insert(customerTable, null, customer2);
    }

    private void insertDiscount(SQLiteDatabase db) {
        ContentValues discount = new ContentValues();
        discount.put("Name", "Phiếu giảm giá 10%");
        discount.put("Discount_Price", 0.1);
        discount.put("Min_Order_Price", 1000000);
        discount.put("Start_Date", "2024-11-21");
        discount.put("End_Date", "2024-12-31");
        discount.put("Quantity", 10);
        discount.put("isValid", 1);

        db.insert(discountTable, null, discount);
    }

}