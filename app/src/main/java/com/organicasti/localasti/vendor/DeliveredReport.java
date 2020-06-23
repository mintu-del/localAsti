package com.organicasti.localasti.vendor;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.organicasti.localasti.R;
import com.organicasti.localasti.ReportHolder;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeliveredReport extends Fragment {


    private FirebaseFirestore DB;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deliveryed_report,container,false);


        final RecyclerView Delivered = view.findViewById(R.id.delivered_recycler);

        final ArrayList<ReportHolder> reportHolderArrayList = new ArrayList<>();

        DB = FirebaseFirestore.getInstance();

        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final CollectionReference ReportRef = DB.collection("Orders_OneTime");

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        Delivered.setLayoutManager(linearLayout);


        ReportRef
                .whereEqualTo("VendorID",UID)
                .whereEqualTo("Delivered",true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                        {
                            String CustomerUID = snapshot.get("CustomerID").toString();
                            final String ProductID = snapshot.get("ProductID").toString();
                            final String ProductQuantity = snapshot.get("Quantity").toString();

                            DB.collection("Users")
                                    .document(CustomerUID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful())
                                            {
                                                DocumentSnapshot vendorSnap = task.getResult();
                                                assert vendorSnap != null;
                                                final String CustomerName  = vendorSnap.get("FirstName").toString();
                                                final String CustomerNumber = vendorSnap.get("MobileNumber").toString();
                                                //Getting Product Data
                                                DB.collection("Products")
                                                        .document(ProductID)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    DocumentSnapshot productSnap = task.getResult();
                                                                    String ProductName = productSnap.get("ProductName").toString();
                                                                    String ProductPrice = productSnap.get("Rate").toString();
                                                                    String Description = productSnap.get("Description").toString();
                                                                    reportHolderArrayList.add(new ReportHolder(
                                                                            CustomerNumber,CustomerName,ProductName,
                                                                            ProductPrice,ProductQuantity,Description));

                                                                    OrderDeliveredAdapter OrdersAdapter = new OrderDeliveredAdapter(reportHolderArrayList);
                                                                    Delivered.setAdapter(OrdersAdapter);
                                                                    Delivered.setHasFixedSize(true);
                                                                }

                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }


                    }
                });



        return view;
    }
}
