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

import com.organicasti.localasti.ItemAdapter;
import com.organicasti.localasti.R;
import com.organicasti.localasti.customer.CustProduct_Subclass;
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

public class Order_OneTime extends Fragment {


    FirebaseFirestore db;
    ItemAdapter itemAdapter;
    CustomerClass customerClass;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Context c;

    String customerfname, customerlname, address, mobilenumber;
    //final Map<String, ArrayList<OrderDataOneTime>> getOrder;
    String prodname;
    double amount;
    List<CustProduct_Subclass> subclass;
    List<SelectedItems> selectedItems;
    List<CustomerClass> obj;
    Button delivery;
    ProgressDialog progressDialog;
    private boolean isLoopEnded = false;
    private static  DateFormat sdf = null;

    public Order_OneTime(Context c) {
        // Required empty public constructor
        this.c = c;
        db = FirebaseFirestore.getInstance();
        // getOrder = new HashMap<String, ArrayList<OrderDataOneTime>>();
        subclass = new ArrayList<CustProduct_Subclass>();
        obj = new ArrayList<CustomerClass>();
        selectedItems = new ArrayList<SelectedItems>();


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_order__one_time, container, false);
        delivery = (Button) rootView.findViewById(R.id.mark_delivery_onetime);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycViewOneTime);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final List<CustomerClass> itemList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Delivery");
        progressDialog.setMessage("Please wait...");

        //Contains list of Customer that this vendor has
        final List<CustomerClass> customerlist = new ArrayList<CustomerClass>();

        //db.collection("Orders_OneTime").whereEqualTo("VendorID", user.getUid()).whereEqualTo("Delivered",false)
           sdf = new SimpleDateFormat("yyyy/MM/dd");
        //To uniquely get every customer
        final Map<String, Object> customerdetails = new HashMap<String, Object>();
        Log.d(TAG, user.getUid());
        db.collection("Orders_OneTime").whereEqualTo("VendorID", user.getUid()).whereEqualTo("Delivered", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Log.d(TAG, doc.getId()+"Onetime");
                                Map<String, Object> m = doc.getData();

                                String key = m.get("CustomerID").toString();
                                if (customerdetails.get(key) == null) {
                                    CustomerClass obj = new CustomerClass(m.get("Name").toString(), m.get("Address").toString(), m.get("MobileNumber").toString(), m.get("CustomerID").toString());
                                    obj.setVendorID(user.getUid());
                                    customerlist.add(obj);
                                    Log.d(TAG, m.get("Name").toString()+"Added");
                                }
                                customerdetails.put(key, new Object());
                            }
                            Log.d(TAG, "Customer Details: "+    customerdetails.size()+"");

                            itemAdapter = new ItemAdapter(customerlist, getContext(), selectedItems);
                            recyclerView.setAdapter(itemAdapter);
                            itemAdapter.notifyDataSetChanged();

                        } else {

                        }
                    }
                });

        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems.size() == 0)
                    Toast.makeText(c, "No item selected", Toast.LENGTH_SHORT).show();
                else {
                    Log.d(TAG, selectedItems.size() + "");
                    Toast.makeText(c, selectedItems.size() + " items delivered", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < selectedItems.size(); i++)
                    {
                        //Log.d(TAG, user.getUid());
                        //Log.d(TAG, selectedItems.get(i).getCustomerID());
                       // Log.d(TAG, selectedItems.get(i).getProductID());
                        final int finalI = i;
                        if (selectedItems.size() == i+1)
                        {
                            isLoopEnded = true;
                        }

                        db.collection("Orders_OneTime").document(selectedItems.get(i).getSubscriptionID()) // Basically ID in One_Time Orders folder in database //
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    final DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                                        db.collection("Products").document(selectedItems.get(finalI).productID)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        progressDialog.show();

                                                        String  price = Objects.requireNonNull(documentSnapshot.get("Rate")).toString();
                                                        String  Quantity = Objects.requireNonNull(doc.get("Quantity")).toString();
                                                        double amount = Double.parseDouble(price)*Double.parseDouble(Quantity);

                                                        UpdateReport(amount,selectedItems.get(finalI).getCustomerID(), finalI);

                                                        Log.d(TAG, doc.getId());
                                                        Map<String, Object> m = new HashMap<String, Object>();
                                                        m.put("Delivered", true);
                                                        Date date = new Date(System.currentTimeMillis());
                                                        String Formatteddate = formatDate(date);
                                                        m.put("DeliveredDate", Formatteddate);

                                                        Log.d(TAG, doc.getId());

                                                        db.collection("Orders_OneTime").document(doc.getId()).update(m);

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
                        /*db.collection("Orders_OneTime").whereEqualTo("VendorID", user.getUid())
                                .whereEqualTo("CustomerID", selectedItems.get(i).getCustomerID())
                                .whereEqualTo("ProductID", selectedItems.get(i).getProductID())
                                .whereEqualTo("Delivered", false)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (final QueryDocumentSnapshot doc : task.getResult())
                                            {
                                            db.collection("Products").document(selectedItems.get(finalI).productID)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                            progressDialog.show();

                                                            String  price = Objects.requireNonNull(documentSnapshot.get("Rate")).toString();
                                                            String  Quantity = Objects.requireNonNull(doc.get("Quantity")).toString();
                                                            double amount = Double.parseDouble(price)*Double.parseDouble(Quantity);

                                                            UpdateReport(amount,selectedItems.get(finalI).getCustomerID(), finalI);

                                                            Log.d(TAG, doc.getId());
                                                            Map<String, Object> m = new HashMap<String, Object>();
                                                            m.put("Delivered", true);
                                                            Log.d(TAG, doc.getId());

                                                            db.collection("Orders_OneTime").document(doc.getId()).update(m);

                                                        }
                                                    });
                                            }

                                        }
                                    }
                                });*/
                    }
                    //Refresh the UI here



                }
            }
        });

        return rootView;



    }

    private void UpdateReport(final double Amount, final String CustomerUID, final int position)
    {
        progressDialog.setMessage("Please wait...");
        final DocumentReference Reports = db.collection("Reports").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Reports.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists())
                            {
                                String AMOUNT = Objects.requireNonNull(snapshot.get("Revenue")).toString();
                                double amount = Double.parseDouble(AMOUNT);
                                amount = amount+Amount;
                                Reports.update("Revenue",String.valueOf(amount));
                                UpdateCustomerReports(String.valueOf(Amount),Reports,CustomerUID,position);
                            }
                            else {
                                Map<String,Object> UpdateReport = new HashMap<>();
                                UpdateReport.put("Revenue",String.valueOf(Amount));
                                UpdateReport.put("VendorUID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Reports.set(UpdateReport);
                                UpdateCustomerReports(String.valueOf(Amount),Reports,CustomerUID,position);
                            }
                        }
                        else {
                            Log.d("Task Ex",task.getException().toString());
                        }

                    }
                });
    }

    private void UpdateCustomerReports(final String Amount, final DocumentReference ReportRef, String CustomerUID, final int position)
    {
        final DocumentReference CustomerReports = ReportRef.collection("Customers").document(CustomerUID);

        CustomerReports.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            Double Revenue = Double.valueOf(Objects.requireNonNull(documentSnapshot.get("Revenue")).toString());
                            CustomerReports
                                    .update("Revenue",String.valueOf(Revenue+Double.parseDouble(Amount)))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                          Reload();
                                        }
                                    });
                        }
                        else {
                            Map<String ,Object> updateCustomerAmount = new HashMap<>();
                            updateCustomerAmount.put("Revenue",Amount);
                            CustomerReports
                                    .set(updateCustomerAmount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Reload();
                                        }
                                    });
                        }
                    }
                });

    }

    private void Reload()
    {
        if (isLoopEnded)
        {
            progressDialog.dismiss();
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(Order_OneTime.this).attach(Order_OneTime.this).commit();
            selectedItems.clear();
        }
    }

    public interface onFragmentInteractionListener {
    }
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }
}