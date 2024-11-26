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

import com.example.project1.Adapter.DiscountAdapter;
import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.DiscountDao;
import com.example.project1.Model.DiscountModel;
import com.example.project1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Discount extends Fragment {
    private List<DiscountModel> discountList = new ArrayList<>();
    private DiscountAdapter adapter;
    private ListView discountView;
    private ImageView filterDiscount;
    private EditText searchDiscount;
    private Integer currentFilterStatus = null;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discount, container, false);

        initializeUI(view);
        loadData();
        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        discountView = view.findViewById(R.id.listDiscount);
        filterDiscount = view.findViewById(R.id.filterDiscount);
        searchDiscount = view.findViewById(R.id.searchDiscount);
        FloatingActionButton btnAddDiscount = view.findViewById(R.id.btn_addDiscount);

        btnAddDiscount.setOnClickListener(v -> showDialogAddDiscount());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DiscountDao discountDao = new DiscountDao(db);

        discountList.clear();
        discountList.addAll(discountDao.getAlLDiscount());

        adapter = new DiscountAdapter(getContext(), discountList);
        discountView.setAdapter(adapter);
    }

    private void setupListeners() {
        searchDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDiscountAndStatus(s.toString(), currentFilterStatus);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filterDiscount.setOnClickListener(this::filterClick);
    }

    private void filterDiscountAndStatus(String query, Integer statusFilter) {
        List<DiscountModel> filteredList = new ArrayList<>();
        Double searchMinOrder = null;

        // Chuyển đổi query thành số nếu có thể
        if (!TextUtils.isEmpty(query)) {
            try {
                searchMinOrder = Double.parseDouble(query);
            } catch (NumberFormatException e) {
                // Nếu không phải số, searchMinOrder sẽ là null
            }
        }

        for (DiscountModel discount : discountList) {
            boolean matchesSearch = false;

            // Kiểm tra tìm kiếm
            if (searchMinOrder != null) {
                matchesSearch = discount.getMinOrderPrice() == searchMinOrder;
            } else {
                matchesSearch = String.valueOf(discount.getId()).contains(query)
                        || discount.getName().toLowerCase().contains(query.toLowerCase());
            }

            // Kiểm tra theo trạng thái (valid/expired)
            boolean matchesStatus = (statusFilter == null)
                    || (statusFilter == R.id.filter_valid_discount && discount.isValid())
                    || (statusFilter == R.id.filter_expired_discount && !discount.isValid());

            if (matchesSearch && matchesStatus) {
                filteredList.add(discount);
            }
        }

        adapter.updateList(filteredList);
    }


    private void filterClick(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_discount, popupMenu.getMenu());

        if (currentFilterStatus != null) {
            popupMenu.getMenu().findItem(currentFilterStatus).setChecked(true);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.filter_clear_discount) {
                currentFilterStatus = null;
                filterDiscountAndStatus(searchDiscount.getText().toString(), null);
            } else {
                currentFilterStatus = itemId;
                filterDiscountAndStatus(searchDiscount.getText().toString(), itemId);
            }
            return true;
        });

        popupMenu.show();
    }

    private void showDialogAddDiscount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_discount, null);
        builder.setView(dialogView);

        EditText priceField = dialogView.findViewById(R.id.priceAddDiscount);
        EditText minPriceField = dialogView.findViewById(R.id.minPriceAddDiscount);
        EditText endDateField = dialogView.findViewById(R.id.endDateAddDiscount);
        EditText quantityField = dialogView.findViewById(R.id.quantityAddDiscount);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String price = priceField.getText().toString().trim();
                String minPrice = minPriceField.getText().toString().trim();
                String endDate = endDateField.getText().toString().trim();
                String quantity = quantityField.getText().toString().trim();

                // Lấy ngày hiện tại làm startDate
                String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                if (validateDiscountInput(priceField, minPriceField, endDateField, quantityField, price, minPrice, endDate, quantity)) {
                    DiscountModel newDiscount = new DiscountModel();
                    newDiscount.setDiscountPrice(Float.parseFloat(price));
                    newDiscount.setName("Phiếu giảm giá " + newDiscount.getDiscountPrice() * 100 + "%");
                    newDiscount.setMinOrderPrice(Double.parseDouble(minPrice));
                    newDiscount.setStartDate(startDate); // Lưu startDate
                    newDiscount.setEndDate(endDate);
                    newDiscount.setQuantity(Integer.parseInt(quantity)); // Lưu số lượng
                    newDiscount.setValid(true);

                    DiscountDao discountDao = new DiscountDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (discountDao.insert(newDiscount)) {
                        loadData();
                        Toast.makeText(getContext(), "Thêm giảm giá thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm giảm giá!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateDiscountInput(EditText priceField, EditText minPriceField, EditText endDateField, EditText quantityField,
                                          String price, String minPrice, String endDate, String quantityStr) {

        // Kiểm tra giá giảm giá là số thực hợp lệ
        if (priceField != null && (TextUtils.isEmpty(price) || !isValidFloat(price))) {
            priceField.setError("Giá giảm giá không hợp lệ!");
            return false;
        }

        // Kiểm tra giá đơn hàng là số thực hợp lệ
        if (minPriceField != null && (TextUtils.isEmpty(minPrice) || !isValidDouble(minPrice))) {
            minPriceField.setError("Giá đơn hàng không hợp lệ!");
            return false;
        }

        // Kiểm tra ngày kết thúc có hợp lệ không
        if (TextUtils.isEmpty(endDate)) {
            if (endDateField != null) {
                endDateField.setError("Vui lòng nhập ngày kết thúc!");
            }
            return false;
        }
        if (!isValidDate(endDate)) {
            endDateField.setError("Ngày kết thúc không hợp lệ!");
            return false;
        }

        // Kiểm tra số lượng là số nguyên hợp lệ
        if (quantityField != null && (TextUtils.isEmpty(quantityStr) || !isValidInteger(quantityStr))) {
            quantityField.setError("Số lượng không hợp lệ!");
            return false;
        }

        return true;
    }

    private boolean isValidFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false); // Tắt chế độ linh hoạt (lenient mode)
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate != null && !parsedDate.before(new Date()); // Kiểm tra ngày không được trước ngày hiện tại
        } catch (Exception e) {
            return false;
        }
    }
}
