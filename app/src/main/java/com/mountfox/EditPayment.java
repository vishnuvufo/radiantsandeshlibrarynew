package com.mountfox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class EditPayment extends Activity implements View.OnClickListener {
    int mHour, mMinute;//satz
    private String type = "", pickup_amount = "", pickup_amount1 = "", amt = "",
            deposit_slip = "", diff_slip = "", pis_no = "", hci_no = "", sealtag_no = "",
            device_id = "",
            pin_status = "", Strtrans_param = "", client_code = "", Strdeno = "", strdenoParam = "";
    private static final int TIME_DIALOG_ID = 0;
    private static final String TAG = "EditPayment";
    static int transaction_pin = 0, activity_pass = 0, viewPosition;
    double latitude = 12.982733625, longitude = 80.252031675;
    private PopupWindow spinnerWindow;
    private ListView listSpinner;
    private Dialog dialog;
    private EditText pickup_amount_txt, dep_no_txt, pis_no_txt, hci_no_txt,
            rec_no_txt, dep_amount_txt, remarks_txt, txt, edtxtDeno2000, edtxtDeno1000,
            edtxtDeno500, edtxtDeno100, edtxtDeno50, edtxtDeno20, edtxtDeno10,
            edtxtDeno5, edtxtDenoCoins, bank_dep_slip_edt, req_edt, edt_spin, edt_dep_branch;
    private TextView receipt_status_spinner, tv_info, tv_error, Header, diff_no_txt, pickup_time;
    private Button submit_btn, delete_btn;
    private ProgressDialog progressDialog;
    private GetJson getJson;
    private DbHandler dbHandler;
    private GPSTracker gpsTracker;
    private TelephonyManager telephonyManager;
    private List<BasicNameValuePair> params;
    final Context context = this;
    private ArrayAdapter<String> spinnerAdapter;
    private LinearLayout layout1, layout2, layout3, lin_bank, lin_vault, lin_dep, lin_pis, lin_hci, lin_receipt, lin_dep_slip;
    private Spinner bank_spin, bvType_spin, vault_spin, spin_branch;

    private ArrayAdapter<String> bankSpin_adap, vaultSpin_adap, branch_adap;
    private ArrayList<String> cspin_arls = new ArrayList<String>();
    private ArrayList<String> bank_arls = new ArrayList<String>();
    private ArrayList<String> vault_arls = new ArrayList<String>();
    private ArrayList<String> type_arls = new ArrayList<String>();
    private ArrayList<Bank_Pojo> bank_acc_arls = new ArrayList<Bank_Pojo>();
    private ArrayList<String> accounts = new ArrayList<String>();
    private ArrayList<String> acc_ids = new ArrayList<String>();

    PreferenceHelper helper;
    Context mContext;

    List<String> rec_status_option;
//    List<String> rec_status_option = new ArrayList<String>();
    //    private String rec_status_option[] = {  "Dry Run",
//            "Cash Received",
//            "CE visited and No cash",
//            "CE Visited and Shop Closed",
//            "Difference in Cash",
//            "CE Visited No Cash, Customer refused to sign NCR",
//            "Counting and Verfication done at customer permises",
//            "Counting Machine Not Made Available and 100 % Counting Not Done",
//            "Called Up & Checked No Cash",
//            "Inactive",
//            "Pickup On Hold",
//            "CE Not Visited",
//            "Bank Holiday"};
    private int txtWidth, diff_amount = 0, diff_deno2000 = 0, diff_deno1000 = 0, diff_deno500 = 0, diff_deno100 = 0, diff_deno50 = 0, diff_deno20 = 0,
        diff_deno10 = 0, diff_deno5 = 0, diff_denocoins = 0;
    private String rid = "", trans_id = "", point_name = "", req_amount = "", no_recs = "", captions = "", trans_param = "", deno = "", dep_amount = "",
            dep_type = "", bank_name = "", branch_name = "", account_no = "", deposit_slip_no = "", vault_name = "", rec_status = "", remarks = "",
            ce_id = "", rec_datetime = "", pick_amt = "", pis = "", seal = "", hci = "", receipt_no = "", i_rec = "";
    private ArrayList<String> pick_arls = new ArrayList<String>();
    private ArrayList<String> pis_arls = new ArrayList<String>();
    //private ArrayList<String>   seal_arls   =   new ArrayList<String>();
    private ArrayList<String> hci_arls = new ArrayList<String>();
    private ArrayList<String> rec_arls = new ArrayList<String>();
    private ArrayList<String> cc_arls = new ArrayList<String>();
    private ArrayList<String> irec_arls = new ArrayList<String>();
    private ArrayList<String> dep_no_arls = new ArrayList<String>();
    private ArrayList<String> den2000_arls = new ArrayList<String>();
    private ArrayList<String> den1000_arls = new ArrayList<String>();
    private ArrayList<String> den500_arls = new ArrayList<String>();
    private ArrayList<String> deno100_arls = new ArrayList<String>();
    private ArrayList<String> den50_arls = new ArrayList<String>();
    private ArrayList<String> deno20_arls = new ArrayList<String>();
    private ArrayList<String> deno10_arls = new ArrayList<String>();
    private ArrayList<String> deno5_arls = new ArrayList<String>();
    private ArrayList<String> denoc_arls = new ArrayList<String>();

    private LinearLayout lin_deno, lin_req, rec_no_lin, hci_no_lin, pis_no_lin, dep_no_lin;
    private List<BasicNameValuePair> mEntity = new ArrayList<BasicNameValuePair>();
    private List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
    private int count = 0, cur_pos = 0, final_cur_pos = 0, deno_stat = 0;
    TextView txt_1, txt_2, txt_3, txt_4, txt_5;
    private ArrayList<String> del_ids = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_edit_payment);

        mContext = this;
        helper = new PreferenceHelper();

        rec_status_option = new ArrayList<String>();
        try {
            JSONArray j1 = new JSONArray(helper.getPickupRemark1(mContext));
            JSONArray j2 = new JSONArray(helper.getPickupRemark2(mContext));
            JSONArray j3 = new JSONArray(helper.getPickupRemark3(mContext));
//            JSONArray j4 = new JSONArray(helper.getPickupRemark4(mContext));
//            JSONArray j5 = new JSONArray(helper.getPickupRemark5(mContext));
            for (int i = 0; i < j1.length(); i++) {
                rec_status_option.add(j1.getString(i));
            }
            for (int i = 0; i < j2.length(); i++) {
                rec_status_option.add(j2.getString(i));
            }
            for (int i = 0; i < j3.length(); i++) {
                rec_status_option.add(j3.getString(i));
            }
//            for (int i = 0; i < j4.length(); i++) {
//                rec_status_option.add(j4.getString(i));
//            }
//            for (int i = 0; i < j5.length(); i++) {
//                rec_status_option.add(j5.getString(i));
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeComponents();
    }

    @SuppressLint("MissingPermission")
    public void initializeComponents() {
        Header = (TextView) findViewById(R.id.Header);
        pickup_amount_txt = (EditText) findViewById(R.id.pickup_amount_txt);
        bank_dep_slip_edt = (EditText) findViewById(R.id.bank_dep_slip_txt);
        dep_no_txt = (EditText) findViewById(R.id.dep_no_txt);
        diff_no_txt = (EditText) findViewById(R.id.diff_amount_txt);
        pis_no_txt = (EditText) findViewById(R.id.pis_no_txt);
        hci_no_txt = (EditText) findViewById(R.id.hci_no_txt);
        rec_no_txt = (EditText) findViewById(R.id.rec_no_txt);
        dep_amount_txt = (EditText) findViewById(R.id.dep_amount_txt);
        remarks_txt = (EditText) findViewById(R.id.remarks_txt);
        receipt_status_spinner = (TextView) findViewById(R.id.receipt_status_spinner);
        edtxtDeno2000 = (EditText) findViewById(R.id.deno_2000);
        edtxtDeno1000 = (EditText) findViewById(R.id.deno_1000);
        edtxtDeno500 = (EditText) findViewById(R.id.deno_500);
        edtxtDeno100 = (EditText) findViewById(R.id.deno_100);
        edtxtDeno50 = (EditText) findViewById(R.id.deno_50);
        edtxtDeno20 = (EditText) findViewById(R.id.deno_20);
        edtxtDeno10 = (EditText) findViewById(R.id.deno_10);
        edtxtDeno5 = (EditText) findViewById(R.id.deno_5);
        edtxtDenoCoins = (EditText) findViewById(R.id.deno_coins);
        edt_dep_branch = (EditText) findViewById(R.id.branch_name_edt);
//        pickup_time=(EditText)findViewById(R.id.pickup_time);//satz
        bank_dep_slip_edt = (EditText) findViewById(R.id.bank_dep_slip_txt);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        //  delete_btn      = (Button) findViewById(R.id.delete_btn);
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout3 = (LinearLayout) findViewById(R.id.layout3);
        lin_bank = (LinearLayout) findViewById(R.id.lin_bank);
        lin_dep = (LinearLayout) findViewById(R.id.lin_dep);
        lin_vault = (LinearLayout) findViewById(R.id.lin_vault);
        lin_dep_slip = (LinearLayout) findViewById(R.id.dep_no_lin);
        lin_pis = (LinearLayout) findViewById(R.id.pis_no_lin);
        lin_hci = (LinearLayout) findViewById(R.id.hci_no_lin);
        lin_receipt = (LinearLayout) findViewById(R.id.rec_no_lin);
        bvType_spin = (Spinner) findViewById(R.id.dep_type_spin);
        bank_spin = (Spinner) findViewById(R.id.bank_name_spin);
        edt_spin = (EditText) findViewById(R.id.edt_spin);
        vault_spin = (Spinner) findViewById(R.id.vault_name_spin);
//        spin_acc        = (Spinner)      findViewById(R.id.acc_no_spin);
        spin_branch = (Spinner) findViewById(R.id.branch_name_spin);
        lin_req = (LinearLayout) findViewById(R.id.req_lin);
        req_edt = (EditText) findViewById(R.id.req_amount_txt);
        lin_deno = (LinearLayout) findViewById(R.id.lin_deno);
        txt_1 = (TextView) findViewById(R.id.txt_1);
        txt_2 = (TextView) findViewById(R.id.txt_2);
        txt_3 = (TextView) findViewById(R.id.txt_3);
        txt_4 = (TextView) findViewById(R.id.txt_4);
        txt_5 = (TextView) findViewById(R.id.txt_5);
        rec_no_lin = (LinearLayout) findViewById(R.id.rec_no_lin);
        hci_no_lin = (LinearLayout) findViewById(R.id.hci_no_lin);
        pis_no_lin = (LinearLayout) findViewById(R.id.pis_no_lin);
        dep_no_lin = (LinearLayout) findViewById(R.id.dep_no_lin);
        lin_req.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);


        //cspin_arls      =
        if (getIntent().hasExtra("ccode")) {
            //Log.v("EditPayment",getIntent().getStringExtra("ccode"));
            String[] ccode = getIntent().getStringExtra("ccode").split(",");
            for (int i = 0; i < ccode.length; i++) {
                cspin_arls.add(ccode[i]);
                //Log.v("EditPayment",ccode[i]);
            }
            if (ccode.length <= 0)
                edt_spin.setVisibility(View.GONE);
            if (cspin_arls.size() <= 0)
                cspin_arls.add("0");
        }
        type_arls.add("Burial");
        type_arls.add("Partner Bank");
        type_arls.add("Client Bank");
        type_arls.add("Vault");
        dbHandler = new DbHandler(this);

        bank_arls = dbHandler.get_list("bank_list");
        vault_arls = dbHandler.get_list("vault_list");
        // bvType_spin.setPrompt("Select Deposit Type");
        //bank_spin.setPrompt("Select Bank");
        //vault_spin.setPrompt("Select Vault");

        type_arls.add(0, "Select Deposit Type");
        bank_arls.add(0, "Select Bank");
        vault_arls.add(0, "Select Vault");
        bvType_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 4) {
                    lin_vault.setVisibility(View.GONE);
                    if (count == cur_pos)
                        lin_bank.setVisibility(View.VISIBLE);
                    if (i != 0) {
                        bank_arls = dbHandler.get_list("bank_list");
                        bank_spin.setAdapter(bankSpin_adap);
                    } else {
                        //  bank_arls.clear();
                        bank_spin.setAdapter(bankSpin_adap);
                        accounts.clear();
                        acc_ids.clear();
                        branch_adap.notifyDataSetChanged();
                    }
                } else {
                    lin_vault.setVisibility(View.VISIBLE);
                    lin_bank.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//satz
//        pickup_time.setInputType(InputType.TYPE_NULL);
//        pickup_time.setOnClickListener(new View.OnClickListener() {
//                                           @Override
//                                           public void onClick(View v) {
//                                               datetime();
//                                           }
//                                       });
//
        vaultSpin_adap = new ArrayAdapter<String>(EditPayment.this, R.layout.spinner_items, vault_arls) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditPayment.this, R.layout.spinner_items, null);
                TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(vault_arls.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditPayment.this, R.layout.spinner_items, null);

                // if(position!=0)
                {
                    TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(vault_arls.get(position));
                }
                return convertView;
            }
        };

        bankSpin_adap = new ArrayAdapter<String>(EditPayment.this, R.layout.spinner_items, bank_arls) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditPayment.this, R.layout.spinner_items, null);
                TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(bank_arls.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditPayment.this, R.layout.spinner_items, null);
                // if(position!=0)
                try {
                    TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(bank_arls.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return convertView;
            }
        };

        branch_adap = new ArrayAdapter<String>(this, R.layout.spinner_items, accounts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditPayment.this, R.layout.spinner_items, null);
                TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(accounts.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditPayment.this, R.layout.spinner_items, null);
                {
                    TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(accounts.get(position));
                }
                return convertView;
            }
        };
        spin_branch.setAdapter(branch_adap);

        /*acc_adap    =   new ArrayAdapter<String>(this,R.layout.spinner_items,accounts)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView =   View.inflate(EditPayment.this,R.layout.spinner_items,null);
                TextView    txt     = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(accounts.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView =   View.inflate(EditPayment.this,R.layout.spinner_items,null);
                //  if(position!=0)
                {
                    TextView    txt     = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(accounts.get(position));
                }
                return convertView;
            }
        };
        spin_acc.setAdapter(acc_adap);*/
        bank_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //if(i!=0)
                {
                    bank_acc_arls.clear();
                    bank_acc_arls = dbHandler.get_list_where("bank_list", bank_arls.get(i), "", "0");
                    //    branches.clear();
                    accounts.clear();
                    acc_ids.clear();
                    //Log.v("EditPayment","bank_acc_arls size"+bank_acc_arls.size());
                    for (int ii = 0; ii < bank_acc_arls.size(); ii++) {
                        //  if(!branches.contains(bank_acc_arls.get(ii).getBranch_name()))
                        acc_ids.add(bank_acc_arls.get(ii).getId());
                        //Log.v("EditPayment","acc_ids"+acc_ids.get(ii)+":"+acc_ids.size());
                        accounts.add(bank_acc_arls.get(ii).getAcc_no());
                    }
                    //edt_branch.setText(bank_acc_arls.get(0).getAcc_no());
                    branch_adap.notifyDataSetChanged();
                    //acc_adap.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        bank_spin.setAdapter(bankSpin_adap);

        /*spin_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bank_acc_arls.clear();
                bank_acc_arls = dbHandler.get_list_where("bank_list", bank_arls.get(bank_spin.getSelectedItemPosition()), branches.get(spin_branch.getSelectedItemPosition()), "1");
                accounts.clear();
                for (int ii = 0; ii < bank_acc_arls.size(); ii++) {
                    accounts.add(bank_acc_arls.get(ii).getAcc_no());
                    //Log.v("ReceivePayment", "received accounts:" + accounts.get(ii));
                }
                //acc_adap.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        bvType_spin.setAdapter(new ArrayAdapter<String>(EditPayment.this, android.R.layout.simple_list_item_1, android.R.id.text1, type_arls) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(EditPayment.this, android.R.layout.simple_list_item_1, null);

                // if(position!=0)
                {
                    TextView _txt = (TextView) convertView.findViewById(android.R.id.text1);
                    _txt.setTextColor(Color.BLACK);
                    _txt.setText(type_arls.get(position));
                }
                return convertView;
            }
        });
        vault_spin.setAdapter(vaultSpin_adap);
        /*progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in Progress");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.progressbar));*/

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submit_btn.getText().equals("Next")) {
                    //Log.v("EditPayment","final_cur_pos:"+final_cur_pos+":cursor:"+cur_pos);
                    //  if()

                    cur_pos += 1;

                    //  Header.setText("(" + type + " )"+", Receipt :"+cur_pos+1);
                    // submit_updates();
                    final_cur_pos = cur_pos;
                    update();

                    //deno_calc();
                } else {

                    get_finalAmt();

                    //Toast.makeText(EditPayment.this,"Final",Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               *//* final AlertDialog dialog1 =   new AlertDialog.Builder(EditPayment.this).create();
                dialog1.setMessage("Do you wanna delete?");
                dialog1.setButton(DialogInterface.BUTTON1,"Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        del_ids.add(String.valueOf(cur_pos));
                        den1000_arls.set(cur_pos,"d");
                        den500_arls.set(cur_pos,"d");
                        deno100_arls.set(cur_pos,"d");
                        den50_arls.set(cur_pos,"d");
                        deno20_arls.set(cur_pos,"d");
                        deno10_arls.set(cur_pos,"d");
                        deno5_arls.set(cur_pos,"d");
                        denoc_arls.set(cur_pos,"d");
                        pis_arls.set(final_cur_pos,"d");
                        dep_no_arls.set(final_cur_pos,"d");
                        pis_arls.set(final_cur_pos,"d");
                        hci_arls.set(final_cur_pos,"d");
                        rec_arls.set(final_cur_pos,"d");
                        dep_no_arls.set(final_cur_pos,"d");
                        dialog1.dismiss();
                    }
                });
                dialog1.setButton(DialogInterface.BUTTON2,"Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    dialog1.dismiss();
                    }
                });
                dialog1.show();*//*
            }
        });*/
        transaction_pin = 0;
        activity_pass = 0;
        ce_id = getIntent().getStringExtra("ce_id");
        //      trans_id = getIntent().getStringExtra("trans_id");
        //      amt =   getIntent().getStringExtra("amt");
//        //Log.e("Trans id", trans_id);
        type = getIntent().getStringExtra("type");

        Header.setText(getIntent().getStringExtra("cust_name") + ": (" + type + " )" + ", Receipt :" + (cur_pos + 1));

        //      pin_status = getIntent().getStringExtra("pin_status");
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        device_id = telephonyManager.getDeviceId();

        //   pickup_amount_txt.setHint(type + " Amount");
        if (Config.DEBUG) {
            //Log.d(TAG, "Ce_id: " + ce_id + ", Trans Id: " + trans_id
            //         + ", Type: " + type);
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
        mEntity.add(new BasicNameValuePair("opt", "edit_bill"));
        mEntity.add(new BasicNameValuePair("ce_id", getIntent().getStringExtra("cce_id")));
        mEntity.add(new BasicNameValuePair("lat", getIntent().getStringExtra("lat")));
        mEntity.add(new BasicNameValuePair("lon", getIntent().getStringExtra("lon")));
        mEntity.add(new BasicNameValuePair("IMIE", getIntent().getStringExtra("imei")));
        mEntity.add(new BasicNameValuePair("trans_id", getIntent().getStringExtra("trans_id")));
        mEntity.add(new BasicNameValuePair("final", "1"));
        new Get_Bill().execute();
    }

    @Override
    public void onClick(View view) {

    }

    class Get_Bill extends AsyncTask<Void, Void, Void> {

        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EditPayment.this);
            progressDialog.setMessage("Loading, Please Wait ...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            count = Integer.parseInt(no_recs);

            int j = 0;
            // if(Integer.parseInt(no_recs)>1)

            String[] test = trans_param.split(",");
            for (int i = 0; i < Integer.parseInt(no_recs); ) {
                pick_arls.add(test[j]);
                dep_no_arls.add(test[j + 1]);
                pis_arls.add(test[j + 2]);
                hci_arls.add(test[j + 3]);
                rec_arls.add(test[j + 4]);
                cc_arls.add(test[j + 5]);
                if (no_recs.equalsIgnoreCase("1"))
                    irec_arls.add(rec_status);
                else
                    irec_arls.add(test[j + 6]);
                //Log.v("EditPayment", "pick:" + pick_arls.get(i) + ":dep:" + dep_no_arls.get(i) + ":pis:" + pis_arls.get(i) + ":hci:" + hci_arls.get(i) + ":rec:" + rec_arls.get(i) + ":ccode:" + cc_arls.get(i));
                i += 1;
                j += 7;
            }

            j = 0;
            //Integer.parseInt(deno.split("|")[0])


            // for(int i=0;i<test.length;i++)
            //   //Log.v("EditPayment",test[i]);
            String[] tem = new String[deno.split(",").length + 2];
            tem = deno.split(",");

            for (int i = 0; i < Integer.parseInt(tem[0]); i++) {
                den2000_arls.add(tem[j + 1]);
                den1000_arls.add(tem[j + 2]);
                den500_arls.add(tem[j + 3]);
                deno100_arls.add(tem[j + 4]);
                den50_arls.add(tem[j + 5]);
                deno20_arls.add(tem[j + 6]);
                deno10_arls.add(tem[j + 7]);
                deno5_arls.add(tem[j + 8]);
                denoc_arls.add(tem[j + 9]);
                j += 9;
            }
            for (int i = 0; i < deno20_arls.size(); i++)
                //Log.v("EditPayment","deno:"+tem[0]+":"+den1000_arls.get(i));
                if (Integer.parseInt(no_recs) != 1) {
                    //   layout3.setVisibility(View.GONE);
                    //layout2.setVisibility(View.GONE);
                    layout1.setVisibility(View.GONE);
                    lin_bank.setVisibility(View.GONE);
                    lin_dep.setVisibility(View.GONE);
                }


            if (deno_stat == 0) {
                lin_deno.setVisibility(View.GONE);
            }
            update();
            txt_change();
            String[] caps_ = captions.split(",");

            if (caps_[1].equalsIgnoreCase("0")) {
                txt_2.setVisibility(View.GONE);
                dep_no_lin.setVisibility(View.GONE);
            } else {
                txt_2.setText(caps_[1]);
            }

            if (caps_[2].equalsIgnoreCase("0")) {
                txt_3.setVisibility(View.GONE);
                pis_no_lin.setVisibility(View.GONE);
            } else {
                txt_3.setText(caps_[2]);
            }
            if (caps_[3].equalsIgnoreCase("0")) {
                txt_4.setVisibility(View.GONE);
                hci_no_lin.setVisibility(View.GONE);
            } else {
                txt_4.setText(caps_[3]);
            }

            if (caps_[4].equalsIgnoreCase("0")) {
                txt_5.setVisibility(View.GONE);
                rec_no_lin.setVisibility(View.GONE);
            } else {
                txt_5.setText(caps_[4]);
            }

            if (progressDialog.isShowing())
                progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpParams params = new BasicHttpParams();

                HttpConnectionParams.setTcpNoDelay(params, true);

                HttpClient httpclient = new DefaultHttpClient(params);

                HttpPost httppost = new HttpPost(Config.url1);

                httppost.setEntity(new UrlEncodedFormEntity(mEntity));

                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);
                if (Config.DEBUG) {
                    //Log.d("EditReceipt", "Result : " + result);
                }
                jsonObject = new JSONObject(result);
                if (!TextUtils.isEmpty(jsonObject.getString("rid"))) {
                    rid = jsonObject.getString("rid");
                    trans_id = jsonObject.getString("trans_id");
                    point_name = jsonObject.getString("point_name");
                    req_amount = jsonObject.getString("req_amount");
                    no_recs = jsonObject.getString("no_recs");
                    captions = jsonObject.getString("captions");
                    trans_param = jsonObject.getString("trans_param");
                    deno = jsonObject.getString("deno");
                    dep_amount = jsonObject.getString("dep_amount");
                    dep_type = jsonObject.getString("dep_type");
                    bank_name = jsonObject.getString("bank_name");
                    branch_name = jsonObject.getString("branch_name");
                    account_no = jsonObject.getString("account_no");
                    deposit_slip_no = jsonObject.getString("deposit_slip_no");
                    vault_name = jsonObject.getString("vault_name");
                    rec_status = jsonObject.getString("rec_status");
                    remarks = jsonObject.getString("remarks");
                    if (remarks.length() != 0)
                        rec_status = remarks;

                    ce_id = jsonObject.getString("ce_id");
                    rec_datetime = jsonObject.getString("rec_datetime");
                    deno_stat = Integer.parseInt(jsonObject.getString("deno_status"));
                }


            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog.isShowing())
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
        remarks_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                irec_arls.set(cur_pos, remarks_txt.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        listSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
//                receipt_status_spinner.setText(rec_status_option[position]);
                receipt_status_spinner.setText(rec_status_option.get(position));
                if (receipt_status_spinner.getText().toString().equalsIgnoreCase("Others")) {

                    layout3.setVisibility(View.VISIBLE);
                } else {
                    irec_arls.set(cur_pos, receipt_status_spinner.getText().toString());

                    layout3.setVisibility(View.GONE);
                    //  remarks_txt.requestFocus();
                }
                spinnerWindow.dismiss();
            }
        });

    }

    public void update() {
        // if(no_recs.equalsIgnoreCase(""))

        if (cur_pos < count) {

            edt_spin.setText(cc_arls.get(cur_pos));
            pickup_amount_txt.setText(pick_arls.get(cur_pos));
            req_edt.setText(req_amount);
            // dep_no_txt.setText(dep_no_arls.get(cur_pos));
            pis_no_txt.setText(pis_arls.get(cur_pos));
            hci_no_txt.setText(hci_arls.get(cur_pos));
            rec_no_txt.setText(rec_arls.get(cur_pos));
            dep_no_txt.setText(dep_no_arls.get(cur_pos));


            if (deno_stat == 1 && count == 1 && cur_pos == 0) {
                {
                    lin_deno.setVisibility(View.VISIBLE);
                    edtxtDeno2000.setText(den2000_arls.get(cur_pos));
                    edtxtDeno1000.setText(den1000_arls.get(cur_pos));
                    edtxtDeno500.setText(den500_arls.get(cur_pos));
                    edtxtDeno100.setText(deno100_arls.get(cur_pos));
                    edtxtDeno50.setText(den50_arls.get(cur_pos));
                    edtxtDeno20.setText(deno20_arls.get(cur_pos));
                    edtxtDeno10.setText(deno10_arls.get(cur_pos));
                    edtxtDeno5.setText(deno5_arls.get(cur_pos));
                    edtxtDenoCoins.setText(denoc_arls.get(cur_pos));
                }
            } else if (deno_stat == 2) {
                lin_deno.setVisibility(View.VISIBLE);
                edtxtDeno2000.setText(den2000_arls.get(cur_pos));
                edtxtDeno1000.setText(den1000_arls.get(cur_pos));
                edtxtDeno500.setText(den500_arls.get(cur_pos));
                edtxtDeno100.setText(deno100_arls.get(cur_pos));
                edtxtDeno50.setText(den50_arls.get(cur_pos));
                edtxtDeno20.setText(deno20_arls.get(cur_pos));
                edtxtDeno10.setText(deno10_arls.get(cur_pos));
                edtxtDeno5.setText(deno5_arls.get(cur_pos));
                edtxtDenoCoins.setText(denoc_arls.get(cur_pos));
            } else
                lin_deno.setVisibility(View.GONE);


            //  bank_dep_slip_edt.setText(bank_dep_slip_str);
            // //Log.v("EditPayment","bank_name:"+bank_name);
            //   //Log.v("EditPayment","bank_name pos"+bank_arls.get(bank_arls.indexOf(bank_name)));

            if (!TextUtils.isEmpty(dep_type))
                bvType_spin.setSelection(type_arls.indexOf(dep_type));
            //Log.v("EditPayment","dep_type:"+dep_type+":"+account_no+":"+branch_name);
            if (!TextUtils.isEmpty(bank_name)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bank_spin.setSelection(bank_arls.indexOf(bank_name));

                    }
                }, 200);
                if (!TextUtils.isEmpty(branch_name))
                    edt_dep_branch.setText(branch_name);

                // bankSpin_adap.notifyDataSetChanged();
                //bank_spin.setSelection(5,true);

//                   //Log.v("EditPayment", "bank select count:" + bank_arls.get(bank_spin.getSelectedItemPosition()) + ":" + bank_spin.getSelectedItemPosition());
            }
            if (!TextUtils.isEmpty(account_no))
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spin_branch.setSelection(accounts.indexOf(account_no));

                    }
                }, 400);
                /*if(!TextUtils.isEmpty(account_no))
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            spin_acc.setSelection(accounts.indexOf(account_no));
                        }
                    },600);*/
            if (!TextUtils.isEmpty(vault_name))
                vault_spin.setSelection(vault_arls.indexOf(vault_name));
            //Log.v("EditPayment","vault_name:"+vault_name);

            dep_amount_txt.setText(dep_amount);
            // receipt_status_spinner.setEnabled(false);

            receipt_status_spinner.setText(irec_arls.get(cur_pos));
            if (receipt_status_spinner.getText().toString().equalsIgnoreCase("others"))
                layout3.setVisibility(View.VISIBLE);
            else
                layout3.setVisibility(View.GONE);
                /*if(!receipt_status_spinner.getText().toString().equalsIgnoreCase("others"))
                {
                    remarks_txt.setText("");
                    remarks =   "";
                    layout3.setVisibility(View.GONE);
                }
                else
                */
            bank_dep_slip_edt.setText(deposit_slip_no);
            //    //Log.v("EditPayment","dep_type0:"+dep_type+":"+account_no+":"+branch_name);

            //Log.v("EditPayment", "count:" + no_recs);
            //if (cur_pos < count)
            {
                submit_btn.setText("Next");
                Header.setText(getIntent().getStringExtra("cust_name") + ": (" + type + " )" + ", Receipt :" + (cur_pos + 1));
            }
            if (count == 1) {
                submit_btn.setText("Submit");
                /*
                    submit_btn.setText("Submit");

                    submit_btn.setText("Submit");
                    final_cur_pos--;
                    lin_bank.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                    //  layout2.setVisibility(View.VISIBLE);
                    lin_dep.setVisibility(View.VISIBLE);
                    if(!TextUtils.isEmpty(dep_type))
                        bvType_spin.setSelection(type_arls.indexOf(dep_type));
                    //Log.v("EditPayment","dep_type0:"+dep_type+":"+account_no+":"+branch_name+":"+acc_ids.size());
                    if(!TextUtils.isEmpty(bank_name) )
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bank_spin.setSelection(bank_arls.indexOf(bank_name));

                            }
                        },200);
                        if(!TextUtils.isEmpty(account_no))
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    spin_branch.setSelection(acc_ids.indexOf(account_no));
                                }
                            },400);

                        }

                        // bankSpin_adap.notifyDataSetChanged();
                        //bank_spin.setSelection(5,true);

//                   //Log.v("EditPayment", "bank select count:" + bank_arls.get(bank_spin.getSelectedItemPosition()) + ":" + bank_spin.getSelectedItemPosition());
                    }
                    if(!TextUtils.isEmpty(branch_name))
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                edt_dep_branch.setText(branch_name);
                            }
                        },600);
                *//*if(!TextUtils.isEmpty(account_no))
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            spin_acc.setSelection(accounts.indexOf(account_no));
                        }
                    },600);*//*
                    if(!TextUtils.isEmpty(vault_name))
                        vault_spin.setSelection(vault_arls.indexOf(vault_name));
                    //Log.v("EditPayment","vault_name:"+vault_name);

                    dep_amount_txt.setText(dep_amount);
                    // layout3.setVisibility(View.VISIBLE);

                    if(deno_stat==1&&count==cur_pos&&cur_pos!=1)
                    {
                        for(int i=0;i<deno20_arls.size();i++)
                            //Log.v("EditPayment","deno"+den1000_arls.get(i));

                        lin_deno.setVisibility(View.VISIBLE);
                        try {
                            // final_cur_pos--;
                            edtxtDeno1000.setText(den1000_arls.get(cur_pos - 1));
                            edtxtDeno500.setText(den500_arls.get(cur_pos - 1));
                            edtxtDeno100.setText(deno100_arls.get(cur_pos - 1));
                            edtxtDeno50.setText(den50_arls.get(cur_pos - 1));
                            edtxtDeno20.setText(deno20_arls.get(cur_pos - 1));
                            edtxtDeno10.setText(deno10_arls.get(cur_pos - 1));
                            edtxtDeno5.setText(deno5_arls.get(cur_pos - 1));
                            edtxtDenoCoins.setText(denoc_arls.get(cur_pos - 1));
                        }catch (Exception e){e.printStackTrace();}
                    }
*/
            }
        } else if (cur_pos == count) {
            submit_btn.setText("Submit");
            final_cur_pos--;
            lin_bank.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.VISIBLE);
            //  layout2.setVisibility(View.VISIBLE);
            lin_dep.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(dep_type))
                bvType_spin.setSelection(type_arls.indexOf(dep_type));
            //Log.v("EditPayment","dep_type:"+dep_type+":"+account_no+":"+branch_name);
            if (!TextUtils.isEmpty(bank_name)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bank_spin.setSelection(bank_arls.indexOf(bank_name));

                    }
                }, 200);
                if (!TextUtils.isEmpty(branch_name))
                    edt_dep_branch.setText(branch_name);

                // bankSpin_adap.notifyDataSetChanged();
                //bank_spin.setSelection(5,true);

//                   //Log.v("EditPayment", "bank select count:" + bank_arls.get(bank_spin.getSelectedItemPosition()) + ":" + bank_spin.getSelectedItemPosition());
            }
            if (!TextUtils.isEmpty(account_no))
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spin_branch.setSelection(accounts.indexOf(account_no));

                    }
                }, 400);
                /*if(!TextUtils.isEmpty(account_no))
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            spin_acc.setSelection(accounts.indexOf(account_no));
                        }
                    },600);*/
            if (!TextUtils.isEmpty(vault_name))
                vault_spin.setSelection(vault_arls.indexOf(vault_name));
            //Log.v("EditPayment","vault_name:"+vault_name);

            dep_amount_txt.setText(dep_amount);
            // layout3.setVisibility(View.VISIBLE);

            if (deno_stat == 1 && count == cur_pos && cur_pos != 1) {
                for (int i = 0; i < deno20_arls.size(); i++)
                    //Log.v("EditPayment","deno"+den1000_arls.get(i));

                    lin_deno.setVisibility(View.VISIBLE);
                try {
                    // final_cur_pos--;
                    edtxtDeno2000.setText(den2000_arls.get(cur_pos - 1));
                    edtxtDeno1000.setText(den1000_arls.get(cur_pos - 1));
                    edtxtDeno500.setText(den500_arls.get(cur_pos - 1));
                    edtxtDeno100.setText(deno100_arls.get(cur_pos - 1));
                    edtxtDeno50.setText(den50_arls.get(cur_pos - 1));
                    edtxtDeno20.setText(deno20_arls.get(cur_pos - 1));
                    edtxtDeno10.setText(deno10_arls.get(cur_pos - 1));
                    edtxtDeno5.setText(deno5_arls.get(cur_pos - 1));
                    edtxtDenoCoins.setText(denoc_arls.get(cur_pos - 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void submit_updates() {
        //Log.v("EditPayment","beg_den1000_arls:"+den1000_arls.get(final_cur_pos)+":final_cur_pos:"+final_cur_pos);
        cc_arls.set(final_cur_pos, edt_spin.getText().toString());
        if (!TextUtils.isEmpty(pickup_amount_txt.getText().toString()))
            pick_arls.set(final_cur_pos, pickup_amount_txt.getText().toString());
        else
            pis_arls.set(final_cur_pos, "0");
        if (!TextUtils.isEmpty(req_edt.getText().toString()))
            req_amount = req_edt.getText().toString();
        else
            req_amount = "0";
        if (!TextUtils.isEmpty(dep_no_txt.getText().toString()))
            dep_no_arls.set(final_cur_pos, dep_no_txt.getText().toString());
        else
            dep_no_arls.set(final_cur_pos, "0");
        if (!TextUtils.isEmpty(pis_no_txt.getText().toString()))
            pis_arls.set(final_cur_pos, pis_no_txt.getText().toString());
        else
            pis_arls.set(final_cur_pos, "0");
        if (!TextUtils.isEmpty(hci_no_txt.getText().toString()))
            hci_arls.set(final_cur_pos, hci_no_txt.getText().toString());
        else
            hci_arls.set(final_cur_pos, "0");
        if (!TextUtils.isEmpty(rec_no_txt.getText().toString()))
            rec_arls.set(final_cur_pos, rec_no_txt.getText().toString());
        else
            rec_arls.set(final_cur_pos, "0");
        if (!TextUtils.isEmpty(dep_no_txt.getText().toString()))
            dep_no_arls.set(final_cur_pos, dep_no_txt.getText().toString());
        else
            dep_no_arls.set(final_cur_pos, "0");

        // for(int i=0;i<pick_arls.size();i++)
        //Log.v("EditPayment","den1000_arls:"+den1000_arls.get(final_cur_pos)+":final_cur_pos:"+final_cur_pos);


        //   if()
    }

    public void calc_denom() {
        if (den1000_arls.size() > 0) {
            if (!TextUtils.isEmpty(edtxtDeno1000.getText().toString()))
                den1000_arls.set(final_cur_pos, edtxtDeno1000.getText().toString());
            else
                den1000_arls.set(final_cur_pos, "0");
            if (!TextUtils.isEmpty(edtxtDeno500.getText().toString()))
                den500_arls.set(final_cur_pos, edtxtDeno500.getText().toString());
            else
                den500_arls.set(final_cur_pos, "0");
            if (!TextUtils.isEmpty(edtxtDeno100.getText().toString()))
                deno100_arls.set(final_cur_pos, edtxtDeno100.getText().toString());
            else
                deno100_arls.set(final_cur_pos, "0");
            if (!TextUtils.isEmpty(edtxtDeno50.getText().toString()))
                den50_arls.set(final_cur_pos, edtxtDeno50.getText().toString());
            else
                den50_arls.set(final_cur_pos, "0");
            if (!TextUtils.isEmpty(edtxtDeno20.getText().toString()))
                deno20_arls.set(final_cur_pos, edtxtDeno20.getText().toString());
            else
                deno20_arls.set(final_cur_pos, "0");
            if (!TextUtils.isEmpty(edtxtDeno5.getText().toString()))
                deno5_arls.set(final_cur_pos, edtxtDeno5.getText().toString());
            else
                deno5_arls.set(final_cur_pos, "0");
            if (!TextUtils.isEmpty(edtxtDenoCoins.getText().toString()))
                denoc_arls.set(final_cur_pos, edtxtDenoCoins.getText().toString());
            else
                denoc_arls.set(final_cur_pos, "0");
        }
    }

    public void get_finalAmt() {

        trans_param = "";
        deno = "";
        for (int i = 0; i < count; i++) {
            trans_param = trans_param + pick_arls.get(i) + "|"
                    + dep_no_arls.get(i) + "|" + pis_arls.get(i)
                    + "|" + hci_arls.get(i) + "|"
                    + rec_arls.get(i) + "|" + cc_arls.get(i) + "|" + irec_arls.get(i);

            if (den1000_arls.size() > 0) {
                /*if(i==0)
                    deno="|";*/

                deno = deno + den2000_arls.get(i) + "|" + den1000_arls.get(i) + "|" + den500_arls.get(i) + "|" + deno100_arls.get(i)
                        + "|" + den50_arls.get(i) + "|" + deno20_arls.get(i) + "|" + deno10_arls.get(i) + "|"
                        + deno5_arls.get(i) + "|" + denoc_arls.get(i);
            }
            if ((i + 1) != count) {
                trans_param = trans_param + "^";
                deno = deno + "^";
            }
        }
        if (bank_spin.getAdapter() != null)
            if (bvType_spin.getSelectedItemPosition() != 0)
                dep_type = type_arls.get(bvType_spin.getSelectedItemPosition());
        if (bank_arls.size() > 0)
            if (bank_spin.getSelectedItemPosition() > 0)
                bank_name = bank_arls.get(bank_spin.getSelectedItemPosition());
        if (vault_arls.size() > 0)
            if (vault_spin.getSelectedItemPosition() > 0)
                vault_name = vault_arls.get(vault_spin.getSelectedItemPosition());

        if (dep_type.equalsIgnoreCase("Vault")) {
            bank_name = "";
            account_no = "";
            branch_name = "";
        } else
            vault_name = "";
        if (accounts.size() != 0)
            if (!TextUtils.isEmpty(accounts.get(spin_branch.getSelectedItemPosition())))
                account_no = acc_ids.get(spin_branch.getSelectedItemPosition());
            else
                account_no = "";
        if (!TextUtils.isEmpty(edt_dep_branch.getText().toString()))
            branch_name = edt_dep_branch.getText().toString(); //branches.get(spin_branch.getSelectedItemPosition());
        else
            branch_name = "";
        /*if(!TextUtils.isEmpty(edt_branch.getText().toString()))
            branch_name =   edt_branch.getText().toString();
        else
            branch_name =   "";
        if(!TextUtils.isEmpty(edt_acc.getText().toString()))
            account_no  =   edt_acc.getText().toString();
        else
            account_no  =   "";*/

        //Log.v("EditPayment","transparam:"+trans_param);
        //Log.v("EditPayment","deno:"+deno);
        nameValuePairs.add(new BasicNameValuePair("opt", "rec_info"));
        nameValuePairs.add(new BasicNameValuePair("type", type));
        nameValuePairs.add(new BasicNameValuePair("ce_id", ce_id));
        nameValuePairs.add(new BasicNameValuePair("trans_id", trans_id));
        // //Log.e("test", "" + strNoTrans);
        nameValuePairs.add(new BasicNameValuePair("no_recs", String
                .valueOf(count)));
        // //Log.e("test1", Strtrans_param);
        nameValuePairs.add(new BasicNameValuePair("trans_param", trans_param));
        //   //Log.e("test2", strdenoParam);
        nameValuePairs.add(new BasicNameValuePair("deno", deno));
        nameValuePairs.add(new BasicNameValuePair("dep_type", dep_type));
        nameValuePairs.add(new BasicNameValuePair("bank_name", bank_name));
        nameValuePairs.add(new BasicNameValuePair("branch_name", branch_name));
        nameValuePairs.add(new BasicNameValuePair("account_no", account_no));
        nameValuePairs.add(new BasicNameValuePair("vault_name", vault_name));
        nameValuePairs.add(new BasicNameValuePair("dep_amount", dep_amount));
        nameValuePairs.add(new BasicNameValuePair("rec_status", rec_status));

        nameValuePairs.add(new BasicNameValuePair("remarks", remarks));
        nameValuePairs.add(new BasicNameValuePair("device_id", device_id));
        nameValuePairs.add(new BasicNameValuePair("client_code", client_code));
        nameValuePairs.add(new BasicNameValuePair("deposit_slip_no", deposit_slip_no));
        nameValuePairs.add(new BasicNameValuePair("final", "1"));
        new Submit().execute();
    }

    class Submit extends AsyncTask<Void, Void, Void> {
        String result = "";
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditPayment.this);
            progressDialog.setMessage("Loading, Please wait ....");
            if (!progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jsonObject != null && jsonObject.getString("status").equalsIgnoreCase("success"))
                    Toast.makeText(EditPayment.this, "Updation Success", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(EditPayment.this, "Can't Update Now, Please Enter New Receipt", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpParams params = new BasicHttpParams();

                HttpConnectionParams.setTcpNoDelay(params, true);

                HttpClient httpclient = new DefaultHttpClient(params);

                HttpPost httppost = new HttpPost(Config.url1);

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                result = EntityUtils.toString(httpEntity);
                if (Config.DEBUG) {
                    //Log.d("EditReceipt", "Result : " + result);
                }
                jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    startActivity(new Intent(EditPayment.this, Home.class).putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
                //Log.v("EditPayment","Submit Response:"+jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            return null;
        }
    }

    public void txt_change() {
        //Log.v("EditPayment","final_cur_pos_edt"+final_cur_pos);

        edtxtDeno2000.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                den2000_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtxtDeno1000.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                den1000_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno500.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                den500_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno100.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                deno100_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                den50_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                deno20_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                deno10_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                deno5_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDenoCoins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                denoc_arls.set(final_cur_pos, charSequence.toString());
                deno_calc();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        req_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pickup_amount_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                pick_arls.set(final_cur_pos, charSequence.toString());
                try {
                    if (!TextUtils.isEmpty(pickup_amount_txt.getText().toString()) && !pickup_amount_txt.getText().toString().equals("0")) {
                        diff_amount = 0 - Integer.parseInt(pickup_amount_txt.getText().toString());
                        diff_no_txt.setText(String.valueOf(diff_amount));
                        pickup_amount1 = String.valueOf(-diff_amount);
                        if (deno_stat == 0) {
                            diff_no_txt.setText("0");
                            diff_amount = 0;
                        } else if (deno_stat == 1 && cur_pos < count) {
                            diff_no_txt.setText("0");
                            diff_amount = 0;
                        } else if (deno_stat == 1 && cur_pos == count) {
                            //   //Log.v("ReceivePayment","list of pickup amount size"+listPickupAmout.size());
                            for (int cou = 0; cou < pick_arls.size(); cou++) {
                                pickup_amount = String.valueOf(Integer.parseInt(pick_arls.get(cou)) + Integer.parseInt(pickup_amount_txt.getText().toString()));
                                diff_amount = -Integer.parseInt(pick_arls.get(cou)) + diff_amount;
                                diff_no_txt.setText(String.valueOf(diff_amount));
                                pickup_amount1 = String.valueOf(-diff_amount);
                                //Log.v("ReceivePayment","list of pickup amount"+pick_arls.get(cou)+":"+pickup_amount);
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dep_no_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                dep_no_arls.set(final_cur_pos, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pis_no_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                pis_arls.set(final_cur_pos, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        hci_no_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                hci_arls.set(final_cur_pos, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rec_no_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                rec_arls.set(final_cur_pos, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        bank_dep_slip_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                deposit_slip_no = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dep_amount_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                dep_amount = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void deno_calc() {
        if (!TextUtils.isEmpty(edtxtDenoCoins.getText().toString()))
            diff_denocoins = Integer.parseInt(edtxtDenoCoins.getText().toString());
        else
            diff_denocoins = 0;
        if (!TextUtils.isEmpty(edtxtDeno5.getText().toString()))
            diff_deno5 = Integer.parseInt(edtxtDeno5.getText().toString()) * 5;
        else
            diff_deno5 = 0;
        if (!TextUtils.isEmpty(edtxtDeno10.getText().toString()))
            diff_deno10 = Integer.parseInt(edtxtDeno10.getText().toString()) * 10;
        else
            diff_deno10 = 0;
        if (!TextUtils.isEmpty(edtxtDeno20.getText().toString()))
            diff_deno20 = Integer.parseInt(edtxtDeno20.getText().toString()) * 20;
        else
            diff_deno20 = 0;
        if (!TextUtils.isEmpty(edtxtDeno50.getText().toString()))
            diff_deno50 = Integer.parseInt(edtxtDeno50.getText().toString()) * 50;
        else
            diff_deno50 = 0;
        if (!TextUtils.isEmpty(edtxtDeno100.getText().toString()))
            diff_deno100 = Integer.parseInt(edtxtDeno100.getText().toString()) * 100;
        else
            diff_deno100 = 0;
        if (!TextUtils.isEmpty(edtxtDeno500.getText().toString()))
            diff_deno500 = Integer.parseInt(edtxtDeno500.getText().toString()) * 500;
        else
            diff_deno500 = 0;
        if (!TextUtils.isEmpty(edtxtDeno1000.getText().toString()))
            diff_deno1000 = Integer.parseInt(edtxtDeno1000.getText().toString()) * 1000;
        else
            diff_deno1000 = 0;
        if (!TextUtils.isEmpty(edtxtDeno2000.getText().toString()))
            diff_deno2000 = Integer.parseInt(edtxtDeno2000.getText().toString()) * 2000;
        else
            diff_deno2000 = 0;
        if (!TextUtils.isEmpty(pickup_amount1))
            diff_amount = -Integer.parseInt(pickup_amount1) + diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
        diff_no_txt.setText(String.valueOf(diff_amount));
    }
    //satz
//    public void datetime()
//    {
//        final Calendar c = Calendar.getInstance();
//        mHour = c.get(Calendar.HOUR_OF_DAY);
//        mMinute = c.get(Calendar.MINUTE);
//        TimePickerDialog tpd = new TimePickerDialog(this,
//                new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay,int minute)
//                    {
//                                pickup_time.setText(""+hourOfDay+" : "+minute);
//                    }
//                }, mHour, mMinute, false);
//        tpd.show();
//    }
    //
}
