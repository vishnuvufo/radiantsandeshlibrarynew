package com.mountfox;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mountfox.Delhivery_Pay.Delivery_EntryCash;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.Services.dataRequest.DeliveryTransListRequestData;
import com.mountfox.Services.serviceRequest.ServiceRequestPOSTImpl;
import com.mountfox.otpresponse.OtpResponse;
import com.mountfox.response.BankDepositBankname;
import com.mountfox.response.Data;
import com.mountfox.response.PickupStandardRemarks;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mountfox.Connectivity.isConnected;
import static com.mountfox.sharedPref.ConstantValues.TAG_CEID;
import static com.mountfox.sharedPref.ConstantValues.TAG_CE_MOBILE;
import static com.mountfox.sharedPref.ConstantValues.TAG_TRANSID;

public class Login extends Activity implements OnClickListener, GetJson.CallbackInterface {
    EditText pinno_text,dob_text;
    ImageView login_button;
    String pin_no = "", ce_name = "", ce_id = "",ce_mobile="", location = "", email_id = "",dob="", dateString="",dobreverse="";
    public static String ce_id_main;
    String imei = "", simno = "";
    DbHandler dbHandler;
    Cursor cursor;
    List<BasicNameValuePair> params;
    Intent home_activity;
    private static final String TAG = "Login";
    ProgressDialog progressDialog;
    PendingTransactionProgress pendingTransactionProgress;
    SharedPreferences prefs;
    Context mContext;
    PreferenceHelper helper;
    String mobileDetails = "",ConfigIp="",Urlstatus="",URL = "";
    String currentVersion;
    private GPSTracker gpsTracker;
    double latitude = 12.982733625, longitude = 80.252031675;
    ApiInterface apiInterface;
    List<String> Standardremarks = new ArrayList<>();
    List<String> StandardremarksList = new ArrayList<>();



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.login);
        apiInterface = Constants.getClient().create(ApiInterface.class);
        prefs = getSharedPreferences("mountfox", Context.MODE_PRIVATE);
        mContext = this;
        helper = new PreferenceHelper();
        Clearcache(Login.this);
       // mobileDetails = Utils.CreateMobileInformationString(mContext);
        initializeComponents();

        GetPickup_RemarksAsyncTask getPickup_remarksAsyncTask = new GetPickup_RemarksAsyncTask();
        getPickup_remarksAsyncTask.execute();

        GetStandardPickup_RemarksAsyncTask getstandardPickup_remarksAsyncTask = new GetStandardPickup_RemarksAsyncTask();
        getstandardPickup_remarksAsyncTask.execute();

        GetAccountDetailsAsyncTask getAccountDetailsAsyncTask = new GetAccountDetailsAsyncTask();
        getAccountDetailsAsyncTask.execute();
        //latlong
        gpsTracker = new GPSTracker(Login.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
           Log.e(TAG,"latitude ->"+latitude);
           Log.e(TAG,"longitude ->"+longitude);
        }

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(Login.this, new
                    String[]{android.Manifest.permission.CAMERA}, 201);
        }



    }

    @SuppressLint("MissingPermission")
    public void initializeComponents() {
        pinno_text = (EditText) findViewById(R.id.pinno_text);
        dob_text = (EditText)findViewById(R.id.dob_text);
        login_button = (ImageView) findViewById(R.id.login_button);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating Credentials");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
        login_button.setOnClickListener(this);
        dob_text.setOnClickListener(this);
        dbHandler = new DbHandler(Login.this);
        //  dbHandler.createTables();
        dbHandler.deleteTimeoutTransaction();
        home_activity = new Intent(this, Home.class);

        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Config.DEBUG) {
            //Log.i("Imei:", imei);
        }
    }

    @SuppressWarnings("unchecked")
    public void onClick(View v) {
        if (v == login_button) {
            pin_no = pinno_text.getText().toString();
            dobreverse = dob_text.getText().toString();

            try {
                //current date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date objDate = dateFormat.parse(dobreverse);
                //Expected date format
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                dob = dateFormat2.format(objDate);
                Log.d("Date Format:", "Final Date:"+dob);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            //current date format
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//            Date objDate = dateFormat.parse(dobreverse);
//            //Expected date format
//            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//            String finalDate = dateFormat2.format(objDate);
//
//            Log.d("Date Format:", "Final Date:"+finalDate);


            String pickupRemarks = helper.getPickupRemark2(mContext);
            if (pickupRemarks == null || pickupRemarks.isEmpty() || pickupRemarks.equals("")) {
                GetStandardPickup_RemarksAsyncTask obj = new GetStandardPickup_RemarksAsyncTask();
                obj.execute();
            }
            if (pickupRemarks == null || pickupRemarks.isEmpty() || pickupRemarks.equals("")) {
                GetPickup_RemarksAsyncTask obj = new GetPickup_RemarksAsyncTask();
                obj.execute();
            }

            if (pin_no == null | pin_no.equals("")) {
                Toast.makeText(getApplicationContext(), "Please Enter Pin No", Toast.LENGTH_SHORT).show();
            }else if(dob == null | dob.equals("")){
                Toast.makeText(getApplicationContext(), "Please Select Date of Birth", Toast.LENGTH_SHORT).show();
            }else {
                // test 27.7.17
                if (Utils.isInternetAvailable(getApplicationContext())) {
                    progressDialog.show();
                    params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("opt", "login"));
                    params.add(new BasicNameValuePair("pin_no", ""+pin_no));
                    params.add(new BasicNameValuePair("dob", ""+dob));
                    params.add(new BasicNameValuePair("IMIE", ""));
                    params.add(new BasicNameValuePair("simno", ""));
                    params.add(new BasicNameValuePair("mobileDetails", ""));
                    params.add(new BasicNameValuePair("final", "1"));
                    GetJson getJson = new GetJson(Login.this,this);
                    getJson.execute(params);
                    System.out.println("LOGIN REQUEST  >>>" + params);
                } else {
                    SQLiteDatabase db = getApplicationContext().openOrCreateDatabase("mountfox", Context.MODE_PRIVATE, null);
                    String sql = "select *from login where pin_no=" + pin_no;
                    cursor = db.rawQuery(sql, null);
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        ce_name = cursor.getString(cursor.getColumnIndex("ce_name"));
                        ce_id = cursor.getString(cursor.getColumnIndex("ce_id"));
                        location = cursor.getString(cursor.getColumnIndex("location"));
                        email_id = cursor.getString(cursor.getColumnIndex("email_id"));
                        cursor.close();
                        db.close();
                        if (Config.DEBUG) {
                            //Log.d(TAG, " Values : Ce_name,ce_id,location,email_id: "
                            //      + ce_name + "," + ce_id + "," + location + ","
                            //       + email_id);
                        }

    /// auto update
                        if (!Config.version.equalsIgnoreCase(prefs.getString("version", ""))) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                            alertDialog.setMessage("Please Update Your App To Newer Version");
                            alertDialog.setButton(DialogInterface.BUTTON1, "Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("market://details?id=com.mountfox"));
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    alertDialog.dismiss();
                                    finish();
                                }
                            });
                            alertDialog.show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authenticated Success", Toast.LENGTH_SHORT).show();
                            home_activity.putExtra("ce_id", ce_id);
                            ce_id_main = ce_id;
                            finish();
                            startActivity(home_activity);
                        }
                    } else {
                        cursor.close();
                        db.close();
                    }
                }
            }
        }
        if(v==dob_text){
            showDatePickDialog();
        }
    }

    private void showDatePickDialog() {
        // calender class's instance and get current date , month and year from calender
        int mYear, mMonth, mDay;
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR); // current year
        mMonth = calendar.get(Calendar.MONTH); // current month
        mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day

        // date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(Login.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String dateString = dateFormat.format(calendar.getTime());
                        SimpleDateFormat dateFormatreverse = new SimpleDateFormat("dd-MM-yyyy");
                        String dateStringreverse = dateFormatreverse.format(calendar.getTime());
                        dob_text.setText(dateStringreverse);
                    }
                }, mYear, mMonth, mDay);
        try {
            long time = System.currentTimeMillis();
            datePickerDialog.getDatePicker().setMaxDate(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        datePickerDialog.show();

    }

    public void onRequestCompleted(JSONObject object) {
        progressDialog.dismiss();
        if (object != null) {
            if (Config.DEBUG) {
                Log.d(TAG, "=================" + object.toString());
            }
            try {
                String msg = object.getString("msg");
                if (Config.DEBUG) {
                    //Log.d(TAG, "Msg:" + msg);
                }

                if (msg.equals("success")) {
                    prefs.edit().putString("version", object.getString("version")).commit();
                    ce_name = object.getString("ce_name");
                    ce_id = object.getString("ce_id");
               //     ce_mobile = object.getString("mobile");
                    email_id = object.getString("email_id");
                    location = object.getString("location");
                    if (Config.DEBUG) {
                        //Log.d(TAG, "Values: " + ce_name + "," + ce_id + ","
                        //      + email_id + "," + location);
                    }
                    dbHandler.delete("delete from  login");
                    ContentValues cv = new ContentValues();
                    cv.put("pin_no", pin_no);
                    cv.put("ce_name", ce_name);
                    cv.put("ce_id", ce_id);
                    cv.put("email_id", email_id);
                    cv.put("location", location);
                    if (dbHandler.insert("login", cv)) {
                        if (Config.DEBUG) {
                            //Log.d(TAG, "New PinNo Inserted");
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Authenticated Success", Toast.LENGTH_SHORT).show();
                    send_data();
                } else
                    Toast.makeText(getApplicationContext(), "Invalid Pin No or Date of Birth", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            if (Config.DEBUG) {
                //Log.e(TAG, "Communication Failure");
            }
            Toast.makeText(getApplicationContext(), "Communication Failure. Please Try Again!", Toast.LENGTH_SHORT).show();
            if (!dbHandler.getIP().equals("")) {
                Config.url1 = dbHandler.getIP();
                Log.d("working_ip", "after_changed:" + Config.url1);
            } else {
                Toast.makeText(getApplicationContext(), "Please wait we are trying to reach our another Server", Toast.LENGTH_SHORT).show();
                ArrayList<String> IPs_array_list = new ArrayList<>();
                ArrayList<String> status_arryList = new ArrayList<>();
                IPs_array_list.add(Config.url2);
//                IPs_array_list.add("http://182.156.227.180/RCMS/mfservices_test_2000.php");
                status_arryList.add("1");
                ContentValues cc = new ContentValues();
                for (int i = 0; i < IPs_array_list.size(); i++) {
                    cc.put("address", IPs_array_list.get(i));
                    cc.put("status", status_arryList.get(i));
                    if (!dbHandler.checkAvailableOrNot(IPs_array_list.get(i)))
                        dbHandler.insert("ipaddress", cc);
                    Log.d("ip", "inserted_ip:" + IPs_array_list.get(i));
                }
                Config.url1 = IPs_array_list.get(0);
                Log.d("Config.url", "Config.url=" + Config.url1);

            }
        }
    }

    //code to send offline data
    public void send_data() {
       if (!Config.version.equalsIgnoreCase(prefs.getString("version", ""))) {
            {
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage("Please Update Your App To Newer Version,Click Ok To Download");
                alertDialog.setButton(DialogInterface.BUTTON1, "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        alertDialog.dismiss();
                        //     new FtpDownload_Async().execute();
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                        //finish();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON2, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
            }
        } else {
            Log.e(TAG, "-----------test 27.7.17----------Accurate Place---");
            home_activity = new Intent(Login.this, Home.class);
            ce_id_main = ce_id;
            home_activity.putExtra("ce_id", ce_id);
           SharedPreference.setDefaults(Login.this, TAG_CEID,ce_id);
         //  SharedPreference.setDefaults(Login.this, TAG_CE_MOBILE,ce_mobile);
           Log.e(TAG, "CE_ID------------------------>>>> " + ce_id);
           //Log.e(TAG, "CE_MOBILE--------------------->>>> " + ce_mobile);
            finish();
            startActivity(home_activity);
        }
    }

//    class FtpDownload_Async extends AsyncTask<Void, Void, Void> {
//
//        ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(Login.this);
//            progressDialog.setMessage("Downloading Apk, Please wait");
//            progressDialog.setCancelable(false);
//            if (!progressDialog.isShowing())
//                progressDialog.show();
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
//            super.onPostExecute(aVoid);
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            Update();
//            return null;
//        }
//    }

//    public void Update() {
//        FileOutputStream videoOut;
//        File targetFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mountfox.apk");
//
//        FTPClient ftpClient = new FTPClient();
//        try {
//            ftpClient.connect("203.196.171.252", 21);
//            ftpClient.enterLocalPassiveMode();
//            ftpClient.login("androidapp", "tech789mint*");
//
//            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);// Used for video
//            targetFile.createNewFile();
//            videoOut = new FileOutputStream(targetFile);
//            boolean result = ftpClient.retrieveFile("/" + "mountfox.apk", videoOut);
//
//            ftpClient.disconnect();
//            videoOut.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


    private class GetStandardPickup_RemarksAsyncTask extends AsyncTask<String, String, String>{
        boolean isSuccess;
        String msg = "";
        Boolean connected = false;
        String Response = "";

        @Override
        protected String doInBackground(String... strings) {
            if (Utils.isInternetAvailable(mContext)) {
                try{
                    Call<PickupStandardRemarks> pickupStandardRemarksCall = apiInterface.doStandardRemarksResponse();
                    pickupStandardRemarksCall.enqueue(new Callback<PickupStandardRemarks>() {
                        @Override
                        public void onResponse(Call<PickupStandardRemarks> call, Response<PickupStandardRemarks> response) {
                            if (response.code() == 200) {
                                if(response.body().getCode().equals("000")){
                                    Standardremarks = response.body().getData();
                                    for(int i =0 ; i<Standardremarks.size();i++){
                                        StandardremarksList.add(Standardremarks.get(i));
                                    }
                                    helper.setPickupRemark1(mContext, StandardremarksList.toString());
                                    Log.e(TAG,"StandardremarksList.toString()>>"+StandardremarksList.toString());
                                }else {
                                    Log.e(TAG,"esvs-->");
                                }
                            }else {
                                Toast.makeText(mContext, "Try Again Later", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "no200 ->");
                            }
                        }
                        @Override
                        public void onFailure(Call<PickupStandardRemarks> call, Throwable t) {
                            Toast.makeText(mContext, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e){
                    Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"exception e ->"+e.getMessage());
                }
            }
            return null;
        }}


    private class GetPickup_RemarksAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess;
        String msg = "";
        Boolean connected = false;
        String Response = "";
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.isInternetAvailable(mContext)) {
                connected = true;
                if(SharedPreference.getDefaults(Login.this, ConstantValues.TAG_URLVALIDATE).equals("dontswap")){
                    URL = Config.url1+"?opt=pickup_remarks";
                    Response = new ServiceRequestPOSTImpl().requestService(URL, "");
                    System.out.println("PickupRemarks URL >>>" + URL);
                    System.out.println("PickupRemarks Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            msg = js.getString("msg");
                            if (msg.equalsIgnoreCase("success")) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }
                }else if(SharedPreference.getDefaults(Login.this, ConstantValues.TAG_URLVALIDATE).equals("swap")) {
                    URL = Config.url1+"?opt=pickup_remarks";
                    Response = new ServiceRequestPOSTImpl().requestService(URL, "");
                    System.out.println("PickupRemarks URL >>>" + URL);
                    System.out.println("PickupRemarks Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            msg = js.getString("msg");
                            if (msg.equalsIgnoreCase("success")) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }
                }
            } else {
                isSuccess = false;
                connected = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isSuccess) {
                try {
                    JSONObject js = new JSONObject(Response);
                    if (js.getJSONArray("remark1") != null) {
                        JSONArray remark1 = js.getJSONArray("remark1");
                        helper.setPickupRemark1(mContext, remark1.toString());
                    } else
                        helper.setPickupRemark1(mContext, "");

                    if (js.getJSONArray("remark2") != null) {
                        JSONArray remark2 = js.getJSONArray("remark2");
                        helper.setPickupRemark2(mContext, remark2.toString());
                    } else
                        helper.setPickupRemark2(mContext, "");

                    if (js.getJSONArray("remark3") != null) {
                        JSONArray remark3 = js.getJSONArray("remark3");
                        helper.setPickupRemark3(mContext, remark3.toString());
                    } else
                        helper.setPickupRemark3(mContext, "");
                } catch (JSONException jsE) {
                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
            } else {
                if (!connected) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                 //   ConfigIp = Config.url2;
                }
            }
        }
    }

    private class GetAccountDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess;
        int status;
        String statusMessage = "";
        Boolean connected = false;
        String Response = "";
        SpotsDialog progressDialog = new SpotsDialog(mContext, "Loading...");

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.isInternetConnection(mContext)) {
                connected = true;
                if(SharedPreference.getDefaults(Login.this, ConstantValues.TAG_URLVALIDATE).equals("dontswap")){
                    URL = Config.url1;
                    DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                    requestData.setOpt("load_acc");
                    requestData.setCe_Id("");
                    System.out.println("GetAccDetails Request >>>" + requestData.constructRequestData());
                    Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                    System.out.println("GetAccDetails URL >>>" + URL);
                    System.out.println("GetAccDetails Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            status = js.getInt("status");
                            if (status == 0) {
                                isSuccess = true;
                            } else if (status == 1) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }

                }else if(SharedPreference.getDefaults(Login.this, ConstantValues.TAG_URLVALIDATE).equals("swap")) {
                    URL = Config.url2;
                    DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                    requestData.setOpt("load_acc");
                    requestData.setCe_Id("");
                    System.out.println("GetAccDetails Request >>>" + requestData.constructRequestData());
                    Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                    System.out.println("GetAccDetails URL >>>" + URL);
                    System.out.println("GetAccDetails Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            status = js.getInt("status");
                            if (status == 0) {
                                isSuccess = true;
                            } else if (status == 1) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }
                }
            } else {
                isSuccess = false;
                connected = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (isSuccess && status == 0) {
                try {
                    JSONObject js = new JSONObject(Response);
                    JSONArray burial = js.getJSONArray("burial");
                    JSONArray partner_bank = js.getJSONArray("partner_bank");
                    JSONArray client_bank = js.getJSONArray("client_bank");

                    helper.setBurialList(mContext, burial.toString());
                    helper.setPbList(mContext, partner_bank.toString());
                    helper.setCbList(mContext, client_bank.toString());

                } catch (JSONException jsE) {
                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
            } else if (isSuccess && status == 1) {
                Toast.makeText(mContext, "No Record Found", Toast.LENGTH_LONG).show();
            } else if (!isSuccess && connected) {
                Toast.makeText(mContext, "Communication Failure, can't reach the host. Please Try Again!", Toast.LENGTH_LONG).show();
            //    Config.url=Config.url2;
            } else {
                if (!connected) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public static void Clearcache(Context context){
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


}






