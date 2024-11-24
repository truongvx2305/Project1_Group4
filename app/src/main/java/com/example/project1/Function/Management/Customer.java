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
import android.widget.CheckBox;
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
    private List<CustomerModel> customerList;
    private ListView customerView;
    private CustomerAdapter adapter;
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

        // Kết nối cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        CustomerDao customerDao = new CustomerDao(db);

        // Lấy danh sách khách hàng từ database
        customerList = customerDao.getCustomerList();

        // Liên kết ListView
        customerView = view.findViewById(R.id.listCustomer);

        // Khởi tạo adapter và gán cho ListView
        adapter = new CustomerAdapter(getContext(), customerList);
        customerView.setAdapter(adapter);

        // Thiết lập tìm kiếm
        searchCustomer = view.findViewById(R.id.searchCustomer);
        searchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomerAndStatus(s.toString(), currentFilterStatus); // Kết hợp tìm kiếm và trạng thái
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Thiết lập nút lọc
        filterCustomer = view.findViewById(R.id.filterCustomer);
        filterCustomer.setOnClickListener(this::filterClick);

        // Nút thêm khách hàng
        FloatingActionButton btnAddCustomer = view.findViewById(R.id.btn_addCustomer);
        btnAddCustomer.setOnClickListener(v -> showDialogAddCustomer());

        // Thêm sự kiện khi nhấn vào một khách hàng
        customerView.setOnItemClickListener((parent, view1, position, id) -> {
            CustomerModel selectedCustomer = customerList.get(position);
            showUpdateCustomerDialog(selectedCustomer);
        });


        return view;
    }

    // Lọc khách hàng theo tìm kiếm và trạng thái
    private void filterCustomerAndStatus(String query, Integer statusFilter) {
        if (query != null && query.trim().isEmpty()) {
            query = ""; // Nếu chỉ nhập khoảng trắng, xem như không có tìm kiếm
        }

        List<CustomerModel> filteredList = new ArrayList<>();

        for (CustomerModel customer : customerList) {
            boolean matchesSearch = query == null || query.isEmpty()
                    || String.valueOf(customer.getId()).contains(query)
                    || customer.getName().toLowerCase().contains(query.toLowerCase())
                    || customer.getPhoneNumber().contains(query);

            // Kiểm tra trạng thái khách hàng
            boolean matchesStatus = (statusFilter == null) // Không lọc trạng thái
                    || (statusFilter == R.id.filter_VIP_customer && customer.isVIP()) // Lọc khách hàng VIP
                    || (statusFilter == R.id.filter_customer && !customer.isVIP()); // Lọc khách hàng thường

            // Chỉ thêm vào danh sách nếu thỏa mãn cả tìm kiếm và trạng thái
            if (matchesSearch && matchesStatus) {
                filteredList.add(customer);
            }
        }

        adapter = new CustomerAdapter(getContext(), filteredList);
        customerView.setAdapter(adapter);
    }

    // Bộ lọc trạng thái khách hàng
    private void filterClick(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_customer, popupMenu.getMenu());

        // Đặt tick cho trạng thái hiện tại nếu có
        if (currentFilterStatus != null) {
            popupMenu.getMenu().findItem(currentFilterStatus).setChecked(true);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.filter_clear_customer) {
                // Xóa bộ lọc
                currentFilterStatus = null;
                filterCustomerAndStatus(searchCustomer.getText().toString(), null);
            } else {
                // Đặt trạng thái mới
                currentFilterStatus = itemId;

                // Áp dụng lọc trạng thái
                filterCustomerAndStatus(searchCustomer.getText().toString(), itemId);
            }

            return true;
        });

        popupMenu.show();
    }

    // Hiển thị dialog thêm khách hàng mới
    private void showDialogAddCustomer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_customer, null);
        builder.setView(dialogView);

        EditText usernameAddCustomer = dialogView.findViewById(R.id.nameAddCustomer);
        EditText sdtAddCustomer = dialogView.findViewById(R.id.phoneAddCustomer);

        builder.setPositiveButton("Thêm", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = usernameAddCustomer.getText().toString().trim();
                String sdt = sdtAddCustomer.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (TextUtils.isEmpty(name)) {
                    usernameAddCustomer.setError("Vui lòng nhập tên khách hàng!");
                    return;
                }
                if (TextUtils.isEmpty(sdt)) {
                    sdtAddCustomer.setError("Vui lòng nhập số điện thoại!");
                    return;
                }
                if (!sdt.matches("^\\d{10,}$")) {
                    sdtAddCustomer.setError("Số điện thoại không hợp lệ!");
                    return;
                }

                // Tạo đối tượng CustomerModel mới
                CustomerModel newCustomer = new CustomerModel();
                newCustomer.setName(name);
                newCustomer.setPhoneNumber(sdt);
                newCustomer.setVIP(false);

                // Thêm vào cơ sở dữ liệu
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                CustomerDao customerDao = new CustomerDao(db);

                boolean isInserted = customerDao.insert(newCustomer);

                if (isInserted) {
                    customerList.clear();
                    customerList.addAll(customerDao.getCustomerList()); // Tải lại từ cơ sở dữ liệu
                    adapter.notifyDataSetChanged();

                    Toast.makeText(getContext(), "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Có lỗi xảy ra khi thêm khách hàng", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    // Hiển thị dialog cập nhật thông tin khách hàng
    private void showUpdateCustomerDialog(CustomerModel customer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_update_customer, null);
        builder.setView(dialogView);

        EditText nameEdit = dialogView.findViewById(R.id.nameUpdateCustomer);
        EditText phoneEdit = dialogView.findViewById(R.id.phoneUpdateCustomer);
        CheckBox vipCheckBox = dialogView.findViewById(R.id.vipUpdateCustomer);

        // Gán giá trị hiện tại vào dialog
        nameEdit.setText(customer.getName());
        phoneEdit.setText(customer.getPhoneNumber());
        vipCheckBox.setChecked(customer.isVIP());

        builder.setPositiveButton("Cập nhật", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button updateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            updateButton.setOnClickListener(v -> {
                String newName = nameEdit.getText().toString().trim();
                String newPhone = phoneEdit.getText().toString().trim();
                boolean isVIP = vipCheckBox.isChecked();

                // Kiểm tra dữ liệu đầu vào
                if (TextUtils.isEmpty(newName)) {
                    nameEdit.setError("Vui lòng nhập tên khách hàng!");
                    return;
                }
                if (TextUtils.isEmpty(newPhone)) {
                    phoneEdit.setError("Vui lòng nhập số điện thoại!");
                    return;
                }
                if (!newPhone.matches("^\\d{10,}$")) {
                    phoneEdit.setError("Số điện thoại không hợp lệ!");
                    return;
                }

                // Cập nhật thông tin khách hàng
                customer.setName(newName);
                customer.setPhoneNumber(newPhone);
                customer.setVIP(isVIP);

                CustomerDao customerDao = new CustomerDao(new DatabaseHelper(getContext()).getWritableDatabase());
                boolean isUpdated = customerDao.updateCusProfile(customer);

                if (isUpdated) {
                    customerList.clear();
                    customerList.addAll(customerDao.getCustomerList()); // Tải lại danh sách
                    adapter.notifyDataSetChanged();

                    Toast.makeText(getContext(), "Cập nhật khách hàng thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Có lỗi xảy ra khi cập nhật khách hàng", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

}
