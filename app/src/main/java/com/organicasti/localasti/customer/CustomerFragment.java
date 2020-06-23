package com.organicasti.localasti.customer;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.organicasti.localasti.R;
import com.organicasti.localasti.ViewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class CustomerFragment extends Fragment {
public String Add;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_customer,container,false);
             //viewpager and tablayout is used to provide swipe between tabs at the top
        ViewPager viewPager = view.findViewById(R.id.viewPager_subscription);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        tabLayout.setupWithViewPager(viewPager);
        SupplierFragment supplierFragment = new SupplierFragment();
       ProductOrderFragment productOrderFragment = new ProductOrderFragment();
  // making supplier to be written at top
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        adapter.AddFragment(supplierFragment,"Supplier");
       // adapter.AddFragment(productOrderFragment,"Product");
        viewPager.setAdapter(adapter);







        return view;

    }
}
