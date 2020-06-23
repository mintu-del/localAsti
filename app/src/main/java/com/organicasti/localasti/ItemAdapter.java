package com.organicasti.localasti;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.organicasti.localasti.customer.CustProduct_Subclass;
import com.organicasti.localasti.customer.CustomerClass;
import com.organicasti.localasti.vendor.SelectedItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

/*  Aditya Kumar
    Used for the Outermost card with Customer and their product details.
    Binds one customer to one Holder.
    For layout of the card, check customer_row_0.xmlml
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<CustomerClass> itemList;
    Context c;
    FirebaseFirestore db;
    //String customerID;
    //CustomerClass customerObj;
    double tot_amount = 0;
    List<SelectedItems> selectedItems;



    public ItemAdapter(List<CustomerClass> itemList, Context c, List<SelectedItems> selectedItems) {
        this.itemList = itemList;
        this.c = c;
        this.selectedItems = selectedItems;
        db = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_row_0, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final CustomerClass customerObj = itemList.get(position);
        holder.cust_name.setText(customerObj.getCust_name());
        holder.cust_phone_no.setText(customerObj.getPhone_no());
        holder.cust_address.setText(customerObj.getAddress());
        final String customerID = customerObj.getCustomerID();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //custproductList.clear();

        final Map<String, CustProduct_Subclass> productList = new HashMap<String, CustProduct_Subclass>();

        db.collection("Orders_OneTime").whereEqualTo("CustomerID", customerID).whereEqualTo("VendorID", user.getUid()).whereEqualTo("Delivered", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            tot_amount = 0;
                            List<CustProduct_Subclass> custproductList = new ArrayList<CustProduct_Subclass>();
                            for(QueryDocumentSnapshot doc: task.getResult()) {
                                Map<String, Object> m = doc.getData();
                                Log.d(TAG,doc.getId()+position);

                                String key = m.get("ProductID").toString();
                                tot_amount += Double.parseDouble(m.get("Amount").toString())*(Double.parseDouble(m.get("Quantity").toString()));
                                CustProduct_Subclass obj = new CustProduct_Subclass(m.get("ProductName").toString(), m.get("Quantity").toString(), Double.toString(Double.parseDouble(m.get("Amount").toString())*(Double.parseDouble(m.get("Quantity").toString()))), m.get("Description").toString(), "false");
                                obj.setCustomerID(customerID);
                                obj.setSubscriptionID(doc.getId());
                                obj.setProductID(key);
                                obj.setVendorID(user.getUid());
                                custproductList.add(obj);
                            }
                            //Log.d(TAG,custproductList.size()+"yahalo");
                            customerObj.setProductList(custproductList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(
                                    holder.productRecyclerView.getContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                            );
                            //holder.amount.setText("Rs. "+ Double.toString(tot_amount));
                            layoutManager.setInitialPrefetchItemCount(customerObj.getProductList().size());
                            SubItemAdapter subItemAdapter = new SubItemAdapter(customerObj.getProductList(),c, selectedItems);
                            holder.productRecyclerView.setLayoutManager(layoutManager);
                            holder.productRecyclerView.setAdapter(subItemAdapter);
                            holder.productRecyclerView.setRecycledViewPool(viewPool);

                        }
                        else {

                        }
                    }
                });



        //}


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView cust_name;
        private TextView cust_phone_no;
        private  TextView cust_address;
        private TextView amount;
        private RecyclerView productRecyclerView;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cust_name = itemView.findViewById(R.id.customer_name);
            cust_phone_no = itemView.findViewById(R.id.phone_no);
            cust_address = itemView.findViewById(R.id.address);
            //amount = itemView.findViewById(R.id.amount_left);
            productRecyclerView = itemView.findViewById(R.id.recycViewProductList);
        }
    }

    public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {
        public List<CustProduct_Subclass> subItemList;
        List<SelectedItems> selectedItems;
        Context c;

        SubItemAdapter(List<CustProduct_Subclass> subItemList, Context c, List<SelectedItems> selectedItems) {
            this.subItemList = subItemList;
            this.selectedItems = selectedItems;
            this.c = c;
        }

        @NonNull
        @Override
        public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_product_row_0, parent, false);
            return new SubItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SubItemViewHolder holder, final int position) {
            final CustProduct_Subclass subItem = subItemList.get(position);
            holder.prod_name.setText(subItem.getProductName());
            holder.prod_qty.setText(subItem.getQuantity());
            holder.prod_amnt.setText(subItem.getAmount().toString());
            holder.description.setText(subItem.getDescription());


            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        SelectedItems obj = new SelectedItems(subItem.getCustomerID(), subItem.getVendorID(), subItem.getProductID());
                        obj.setSubscriptionID(subItem.getSubscriptionID());
                        selectedItems.add(obj);
                    } else {
                        for(int i=0; i<selectedItems.size(); i++) {
                            if(selectedItems.get(i).getSubscriptionID().equals(subItem.getSubscriptionID())) {
                                selectedItems.remove(i);
                                break;
                            }
                        }
                    }
                    // productDetails.setOrderList(SelectedList);
                }
            });
        }

        @Override
        public int getItemCount() {
            return subItemList.size();
        }

        public class SubItemViewHolder extends RecyclerView.ViewHolder {
            TextView prod_name;
            TextView prod_qty;
            TextView prod_amnt;
            CheckBox checkbox;
            TextView description;
            public SubItemViewHolder(@NonNull View itemView) {
                super(itemView);
                prod_name = itemView.findViewById(R.id.product_name);
                prod_amnt = itemView.findViewById(R.id.amount_order);
                prod_qty = itemView.findViewById(R.id.quantity_order);
                checkbox = itemView.findViewById(R.id.delivered_checkbox);
                description = itemView.findViewById(R.id.product_description);
            }
        }
    }

}