package com.organicasti.localasti.vendor;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.organicasti.localasti.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private Button save;
    private Button addAddress,addCert;
    private TextView deliverytext,addresstext;
    private TextView certtext,certTypetext,certNum,certNo;

    private CheckBox sun,mon,tue,wed,thu,fri,sat;

    private Spinner spinner,spinnerCert;

    public static final String TAG = "TAG";


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String mName,mPhone,mUPI,verification;
    ArrayList<String> location,certificate,dDays,certificateNo;



    private FirebaseFirestore DB;
    ArrayList<String> loc,cert;

    ArrayAdapter<String> adapt,certAdapt;
    ArrayList<String> spinnerDataList,certDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      final  View view = inflater.inflate(R.layout.fragment_profile,container,false);



        final TextView cust_name = view.findViewById(R.id.profile_vend_actual_name);
        final TextView cust_phone_no = view.findViewById(R.id.profile_vend_actual_mobile_no);
        final TextView cust_upi = view.findViewById(R.id.profile_vend_actual_upi);
        final TextView vend_delivery=view.findViewById(R.id.profile_vend_actual_delivery);
        final TextView vend_certificate=view.findViewById(R.id.profile_vend_actual_cert);
        final TextView vend_dDays=view.findViewById(R.id.profile_vend_actual_dDays);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        final DocumentReference docRef =fStore.collection("Users")
                .document(fAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    mName = documentSnapshot.getString("FirstName") + " "
                            + documentSnapshot.getString("LastName");
                    mPhone = fAuth.getCurrentUser().getPhoneNumber();
                    mUPI=documentSnapshot.getString("UPIID");
                    verification=documentSnapshot.getString("Verify");
                    Log.d(TAG,"id Verification"+verification);

                    //for diplaying location-v
                    location=(ArrayList<String>)documentSnapshot.get("DeliveryLocation");
                    if(location!=null){
                        Log.d(TAG, location.toString());

                        for(int i=0;i<location.size();i++){
                            vend_delivery.append(location.get(i));
                            vend_delivery.append(", ");
                        }
                    }
                    certificate=(ArrayList<String>)documentSnapshot.get("Certificate");
                    certificateNo=(ArrayList<String>)documentSnapshot.get("Certificate No");

                    if(certificate!=null){
                        Log.d(TAG, certificate.toString());

                        for(int i=0;i<certificate.size();i++){
                            vend_certificate.append(certificateNo.get(i)+" ("+certificate.get(i)+")");
                            vend_certificate.append("\n");
                        }
                    }
                    dDays=(ArrayList<String>)documentSnapshot.get("DeliveryDays");
                    if(dDays!=null){
                        Log.d(TAG, dDays.toString());

                        for(int i=0;i<dDays.size();i++){
                            vend_dDays.append(dDays.get(i));
                            vend_dDays.append(", ");
                        }
                    }


                    cust_name.setText(mName);
                    cust_phone_no.setText(mPhone);
                    cust_upi.setText(mUPI);
                }else {
                    Log.d(TAG, "Retrieving Data: Profile Data Not Found ");
                }
            }
        });


        save = view.findViewById(R.id.save_button_vend_profile);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
            }
        });

        deliverytext=view.findViewById(R.id.textdelivery);
        addresstext=view.findViewById(R.id.addresstext);
        deliverytext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(verification.equals("1")) {
                if(addAddress.getVisibility()==view.VISIBLE &&
                        spinner.getVisibility()==view.VISIBLE &&
                        addresstext.getVisibility()==view.VISIBLE){
                    Log.d(TAG,"corrrect "+"true");

                    addAddress.setVisibility(View.GONE);
                    spinner.setVisibility(view.GONE);
                    addresstext.setVisibility(view.GONE);
                }
                else if(addAddress.getVisibility()==view.GONE &&
                        spinner.getVisibility()==view.GONE &&
                        addresstext.getVisibility()==view.GONE){
                    Log.d(TAG,"corrrect "+"false ");

                    addAddress.setVisibility(View.VISIBLE);
                    spinner.setVisibility(view.VISIBLE);
                    addresstext.setVisibility(view.VISIBLE);

                }


                spinnerDataList=new ArrayList<>();
                adapt=new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item,spinnerDataList);

                spinner.setAdapter(adapt);
                DB = FirebaseFirestore.getInstance();
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
//                                    Log.d(TAG," loc "+loc.get(i));
                                    spinnerDataList.add(loc.get(i));

                                }
                            }


                        }
                        adapt.notifyDataSetChanged();
                    }
                });
                }else if(verification.equals("0")){
                    Toast.makeText(getActivity(), "Your profile is not active.\nIt will be verified first", Toast.LENGTH_SHORT).show();
                }

            }

        });


        //adding location to database-v
        addAddress=view.findViewById(R.id.add_vend_address);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loc = spinner.getSelectedItem().toString();
                docRef.update("DeliveryLocation", FieldValue.arrayUnion(loc));
                Toast.makeText(getActivity(), "added : " + loc, Toast.LENGTH_SHORT)
                        .show();

                // Reload current fragment
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                //Toast.makeText(getActivity(), "add Address", Toast.LENGTH_SHORT).show();
            }
        });



        certtext=view.findViewById(R.id.textCertifiate);

        certNum=view.findViewById(R.id.certNum);
        certNo=view.findViewById(R.id.certNo);


        certTypetext=view.findViewById(R.id.certTypetext);
        addCert=view.findViewById(R.id.add_vend_cert);
        spinnerCert = (Spinner) view.findViewById(R.id.spinnerCert);

        certtext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(verification.equals("0"))
                {
                    if(addCert.getVisibility()==view.VISIBLE &&
                        spinnerCert.getVisibility()==view.VISIBLE &&
                        certNo.getVisibility()==view.VISIBLE &&
                        certNum.getVisibility()==view.VISIBLE &&
                        certTypetext.getVisibility()==view.VISIBLE){
                    Log.d(TAG,"corrrect "+"true");

                    addCert.setVisibility(View.GONE);
                    spinnerCert.setVisibility(view.GONE);
                    certTypetext.setVisibility(view.GONE);
                    certNo.setVisibility(View.GONE);
                    certNum.setVisibility(View.GONE);
                }
                    else if(addCert.getVisibility()==view.GONE &&
                        spinnerCert.getVisibility()==view.GONE &&
                        certNo.getVisibility()==view.GONE &&
                        certNum.getVisibility()==view.GONE &&
                        certTypetext.getVisibility()==view.GONE){
                    Log.d(TAG,"corrrect "+"false ");

                    addCert.setVisibility(View.VISIBLE);
                    spinnerCert.setVisibility(view.VISIBLE);
                    certTypetext.setVisibility(view.VISIBLE);
                    certNo.setVisibility(View.VISIBLE);
                    certNum.setVisibility(View.VISIBLE);
                }



                certDataList=new ArrayList<>();
                certAdapt=new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item,certDataList);

                spinnerCert.setAdapter(certAdapt);
                DB = FirebaseFirestore.getInstance();
                final CollectionReference Cert_Ref = DB.collection("Certificates");

                Cert_Ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                        {
                            cert=(ArrayList<String>)snapshot.get("Type");
                            if(cert!=null){
                                for(int i=0;i<cert.size();i++){
//                                    Log.d(TAG," loc "+loc.get(i));
                                    certDataList.add(cert.get(i));

                                }
                            }


                        }
                        certAdapt.notifyDataSetChanged();
                    }
                });
            }else if(verification.equals("1")){
                Toast.makeText(getActivity(), "Your profile is active.\nCan't add now", Toast.LENGTH_SHORT).show();
            }

            }

        });

        addCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cert = spinnerCert.getSelectedItem().toString();

                docRef.update("Certificate", FieldValue.arrayUnion(cert));
                docRef.update("Certificate No", FieldValue.arrayUnion(certNo.getText().toString()));


                Toast.makeText(getActivity(), "added : " + cert, Toast.LENGTH_SHORT)
                        .show();

                // Reload current fragment
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                //Toast.makeText(getActivity(), "add Address", Toast.LENGTH_SHORT).show();
            }
        });


        final ArrayList<String> days=new ArrayList<>();
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.dayCategory);
        final RadioButton allDay = (RadioButton) view.findViewById(R.id.allDays);
        final RadioButton custom = (RadioButton) view.findViewById(R.id.customDay);
        sun=(CheckBox)view.findViewById(R.id.sun);
        mon=(CheckBox)view.findViewById(R.id.mon);
        tue=(CheckBox)view.findViewById(R.id.tue);
        wed=(CheckBox)view.findViewById(R.id.wed);
        thu=(CheckBox)view.findViewById(R.id.thu);
        fri=(CheckBox)view.findViewById(R.id.fri);
        sat=(CheckBox)view.findViewById(R.id.sat);

        final LinearLayout x=(LinearLayout)view.findViewById(R.id.deliveryLayout);

        final Button add_days=(Button)view.findViewById(R.id.add_vend_days);

        TextView textDays=view.findViewById(R.id.textDays);

        textDays.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(allDay.getVisibility()==view.VISIBLE &&
                        custom.getVisibility()==view.VISIBLE &&
                        add_days.getVisibility()==view.VISIBLE){

                    allDay.setVisibility(View.GONE);
                    custom.setVisibility(View.GONE);
                    add_days.setVisibility(View.GONE);
                    if(x.getVisibility()==View.VISIBLE)
                    x.setVisibility(View.GONE);

                }
                else if(allDay.getVisibility()==view.GONE &&
                        custom.getVisibility()==view.GONE &&
                        add_days.getVisibility()==view.GONE)
                        {
                         allDay.setVisibility(View.VISIBLE);
                         custom.setVisibility(View.VISIBLE);
                         add_days.setVisibility(View.VISIBLE);
                }



                allDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"VEDANT "+"allDay");


                        sun.setVisibility(view.GONE);
                        mon.setVisibility(View.GONE);
                        tue.setVisibility(View.GONE);
                        wed.setVisibility(view.GONE);
                        thu.setVisibility(View.GONE);
                        fri.setVisibility(View.GONE);
                        sat.setVisibility(View.GONE);
                    }
                });

                custom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"VEDANT "+" custom");
                        if(x.getVisibility()==View.GONE)
                            x.setVisibility(View.VISIBLE);

                        sun.setVisibility(view.VISIBLE);
                        mon.setVisibility(View.VISIBLE);
                        tue.setVisibility(View.VISIBLE);
                        wed.setVisibility(view.VISIBLE);
                        thu.setVisibility(View.VISIBLE);
                        fri.setVisibility(View.VISIBLE);
                        sat.setVisibility(View.VISIBLE);





                    }
                });
            }

        });
        add_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"selected radio "+custom.isSelected()+allDay.isChecked());
                if(custom.isChecked()) {
                    if (sun.isChecked())
                        days.add("sunday");
                    if (mon.isChecked())
                        days.add("monday");
                    if (tue.isChecked())
                        days.add("tuesday");
                    if (wed.isChecked())
                        days.add("wednesday");
                    if (thu.isChecked())
                        days.add("thursday");
                    if (fri.isChecked())
                        days.add("friday");
                    if (sat.isChecked())
                        days.add("saturday");
                }else if(allDay.isChecked()){
                    days.add("sunday");
                    days.add("monday");
                    days.add("tuesday");
                    days.add("wednesday");
                    days.add("thursday");
                    days.add("friday");
                    days.add("saturday");
                }

                Log.d(TAG," no days "+days);

                if(dDays!=null){
                    Log.d(TAG, dDays.toString());

                    for(int i=0;i<dDays.size();i++){
                        docRef.update("DeliveryDays", FieldValue.arrayRemove(dDays.get(i)));
                    }
                }

                for(int i=0;i<days.size();i++){
                    docRef.update("DeliveryDays", FieldValue.arrayUnion(days.get(i)));
                }


                Toast.makeText(getActivity(), "added : " + days, Toast.LENGTH_SHORT)
                        .show();

                // Reload current fragment
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                //Toast.makeText(getActivity(), "add Address", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
        
    }

}
