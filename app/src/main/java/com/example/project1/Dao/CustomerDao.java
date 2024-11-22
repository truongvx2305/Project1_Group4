package com.example.project1.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.project1.Model.CustomerModel;


import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    private final SQLiteDatabase db;

    public CustomerDao(SQLiteDatabase db) {
        this.db = db;
    }

    //Thêm khách hàng
    public boolean insert(CustomerModel customer){
        ContentValues values = new ContentValues();
        values.put("Name", customer.getName());
        values.put("Phone_Number", customer.getPhoneNumber());
        values.put("isVIP", customer.isVIP() ? 1 : 0);

        long result = db.insert("customer", null, values);
        if (result != -1) {
            customer.setId((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }

    // Xóa tài khoản
    public boolean delete(String customerName) {
        int result = db.delete("customer", "Name = ?", new String[]{customerName});
        return result > 0;
    }

    // Cập nhật thông tin người dùng
    public boolean updateCusProfile(CustomerModel customer) {
        // Kiểm tra dữ liệu đầu vào
        if (customer == null || customer.getId() <= 0) {
            Log.e("Update", "Dữ liệu khách hàng không hợp lệ.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Name", customer.getName());
        values.put("Phone_Number", customer.getPhoneNumber());
        values.put("isVIP", customer.isVIP() ? 1 : 0);

        // Thực hiện cập nhật
        int result = db.update("customer", values, "ID_Customer = ?", new String[]{String.valueOf(customer.getId())});

        // Kiểm tra kết quả
        if (result > 0) {
            Log.d("Update", "Cập nhật thông tin khách hàng thành công.");
            return true; // Cập nhật thành công
        } else {
            Log.d("Update", "Không có bản ghi nào được cập nhật.");
            return false; // Không có dòng nào được cập nhật
        }
    }

    // Lấy danh sách khách hàng
    public List<CustomerModel> getCustomerList() {
        List<CustomerModel> customerList = new ArrayList<>();

        // Câu truy vấn
        String query = "SELECT * FROM customer";

        // Thực hiện truy vấn
        Cursor cursor = db.rawQuery(query, null);

        // Duyệt qua từng bản ghi trong kết quả
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Tạo đối tượng CustomerModel từ dữ liệu trong cursor
                CustomerModel customer = new CustomerModel();
                customer.setId(cursor.getInt(cursor.getColumnIndexOrThrow("ID_Customer")));
                customer.setName(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
                customer.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number")));
                customer.setVIP(cursor.getInt(cursor.getColumnIndexOrThrow("isVIP")) == 1);

                // Thêm vào danh sách
                customerList.add(customer);
            } while (cursor.moveToNext());
        }

        // Đóng cursor
        if (cursor != null) {
            cursor.close();
        }

        return customerList;
    }


}
