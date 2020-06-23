package com.organicasti.localasti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.organicasti.localasti.customer.Customer_Act;
import com.organicasti.localasti.vendor.Vendor_Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class UserDetails extends AppCompatActivity {

    EditText uid,firstName,lastName,email,password,conformPassword,address;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    RadioGroup userType,userAdd;
    Spinner locality,area;

    private FirebaseFirestore DB;
    ArrayList<String> loc,cities;
    ArrayList<String> area_list;

    ArrayAdapter<String> adapt;
    ArrayList<String> spinnerDataList;

    ArrayAdapter<String> adapt1;
    ArrayList<String> spinnerDataList1;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailAddress);
        saveBtn = findViewById(R.id.saveBtn);
//        password = findViewById(R.id.password);
//        conformPassword = findViewById(R.id.conform_password);
        userType = findViewById(R.id.category);
        address = findViewById(R.id.address);
        uid=findViewById(R.id.uid);

        locality = (Spinner)findViewById(R.id.locality_user);
        area = (Spinner)findViewById(R.id.area_user);




        spinnerDataList=new ArrayList<>();
        spinnerDataList1=new ArrayList<>();

        DB = FirebaseFirestore.getInstance();
        final CollectionReference Loc_Ref = DB.collection("Locations");

        /*when need to set for according to area


        cities = new ArrayList<>();
        DocumentReference codesRef = DB.collection("Locations").document("OHKF0FpUF2E5GyD8fFKN");
        codesRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    Map<String, Object> map = task.getResult().getData();
                    Log.d("TAGA","map "+map.toString());
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        cities.add(entry.getKey());

                        Log.d("TAGA", "cities are in"+cities);
                        Log.d("TAGA","valye "+entry.getValue());

                    }
                    //Do what you want to do with your list
                }
            }
        });

        Log.d("TAGA", "cities are "+cities);
*/




        Loc_Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    loc=(ArrayList<String>)snapshot.get("location");
                    if(loc!=null){

                        for(int i=0;i<loc.size();i++){
                            Log.d("f","vedd loc "+loc.get(i));
                            spinnerDataList.add(loc.get(i));
                        }
                    }
                }
                adapt.notifyDataSetChanged();
            }
        });
        adapt=new ArrayAdapter<String>(UserDetails.this,
                        android.R.layout.simple_spinner_dropdown_item,spinnerDataList);

        locality.setAdapter(adapt);





        locality.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                final String area_name=locality.getSelectedItem().toString();
                Log.d("f","spinner location"+area_name);

                Loc_Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                        {
                            spinnerDataList1.clear();
                            area_list=(ArrayList<String>)snapshot.get(area_name);
                            if(area_list!=null){

                                for(int i=0;i<area_list.size();i++){
                                    Log.d("f","vedd loc "+area_list.get(i));
                                    spinnerDataList1.add(area_list.get(i));

                                }
                            }
                        }
                        adapt1.notifyDataSetChanged();
                    }
                });
                Log.d("f","area list"+spinnerDataList1);

                    adapt1=new ArrayAdapter<String>(UserDetails.this,
                            android.R.layout.simple_spinner_dropdown_item, spinnerDataList1);
                    Log.d("f","area list 0"+spinnerDataList1);


                area.setAdapter(adapt1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        adapt1=new ArrayAdapter<String>(UserDetails.this,
//                android.R.layout.simple_spinner_dropdown_item,spinnerDataList1);
//
//
//        area.setAdapter(adapt1);



      /*
        final CollectionReference Loc_Ref = DB.collection("Locations");

        Loc_Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    loc=(ArrayList<String>)snapshot.get("location");
                    if(loc!=null){

                        for(int i=0;i<loc.size();i++){
                                    Log.d("f","vedd loc "+loc.get(i));
                            spinnerDataList.add(loc.get(i));
                        }
                    }
                }
                adapt.notifyDataSetChanged();
            }
        });

        final CollectionReference Area_Ref = DB.collection("Locations");

        Loc_Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    area_list=(ArrayList<String>)snapshot.get("area");
                    if(area_list!=null){

                        for(int i=0;i<area_list.size();i++){
                            Log.d("f","vedd loc "+area_list.get(i));
                            spinnerDataList1.add(area_list.get(i));
                        }
                    }
                }
                adapt1.notifyDataSetChanged();
            }
        });
*/



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        //terms and condition part
        final CheckBox terms=(CheckBox)findViewById(R.id.terms);
        final TextView terms2=(TextView)findViewById(R.id.terms2);

        //linking for checkbox-terms and textview linkterms
        final TextView link=(TextView)findViewById(R.id.linkterms);
        link.setMovementMethod(LinkMovementMethod.getInstance());

        ///////////////////////////


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!firstName.getText().toString().isEmpty()&& !lastName.getText().toString().isEmpty() &&
//                        !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()
//                        && !conformPassword.getText().toString().isEmpty() &&
                        !address.getText().toString().isEmpty()) {
//                    if (password.getText().toString().equals(conformPassword.getText().toString()) && password.length() > 6)
//                    {
                    if (terms.isChecked() == true) {
                        final ProgressDialog progressDialog = new ProgressDialog(UserDetails.this);
                        progressDialog.show();
                        progressDialog.setMessage("Updating...");

                        DocumentReference docRef = fStore.collection("Users").document(userID);

                        Map<String,Object> user = new HashMap<>();
                        user.put("UID",fAuth.getCurrentUser().getUid());
                        user.put("FirstName",firstName.getText().toString());
                        user.put("LastName",lastName.getText().toString());
                        user.put("Email",email.getText().toString());
//                        user.put("Password",password.getText().toString());
                        user.put("MobileNumber",fAuth.getCurrentUser().getPhoneNumber());
                        user.put("UserType",getSelected());
                        user.put("Address",address.getText().toString());
                        user.put("Location",locality.getSelectedItem().toString());
                        user.put("Area",area.getSelectedItem().toString());
                        user.put("UPIID",uid.getText().toString());
                        if (getSelected().equals("Vendor")) {
                            user.put("Verify", "0");
                        }


                        docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    if (getSelected().equals("Vendor")){
                                        setVendor(true);
                                    }else {
                                        setVendor(false);
                                    }
                                    GotoActivity();
                                }else{
                                    Toast.makeText(UserDetails.this, "Some thing went out wrong", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });

//                    }
//                    else {
//                        Toast.makeText(UserDetails.this, "Password not match or less then 6", Toast.LENGTH_SHORT).show();
//                    }
                    } else {
                        Toast.makeText(UserDetails.this, "Accept T&C", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(UserDetails.this, "Fill the required Details", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void onRadioButtonClicked(View v){
        RadioGroup rg = (RadioGroup) findViewById(R.id.category);
        RadioButton vendor = (RadioButton) findViewById(R.id.vendor);
        RadioButton customer = (RadioButton) findViewById(R.id.customer);
        uid=findViewById(R.id.uid);

        // Is the current Radio Button checked?
        boolean checked = ((RadioButton) v).isChecked();

        switch(v.getId()){
            case R.id.customer:
                if(checked){
                    customer.setTextColor(Color.RED);
                    vendor.setTextColor(Color.GRAY);
                    uid.setVisibility(View.GONE);
                }
                break;

            case R.id.vendor:
                if(checked){
                    vendor.setTextColor(Color.RED);
                    customer.setTextColor(Color.GRAY);
                    uid.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
    private String  getSelected()
    {
        int radioButtonID = userType.getCheckedRadioButtonId();
        View radioButton = userType.findViewById(radioButtonID);
        int id = userType.indexOfChild(radioButton);
        RadioButton RB = (RadioButton) userType.getChildAt(id);

        return RB.getText().toString();
    }

    private void setVendor(boolean vendor)
    {
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isVendor", vendor).apply();
    }

    private boolean getVendor()
    {
        return  getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isVendor", false);
    }

    private void GotoActivity()
    {
        if (getVendor()) {
            startActivity(new Intent(this, Vendor_Activity.class));
        } else {
            startActivity(new Intent(this, Customer_Act.class));
        }
    }

}
