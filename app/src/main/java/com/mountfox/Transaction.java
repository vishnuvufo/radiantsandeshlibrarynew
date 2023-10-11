package com.mountfox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class Transaction extends AppCompatActivity implements OnItemClickListener, GetJson.CallbackInterface {
    ListView trans_listview;
    TextView  pickup_session_text;
    Intent recInfoIntent;
    public static String dep_type_data = "";
    String trans_id = "", point_name = "", cust_name = "",
            type = "", amount = "", pin_status = "", client_code_txt = "", trans_date = "",
            pin_no = "", deposite_type_Strng = "",otp_flag = "",otp_day = "",pickup_session="" ;
    public static String ce_id = "", deposit_type_strng;
    String types[], trans_ids[], pin_statuss[], pin_nos[], cust_names[], deposit_type_strng_array[],otp_flags[],otp_days[];
    String imei = "908372827282", simno = "721907317389173173";
    int day = 0, month = 0, year = 0, hour = 0, minute = 0;
    int lat = 1, lon = 2;
    double latitude = 12.982733625, longitude = 80.252031675;
    GPSTracker gpsTracker;
    List<BasicNameValuePair> params;
    private static final String TAG = "Transaction";
    ProgressDialog progressDialog;
    Calendar calendar;
    ContentValues contentValues;
    DbHandler dbHandler;
    int resumeCheck = 0;
    //value used for ReceivePayment
    public ArrayList<String> client_code = new ArrayList<String>();
    private ArrayList<String> captions = new ArrayList<String>();
    private ArrayList<String> deno_status = new ArrayList<String>();
    private ArrayList<String> client_amt = new ArrayList<String>();
    private ArrayList<String> amts = new ArrayList<String>();
    private ArrayList<String> shop_id = new ArrayList<String>();
    private ArrayList<String> stop_id = new ArrayList<String>();



    public ArrayList<String> qr_json = new ArrayList<>();
    public ArrayList<String> dep_typess = new ArrayList<>();
    private ArrayList<String> rec_pos = new ArrayList<String>();
    private ArrayList<String> qr_status = new ArrayList<String>();
    private ArrayList<String> dupRecptNoValStatus = new ArrayList<String>();
    private static final int REQUEST_CODE_LIST_ON_CLICK = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.transaction);


        Clearcache(Transaction.this);
        initializeComponents();


    }

    @SuppressWarnings("unchecked")
    public void initializeComponents() {
        trans_listview = (ListView) findViewById(R.id.trans_listview);
        trans_listview.setOnItemClickListener(this);
        GetTransactions();
    }

    @SuppressLint("MissingPermission")
    public void GetTransactions() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Information");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
        gpsTracker = new GPSTracker(this);
        ce_id = getIntent().getStringExtra("ce_id");
        ce_id = Login.ce_id_main;
        Log.e(TAG, "Cashpickupceid:::" + ce_id);
        if (Config.DEBUG) {
            //Log.i(TAG, "Ce_id: " + ce_id);
        }

        dbHandler = new DbHandler(Transaction.this);
        dbHandler.deleteTimeoutTransaction();
        if (Utils.isInternetAvailable(getApplicationContext())) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imei = telephonyManager.getDeviceId();
                simno = telephonyManager.getSimSerialNumber();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Config.DEBUG) {
                //Log.i("Imei:", imei);
            }

            gpsTracker = new GPSTracker(this);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                lat = (int) (latitude * 1E6);
                lon = (int) (longitude * 1E6);
//                if (Config.DEBUG) {
//                    //Log.i("Lat & Lon :", lat + "," + lat+":"+imei+":"+ce_id);
//                }
                progressDialog.show();
                params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("opt", "view_trans"));
                //   params.add(new BasicNameValuePair("ce_id", ce_id));
                params.add(new BasicNameValuePair("ce_id", Login.ce_id_main));
                params.add(new BasicNameValuePair("lat", "" + lat));
                params.add(new BasicNameValuePair("lon", "" + lon));
                params.add(new BasicNameValuePair("IMIE", "imei"));
                params.add(new BasicNameValuePair("final", "1"));
                GetJson getJson = new GetJson(Transaction.this,this);
                getJson.execute(params);
            } else {
                buildAlertMessageNoGps();
                Toast.makeText(getApplicationContext(), "Please enable the Location Service(GPS/WIFI) for view transactions", Toast.LENGTH_SHORT).show();
            }
        } else {
            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> value;
            //21-04-2015
            String date = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
            if (date.length() == 1)
                date = "0" + date;
            String mon = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
            if (mon.length() == 1)
                mon = "0" + mon;
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            ContentValues contentValues[] = dbHandler.select("select *from transactions where show='yes' and ce_id='" + ce_id + "' and trans_date='" + date + "-" + mon + "-" + year + "'");
            int n = contentValues.length;
            types = new String[n];
            trans_ids = new String[n];
            pin_statuss = new String[n];
            pin_nos = new String[n];
            otp_flags = new String[n];
            otp_days = new String[n];
            cust_names = new String[n];
            deposit_type_strng_array = new String[n];
            if (n == 0)
                Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
            if (contentValues.length > 0) {
                client_code.clear();
                deno_status.clear();
                client_amt.clear();
                captions.clear();
                shop_id.clear();
                dep_typess.clear();
                amts.clear();
                for (int i = 0; i < contentValues.length; i++) {
                    types[i] = contentValues[i].getAsString("type");
                    trans_ids[i] = contentValues[i].getAsString("trans_id");
                    pin_statuss[i] = contentValues[i].getAsString("pin_status");
                    cust_names[i] = contentValues[i].getAsString("cust_name");
                    deposit_type_strng_array[i] = contentValues[i].getAsString("dep_typeee");
                    pin_nos[i] = contentValues[i].getAsString("pin_no");
                    otp_flags[i] = contentValues[i].getAsString("otp_flag");
                    otp_days[i] = contentValues[i].getAsString("otp_day");
                    amount = contentValues[i].getAsString("amount");
                    pickup_session =contentValues[i].getAsString("pickup_session");

//// evening_pickup
//
//                    Log.e(TAG,"evening_pickup -------"+evening_pickup);
                    point_name = contentValues[i].getAsString("point_name");
                    client_code_txt = contentValues[i].getAsString("client_code");
                    captions.add(contentValues[i].getAsString("captions"));
                    shop_id.add(contentValues[i].getAsString("shop_id"));
                    client_code.add(client_code_txt);
                    deno_status.add(contentValues[i].getAsString("deno_status"));
                    client_amt.add(contentValues[i].getAsString("client_amt"));
                    cust_name = contentValues[i].getAsString("cust_name");
                    pin_no = contentValues[i].getAsString("pin_no");
                    otp_flag = contentValues[i].getAsString("otp_flag");
                    amts.add(contentValues[i].getAsString("amount"));
                    pin_nos[i] = pin_no;
                    otp_flags[i] = otp_flag;
                    otp_days[i] = otp_day;
                    value = new HashMap<String, String>();
                    value.put("amount", amount);
                    value.put("point_name", point_name);
                    value.put("cust_name", cust_name);
                    value.put("trans_type", contentValues[i].getAsString("type"));
// Evening_pickup
                    value.put("pickup_session",pickup_session);


                    String temp_str = "";
                    if (client_code_txt.length() < 0 || client_code_txt.equals(""))
                        temp_str = "0";
                    else
                        temp_str = String.valueOf(client_code_txt.split(",").length);
                    value.put("client_code", String.valueOf("Client Code:" + temp_str));

                    // value.put("client_code",String.valueOf("Client Code:" + contentValues[i].getAsString("client_code").split(",").length));
                    // Log.v(TAG,"client code length offline"+contentValues[i].getAsString("client_code").split(",").length);
                    list.add(value);
                }

                trans_listview.setAdapter(new SimpleAdapter(this, list,
                        R.layout.translist_item, new String[]{"amount", "point_name", "cust_name", "trans_type","client_code","pickup_session"},
                        new int[]{R.id.amount_txt, R.id.pointname_txt, R.id.customername_txt, R.id.amount_txt_v,R.id.ccode_txt,R.id.session_txt}));
            } else {
                trans_listview.setAdapter(null);
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

    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        Log.d("deposit_type_strng_ar", "deposit_type_strng_ar:" + deposit_type_strng_array[position]);


        if (Config.DEBUG) {
            //Log.d(TAG, "Trans Id: " + trans_ids[position] + ", Type: "
            //+ types[position]);
        }
        new TransactionSingleItemDataCenter(ce_id, trans_ids[position],
                pin_statuss[position], pin_nos[position], types[position],
                client_code.get(position), deno_status.get(position),
                client_amt.get(position), captions.get(position),
                amts.get(position), cust_names[position], deposit_type_strng_array[position],otp_flags[position],otp_days[position]);

        Log.d("Data_inserted", "Data inserted :" + captions.get(position));
        Log.d("stop_id_inserted", "stop_id inserted :" + stop_id.get(position));
        recInfoIntent = new Intent(this, ReceivePayment.class);
        recInfoIntent.putExtra("ce_id", ce_id);
        recInfoIntent.putExtra("trans_id", trans_ids[position]);
        recInfoIntent.putExtra("pin_status", pin_statuss[position]);
        recInfoIntent.putExtra("pin_no", pin_nos[position]);
        recInfoIntent.putExtra("type", types[position]);
        recInfoIntent.putExtra("ccode", client_code.get(position)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        recInfoIntent.putExtra("deno_status", deno_status.get(position));
        recInfoIntent.putExtra("client_amt", client_amt.get(position));
        recInfoIntent.putExtra("captions", captions.get(position));
        recInfoIntent.putExtra("amt", amts.get(position));
        recInfoIntent.putExtra("point_name", cust_names[position]);
        recInfoIntent.putExtra("stop_id", stop_id.get(position));
//qr json
        recInfoIntent.putExtra("stop_id", stop_id.get(position));
//        recInfoIntent.putExtra("qr_json", qr_json.get(position));

        recInfoIntent.putExtra("rec_pos", rec_pos.get(position));
        recInfoIntent.putExtra("qr_status", qr_status.get(position));
        recInfoIntent.putExtra("dupRecptNoValStatus", dupRecptNoValStatus.get(position));
        recInfoIntent.putExtra("otp_flag", otp_flags[position]);
        recInfoIntent.putExtra("otp_day", otp_days[position]);
        recInfoIntent.putExtra("shop_id", shop_id.get(position));
//        recInfoIntent.putExtra("dep_typess", dep_typess.get(position));
        Log.e(TAG,"otp_flag_chack ->"+otp_flags[position]);
        Log.e(TAG,"otp_day_chack ->"+otp_days[position]);

        //Log.v("Transaction","deno_status"+deno_status.get(position));
        finish();
        startActivityForResult(recInfoIntent, REQUEST_CODE_LIST_ON_CLICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIST_ON_CLICK && resultCode == RESULT_OK)
            initializeComponents();
    }

    @Override
    public void onBackPressed() {
        final Intent inte = new Intent(this, ModeOfTransactionActivity.class);
        inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(inte);
        finish();
    }

    public void onRequestCompleted(JSONObject object) {
        progressDialog.dismiss();
        if (object != null) {
            if (Config.DEBUG) {
                //Log.d(TAG, "Result Json: " + object.toString());
            }
            try {
                String msg = object.getString("msg");
                if (Config.DEBUG) {
                    //Log.d(TAG, "Msg:" + msg);
                }
                if (msg.equals("success")) {

                    try {
                        // test 27.7.17
                        SharedData sharedData = SharedData.getInstance(this);
                        sharedData.saveData("transactions", object.toString());
                        //Log.e(TAG,"----Tatal Response For List----"+sharedData.getData("transactions"));

                    } catch (Exception e) {
                        Log.e(TAG, "----Exception----" + e.toString());
                    }

                    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                    HashMap<String, String> value;
                    JSONArray ja = object.getJSONArray("transactions");
                    if (Config.DEBUG) {
                        //Log.d(TAG, "Json Array: " + ja.toString());
                    }
                    dbHandler.delete("delete from  transactions");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject inner_jo = ja.getJSONObject(i);
                        amount = inner_jo.getString("amount");
                        pickup_session =inner_jo.getString("pickup_session");
                        point_name = inner_jo.getString("point_name");
                        cust_name = inner_jo.getString("cust_name");
                        type = inner_jo.getString("type");
                        trans_id = inner_jo.getString("trans_id");
                        deposit_type_strng = inner_jo.getString("dep_typess");
                        dep_type_data = inner_jo.getString("dep_typess");
//                        Log.d("dep_type_data", "dep_type_data" + dep_type_data);
                        pin_status = inner_jo.getString("pin_status");
                        //End
                        trans_date = inner_jo.getString("trans_date");
                        //Log.v("Transaction","trans"+trans_date);
                        client_code_txt = inner_jo.getString("client_code");
                        client_code.add(inner_jo.getString("client_code"));
                        deno_status.add(inner_jo.getString("deno_status"));
                        amts.add(inner_jo.getString("amount"));
                        client_amt.add(inner_jo.getString("client_code_amount"));
                        pin_no = inner_jo.getString("pin_no");
                        otp_flag = inner_jo.getString("otp_flag");
                        otp_day = inner_jo.getString("otp_day");
                        //Log.v(TAG,"client code service"+client_code.get(i)+":"+cust_name);
                        //if(inner_jo.has("captions"))
                        captions.add(inner_jo.getString("captions"));
                        shop_id.add(inner_jo.getString("shop_id"));
                        stop_id.add(inner_jo.getString("stop_id"));
/// qr json
//                        qr_json.add(inner_jo.getString("qr_json"));
// Evening pickup
//                        pickupSession.add(inner_jo.getString("pickup_session"));

                        rec_pos.add(inner_jo.getString("rec_pos"));
                        qr_status.add(inner_jo.getString("qr_status"));
                        dupRecptNoValStatus.add(inner_jo.getString("dupRecptNoValStatus"));
//                        dep_typess.add(inner_jo.getString("dep_typess"));
//                        Log.d("dep_typessss:","dep_typessss"+dep_typess);
                        /*else

                            captions.add("Pickup Amount,Seal Tag No, PIS No, HCI No, Receipt No");*/
                        if (Config.DEBUG) {
                            //Log.d(TAG, "Result Inner Object: "+pin_status+"," + amount + ","
                            //       + point_name + "," + cust_name + "," + type
                            //    + "," + trans_id);
                        }

                        calendar = Calendar.getInstance();
                        day = calendar.get(Calendar.DAY_OF_MONTH);
                        month = calendar.get(Calendar.MONTH);
                        year = calendar.get(Calendar.YEAR);
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        minute = calendar.get(Calendar.MINUTE);

                        if (dbHandler.isExistRow("transactions", trans_id)) {
                            dbHandler
                                    .execute("update transactions set point_name='"
                                            + point_name
                                            + "', cust_name='"
                                            + cust_name
                                            + "', dep_typeee='" +
                                            dep_type_data + "', type='"
                                            + type
                                            + "', amount="
                                            + amount
                                            + "', pickup_session="
                                            + pickup_session
                                            //Rajapandiyan
                                            + ", pin_status='"
                                            + pin_status
                                            + "', client_code='" + client_code_txt// End
                                            + "' where trans_id='"
                                            + trans_id
                                            + "'");
                            Log.d("Updatedd", "Updatedd");
                            if (Config.DEBUG) {
                                //Log.d(TAG, "The transaction with trans_id: "
                                //+ trans_id + " updated");
                            }

                        } else {
                            contentValues = new ContentValues();
                            contentValues.put("trans_id", trans_id);
                            contentValues.put("point_name", point_name);
                            contentValues.put("client_code", client_code_txt);
                            contentValues.put("deno_status", deno_status.get(i));
                            contentValues.put("client_amt", client_amt.get(i));
                            contentValues.put("cust_name", cust_name);
                            contentValues.put("type", type);
                            contentValues.put("amount", amount);
                            contentValues.put("pin_status", pin_status);
                            contentValues.put("ce_id", ce_id);
                            contentValues.put("day", day);
                            contentValues.put("month", month);
                            contentValues.put("year", year);
                            contentValues.put("hour", hour);
                            contentValues.put("minute", minute);
                            contentValues.put("pickup_session", pickup_session);
                            contentValues.put("client_code", client_code.get(i));
                            contentValues.put("captions", captions.get(i));
                            contentValues.put("shop_id", shop_id.get(i));
                            contentValues.put("trans_date", trans_date);
                            contentValues.put("pin_no", pin_no);
                            contentValues.put("dep_typeee", deposit_type_strng);
                            contentValues.put("otp_flag",otp_flag);
                            contentValues.put("otp_day",otp_day);
                            //      //Log.v(TAG,"client code services"+client_code.get(i));
                            dbHandler.insert("transactions", contentValues);
                        }
                    }

                    ContentValues contentValues[] = dbHandler
                            .select("select * from transactions where show='yes' and ce_id='"
                                    + ce_id + "'");
                    int n = contentValues.length;
                    types = new String[n];
                    trans_ids = new String[n];
                    pin_statuss = new String[n];
                    pin_nos = new String[n];
                    otp_flags = new String[n];
                    otp_days = new String[n];
                    cust_names = new String[n];
                    deposit_type_strng_array = new String[n];

                    if (n == 0)
                        Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
                    client_code.clear();
                    deno_status.clear();
                    client_amt.clear();
                    captions.clear();
                    shop_id.clear();
                    dep_typess.clear();
                    amts.clear();
                    for (int i = 0; i < contentValues.length; i++) {
                        types[i] = contentValues[i].getAsString("type");
                        trans_ids[i] = contentValues[i].getAsString("trans_id");
                        pin_statuss[i] = contentValues[i].getAsString("pin_status");
                        cust_names[i] = contentValues[i].getAsString("cust_name");
                        deposit_type_strng_array[i] = contentValues[i].getAsString("dep_typeee");
                        pin_nos[i] = contentValues[i].getAsString("pin_no");
                        otp_flags[i] = contentValues[i].getAsString("otp_flag");
                        otp_days[i] = contentValues[i].getAsString("otp_day");
                        amount = contentValues[i].getAsString("amount");
                        pickup_session = contentValues[i].getAsString("pickup_session");

                        point_name = contentValues[i].getAsString("point_name");
                        client_code_txt = contentValues[i].getAsString("client_code");
                        client_code.add(client_code_txt);
                        captions.add(contentValues[i].getAsString("captions"));
                        shop_id.add(contentValues[i].getAsString("shop_id"));
                        deno_status.add(contentValues[i].getAsString("deno_status"));
                        amts.add(contentValues[i].getAsString("amount"));
                        client_amt.add(contentValues[i].getAsString("client_amt"));
                        cust_name = contentValues[i].getAsString("cust_name");
                        value = new HashMap<String, String>();
                        value.put("amount", amount);
                        value.put("point_name", point_name);
                        value.put("cust_name", cust_name);
                        value.put("trans_type", contentValues[i].getAsString("type"));
                        value.put("pickup_session",pickup_session);


                        String temp_str = "";
                        if (client_code_txt.length() < 0 || client_code_txt.equals(""))
                            temp_str = "0";
                        else
                            temp_str = String.valueOf(client_code_txt.split(",").length);
                        value.put("client_code", String.valueOf("Client Code:" + temp_str));
                        //Log.v(TAG,"client code length"+client_code_txt+":"+contentValues[i].getAsString("cust_name")+":"+temp_str);
                        list.add(value);
                    }
                    trans_listview.setAdapter(new SimpleAdapter(this, list, R.layout.translist_item,
                            new String[]{"amount", "point_name", "cust_name", "trans_type", "client_code", "pickup_session"}, new int[]
                            { R.id.amount_txt, R.id.pointname_txt, R.id.customername_txt, R.id.amount_txt_v,R.id.ccode_txt,R.id.session_txt}));
                } else
                    Toast.makeText(getApplicationContext(), "No Record Found", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
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
