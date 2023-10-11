package com.mountfox;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by RITS03 on 11-06-2015.
 */
public class Get_Json extends AsyncTask<List<Pair<String, String>>, Integer, JSONObject> {

    public static final String TAG = Get_Json.class.getSimpleName();
    Context context;
    ProgressDialog progressDialog;
    Result_Json result_json;
    public static boolean run = false;
    public static String page_name = "";
    HttpURLConnection connection = null;
    public String URL="";
    Connectivity connectivity;

    public interface Result_Json {
        public void OnRequestCompleted(JSONObject jsonObject) throws JSONException;
    };

    public Get_Json(Context context, Result_Json result_json) {
        this.context = context;
        this.result_json = result_json;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Message");
        progressDialog.setMessage("Please Wait, We are Initializing..");
        progressDialog.setCancelable(false);
    }


    @Override
    protected void onPreExecute() {

        Log.e("Get_Json", "Loading ");
        if (connectivity.isConnected(context)) {
            Log.e("Internet is Connected", "Internet is Connected");
            if (!progressDialog.isShowing() && progressDialog != null)
                progressDialog.show();
            if (!connectivity.isConnectedFast(context))
                Toast.makeText(context, "You are using slow, will take more time to load", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("Json Else blck", "Json Else blck");
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Warning!...");
            alertDialog.setMessage("You are not connected to internet");
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    int p = android.os.Process.myPid();
                    android.os.Process.killProcess(p);
                    System.exit(0);
                }
            });
            alertDialog.show();
            cancel(true);
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        run = false;
        try {
            if (progressDialog.isShowing() && progressDialog != null)
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPostExecute(jsonObject);
    }


    @Override
    protected void onCancelled(JSONObject jsonObject) {
        if (connection != null)
            connection.disconnect();
        super.onCancelled(jsonObject);
    }

    @Override
    protected JSONObject doInBackground(List<Pair<String, String>>... pairList) {

       /* ((Activity) context).runOnUiThread(new Runnable(){
            public void run() {
                progressDialog.show();
            }});*/
        InputStream is = null;
        String result = "";
        JSONObject jsonObject = null;
        StringBuffer sb = new StringBuffer("");
        try {
            if(SharedPreference.getDefaults(context, ConstantValues.TAG_URLVALIDATE).equals("dontswap")){
                URL=Config.url1;
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "");
                connection.setReadTimeout(25000);
                connection.setConnectTimeout(25000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                Log.e("Get_Json", "url:" + url.toString());
            }else if(SharedPreference.getDefaults(context, ConstantValues.TAG_URLVALIDATE).equals("swap")) {
                URL=Config.url2;
                URL url = new URL(URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "");
                connection.setReadTimeout(25000);
                connection.setConnectTimeout(25000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                Log.e("Get_Json", "url:" + url.toString());
            }

            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            Uri.Builder builder = new Uri.Builder();
            for (int i = 0; i < pairList[0].size(); i++) {
                Log.e("Get_Json", "pair:" + pairList[0].get(i).first + ":" + pairList[0].size());
                builder.appendQueryParameter(pairList[0].get(i).first, pairList[0].get(i).second);
            }

            Log.e("Get_Json", "params:" + builder.build().getEncodedQuery());
            bufferedWriter.write(builder.build().getEncodedQuery());
            //List<Pair<String,String>> pairList    =   new ArrayList<>();
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            connection.connect();

       //     Log.e("Get_Json", "url:" + url.toString());
            Log.e("Connection Url", "Connection Url" + connection.getURL());
            Log.e("Buffer Writer", "Buffer Writer" + bufferedWriter.toString());

            InputStream inputStream = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";


            //Log.d("Get_Json","rd"+rd.readLine());
            while ((line = rd.readLine()) != null) {
                sb.append(line);
                Log.e("Get_Json", "get_json:" + line);
            }
            connection.disconnect();
        } catch (IOException e) {
            // writing exception to log
            connection.disconnect();
            e.printStackTrace();
        }

        try {
            Log.e("Get_Json", "response:" + sb.toString());
            jsonObject = new JSONObject(sb.toString());
            Log.e("Get_Json", "JsonObj response:" + jsonObject.toString());
            result_json.OnRequestCompleted(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*


        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                Log.v("Get_Json", line);
            }
            is.close();
            result = sb.toString();
        } catch(Exception e) {

            return null;
        }

        // Convert string to object
        try {
            jsonObject = new JSONObject(result);
            Log.v("Get_Json", "result:" + jsonObject.toString());
        } catch(JSONException e) {
e.printStackTrace();
            return null;
        }*/
        /*((Activity) context).runOnUiThread(new Runnable(){
            public void run() {
                progressDialog.dismiss();
            }});*/
        return jsonObject;

    }
}