package com.mountfox.Delivery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class DeliveryListActivity extends Activity {

    Context mContext;
    Activity activity;
    String ce_id = "";
    RecyclerView recyclerView;
    DeliveryRecyclerAdapter recyclerAdapter;
    TextView tvNoRecordDelivery;
    ArrayList<Bundle> deliveryList;
    GPSTracker gpsTracker;
    double d_latitude = 12.982733625, d_longitude = 80.252031675;
    int lat = 1, lon = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_delivery_list);
        Clearcache(DeliveryListActivity.this);
        mContext = this;
        activity = this;

        ce_id =  Login.ce_id_main;
//        ce_id =  getIntent().getExtras().getString("ce_id");
//        System.out.println("DA ce_id >>> " + ce_id);

        tvNoRecordDelivery = (TextView) findViewById(R.id.tvNoRecordDelivery);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_delivery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {

            d_latitude = gpsTracker.getLatitude();
            d_longitude = gpsTracker.getLongitude();
            lat = (int) (d_latitude * 1E6);
            lon = (int) (d_longitude * 1E6);
            System.out.println("Lat d, Lng d >>>" + d_latitude + ", " + d_longitude);
            System.out.println("Lat, Lng >>>" + lat + ", " + lon);


            GetDeliveryTransactionsAsyncTask getDeliveryTransactionsAsyncTask = new GetDeliveryTransactionsAsyncTask();
            getDeliveryTransactionsAsyncTask.execute();
        } else {
            gpsTracker.showSettingsAlert();
//            buildAlertMessageNoGps();
            Toast.makeText(
                    getApplicationContext(),
                    "Please enable the Location Service(GPS)for view transactions",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private class GetDeliveryTransactionsAsyncTask extends AsyncTask<Void, Void, Void> {
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
                String Urlstatus = SharedPreference.getDefaults(DeliveryListActivity.this, ConstantValues.TAG_URLVALIDATE);
                if(Urlstatus.equals("dontswap")){
                    URL=Config.url1;
                    DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                    requestData.setOpt("deliveryList");
                    requestData.setCe_Id(ce_id);
                    requestData.setTransactionId("");
                    Response = new ServiceRequestPOSTImpl().requestService(Config.url1, requestData.constructRequestData());
                }else if(Urlstatus.equals("swap")) {
                    URL=Config.url2;
                    DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                    requestData.setOpt("deliveryList");
                    requestData.setCe_Id(ce_id);
                    requestData.setTransactionId("");
                    Response = new ServiceRequestPOSTImpl().requestService(Config.url2, requestData.constructRequestData());
                }
                System.out.println("Delivery URL >>>" +  URL);
                System.out.println("Delivery Response >>>" + Response);

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
            deliveryList = new ArrayList<>();
            if (isSuccess && status == 0) {
//            if (isSuccess) {
                try {
                    JSONObject js = new JSONObject(Response);
                    JSONArray delivery = js.getJSONArray("delivery");
                    for (int i = 0; i < delivery.length(); i++) {
                        JSONObject jsObj = delivery.getJSONObject(i);

//                        String numoftrans=jsObj.getString("numoftrans");
                        String trans_id=jsObj.getString("trans_id");
                        String delivery_address=jsObj.getString("delivery_address");
                        String cust_name=jsObj.getString("cust_name");
//                        String pickup_type=jsObj.getString("pickup_type");
                        String request_amount=jsObj.getString("request_amount");
                        String pin_status=jsObj.getString("pin_status");
                        String client_name=jsObj.getString("client_name");
                        String client_codecount=jsObj.getInt("client_codecount")+"";
                        String DepType=jsObj.getString("DepType");
                        String tranStatus=jsObj.getInt("tranStatus")+"";

                        Bundle bundle=new Bundle();
//                        bundle.putString("numoftrans",numoftrans);
                        bundle.putString("trans_id",trans_id);
                        bundle.putString("delivery_address",delivery_address);
                        bundle.putString("cust_name",cust_name);
//                        bundle.putString("pickup_type",pickup_type);
                        bundle.putString("request_amount",request_amount);
                        bundle.putString("pin_status",pin_status);
                        bundle.putString("client_name",client_name);
                        bundle.putString("client_codecount",client_codecount);
                        bundle.putString("DepType",DepType);
                        bundle.putString("tranStatus",tranStatus);

                        deliveryList.add(bundle);
                    }
//                loadLiveRateList();

                recyclerView.setVisibility(View.VISIBLE);
                tvNoRecordDelivery.setVisibility(View.GONE);
                recyclerAdapter = new DeliveryRecyclerAdapter(mContext, deliveryList,ce_id);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();


                } catch (JSONException jsE) {

                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
            } else if (isSuccess && status == 1) {
                Toast.makeText(activity, "No Record Found", Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.GONE);
                tvNoRecordDelivery.setVisibility(View.VISIBLE);
            } else if (!isSuccess && connected) {
                Toast.makeText(activity, "Communication Failure, can't reach the host. Please Try Again!", Toast.LENGTH_LONG).show();
            }else {
                if (!connected) {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertTheme);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        final Intent inte = new Intent(this, ModeOfTransactionActivity.class);
        inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(inte);
        finish();
    }

//    public boolean isInternetConnection() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    public void loadLiveRateList() {
        deliveryList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Bundle bundle = new Bundle();
//            bundle.putString("name", "Patient Name Comes" + i);
            switch (i) {
                case 0: {
                    bundle.putString("trans_id", "40098578");
                    bundle.putString("pickup_name", "Chittapur Road,Siraj-E-Millat colony,Yadgiri");
                    bundle.putString("cust_name", "TON INDIA CREDIT- Delivery");
                    bundle.putString("pickup_amount", "1000000");
                    bundle.putString("client_codecount", "1");
                    bundle.putString("client_name", "State Bank");
                }
                case 1: {
                    bundle.putString("trans_id", "40098575");
                    bundle.putString("pickup_name", "Chittapur Road,Siraj-E-Millat colony,Yadgiri");
                    bundle.putString("cust_name", "LERTON INDIA CREDIT- Delivery");
                    bundle.putString("pickup_amount", "3005500");
                    bundle.putString("client_codecount", "1");
                    bundle.putString("client_name", "City Bank");
                }
                case 2: {
                    bundle.putString("trans_id", "40098571");
                    bundle.putString("pickup_name", "Chittapur Road,Siraj-E-Millat colony,Yadgiri");
                    bundle.putString("cust_name", "FULLERTON INDIA CREDIT- Delivery");
                    bundle.putString("pickup_amount", "200000");
                    bundle.putString("client_codecount", "1");
                    bundle.putString("client_name", "Standard Chartered Bank");
                }

//            listPosts.add(new RecommendedPostsList(bundle));
//            postList.add(bundle);
                deliveryList.add(bundle);
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
