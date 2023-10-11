package com.mountfox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class CancelReceipt extends Activity implements OnItemClickListener,
        GetJson.CallbackInterface {

    String ce_id = "", imei = "908372827282", trans_id = "", point_name = "",
            cust_name = "", type = "", req_amount = "", pickup_amount = "",trans_date="";
    int lat = 1, lon = 2;
    double latitude = 12.982733625, longitude = 80.252031675;

    String pickup_amounts[], req_amounts[], point_names[], cust_names[],
            types[], trans_ids[],trans_dates[];

    GPSTracker gpsTracker;
    List<BasicNameValuePair> params;
    private static final String TAG = "CancelReceipt";
    ProgressDialog progressDialog;
    ListView trans_listview;
    ImageView connectPrinter;
    ContentValues contentValues;
    DbHandler dbHandler;
    static int option = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.cancel_receipt);
        initializeComponents();
    }

    protected void onResume() {
        super.onResume();
        if (PrinterSelection.isPrinterConnected)
            connectPrinter.setImageResource(R.drawable.printer_on);
        else
            connectPrinter.setImageResource(R.drawable.printer_off);
    }

    public void initializeComponents() {
        connectPrinter = (ImageView) findViewById(R.id.connectPrinter);
        trans_listview = (ListView) findViewById(R.id.trans_listview);
        trans_listview.setOnItemClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Information");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
        gpsTracker = new GPSTracker(this);
        ce_id = getIntent().getStringExtra("ce_id");
        if (Config.DEBUG)
        {
            //Log.i(TAG, "Ce_id: " + ce_id);
        }

        dbHandler = new DbHandler(CancelReceipt.this);

        if (PrinterSelection.isPrinterConnected)
            connectPrinter.setImageResource(R.drawable.printer_on);
        else
            connectPrinter.setImageResource(R.drawable.printer_off);

        connectPrinter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(CancelReceipt.this,
                        PrinterSelection.class));
            }
        });
        loadData();
    }

    @SuppressWarnings("unchecked")
    public void loadData() {
        trans_listview.setAdapter(null);
        if (Utils.isInternetAvailable(getApplicationContext())) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imei = telephonyManager.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Config.DEBUG)
            {
                //Log.i("Imei:", imei);
            }
            gpsTracker = new GPSTracker(this);

            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                lat = (int) (latitude * 1E6);
                lon = (int) (longitude * 1E6);
                if (Config.DEBUG)
                {
                    //Log.i("Lat & Lon :", lat + "," + lat);
                }
                progressDialog.setTitle("Fetching Information");
                progressDialog.show();
                params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("opt", "view_rec"));
                params.add(new BasicNameValuePair("ce_id", ce_id));
                params.add(new BasicNameValuePair("lat", "" + lat));
                params.add(new BasicNameValuePair("lon", "" + lon));
                params.add(new BasicNameValuePair("IMIE", imei));
                params.add(new BasicNameValuePair("final", "1"));
                GetJson getJson = new GetJson(CancelReceipt.this,this);
                getJson.execute(params);
            } else
                Toast.makeText( getApplicationContext(),"Please enable the Location Service(GPS/WIFI) for view receipts",Toast.LENGTH_SHORT).show();
        } else
        {
            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> value;
            String date = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
            if(date.length()==1)
                date        =   "0"+date;
            String mon = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
            if(mon.length()==1)
                mon        =   "0"+mon;
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            ContentValues contentValues[] = dbHandler.select("select *from receipt where show='yes' and ce_id='" + ce_id + "'and trans_date='"+date+"-"+mon+"-"+year+"'");
            int n = contentValues.length;
            pickup_amounts = new String[n];
            req_amounts = new String[n];
            point_names = new String[n];
            cust_names = new String[n];
            types = new String[n];
            trans_ids = new String[n];
            trans_dates =   new String[n];
            if (contentValues.length > 0) {
                for (int i = 0; i < contentValues.length; i++) {
                    pickup_amount = (String) contentValues[i].get("pickup_amount");
                    req_amount = (String) contentValues[i].get("req_amount");
                    point_name = (String) contentValues[i].get("point_name");
                    cust_name = (String) contentValues[i].get("cust_name");
                    type = (String) contentValues[i].get("type");
                    trans_id = (String) contentValues[i].get("trans_id");
                    trans_date  =   (String) contentValues[i].getAsString("trans_date");
                    pickup_amounts[i] = pickup_amount;
                    req_amounts[i] = req_amount;
                    point_names[i] = point_name;
                    cust_names[i] = cust_name;
                    types[i] = type;
                    trans_ids[i] = trans_id;
                    trans_dates[i]=trans_date;
                    value = new HashMap<String, String>();
                    value.put("pickup_amount", pickup_amount);
                    value.put("point_name", point_name);
                    value.put("cust_name", cust_name);
                    list.add(value);
                }
                trans_listview.setAdapter(new SimpleAdapter(this, list,
                        R.layout.translist_item, new String[] {
                                "pickup_amount", "point_name", "cust_name" },
                        new int[] { R.id.amount_txt, R.id.pointname_txt,
                                R.id.customername_txt }));
            } else {
                trans_listview.setAdapter(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        final int seletedPosition = arg2;
        final String printDetails = "\nTransaction Id: "
                + trans_ids[seletedPosition] + "\nType: "
                + types[seletedPosition] + "\nPickup Amount: "
                + pickup_amounts[seletedPosition] + "\nRequest Amount: "
                + req_amounts[seletedPosition] + "\nCustomer Name: "
                + cust_names[seletedPosition] + "\nPoint Name: "
                + point_names[seletedPosition];

        final Dialog printDialog = new Dialog(this);
        printDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        printDialog.setContentView(R.layout.transaction_print);
        printDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView print_close_img = (ImageView) printDialog
                .findViewById(R.id.print_close_img);
        TextView print_details_txt = (TextView) printDialog
                .findViewById(R.id.print_details_txt);
        Button print_btn = (Button) printDialog.findViewById(R.id.print_btn);

        @SuppressWarnings("deprecation")
        int width = getWindowManager().getDefaultDisplay().getWidth() - 50;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
                LayoutParams.WRAP_CONTENT);
        print_details_txt.setLayoutParams(lp);
        print_details_txt.setText(printDetails);

        print_btn.setText("Cancel Receipt");
        print_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (Utils.isInternetAvailable(CancelReceipt.this)) {
                    if (gpsTracker.canGetLocation())
                    {
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();
                        lat = (int) (latitude * 1E6);
                        lon = (int) (longitude * 1E6);
                        if (Config.DEBUG)
                        {
                            //Log.i("Lat & Lon :", lat + "," + lat);
                        }
                        option = 1;
                        printDialog.cancel();

                        progressDialog.setTitle("Canceling the receipt");
                        progressDialog.show();
                        trans_id = trans_ids[seletedPosition];
                        params = new ArrayList<BasicNameValuePair>();
                        params.add(new BasicNameValuePair("opt", "cancel_rec"));
                        params.add(new BasicNameValuePair("trans_id",trans_ids[seletedPosition]));
                        params.add(new BasicNameValuePair("ce_id", ce_id));
                        params.add(new BasicNameValuePair("lat", "" + lat));
                        params.add(new BasicNameValuePair("lon", "" + lon));
                        params.add(new BasicNameValuePair("IMIE", imei));
                        params.add(new BasicNameValuePair("final", "1"));
                        GetJson getJson = new GetJson(CancelReceipt.this,CancelReceipt.this);
                        getJson.execute(params);
                    } else
                        Toast.makeText(
                                getApplicationContext(),
                                "Please enable the Location Service(GPS/WIFI) for cancel the receipt",
                                Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enable the Internet Connection for cancel the receipt",
                            Toast.LENGTH_SHORT).show();

            }
        });

        print_close_img.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                printDialog.cancel();
            }
        });
        printDialog.setCancelable(false);
        printDialog.show();
    }

    public void onRequestCompleted(JSONObject object) {
        progressDialog.dismiss();

        if (option == 0) {
            if (object != null) {
                if (Config.DEBUG)
                {
                    //Log.d(TAG, "Result Json: " + object.toString());
                }
                try {
                    String msg = object.getString("msg");
                    if (Config.DEBUG)
                    {
                        //Log.d(TAG, "Msg:" + msg);
                    }
                    if (msg.equals("success")) {
                        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                        HashMap<String, String> value;

                        JSONArray ja = object.getJSONArray("transactions");
                        if (Config.DEBUG)
                        {
                            //Log.d(TAG, "Json Array: " + ja.toString());
                        }
                        dbHandler.delete("delete from "+"receipt");

                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject inner_jo = ja.getJSONObject(i);
                            pickup_amount = inner_jo.getString("pickup_amount");
                            req_amount = inner_jo.getString("req_amount");
                            point_name = inner_jo.getString("point_name");
                            cust_name = inner_jo.getString("cust_name");
                            type = inner_jo.getString("type");
                            trans_id = inner_jo.getString("trans_id");
                            trans_date  =   inner_jo.getString("trans_date");
                            if (Config.DEBUG)
                            {
                             //   Log.d(TAG, "Result Inner Object: " + pickup_amount
                               //         + "," + point_name + "," + cust_name + ","
                                //+ type + "," + trans_id);
                            }

                            contentValues = new ContentValues();
                            contentValues.put("trans_id", trans_id);
                            contentValues.put("point_name", point_name);
                            contentValues.put("cust_name", cust_name);
                            contentValues.put("type", type);
                            contentValues.put("req_amount", req_amount);
                            contentValues.put("pickup_amount", pickup_amount);
                            contentValues.put("ce_id", ce_id);
                            contentValues.put("trans_date",trans_date);
                            // Update/Insert Data
                            if (dbHandler.isExistRow("receipt", trans_id)) {
                                dbHandler
                                        .execute("update receipt set point_name='"
                                                + point_name
                                                + "', cust_name='"
                                                + cust_name
                                                + "', type='"
                                                + type
                                                + "', req_amount="
                                                + req_amount
                                                + ", pickup_amount="
                                                + pickup_amount
                                                + " where trans_id='"
                                                + trans_id + "'");
                                // dbHandler.update("receipt", contentValues);
                            } else
                                dbHandler.insert("receipt", contentValues);
                        }

                        ContentValues contentValues[] = dbHandler
                                .select("select *from receipt where show='yes' and ce_id='"
                                        + ce_id + "'");
                        int n = contentValues.length;
                        pickup_amounts = new String[n];
                        req_amounts = new String[n];
                        point_names = new String[n];
                        cust_names = new String[n];
                        types = new String[n];
                        trans_ids = new String[n];

                        if (n == 0)
                            Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < contentValues.length; i++) {
                            pickup_amount = (String) contentValues[i]
                                    .get("pickup_amount");
                            req_amount = (String) contentValues[i]
                                    .get("req_amount");
                            point_name = (String) contentValues[i]
                                    .get("point_name");
                            cust_name = (String) contentValues[i]
                                    .get("cust_name");
                            type = (String) contentValues[i].get("type");
                            trans_id = (String) contentValues[i]
                                    .get("trans_id");

                            pickup_amounts[i] = pickup_amount;
                            req_amounts[i] = req_amount;
                            point_names[i] = point_name;
                            cust_names[i] = cust_name;
                            types[i] = type;
                            trans_ids[i] = trans_id;

                            value = new HashMap<String, String>();
                            value.put("pickup_amount", pickup_amount);
                            value.put("point_name", point_name);
                            value.put("cust_name", cust_name);
                            list.add(value);
                        }
                        trans_listview.setAdapter(new SimpleAdapter(this, list, R.layout.translist_item, new String[] {
                                        "pickup_amount", "point_name","cust_name" }, new int[] {
                                        R.id.amount_txt, R.id.pointname_txt,
                                        R.id.customername_txt }));
                    } else
                        Toast.makeText(
                                getApplicationContext(),
                                "No Record Found",
                                Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (Config.DEBUG)
                {
                    //Log.e(TAG, "Communication Failure");
                }
                Toast.makeText(getApplicationContext(),
                        "Communication Failure. Please Try Again!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (option == 1) {
            option = 0;
            if (object != null) {
                if (Config.DEBUG)
                {
                    //Log.d(TAG, "Result Json: " + object.toString());
                }
                try {
                    String status = object.getString("status");
                    if (status.equals("success")) {
                        Toast.makeText(getApplicationContext(),
                                "The receipt was canceled", Toast.LENGTH_SHORT)
                                .show();
                        dbHandler.execute("update receipt set show='no' where trans_id='"
                                        + trans_id + "'");
                        dbHandler.execute("update transactions set show='yes' where trans_id='"
                                + trans_id + "'");
                        if (Config.DEBUG)
                        {
                            //Log.i(TAG, "Canceled Receipt Trans_id: " + trans_id);
                        }
                        loadData();
                    } else
                        Toast.makeText(
                                getApplicationContext(),
                                "Failed to cancel the receipt. Please Try Again",
                                Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (Config.DEBUG)
                {
                    //Log.e(TAG, "Communication Failure");
                }
                Toast.makeText(getApplicationContext(),
                        "Communication Failure. Please Try Again!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(CancelReceipt.this,Home.class).putExtra("ce_id", ce_id));
        super.onBackPressed();
    }
}
