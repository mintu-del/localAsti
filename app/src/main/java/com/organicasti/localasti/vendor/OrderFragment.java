package com.organicasti.localasti.vendor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.organicasti.localasti.R;
import com.organicasti.localasti.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/*
    Aditya Kumar
    Uses class Order_Subscription and Order_OneTime, PagerAdapter
    The XML File for this is fragment_order_0.xmlml
    Mainly contains Generation of Tabs and ViewPager code
 */

public class OrderFragment extends Fragment implements Order_Subscription.onFragmentInteractionListener, Order_OneTime.onFragmentInteractionListener {

    public ArrayList<SelectedItems> selectedItems;
    Context c;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectedItems = new ArrayList<SelectedItems>();
        c = getContext();
        View rootView = inflater.inflate(R.layout.fragment_order_0,container,false);
        final TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.pager);
        tabLayout.setupWithViewPager(viewPager);

        Order_OneTime order_oneTime = new Order_OneTime(c);
        Order_Subscription order_subscription = new Order_Subscription(c);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(),0);

        adapter.AddFragment(order_oneTime,"One Time");
        adapter.AddFragment(order_subscription,"Subscription");

        viewPager.setAdapter(adapter);




        return rootView;

    }
    public interface onFragmentInteractionListener {
    }
}