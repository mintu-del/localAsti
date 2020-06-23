package com.organicasti.localasti.customer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.organicasti.localasti.ProductDetails;
import com.organicasti.localasti.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Delivery extends AppCompatActivity {

    private Button fromDate;
    private Button toDate;
    FirebaseFirestore db;
    int fromday, frommonth,fromyear;
    Date fromdater,todater;
    int today, tomonth,toyear;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    ProgressDialog progressDialog;
    LinearLayout linearLayout;
    String ProductPrice, ProductQuantity;
    TextView noofdays,priceperday, totalprice;
    TextView totalPriceOneTime;
    double total;
    LinearLayout contentlinearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        db = FirebaseFirestore.getInstance();
        RecyclerView rv = (RecyclerView)findViewById(R.id.delivery_recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        totalPriceOneTime = findViewById(R.id.total_amount_onetime);
        contentlinearlayout = findViewById(R.id.delivery_page_content);






        ArrayList<ProductDetails> productDetails = new ArrayList<>();
        //TextView productname = findViewById(R.id.product_name);
        //TextView productprice = findViewById(R.id.product_price);
        //TextView productquantity = findViewById(R.id.product_quantity);
        //final TextView vendorname = findViewById(R.id.Vendor_name);
        linearLayout = findViewById(R.id.order_summary);
        noofdays = findViewById(R.id.noofdays);
        priceperday = findViewById(R.id.amount_oneday);
        totalprice = findViewById(R.id.amounttotal);
        linearLayout.setVisibility(View.GONE);

        final TextView onetime = findViewById(R.id.oneTimeDeliveryNote);
        final LinearLayout subscriptionLayout = findViewById(R.id.Subscription_layout);
        final LinearLayout oneTimeOrderSummary = findViewById(R.id.onetime_totalPrice);
        fromDate = findViewById(R.id.from_button);
        toDate = findViewById(R.id.to_button);
        Button aContinue = findViewById(R.id.done);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait...");

       Intent intent = getIntent();
       final String Type = intent.getStringExtra("Type");
       final String Cname=intent.getStringExtra("Name");
       final String Address = intent.getStringExtra("Address");
       final String CustomerId = intent.getStringExtra("CustomerId");
       final String MobileNumber = intent.getStringExtra("MobileNumber");
       final String location  = intent.getStringExtra("Location");



       //for multiple products
        final ArrayList<String> mname = (ArrayList<String>) getIntent().getSerializableExtra("mProductName");
        final ArrayList<String> mQuantity = (ArrayList<String>) getIntent().getSerializableExtra("mQuantity");
        final ArrayList<String> mPrice = (ArrayList<String>) getIntent().getSerializableExtra("mPrice");
        final ArrayList<String> mVendorId = (ArrayList<String>) getIntent().getSerializableExtra("mVendorId");
        final ArrayList<String> mDesc = (ArrayList<String>) getIntent().getSerializableExtra("mDesc");
        final ArrayList<String> mProductId = (ArrayList<String>) getIntent().getSerializableExtra("mProductId");

        final boolean orderPlaced[] = new boolean[mname.size()];
        for(int i=0; i<mname.size(); i++) orderPlaced[i] = false;

        total = 0;
        for(int i=0; i<mname.size(); i++) {
            total += Double.parseDouble(Double.toString(Integer.parseInt(mQuantity.get(i))*Double.parseDouble(mPrice.get(i))));
        }
        totalPriceOneTime.setText(Double.toString(total));

        DeliveryOrderAdapter deliveryOrderAdapter = new DeliveryOrderAdapter(mname, mQuantity,mPrice,mVendorId,mDesc,mProductId);
        rv.setAdapter(deliveryOrderAdapter);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(true);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        fromDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v)
            {
                DatePicker(fromDate, "From");
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v)
            {
                DatePicker(toDate, "To");
            }
        });

        aContinue.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v)
            {
                progressDialog.setMessage("Placing Order");

                assert Type != null;
                if (!Type.equals("One Time"))
                {
                    if(fromdater != null && todater != null ) {
                        Log.d(TAG, fromdater.toString());
                        Log.d(TAG,todater.toString()+"aah");
                        Date date = new Date();
                        date.setTime(0);
                        
                        Log.d(TAG,date.toString());

                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        contentlinearlayout.setAlpha((float)0.15);
                        for(int i=0; i<mname.size(); i++) {
                            final int varI = i;
                            final double v2 = Double.parseDouble(mPrice.get(i)) * Integer.parseInt(mQuantity.get(i));
                            Map<String, Object> subscription = new HashMap<String, Object>();
                            subscription.clear();
                            subscription.put("Rate", mPrice.get(i)); //Price per Unity
                            subscription.put("Quantity", mQuantity.get(i));
                            subscription.put("Amount", Double.toString(v2));
                            subscription.put("CustomerUID", CustomerId);
                            subscription.put("VendorUID", mVendorId.get(i));
                            subscription.put("NoDelivery", "false");
                            subscription.put("LastDelivered", sdf.format(date));
                            subscription.put("Status", "Pending");
                            subscription.put("From", sdf.format(fromdater));
                            subscription.put("To", sdf.format(todater));
                            subscription.put("ProductUID", mProductId.get(i));
                            subscription.put("Location", location); //Used for Admin
                            Log.d(TAG, "Hello");
                            List<String> sampleList = new ArrayList<>(); // Inserting Empty Non_delivery Dates
                            subscription.put("NoDeliveryFrom", sampleList);
                            subscription.put("NoDeliveryTo", sampleList);
                            final String pid = mProductId.get(i);
                            final String pquant = mQuantity.get(i);
                            final int index =i;
                            // UpdateCustomerReports();
                            if(orderPlaced[varI] == false) {
                                orderPlaced[varI] = true;
                                db.collection("Subscriptions")
                                        .add(subscription)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                //Toast.makeText(Delivery.this, "Order Placed !", Toast.LENGTH_SHORT).show();
                                                //finish();



                                                Map<String, Object> quantity = new HashMap<>();
                                                quantity.put("Quantity", pquant);
                                                db.collection("Subscriptions")
                                                        .document(documentReference.getId())
                                                        .collection("Products")
                                                        .document(pid)
                                                        .set(quantity)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                /////////////////////////////////////////////////////////////////
                                                                if (index == mname.size() - 1) {
                                                                    progressDialog.dismiss();
                                                                    progressDialog.setCancelable(true);
                                                                    progressDialog.setMessage("Please wait...");
                                                                    Intent intentc = new Intent(getApplicationContext(), Customer_Act.class);
                                                                    intentc.putExtra("OpenSubscription", "True");
                                                                    startActivity(intentc);
                                                                    finish();
                                                                }
                                                                /////////////////////////////////////////////////////////////////

                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                progressDialog.setCancelable(true);
                                                progressDialog.setMessage("Please wait...");
                                                Toast.makeText(Delivery.this, "Couldn't place some orders", Toast.LENGTH_SHORT).show();

                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                        }


                    }

                    else {
                        Toast.makeText(Delivery.this, "Please enter correct dates", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    contentlinearlayout.setAlpha((float)0.15);

                    for( int i=0; i<mname.size(); i = i+1) {
                        Map<String, Object> m = new HashMap<>();
                        m.clear();
                        m.put("VendorID", mVendorId.get(i));
                        m.put("Quantity", mQuantity.get(i));
                        m.put("ProductName", mname.get(i));
                        m.put("ProductID", mProductId.get(i));
                        m.put("Amount", mPrice.get(i));
                        m.put("Description", mDesc.get(i));
                        m.put("Name", Cname);
                        m.put("MobileNumber", MobileNumber);
                        m.put("Delivered", false);
                        m.put("CustomerID", CustomerId);
                        m.put("Address", Address);
                        m.put("Location", location);
                        Date date = new Date(System.currentTimeMillis());
                        m.put("Date", sdf.format(date));
                        final double v2 = Double.parseDouble(mPrice.get(i)) * Integer.parseInt(mQuantity.get(i));

                        final String pid = mProductId.get(i);
                        final String pquant = mQuantity.get(i);
                        final int index = i;

                        if (orderPlaced[index] == false) {
                            orderPlaced[index] = true;


                            db.collection("Orders_OneTime")
                                    .add(m)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                            db.collection("Products").document(pid)
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    String inventory = "0";
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Map<String, Object> m = document.getData();
                                                            inventory = m.get("Quantity").toString();
                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                        Map<String, Object> q = new HashMap<>();
                                                        q.put("Quantity", Integer.parseInt(inventory) - Integer.parseInt(pquant));
                                                        db.collection("Products").document(pid).update(q);
                                                        if (index == mname.size() - 1) {
                                                            progressDialog.dismiss();
                                                            progressDialog.setCancelable(true);
                                                            progressDialog.setMessage("Please wait...");

                                                            Toast.makeText(Delivery.this, "Order Placed !", Toast.LENGTH_SHORT).show();
                                                            Intent intentc = new Intent(getApplicationContext(), Customer_Act.class);
                                                            intentc.putExtra("OpenShowOrder", "True");
                                                            startActivity(intentc);
                                                            finish();
                                                        }

                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            progressDialog.dismiss();
                                            progressDialog.setMessage("Please wait...");
                                        }
                                    });
                        }
                    }
                }


            }
        });




        //productname.setText(ProductName);
        //productprice.setText(ProductPrice);
        //productquantity.setText(ProductQuantity);

        /*
        Getting type of order is one type or subscription
         */
        if (!Type.equals("One Time"))
        {
            onetime.setVisibility(View.GONE);
            oneTimeOrderSummary.setVisibility(View.GONE);
        }
        else {
            subscriptionLayout.setVisibility(View.GONE);
        }



    }




    //Date picker for selecting from date to to date
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void DatePicker(final Button button, String type)
    {

        if(type.equals("From"))
        {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            button.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                            fromday = dayOfMonth;
                            frommonth = month;
                            fromyear = year;
                            fromdater = new GregorianCalendar(fromyear, frommonth, fromday).getTime();
                            if(fromdater != null && todater != null) {
                                double NumberOfDays = (double) ((todater.getTime() - fromdater.getTime()) / (1000 * 60 * 60 * 24));
                                noofdays.setText(Integer.toString((int) NumberOfDays));
                                priceperday.setText(Double.toString(total));
                                totalprice.setText(Double.toString((int)NumberOfDays *total));
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000+24*60*60*1000);
            datePickerDialog.show();
        }
        else {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            button.setText(dayOfMonth + "/" + (month+1) + "/" + year);

                            today = dayOfMonth;
                            tomonth = month;
                            toyear = year;
                            todater = new GregorianCalendar(toyear, tomonth, today).getTime();
                            if(fromdater != null && todater != null) {
                                double NumberOfDays = (double) (((todater.getTime() - fromdater.getTime()) / (1000 * 60 * 60 * 24))+1);
                                noofdays.setText(Integer.toString((int) NumberOfDays));
                                priceperday.setText(Double.toString(total));
                                totalprice.setText(Double.toString((int)NumberOfDays * total));
                                linearLayout.setVisibility(View.VISIBLE);
                            }




                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-100+24*60*60*1000);
            datePickerDialog.show();
        }
    }

}
