package com.mountfox.Delhivery_Pay;

import static com.mountfox.sharedPref.ConstantValues.TAG_CE_MOBILE;
import static com.mountfox.sharedPref.ConstantValues.TAG_PHONENUMBER;
import static com.mountfox.sharedPref.ConstantValues.TAG_TRANSID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mountfox.CaptureActivityPortrait;
import com.mountfox.Delhivery_Pay.Poojo_Class.DelhiveryOtpResponse;
import com.mountfox.Delhivery_Pay.Poojo_Class.Delivery_EntryResponse;
import com.mountfox.ModeOfTransactionActivity;
import com.mountfox.R;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Delivery_EntryCash extends AppCompatActivity {
    public static final String TAG = Delivery_EntryCash.class.getSimpleName();
    ApiInterface apiInterface;

    private EditText et_managername,et_email,et_phone,et_center_name,et_center_code,et_txt_id,et_amount, ed_transactionId,ed_mode,ed_Totalamount,ed_totalAmount,ed_agentMobile ;
    private LinearLayout submit;
    private ImageView back;
    private ProgressDialog progressDialog;
    private String emp_id="",dc_id="";

    ///
    EditText   ed_2000,ed_500,ed_200,ed_100,ed_50,ed_20,ed_10,ed_5,ed_2,ed_coins,
            ed_difference;
    public int  diff_amount = 0, diff_deno2000 = 0, diff_deno200 = 0, diff_deno1000 = 0, diff_deno500 = 0,
            diff_deno100 = 0, diff_deno50 = 0, diff_deno20 = 0, diff_deno10 = 0, diff_deno5 = 0,diff_deno2 = 0, diff_denocoins = 0,totalslipcount=0;
    String pickup_amount="";
    String pickup_amount1="",ce_mobile="";

    private ImageView qr_scan;
    private TextView txt;
    private IntentIntegrator qrScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_entry_cash);
        apiInterface = Constants.getdelivery().create(ApiInterface.class);
        progressDialog = new ProgressDialog(Delivery_EntryCash.this);
        qr_scan = findViewById(R.id.qr_code);
        ed_Totalamount = findViewById(R.id.ed_depositAmount);
        ed_agentMobile = findViewById(R.id.ed_agentMobile);
        submit = findViewById(R.id.submit_entry);
        back = findViewById(R.id.entry_back);


        et_managername = findViewById(R.id.ed_managername);
        et_email = findViewById(R.id.ed_emailaddress);
        et_phone = findViewById(R.id.ed_phone_number);
        et_center_name = findViewById(R.id.ed_centername);
        et_center_code = findViewById(R.id.ed_centercode);
        et_txt_id = findViewById(R.id.ed_depositId);
        et_amount = findViewById(R.id.ed_totalAmount);
        ////////
        ed_2000 = findViewById(R.id.ed_n2000);
        ed_500 = findViewById(R.id.ed_500);
        ed_200 = findViewById(R.id.ed_200);
        ed_100 = findViewById(R.id.ed_100);
        ed_50 = findViewById(R.id.ed_50);
        ed_20 = findViewById(R.id.ed_20);
        ed_10 = findViewById(R.id.ed_10);
        ed_5 = findViewById(R.id.ed_5);
        ed_2 = findViewById(R.id.ed_2);
        ed_coins = findViewById(R.id.ed_coins);
        ed_difference = findViewById(R.id.ed_difference);
        ///////////
        qrScan = new IntentIntegrator(Delivery_EntryCash.this);
      ///  ce_mobile = SharedPreference.getDefaults(Delivery_EntryCash.this, TAG_CE_MOBILE);

      //  ed_agentMobile.setText(ce_mobile);


        ed_Totalamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                ed_totalAmount.setText(ed_Totalamount.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });




        qr_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.setPrompt("Scan a QR Code");
                qrScan.setCameraId(0); // Use a specific camera of the device
                qrScan.setOrientationLocked(true);
                qrScan.setBeepEnabled(true);
                qrScan.setCaptureActivity(CaptureActivityPortrait.class);
                qrScan.initiateScan();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Delivery_EntryCash.this, ModeOfTransactionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        /////////////


        ed_2000.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_500.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_200.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_100.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_50.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_20.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_10.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        ed_5.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_2.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_coins.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_difference.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        ed_Totalamount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {









                if (!TextUtils.isEmpty(ed_Totalamount.getText().toString()) && !ed_Totalamount.getText().toString().equals("0")) {

                    if (!TextUtils.isEmpty(ed_coins.getText().toString())){
                        diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                    } else {
                        diff_denocoins = 0;
                    }

                    if (!TextUtils.isEmpty(ed_2.getText().toString())){
                        diff_deno2 = Integer.parseInt(ed_2.getText().toString())*2;
                    } else {
                        diff_deno2 = 0;
                    }

                    if (!TextUtils.isEmpty(ed_5.getText().toString())){
                        diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                    } else{
                        diff_deno5 = 0;
                    }
                    if (!TextUtils.isEmpty(ed_10.getText().toString())){
                        diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                    } else{
                        diff_deno10 = 0;
                    }

                    if (!TextUtils.isEmpty(ed_20.getText().toString())){
                        diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                    } else{
                        diff_deno20 = 0;
                    }

                    if (!TextUtils.isEmpty(ed_50.getText().toString())){
                        diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                    }
                    else{
                        diff_deno50 = 0;
                    }

                    if (!TextUtils.isEmpty(ed_100.getText().toString())){
                        diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                    } else{
                        diff_deno100 = 0;
                    }

                    if (!TextUtils.isEmpty(ed_200.getText().toString())){
                        diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                    }
                    else{
                        diff_deno200 = 0;
                    }

                    if (!TextUtils.isEmpty(ed_500.getText().toString())){
                        diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                    }
                    else{
                        diff_deno500 = 0;
                    }

                    if (!TextUtils.isEmpty(ed_2000.getText().toString())){
                        diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                    }
                    else{
                        diff_deno2000 = 0;
                    }
                    int totalDenom = diff_deno2000 + diff_deno500  + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_deno2+diff_denocoins;
                    int changing_diff_amount = totalDenom - Integer.parseInt(ed_Totalamount.getText().toString());
                    ed_difference.setText(String.valueOf(changing_diff_amount));
                    diff_amount = 0 - Integer.parseInt(ed_Totalamount.getText().toString());
                    pickup_amount1 = String.valueOf(-diff_amount);
//                    if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("0")) {
//                        ed_difference.setText("0");
//                        diff_amount = 0;
//                    } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans < strNoTrans) {
//                        ed_difference.setText("0");
//                        diff_amount = 0;
//                    }
                } else if (TextUtils.isEmpty(ed_Totalamount.getText().toString()) || ed_Totalamount.getText().toString().equals("0")) {
                    ed_2000.setText("");
                    ed_500.setText("");
                    ed_200.setText("");
                    ed_100.setText("");
                    ed_50.setText("");
                    ed_20.setText("");
                    ed_10.setText("");
                    ed_5.setText("");
                    ed_2.setText("");
                    ed_coins.setText("");
                    ed_difference.setText("");
                    pickup_amount = "";
                    pickup_amount1 = "";
                    diff_deno2000 = 0;
                    diff_deno1000 = 0;
                    diff_deno500 = 0;
                    diff_deno200 = 0;
                    diff_deno100 = 0;
                    diff_deno50 = 0;
                    diff_deno20 = 0;
                    diff_deno10 = 0;
                    diff_deno5 = 0;
                    diff_deno2 = 0;
                    diff_denocoins = 0;
                    diff_amount = 0;
//                    if(ed_remarks.getText().toString().equals("Cash Received")||ed_remarks.getText().toString().equals("Authorised person not there")||ed_remarks.getText().toString().equals("Pickups not happened due to local issues")||ed_remarks.getText().toString().equals("Holiday")||ed_remarks.getText().toString().equals("Others")){
//                        ed_remarks.setText("");
//                        ed_others.setText("");
//                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if (strGetNoTrans == 1) {
//                    ed_Totalamount.setText(ed_Totalamount.getText().toString());
//                }
                if (ed_Totalamount.getText().toString().trim().equals("0")) {
                    ed_difference.setText("0");
//                    if(ed_remarks.getText().toString().equals("Cash Received")||ed_remarks.getText().toString().equals("Authorised person not there")||ed_remarks.getText().toString().equals("Pickups not happened due to local issues")||ed_remarks.getText().toString().equals("Holiday")||ed_remarks.getText().toString().equals("Others")){
//                        ed_remarks.setText("");
//                        ed_others.setText("");
//                    }
                }
                if (ed_Totalamount.getText().toString().trim().equals("")) {
                    ed_Totalamount.getText().clear();
//                    ed_collectedamount.getText().clear();
//                    dd_collectamount.setText("");
//                    ed_remarks.setText("");
//                    ed_others.setText("");

                }
            }
        });



/////  denomination
        ed_2000.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {



                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;

                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;

                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                 else
                    diff_deno2000 = 0;

                if (!TextUtils.isEmpty(ed_200.getText().toString())) {
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                }
                else {
                    diff_deno200 = 0;
                }

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ed_500.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {



                if (ed_500.getText().toString().length()<10){
                    Toast.makeText(Delivery_EntryCash.this, "You Reached Maximum Limit", Toast.LENGTH_SHORT).show();
                }







                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;


                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;


                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;


                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ed_200.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;



                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;



                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;


                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2+ diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ed_100.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;



                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;


                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;
                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ed_50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;



                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;






                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;
                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;
                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ed_20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;



                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;




                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;
                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ed_10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;


                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;




                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;


                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ed_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;


                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;


                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;


                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ed_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;


                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;


                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;
                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;


                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ed_coins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(ed_coins.getText().toString()))
                    diff_denocoins = Integer.parseInt(ed_coins.getText().toString());
                else
                    diff_denocoins = 0;


                if (!TextUtils.isEmpty(ed_2.getText().toString()))
                    diff_deno2 = Integer.parseInt(ed_2.getText().toString()) * 2;
                else
                    diff_deno2 = 0;


                if (!TextUtils.isEmpty(ed_5.getText().toString()))
                    diff_deno5 = Integer.parseInt(ed_5.getText().toString()) * 5;
                else
                    diff_deno5 = 0;


                if (!TextUtils.isEmpty(ed_10.getText().toString()))
                    diff_deno10 = Integer.parseInt(ed_10.getText().toString()) * 10;
                else
                    diff_deno10 = 0;
                if (!TextUtils.isEmpty(ed_20.getText().toString()))
                    diff_deno20 = Integer.parseInt(ed_20.getText().toString()) * 20;
                else
                    diff_deno20 = 0;
                if (!TextUtils.isEmpty(ed_50.getText().toString()))
                    diff_deno50 = Integer.parseInt(ed_50.getText().toString()) * 50;
                else
                    diff_deno50 = 0;
                if (!TextUtils.isEmpty(ed_100.getText().toString()))
                    diff_deno100 = Integer.parseInt(ed_100.getText().toString()) * 100;
                else
                    diff_deno100 = 0;
                if (!TextUtils.isEmpty(ed_500.getText().toString()))
                    diff_deno500 = Integer.parseInt(ed_500.getText().toString()) * 500;
                else
                    diff_deno500 = 0;
                if (!TextUtils.isEmpty(ed_2000.getText().toString()))
                    diff_deno2000 = Integer.parseInt(ed_2000.getText().toString()) * 2000;
                else
                    diff_deno2000 = 0;


                if (!TextUtils.isEmpty(ed_200.getText().toString()))
                    diff_deno200 = Integer.parseInt(ed_200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 +diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 +diff_deno2 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                ed_difference.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });




        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sname=et_managername.getText().toString();
                String semail=et_email.getText().toString();
                String sphone=et_phone.getText().toString();
                String scenter_name=et_center_name.getText().toString();
                String scenter_code=et_center_code.getText().toString();
                String stxt_id=et_txt_id.getText().toString();
                String samount=et_amount.getText().toString();
                String sdepositAmount = ed_Totalamount.getText().toString();
                String sagentMobile = ed_agentMobile.getText().toString();
                String s2000 = ed_2000.getText().toString();
                String s500 = ed_500.getText().toString();
                String s200 = ed_200.getText().toString();
                String s100 = ed_100.getText().toString();
                String s50 = ed_50.getText().toString();
                String s20 = ed_20.getText().toString();
                String s10 = ed_10.getText().toString();
                String s5 = ed_5.getText().toString();
                String s2 = ed_2.getText().toString();
                String scoin = ed_coins.getText().toString();



                if (sname.equals("") || sname.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Enter Name", Toast.LENGTH_SHORT).show();
                }
                else if (semail.equals("") || semail.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if (sphone.equals("") || sphone.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Please Enter Mobile", Toast.LENGTH_SHORT).show();
                }

                else  if (scenter_name.equals("") || scenter_name.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Enter Center Name", Toast.LENGTH_SHORT).show();
                }
                else if (scenter_code.equals("") || scenter_code.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Please Enter Center Code", Toast.LENGTH_SHORT).show();
                }
                else if (stxt_id.equals("") || stxt_id.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Please Enter Unique ID", Toast.LENGTH_SHORT).show();
                }

               else if (samount.equals("") || samount.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Enter Amount", Toast.LENGTH_SHORT).show();
                }

                else if (sdepositAmount.equals("") || sdepositAmount.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Please Enter Deposit Amount", Toast.LENGTH_SHORT).show();
                }
                else if (!samount.equals(sdepositAmount)){
                    Toast.makeText(Delivery_EntryCash.this, "Amount & Total not Same", Toast.LENGTH_SHORT).show();
                }



                else if(!ed_difference.getText().toString().equals("0")){
                    Toast.makeText(Delivery_EntryCash.this,"Please check the Denomination", Toast.LENGTH_SHORT).show();
                }
                else if (sagentMobile.equals("") || sagentMobile.isEmpty()) {
                    Toast.makeText(Delivery_EntryCash.this, "Please Enter Agent Mobile", Toast.LENGTH_SHORT).show();
                }

                else {
//
                    new onDeliveryEntryTASK(Delivery_EntryCash.this ,sname,semail,sphone,scenter_name,scenter_code,stxt_id,samount,sdepositAmount,s2000,s500,s200,s100,s50,s20,s10,s5,s2,scoin,sagentMobile).execute();
                }
            }
        });





    }


    private class onDeliveryEntryTASK extends AsyncTask<String, String, String> {
        private Activity activity;
        private String  strname,stremail,strphone,strcenter_name,strcenter_code,strtxt_id,stramount,depositAmount,str2000,str500,str200,str100,str50,str20,str10,str5,str2,strcoin,agentMobile;
        public onDeliveryEntryTASK(Activity activity, String strname, String stremail, String strphone, String strcenter_name, String strcenter_code, String strtxt_id, String stramount, String depositAmount,  String str2000, String str500, String str200, String str100, String str50, String str20, String str10, String str5, String str2, String strcoin, String agentMobile) {
            this.activity=activity;
            this.strname = strname;
            this.stremail = stremail;
            this.strphone = strphone;
            this.strcenter_name = strcenter_name;
            this.strcenter_code = strcenter_code;
            this.strtxt_id = strtxt_id;
            this.stramount = stramount;
            this.depositAmount =depositAmount;
            this.str2000 = str2000;
            this.str500 = str500;
            this.str200 = str200;
            this.str100 = str100;
            this.str50 = str50;
            this.str20 = str20;
            this.str10 = str10;
            this.str5 = str5;
            this.str2 = str2;
            this.strcoin = strcoin;
            this.agentMobile = agentMobile;

        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                JsonObject obj = new JsonObject();
                JsonObject DelhiveryBody = new JsonObject();
                JsonObject DelhiveryHeader = new JsonObject();
                JsonObject DelhiveryRequest = new JsonObject();
                DelhiveryBody.addProperty("name",strname);
                DelhiveryBody.addProperty("email",stremail);
                DelhiveryBody.addProperty("phone",strphone);
                DelhiveryBody.addProperty("center_name",strcenter_name);
                DelhiveryBody.addProperty("center_code",strcenter_code);
                DelhiveryBody.addProperty("txt_id",strtxt_id);
                DelhiveryBody.addProperty("amount",stramount);
                DelhiveryBody.addProperty("totalAmount",depositAmount);
                DelhiveryBody.addProperty("deno_2000",str2000);
                DelhiveryBody.addProperty("deno_500",str500);
                DelhiveryBody.addProperty("deno_200",str200);
                DelhiveryBody.addProperty("deno_100",str100);
                DelhiveryBody.addProperty("deno_50",str50);
                DelhiveryBody.addProperty("deno_20",str20);
                DelhiveryBody.addProperty("deno_10",str10);
                DelhiveryBody.addProperty("deno_5",str5);
                DelhiveryBody.addProperty("deno_2",str2);
                DelhiveryBody.addProperty("deno_1",strcoin);
                DelhiveryBody.addProperty("agentMobile",agentMobile);
                obj.add("DelhiveryBody",DelhiveryBody);
                DelhiveryHeader.addProperty("serviceRequestVersion","1.0");
                DelhiveryHeader.addProperty("serviceRequestId","DelhiveryQRCode");
                obj.add("DelhiveryHeader",DelhiveryHeader);
                DelhiveryRequest.add("DelhiveryRequest",obj);
                Log.e(TAG,"............Json >>"+DelhiveryRequest);

                Call<Delivery_EntryResponse> tracking = apiInterface.getdeliveryEntryCash(DelhiveryRequest);
                tracking.enqueue(new Callback<Delivery_EntryResponse>() {
                    @Override
                    public void onResponse(Call<Delivery_EntryResponse> call, Response<Delivery_EntryResponse> response) {
                        if (response.code() == 200) {
                            Log.e(TAG,"inside --->>>> "+response);
                            String stTransid=response.body().getTransactionId();
                            if (response.body().getCode().equals("001")){
                                new onDelhiveryOtpTask(activity,strphone,stTransid).execute();
                                Log.e(TAG,"strphone--->>>> "+response.body().getStatus());
                                Log.e(TAG,"secess --->>>> "+response.body().getStatus());

                            }else {
                                Log.e(TAG,"FAILURE --->>>> "+response.body().getStatus());
                                Toast.makeText(Delivery_EntryCash.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();

                            }

                        }
                        else {

                            Log.e(TAG,"outside>>>> "+response);
                            Toast.makeText(Delivery_EntryCash.this,  "Try again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }


                    }
                    @Override
                    public void onFailure(Call<Delivery_EntryResponse> call, Throwable t) {
                        progressDialog.cancel();
                        Toast.makeText(Delivery_EntryCash.this,  Objects.requireNonNull(t.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                progressDialog.cancel();
                Toast.makeText(Delivery_EntryCash.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }



///  ------------------------------------------------------------------------

    private class onDelhiveryOtpTask extends AsyncTask<String, String, String> {
        private Activity activity;
        private String strphone,transid;

        public onDelhiveryOtpTask(Activity activity, String strphone, String transid) {
            this.activity = activity;
            this.strphone = strphone;
            this.transid = transid;


        }



        @Override
        protected String doInBackground(String... strings) {
            try {

                JsonObject obj = new JsonObject();
                JsonObject DelhiveryBody = new JsonObject();
                JsonObject DelhiveryHeader = new JsonObject();
                JsonObject DelhiveryRequest = new JsonObject();
                DelhiveryBody.addProperty("phone",strphone);
                DelhiveryBody.addProperty("transactionId",transid);
                obj.add("DelhiveryBody",DelhiveryBody);
                DelhiveryHeader.addProperty("serviceRequestVersion","1.0");
                DelhiveryHeader.addProperty("serviceRequestId","DelhiveryOtp");
                obj.add("DelhiveryHeader",DelhiveryHeader);
                DelhiveryRequest.add("DelhiveryRequest",obj);
                Log.e(TAG,"DelhiveryOtp ---------- >>"+DelhiveryRequest);

                Call<DelhiveryOtpResponse> tracking = apiInterface.getdeliveryotp(DelhiveryRequest);
                tracking.enqueue(new Callback<DelhiveryOtpResponse>() {
                    @Override
                    public void onResponse(Call<DelhiveryOtpResponse> call, Response<DelhiveryOtpResponse> response) {
                        if (response.code() == 200) {
                            String phone = response.body().getStatus().getPhone();
                            String trans = response.body().getStatus().getTransactionId();



                            if (response.body().getCode().equals("003")){
                                activity.startActivity(new Intent(activity, DeliveryOtpVerify.class));
                                Log.e(TAG, "otp one>>>> " + response.body().getStatus().getOtp());
                                SharedPreference.setDefaults(Delivery_EntryCash.this, TAG_TRANSID,response.body().getStatus().getTransactionId());
                                SharedPreference.setDefaults(Delivery_EntryCash.this, TAG_PHONENUMBER,response.body().getStatus().getPhone());
                            }
                           else if (response.body().getCode().equals("006")){
                                Toast.makeText(Delivery_EntryCash.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "otp one>>>> " + response.body().getStatus().getOtp());
                            }


                            else {
                                Log.e(TAG,"FAILURE --->>>> "+response.body().getStatus());
                                Toast.makeText(Delivery_EntryCash.this, response.body().getStatus().getMsg(), Toast.LENGTH_SHORT).show();

                            }



                        } else {
                            Log.e(TAG, "outside>>>> " + response);
                            Toast.makeText(activity, "Try again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }

                    @Override
                    public void onFailure(Call<DelhiveryOtpResponse> call, Throwable t) {
                        progressDialog.cancel();
                        Toast.makeText(activity, Objects.requireNonNull(t.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                progressDialog.cancel();
                Toast.makeText(activity, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "inside----------->");
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Log.e(TAG, "Cancelled----------->");
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {

                try {


                    JSONObject js = new JSONObject(intentResult.getContents());
                     String strname = "", stremail = "", strphone = "", strcenter_name = "", strcenter_code = "", strtxt_id = "",stramount="";



                    if (js.has("name")) {
                        strname = js.getString("name");

                    }
                    if (js.has("email")) {
                        stremail = js.getString("email");

                    }
                    if (js.has("phone")) {
                        strphone = js.getString("phone");

                    }

                    if (js.has("centername")) {
                        strcenter_name = js.getString("centername");
                    }
                    if (js.has("centercode")) {
                        strcenter_code = js.getString("centercode");
                    }

                    if (js.has("txtid")) {
                        strtxt_id = js.getString("txtid");
                    }
                    if (js.has("amount")) {
                        stramount = js.getString("amount");
                    }

                    et_managername.setText(strname);
                    et_email.setText(stremail);
                    et_phone.setText(strphone);
                    et_center_name.setText(strcenter_name);
                    et_center_code.setText(strcenter_code);
                    et_txt_id.setText(strtxt_id);
                    et_amount.setText(stramount);



//                    et_managername.setText(PointdetailsclientcodeScanarray.get(0));
                    Toast.makeText(getBaseContext(), "SUCESS", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "SUCESS----------------->>>>>>"+intentResult.getContents());


                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Invalid QR ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Exception------------>>>"+e);
                    e.printStackTrace();
                }

            }
        } else {
            Toast.makeText(getBaseContext(), "Invalid QR ", Toast.LENGTH_SHORT).show();

            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}