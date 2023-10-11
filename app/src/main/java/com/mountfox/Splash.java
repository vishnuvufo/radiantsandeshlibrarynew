package com.mountfox;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.*;
import android.widget.ProgressBar;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Splash extends Activity implements GetJson.CallbackInterface
{
    private     ProgressBar progressBar;
    private     DbHandler   dbHandler;
    private     int         wait        = 0;
    private     GetJson     getJson,getJson1;
    private     List<BasicNameValuePair> entity,entity1;
    private SharedPreferences   sharedPreferences;
    private AlarmManager        alarmManager;
    private Calendar            calendar,cal;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Intent intent = new Intent("com.mountfox.receive");

        PendingIntent sender = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        // We want the alarm to go off 3 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 3 ;//start 3 seconds after first register.

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) this
                .getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                600000, sender);

        progressBar         = (ProgressBar) findViewById(R.id.loadingProgress);

        sharedPreferences   =   getSharedPreferences("mountfox", Context.MODE_PRIVATE);
        dbHandler           = new DbHandler(this);
        dbHandler.createables();
        if(sharedPreferences.getString("check_stat","1").equalsIgnoreCase("1"))
        {

            if(!Utils.isInternetAvailable(Splash.this))
            {
                final AlertDialog alertDialog =   new AlertDialog.Builder(Splash.this).create();
                alertDialog.setMessage("Enable Internet Connection");
                alertDialog.setButton(DialogInterface.BUTTON1,"Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                        startActivity(intent);
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON2,"Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
            }
            else {
                //Date    date    =   new Date();
                //date.setTime(System.currentTimeMillis());
                //date.setHours(11);
          /*  calendar       =  Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

         //   calendar.add(Calendar.MINUTE,0);
         //   calendar.set(2015,5,04,11,00,00);
            cal           =     Calendar.getInstance();
         //   cal.set(Calendar.AM_PM,Calendar.PM);
            cal.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE),23
                        ,0,0);
            calendar.setTimeInMillis(cal.getTimeInMillis());
            Intent          inten         =   new Intent(this,Delete_Db_Receive.class);
            alarmManager                  = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent   =   PendingIntent.getBroadcast(this,12,inten,PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            //Log.v("Splash","Alarm set"+String.valueOf(calendar.get(Calendar.MONTH)));*/
                getJson = new GetJson(Splash.this,this);
                entity = new ArrayList<BasicNameValuePair>();
                entity.add(new BasicNameValuePair("opt", "bank_list"));
                getJson.execute(entity);
                entity1 = new ArrayList<BasicNameValuePair>();
                entity1.add(new BasicNameValuePair("opt", "vault_list"));

                getJson1 =
                        new GetJson(Splash.this,new GetJson.CallbackInterface() {

                            @Override
                            public void onRequestCompleted(JSONObject object) {
                                try {
                                    ArrayList<String> list = new ArrayList<String>();
                                    String msg = object.getString("msg");
                                    if (msg.equalsIgnoreCase("success")) {
                                        JSONArray jsonArray = object.getJSONArray("vaults");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            list.add(jsonArray.getJSONObject(i).getString("valut_name"));
                                            //Log.v("Splash1", "vaultname" + list.get(i));
                                        }
                                        dbHandler.insert_vaultName(list);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                getJson1.execute(entity1);
                Thread welcomeThread = new Thread() {

                    public void run() {
                        try {
                            super.run();
                            while (wait <=100) {
                                sleep(100);
                                if(sharedPreferences.getString("check_stat","2").equalsIgnoreCase("2"))
                                    wait+=10;
                            }
                        } catch (Exception e) {
                            System.out.println("Splash Screen Exception=" + e);
                        } finally {
                            startActivity(new Intent(getApplicationContext(),
                                    Login.class));
                            finish();
                        }
                    }
                };
                welcomeThread.start();
            }
        }
        else
        {
            wait=10;
            Thread welcomeThread = new Thread() {
                public void run() {
                    try {
                        super.run();
                        while (wait <=100) {
                            sleep(100);
                            if(sharedPreferences.getString("check_stat","2").equalsIgnoreCase("2"))
                                wait+=10;
                        }
                    } catch (Exception e) {
                        System.out.println("Splash Screen Exception=" + e);
                    } finally {
                        startActivity(new Intent(getApplicationContext(),
                                Login.class));
                        finish();
                    }
                }
            };
            welcomeThread.start();
        }
        final int welcomeScreenDisplay = 3000;
    }

    @Override
    public void onRequestCompleted(JSONObject object)
    {
        try
        {
            ArrayList<String>   list    =   new ArrayList<String>();
            ArrayList<String>   branch  =   new ArrayList<String>();
            ArrayList<String>   acc_no  =   new ArrayList<String>();
            ArrayList<String>   acc_ids =   new ArrayList<String>();
            String              msg     =   object.getString("msg");
            if(msg.equalsIgnoreCase("success"))
            {
                JSONArray   jsonArray   =   object.getJSONArray("banks");
                for(int i=0;i<jsonArray.length();i++)
                {
                    list.add(jsonArray.getJSONObject(i).getString("bank_name"));
                    branch.add(jsonArray.getJSONObject(i).getString("branch_name"));
                    acc_no.add(jsonArray.getJSONObject(i).getString("acc_no"));
                    acc_ids.add(jsonArray.getJSONObject(i).getString("acc_id"));
                    //Log.v("Splash2","bankname"+list.get(i));
                }
                dbHandler.insert_bankName(list,acc_ids,branch,acc_no);
            }
            wait=1000;
            sharedPreferences.edit().putString("check_stat","2").commit();
        }
        catch (Exception e)
        {
            wait=1000;
            e.printStackTrace();
        }
    }
}