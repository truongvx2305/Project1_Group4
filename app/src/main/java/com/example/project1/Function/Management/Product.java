package com.example.project1.Function.Management;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.example.project1.Adapter.ProductAdapter;
import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.ProductDao;
import com.example.project1.Model.ProductModel;
import com.example.project1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Product extends Fragment {
    private List<ProductModel> productList = new ArrayList<>();
    private ProductAdapter adapter;
    private ListView productView;
    private ImageView filterProduct;
    private EditText searchProduct;
    private Integer currentFilterStatus = null;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product, container, false);

        initializeUI(view);

        loadData();

        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        productView = view.findViewById(R.id.listProduct);
        filterProduct = view.findViewById(R.id.filterProduct);
        searchProduct = view.findViewById(R.id.searchProduct);
        FloatingActionButton btnAddProduct = view.findViewById(R.id.btn_addProduct);

        btnAddProduct.setOnClickListener(v -> showDialogAddProduct());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ProductDao productDao = new ProductDao(db);

        productList.clear();
        productList.addAll(productDao.getProductList());

        adapter = new ProductAdapter(getContext(), productList);
        productView.setAdapter(adapter);
    }

    private void setupListeners() {
        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProductAndStatus(s.toString(), currentFilterStatus);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không xử lý
            }
        });

//        filterProduct.setOnClickListener(this::filterClick);

        productView.setOnItemClickListener((parent, view, position, id) -> {
            ProductModel selectedProduct = productList.get(position);
            showUpdateProductDialog(selectedProduct);
        });
    }

    private void filterProductAndStatus(String query, Integer statusFilter) {
        List<ProductModel> filteredList = new ArrayList<>();

        for (ProductModel product : productList) {
            boolean matchesSearch = TextUtils.isEmpty(query)
                    || String.valueOf(product.getId()).contains(query)
                    || product.getName().toLowerCase().contains(query.toLowerCase())
                    || product.getProductType().toLowerCase().contains(query.toLowerCase());

        }

        adapter.updateList(filteredList);
    }

//    private void filterClick(View v) {
//        PopupMenu popupMenu = new PopupMenu(getContext(), v);
//        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_product, popupMenu.getMenu());
//
//        if (currentFilterStatus != null) {
//            popupMenu.getMenu().findItem(currentFilterStatus).setChecked(true);
//        }
//
//        popupMenu.setOnMenuItemClickListener(item -> {
//            int itemId = item.getItemId();
//            if (itemId == R.id.filter_clear_product) {
//                currentFilterStatus = null;
//                filterProductAndStatus(searchProduct.getText().toString(), null);
//            } else {
//                currentFilterStatus = itemId;
//                filterProductAndStatus(searchProduct.getText().toString(), itemId);
//            }
//            return true;
//        });
//
//        popupMenu.show();
//    }

    private void showDialogAddProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameAddProduct);
        EditText typeField = dialogView.findViewById(R.id.typeAddProduct);
        EditText priceField = dialogView.findViewById(R.id.priceAddProduct);
        EditText brandField = dialogView.findViewById(R.id.brandAddProduct);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();
                String type = typeField.getText().toString().trim();
                String priceStr = priceField.getText().toString().trim();
                String brand = brandField.getText().toString().trim();

                if (validateProductInput(nameField, typeField, priceField, brandField, name, type, priceStr, brand)) {
                    double price = Double.parseDouble(priceStr);
                    ProductModel newProduct = new ProductModel();
                    newProduct.setName(name);
                    newProduct.setProductType(type);
                    newProduct.setDiscountPrice(price);
                    newProduct.setProductBrand(brand);

                    ProductDao productDao = new ProductDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (productDao.insert(newProduct)) {
                        loadData();
                        Toast.makeText(getContext(), "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateProductInput(EditText nameField, EditText typeField, EditText priceField, EditText brandField, String name, String type, String priceStr, String brand) {
        if (TextUtils.isEmpty(name)) {
            nameField.setError("Vui lòng nhập tên sản phẩm!");
            return false;
        }

        if (TextUtils.isEmpty(type)) {
            typeField.setError("Vui lòng nhập loại sản phẩm!");
            return false;
        }

        if (TextUtils.isEmpty(priceStr) || !priceStr.matches("\\d+(\\.\\d{1,2})?")) {
            priceField.setError("Giá không hợp lệ!");
            return false;
        }

        if (TextUtils.isEmpty(brand)) {
            brandField.setError("Vui lòng nhập thương hiệu sản phẩm!");
            return false;
        }

        return true;
    }
    private void showUpdateProductDialog(ProductModel product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_product, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameUpdateProduct);
        EditText typeField = dialogView.findViewById(R.id.typeUpdateProduct);
        EditText priceField = dialogView.findViewById(R.id.priceUpdateProduct);
        EditText brandField = dialogView.findViewById(R.id.brandUpdateProduct);

        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getDiscountPrice()));
        typeField.setText(product.getProductType());
        brandField.setText(product.getProductBrand());

        builder.setPositiveButton("Cập nhật", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            updateButton.setOnClickListener(v -> {
                String newName = nameField.getText().toString().trim();
                String newType = typeField.getText().toString().trim();
                String priceStr = priceField.getText().toString().trim();
                String newBrand = brandField.getText().toString().trim();

                if (validateProductInput(nameField, typeField, priceField, brandField, newName, newType, priceStr, newBrand)) {
                    double newPrice = Double.parseDouble(priceStr);
                    product.setName(newName);
                    product.setProductType(newType);
                    product.setDiscountPrice(newPrice);
                    product.setProductBrand(newBrand);

                    ProductDao productDao = new ProductDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (productDao.updateProduct(product)) {
                        loadData();  // Tải lại danh sách sản phẩm
                        Toast.makeText(getContext(), "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi cập nhật sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

}

