package com.mountfox.Delivery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.mountfox.Config;
import com.mountfox.ExceptionHandler;
import com.mountfox.GPSTracker;
import com.mountfox.Home;
import com.mountfox.Login;
import com.mountfox.ModeOfTransactionActivity;
import com.mountfox.PreferenceHelper;
import com.mountfox.R;
import com.mountfox.Services.dataRequest.BankAccNoListRequestData;
import com.mountfox.Services.dataRequest.DeliveryTransListRequestData;
import com.mountfox.Services.dataRequest.SubmitDeliveryDetailsRequestData;
import com.mountfox.Services.serviceRequest.ServiceRequestPOSTImpl;
import com.mountfox.Utils;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class DeliverySubmitActivity extends Activity {

    Context mContext;
    Activity activity;
    PreferenceHelper helper;
    String ce_id = "", trans_id = "", client_codecount = "", client_name = "", request_amount = "", calc_delivery_amt = "", bankType = "";
    String withdrawStatus = "", req_amt = "", delivery_amt = "", tot_diff_amt = "", delivery_to = "", recp_no = "", recp_status = "", ce_name = "",
            ref_no = "", remarkType = "", additionalRemark = "", acc_type = "", acc_no = "", bank_name = "", branch_name = "", accId = "", cheque_no = "",
            cheque_amt = "", delivery_time = "", withdraw_time = "", denominations = "", deno_diff_no = "", currentDate = "", currentTime = "", device_id = "",
            IMEI_no = "", s_latitude = "", s_longitude = "";
    String hReqAmt = "", hDeliveryAmt = "", hDiffAmt = "", hDeliveryTo = "", hDeliveryTime = "", hRecptNo = "", hRecpStatus = "", hCEName = "", hRefNo = "", hChequeNo = "",
            hChequeAmount = "", hWithdrawTime = "";
    private CheckBox deliveryType_checkBox;
    private LinearLayout layout_receiptNo, layout_ReceiptStatus, layoutDeliveryTime, layout_RefNo, layout_cheque, layout_bankDetails;
    private EditText et_req_amount_txt, delivery_amount_txt, et_AccountNo_delivery, et_BankName_delivery, et_BranchName_delivery, et_chequeNo,
            et_WithdrawAmount, et_DeliveryTime, et_WithDrawTime, deno_diff_no_txt, Total_difference_amount_txt, et_deliveryTo, et_receiptNo, et_ReceiptStatus, et_Ce_name,
            et_RefNo, et_additional_remarks, edtxtDeno1000, edtxtDeno2000, edtxtDeno200, edtxtDeno500, edtxtDeno100, edtxtDeno50, edtxtDeno20, edtxtDeno10,
            edtxtDeno5, edtxtDenoCoins;
    private TextView tvClientNameHeader;
    private Spinner bankType_spin, remarks_type;
    private AutoCompleteTextView account_autocomplete;
    ImageView img_ClearAutoText;
    private Button btSubmit;
    int diff_amount = 0, diff_deno2000 = 0, diff_deno200 = 0, diff_deno1000 = 0, diff_deno500 = 0,
            diff_deno100 = 0, diff_deno50 = 0, diff_deno20 = 0, diff_deno10 = 0, diff_deno5 = 0, diff_denocoins = 0;
    int strDeno2000, strDeno200, strDeno1000, strDeno500, strDeno100, strDeno50, strDeno20, strDeno10, strDeno5, strDenoCoins;

    private TelephonyManager telephonyManager;
    GPSTracker gpsTracker;
    double d_latitude = 12.982733625, d_longitude = 80.252031675;
//    int lat = 1, lon = 2;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_delivery_submit);
        mContext = this;
        activity = this;
        helper = new PreferenceHelper();
        gpsTracker = new GPSTracker(this);
        init_Components();
        ce_id =  Login.ce_id_main;
        request_amount = getIntent().getStringExtra("request_amount");
        if (request_amount.isEmpty())
            request_amount = 0 + "";

        trans_id = getIntent().getStringExtra("trans_id");
        client_name = getIntent().getStringExtra("client_name");
        client_codecount = getIntent().getStringExtra("client_codecount");
        bankType = getIntent().getStringExtra("DepType");

        et_req_amount_txt.setText("Request Amount - " + request_amount);
        tvClientNameHeader.setText(client_name);

        deliveryType_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    layout_cheque.setVisibility(View.VISIBLE);
                else {
                    layout_cheque.setVisibility(View.GONE);
                    withdrawStatus = "false";
                    acc_type = "";
                    accId = "";
                    acc_no = "";
                    bank_name = "";
                    branch_name = "";
                    cheque_no = "";
                    cheque_amt = "";
                    withdraw_time = "";

                    account_autocomplete.setText("");
                    et_BankName_delivery.setText("");
                    et_BranchName_delivery.setText("");
                    et_AccountNo_delivery.setText("");
                    et_chequeNo.setText("");
                    et_WithdrawAmount.setText("");
                    et_WithDrawTime.setText("");
                }
            }
        });

        List<String> depositTypeList = new ArrayList<String>();
        depositTypeList.add(0, "--Select Deposit Type--");
        depositTypeList.add("Burial");
        depositTypeList.add("Partner Bank");
        depositTypeList.add("Client Bank");
        depositTypeList.add("Vault");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.simple_spinner_item, depositTypeList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, DeliveryList);
        bankType_spin.setAdapter(dataAdapter);

        for (int i = 0; i < depositTypeList.size(); i++) {
            if (depositTypeList.get(i).equals(bankType)) {
                bankType_spin.setSelection(i);
                bankType_spin.setEnabled(false);
            }
        }

        bankType_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (bankType_spin.getSelectedItemPosition() != 0) {
                if (position == 0) {
                    layout_bankDetails.setVisibility(View.VISIBLE);
                    clear();
//                    Toast.makeText(mContext,"Select bank type",Toast.LENGTH_LONG).show();
                } else if (position == 4) {
                    layout_bankDetails.setVisibility(View.GONE);
                    clear();
                } else {
                    layout_bankDetails.setVisibility(View.VISIBLE);
                    bankType = bankType_spin.getSelectedItem().toString();
                    GetAccountDetailsAsyncTask getAccountDetailsAsyncTask = new GetAccountDetailsAsyncTask();
                    getAccountDetailsAsyncTask.execute();
                    try {
                        JSONArray ja = new JSONArray();
                        List<String> accList;
                        switch (bankType) {
                            case "Burial":
                                ja = new JSONArray(helper.getBurialList(mContext));
                                break;
                            case "Client Bank":
                                ja = new JSONArray(helper.getCbList(mContext));
                                break;
                            case "Partner Bank":
                                ja = new JSONArray(helper.getPbList(mContext));
                                break;
                        }

                        accList = new ArrayList<String>();
                        for (int i = 0; i < ja.length(); i++) {
                            accList.add(ja.getString(i));
                        }

//            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, accList);
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, accList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, remarksList);
                        account_autocomplete.setThreshold(1);
                        account_autocomplete.setAdapter(dataAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        account_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("========>>", account_autocomplete.getText().toString());

                String bankDetails = account_autocomplete.getText().toString();
                String[] separated = bankDetails.split(",");

                et_BankName_delivery.setText(separated[0]);
                et_BranchName_delivery.setText(separated[1]);
                et_AccountNo_delivery.setText(separated[2]);
                accId = separated[3];
            }
        });

        try {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
          //  device_id = telephonyManager.getDeviceId();
            device_id = "";
            IMEI_no = telephonyManager.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        account_autocomplete.setText("");
        et_BankName_delivery.setText("");
        et_BranchName_delivery.setText("");
        et_AccountNo_delivery.setText("");
        accId = "";
    }

    private void init_Components() {
        tvClientNameHeader = (TextView) findViewById(R.id.tvClientNameHeader);
        deliveryType_checkBox = (CheckBox) findViewById(R.id.deliveryType_checkBox);
        et_req_amount_txt = (EditText) findViewById(R.id.req_amount_delivery);
        delivery_amount_txt = (EditText) findViewById(R.id.delivery_amount_delivery);
        Total_difference_amount_txt = (EditText) findViewById(R.id.Total_difference_amount_delivery);
        et_deliveryTo = (EditText) findViewById(R.id.et_deliveryTo_delivery);
        et_receiptNo = (EditText) findViewById(R.id.et_receiptNo_delivery);
        layout_ReceiptStatus = (LinearLayout) findViewById(R.id.layout_ReceiptStatus);
        et_ReceiptStatus = (EditText) findViewById(R.id.et_ReceiptStatus_delivery);
        layoutDeliveryTime = (LinearLayout) findViewById(R.id.layoutDeliveryTime);
        layout_receiptNo = (LinearLayout) findViewById(R.id.layout_receiptNo_delivery);
        et_Ce_name = (EditText) findViewById(R.id.et_Ce_name_delivery);
        layout_RefNo = (LinearLayout) findViewById(R.id.layout_RefNo);
        et_RefNo = (EditText) findViewById(R.id.et_RefNo_delivery);
        remarks_type = (Spinner) findViewById(R.id.remarks_type_spin_delivery);
        et_additional_remarks = (EditText) findViewById(R.id.et_additional_remarks_delivery);

        layout_cheque = (LinearLayout) findViewById(R.id.layout_cheque);
        bankType_spin = (Spinner) findViewById(R.id.BankType_spin);
        layout_bankDetails = (LinearLayout) findViewById(R.id.layout_bankDetails);
        account_autocomplete = (AutoCompleteTextView) findViewById(R.id.accounts_autocomplete);
        img_ClearAutoText = (ImageView) findViewById(R.id.img_ClearAutoText);
        et_AccountNo_delivery = (EditText) findViewById(R.id.et_AccountNo_delivery);
        et_BankName_delivery = (EditText) findViewById(R.id.et_BankName_delivery);
        et_BranchName_delivery = (EditText) findViewById(R.id.et_BranchName_delivery);
        et_chequeNo = (EditText) findViewById(R.id.et_chequeNo_delivery);
        et_WithdrawAmount = (EditText) findViewById(R.id.et_WithdrawAmount_delivery);
        et_DeliveryTime = (EditText) findViewById(R.id.et_DeliveryTime_delivery);
        et_WithDrawTime = (EditText) findViewById(R.id.et_WithDrawTime_delivery);

        edtxtDeno2000 = (EditText) findViewById(R.id.deno_2000_D);
        edtxtDeno1000 = (EditText) findViewById(R.id.deno_1000_D);
        edtxtDeno500 = (EditText) findViewById(R.id.deno_500_D);
        edtxtDeno200 = (EditText) findViewById(R.id.deno_200_D);
        edtxtDeno100 = (EditText) findViewById(R.id.deno_100_D);
        edtxtDeno50 = (EditText) findViewById(R.id.deno_50_D);
        edtxtDeno20 = (EditText) findViewById(R.id.deno_20_D);
        edtxtDeno10 = (EditText) findViewById(R.id.deno_10_D);
        edtxtDeno5 = (EditText) findViewById(R.id.deno_5_D);
        edtxtDenoCoins = (EditText) findViewById(R.id.deno_coins_D);
        deno_diff_no_txt = (EditText) findViewById(R.id.pick_diff_amount_D);
        btSubmit = (Button) findViewById(R.id.submit_btn_delivery);

        btSubmit.setOnClickListener(myClickhandler);
        img_ClearAutoText.setOnClickListener(myClickhandler);
        et_DeliveryTime.setOnClickListener(myClickhandler);
        et_WithDrawTime.setOnClickListener(myClickhandler);

        delivery_amount_txt.addTextChangedListener(new myTextWatcher(delivery_amount_txt));
        edtxtDeno2000.addTextChangedListener(new myTextWatcher(edtxtDeno2000));
        edtxtDeno1000.addTextChangedListener(new myTextWatcher(edtxtDeno1000));
        edtxtDeno500.addTextChangedListener(new myTextWatcher(edtxtDeno500));
        edtxtDeno200.addTextChangedListener(new myTextWatcher(edtxtDeno200));
        edtxtDeno100.addTextChangedListener(new myTextWatcher(edtxtDeno100));
        edtxtDeno50.addTextChangedListener(new myTextWatcher(edtxtDeno50));
        edtxtDeno20.addTextChangedListener(new myTextWatcher(edtxtDeno20));
        edtxtDeno10.addTextChangedListener(new myTextWatcher(edtxtDeno10));
        edtxtDeno5.addTextChangedListener(new myTextWatcher(edtxtDeno5));
        edtxtDenoCoins.addTextChangedListener(new myTextWatcher(edtxtDenoCoins));
//        account_autocomplete.addTextChangedListener(new myTextWatcher(account_autocomplete));

        GetDeliveryHintNameAsyncTask getDeliveryHintNameAsyncTask = new GetDeliveryHintNameAsyncTask();
        getDeliveryHintNameAsyncTask.execute();
    }

    View.OnClickListener myClickhandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submit_btn_delivery:
                    if (Utils.isInternetConnection(mContext)) {
                        if (getAllDatasToSubmit()) {

                            if (deliveryType_checkBox.isChecked()) {
                                withdrawStatus = "true";
                                if (bankType_spin.getSelectedItemPosition() == 0) {
                                    showToast("Select deposit type");
                                    return;
                                }
                                if (bankType_spin.getSelectedItemPosition() != 4 && acc_no.length() == 0) {
                                    if (acc_no.isEmpty())
                                        showToast("Select the account number");
                                    return;
                                }
                                if (cheque_no.isEmpty()) {
                                    if (!hChequeNo.isEmpty())
                                        showToast("Enter the " + hChequeNo);
                                    else showToast("Enter the Cheque No.");
                                    return;
                                }
                                if (cheque_amt.isEmpty()) {
                                    if (!hChequeAmount.isEmpty())
                                        showToast("Enter the " + hChequeAmount);
                                    else
                                        showToast("Enter the Withdraw Amount");
                                    return;
                                }
                                if (withdraw_time.isEmpty()) {
                                    if (!hWithdrawTime.isEmpty())
                                        showToast("Enter the " + hWithdrawTime);
                                    else
                                        showToast("Enter the Withdraw Time");
                                    return;
                                }
                            } else {
                                withdrawStatus = "false";
//                                acc_type = "";
                                accId = "";
                                acc_no = "";
                                bank_name = "";
                                branch_name = "";
                                cheque_no = "";
                                cheque_amt = "";
                                withdraw_time = "";
                            }
                            if (!deno_diff_no.equals("0")) {
                                showToast("Denomination mismatch");
                                return;
                            } else {
                                if (gpsTracker.canGetLocation()) {

                                    d_latitude = gpsTracker.getLatitude();
                                    d_longitude = gpsTracker.getLongitude();
//                                    lat = (int) (d_latitude * 1E6);
//                                    lon = (int) (d_longitude * 1E6);
                                    System.out.println("Lat d, Lng d >>>" + d_latitude + ", " + d_longitude);
                                    s_latitude = d_latitude + "";
                                    s_longitude = d_longitude + "";
                                    System.out.println("Lat S, Lng S >>>" + s_latitude + ", " + s_latitude);

//                                submitLayout();
                                    confirmationLayout();
//                                    SubmitDeliveryDetailsAsyncTask submitDeliveryDetailsAsyncTask = new SubmitDeliveryDetailsAsyncTask();
//                                    submitDeliveryDetailsAsyncTask.execute();
                                } else {
                                    gpsTracker.showSettingsAlert();
                                    Toast.makeText(getApplicationContext(),
                                            "Please enable the Location Service(GPS)for view transactions",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    } else
                        Toast.makeText(mContext, "Check your Internet connection", Toast.LENGTH_LONG).show();

                    break;
                case R.id.img_ClearAutoText:
                    clear();
                    break;
                case R.id.et_WithDrawTime_delivery:
                    ShowTimePicker(et_WithDrawTime);
                    break;
                case R.id.et_DeliveryTime_delivery:
                    ShowTimePicker(et_DeliveryTime);
                    break;

            }
        }
    };

    private void ShowTimePicker(final TextView txView) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//                    sdf.parse(selectedFromTime);

            hour = sdf.getCalendar().getTime().getHours();
            minute = sdf.getCalendar().getTime().getMinutes();

        } catch (Exception exp) {
            exp.printStackTrace();
        }

        mTimePicker = new TimePickerDialog(mContext, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                timePicker.setIs24HourView(false);
                String NoonFormat = "";
                String selectedTime = getTime(selectedHour, selectedMinute);

                if (selectedTime != null && selectedTime.length() > 0) {
                    if (selectedTime.toLowerCase().contains("a")) {
                        NoonFormat = "AM";
                    } else if (selectedTime.toLowerCase().contains("p")) {
                        NoonFormat = "PM";
                    }
                    txView.setText(selectedTime.substring(0, 5) + " " + NoonFormat);

                }
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private String getTime(int hr, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm a");
        return formatter.format(cal.getTime());
    }

    private void getDateTime() {
        Date date_time = Calendar.getInstance().getTime();
        System.out.println("Current time => " + date_time);
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("h:mm a");
        currentDate = date.format(date_time);
        currentTime = time.format(date_time);
        System.out.println("date, time >>> " + currentDate + ", " + currentTime);
    }

    private class myTextWatcher implements TextWatcher {
        private View view;

        private myTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String userInput = editable.toString();
            switch (view.getId()) {
                case R.id.delivery_amount_delivery:
                    if (!TextUtils.isEmpty(delivery_amount_txt.getText().toString()) && !delivery_amount_txt.getText().toString().equals("0")) {


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

                        if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                            diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                        else
                            diff_deno200 = 0;

                        int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;

                        diff_amount = totalDenom - Integer.parseInt(delivery_amount_txt.getText().toString());
//                    deposit_amount_total=0-Integer.parseInt(pickup_amount_txt.getText().toString());
                        deno_diff_no_txt.setText(String.valueOf(diff_amount));
//                        calc_delivery_amt = String.valueOf(-diff_amount);
                        calc_delivery_amt = delivery_amount_txt.getText().toString();

                        int tot_diff_amount = Integer.parseInt(delivery_amount_txt.getText().toString()) - Integer.parseInt(request_amount);
                        Total_difference_amount_txt.setText(String.valueOf(tot_diff_amount));
                    } else {
                        deno_diff_no_txt.setText("0");
                        Total_difference_amount_txt.setText("0");
                    }
                    break;
                case R.id.deno_2000_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_1000_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_500_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_200_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_100_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_50_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_20_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_10_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_5_D:
                    calculateDifferenceAmount();
                    break;
                case R.id.deno_coins_D:
                    calculateDifferenceAmount();
                    break;
            }
        }
    }

    private void calculateDifferenceAmount() {
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

        if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
            diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
        else
            diff_deno200 = 0;

        ////////////////////////////
        int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
        if (!TextUtils.isEmpty(calc_delivery_amt))
            diff_amount = -Integer.parseInt(calc_delivery_amt) + totalDenom;
        else diff_amount = totalDenom;
        deno_diff_no_txt.setText(String.valueOf(diff_amount));
        ////////////////////////////

//        if (!TextUtils.isEmpty(calc_delivery_amt))
//            diff_amount = -Integer.parseInt(calc_delivery_amt) + diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
//        deno_diff_no_txt.setText(String.valueOf(diff_amount));

    }

    private String getDenomationAmount() {
        String denoAmt = "";
        // test sujith..0
        if (edtxtDeno2000.getText().toString().equals(""))
            strDeno2000 = 0;
        else
            strDeno2000 = Integer.parseInt(edtxtDeno2000.getText().toString());

        if (edtxtDeno200.getText().toString().equals(""))
            strDeno200 = 0;
        else
            strDeno200 = Integer.parseInt(edtxtDeno200.getText().toString());

        if (edtxtDeno1000.getText().toString().equals(""))
            strDeno1000 = 0;
        else
            strDeno1000 = Integer.parseInt(edtxtDeno1000.getText().toString());
        if (edtxtDeno500.getText().toString().equals(""))
            strDeno500 = 0;
        else
            strDeno500 = Integer.parseInt(edtxtDeno500.getText().toString());

        if (edtxtDeno100.getText().toString().equals(""))
            strDeno100 = 0;
        else
            strDeno100 = Integer.parseInt(edtxtDeno100.getText().toString());
        if (edtxtDeno50.getText().toString().equals(""))
            strDeno50 = 0;
        else
            strDeno50 = Integer.parseInt(edtxtDeno50.getText().toString());
        if (edtxtDeno20.getText().toString().equals(""))
            strDeno20 = 0;
        else
            strDeno20 = Integer.parseInt(edtxtDeno20.getText().toString());
        if (edtxtDeno10.getText().toString().equals(""))
            strDeno10 = 0;
        else
            strDeno10 = Integer.parseInt(edtxtDeno10.getText().toString());

        if (edtxtDeno5.getText().toString().equals(""))
            strDeno5 = 0;
        else
            strDeno5 = Integer.parseInt(edtxtDeno5.getText().toString());
        if (edtxtDenoCoins.getText().toString().equals(""))
            strDenoCoins = 0;
        else
            strDenoCoins = Integer.parseInt(edtxtDenoCoins.getText().toString());

        denoAmt = strDeno2000 + "|" + strDeno1000 + "|" + strDeno500 + "|" + strDeno200 + "|" + strDeno100
                + "|" + strDeno50 + "|" + strDeno20 + "|" + strDeno10 + "|"
                + strDeno5 + "|" + strDenoCoins;


        return denoAmt;
    }

    private boolean getAllDatasToSubmit() {

//        req_amt = et_req_amount_txt.getText().toString().trim();
        req_amt = request_amount;
        delivery_amt = delivery_amount_txt.getText().toString().trim();
        if (delivery_amt.isEmpty() || delivery_amt.equals("0")) {
            showToast("Enter the " + hDeliveryAmt);
            return false;
        }
        tot_diff_amt = Total_difference_amount_txt.getText().toString().trim();
        delivery_to = et_deliveryTo.getText().toString().trim();
        if (delivery_to.isEmpty() || delivery_to.equals("0")) {
            showToast("Enter the " + hDeliveryTo);
            return false;
        }
        recp_no = et_receiptNo.getText().toString().trim();
        if (layout_receiptNo.getVisibility() == View.VISIBLE) {
            if (recp_no.isEmpty() || recp_no.equals("")) {
                showToast("Enter the " + hRecptNo);
                return false;
            }
        }

        delivery_time = et_DeliveryTime.getText().toString().trim();
        if (layoutDeliveryTime.getVisibility() == View.VISIBLE) {
            if (delivery_time.isEmpty() || delivery_time.equals("")) {
                showToast("Enter the " + hDeliveryTime);
                return false;
            }
        }
        recp_status = et_ReceiptStatus.getText().toString().trim();

//        if (!hCEName.isEmpty() && hCEName != "" && !hCEName.equals("")) {
        ce_name = et_Ce_name.getText().toString().trim();
//            if (ce_name.isEmpty() || ce_name.equals("")) {
//                showToast("Enter the " + hCEName);
//                return;
//            }
//        }

        if (!hRefNo.isEmpty() && hRefNo != "" && !hRefNo.equals("")) {
            ref_no = et_RefNo.getText().toString().trim();
            if (ref_no.isEmpty() || ref_no.equals("")) {
                showToast("Enter the " + hRefNo);
                return false;
            }
        }

        if (remarks_type.getSelectedItemPosition() == 0) {
            showToast("Select Remarks Type");
            return false;
        } else
            remarkType = remarks_type.getSelectedItem().toString();

        additionalRemark = et_additional_remarks.getText().toString().trim();
        acc_type = bankType_spin.getSelectedItem().toString();
        acc_no = et_AccountNo_delivery.getText().toString();
        bank_name = et_BankName_delivery.getText().toString();
        branch_name = et_BranchName_delivery.getText().toString();
        cheque_no = et_chequeNo.getText().toString().trim();
        cheque_amt = et_WithdrawAmount.getText().toString().trim();
        withdraw_time = et_WithDrawTime.getText().toString().trim();

        denominations = getDenomationAmount();
        deno_diff_no = deno_diff_no_txt.getText().toString().trim();
        getDateTime();
//        System.out.println("All data >>> ");
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    private class GetDeliveryHintNameAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess;
        int status;
        String statusMessage = "";
        Boolean connected = false;
        String Response = "";
        SpotsDialog progressDialog = new SpotsDialog(mContext, "Loading...");

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.isInternetConnection(mContext)) {
                connected = true;
                String URL ="";
                if(SharedPreference.getDefaults(DeliverySubmitActivity.this, ConstantValues.TAG_URLVALIDATE).equals("dontswap")){
                    URL=Config.url1;
                    DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                    requestData.setOpt("deliveryHintDetails");
                    requestData.setCe_Id(ce_id);
                    requestData.setTransactionId(trans_id);
                    Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                    System.out.println("GetDeliveryHint Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            status = js.getInt("status");
                            if (status == 0) {
                                isSuccess = true;
                            } else if (status == 1) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }

                }else if(SharedPreference.getDefaults(DeliverySubmitActivity.this, ConstantValues.TAG_URLVALIDATE).equals("swap")) {
                    URL=Config.url2;
                    DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                    requestData.setOpt("deliveryHintDetails");
                    requestData.setCe_Id(ce_id);
                    requestData.setTransactionId(trans_id);
                    Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                    System.out.println("GetDeliveryHint Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            status = js.getInt("status");
                            if (status == 0) {
                                isSuccess = true;
                            } else if (status == 1) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }
                }



            } else {
                isSuccess = false;
                connected = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (isSuccess && status == 0) {
//            if (isSuccess) {
                try {
                    JSONObject js = new JSONObject(Response);
                    JSONObject jh = js.getJSONObject("transdetails");

//        deliveryType_checkBox,et_req_amount_txt,delivery_amount_txt,Total_difference_amount_txt,et_deliveryTo,et_receiptNo,et_Ce_name,
//et_RefNo,remarks_type,et_additional_remarks, et_chequeNo,et_WithdrawAmount,et_WithDrawTime;

                    hReqAmt = jh.getString("Request_Amount");
                    hDeliveryAmt = jh.getString("Delivery_Amount");
                    hDiffAmt = jh.getString("Difference_Amount");
                    hDeliveryTo = jh.getString("Delivery_To");
                    hRecptNo = jh.getString("Cash_Receipt_No");

                    deliveryType_checkBox.setText(jh.getString("Withdraw_Status"));
                    if (!hReqAmt.isEmpty() && hReqAmt != "" && !hReqAmt.equals(""))
                        et_req_amount_txt.setHint(hReqAmt);
                    if (!hDeliveryAmt.isEmpty() && hDeliveryAmt != "" && !hDeliveryAmt.equals(""))
                        delivery_amount_txt.setHint(hDeliveryAmt);
                    if (!hDiffAmt.isEmpty() && hDiffAmt != "" && !hDiffAmt.equals(""))
                        Total_difference_amount_txt.setHint(hDiffAmt);
                    if (!hDeliveryTo.isEmpty() && hDeliveryTo != "" && !hDeliveryTo.equals(""))
                        et_deliveryTo.setHint(hDeliveryTo);
//                    if (!hRecptNo.isEmpty() && hRecptNo != "" && !hRecptNo.equals(""))
//                        et_receiptNo.setHint(hRecptNo);

//                    hRecpStatus = jh.getString("RecStatus");
                    if (!hRecptNo.isEmpty() && hRecptNo != "" && !hRecptNo.equals(""))
                        et_receiptNo.setHint(hRecptNo);//
                    else layout_receiptNo.setVisibility(View.GONE);

                    hRecpStatus = jh.getString("RecStatus");
                    if (!hRecpStatus.isEmpty() && hRecpStatus != "" && !hRecpStatus.equals(""))
                        et_ReceiptStatus.setHint(hRecpStatus);//
                    else layout_ReceiptStatus.setVisibility(View.GONE);

//                    hCEName = jh.getString("CE_Name");
//                    if (!hCEName.isEmpty() && hCEName != "" && !hCEName.equals(""))
//                        et_Ce_name.setHint(hCEName);//
//                    else layout_CeName.setVisibility(View.GONE);

                    hDeliveryTime = jh.getString("Delivery_Time");
                    if (!hDeliveryTime.isEmpty() && hDeliveryTime != "" && !hDeliveryTime.equals(""))
                        et_DeliveryTime.setHint(hDeliveryTime);//
                    else layoutDeliveryTime.setVisibility(View.GONE);

                    hRefNo = jh.getString("Ref_No");
                    if (!hRefNo.isEmpty() && hRefNo != "" && !hRefNo.equals(""))
                        et_RefNo.setHint(hRefNo);//
                    else layout_RefNo.setVisibility(View.GONE);

                    String OtherRemarks = jh.getString("Other_Remarks");
                    if (!OtherRemarks.isEmpty() && OtherRemarks != "" && !OtherRemarks.equals(""))
                        et_additional_remarks.setHint(OtherRemarks);

                    String Bank_Account_No = jh.getString("Bank_Account_No");
                    if (!Bank_Account_No.isEmpty() && Bank_Account_No != "" && !Bank_Account_No.equals(""))
                        et_AccountNo_delivery.setHint(Bank_Account_No);

                    hChequeNo = jh.getString("Cheque_No");
                    if (!hChequeNo.isEmpty())
                        et_chequeNo.setHint(hChequeNo);

                    hChequeAmount = jh.getString("Cheque_Amount");
                    if (!hChequeAmount.isEmpty())
                        et_WithdrawAmount.setHint(hChequeAmount);

                    hWithdrawTime = jh.getString("Withdraw_Time");
                    if (!hWithdrawTime.isEmpty())
                        et_WithDrawTime.setHint(hWithdrawTime);

                    JSONArray ja = js.getJSONArray("deliveryremarks");
                    List<String> remarksList = new ArrayList<String>();
                    remarksList.add("--Select Remarks Type--");
                    for (int i = 0; i < ja.length(); i++) {
                        remarksList.add(ja.getString(i));
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.simple_spinner_item, remarksList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, remarksList);
                    remarks_type.setAdapter(dataAdapter);


                    String sCeName = js.getString("CeName");
                    String sCeId = js.getString("CeID");
                    String ceName_ceId = sCeName + ", " + sCeId;

                    if (!sCeName.isEmpty() && sCeName != "" && !sCeName.equals("")) {
                        et_Ce_name.setText(ceName_ceId);//
                        et_Ce_name.setEnabled(false);
                    } else et_Ce_name.setEnabled(true);

                } catch (JSONException jsE) {

                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
            } else if (isSuccess && status == 1) {
                Toast.makeText(activity, "No Record Found", Toast.LENGTH_LONG).show();
            } else if (!isSuccess && connected) {
                Toast.makeText(activity, "Communication Failure, can't reach the host. Please Try Again!", Toast.LENGTH_LONG).show();
            } else {
                if (!connected) {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class GetAccountDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess;
        int status;
        String statusMessage = "";
        Boolean connected = false;
        String Response = "";
        SpotsDialog progressDialog = new SpotsDialog(mContext, "Loading...");

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.isInternetConnection(mContext)) {
                connected = true;
                if(SharedPreference.getDefaults(DeliverySubmitActivity.this, ConstantValues.TAG_URLVALIDATE).equals("dontswap")) {
                    String URL = Config.url1;
                    BankAccNoListRequestData requestData = new BankAccNoListRequestData();
                    requestData.setOpt("bankAccNoList");
                    requestData.setBankType(bankType);
                    requestData.setAccNo("");
                    System.out.println("GetAccDetails URL >>>" + URL);
                    System.out.println("GetAccDetails Request >>>" + requestData.constructRequestData());
                    Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                    System.out.println("GetAccDetails Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            status = js.getInt("status");
                            if (status == 0) {
                                isSuccess = true;
                            } else if (status == 1) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }
                }else if(SharedPreference.getDefaults(DeliverySubmitActivity.this, ConstantValues.TAG_URLVALIDATE).equals("swap")){
                    String URL = Config.url2;
                    BankAccNoListRequestData requestData = new BankAccNoListRequestData();
                    requestData.setOpt("bankAccNoList");
                    requestData.setBankType(bankType);
                    requestData.setAccNo("");
                    System.out.println("GetAccDetails URL >>>" + URL);
                    System.out.println("GetAccDetails Request >>>" + requestData.constructRequestData());
                    Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                    System.out.println("GetAccDetails Response >>>" + Response);
                    if (Response != null && Response.length() > 0) {
                        try {
                            JSONObject js = new JSONObject(Response);
                            status = js.getInt("status");
                            if (status == 0) {
                                isSuccess = true;
                            } else if (status == 1) {
                                isSuccess = true;
                            } else {
                                isSuccess = false;
                            }
                        } catch (JSONException jsE) {
                            isSuccess = false;//false
                            System.out.println("JSONException" + jsE.getMessage());
                            jsE.printStackTrace();
                        }
                    } else {
                        isSuccess = false;//false
                        connected = true;
                    }
                }
            } else {
                isSuccess = false;
                connected = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (isSuccess && status == 0) {
//            if (isSuccess) {
                try {
                    JSONObject js = new JSONObject(Response);
                    JSONArray ja = js.getJSONArray("transdetails");
                    List<String> accList = new ArrayList<String>();
                    for (int i = 0; i < ja.length(); i++) {
                        accList.add(ja.getString(i));
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.simple_spinner_item, accList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, remarksList);
                    account_autocomplete.setThreshold(1);
                    account_autocomplete.setAdapter(dataAdapter);


                } catch (JSONException jsE) {

                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
            } else if (isSuccess && status == 1) {
                Toast.makeText(activity, "No Record Found", Toast.LENGTH_LONG).show();
            } else if (!isSuccess && connected) {
                Toast.makeText(activity, "Communication Failure, can't reach the host. Please Try Again!", Toast.LENGTH_LONG).show();
            } else {
                if (!connected) {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class SubmitDeliveryDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess;
        int status;
        String statusMessage = "";
        Boolean connected = false;
        String Response = "";
        SpotsDialog progressDialog = new SpotsDialog(mContext, "Loading...");

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.isInternetConnection(mContext)) {
                connected = true;
               if(SharedPreference.getDefaults(DeliverySubmitActivity.this, ConstantValues.TAG_URLVALIDATE).equals("dontswap")){
                   String URL = Config.url1;
                   SubmitDeliveryDetailsRequestData requestData = new SubmitDeliveryDetailsRequestData();
                   requestData.setOpt("submitDelivery");
                   requestData.setCe_Id(ce_id);
                   requestData.setTransactionId(trans_id);
                   requestData.setClientCode(client_codecount);
                   requestData.setWithdrawStatus(withdrawStatus);
                   requestData.setReqAmt(req_amt);
                   requestData.setDeliveryAmt(delivery_amt);
                   requestData.setDifferenceAmt(tot_diff_amt);
                   requestData.setDeliveryTo(delivery_to);
                   requestData.setReceiptNo(recp_no);
                   requestData.setReceiptStatus(recp_status);
                   requestData.setCE_Name(ce_name);
                   requestData.setDeliveryTime(delivery_time);
                   requestData.setRefNo(ref_no);
                   requestData.setRemarkType(remarkType);
                   requestData.setOtherRemark(additionalRemark);
                   requestData.setAccType(acc_type);
                   requestData.setAccNo(acc_no);
                   requestData.setBankName(bank_name);
                   requestData.setBranchName(branch_name);
                   requestData.setAccId(accId);
                   requestData.setChequeNo(cheque_no);
                   requestData.setChequeAmt(cheque_amt);
                   requestData.setWithdrawTime(withdraw_time);
                   requestData.setDenominations(denominations);
                   requestData.setPickupDiffNo(deno_diff_no);
                   requestData.setCurrentDate(currentDate);
                   requestData.setCurrentTime(currentTime);
                   requestData.setDeviceId(device_id);
                   requestData.setIMEI_No(IMEI_no);
                   requestData.setLatitude(s_latitude);
                   requestData.setLongitude(s_longitude);

                   System.out.println("SubmitDelivery URL >>>" + URL);
                   System.out.println("SubmitDelivery Request >>>" + requestData.constructRequestData());

                   Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());

                   System.out.println("SubmitDelivery Response >>>" + Response);

                   if (Response != null && Response.length() > 0) {
                       try {
                           JSONObject js = new JSONObject(Response);
                           status = js.getInt("status");
                           statusMessage = js.getString("statusMessage");
                           if (status == 0) {
                               isSuccess = true;
                           } else if (status == 1) {
                               isSuccess = true;
                           } else {
                               isSuccess = false;
                           }
                       } catch (JSONException jsE) {
                           isSuccess = false;//false
                           System.out.println("JSONException" + jsE.getMessage());
                           jsE.printStackTrace();
                       }
                   } else {
                       isSuccess = false;//false
                       connected = true;
                   }
               }else if(SharedPreference.getDefaults(DeliverySubmitActivity.this, ConstantValues.TAG_URLVALIDATE).equals("swap")){
                   String URL = Config.url2;
                   SubmitDeliveryDetailsRequestData requestData = new SubmitDeliveryDetailsRequestData();
                   requestData.setOpt("submitDelivery");
                   requestData.setCe_Id(ce_id);
                   requestData.setTransactionId(trans_id);
                   requestData.setClientCode(client_codecount);
                   requestData.setWithdrawStatus(withdrawStatus);
                   requestData.setReqAmt(req_amt);
                   requestData.setDeliveryAmt(delivery_amt);
                   requestData.setDifferenceAmt(tot_diff_amt);
                   requestData.setDeliveryTo(delivery_to);
                   requestData.setReceiptNo(recp_no);
                   requestData.setReceiptStatus(recp_status);
                   requestData.setCE_Name(ce_name);
                   requestData.setDeliveryTime(delivery_time);
                   requestData.setRefNo(ref_no);
                   requestData.setRemarkType(remarkType);
                   requestData.setOtherRemark(additionalRemark);
                   requestData.setAccType(acc_type);
                   requestData.setAccNo(acc_no);
                   requestData.setBankName(bank_name);
                   requestData.setBranchName(branch_name);
                   requestData.setAccId(accId);
                   requestData.setChequeNo(cheque_no);
                   requestData.setChequeAmt(cheque_amt);
                   requestData.setWithdrawTime(withdraw_time);
                   requestData.setDenominations(denominations);
                   requestData.setPickupDiffNo(deno_diff_no);
                   requestData.setCurrentDate(currentDate);
                   requestData.setCurrentTime(currentTime);
                   requestData.setDeviceId(device_id);
                   requestData.setIMEI_No(IMEI_no);
                   requestData.setLatitude(s_latitude);
                   requestData.setLongitude(s_longitude);

                   System.out.println("SubmitDelivery URL >>>" + URL);
                   System.out.println("SubmitDelivery Request >>>" + requestData.constructRequestData());

                   Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());

                   System.out.println("SubmitDelivery Response >>>" + Response);

                   if (Response != null && Response.length() > 0) {
                       try {
                           JSONObject js = new JSONObject(Response);
                           status = js.getInt("status");
                           statusMessage = js.getString("statusMessage");
                           if (status == 0) {
                               isSuccess = true;
                           } else if (status == 1) {
                               isSuccess = true;
                           } else {
                               isSuccess = false;
                           }
                       } catch (JSONException jsE) {
                           isSuccess = false;//false
                           System.out.println("JSONException" + jsE.getMessage());
                           jsE.printStackTrace();
                       }
                   } else {
                       isSuccess = false;//false
                       connected = true;
                   }
               }
            } else {
                isSuccess = false;
                connected = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (isSuccess && status == 0) {
                Toast.makeText(activity, statusMessage, Toast.LENGTH_LONG).show();
                Intent inte = new Intent(mContext, DeliveryListActivity.class);
                inte.putExtra("ce_id", ce_id);
                inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inte);
                finish();
            } else if (isSuccess && status == 1) {
                Toast.makeText(activity, statusMessage, Toast.LENGTH_LONG).show();
            } else if (!isSuccess && connected) {
                Toast.makeText(activity, "Communication Failure, can't reach the host. Please Try Again!", Toast.LENGTH_LONG).show();
            } else {
                if (!connected) {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void submitLayout() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View view = inflater.inflate(R.layout.pop_up_delete_alert_layout, null);

            TextView heading = (TextView) view.findViewById(R.id.tvAlertHeading);
            TextView pop_msg = (TextView) view.findViewById(R.id.pop_msg);
            LinearLayout close_pop = (LinearLayout) view.findViewById(R.id.close_pop);

            TextView no = (TextView) view.findViewById(R.id.no);
            TextView yes = (TextView) view.findViewById(R.id.yes);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setView(view);

            final AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
            alert.show();
            alert.setCanceledOnTouchOutside(false);
            pop_msg.setText("Are you sure do you want to submit !");

            close_pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
//                    SubmitDeliveryDetailsAsyncTask submitDeliveryDetailsAsyncTask = new SubmitDeliveryDetailsAsyncTask();
//                    submitDeliveryDetailsAsyncTask.execute();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmationLayout() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View view = inflater.inflate(R.layout.pop_up_confirmation_alert_layout, null);

            TextView heading = (TextView) view.findViewById(R.id.tvAlertHeading);
            TextView pop_msg = (TextView) view.findViewById(R.id.pop_msg);
            LinearLayout close_pop = (LinearLayout) view.findViewById(R.id.close_pop);

            final TextView no = (TextView) view.findViewById(R.id.no);
            final TextView yes = (TextView) view.findViewById(R.id.yes);
            TextView date = (TextView) view.findViewById(R.id.tvDatePopUp);
            TextView time = (TextView) view.findViewById(R.id.tvTimePopUp);
            TextView reqAmt = (TextView) view.findViewById(R.id.tvReqAmtPopUp);
            TextView delAmt = (TextView) view.findViewById(R.id.tvDelAmtPopUp);
            TextView diffAmt = (TextView) view.findViewById(R.id.tvDiffAmtPopUp);
            TextView delTo = (TextView) view.findViewById(R.id.tvDelToPopUp);
            TextView recptNo = (TextView) view.findViewById(R.id.tvRecptNoPopUp);
            TextView remarkTyp = (TextView) view.findViewById(R.id.tvRemarkTypePopUp);

            date.setText("Current Date : " + currentDate);
            time.setText("Current Time : " + currentTime);
            if (!hReqAmt.isEmpty())
                reqAmt.setText(hReqAmt + " : " + request_amount);
            else
                reqAmt.setVisibility(View.GONE);

            if (!hDeliveryAmt.isEmpty())
                delAmt.setText(hDeliveryAmt + " : " + delivery_amt);
            else
                delAmt.setVisibility(View.GONE);

            if (!hDeliveryTo.isEmpty())
                delTo.setText(hDeliveryTo + " : " + delivery_to);
            else
                delTo.setVisibility(View.GONE);

            diffAmt.setText("Difference Amount : " + deno_diff_no);
            if (layout_receiptNo.getVisibility() == View.VISIBLE)
                recptNo.setText(hRecptNo + " : " + recp_no);
            else recptNo.setVisibility(View.GONE);
            remarkTyp.setText("Remark Type : " + remarkType);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setView(view);
            final AlertDialog alert = dialogBuilder.create();
//            alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
            pop_msg.setText("Are you sure do you want to submit !");

            close_pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    no.setBackgroundResource(R.drawable.custom_all_cornered_color_primary_bg_design);
                    yes.setBackgroundResource(R.drawable.custom_all_cornered_white_bg_design);
                    no.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    yes.setTextColor(ContextCompat.getColor(mContext, R.color.app_bg));
                    alert.dismiss();

                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    no.setBackgroundResource(R.drawable.custom_all_cornered_white_bg_design);
                    yes.setBackgroundResource(R.drawable.custom_all_cornered_color_primary_bg_design);
                    no.setTextColor(ContextCompat.getColor(mContext, R.color.app_bg));
                    yes.setTextColor(ContextCompat.getColor(mContext, R.color.white));

                    SubmitDeliveryDetailsAsyncTask submitDeliveryDetailsAsyncTask = new SubmitDeliveryDetailsAsyncTask();
                    submitDeliveryDetailsAsyncTask.execute();

                    alert.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Clearcache(Context context){
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


}
