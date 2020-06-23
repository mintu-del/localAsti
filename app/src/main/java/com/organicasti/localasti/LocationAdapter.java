package com.organicasti.localasti;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    public static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    ArrayList<String> data;
    FirebaseFirestore db;
    Context c;
    public LocationAdapter(ArrayList<String> locationName, Context c) {
        data = locationName;
        this.c = c;
    }
    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.location_row_admin, parent,false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocationViewHolder holder, int position) {
        final String location = data.get(position);
        holder.locationName.setText(location);
        db.collection("Orders_OneTime").whereEqualTo("Delivered", false).whereEqualTo("Location", location)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                holder.deleteButton.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Subscriptions").whereEqualTo("Location", location)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot doc: task.getResult()) {
                                final Map<String, Object> m = doc.getData();
                                Date dater;

                                dater = new Date(System.currentTimeMillis());
                                try {
                                    dater = sdf.parse(sdf.format(dater));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Date toDate = new Date(0);

                                try {
                                    toDate = sdf.parse(m.get("To").toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if(dater.compareTo(toDate) <= 0) {
                                    holder.deleteButton.setVisibility(View.GONE);
                                }

                            }
                        }
                    }
                });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Locations").document("QOeRVXqobfW3ZUeR9K9y")
                        .update("location", FieldValue.arrayRemove(location));
                data.remove(location);
                Toast.makeText(c, location+" deleted !", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView locationName;
        Button deleteButton ;
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationName = (TextView)itemView.findViewById(R.id.location_name);
            deleteButton = (Button)itemView.findViewById(R.id.delete_location);
        }
    }
}
