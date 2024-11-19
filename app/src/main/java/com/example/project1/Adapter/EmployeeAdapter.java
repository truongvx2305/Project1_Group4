package com.example.project1.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project1.Model.UserModel;
import com.example.project1.R;

import java.util.List;

public class EmployeeAdapter extends BaseAdapter {
    private Context context;
    private List<UserModel> employeeList;

    public EmployeeAdapter(Context context, List<UserModel> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
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
        TextView idEmployee = convertView.findViewById(R.id.idEmployee);
        TextView nameEmployee = convertView.findViewById(R.id.nameEmployee);
        TextView emailEmployee = convertView.findViewById(R.id.emailEmployee);
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

        idEmployee.setText("Mã nhân viên: " + employee.getId());
        nameEmployee.setText("Họ tên: " + employee.getName());
        emailEmployee.setText("Email: " + employee.getEmail());
        phoneEmployee.setText("SĐT: " + employee.getPhoneNumber());
        statusEmployee.setText("Trạng thái: " + employee.getActiveStatus());

        return convertView;
    }
}
