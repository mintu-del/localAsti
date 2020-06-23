package com.organicasti.localasti.customer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.internal.$Gson$Preconditions;
import com.organicasti.localasti.ContactFrag;
import com.organicasti.localasti.ProductDetails;
import com.organicasti.localasti.R;

import com.organicasti.localasti.MainActivity;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Customer_Act extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final String TAG = "TAG";
    private DrawerLayout drawer;
    private FirebaseUser user;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        db = FirebaseFirestore.getInstance();



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, token);

                        // Log and toast


                        db.collection("Users").document(user.getUid())
                                .update("FCMToken", token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });

                    }
                });





        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView1 = (NavigationView) findViewById(R.id.your_nav_view_id);
        //setting your phonenumber to the user name section on top of your navigation bar
        View header = navigationView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.user_num);
        Log.d(TAG,"user panel"+text.getText().toString());
        text.setText(user.getPhoneNumber().toString());


        if(savedInstanceState== null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                    new CustomerFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home_customer);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        //Following lines are to open Subscription Fragment after a Subscription is done
        //This intent is loaded in Delivery.java file
        Intent intent = getIntent();
        if(intent.hasExtra("OpenSubscription") && intent.getStringExtra("OpenSubscription").equals("True") == true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                    new CustomerSubscriptionFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_subscription_customer);
        }
        if(intent.hasExtra("OpenShowOrder") && intent.getStringExtra("OpenShowOrder").equals("True") == true) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                    new CustomerOrderFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_order_customer);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {


            case R.id.nav_home_customer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                        new CustomerFragment()).commit();
                break;

            case R.id.nav_profile_customer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                        new CustomerProfileFragment()).commit();
                break;

            case R.id.nav_order_customer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                        new CustomerOrderFragment()).commit();

                break;
            case R.id.nav_subscription_customer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                        new CustomerSubscriptionFragment()).commit();
                break;

            case R.id.nav_contactus_customer:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_customer,
                        new ContactFrag()).commit();
                break;
            case R.id.nav_logout_customer:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_0,menu);

//        MenuInflater mn = getMenuInflater();
//        mn.inflate(R.menu.main_menu_0,menu);


        //search button on toolbar
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("Type to search");



        String abc="abc Def";
        String def="def";

        Log.d(TAG,"wht tf"+abc.contains("def"));

        Log.d(TAG,"search ");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when we press search button
                Log.d(TAG,"search pressed");
                searchData(s);
                Log.d(TAG,"search data "+s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called as and when we type even a  single letter
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    /*
            search querry

     */

    public void searchData(final String s){

        Log.d(TAG,"search data 2"+s);
        db = FirebaseFirestore.getInstance();
        db.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //when search complete

                        //show data
                        for(DocumentSnapshot doc:task.getResult())
                        {
                            String nm=doc.get("ProductName").toString().toLowerCase();
                            if(nm.equals(s.toLowerCase())){
                                Map<String, Object> m = doc.getData();
                                        m.get("ProductName").toString();
                                        m.get("Quantity").toString();
                                        m.get("Rate").toString();

                                Log.d(TAG, "search DocumentSnapshot product: " + doc.getData());
                            }

//                            Log.d(TAG,"search Name "+nm);
                        }

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Customer_Act.this, "some error", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId())
        {
            case R.id.location:
                Toast.makeText(this, "Location Clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.notification:
                Toast.makeText(this, "Notification Clicked", Toast.LENGTH_SHORT).show();
                Log.d("","Notification clicked");
                return true;
            case R.id.search:
                Log.d("","search clicked");

                return true;
        }
        return true;
    }
}
