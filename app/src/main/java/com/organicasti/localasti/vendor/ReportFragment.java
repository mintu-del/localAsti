package com.organicasti.localasti.vendor;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.organicasti.localasti.R;
import com.organicasti.localasti.ViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import androidx.viewpager.widget.ViewPager;

/**
 * Sai Gopal Report fragment
 */
public class ReportFragment extends Fragment {

    private FirebaseFirestore DB;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_report_0, container,false);


        final ArrayList<ReportsHolder> noDeliveryArrayList = new ArrayList<>();
        final ArrayList<ReportsHolder> revenueArrayList = new ArrayList<>();

        DB = FirebaseFirestore.getInstance();

        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final CollectionReference ReportRef = DB.collection("Reports").document(UID).collection("Customers");

        final RecyclerView revenueAmount = view.findViewById(R.id.Revenue_Amount_recycler);
        LinearLayoutManager Layout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        revenueAmount.setLayoutManager(Layout);


        ViewPager viewPager = view.findViewById(R.id.viewPager_report);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);

        NoDeliveryReport noDeliveryReport = new NoDeliveryReport();
        DeliveredReport deliveredReport = new DeliveredReport();
        SubscriptionDeliveryReportFragment subscriptionDelivery = new SubscriptionDeliveryReportFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        adapter.AddFragment(noDeliveryReport,"No Delivery");
        adapter.AddFragment(deliveredReport,"OneTime Delivered");
        adapter.AddFragment(subscriptionDelivery,"Subscription Delivered");
        viewPager.setAdapter(adapter);


        //Getting Data
        ReportRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (final QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult()))
                            {
                                if (snapshot.exists())
                                {
                                    DB.collection("Users")
                                            .document(snapshot.getId())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    //Adding Amount and revenue to the adapter according to Customer
                                                    ReportsHolder reportsHolder = null;
                                                    try
                                                    {
                                                        reportsHolder = new ReportsHolder
                                                                (
                                                                        Objects.requireNonNull(snapshot.get("Revenue")).toString(),
                                                                        Objects.requireNonNull(documentSnapshot.get("FirstName")).toString()
                                                                );
                                                        revenueArrayList.add(reportsHolder);

                                                    }
                                                    catch (Exception e){

                                                    }
                                                    RevenueAdapter revenueAdapter = new RevenueAdapter(revenueArrayList);
                                                    revenueAmount.setAdapter(revenueAdapter) ;
                                                    revenueAmount.setHasFixedSize(true);
                                                }
                                            });

                                }

                            }

                        }
                        else {
                            Toast.makeText(getActivity(), ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });




        return view;
    }



    public static class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.RevenueViewHolder>{

        ArrayList<ReportsHolder>  arrayList ;

        RevenueAdapter(ArrayList<ReportsHolder> arrayList) {
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public RevenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_amount_revenue_items,parent,false);
            return new RevenueViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RevenueViewHolder holder, int position)
        {
            holder.Name.setText(arrayList.get(position).getName());
            holder.revenue.setText(arrayList.get(position).getRevenue());
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        static class RevenueViewHolder extends RecyclerView.ViewHolder {
            TextView revenue,amount,Name;
            RevenueViewHolder(@NonNull View itemView) {
                super(itemView);
                Name = itemView.findViewById(R.id.customer_name);
                revenue = itemView.findViewById(R.id.revenue);
            }
        }
    }

}
