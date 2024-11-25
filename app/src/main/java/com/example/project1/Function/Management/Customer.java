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

import com.example.project1.Adapter.CustomerAdapter;
import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.CustomerDao;
import com.example.project1.Model.CustomerModel;
import com.example.project1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Fragment {
    private List<CustomerModel> customerList = new ArrayList<>();
    private CustomerAdapter adapter;
    private ListView customerView;
    private ImageView filterCustomer;
    private EditText searchCustomer;
    private Integer currentFilterStatus = null;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer, container, false);

        initializeUI(view);

        loadData();

        setupListeners();

        return view;
    }

    private void initializeUI(View view) {
        customerView = view.findViewById(R.id.listCustomer);
        filterCustomer = view.findViewById(R.id.filterCustomer);
        searchCustomer = view.findViewById(R.id.searchCustomer);
        FloatingActionButton btnAddCustomer = view.findViewById(R.id.btn_addCustomer);

        btnAddCustomer.setOnClickListener(v -> showDialogAddCustomer());
    }

    private void loadData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        CustomerDao customerDao = new CustomerDao(db);

        customerList.clear();
        customerList.addAll(customerDao.getCustomerList());

        adapter = new CustomerAdapter(getContext(), customerList);
        customerView.setAdapter(adapter);
    }

    private void setupListeners() {
        searchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomerAndStatus(s.toString(), currentFilterStatus);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không xử lý
            }
        });

        filterCustomer.setOnClickListener(this::filterClick);

        customerView.setOnItemClickListener((parent, view, position, id) -> {
            CustomerModel selectedCustomer = customerList.get(position);
            showUpdateCustomerDialog(selectedCustomer);
        });
    }

    private void filterCustomerAndStatus(String query, Integer statusFilter) {
        List<CustomerModel> filteredList = new ArrayList<>();

        for (CustomerModel customer : customerList) {
            boolean matchesSearch = TextUtils.isEmpty(query)
                    || String.valueOf(customer.getId()).contains(query)
                    || customer.getName().toLowerCase().contains(query.toLowerCase())
                    || customer.getPhoneNumber().contains(query);

            boolean matchesStatus = (statusFilter == null)
                    || (statusFilter == R.id.filter_VIP_customer && customer.isVIP())
                    || (statusFilter == R.id.filter_customer && !customer.isVIP());

            if (matchesSearch && matchesStatus) {
                filteredList.add(customer);
            }
        }

        adapter.updateList(filteredList);
    }

    private void filterClick(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_customer, popupMenu.getMenu());

        if (currentFilterStatus != null) {
            popupMenu.getMenu().findItem(currentFilterStatus).setChecked(true);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.filter_clear_customer) {
                currentFilterStatus = null;
                filterCustomerAndStatus(searchCustomer.getText().toString(), null);
            } else {
                currentFilterStatus = itemId;
                filterCustomerAndStatus(searchCustomer.getText().toString(), itemId);
            }
            return true;
        });

        popupMenu.show();
    }

    private void showDialogAddCustomer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_customer, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameAddCustomer);
        EditText phoneField = dialogView.findViewById(R.id.phoneAddCustomer);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();
                String phone = phoneField.getText().toString().trim();

                if (validateCustomerInput(nameField, phoneField, name, phone)) {
                    CustomerModel newCustomer = new CustomerModel();
                    newCustomer.setName(name);
                    newCustomer.setPhoneNumber(phone);
                    newCustomer.setVIP(false);

                    CustomerDao customerDao = new CustomerDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (customerDao.insert(newCustomer)) {
                        loadData();
                        Toast.makeText(getContext(), "Thêm khách hàng thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm khách hàng!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateCustomerInput(EditText nameField, EditText phoneField, String name, String phone) {
        if (TextUtils.isEmpty(name)) {
            nameField.setError("Vui lòng nhập tên khách hàng!");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneField.setError("Vui lòng nhập số điện thoại!");
            return false;
        }

        if (!phone.matches("^\\d{10,}$")) {
            phoneField.setError("Số điện thoại không hợp lệ!");
            return false;
        }

        CustomerDao customerDao = new CustomerDao(new DatabaseHelper(getContext()).getWritableDatabase());
        if (customerDao.isPhoneNumberExists(phone)) {
            phoneField.setError("Số điện thoại đã tồn tại!");
            return false;
        }

        return true;
    }

    private void showUpdateCustomerDialog(CustomerModel customer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_customer, null);
        builder.setView(dialogView);

        EditText nameField = dialogView.findViewById(R.id.nameUpdateCustomer);
        EditText phoneField = dialogView.findViewById(R.id.phoneUpdateCustomer);

        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhoneNumber());

        builder.setPositiveButton("Cập nhật", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            updateButton.setOnClickListener(v -> {
                String newName = nameField.getText().toString().trim();
                String newPhone = phoneField.getText().toString().trim();

                if (validateCustomerInput(nameField, phoneField, newName, newPhone)) {
                    customer.setName(newName);
                    customer.setPhoneNumber(newPhone);

                    CustomerDao customerDao = new CustomerDao(new DatabaseHelper(getContext()).getWritableDatabase());
                    if (customerDao.updateCusProfile(customer)) {
                        loadData();
                        Toast.makeText(getContext(), "Cập nhật khách hàng thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
    }
}
