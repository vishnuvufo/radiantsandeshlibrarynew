package com.mountfox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EditReceipt extends Activity {

    ProgressDialog          progressDialog;
    ArrayList<String>       trans_id        =   new ArrayList<String>();
    ArrayList<String>       point_name      =   new ArrayList<String>();
    ArrayList<String>       cust_name       =   new ArrayList<String>();
    ArrayList<String>       type            =   new ArrayList<String>();
    ArrayList<String>       req_amount      =   new ArrayList<String>();
    ArrayList<String>       pickup_amount   =   new ArrayList<String>();
    ArrayList<String>       client_code     =   new ArrayList<String>();
    ArrayList<String>       trans_date      =   new ArrayList<String>();
    List<BasicNameValuePair> mEntity        =   new ArrayList<BasicNameValuePair>();
    GPSTracker              gpsTracker;
    ListView                trans_listView;
    String                  ce_id           =   "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_edit_receipt);
        final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        trans_listView      = (ListView) findViewById(R.id.trans_listview);
        gpsTracker          =   new GPSTracker(this);
        ce_id               =   getIntent().getStringExtra("ce_id");
        if(gpsTracker.canGetLocation()) {
            mEntity.add(new BasicNameValuePair("ce_id", getIntent().getStringExtra("ce_id")));
            mEntity.add(new BasicNameValuePair("opt", "view_rec"));
            mEntity.add(new BasicNameValuePair("lat",String.valueOf(gpsTracker.getLatitude())));
            mEntity.add(new BasicNameValuePair("lon",String.valueOf(gpsTracker.getLongitude())));
            mEntity.add(new BasicNameValuePair("IMIE",String.valueOf(telephonyManager.getDeviceId())));
            mEntity.add(new BasicNameValuePair("final","1"));
            new Get_Rec().execute();
        }
        else
            gpsTracker.showSettingsAlert();
    trans_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            startActivity(new Intent(EditReceipt.this,EditPayment.class).putExtra("ce_id",ce_id).putExtra("lat", String.valueOf(gpsTracker.getLatitude())).putExtra("lon",String.valueOf(gpsTracker.getLongitude())).putExtra("imei",telephonyManager.getDeviceId()).putExtra("trans_id",trans_id.get(i)).putExtra("type",type.get(i)).putExtra("pickup_amount",pickup_amount.get(i)).putExtra("point_name",point_name.get(i)).putExtra("cust_name",cust_name.get(i)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    });
    }

    class Get_Rec extends AsyncTask<Void,Void,Void>
    {

        String          result  =   "";
        JSONObject      jsonObject;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> value;
        @Override
        protected void onPreExecute() {
            progressDialog      =   new ProgressDialog(EditReceipt.this);
            progressDialog.setMessage("Loading, Please Wait ...");
            progressDialog.show();
            trans_date.clear();
            trans_id.clear();
            point_name.clear();
            cust_name.clear();
            type.clear();
            req_amount.clear();
            pickup_amount.clear();
            client_code.clear();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            trans_listView.setAdapter(new SimpleAdapter(EditReceipt.this, list,
                    R.layout.translist_item, new String[]{
                    "pickup_amount", "point_name", "cust_name"},
                    new int[]{R.id.amount_txt, R.id.pointname_txt,
                            R.id.customername_txt}));
            if(point_name.size()<0)
                Toast.makeText(EditReceipt.this,"No Record Found",Toast.LENGTH_SHORT).show();
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setTcpNoDelay(params, true);
                HttpClient httpclient = new DefaultHttpClient(params);
                HttpPost httppost = new HttpPost(Config.url1);
                httppost.setEntity(new UrlEncodedFormEntity( mEntity));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);
                if (Config.DEBUG) {
                    //Log.d("EditReceipt", "Result : " + result);
                }
                jsonObject = new JSONObject(result);
                if(jsonObject.getString("msg").equalsIgnoreCase("success")){
                    JSONArray   jsonArray   =   jsonObject.getJSONArray("transactions");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject  jsonObject1 =  jsonArray.getJSONObject(i);
                                trans_id.add(jsonObject1.getString("trans_id"));
                                point_name.add(jsonObject1.getString("point_name"));
                                cust_name.add(jsonObject1.getString("cust_name"));
                                type.add(jsonObject1.getString("type"));
                                req_amount.add(jsonObject1.getString("req_amount"));
                                pickup_amount.add(jsonObject1.getString("pickup_amount"));
                                client_code.add(jsonObject1.getString("client_code"));
                                trans_date.add(jsonObject1.getString("trans_date"));
                                value = new HashMap<String, String>();
                                value.put("pickup_amount", pickup_amount.get(i));
                                value.put("point_name", point_name.get(i));
                                value.put("cust_name", cust_name.get(i));
                                list.add(value);
                            }
                }
            }catch (Exception e){e.printStackTrace();
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(EditReceipt.this,Home.class).putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        super.onBackPressed();
    }

}
