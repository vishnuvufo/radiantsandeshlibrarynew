package com.mountfox.Delhivery_Pay;

import static com.mountfox.sharedPref.ConstantValues.TAG_PHONENUMBER;
import static com.mountfox.sharedPref.ConstantValues.TAG_TRANSID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.mountfox.CaptureActivityPortrait;
import com.mountfox.Delhivery_Pay.Poojo_Class.DelhiveryOtpResponse;
import com.mountfox.Delhivery_Pay.Poojo_Class.OtpVerifyResponse;
import com.mountfox.Home;
import com.mountfox.ModeOfTransactionActivity;
import com.mountfox.R;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.sharedPref.SharedPreference;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryOtpVerify extends AppCompatActivity {
    public static final String TAG = DeliveryOtpVerify.class.getSimpleName();
    ApiInterface apiInterface;


    private EditText emp_id;
    private LinearLayout submit;
    private ImageView back;
    private TextView qr_scan,resend;
    private ProgressDialog progressDialog;
    private IntentIntegrator qrScan;
    private String transid = "",phone="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_otp_diag);
        apiInterface = Constants.getdelivery().create(ApiInterface.class);
        progressDialog = new ProgressDialog(DeliveryOtpVerify.this);
        qrScan = new IntentIntegrator(DeliveryOtpVerify.this);



        transid = SharedPreference.getDefaults(DeliveryOtpVerify.this, TAG_TRANSID);
        phone = SharedPreference.getDefaults(DeliveryOtpVerify.this, TAG_PHONENUMBER);
        Log.e(TAG,"............transid >>"+transid);
        Log.e(TAG,"............phone >>"+phone);



        resend = findViewById(R.id.tv_resendd);
        submit = findViewById(R.id.ln_submitt);
        emp_id = findViewById(R.id.employeeid_enter);
        back = findViewById(R.id.back);
        qr_scan = findViewById(R.id.qr_scan);


        qr_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.setPrompt("Scan a QR Code");
                qrScan.setCameraId(0); // Use a specific camera of the device
                qrScan.setOrientationLocked(true);
                qrScan.setBeepEnabled(false);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strotp = emp_id.getText().toString();
                if (strotp.equals("") || strotp.isEmpty()) {
                    Toast.makeText(DeliveryOtpVerify.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    new onDelhiveryOtpVerifyTask(DeliveryOtpVerify.this, strotp).execute();
                }
            }
        });


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new onRESENDOtpTask(DeliveryOtpVerify.this, phone,transid).execute();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryOtpVerify.this, Delivery_EntryCash.class);
                startActivity(intent);
                finish();
            }
        });

    }






    private class onDelhiveryOtpVerifyTask extends AsyncTask<String, String, String> {
        private Activity activity;
        private String strotp,strphone;

        public onDelhiveryOtpVerifyTask(Activity activity, String transid) {
            this.activity = activity;
            this.strotp = transid;
            this.strphone = strphone;

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JsonObject obj = new JsonObject();
                JsonObject DelhiveryBody = new JsonObject();
                JsonObject DelhiveryHeader = new JsonObject();
                JsonObject DelhiveryRequest = new JsonObject();
                DelhiveryBody.addProperty("transactionId",transid);
                DelhiveryBody.addProperty("phone",phone);
                DelhiveryBody.addProperty("otp",strotp);
                obj.add("DelhiveryBody",DelhiveryBody);
                DelhiveryHeader.addProperty("serviceRequestVersion","1.0");
                DelhiveryHeader.addProperty("serviceRequestId","DelhiveryVerifyOtp");
                obj.add("DelhiveryHeader",DelhiveryHeader);
                DelhiveryRequest.add("DelhiveryRequest",obj);
                Log.e(TAG,".otp verify..Json >>"+DelhiveryRequest);
                Call<OtpVerifyResponse> tracking = apiInterface.getotpverify(DelhiveryRequest);
                tracking.enqueue(new Callback<OtpVerifyResponse>() {
                    @Override
                    public void onResponse(Call<OtpVerifyResponse> call, Response<OtpVerifyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().getCode().equals("007")) {
                                Intent intent=new Intent(DeliveryOtpVerify.this,Home.class);
                                startActivity(intent);
                                Log.e(TAG, "inside ======  >>>> " + response.body().getStatus().getMsg());
                                Log.e(TAG, "inside ======  >>>> " + response.body().getStatus().getOtp());
                                Toast.makeText(activity,  "Sucessfully Completed", Toast.LENGTH_SHORT).show();
                            }

                            else  if (response.body().getCode().equals("008")) {
//                                Log.e(TAG, "inside ======  >>>> " + response.body().getStatus().getOtp());
                                Toast.makeText(activity,  "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                            else   if (response.body().getCode().equals("009")) {
                                Log.e(TAG, "inside ======  >>>> " + response.body().getStatus().getOtp());
                                Toast.makeText(activity,  "Try Again Later", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.e(TAG, "outside ======  >>>> " + response.body().getStatus().getMsg());
                                Toast.makeText(activity,  "Invalid OTP", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "outside>>>> " + response);
                            Toast.makeText(activity, "Try again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpVerifyResponse> call, Throwable t) {
                        progressDialog.cancel();
                        Log.e(TAG, "t ---------->>>> "+t.getMessage() );

                        Toast.makeText(activity,  "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "e ---------->>>> "+e.getMessage() );

                progressDialog.cancel();
                Toast.makeText(activity, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

////////////////  *********** Resend ***************

    private class onRESENDOtpTask extends AsyncTask<String, String, String> {
        private Activity activity;
        private String strphone,transid;

        public onRESENDOtpTask(Activity activity, String strphone, String transid) {
            this.activity = activity;
            this.strphone = strphone;
            this.transid = transid;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                JsonObject obj = new JsonObject();
                JsonObject DelhiveryBody = new JsonObject();
                JsonObject DelhiveryHeader = new JsonObject();
                JsonObject DelhiveryRequest = new JsonObject();
                DelhiveryBody.addProperty("phone",strphone);
                DelhiveryBody.addProperty("transactionId",transid);
                obj.add("DelhiveryBody",DelhiveryBody);
                DelhiveryHeader.addProperty("serviceRequestVersion","1.0");
                DelhiveryHeader.addProperty("serviceRequestId","DelhiveryOtp");
                obj.add("DelhiveryHeader",DelhiveryHeader);
                DelhiveryRequest.add("DelhiveryRequest",obj);
                Log.e(TAG,"DelhiveryOtp ---------- >>"+DelhiveryRequest);
                Call<DelhiveryOtpResponse> tracking = apiInterface.getdeliveryotp(DelhiveryRequest);
                tracking.enqueue(new Callback<DelhiveryOtpResponse>() {
                    @Override
                    public void onResponse(Call<DelhiveryOtpResponse> call, Response<DelhiveryOtpResponse> response) {
                        if (response.code() == 200) {
                            String phone = response.body().getStatus().getPhone();
                            String trans = response.body().getStatus().getTransactionId();

                            if (response.body().getCode().equals("003")){
//                                activity.startActivity(new Intent(activity, DeliveryOtpVerify.class));
                                Toast.makeText(DeliveryOtpVerify.this, "OTP Sended Register Mobile Number", Toast.LENGTH_SHORT).show();

                                Log.e(TAG, "otp one>>>> " + response.body().getStatus().getOtp());
                                SharedPreference.setDefaults(DeliveryOtpVerify.this, TAG_TRANSID,response.body().getStatus().getTransactionId());
                                SharedPreference.setDefaults(DeliveryOtpVerify.this, TAG_PHONENUMBER,response.body().getStatus().getPhone());

                            }
                            else if (response.body().getCode().equals("006")){
                                Toast.makeText(DeliveryOtpVerify.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "otp one>>>> " + response.body().getStatus().getOtp());
                            }
                            else {
                                Log.e(TAG,"FAILURE --->>>> "+response.body().getStatus());
                                Toast.makeText(DeliveryOtpVerify.this, response.body().getStatus().getMsg(), Toast.LENGTH_SHORT).show();

                            }



                        } else {
                            Log.e(TAG, "outside>>>> " + response);
                            Toast.makeText(activity, "Try again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }

                    @Override
                    public void onFailure(Call<DelhiveryOtpResponse> call, Throwable t) {
                        progressDialog.cancel();
                        Toast.makeText(DeliveryOtpVerify.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                progressDialog.cancel();
                Toast.makeText(activity, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }
    
    
    
    





    boolean doubleBackToExitPressedOnce = false;
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Again To Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }





}