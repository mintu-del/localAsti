package com.organicasti.localasti.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.organicasti.localasti.R;
import static androidx.constraintlayout.widget.Constraints.TAG;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FarmerCertificateAdapter extends RecyclerView.Adapter<FarmerCertificateAdapter.CertificateViewHolder> {
    List<String> certificateType;
    List<String> certificateNo;
    ArrayList<String> certi, URL;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    Context context;
    public FarmerCertificateAdapter(List<String> certificateType, List<String> certificateNo, Context context) {
        this.context = context;
        this.certificateType = certificateType;
        this.certificateNo = certificateNo;
        db = FirebaseFirestore.getInstance();


    }
    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.certificate_row_farmer_profile, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final  CertificateViewHolder holder, final int position) {
        final String certificate = certificateNo.get(position);
        holder.certificateID.setText(certificate);
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Please wait...");


        if(certificateType.get(position).equals("NPOP")) {
            holder.img.setImageResource(R.drawable.img1);

        }
        else if(certificateType.get(position).equals("PGS Green")) {
            holder.img.setImageResource(R.drawable.img0);

        }
        else if(certificateType.get(position).equals("PGS Organic")) {
            holder.img.setImageResource(R.drawable.img2);
        }
        else if(certificateType.get(position).equals("Javic Bharat")) {
            holder.img.setImageResource(R.drawable.img3);
        }
        else if(certificateType.get(position).equals("FSSAI")) {
            holder.img.setImageResource(R.drawable.img4);
        }

       /* db.collection("Certificates").document("PoLY2uqdZpLphFtsKLvd")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        certi = (ArrayList<String>)document.get("Type");
                        URL = (ArrayList<String>)document.get("URL");

                        for(int i=0; i<certi.size(); i++) {
                            if(certi.get(i).equals(certificateType.get(position)) == true) {
                                new ImageLoadTask(URL.get(i), holder.img).execute();
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
        }); */




    }

    @Override
    public int getItemCount() {
        return certificateNo.size();
    }

    public class CertificateViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView certificateID;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.certificate_logo);
            certificateID = itemView.findViewById(R.id.certificate_id);
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
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
