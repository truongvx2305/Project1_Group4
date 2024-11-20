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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.example.project1.Adapter.EmployeeAdapter;
import com.example.project1.Dao.UserDao;
import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Model.UserModel;
import com.example.project1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Employee extends Fragment {
    private String username;
    private EditText searchEmployee;
    private ImageView filterEmployee;
    private ListView listEmployee;
    private List<UserModel> employeeList; // Danh sách gốc
    private EmployeeAdapter adapter;     // Adapter hiển thị
    private Integer currentFilterStatus = null; // Biến lưu trạng thái hiện tại (null: không có bộ lọc)

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee, container, false);

        // Kết nối cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        UserDao userDao = new UserDao(db);

        // Lấy danh sách nhân viên từ database
        employeeList = userDao.getAllEmployees();


        // Sắp xếp danh sách ban đầu (người còn hợp đồng trước)
        sortEmployeeList();

        // Gán adapter cho ListView
        listEmployee = view.findViewById(R.id.listEmployee);
        adapter = new EmployeeAdapter(getContext(), employeeList, username); // Truyền username
        listEmployee.setAdapter(adapter);

        // Tìm kiếm
        searchEmployee = view.findViewById(R.id.searchEmployee);
        searchEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEmployeesAndStatus(s.toString(), currentFilterStatus); // Kết hợp tìm kiếm và trạng thái
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Lọc theo trạng thái
        filterEmployee = view.findViewById(R.id.filterEmployee);
        filterEmployee.setOnClickListener(this::filterClick);

        FloatingActionButton btnAddEmployee = view.findViewById(R.id.btn_addEmployee);
        btnAddEmployee.setOnClickListener(v -> {
            showDialogAddEmployee();
        });

        return view;
    }

    // Hàm kết hợp tìm kiếm và lọc trạng thái
    private void filterEmployeesAndStatus(String query, Integer statusFilter) {
        List<UserModel> filteredList = new ArrayList<>();

        for (UserModel employee : employeeList) {
            boolean matchesSearch = query == null || query.isEmpty()
                    || String.valueOf(employee.getId()).contains(query)
                    || employee.getName().toLowerCase().contains(query.toLowerCase())
                    || employee.getPhoneNumber().contains(query);

            boolean matchesStatus = true; // Mặc định không áp dụng lọc trạng thái
            if (statusFilter != null) {
                if (statusFilter == R.id.filter_active) {
                    matchesStatus = employee.isActive();
                } else if (statusFilter == R.id.filter_inactive) {
                    matchesStatus = !employee.isActive();
                }
            }

            if (matchesSearch && matchesStatus) {
                filteredList.add(employee);
            }
        }

        // Cập nhật adapter với danh sách đã lọc
        adapter = new EmployeeAdapter(getContext(), filteredList, username);
        listEmployee.setAdapter(adapter);
    }

    private void filterClick(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_filter_status, popupMenu.getMenu());

        // Đặt tick cho trạng thái hiện tại nếu có
        if (currentFilterStatus != null) {
            popupMenu.getMenu().findItem(currentFilterStatus).setChecked(true);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.filter_clear) {
                // Xóa bộ lọc
                currentFilterStatus = null;
                filterEmployeesAndStatus(searchEmployee.getText().toString(), null);

                // Reset trạng thái chọn cho tất cả mục
                popupMenu.getMenu().findItem(R.id.filter_active).setChecked(false);
                popupMenu.getMenu().findItem(R.id.filter_inactive).setChecked(false);
            } else {
                // Đặt trạng thái mới
                currentFilterStatus = itemId;

                // Áp dụng lọc trạng thái
                if (itemId == R.id.filter_active) {
                    filterEmployeesAndStatus(searchEmployee.getText().toString(), R.id.filter_active);
                } else if (itemId == R.id.filter_inactive) {
                    filterEmployeesAndStatus(searchEmployee.getText().toString(), R.id.filter_inactive);
                }

                // Đặt tick trạng thái được chọn, bỏ tick trạng thái khác
                popupMenu.getMenu().findItem(R.id.filter_active).setChecked(itemId == R.id.filter_active);
                popupMenu.getMenu().findItem(R.id.filter_inactive).setChecked(itemId == R.id.filter_inactive);
            }

            return true;
        });

        popupMenu.show();
    }

    // Hàm sắp xếp danh sách (người còn hợp đồng trước)
    private void sortEmployeeList() {
        Collections.sort(employeeList, (e1, e2) -> Boolean.compare(e2.isActive(), e1.isActive()));
    }

    private void showDialogAddEmployee() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_employee, null);
        builder.setView(dialogView);

        EditText usernameAddEmployee = dialogView.findViewById(R.id.usernameAddEmployee);
        EditText passwordAddEmployee = dialogView.findViewById(R.id.passwordAddEmployee);

        builder.setPositiveButton("Thêm", null); // Để null để xử lý kiểm tra sau
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String username = usernameAddEmployee.getText().toString().trim();
                String password = passwordAddEmployee.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra trùng lặp tên đăng nhập
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                UserDao userDao = new UserDao(db);

                if (userDao.checkUsername(username)) {
                    Toast.makeText(getContext(), "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo đối tượng UserModel mới
                UserModel newEmployee = new UserModel();
                newEmployee.setUsername(username);
                newEmployee.setPassword(password);
                newEmployee.setAdmin(false);
                newEmployee.setActive(true);

                // Thêm vào cơ sở dữ liệu
                boolean isInserted = userDao.insert(newEmployee);

                if (isInserted) {
                    // Cập nhật danh sách hiển thị
                    employeeList.add(newEmployee);
                    sortEmployeeList();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Thêm nhân viên thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Có lỗi xảy ra khi thêm nhân viên", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
