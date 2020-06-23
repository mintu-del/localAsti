package com.organicasti.localasti;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Manage_Locations_Admin extends AppCompatActivity {
    FirebaseFirestore db;
    ArrayList<String> data;
    Button addProduct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_manage__locations__admin);
        addProduct = (Button)findViewById(R.id.location_add);
        final RecyclerView rv = (RecyclerView)findViewById(R.id.locationRecyclerView);


        db.collection("Locations").document("QOeRVXqobfW3ZUeR9K9y") // Change this name when you change the document name
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        data = (ArrayList<String>)document.get("location");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Manage_Locations_Admin.this);
                    rv.setLayoutManager(linearLayoutManager);
                    LocationAdapter locationAdapter = new LocationAdapter(data, Manage_Locations_Admin.this);
                    locationAdapter.notifyDataSetChanged();
                    rv.setAdapter(locationAdapter);

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Manage_Locations_Admin.this);
                builder.setTitle("Add Product");
                View viewInflated = LayoutInflater.from(Manage_Locations_Admin.this).inflate(R.layout.add_product_vendor, (ViewGroup) findViewById(android.R.id.content), false);
                final LinearLayout l1 = (LinearLayout)viewInflated.findViewById(R.id.linearlayoutproductQuantity);
                final LinearLayout l2 = (LinearLayout)viewInflated.findViewById(R.id.linearlayoutproductRate);
                final LinearLayout l3 = (LinearLayout)viewInflated.findViewById(R.id.linearlayoutproductDesc);
                TextView nameleft = (TextView)viewInflated.findViewById(R.id.productnametextview);
                nameleft.setText("Location Name");
                l1.setVisibility(View.GONE);
                l2.setVisibility(View.GONE);
                l3.setVisibility(View.GONE);
                final EditText name = (EditText) viewInflated.findViewById(R.id.product_name_add);

                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String locationname = name.getText().toString();
                        dialog.dismiss();
                        if(locationname.equals("") == false) {
                            db.collection("Locations").document("QOeRVXqobfW3ZUeR9K9y").update("location", FieldValue.arrayUnion(locationname));
                            data.add(locationname);
                            LocationAdapter locationAdapter = new LocationAdapter(data, Manage_Locations_Admin.this);
                            locationAdapter.notifyDataSetChanged();
                            Toast.makeText(Manage_Locations_Admin.this, locationname+" added !", Toast.LENGTH_SHORT).show();
                            rv.setAdapter(locationAdapter);
                        }
                        else {
                            Toast.makeText(Manage_Locations_Admin.this, "Enter Valid Location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

    }
}
