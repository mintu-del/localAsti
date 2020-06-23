package com.organicasti.localasti.vendor;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.organicasti.localasti.ProductAdapter;
import com.organicasti.localasti.ProductDetails;
import com.organicasti.localasti.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionDeliveryReportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_subscription_reports,container,false);

        final RecyclerView SubscriptionDeliveredReport = view.findViewById(R.id.Subscription_Delivered_report);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        SubscriptionDeliveredReport.setLayoutManager(layoutManager);

        final ArrayList<ReportsHolder> holderArrayList = new ArrayList<>();

        final FirebaseFirestore DB = FirebaseFirestore.getInstance();
        final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");


        DB.collection("Subscriptions")
                .whereEqualTo("VendorUID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (final QueryDocumentSnapshot snapshot: queryDocumentSnapshots)
                        {
                            String LastDelivered = snapshot.get("LastDelivered").toString();
                            Date lastDelivered;
                            try {
                                lastDelivered = sdf.parse(LastDelivered);
                                assert lastDelivered != null;
                              if (lastDelivered.getTime() != -19800000)
                                {
                                    String CustomerUID = snapshot.get("CustomerUID").toString();
                                    String SubscriptionID = snapshot.getId();
                                    String Amount = snapshot.get("Amount").toString();
                                    String ProductID = snapshot.get("ProductUID").toString();
                                    String Quantity = snapshot.get("Quantity").toString();

                                    holderArrayList.add(new ReportsHolder(lastDelivered.toString(),
                                            CustomerUID,
                                            Amount,
                                            ProductID,
                                            SubscriptionID,
                                            Quantity));
                                    SubscriptionDeliveredReportAdapter adapter = new SubscriptionDeliveredReportAdapter(holderArrayList);
                                    SubscriptionDeliveredReport.setAdapter(adapter);
                                    SubscriptionDeliveredReport.setHasFixedSize(true);

                               }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });





        return view;
    }

    private class SubscriptionDeliveredReportAdapter extends RecyclerView.Adapter<SubscriptionDeliveredReportAdapter.viewHolder>{


        ArrayList<ReportsHolder> reportsHolders ;

        SubscriptionDeliveredReportAdapter(ArrayList<ReportsHolder> reportsHolders) {
            this.reportsHolders = reportsHolders;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subsctiption_delivered_report_items,parent,false);
            return new viewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final viewHolder holder, final int position)
        {
            holder.Amount.setText("Amount : "+reportsHolders.get(position).getAmount1());
            holder.LastDeliverDate.setText("Last Delivery at : "+reportsHolders.get(position).getLastDelivery()
                    .replace("00:00:00","").replace(" GMT+05:30 ","").replace("1970","").trim());

           FirebaseFirestore DB = FirebaseFirestore.getInstance();


            DB.collection("Users")
                    .document(reportsHolders.get(position).getCustomerUID())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot)
                        {
                            String CustomerName = documentSnapshot.get("FirstName").toString();
                            holder.CustomerName.setText(CustomerName);
                        }
                    });



            DB.collection("Products")
                    .whereEqualTo("ProductID",reportsHolders.get(position).getProductID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            for (QueryDocumentSnapshot snapshot1 : Objects.requireNonNull(task.getResult()))
                            {
                                if (snapshot1.exists())
                                {
                                    String Name = Objects.requireNonNull(snapshot1.get("ProductName")).toString();
                                    String Quantity =   Objects.requireNonNull(reportsHolders.get(position).getQuantity());
                                    String Price = Objects.requireNonNull(snapshot1.get("Rate")).toString();
                                    String description = Objects.requireNonNull(snapshot1.get("Description")).toString();
                                    ProductDetails details = new ProductDetails(Name,Quantity,Price,description);
                                    holder.productDetailsArrayList.add(details);
                                }

                            }
                            final ProductAdapter ProductAdapter = new ProductAdapter(holder.productDetailsArrayList);
                            holder.ProductRecyclerView.setHasFixedSize(true);
                            holder.ProductRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            holder.ProductRecyclerView.setAdapter(ProductAdapter);

                        }
                    });

        }

        @Override
        public int getItemCount() {
            return reportsHolders.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{

            TextView Amount,CustomerName,LastDeliverDate,Description;
            RecyclerView ProductRecyclerView;
            ArrayList<ProductDetails> productDetailsArrayList;
            viewHolder(@NonNull View itemView) {
                super(itemView);

                productDetailsArrayList = new ArrayList<>();
                Amount = itemView.findViewById(R.id.amount);
                CustomerName = itemView.findViewById(R.id.customer_name);
                LastDeliverDate = itemView.findViewById(R.id.last_delivery_date);
                ProductRecyclerView = itemView.findViewById(R.id.Products_recyclerView);
            }
        }
    }

}
