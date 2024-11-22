package com.example.project1.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project1.Model.CustomerModel;
import com.example.project1.Model.UserModel;
import com.example.project1.R;


import java.util.List;

public class CustomerAdapter extends BaseAdapter {
    private Context context;
    private List<CustomerModel> customerList;


    public CustomerAdapter(Context context, List<CustomerModel> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @Override
    public int getCount() {
        return customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return customerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            // Inflate layout cho từng item
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_customer, parent, false);

            // Khởi tạo ViewHolder
            holder = new ViewHolder();
            holder.cusName = convertView.findViewById(R.id.nameCustomer);
            holder.cusPhone = convertView.findViewById(R.id.phoneCustomer);
            holder.cusStatus = convertView.findViewById(R.id.isVipCustomer);

            // Lưu ViewHolder vào View
            convertView.setTag(holder);
        } else {
            // Lấy ViewHolder đã lưu từ View
            holder = (ViewHolder) convertView.getTag();
        }

        // Gán dữ liệu từ CustomerModel vào View
        CustomerModel customer = customerList.get(position);
        holder.cusName.setText("Họ tên: " + customer.getName());
        holder.cusPhone.setText("Số điện thoại: " + customer.getPhoneNumber());
        holder.cusStatus.setText(customer.getStatus());

        // Đặt màu cho trạng thái
        int statusColor = customer.isVIP()
                ? context.getResources().getColor(R.color.yellow) // Màu vàng
                : context.getResources().getColor(R.color.white); // Màu đen
        holder.cusStatus.setTextColor(statusColor);

        return convertView;
    }

    // ViewHolder để tối ưu hiệu suất
    private static class ViewHolder {
        TextView cusName;
        TextView cusPhone;
        TextView cusStatus;
    }
}

