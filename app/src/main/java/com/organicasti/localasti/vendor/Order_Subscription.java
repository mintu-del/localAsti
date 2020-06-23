package com.organicasti.localasti.vendor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.organicasti.localasti.ItemAdapterSubscription;
import com.organicasti.localasti.R;
import com.organicasti.localasti.customer.CustomerClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Order_Subscription extends Fragment {
    public static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    FirebaseFirestore db;
    ItemAdapterSubscription itemAdapter;
    CustomerClass customerClass;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<SelectedItems> selectedItems;
    Button delivery;
    Context c;
    Date dater;
    private ProgressDialog progressDialog;


    public Order_Subscription(Context c) {
        this.c = c;
        db = FirebaseFirestore.getInstance();
        selectedItems = new ArrayList<SelectedItems>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_order__subscription_0, container, false);
        delivery = (Button)rootView.findViewById(R.id.mark_delivery_subscription);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recycViewSubscription);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        final List<CustomerClass> customerlist = new ArrayList<CustomerClass>();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Delivery");

        Log.d(TAG, "Helo");
        final Map<String, Object> uniqueRelevantCustomers = new HashMap<>();
        dater = new Date(System.currentTimeMillis());
        try {
            dater = sdf.parse(sdf.format(dater));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        db.collection("Subscriptions").whereEqualTo("VendorUID", user.getUid()).whereEqualTo("Status", "Existing")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "Inside");
                            for(QueryDocumentSnapshot doc: task.getResult()) {

                                final Map<String, Object> m = doc.getData();
                                Date frmDate = new Date(0);
                                Date toDate = new Date(0);
                                Date lastDelivered = new Date(0);
                                //Log.d(TAG, todayTimestamp.compareTo((Timestamp)m.get("From"))+"");
                                //Log.d(TAG, todayTimestamp.compareTo((Timestamp)m.get("To"))+"");
                                try {
                                    frmDate = sdf.parse(m.get("From").toString());
                                    toDate = sdf.parse(m.get("To").toString());
                                    lastDelivered = sdf.parse(m.get("LastDelivered").toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if(dater.compareTo(frmDate) >= 0 && dater.compareTo(toDate) <= 0 && dater.compareTo(lastDelivered) == 1) {
                                    Log.d(TAG,"retrieving list now");
                                    List<String> noDeliveryFrom = (List<String>) doc.get("NoDeliveryFrom");
                                    List<String> noDeliveryTo = (List<String>) doc.get("NoDeliveryTo");
                                    boolean donotdeliver = false;

                                    for (int i = 0; i < noDeliveryFrom.size(); i++) {
                                        if(noDeliveryFrom.get(i) != null && noDeliveryTo.get(i) != null) {
                                            Date start = new Date(0);
                                            Date end = new Date(0);

                                            try {
                                                start = sdf.parse(noDeliveryFrom.get(i));
                                                end = sdf.parse(noDeliveryTo.get(i));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            if ((dater.compareTo(start) >= 0) && (dater.compareTo(end) <= 0)) {
                                                donotdeliver = true;
                                                Log.d(TAG, "Do not deliver and show");
                                            }
                                        }
                                    }

                                    if (donotdeliver == false) {

                                        String key = m.get("CustomerUID").toString();
                                        //Log.d(TAG, "outside");
                                        if (uniqueRelevantCustomers.get(key) == null) {
                                            final DocumentReference docRef = db.collection("Users").document(key);

                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Log.d(TAG, "DocumentSnapshot data:123 " + document.getData());
                                                            Map<String, Object> customerMap = document.getData();
                                                            customerlist.add(new CustomerClass(customerMap.get("FirstName").toString() + " " + customerMap.get("LastName").toString(), customerMap.get("Address").toString(), customerMap.get("MobileNumber").toString(), docRef.getId()));
                                                            uniqueRelevantCustomers.put(docRef.getId(), new Object());
                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                        itemAdapter = new ItemAdapterSubscription(customerlist, getContext(), selectedItems);
                                                        recyclerView.setAdapter(itemAdapter);
                                                        itemAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });

                                        }
                                        uniqueRelevantCustomers.put(key, new Object());

                                    }
                                }

                            }
                        }
                    }
                });


        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clicked");
                if (selectedItems.size() == 0)
                    Toast.makeText(c, "No item selected", Toast.LENGTH_SHORT).show();
                else {
                    Log.d(TAG, selectedItems.size() + "");
                    Toast.makeText(c, selectedItems.size() + " items delivered", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        final int finalI = i;

                        db.collection("Subscriptions").document(selectedItems.get(i).getSubscriptionID())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                                        final Map<String,Object> updateMap = new HashMap<>();
                                        Date date = new Date(System.currentTimeMillis());
                                        updateMap.put("LastDelivered", sdf.format(date));
                                        final String SubscriptionID = doc.getId();
                                        db.collection("Subscriptions").document(SubscriptionID).collection("Products")
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                                    {
                                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                                        {
                                                            db.collection("Products").document(snapshot.getId())
                                                                    .get()
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                                        @Override
                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                            String  price = Objects.requireNonNull(documentSnapshot.get("Rate")).toString();
                                                                            String  Quantity = Objects.requireNonNull(doc.get("Quantity")).toString();
                                                                            double amount = Double.parseDouble(price)*Double.parseDouble(Quantity);
                                                                            Toast.makeText(getContext(), ""+amount, Toast.LENGTH_SHORT).show();

                                                                            UpdateAmount(amount,SubscriptionID);

                                                                            UpdateReport(amount,selectedItems.get(finalI).getCustomerID());
                                                                            db.collection("Subscriptions").document(doc.getId()).update(updateMap);


                                                                        }
                                                                    });

                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                        /*db.collection("Subscriptions").whereEqualTo("VendorUID",
                                user.getUid()).whereEqualTo("Status", "Existing")
                                .whereEqualTo("CustomerUID", selectedItems.get(i).getCustomerID())
                                .whereEqualTo("ProductUID", selectedItems.get(i).getProductID())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        Log.d(TAG, "Button pressed1");
                                        if (task.isSuccessful()) {
                                            for (final QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                                Log.d(TAG, doc.getId());

                                                Map<String, Object> m = doc.getData();
                                                Date lastdelivered = new Date(0);
                                                Date frmDate  = new Date(0);
                                                Date toDate  = new Date(0);
                                                try {
                                                     lastdelivered = sdf.parse(Objects.requireNonNull(doc.get("LastDelivered")).toString());
                                                     frmDate = sdf.parse(Objects.requireNonNull(m.get("From")).toString());
                                                     toDate = sdf.parse(Objects.requireNonNull(m.get("To")).toString());
                                                     dater = sdf.parse(sdf.format(new Date(System.currentTimeMillis())));
                                                     Log.d(TAG, dater.toString());
                                                     Log.d(TAG,dater.compareTo(frmDate)+"");
                                                    Log.d(TAG,dater.compareTo(toDate)+"");
                                                    Log.d(TAG,dater.compareTo(lastdelivered)+"");


                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                if(dater.compareTo(frmDate) >= 0 && dater.compareTo(toDate) <= 0 && dater.compareTo(lastdelivered) == 1) {
                                                    Log.d(TAG, "Button pressed3");
                                                    List<String> noDeliveryFrom = (List<String>) doc.get("NoDeliveryFrom");
                                                    List<String> noDeliveryTo = (List<String>) doc.get("NoDeliveryTo");
                                                    boolean donotdeliver = false;

                                                    for (int i = 0; i < noDeliveryFrom.size(); i++) {
                                                        if(noDeliveryFrom.get(i) != null && noDeliveryTo.get(i) != null) {
                                                            Date start = new Date(0);
                                                            Date end = new Date(0);

                                                            try {
                                                                start = sdf.parse(noDeliveryFrom.get(i));
                                                                end = sdf.parse(noDeliveryTo.get(i));
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                            if ((dater.compareTo(start) >= 0) && (dater.compareTo(end) <= 0)) {
                                                                donotdeliver = true;
                                                                Log.d(TAG, "Do not Deliver!");
                                                            }
                                                        }
                                                    }

                                                    if (donotdeliver == false) {
                                                        //Change last delivered Date
                                                        final Map<String,Object> updateMap = new HashMap<>();
                                                        Date date = new Date(System.currentTimeMillis());
                                                        updateMap.put("LastDelivered", sdf.format(date));
                                                        final String SubscriptionID = doc.getId();
                                                        db.collection("Subscriptions").document(SubscriptionID).collection("Products")
                                                                .get()
                                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                                                    {
                                                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                                                        {
                                                                            db.collection("Products").document(snapshot.getId())
                                                                                    .get()
                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                                                        @Override
                                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                            String  price = Objects.requireNonNull(documentSnapshot.get("Rate")).toString();
                                                                                            String  Quantity = Objects.requireNonNull(doc.get("Quantity")).toString();
                                                                                            double amount = Double.parseDouble(price)*Double.parseDouble(Quantity);
                                                                                            Toast.makeText(getContext(), ""+amount, Toast.LENGTH_SHORT).show();

                                                                                            UpdateAmount(amount,SubscriptionID);

                                                                                            UpdateReport(amount,selectedItems.get(finalI).getCustomerID());
                                                                                            db.collection("Subscriptions").document(doc.getId()).update(updateMap);


                                                                                        }
                                                                                    });

                                                                        }
                                                                    }
                                                                });

                                                    }
                                                }

                                            }

                                        }
                                    }
                                }); */
                    }
                    //Refresh the UI here



                }
            }
        });



        return rootView;
    }


    private void UpdateReport(final double Amount, final String CustomerUID)
    {
        final DocumentReference Reports = db.collection("Reports").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Reports.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot snapshot = task.getResult();
                            assert snapshot != null;
                            if (snapshot.exists())
                            {
                                String AMOUNT = Objects.requireNonNull(snapshot.get("Revenue")).toString();
                                double amount = Double.parseDouble(AMOUNT);
                                amount = amount+Amount;
                                Reports.update("Revenue",String.valueOf(amount));
                                UpdateCustomerReports(String.valueOf(Amount),Reports,CustomerUID);
                            }
                            else {
                                Map<String,Object> UpdateReport = new HashMap<>();
                                UpdateReport.put("Revenue",String.valueOf(Amount));
                                UpdateReport.put("VendorUID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Reports.set(UpdateReport);
                                UpdateCustomerReports(String.valueOf(Amount),Reports,CustomerUID);
                            }
                        }
                        else {
                            Log.d("Task Ex",task.getException().toString());
                        }

                    }
                });
    }

    private void UpdateCustomerReports(final String Amount, DocumentReference ReportRef, String CustomerUID)
    {
        final DocumentReference CustomerReports = ReportRef.collection("Customers").document(CustomerUID);

        CustomerReports.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            Double Revenue = Double.valueOf(documentSnapshot.get("Revenue").toString());
                            Revenue = Revenue+Double.parseDouble(Amount);
                            CustomerReports.update("Revenue",String.valueOf(Revenue))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                                            if (Build.VERSION.SDK_INT >= 26) {
                                                ft.setReorderingAllowed(false);
                                            }
                                            ft.detach(Order_Subscription.this).attach(Order_Subscription.this).commit();
                                            selectedItems.clear();
                                        }
                                    });
                        }
                        else {
                            Map<String ,Object> updateCustomerAmount = new HashMap<>();
                            updateCustomerAmount.put("Revenue",Amount);
                            CustomerReports.set(updateCustomerAmount).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        ft.setReorderingAllowed(false);
                                    }
                                    ft.detach(Order_Subscription.this).attach(Order_Subscription.this).commit();
                                    selectedItems.clear();
                                }
                            });
                        }
                    }
                });

    }

    private void UpdateAmount(final double amount, final String SubscriptionID)
    {
        db.collection("Subscriptions").document(SubscriptionID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String Amount = Objects.requireNonNull(documentSnapshot.get("Amount")).toString();
                        double FinalAMount = Double.parseDouble(Amount);
                        db.collection("Subscriptions").document(SubscriptionID)
                                .update("Amount",String.valueOf(FinalAMount-amount));
                    }
                });

    }


    public interface onFragmentInteractionListener {
    }

}