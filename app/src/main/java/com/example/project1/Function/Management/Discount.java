package com.example.project1.Function.Management;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        // Kiểm tra và cập nhật trạng thái isValid
        updateDiscountValidity();

        adapter = new DiscountAdapter(getContext(), discountList);
        discountView.setAdapter(adapter);
    }

    private void updateDiscountValidity() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();

        DiscountDao discountDao = new DiscountDao(new DatabaseHelper(getContext()).getWritableDatabase());

        for (DiscountModel discount : discountList) {
            try {
                Date endDate = sdf.parse(discount.getEndDate());
                if (endDate != null && endDate.before(currentDate) && discount.isValid()) {
                    discount.setValid(false); // Cập nhật trạng thái trong danh sách
                    discountDao.update(discount); // Lưu trạng thái vào cơ sở dữ liệu
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log lỗi nếu có
            }
        }
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

        if (!TextUtils.isEmpty(query)) {
            try {
                searchMinOrder = Double.parseDouble(query);
            } catch (NumberFormatException ignored) {
            }
        }

        for (DiscountModel discount : discountList) {
            boolean matchesSearch = searchMinOrder != null
                    ? discount.getMinOrderPrice() == searchMinOrder
                    : String.valueOf(discount.getId()).contains(query)
                    || discount.getName().toLowerCase().contains(query.toLowerCase());

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

        // Khi nhấn vào endDateField, hiển thị DatePickerDialog
        endDateField.setOnClickListener(v -> showDatePickerDialog(endDateField));

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String price = priceField.getText().toString().trim();
                String minPrice = minPriceField.getText().toString().trim();
                String endDate = endDateField.getText().toString().trim();
                String quantity = quantityField.getText().toString().trim();

                String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                if (validateDiscountInput(priceField, minPriceField, endDateField, quantityField, price, minPrice, endDate, quantity)) {
                    DiscountModel newDiscount = new DiscountModel();
                    newDiscount.setDiscountPrice(Float.parseFloat(price));
                    String formattedPercentage = new DecimalFormat("#.#").format(newDiscount.getDiscountPrice() * 100);
                    newDiscount.setName("Phiếu giảm giá " + formattedPercentage + "%");
                    newDiscount.setMinOrderPrice(Integer.parseInt(minPrice));
                    newDiscount.setStartDate(startDate);
                    newDiscount.setEndDate(endDate);
                    newDiscount.setQuantity(Integer.parseInt(quantity));
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

    // Hiển thị DatePickerDialog
    private void showDatePickerDialog(EditText editText) {
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng ngày và đặt vào EditText
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    editText.setText(selectedDate);
                }, year, month, day);

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

    private boolean validateDiscountInput(EditText priceField, EditText minPriceField, EditText endDateField, EditText quantityField,
                                          String price, String minPrice, String endDate, String quantityStr) {
        if (priceField != null && (TextUtils.isEmpty(price) || !isValidFloat(price))) {
            priceField.setError("Giá giảm giá không hợp lệ!");
            return false;
        }

        if (minPriceField != null && (TextUtils.isEmpty(minPrice) || !isValidDouble(minPrice))) {
            minPriceField.setError("Giá đơn hàng không hợp lệ!");
            return false;
        }

        if (TextUtils.isEmpty(endDate)) {
            endDateField.setError("Vui lòng nhập ngày kết thúc!");
            return false;
        }

        if (!isValidDate(endDate)) {
            endDateField.setError("Ngày kết thúc không hợp lệ!");
            return false;
        }

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

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
