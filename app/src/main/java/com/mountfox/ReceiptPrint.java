package com.mountfox;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.text.TextUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptPrint extends Activity implements OnItemClickListener,
		GetJson.CallbackInterface {

	private String ce_id = "", imei = "908372827282", trans_id = "",
			point_name = "", cust_name = "", type = "", req_amount = "",
			pickup_amount = "", rec_datetime = "", rec_status = "",
			dep_amount = "", sealtag_no = "",ccode="", remarks = "", pis_no = "",
			deposit_slip = "", hci_no = "", tid = "0", rid = "", transParam="",captions_txt="",
			no_recs, deno, pickup_amountf, deposit_slipf, pis_nof, hci_nof,
			sealtag_nof,ccodef;
	int lat = 1, lon = 2;
	double latitude = 12.982733625, longitude = 80.252031675;

	String pickup_amounts[], req_amounts[], point_names[], cust_names[],
			types[], trans_ids[];
	GPSTracker gpsTracker;
	List<BasicNameValuePair> params;
	private static final String TAG = "ReceiptPrint";
	ProgressDialog progressDialog;
	ListView trans_listview;
	static ImageView connectPrinter;
	ImageView printerType;
	String[] spiltTransParam, strMultipleTrans;
	ContentValues contentValues;
	DbHandler dbHandler;
	static int option = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
		setContentView(R.layout.receipt_print);
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
		printerType = (ImageView) findViewById(R.id.printerType);
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
		if (Config.DEBUG) {
			//Log.i(TAG, "Ce_id: " + ce_id);
		}
		dbHandler = new DbHandler(ReceiptPrint.this);

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
				startActivity(new Intent(ReceiptPrint.this,
						PrinterSelection.class));
			}
		});
		loadData();
	}

	@SuppressWarnings("unchecked")
	public void loadData() {
		if (Utils.isInternetAvailable(getApplicationContext())) {
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			try {
				imei = telephonyManager.getDeviceId();
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
				if (Config.DEBUG) {
					//Log.i("Lat & Lon :", lat + "," + lat);
				}

				progressDialog.show();
				params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("opt", "view_rec"));
				params.add(new BasicNameValuePair("ce_id", ce_id));
				params.add(new BasicNameValuePair("lat", "" + lat));
				params.add(new BasicNameValuePair("lon", "" + lon));
				params.add(new BasicNameValuePair("IMIE", imei));
				params.add(new BasicNameValuePair("final", "1"));
				GetJson getJson = new GetJson(ReceiptPrint.this,this);
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

	@SuppressWarnings("unchecked")
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if (Utils.isInternetAvailable(getApplicationContext())) {
			if (gpsTracker.canGetLocation()) {
				latitude = gpsTracker.getLatitude();
				longitude = gpsTracker.getLongitude();
				lat = (int) (latitude * 1E6);
				lon = (int) (longitude * 1E6);
				if (Config.DEBUG) {
					//Log.i("Lat & Lon :", lat + "," + lat);
				}
				option = 1;
				progressDialog.show();
				trans_id = trans_ids[arg2];
				cust_name = cust_names[arg2];
				type = types[arg2];

				params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("opt", "view_bill"));
				params.add(new BasicNameValuePair("ce_id", ce_id));
				params.add(new BasicNameValuePair("trans_id", trans_ids[arg2]));
				params.add(new BasicNameValuePair("lat", "" + lat));
				params.add(new BasicNameValuePair("lon", "" + lon));
				params.add(new BasicNameValuePair("IMIE", imei));
				params.add(new BasicNameValuePair("final", "1"));
				GetJson getJson = new GetJson(ReceiptPrint.this,this);
				getJson.execute(params);
			} else
				Toast.makeText(
						getApplicationContext(),
						"Please enable the Location Service(GPS/WIFI) for print the receipt",
						Toast.LENGTH_SHORT).show();

		} else
			Toast.makeText(
					getApplicationContext(),
					"Please enable the Internet Connection for print the receipt",
					Toast.LENGTH_SHORT).show();
	}

	@SuppressWarnings("unused")
	public void onRequestCompleted(JSONObject object) {
		progressDialog.dismiss();
		if (option == 0) {
			if (object != null) {
				try {
					String msg = object.getString("msg");
					if (msg.equals("success")) {
						ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
						HashMap<String, String> value;
                        dbHandler.delete("delete from  receipt");
						JSONArray ja = object.getJSONArray("transactions");
						for (int i = 0; i < ja.length(); i++) {
							JSONObject inner_jo = ja.getJSONObject(i);
							pickup_amount = inner_jo.getString("pickup_amount");
							req_amount = inner_jo.getString("req_amount");
							point_name = inner_jo.getString("point_name");
							cust_name = inner_jo.getString("cust_name");
							type = inner_jo.getString("type");
							trans_id = inner_jo.getString("trans_id");

							contentValues = new ContentValues();
							contentValues.put("trans_id", trans_id);
							contentValues.put("point_name", point_name);
							contentValues.put("cust_name", cust_name);
							contentValues.put("type", type);
							contentValues.put("req_amount", req_amount);
							contentValues.put("pickup_amount", pickup_amount);
							contentValues.put("ce_id", ce_id);
							// Update/Insert Data
                            //code commented by anbarasu
							/*if (dbHandler.isExistRow("receipt", trans_id))
                            {
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
								// dbHandler.update("receipt", contentValues);
							} else

*/
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

							trans_listview.setAdapter(new SimpleAdapter(this,
									list, R.layout.translist_item,
									new String[] { "pickup_amount",
											"point_name", "cust_name" },
									new int[] { R.id.amount_txt,
											R.id.pointname_txt,
											R.id.customername_txt }));

						}
					} else
						Toast.makeText(getApplicationContext(),
								"No Record Found", Toast.LENGTH_SHORT).show();
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
				try {
					rec_datetime = object.getString("rec_datetime");
					ce_id = object.getString("ce_id");
					rec_status = object.getString("rec_status");
					trans_id = object.getString("trans_id");
					rid = object.getString("rid");
					dep_amount = object.getString("dep_amount");
					no_recs = object.getString("no_recs");
					point_name = object.getString("point_name");
					transParam = object.getString("trans_param");
                    captions_txt    =   object.getString("captions");
					remarks = object.getString("remarks");
					deno = object.getString("deno");
					req_amount = object.getString("req_amount");

					tid = PrinterSelection.address;
					int noOFTrans = Integer.parseInt(no_recs);
					if (noOFTrans != 0) {
						if (noOFTrans == 1) {
							spiltTransParam = transParam.split("\\|");

							pickup_amount = spiltTransParam[0];
							deposit_slip = spiltTransParam[1];
							pis_no = spiltTransParam[2];
							hci_no = spiltTransParam[3];
							sealtag_no = spiltTransParam[4];
                            ccode   =   spiltTransParam[5];
							pickup_amountf = captions_txt.split(",")[0]+": "
									+ pickup_amount;
                            if(!captions_txt.split(",")[1].equalsIgnoreCase("0"))
							    deposit_slipf = captions_txt.split(",")[1]+": " + deposit_slip;
                            else
                                deposit_slipf   =   "";
                            if(!captions_txt.split(",")[2].equalsIgnoreCase("0"))
							    pis_nof = captions_txt.split(",")[2]+": " + pis_no;
                            else
                                pis_nof =   "";
                            if(!captions_txt.split(",")[3].equalsIgnoreCase("0"))
							    hci_nof = captions_txt.split(",")[3]+": " + hci_no;
                            else
                                hci_nof="";
                            if(!captions_txt.split(",")[4].equalsIgnoreCase("0"))
                                sealtag_nof = captions_txt.split(",")[4]+": " + sealtag_no;
                            else
                                sealtag_nof ="";

                            ccodef   =   "Client Code: "+ccode;
						} else {
							strMultipleTrans = transParam.split("\\^");
						}

						String datef = "Date: ", timef = "Time: ", imeif = "IMIE ID: "
								+ imei, tidf = "TID: " + tid, trans_idf = "Trans. ID: "
								+ trans_id, ridf = "RID: " + rid, ce_idf = "CE ID: "
								+ ce_id, cust_namef = "Customer Name: "
								+ cust_name, point_namef = "Point Name: "
								+ point_name, typef = "Trans. Type: " + type, req_amountf = "Req. Amount: "
								+ req_amount, rec_statusf = "Receipt Status: "
								+ rec_status, remarksf = "Remarks: " + remarks;

						Calendar calendar = Calendar.getInstance();

						if (calendar.get(Calendar.MONTH) < 9) {
							if (calendar.get(Calendar.DATE) < 10)
								datef = "Date: " + "0"
										+ calendar.get(Calendar.DATE) + "-"
										+ "0"
										+ (calendar.get(Calendar.MONTH) + 1)
										+ "-" + calendar.get(Calendar.YEAR);
							else
								datef = "Date: " + calendar.get(Calendar.DATE)
										+ "-" + "0"
										+ (calendar.get(Calendar.MONTH) + 1)
										+ "-" + calendar.get(Calendar.YEAR);
						} else {
							if (calendar.get(Calendar.DATE) < 10)
								datef = "Date: " + "0"
										+ calendar.get(Calendar.DATE) + "-"
										+ (calendar.get(Calendar.MONTH) + 1)
										+ "-" + calendar.get(Calendar.YEAR);
							else
								datef = "Date: " + calendar.get(Calendar.DATE)
										+ "-"
										+ (calendar.get(Calendar.MONTH) + 1)
										+ "-" + calendar.get(Calendar.YEAR);
						}

						timef = "Time: " + calendar.get(Calendar.HOUR_OF_DAY)
								+ ":" + calendar.get(Calendar.MINUTE) + ":"
								+ calendar.get(Calendar.SECOND);

						final String printDetails = "\nReceived Date: "
								+ rec_datetime + "\nCe_ID: " + ce_id
								+ "\nReceived Status: " + rec_status
								+ "\nTransaction Id: " + trans_id
								+ "\nDeposit Amount: " + dep_amount
								+ "\nPickup Amount: " + pickup_amount
								+ "\nPoint Name: " + point_name
								+ "\nSealtag No: " + sealtag_no
                                + "\nClient Code: "+ccode
                                + "\nRemarks: "
								+ remarks + "\nPis No: " + pis_no
								+ "\nDeposit Slip: " + deposit_slip
								+ "\nHci No: " + hci_no + "\nReq. Amount: "
								+ req_amount;

						String line_under = "\n______________________________________";
						String line_tilde = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

						final StringBuilder format = new StringBuilder();

						int total_line = 38;
						int sp = total_line - datef.length() - timef.length();
						format.append("\n" + datef + getSpace(sp) + timef);

						// sp = total_line - imeif.length() - tidf.length();
						// format.append("\n" + imeif + getSpace(sp) + tidf);

						format.append("\n" + imeif);
						format.append("\n" + tidf);

						sp = total_line - trans_idf.length() - ridf.length();
						format.append("\n" + trans_idf + getSpace(sp) + ridf);

						format.append("\n" + ce_idf);
						format.append(line_tilde);

						format.append("\n" + cust_namef);
						format.append("\n" + point_namef);
						format.append("\n" + typef);
						format.append("\n" + req_amountf);

						if (noOFTrans == 1) {
                            format.append("\n" + pickup_amountf);
                            if(!captions_txt.split(",")[1].equalsIgnoreCase("0"))
                                format.append("\n" + deposit_slipf);
                            if(!captions_txt.split(",")[2].equalsIgnoreCase("0"))
							    format.append("\n" + pis_nof);
                            if(!captions_txt.split(",")[3].equalsIgnoreCase("0"))
							    format.append("\n" + hci_nof);
                            if(!captions_txt.split(",")[4].equalsIgnoreCase("0"))
							    format.append("\n" + sealtag_nof);
                            format.append("\n" + ccodef);
						} else {
							for (int i = 0; i < strMultipleTrans.length; i++) {
								spiltTransParam = strMultipleTrans[i]
										.split("\\|");
								pickup_amount = spiltTransParam[0];
								deposit_slip = spiltTransParam[1];
								pis_no = spiltTransParam[2];
								hci_no = spiltTransParam[3];
								sealtag_no = spiltTransParam[4];
                                ccode   =   spiltTransParam[5];
								pickup_amountf = captions_txt.split(",")[0]+": "
										+ pickup_amount;

                                if(!captions_txt.split(",")[1].equalsIgnoreCase("0"))
								deposit_slipf = captions_txt.split(",")[1]+": "
										+ deposit_slip;
                                else
                                    deposit_slipf="";
                                if(!captions_txt.split(",")[2].equalsIgnoreCase("0"))
								pis_nof = captions_txt.split(",")[2]+": " + pis_no;
                                else
                                    pis_nof =   "";
                                if(!captions_txt.split(",")[3].equalsIgnoreCase("0"))
								hci_nof = captions_txt.split(",")[3]+": " + hci_no;
                                else
                                    hci_nof="";
                                if(!captions_txt.split(",")[4].equalsIgnoreCase("0"))
								sealtag_nof = captions_txt.split(",")[4]+": " + sealtag_no;
                                else
                                sealtag_nof =   "";
                                ccodef  =   "Client Code: "+ccode;
								format.append("\n" + pickup_amountf);
                                if(!captions_txt.split(",")[1].equalsIgnoreCase("0"))
								    format.append("\n" + deposit_slipf);
                                if(!captions_txt.split(",")[2].equalsIgnoreCase("0"))
								    format.append("\n" + pis_nof);
                                if(!captions_txt.split(",")[3].equalsIgnoreCase("0"))
								    format.append("\n" + hci_nof);
                                if(!captions_txt.split(",")[4].equalsIgnoreCase("0"))
								    format.append("\n" + sealtag_nof);
                                format.append("\n" + ccodef);
								format.append("\n");
							}
						}

						format.append("\n" + rec_statusf);
						format.append("\n" + remarksf);
						format.append(line_tilde);

						String signf = "Sign: ";
						sp = total_line - signf.length();
						format.append("\n\n" + signf + getLine(sp));

						final Dialog printDialog = new Dialog(this);
						printDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						printDialog.setContentView(R.layout.transaction_print);
						printDialog.getWindow().setBackgroundDrawable(
								new ColorDrawable(Color.TRANSPARENT));

						ImageView print_close_img = (ImageView) printDialog
								.findViewById(R.id.print_close_img);
						TextView print_details_txt = (TextView) printDialog
								.findViewById(R.id.print_details_txt);
						Button print_btn = (Button) printDialog
								.findViewById(R.id.print_btn);

						@SuppressWarnings("deprecation")
						int width = getWindowManager().getDefaultDisplay()
								.getWidth() - 50;
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								width, LayoutParams.WRAP_CONTENT);
						print_details_txt.setLayoutParams(lp);
						print_details_txt.setText(format);
						if (dbHandler.isAlreadyPrinted(trans_id))
							print_btn.setText("RePrint");

						final PrinterSelection connectingPrinter = new PrinterSelection();
						print_btn.setOnClickListener(new OnClickListener() {
							@SuppressWarnings("static-access")
							public void onClick(View v) {
								if (connectingPrinter.isPrinterConnected) {
									if (connectingPrinter.printReceipt(format
											.toString().getBytes())) {
										Toast.makeText(getApplicationContext(),
												"Receipt Printed",
												Toast.LENGTH_SHORT).show();
										dbHandler
												.execute("update receipt set printed='yes' where trans_id='"
														+ trans_id + "'");
									} else
										Toast.makeText(
												getApplicationContext(),
												"Failed to print. Please reconnect the printer.",
												Toast.LENGTH_SHORT).show();
								} else
									Toast.makeText(getApplicationContext(),
											"Please connect printer",
											Toast.LENGTH_SHORT).show();
								printDialog.cancel();
							}
						});

						print_close_img
								.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										printDialog.cancel();
									}
								});
						printDialog.setCancelable(false);
						printDialog.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Failure transaction occured!",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
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
        startActivity(new Intent(ReceiptPrint.this,Home.class).putExtra("ce_id", ce_id));
        super.onBackPressed();
    }
}
