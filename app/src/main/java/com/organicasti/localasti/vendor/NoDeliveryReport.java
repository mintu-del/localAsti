package com.organicasti.localasti.vendor;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.organicasti.localasti.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NoDeliveryReport extends Fragment {

    private FirebaseFirestore DB;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nodelivery_report,container,false);

        final RecyclerView noDelivery = view.findViewById(R.id.no_delivery_recycler);

        final ArrayList<ReportsHolder> noDeliveryArrayList = new ArrayList<>();

        DB = FirebaseFirestore.getInstance();

        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final CollectionReference ReportRef = DB.collection("Reports").document(UID).collection("Customers");


        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        noDelivery.setLayoutManager(linearLayout);


        ReportRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                //Getting NoDelivery Data from Database
                                    ReportRef.document(snapshot.getId()).collection("NoDelivery")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (task.isSuccessful()) {
                                                        for (final QueryDocumentSnapshot snapshot1 : Objects.requireNonNull(task.getResult())) {
                                                            //getting Users Name from Database
                                                            if (Objects.requireNonNull(snapshot1.get("NoDelivery")).toString().equals("true"))
                                                            {
                                                                DB.collection("Users")
                                                                        .document(snapshot.getId())
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                                                    assert documentSnapshot != null;
                                                                                    ReportsHolder reportsHolder;
                                                                                    reportsHolder = new ReportsHolder(
                                                                                            Objects.requireNonNull(snapshot1.get("From")).toString(),
                                                                                            Objects.requireNonNull(snapshot1.get("To")).toString(),
                                                                                            Objects.requireNonNull(documentSnapshot.get("FirstName")).toString(),
                                                                                            snapshot1.getId()
                                                                                    );

                                                                                    noDeliveryArrayList.add(reportsHolder);
                                                                                    NoDeliveryAdapter noDeliveryAdapter = new NoDeliveryAdapter(noDeliveryArrayList, getActivity());
                                                                                    noDelivery.setAdapter(noDeliveryAdapter);
                                                                                    noDelivery.setHasFixedSize(true);
                                                                                }
                                                                            }
                                                                        });
                                                            }

                                                        }
                                                    }
                                                }
                                            });

                            }

                        }

                    }
                });


        return view;
    }
}
