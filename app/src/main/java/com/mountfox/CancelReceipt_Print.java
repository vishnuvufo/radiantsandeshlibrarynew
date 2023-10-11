package com.mountfox;

import java.util.ArrayList;
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

public class CancelReceipt_Print extends Activity implements
		OnItemClickListener, GetJson.CallbackInterface {

	String ce_id = "", imei = "908372827282", trans_id = "", point_name = "",
			cust_name = "", type = "", req_amount = "", pickup_amount = "";
	int lat = 1, lon = 2;
	double latitude = 12.982733625, longitude = 80.252031675;

	String pickup_amounts[], req_amounts[], point_names[], cust_names[],
			types[], trans_ids[];

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
		progressDialog.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progressbar));
		gpsTracker = new GPSTracker(this);

		ce_id = getIntent().getStringExtra("ce_id");
		dbHandler = new DbHandler(CancelReceipt_Print.this);

		if (PrinterSelection.isPrinterConnected)
			connectPrinter.setImageResource(R.drawable.printer_on);
		else
			connectPrinter.setImageResource(R.drawable.printer_off);

		connectPrinter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(CancelReceipt_Print.this,
						PrinterSelection.class));
			}
		});
		loadData();
	}

	@SuppressWarnings("unchecked")
	public void loadData() {
		if (Utils.isInternetAvailable(getApplicationContext())) {
			try {
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				imei = telephonyManager.getDeviceId();
			} catch (Exception e) {
				e.printStackTrace();
			}
			gpsTracker = new GPSTracker(this);

			if (gpsTracker.canGetLocation()) {
				latitude = gpsTracker.getLatitude();
				longitude = gpsTracker.getLongitude();
				lat = (int) (latitude * 1E6);
				lon = (int) (longitude * 1E6);
				//Log.i("Lat & Lon :", lat + "," + lat);

				progressDialog.setTitle("Fetching Information");
				progressDialog.show();
				params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("opt", "view_rec"));
				params.add(new BasicNameValuePair("ce_id", ce_id));
				params.add(new BasicNameValuePair("lat", "" + lat));
				params.add(new BasicNameValuePair("lon", "" + lon));
				params.add(new BasicNameValuePair("IMIE", imei));
				params.add(new BasicNameValuePair("final", "1"));
				GetJson getJson = new GetJson(CancelReceipt_Print.this,this);
				getJson.execute(params);
			} else
				Toast.makeText(
						getApplicationContext(),
						"Please enable the Location Service(GPS/WIFI) for view receipts",
						Toast.LENGTH_SHORT).show();

		} else {
			ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> value;

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
			if (contentValues.length > 0) {
				for (int i = 0; i < contentValues.length; i++) {
					pickup_amount = (String) contentValues[i]
							.get("pickup_amount");
					req_amount = (String) contentValues[i].get("req_amount");
					point_name = (String) contentValues[i].get("point_name");
					cust_name = (String) contentValues[i].get("cust_name");
					type = (String) contentValues[i].get("type");
					trans_id = (String) contentValues[i].get("trans_id");

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

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
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
		printDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));

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
		final PrinterSelection connectingPrinter = new PrinterSelection();
		print_btn.setOnClickListener(new OnClickListener() {
			@SuppressWarnings({ "unchecked", "static-access" })
			public void onClick(View v) {
				if (Utils.isInternetAvailable(CancelReceipt_Print.this)) {
					if (gpsTracker.canGetLocation()) {
						latitude = gpsTracker.getLatitude();
						longitude = gpsTracker.getLongitude();
						lat = (int) (latitude * 1E6);
						lon = (int) (longitude * 1E6);
						//Log.i("Lat & Lon :", lat + "," + lat);
						option = 1;
						printDialog.cancel();

						if (connectingPrinter.isPrinterConnected) {
							if (connectingPrinter.printReceipt(printDetails
									.getBytes())) {
								Toast.makeText(getApplicationContext(),
										"Receipt Printed", Toast.LENGTH_SHORT)
										.show();
								progressDialog
										.setTitle("Canceling the receipt");
								progressDialog.show();
								trans_id = trans_ids[seletedPosition];
								params = new ArrayList<BasicNameValuePair>();
								params.add(new BasicNameValuePair("opt",
										"cancel_rec"));
								params.add(new BasicNameValuePair("trans_id",
										trans_ids[seletedPosition]));
								params.add(new BasicNameValuePair("ce_id",
										ce_id));
								params.add(new BasicNameValuePair("lat", ""
										+ lat));
								params.add(new BasicNameValuePair("lon", ""
										+ lon));
								params.add(new BasicNameValuePair("IMIE", imei));
								params.add(new BasicNameValuePair("final", "1"));
								GetJson getJson = new GetJson(
										CancelReceipt_Print.this,CancelReceipt_Print.this);
								getJson.execute(params);
							} else
								Toast.makeText(
										getApplicationContext(),
										"Failed to print. Please reconnect the printer.",
										Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(getApplicationContext(),
									"Please connect printer",
									Toast.LENGTH_SHORT).show();
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
				//Log.d(TAG, "Result Json: " + object.toString());
				try {
					String msg = object.getString("msg");
					//Log.d(TAG, "Msg:" + msg);
					if (msg.equals("sucess")) {
						ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> value;

						JSONArray ja = object.getJSONArray("transactions");
						//Log.d(TAG, "Json Array: " + ja.toString());
                        dbHandler.delete("delete from  receipt");

						for (int i = 0; i < ja.length(); i++) {
							JSONObject inner_jo = ja.getJSONObject(i);
							pickup_amount = inner_jo.getString("pickup_amount");
							req_amount = inner_jo.getString("req_amount");
							point_name = inner_jo.getString("point_name");
							cust_name = inner_jo.getString("cust_name");
							type = inner_jo.getString("type");
							trans_id = inner_jo.getString("trans_id");

							//Log.d(TAG, "Result Inner Object: " + pickup_amount
								//	+ "," + point_name + "," + cust_name + ","
								//	+ type + "," + trans_id);

							contentValues = new ContentValues();
							contentValues.put("trans_id", trans_id);
							contentValues.put("point_name", point_name);
							contentValues.put("cust_name", cust_name);
							contentValues.put("type", type);
							contentValues.put("req_amount", req_amount);
							contentValues.put("pickup_amount", pickup_amount);
							contentValues.put("ce_id", ce_id);

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
						trans_listview.setAdapter(new SimpleAdapter(this, list,
								R.layout.translist_item, new String[] {
										"pickup_amount", "point_name",
										"cust_name" }, new int[] {
										R.id.amount_txt, R.id.pointname_txt,
										R.id.customername_txt }));
					} else
						Toast.makeText(
								getApplicationContext(),
								"Failed to fetch information. Please Try Again!",
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"Communication Failure. Please Try Again!",
						Toast.LENGTH_SHORT).show();
			}

		} else if (option == 1) {
			option = 0;
			if (object != null) {
				//Log.d(TAG, "Result Json: " + object.toString());
				try {
					String status = object.getString("status");
					if (status.equals("success")) {
						Toast.makeText(getApplicationContext(),
								"The receipt was canceled", Toast.LENGTH_SHORT)
								.show();
						dbHandler
								.execute("update receipt set show='no' where trans_id='"
										+ trans_id + "'");
						//Log.i(TAG, "Canceled Receipt Trans_id: " + trans_id);
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
				Toast.makeText(getApplicationContext(),
						"Communication Failure. Please Try Again!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(CancelReceipt_Print.this,Home.class).putExtra("ce_id", ce_id));
        super.onBackPressed();
    }
}
