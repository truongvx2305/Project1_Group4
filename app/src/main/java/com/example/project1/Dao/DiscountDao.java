package com.example.project1.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.project1.Model.DiscountModel;
import com.example.project1.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class DiscountDao {
    private final SQLiteDatabase db;

    public DiscountDao(SQLiteDatabase db) {
        this.db = db;
    }

    // 1. Thêm và cập nhật phiếu giảm giá

    // Thêm
    public boolean insert(DiscountModel discount) {
        ContentValues values = new ContentValues();
        values.put("Name", discount.getName());
        values.put("Discount_Price", discount.getDiscountPrice());
        values.put("Min_Order_Price", discount.getMinOrderPrice());
        values.put("Start_Date", discount.getStartDate());
        values.put("End_Date", discount.getEndDate());

        long result = db.insert("discount", null, values);
        if (result != -1) {
            discount.setId((int) result); // Gán ID vừa được sinh ra
            return true;
        }
        return false;
    }

    // Cập nhật
    public void updateDiscount(DiscountModel discount) {
        ContentValues values = new ContentValues();
        values.put("Name", discount.getName());
        values.put("Discount_Price", discount.getDiscountPrice());
        values.put("Min_Order_Price", discount.getMinOrderPrice());
        values.put("Start_Date", discount.getStartDate());
        values.put("End_Date", discount.getEndDate());

        int result = db.update("discount", values, "ID_Discount = ?", new String[]{String.valueOf(discount.getId())});
    }

    // Xóa
    public boolean delete(String name) {
        int result = db.delete("discount", "Name = ?", new String[]{name});
        return result > 0;
    }

    // 2. Các phương thức kiểm tra dữ liệu

    // 3. Các phương thức lấy phiếu giảm giá

    // Lấy danh sách
    public List<DiscountModel> getAlLDiscount() {
        List<DiscountModel> discounts = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM discount", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_Discount"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                float discountPrice = cursor.getFloat(cursor.getColumnIndexOrThrow("Discount_Price"));
                double minOrderPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("Min_Order_Price"));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow("Start_Date"));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow("End_Date"));
                boolean isValid = cursor.getInt(cursor.getColumnIndexOrThrow("isValid")) == 1;

                DiscountModel discountModel = new DiscountModel(id, name, discountPrice, minOrderPrice, startDate, endDate, isValid);
                discounts.add(discountModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return discounts;
    }
}
