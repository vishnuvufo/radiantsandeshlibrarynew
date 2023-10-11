package com.mountfox;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;


public class GetJson extends
		AsyncTask<List<BasicNameValuePair>, Void, JSONObject> {

	public interface CallbackInterface {
		public void onRequestCompleted(JSONObject object);
	}

	private static final String TAG = "JSON Parser";
	private String webservice_url = "" ;
	private String result = "";
	private JSONObject jsonObject = null;
	private CallbackInterface mCallback;
	private Context context;

	public GetJson(Context context,CallbackInterface callback) {
		this.context = context;
		mCallback = callback;
		if(SharedPreference.getDefaults(context, ConstantValues.TAG_URLVALIDATE).equals("dontswap")){
			webservice_url=Config.url1;
		}else if(SharedPreference.getDefaults(context, ConstantValues.TAG_URLVALIDATE).equals("swap")) {
			webservice_url=Config.url2;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected JSONObject doInBackground(List<BasicNameValuePair>... params) {
		List<BasicNameValuePair> url = params[0];
		return getJSONFromURL(url);
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		mCallback.onRequestCompleted(result);
	}

	private JSONObject getJSONFromURL(List<BasicNameValuePair> mEntity) {
		try {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setTcpNoDelay(params, true);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);
			HttpClient httpclient = new DefaultHttpClient(params);
			HttpPost httppost = new HttpPost(webservice_url);
			httppost.setEntity(new UrlEncodedFormEntity((List<? extends NameValuePair>) mEntity));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity httpEntity = response.getEntity();
			result = EntityUtils.toString(httpEntity);
			if (Config.DEBUG) {
				Log.d(TAG, "Result : " + result);
			}
			jsonObject = new JSONObject(result);

		} catch (IOException e) {
			Log.e("Connection Timed","Connection Timed");
			if (Config.DEBUG) {
				Log.e("Connection Timed","Connection Timed");
			}
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			if (Config.DEBUG) {
			}
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			if (Config.DEBUG) {
			}
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}
}