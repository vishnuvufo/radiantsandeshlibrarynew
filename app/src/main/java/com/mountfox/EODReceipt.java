package com.mountfox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EODReceipt extends Activity implements GetJson.CallbackInterface {

	String ce_id = "", imei = "908372827282", trans_id = "", point_name = "", cust_name = "", type = "", req_amount = "", pickup_amount = "", tid = "0", rid = "";
	int lat = 1, lon = 2;
	double latitude = 12.982733625, longitude = 80.252031675;
	GPSTracker gpsTracker;
	List<BasicNameValuePair> params;
	private static final String TAG = "EODReceipt";
	ProgressDialog progressDialog;
	static ImageView connectPrinter;
	ImageView printerType;
	ScrollView printContent;
	TextView print_details_txt;
	Button print_btn;
	String total_trans = "", total_request = "", total_pickup = "",
			total_deposit = "", dep_bd = "", dep_pb = "", dep_cb = "",
			cih = "", ce_name = "", burial_amt = "", pb_amt = "", cb_amt = "",
			no_trans = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
       this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
		setContentView(R.layout.eod_receipt);
	}

	protected void onResume() {
		super.onResume();
		initializeComponents();
	}

	@SuppressWarnings("unchecked")
	public void initializeComponents() {

		printerType = (ImageView) findViewById(R.id.printerType);
		connectPrinter = (ImageView) findViewById(R.id.connectPrinter);
		printContent = (ScrollView) findViewById(R.id.printContent);
		print_details_txt = (TextView) findViewById(R.id.print_details_txt);
		print_btn = (Button) findViewById(R.id.print_btn);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("Fetching Information");
		progressDialog.setMessage("Please Wait...");
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progressbar));
		gpsTracker = new GPSTracker(this);
		ce_id = getIntent().getStringExtra("ce_id");
		if (Config.DEBUG) {
			//Log.i(TAG, "Ce_id: " + ce_id);
		}

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		try {
			imei = telephonyManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Config.DEBUG) {
			//Log.i("Imei:", imei);
		}

		if (PrinterSelection.PRINTER_TYPE == PrinterSelection.PRINTER_2MM) {
			printerType.setImageResource(R.drawable.btn_printer_2mm);
		} else if (PrinterSelection.PRINTER_TYPE == PrinterSelection.PRINTER_3MM) {
			printerType.setImageResource(R.drawable.btn_printer_3mm);
		}

		printerType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (PrinterSelection.PRINTER_TYPE == PrinterSelection.PRINTER_2MM) {
					printerType.setImageResource(R.drawable.btn_printer_3mm);
					PrinterSelection.PRINTER_TYPE = PrinterSelection.PRINTER_3MM;
				} else if (PrinterSelection.PRINTER_TYPE == PrinterSelection.PRINTER_3MM) {
					printerType.setImageResource(R.drawable.btn_printer_2mm);
					PrinterSelection.PRINTER_TYPE = PrinterSelection.PRINTER_2MM;
				}
			}
		});

		if (PrinterSelection.isPrinterConnected)
			connectPrinter.setImageResource(R.drawable.printer_on);
		else
			connectPrinter.setImageResource(R.drawable.printer_off);

		connectPrinter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(EODReceipt.this, PrinterSelection.class));
			}
		});

		if (gpsTracker.canGetLocation()) {
			latitude = gpsTracker.getLatitude();
			longitude = gpsTracker.getLongitude();
			lat = (int) (latitude * 1E6);
			lon = (int) (longitude * 1E6);
			if (Config.DEBUG) {
			}
			progressDialog.show();
			params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("opt", "eod_print"));
			params.add(new BasicNameValuePair("ce_id", ce_id));
			params.add(new BasicNameValuePair("lat", "" + lat));
			params.add(new BasicNameValuePair("lon", "" + lon));
			params.add(new BasicNameValuePair("IMIE", imei));
			params.add(new BasicNameValuePair("final", "1"));
            // utils connection added by anbarasu to check internet connection
            if(Utils.isInternetAvailable(EODReceipt.this))
            {
                GetJson getJson = new GetJson(EODReceipt.this,this);
			    getJson.execute(params);
            }
            else {
                Toast.makeText(getApplicationContext(), "Please enable the Internet Connection for Authentication", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(EODReceipt.this,Home.class).putExtra("ce_id", ce_id));
            }
		} else{
			Toast.makeText(getApplicationContext(), "Please enable the Location Service(GPS/WIFI) for print the EOD Receipt", Toast.LENGTH_SHORT).show();
		}


	}

	public void onRequestCompleted(JSONObject object) {
		progressDialog.dismiss();
		if (object != null) {
			if (Config.DEBUG) {
				//Log.d(TAG, "Result Json: " + object.toString());
			}

			try {
				ce_id = object.getString("ce_id");
				total_pickup = object.getString("total_pickup");
				cih = object.getString("cih");
				ce_name = object.getString("ce_name");
				total_deposit = object.getString("total_deposit");
				total_trans = object.getString("total_trans");
				total_request = object.getString("total_request");

				burial_amt = object.getString("burial_amt");
				pb_amt = object.getString("pb_amt");
				cb_amt = object.getString("cb_amt");
				no_trans = object.getString("no_trans");

				tid = PrinterSelection.address;

				if (Config.DEBUG) {
					//Log.d(TAG, "Ce_id: " + ce_id + ", Total_Pickup: "
					//		+ total_pickup + ", CIH: " + cih + ", CE_NAME: "
					//		+ ce_name + ", Total_Deposit: " + total_deposit
					//		+ ", Total Transaction: " + total_trans
						//	+ ", Total Request: " + total_request);
				}

				String line = "\n______________________________________";
				String datef = "", timef = "", imeif = "IMIE ID: " + imei, tidf = "TID: "
						+ tid, ce_idf = "CE ID: " + ce_id;

				Calendar calendar = Calendar.getInstance();

				if (calendar.get(Calendar.MONTH) < 9) {
					if (calendar.get(Calendar.DATE) < 10)
						datef = "Date: " + "0" + calendar.get(Calendar.DATE)
								+ "-" + "0"
								+ (calendar.get(Calendar.MONTH) + 1) + "-"
								+ calendar.get(Calendar.YEAR);
					else
						datef = "Date: " + calendar.get(Calendar.DATE) + "-"
								+ "0" + (calendar.get(Calendar.MONTH) + 1)
								+ "-" + calendar.get(Calendar.YEAR);
				} else {
					if (calendar.get(Calendar.DATE) < 10)
						datef = "Date: " + "0" + calendar.get(Calendar.DATE)
								+ "-" + (calendar.get(Calendar.MONTH) + 1)
								+ "-" + calendar.get(Calendar.YEAR);
					else
						datef = "Date: " + calendar.get(Calendar.DATE) + "-"
								+ (calendar.get(Calendar.MONTH) + 1) + "-"
								+ calendar.get(Calendar.YEAR);
				}

				timef = "Time: " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
						+ calendar.get(Calendar.MINUTE) + ":"
						+ calendar.get(Calendar.SECOND);

				final StringBuilder format = new StringBuilder();
				int total_line = 38;

				// Printing Start
				int sp = total_line - datef.length() - timef.length();
				format.append("\n" + datef + getSpace(sp) + timef);

				format.append("\n" + imeif);
				format.append("\n" + tidf);
				format.append("\n" + ce_idf);
				format.append(line);

				format.append("\nTotal Transactions: " + total_trans);
				format.append("\nTotal Request Amount: " + total_request);
				format.append("\nTotal Pickup Amount: " + total_pickup);
				format.append("\nTotal Deposit Amount: " + total_deposit);
				format.append("\nDeposit - Burial Amount: " + burial_amt);
				format.append("\nDeposit - PB Amount: " + pb_amt);
				format.append("\nDeposit - CB Amount: " + cb_amt);
				format.append("\nCIH: " + cih);
				format.append(line);

				sp = (int) ((total_line - "TRANSACTION DETAILS".length()) / 2);
				format.append("\n" + getSpace(sp) + "TRANSACTION DETAILS");
				format.append(line);

				JSONArray ja = object.getJSONArray("transactions");
				if (Config.DEBUG) {
					//Log.d(TAG, "Json Array: " + ja.toString());
				}
				for (int i = 0; i < ja.length(); i++) {
					JSONObject inner_jo = ja.getJSONObject(i);
					point_name = inner_jo.getString("point_name");
					cust_name = inner_jo.getString("cust_name");
					type = inner_jo.getString("type");
					trans_id = inner_jo.getString("trans_id");
					pickup_amount = inner_jo.getString("pickup_amount");
					req_amount = inner_jo.getString("req_amount");
					rid = inner_jo.getString("rid");

					if (Config.DEBUG) {
						//Log.d(TAG, "Result Inner Object:  Point Name: "
							//	+ point_name + ", Cust_Name: " + cust_name
							//	+ ", Type: " + type + ", Trans_id: " + trans_id
							//	+ ", Pickup_Amount: " + pickup_amount
								//+ ", Request_Amount: " + req_amount);
					}

					// Printer Transaction Start
					format.append("\nTrans. ID: " + trans_id);
					format.append("\nRID: " + rid);
					format.append("\nCustomer Name: " + cust_name);
					format.append("\nPoint Name: " + point_name);
					format.append("\nType: " + type);
					format.append("\nAmount: " + pickup_amount);
					format.append(line);
					// Printer Transaction End
				}

				// Received Transaction
				sp = (int) ((total_line - ("Received Trans.: " + no_trans)
						.length()) / 2);
				format.append("\n" + getSpace(sp) + "Received Trans.: "
						+ no_trans);
				format.append(line);

				// Total Transaction
				sp = (int) ((total_line - ("Total Transactions: " + total_trans)
						.length()) / 2);
				format.append("\n" + getSpace(sp) + "Total Transactions: "
						+ total_trans);
				format.append(line);
				// Printing End

				print_details_txt.setText(format);
				printContent.setVisibility(View.VISIBLE);

				final PrinterSelection connectingPrinter = new PrinterSelection();
				print_btn.setOnClickListener(new OnClickListener() {
					@SuppressWarnings("static-access")
					public void onClick(View v) {
						if (connectingPrinter.isPrinterConnected) {
							if (connectingPrinter.printEod(format.toString()
									.getBytes())) {
								Toast.makeText(getApplicationContext(),
										"EOD Report Printed",
										Toast.LENGTH_SHORT).show();
								finish();
							} else
								Toast.makeText(
										getApplicationContext(),
										"Failed to print. Please reconnect the printer.",
										Toast.LENGTH_SHORT).show();
						} else
							Toast.makeText(getApplicationContext(),
									"Please connect printer",
									Toast.LENGTH_SHORT).show();
					}
				});

			} catch (Exception e) {
				finish();
				e.printStackTrace();
			}
		} else {
			if (Config.DEBUG) {
				//Log.e(TAG, "Communication Failure");
			}
			Toast.makeText(getApplicationContext(),
					"Communication Failure. Please Try Again!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public String getSpace(int n) {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		for (int i = 0; i < n; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public String getLine(int n) {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		for (int i = 0; i < n; i++) {
			sb.append("_");
		}
		return sb.toString();
	}
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(EODReceipt.this,Home.class).putExtra("ce_id", ce_id));
        super.onBackPressed();
    }
}
