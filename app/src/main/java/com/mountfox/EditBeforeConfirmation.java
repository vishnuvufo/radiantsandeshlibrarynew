package com.mountfox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EditBeforeConfirmation extends Activity {

    public EditText req_amount_txt, pickup_amount_txt, dep_no_txt, pis_no_txt, hci_no_txt, seal_no_txt;
    public Spinner ccode_spin;
    public Button submit_btn;
    private Dialog dialog;
    private ListView listSpinner;

    private PopupWindow spinnerWindow;

    public int txtWidth;
    public String  Strtrans_param="";

    private DbHandler dbHandler;

    private TextView receipt_status_spinner;


//    private String ce_id = "", trans_id = "", type = "", pickup_amount = "", pickup_amount1 = "", amt = "",
//            deposit_slip = "", diff_slip = "", pis_no = "", hci_no = "", sealtag_no = "",
//            dep_amount = "", rec_status = "", remarks = "", device_id = "",
//            pin_status = "", Strtrans_param = "", client_code = "", Strdeno = "", strdenoParam = "", bank_dep_slip_str = "";

  public  String client_code="",strdenoParam="",strQrParam="";

    private int strNoTrans, strGetNoTrans = 1, lat = 1, lon = 2;
    private GPSTracker gpsTracker;
    private TelephonyManager telephonyManager;
    private List<BasicNameValuePair> params;
    private GetJson getJson;
    static int transaction_pin = 0, activity_pass = 0, viewPosition;
    double latitude = 12.982733625, longitude = 80.252031675;
    public EditText txt;
    public TextView tv_info, tv_error;
    public String img_path = "";
    private ProgressDialog progressDialog;

    private ArrayAdapter<String> cspin_adap, bankSpin_adap, vaultSpin_adap, branch_adap, cc_adap;
    private ArrayAdapter<String> spinnerAdapter;


    private ArrayList<String> cspin_arls = new ArrayList<String>();

    private LinearLayout lin_deno, lin_req;

    public ListView dialoglist;

    public String captions = "";

    private LinearLayout layout1, layout2, layout3, new_deposite_block, lin_bank, lin_vault, lin_dep, lin_pis, lin_hci, lin_receipt, lin_dep_slip, deposite_line;


    private String[] caps_arr = null;

    private String rec_status_option[] = {"Cash Received", "No Cash",
            "CE visited in No cash", "Shop Closed", "Difference in Cash",
            "Customer refused to sign", "Cash Delivered",
            "Partially Cash Delivered", "No Cash Delivered",
            "Customer refused to Accept", "Cash Deposited", "Cash Vaulted", "Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_edit_before_confirmation);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in Progress");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        device_id = telephonyManager.getDeviceId();
        dbHandler = new DbHandler(this);
        req_amount_txt = (EditText) findViewById(R.id.req_amount_txt);
        pickup_amount_txt = (EditText) findViewById(R.id.pickup_amount_txt);
        dep_no_txt = (EditText) findViewById(R.id.dep_no_txt);
        pis_no_txt = (EditText) findViewById(R.id.pis_no_txt);
        hci_no_txt = (EditText) findViewById(R.id.hci_no_txt);
        seal_no_txt = (EditText) findViewById(R.id.seal_no_txt);
        ccode_spin = (Spinner) findViewById(R.id.ccode_spin);
        submit_btn = (Button) findViewById(R.id.submit_btn);

        receipt_status_spinner = (TextView) findViewById(R.id.receipt_status_spinner);


        lin_dep_slip = (LinearLayout) findViewById(R.id.dep_no_lin);
        lin_pis = (LinearLayout) findViewById(R.id.pis_no_lin);
        lin_hci = (LinearLayout) findViewById(R.id.hci_no_lin);
        lin_receipt = (LinearLayout) findViewById(R.id.seal_no_lin);
        lin_req = (LinearLayout) findViewById(R.id.req_lin);

        captions = TransactionSingleItemDataCenter.captions;
        Log.v("ReceivePayment", "captions" + captions);
        caps_arr = new String[captions.split(",").length + 2];
        caps_arr = captions.split(",");

        Log.v("ReceivePayment11", TransactionSingleItemDataCenter.client_code);
        String[] ccode = TransactionSingleItemDataCenter.client_code.split(",");
        for (int i = 0; i < ccode.length; i++) {
            cspin_arls.add(ccode[i]);
        }



        ViewTreeObserver vto = receipt_status_spinner.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                receipt_status_spinner.getViewTreeObserver()
                        .removeOnPreDrawListener(this);
                txtWidth = receipt_status_spinner.getMeasuredWidth();
                return false;
            }
        });

        receipt_status_spinner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewPosition = 1;
                showPopup(v);
            }
        });



        cspin_adap = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, cspin_arls) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditBeforeConfirmation.this, android.R.layout.simple_list_item_1, null);
                TextView ccode_txt = (TextView) convertView.findViewById(android.R.id.text1);
                ccode_txt.setTextColor(Color.BLACK);
                ccode_txt.setText(cspin_arls.get(position));
                return convertView;
            }
        };
        ccode_spin.setAdapter(cspin_adap);

        for (int i = 0; i < caps_arr.length; i++)
            //Log.v("ReceivePayment","captions"+caps_arr[i]);
            if (!caps_arr[1].equalsIgnoreCase("0"))
                dep_no_txt.setHint(caps_arr[1]);
            else
                lin_dep_slip.setVisibility(View.GONE);
        if (!caps_arr[2].equalsIgnoreCase("0"))
            pis_no_txt.setHint(caps_arr[2]);
        else
            lin_pis.setVisibility(View.GONE);
        if (!caps_arr[3].equalsIgnoreCase("0"))
            hci_no_txt.setHint(caps_arr[3]);
        else
            lin_hci.setVisibility(View.GONE);
        if (!caps_arr[4].equalsIgnoreCase("0"))
            seal_no_txt.setHint(caps_arr[4]);
        else
            lin_receipt.setVisibility(View.GONE);

        Log.d("Pickamnt", "currentArray::" + DataBeforeConformation.pickupAmountArrayList);

        if (!DataBeforeConformation.pickupAmountArrayList.get(EditItemSelectedPosition.getSelecteditem()).equals("0"))
            pickup_amount_txt.setText(DataBeforeConformation.pickupAmountArrayList.get(EditItemSelectedPosition.getSelecteditem()));
        Log.d("Pickamnt", EditItemSelectedPosition.getSelecteditem() + "th position value::" + DataBeforeConformation.pickupAmountArrayList.get(EditItemSelectedPosition.getSelecteditem()));
        if (!DataBeforeConformation.DepositeSlipNumberArrayList.get(EditItemSelectedPosition.getSelecteditem()).equals("0"))
            dep_no_txt.setText(DataBeforeConformation.DepositeSlipNumberArrayList.get(EditItemSelectedPosition.getSelecteditem()));
        Log.d("slipnumber", EditItemSelectedPosition.getSelecteditem() + "th position value::" + DataBeforeConformation.DepositeSlipNumberArrayList.get(EditItemSelectedPosition.getSelecteditem()));
        if (!DataBeforeConformation.PISnumberArrayList.get(EditItemSelectedPosition.getSelecteditem()).equals("0"))
            pis_no_txt.setText(DataBeforeConformation.PISnumberArrayList.get(EditItemSelectedPosition.getSelecteditem()));
        if (!DataBeforeConformation.HCInumberArrayList.get(EditItemSelectedPosition.getSelecteditem()).equals("0"))
            hci_no_txt.setText(DataBeforeConformation.HCInumberArrayList.get(EditItemSelectedPosition.getSelecteditem()));
        if (DataBeforeConformation.SealTagArrayList.get(EditItemSelectedPosition.getSelecteditem()) != null)
            seal_no_txt.setText(DataBeforeConformation.SealTagArrayList.get(EditItemSelectedPosition.getSelecteditem()));

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBeforeConformation.pickupAmountArrayList.set(EditItemSelectedPosition.getSelecteditem(), pickup_amount_txt.getText().toString());
                Log.d("Pickamnt", "afterEdited::" + DataBeforeConformation.pickupAmountArrayList);
                DataBeforeConformation.DepositeSlipNumberArrayList.set(EditItemSelectedPosition.getSelecteditem(), dep_no_txt.getText().toString());
                Log.d("deposit slip number", "afterEdited::" + DataBeforeConformation.DepositeSlipNumberArrayList);
                DataBeforeConformation.PISnumberArrayList.set(EditItemSelectedPosition.getSelecteditem(), pis_no_txt.getText().toString());
                Log.d("PIS number", "afterEdited::" + DataBeforeConformation.PISnumberArrayList);
                DataBeforeConformation.HCInumberArrayList.set(EditItemSelectedPosition.getSelecteditem(), hci_no_txt.getText().toString());
                Log.d("HCI number", "afterEdited::" + DataBeforeConformation.HCInumberArrayList);
                DataBeforeConformation.SealTagArrayList.set(EditItemSelectedPosition.getSelecteditem(), seal_no_txt.getText().toString());
                Log.d("Seal Tag", "afterEdited::" + DataBeforeConformation.SealTagArrayList);
                if(pickup_amount_txt.getText().toString().equals(""))
                {
                    Toast.makeText(EditBeforeConfirmation.this, "Pickup amount is empty", Toast.LENGTH_SHORT).show();
                }else {
                    showEnteredData();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        final Dialog exitDialog = new Dialog(this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.calcel_transaction_dialog);
        exitDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        Button yes_btn, no_btn;
        yes_btn = (Button) exitDialog.findViewById(R.id.yes_btn);
        no_btn = (Button) exitDialog.findViewById(R.id.no_btn);
        yes_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
                finish();
                Intent ii = new Intent(getApplicationContext(), Home.class);
                startActivity(ii);
            }
        });
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }


    public void showEnteredData() {

        final AlertDialog alertDialog = new AlertDialog.Builder(EditBeforeConfirmation.this).create();
        alertDialog.setTitle("");
        View view = View.inflate(EditBeforeConfirmation.this, R.layout.confirm_screen, null);
        dialoglist = (ListView) view.findViewById(R.id.dialoglist);
        TextView deposite_type = (TextView) view.findViewById(R.id.deposite_type);
        TextView bank_name = (TextView) view.findViewById(R.id.bank_name);
        TextView account_number = (TextView) view.findViewById(R.id.account_number);
        TextView deposite_branch = (TextView) view.findViewById(R.id.deposite_branch);
        TextView deposite_slip_number = (TextView) view.findViewById(R.id.deposite_slip_number);
        TextView deposite_amount = (TextView) view.findViewById(R.id.deposite_amount);
        TextView remarks = (TextView) view.findViewById(R.id.remarks);

        LinearLayout bank_name_lin_layout = (LinearLayout) view.findViewById(R.id.bank_name_lin_layout);
        LinearLayout deposit_type_lin_layout = (LinearLayout) view.findViewById(R.id.deposit_type_lin_layout);
        LinearLayout account_lin_layout = (LinearLayout) view.findViewById(R.id.account_lin_layout);
        LinearLayout deposit_branch_lin_layout = (LinearLayout) view.findViewById(R.id.deposit_branch_lin_layout);
        LinearLayout deposit_sleep_number_lin_layout = (LinearLayout) view.findViewById(R.id.deposit_sleep_number_lin_layout);
        LinearLayout deposite_amount_lin_layout = (LinearLayout) view.findViewById(R.id.deposite_amount_lin_layout);
        LinearLayout remarks_lin_layout = (LinearLayout) view.findViewById(R.id.remarks_lin_layout);

        ImageView edit_icon=(ImageView)view.findViewById(R.id.edit_icon);

        edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iii=new Intent(getApplicationContext(),Deposit_amount_edit_screen.class);
                startActivity(iii);
            }
        });

        if (!DepositAmountDataCenter.remarks_data.equals("")) {
            remarks.setText(DepositAmountDataCenter.remarks_data);
            remarks_lin_layout.setVisibility(View.VISIBLE);
        } else {
            remarks_lin_layout.setVisibility(View.GONE);
        }
        if (!DepositAmountDataCenter.dep_amount_data.equals("")) {
            deposite_amount.setText(DepositAmountDataCenter.dep_amount_data);
            deposite_amount_lin_layout.setVisibility(View.VISIBLE);
        } else {
            deposite_amount_lin_layout.setVisibility(View.GONE);
        }

        if (!DepositAmountDataCenter.bank_dep_slip_str.equals("")) {
            deposit_sleep_number_lin_layout.setVisibility(View.VISIBLE);
            deposite_slip_number.setText(DepositAmountDataCenter.bank_dep_slip_str);
        } else {
            deposit_sleep_number_lin_layout.setVisibility(View.GONE);
        }
        if (!DepositAmountDataCenter.branch_name_data.equals("")) {
            deposit_branch_lin_layout.setVisibility(View.VISIBLE);
            deposite_branch.setText(DepositAmountDataCenter.branch_name_data);
        } else {
            deposit_branch_lin_layout.setVisibility(View.GONE);
        }
        if (!DepositAmountDataCenter.account_autocomplete_data.equals("")) {
            account_lin_layout.setVisibility(View.VISIBLE);
            account_number.setText(DepositAmountDataCenter.account_autocomplete_data);
        } else {
            account_lin_layout.setVisibility(View.GONE);
        }
        if (!DepositAmountDataCenter.bank_nam_data.equals("")) {
            bank_name_lin_layout.setVisibility(View.VISIBLE);
            bank_name.setText(DepositAmountDataCenter.bank_nam_data);
        } else {
            bank_name_lin_layout.setVisibility(View.GONE);
        }

        if (!DepositAmountDataCenter.typ_data.equals("")) {
            deposit_type_lin_layout.setVisibility(View.VISIBLE);
            deposite_type.setText(DepositAmountDataCenter.typ_data);
        } else {
            deposit_type_lin_layout.setVisibility(View.GONE);
        }

        view.setPadding(10, 10, 10, 10);
        alertDialog.setView(view);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                Intent iii=new Intent(getApplicationContext(),Home.class);
            }
        });

        ConfirmScreenListItemAdapter adapter = new ConfirmScreenListItemAdapter(getApplicationContext(), DataBeforeConformation.pickupAmountArrayList, DataBeforeConformation.DepositeSlipNumberArrayList, DataBeforeConformation.PISnumberArrayList, DataBeforeConformation.HCInumberArrayList, DataBeforeConformation.SealTagArrayList, DataBeforeConformation.ClientCodeArrayList);
        dialoglist.setAdapter(adapter);

        Log.d("Adapter set", "Adapter set");

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Utils.isInternetAvailable(getApplicationContext())) {
                    alertDialog.dismiss();
                    checkPinStatus();

                } else {
//                    storeValueToDB();
                }
            }
        });
        alertDialog.show();
    }

    private void showPinEntryDialog() {


        if (TransactionSingleItemDataCenter.pin_statuss.equals("1")) {
            // create a Dialog component
            dialog = new Dialog(EditBeforeConfirmation.this);

            // tell the Dialog to use the dialog.xml as it's layout
            // description
            dialog.setContentView(R.layout.receivepayment_dialog);
            dialog.setTitle("Customer PIN Verification");

            txt = (EditText) dialog.findViewById(R.id.receivepayment_dialog__txt);

            tv_info = (TextView) dialog
                    .findViewById(R.id.receivepayment_dialog_info);
            tv_error = (TextView) dialog
                    .findViewById(R.id.receivepayment_dialog_error);

            final Button dialogButton = (Button) dialog
                    .findViewById(R.id.receivepayment_dialog_submit_btn);
            dialogButton.setVisibility(View.GONE);
            txt.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    if (count > 3 && count < 11) {
                        dialogButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    if (count > 3 && count < 11) {
                        dialogButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 3 && s.length() < 11) {
                        dialogButton.setVisibility(View.VISIBLE);
                    } else {
                        dialogButton.setVisibility(View.GONE);
                    }
                }
            });
            final Intent inte = new Intent(this, Home.class);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onClick(View v) {
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    if (txt.getText().equals("")) {

                    } else {
                        if (transaction_pin < 2) {
                            gpsTracker = new GPSTracker(getApplicationContext());
                            if (gpsTracker.canGetLocation()) {
                                latitude = gpsTracker.getLatitude();
                                longitude = gpsTracker.getLongitude();
                                lat = (int) (latitude * 1E6);
                                lon = (int) (longitude * 1E6);
                                //Log.i("Lat & Lon :", lat + "," + lat);
                                params = new ArrayList<BasicNameValuePair>();
                                params.add(new BasicNameValuePair("opt",
                                        "check_cpin"));
                                params.add(new BasicNameValuePair("ce_id", Login.ce_id_main));
                                params.add(new BasicNameValuePair("trans_id",
                                        TransactionSingleItemDataCenter.trans_ids));
                                params.add(new BasicNameValuePair("cpin", txt
                                        .getText().toString()));
                                params.add(new BasicNameValuePair("attempt", String
                                        .valueOf(transaction_pin)));
                                params.add(new BasicNameValuePair("lat", String
                                        .valueOf(lat)));
                                params.add(new BasicNameValuePair("lon", String
                                        .valueOf(lon)));
                                params.add(new BasicNameValuePair("IMIE", String
                                        .valueOf(telephonyManager
                                                .getSimSerialNumber())));
                                Log.d("SerialNumber", "::" + telephonyManager.getSimSerialNumber());
                                params.add(new BasicNameValuePair("final", "1"));
                                getJson = new GetJson(EditBeforeConfirmation.this,new GetJson.CallbackInterface() {

                                    @Override
                                    public void onRequestCompleted(JSONObject object) {
                                        String status = "";
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        try {
                                            status = object.getString("status");
                                            if (status.equals("success")) {
                                                //Log.e("Raja", "3");
                                                dialog.dismiss();
                                                sendValueToServer();
                                            } else if (status.equals("failure")) {
                                                //Log.e("Raja", "4");
                                                transaction_pin++;
                                                int trans_pin = 3 - transaction_pin;
                                                tv_info.setText("Attempts remaining : " + trans_pin);
                                                tv_error.setText("Invalid pin number");

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                        }
                                    }
                                });
                                getJson.execute(params);

                            } else
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Please enable the Location Service(GPS/WIFI) for view receipts",
                                        Toast.LENGTH_SHORT).show();
                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            dialog.dismiss();
                            inte.putExtra("ce_id", Login.ce_id_main).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(inte);
                            finish();

                        }
                    }
                }
            });
            dialog.show();
        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(EditBeforeConfirmation.this).create();
            final View signView_layout = View.inflate(EditBeforeConfirmation.this, R.layout.sign_inc, null);

            final Signature signView = (Signature) signView_layout.findViewById(R.id.sign_view);
            signView_layout.setDrawingCacheEnabled(true);
            alertDialog.setView(signView_layout);
            alertDialog.setTitle("Signature");
            alertDialog.setCancelable(false);
          /*  alertDialog.setButton(DialogInterface.BUTTON1, "Clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    (signView).clearCanvas();

                }
            });*/
            alertDialog.setButton(DialogInterface.BUTTON2, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Bitmap bitmap = signView_layout.getDrawingCache();
                    File file = new File(getBaseContext().getFilesDir().getPath()/*Environment.getExternalStorageDirectory().getPath()*/ + File.separator + TransactionSingleItemDataCenter.trans_ids + ".jpeg");

                    img_path = file.getPath();
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        fileOutputStream.write(byteArrayOutputStream.toByteArray());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    alertDialog.dismiss();
                    sendValueToServer();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON3, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    @SuppressWarnings("unchecked")
    public void sendValueToServer() {
        if (Utils.isInternetAvailable(getApplicationContext())) {
            //	progressDialog.show();
            params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("opt", "rec_info"));
            params.add(new BasicNameValuePair("type", TransactionSingleItemDataCenter.types));
            params.add(new BasicNameValuePair("ce_id", Login.ce_id_main));
            params.add(new BasicNameValuePair("trans_id", TransactionSingleItemDataCenter.trans_ids));
            //Log.e("test", "" + strNoTrans);
            params.add(new BasicNameValuePair("no_recs", String
                    .valueOf(ReceivePayment.strNoTrans)));
            //Log.e("test1", Strtrans_param);
            params.add(new BasicNameValuePair("trans_param", Strtrans_param));
            //Log.e("test2", strdenoParam);
            params.add(new BasicNameValuePair("deno", strdenoParam));
            params.add(new BasicNameValuePair("dep_type", DepositAmountDataCenter.typ_data));
            params.add(new BasicNameValuePair("bank_name", DepositAmountDataCenter.bank_nam_data));
            params.add(new BasicNameValuePair("branch_name", DepositAmountDataCenter.branch_name_data));
            params.add(new BasicNameValuePair("account_no", DepositAmountDataCenter.account_autocomplete_data));
            params.add(new BasicNameValuePair("vault_name", DepositAmountDataCenter.vault_name));

            int total_dep_amount=0;
            for (int i=0;i<DataBeforeConformation.pickupAmountArrayList.size();i++)
            {
                total_dep_amount+=Integer.parseInt(DataBeforeConformation.pickupAmountArrayList.get(i)) ;
                Log.d("total_dep_amount","total_dep_amount"+total_dep_amount);
            }

            params.add(new BasicNameValuePair("dep_amount", total_dep_amount+""));
            params.add(new BasicNameValuePair("rec_status", DepositAmountDataCenter.rec_status));

            params.add(new BasicNameValuePair("remarks", DepositAmountDataCenter.remarks));
            params.add(new BasicNameValuePair("device_id", ""));
            params.add(new BasicNameValuePair("client_code", client_code));
            //TODO
            params.add(new BasicNameValuePair("deposit_slip_no", DepositAmountDataCenter.bank_dep_slip_str));

            //Log.d("ReceivePayment",img_path);
            params.add(new BasicNameValuePair("final", "1"));
            params.add(new BasicNameValuePair("qr_details", strQrParam));
            getJson = new GetJson(EditBeforeConfirmation.this,new GetJson.CallbackInterface() {

                @Override
                public void onRequestCompleted(JSONObject object) {
                    progressDialog.dismiss();
                    String status = "", transrec_id = "";
                    try {
                        status = object.getString("status");
                        transrec_id = object.getString("transrec_id");
                    } catch (Exception ex) {
                        transrec_id = "";
                    }
                    if (status.equals("success")) {
                        //Log.e("Raja", "5");
                        dbHandler
                                .execute("update transactions set show='no' where trans_id='"
                                        + TransactionSingleItemDataCenter.trans_ids + "'");

                        if (dbHandler.isExistRow("receipt", TransactionSingleItemDataCenter.trans_ids)) {
                            dbHandler
                                    .execute("update receipt set show='yes' where trans_id='"
                                            + TransactionSingleItemDataCenter.trans_ids + "'");
                            if (Config.DEBUG) {
                                //Log.d(TAG, "The transaction with trans_id: "
                                //	+ trans_id + " updated");
                            }

                        }
                        if (!transrec_id.equals(""))
                        {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Transaction Completed with Transaction Receipt Id: "
                                            + transrec_id, Toast.LENGTH_LONG)
                                    .show();
                        }
                        EditBeforeConfirmation.this.setResult(RESULT_OK);
                        final Intent inte = new Intent(EditBeforeConfirmation.this, Home.class);
                        inte.putExtra("ce_id", Login.ce_id_main).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(inte);
                        finish();
                    } else {

                        //Log.e(TAG, "Pandiyan");
                        Toast.makeText(
                                getApplicationContext(),
                                "Transaction Failed. Try Again with correct information.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (TransactionSingleItemDataCenter.pin_statuss.equals("1"))
                getJson.execute(params);

            if (TransactionSingleItemDataCenter.pin_statuss.equals("2"))
                new Reg_Async().execute();
        }
    }

    public void checkPinStatus()
    {
        Strtrans_param = "";
        strdenoParam = "";
        strQrParam="";
        client_code = "";
        //Log.i(TAG, "Check PIN Status" + pin_status);
        for (int i = 0; i < ReceivePayment.strNoTrans; i++) {
            if (Strtrans_param.equals("")) {
                Log.d("pickupamount::","pickupamount_fnl::"+DataBeforeConformation.pickupAmountArrayList.get(i));
                Strtrans_param = DataBeforeConformation.pickupAmountArrayList.get(i) + "|"
                        + DataBeforeConformation.DepositeSlipNumberArrayList.get(i) + "|" + DataBeforeConformation.PISnumberArrayList.get(i)
                        + "|" + DataBeforeConformation.HCInumberArrayList.get(i) + "|"
                        + DataBeforeConformation.SealTagArrayList.get(i) + "|" + DataBeforeConformation.ClientCodeArrayList.get(i) + "|" + ReceivePayment.lst_remarks.get(i);
                if (ReceivePayment.strNoTrans != 1)
                    Strtrans_param = Strtrans_param + "|" + ReceivePayment.lst_remarks.get(i);

                client_code = DataBeforeConformation.ClientCodeArrayList.get(i);
                //Log.v(TAG,"Transparam"+Strtrans_param);
                strdenoParam = ReceivePayment.listDeno.get(i);
                strQrParam = ReceivePayment.listQrDetails.get(i);
            } else {
                Log.d("pickupamount::","pickupamount_fnl::"+DataBeforeConformation.pickupAmountArrayList.get(i));

                Strtrans_param = Strtrans_param + "^"
                        + DataBeforeConformation.pickupAmountArrayList.get(i) + "|"
                        + DataBeforeConformation.DepositeSlipNumberArrayList.get(i) + "|" + DataBeforeConformation.PISnumberArrayList.get(i)
                        + "|" + DataBeforeConformation.HCInumberArrayList.get(i) + "|"
                        + DataBeforeConformation.SealTagArrayList.get(i) + "|" + DataBeforeConformation.ClientCodeArrayList.get(i) + "|" + ReceivePayment.lst_remarks.get(i);

                if (ReceivePayment.strNoTrans != 1)
                    Strtrans_param = Strtrans_param + "|" + ReceivePayment.lst_remarks.get(i);
                //Log.v(TAG,"Transparam:"+Strtrans_param);
                strdenoParam = strdenoParam + "^" + ReceivePayment.listDeno.get(i);
                strQrParam = strQrParam + "^" + ReceivePayment.listQrDetails.get(i);
                client_code = ReceivePayment.list_ccode.get(i);
            }

            Log.d("Strtrans_param","Strtrans_param::##"+Strtrans_param);
        }
//        dialog.dismiss();
        showPinEntryDialog();
//        showEnteredData();
    }


    class Reg_Async extends AsyncTask<Void, Void, Void> {
        String test = "";
        StringBuilder stringBuilder = new StringBuilder();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            test = "false";
            progressDialog = new ProgressDialog(EditBeforeConfirmation.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
          /*  try {

                JSONObject      jsonObject =   new JSONObject(stringBuilder.toString());
                if(jsonObject1.getString("success").equals("1")) {
                    Toast.makeText(ReceivePayment.this, jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    //   startActivity(new Intent(ReceivePayment.this,Reg.class));
                    //((HomeScreen) ReceivePayment.this).set_Fragment(PlaceholderFragment.newInstance(1));
                }
                else
                    Toast.makeText(ReceivePayment.this,jsonObject1.getString("message"),Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            String status = "", transrec_id = "";
            try {
                status = jsonObject.getString("status");
                transrec_id = jsonObject.getString("transrec_id");
            } catch (Exception ex) {
                transrec_id = "";
            }
            if (status.equals("success")) {
                //Log.e("Raja", "5");
                dbHandler
                        .execute("update transactions set show='no' where trans_id='"
                                + TransactionSingleItemDataCenter.trans_ids + "'");

                if (dbHandler.isExistRow("receipt", TransactionSingleItemDataCenter.trans_ids)) {
                    dbHandler
                            .execute("update receipt set show='yes' where trans_id='"
                                    + TransactionSingleItemDataCenter.trans_ids + "'");
                    if (Config.DEBUG) {
                        //Log.d(TAG, "The transaction with trans_id: "
                        //    + trans_id + " updated");
                    }

                }
                if (!transrec_id.equals("")) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Transaction Completed with Transaction Receipt Id: "
                                    + transrec_id, Toast.LENGTH_LONG)
                            .show();
                }
                EditBeforeConfirmation.this.setResult(RESULT_OK);
                final Intent inte = new Intent(EditBeforeConfirmation.this, Home.class);
                inte.putExtra("ce_id", Login.ce_id_main).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(inte);
                finish();
            } else {
                //Log.e(TAG, "Pandiyan");
                Toast.makeText(
                        getApplicationContext(),
                        "Transaction Failed. Try Again with correct information.",
                        Toast.LENGTH_SHORT).show();
            }

/*
            if(stringBuilder.toString().contains("success"))
            {
                Toast.makeText(ReceivePayment.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
             //   startActivity(new Intent(ReceivePayment.this,Reg.class));
                ((HomeScreen)ReceivePayment.this).set_Fragment(AdFragment.newInstance("",""));
            }
            else
                Toast.makeText(ReceivePayment.this,"Upload failed",Toast.LENGTH_LONG).show();
*/
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Config.url1);

                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                //multipartEntity.addPart("userid",new StringBody(ReceivePayment.this.getSharedPreferences(Config.shared_name, Context.MODE_PRIVATE).getString("userid","")));
//                multipartEntity.addPart("comments",new StringBody(str_comments));
                //              multipartEntity.addPart("prescription",new FileBody(new File(selectedImagePath/*"https://www.google.co.in/images/srpr/logo11w.png"*/),"image/png"));
//                //Log.v("Login","Selectedpath"+selectedImagePath);


                for (int i = 0; i < params.size(); i++)
                    multipartEntity.addPart(params.get(i).getName(), new StringBody(params.get(i).getValue()));
                //params.add(new BasicNameValuePair("sign_image",img_path));
                multipartEntity.addPart("sign_image", new FileBody(new File(img_path)));
                //  multipartEntity.addPart("prescription",new FileBody(new File(selectedImagePath/*"https://www.google.co.in/images/srpr/logo11w.png"*/),"image/png"));
                httpPost.setEntity(multipartEntity);
                HttpResponse httpResponse = httpClient.execute(httpPost);

                /*list.add(new BasicNameValuePair("studentname",str_name));
                list.add(new BasicNameValuePair("studentclass",str_class));
                list.add(new BasicNameValuePair("rollno",str_roll));
                list.add(new BasicNameValuePair("department",str_dept));
                list.add(new BasicNameValuePair("password",str_pass));
                list.add(new BasicNameValuePair("profileimage", Base64.encodeToString(bytes,0)));

*/
                BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF8"));
                while ((test = bufferedInputStream.readLine()) != null) {
                    stringBuilder.append(test);
                }
                //Log.v("Login", stringBuilder.toString() + "asd" + httpResponse.getStatusLine().getStatusCode());

            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
            return null;
        }
    }


    private void showPopup(View view) {
        spinnerWindow = PopupHelper
                .newBasicPopupWindow(getApplicationContext());
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.spinner_listview, null);
        spinnerWindow.setContentView(popupView);
        listSpinner = (ListView) popupView.findViewById(R.id.listview);

        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_items,
                rec_status_option);
        listSpinner.setAdapter(spinnerAdapter);
        spinnerWindow.setWidth(txtWidth);
        spinnerWindow.setAnimationStyle(R.style.Animations_GrowFromTop);
        spinnerWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        listSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
                receipt_status_spinner.setText(rec_status_option[position]);
                if (receipt_status_spinner.getText().toString().equalsIgnoreCase("Others"))
                    layout3.setVisibility(View.VISIBLE);
                else {
                    layout3.setVisibility(View.GONE);
                    //  remarks_txt.requestFocus();
                }
                spinnerWindow.dismiss();
            }
        });

    }


}