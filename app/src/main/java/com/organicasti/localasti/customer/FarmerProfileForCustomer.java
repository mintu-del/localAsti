package com.organicasti.localasti.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.organicasti.localasti.R;

import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FarmerProfileForCustomer extends AppCompatActivity {
    FirebaseFirestore db;
    TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_farmer_profile_for_customer);
        Intent intent = getIntent();
        String vendorID = intent.getStringExtra("VendorID");
        name = findViewById(R.id.farmer_profile_name);
        final RecyclerView rv = (RecyclerView)findViewById(R.id.certificate_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);


        db.collection("Users").document(vendorID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> m = document.getData();
                        name.setText(m.get("FirstName") +" "+ m.get("LastName"));
                        List<String> certificateTypes = (List<String>)document.get("Certificate");
                        List<String> certificateNo = (List<String>)document.get("Certificate No");
                        List<String> daysofDelivery = (List<String>)document.get("DeliveryDays");
                        for(int i=0; i<daysofDelivery.size(); i++) {
                            TextView days;
                            switch(daysofDelivery.get(i)) {
                                case "sunday":
                                    days = findViewById(R.id.day_sun);
                                    days.setVisibility(View.VISIBLE);
                                    break;
                                case "monday":
                                    days = findViewById(R.id.day_mon);
                                    days.setVisibility(View.VISIBLE);
                                    break;
                                case "tuesday":
                                    days = findViewById(R.id.day_tue);
                                    days.setVisibility(View.VISIBLE);
                                    break;
                                case "wednesday":
                                    days = findViewById(R.id.day_wed);
                                    days.setVisibility(View.VISIBLE);
                                    break;
                                case "thursday":
                                    days = findViewById(R.id.day_thu);
                                    days.setVisibility(View.VISIBLE);
                                    break;
                                case "friday":
                                    days = findViewById(R.id.day_fri);
                                    days.setVisibility(View.VISIBLE);
                                    break;
                                case "saturday":
                                    days = findViewById(R.id.day_sat);
                                    days.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                        rv.setAdapter(new FarmerCertificateAdapter(certificateTypes, certificateNo, FarmerProfileForCustomer.this));


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




    }
}
