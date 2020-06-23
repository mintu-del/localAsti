package com.organicasti.localasti;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static java.util.Objects.requireNonNull;

/**
 * Sai Gopal
 * Fragment for Pending subscription
 */

public class Far_pending_requests extends Fragment
{

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_pending_requests, container,false);


        final RecyclerView pendingReqRecyclerView = view.findViewById(R.id.pending_req_recyclerView);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        pendingReqRecyclerView.setLayoutManager(linearLayout);

        final FirebaseFirestore FStore = FirebaseFirestore.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final ArrayList<CustomerDetails> Customerdetails = new ArrayList<>();
        String UID = requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        CollectionReference PendingRef = FStore.collection("Subscriptions");

        PendingRef
                .whereEqualTo("VendorUID", Objects.requireNonNull(UID))
                .whereEqualTo("Status","Pending")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        progressDialog.dismiss();
                        for (final QueryDocumentSnapshot RootSnapshot : requireNonNull(task.getResult()))
                        {
                            final String CustomerUID = requireNonNull(RootSnapshot.get("CustomerUID")).toString();
                            FStore.collection("Users")
                                   .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot snapshot : requireNonNull(task.getResult()))
                                            {
                                                if (snapshot.getId().equals(CustomerUID))
                                                {
                                                    Customerdetails.add( new CustomerDetails(
                                                            requireNonNull(snapshot.get("FirstName")).toString(),
                                                            requireNonNull(snapshot.get("Address")).toString(),
                                                            requireNonNull(snapshot.get("MobileNumber")).toString(),
                                                            RootSnapshot.getId(),
                                                            null
                                                    ));
                                                }
                                            }
                                            PendingAndExistingReqAdapter adapter = new PendingAndExistingReqAdapter("Pending",getActivity(), Customerdetails);
                                            pendingReqRecyclerView.setAdapter(adapter);
                                            pendingReqRecyclerView.setHasFixedSize(true);
                                        }
                                    });
                        }

                    }
                });

        return view;
    }


}
