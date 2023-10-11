package com.mountfox.Cheque_Pickup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mountfox.Config;
import com.mountfox.ExceptionHandler;
import com.mountfox.GPSTracker;
import com.mountfox.Login;
import com.mountfox.ModeOfTransactionActivity;
import com.mountfox.R;
import com.mountfox.Services.dataRequest.DeliveryTransListRequestData;
import com.mountfox.Services.serviceRequest.ServiceRequestPOSTImpl;
import com.mountfox.Utils;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ChequePickupListActivity extends Activity {

    Context mContext;
    Activity activity;
    String ce_id = "";
    RecyclerView recyclerView;
    ChequePickupRecyclerAdapter recyclerAdapter;
    TextView tvNoRecord;
    ArrayList<Bundle> checkPickupList;
    GPSTracker gpsTracker;
    double latitude = 12.982733625, longitude = 80.252031675;
    int lat = 1, lon = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_cheque_pickup_list);
        mContext = this;
        activity = this;
        ce_id = Login.ce_id_main;

        Clearcache(ChequePickupListActivity.this);
        tvNoRecord = (TextView) findViewById(R.id.tvNoRecordCheckPickup);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_check_pickup);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            lat = (int) (latitude * 1E6);
            lon = (int) (longitude * 1E6);

//                Log.i("Lat & Lon :", lat + "," + lat+":"+imei+":"+ce_id);
            System.out.println("Lat >>>" + lat + " Long >>> " + lon);
            System.out.println("Lat >>>" + lat + " Long >>> " + lon);

            GetCheckPickupTransactionsAsyncTask getCheckPickupTransactionsAsyncTask = new GetCheckPickupTransactionsAsyncTask();
            getCheckPickupTransactionsAsyncTask.execute();
        } else {
            gpsTracker.showSettingsAlert();
//            buildAlertMessageNoGps();
            Toast.makeText(
                    getApplicationContext(),
                    "Please enable the Location Service(GPS)for view transactions",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private class GetCheckPickupTransactionsAsyncTask extends AsyncTask<Void, Void, Void> {
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

                String URL="";

                String Urlstatus = SharedPreference.getDefaults(ChequePickupListActivity.this, ConstantValues.TAG_URLVALIDATE);

                if(Urlstatus.equals("dontswap")){
                    URL=Config.url1;
                }else if(Urlstatus.equals("swap")) {
                    URL=Config.url2;
                }

                DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                requestData.setOpt("chequePickupList");
                requestData.setCe_Id(ce_id);
                requestData.setTransactionId("");

                System.out.println("chequePickupList URL >>>" +  URL);
                System.out.println("chequePickupList Request >>>" +  requestData.constructRequestData());
                Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                System.out.println("chequePickupList Response >>>" + Response);

                if (Response != null && Response.length() > 0) {
                    try {
                        JSONObject js = new JSONObject(Response);
                        status = js.getInt("status");
                        statusMessage = js.getString("statusMessage");
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
            checkPickupList = new ArrayList<>();
            if (isSuccess && status == 0) {
                try {
                    JSONObject js = new JSONObject(Response);
                    JSONArray delivery = js.getJSONArray("chequePickup");
                    for (int i = 0; i < delivery.length(); i++) {
                        JSONObject jsObj = delivery.getJSONObject(i);

                        String trans_id=jsObj.getString("trans_id");
                        String cust_name=jsObj.getString("cust_name");
                        String cust_address=jsObj.getString("cust_address");
                        String cust_code=jsObj.getString("cust_code");
                        String request_amount=jsObj.getString("request_amount");
                        String client_name=jsObj.getString("client_name");
                        String client_codecount=jsObj.getInt("client_codecount")+"";
                        String tranStatus=jsObj.getInt("tranStatus")+"";
                        String dep_type=jsObj.getString("dep_type");
                        String clientId=jsObj.getString("clientId");


                        Bundle bundle=new Bundle();
//                        bundle.putString("numoftrans",numoftrans);
                        bundle.putString("trans_id",trans_id);
                        bundle.putString("cust_name",cust_name);
                        bundle.putString("cust_address",cust_address);
                        bundle.putString("cust_code",cust_code);
                        bundle.putString("request_amount",request_amount);
                        bundle.putString("client_name",client_name);
                        bundle.putString("client_codecount",client_codecount);
                        bundle.putString("tranStatus",tranStatus);
                        bundle.putString("dep_type",dep_type);
                        bundle.putString("clientId",clientId);

                        checkPickupList.add(bundle);
                    }

                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoRecord.setVisibility(View.GONE);
                    recyclerAdapter = new ChequePickupRecyclerAdapter(mContext, checkPickupList,ce_id);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerAdapter.notifyDataSetChanged();


                } catch (JSONException jsE) {

                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
            } else if (isSuccess && status == 1) {
                Toast.makeText(activity, statusMessage, Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.GONE);
                tvNoRecord.setVisibility(View.VISIBLE);
            }else if (!isSuccess && connected) {
                Toast.makeText(activity, "Communication Failure, can't reach the host. Please Try Again!", Toast.LENGTH_LONG).show();
            } else {
                if (!connected) {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        final Intent inte = new Intent(this, ModeOfTransactionActivity.class);
        inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(inte);
        finish();
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
