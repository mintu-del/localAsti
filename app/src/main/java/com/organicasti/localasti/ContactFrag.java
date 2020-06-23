package com.organicasti.localasti;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

//import com.milkasti.milkasti.R;


public class ContactFrag extends Fragment {

    private Button save;
    private Button addAddress;
    private TextView deliverytext,addresstext;

    private Spinner spinner;

    public static final String TAG = "TAG";


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String mEmail,mPhone;
    ArrayList<String> location;

    private FirebaseFirestore DB;
    ArrayList<String> loc;

    ArrayAdapter<String> adapt;
    ArrayList<String> spinnerDataList;

    public ContactFrag() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final  View view = inflater.inflate(R.layout.fragment_contact,container,false);

        final TextView email = view.findViewById(R.id.profile_contact_email);
        final TextView phone_no = view.findViewById(R.id.profile_contact_mobile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        final DocumentReference docRef =fStore.collection("Contact")
                .document("Contact_database");

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    mEmail = documentSnapshot.getString("Email");
                    mPhone = documentSnapshot.getString("mobile");





                    email.setText(mEmail);
                    phone_no.setText(mPhone);

                }else {
                    Log.d(TAG, "Retrieving Data: Profile Data Not Found ");
                }
            }
        });


        return view;










    }
}
