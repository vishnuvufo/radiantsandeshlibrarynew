package com.mountfox;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;


public class Service extends Activity implements GetJson.CallbackInterface {

	String imei = "908372827282", simno = "721907317389173173", ce_id = "",
			trans_id = "", pickup_amount = "", deposit_slip = "", pis_no = "",
			hci_no = "", sealtag_no = "", dep_amount = "", rec_status = "",
			remarks = "", device_id = "", type = "";
	double latitude = 12.982733625, longitude = 80.252031675;
	int pin_no = 261976;
	List<BasicNameValuePair> params;
	private static final String TAG = "Result Json";
	ProgressDialog progressDialog;

	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Fetching Information");
		progressDialog.setMessage("Please Wait...");
		progressDialog.setCancelable(false);

		try {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
			simno = telephonyManager.getSimSerialNumber();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Log.i("Imei:", imei);
		// //Log.i("SimNo: ",simno);

		GetJson getJson = new GetJson(Service.this,this);
		progressDialog.show();

		// RQ1 Login
		params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("opt", "login"));
		params.add(new BasicNameValuePair("pin_no", "" + pin_no));
		params.add(new BasicNameValuePair("IMIE", ""));
		params.add(new BasicNameValuePair("simno", ""));
		params.add(new BasicNameValuePair("final", "1"));
		//getJson.execute(params);

		// RQ2 View Transactions of the user
		int lat = (int) (latitude * 1E6);
		int lon = (int) (longitude * 1E6);
		//Log.i("Lat & Lon :", lat + "," + lat);
		ce_id = "RAD9497";
		params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("opt", "view_trans"));
		params.add(new BasicNameValuePair("ce_id", ce_id));
		params.add(new BasicNameValuePair("lat", "" + lat));
		params.add(new BasicNameValuePair("lon", "" + lon));
		params.add(new BasicNameValuePair("IMIE", imei));
		params.add(new BasicNameValuePair("final", "1"));
		getJson.execute(params);

		// RQ3 Receive Fund and Update with server
		type = "Pickup";
		trans_id = "4904963";//"4795656"
		pickup_amount = "250";
		deposit_slip = "save";
		pis_no = "123";
		hci_no = "234";
		sealtag_no = "344";
		dep_amount = "28112";
		rec_status = "Others";
		remarks = "Received Today";
		device_id = imei;
		
		/*	 1.	Cash Received
			 2.	No Cash
	 	   	 3.	CE visited in No cash 
			 4.	Shop Closed 
			 5.	Difference in Cash 
			 6.	Customer refused to sign 
			 7.	Cash Delivered
			 8.	Partially Cash Delivered 
			 9.	No Cash Delivered 
			10.	Customer refused to Accept 
			11.	Others	*/

		params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("opt", "rec_info"));
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("ce_id", ce_id));
		params.add(new BasicNameValuePair("trans_id", trans_id));
		params.add(new BasicNameValuePair("pickup_amount", pickup_amount));
		params.add(new BasicNameValuePair("deposit_slip", deposit_slip));
		params.add(new BasicNameValuePair("pis_no", pis_no));
		params.add(new BasicNameValuePair("hci_no", hci_no));
		params.add(new BasicNameValuePair("sealtag_no", sealtag_no));
		params.add(new BasicNameValuePair("dep_amount", dep_amount));
		params.add(new BasicNameValuePair("rec_status", rec_status));
		params.add(new BasicNameValuePair("remarks", remarks));
		params.add(new BasicNameValuePair("device_id", device_id));
		params.add(new BasicNameValuePair("final", "1"));
		//getJson.execute(params);

		// RQ4 View Receipt Transactions of the user
		params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("opt", "view_rec"));
		params.add(new BasicNameValuePair("ce_id", ce_id));
		params.add(new BasicNameValuePair("lat", "" + latitude));
		params.add(new BasicNameValuePair("lon", "" + longitude));
		params.add(new BasicNameValuePair("IMIE", imei));
		params.add(new BasicNameValuePair("final", "1"));
		//getJson.execute(params);

		// RQ5 View Bill of the transaction
		trans_id = "4795656";
		params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("opt", "view_bill"));
		params.add(new BasicNameValuePair("ce_id", ce_id));
		params.add(new BasicNameValuePair("trans_id", trans_id));
		params.add(new BasicNameValuePair("lat", "" + latitude));
		params.add(new BasicNameValuePair("lon", "" + longitude));
		params.add(new BasicNameValuePair("IMIE", imei));
		params.add(new BasicNameValuePair("final", "1"));
		//getJson.execute(params);

		//RQ6 Cancel Receipt
		trans_id = "47956";
		params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("opt", "cancel_rec"));
		params.add(new BasicNameValuePair("trans_id", trans_id));
		params.add(new BasicNameValuePair("ce_id", ce_id));
		params.add(new BasicNameValuePair("lat", "" + latitude));
		params.add(new BasicNameValuePair("lon", "" + longitude));
		params.add(new BasicNameValuePair("IMIE", imei));
		params.add(new BasicNameValuePair("final", "1"));
		//getJson.execute(params);

		// RQ7 Eod Print
		params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("opt", "eod_print"));
		params.add(new BasicNameValuePair("ce_id", ce_id));
		params.add(new BasicNameValuePair("lat", "" + latitude));
		params.add(new BasicNameValuePair("lon", "" + longitude));
		params.add(new BasicNameValuePair("IMIE", imei));
		params.add(new BasicNameValuePair("final", "1"));
		//getJson.execute(params);.
		
		
	}

	@Override
	public void onRequestCompleted(JSONObject object) {
		if (object != null) {
			//Log.i(TAG, object.toString());
		}
		progressDialog.dismiss();
	}
}
