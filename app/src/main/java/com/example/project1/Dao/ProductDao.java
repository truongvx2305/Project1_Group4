package com.example.project1.Dao;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import com.example.project1.Model.ProductModel;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private final SQLiteDatabase db;
    private static final String productTable = "product";

    public ProductDao(SQLiteDatabase db) {
        this.db = db;
    }

    // Tạo bảng sản phẩm
    public void createProductTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + productTable + " (" +
                "ID_Product INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Product_Name TEXT, " +
                "Discount_Price REAL, " +
                "Product_Type TEXT, " +
                "Product_Brand TEXT)");
    }

    // Thêm sản phẩm
    public boolean insert(ProductModel product) {
        ContentValues values = new ContentValues();
        values.put("Product_Name", product.getName());
        values.put("Discount_Price", product.getDiscountPrice());
        values.put("Product_Type", product.getProductType());
        values.put("Product_Brand", product.getProductBrand());

        long result = db.insert(productTable, null, values);
        return result != -1;
    }

    // Cập nhật sản phẩm
    public boolean updateProduct(ProductModel product) {
        ContentValues values = new ContentValues();
        values.put("Product_Name", product.getName());
        values.put("Discount_Price", product.getDiscountPrice());
        values.put("Product_Type", product.getProductType());
        values.put("Product_Brand", product.getProductBrand());

        int result = db.update(productTable, values, "ID_Product = ?", new String[]{String.valueOf(product.getId())});
        return result > 0;
    }

    // Xóa sản phẩm
    public boolean delete(int productId) {
        int result = db.delete(productTable, "ID_Product = ?", new String[]{String.valueOf(productId)});
        return result > 0;
    }

    // Lấy danh sách sản phẩm
    public List<ProductModel> getProductList() {
        List<ProductModel> productList = new ArrayList<>();
        String query = "SELECT * FROM " + productTable;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ProductModel product = new ProductModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow("ID_Product")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Product_Name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("Discount_Price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Product_Type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Product_Brand"))
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return productList;
    }
}

