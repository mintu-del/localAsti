package com.organicasti.localasti.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.organicasti.localasti.List;
import com.organicasti.localasti.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductOrderFragment extends Fragment
{
    Activity mActivity;
    int select=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_cust,container,false);

        Button oneTime = view.findViewById(R.id.onetime);
        Button subscription = view.findViewById(R.id.subscrition);

        List[] myListData = new List[]
                {
                        new List("MILK"),
                        new List("CURD"),
                        new List("GEE"),
                };

        ArrayList<List> lis = new ArrayList<>();
        for (int i = 0;i<10;i++) {
            List list = new List("Product name"+i);
            lis.add(list);
        }

        RecyclerView recyclerView = view.findViewById(R.id.vendor_list);
        final ProductsAdapters adapter = new ProductsAdapters(lis,getContext(),"product");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);//to show deails of it

        oneTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                showSelected("One Time",adapter);
                //Toast.makeText(getActivity(), "One Time", Toast.LENGTH_SHORT).show();
            }
        });
        subscription.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
               // Toast.makeText(getActivity(), "Subscription", Toast.LENGTH_SHORT).show();
                showSelected("Subscription",adapter);
            }
        });


        return view;

    }

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showSelected(final String title, ProductsAdapters adapter){
        int arraySize = adapter.getSelectedList().size();

        if (arraySize > 0)
        {
            if (arraySize  < 4 )
            {
                final String name= String.valueOf(adapter.getSelectedList().get(0));
                final String quantity = String.valueOf(adapter.getSelectedList().get(1));
                final String price = String.valueOf(adapter.getSelectedList().get(2));
                new MaterialAlertDialogBuilder(Objects.requireNonNull(getContext()))
                        .setTitle(title)
                        .setMessage("SELECTED ITEMS (" + arraySize + ") : " + adapter.getSelectedList().toString().replace("[", "").replace("]", ""))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent intent = new Intent(getContext(),Delivery.class);
                                intent.putExtra("PN",name);
                                intent.putExtra("PQ",quantity);
                                intent.putExtra("PP",price);
                                intent.putExtra("Type",title);
                                startActivity(intent);

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .show();
            }
            else
            {
                Toast.makeText(getActivity(), "Select One product at a time only", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Toast.makeText(getActivity(), "Select Atleast One ", Toast.LENGTH_SHORT).show();
        }

    }
}
