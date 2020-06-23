package com.organicasti.localasti.vendor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.organicasti.localasti.ProductAdapter;
import com.organicasti.localasti.ProductDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.organicasti.localasti.R;


import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NoDeliveryAdapter extends RecyclerView.Adapter<NoDeliveryAdapter.NoDeliveryViewHolder>
{
    private ArrayList<ReportsHolder> reportsHolders;
    private Activity activity ;

    NoDeliveryAdapter(ArrayList<ReportsHolder> reportsHolders, Activity activity) {
        this.reportsHolders = reportsHolders;
        this.activity = activity;
    }


    @NonNull
    @Override
    public NoDeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nodeliveryreport_items,parent,false);
        return new NoDeliveryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final NoDeliveryViewHolder holder, int position) {

        holder.from.setText("From : " +reportsHolders.get(position).getFrom());
        holder.to.setText("To : "+reportsHolders.get(position).getTo());
        holder.customerName.setText(reportsHolders.get(position).getName());
        final FirebaseFirestore DB = FirebaseFirestore.getInstance();
        final CollectionReference ProductsRef = DB.collection("Subscriptions")
                .document(reportsHolders.get(position).getSubscriptionUID())
                .collection("Products");

        ProductsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (final QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult()))
                            {
                                if (snapshot.exists())
                                {
                                    DB.collection("Products")
                                            .document(snapshot.getId())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    DocumentSnapshot snapshot1 = documentSnapshot;
                                                    String Name = Objects.requireNonNull(snapshot1.get("ProductName")).toString();
                                                    String Quantity =   Objects.requireNonNull(snapshot.get("Quantity")).toString();
                                                    String Description = snapshot1.get("Description").toString();
                                                    String Price = Objects.requireNonNull(snapshot1.get("Rate")).toString();
                                                    ProductDetails details = new ProductDetails(Name,Quantity,Price,Description);
                                                    holder.productDetailsArrayList.add(details);

                                                    final ProductAdapter ProductAdapter = new ProductAdapter(holder.productDetailsArrayList);
                                                    holder.NoDeliveryProducts.setHasFixedSize(true);
                                                    holder.NoDeliveryProducts.setLayoutManager(new LinearLayoutManager(activity));
                                                    holder.NoDeliveryProducts.setAdapter(ProductAdapter);
                                                }
                                            });

                                }
                            }


                        }
                        else {
                            Toast.makeText(activity, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public int getItemCount() {
        return reportsHolders.size();
    }

    static class NoDeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView from,to,customerName;
        RecyclerView NoDeliveryProducts;
        ArrayList<ProductDetails> productDetailsArrayList;
        NoDeliveryViewHolder(@NonNull View itemView) {
            super(itemView);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            customerName = itemView.findViewById(R.id.customer_name);
            productDetailsArrayList = new ArrayList<>();
            NoDeliveryProducts = itemView.findViewById(R.id.no_delivery_products_recycler);
        }
    }
}
