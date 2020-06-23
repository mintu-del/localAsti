package com.organicasti.localasti.customer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.organicasti.localasti.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.organicasti.localasti.R;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class SupplierFragment extends Fragment
{
    String mDAN;
    String Add;
    FirebaseFirestore db;
    String actualAddress;

    ProductsAdapters adapter;
    private Spinner locality;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String Addr;

    String Name,Address,MobileNumber,CustomerId;

    public SupplierFragment() {
        //this.Addr=Add;
        //Log.d(TAG,"n ADDRESS Addr"+Addr);
        db = FirebaseFirestore.getInstance();
    }






    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_order_cust,container,false);

        Button oneTime = view.findViewById(R.id.onetime);
        Button subscription = view.findViewById(R.id.subscrition);

        //Address(view);


        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Subscription", Toast.LENGTH_SHORT).show();
            }
        });

        //Log.d(TAG, "ADDRESS is "+Address(view));
        Log.d(TAG, "n ADDRESS is new "+Addr);

        final ArrayList<List> lis = new ArrayList<>();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final RecyclerView recyclerView = view.findViewById(R.id.vendor_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));





        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        DocumentReference docRef =fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Add = documentSnapshot.getString("Location");
                    actualAddress = documentSnapshot.getString("Address");//Changed Location to Address
                    //for delivery use
                    Name = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");

                    MobileNumber = fAuth.getCurrentUser().getPhoneNumber();
                    CustomerId=fAuth.getCurrentUser().getUid();

                }else {
                    Log.d(TAG, "Retrieving Data: Profile Data Not Found ");
                }
                db.collection("Users").whereEqualTo("UserType","Vendor")
                         .whereArrayContains("DeliveryLocation",Add)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG,"START");
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // DocumentReference documentReference = document.getDocumentReference("Products");
                                        //  Log.d(TAG,"Try"+documentReference.toString());
                                        Map<String, Object> m = document.getData();
                                        List lisobj = new List(m.get("FirstName").toString()+" "+m.get("LastName").toString(),
                                                R.drawable.ic_person, document.getId().toString());
                                        lisobj.setCertificates((ArrayList<String>)document.get("Certificate"));
                                        lisobj.setDeliveryDays((ArrayList<String>)document.get("DeliveryDays"));
                                        lis.add(lisobj);

                                        Log.d(TAG, "DocumentSnapshot data: " + document.getId().toString());
                                    }
                                    adapter = new ProductsAdapters(lis,getContext(),"supplier");
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    progressDialog.dismiss();



                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });






        //final ProductsAdapters adapter = new ProductsAdapters(lis,getContext(),"supplier");
        //recyclerView.setAdapter(adapter);


        oneTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
               // Toast.makeText(getActivity(), "One Time", Toast.LENGTH_SHORT).show();
               showSelected("One Time",adapter);
            }
        });
        subscription.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Subscription", Toast.LENGTH_SHORT).show();
                showSelected("Subscription",adapter);
            }
        });

        return view;
    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showSelected(final String title, final ProductsAdapters adapter)
    {
        if (adapter.getSelectedList() != null)
        {
            int arraySize = adapter.getSelectedList().size();
            if (arraySize > 0)
            {
                //REMOVE this if condition for multiple products---value parsed to delivery address
                //if (arraySize  < 6 )
               // {


                    final ArrayList<String> m_name=new ArrayList<String>();
                    final ArrayList<String> m_quantity=new ArrayList<String>();
                    final ArrayList<String> m_price=new ArrayList<String>();
                    final ArrayList<String> m_vendorID=new ArrayList<String>();
                    final ArrayList<String> m_desc=new ArrayList<String>();
                    final ArrayList<String> m_productID=new ArrayList<String>();

                    //setting from adapter
                    for(int i=0;i<arraySize;i=i+6)
                    {
                        m_name.add(String.valueOf(adapter.getSelectedList().get(i)));
                        m_quantity.add(String.valueOf(adapter.getSelectedList().get(i+1)));
                        m_price.add(String.valueOf(adapter.getSelectedList().get(i+2)));
                        m_vendorID.add(String.valueOf(adapter.getSelectedList().get(i+3)));
                        m_desc.add(String.valueOf(adapter.getSelectedList().get(i+4)));
                        m_productID.add(String.valueOf(adapter.getSelectedList().get(i+5)));
                    }

                    //Log.d(TAG,"array selected "+adapter.getSelectedList());
                    Log.d(TAG,"multiple_selection"+
                            m_name+"\n"+
                            m_quantity+"\n"+
                            m_price+"\n"+
                            m_vendorID+"\n"+
                            m_productID);

                    //ved-setting up array list to pass in delivery
                    final String name= String.valueOf(adapter.getSelectedList().get(0));
                    final String quantity = String.valueOf(adapter.getSelectedList().get(1));
                    final String price = String.valueOf(adapter.getSelectedList().get(2));
                    final String vendorID = String.valueOf(adapter.getSelectedList().get(3));
                    final String desc = String.valueOf(adapter.getSelectedList().get(4));
                    final String productID= String.valueOf(adapter.getSelectedList().get(5));


                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(title)
                        .setMessage("You have selected "+ title)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent intent = new Intent(getContext(),Delivery.class);

                                //passing data from fragment to delivery activity-for one product
                                intent.putExtra("ProductName",name);
                                intent.putExtra("Quantity",quantity);
                                intent.putExtra("Price",price);
                                intent.putExtra("VendorId",vendorID);
                                intent.putExtra("Description",desc);
                                intent.putExtra("ProductId",productID);

                                //CUSTOMER PART
                                intent.putExtra("Name",Name);
                                intent.putExtra("CustomerId",CustomerId);
                                intent.putExtra("MobileNumber",MobileNumber);
                                intent.putExtra("Address",actualAddress);
                                intent.putExtra("Location", Add);

                                //FOR MULTIPLE PRODUCTS

                                intent.putExtra("mProductName",m_name);
                                intent.putExtra("mQuantity",m_quantity);
                                intent.putExtra("mPrice",m_price);
                                intent.putExtra("mVendorId",m_vendorID);
                                intent.putExtra("mDesc",m_desc);
                                intent.putExtra("mProductId",m_productID);

                                //one time or subscription type
                                intent.putExtra("Type",title);
                                startActivity(intent);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
               // }
               // else
               // {
               //     Toast.makeText(getActivity(), "Select One product at a time only", Toast.LENGTH_SHORT).show();
               // }

            }
            else {
                Toast.makeText(getActivity(), "Select Atleast One ", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getActivity(), "Select Atleast One ", Toast.LENGTH_SHORT).show();
        }
    }



}
