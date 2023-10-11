package com.mountfox.Cheque_Pickup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.mountfox.Config;
import com.mountfox.ConfirmScreenListItemAdapter;
import com.mountfox.DataBeforeConformation;
import com.mountfox.Delivery.DeliverySubmitActivity;
import com.mountfox.DepositAmountDataCenter;
import com.mountfox.Deposit_amount_edit_screen;
import com.mountfox.ExceptionHandler;
import com.mountfox.GPSTracker;
import com.mountfox.Home;
import com.mountfox.ModeOfTransactionActivity;
import com.mountfox.PreferenceHelper;
import com.mountfox.R;
import com.mountfox.ReceivePayment;
import com.mountfox.Services.dataRequest.BankAccNoListRequestData;
import com.mountfox.Services.dataRequest.DeliveryTransListRequestData;
import com.mountfox.Services.dataRequest.SubmitChequePickupDetailsRequestData;
import com.mountfox.Services.dataRequest.SubmitDeliveryDetailsRequestData;
import com.mountfox.Services.serviceRequest.ServiceRequestPOSTImpl;
import com.mountfox.Transaction;
import com.mountfox.Utils;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ChequePickupSubmitActivity extends Activity {
    Context mContext;
    Activity activity;
    PreferenceHelper helper;
    private TelephonyManager telephonyManager;
    GPSTracker gpsTracker;
    double d_latitude = 12.982733625, d_longitude = 80.252031675;
//    int lat = 1, lon = 2;

    String ce_id = "", bankType = "", trans_id = "", client_codecount = "", client_name = "", clientId = "", cust_code = "", device_id = "", IMEI_no = "",
            s_latitude = "", s_longitude = "";
    int iCustCodeCount = 0;
    TextView tvClientNameHeader, tvNoOfTransaction;
    private AutoCompleteTextView account_autocomplete;
    ImageView img_ClearAutoText;
    LinearLayout layoutNoOfCheque, layoutChequeNo, layoutReceiptNo, layoutHciSlipNo, layoutChequeAmount, layoutDepositBank, layoutAccDetailsSearch, layoutAccountNo, layoutCourierSentDateAndTime,
            layoutCourierDesignation, layoutDistance, layoutCourierCharges, layoutCourierName, layoutPODno, layoutCourierStatus, layoutScanCopyStatus, layoutRemark;
    EditText etNoOfCheque, etChequeNo, etReceiptNo, etHciSlipNo, etChequeAmount, etAccountNo, etBankName, etCourierSentDateAndTime, etCourierDesignation, etDistance,
            etCourierCharges, etCourierName, etPODno, etCourierStatus, etScanCopyStatus, etRemarks;
    Button submitBtn;
    String hNoOfCheque = "", hChequeNo = "", hReceiptNo = "", hHciSlipNo = "", hChequeAmt = "", hDepositBank = "", hAccNo = "", hSendTime = "", hDestination = "",
            hDistance = "", hChargers = "", hCourierName = "", hPODno = "", hCourierStatus = "", hScanCopy = "", hRemarks = "";
    String sNoOfCheque = "", sChequeNo = "", sReceiptNo = "", sHciSlipNo = "", sChequeAmt = "", sDepositBank = "", sAccNo = "", sSendTime = "", sDestination = "",
            sDistance = "", sChargers = "", sCourierName = "", sPODno = "", sCourierStatus = "", sScanCopy = "", sRemarks = "", accId = "";

    int iNoOfTransaction = 0;
    int iTransCount = 1;
    List<String> NoOfTransactionDummy = new ArrayList<>();
    List<String> NoOfChequeList = new ArrayList<>();
    List<String> ChequeNoList = new ArrayList<>();
    List<String> ReceiptNoList = new ArrayList<>();
    List<String> HciSlipNoList = new ArrayList<>();
    List<String> ChequeAmtList = new ArrayList<>();
    List<String> DepositBankList = new ArrayList<>();
    List<String> AccNoList = new ArrayList<>();
    List<String> SendTimeList = new ArrayList<>();
    List<String> DestinationList = new ArrayList<>();
    List<String> DistanceList = new ArrayList<>();
    List<String> ChargersList = new ArrayList<>();
    List<String> CourierNameList = new ArrayList<>();
    List<String> PODnoList = new ArrayList<>();
    List<String> CourierStatusList = new ArrayList<>();
    List<String> ScanCopyList = new ArrayList<>();
    List<String> RemarksList = new ArrayList<>();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_cheque_pickup_submit);

        mContext = this;
        activity = this;
        helper = new PreferenceHelper();
        gpsTracker = new GPSTracker(this);


        ce_id = getIntent().getStringExtra("ce_id");
        trans_id = getIntent().getStringExtra("trans_id");
        client_name = getIntent().getStringExtra("client_name");
        client_codecount = getIntent().getStringExtra("client_codecount");
        cust_code = getIntent().getStringExtra("cust_code");
        bankType = getIntent().getStringExtra("dep_type");
        clientId = getIntent().getStringExtra("clientId");
        iCustCodeCount = Integer.parseInt(getIntent().getStringExtra("iCustCodeCount"));

        System.out.println("Trans Id >>> " + trans_id);
        initComponents();
        alertDialogToGetNoofCheque();

        tvClientNameHeader.setText(client_name);

        try {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            device_id = telephonyManager.getDeviceId();
            IMEI_no = telephonyManager.getSimSerialNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        account_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("========>>", account_autocomplete.getText().toString());

                String bankDetails = account_autocomplete.getText().toString();
                String[] separated = bankDetails.split(",");

                etBankName.setText(separated[0]);
                etAccountNo.setText(separated[2]);
                accId = separated[3];
            }
        });

//        if (clientId.equals("38")) {
//            etBankName.setEnabled(true);
//        } else
//            etBankName.setEnabled(false);

    }

    private void initComponents() {
        tvClientNameHeader = (TextView) findViewById(R.id.tvClientNameHeaderCheckPickup);
        tvNoOfTransaction = (TextView) findViewById(R.id.tvNoOfTransaction);

        layoutNoOfCheque = (LinearLayout) findViewById(R.id.layoutNoOfChequeCheckPickup);
        layoutChequeNo = (LinearLayout) findViewById(R.id.layoutChequeNoCheckPickup);
        layoutReceiptNo = (LinearLayout) findViewById(R.id.layoutReceiptNoCheckPickup);
        layoutHciSlipNo = (LinearLayout) findViewById(R.id.layoutHciSlipNoCheckPickup);
        layoutChequeAmount = (LinearLayout) findViewById(R.id.layoutChequeAmountCheckPickup);
        layoutAccDetailsSearch = (LinearLayout) findViewById(R.id.layoutAccDetailsSearch);
        layoutAccountNo = (LinearLayout) findViewById(R.id.layoutAccountNoCheckPickup);
        layoutDepositBank = (LinearLayout) findViewById(R.id.layoutDepositBankCheckPickup);
        layoutCourierSentDateAndTime = (LinearLayout) findViewById(R.id.layoutCourierSentDateAndTimeCheckPickup);
        layoutCourierDesignation = (LinearLayout) findViewById(R.id.layoutCourierDesignationCheckPickup);
        layoutDistance = (LinearLayout) findViewById(R.id.layoutDistanceCheckPickup);
        layoutCourierCharges = (LinearLayout) findViewById(R.id.layoutCourierChargesCheckPickup);
        layoutCourierName = (LinearLayout) findViewById(R.id.layoutCourierNameCheckPickup);
        layoutPODno = (LinearLayout) findViewById(R.id.layoutPODnoCheckPickup);
        layoutCourierStatus = (LinearLayout) findViewById(R.id.layoutCourierStatusCheckPickup);
        layoutScanCopyStatus = (LinearLayout) findViewById(R.id.layoutScanCopyStatusCheckPickup);
        layoutRemark = (LinearLayout) findViewById(R.id.layoutRemarkCheckPickup);

        etNoOfCheque = (EditText) findViewById(R.id.etNoOfChequeCheckPickup);
        etChequeNo = (EditText) findViewById(R.id.etChequeNoCheckPickup);
        etReceiptNo = (EditText) findViewById(R.id.etReceiptNoCheckPickup);
        etHciSlipNo = (EditText) findViewById(R.id.etHciSlipNoCheckPickup);
        etChequeAmount = (EditText) findViewById(R.id.etChequeAmountCheckPickup);

        account_autocomplete = (AutoCompleteTextView) findViewById(R.id.accounts_autocompleteCheque);
        img_ClearAutoText = (ImageView) findViewById(R.id.img_ClearAutoTextCheque);
        etAccountNo = (EditText) findViewById(R.id.etAccountNoCheckPickup);
        etBankName = (EditText) findViewById(R.id.etDepositBankCheckPickup);

        etCourierSentDateAndTime = (EditText) findViewById(R.id.etCourierSentDateAndTimeCheckPickup);
        etCourierDesignation = (EditText) findViewById(R.id.etCourierDesignationCheckPickup);
        etDistance = (EditText) findViewById(R.id.etDistanceCheckPickup);
        etCourierCharges = (EditText) findViewById(R.id.etCourierChargesCheckPickup);
        etCourierName = (EditText) findViewById(R.id.etCourierNameCheckPickup);
        etPODno = (EditText) findViewById(R.id.etPODnoCheckPickup);
        etCourierStatus = (EditText) findViewById(R.id.etCourierStatusCheckPickup);
        etScanCopyStatus = (EditText) findViewById(R.id.etScanCopyStatusCheckPickup);
        etRemarks = (EditText) findViewById(R.id.etRemarksCheckPickup);

        submitBtn = (Button) findViewById(R.id.submitBtnCheckPickup);

        submitBtn.setOnClickListener(myClickhandler);
        img_ClearAutoText.setOnClickListener(myClickhandler);

        GetChequePickupHintNameAsyncTask getChequePickupHintNameAsyncTask = new GetChequePickupHintNameAsyncTask();
        getChequePickupHintNameAsyncTask.execute();

//        GetAccountDetailsAsyncTask getAccountDetailsAsyncTask = new GetAccountDetailsAsyncTask();
//        getAccountDetailsAsyncTask.execute();

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

    private void clear() {
        account_autocomplete.setText("");
        etBankName.setText("");
        etAccountNo.setText("");
        accId = "";
    }

    View.OnClickListener myClickhandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_ClearAutoTextCheque:
                    clear();
                    break;
                case R.id.submitBtnCheckPickup:

                    if (Utils.isInternetConnection(mContext)) {
                        if (getAllDatasToSubmit()) {

                            if (gpsTracker.canGetLocation()) {
                                d_latitude = gpsTracker.getLatitude();
                                d_longitude = gpsTracker.getLongitude();
//                                lat = (int) (d_latitude * 1E6);
//                                lon = (int) (d_longitude * 1E6);
                                s_latitude = String.valueOf(d_latitude);
                                s_longitude = String.valueOf(d_longitude);
                                System.out.println("Lat, Lng >>>" + s_latitude + ", " + s_longitude);

                                confirmationLayout();

                            } else {
                                gpsTracker.showSettingsAlert();
                                Toast.makeText(getApplicationContext(),
                                        "Please enable the Location Service(GPS)for view transactions",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else
                        Toast.makeText(mContext, "Check your Internet connection", Toast.LENGTH_LONG).show();

            }
        }
    };

    private boolean getAllDatasToSubmit() {

        if (!hChequeNo.isEmpty() && hChequeNo != "" && !hChequeNo.equals("")) {
            sChequeNo = etChequeNo.getText().toString().trim();
            if (sChequeNo.isEmpty() || sChequeNo.equals("")) {
                showToast("Enter the " + hChequeNo);
                return false;
            }
        }
        if (!hReceiptNo.isEmpty() && hReceiptNo != "" && !hReceiptNo.equals("")) {
            sReceiptNo = etReceiptNo.getText().toString().trim();
            if (sReceiptNo.isEmpty() || sReceiptNo.equals("")) {
                showToast("Enter the " + hReceiptNo);
                return false;
            }
        }
        if (!hHciSlipNo.isEmpty() && hHciSlipNo != "" && !hHciSlipNo.equals("")) {
            sHciSlipNo = etHciSlipNo.getText().toString().trim();
            if (sHciSlipNo.isEmpty() || sHciSlipNo.equals("")) {
                showToast("Enter the " + hHciSlipNo);
                return false;
            }
        }
        if (!hChequeAmt.isEmpty() && hChequeAmt != "" && !hChequeAmt.equals("")) {
            sChequeAmt = etChequeAmount.getText().toString().trim();
            if (sChequeAmt.isEmpty() || sChequeAmt.equals("")) {
                showToast("Enter the " + hChequeAmt);
                return false;
            }
        }

        if (!hDepositBank.isEmpty() && hDepositBank != "" && !hDepositBank.equals("")) {
            sDepositBank = etBankName.getText().toString().trim();
            if (sDepositBank.isEmpty() || sDepositBank.equals("")) {
                showToast("Enter the " + hDepositBank);
                return false;
            }
        }
        if (!hAccNo.isEmpty() && hAccNo != "" && !hAccNo.equals("")) {
            sAccNo = etAccountNo.getText().toString().trim();
            if (sAccNo.isEmpty() || sAccNo.equals("")) {
                showToast("Enter the " + hAccNo);
                return false;
            }
        }
        if (!hSendTime.isEmpty() && hSendTime != "" && !hSendTime.equals("")) {
            sSendTime = etCourierSentDateAndTime.getText().toString().trim();
            if (sSendTime.isEmpty() || sSendTime.equals("")) {
                showToast("Enter the " + hSendTime);
                return false;
            }
        }
        if (!hDestination.isEmpty() && hDestination != "" && !hDestination.equals("")) {
            sDestination = etCourierDesignation.getText().toString().trim();
            if (sDestination.isEmpty() || sDestination.equals("")) {
                showToast("Enter the " + hDestination);
                return false;
            }
        }

        if (!hDistance.isEmpty() && hDistance != "" && !hDistance.equals("")) {
            sDistance = etDistance.getText().toString().trim();
            if (sDistance.isEmpty() || sDistance.equals("")) {
                showToast("Enter the " + hDistance);
                return false;
            }
        }
        if (!hChargers.isEmpty() && hChargers != "" && !hChargers.equals("")) {
            sChargers = etCourierCharges.getText().toString().trim();
            if (sChargers.isEmpty() || sChargers.equals("")) {
                showToast("Enter the " + hChargers);
                return false;
            }
        }
        if (!hCourierName.isEmpty() && hCourierName != "" && !hCourierName.equals("")) {
            sCourierName = etCourierName.getText().toString().trim();
            if (sCourierName.isEmpty() || sCourierName.equals("")) {
                showToast("Enter the " + hCourierName);
                return false;
            }
        }
        if (!hPODno.isEmpty() && hPODno != "" && !hPODno.equals("")) {
            sPODno = etPODno.getText().toString().trim();
            if (sPODno.isEmpty() || sPODno.equals("")) {
                showToast("Enter the " + hPODno);
                return false;
            }
        }
        if (!hCourierStatus.isEmpty() && hCourierStatus != "" && !hCourierStatus.equals("")) {
            sCourierStatus = etCourierStatus.getText().toString().trim();
            if (sCourierStatus.isEmpty() || sCourierStatus.equals("")) {
                showToast("Enter the " + hCourierStatus);
                return false;
            }
        }
        if (!hScanCopy.isEmpty() && hScanCopy != "" && !hScanCopy.equals("")) {
            sScanCopy = etScanCopyStatus.getText().toString().trim();
            if (sScanCopy.isEmpty() || sScanCopy.equals("")) {
                showToast("Enter the " + hScanCopy);
                return false;
            }
        }
//        if (!hRemarks.isEmpty() && hRemarks != "" && !hRemarks.equals("")) {
//            sRemarks = etRemarks.getText().toString().trim();
//            if (sRemarks.isEmpty() || sRemarks.equals("")) {
//                showToast("Enter the " + hRemarks);
//                return false;
//            }
//        }

        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
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

                String URL="";
                String Urlstatus = SharedPreference.getDefaults(ChequePickupSubmitActivity.this, ConstantValues.TAG_URLVALIDATE);

                if(Urlstatus.equals("dontswap")){
                    URL=Config.url1;
                }else if(Urlstatus.equals("swap")) {
                    URL=Config.url2;
                }

                BankAccNoListRequestData requestData = new BankAccNoListRequestData();
                requestData.setOpt("bankAccNoList");
                requestData.setBankType(bankType);
                requestData.setAccNo("");
                System.out.println("GetAccDetails URL >>>" + URL);
                System.out.println("GetAccDetails Request >>>" + requestData.constructRequestData());
                Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                System.out.println("GetAccDetails Response >>> Done");

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

    private class GetChequePickupHintNameAsyncTask extends AsyncTask<Void, Void, Void> {
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
                String URL="";
                String Urlstatus = SharedPreference.getDefaults(ChequePickupSubmitActivity.this, ConstantValues.TAG_URLVALIDATE);
                if(Urlstatus.equals("dontswap")){
                    URL=Config.url1;
                }else if(Urlstatus.equals("swap")) {
                    URL=Config.url2;
                }
                DeliveryTransListRequestData requestData = new DeliveryTransListRequestData();
                requestData.setOpt("chequePickupHintDetails");
                requestData.setCe_Id(ce_id);
                requestData.setTransactionId(trans_id);
                System.out.println("GetChequePickupHint URL >>>" + URL);
                System.out.println("GetChequePickupHint Request >>>" + requestData.constructRequestData());
                Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());
                System.out.println("GetChequePickupHint Response >>>" + Response);

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

                    if (jh != null && jh.length() > 0) {

//                    hNoOfCheque="",hChequeNo = "", hReceiptNo = "", hHciSlipNo = "", hChequeAmt = "", hDepositBank = "", hAccNo = "", hSendTime = "", hDestination = "",
//                            hDistance = "", hChargers = "", hCourierName = "", hPODno = "", hCourierStatus = "", hScanCopy = "", hRemarks = "";

                        hNoOfCheque = jh.getString("NoofCheque");
                        hChequeNo = jh.getString("ChequeNo");
                        hReceiptNo = jh.getString("recNo");
                        hHciSlipNo = jh.getString("hclSlipNo");
                        hChequeAmt = jh.getString("chequeAmount");
                        hDepositBank = jh.getString("depositBank");
                        hAccNo = jh.getString("accountNo");
                        hSendTime = jh.getString("sendTime");
                        hDestination = jh.getString("destination");
                        hDistance = jh.getString("distance_ctobank");
                        hChargers = jh.getString("charges");
                        hCourierName = jh.getString("courierName");
                        hPODno = jh.getString("podNo");
                        hCourierStatus = jh.getString("courierStatus");
                        hScanCopy = jh.getString("scanCopy");
                        hRemarks = jh.getString("Remarks");

//                    layoutChequeNo, layoutReceiptNo, layoutHciSlipNo, layoutChequeAmount, layoutDepositBank, layoutAccountNo, layoutCourierSentDateAndTime,
//                            layoutCourierDesignation, layoutDistance, layoutCourierCharges, layoutCourierName, layoutPODno, layoutCourierStatus,
//                            layoutScanCopyStatus, layoutRemark;
//                    EditText etChequeNo, etReceiptNo, etHciSlipNo, etChequeAmount, etDepositBank, etAccountNo, etCourierSentDateAndTime, etCourierDesignation,
//                            etDistance, etCourierCharges, etCourierName, etPODno, etCourierStatus, etScanCopyStatus, etRemarks;

                        ///////////////////////////////////////////////
                        if (!hNoOfCheque.isEmpty() && hNoOfCheque != "" && !hNoOfCheque.equals(""))
                            etNoOfCheque.setHint(hNoOfCheque);
                        else layoutNoOfCheque.setVisibility(View.GONE);

                        if (!hChequeNo.isEmpty() && hChequeNo != "" && !hChequeNo.equals(""))
                            etChequeNo.setHint(hChequeNo);
                        else layoutChequeNo.setVisibility(View.GONE);

                        if (!hReceiptNo.isEmpty() && hReceiptNo != "" && !hReceiptNo.equals(""))
                            etReceiptNo.setHint(hReceiptNo);
                        else layoutReceiptNo.setVisibility(View.GONE);

                        if (!hHciSlipNo.isEmpty() && hHciSlipNo != "" && !hHciSlipNo.equals(""))
                            etHciSlipNo.setHint(hHciSlipNo);
                        else layoutHciSlipNo.setVisibility(View.GONE);

                        if (!hChequeAmt.isEmpty() && hChequeAmt != "" && !hChequeAmt.equals(""))
                            etChequeAmount.setHint(hChequeAmt);
                        else layoutChequeAmount.setVisibility(View.GONE);

                        if (!hDepositBank.isEmpty() && hDepositBank != "" && !hDepositBank.equals(""))
                            etBankName.setHint(hDepositBank);
                        else layoutDepositBank.setVisibility(View.GONE);

                        if (!hAccNo.isEmpty() && hAccNo != "" && !hAccNo.equals(""))
                            etAccountNo.setHint(hAccNo);
                        else {
                            layoutAccDetailsSearch.setVisibility(View.GONE);
                            layoutAccountNo.setVisibility(View.GONE);
                            etBankName.setEnabled(true);
                        }

                        if (!hSendTime.isEmpty() && hSendTime != "" && !hSendTime.equals(""))
                            etCourierSentDateAndTime.setHint(hSendTime);
                        else layoutCourierSentDateAndTime.setVisibility(View.GONE);

                        if (!hDestination.isEmpty() && hDestination != "" && !hDestination.equals(""))
                            etCourierDesignation.setHint(hDestination);
                        else layoutCourierDesignation.setVisibility(View.GONE);

                        if (!hDistance.isEmpty() && hDistance != "" && !hDistance.equals(""))
                            etDistance.setHint(hDistance);
                        else layoutDistance.setVisibility(View.GONE);

                        if (!hChargers.isEmpty() && hChargers != "" && !hChargers.equals(""))
                            etCourierCharges.setHint(hChargers);
                        else layoutCourierCharges.setVisibility(View.GONE);

                        if (!hCourierName.isEmpty() && hCourierName != "" && !hCourierName.equals(""))
                            etCourierName.setHint(hCourierName);
                        else layoutCourierName.setVisibility(View.GONE);

                        if (!hPODno.isEmpty() && hPODno != "" && !hPODno.equals(""))
                            etPODno.setHint(hPODno);
                        else layoutPODno.setVisibility(View.GONE);

                        if (!hCourierStatus.isEmpty() && hCourierStatus != "" && !hCourierStatus.equals(""))
                            etCourierStatus.setHint(hCourierStatus);
                        else layoutCourierStatus.setVisibility(View.GONE);

                        if (!hScanCopy.isEmpty() && hScanCopy != "" && !hScanCopy.equals(""))
                            etScanCopyStatus.setHint(hScanCopy);
                        else layoutScanCopyStatus.setVisibility(View.GONE);

                        if (!hRemarks.isEmpty() && hRemarks != "" && !hRemarks.equals(""))
                            etRemarks.setHint(hRemarks);
                        else layoutRemark.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(activity, "Hint details are not received", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException jsE) {

                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
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

    private class SubmitChequePickupDetailsAsyncTask extends AsyncTask<Void, Void, Void> {
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
                String URL="";
                String Urlstatus = SharedPreference.getDefaults(ChequePickupSubmitActivity.this, ConstantValues.TAG_URLVALIDATE);
                if(Urlstatus.equals("dontswap")){
                    URL=Config.url1;
                }else if(Urlstatus.equals("swap")) {
                    URL=Config.url2;
                }
                SubmitChequePickupDetailsRequestData requestData = new SubmitChequePickupDetailsRequestData();
                requestData.setOpt("submitCheque");
                requestData.setCe_Id(ce_id);
                requestData.setTransactionId(trans_id);
                requestData.setNoOfTransaction(iNoOfTransaction + "");
                requestData.setNoOfCheque(sNoOfCheque);
                requestData.setChequeNo(sChequeNo);
                requestData.setReceiptNo(sReceiptNo);
                requestData.setHciSlipNo(sHciSlipNo);
                requestData.setChequeAmt(sChequeAmt);
                requestData.setDepositBank(sDepositBank);
                requestData.setAccNo(sAccNo);
                requestData.setSendTime(sSendTime);
                requestData.setDestination(sDestination);
                requestData.setDistance(sDistance);
                requestData.setChargers(sChargers);
                requestData.setCourierName(sCourierName);
                requestData.setPODno(sPODno);
                requestData.setCourierStatus(sCourierStatus);
                requestData.setScanCopy(sScanCopy);
                requestData.setRemarks(sRemarks);

                requestData.setDeviceId(device_id);
                requestData.setIMEI_No(IMEI_no);
                requestData.setLatitude(s_latitude);
                requestData.setLongitude(s_longitude);

                System.out.println("submitCheque URL >>>" + URL);
                System.out.println("submitCheque Request >>>" + requestData.constructRequestData());

                Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());

                System.out.println("submitCheque Response >>>" + Response);

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
                Intent inte = new Intent(mContext, ChequePickupListActivity.class);
                inte.putExtra("ce_id", ce_id);
//                inte.putExtra("ce_id", ce_id).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public void alertDialogToGetNoofCheque() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle("Radiant Sandesh");
        builder.setMessage("Enter the number Of Transaction");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String i = input.getText().toString().trim();
                        if (i.isEmpty() || i == "" || i.equals("") || i.equals("0")) {
                            Toast.makeText(mContext, "Please Enter the No. of Transaction", Toast.LENGTH_LONG).show();
                        } else if (Integer.parseInt(i) < iCustCodeCount) {
                            Toast.makeText(getApplicationContext(), "Please Enter the No Of Transactions as " + iCustCodeCount + " or above", Toast.LENGTH_SHORT).show();
                        } else if (Integer.parseInt(i) > 8) {
                            Toast.makeText(mContext, "You can enter the maximum of 8 No. of Transactions", Toast.LENGTH_LONG).show();
                        } else {
                            iNoOfTransaction = Integer.parseInt(i);
                            tvNoOfTransaction.setText("No. of Transaction : " + iTransCount + "/" + iNoOfTransaction);
                            dialog.dismiss();
                        }
                    }
                });

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

            sNoOfCheque = etNoOfCheque.getText().toString().trim();
            sChequeNo = etChequeNo.getText().toString().trim();
            sReceiptNo = etReceiptNo.getText().toString().trim();
            sHciSlipNo = etHciSlipNo.getText().toString().trim();
            sChequeAmt = etChequeAmount.getText().toString().trim();
            sDepositBank = etBankName.getText().toString().trim();
            sAccNo = etAccountNo.getText().toString().trim();
            sSendTime = etCourierSentDateAndTime.getText().toString().trim();
            sDestination = etCourierDesignation.getText().toString().trim();
            sDistance = etDistance.getText().toString().trim();
            sChargers = etCourierCharges.getText().toString().trim();
            sCourierName = etCourierName.getText().toString().trim();
            sPODno = etPODno.getText().toString().trim();
            sCourierStatus = etCourierStatus.getText().toString().trim();
            sScanCopy = etScanCopyStatus.getText().toString().trim();
            sRemarks = etRemarks.getText().toString().trim();

            Date date_time = Calendar.getInstance().getTime();
            System.out.println("Current time => " + date_time);
            SimpleDateFormat da = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat ti = new SimpleDateFormat("h:mm a");
            String currentDate = da.format(date_time);
            String currentTime = ti.format(date_time);
            System.out.println("date, time >>> " + currentDate + ", " + currentTime);

            date.setText("Current Date : " + currentDate);
            time.setText("Current Time : " + currentTime);
            if (!hChequeNo.isEmpty())
                reqAmt.setText(hChequeNo + " : " + sChequeNo);
            else reqAmt.setVisibility(View.GONE);

            if (!hChequeAmt.isEmpty())
                delAmt.setText(hChequeAmt + " : " + sChequeAmt);
            else
                delAmt.setVisibility(View.GONE);

            if (!hAccNo.isEmpty())
                diffAmt.setText(hAccNo + " : " + sAccNo);
            else
                diffAmt.setVisibility(View.GONE);

            if (!hDepositBank.isEmpty())
                delTo.setText(hDepositBank + " : " + sDepositBank);
            else
                delTo.setVisibility(View.GONE);

            if (!hReceiptNo.isEmpty())
                recptNo.setText(hReceiptNo + " : " + sReceiptNo);
            else recptNo.setVisibility(View.GONE);

            remarkTyp.setText(hRemarks + " : " + sRemarks);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            dialogBuilder.setView(view);
            final AlertDialog alert = dialogBuilder.create();
//            alert.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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
                    alert.dismiss();

                    if (iNoOfTransaction == 1) {
                        sNoOfCheque = etNoOfCheque.getText().toString().trim();
                        sChequeNo = etChequeNo.getText().toString().trim();
                        sReceiptNo = etReceiptNo.getText().toString().trim();
                        sHciSlipNo = etHciSlipNo.getText().toString().trim();
                        sChequeAmt = etChequeAmount.getText().toString().trim();
                        sDepositBank = etBankName.getText().toString().trim();
                        sAccNo = etAccountNo.getText().toString().trim();
                        sSendTime = etCourierSentDateAndTime.getText().toString().trim();
                        sDestination = etCourierDesignation.getText().toString().trim();
                        sDistance = etDistance.getText().toString().trim();
                        sChargers = etCourierCharges.getText().toString().trim();
                        sCourierName = etCourierName.getText().toString().trim();
                        sPODno = etPODno.getText().toString().trim();
                        sCourierStatus = etCourierStatus.getText().toString().trim();
                        sScanCopy = etScanCopyStatus.getText().toString().trim();
                        sRemarks = etRemarks.getText().toString().trim();

                        SubmitChequePickupDetailsAsyncTask submitChequePickupDetailsAsyncTask = new SubmitChequePickupDetailsAsyncTask();
                        submitChequePickupDetailsAsyncTask.execute();

                    } else if (iNoOfTransaction > 1) {
                        NoOfTransactionDummy.add(iTransCount + "");
                        NoOfChequeList.add(etNoOfCheque.getText().toString().trim());
                        ChequeNoList.add(etChequeNo.getText().toString().trim());
                        ReceiptNoList.add(etReceiptNo.getText().toString().trim());
                        HciSlipNoList.add(etHciSlipNo.getText().toString().trim());
                        ChequeAmtList.add(etChequeAmount.getText().toString().trim());
                        DepositBankList.add(etBankName.getText().toString().trim());
                        AccNoList.add(etAccountNo.getText().toString().trim());
                        SendTimeList.add(etCourierSentDateAndTime.getText().toString().trim());
                        DestinationList.add(etCourierDesignation.getText().toString().trim());
                        DistanceList.add(etDistance.getText().toString().trim());
                        ChargersList.add(etCourierCharges.getText().toString().trim());
                        CourierNameList.add(etCourierName.getText().toString().trim());
                        PODnoList.add(etPODno.getText().toString().trim());
                        CourierStatusList.add(etCourierStatus.getText().toString().trim());
                        ScanCopyList.add(etScanCopyStatus.getText().toString().trim());
                        RemarksList.add(etRemarks.getText().toString().trim());

                        if (iNoOfTransaction > NoOfTransactionDummy.size()) {
                            sNoOfCheque = "";
                            sChequeNo = "";
                            sReceiptNo = "";
                            sHciSlipNo = "";
                            sChequeAmt = "";
                            sDepositBank = "";
                            sAccNo = "";
                            sSendTime = "";
                            sDestination = "";
                            sDistance = "";
                            sChargers = "";
                            sCourierName = "";
                            sPODno = "";
                            sCourierStatus = "";
                            sScanCopy = "";
                            sRemarks = "";

                            etNoOfCheque.setText("");
                            etChequeNo.setText("");
                            etReceiptNo.setText("");
                            etHciSlipNo.setText("");
                            etChequeAmount.setText("");
                            clear();
                            etCourierSentDateAndTime.setText("");
                            etCourierDesignation.setText("");
                            etDistance.setText("");
                            etCourierCharges.setText("");
                            etCourierName.setText("");
                            etPODno.setText("");
                            etCourierStatus.setText("");
                            etScanCopyStatus.setText("");
                            etRemarks.setText("");

                            iTransCount = iTransCount + 1;
                            tvNoOfTransaction.setText("No. of Transaction : " + iTransCount + "/" + iNoOfTransaction);

                        } else if (iNoOfTransaction == NoOfTransactionDummy.size()) {
                            for (int i = 0; i < NoOfTransactionDummy.size(); i++) {
                                if (i == 0) {
                                    sNoOfCheque = NoOfChequeList.get(i).trim();
                                    sChequeNo = ChequeNoList.get(i).trim();
                                    sReceiptNo = ReceiptNoList.get(i).trim();
                                    sHciSlipNo = HciSlipNoList.get(i).trim();
                                    sChequeAmt = ChequeAmtList.get(i).trim();
                                    sDepositBank = DepositBankList.get(i).trim();
                                    sAccNo = AccNoList.get(i).trim();
                                    sSendTime = SendTimeList.get(i).trim();
                                    sDestination = DestinationList.get(i).trim();
                                    sDistance = DistanceList.get(i).trim();
                                    sChargers = ChargersList.get(i).trim();
                                    sCourierName = CourierNameList.get(i).trim();
                                    sPODno = PODnoList.get(i).trim();
                                    sCourierStatus = CourierStatusList.get(i).trim();
                                    sScanCopy = ScanCopyList.get(i).trim();
                                    sRemarks = RemarksList.get(i).trim();
                                } else {
                                    sNoOfCheque = sNoOfCheque + ", " + NoOfChequeList.get(i).trim();
                                    sChequeNo = sChequeNo + ", " + ChequeNoList.get(i).trim();
                                    sReceiptNo = sReceiptNo + ", " + ReceiptNoList.get(i).trim();
                                    sHciSlipNo = sHciSlipNo + ", " + HciSlipNoList.get(i).trim();
                                    sChequeAmt = sChequeAmt + ", " + ChequeAmtList.get(i).trim();
                                    sDepositBank = sDepositBank + ", " + DepositBankList.get(i).trim();
                                    sAccNo = sAccNo + ", " + AccNoList.get(i).trim();
                                    sSendTime = sSendTime + ", " + SendTimeList.get(i).trim();
                                    sDestination = sDestination + ", " + DestinationList.get(i).trim();
                                    sDistance = sDistance + ", " + DistanceList.get(i).trim();
                                    sChargers = sChargers + ", " + ChargersList.get(i).trim();
                                    sCourierName = sCourierName + ", " + CourierNameList.get(i).trim();
                                    sPODno = sPODno + ", " + PODnoList.get(i).trim();
                                    sCourierStatus = sCourierStatus + ", " + CourierStatusList.get(i).trim();
                                    sScanCopy = sScanCopy + ", " + ScanCopyList.get(i).trim();
                                    sRemarks = sRemarks + ", " + RemarksList.get(i).trim();
                                }
                            }
                            SubmitChequePickupDetailsAsyncTask submitChequePickupDetailsAsyncTask = new SubmitChequePickupDetailsAsyncTask();
                            submitChequePickupDetailsAsyncTask.execute();
                        }
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
