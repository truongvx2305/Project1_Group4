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
import com.example.project1.Adapter.EmployeeAdapter;
import com.example.project1.DB.DatabaseHelper;
import com.example.project1.Dao.CustomerDao;
import com.example.project1.Dao.UserDao;
import com.example.project1.Model.CustomerModel;
import com.example.project1.Model.UserModel;
import com.example.project1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Customer extends Fragment {
    private List<CustomerModel> customerList;
    private ListView Customerview;
    private CustomerAdapter adapter;
    private ImageView filterCustomer;
    private EditText searchCustomer;
    private Integer currentFilterStatus = null;
    private String username;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer, container, false);

        //Kết nối cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        CustomerDao customerDao = new CustomerDao(db);

        customerList = customerDao.getCustomerList();

        Customerview = view.findViewById(R.id.listCustomer);
        Customerview.setAdapter(adapter);

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

        filterCustomer = view.findViewById(R.id.filterCustomer);
        filterCustomer.setOnClickListener(this::filterClick);

        FloatingActionButton btnAddEmployee = view.findViewById(R.id.btn_addCustomer);
        btnAddEmployee.setOnClickListener(v -> {
            showDialogAddCustomer();
        });

        return view;
    }

 //lọc khách hàng vip hay khách hàng thường
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
                 || (statusFilter == 1 && customer.isVIP()) // Lọc khách hàng VIP
                 || (statusFilter == 0 && !customer.isVIP()); // Lọc khách hàng thường

         // Chỉ thêm vào danh sách nếu thỏa mãn cả tìm kiếm và trạng thái
         if (matchesSearch && matchesStatus) {
             filteredList.add(customer);
         }
     }

     adapter = new CustomerAdapter(getContext(), filteredList);
     Customerview.setAdapter(adapter);
 }



    //filter
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

                // Reset trạng thái chọn cho tất cả mục
                popupMenu.getMenu().findItem(R.id.filter_VIP_customer).setChecked(false);
                popupMenu.getMenu().findItem(R.id.filter_customer).setChecked(false);
            } else {
                // Đặt trạng thái mới
                currentFilterStatus = itemId;

                // Áp dụng lọc trạng thái
                if (itemId == R.id.filter_VIP_customer) {
                    filterCustomerAndStatus(searchCustomer.getText().toString(), R.id.filter_VIP_customer);
                } else if (itemId == R.id.filter_customer) {
                    filterCustomerAndStatus(searchCustomer.getText().toString(), R.id.filter_customer);
                }

                // Đặt tick trạng thái được chọn, bỏ tick trạng thái khác
                popupMenu.getMenu().findItem(R.id.filter_VIP_customer).setChecked(itemId == R.id.filter_VIP_customer);
                popupMenu.getMenu().findItem(R.id.filter_customer).setChecked(itemId == R.id.filter_customer);
            }

            return true;
        });

        popupMenu.show();
    }


    private void showDialogAddCustomer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_customer, null);
        builder.setView(dialogView);

        EditText usernameAddCustomer = dialogView.findViewById(R.id.usernameAddCustomer);
        EditText sdtAddCustomer = dialogView.findViewById(R.id.sdtAddCustomer);

        builder.setPositiveButton("Thêm", null); // Để null để xử lý kiểm tra sau
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(v -> {
                String name = usernameAddCustomer.getText().toString().trim();
                String sdt  = sdtAddCustomer.getText().toString().trim();

                // 1. Kiểm tra trống
                if (TextUtils.isEmpty(name)) {
                    usernameAddCustomer.setError("Vui lòng nhập tên khách hàng!");
                    return;
                }
                if (TextUtils.isEmpty(sdt)) {
                    sdtAddCustomer.setError("Vui lòng nhập số điện thoại!");
                    return;
                }


                // 2. Kiểm tra định dạng tên khách hàng (nếu là email hoặc không chứa ký tự đặc biệt)
                if (!name.matches("^[a-zA-Z0-9._-]{5,}$")) {
                    usernameAddCustomer.setError("Tên khách hàng chỉ được chứa chữ, số và ký tự ._-");
                    return;
                }

                if (!sdt.matches("^\\d{10,}$")) {
                    sdtAddCustomer.setError("Số điện thoại phải chứa ít nhất 10 chữ số và chỉ bao gồm số!");
                    return;
                }



                // Tạo đối tượng CustomerModel mới
                CustomerModel newCustomer = new CustomerModel();
                newCustomer.setName(name);
                newCustomer.setPhoneNumber(sdt);
                newCustomer.setVIP(false);

                // Thêm vào cơ sở dữ liệu
                boolean isInserted = CustomerDao.insert(newCustomer);

                if (isInserted) {
                    // Cập nhật danh sách hiển thị
                    customerList.add(newCustomer);

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




}