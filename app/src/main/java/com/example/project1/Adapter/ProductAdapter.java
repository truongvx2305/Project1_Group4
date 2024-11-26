package com.example.project1.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project1.Model.ProductModel;
import com.example.project1.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private final Context context;
    private final List<ProductModel> productList;

    public ProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = new ArrayList<>(productList);
    }

    public void updateList(List<ProductModel> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_product, parent, false);

            holder = new ViewHolder();
            holder.productName = convertView.findViewById(R.id.nameProduct);
            holder.productPrice = convertView.findViewById(R.id.priceProduct);
            holder.productBrand = convertView.findViewById(R.id.brandProduct);
            holder.productType = convertView.findViewById(R.id.typeProduct);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy dữ liệu từ ProductModel vào View
        ProductModel product = productList.get(position);
        holder.productName.setText("Tên sản phẩm: " + product.getName());
        holder.productPrice.setText("Giá: " + product.getDiscountPrice());
        holder.productBrand.setText("Thương hiệu: " + product.getProductBrand());
        holder.productType.setText("Loại: " + product.getProductType());

        return convertView;
    }

    // ViewHolder để tối ưu hiệu suất
    private static class ViewHolder {
        TextView productName;
        TextView productPrice;
        TextView productBrand;
        TextView productType;
    }
}
