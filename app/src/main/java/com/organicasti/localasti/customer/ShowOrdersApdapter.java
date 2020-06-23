package com.organicasti.localasti.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.organicasti.localasti.R;
import com.organicasti.localasti.vendor.OrderData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ShowOrdersApdapter extends RecyclerView.Adapter<ShowOrdersApdapter.OrderAdapter> {
    List<OrderData> orderData;
    FirebaseFirestore db;
    FirebaseUser user;
    Context c;
    public static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    public ShowOrdersApdapter(List<OrderData> orderData, Context c) {
        this.orderData = orderData;
        this.c = c;
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

    }
    @NonNull
    @Override
    public OrderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_details_summary, parent, false);
        return new OrderAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter holder, int position) {
        final OrderData obj = orderData.get(position);
        holder.background.setBackgroundColor(Color.parseColor("#27FF0000"));
        holder.makePaymentUsing.setText("Pay using UPI :");
        holder.background.setVisibility(View.INVISIBLE);
        holder.name.setText("");
        holder.price.setText("");
        holder.quantity.setText("");
        holder.delivered.setText("");
        holder.orderID.setText("");
        holder.date.setText("");
        holder.vendorname.setText("");
        holder.upi.setText("");
        holder.description.setText("");
        holder.orderID_label.setText("");

        final ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setMessage("Loading Orders");
        progressDialog.setCancelable(false);

       // progressDialog.show();



        db.collection("Users").document(obj.getVendorID())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> m = document.getData();
                        holder.name.setText(obj.getProductName());
                        holder.price.setText(obj.getRateperunit());
                        holder.quantity.setText(obj.getQuantity());
                        holder.delivered.setText(obj.getIsDelivered());
                        holder.orderID.setText(obj.getOrderID());
                        if(obj.getIsDelivered().equals("Delivered") == true) {
                            holder.makePaymentUsing.setText("Paid using UPI :");
                            holder.background.setBackgroundColor(Color.parseColor("#56B8FF9A"));
                        }
                        holder.date.setText(sdf.format(obj.getDateofOrder()));
                        holder.vendorname.setText(m.get("FirstName").toString()+" "+m.get("LastName"));
                        holder.upi.setText(m.get("UPIID").toString());
                        holder.description.setText(obj.getProductDescription());
                        holder.orderID_label.setText("Order ID");
                        holder.background.setVisibility(View.VISIBLE);
                        //progressDialog.dismiss();

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class OrderAdapter extends RecyclerView.ViewHolder {

        TextView name,quantity,price, delivered,date;
        TextView upi, vendorname, description, makePaymentUsing;
        LinearLayout background;
        TextView orderID, orderID_label;



        public OrderAdapter(@NonNull View itemView) {

            super(itemView);
            name = itemView.findViewById(R.id.order_name);
            quantity = itemView.findViewById(R.id.order_quantity);
            price = itemView.findViewById(R.id.order_price);
            delivered = itemView.findViewById(R.id.order_delivered);
            date = itemView.findViewById(R.id.order_date);
            vendorname = itemView.findViewById(R.id.order_vendorname);
            upi = itemView.findViewById(R.id.order_UPI);
            description = itemView.findViewById(R.id.order_description);
            makePaymentUsing = itemView.findViewById(R.id.makePaymentusing);
            background = itemView.findViewById(R.id.background_order);
            orderID = itemView.findViewById(R.id.order_id);
            orderID_label = itemView.findViewById(R.id.order_id_label);
        }
    }
}
