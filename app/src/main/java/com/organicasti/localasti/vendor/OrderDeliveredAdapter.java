package com.organicasti.localasti.vendor;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.organicasti.localasti.R;
import com.organicasti.localasti.ReportHolder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderDeliveredAdapter extends RecyclerView.Adapter<OrderDeliveredAdapter.ViewHolder>{

    private ArrayList<ReportHolder> adminHolders;

    OrderDeliveredAdapter(ArrayList<ReportHolder> adminHolders) {
        this.adminHolders = adminHolders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_report_orders_items,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        String price = adminHolders.get(position).getProductPrice();
        String Quantity = adminHolders.get(position).getProductQuantity();
        holder.VendorName.setText("Number : "+adminHolders.get(position).getVendorName());
        holder.CustomerName.setText("Name : "+adminHolders.get(position).getCustomerName());
        holder.Product.setText(adminHolders.get(position).getProductName());
        holder.Quantity.setText(Quantity);
        holder.Price.setText(Double.parseDouble(price)*Double.parseDouble(Quantity)+" Rs");
        holder.Description.setText(adminHolders.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return adminHolders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView CustomerName,VendorName,Product,Price,Quantity,Description;
        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            CustomerName = itemView.findViewById(R.id.customer_name);
            VendorName = itemView.findViewById(R.id.vendor_name);
            Product = itemView.findViewById(R.id.product_name);
            Price = itemView.findViewById(R.id.product_price);
            Quantity = itemView.findViewById(R.id.product_quantity);
            Description = itemView.findViewById(R.id.description);
        }
    }
}
