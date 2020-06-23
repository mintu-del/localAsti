package com.organicasti.localasti.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.organicasti.localasti.R;

import androidx.fragment.app.Fragment;

/*Aditya Kumar

*/
public class CustomerProfileFragment extends Fragment {

    public static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String mName,mDAN,mDA,mPhone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_profile, container, false);


        Button save = view.findViewById(R.id.save_button_cust_profile);
        Button update = view.findViewById(R.id.update_button_cust_profile);
         final TextView cust_name = view.findViewById(R.id.profile_cust_actual_name);
         final TextView cust_phone_no = view.findViewById(R.id.profile_cust_actual_mobile_no);
        final TextView cust_delivery_addr_name = view.findViewById(R.id.profile_cust_actual_del_address_name);
        final TextView cust_delivery_addr = view.findViewById(R.id.profile_cust_actual_del_address);
        final EditText cust_deliver_addr_name_update = view.findViewById(R.id.editTextDelName);
        final EditText cust_deliver_addr_addr_update = view.findViewById(R.id.editTextDelAddr);



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        DocumentReference docRef =fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    mName = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
                    mDAN = documentSnapshot.getString("Location");
                    mDA = documentSnapshot.getString("Address");
                    mPhone = fAuth.getCurrentUser().getPhoneNumber();

                    cust_name.setText(mName);
                    cust_delivery_addr_name.setText(mDAN);
                    cust_delivery_addr.setText(mDA);
                    cust_phone_no.setText(mPhone);



                }else {
                    Log.d(TAG, "Retrieving Data: Profile Data Not Found ");
                }
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  cust_delivery_addr_name.setVisibility(View.GONE);
                cust_deliver_addr_addr_update.setVisibility(View.VISIBLE);
                cust_deliver_addr_name_update.setVisibility(View.VISIBLE);
                cust_delivery_addr.setVisibility(View.GONE);

            */
              Toast.makeText(getContext(), "Updating", Toast.LENGTH_SHORT).show();
            }

        });







        return view;
    }
}
