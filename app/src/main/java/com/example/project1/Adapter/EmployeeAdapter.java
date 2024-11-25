package com.example.project1.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.UserDao;
import com.example.project1.Function.SecurityLock;
import com.example.project1.Model.CustomerModel;
import com.example.project1.Model.UserModel;
import com.example.project1.Navigation;
import com.example.project1.R;

import java.util.Collections;
import java.util.List;

public class EmployeeAdapter extends BaseAdapter {
    private Context context;
    private List<UserModel> employeeList;
    private String username;

    public EmployeeAdapter(Context context, List<UserModel> employeeList, String username) {
        this.context = context;
        this.employeeList = employeeList;
        this.username = username;
    }

    public void updateList(List<UserModel> newList) {
        employeeList.clear();
        employeeList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return employeeList.size();
    }

    @Override
    public Object getItem(int position) {
        return employeeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_employee, parent, false);
        }

        ImageView imgEmployee = convertView.findViewById(R.id.imgEmployee);
        TextView nameEmployee = convertView.findViewById(R.id.nameEmployee);
        TextView phoneEmployee = convertView.findViewById(R.id.phoneEmployee);
        TextView statusEmployee = convertView.findViewById(R.id.statusEmployee);

        UserModel employee = employeeList.get(position);

        // Set image (nếu có)
        byte[] imageBytes = employee.getImage();
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgEmployee.setImageBitmap(bitmap);
        } else {
            imgEmployee.setImageResource(R.drawable.user2); // Ảnh mặc định
        }

        nameEmployee.setText("Họ tên: " + employee.getName());
        phoneEmployee.setText("SĐT: " + employee.getPhoneNumber());
        statusEmployee.setText("Trạng thái: " + employee.getActiveStatus());

        LinearLayout linearLayoutEmployee = convertView.findViewById(R.id.linearLayoutEmployee);
        // Đổi màu nền theo trạng thái
        if (employee.isActive()) {
            linearLayoutEmployee.setBackgroundColor(context.getResources().getColor(android.R.color.white)); // Màu trắng
            imgEmployee.setBackgroundColor(context.getResources().getColor(android.R.color.white)); // Màu trắng
        } else {
            linearLayoutEmployee.setBackgroundColor(context.getResources().getColor(R.color.gray)); // Màu xám
            imgEmployee.setBackgroundColor(context.getResources().getColor(R.color.gray)); // Màu xám
        }

        // Thêm sự kiện click
        convertView.setOnClickListener(v -> showOptionsDialog(employee));

        return convertView;
    }

    // Hiển thị Dialog chọn hành động
    private void showOptionsDialog(UserModel employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{"Tài khoản & mật khẩu", "Thông tin chi tiết"}, (dialog, which) -> {
            if (which == 0) {
                showAccountPassword(employee);
            } else if (which == 1) {
                showEmployeeDetailDialog(employee);
            }
        });
        builder.create().show();
    }

    // Hiển thị chi tiết nhân viên
    // Hiển thị chi tiết nhân viên
    @SuppressLint("SetTextI18n")
    private void showEmployeeDetailDialog(UserModel employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogDetailView = LayoutInflater.from(context).inflate(R.layout.dialog_employee_detail, null);
        builder.setView(dialogDetailView);

        TextView idEmployeeDetail = dialogDetailView.findViewById(R.id.idEmployeeDetail);
        TextView nameEmployeeDetail = dialogDetailView.findViewById(R.id.nameEmployeeDetail);
        TextView emailEmployeeDetail = dialogDetailView.findViewById(R.id.emailEmployeeDetail);
        TextView phoneEmployeeDetail = dialogDetailView.findViewById(R.id.phoneEmployeeDetail);
        TextView roleEmployeeDetail = dialogDetailView.findViewById(R.id.roleEmployeeDetail);
        ImageView imgEmployeeDetail = dialogDetailView.findViewById(R.id.imgEmployeeDetail);
        Spinner statusEmployeeDetail = dialogDetailView.findViewById(R.id.statusEmployeeDetail);

        // Đổ dữ liệu
        idEmployeeDetail.setText("Mã nhân viên: " + employee.getId());
        nameEmployeeDetail.setText("Họ và tên: " + employee.getName());
        emailEmployeeDetail.setText("Email: " + employee.getEmail());
        phoneEmployeeDetail.setText("Số điện thoại: " + employee.getPhoneNumber());
        roleEmployeeDetail.setText("Chức vụ: " + employee.getRole());

        // Set image (nếu có)
        byte[] imageBytes = employee.getImage();
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgEmployeeDetail.setImageBitmap(bitmap);
        } else {
            imgEmployeeDetail.setImageResource(R.drawable.user2); // Ảnh mặc định
        }

        // Cập nhật Spinner với danh sách trạng thái
        String[] statuses = {"Đi làm", "Nghỉ làm"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusEmployeeDetail.setAdapter(statusAdapter);

        // Chọn trạng thái theo thông tin của nhân viên
        if (employee.isActive()) {
            statusEmployeeDetail.setSelection(0); // "Còn hạn hợp đồng"
        } else {
            statusEmployeeDetail.setSelection(1); // "Đã hết hạn hợp đồng"
        }

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Lấy trạng thái mới từ Spinner
            boolean newStatus = statusEmployeeDetail.getSelectedItemPosition() == 0; // Nếu chọn "Còn hạn hợp đồng"

            // Cập nhật trạng thái mới cho nhân viên
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            UserDao userDao = new UserDao(db);
            boolean isUpdated = userDao.updateEmployeeStatus(employee.getId(), newStatus);

            if (isUpdated) {
                // Cập nhật trạng thái trong list
                employee.setActive(newStatus);
                sortEmployeeList();

                // Cập nhật giao diện (notifyDataSetChanged để Adapter cập nhật)
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Hiển thị tài khoản và mật khẩu
    @SuppressLint("SetTextI18n")
    private void showAccountPassword(UserModel employee) {
        // Kiểm tra xem người dùng đã có mã bảo mật chưa
        UserDao userDao = new UserDao(new DatabaseHelper(context).getReadableDatabase());
        String existingLock = userDao.getSecurityLock(username); // Sử dụng username của người dùng đăng nhập

        if (TextUtils.isEmpty(existingLock)) {
            // Hiển thị dialog gợi ý tạo mã bảo mật
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Tạo mã bảo mật");
            builder.setMessage("Bạn chưa có mã bảo mật. Bạn có muốn tạo mã bảo mật không?");
            builder.setPositiveButton("Có", (dialog, which) -> {
                // Chuyển sang fragment thêm mã bảo mật
                if (context instanceof Navigation) {
                    Navigation navigation = (Navigation) context;
                    Fragment securityCodeFragment = new SecurityLock();
                    ((SecurityLock) securityCodeFragment).setUsername(username); // Truyền username của người dùng đăng nhập
                    navigation.loadFragment(securityCodeFragment, "Khóa bảo mật");
                }
            });
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
            builder.create().show();
            return;
        }

        // Tạo dialog nhập mã bảo mật
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogSecurityView = LayoutInflater.from(context).inflate(R.layout.dialog_security_code, null);
        builder.setView(dialogSecurityView);

        EditText securityCodeDialog = dialogSecurityView.findViewById(R.id.edt_securityCodeDialog);
        TextView SecurityMessageDialog = dialogSecurityView.findViewById(R.id.securityMessageDialog);
        Button btnSubmit = dialogSecurityView.findViewById(R.id.btn_submitSecurity);

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            String securityCode = securityCodeDialog.getText().toString().trim();

            if (securityCode.isEmpty()) {
                SecurityMessageDialog.setText("Vui lòng nhập mã bảo mật!");
                SecurityMessageDialog.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                return;
            }

            // Kiểm tra mã bảo mật
            if (userDao.checkSecurityLock(username, securityCode)) {
                dialog.dismiss();

                // Hiển thị thông tin tài khoản và mật khẩu
                AlertDialog.Builder accountDialogBuilder = new AlertDialog.Builder(context);
                View dialogShowAccountView = LayoutInflater.from(context).inflate(R.layout.dialog_show_username_password, null);
                accountDialogBuilder.setView(dialogShowAccountView);

                TextView nameShowUsernamePassword = dialogShowAccountView.findViewById(R.id.nameShowUsernamePassword);
                TextView showUsername = dialogShowAccountView.findViewById(R.id.showUsername);
                TextView showPassword = dialogShowAccountView.findViewById(R.id.showPassword);
                ImageView imgShowUsernamePassword = dialogShowAccountView.findViewById(R.id.imgShowUsernamePassword);

                byte[] imageBytes = employee.getImage();
                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imgShowUsernamePassword.setImageBitmap(bitmap);
                } else {
                    imgShowUsernamePassword.setImageResource(R.drawable.user2); // Ảnh mặc định
                }
                nameShowUsernamePassword.setText("Họ và tên: " + employee.getName());
                showUsername.setText("Tên tài khoản: " + employee.getUsername());
                showPassword.setText("Mật khẩu: " + employee.getPassword());

                accountDialogBuilder.setPositiveButton("Đóng", (d, which) -> d.dismiss());
                accountDialogBuilder.create().show();
            } else {
                SecurityMessageDialog.setText("Mã bảo mật không đúng!");
                SecurityMessageDialog.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
            }
        });

        dialog.show();
    }

    // Hàm sắp xếp danh sách (người còn hợp đồng trước)
    private void sortEmployeeList() {
        Collections.sort(employeeList, (e1, e2) -> Boolean.compare(e2.isActive(), e1.isActive()));
    }
}
