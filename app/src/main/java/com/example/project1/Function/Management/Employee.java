package com.example.project1.Function.Management;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.example.project1.Adapter.EmployeeAdapter;
import com.example.project1.Dao.UserDao;
import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Model.UserModel;
import com.example.project1.R;

import java.util.ArrayList;
import java.util.List;

public class Employee extends Fragment {
    private EditText searchEmployee;
    private ImageView searchIconEmployee;
    private ListView listEmployee;
    private List<UserModel> employeeList; // Danh sách gốc
    private EmployeeAdapter adapter;     // Adapter hiển thị
    private Integer currentFilterStatus = null; // Biến lưu trạng thái hiện tại (null: không có bộ lọc)

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

        // Gán adapter cho ListView
        listEmployee = view.findViewById(R.id.listEmployee);
        adapter = new EmployeeAdapter(getContext(), employeeList);
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
        searchIconEmployee = view.findViewById(R.id.searchIconEmployee);
        searchIconEmployee.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_filter_status, popupMenu.getMenu());

            // Đặt tick cho trạng thái hiện tại nếu có
            if (currentFilterStatus != null) {
                popupMenu.getMenu().findItem(currentFilterStatus).setChecked(true);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (currentFilterStatus != null && currentFilterStatus == itemId) {
                    // Nếu nhấn lại trạng thái đã chọn -> hủy lọc
                    currentFilterStatus = null;
                    filterEmployeesAndStatus(searchEmployee.getText().toString(), null); // Reset trạng thái
                } else {
                    // Áp dụng trạng thái mới
                    currentFilterStatus = itemId;
                    if (itemId == R.id.filter_active) {
                        filterEmployeesAndStatus(searchEmployee.getText().toString(), R.id.filter_active);
                    } else if (itemId == R.id.filter_inactive) {
                        filterEmployeesAndStatus(searchEmployee.getText().toString(), R.id.filter_inactive);
                    }
                }
                return true;
            });

            popupMenu.show();
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
        adapter = new EmployeeAdapter(getContext(), filteredList);
        listEmployee.setAdapter(adapter);
    }
}