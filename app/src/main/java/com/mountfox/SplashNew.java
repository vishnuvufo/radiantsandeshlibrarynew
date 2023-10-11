package com.mountfox;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mountfox.PreferenceHelper;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.Services.serviceRequest.ServiceRequestPOSTImpl;
import com.mountfox.response.BankDepositBankname;
import com.mountfox.response.BankResponse;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashNew extends Activity {

    public static final String TAG =SplashNew.class.getSimpleName();
    private ProgressBar progressBar;
    Context mContext;
    private DbHandler dbHandler;
    private int wait = 0;
    private GetJson getJson, getJson1;
    private List<BasicNameValuePair> entity, entity1;
    private SharedPreferences sharedPreferences;
    private AlarmManager alarmManager;
    private Calendar calendar, cal;
    public ArrayList<String> IPs_array_list = new ArrayList<>();
    public ArrayList<String> status_arryList = new ArrayList<>();
    public static String working_ip = "";
    public final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    public    Handler handler;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION};
    public  Get_Json get_json;
    // solve the app permission problem in this Activity..............
    ApiInterface apiInterface;
    public Boolean urlstatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.splash);
        apiInterface = Constants.getClient().create(ApiInterface.class);
        dbHandler = new DbHandler(this);
        dbHandler.createables();
        mContext = this;
        Clearcache(SplashNew.this);
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            OnValidateUrl();
            //checkAllPermissionWasAccept();
        }
        progressBar = (ProgressBar) findViewById(R.id.loadingProgress);
    }

    public void OnValidateUrl() {
        try {
            Call<BankDepositBankname> bankDepositBanknameCall = apiInterface.doBankNameResponse("BankDetails");
            bankDepositBanknameCall.enqueue(new Callback<BankDepositBankname>() {
                @Override
                public void onResponse(Call<BankDepositBankname> call, Response<BankDepositBankname> response) {
                    if (response.code() == 200) {
                        checkAllPermissionWasAccept(SharedPreference.setDefaults(getApplicationContext(), ConstantValues.TAG_URLVALIDATE, "dontswap"));
                        Log.e(TAG, "200 ->");
                    }else {
                        checkAllPermissionWasAccept(SharedPreference.setDefaults(getApplicationContext(), ConstantValues.TAG_URLVALIDATE, "swap"));
                        Log.e(TAG, "no200 ->");
                    }
                }
                @Override
                public void onFailure(Call<BankDepositBankname> call, Throwable t) {
                    checkAllPermissionWasAccept(SharedPreference.setDefaults(getApplicationContext(), ConstantValues.TAG_URLVALIDATE, "swap"));
                    Log.e(TAG, "no200 ->");
                }
            });
        } catch (Exception e) {
            checkAllPermissionWasAccept(SharedPreference.setDefaults(getApplicationContext(), ConstantValues.TAG_URLVALIDATE, "swap"));
            Log.e(TAG, "no200 ->");
        }



    }




    public void checkAllPermissionWasAccept(String urlstatus) {
        sharedPreferences = getSharedPreferences("mountfox", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("check_stat", "1").equalsIgnoreCase("1")) {
            getBankList(urlstatus);
        } else {
            wait = 10;
            Thread welcomeThread = new Thread() {
                public void run() {
                    try {
                        super.run();
                        while (wait <= 100) {
                            sleep(100);
                            if (sharedPreferences.getString("check_stat", "2").equalsIgnoreCase("2"))
                                wait += 10;
                        }
                    } catch (Exception e) {
                        System.out.println("Splash Screen Exception=" + e);
                    } finally {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                }
            };
            welcomeThread.start();

        }
    }

    public void getVault() {
        Get_Json get_json = new Get_Json(SplashNew.this, new Get_Json.Result_Json() {
            @Override
            public void OnRequestCompleted(JSONObject jsonObject) {

                if (jsonObject != null) {
                    try {
                        ArrayList<String> list = new ArrayList<String>();
                        String msg = jsonObject.getString("msg");
                        //    if (msg.equalsIgnoreCase("success"))
//                            Toast.makeText(getApplicationContext(),
//                                    "Vault success",
//                                    Toast.LENGTH_SHORT).show();



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SplashNew.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
//                    Config.url = "http://182.156.227.180/RCMS/mfservices_test_2000.php";
                    Config.url1=Config.url2;
                    getVault();

//                        Toast.makeText(getApplicationContext(),
//                                "Please wait we trying to reach our another Server",
//                                Toast.LENGTH_SHORT).show();
//
//                        IPs_array_list.add("http://182.156.227.180/RCMS/mfservices_test_2000.php");
//
//                        status_arryList.add("1");
//
//                        ContentValues cc=new ContentValues();
//                        for (int i=0;i<IPs_array_list.size();i++) {
//                            cc.put("address",IPs_array_list.get(i));
//                            cc.put("status", status_arryList.get(i));
//                            if(!dbHandler.checkAvailableOrNot(IPs_array_list.get(i)))
//                                dbHandler.insert("ipaddress", cc);
//                            Log.d("ip","inserted_ip:"+IPs_array_list.get(i));
//                        }
//                        Config.url =  IPs_array_list.get(0);
//                        Log.d("Config.url", "Config.url=" + Config.url);
//                        getVault();
                }
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            Log.e("Get Profile Data", "jsonObj:" + jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<String, String>("opt", "vault_list"));
        get_json.execute(pairs);
    }

    public void getBankList(String urlstatus) {
        Get_Json get_json = new Get_Json(SplashNew.this, new Get_Json.Result_Json() {

            @Override
            public void OnRequestCompleted(JSONObject jsonObject) {

                if (jsonObject != null) {
                    try {
                        ArrayList<String> list = new ArrayList<String>();
                        ArrayList<String> branch = new ArrayList<String>();
                        ArrayList<String> acc_no = new ArrayList<String>();
                        ArrayList<String> acc_ids = new ArrayList<String>();
                        String msg = jsonObject.getString("msg");
                        if (msg.equalsIgnoreCase("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("banks");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Log.d("bank_name-id", "" + i + "#" + jsonArray.getJSONObject(i).getString("bank_name"));
                                list.add(jsonArray.getJSONObject(i).getString("bank_name"));
                                branch.add(jsonArray.getJSONObject(i).getString("branch_name"));
                                acc_no.add(jsonArray.getJSONObject(i).getString("acc_no"));
                                Log.d("", "accacc_no_id#" + jsonArray.getJSONObject(i).getString("acc_no"));
                                acc_ids.add(jsonArray.getJSONObject(i).getString("acc_id"));
                                Log.d("", "acc_id#" + jsonArray.getJSONObject(i).getString("acc_id"));
                                //Log.v("Splash2","bankname"+list.get(i));
                            }
                            dbHandler.insert_bankName(list, acc_ids, branch, acc_no);
                        } else {

                        }
                        wait = 1000;
//                        sharedPreferences.edit().putString("check_stat", "2").commit();
                        sharedPreferences.edit().putString("check_stat", "2").commit();
                        startActivity(new Intent(getApplicationContext(), Login.class));
//                        startActivity(new Intent(getApplicationContext(), Deposit_amount_edit_screen.class));

                        finish();
                    } catch (Exception e) {
                        wait = 1000;
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Contact IT Support Team", Toast.LENGTH_SHORT).show();
                }

            }
        });

        JSONObject jsonObject = new JSONObject();
        try {
            Log.e("Get Profile Data", "jsonObj:" + jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<String, String>("opt", "bank_list"));
        get_json.execute(pairs);
    }

    // check permissionsssss

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "Required permission >>>>" +permissions[index]);

                        Toast.makeText(this, "Required permission '" + permissions[index] + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }

                // all permissions were granted
                //Toast.makeText(getApplicationContext(), "All Permission Success", Toast.LENGTH_SHORT).show();
                // here u start all of the process after u get all success while get permission...........
                //   checkAllPermissionWasAccept();

                OnValidateUrl();
                break;
        }
    }
    public static void Clearcache(Context context){
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
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
