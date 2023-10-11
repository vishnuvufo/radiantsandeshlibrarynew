package com.mountfox.Services.serviceRequest;

import android.util.Log;

import com.mountfox.Config;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings("deprecation")
public class ServiceRequestPOSTImpl implements ServiceRequest {



	static StringBuilder sb = new StringBuilder();
	public static final String TAG = ServiceRequestPOSTImpl.class.getSimpleName();
	public static int statusCode;
	public String requestService(String requestURL, String requestData) {

	//	String radmustwo = Config.url2;

		try {
			URL url = new URL(requestURL);
			URLConnection urlConnection = (HttpURLConnection) url.openConnection();
			((HttpURLConnection) urlConnection).setRequestMethod("POST");
			urlConnection.setConnectTimeout(10000);
			urlConnection.connect();
		//	statusCode = ((HttpURLConnection) urlConnection).getResponseCode();
		//	Log.e(TAG,"status code->"+statusCode);

		//	if(statusCode==200){
				Log.e(TAG,"radmus one ->"+requestURL);
				Log.e(TAG,"radmus one data ->"+requestData);
				sb.setLength(0);
				HttpClient client = new DefaultHttpClient();
				HttpPost req  = new HttpPost(new URI(requestURL));
				req.setEntity(new StringEntity(requestData));
				req.setHeader("Accept", "application/json");
				req.setHeader("Content-type", "application/json");
				HttpResponse resp = client.execute(req);
				BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
				sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				client.getConnectionManager().closeExpiredConnections();
				client.getConnectionManager().shutdown();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG,"eee ->"+e.getMessage());
			sb.setLength(0);
			HttpClient client = new DefaultHttpClient();
			HttpPost req  = null;
			try {
				req = new HttpPost(new URI(requestURL));
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			try {
				req.setEntity(new StringEntity(requestData));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			req.setHeader("Accept", "application/json");
			req.setHeader("Content-type", "application/json");
			HttpResponse resp = null;
			try {
				resp = client.execute(req);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			sb = new StringBuilder();
			String line;
			while (true) {
				try {
					if (!((line = br.readLine()) != null)) break;
					sb.append(line + "\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			try {
				br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			client.getConnectionManager().closeExpiredConnections();
			client.getConnectionManager().shutdown();
		}
		return sb.toString();
	}




}

