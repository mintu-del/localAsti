package com.organicasti.localasti.customer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.organicasti.localasti.R;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DeliveryOrderAdapter extends RecyclerView.Adapter<DeliveryOrderAdapter.DeliveryOrderViewHolder> {
    List<String> name, quantity, price, vendorID, description,productID;
    final FirebaseUser user;
    FirebaseFirestore db;
    public DeliveryOrderAdapter(List<String> name,List<String> quantity,List<String> price,List<String> vendorID, List<String> description,List<String> productID ) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.vendorID = vendorID;
        this.description = description;
        this.productID = productID;
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public DeliveryOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.delivery_order_items,parent,false);
        return new DeliveryOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeliveryOrderViewHolder holder, final int position) {
        DocumentReference docRef = db.collection("Users").document(vendorID.get(position));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Map<String, Object> m = document.getData();
                        holder.fromtxtv.setText(m.get("FirstName").toString()+" "+m.get("LastName"));
                        holder.nametxtv.setText(name.get(position));
                        holder.pricetxtv.setText(price.get(position));
                        holder.quantitytxtv.setText(quantity.get(position));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class DeliveryOrderViewHolder extends RecyclerView.ViewHolder {
        TextView nametxtv,pricetxtv,quantitytxtv,fromtxtv;
        public DeliveryOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            nametxtv = itemView.findViewById(R.id.product_name);
            pricetxtv = itemView.findViewById(R.id.product_price);
            quantitytxtv = itemView.findViewById(R.id.product_quantity);
            fromtxtv = itemView.findViewById(R.id.product_from);
        }
    }
}
