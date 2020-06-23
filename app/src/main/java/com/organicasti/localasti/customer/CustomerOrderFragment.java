package com.organicasti.localasti.customer;

    import android.app.ProgressDialog;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QueryDocumentSnapshot;
    import com.google.firebase.firestore.QuerySnapshot;
    import com.organicasti.localasti.R;
    import com.organicasti.localasti.vendor.OrderData;

    import java.text.DateFormat;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.Map;

    import static androidx.constraintlayout.widget.Constraints.TAG;

    public class CustomerOrderFragment extends Fragment {

        FirebaseFirestore db;
        RecyclerView rv;
        ArrayList<OrderData> orderDataList;
        private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        TextView orderplaced;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view =  inflater.inflate(R.layout.fragment_showorders_customer, container, false);

            orderDataList = new ArrayList<>();
            db = FirebaseFirestore.getInstance();

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            rv = (RecyclerView)view.findViewById(R.id.recent_orders);

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Orders");
            progressDialog.setCancelable(false);

            Log.d(TAG, "Hello");

            progressDialog.show();


            db.collection("Orders_OneTime").whereEqualTo("CustomerID", user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Date> dateArrayList = new ArrayList<>();
                                Log.d(TAG, "Hello2");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> m = document.getData();
                                    Log.d(TAG, document.getId()+"");

                                    Date date = new Date(0);
                                    try {
                                        date = sdf.parse(m.get("Date").toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    OrderData obj = new OrderData(m.get("ProductName").toString(), m.get("Quantity").toString(), Double.toString(Double.parseDouble(m.get("Amount").toString())*Integer.parseInt(m.get("Quantity").toString())), date, "", m.get("VendorID").toString(), Boolean.parseBoolean(m.get("Delivered").toString())?"Delivered":"Not Delivered");
                                    obj.setProductDescription(m.get("Description").toString());
                                    obj.setOrderID(document.getId());
                                    orderDataList.add(obj);
                                    dateArrayList.add(date);
                                    int n = dateArrayList.size();
                                    while(n-2 >= 0 && dateArrayList.get(n-2).compareTo(dateArrayList.get(n-1)) < 0) {
                                        Date temp = dateArrayList.get(n-2);
                                        OrderData temp1 = orderDataList.get(n-2);
                                        dateArrayList.set(n-2, dateArrayList.get(n-1));
                                        orderDataList.set(n-2, orderDataList.get(n-1));
                                        dateArrayList.set(n-1, temp);
                                        orderDataList.set(n-1, temp1);
                                        n--;
                                    }
                                }
                                progressDialog.dismiss();

                                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                                ShowOrdersApdapter showOrdersApdapter = new ShowOrdersApdapter(orderDataList, getContext());
                                rv.setAdapter(showOrdersApdapter);
                                showOrdersApdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });



            return view;
        }
    }
