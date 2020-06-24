package com.organicasti.localasti.customer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.organicasti.localasti.CustomerDetails;
import com.organicasti.localasti.ProductAdapter;
import com.organicasti.localasti.ProductDetails;
import com.organicasti.localasti.R;
import com.organicasti.localasti.vendor.Order_OneTime;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.util.Objects.requireNonNull;

public class CustomerSubscriptionFragment extends Fragment
{
    private final FirebaseFirestore FStore = FirebaseFirestore.getInstance();

    private ArrayList VendorUIDArrayList = new ArrayList<>();
    private String UID;
    private CollectionReference SubscriptionRef;
    private ProgressDialog progressDialog;



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_customer_subscription,container,false);

        final RecyclerView CustomerSubscriptionRecycler = view.findViewById(R.id.Customer_Subscription_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        CustomerSubscriptionRecycler.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final ArrayList<CustomerDetails> Customerdetails = new ArrayList<>();
        UID = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        CollectionReference SubscriptionRef = FStore.collection("Subscriptions");

        SubscriptionRef
                .whereEqualTo("CustomerUID", Objects.requireNonNull(UID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        progressDialog.dismiss();
                        for (final QueryDocumentSnapshot RootSnapshot : requireNonNull(task.getResult()))
                        {
                            final String Amount = requireNonNull(RootSnapshot.get("Amount")).toString();
                            final String VendorUID = requireNonNull(RootSnapshot.get("VendorUID")).toString();
                            FStore.collection("Users")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot snapshot : requireNonNull(task.getResult()))
                                            {
                                                if (snapshot.getId().equals(VendorUID))
                                                {
                                                    VendorUIDArrayList.add(VendorUID);
                                                        Customerdetails.add( new CustomerDetails
                                                                (
                                                                requireNonNull(snapshot.get("FirstName")).toString(),
                                                                Amount,
                                                                requireNonNull(snapshot.get("MobileNumber")).toString(),
                                                                RootSnapshot.getId()
                                                                ));
                                                }
                                            }
                                            CustomerSubscriptionAdapter adapter = new CustomerSubscriptionAdapter(Customerdetails);
                                            CustomerSubscriptionRecycler.setAdapter(adapter);
                                            CustomerSubscriptionRecycler.setHasFixedSize(true);
                                        }
                                    });
                        }

                    }
                });



        return view;
    }

    public class CustomerSubscriptionAdapter extends Adapter<CustomerSubscriptionAdapter.ViewHolder>
    {

        private ArrayList<CustomerDetails> customerDetailsArrayList;

        CustomerSubscriptionAdapter(ArrayList<CustomerDetails> customerDetailsArrayList) {
            this.customerDetailsArrayList = customerDetailsArrayList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_subscription_items,parent,false);
            return new ViewHolder(view);
        }

        @Override
        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
        {
            final String[] amount = new String[1];

            @SuppressLint("SimpleDateFormat")
            final DateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd");
            final boolean[] isNoDeliveryCancelable = {false};

            SubscriptionRef = FStore.collection("Subscriptions");
            SubscriptionRef
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            for (final QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult()))
                            {
                                if (snapshot.getId().equals(customerDetailsArrayList.get(position).getSubscriptionID()))
                                {
                                    double NumberOfDays = 0.0;
                                    try{
                                        String FromDate = requireNonNull(snapshot.get("From")).toString();
                                        String ToDate = requireNonNull(snapshot.get("To")).toString();
                                        holder.ToDate = ToDate;

                                        Date fromDate = sdf.parse(FromDate);
                                        Date toDate = sdf.parse(ToDate);

                                        assert toDate != null;
                                        assert fromDate != null;

                                        NumberOfDays = (double) ((toDate.getTime()-fromDate.getTime())/(1000 * 60 * 60 * 24));
                                        NumberOfDays = NumberOfDays + 1;
                                        holder.FromTo.setText("From : "+FromDate+"\nTO : " +ToDate);

                                    }
                                    catch (Exception e)
                                    {
                                        Log.d("Range",e.toString());
                                    }
                                    String Status = requireNonNull(snapshot.get("Status")).toString();
                                    String NoDelivery = requireNonNull(snapshot.get("NoDelivery")).toString();
                                    String ProductUID = null;
                                    try {
                                        ProductUID = requireNonNull(snapshot.get("ProductUID")).toString();
                                    }catch (Exception ignored){
                                        
                                    }
                                    holder.Status.setText(Status);
                                    if (Status.equals("Existing"))
                                    {
                                        holder.NoDelivery.setVisibility(View.VISIBLE);
                                        amount[0] = requireNonNull(snapshot.get("Amount")).toString();
                                        if (NoDelivery.equals("true"))
                                        {
                                            holder.NoDelivery.setText("Please wait...");

                                            FStore.collection("Reports").document(String.valueOf(VendorUIDArrayList.get(position)))
                                                .collection("Customers").document(UID)
                                                .collection("NoDelivery").document(snapshot.getId())
                                                .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                                    {
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        assert documentSnapshot != null;
                                                        if (task.isSuccessful())
                                                        {
                                                            String FromDate = requireNonNull(documentSnapshot.get("From")).toString();
                                                            String ToDate = requireNonNull(documentSnapshot.get("To")).toString();
                                                            try {
                                                                Date toDate = sdf.parse(ToDate);
                                                                Date fromDate = sdf.parse(FromDate);
                                                                Date dater = new Date(System.currentTimeMillis()+80000000);
                                                                try {
                                                                    dater = sdf.parse(sdf.format(dater));
                                                                } catch (java.text.ParseException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                assert toDate != null;
                                                                if (dater.after(toDate))
                                                                {
                                                                    holder.NoDelivery.setText("Set No Delivery Dates");
                                                                }
                                                                assert fromDate != null;

                                                                if (!dater.after(fromDate))
                                                                {
                                                                    isNoDeliveryCancelable[0] = true;
                                                                    holder.NoDelivery.setText("Cancel No Delivery\nFrom : "+FromDate+" To : "+ToDate);
                                                                }

                                                                else {
                                                                    holder.NoDelivery.setEnabled(false);
                                                                    holder.NoDelivery.setText("No Delivery : "+FromDate+"-"+ToDate);
                                                                }
                                                            }
                                                            catch (ParseException e) {
                                                                e.printStackTrace();
                                                            } catch (java.text.ParseException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    }
                                                });
                                        }
                                    }
                                    else {
                                        amount[0] = "Not set";
                                        holder.cancelSubscription.setVisibility(View.VISIBLE);
                                    }
                                    final double finalNumberOfDays = NumberOfDays;
                                    FStore.collection("Products")
                                            .whereEqualTo("ProductID",ProductUID)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                                {
                                                    double TotalPrice = 0.0;
                                                    for (QueryDocumentSnapshot snapshot1 : Objects.requireNonNull(task.getResult()))
                                                    {
                                                        if (snapshot1.exists())
                                                        {
                                                            String Name = Objects.requireNonNull(snapshot1.get("ProductName")).toString();
                                                            String Quantity =   Objects.requireNonNull(snapshot.get("Quantity")).toString();
                                                            String Price = Objects.requireNonNull(snapshot1.get("Rate")).toString();
                                                            String description = snapshot1.get("Description").toString();
                                                   //         String Quantity =   Objects.requireNonNull(snapshot.get("Quantity")).toString();

                                                            holder.documentID = snapshot.getId();
                                                            ProductDetails details = new ProductDetails(Name,Quantity,Price,description);
                                                            holder.productDetailsArrayList.add(details);
                                                            TotalPrice = TotalPrice+Double.parseDouble(Price);
                                                            holder.Amount.setText("Total Price : "+TotalPrice*Double.parseDouble(Quantity)
                                                                    +"\nAmount By Vendor for "+
                                                                    String.valueOf(finalNumberOfDays).replace(".0","")
                                                                    +" Days : "+ amount[0]);
                                                        }

                                                    }
                                                    final ProductAdapter ProductAdapter = new ProductAdapter(holder.productDetailsArrayList);
                                                    holder.ProductsRecyclerView.setHasFixedSize(true);
                                                    holder.ProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                    holder.ProductsRecyclerView.setAdapter(ProductAdapter);
                                                }
                                            });

                                }

                            }

                        }
                    });


            holder.VendorPhone.setText(customerDetailsArrayList.get(position).getCustomerPhoneNumber());
            //In the place of vendor address textView placing amount
            holder.VendorAddress.setText("Remaining Amount : "+customerDetailsArrayList.get(position).getAmount());
            holder.VendorName.setText(customerDetailsArrayList.get(position).getCustomerName());


            holder.NoDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (isNoDeliveryCancelable[0])
                    {
                        progressDialog.show();
                        //Updating Report part
                        FStore.collection("Reports").document(String.valueOf(VendorUIDArrayList.get(position)))
                                .collection("Customers").document(UID).collection("NoDelivery").document(holder.documentID)
                                .update("NoDelivery","false");

                        //Updating NoDeliveryFrom and To array list
                        List<String> emptyList = new ArrayList<>();
                        SubscriptionRef.document(customerDetailsArrayList.get(position).getSubscriptionID())
                                .update("NoDeliveryFrom",emptyList);
                        SubscriptionRef.document(customerDetailsArrayList.get(position).getSubscriptionID())
                                .update("NoDeliveryTo",emptyList);


                        SubscriptionRef.document(customerDetailsArrayList.get(position).getSubscriptionID())
                                .update("NoDelivery","false")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                requireActivity().recreate();
                            }
                        });
                    }
                    else {
                        final String[] DateRange = new String[1];
                        CalendarConstraints.Builder calenderConstrain = new CalendarConstraints.Builder();
                        Date tomorrow = new Date(System.currentTimeMillis());
                        calenderConstrain.setValidator(DateValidatorPointForward.from(tomorrow.getTime()));

                        final MaterialDatePicker.Builder datepicker = MaterialDatePicker.Builder.dateRangePicker();
                        datepicker.setCalendarConstraints(calenderConstrain.build());

                        datepicker.setTitleText("Select NoDelivery Date's");
                        final MaterialDatePicker DatePicker = datepicker.build();
                        DatePicker.show(getChildFragmentManager(), "From DATE_PICKER");
                        DatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onPositiveButtonClick(Object selection)
                            {
                                DateRange[0] = DatePicker.getHeaderText();
                                requireNonNull(DatePicker.getSelection()).toString();

                                int index = DateRange[0].length()-1;
                                int middleIndex = (index/2)+1;

                                final String fromDate = DateRange[0].substring(0,middleIndex-1).trim();
                                final String toDate = DateRange[0].substring(middleIndex+1).trim();

                                holder.NoDelivery.setText(DateRange[0]);
                                new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("No Delivery")
                                        .setMessage("Are you Sure No Delivery for "+DateRange[0])
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                if (isDateAfter(ChangeDateFormat(toDate),holder.ToDate))
                                                {
                                                    SetDateRange(position,fromDate,toDate,holder.documentID);
                                                }
                                                else {
                                                    Toast.makeText(getContext(), "End No-delivery Date is more than Subscription date", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        });

                    }

                }
            });

            holder.cancelSubscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    progressDialog.show();
                    SubscriptionRef.document(customerDetailsArrayList.get(position).getSubscriptionID())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressDialog.dismiss();
                                    customerDetailsArrayList.remove(position);
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    notifyDataSetChanged();
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        ft.setReorderingAllowed(false);
                                    }
                                    ft.detach(CustomerSubscriptionFragment.this).attach(CustomerSubscriptionFragment.this).commit();
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Can't delete subscription", Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });
                }

            });

        }

        @Override
        public int getItemCount() {
            return customerDetailsArrayList.size();
        }

         class ViewHolder extends RecyclerView.ViewHolder
        {
            RecyclerView ProductsRecyclerView;
            ArrayList<ProductDetails> productDetailsArrayList;
            String documentID ,ToDate;
            TextView VendorName,VendorAddress,VendorPhone,Status,FromTo,Amount;
            Button NoDelivery;
            Button cancelSubscription;
            ViewHolder(@NonNull View itemView)
            {
                super(itemView);
                VendorName = itemView.findViewById(R.id.vendor_name);
                VendorAddress = itemView.findViewById(R.id.vendor_address);
                VendorPhone = itemView.findViewById(R.id.vendor_number);
                FromTo = itemView.findViewById(R.id.FromDate_to_ToDate);
                Amount = itemView.findViewById(R.id.amount);
                Status = itemView.findViewById(R.id.status);
                productDetailsArrayList = new ArrayList<>();
                ProductsRecyclerView = itemView.findViewById(R.id.customer_subscription_products_recycler);
                NoDelivery = itemView.findViewById(R.id.no_delivery_button);
                cancelSubscription = itemView.findViewById(R.id.cancel_pending_subscription);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void SetDateRange(int position, String from, String to, final String DocumentID)
    {
        progressDialog.show();
        progressDialog.setTitle("No Delivery");
        progressDialog.setMessage("Updating...");
        Map<String , Object> NoDeliveryDates = new HashMap<>();
        NoDeliveryDates.put("From",ChangeDateFormat(from));
        NoDeliveryDates.put("To",ChangeDateFormat(to));
        NoDeliveryDates.put("NoDelivery","true");

        FStore.collection("Subscriptions")
                .document(DocumentID)
                .update("NoDeliveryFrom", Collections.singletonList(ChangeDateFormat(from)));

        FStore.collection("Subscriptions")
                .document(DocumentID)
                .update("NoDeliveryTo", Collections.singletonList(ChangeDateFormat(to)));


        FStore.collection("Reports").document(String.valueOf(VendorUIDArrayList.get(position)))
                .collection("Customers").document(UID).collection("NoDelivery").document(DocumentID)
                .set(NoDeliveryDates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        SubscriptionRef.document(DocumentID)
                                .update("NoDelivery","true")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        progressDialog.dismiss();
                                        requireActivity().recreate();
                                    }
                                });
                    }
                });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private String ChangeDateFormat(String date)
    {
        if (date.length() < 8) {
            date = date+" "+Calendar.getInstance().get(Calendar.YEAR);
        }
        String dtStart = date;
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat NewFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date formatDate = null;
        try {
            formatDate = format.parse(dtStart);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return NewFormat.format(formatDate);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isDateAfter(String startDate, String endDate)
    {
        try {
            String myFormatString = "yyyy/MM/dd";
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date endingDate = df.parse(endDate);
            Date startingDate = df.parse(startDate);
            return endingDate.after(startingDate);
        }
        catch (Exception e) {
            return false;
        }
    }
}
