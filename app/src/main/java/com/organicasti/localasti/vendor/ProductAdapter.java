package com.organicasti.localasti.vendor;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.organicasti.localasti.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    Context c;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    List<ProductVendorClass> data;


    //New Addition

    public interface OnItemClickListener {
        public void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }


    public ProductAdapter(List<ProductVendorClass> data) {
        this.data  = data;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        c = parent.getContext();
        View view = inflater.inflate(R.layout.productvendorrecycview, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, final int position) {
        View v = holder.itemView;
        ProductVendorClass prodObj = data.get(position);
        holder.prodname.setText(prodObj.ProductName);
        holder.prodQuant.setText(prodObj.Quantity);
//        Log.d(TAG,"product ved"+" "+prodObj.ProductName+" "+prodObj.Quantity+" "+prodObj.Rate+" "+prodObj.Description);
//        Log.d(TAG,"product ved desc "+prodObj.getDescription());
        holder.prodAmount.setText(prodObj.Rate);
        holder.prodDesc.setText(prodObj.Description);



        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Modify Product");
                final String old_name = holder.prodname.getText().toString();
                View viewInflated = LayoutInflater.from(c).inflate(R.layout.add_product_vendor, (ViewGroup) v.findViewById(R.id.content), false);
                final EditText name = (EditText) viewInflated.findViewById(R.id.product_name_add);
                final EditText quantity = (EditText) viewInflated.findViewById(R.id.product_quantity_add);
                final EditText rate = (EditText) viewInflated.findViewById(R.id.product_rate_add);
                final EditText desc = (EditText) viewInflated.findViewById(R.id.product_desc_add);

                rate.setText(holder.prodAmount.getText());
                name.setText(holder.prodname.getText());
                quantity.setText(holder.prodQuant.getText());
                desc.setText(holder.prodDesc.getText());

                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String prod_name = name.getText().toString();
                        int quant = Integer.parseInt(quantity.getText().toString());
                        double ratee = Double.parseDouble(rate.getText().toString());
                        String productID = data.get(position).productID;
                        final String prod_desc = desc.getText().toString();
                        data.remove(position);
                        data.add(new ProductVendorClass(prod_name, productID, Integer.toString(quant),Double.toString(ratee),prod_desc));
                        notifyDataSetChanged();


                        db.collection("Products").document(productID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });


                        Map<String, Object> product = new HashMap<>();
                        product.put("ProductName", prod_name);
                        product.put("Quantity", quant);
                        product.put("Rate", ratee);
                        product.put("ProductID", productID);
                        product.put("VendorID", user.getUid());
                        product.put("Description", prod_desc);

                        db.collection("Products").document(productID)
                                .set(product)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        holder.mview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);

                final String old_name = holder.prodname.getText().toString();
                builder.setTitle("Delete "+old_name+"?");
                View viewInflated = LayoutInflater.from(c).inflate(R.layout.delete_vendor_product, (ViewGroup) v.findViewById(R.id.content), false);
                //Toast.makeText(c, "Long click", Toast.LENGTH_SHORT).show();
                builder.setView(viewInflated);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String productID = data.get(position).productID;
                        data.remove(position);
                        notifyDataSetChanged();

                        db.collection("Products").document(productID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView prodname, prodQuant,prodAmount,prodDesc;
        View mview;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            prodname = (TextView)itemView.findViewById(R.id.product_name);
            prodQuant = (TextView)itemView.findViewById(R.id.product_quantity);
            prodAmount = (TextView)itemView.findViewById(R.id.product_amount);
            prodDesc=(TextView)itemView.findViewById(R.id.product_descr);

            mview = itemView;

        }
    }
}
