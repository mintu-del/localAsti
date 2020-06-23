package com.organicasti.localasti.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.organicasti.localasti.List;
import com.organicasti.localasti.ProductDetails;
import com.organicasti.localasti.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 First adapter for both Supplier and Product . main items like Vendor names,logo or Product names
 */

public class ProductsAdapters extends RecyclerView.Adapter<ProductsAdapters.viewHolder>
{

    private ArrayList<List> list;
    private Context context;
    private String fragmentName;
    private String vendorUID;
    private ArrayList SelectedList = new ArrayList<>();
    VendorProductAdapter vendorProductAdapter;
//    public String qty;
//    TextView quan;

    ArrayList getSelectedList() {
        return SelectedList;
    }
    FirebaseFirestore db;

    ProductsAdapters(ArrayList<List> list, Context context, String fragmentName) {
        this.list = list;
        this.context = context;
        this.fragmentName = fragmentName;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_vendor_list,parent,false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {


        final boolean[] isExpended = {false};
        holder.Name.setText(list.get(position).getVendorName());
        //holder.vendorLogo.setImageResource(list.get(position).getVendorLogo());
        holder.vendorProductList.setHasFixedSize(true);
        holder.vendorProductList.setLayoutManager(new LinearLayoutManager(context));
        holder.certificatelist.setHasFixedSize(true);
        holder.certificatelist.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        VendorSupplierPageCertificateAdapter vendorSupplierPageCertificateAdapter = new VendorSupplierPageCertificateAdapter(list.get(position).getCertificates(), context);
        holder.certificatelist.setAdapter(vendorSupplierPageCertificateAdapter);
        vendorSupplierPageCertificateAdapter.notifyDataSetChanged();

        //getting data for each product under vendor or product
        final ArrayList<ProductDetails> productDetails = new ArrayList<ProductDetails>();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (fragmentName.equals("supplier"))
        {
            //data for supplies
            holder.Title.setText("Product");
            // ProductDetails products =  new ProductDetails("milk","hbhk","jhbgre");
            // lis.add(products);


            java.util.List<String> daysofDelivery = list.get(position).getDeliveryDays();
            for(int i=0; i<daysofDelivery.size(); i++) {
                TextView days;
                switch(daysofDelivery.get(i)) {
                    case "sunday":
                        holder.sun.setVisibility(View.VISIBLE);
                        break;
                    case "monday":
                        holder.mon.setVisibility(View.VISIBLE);
                        break;
                    case "tuesday":
                        holder.tue.setVisibility(View.VISIBLE);
                        break;
                    case "wednesday":
                        holder.wed.setVisibility(View.VISIBLE);
                        break;
                    case "thursday":
                        holder.thu.setVisibility(View.VISIBLE);
                        break;
                    case "friday":
                        holder.fri.setVisibility(View.VISIBLE);
                        break;
                    case "saturday":
                        holder.sat.setVisibility(View.VISIBLE);
                        break;
                }
            }


            db.collection("Products")
                    .whereEqualTo("VendorID", list.get(position).getVendorID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> m = document.getData();
                                    Log.d(TAG,"vendor id"+ list.get(position).getVendorID());
                                    productDetails.add(new ProductDetails(
                                            m.get("ProductName").toString(),
                                            m.get("Quantity").toString(),
                                            m.get("Rate").toString(),
                                            1,
                                            list.get(position).getVendorID(),
                                            m.get("ProductID").toString(),
                                            m.get("Description").toString()));
                                    Log.d(TAG, "DocumentSnapshot product: " + document.getData());
                                }
                                vendorProductAdapter = new VendorProductAdapter(productDetails);
                                holder.vendorProductList.setAdapter(vendorProductAdapter);
                                vendorProductAdapter.notifyDataSetChanged();
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });


        }
        else {
            //data for products
            holder.Title.setText("Vendor");

        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isExpended[0])
                {
                    holder.detailsLayout.setVisibility(View.GONE);
                    holder.vendorProductList.setVisibility(View.GONE);
                    isExpended[0]=false;
                }
                else
                {
                    holder.vendorProductList.setVisibility(View.VISIBLE);
                    holder.detailsLayout.setVisibility(View.VISIBLE);
                    isExpended[0] = true;

                }

            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentc = new Intent(context, FarmerProfileForCustomer.class);
                intentc.putExtra("VendorID",list.get(position).getVendorID());
                context.startActivity(intentc);
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{

        TextView Name,Title;
        ImageView vendorLogo;
        RecyclerView vendorProductList;
        RelativeLayout detailsLayout;
        RelativeLayout itemLayout;
        Button profile;
        TextView sun,mon,tue,wed,thu,fri,sat;
        RecyclerView certificatelist;

        viewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            //vendorLogo = itemView.findViewById(R.id.vendor_logo);
            itemLayout = itemView.findViewById(R.id.item_layout);
            vendorProductList = itemView.findViewById(R.id.vendor_product_list);
            detailsLayout = itemView.findViewById(R.id.details_layout);
            Title = itemView.findViewById(R.id.name_of_recycler_item);
            profile = itemView.findViewById(R.id.show_farmer_profile);
            sun = itemView.findViewById(R.id.day_sun1);
            mon = itemView.findViewById(R.id.day_mon1);
            tue = itemView.findViewById(R.id.day_tue1);
            wed = itemView.findViewById(R.id.day_wed1);
            thu = itemView.findViewById(R.id.day_thu1);
            fri = itemView.findViewById(R.id.day_fri1);
            sat = itemView.findViewById(R.id.day_sat1);
            certificatelist = itemView.findViewById(R.id.supplierfragmentcertificatelogos);

        }
    }



    /**
     Second adapter for both one time and subscription for child items
     */

    public class VendorProductAdapter extends RecyclerView.Adapter<VendorProductAdapter.viewHolder>{

        private ArrayList<ProductDetails> productDetails;

        // private ArrayList<ProductDetails> productDetails;

        VendorProductAdapter(ArrayList<ProductDetails> productDetails) {
            this.productDetails = productDetails;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cust,parent,false);

//            quan=view.findViewById(R.id.quantity_count);
//            Log.d(TAG,"quan= "+quan.getText());
            return new viewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final viewHolder holder, final int position)
        {
            //if(productDetails.get(position) != null) {
            final String Name = productDetails.get(position).getName();
            final String Price = productDetails.get(position).getProductPrice();
            final String Quantity = productDetails.get(position).getProductQuantity();
            final int MinPackingQuantity = productDetails.get(position).getMinPackingQuantity();

            final String Description=productDetails.get(position).getDescription();
            Log.d(TAG,"DESCRIPTION "+Description);
            //for delivery options
            final String productID=productDetails.get(position).getProductID();
            final String vendorId=productDetails.get(position).getVendorID();





            //final String QuantityType = productDetails[position].getQuantityType();
            holder.Name.setText(Name);
            holder.Price.setText(Price);// + "/" + MinPackingQuantity);
            holder.Quantity.setText("0");
            holder.Description.setText(Description);


            holder.Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    holder.Quantity.setText(Count(holder.Quantity.getText().toString(), MinPackingQuantity));
//                    Log.d(TAG,"quantity hold= "+holder.Quantity.getText());
                    int maxQ=Integer.parseInt(Quantity);
                    int setQ=Integer.parseInt(holder.Quantity.getText().toString());
                    Log.d(TAG,"int comp maxQ setQ"+maxQ+" "+setQ);

                    if(maxQ>setQ){
                        Log.d(TAG,"int comp maxQ < setQ"+maxQ+" "+setQ);
                        holder.Quantity.setText(Count(holder.Quantity.getText().toString(), MinPackingQuantity));
                        Log.d(TAG,"Comp to hold= "+holder.Quantity.getText());

                    }else{
                        Toast.makeText(context, "Max limit reached", Toast.LENGTH_SHORT).show();
                    }


                    Log.d(TAG,"Comp to"+holder.Quantity.getText().toString().compareTo(Quantity)+" quan"+Quantity);

                }
            });
            holder.Remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    holder.Quantity.setText(Count(holder.Quantity.getText().toString(), -MinPackingQuantity));
//                    Log.d(TAG,"quantity hold= "+holder.Quantity.getText());
                    if(holder.Quantity.getText().toString().equals("1")){
                        Log.d(TAG,"quantity hold 0cond= "+holder.Quantity.getText());
                        holder.Quantity.setText("0");
                    }
                    else if(holder.Quantity.getText().toString().equals("0")){
                        holder.Quantity.setText("0");
                    }
                    else{
                        holder.Quantity.setText(Count(holder.Quantity.getText().toString(), -MinPackingQuantity));
                        Log.d(TAG,"quantity hold= "+holder.Quantity.getText());}

                }
            });
            holder.Checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        SelectedList.add(Name);

//                        Log.d(TAG,"quantity hold check= "+holder.Quantity.getText());

                        SelectedList.add(holder.Quantity.getText().toString());
                        SelectedList.add(Price + " ");
                        SelectedList.add(vendorId);
                        SelectedList.add(Description);
                        SelectedList.add(productID);


                        holder.Add.setVisibility(View.INVISIBLE);
                        holder.Remove.setVisibility(View.INVISIBLE);
//                        holder.Add.setEnabled(false);




                    } else {
//                        Log.d(TAG,"quantity hold check2= "+holder.Quantity.getText());

                        for(int i=0; i<SelectedList.size(); i += 6) {
                            if(SelectedList.get(i+5).equals(productID)) {
                                int indextoremove = i;
                                for(int j=0; j<6; j++) {
                                    SelectedList.remove(indextoremove);
                                }
                                break;
                            }
                        }
                      /*  SelectedList.remove(1);
                        SelectedList.remove(Name);
                        SelectedList.remove(Price + " ");
                        SelectedList.remove(vendorId.toString());
                        SelectedList.remove(productID); */
                        holder.Add.setVisibility(View.VISIBLE);
                        holder.Remove.setVisibility(View.VISIBLE);
                    }
                    // productDetails.setOrderList(SelectedList);
                }
            });
        }

        // }

        private String Count(String  Count, int Value)
        {
            int count = Integer.parseInt(Count.replace("ml","").trim());
            count = count + Value;
            if (!(count <= Value || count <= 0))
            {
                return String.valueOf(count);
            }
            else {
                return String.valueOf(Value).replace("-","");
            }
        }

        @Override
        public int getItemCount() {
            return productDetails.size();
        }

        class viewHolder extends RecyclerView.ViewHolder{
            TextView Name,Quantity,Price,Description;
            ImageButton Add,Remove;
            CheckBox Checkbox;
            viewHolder(@NonNull View itemView) {
                super(itemView);
                Name = itemView.findViewById(R.id.name);
                Quantity = itemView.findViewById(R.id.quantity_count);
                Price = itemView.findViewById(R.id.product_price);
                Add = itemView.findViewById(R.id.add);
                Remove = itemView.findViewById(R.id.remove);
                Checkbox = itemView.findViewById(R.id.checkbox);
                Description=itemView.findViewById(R.id.description);

            }
        }



    }

    public class VendorSupplierPageCertificateAdapter extends RecyclerView.Adapter<VendorSupplierPageCertificateAdapter.CertificateViewHolder> {

        ArrayList<String> Certificatetype;
        ArrayList<String> certi, URL;
        Context c;

        public VendorSupplierPageCertificateAdapter(ArrayList<String> certificatetype, Context c) {
            this.Certificatetype = certificatetype;
            this.c = c;
        }
        @NonNull
        @Override
        public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.farmer_certificate_supplier_fragment, parent, false);
            return new CertificateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final CertificateViewHolder holder,final int position) {
            /*db.collection("Certificates").document("PoLY2uqdZpLphFtsKLvd")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            certi = (ArrayList<String>)document.get("Type");
                            URL = (ArrayList<String>)document.get("URL");

                            for(int i=0; i<certi.size(); i++) {
                                if(certi.get(i).equals(Certificatetype.get(position)) == true) {
                                    new SmallImageLoadTask(URL.get(i), holder.img).execute();
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });*/
            if(Certificatetype.get(position).equals("NPOP")) {
                holder.img.setImageResource(R.drawable.img1);

            }
            else if(Certificatetype.get(position).equals("PGS Green")) {
                holder.img.setImageResource(R.drawable.img0);

            }
            else if(Certificatetype.get(position).equals("PGS Organic")) {
                holder.img.setImageResource(R.drawable.img2);
            }
            else if(Certificatetype.get(position).equals("Javic Bharat")) {
                holder.img.setImageResource(R.drawable.img3);
            }
            else if(Certificatetype.get(position).equals("FSSAI")) {
                holder.img.setImageResource(R.drawable.img4);
            }

        }

        @Override
        public int getItemCount() {
            return Certificatetype.size();
        }

        public class CertificateViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            public CertificateViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.certificate_small_logo);
            }
        }

        public class SmallImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

            private String url;
            private ImageView imageView;

            public SmallImageLoadTask(String url, ImageView imageView) {
                this.url = url;
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    java.net.URL urlConnection = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlConnection
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                imageView.setImageBitmap(result);
            }

        }

    }
}

