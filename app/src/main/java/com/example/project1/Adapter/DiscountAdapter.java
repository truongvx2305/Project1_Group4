package com.example.project1.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project1.Model.DiscountModel;
import com.example.project1.R;

import java.util.ArrayList;
import java.util.List;

public class DiscountAdapter extends BaseAdapter {
    private final Context context;
    private final List<DiscountModel> discountList;

    public DiscountAdapter(Context context, List<DiscountModel> discountList) {
        this.context = context;
        this.discountList = new ArrayList<>(discountList);
    }

    public void updateList(List<DiscountModel> newList) {
        discountList.clear();
        discountList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discountList.size();
    }

    @Override
    public Object getItem(int position) {
        return discountList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_discount, parent, false);

            holder = new ViewHolder();
            holder.imgDiscount = convertView.findViewById(R.id.imgDiscount);
            holder.nameDiscount = convertView.findViewById(R.id.nameDiscount);
            holder.priceDiscount = convertView.findViewById(R.id.priceDiscount);
            holder.minPriceDiscount = convertView.findViewById(R.id.minPriceDiscount);
            holder.quantityDiscount = convertView.findViewById(R.id.quantityDiscount);
            holder.endDateDiscount = convertView.findViewById(R.id.endDateDiscount);
            holder.statusDiscount = convertView.findViewById(R.id.statusDiscount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Gán dữ liệu từ DiscountModel vào View
        DiscountModel discount = discountList.get(position);
        holder.nameDiscount.setText(discount.getName());
        holder.priceDiscount.setText("Giảm giá: " + discount.getDiscountPrice() + "%");
        holder.minPriceDiscount.setText("Giá tối thiểu: " + discount.getMinOrderPrice() + " VND");
        holder.quantityDiscount.setText("Số lượng: " + position); // Nếu có trường số lượng, thay thế vào đây
        holder.endDateDiscount.setText("Ngày kết thúc: " + discount.getEndDate());
        holder.statusDiscount.setText("Trạng thái: " + discount.getStatus());

        // Đặt hình ảnh cho imgDiscount (nếu cần thay đổi theo trạng thái hoặc dữ liệu)
        holder.imgDiscount.setImageResource(R.drawable.discount2);

        return convertView;
    }

    // ViewHolder để tối ưu hiệu suất
    private static class ViewHolder {
        ImageView imgDiscount;
        TextView nameDiscount;
        TextView priceDiscount;
        TextView minPriceDiscount;
        TextView quantityDiscount;
        TextView endDateDiscount;
        TextView statusDiscount;
    }
}
