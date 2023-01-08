package com.example.madclassproj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Hashtable;
import java.util.LinkedList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> {
    Context ct;
    LayoutInflater mInflater;
    LinkedList<Product> mProductList;

    public static class ProductListViewHolder extends RecyclerView.ViewHolder {

        public View item_layout;
        public TextView tv_product_id;
        public TextView tv_product_title;
        public TextView tv_product_price;
        public TextView tv_product_qty;
        public ImageButton btn_inc;
        public ImageButton btn_dec;

        TextView total = (OrderActivity.prod).findViewById(R.id.total);

        public ProductListViewHolder(View v, final ProductListAdapter adapter) {
            super(v);
            item_layout = v;
            tv_product_id = v.findViewById(R.id.tv_product_id);
            tv_product_title = v.findViewById(R.id.tv_product_title);
            tv_product_price = v.findViewById(R.id.tv_product_price);
            tv_product_qty = v.findViewById(R.id.tv_product_qty);
            btn_inc = v.findViewById(R.id.btn_inc);
            btn_dec = v.findViewById(R.id.btn_dec);

            btn_inc.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int product_id = Integer.parseInt(tv_product_id.getText().toString());
                            int upd_qty = Integer.parseInt(tv_product_qty.getText().toString()) + 1;
                            tv_product_qty.setText(String.valueOf(upd_qty));
                            for(final Product product : adapter.mProductList){
                                if(product.getId()==product_id){
                                    product.setQuantity(upd_qty);
                                    float totalA= Float.parseFloat(total.getText().toString());
                                    total.setText(String.valueOf(totalA + product.getPrice()));
                                }
                            }
                        }
                    }
            );
            btn_dec.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int product_id = Integer.parseInt(tv_product_id.getText().toString());
                            int upd_qty = Integer.parseInt(tv_product_qty.getText().toString()) - 1;
                            if (upd_qty >= 0) {
                                tv_product_qty.setText(String.valueOf(upd_qty));
                                for(final Product product : adapter.mProductList){
                                if(product.getId()==product_id){
                                    product.setQuantity(upd_qty);
                                    float totalB= Float.parseFloat(total.getText().toString());
                                    total.setText(String.valueOf(totalB - product.getPrice()));
                                }

                            }
                            }
                        }
                    }
            );
        }
    }
    public ProductListAdapter(Context context, LinkedList<Product> ProductList) {
        ct = context;
        mInflater = LayoutInflater.from(context);
        this.mProductList = ProductList;
    }
    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create view from layout
        View mItemView = mInflater.inflate(R.layout.product_layout, parent, false);
        return new ProductListViewHolder(mItemView, this);
    }
    @Override
    public void onBindViewHolder(final ProductListViewHolder holder, final int position) {
        // Retrieve the data for that position
        final Product mCurrent = mProductList.get(position);
        // Add the data to the view
        holder.tv_product_qty.setText("0");
        holder.tv_product_id.setText(String.valueOf(mCurrent.getId()));
        holder.tv_product_title.setText(mCurrent.getTitle());
        holder.tv_product_price.setText(String.format("%.2f", mCurrent.getPrice()) + " \u20ac");
        holder.tv_product_qty.setText(String.valueOf(mCurrent.getQuantity())); //insert quantity (for PaymentActivity)

        if (ct.getClass().getSimpleName().equals("PaymentActivity")) {
            holder.btn_inc.setVisibility(View.GONE);
            holder.btn_dec.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        // Return the number of data items to display
        return mProductList.size();
    }
}

