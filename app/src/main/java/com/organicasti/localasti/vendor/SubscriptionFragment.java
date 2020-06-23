package com.organicasti.localasti.vendor;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.organicasti.localasti.Far_manage_existing;
import com.organicasti.localasti.Far_pending_requests;
import com.organicasti.localasti.R;
import com.organicasti.localasti.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class SubscriptionFragment extends Fragment {

    private Button addNew;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subscription_0,container,false);

        addNew = view.findViewById(R.id.addNewSubscription);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Add new subscription", Toast.LENGTH_SHORT).show();
            }
        });


        ViewPager viewPager = view.findViewById(R.id.viewPager_subscription);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);

        Far_manage_existing manage_existing = new Far_manage_existing();
        Far_pending_requests pending_requests = new Far_pending_requests();


        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        adapter.AddFragment(pending_requests,"Pending Requests");
        adapter.AddFragment(manage_existing,"Manage Existing");
        viewPager.setAdapter(adapter);




        return view;
        
    }
}
