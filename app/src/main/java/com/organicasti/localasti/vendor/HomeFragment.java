package com.organicasti.localasti.vendor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.organicasti.localasti.ProductDetails;
import com.organicasti.localasti.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.organicasti.localasti.vendor.Order_Subscription.sdf;

public class HomeFragment extends Fragment {


    public static final String TAG = "TAG";
    private float amt=0,rt=0,qt=0, monthly_total_amt =0;
    private float oamt=0,ort=0,oqt=0,o_total_amt=0;
    private int pending_order=0, monthly_total_del_order =0,day_total_del_order=0;
    private Date coDate;
    private Date dater;
    private Date today,yesterday;
    private ArrayList<String> ProductName = new ArrayList<>();
    private ArrayList<String> ProductDesc = new ArrayList<>();
    private ArrayList<Integer> Quantity =  new ArrayList<>();
    private ArrayList<String> ProductIDList = new ArrayList<>();

    private ArrayList<ProductDetails> productAndQuantity = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseFirestore db;


    public HomeFragment() {
        //this.Addr=Add;
//        Log.d(TAG,"n ADDRESS Addr"+Addr);
        db = FirebaseFirestore.getInstance();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_home_vend,container,false);
        final TextView  one_day_del=view.findViewById(R.id.delivered_order);
        final TextView total_pend=view.findViewById(R.id.pending_order);

        final TextView one_total_value=view.findViewById(R.id.value_order);
        final TextView monthly_total_value=view.findViewById(R.id.total_value_order);
        final TextView monthly_delivered=view.findViewById(R.id.total_delivered_order);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.Product_and_quantity);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayout);

        //for tomorrow date
        dater = new Date(System.currentTimeMillis()+86400000);
        try {
            dater = sdf.parse(sdf.format(dater));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // for today
        today = new Date(System.currentTimeMillis());
        try {
            today = sdf.parse(sdf.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // for Yesterday
        yesterday = new Date(System.currentTimeMillis()-86400000);
        try {
            yesterday = sdf.parse(sdf.format(yesterday));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //TIME
        final Date currentDate = Calendar.getInstance().getTime();
        Log.d(TAG,"current date "+currentDate);

        //for 1 month earlier
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        final Date oldMonth = calendar.getTime();
        Log.d(TAG,"month date "+oldMonth);

        //one day earlier to present
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        final Date oldDay = calendar.getTime();
        Log.d(TAG,"olld day "+oldDay);


        final Timestamp timestamp = new Timestamp(new Date());

//        Log.d(TAG,"abcd timestap"+timestamp);


        //for loading data......
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        /////

        final FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        db.collection("Orders_OneTime")
                .whereEqualTo("VendorID", Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {


                                //for all here
                                if(Objects.requireNonNull(document.get("Delivered")).toString().equals("false"))
                                {
                                    pending_order++;
                                }else{
                                    //ordered date of user
                                   String date = Objects.requireNonNull(document.get("Date")).toString();
                                    @SuppressLint("SimpleDateFormat")
                                    final DateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd");
                                    Date codate = null;
                                    try {
                                        codate = sdf.parse(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    coDate = codate;
                                    Log.d(TAG,"odate order date "+coDate);

                                    if(coDate.compareTo(oldDay)==1 && coDate.compareTo(currentDate)==-1 )
                                    {
                                        Log.d(TAG,"day total onetime"+day_total_del_order);
                                        day_total_del_order++;
                                        oqt=Float.parseFloat(document.get("Quantity").toString());
                                        ort=Float.parseFloat(document.get("Amount").toString());
                                        oamt=oqt*ort;
                                       o_total_amt = o_total_amt +oamt;

                                    }

                                    //for 30 days
                                    if(coDate.compareTo(oldMonth)==1 && coDate.compareTo(currentDate)==-1 )
                                    {
                                        monthly_total_del_order++;
                                        Log.d(TAG,"1total order "+monthly_total_del_order);

//                                        Log.d(TAG, "milk doc "+document.getId() + " => " + document.getData());
//
//                                        Log.d(TAG,"compare old"+coDate.compareTo(oldMonth));
//                                        Log.d(TAG,"compare new"+coDate.compareTo(currentDate));
                                        qt=Float.parseFloat(document.get("Quantity").toString());
                                        rt=Float.parseFloat(document.get("Amount").toString());
                                        amt=qt*rt;
                                        monthly_total_amt = monthly_total_amt +amt;
                                    }


                                }

//                                Log.d(TAG,"Total amount= "+ monthly_total_amt);
//                                Log.d(TAG,"pending_order "+pending_order);
//                                Log.d(TAG,"monthly_total_del_order "+ monthly_total_del_order);

                                //one day
//                                one_day_del.setText(String.valueOf(day_total_del_order));
//                                one_total_value.setText(String.valueOf(o_total_amt));

                                //monthly
//                                monthly_delivered.setText(String.valueOf(monthly_total_del_order));
//                                monthly_total_value.setText(String.valueOf(monthly_total_amt));

//                                total_pend.setText( String.valueOf(pending_order));

                                Log.d(TAG,"good quantity"+qt);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }


//for subscription in home vendor details

                        db.collection("Subscriptions")
                                .whereEqualTo("VendorUID", fAuth.getCurrentUser().getUid())
                                .whereEqualTo("Status", "Existing")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                    {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                        {
                                            String ProductID = Objects.requireNonNull(snapshot.get("ProductUID")).toString();

                                            final String quantity = Objects.requireNonNull(snapshot.get("Quantity")).toString();
                                            String rate=Objects.requireNonNull(snapshot.get("Rate")).toString();
                                            String amount=Objects.requireNonNull(snapshot.get("Amount")).toString();

                                            String lstdel = Objects.requireNonNull(snapshot.get("LastDelivered")).toString();
                                            String from = Objects.requireNonNull(snapshot.get("From")).toString();
                                            String to = Objects.requireNonNull(snapshot.get("To")).toString();
                                            String noDelivery = Objects.requireNonNull(snapshot.get("NoDelivery")).toString();

                                            try {
                                                Date From = sdf.parse(from);
                                                Date To = sdf.parse(to);
                                                Date LstDel=sdf.parse(lstdel);

                                                assert From != null;
                                                assert To != null;
                                                assert LstDel!=null;

                                                Log.d(TAG,"atodater to"+today+" " +To);
                                                Log.d(TAG,"atodater check "+today.compareTo(To));

                                                //if delivery is for  today or after this day
                                                if (today.compareTo(To) < 1)
                                                {
                                                    if ((today.after(From)) || today.compareTo(From) ==0)
                                                    {
                                                        if (noDelivery.equals("true"))
                                                        {
                                                            List<String> noDeliveryFrom = (List<String>) snapshot.get("NoDeliveryFrom");
                                                            List<String> noDeliveryTo = (List<String>) snapshot.get("NoDeliveryTo");
                                                            boolean donotdeliver = false;
                                                            assert noDeliveryFrom != null;
                                                            for (int i = 0; i < noDeliveryFrom.size(); i++)
                                                            {
                                                                //2 lines move top -(old) under if
                                                                Date start = new Date(0);
                                                                Date end = new Date(0);
                                                                if(noDeliveryFrom.get(i) != null && noDeliveryTo.get(i) != null) {

                                                                    try {
                                                                        start = sdf.parse(noDeliveryFrom.get(i));
                                                                        end = sdf.parse(noDeliveryTo.get(i));
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    Log.d(TAG,"confuse strt "+start+"com "+today.compareTo(start));
                                                                    Log.d(TAG,"confuse end "+end+"com"+today.compareTo(end));

                                                                    if (((today.compareTo(start) > 0) && (today.compareTo(end) > 0)) //start-end before today
                                                                          || ((today.compareTo(start) < 0) && (today.compareTo(end) < 0))) {//start-end after today

                                                                        donotdeliver = true;
                                                                        Log.d(TAG, "Do not deliver and show "+donotdeliver);
                                                                    }
                                                                }
                                                                if (donotdeliver){
                                                                    //if no delivery true and today delivery
                                                                    if(today.compareTo(LstDel)>0){
                                                                        pending_order++;

                                                                        Log.d(TAG,"start end "+start.compareTo(yesterday) +" "+ yesterday.compareTo(end));
                                                                        if(start.compareTo(yesterday)<0 && yesterday.compareTo(end)>0 ||
                                                                                start.compareTo(yesterday)>0 && yesterday.compareTo(end)<0)
                                                                        {
                                                                            if(yesterday.compareTo(LstDel)>0){
                                                                                pending_order++;
                                                                                Log.d(TAG,"start end p"+pending_order);

                                                                            }
                                                                        }
                                                                        long difference, nodeldiff;
                                                                        int nodeliverysub = 0;
                                                                        if (oldMonth.compareTo(From) > 0) {

                                                                            difference = Math.abs(LstDel.getTime() - oldMonth.getTime());
                                                                            Log.d(TAG, "newls lst del - old ");
                                                                        } else {
                                                                            difference = Math.abs(LstDel.getTime() - From.getTime());
                                                                            Log.d(TAG, "newls lst del - from ");
                                                                        }

//                                                                        Log.d(TAG,"newls deiif"+difference);
                                                                        Log.d(TAG, "newls comparison today end " + today + " " + end + " " + today.compareTo(end));

                                                                        if (today.compareTo(end) > 0) {
                                                                            if (oldMonth.compareTo(start) > 0) {

                                                                                nodeldiff = Math.abs(end.getTime() - oldMonth.getTime());
                                                                                Log.d(TAG, "newls nodel lst del - old ");
                                                                            } else {
                                                                                nodeldiff = Math.abs(end.getTime() - start.getTime());
                                                                                Log.d(TAG, "newls nodel lst del - from ");
                                                                            }
                                                                            long differenceDates = (nodeldiff / (24 * 60 * 60 * 1000)) + 1;
                                                                            nodeliverysub = Integer.parseInt(Long.toString(differenceDates));

                                                                        }

                                                                        //this condition if
//                                                                        if(LstDel.compareTo(From)<0 && oldMonth.compareTo(From) < 0) {
//                                                                            pending_order++;
//                                                                        }

                                                                        if (LstDel.compareTo(From) > 0 || LstDel.compareTo(From) == 0) {


                                                                            long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                                            int totala = Integer.parseInt(Long.toString(differenceDates));
                                                                            Log.d(TAG, "newls 1nodeliverysub " + nodeliverysub);

                                                                            int total_order = totala;
                                                                            Log.d(TAG, "newls total" + total_order + " " + totala);


                                                                            float amt = Float.parseFloat(rate) * Float.parseFloat(quantity) * total_order;

                                                                            //in 24 hrs
                                                                            day_total_del_order++;
                                                                            o_total_amt = o_total_amt + amt / total_order;


                                                                            //need to update total delivery in 30 days
                                                                            monthly_total_del_order = monthly_total_del_order + total_order;
                                                                            monthly_total_amt = monthly_total_amt + amt;

                                                                        }
                                                                            Log.d(TAG, "newls \n" + day_total_del_order + "\n" + monthly_total_del_order);

                                                                    }
                                                                    if(today.compareTo(LstDel)==0) {


                                                                        long difference, nodeldiff;
                                                                        int nodeliverysub = 0;
                                                                        if (oldMonth.compareTo(From) > 0) {

                                                                            difference = Math.abs(LstDel.getTime() - oldMonth.getTime());
                                                                            Log.d(TAG, "lst del - old ");
                                                                        } else {
                                                                            difference = Math.abs(LstDel.getTime() - From.getTime());
                                                                            Log.d(TAG, "lst del - from ");
                                                                        }

                                                                        Log.d(TAG, "comparison today end " + today + " " + end + " " + today.compareTo(end));

                                                                        if (today.compareTo(end) > 0) {
                                                                            if (oldMonth.compareTo(start) > 0) {

                                                                                nodeldiff = Math.abs(end.getTime() - oldMonth.getTime());
                                                                                Log.d(TAG, "lst del - old ");
                                                                            } else {
                                                                                nodeldiff = Math.abs(end.getTime() - start.getTime());
                                                                                Log.d(TAG, "lst del - from ");
                                                                            }
                                                                            long differenceDates = (nodeldiff / (24 * 60 * 60 * 1000)) + 1;
                                                                            nodeliverysub = Integer.parseInt(Long.toString(differenceDates));

                                                                        }

                                                                        long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                                        int total = Integer.parseInt(Long.toString(differenceDates));
                                                                        Log.d(TAG, "nodeliverysub " + nodeliverysub);

                                                                        int total_order = total - nodeliverysub;


                                                                        float amt = Float.parseFloat(rate) * Float.parseFloat(quantity) * total_order;

                                                                        //in 24 hrs
                                                                        day_total_del_order++;
                                                                        o_total_amt = o_total_amt + amt / total_order;


                                                                        //need to update total delivery in 30 days
                                                                        monthly_total_del_order = monthly_total_del_order + total_order;
                                                                        monthly_total_amt = monthly_total_amt + amt;


                                                                    }


//
                                                                }else{
                                                                    //no del is true but today no delivery
                                                                    int fcompare=0;

                                                                    Log.d(TAG,"my product"+ProductID);
                                                                    long difference;

                                                                    if(oldMonth.compareTo(From)>0){
                                                                        difference = Math.abs(LstDel.getTime() - oldMonth.getTime());
                                                                        Log.d(TAG,"lst del - old ");
                                                                    }else{
                                                                         difference = Math.abs(LstDel.getTime() - From.getTime());
                                                                        Log.d(TAG,"lst del - from ");
                                                                    }


                                                                    if (LstDel.compareTo(From) > 0 || LstDel.compareTo(From) == 0) {

                                                                        long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                                        int total = Integer.parseInt(Long.toString(differenceDates));
                                                                        int total_order = total;

                                                                        float amt = Float.parseFloat(rate) * Float.parseFloat(quantity) * total_order;
                                                                        //update last 24 hrs delivery and value
                                                                        if (yesterday.compareTo(LstDel) == 0) {
                                                                            day_total_del_order++;
                                                                            o_total_amt = o_total_amt + amt / total_order;

                                                                        }

                                                                        //need to update total delivery in 30 days
                                                                        monthly_total_del_order = monthly_total_del_order + total_order;
                                                                        monthly_total_amt = monthly_total_amt + amt;
                                                                    }
                                                                }

                                                            }
                                                        }
                                                        else {
                                                            //if no delivery is false
                                                            //where today is after "from" and is before "to"

                                                            //to select range- if "subscribe to" was earlier to 30 days on count on anything
                                                            if (oldMonth.compareTo(To) < 1) {

                                                                int fcompare = 0;

                                                                //if "subscribe" from is earlier to 30days subtract extra values
                                                                if (oldMonth.compareTo(From) > 0) {
                                                                    long difference = Math.abs(oldMonth.getTime() - From.getTime());
                                                                    long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                                    fcompare = Integer.parseInt(Long.toString(differenceDates));

                                                                }


                                                                if(LstDel.compareTo(From)<0 && oldMonth.compareTo(From) < 0) {
                                                                    pending_order++;
                                                                }

                                                                if (LstDel.compareTo(From) > 0 || LstDel.compareTo(From) == 0){

                                                                    long difference = Math.abs(LstDel.getTime() - From.getTime());
                                                                    long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                                int total = Integer.parseInt(Long.toString(differenceDates));
                                                                int total_order = total - fcompare;

                                                                float amt = Float.parseFloat(rate) * Float.parseFloat(quantity) * total_order;

                                                                //if last delivered is today
                                                                if (today.compareTo(LstDel) == 0) {
                                                                    day_total_del_order++;
                                                                    o_total_amt = o_total_amt + amt / total_order;
                                                                }
                                                                //if last delivered yesterday and is pending for today
                                                                else if (yesterday.compareTo(LstDel) == 0) {

                                                                    day_total_del_order++;
                                                                    o_total_amt = o_total_amt + amt / total_order;
                                                                    pending_order++;
                                                                }
                                                                else if(yesterday.compareTo(LstDel)<0)
                                                                {
                                                                    pending_order=pending_order+2;
                                                                }

                                                                /*
                                                                //pending order condition
                                                                int fPend=0;
                                                                long pend = Math.abs(today.getTime() - LstDel.getTime());
                                                                long differencePend = (pend / (24 * 60 * 60 * 1000));
                                                                fPend = Integer.parseInt(Long.toString(differencePend));
                                                                Log.d(TAG,"fpend1 "+today.getTime()+" "+LstDel.getTime());
                                                                Log.d(TAG, "fpend " + fPend);
                                                                pending_order=pending_order+fPend;

                                                                /////////////////////////////////////

                                                                 */



                                                                Log.d(TAG, "day total " + day_total_del_order);

                                                                //for total monthly order ,value
                                                                monthly_total_del_order = monthly_total_del_order + total_order;
                                                                monthly_total_amt = monthly_total_amt + amt;

                                                                Log.d(TAG, "d dtae " + ProductID
                                                                        + "total " + total
                                                                        + " torder " + total_order
                                                                        + " fc " + fcompare);
                                                            }

                                                            }
                                                        }
                                                    }

                                                }
                                                //this is if subscription ends earlier -all conditions
                                                else {
                                                    Log.d(TAG,"oldMonth value "+oldMonth+ " "+ To+" "+oldMonth.compareTo(To));

                                                    if (oldMonth.compareTo(To) < 1) {

                                                        int fcompare=0;
                                                        int no_del = 0;

                                                        if (noDelivery.equals("true")) {
                                                            List<String> noDeliveryFrom = (List<String>) snapshot.get("NoDeliveryFrom");
                                                            List<String> noDeliveryTo = (List<String>) snapshot.get("NoDeliveryTo");
                                                            boolean donotdeliver = false;
                                                            assert noDeliveryFrom != null;
                                                            for (int i = 0; i < noDeliveryFrom.size(); i++) {
                                                                if (noDeliveryFrom.get(i) != null && noDeliveryTo.get(i) != null) {
                                                                    Date start = new Date(0);
                                                                    Date end = new Date(0);

                                                                    try {
                                                                        start = sdf.parse(noDeliveryFrom.get(i));
                                                                        end = sdf.parse(noDeliveryTo.get(i));
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
//                                                Log.d(TAG,"strt "+start+" "+end);
                                                                    //for subtracting no delivery days -v
                                                                    long difference = Math.abs(end.getTime() - start.getTime());
                                                                    long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                                    //Convert long to String
                                                                    String dayDifference = Long.toString(differenceDates);
                                                                    no_del = Integer.parseInt(dayDifference);
                                                                    Log.d(TAG, "total no_del " + dayDifference);
                                                                }
                                                            }

                                                        }


                                                        if(oldMonth.compareTo(From)>0){
                                                            long difference = Math.abs(oldMonth.getTime() - From.getTime());
                                                            long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                            fcompare = Integer.parseInt(Long.toString(differenceDates));

                                                        }


                                                        long difference = Math.abs(To.getTime() - From.getTime());
                                                        long differenceDates = (difference / (24 * 60 * 60 * 1000)) + 1;
                                                        int total = Integer.parseInt(Long.toString(differenceDates));
                                                        int total_order = total - no_del-fcompare;

                                                        float amt = Float.parseFloat(rate) * Float.parseFloat(quantity) * total_order;

                                                        //if last day subscription finished
                                                        if (yesterday.compareTo(To) == 0) {
                                                            day_total_del_order++;
                                                            o_total_amt = o_total_amt + amt / total_order;
                                                        }

                                                        Log.d(TAG,"day total "+day_total_del_order);

                                                        //for total monthly order ,value
                                                        monthly_total_del_order = monthly_total_del_order + total_order;
                                                        monthly_total_amt = monthly_total_amt + amt;

//                                    Log.d(TAG,"ttal amt "+amt);
//                                    Log.d(TAG,"ttal full amt "+monthly_total_amt);
//                                    Log.d(TAG,"1total days "+total);
//                                    Log.d(TAG,"1total order "+total_order);
//                                    Log.d(TAG,"1total order mo "+monthly_total_del_order);


                                                    }
                                                }

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        total_pend.setText( String.valueOf(pending_order));

                                        one_day_del.setText(String.valueOf(day_total_del_order));
                                        one_total_value.setText(String.valueOf(o_total_amt));

                                        monthly_delivered.setText(String.valueOf(monthly_total_del_order));
                                        monthly_total_value.setText(String.valueOf(monthly_total_amt));
                                        progressDialog.dismiss();



                                    }
                                });
                    }
                });





        db.collection("Subscriptions")
                .whereEqualTo("VendorUID", fAuth.getCurrentUser().getUid())
                .whereEqualTo("Status", "Existing")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                        {
                            String ProductID = Objects.requireNonNull(snapshot.get("ProductUID")).toString();
                            final String quantity = Objects.requireNonNull(snapshot.get("Quantity")).toString();
                            String from = Objects.requireNonNull(snapshot.get("From")).toString();
                            String to = Objects.requireNonNull(snapshot.get("To")).toString();
                            String noDelivery = Objects.requireNonNull(snapshot.get("NoDelivery")).toString();

                            try {
                                Date From = sdf.parse(from);
                                Date To = sdf.parse(to);
                                assert From != null;
                                assert To != null;
//                                Log.d(TAG,"medater to"+dater+" " +To);
//                                Log.d(TAG,"medater check "+dater.compareTo(To));

                                if (dater.compareTo(To) < 0)
                                {
                                    if ((dater.after(From)) || dater.compareTo(From) ==0)
                                    {
                                        if (noDelivery.equals("true"))
                                        {
                                            List<String> noDeliveryFrom = (List<String>) snapshot.get("NoDeliveryFrom");
                                            List<String> noDeliveryTo = (List<String>) snapshot.get("NoDeliveryTo");
                                            boolean donotdeliver = false;
                                            assert noDeliveryFrom != null;
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
                                                    if ((dater.compareTo(start) < 0) && (dater.compareTo(end) < 0)) {
                                                        donotdeliver = true;
                                                        Log.d(TAG, "Do not deliver and show");
                                                    }
                                                }
                                                if (donotdeliver){
                                                    getData(ProductID,quantity);
                                                }

                                            }
                                        } else {
                                            getData(ProductID,quantity);
                                        }
                                    }

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    progressBar.setVisibility(View.GONE);
                                    for (int i=0;i<ProductIDList.size();i++)
                                    {
                                        productAndQuantity.add(new ProductDetails(
                                                ProductName.get(i) +"\n"+
                                                        ProductDesc.get(i) +"\nQuantity : "+
                                                        Quantity.get(i).toString()));
                                    }
                                    //Toast.makeText(getContext(), ""+productAndQuantity.toString(), Toast.LENGTH_SHORT).show();
                                    ProductAndQuantityAdapter adapter = new ProductAndQuantityAdapter(productAndQuantity);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setHasFixedSize(true);

                                }catch (Exception e){
                                    Log.d(TAG,"List exception",e);
                                }
                            }
                        }, 3000);
                    }
                });



        return view;
        
    }

    private class ProductAndQuantityAdapter extends RecyclerView.Adapter<ProductAndQuantityAdapter.viewHolder>{

        private ArrayList<ProductDetails> productDetailsArrayList;

        ProductAndQuantityAdapter(ArrayList<ProductDetails> productDetailsArrayList) {
            this.productDetailsArrayList = productDetailsArrayList;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_and_quantity_items,parent,false);
            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {

            holder.text.setText(productAndQuantity.get(position).getProductAndQuantity());

        }

        @Override
        public int getItemCount() {
            return productDetailsArrayList.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{
            TextView text ;
            viewHolder(@NonNull View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.Product_and_quantity_text);
            }
        }
    }


    private void getData(String ProductID, final String quantity){
        db.collection("Products").document(ProductID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        String Name = Objects.requireNonNull(documentSnapshot.get("ProductName")).toString();
                        String ProductID = Objects.requireNonNull(documentSnapshot.get("ProductID")).toString();
                        String Decription = Objects.requireNonNull(documentSnapshot.get("Description")).toString();
                        if (ProductIDList.contains(ProductID))
                        {
                            int index =  ProductIDList.indexOf(ProductID);
                            int SumQuantity = Quantity.get(index);
                            SumQuantity = SumQuantity+Integer.parseInt(quantity);
                            Quantity.add(index,SumQuantity);
                            ProductName.add(ProductIDList.indexOf(ProductID),Name);
                            Quantity.add(ProductIDList.indexOf(ProductID), SumQuantity);
                            ProductDesc.add(ProductIDList.indexOf(ProductID),Decription);
                        }
                        else {
                            ProductIDList.add(ProductID);
                            ProductName.add(ProductIDList.indexOf(ProductID),Name);
                            Quantity.add(ProductIDList.indexOf(ProductID), Integer.valueOf(quantity));
                            ProductDesc.add(ProductIDList.indexOf(ProductID),Decription);
                        }
                    }
                });
    }


}
