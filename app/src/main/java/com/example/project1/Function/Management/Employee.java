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
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class Employee extends Fragment {
    private EditText searchEmployee;
    private ImageView filterEmployee;
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
        filterEmployee = view.findViewById(R.id.filterEmployee);
        filterEmployee.setOnClickListener(this::filterAction);

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

    private void filterAction(View v) {
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

    private void showEmployeeDetailDialog(UserModel employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_employee_detail, null);
        builder.setView(dialogView);

        // Ánh xạ các thành phần trong Dialog
        TextView idEmployeeDetail = dialogView.findViewById(R.id.idEmployeeDetail);
        TextView nameEmployeeDetail = dialogView.findViewById(R.id.nameEmployeeDetail);
        TextView emailEmployeeDetail = dialogView.findViewById(R.id.emailEmployeeDetail);
        TextView phoneEmployeeDetail = dialogView.findViewById(R.id.phoneEmployeeDetail);
        TextView roleEmployeeDetail = dialogView.findViewById(R.id.roleEmployeeDetail);
        ImageView imgEmployee = dialogView.findViewById(R.id.imgEmployee);

        // Hiển thị thông tin từ đối tượng `UserModel`
        idEmployeeDetail.setText(String.format("Mã nhân viên: %s", employee.getId()));
        nameEmployeeDetail.setText(String.format("Họ và tên: %s", employee.getName()));
        emailEmployeeDetail.setText(String.format("Email: %s", employee.getEmail()));
        phoneEmployeeDetail.setText(String.format("Số điện thoại: %s", employee.getPhoneNumber()));
        roleEmployeeDetail.setText(String.format("Chức vụ: %s", employee.getRole()));

        // Đặt ảnh nếu có (hoặc ảnh mặc định)
        imgEmployee.setImageResource(R.drawable.user2);

        // Thêm nút đóng
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        // Hiển thị Dialog
        builder.create().show();
    }

    private void showAccountPassword(UserModel employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tài khoản mật khẩu");

        // Nội dung thông tin tài khoản
        String message = String.format("Tên đăng nhập: %s\nMật khẩu: %s",
                employee.getUsername(), employee.getPassword());
        builder.setMessage(message);

        // Nút đóng
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

}
