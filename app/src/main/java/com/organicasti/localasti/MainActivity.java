package com.organicasti.localasti;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.organicasti.localasti.customer.Customer_Act;
import com.organicasti.localasti.vendor.Vendor_Activity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String TAG = "Phone Number Verification";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean isCodeSent = false;
    private boolean isAuthProcessing = false;
    private FirebaseUser user;
    public ProgressDialog progressDialog;
    public long phoneNoDetails;
    private LinearLayout statusLayout;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText phoneNumber = findViewById(R.id.etName);
        final Button login = findViewById(R.id.button);
        final Button resendCode = findViewById(R.id.resend_button);
        final EditText verificationCode = findViewById(R.id.verification_code);
        final TextView Status = findViewById(R.id.status);

         //A dialog showing a progress indicator and an optional text message or view. Only a text message or a view can be used at the same time.
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        statusLayout = findViewById(R.id.status_layout);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneNumber.getText().toString();
                if (number.length() == 10) {
                    statusLayout.setVisibility(View.VISIBLE);
                    resendVerificationCode("+91" + number, mResendToken);
                } else
                    Toast.makeText(MainActivity.this, "Enter valid number", Toast.LENGTH_SHORT).show();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( phoneNumber.getText().toString().length()!=0){


                //login.setEnabled(false);
                String number = phoneNumber.getText().toString();
                Log.d(TAG,"lenght of num"+number.length());
                phoneNoDetails = Long.parseLong(number);
                if (isCodeSent) {
                    //taking verification code from user
                    String code = verificationCode.getText().toString();
                    if (code.length() >= 6) {
                        progressDialog.show();
                        progressDialog.setMessage("Checking... OTP");
                        verifyPhoneNumberWithCode(mVerificationId, code);
                    } else {
                        Toast.makeText(MainActivity.this, "Enter valid code", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (number.length() == 10) {
                    statusLayout.setVisibility(View.VISIBLE);
                    startPhoneNumberVerification("+91" + number);
                }else {
                    Toast.makeText(MainActivity.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                }
                }else{
                    Toast.makeText(MainActivity.this, "Enter your mobile number", Toast.LENGTH_SHORT).show();

                }

            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential)
            {
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                // login button is not clickable
                login.setEnabled(true);
                statusLayout.setVisibility(View.GONE);
                mVerificationInProgress = false;
                verificationCode.setText(credential.getSmsCode());
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException ex)
            {
                progressDialog.dismiss();
                login.setEnabled(true);
                mVerificationInProgress = false;
                Log.d("Firebase Exception", String.valueOf(ex));
                if (ex instanceof FirebaseAuthInvalidCredentialsException)
                {
                    verificationCode.setError("Invalid phone number");
                }
                else if (ex instanceof FirebaseTooManyRequestsException)
                {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Quota exceeded.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Enter valid code", Toast.LENGTH_SHORT).show();
                }

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token)
            {
                login.setEnabled(true);
                verificationCode.setVisibility(View.VISIBLE);
                resendCode.setVisibility(View.VISIBLE);
                mVerificationId = verificationId;
                mResendToken = token;
                isCodeSent = true;
                login.setText("Verify otp");
                Status.setText("Waiting... for OTP");
            }
        };

    }


    public void openActivityVendor(){
        Intent intent=new Intent(this, Vendor_Activity.class);
        startActivity(intent);
    }

    public void openActivityCustomer() {
        Intent intent = new Intent(this, Customer_Act.class);
        startActivity(intent);
    }

    /*
    public void openActivityAdmin() {
        Intent intent = new Intent(this, Admin_Activity.class);
        startActivity(intent);
    }*/
    @Override
    protected void onStart() {
        super.onStart();
        if (user != null)
        {
            checkUserProfile();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        isAuthProcessing = false;
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                            checkUserProfile();
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Log.d(TAG,"InValidDetails",task.getException());
                                statusLayout.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Enter valid code", Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        isAuthProcessing = true;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        isAuthProcessing = true;
        //verifyPhoneNumber(String phoneNumber, long timeout, TimeUnit unit, Activity activity, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks)
        //Starts the phone number verification process for the given phone number.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    //updating userType in SharedPreferences
    private void setVendor(boolean vendor)
    {
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isVendor", vendor).apply();
    }

    private boolean getVendor()
    {
        return  getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isVendor", false);
    }

    //customer
    private void setCustomer(boolean customer)
    {
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isCustomer", customer).apply();
    }

    private boolean getCustomer()
    {
        return  getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isCustomer", false);
    }


    private void GotoActivity()
    {
        if (getVendor()) {
            openActivityVendor();
        } else if (getCustomer()){
            openActivityCustomer();
        }else{
            startActivity(new Intent(getApplicationContext(),UserDetails.class));
            finish();
        }
    }


    private void checkUserProfile()
    {

        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        DocumentReference docRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                progressDialog.dismiss();
                if(documentSnapshot.exists())
                {
                    String UserType = Objects.requireNonNull(documentSnapshot.get("UserType")).toString();
                    if (UserType.equals("Vendor"))
                    {
//                        ArrayList<String> group = (ArrayList<String>) documentSnapshot.get("DeliveryLoaction");
//                       int k=100;
//                        if(group==null){
//                            k=0;}
//                        else{
//                            k=1;}
//                        Log.println(k,"Group","group location");
                        openActivityVendor();
                    }
                    else if(UserType.equals("Customer")) {
                       openActivityCustomer();
                    }
                    else if (UserType.equals("Admin")){
//                        startActivity(new Intent(MainActivity.this,Admin.class));
//                        openActivityAdmin();
//                        finish();
                    }
                    else{
                        startActivity(new Intent(getApplicationContext(),UserDetails.class));
                        finish();

                    }

                }
                else{
                    startActivity(new Intent(getApplicationContext(),UserDetails.class));
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isAuthProcessing) {
            finishAffinity();
        }
        else {
            super.onBackPressed();
        }
    }
}
