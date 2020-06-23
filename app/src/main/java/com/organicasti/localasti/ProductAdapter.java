package com.organicasti.localasti;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder>{


    private ArrayList<ProductDetails> productDetailsArrayList;

    public ProductAdapter(ArrayList<ProductDetails> productDetailsArrayList) {
        this.productDetailsArrayList = productDetailsArrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_product_ist,parent,false);
        return new viewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position)
    {
        final String Name = productDetailsArrayList.get(position).getName();
        final String Price = productDetailsArrayList.get(position).getProductPrice();
        final String Quantity = productDetailsArrayList.get(position).getProductQuantity();
        holder.Name.setText(Name);
        holder.Quantity.setText(Quantity);
        holder.Price.setText(Price);
        holder.Description.setText(productDetailsArrayList.get(position).getDescription());
     // holder.CumulativeAmount.setText(getCountOfDays(productDetailsArrayList.get(position).getFromDate(),productDetailsArrayList.get(position).getToDate()));
    }


    @Override
    public int getItemCount() {
        return productDetailsArrayList.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{
        TextView Name,Quantity,Price,Description;

        viewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.product_name);
            Quantity = itemView.findViewById(R.id.product_quantity);
            Price = itemView.findViewById(R.id.product_amount);
            Description = itemView.findViewById(R.id.description);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        assert createdConvertedDate != null;
        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            assert todayWithZeroTime != null;
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }


    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */

        Calendar eCal = Calendar.getInstance();
        assert expireCovertedDate != null;
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("For Days" + (int) dayCount + " Days");
    }


}
