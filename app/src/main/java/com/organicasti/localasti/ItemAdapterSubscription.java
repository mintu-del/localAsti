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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class ItemAdapterSubscription extends RecyclerView.Adapter<ItemAdapterSubscription.ItemViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<CustomerClass> itemList;
    Context c;
    public static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    FirebaseFirestore db;
    //String customerID;
    //CustomerClass customerObj;
    double tot_amount = 0;
    Date date;
    List<SelectedItems> selectedItems;



    public ItemAdapterSubscription(List<CustomerClass> itemList, Context c, List<SelectedItems> selectedItems) {
        this.itemList = itemList;
        Log.d(TAG, "Size of list: "+itemList.size());
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
        final Date dater = new Date(System.currentTimeMillis());
        Log.d(TAG,dater.toString());

        final boolean[] showYesterdayProduct = {true}; //

        //custproductList.clear();

        final Map<String, CustProduct_Subclass> productList = new HashMap<String, CustProduct_Subclass>();
         date = new Date(System.currentTimeMillis());
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.collection("Subscriptions").whereEqualTo("Status", "Existing").whereEqualTo("CustomerUID", customerID).whereEqualTo("VendorUID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            tot_amount = 0;
                            //Log.d(TAG, "Inside again2");
                            final List<CustProduct_Subclass> custproductList = new ArrayList<CustProduct_Subclass>();
                            for(final QueryDocumentSnapshot doc: task.getResult()) {

                                final Map<String, Object> customerMap = doc.getData();
                                Date frmDate = new Date(0);
                                Date toDate = new Date(0);
                                Date lastDelivered = new Date(0);
                                //Log.d(TAG, todayTimestamp.compareTo((Timestamp)m.get("From"))+"");
                                //Log.d(TAG, todayTimestamp.compareTo((Timestamp)m.get("To"))+"");
                                try {
                                    frmDate = sdf.parse(customerMap.get("From").toString());
                                    toDate = sdf.parse(customerMap.get("To").toString());
                                    lastDelivered = sdf.parse(customerMap.get("LastDelivered").toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //Log.d(TAG, "Inside again1");
                                if (date.compareTo(frmDate) >= 0 && date.compareTo(toDate) <= 0 && date.compareTo(lastDelivered) == 1) {

                                    List<String> noDeliveryFrom = (List<String>) doc.get("NoDeliveryFrom");
                                    List<String> noDeliveryTo = (List<String>) doc.get("NoDeliveryTo");
                                    boolean showProduct = true;

                                    for (int i = 0; i < noDeliveryFrom.size(); i++) {
                                        if(noDeliveryFrom.get(i) != null && noDeliveryTo.get(i) != null) {
                                            Date start = new Date(0);
                                            Date end = new Date(0);

                                            try {
                                                start = sdf.parse(noDeliveryFrom.get(i));
                                                end = sdf.parse(noDeliveryTo.get(i));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            if ((dater.compareTo(start) >= 0) && (dater.compareTo(end) <= 0)) {
                                                showProduct = false;
                                                Log.d(TAG, "Do not deliver and show");
                                            }
                                        }
                                    }
                                    //Show yesterday product
                                    Date yesterday = new Date(date.getTime()-(24*60*60*1000));
                                    if(yesterday.compareTo(frmDate) >=0) {
                                        if(yesterday.compareTo(lastDelivered) <= 0) showYesterdayProduct[0] = false;
                                        else {
                                            for (int i = 0; i < noDeliveryFrom.size(); i++) {
                                                if (noDeliveryFrom.get(i) != null && noDeliveryTo.get(i) != null) {
                                                    Date start = new Date(0);
                                                    Date end = new Date(0);

                                                    try {
                                                        start = sdf.parse(noDeliveryFrom.get(i));
                                                        end = sdf.parse(noDeliveryTo.get(i));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if ((yesterday.compareTo(start) >= 0) && (yesterday.compareTo(end) <= 0)) {
                                                        showYesterdayProduct[0] = false;
                                                        Log.d(TAG, "Do not deliver and show");
                                                    }
                                                }
                                            }
                                        }


                                    }
                                    else {
                                        showYesterdayProduct[0] = false;
                                    }
                                    //

                                    if (showProduct == true) {
                                        Log.d(TAG, "Inside again");
                                        String key = customerMap.get("ProductUID").toString();
                                        final String quantit = customerMap.get("Quantity").toString();
                                        Log.d(TAG, key);

                                        DocumentReference docRef = db.collection("Products").document(key);
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                        Map<String, Object> productMap = document.getData();
                                                        tot_amount += Double.parseDouble(productMap.get("Rate").toString()) * (Double.parseDouble(quantit));
                                                        CustProduct_Subclass objcust = new CustProduct_Subclass(productMap.get("ProductName").toString(), quantit, Double.toString(Double.parseDouble(productMap.get("Rate").toString()) * (Double.parseDouble(quantit))), productMap.get("Description").toString(), "");
                                                        objcust.setProductID(document.getId());
                                                        objcust.setCustomerID(customerID);
                                                        objcust.setSubscriptionID(doc.getId()); //
                                                        objcust.setVendorID(customerMap.get("VendorUID").toString());
                                                        //YesterdayProduct
                                                        if(showYesterdayProduct[0] == true) {
                                                            CustProduct_Subclass objcust1 = new CustProduct_Subclass(productMap.get("ProductName").toString(), quantit, Double.toString(Double.parseDouble(productMap.get("Rate").toString()) * (Double.parseDouble(quantit))), productMap.get("Description").toString(), "");
                                                            objcust1.setProductID(document.getId());
                                                            objcust1.setCustomerID(customerID);
                                                            objcust1.setSubscriptionID(doc.getId()); //
                                                            objcust1.setVendorID(customerMap.get("VendorUID").toString());
                                                            objcust.setYesterdayProduct(true);
                                                            custproductList.add(objcust1);

                                                        }
                                                        //
                                                        custproductList.add(objcust);
                                                    } else {
                                                        Log.d(TAG, "No such document");
                                                    }
                                                    customerObj.setProductList(custproductList);
                                                    customerObj.setVendorID(user.getUid());

                                                    LinearLayoutManager layoutManager = new LinearLayoutManager(
                                                            holder.productRecyclerView.getContext(),
                                                            LinearLayoutManager.VERTICAL,
                                                            false
                                                    );
                                                   // holder.amount.setText("Rs. " + Double.toString(tot_amount));
                                                    layoutManager.setInitialPrefetchItemCount(customerObj.getProductList().size());
                                                    SubItemAdapter subItemAdapter = new SubItemAdapter(customerObj.getProductList(), c, selectedItems);
                                                    holder.productRecyclerView.setLayoutManager(layoutManager);
                                                    holder.productRecyclerView.setAdapter(subItemAdapter);
                                                    holder.productRecyclerView.setRecycledViewPool(viewPool);
                                                } else {
                                                    Log.d(TAG, "get failed with ", task.getException());
                                                }
                                            }
                                        });
                                    }
                                    else {

                                    }
                                }

                            }
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
            if(subItem.isYesterdayProduct() == true) { //
                holder.checkbox.setVisibility(View.GONE); //
                holder.yesterdayProd.setVisibility(View.VISIBLE);
            } //


            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        SelectedItems selectedobj = new SelectedItems(subItem.getCustomerID(), subItem.getVendorID(), subItem.getProductID());
                        selectedobj.setSubscriptionID(subItem.getSubscriptionID());
                        selectedItems.add(selectedobj);

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
            TextView yesterdayProd;
            public SubItemViewHolder(@NonNull View itemView) {
                super(itemView);
                prod_name = itemView.findViewById(R.id.product_name);
                prod_amnt = itemView.findViewById(R.id.amount_order);
                prod_qty = itemView.findViewById(R.id.quantity_order);
                checkbox = itemView.findViewById(R.id.delivered_checkbox);
                description = itemView.findViewById(R.id.product_description);
                yesterdayProd = itemView.findViewById(R.id.yesterdayProduct);

            }
        }
    }

}