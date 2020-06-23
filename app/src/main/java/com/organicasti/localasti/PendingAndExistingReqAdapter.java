package com.organicasti.localasti;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Sai gopal Adapter for both pending and existing Subscription for both users
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PendingAndExistingReqAdapter extends RecyclerView.Adapter<PendingAndExistingReqAdapter.viewholder>{

    private String fragmentName;
    private Activity context;
    private ArrayList<CustomerDetails> arrayList;
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private CollectionReference SubscriptionRef;

    PendingAndExistingReqAdapter(String fragmentName, Activity context, ArrayList<CustomerDetails> arrayList) {
        this.fragmentName = fragmentName;
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_subscription,parent,false);
        return new viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position)
    {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        SubscriptionRef = DB.collection("Subscriptions");
        SubscriptionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        for (final QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult()))
                        {
                            if (snapshot.getId().equals(arrayList.get(position).getSubscriptionID()))
                            {
                                double NumberOfDays = 0.0;
                                String ProductID = snapshot.get("ProductUID").toString();
                                try{

                                    String FromDate = snapshot.get("From").toString();
                                    String ToDate = snapshot.get("To").toString();

                                    Date fromDate = sdf.parse(FromDate);
                                    Date toDate = sdf.parse(ToDate);

                                    assert toDate != null;
                                    assert fromDate != null;

                                    NumberOfDays = (double) ((toDate.getTime()-fromDate.getTime())/(1000 * 60 * 60 * 24));
                                    NumberOfDays = NumberOfDays + 1;
                                    holder.SubscriptionRange.setText("From : "+FromDate+"\nTO : " +ToDate);
                                }
                                catch (Exception e)
                                {
                                    Log.d("Range",e.toString());
                                }
                                if (fragmentName.equals("Existing"))
                                {
                                    holder.amount.setText(snapshot.get("Amount").toString());
                                }
                                final double finalNumberOfDays = NumberOfDays;
                                DB.collection("Products")
                                        .whereEqualTo("ProductID",ProductID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                                            {
                                                double TotalPrice = 0.0;
                                                for (QueryDocumentSnapshot snapshot1 : Objects.requireNonNull(task.getResult()))
                                                {

                                                    double FinalPrice ;
                                                    String Name = Objects.requireNonNull(snapshot1.get("ProductName")).toString();
                                                    String Quantity =   Objects.requireNonNull(snapshot.get("Quantity")).toString();
                                                    String Price = Objects.requireNonNull(snapshot1.get("Rate")).toString();
                                                    String description = snapshot1.get("Description").toString();
                                                    holder.documentID = snapshot.getId();
                                                    ProductDetails details = new ProductDetails(Name,Quantity,Price,description);

                                                    double price = Double.parseDouble(Quantity)*Double.parseDouble(Price);
                                                    TotalPrice = TotalPrice+(price);
                                                    FinalPrice = finalNumberOfDays*TotalPrice;
                                                    holder.cumulative_amount.setText("Total Price : "+TotalPrice+"Rs"+"\nFinal amount for "+ String.valueOf(finalNumberOfDays).replace(".0","")
                                                            +" Days is " +FinalPrice+"Rs");
                                                    if (fragmentName.equals("Pending"))
                                                    {
                                                        holder.amount.setText(FinalPrice+"");
                                                        holder.FinalPrice = FinalPrice;
                                                    }
                                                    holder.productDetailsArrayList.add(details);
                                                }
                                                final ProductAdapter ProductAdapter = new ProductAdapter(holder.productDetailsArrayList);
                                                holder.ProductsRecyclerView.setHasFixedSize(true);
                                                holder.ProductsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                                holder.ProductsRecyclerView.setAdapter(ProductAdapter);

                                            }
                                        });

                            }

                        }

                    }
                });




        holder.Cname.setText(arrayList.get(position).getCustomerName());
        holder.Cadd.setText(arrayList.get(position).getCustomerAddress()+" : "+arrayList.get(position).getCustomerPhoneNumber());
        holder.amount.setText(arrayList.get(position).getAmount());

        //Activate button for both activate  and save
        holder.activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String amount = holder.amount.getText().toString();
                if (!amount.isEmpty())
                {
                    if (fragmentName.equals("Pending"))
                    {
                        if (holder.FinalPrice < Double.parseDouble(amount))
                        {
                            Toast.makeText(context, "Amount can't be more than final amount", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.show();
                            progressDialog.setTitle("Updating");
                            Map<String, Object> Update = new HashMap<>();
                            Update.put("Amount", amount);
                            Update.put("Status", "Existing");
                            SubscriptionRef.document(holder.documentID)
                                    .update(Update)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                                                context.recreate();
                                            } else {
                                                Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                    else
                    {
                        SubscriptionRef.document(holder.documentID).update("Amount",amount);
                        Toast.makeText(context, "Amount saved", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //managing Delete option
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(holder.amount.getText().toString().trim().equals("0") || holder.amount.getText().toString().trim().isEmpty())
                {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Delete : "+arrayList.get(position).getCustomerName())
                            .setMessage("Are sure do you want to delete Subscription for customer ")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    WriteBatch batch = DB.batch();
                                    progressDialog.setTitle("Deleting");
                                    progressDialog.setMessage(arrayList.get(position).getCustomerName());
                                    progressDialog.show();
                                    batch.delete( SubscriptionRef.document(arrayList.get(position).getSubscriptionID()));

                                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                context.recreate();
                                            }
                                            else {
                                                Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
                                                Log.d("Delete Batch exception ",task.getException().toString());
                                            }
                                        }
                                    });
                                }
                            })
                            .show();
                }
                else {
                    Toast.makeText(context, "Amount Must be 0 to Delete", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewholder extends RecyclerView.ViewHolder{

        TextView Cname,Cadd,cumulative_amount,SubscriptionRange;
        ImageButton delete;
        RecyclerView ProductsRecyclerView;
        ArrayList<ProductDetails> productDetailsArrayList;
        String documentID;
        double FinalPrice ;
        EditText amount ;
        Button activate;
        @SuppressLint("SetTextI18n")
        viewholder(@NonNull View itemView)
        {
            super(itemView);
            Cname = itemView.findViewById(R.id.customer_name);
            Cadd = itemView.findViewById(R.id.customer_adr_pho);
            delete = itemView.findViewById(R.id.delete_button);
            ProductsRecyclerView = itemView.findViewById(R.id.Products_recyclerView);
            productDetailsArrayList = new ArrayList<>();
            amount = itemView.findViewById(R.id.amount);
            cumulative_amount = itemView.findViewById(R.id.cumulative_amount);
            activate = itemView.findViewById(R.id.activate_button);
            SubscriptionRange = itemView.findViewById(R.id.subscription_date_range);
           // activate.setVisibility(View.GONE);

        }
    }



}
