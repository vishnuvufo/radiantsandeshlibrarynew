package com.mountfox;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Locate_Me extends Activity {

    ListView                lst_fav;
    ArrayList<String>       points  =   new ArrayList<String>();
    ArrayAdapter<String>    adapter;
    String                  imei    =   "",simno="",lat="",lon="";
    GPSTracker              gpsTracker;
    TelephonyManager        telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate__me);
        lst_fav         = (ListView) findViewById(R.id.lst_fav);
        try {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei            = telephonyManager.getDeviceId();
            simno           = telephonyManager.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gpsTracker      = new GPSTracker(this);
        if(gpsTracker.canGetLocation()) {
            lat = String.valueOf(gpsTracker.getLatitude());
            lon = String.valueOf(gpsTracker.getLongitude());
        }
        else
            gpsTracker.showSettingsAlert();
        if(Utils.isInternetAvailable(this))
            new Fetch_Points().execute();
        else
            Toast.makeText(this,"This transaction is in pending. Please enable the internet to synchronize with server.",Toast.LENGTH_SHORT).show();
    }
    class Fetch_Points extends AsyncTask<Void,Void,Void>
    {

        StringBuilder       stringBuilder   =   new StringBuilder();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter         = new ArrayAdapter<String>(getApplicationContext(),R.layout.locate_adap,R.id.txt_fav,points);
            lst_fav.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                HttpClient httpClient      =   new DefaultHttpClient();
                HttpPost httpPost        =   new HttpPost(Config.url1);
                List<NameValuePair> list        =   new ArrayList<NameValuePair>();

                list.add(new BasicNameValuePair("opt", "view_trans"));
                list.add(new BasicNameValuePair("ce_id", "RAD0039"));
                list.add(new BasicNameValuePair("lat",  lat));
                list.add(new BasicNameValuePair("lon",  lon));
                list.add(new BasicNameValuePair("IMIE", imei));
                list.add(new BasicNameValuePair("final", "1"));
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse    =   httpClient.execute(httpPost);

                BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF8"));
                String          test    =   "";
                while ((test=bufferedInputStream.readLine())!=null)
                {
                    stringBuilder.append(test);
                    //Log.v("Locate_Me", test + "asd" + httpResponse.getStatusLine().getStatusCode());
                }
                JSONObject jsonObject  =   new JSONObject(stringBuilder.toString());
                JSONArray jsonArray   =   jsonObject.getJSONArray("transactions");
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject  jsonObject1 =   jsonArray.getJSONObject(i);
                    points.add(jsonObject1.getString("point_name"));
                }
            }catch (Exception e){e.printStackTrace();}
            return null;
        }
    }
}
