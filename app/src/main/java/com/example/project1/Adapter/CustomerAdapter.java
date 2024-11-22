package com.example.project1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project1.Model.CustomerModel;
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

            // Lưu ViewHolder vào View
            convertView.setTag(holder);
        } else {
            // Lấy ViewHolder đã lưu từ View
            holder = (ViewHolder) convertView.getTag();
        }

        // Gán dữ liệu từ CustomerModel vào View
        CustomerModel customer = customerList.get(position);
        holder.cusName.setText(customer.getName());
        holder.cusPhone.setText(customer.getPhoneNumber());

        return convertView;
    }

    // ViewHolder để tối ưu hiệu suất
    private static class ViewHolder {
        TextView cusName;
        TextView cusPhone;
    }

    }

