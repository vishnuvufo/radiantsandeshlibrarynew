package com.mountfox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mountfox.GetJson.CallbackInterface;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.Services.dataRequest.PickupRecptNoDuplicationRequestData;
import com.mountfox.Services.serviceRequest.ServiceRequestPOSTImpl;
import com.mountfox.otpresponse.OtpResponse;
import com.mountfox.response.PickupStandardRemarks;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReceivePayment extends Activity implements OnClickListener {
    int mHour, mMinute;
    private String ce_id = "", trans_id = "", mobile_no = "", type = "", pickup_amount = "", pickup_amount1 = "", amt = "",
            deposit_slip = "", diff_slip = "", pis_no = "", hci_no = "", sealtag_no = "", pis_date = "",
            dep_amount = "", rec_status = "", master_rec_status = "", seconday_rec_status = "", secondayothers_rec_status = "", remarks = "", device_id = "",
            pin_status = "", client_id = "", Strtrans_param = "", Strmaster_param = "", client_code = "", Strdeno = "",
            strdenoParam = "", strQrParam = "", bank_dep_slip_str = "", img_path = "", shop_id = "", s_latitude = "", s_longitude = "";
    private static final String TAG = "ReceivePayment";
    static int transaction_pin = 0, activity_pass = 0, viewPosition;
    double latitude = 12.982733625, longitude = 80.252031675;
    private PopupWindow spinnerWindow;
    private ListView listSpinner;
    private Dialog dialog;

    private EditText hci_no_txt;
    private EditText aditional_remarks_editText, pickup_amount_txt, dep_no_txt, diff_no_txt, pis_no_txt,
            seal_no_txt, dep_amount_txt, remarks_txt, txt, edtxtDeno1000, edtxtDeno2000, edtxtDeno200,
            edtxtDeno500, edtxtDeno100, edtxtDeno50, edtxtDeno20, edtxtDeno10, edtxtDeno5, edtxtDenoCoins, bank_dep_slip_edt, req_edt,
            edt_dep_branch, pickup_time, pis_date_txt;
    private AutoCompleteTextView account_autocomplete;
    private TextView receipt_status_spinner, child_remarks_receipt_status_spinner, tv_info, tv_error, Header;
    private ImageView img_cancel, img_close_OTP;
    private Button submit_btn;
    private ProgressDialog progressDialog;
    private GetJson getJson;
    private DbHandler dbHandler;
    private GPSTracker gpsTracker;
    private List<BasicNameValuePair> params;
    final Context context = this;
    private ArrayAdapter<String> spinnerAdapter;
    private LinearLayout aditional_remarks_layout, layout1, layout2, layout3, childlayout2, new_deposite_block, lin_bank, lin_vault, lin_dep, lin_pis, lin_hci,
            lin_receipt, lin_dep_slip, deposite_line, pis_date_lin;
    private Spinner ccode_spin, bank_spin, bvType_spin, vault_spin, spin_branch;
    private ArrayAdapter<String> cspin_adap, bankSpin_adap, vaultSpin_adap, branch_adap, cc_adap;
    private ArrayList<String> cspin_arls = new ArrayList<String>();
    private ArrayList<String> bank_arls = new ArrayList<String>();
    private ArrayList<String> account_id = new ArrayList<String>();
    private ArrayList<String> vault_arls = new ArrayList<String>();
    private ArrayList<String> type_arls = new ArrayList<String>();
    public static int Vault_spinner_selected_position = 0;
    public static int deposite_type_selected_item = 0;
    ApiInterface apiInterface, apiInterfaceTwo, apiInterfaceLocal;
    ListView dialoglist;
    int Client_code_size = 0;
    PreferenceHelper helper;
    List<String> rec_status_option = new ArrayList<String>();
    public static int strNoTrans = 0;
    public int strGetNoTrans = 1, lat = 1, lon = 2, txtWidth, strDeno2000, strDeno200,
            strDeno1000, strDeno500, strDeno100, strDeno50, strDeno20,
            strDeno10, strDeno5, strDenoCoins, diff_amount = 0, diff_deno2000 = 0, diff_deno200 = 0, diff_deno1000 = 0, diff_deno500 = 0,
            diff_deno100 = 0, diff_deno50 = 0, diff_deno20 = 0, diff_deno10 = 0, diff_deno5 = 0, diff_denocoins = 0;
    static String account_id_string = "";
    public static List<String> listPickupAmout, listDepasitSlip, listPISNo, listPISDate, lst_remarks, listHCINo, listSealTagNo, listDeno, list_ccode, list_remarks, list_masterremarks, listQrDetails;
    AlertDialog dialog1;
    private int offf_pin_count = 4;
    private String typ = "", bank_nam = "", branch_name = "", vault_name = "",
            acc_no = "", captions = "", dep_typees = "", account_number_auto = "", DayinWeek = "", qr_json = "", stopId = "";
    private String[] stop_id = null;

    private String[] caps_arr = null;
    private String[] client_amt = null;
    private LinearLayout lin_deno, lin_req;
    private ArrayList<Bank_Pojo> bank_acc_arls = new ArrayList<Bank_Pojo>();
    private ArrayList<String> accounts = new ArrayList<String>();
    private ArrayList<String> acc_ids = new ArrayList<String>();
    String qr_status = "", rec_pos = "", dupRecptNoValStatus = "", otp_flag, otp_day, mobile_otp, otp_mobile;

    String flag_client_id = "";
    Context mContext;
//        Boolean isReceiptNumDuplication = true;

    String checkReceiptDuplicate = "", clientId = "";
    EditText etClearRecptNo;

    private static final int RESULT_LOAD_IMG = 188;
    SurfaceView surfaceView;
    private CameraSource cameraSource;
    FlashlightProvider flashlightProvider;
    private Camera mCamera = null;
    boolean flashmode = false;
    private Uri imageUri;
    private BarcodeDetector detector;
    androidx.appcompat.app.AlertDialog alert;
    TextView tvQRResult;
    String sQRDetails = "";
    String QrfirstWord = "";
    int getQrValue = 0;
    String duplicateRecptIsBtnEnable = "Yes";
    String duplicateRecptMsg = "";
    public Boolean expectionalqrscan = false;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    ImageView imgQrScanReceivePayment;
    private String blockCharacterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-";
    List<String> Standardremarks = new ArrayList<>();
    List<String> ChildStandardremarks = new ArrayList<>();
    List<String> StandardremarksList = new ArrayList<>();
    List<String> ChildStandardremarksList = new ArrayList<>();
    private ListPopupWindow standardpopup;
    private ListPopupWindow childstandardpopup;
    private String[] standardpopuplist;
    private LinearLayout cam_scan;
    private EditText ed_mobilenumber_OTP;
    private LinearLayout ln_submit_OTP;
    String mobileotpstatus = "", mobileotpce_id = "", mobileotptrans_id = "", mobileotpshop_id = "", mobileno = "";





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.receive_payment);
        apiInterface = Constants.getClient().create(ApiInterface.class);
        apiInterfaceTwo = Constants.getClientTwo().create(ApiInterface.class);
        apiInterfaceLocal = Constants.getClientLocal().create(ApiInterface.class);
        mContext = this;
        helper = new PreferenceHelper();




//        camera = findViewById(R.id.tvClearHciNoTxt);
//
//        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
//            @Override
//            public void onScannerStarted(ScannerLiveView scanner) {
//                // method is called when scanner is started
//                Toast.makeText(ReceivePayment.this, "Scanner Started", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onScannerStopped(ScannerLiveView scanner) {
//                // method is called when scanner is stopped.
//                Toast.makeText(ReceivePayment.this, "Scanner Stopped", Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onScannerError(Throwable err) {
//                // method is called when scanner gives some error.
//                Toast.makeText(ReceivePayment.this, "Scanner Error: " + err.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onCodeScanned(String data) {
//                hci_no_txt.setText(data);
//                Log.e(TAG, "DATAAAAAA>>>>>>....."+data);
//            }
//        });


//qr_json
//        qr_json = getIntent().getStringExtra("qr_json");
//        Log.e(TAG,"QR_json-->"+qr_json);


//        try {
//            JSONArray ja = new JSONArray(helper.getPickupRemark1(mContext));
//            for (int i = 0; i < ja.length(); i++) {
//                StandardremarksList.add(ja.toString(i));
//            }
//            Log.e(TAG,"StandardRemarks-->"+StandardremarksList.toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG,"StandardRemarks-->"+e.getMessage());
//        }
        initializeComponents();
        // test 27.7.17
//        myInit();
        alertDialog();
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(ReceivePayment.this, new String[]{android.Manifest.permission.CAMERA}, 201);
        }
    }

    private void myInit() {
        SharedData sharedData = SharedData.getInstance(this);
        String response = sharedData.getData("transactions");
//        Log.e(TAG, "----Total REsponse--in Receive PAyment PAgeeeee-----" + response);
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("msg").equals("success")) {
                HashMap<String, String> hm;
                JSONArray ja = jsonObject.getJSONArray("transactions");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject inner_jo = ja.getJSONObject(i);
                    hm = new HashMap<String, String>();
                    hm.put("trans_id", inner_jo.getString("trans_id"));
                    hm.put("client_id", inner_jo.getString("client_id"));
                    arrayList.add(hm);
                }
            } else {
                Toast.makeText(getApplicationContext(), "No Data Found ReLogin", Toast.LENGTH_SHORT).show();
            }
            Log.e(TAG + arrayList.size() + "---" + TransactionSingleItemDataCenter.trans_ids, "----Arraylist.toString()----" + arrayList.toString());
            for (Map<String, String> map : arrayList) {
                if (TransactionSingleItemDataCenter.trans_ids.equals(map.get("trans_id"))) {
                    clientId = map.get("client_id");
                    Log.e(TAG, "clientId>>-->" + clientId);

                    if (map.get("client_id").equals("22")) {
                        System.out.println("Final Answer--  " + map.get("client_id"));
                        //Toast.makeText(getApplicationContext(),"Client ID is 22",Toast.LENGTH_SHORT).show();
                        // new sujith
                        flag_client_id = "22";
                        seal_no_txt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mine_setEditTextMaxLength(seal_no_txt, 10);
                        // new sujith1f

                        hci_no_txt.setFilters(new InputFilter[]{
                                new InputFilter() {
                                    public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                                        for (int i = start; i < end; i++) {
                                            if (Character.isSpaceChar(src.charAt(i))) {
                                                return "";
                                            }
                                        }
                                        if (src.toString().matches("[a-zA-Z 0-9]+")) {
                                            return src;
                                        }
                                        return "";
                                    }
                                }
                        });
                    } else {
                        System.out.println("-No change-");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void mine_setEditTextMaxLength(final EditText editText, int length) {
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(FilterArray);
    }

    @SuppressLint("LongLogTag")
    public void initializeComponents() {
        imgQrScanReceivePayment = (ImageView) findViewById(R.id.imgQrScanReceivePayment);
        tvQRResult = (TextView) findViewById(R.id.tvQRResultReceivePayment);
        flashlightProvider = new FlashlightProvider(this);


        imgQrScanReceivePayment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ExeceptionalQRScan(false);
                expectionalqrscan = false;
                getQrValue = 0;
                showQRcodeLayout(0);
                imgQrScanReceivePayment.setVisibility(View.GONE);
            }
        });

///////         hcl no  qr text insert in textview
        ImageView tvClearHciNoTxt = (ImageView) findViewById(R.id.tvClearHciNoTxt);


        Header = (TextView) findViewById(R.id.Header);
        pickup_amount_txt = (EditText) findViewById(R.id.pickup_amount_txt);
        bank_dep_slip_edt = (EditText) findViewById(R.id.bank_dep_slip_txt);
        dep_no_txt = (EditText) findViewById(R.id.dep_no_txt);
        diff_no_txt = (EditText) findViewById(R.id.diff_amount_txt);
        pis_no_txt = (EditText) findViewById(R.id.pis_no_txt);
        hci_no_txt = (EditText) findViewById(R.id.hci_no_txt);
        seal_no_txt = (EditText) findViewById(R.id.seal_no_txt);
        TextView tvClearSealNoTxt = (TextView) findViewById(R.id.tvClearSealNoTxt);
        dep_amount_txt = (EditText) findViewById(R.id.dep_amount_txt);
        remarks_txt = (EditText) findViewById(R.id.remarks_txt);
        receipt_status_spinner = (TextView) findViewById(R.id.receipt_status_spinner);
        child_remarks_receipt_status_spinner = (TextView) findViewById(R.id.child_remarks_receipt_status_spinner);
        pis_date_txt = (EditText) findViewById(R.id.pis_date_txt);
        edtxtDeno2000 = (EditText) findViewById(R.id.deno_2000);
        edtxtDeno200 = (EditText) findViewById(R.id.deno_200);
        edtxtDeno1000 = (EditText) findViewById(R.id.deno_1000);
        edtxtDeno500 = (EditText) findViewById(R.id.deno_500);
        edtxtDeno100 = (EditText) findViewById(R.id.deno_100);
        edtxtDeno50 = (EditText) findViewById(R.id.deno_50);
        edtxtDeno20 = (EditText) findViewById(R.id.deno_20);
        edtxtDeno10 = (EditText) findViewById(R.id.deno_10);
        edtxtDeno5 = (EditText) findViewById(R.id.deno_5);
        edtxtDenoCoins = (EditText) findViewById(R.id.deno_coins);
        edt_dep_branch = (EditText) findViewById(R.id.branch_name_edt);
        bank_dep_slip_edt = (EditText) findViewById(R.id.bank_dep_slip_txt);
        aditional_remarks_layout = (LinearLayout) findViewById(R.id.aditional_remarks_layout);
        aditional_remarks_editText = (EditText) findViewById(R.id.aditional_remarks_editText);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        pis_date_lin = (LinearLayout) findViewById(R.id.pis_date_lin);
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout3 = (LinearLayout) findViewById(R.id.layout3);
        childlayout2 = (LinearLayout) findViewById(R.id.childlayout2);
        lin_bank = (LinearLayout) findViewById(R.id.lin_bank);
        new_deposite_block = (LinearLayout) findViewById(R.id.new_deposite_block);
        lin_dep = (LinearLayout) findViewById(R.id.lin_dep);
//        deposite_line=(LinearLayout)findViewById(R.id.deposite_line);
        lin_vault = (LinearLayout) findViewById(R.id.lin_vault);
        lin_dep_slip = (LinearLayout) findViewById(R.id.dep_no_lin);
        lin_pis = (LinearLayout) findViewById(R.id.pis_no_lin);
        lin_hci = (LinearLayout) findViewById(R.id.hci_no_lin);
        lin_receipt = (LinearLayout) findViewById(R.id.seal_no_lin);
        bvType_spin = (Spinner) findViewById(R.id.dep_type_spin);
        bank_spin = (Spinner) findViewById(R.id.bank_name_spin);
        ccode_spin = (Spinner) findViewById(R.id.ccode_spin);
        vault_spin = (Spinner) findViewById(R.id.vault_name_spin);
        // spin_acc        = (Spinner)      findViewById(R.id.acc_no_spin);
        spin_branch = (Spinner) findViewById(R.id.branch_name_spin);
        lin_req = (LinearLayout) findViewById(R.id.req_lin);
        req_edt = (EditText) findViewById(R.id.req_amount_txt);
        lin_deno = (LinearLayout) findViewById(R.id.lin_deno);
        account_autocomplete = (AutoCompleteTextView) findViewById(R.id.accounts_autocomplete);



//
//        tvClearHciNoTxt.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                expectionalqrscan = false;
//                getQrValue = 0;
//               HclNumberQR(0);
//                ExeceptionalQRScan(true);
//            }
//        });


        // test 28.11.2018
        myInit();
        account_autocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                branch_adap.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //Thamizh
        if (getIntent().hasExtra("stop_id")) {
            stopId = getIntent().getStringExtra("stop_id");
            System.out.println("shop_id trans >>> " + stopId);
            stop_id = stopId.split(",");
        }
        // qr_json

        if (getIntent().hasExtra("qr_status"))
            qr_status = getIntent().getStringExtra("qr_status");

        if (getIntent().hasExtra("rec_pos"))
            rec_pos = getIntent().getStringExtra("rec_pos");

        if (getIntent().hasExtra("dupRecptNoValStatus"))
            dupRecptNoValStatus = getIntent().getStringExtra("dupRecptNoValStatus");


        if (getIntent().hasExtra("otp_flag")) {
            otp_flag = getIntent().getStringExtra("otp_flag");
            Log.e(TAG, "otp_flag_msg-->" + otp_flag);
        }

        if (getIntent().hasExtra("otp_day")) {
            otp_day = getIntent().getStringExtra("otp_day");
            Log.e(TAG, "otp_day_msg-->" + otp_day);
        }

/////////////// mobile OTP ********************

        if (getIntent().hasExtra("AxisTransaction")) {
            mobile_otp = getIntent().getStringExtra("AxisTransaction");
            Log.e(TAG, "AxisTransaction OTP _msg-->" + mobile_otp);
        }


//////////////////////////////////////////////////////

        if (getIntent().hasExtra("shop_id")) {
            shop_id = getIntent().getStringExtra("shop_id");
            Log.e(TAG, "shop_id_msg-->" + shop_id);
        }


        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        if (dayOfTheWeek.equals("Monday")) {
            DayinWeek = "Mon";
        } else if (dayOfTheWeek.equals("Tuesday")) {
            DayinWeek = "Tue";
        } else if (dayOfTheWeek.equals("Wednesday")) {
            DayinWeek = "Wed";
        } else if (dayOfTheWeek.equals("Thursday")) {
            DayinWeek = "Thu";
        } else if (dayOfTheWeek.equals("Friday")) {
            DayinWeek = "Fri";
        } else if (dayOfTheWeek.equals("Saturday")) {
            DayinWeek = "Sat";
        } else if (dayOfTheWeek.equals("Sunday")) {
            DayinWeek = "Sun";
        } else {

        }
        Log.e(TAG, "day in week ->" + DayinWeek);
        captions = TransactionSingleItemDataCenter.captions;
        Log.w("ReceivePayment", "captions----SetList order Of Hint in That PAge--------" + captions);
        caps_arr = new String[captions.split(",").length + 2];
        caps_arr = captions.split(",");
        Log.e(TAG, "hintcaps_arr>>" + caps_arr);

        dep_no_txt.setFilters(new InputFilter[]{filter});


        for (int i = 0; i < caps_arr.length; i++)
            //Log.v("ReceivePayment","captions"+caps_arr[i]);
            if (!caps_arr[1].equalsIgnoreCase("0")) {
                dep_no_txt.setHint(caps_arr[1]);
                if (clientId.equals("2")) {
                    dep_no_txt.setFilters(new InputFilter[]{
                            new InputFilter() {
                                public CharSequence filter(CharSequence type, int start, int end, Spanned dst, int dstart, int dend) {
                                    for (int i = start; i < end; i++) {
                                        if (Character.isSpaceChar(type.charAt(i))) {
                                            return "";
                                        }
                                    }
                                    if (type.toString().matches(
                                            "[a-zA-Z 0-9-]+")) {
                                        return type;
                                    }
                                    return "";
                                }
                            }
                    });
                } else {

                    dep_no_txt.setFilters(new InputFilter[]{
                            new InputFilter() {
                                public CharSequence filter(CharSequence type, int start, int end, Spanned dst, int dstart, int dend) {
                                    for (int i = start; i < end; i++) {
                                        if (Character.isSpaceChar(type.charAt(i))) {
                                            return "";
                                        }
                                    }
                                    if (type.toString().matches(
                                            "[a-zA-Z 0-9]+")) {
                                        return type;
                                    }
                                    return "";
                                }
                            }
                    });

                }
                System.out.println(" PIS  NO >>>>>>>>>>>>>>> ");
                Log.e(TAG, "hintdep_no_txt>>" + caps_arr[1]);
            } else
                lin_dep_slip.setVisibility(View.GONE);
        if (!caps_arr[2].equalsIgnoreCase("0")) {
            pis_no_txt.setHint(caps_arr[2]);
            Log.e(TAG, "hintpis_no_txt>>" + caps_arr[2]);
        } else
            lin_pis.setVisibility(View.GONE);
        if (!caps_arr[3].equalsIgnoreCase("0")) {
            hci_no_txt.setHint(caps_arr[3]);
            Log.e(TAG, "hinthci_no_txt>>" + caps_arr[3]);
        } else
            lin_hci.setVisibility(View.GONE);
        if (!caps_arr[4].equalsIgnoreCase("0")) {
            seal_no_txt.setHint(caps_arr[4]);
            Log.e(TAG, "hintseal_no_txt>>" + caps_arr[4]);
        } else
            lin_receipt.setVisibility(View.GONE);
//        }
        //
//        if (getIntent().hasExtra("client_amt")) {
//        client_amt = TransactionSingleItemDataCenter.amts.split(",");
        client_amt = TransactionSingleItemDataCenter.client_amt.split(",");
//        }
        layout3.setVisibility(View.GONE);
        //code for calculating difference amount
        loadStandardPickupRemarks();
        ///standard remarks
        //  loadPickupRemarks(rec_pos);

        pickup_amount_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }


            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!TextUtils.isEmpty(pickup_amount_txt.getText().toString()) && !pickup_amount_txt.getText().toString().equals("0")) {


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

                    //test sujith..4
                    if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                        diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                    else
                        diff_deno200 = 0;

                    int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;

                    int changing_diff_amount = totalDenom - Integer.parseInt(pickup_amount_txt.getText().toString());
                    diff_no_txt.setText(String.valueOf(changing_diff_amount));

                    diff_amount = 0 - Integer.parseInt(pickup_amount_txt.getText().toString());
                    pickup_amount1 = String.valueOf(-diff_amount);
//                    if (getIntent().hasExtra("deno_status"))
                    if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("0")) {
                        diff_no_txt.setText("0");
                        diff_amount = 0;
                    } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans < strNoTrans) {
                        diff_no_txt.setText("0");
                        diff_amount = 0;
                    } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans == strNoTrans) {
                        //Log.v("ReceivePayment","list of pickup amount size"+listPickupAmout.size());
                        for (int cou = 0; cou < listPickupAmout.size(); cou++) {
                            pickup_amount = String.valueOf(Integer.parseInt(listPickupAmout.get(cou)) + Integer.parseInt(pickup_amount_txt.getText().toString()));
                            diff_amount = -Integer.parseInt(listPickupAmout.get(cou)) + diff_amount;
//                            deposit_amount_total=-Integer.parseInt(listPickupAmout.get(cou)) + deposit_amount_total;
                            diff_no_txt.setText(String.valueOf(diff_amount));
//                            dep_amount_txt.setText(diff_amount);
                            dep_amount_txt.setText(String.valueOf(Math.abs(diff_amount)));
                            pickup_amount1 = String.valueOf(-diff_amount);
                            Log.d("afterTextChanged", "afterTextChanged##" + pickup_amount_txt.getText());
                            //Log.v("ReceivePayment","list of pickup amount"+listPickupAmout.get(cou)+":"+pickup_amount);
                        }
                    }
                } else if (TextUtils.isEmpty(pickup_amount_txt.getText().toString()) || pickup_amount_txt.getText().toString().equals("0")) {
                    edtxtDeno2000.setText("");
                    edtxtDeno200.setText("");
                    edtxtDeno1000.setText("");
                    edtxtDeno500.setText("");
                    edtxtDeno100.setText("");
                    edtxtDeno50.setText("");
                    edtxtDeno20.setText("");
                    edtxtDeno10.setText("");
                    edtxtDeno5.setText("");
                    edtxtDenoCoins.setText("");
                    diff_no_txt.setText("");

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
                    diff_denocoins = 0;
                    diff_amount = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (strGetNoTrans == 1) {
                    dep_amount_txt.setText(pickup_amount_txt.getText().toString());
                }
                if (pickup_amount_txt.getText().toString().trim().equals("0")) {
                    diff_no_txt.setText("0");
                }

                ///////////////////////////////////////// 24.04.2018

                String recpNo = "";
                if (flag_client_id.equals("22") && !hci_no_txt.getText().toString().trim().equals("") ||
//                        Double.parseDouble(hci_no_txt.getText().toString().trim()) != 0 ||
                        !hci_no_txt.getText().toString().trim().equals("0")) {
                    recpNo = hci_no_txt.getText().toString().trim();
                } else if (flag_client_id.equals("22") && !seal_no_txt.getText().toString().trim().equals("") ||
//                        Double.parseDouble(seal_no_txt.getText().toString().trim()) != 0 ||
                        !seal_no_txt.getText().toString().trim().equals("0")) {
                    recpNo = seal_no_txt.getText().toString().trim();
                } else
                    recpNo = "";

                // loadPickupRemarks(recpNo);
            }
        });

        seal_no_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                String recpNo = "";
                if (flag_client_id.equals("22") && !seal_no_txt.getText().toString().trim().equals("") ||
//                        Double.parseDouble(seal_no_txt.getText().toString().trim()) != 0 ||
                        !seal_no_txt.getText().toString().trim().equals("0")) {
                    recpNo = seal_no_txt.getText().toString().trim();
                } else
                    recpNo = "";

                // loadPickupRemarks(recpNo);
            }
        });

        seal_no_txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (dupRecptNoValStatus.equals("1") && (rec_pos.equals("sealNo")) || dupRecptNoValStatus.equals("2")) {
                        checkReceiptDuplicate = "";
                        etClearRecptNo = seal_no_txt;
                        checkReceiptDuplicate = seal_no_txt.getText().toString().trim();
                        if (!checkReceiptDuplicate.isEmpty()) {
                            GetReceiptDuplicateStatusAsyncTask getReceiptDuplicateStatusAsyncTask = new GetReceiptDuplicateStatusAsyncTask();
                            getReceiptDuplicateStatusAsyncTask.execute();
                        }
                    }
                }
            }
        });
        hci_no_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
//                        && seal_no_txt.getText().toString().trim().length() < 5) { // new sujith
                String recpNo = "";
                if (flag_client_id.equals("22") && !hci_no_txt.getText().toString().trim().equals("") ||
//                        Double.parseDouble(hci_no_txt.getText().toString().trim()) != 0 ||
                        !hci_no_txt.getText().toString().trim().equals("0")) {
                    recpNo = hci_no_txt.getText().toString().trim();
                } else
                    recpNo = "";
                //   loadPickupRemarks(recpNo);
            }
        });


        hci_no_txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (dupRecptNoValStatus.equals("1") && (rec_pos.equals("hciNo")) || dupRecptNoValStatus.equals("2")) {
                        checkReceiptDuplicate = "";
                        etClearRecptNo = hci_no_txt;
                        checkReceiptDuplicate = hci_no_txt.getText().toString().trim();
                        if (!checkReceiptDuplicate.isEmpty()) {
                            GetReceiptDuplicateStatusAsyncTask getReceiptDuplicateStatusAsyncTask = new GetReceiptDuplicateStatusAsyncTask();
                            getReceiptDuplicateStatusAsyncTask.execute();
                        }
                    }
                }
            }
        });


        //cspin_arls      =
//        if (getIntent().hasExtra("ccode")) {

        Log.v("ReceivePayment11", TransactionSingleItemDataCenter.client_code);
        String[] ccode = TransactionSingleItemDataCenter.client_code.split(",");
        Client_code_size = ccode.length;
        for (int i = 0; i < ccode.length; i++) {
            //   if(QrfirstWord.equals("RELIANCE")){

            // }else {
            cspin_arls.add(ccode[i]);
            //  }

            //Log.v("ReceivePayment",ccode[i]);
        }

        Log.e(TAG, "ccode-->" + ccode.length);
        Log.e(TAG, "cspin_arls-->" + cspin_arls.size());

        if (ccode.length <= 0)
            ccode_spin.setVisibility(View.GONE);
        if (cspin_arls.size() <= 0)
            cspin_arls.add("0");


        Log.e(TAG, "cspin_arls>>" + cspin_arls);

//        if (!flag_client_id.equals("22")) {
        if (qr_status.equals("No")) {
            imgQrScanReceivePayment.setVisibility(View.GONE);

        } else {
            if (rec_pos.equals("sealNo")) {
                seal_no_txt.setEnabled(false);
                tvClearSealNoTxt.setVisibility(View.VISIBLE);
            }
            if (rec_pos.equals("hciNo")) {
                hci_no_txt.setEnabled(false);
                tvClearHciNoTxt.setVisibility(View.VISIBLE);
            }
            //      ccode_spin.setEnabled(true);
            ccode_spin.setEnabled(true);
            lin_req.setVisibility(View.GONE);
        }
//        }

        tvClearSealNoTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                seal_no_txt.setText("");
            }
        });

//        tvClearHciNoTxt.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hci_no_txt.setText("");
//            }
//        });


        type_arls.add(0, "Select Deposit Type");
//        Log.d("dep_type", "dep_type::" + TransactionSingleItemDataCenter.dep_type);
        type_arls.add("Burial");
        type_arls.add("Partner Bank");
        type_arls.add("Client Bank");
        type_arls.add("Vault");


        dbHandler = new DbHandler(this);

        bank_arls = dbHandler.get_list("bank_list");
//        account_id=dbHandler.getAccount_Id("bank_list");

        vault_arls = dbHandler.get_list("vault_list");

        // bvType_spin.setPrompt("Select Deposit Type");
        // bank_spin.setPrompt("Select Bank");
        // vault_spin.setPrompt("Select Vault");

        bank_arls.add(0, "Select Bank");
        vault_arls.add(0, "Select Vault");

        bvType_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i != 4) {
                    deposite_type_selected_item = i;
                    lin_vault.setVisibility(View.GONE);
                    new_deposite_block.setVisibility(View.VISIBLE);
                    if (i != 0)
                        bank_spin.setAdapter(bankSpin_adap);
                    else {
                        bank_spin.setAdapter(null);
                        acc_ids.clear();
                        accounts.clear();
                        branch_adap.notifyDataSetChanged();
//                        acc_adap.notifyDataSetChanged();
                    }
                } else {
                    lin_vault.setVisibility(View.VISIBLE);
                    new_deposite_block.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

//satz
//        pickup_time.setInputType(InputType.TYPE_NULL);
//        pickup_time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                datetime();
//            }
//        });
//

        vaultSpin_adap = new ArrayAdapter<String>(ReceivePayment.this, R.layout.spinner_items, vault_arls) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(ReceivePayment.this, R.layout.spinner_items, null);
                TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(vault_arls.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(ReceivePayment.this, R.layout.spinner_items, null);
                // if(position!=0)
                {
                    TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(vault_arls.get(position));
                }
                return convertView;
            }
        };

        bankSpin_adap = new ArrayAdapter<String>(ReceivePayment.this, R.layout.spinner_items, bank_arls) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(ReceivePayment.this, R.layout.spinner_items, null);
                TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(bank_arls.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(ReceivePayment.this, R.layout.spinner_items, null);
                {
                    TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(bank_arls.get(position));
                }
                return convertView;
            }
        };

        branch_adap = new ArrayAdapter<String>(this, R.layout.spinner_items, R.id.spinner_item_id, accounts);
        account_autocomplete.setAdapter(branch_adap);
        account_autocomplete.setThreshold(1);
        account_autocomplete.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view = View.inflate(ReceivePayment.this, R.layout.spinner_items, null);
                TextView txt = (TextView) view.findViewById(R.id.spinner_item_id);
                {
                    if (!TextUtils.isEmpty(accounts.get(position)))
                        txt.setText(accounts.get(position));
                    int index = accounts.indexOf(account_autocomplete.getText().toString());
                    account_number_auto = account_autocomplete.getText().toString();
                    Log.d("index", "bbb" + index);
                    acc_no = "" + index;
                }
                Log.d("Autocmp ID", "Auto cmp" + accounts.get(position));
            }
        });

        //    spin_acc.setAdapter(acc_adap);
        bank_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //if(i!=0)
                {
                    bank_acc_arls.clear();
                    bank_acc_arls = dbHandler.get_list_where("bank_list", bank_arls.get(i), "", "0");
                    acc_ids.clear();
                    accounts.clear();
                    for (int ii = 0; ii < bank_acc_arls.size(); ii++) {
                        //if(!branches.contains(bank_acc_arls.get(ii).getBranch_name()))
                        acc_ids.add(bank_acc_arls.get(ii).getId());
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


        bvType_spin.setAdapter(new ArrayAdapter<String>(ReceivePayment.this, android.R.layout.simple_list_item_1, android.R.id.text1, type_arls) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(ReceivePayment.this, android.R.layout.simple_list_item_1, null);

                // if(position!=0)
                {
                    TextView _txt = (TextView) convertView.findViewById(android.R.id.text1);
                    _txt.setTextColor(Color.BLACK);
                    _txt.setText(type_arls.get(position));
                }
                return convertView;
            }
        });

//        bvType_spin.setSelection(2);

        if (TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Burial")) {
            Log.d("dep_type#", "Burial");
            bvType_spin.setSelection(1);
            if (bvType_spin.getSelectedItemPosition() != 4) {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
                lin_vault.setVisibility(View.GONE);
                new_deposite_block.setVisibility(View.VISIBLE);
                if (bvType_spin.getSelectedItemPosition() != 0)
                    bank_spin.setAdapter(bankSpin_adap);
                else {
                    bank_spin.setAdapter(null);
                    acc_ids.clear();
                    accounts.clear();
                    branch_adap.notifyDataSetChanged();
//                        acc_adap.notifyDataSetChanged();
                }
            } else {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
                lin_vault.setVisibility(View.VISIBLE);
                new_deposite_block.setVisibility(View.GONE);
            }
        }
        if (TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Partner Bank")) {
            Log.d("dep_type#", "Partner Bank");
            bvType_spin.setSelection(2);
            if (bvType_spin.getSelectedItemPosition() != 4) {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();

                lin_vault.setVisibility(View.GONE);
                new_deposite_block.setVisibility(View.VISIBLE);
                if (bvType_spin.getSelectedItemPosition() != 0)
                    bank_spin.setAdapter(bankSpin_adap);
                else {
                    bank_spin.setAdapter(null);
                    acc_ids.clear();
                    accounts.clear();
                    branch_adap.notifyDataSetChanged();
//                        acc_adap.notifyDataSetChanged();
                }
            } else {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
                lin_vault.setVisibility(View.VISIBLE);
                new_deposite_block.setVisibility(View.GONE);
            }
        }
        if (TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Client Bank")) {
            Log.d("dep_type#", "Client Bank");
            bvType_spin.setSelection(3);
            if (bvType_spin.getSelectedItemPosition() != 4) {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();

                lin_vault.setVisibility(View.GONE);
                new_deposite_block.setVisibility(View.VISIBLE);
                if (bvType_spin.getSelectedItemPosition() != 0)
                    bank_spin.setAdapter(bankSpin_adap);
                else {
                    bank_spin.setAdapter(null);
                    acc_ids.clear();
                    accounts.clear();
                    branch_adap.notifyDataSetChanged();
//                        acc_adap.notifyDataSetChanged();
                }
            } else {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
                lin_vault.setVisibility(View.VISIBLE);
                new_deposite_block.setVisibility(View.GONE);
            }
        }
        if (TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Vault")) {
            Log.d("dep_type#", "Vault");

            bvType_spin.setSelection(4);

            if (bvType_spin.getSelectedItemPosition() != 4) {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();

                lin_vault.setVisibility(View.GONE);
                new_deposite_block.setVisibility(View.VISIBLE);
                if (bvType_spin.getSelectedItemPosition() != 0)
                    bank_spin.setAdapter(bankSpin_adap);
                else {
                    bank_spin.setAdapter(null);
                    acc_ids.clear();
                    accounts.clear();
                    branch_adap.notifyDataSetChanged();
//                        acc_adap.notifyDataSetChanged();
                }
            } else {
                deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
                lin_vault.setVisibility(View.VISIBLE);
                new_deposite_block.setVisibility(View.GONE);
            }
        }

        if (bvType_spin.getSelectedItemPosition() != 4) {
            deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
            lin_vault.setVisibility(View.GONE);
            new_deposite_block.setVisibility(View.VISIBLE);
            if (bvType_spin.getSelectedItemPosition() != 0)
                bank_spin.setAdapter(bankSpin_adap);
            else {
                bank_spin.setAdapter(null);
                acc_ids.clear();
                accounts.clear();
                branch_adap.notifyDataSetChanged();
//                        acc_adap.notifyDataSetChanged();
            }
        } else {
            deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
            lin_vault.setVisibility(View.VISIBLE);
            new_deposite_block.setVisibility(View.GONE);
        }

        cspin_adap = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, cspin_arls) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(ReceivePayment.this, android.R.layout.simple_list_item_1, null);
                TextView ccode_txt = (TextView) convertView.findViewById(android.R.id.text1);
                ccode_txt.setTextColor(Color.BLACK);
                ccode_txt.setText(cspin_arls.get(position));
                return convertView;
            }
        };
        vault_spin.setAdapter(vaultSpin_adap);
        ccode_spin.setAdapter(cspin_adap);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in Progress");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
        submit_btn.setOnClickListener(this);
//        can FLASH to 522 199
        transaction_pin = 0;
        activity_pass = 0;
//        ce_id = getIntent().getStringExtra("ce_id");
        ce_id = TransactionSingleItemDataCenter.ce_id_;

        Log.e(TAG, "Receivepaymentceid-->" + ce_id);

//        trans_id = getIntent().getStringExtra("trans_id");
        trans_id = TransactionSingleItemDataCenter.trans_ids;

        // test 27.7.17
        Log.e("--------Receivepayment.java--------", "------trans_id-----" + trans_id);
//        amt = getIntent().getStringExtra("amt");
        amt = TransactionSingleItemDataCenter.amts;
        //Log.e("Trans id", trans_id);
//        type = getIntent().getStringExtra("type");
        type = TransactionSingleItemDataCenter.types;
        Header.setText(TransactionSingleItemDataCenter.cust_names + ": (" + type + " )" + ", Receipt :" + strGetNoTrans);
        String mystring = "" + TransactionSingleItemDataCenter.cust_names;
        String arr[] = mystring.split(" ", 2);
        QrfirstWord = arr[0]; //Experts
        Log.e(TAG, "firstWord" + QrfirstWord);
        Log.e(TAG, "type" + type);
        ///reliance QR
        if (qr_status.equals("Yes")) {
            imgQrScanReceivePayment.setVisibility(View.VISIBLE);
            if (QrfirstWord.equals("RELIANCE") || QrfirstWord.equals("Reliance")) {
                pis_date_lin.setVisibility(View.VISIBLE);
                pis_date_txt.setEnabled(false);
            } else {
                pis_date_lin.setVisibility(View.GONE);
            }
        } else if (qr_status.equals("No")) {
            pis_date_lin.setVisibility(View.GONE);
        }
        if (qr_status.equals("No")) {
            if (strGetNoTrans > Client_code_size) {
                ccode_spin.setSelection(0);
            } else
                ccode_spin.setSelection(strGetNoTrans - 1);
        } else {
            if (QrfirstWord.equals("RELIANCE") || QrfirstWord.equals("Reliance")) {

            } else {
                //   ccode_spin.setAdapter(null);
            }

        }
        //
//        if (!flag_client_id.equals("22")) {
//        pin_status = getIntent().getStringExtra("pin_status");
        pin_status = TransactionSingleItemDataCenter.pin_statuss;
//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            device_id = Utils.getIMEI(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pickup_amount_txt.setHint(type + " Amount");
        if (Config.DEBUG) {
            //Log.d(TAG, "Ce_id: " + ce_id + ", Trans Id: " + trans_id
            //+ ", Type: " + type);
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

        receipt_status_spinner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPosition = 1;
                standardpopup = new ListPopupWindow(mContext);
                standardpopup.setAdapter(new ArrayAdapter<String>(ReceivePayment.this, R.layout.to_popupspinner, StandardremarksList));
                standardpopup.setAnchorView(receipt_status_spinner);
                standardpopup.setModal(true);
                standardpopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String remarks = StandardremarksList.get(position);
                        receipt_status_spinner.setText(remarks);
                        aditional_remarks_layout.setVisibility(View.VISIBLE);
                        childlayout2.setVisibility(View.VISIBLE);
                        LoadChildStandardRemarks(remarks);
                        if (remarks.equals("Others")) {
                            aditional_remarks_editText.setHint("Other Reasons");
                        } else if (aditional_remarks_editText.getHint().equals("Other Reasons")) {
                            aditional_remarks_editText.setHint("Additional Remarks");
                        }
                        standardpopup.dismiss();
                    }
                });
                standardpopup.show();
            }
        });

        child_remarks_receipt_status_spinner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPosition = 1;
                childstandardpopup = new ListPopupWindow(mContext);
                childstandardpopup.setAdapter(new ArrayAdapter<String>(ReceivePayment.this, R.layout.to_popupspinner, ChildStandardremarksList));
                childstandardpopup.setAnchorView(child_remarks_receipt_status_spinner);
                childstandardpopup.setModal(true);
                childstandardpopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String childremarks = ChildStandardremarksList.get(position);
                        child_remarks_receipt_status_spinner.setText(childremarks);
                        childstandardpopup.dismiss();
                        if (childremarks.equals("Others")) {
                            aditional_remarks_editText.setHint("Other Reasons");
                        } else {
                            aditional_remarks_editText.setHint("Additional Remarks");
                        }
                    }
                });
                childstandardpopup.show();
            }
        });


        ccode_spin.setPrompt("Client Code - Point Code");
        //TODO
        //code for request amount
        ccode_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Log.v("ReceivePayment","camt"+amt);
                if (!amt.equalsIgnoreCase("0")) {
                    lin_req.setVisibility(View.VISIBLE);
                    req_edt.setText("Total Request Amount: " + client_amt[i]);
                    req_edt.setEnabled(false);
                } else {
                    lin_req.setVisibility(View.GONE);
                }
                //   ccode_spin.setAdapter((SpinnerAdapter) cspin_arls);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            if (clientId.equals("2")) {
                if (source != null && !blockCharacterSet.contains(("" + source))) {
                    return "";
                }
            } else {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
            }
            return null;
        }
    };


    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setTitle("Radiant Sandesh");
        builder.setMessage("Enter No Of Transaction");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog1 = builder.create();
        dialog1.show();
        dialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = input.getText().toString();
                        if (!password.equals("") && !password.trim().equals("0")) {
                            if (Integer.parseInt(password) >= Client_code_size) {
                                if (!password.trim().equals("1")) {
                                    layout1.setVisibility(View.GONE);
                                    layout2.setVisibility(View.VISIBLE);
                                    lin_bank.setVisibility(View.GONE);
                                    lin_dep.setVisibility(View.GONE);
                                }
                                strNoTrans = Integer.parseInt(password);
                                deno();
                                listPickupAmout = new ArrayList<String>();
                                list_masterremarks = new ArrayList<String>();
                                lst_remarks = new ArrayList<String>();
                                listDepasitSlip = new ArrayList<String>();
                                listPISNo = new ArrayList<String>();
                                listPISDate = new ArrayList<String>();
                                listHCINo = new ArrayList<String>();
                                listSealTagNo = new ArrayList<String>();
                                list_ccode = new ArrayList<String>();
                                list_remarks = new ArrayList<String>();
                                listDeno = new ArrayList<String>();
                                listQrDetails = new ArrayList<String>();
                                dialog1.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please Enter the No Of Transactions as " + Client_code_size + " or above", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Enter the No Of Transaction", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

        dialog1.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    dialog1.setCancelable(true);
                    dialog1.dismiss();
                    finish();
                    startActivity(new Intent(ReceivePayment.this, Transaction.class).putExtra("ce_id", ce_id));
                    //Log.v(TAG,"Dialog Back Key Pressed");
                    return true;
                }
                return false;
            }
        });
    }

//    private void showPopup(View view) {
//        spinnerWindow = PopupHelper
//                .newBasicPopupWindow(getApplicationContext());
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View popupView = inflater.inflate(R.layout.spinner_listview, null);
//        spinnerWindow.setContentView(popupView);
//        listSpinner = (ListView) popupView.findViewById(R.id.listview);
//
//        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_items, rec_status_option);
//        listSpinner.setAdapter(spinnerAdapter);
//        spinnerWindow.setWidth(txtWidth);
//        spinnerWindow.setAnimationStyle(R.style.Animations_GrowFromTop);
//        spinnerWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//
//        listSpinner.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                view.setSelected(true);
//                receipt_status_spinner.setText(rec_status_option.get(position));
//                if (receipt_status_spinner.getText().toString().equalsIgnoreCase("Others"))
//                    layout3.setVisibility(View.VISIBLE);
//                else {
//                    layout3.setVisibility(View.GONE);
//                    //  remarks_txt.requestFocus();
//                }
//                spinnerWindow.dismiss();
//            }
//        });
//
//    }

    @SuppressLint("LongLogTag")
    @Override
    public void onClick(View v) {
        if (v == submit_btn) {

            if (duplicateRecptIsBtnEnable.equalsIgnoreCase("Yes")) {

                master_rec_status = receipt_status_spinner.getText().toString();


////////////////////////////////////  ******************************************   ////////////////////////////////
                rec_status = child_remarks_receipt_status_spinner.getText().toString();
                seconday_rec_status = child_remarks_receipt_status_spinner.getText().toString();
                secondayothers_rec_status = aditional_remarks_editText.getText().toString();
                DepositAmountDataCenter.rec_status = rec_status;
                Log.d("rec_status", "rec_status" + rec_status);
                Log.d("rec_status_static_var", "rec_status_static_var" + DepositAmountDataCenter.rec_status);


                remarks = remarks_txt.getText().toString();
                Vault_spinner_selected_position = vault_spin.getSelectedItemPosition();
                //Log.v("ReceivePayment","Difference Amount"+diff_no_txt.getText().toString()+":"+diff_amount);

                Log.e("----------Test-----getDenomationAmount()------", "" + getDenomationAmount());

                if (strNoTrans == 1) {
                    getAmountDetails();
                    Strdeno = getDenomationAmount();
                    dep_amount = dep_amount_txt.getText().toString();
//                deposit_amount_total+=Integer.parseInt(dep_amount);

                    bank_dep_slip_str = bank_dep_slip_edt.getText().toString();

                    master_rec_status = receipt_status_spinner.getText().toString();

                    if (ccode_spin.getSelectedItem() == null) {
                        if (client_code.length() == 0 || client_code == null)
                            client_code = "0";
                    } else {
                        client_code = ccode_spin.getSelectedItem().toString();
                    }
                    if (client_code.length() == 0 || client_code == null)
                        client_code = "0";
                    if (TextUtils.isEmpty(pickup_amount_txt.getText().toString())) {
                        Helper.showLongToast(getApplicationContext(),
                                "Pickup amount is required.");
                    } else if (TextUtils.isEmpty(dep_amount)) {
                        Helper.showLongToast(getApplicationContext(),
                                "Deposit amount is required.");
                    } else if (rec_status.equalsIgnoreCase("Select") || rec_status.isEmpty()) {
                        Helper.showLongToast(getApplicationContext(),
                                "Receipt status is required.");
                    } else if (seconday_rec_status.equalsIgnoreCase("Select") || seconday_rec_status.isEmpty()) {
                        Helper.showLongToast(getApplicationContext(),
                                "Secondary Remarks  is required.");
                    } else if (seconday_rec_status.equals("Others")) {
                        if (aditional_remarks_editText.getText().toString().isEmpty() || aditional_remarks_editText.getText().toString().equals("")) {
                            Helper.showLongToast(getApplicationContext(), "Other Remarks  is required.");
                        } else {
                            if (bvType_spin.getSelectedItemPosition() == 0) {
                                Helper.showLongToast(getApplicationContext(),
                                        "Select Deposit Type.");
                            } else if (flag_client_id.equals("22")
                                    && Double.parseDouble(pickup_amount_txt.getText().toString()) < 0) { // new sujith
                                Toast.makeText(getApplicationContext(), "Enter Signed Value", Toast.LENGTH_SHORT).show();
                            } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                    && seal_no_txt.getText().toString().trim().equals("")) { // new sujith
                                Toast.makeText(getApplicationContext(), "Enter Deposit Slip no.", Toast.LENGTH_SHORT).show();
                            } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                    && Double.parseDouble(seal_no_txt.getText().toString().trim()) == 0) { // new sujith
                                Toast.makeText(getApplicationContext(), "Enter Valid Deposit Slip no.", Toast.LENGTH_SHORT).show();
                            } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                    && seal_no_txt.getText().toString().trim().length() < 5) { // new sujith
                                Toast.makeText(getApplicationContext(), "Enter Minimum 5 digit Deposit Slip no.", Toast.LENGTH_SHORT).show();
                            } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                    && hci_no_txt.getText().toString().trim().equals("")
                                    || hci_no_txt.getText().toString().trim().equals("0")) { // new sujith1
                                Toast.makeText(getApplicationContext(), "Enter Valid Scratch Card no.", Toast.LENGTH_SHORT).show();
                            } else if (diff_no_txt.getText().toString().trim().charAt(0) == '-') { // test 24.3.2017...
                                Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                            } else if (Integer.parseInt(diff_no_txt.getText().toString()) > 0) {
                                Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                            } else if (ccode_spin.getSelectedItem() == null) {
                                Helper.showLongToast(getApplicationContext(), "Please select the client code");
                            } else if (diff_no_txt.getText().equals("0") || diff_amount == 0) {
                                client_code = ccode_spin.getSelectedItem().toString();
                                listPickupAmout.add(pickup_amount);
                                if (aditional_remarks_editText.getText().toString().equals("")) {
                                    lst_remarks.add(seconday_rec_status + "-" + "");
                                } else {
                                    lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                                }
                                listDepasitSlip.add(deposit_slip);
                                listPISNo.add(pis_no);
                                listPISDate.add(pis_date);
                                listHCINo.add(hci_no);
                                listSealTagNo.add(sealtag_no);
                                list_ccode.add(client_code);
                                list_masterremarks.add(master_rec_status);
                                list_remarks.add(remarks);
                                listDeno.add(Strdeno);
                                listQrDetails.add(sQRDetails);
                                checkPinStatus();
                                edtxtDeno2000.setText("");
                                edtxtDeno200.setText("");
                                edtxtDeno1000.setText("");
                                edtxtDeno500.setText("");
                                edtxtDeno100.setText("");
                                edtxtDeno50.setText("");
                                edtxtDeno20.setText("");
                                edtxtDeno10.setText("");
                                edtxtDeno5.setText("");
                                edtxtDenoCoins.setText("");
                                diff_amount = 0;
                                pickup_amount = "";
                                pickup_amount1 = "";
                                sQRDetails = "";
                            }
                        }

                    } else if (bvType_spin.getSelectedItemPosition() == 0) {
                        Helper.showLongToast(getApplicationContext(),
                                "Select Deposit Type.");
                    } else if (flag_client_id.equals("22")
                            && Double.parseDouble(pickup_amount_txt.getText().toString()) < 0) { // new sujith
                        Toast.makeText(getApplicationContext(), "Enter Signed Value", Toast.LENGTH_SHORT).show();
                    } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                            && seal_no_txt.getText().toString().trim().equals("")) { // new sujith
                        Toast.makeText(getApplicationContext(), "Enter Deposit Slip no.", Toast.LENGTH_SHORT).show();
                    } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                            && Double.parseDouble(seal_no_txt.getText().toString().trim()) == 0) { // new sujith
                        Toast.makeText(getApplicationContext(), "Enter Valid Deposit Slip no.", Toast.LENGTH_SHORT).show();
                    } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                            && seal_no_txt.getText().toString().trim().length() < 5) { // new sujith
                        Toast.makeText(getApplicationContext(), "Enter Minimum 5 digit Deposit Slip no.", Toast.LENGTH_SHORT).show();
                    } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                            && hci_no_txt.getText().toString().trim().equals("")
                            || hci_no_txt.getText().toString().trim().equals("0")) { // new sujith1
                        Toast.makeText(getApplicationContext(), "Enter Valid Scratch Card no.", Toast.LENGTH_SHORT).show();
                    } else if (diff_no_txt.getText().toString().trim().charAt(0) == '-') { // test 24.3.2017...
                        Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                    } else if (Integer.parseInt(diff_no_txt.getText().toString()) > 0) {
                        Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                    } else if (ccode_spin.getSelectedItem() == null) {
                        Helper.showLongToast(getApplicationContext(), "Please select the client code");
                    } else if (diff_no_txt.getText().equals("0") || diff_amount == 0) {
                        client_code = ccode_spin.getSelectedItem().toString();
                        listPickupAmout.add(pickup_amount);
                        if (aditional_remarks_editText.getText().toString().equals("")) {
                            lst_remarks.add(seconday_rec_status + "-" + "");
                        } else {
                            lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                        }
                        listDepasitSlip.add(deposit_slip);
                        listPISNo.add(pis_no);
                        listPISDate.add(pis_date);
                        listHCINo.add(hci_no);
                        listSealTagNo.add(sealtag_no);
                        list_ccode.add(client_code);
                        list_masterremarks.add(master_rec_status);
                        listDeno.add(Strdeno);
                        listQrDetails.add(sQRDetails);
                        checkPinStatus();
                        edtxtDeno2000.setText("");
                        edtxtDeno200.setText("");
                        edtxtDeno1000.setText("");
                        edtxtDeno500.setText("");
                        edtxtDeno100.setText("");
                        edtxtDeno50.setText("");
                        edtxtDeno20.setText("");
                        edtxtDeno10.setText("");
                        edtxtDeno5.setText("");
                        edtxtDenoCoins.setText("");
                        diff_amount = 0;
                        pickup_amount = "";
                        pickup_amount1 = "";
                        sQRDetails = "";
                    }


                } else {
                    if (strNoTrans > strGetNoTrans) {
                        getAmountDetails();
                        if (TextUtils.isEmpty(pickup_amount_txt.getText().toString())) {
                            Helper.showLongToast(getApplicationContext(), "Pickup amount is required.");
                        } else if (rec_status.equalsIgnoreCase("Select") || rec_status.isEmpty()) {
                            Helper.showLongToast(getApplicationContext(), "Receipt status is required.");
                        } else if (seconday_rec_status.equalsIgnoreCase("Select") || seconday_rec_status.isEmpty()) {
                            Helper.showLongToast(getApplicationContext(),
                                    "Secondary Remarks  is required.");
                        } else if (seconday_rec_status.equals("Others")) {
                            if (aditional_remarks_editText.getText().toString().isEmpty() || aditional_remarks_editText.getText().toString().equals("")) {
                                Helper.showLongToast(getApplicationContext(), "Other Remarks  is required.");
                            } else {
                                if (flag_client_id.equals("22")
                                        && Double.parseDouble(pickup_amount_txt.getText().toString()) < 0) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Signed Value", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && seal_no_txt.getText().toString().trim().equals("")) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Deposit Slip no.", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && Double.parseDouble(seal_no_txt.getText().toString().trim()) == 0) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Valid Deposit Slip no.", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && seal_no_txt.getText().toString().trim().length() < 5) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Minimum 5 digit Deposit Slip no.", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && hci_no_txt.getText().toString().trim().equals("")
                                        || hci_no_txt.getText().toString().trim().equals("0")) { // new sujith1
                                    Toast.makeText(getApplicationContext(), "Enter Valid Scratch Card no.", Toast.LENGTH_SHORT).show();
                                } else if (diff_no_txt.getText().toString().trim().charAt(0) == '-') { // test 24.3.2017...
                                    Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                                } else if (Integer.parseInt(diff_no_txt.getText().toString()) > 0) {
                                    Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                                } else if (ccode_spin.getSelectedItem() == null) {
                                    Helper.showLongToast(getApplicationContext(), "Please select the client code");
                                } else if (diff_no_txt.getText().toString().trim().equals("0") || diff_amount == 0) {

                                    client_code = ccode_spin.getSelectedItem().toString();
                                    Strdeno = getDenomationAmount();
                                    //Log.i(TAG, "Strdeno" + Strdeno);
                                    strGetNoTrans = strGetNoTrans + 1;
                                    Header.setText(TransactionSingleItemDataCenter.cust_names + ": (" + type + " )" + ", Receipt :" + strGetNoTrans);
//                            if (!flag_client_id.equals("22")) {
                                    if (qr_status.equals("No")) {
                                        if (strGetNoTrans > Client_code_size) {
                                            ccode_spin.setSelection(0);
                                        } else
                                            ccode_spin.setSelection(strGetNoTrans - 1);
                                    }
//                            else
//                            ccode_spin.setAdapter(null);
                                    //Log.i(TAG, "strGetNoTrans" + strGetNoTrans);
                                    listPickupAmout.add(pickup_amount);
                                    pickup_amount = "";
                                    if (TextUtils.isEmpty(seconday_rec_status) || seconday_rec_status.equalsIgnoreCase("others"))
                                        //   lst_remarks.add(remarks);
                                        lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                                    else
                                        lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                                    listDepasitSlip.add(deposit_slip);
                                    listPISNo.add(pis_no);
                                    listPISDate.add(pis_date);
                                    listHCINo.add(hci_no);
                                    listSealTagNo.add(sealtag_no);
                                    list_ccode.add(client_code);
                                    list_masterremarks.add(master_rec_status);
                                    listDeno.add(Strdeno);
                                    listQrDetails.add(sQRDetails);
                                    pickup_amount_txt.setText("");
                                    dep_no_txt.setText("");
                                    diff_no_txt.setText("");
                                    pis_no_txt.setText("");
                                    pis_date_txt.setText("");
                                    hci_no_txt.setText("");
                                    seal_no_txt.setText("");
//        ////////////////////////////////////////////
//                            if (!flag_client_id.equals("22")) {
//                                seal_no_txt.setText("");
//                            }
//     //////////////////////////////////////////////////////////

                                    if (!rec_pos.equals("sealNo")) {
                                        seal_no_txt.setText("");
                                    }
                                    if (!rec_pos.equals("hciNo")) {
                                        hci_no_txt.setText("");
                                    }
                                    remarks_txt.setText("");
                                    edtxtDeno2000.setText("");
                                    edtxtDeno200.setText("");
                                    edtxtDeno1000.setText("");
                                    edtxtDeno500.setText("");
                                    edtxtDeno100.setText("");
                                    edtxtDeno50.setText("");
                                    edtxtDeno20.setText("");
                                    edtxtDeno10.setText("");
                                    edtxtDeno5.setText("");
                                    edtxtDenoCoins.setText("");
                                    receipt_status_spinner.setText("select");
                                    child_remarks_receipt_status_spinner.setText("select");
                                    //                                   imgQrScanReceivePayment.setVisibility(View.VISIBLE);
                                    aditional_remarks_editText.setText("");
                                    layout3.setVisibility(View.GONE);
                                    //  pickup_amount_txt.requestFocus();
                                    childlayout2.setVisibility(View.GONE);
                                    diff_amount = 0;
                                    pickup_amount = "";
                                    pickup_amount1 = "";
                                    sQRDetails = "";
                                }
                            }
                        } else if (flag_client_id.equals("22")
                                && Double.parseDouble(pickup_amount_txt.getText().toString()) < 0) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Signed Value", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && seal_no_txt.getText().toString().trim().equals("")) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Deposit Slip no.", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && Double.parseDouble(seal_no_txt.getText().toString().trim()) == 0) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Valid Deposit Slip no.", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && seal_no_txt.getText().toString().trim().length() < 5) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Minimum 5 digit Deposit Slip no.", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && hci_no_txt.getText().toString().trim().equals("")
                                || hci_no_txt.getText().toString().trim().equals("0")) { // new sujith1
                            Toast.makeText(getApplicationContext(), "Enter Valid Scratch Card no.", Toast.LENGTH_SHORT).show();
                        } else if (diff_no_txt.getText().toString().trim().charAt(0) == '-') { // test 24.3.2017...
                            Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                        } else if (Integer.parseInt(diff_no_txt.getText().toString()) > 0) {
                            Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                        } else if (ccode_spin.getSelectedItem() == null) {
                            Helper.showLongToast(getApplicationContext(), "Please select the client code");
                        } else if (diff_no_txt.getText().toString().trim().equals("0") || diff_amount == 0) {

                            client_code = ccode_spin.getSelectedItem().toString();
                            Strdeno = getDenomationAmount();
                            //Log.i(TAG, "Strdeno" + Strdeno);
                            strGetNoTrans = strGetNoTrans + 1;
                            Header.setText(TransactionSingleItemDataCenter.cust_names + ": (" + type + " )" + ", Receipt :" + strGetNoTrans);
//                            if (!flag_client_id.equals("22")) {
                            if (qr_status.equals("No")) {
                                if (strGetNoTrans > Client_code_size) {
                                    ccode_spin.setSelection(0);
                                } else
                                    ccode_spin.setSelection(strGetNoTrans - 1);
                            }
//                            else
//                            ccode_spin.setAdapter(null);
                            //Log.i(TAG, "strGetNoTrans" + strGetNoTrans);
                            listPickupAmout.add(pickup_amount);
                            pickup_amount = "";
                            if (TextUtils.isEmpty(seconday_rec_status) || seconday_rec_status.equalsIgnoreCase("others"))
                                //  lst_remarks.add(remarks);
                                lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                            else
                                lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                            listDepasitSlip.add(deposit_slip);
                            listPISNo.add(pis_no);
                            listPISDate.add(pis_date);
                            listHCINo.add(hci_no);
                            listSealTagNo.add(sealtag_no);
                            list_ccode.add(client_code);
                            list_masterremarks.add(master_rec_status);
                            listDeno.add(Strdeno);
                            listQrDetails.add(sQRDetails);
                            pickup_amount_txt.setText("");
                            dep_no_txt.setText("");
                            diff_no_txt.setText("");
                            pis_no_txt.setText("");
                            pis_date_txt.setText("");
                            hci_no_txt.setText("");
                            seal_no_txt.setText("");
//                            if (!flag_client_id.equals("22")) {
//                                seal_no_txt.setText("");
//                            }
                            if (!rec_pos.equals("sealNo")) {
                                seal_no_txt.setText("");
                            }
                            if (!rec_pos.equals("hciNo")) {
                                hci_no_txt.setText("");
                            }
                            remarks_txt.setText("");
                            edtxtDeno2000.setText("");
                            edtxtDeno200.setText("");
                            edtxtDeno1000.setText("");
                            edtxtDeno500.setText("");
                            edtxtDeno100.setText("");
                            edtxtDeno50.setText("");
                            edtxtDeno20.setText("");
                            edtxtDeno10.setText("");
                            edtxtDeno5.setText("");
                            edtxtDenoCoins.setText("");
                            receipt_status_spinner.setText("select");
                            child_remarks_receipt_status_spinner.setText("select");
                            aditional_remarks_editText.setText("");
                            //                           imgQrScanReceivePayment.setVisibility(View.VISIBLE);
                            layout3.setVisibility(View.GONE);
                            childlayout2.setVisibility(View.GONE);
                            //  pickup_amount_txt.requestFocus();

                            diff_amount = 0;
                            pickup_amount = "";
                            pickup_amount1 = "";
                            sQRDetails = "";
                        }
                    } else {
                        layout1.setVisibility(View.VISIBLE);
                        layout2.setVisibility(View.VISIBLE);
                        //	layout3.setVisibility(View.VISIBLE);
                        lin_bank.setVisibility(View.VISIBLE);
                        lin_dep.setVisibility(View.VISIBLE);
                        deno();
                        getAmountDetails();
                        dep_amount = dep_amount_txt.getText().toString();
                        bank_dep_slip_str = bank_dep_slip_edt.getText().toString();
                        remarks = remarks_txt.getText().toString();
                        master_rec_status = receipt_status_spinner.getText().toString();
                        rec_status = child_remarks_receipt_status_spinner.getText().toString();
                        seconday_rec_status = child_remarks_receipt_status_spinner.getText().toString();
                        if (TextUtils.isEmpty(pickup_amount_txt.getText().toString())) {
                            Helper.showLongToast(getApplicationContext(),
                                    "Pickup amount is required.");
                        } else if (TextUtils.isEmpty(dep_amount_txt.getText().toString())) {
                            dep_amount_txt.requestFocus();
                            Helper.showLongToast(getApplicationContext(),
                                    "Deposit amount is required.");
                        } else if (rec_status.equalsIgnoreCase("Select") || rec_status.isEmpty()) {
                            Helper.showLongToast(getApplicationContext(),
                                    "Receipt status is required.");
                        } else if (seconday_rec_status.equalsIgnoreCase("Select") || seconday_rec_status.isEmpty()) {
                            Helper.showLongToast(getApplicationContext(),
                                    "Secondary Remarks  is required.");
                        } else if (seconday_rec_status.equals("Others")) {
                            if (aditional_remarks_editText.getText().toString().isEmpty() || aditional_remarks_editText.getText().toString().equals("")) {
                                Helper.showLongToast(getApplicationContext(), "Other Remarks  is required.");
                            } else {
                                if (flag_client_id.equals("22")
                                        && Double.parseDouble(pickup_amount_txt.getText().toString()) < 0) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Signed Value", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && seal_no_txt.getText().toString().trim().equals("")) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Deposit Slip no.", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && Double.parseDouble(seal_no_txt.getText().toString().trim()) == 0) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Valid Deposit Slip no.", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && seal_no_txt.getText().toString().trim().length() < 5) { // new sujith
                                    Toast.makeText(getApplicationContext(), "Enter Minimum 5 digit Deposit Slip no.", Toast.LENGTH_SHORT).show();
                                } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                        && hci_no_txt.getText().toString().trim().equals("")
                                        || hci_no_txt.getText().toString().trim().equals("0")) { // new sujith1
                                    Toast.makeText(getApplicationContext(), "Enter Valid Scratch Card no.", Toast.LENGTH_SHORT).show();
                                } else if (diff_no_txt.getText().toString().trim().charAt(0) == '-') { // test 24.3.2017...
                                    Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                                } else if (Integer.parseInt(diff_no_txt.getText().toString()) > 0) {
                                    Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                                } else if (bvType_spin.getSelectedItemPosition() == 0) {
                                    Helper.showLongToast(getApplicationContext(),
                                            "Select Deposit Type.");
                                } else if (ccode_spin.getSelectedItem() == null) {
                                    Helper.showLongToast(getApplicationContext(), "Please select the client code");
                                } else if (diff_no_txt.getText().toString().trim().equals("0") || diff_amount == 0) {

                                    client_code = ccode_spin.getSelectedItem().toString();

                                    Strdeno = getDenomationAmount();
                                    //Log.i(TAG, "Strdeno" + Strdeno);
                                    strGetNoTrans = strGetNoTrans + 1;
                                    listPickupAmout.add(pickup_amount);
                                    if (TextUtils.isEmpty(seconday_rec_status) || seconday_rec_status.equalsIgnoreCase("others"))
                                        //   lst_remarks.add(remarks);
                                        lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                                    else
                                        lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                                    listDepasitSlip.add(deposit_slip);
                                    listPISNo.add(pis_no);
                                    listPISDate.add(pis_date);
                                    listHCINo.add(hci_no);
                                    listSealTagNo.add(sealtag_no);
                                    list_ccode.add(client_code);
                                    list_masterremarks.add(master_rec_status);
                                    listDeno.add(Strdeno);
                                    listQrDetails.add(sQRDetails);
                                    //checking pin number for confirmation
                                    checkPinStatus();
                                    edtxtDeno2000.setText("");
                                    edtxtDeno200.setText("");
                                    edtxtDeno1000.setText("");
                                    edtxtDeno500.setText("");
                                    edtxtDeno100.setText("");
                                    edtxtDeno50.setText("");
                                    edtxtDeno20.setText("");
                                    edtxtDeno10.setText("");
                                    edtxtDeno5.setText("");
                                    edtxtDenoCoins.setText("");
                                    childlayout2.setVisibility(View.GONE);
                                    diff_amount = 0;
                                    pickup_amount = "";
                                    pickup_amount1 = "";
                                    sQRDetails = "";
                                } else {
                                    Helper.showLongToast(getApplicationContext(), "Diff Amount Mismatch");
                                }
                            }
                        } else if (flag_client_id.equals("22")
                                && Double.parseDouble(pickup_amount_txt.getText().toString()) < 0) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Signed Value", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && seal_no_txt.getText().toString().trim().equals("")) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Deposit Slip no.", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && Double.parseDouble(seal_no_txt.getText().toString().trim()) == 0) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Valid Deposit Slip no.", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && seal_no_txt.getText().toString().trim().length() < 5) { // new sujith
                            Toast.makeText(getApplicationContext(), "Enter Minimum 5 digit Deposit Slip no.", Toast.LENGTH_SHORT).show();
                        } else if (flag_client_id.equals("22") && Double.parseDouble(pickup_amount_txt.getText().toString()) > 0
                                && hci_no_txt.getText().toString().trim().equals("")
                                || hci_no_txt.getText().toString().trim().equals("0")) { // new sujith1
                            Toast.makeText(getApplicationContext(), "Enter Valid Scratch Card no.", Toast.LENGTH_SHORT).show();
                        } else if (diff_no_txt.getText().toString().trim().charAt(0) == '-') { // test 24.3.2017...
                            Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                        } else if (Integer.parseInt(diff_no_txt.getText().toString()) > 0) {
                            Helper.showLongToast(getApplicationContext(), "Please check your denomination total and submit again.");
                        } else if (bvType_spin.getSelectedItemPosition() == 0) {
                            Helper.showLongToast(getApplicationContext(),
                                    "Select Deposit Type.");
                        } else if (ccode_spin.getSelectedItem() == null) {
                            Helper.showLongToast(getApplicationContext(), "Please select the client code");
                        } else if (diff_no_txt.getText().toString().trim().equals("0") || diff_amount == 0) {

                            client_code = ccode_spin.getSelectedItem().toString();

                            Strdeno = getDenomationAmount();
                            //Log.i(TAG, "Strdeno" + Strdeno);
                            strGetNoTrans = strGetNoTrans + 1;
                            listPickupAmout.add(pickup_amount);
                            if (TextUtils.isEmpty(seconday_rec_status) || seconday_rec_status.equalsIgnoreCase("others"))
                                //   lst_remarks.add(remarks);
                                lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                            else
                                lst_remarks.add(seconday_rec_status + "-" + aditional_remarks_editText.getText().toString());
                            listDepasitSlip.add(deposit_slip);
                            listPISNo.add(pis_no);
                            listPISDate.add(pis_date);
                            listHCINo.add(hci_no);
                            listSealTagNo.add(sealtag_no);
                            list_ccode.add(client_code);
                            list_masterremarks.add(master_rec_status);
                            listDeno.add(Strdeno);
                            listQrDetails.add(sQRDetails);

                            //checking pin number for confirmation

                            checkPinStatus();

                            edtxtDeno2000.setText("");
                            edtxtDeno200.setText("");
                            edtxtDeno1000.setText("");
                            edtxtDeno500.setText("");
                            edtxtDeno100.setText("");
                            edtxtDeno50.setText("");
                            edtxtDeno20.setText("");
                            edtxtDeno10.setText("");
                            edtxtDeno5.setText("");
                            edtxtDenoCoins.setText("");
                            diff_amount = 0;
                            pickup_amount = "";
                            pickup_amount1 = "";
                            sQRDetails = "";
                        } else {
                            Helper.showLongToast(getApplicationContext(), "Diff Amount Mismatch");
                        }

                    }
                }
            } else {
                ShowDuplicateRecptNoLayout(duplicateRecptMsg);
            }
        }//submit button end

    }

    private void getAmountDetails() {
        if (!pickup_amount_txt.getText().toString().trim().equals(""))
            pickup_amount = pickup_amount_txt.getText().toString().trim();
        else
            pickup_amount = "0";
        if (!dep_no_txt.getText().toString().trim().equals(""))
            deposit_slip = dep_no_txt.getText().toString().trim();
        else
            deposit_slip = "0";
        if (!pis_no_txt.getText().toString().trim().equals(""))
            pis_no = pis_no_txt.getText().toString().trim();
        else
            pis_no = "0";
        if (!pis_date_txt.getText().toString().trim().equals(""))
            pis_date = pis_date_txt.getText().toString().trim();
        else
            pis_date = "0";
        if (!hci_no_txt.getText().toString().trim().equals(""))
            hci_no = hci_no_txt.getText().toString().trim();
        else
            hci_no = "0";
        if (!seal_no_txt.getText().toString().trim().equals(""))
            sealtag_no = seal_no_txt.getText().toString().trim();
        else
            sealtag_no = "0";
        remarks = remarks_txt.getText().toString();
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

        if (bvType_spin.getSelectedItemPosition() != 0) {
            typ = type_arls.get(bvType_spin.getSelectedItemPosition());
            ReceivePayment.deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
        } else
            typ = "";
        if (bank_spin.getAdapter() != null)
            if (bank_spin.getSelectedItemPosition() != 0) {

                bank_nam = "" + bank_arls.get(bank_spin.getSelectedItemPosition());//    bank_nam = bank_arls.get(bank_spin.getSelectedItemPosition());
            } else
                bank_nam = "";
        if (vault_spin.getSelectedItemPosition() != 0) {
            vault_name = vault_arls.get(vault_spin.getSelectedItemPosition());
            Vault_spinner_selected_position = vault_spin.getSelectedItemPosition();
        } else
            vault_name = "";
        if (strNoTrans == strGetNoTrans) {
//            if(accounts.size()!=0)
//
//            if (!TextUtils.isEmpty(accounts.get(spin_branch.getSelectedItemPosition())))
//                acc_no = acc_ids.get(spin_branch.getSelectedItemPosition());
//            else
//                acc_no = "";
//            acc_no="";

            account_autocomplete.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                        long id) {
                    int index = accounts.indexOf(account_autocomplete.getText().toString());
                    Log.d("index", "bbb2" + index);
                    acc_no = "" + index;
                }
            });

            if (!TextUtils.isEmpty(edt_dep_branch.getText().toString()))
                branch_name = edt_dep_branch.getText().toString(); //branches.get(spin_branch.getSelectedItemPosition());
            else
                branch_name = "";
            if (typ.equalsIgnoreCase("Vault")) {
                bank_nam = "";
                acc_no = "";
                branch_name = "";
            } else
                vault_name = "";
        }
        return denoAmt;
    }

    public void checkPinStatus() {
        if (pin_status.equals("1") || pin_status.equals("2")) {
            Strtrans_param = "";
            Strmaster_param = "";
            strdenoParam = "";
            strQrParam = "";
            client_code = "";
            DataBeforeConformation.pickupAmountArrayList.clear();
            DataBeforeConformation.DepositeSlipNumberArrayList.clear();
            DataBeforeConformation.PISnumberArrayList.clear();
            DataBeforeConformation.PisDateArrayList.clear();
            DataBeforeConformation.HCInumberArrayList.clear();
            DataBeforeConformation.ClientCodeArrayList.clear();
            DataBeforeConformation.SealTagArrayList.clear();
            //Log.i(TAG, "Check PIN Status" + pin_status);
            for (int i = 0; i < strNoTrans; i++) {
                if (Strtrans_param.equals("")) {
                    DataBeforeConformation.pickupAmountArrayList.add(listPickupAmout.get(i));
                    Log.d("pickupAmountArrayList::", "pickupAmountArrayList::" + DataBeforeConformation.pickupAmountArrayList);
                    DataBeforeConformation.DepositeSlipNumberArrayList.add(listDepasitSlip.get(i));
                    DataBeforeConformation.PISnumberArrayList.add(listPISNo.get(i));
                    DataBeforeConformation.HCInumberArrayList.add(listHCINo.get(i));
                    DataBeforeConformation.ClientCodeArrayList.add(list_ccode.get(i));
                    DataBeforeConformation.SealTagArrayList.add(listSealTagNo.get(i));
                    DataBeforeConformation.PisDateArrayList.add(listPISDate.get(i));
                    Strtrans_param = listPickupAmout.get(i) + "|" + listDepasitSlip.get(i) + "|" + listPISNo.get(i) + "|" + listHCINo.get(i) + "|" + listSealTagNo.get(i) + "|" + list_ccode.get(i) + "|" + lst_remarks.get(i) + "|" + listPISDate.get(i) + "|" + list_masterremarks.get(i);
                    if (strNoTrans != 1)
                        // Strtrans_param = Strtrans_param + "|" + lst_remarks.get(i) ;
                        Strtrans_param = Strtrans_param;
                    client_code = list_ccode.get(i);
                    strdenoParam = listDeno.get(i);
                    strQrParam = listQrDetails.get(i);
                    Log.e("Strtrans", i + "<strNoTrans''::------------00-----------" + Strtrans_param);
                    System.out.println("pickup data >>> " + Strtrans_param);
                } else {
                    DataBeforeConformation.pickupAmountArrayList.add(listPickupAmout.get(i));
                    Log.d("pickupAmountArrayList::", "pickupAmountArrayList::" + DataBeforeConformation.pickupAmountArrayList);
                    DataBeforeConformation.DepositeSlipNumberArrayList.add(listDepasitSlip.get(i));
                    DataBeforeConformation.PISnumberArrayList.add(listPISNo.get(i));
                    DataBeforeConformation.HCInumberArrayList.add(listHCINo.get(i));
                    DataBeforeConformation.ClientCodeArrayList.add(list_ccode.get(i));
                    DataBeforeConformation.SealTagArrayList.add(listSealTagNo.get(i));
                    DataBeforeConformation.PisDateArrayList.add(listPISDate.get(i));
                    Strtrans_param = Strtrans_param + "^" + listPickupAmout.get(i) + "|" + listDepasitSlip.get(i) + "|" + listPISNo.get(i) + "|" + listHCINo.get(i) + "|" + listSealTagNo.get(i) + "|" + list_ccode.get(i) + "|" + lst_remarks.get(i) + "|" + listPISDate.get(i) + "|" + list_masterremarks.get(i);
                    if (strNoTrans != 1)
                        //  Strtrans_param = Strtrans_param + "|" + lst_remarks.get(i) ;
                        Strtrans_param = Strtrans_param;
                    strdenoParam = strdenoParam + "^" + listDeno.get(i);
                    strQrParam = strQrParam + "^" + listQrDetails.get(i);
                    client_code = list_ccode.get(i);

                    Log.d("Strtrans", i + "<strNoTrans::------------11-----------" + Strtrans_param);
                }
            }

            showEnteredData();

        } else {
            Strtrans_param = "";
            strdenoParam = "";
            strQrParam = "";
            client_code = "";
            for (int i = 0; i < strNoTrans; i++) {
                if (Strtrans_param.equals("")) {
                    DataBeforeConformation.pickupAmountArrayList.add(listPickupAmout.get(i));
                    Log.d("pickupAmountArrayList::", "pickupAmountArrayList::" + DataBeforeConformation.pickupAmountArrayList);
                    DataBeforeConformation.DepositeSlipNumberArrayList.add(listDepasitSlip.get(i));
                    DataBeforeConformation.PISnumberArrayList.add(listPISNo.get(i));
                    DataBeforeConformation.HCInumberArrayList.add(listHCINo.get(i));
                    DataBeforeConformation.ClientCodeArrayList.add(list_ccode.get(i));
                    DataBeforeConformation.SealTagArrayList.add(listSealTagNo.get(i));
                    DataBeforeConformation.PisDateArrayList.add(listPISDate.get(i));
                    Strtrans_param = listPickupAmout.get(i) + "|" + listDepasitSlip.get(i) + "|" + listPISNo.get(i) + "|" + listHCINo.get(i) + "|" + listSealTagNo.get(i) + "|" + list_ccode.get(i) + "|" + lst_remarks.get(i) + "|" + listPISDate.get(i) + "|" + list_masterremarks.get(i);
                    if (strNoTrans != 1)
                        //    Strtrans_param = Strtrans_param + "|" + lst_remarks.get(i) ;
                        Strtrans_param = Strtrans_param;
                    Strdeno = listDeno.get(i);
                    strQrParam = listQrDetails.get(i);
                    //Log.v(TAG,"Transparams"+Strtrans_param);
                    Log.d("Strtrans_param", "Strtrans_param::------------22-----------" + Strtrans_param);
                    client_code = list_ccode.get(i);
                } else {
                    DataBeforeConformation.pickupAmountArrayList.add(listPickupAmout.get(i));
                    Log.d("pickupAmountArrayList::", "pickupAmountArrayList::" + DataBeforeConformation.pickupAmountArrayList);
                    DataBeforeConformation.DepositeSlipNumberArrayList.add(listDepasitSlip.get(i));
                    DataBeforeConformation.PISnumberArrayList.add(listPISNo.get(i));
                    DataBeforeConformation.PisDateArrayList.add(listPISDate.get(i));
                    DataBeforeConformation.HCInumberArrayList.add(listHCINo.get(i));
                    DataBeforeConformation.ClientCodeArrayList.add(list_ccode.get(i));
                    DataBeforeConformation.SealTagArrayList.add(listSealTagNo.get(i));
                    Strtrans_param = Strtrans_param + "^" + listPickupAmout.get(i) + "|" + listDepasitSlip.get(i) + "|" + listPISNo.get(i) + "|" + listHCINo.get(i) + "|" + listSealTagNo.get(i) + "|" + list_ccode.get(i) + "|" + lst_remarks.get(i) + "|" + listPISDate.get(i) + "|" + list_masterremarks.get(i);
                    if (strNoTrans != 1)
                        //    Strtrans_param = Strtrans_param + "|" + lst_remarks.get(i) ;
                        Strtrans_param = Strtrans_param;
                    //Log.v(TAG,"Transparams:"+Strtrans_param);
                    Log.d("Strtrans_param2nd", "Strtrans_param2nd::------------33-----------" + Strtrans_param);
                    Strdeno = Strdeno + "^" + listDeno.get(i);
                    strQrParam = strQrParam + "^" + listQrDetails.get(i);
                    client_code = list_ccode.get(i);

                }
            }
            showEnteredData();
        }
    }// check pin status end

    private void storeValueToDB() {
        showPinOffline();
    }

    private void showPinOffline() {
        // create a Dialog component
        dialog = new Dialog(context);

        // tell the Dialog to use the dialog.xml as it's layout
        // description
        dialog.setContentView(R.layout.receivepayment_dialog);
        dialog.setTitle("Customer PIN Verification");

        txt = (EditText) dialog.findViewById(R.id.receivepayment_dialog__txt);
        tv_info = (TextView) dialog.findViewById(R.id.receivepayment_dialog_info);
        tv_error = (TextView) dialog.findViewById(R.id.receivepayment_dialog_error);
        img_cancel = (ImageView) dialog.findViewById(R.id.img_cancel);

        final Button dialogButton = (Button) dialog
                .findViewById(R.id.receivepayment_dialog_submit_btn);
        dialogButton.setVisibility(View.GONE);

        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ReceivePayment.this, Transaction.class).putExtra("ce_id", ce_id));
            }
        });

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
        dialogButton.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {

                if (txt.getText().equals("")) {

                } else if (txt.getText().toString().equals(TransactionSingleItemDataCenter.pin_nos)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("type", type);
                    contentValues.put("ce_id", ce_id);
                    contentValues.put("trans_id", trans_id);
                    contentValues.put("no_recs", String.valueOf(strNoTrans));
                    contentValues.put("trans_param", Strtrans_param);
                    contentValues.put("deno", strdenoParam);
                    contentValues.put("dep_amount", dep_amount);
                    contentValues.put("bank_dep_slip", bank_dep_slip_str);
                    contentValues.put("rec_status", rec_status);
                    contentValues.put("remarks", remarks);
                    contentValues.put("device_id", device_id);
                    contentValues.put("dep_type", typ);
                    contentValues.put("bank_name", bank_nam);
                    contentValues.put("branch_name", branch_name);
                    contentValues.put("account_no", account_autocomplete.getText().toString());
                    contentValues.put("vault_name", vault_name);
                    //   contentValues.put("bank_dep_slip",bank_dep_slip_str);
                    //need to check for offline entry
                    if (!dbHandler.insert("ptransactions", contentValues))
                        Toast.makeText(getApplicationContext(),
                                        "This transaction already committed", Toast.LENGTH_SHORT)
                                .show();
                    else {
                        // Updating Transaction List
                        dbHandler
                                .execute("update transactions set show='no' where trans_id='"
                                        + trans_id + "'");
                        if (dbHandler.isExistRow("receipt", trans_id)) {
                            dbHandler
                                    .execute("update receipt set show='yes' where trans_id='"
                                            + trans_id + "'");
                            if (Config.DEBUG) {
                                //Log.d(TAG, "The transaction with trans_id: " + trans_id
                                //+ " updated");
                            }

                        }
                        Toast.makeText(
                                getApplicationContext(),
                                "This transaction is in pending. Please enable the internet to synchronize with server.",
                                Toast.LENGTH_LONG).show();
                        ReceivePayment.this.setResult(RESULT_OK);
                        final Intent inte = new Intent(ReceivePayment.this, Home.class);
                        inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        dialog.dismiss();
                        startActivity(inte);
                        finish();
                    }
                } else if (offf_pin_count > 1) {
                    tv_info.setText("Attempts remaining : " + --offf_pin_count);
                    tv_error.setText("Invalid pin number");
                } else {
                    final Intent inte = new Intent(ReceivePayment.this, Home.class);
                    inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(inte);
                    dialog.dismiss();
                }

            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }


    private void showPinEntryDialog() {

        if (pin_status.equals("1")) {
            // create a Dialog component
            dialog = new Dialog(context);
            // tell the Dialog to use the dialog.xml as it's layout
            // description
            dialog.setContentView(R.layout.receivepayment_dialog);
            dialog.setTitle("Customer PIN Verification");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            txt = (EditText) dialog.findViewById(R.id.receivepayment_dialog__txt);
            tv_info = (TextView) dialog.findViewById(R.id.receivepayment_dialog_info);
            tv_error = (TextView) dialog.findViewById(R.id.receivepayment_dialog_error);
            img_cancel = (ImageView) dialog.findViewById(R.id.img_cancel);
            final Button dialogButton = (Button) dialog.findViewById(R.id.receivepayment_dialog_submit_btn);
            dialogButton.setVisibility(View.GONE);

            img_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    final Intent inte = new Intent(getApplicationContext(), Transaction.class);
                    inte.putExtra("ce_id", ce_id);
                    startActivity(inte);
                }
            });

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
            dialogButton.setOnClickListener(new OnClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void onClick(View v) {
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                    if (txt.getText().equals("")) {

                    } else {
                        if (transaction_pin < 2) {
                            gpsTracker = new GPSTracker(context);
                            if (gpsTracker.canGetLocation()) {
                                latitude = gpsTracker.getLatitude();
                                longitude = gpsTracker.getLongitude();
                                lat = (int) (latitude * 1E6);
                                lon = (int) (longitude * 1E6);
                                //Log.i("Lat & Lon :", lat + "," + lat);
                                params = new ArrayList<BasicNameValuePair>();
                                params.add(new BasicNameValuePair("opt", "check_cpin"));
                                params.add(new BasicNameValuePair("ce_id", ce_id));
                                params.add(new BasicNameValuePair("trans_id", trans_id));
                                params.add(new BasicNameValuePair("cpin", txt.getText().toString()));
                                params.add(new BasicNameValuePair("attempt", String.valueOf(transaction_pin)));
                                params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
                                params.add(new BasicNameValuePair("lon", String.valueOf(lon)));
                                params.add(new BasicNameValuePair("IMIE", String.valueOf(Utils.getSIMnumber(context))));
                                Log.d("ReceivePayment", "::" + Utils.getSIMnumber(context));
                                params.add(new BasicNameValuePair("final", "1"));
                                getJson = new GetJson(ReceivePayment.this, new CallbackInterface() {
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
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
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
                            inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(inte);
                            finish();

                        }
                    }
                }
            });
            dialog.show();
        } else {
            final AlertDialog alertDialog = new AlertDialog.Builder(ReceivePayment.this).create();
            final View signView_layout = View.inflate(ReceivePayment.this, R.layout.sign_inc, null);
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
                    File file = new File(getBaseContext().getFilesDir().getPath()/*Environment.getExternalStorageDirectory().getPath()*/ + File.separator + trans_id + ".jpeg");

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
        Log.d("TEST_ITEMS", "dep_type::" + typ);

        Log.d("TEST_ITEMS", "bank_name_id::" + bank_nam);
        Log.d("TEST_ITEMS", "DepositAmountDataCenter.bank_nam_data::" + DepositAmountDataCenter.bank_nam_data);
        Log.d("TEST_ITEMS", "DepositAmountDataCenter.account_autocomplete_data::" + DepositAmountDataCenter.account_autocomplete_data);
        Log.d("TEST_ITEMS", "DepositAmountDataCenter.branch_name_data::" + DepositAmountDataCenter.branch_name_data);


        Log.d("TEST_ITEMS", "branch_name::" + edt_dep_branch.getText().toString());

        Log.d("TEST_ITEMS", "account_no_old::" + acc_no);

        Log.d("TEST_ITEMS", "accounts_autocomplete::" + account_autocomplete.getText());
        Log.d("TEST_ITEMS", "strdenoParam::" + strdenoParam);

        account_id_string = dbHandler.getAccount_Id("bank_list", account_number_auto);

        Log.d("", "account_id_result::" + account_id);

//        if(account_id.equals("100")) {

        if (Utils.isInternetAvailable(getApplicationContext())) {
            //	progressDialog.show();

            gpsTracker = new GPSTracker(context);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                lat = (int) (latitude * 1E6);
                lon = (int) (longitude * 1E6);
            }


            params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("opt", "rec_info"));
            params.add(new BasicNameValuePair("type", type));
            params.add(new BasicNameValuePair("ce_id", ce_id));
            params.add(new BasicNameValuePair("trans_id", trans_id));
            //Log.e("test", "" + strNoTrans);
            params.add(new BasicNameValuePair("no_recs", String.valueOf(strNoTrans)));
            //Log.e("test1", Strtrans_param);
            params.add(new BasicNameValuePair("trans_param", Strtrans_param));
            //Log.e("test2", strdenoParam);
            params.add(new BasicNameValuePair("deno", strdenoParam));
            params.add(new BasicNameValuePair("dep_type", typ));
            params.add(new BasicNameValuePair("bank_name", bank_nam));
            params.add(new BasicNameValuePair("branch_name", edt_dep_branch.getText().toString()));
            params.add(new BasicNameValuePair("account_no", account_id_string));
            params.add(new BasicNameValuePair("vault_name", vault_name));
            int total_dep_amount = 0;
            for (int i = 0; i < DataBeforeConformation.pickupAmountArrayList.size(); i++) {
                total_dep_amount += Integer.parseInt(DataBeforeConformation.pickupAmountArrayList.get(i));
                Log.d("total_dep_amount", "total_dep_amount" + total_dep_amount);
            }
            params.add(new BasicNameValuePair("dep_amount", total_dep_amount + ""));
            params.add(new BasicNameValuePair("rec_status", rec_status));
            params.add(new BasicNameValuePair("master", master_rec_status));
            params.add(new BasicNameValuePair("remarks", remarks));
            params.add(new BasicNameValuePair("device_id", device_id));
            params.add(new BasicNameValuePair("client_code", client_code));
            //TODO
            params.add(new BasicNameValuePair("deposit_slip_no", bank_dep_slip_str));
            //Log.d("ReceivePayment",img_path);
            params.add(new BasicNameValuePair("final", "1"));
            params.add(new BasicNameValuePair("Latitude", "" + latitude));
            params.add(new BasicNameValuePair("Longitude", "" + longitude));
            params.add(new BasicNameValuePair("qr_details", strQrParam));


            Log.e(TAG, "opt->" + "rec_info");
            Log.e(TAG, "type->" + type);
            Log.e(TAG, "ce_id->" + ce_id);
            Log.e(TAG, "trans_id->" + trans_id);
            Log.e(TAG, "no_recs->" + String.valueOf(strNoTrans));
            Log.e(TAG, "trans_param->" + Strtrans_param);
            Log.e(TAG, "deno->" + strdenoParam);
            Log.e(TAG, "dep_type->" + typ);
            Log.e(TAG, "bank_name->" + bank_nam);
            Log.e(TAG, "branch_name->" + edt_dep_branch.getText().toString());
            Log.e(TAG, "account_no->" + account_id_string);
            Log.e(TAG, "vault_name->" + vault_name);
            Log.e(TAG, "dep_amount->" + total_dep_amount);
            Log.e(TAG, "rec_status->" + rec_status);
            Log.e(TAG, "remarks->" + remarks);
            Log.e(TAG, "device_id->" + device_id);
            Log.e(TAG, "client_code->" + client_code);
            Log.e(TAG, "deposit_slip_no->" + bank_dep_slip_str);
            Log.e(TAG, "final->" + "1");
            Log.e(TAG, "qr_details->" + strQrParam);
            Log.e(TAG, "master->" + master_rec_status);


            Log.e(TAG, "sumit json request->" + params.toString());
            System.out.println("send pickup data >>>" + params.toString());
            getJson = new GetJson(ReceivePayment.this, new CallbackInterface() {
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
                                        + trans_id + "'");

                        if (dbHandler.isExistRow("receipt", trans_id)) {
                            dbHandler
                                    .execute("update receipt set show='yes' where trans_id='"
                                            + trans_id + "'");
                            if (Config.DEBUG) {
                                //Log.d(TAG, "The transaction with trans_id: "
                                //	+ trans_id + " updated");
                            }

                        }
                        if (!transrec_id.equals("")) {
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Transaction Completed with Transaction Receipt Id: "
                                                    + transrec_id, Toast.LENGTH_LONG)
                                    .show();
                        }
                        ReceivePayment.this.setResult(RESULT_OK);
                        final Intent inte = new Intent(ReceivePayment.this, Home.class);
                        inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(inte);
                        finish();
                    } else {
                        //Log.e(TAG, "Pandiyan");
                        Toast.makeText(getApplicationContext(), "Transaction Failed. Try Again with correct information.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (pin_status.equals("1"))
                getJson.execute(params);

            if (pin_status.equals("2"))
                new Reg_Async().execute();
        }
//        }
    }

    @Override
    public void onBackPressed() {
        if (dialog1.isShowing())
            dialog1.dismiss();
        final Intent inte = new Intent(ReceivePayment.this, Transaction.class);
        inte.putExtra("ce_id", ce_id);
        startActivity(inte);
        finish();
    }

    public void deno() {
//        if (getIntent().hasExtra("deno_status")) {
        if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("0")) {
            diff_no_txt.setText("0");
            diff_amount = 0;
            lin_deno.setVisibility(View.GONE);

        } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans == strNoTrans) {
            lin_deno.setVisibility(View.VISIBLE);
            cal_Deno();

        } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("2")) {

            lin_deno.setVisibility(View.VISIBLE);
            cal_Deno();
        } else {
            lin_deno.setVisibility(View.GONE);
        }
//        } else {
//        }
    }

    public void loadStandardPickupRemarks() {
        try {
            Call<PickupStandardRemarks> pickupStandardRemarksCall = apiInterface.doStandardRemarksResponse();
            pickupStandardRemarksCall.enqueue(new Callback<PickupStandardRemarks>() {
                @Override
                public void onResponse(Call<PickupStandardRemarks> call, Response<PickupStandardRemarks> response) {
                    if (response.code() == 200) {
                        if (response.body().getCode().equals("000")) {
                            receipt_status_spinner.setText("");
                            receipt_status_spinner.setHint("Select");
                            Standardremarks = response.body().getData();
                            for (int i = 0; i < Standardremarks.size(); i++) {
                                StandardremarksList.add(Standardremarks.get(i));
                            }
                        }
                    } else {
                        Toast.makeText(mContext, "Try Again Later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "no200 ->");
                    }
                }

                @Override
                public void onFailure(Call<PickupStandardRemarks> call, Throwable t) {
                    Toast.makeText(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ReceivePayment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "exception e ->" + e.getMessage());
        }
    }


    private void LoadChildStandardRemarks(String mainremarks) {
        try {
            JSONObject jsonObjectresponse = new JSONObject();
            jsonObjectresponse.put("name", mainremarks);
            JsonObject jsonObject = null;
            jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
            Call<PickupStandardRemarks> pickupStandardRemarksCall = apiInterfaceLocal.doPostStandardRemarks(jsonObject);
            pickupStandardRemarksCall.enqueue(new Callback<PickupStandardRemarks>() {
                @Override
                public void onResponse(Call<PickupStandardRemarks> call, Response<PickupStandardRemarks> response) {
                    if (response.code() == 200) {
                        if (response.body().getCode().equals("000")) {
                            ChildStandardremarks.clear();
                            ChildStandardremarksList.clear();
                            child_remarks_receipt_status_spinner.setText("");
                            child_remarks_receipt_status_spinner.setHint("Select");
                            ChildStandardremarks = response.body().getData();
                            for (int i = 0; i < ChildStandardremarks.size(); i++) {
                                ChildStandardremarksList.add(ChildStandardremarks.get(i));
                            }
                        }
                    } else {
                        Toast.makeText(mContext, "Try Again Later", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "no200 ->");
                    }
                }

                @Override
                public void onFailure(Call<PickupStandardRemarks> call, Throwable t) {
                    Toast.makeText(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ReceivePayment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "exception e ->" + e.getMessage());
        }
    }


    public void cal_Deno() {

        edtxtDenoCoins.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..1
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..2
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;

                diff_no_txt.setText(String.valueOf(diff_amount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..3
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno20.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..4
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno50.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..5
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno100.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..6
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno500.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..7
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno1000.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..8
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtxtDeno2000.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..9
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // test sujith 0
        edtxtDeno200.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //Log.v(TAG,"dep_amount_txt");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

                //test sujith..10
                if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                    diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                else
                    diff_deno200 = 0;

                int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
                if (!TextUtils.isEmpty(pickup_amount1))
                    diff_amount = -Integer.parseInt(pickup_amount1) + totalDenom;
                else diff_amount = totalDenom;
                diff_no_txt.setText(String.valueOf(diff_amount));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    class Reg_Async extends AsyncTask<Void, Void, Void> {
        String test = "";
        StringBuilder stringBuilder = new StringBuilder();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            test = "false";
            progressDialog = new ProgressDialog(ReceivePayment.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

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
                                + trans_id + "'");

                if (dbHandler.isExistRow("receipt", trans_id)) {
                    dbHandler
                            .execute("update receipt set show='yes' where trans_id='"
                                    + trans_id + "'");
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
                ReceivePayment.this.setResult(RESULT_OK);
                final Intent inte = new Intent(ReceivePayment.this, Home.class);
                inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(inte);
                finish();
            } else {
                //Log.e(TAG, "Pandiyan");
                Toast.makeText(
                        getApplicationContext(),
                        "Transaction Failed. Try Again with correct information.",
                        Toast.LENGTH_SHORT).show();
            }


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

    //satz
    public void datetime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                pickup_time.setText("Pickup Time : " + hourOfDay + " : " + minute);
            }
        }, mHour, mMinute, false);

        tpd.show();
    }
    //

    public void showEnteredData() {

        final AlertDialog alertDialog = new AlertDialog.Builder(ReceivePayment.this).create();
        alertDialog.setTitle("");
        View view = View.inflate(ReceivePayment.this, R.layout.confirm_screen, null);
        dialoglist = (ListView) view.findViewById(R.id.dialoglist);
        TextView deposite_type = (TextView) view.findViewById(R.id.deposite_type);
        TextView bank_name = (TextView) view.findViewById(R.id.bank_name);
        TextView account_number = (TextView) view.findViewById(R.id.account_number);
        TextView deposite_branch = (TextView) view.findViewById(R.id.deposite_branch);
        TextView deposite_slip_number = (TextView) view.findViewById(R.id.deposite_slip_number);
        TextView deposite_amount = (TextView) view.findViewById(R.id.deposite_amount);
        TextView remarks = (TextView) view.findViewById(R.id.remarks);

        ImageView edit_icon = (ImageView) view.findViewById(R.id.edit_icon);

        edit_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iii = new Intent(getApplicationContext(), Deposit_amount_edit_screen.class);
                startActivity(iii);
            }
        });

        LinearLayout bank_name_lin_layout = (LinearLayout) view.findViewById(R.id.bank_name_lin_layout);
        LinearLayout deposit_type_lin_layout = (LinearLayout) view.findViewById(R.id.deposit_type_lin_layout);
        LinearLayout account_lin_layout = (LinearLayout) view.findViewById(R.id.account_lin_layout);
        LinearLayout deposit_branch_lin_layout = (LinearLayout) view.findViewById(R.id.deposit_branch_lin_layout);
        LinearLayout deposit_sleep_number_lin_layout = (LinearLayout) view.findViewById(R.id.deposit_sleep_number_lin_layout);
        LinearLayout deposite_amount_lin_layout = (LinearLayout) view.findViewById(R.id.deposite_amount_lin_layout);
        LinearLayout remarks_lin_layout = (LinearLayout) view.findViewById(R.id.remarks_lin_layout);

        if (!remarks_txt.getText().toString().equals("")) {
            DepositAmountDataCenter.remarks_data = remarks_txt.getText().toString();
            remarks.setText(remarks_txt.getText().toString());
            remarks_lin_layout.setVisibility(View.VISIBLE);
        } else {
            remarks_lin_layout.setVisibility(View.GONE);
        }
        if (!dep_amount.equals("")) {
            deposite_amount.setText(dep_amount);
            DepositAmountDataCenter.dep_amount_data = dep_amount;

            deposite_amount_lin_layout.setVisibility(View.VISIBLE);
        } else {
            deposite_amount_lin_layout.setVisibility(View.GONE);
        }

        if (!bank_dep_slip_str.equals("")) {
            DepositAmountDataCenter.bank_dep_slip_str = bank_dep_slip_str;
            deposit_sleep_number_lin_layout.setVisibility(View.VISIBLE);
            deposite_slip_number.setText(bank_dep_slip_str);
        } else {
            deposit_sleep_number_lin_layout.setVisibility(View.GONE);
        }
        if (!branch_name.equals("")) {
            DepositAmountDataCenter.branch_name_data = branch_name;
            deposit_branch_lin_layout.setVisibility(View.VISIBLE);
            deposite_branch.setText(branch_name);
        } else {
            deposit_branch_lin_layout.setVisibility(View.GONE);
        }
        if (!account_autocomplete.getText().toString().equals("")) {
            DepositAmountDataCenter.account_autocomplete_data = dbHandler.getAccount_Id("bank_list", account_number_auto);
            Log.d("accoun::", "account_autocomplete_data" + DepositAmountDataCenter.account_autocomplete_data);
            account_lin_layout.setVisibility(View.VISIBLE);
            account_number.setText(account_autocomplete.getText().toString());
        } else {
            account_lin_layout.setVisibility(View.GONE);
        }
        if (!bank_nam.equals("")) {
            DepositAmountDataCenter.bank_nam_data = bank_nam;
            bank_name_lin_layout.setVisibility(View.VISIBLE);
            //  bank_name.setText(bank_arls.get(bank_spin.getSelectedItemPosition()));
            bank_name.setText(bank_arls.get(bank_spin.getSelectedItemPosition()));
        } else {
            bank_name_lin_layout.setVisibility(View.GONE);
        }

        if (!typ.equals("")) {
            DepositAmountDataCenter.typ_data = typ;
            deposit_type_lin_layout.setVisibility(View.VISIBLE);
            deposite_type.setText(typ);
        } else {
            deposit_type_lin_layout.setVisibility(View.GONE);
        }

        view.setPadding(10, 10, 10, 10);
        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent inte = new Intent(getApplicationContext(), Transaction.class);
                inte.putExtra("ce_id", ce_id);
                startActivity(inte);
            }
        });
        ConfirmScreenListItemAdapter adapter = new ConfirmScreenListItemAdapter(getApplicationContext(), DataBeforeConformation.pickupAmountArrayList, DataBeforeConformation.DepositeSlipNumberArrayList, DataBeforeConformation.PISnumberArrayList, DataBeforeConformation.HCInumberArrayList, DataBeforeConformation.SealTagArrayList, DataBeforeConformation.ClientCodeArrayList);
        dialoglist.setAdapter(adapter);
        Log.d("Adapter set", "Adapter set");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "otp_flags->" + otp_flag);
                if (Utils.isInternetAvailable(getApplicationContext())) {
//                    if(otp_flag.equals("1")&&otp_day.equals("All")){
//                        Toast.makeText(ReceivePayment.this, "you will receive Otp", Toast.LENGTH_SHORT).show();
//                        OtpService("new_otp",ce_id,trans_id,shop_id);
//                    }else if(otp_flag.equals("1")&&otp_day.equals("Sun")){
//                        OtpService("latest_otp",ce_id,trans_id,shop_id);
//                    } else {
//                        showPinEntryDialog();
//                    }

                    if (otp_day.equals("") || otp_day.equals("Standard")) {
                        showPinEntryDialog();
                    }
                    /////****Axis mobile OTP ******
                    else if (otp_day.equals("AxisTransaction")) {

                        mobileno_OTP_diaglog(otp_day, ce_id, trans_id, shop_id, apiInterface);

                    } else {
                        String URL = "";
                        String Urlstatus = SharedPreference.getDefaults(ReceivePayment.this, ConstantValues.TAG_URLVALIDATE);
                        if (Urlstatus.equals("dontswap")) {
                            OtpService("" + otp_day, ce_id, trans_id, shop_id, apiInterface);
                        } else if (Urlstatus.equals("swap")) {
                            OtpService("" + otp_day, ce_id, trans_id, shop_id, apiInterfaceTwo);
                        }
                    }
                } else {
                    Toast.makeText(ReceivePayment.this, "Try Again Later", Toast.LENGTH_SHORT).show();

                    //  storeValueToDB();
                }
            }
        });
        alertDialog.show();
    }


    private class GetReceiptDuplicateStatusAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess;
        String status = "";
        Boolean connected = false;
        String Response = "";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ReceivePayment.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (Utils.isInternetAvailable(mContext)) {
                connected = true;
                String URL = "";
                String Urlstatus = SharedPreference.getDefaults(ReceivePayment.this, ConstantValues.TAG_URLVALIDATE);
                if (Urlstatus.equals("dontswap")) {
                    URL = Config.url1;
                    // Toast.makeText(ChequePickupSubmitActivity.this, "dontswap", Toast.LENGTH_SHORT).show();
                } else if (Urlstatus.equals("swap")) {
                    URL = Config.url2;
                    // Toast.makeText(ChequePickupSubmitActivity.this, "swap", Toast.LENGTH_SHORT).show();
                }
                //     String URL = Config.url1;
                PickupRecptNoDuplicationRequestData requestData = new PickupRecptNoDuplicationRequestData();
                requestData.setOpt("duplicateRecptNo");
                requestData.setCe_Id(ce_id);
                requestData.setRecptNo(checkReceiptDuplicate);
                requestData.setClientId(clientId);

                System.out.println("ReceiptDuplicate URL >>>" + URL);
                System.out.println("ReceiptDuplicate Request >>>" + requestData.constructRequestData());

                Response = new ServiceRequestPOSTImpl().requestService(URL, requestData.constructRequestData());

                System.out.println("ReceiptDuplicate Response >>>" + Response);

                if (Response != null && Response.length() > 0) {
                    try {
                        JSONObject js = new JSONObject(Response);
                        status = js.getString("status");
                        if (!status.isEmpty()) {
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
            if (isSuccess) {
                try {
                    JSONObject js = new JSONObject(Response);
                    status = js.getString("status"); // if status yes then should not show the dialogue and if the status is No then should show dialogue
                    if (status.equalsIgnoreCase("No")) {
                        duplicateRecptMsg = js.getString("msg");
                        duplicateRecptIsBtnEnable = js.getString("enable_btn");
                        ShowDuplicateRecptNoLayout(duplicateRecptMsg);
                    }
                } catch (JSONException jsE) {
                    System.out.println("JSONException" + jsE.getMessage().toString());
                    jsE.printStackTrace();
                }
            } else if (!isSuccess && connected) {
                Toast.makeText(mContext, "Communication Failure, can't reach the host. Please Try Again!", Toast.LENGTH_LONG).show();
            } else {
                if (!connected) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.request_failed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void ShowDuplicateRecptNoLayout(String msg) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layoutView = inflater.inflate(R.layout.calcel_transaction_dialog, null);

            TextView tvTitle = (TextView) layoutView.findViewById(R.id.tvTitle);
            Button no = (Button) layoutView.findViewById(R.id.no_btn);
            Button yes = (Button) layoutView.findViewById(R.id.yes_btn);

            no.setText("Cancel");
            yes.setText("Ok");
            tvTitle.setText(msg);
            tvTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));

            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.AlertTheme);
            dialogBuilder.setView(layoutView);
            final androidx.appcompat.app.AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
//            alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();

            yes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    etClearRecptNo.setText("");
                }
            });
            no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiseDetectorsAndSources(int cameraType) {
        BarcodeDetector detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, detector)
//                .setRequestedPreviewSize(640, 480)
                .setRequestedPreviewSize(1920, 1080)
                .setFacing(cameraType)
                .setAutoFocusEnabled(true) //you should add this 4
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ReceivePayment.this, new
                                String[]{android.Manifest.permission.CAMERA}, 201);
                    }
//                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    Log.e("CAMERA SOURCE", e.getMessage());
                    Toast.makeText(ReceivePayment.this, "CAMERA SOURCE", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraSource != null) {
                    try {
                        cameraSource.release();
                    } catch (NullPointerException ignored) {
                    }
                    cameraSource = null;
                }

            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    tvQRResult.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getQrValue == 0) {
                                String barcodeValue = barcodes.valueAt(0).displayValue;
                                tvQRResult.setText(barcodeValue);
                                if (sQRDetails.isEmpty())
                                    sQRDetails = barcodeValue;
                                else
                                    sQRDetails = sQRDetails + "~ " + barcodeValue;
                                alert.dismiss();
                                getQrValue = 1;
// QR

                                Log.e(TAG, "barcodeValue-->" + barcodeValue);
                                if (barcodeValue.length() > 25) {
                                    try {
                                        JSONObject js = new JSONObject(barcodeValue);
                                        String amount = "", pisdepslipno = "", banknamespin = "", subdivcode = "", custcode = "", pisdate = "";

////////////////////         //hcl no  qr text insert in textview
//                                        if (intentResult!=null){
//                                            String str=intentResult.getContents();
//                                            Log.e(TAG, "STRING-->" + str);
//                                            if (str!=null){
//                                                Log.e(TAG, "inside-->" + intentResult.getContents());
//                                                hci_no_txt.setText(intentResult.getContents());
//                                            }
//
//                                            else {
//                                                Log.e(TAG, "else-->" + intentResult.getContents());
//                                                Toast.makeText(ReceivePayment.this, "Invalid QR", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }else
//                                        {
//                                            Log.e(TAG, "outside-->" + intentResult.getContents());
////                                          super.onActivityResult(requestCode, resultCode, data);
//                                        }


//// // NEW QR
//                                        if (QrfirstWord.equals("RELIANCE") || QrfirstWord.equals("Reliance")) {
//                                            //   if (Arrays.asList(qr_json).toString().trim().matches(js.getString("subdivcode"))) {
//                                            if (qr_json.matches(js.getString("subdivcode"))) {
////                                          if(Reliancesubdivcode.matches(getIntent().getStringExtra("qr_json"))){
//                                                int indexOfAxisStop = Arrays.asList(qr_json).indexOf(js.getString("subdivcode"));
//                                                ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, cspin_arls);
//                                                ccode_spin.setAdapter(dataTypeAdapter);
//                                                ccode_spin.setSelection(indexOfAxisStop);
//
//                                                if (js.has("amount")) {
//                                                    amount = js.getString("amount");
////                                                        pickup_amount_txt.setText(amount);
//                                                }
//                                                if (js.has("pisdepslipno")) {
//                                                    pisdepslipno = js.getString("pisdepslipno");
//                                                }
//                                                if (js.has("pisdate")) {
//                                                    pisdate = js.getString("pisdate");
//                                                }
//                                                pickup_amount_txt.setText(amount);
//                                                pis_date_txt.setText(pisdate);
//
//
//                                                if (js.has("DepositSlipNumber")) {
//                                                    String DepositSlipNumber = js.getString("DepositSlipNumber");
//                                                    if (rec_pos.equals("sealNo"))
//                                                        seal_no_txt.setText(DepositSlipNumber);
//                                                    if (rec_pos.equals("hciNo"))
//                                                        hci_no_txt.setText(DepositSlipNumber);
//                                                }
//                                                if (js.has("AccountNumber")) {
//                                                    String AccountNumber = js.getString("AccountNumber");
//                                                    account_autocomplete.setText(AccountNumber);
//                                                }
//
//                                                if (clientId.equals("81")) {
//                                                    if (TransactionSingleItemDataCenter.dep_type.equals("Burial") || TransactionSingleItemDataCenter.dep_type.equals("burial")) {
//                                                        seal_no_txt.setText(pisdepslipno);
//                                                    } else {
//                                                        seal_no_txt.setText(pisdepslipno);
//                                                    }
//                                                } else if (seal_no_txt.getHint().equals("Deposit Slip No")) {
//                                                    seal_no_txt.setText(pisdepslipno);
//                                                } else if (pis_no_txt.getHint().equals("Axis Deposit Slip No")) {
//                                                    pis_no_txt.setText(pisdepslipno);
//                                                } else {
//                                                    seal_no_txt.setText(pisdepslipno);
//                                                }
//
//
//                                                expectionalqrscan = true;
//                                                ExeceptionalQRScan(true);
//
//                                            } else {
//                                                Log.e(TAG, "1");
//                                                expectionalqrscan = true;
//                                                alert.dismiss();
//                                                ExeceptionalQRScan(true);
//                                                Toast.makeText(ReceivePayment.this, "Unsupported QR or Invalid QR", Toast.LENGTH_SHORT).show();
//                                            }
//
//                              } else
//                                      if (js.getString("CC").matches(stopId)){
//
//                                               int indexOfStopId = Arrays.asList(stop_id).indexOf(js.getString("CC").trim());
//                                               ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, cspin_arls);
//                                               ccode_spin.setAdapter(dataTypeAdapter);
//                                               ccode_spin.setSelection(indexOfStopId);
//
//                                               ///hsbc
//                                               if (js.has("Amt")) {
//                                                   pickup_amount_txt.setText(js.getString("Amt"));
//                                               }
//////add hcl in hscb
//                                               if (js.getString("Hcl").equals("")) {
//                                               } else {
//                                                   hci_no_txt.setText(js.getString("Hcl"));
//                                               }
//
//                                               if (js.getString("Pis").equals("")) {
//                                               } else {
//                                                   seal_no_txt.setText(js.getString("Pis"));
//                                               }
//
//
//                                               if (js.getString("2000s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno2000.setText(js.getString("2000s"));
//                                               }
//
//                                               if (js.getString("500s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno500.setText(js.getString("500s"));
//
//                                               }
//                                               if (js.getString("200s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno200.setText(js.getString("200s"));
//
//                                               }
//                                               if (js.getString("100s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno100.setText(js.getString("100s"));
//
//                                               }
//                                               if (js.getString("50s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno50.setText(js.getString("50s"));
//
//                                               }
//
//                                               if (js.getString("20s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno20.setText(js.getString("20s"));
//
//                                               }
//
//                                               if (js.getString("10s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno10.setText(js.getString("10s"));
//                                               }
//
//                                               if (js.getString("5s").equals("0")) {
//                                               } else {
//                                                   edtxtDeno5.setText(js.getString("5s"));
//                                               }
//
//                                               if (js.getString("coins").equals("0")) {
//                                               } else {
//                                                   edtxtDenoCoins.setText(js.getString("coins"));
//                                               }
//
//                                               pickup_amount_txt.addTextChangedListener(new TextWatcher() {
//                                                   @Override
//                                                   public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                                                   }
//
//                                                   @Override
//                                                   public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                                                       if (!TextUtils.isEmpty(pickup_amount_txt.getText().toString()) && !pickup_amount_txt.getText().toString().equals("0")) {
//                                                           if (!TextUtils.isEmpty(edtxtDenoCoins.getText().toString()))
//                                                               diff_denocoins = Integer.parseInt(edtxtDenoCoins.getText().toString());
//                                                           else
//                                                               diff_denocoins = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno5.getText().toString()))
//                                                               diff_deno5 = Integer.parseInt(edtxtDeno5.getText().toString()) * 5;
//                                                           else
//                                                               diff_deno5 = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno10.getText().toString()))
//                                                               diff_deno10 = Integer.parseInt(edtxtDeno10.getText().toString()) * 10;
//                                                           else
//                                                               diff_deno10 = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno20.getText().toString()))
//                                                               diff_deno20 = Integer.parseInt(edtxtDeno20.getText().toString()) * 20;
//                                                           else
//                                                               diff_deno20 = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno50.getText().toString()))
//                                                               diff_deno50 = Integer.parseInt(edtxtDeno50.getText().toString()) * 50;
//                                                           else
//                                                               diff_deno50 = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno100.getText().toString()))
//                                                               diff_deno100 = Integer.parseInt(edtxtDeno100.getText().toString()) * 100;
//                                                           else
//                                                               diff_deno100 = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno500.getText().toString()))
//                                                               diff_deno500 = Integer.parseInt(edtxtDeno500.getText().toString()) * 500;
//                                                           else
//                                                               diff_deno500 = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno1000.getText().toString()))
//                                                               diff_deno1000 = Integer.parseInt(edtxtDeno1000.getText().toString()) * 1000;
//                                                           else
//                                                               diff_deno1000 = 0;
//                                                           if (!TextUtils.isEmpty(edtxtDeno2000.getText().toString()))
//                                                               diff_deno2000 = Integer.parseInt(edtxtDeno2000.getText().toString()) * 2000;
//                                                           else
//                                                               diff_deno2000 = 0;
//
//                                                           //test sujith..4
//                                                           if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
//                                                               diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
//                                                           else
//                                                               diff_deno200 = 0;
//
//                                                           int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;
//
//                                                           int changing_diff_amount = totalDenom - Integer.parseInt(pickup_amount_txt.getText().toString());
//                                                           diff_no_txt.setText(String.valueOf(changing_diff_amount));
//
//                                                           diff_amount = 0 - Integer.parseInt(pickup_amount_txt.getText().toString());
//                                                           pickup_amount1 = String.valueOf(-diff_amount);
////                    if (getIntent().hasExtra("deno_status"))
//                                                           if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("0")) {
//                                                               diff_no_txt.setText("0");
//                                                               diff_amount = 0;
//                                                           } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans < strNoTrans) {
//                                                               diff_no_txt.setText("0");
//                                                               diff_amount = 0;
//                                                           } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans == strNoTrans) {
//                                                               //Log.v("ReceivePayment","list of pickup amount size"+listPickupAmout.size());
//                                                               for (int cou = 0; cou < listPickupAmout.size(); cou++) {
//                                                                   pickup_amount = String.valueOf(Integer.parseInt(listPickupAmout.get(cou)) + Integer.parseInt(pickup_amount_txt.getText().toString()));
//                                                                   diff_amount = -Integer.parseInt(listPickupAmout.get(cou)) + diff_amount;
////                            deposit_amount_total=-Integer.parseInt(listPickupAmout.get(cou)) + deposit_amount_total;
//                                                                   diff_no_txt.setText(String.valueOf(diff_amount));
////                            dep_amount_txt.setText(diff_amount);
//                                                                   dep_amount_txt.setText(String.valueOf(Math.abs(diff_amount)));
//                                                                   pickup_amount1 = String.valueOf(-diff_amount);
//                                                                   Log.d("afterTextChanged", "afterTextChanged##" + pickup_amount_txt.getText());
//                                                                   //Log.v("ReceivePayment","list of pickup amount"+listPickupAmout.get(cou)+":"+pickup_amount);
//                                                               }
//                                                           }
//                                                       } else if (TextUtils.isEmpty(pickup_amount_txt.getText().toString()) || pickup_amount_txt.getText().toString().equals("0")) {
//                                                           edtxtDeno2000.setText("");
//                                                           edtxtDeno200.setText("");
//                                                           edtxtDeno1000.setText("");
//                                                           edtxtDeno500.setText("");
//                                                           edtxtDeno100.setText("");
//                                                           edtxtDeno50.setText("");
//                                                           edtxtDeno20.setText("");
//                                                           edtxtDeno10.setText("");
//                                                           edtxtDeno5.setText("");
//                                                           edtxtDenoCoins.setText("");
//                                                           diff_no_txt.setText("");
//                                                           pickup_amount = "";
//                                                           pickup_amount1 = "";
//                                                           diff_deno2000 = 0;
//                                                           diff_deno1000 = 0;
//                                                           diff_deno500 = 0;
//                                                           diff_deno200 = 0;
//                                                           diff_deno100 = 0;
//                                                           diff_deno50 = 0;
//                                                           diff_deno20 = 0;
//                                                           diff_deno10 = 0;
//                                                           diff_deno5 = 0;
//                                                           diff_denocoins = 0;
//                                                           diff_amount = 0;
//                                                       }
//                                                   }
//
//                                                   @Override
//                                                   public void afterTextChanged(Editable editable) {
//                                                       if (strGetNoTrans == 1) {
//                                                           dep_amount_txt.setText(pickup_amount_txt.getText().toString());
//                                                       }
//                                                       if (pickup_amount_txt.getText().toString().trim().equals("0")) {
//                                                           diff_no_txt.setText("0");
//                                                       }
//
//                                                       String recpNo = "";
//                                                       if (flag_client_id.equals("22") && !hci_no_txt.getText().toString().trim().equals("") ||
////                        Double.parseDouble(hci_no_txt.getText().toString().trim()) != 0 ||
//                                                               !hci_no_txt.getText().toString().trim().equals("0")) {
//                                                           recpNo = hci_no_txt.getText().toString().trim();
//                                                       } else if (flag_client_id.equals("22") && !seal_no_txt.getText().toString().trim().equals("") ||
////                        Double.parseDouble(seal_no_txt.getText().toString().trim()) != 0 ||
//                                                               !seal_no_txt.getText().toString().trim().equals("0")) {
//                                                           recpNo = seal_no_txt.getText().toString().trim();
//                                                       } else
//                                                           recpNo = "";
//
//                                                       //   loadPickupRemarks(recpNo);
//                                                   }
//                                               });
//                                               cal_Deno();
//                                               ExeceptionalQRScan(true);
//                                        }
//
//


//////////////////////////*********************  QR old start **************************////////////////////////////////////////////
//                                        //// HSBC QR
                                        if (js.has("Amt")) {
                                            ///hsbc
                                            pickup_amount_txt.setText(js.getString("Amt"));
////add hcl in hscb
                                            if (js.getString("Hcl").equals("")) {
                                            } else {
                                                hci_no_txt.setText(js.getString("Hcl"));
                                            }

                                            if (js.getString("Pis").equals("")) {
                                            } else {
                                                seal_no_txt.setText(js.getString("Pis"));
                                            }


                                            if (js.getString("2000s").equals("0")) {
                                            } else {
                                                edtxtDeno2000.setText(js.getString("2000s"));
                                            }

                                            if (js.getString("500s").equals("0")) {
                                            } else {
                                                edtxtDeno500.setText(js.getString("500s"));

                                            }
                                            if (js.getString("200s").equals("0")) {
                                            } else {
                                                edtxtDeno200.setText(js.getString("200s"));

                                            }
                                            if (js.getString("100s").equals("0")) {
                                            } else {
                                                edtxtDeno100.setText(js.getString("100s"));

                                            }
                                            if (js.getString("50s").equals("0")) {
                                            } else {
                                                edtxtDeno50.setText(js.getString("50s"));

                                            }

                                            if (js.getString("20s").equals("0")) {
                                            } else {
                                                edtxtDeno20.setText(js.getString("20s"));

                                            }

                                            if (js.getString("10s").equals("0")) {
                                            } else {
                                                edtxtDeno10.setText(js.getString("10s"));
                                            }

                                            if (js.getString("5s").equals("0")) {
                                            } else {
                                                edtxtDeno5.setText(js.getString("5s"));
                                            }

                                            if (js.getString("coins").equals("0")) {
                                            } else {
                                                edtxtDenoCoins.setText(js.getString("coins"));
                                            }

                                            if (Arrays.asList(stop_id).toString().trim().contains(js.getString("CC").trim())) {
                                                int indexOfStopId = Arrays.asList(stop_id).indexOf(js.getString("CC").trim());
                                                ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, cspin_arls);
                                                ccode_spin.setAdapter(dataTypeAdapter);
                                                ccode_spin.setSelection(indexOfStopId);
                                            }
                                            pickup_amount_txt.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                                    //Log.v(TAG,"dep_amount_txt");
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                                    if (!TextUtils.isEmpty(pickup_amount_txt.getText().toString()) && !pickup_amount_txt.getText().toString().equals("0")) {
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

                                                        //test sujith..4
                                                        if (!TextUtils.isEmpty(edtxtDeno200.getText().toString()))
                                                            diff_deno200 = Integer.parseInt(edtxtDeno200.getText().toString()) * 200;
                                                        else
                                                            diff_deno200 = 0;

                                                        int totalDenom = diff_deno2000 + diff_deno1000 + diff_deno500 + diff_deno200 + diff_deno100 + diff_deno50 + diff_deno20 + diff_deno10 + diff_deno5 + diff_denocoins;

                                                        int changing_diff_amount = totalDenom - Integer.parseInt(pickup_amount_txt.getText().toString());
                                                        diff_no_txt.setText(String.valueOf(changing_diff_amount));

                                                        diff_amount = 0 - Integer.parseInt(pickup_amount_txt.getText().toString());
                                                        pickup_amount1 = String.valueOf(-diff_amount);
//                    if (getIntent().hasExtra("deno_status"))
                                                        if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("0")) {
                                                            diff_no_txt.setText("0");
                                                            diff_amount = 0;
                                                        } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans < strNoTrans) {
                                                            diff_no_txt.setText("0");
                                                            diff_amount = 0;
                                                        } else if (TransactionSingleItemDataCenter.deno_status.equalsIgnoreCase("1") && strGetNoTrans == strNoTrans) {
                                                            //Log.v("ReceivePayment","list of pickup amount size"+listPickupAmout.size());
                                                            for (int cou = 0; cou < listPickupAmout.size(); cou++) {
                                                                pickup_amount = String.valueOf(Integer.parseInt(listPickupAmout.get(cou)) + Integer.parseInt(pickup_amount_txt.getText().toString()));
                                                                diff_amount = -Integer.parseInt(listPickupAmout.get(cou)) + diff_amount;
////                            deposit_amount_total=-Integer.parseInt(listPickupAmout.get(cou)) + deposit_amount_total;
                                                                diff_no_txt.setText(String.valueOf(diff_amount));
/////                            dep_amount_txt.setText(diff_amount);
                                                                dep_amount_txt.setText(String.valueOf(Math.abs(diff_amount)));
                                                                pickup_amount1 = String.valueOf(-diff_amount);
                                                                Log.d("afterTextChanged", "afterTextChanged##" + pickup_amount_txt.getText());
                                                                //                                                               //Log.v("ReceivePayment","list of pickup amount"+listPickupAmout.get(cou)+":"+pickup_amount);
                                                            }
                                                        }
                                                    } else if (TextUtils.isEmpty(pickup_amount_txt.getText().toString()) || pickup_amount_txt.getText().toString().equals("0")) {
                                                        edtxtDeno2000.setText("");
                                                        edtxtDeno200.setText("");
                                                        edtxtDeno1000.setText("");
                                                        edtxtDeno500.setText("");
                                                        edtxtDeno100.setText("");
                                                        edtxtDeno50.setText("");
                                                        edtxtDeno20.setText("");
                                                        edtxtDeno10.setText("");
                                                        edtxtDeno5.setText("");
                                                        edtxtDenoCoins.setText("");
                                                        diff_no_txt.setText("");
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
                                                        diff_denocoins = 0;
                                                        diff_amount = 0;
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable editable) {
                                                    if (strGetNoTrans == 1) {
                                                        dep_amount_txt.setText(pickup_amount_txt.getText().toString());
                                                    }
                                                    if (pickup_amount_txt.getText().toString().trim().equals("0")) {
                                                        diff_no_txt.setText("0");
                                                    }

                                                    String recpNo = "";
                                                    if (flag_client_id.equals("22") && !hci_no_txt.getText().toString().trim().equals("") ||
//                        Double.parseDouble(hci_no_txt.getText().toString().trim()) != 0 ||
                                                            !hci_no_txt.getText().toString().trim().equals("0")) {
                                                        recpNo = hci_no_txt.getText().toString().trim();
                                                    } else if (flag_client_id.equals("22") && !seal_no_txt.getText().toString().trim().equals("") ||
//                        Double.parseDouble(seal_no_txt.getText().toString().trim()) != 0 ||
                                                            !seal_no_txt.getText().toString().trim().equals("0")) {
                                                        recpNo = seal_no_txt.getText().toString().trim();
                                                    } else
                                                        recpNo = "";

                                                    //   loadPickupRemarks(recpNo);
                                                }
                                            });
                                            cal_Deno();
                                            ExeceptionalQRScan(true);
                                        }
                                        // RELIANCE QR
                                        else if (QrfirstWord.equals("RELIANCE") || QrfirstWord.equals("Reliance")) {
                                            if (js.has("subdivcode")) {
                                                String Reliancesubdivcode = js.getString("subdivcode");
                                                if (Arrays.asList(stop_id).toString().trim().contains(Reliancesubdivcode.trim())) {
                                                    int indexOfAxisStop = Arrays.asList(stop_id).indexOf(Reliancesubdivcode.trim());
                                                    ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, cspin_arls);
                                                    ccode_spin.setAdapter(dataTypeAdapter);
                                                    ccode_spin.setSelection(indexOfAxisStop);
                                                    if (js.has("amount")) {
                                                        amount = js.getString("amount");
                                                    }
                                                    if (js.has("pisdepslipno")) {
                                                        pisdepslipno = js.getString("pisdepslipno");
                                                    }
                                                    if (js.has("pisdate")) {
                                                        pisdate = js.getString("pisdate");
                                                    }
                                                    pickup_amount_txt.setText(amount);
                                                    pis_date_txt.setText(pisdate);
                                                    if (clientId.equals("81")) {
                                                        if (TransactionSingleItemDataCenter.dep_type.equals("Burial") || TransactionSingleItemDataCenter.dep_type.equals("burial")) {
                                                            seal_no_txt.setText(pisdepslipno);
                                                        } else {
                                                            seal_no_txt.setText(pisdepslipno);
                                                        }
                                                    } else if (seal_no_txt.getHint().equals("Deposit Slip No")) {
                                                        seal_no_txt.setText(pisdepslipno);
                                                    } else if (pis_no_txt.getHint().equals("Axis Deposit Slip No")) {
                                                        pis_no_txt.setText(pisdepslipno);
                                                    } else {
                                                        seal_no_txt.setText(pisdepslipno);
                                                    }
                                                    expectionalqrscan = true;
                                                    ExeceptionalQRScan(true);
                                                } else {
                                                    Log.e(TAG, "1");
                                                    expectionalqrscan = true;
                                                    alert.dismiss();
                                                    Toast.makeText(ReceivePayment.this, "Unsupported QR or Invalid QR", Toast.LENGTH_SHORT).show();
                                                    ExeceptionalQRScan(true);
                                                }
                                            } else {
                                                Log.e(TAG, "2");
                                                expectionalqrscan = true;
                                                alert.dismiss();
                                                Toast.makeText(ReceivePayment.this, "Unsupported QR or Invalid QR", Toast.LENGTH_SHORT).show();
                                                ExeceptionalQRScan(true);
                                            }
                                        }
//  Axis
                                        else if (js.has("StopID")) {
                                            String StopID = js.getString("StopID");
                                            System.out.println("QR StopId >>> " + StopID);
                                            if (Arrays.asList(stop_id).toString().trim().contains(StopID.trim())) {
                                                int indexOfStopId = Arrays.asList(stop_id).indexOf(StopID.trim());
                                                ArrayAdapter<String> dataTypeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, cspin_arls);
                                                ccode_spin.setAdapter(dataTypeAdapter);
                                                ccode_spin.setSelection(indexOfStopId);

                                                if (js.has("DepositSlipNumber")) {
                                                    String DepositSlipNumber = js.getString("DepositSlipNumber");
                                                    if (rec_pos.equals("sealNo"))
                                                        seal_no_txt.setText(DepositSlipNumber);
                                                    if (rec_pos.equals("hciNo"))
                                                        hci_no_txt.setText(DepositSlipNumber);
                                                }
                                                if (js.has("AccountNumber")) {
                                                    String AccountNumber = js.getString("AccountNumber");
                                                    account_autocomplete.setText(AccountNumber);
                                                }

                                                int Denominations_2000 = 0, Denominations_500 = 0, Denominations_200 = 0, Denominations_100 = 0, Denominations_50 = 0,
                                                        Denominations_20 = 0, Denominations_10 = 0, Denominations_5 = 0, Denominations_coins = 0, TotalAmount = 0;

                                                if (js.has("TotalAmount"))
                                                    TotalAmount = js.getInt("TotalAmount");

                                                if (js.has("Denominations_2000")) {
                                                    Denominations_2000 = js.getInt("Denominations_2000");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_500")) {
                                                    Denominations_500 = js.getInt("Denominations_500");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_200")) {
                                                    Denominations_200 = js.getInt("Denominations_200");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_100")) {
                                                    Denominations_100 = js.getInt("Denominations_100");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_50")) {
                                                    Denominations_50 = js.getInt("Denominations_50");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_20")) {
                                                    Denominations_20 = js.getInt("Denominations_20");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_10")) {
                                                    Denominations_10 = js.getInt("Denominations_10");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_5")) {
                                                    Denominations_5 = js.getInt("Denominations_5");
                                                    disableDenominationFields();
                                                }
                                                if (js.has("Denominations_coins")) {
                                                    Denominations_coins = js.getInt("Denominations_coins");
                                                    disableDenominationFields();
                                                }
                                                dep_amount_txt.setText(String.valueOf(TotalAmount));
                                                pickup_amount_txt.setText(String.valueOf(TotalAmount));
                                                edtxtDeno2000.setText(String.valueOf(Denominations_2000));
                                                edtxtDeno500.setText(String.valueOf(Denominations_500));
                                                edtxtDeno200.setText(String.valueOf(Denominations_200));
                                                edtxtDeno100.setText(String.valueOf(Denominations_100));
                                                edtxtDeno50.setText(String.valueOf(Denominations_50));
                                                edtxtDeno20.setText(String.valueOf(Denominations_20));
                                                edtxtDeno10.setText(String.valueOf(Denominations_10));
                                                edtxtDeno5.setText(String.valueOf(Denominations_5));
                                                edtxtDenoCoins.setText(String.valueOf(Denominations_coins));
                                                expectionalqrscan = true;
                                                ExeceptionalQRScan(true);
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                builder.setMessage("The Stop ID and the Point ID is not matching with the QR code, please scan the " +
                                                                "correct QR code corresponding to the similar Location/Point. QR code's data are declined.!")
                                                        .setCancelable(false)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                //do things
                                                                Intent intent = new Intent(ReceivePayment.this, Transaction.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                AlertDialog alert = builder.create();
                                                alert.setCanceledOnTouchOutside(false);
                                                alert.setCancelable(false);
                                                alert.show();
                                                expectionalqrscan = true;
                                                ExeceptionalQRScan(true);
                                            }
                                        }
// // Invalid qr
                                        else {
                                            Log.e(TAG, "Invalid qr both ");
                                            expectionalqrscan = true;
                                            alert.dismiss();
                                            Toast.makeText(ReceivePayment.this, "Unsupported QR or Invalid QR", Toast.LENGTH_SHORT).show();
                                            ExeceptionalQRScan(true);
                                        }
                                    } catch (JSONException e) {
                                        Log.e(TAG, "4");
                                        Log.e(TAG, "4-->" + e.getMessage());
                                        expectionalqrscan = true;
                                        alert.dismiss();
                                        e.printStackTrace();
                                        Toast.makeText(ReceivePayment.this, "Unsupported QR or Invalid QR", Toast.LENGTH_SHORT).show();
                                        ExeceptionalQRScan(true);
                                        Log.e(TAG, "4->e" + e.getMessage());
                                        Log.e(TAG, "4->e1" + e.getLocalizedMessage());
                                    }
                                } else {
                                    Log.e(TAG, "5");
                                    ExeceptionalQRScan(true);
                                    expectionalqrscan = true;
                                    Toast.makeText(ReceivePayment.this, "Unsupported QR or Invalid QR", Toast.LENGTH_SHORT).show();
                                    if (rec_pos.equals("sealNo"))
                                        seal_no_txt.setText(barcodeValue);
                                    if (rec_pos.equals("hciNo"))
                                        hci_no_txt.setText(barcodeValue);
                                }
                            } else {
                                Log.e(TAG, "6");
                                ExeceptionalQRScan(true);
                                expectionalqrscan = true;
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "7");
                    ExeceptionalQRScan(true);
                    expectionalqrscan = true;
                    Toast.makeText(ReceivePayment.this, "getQrValue == 0 else", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void ExeceptionalQRScan(final boolean b) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imgQrScanReceivePayment.setVisibility(View.VISIBLE);
                if (!expectionalqrscan) {
                    alert.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Invalid QR! Or Scan Properly")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.setCancelable(false);
                    alert.show();
                    expectionalqrscan = true;
                } else {
                    expectionalqrscan = true;
                }
            }
        }, 8000);

    }


    public void disableDenominationFields() {
        pickup_amount_txt.setEnabled(false);
        edtxtDeno2000.setEnabled(false);
        edtxtDeno500.setEnabled(false);
        edtxtDeno200.setEnabled(false);
        edtxtDeno100.setEnabled(false);
        edtxtDeno50.setEnabled(false);
        edtxtDeno20.setEnabled(false);
        edtxtDeno10.setEnabled(false);
        edtxtDeno5.setEnabled(false);
        edtxtDenoCoins.setEnabled(false);
    }

    public void disableRelianceQrFields() {
        pickup_amount_txt.setEnabled(false);
        pis_date_txt.setEnabled(false);
        if (TransactionSingleItemDataCenter.dep_type.equals("Burial") || TransactionSingleItemDataCenter.dep_type.equals("burial")) {
            pis_no_txt.setEnabled(false);
        } else if (clientId.equals("81")) {
            seal_no_txt.setEnabled(false);
        } else {
            seal_no_txt.setEnabled(false);
        }

    }

    public void showQRcodeLayout(final int cameraType) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View view = inflater.inflate(R.layout.pop_up_qr_scanner_layout, null);

            LinearLayout close_pop = (LinearLayout) view.findViewById(R.id.close_pop);
            LinearLayout switchCameraQrPopup = (LinearLayout) view.findViewById(R.id.switchCameraQrPopup);
            LinearLayout torchQrPopup = (LinearLayout) view.findViewById(R.id.torchQrPopup);
            LinearLayout galleryQrPopup = (LinearLayout) view.findViewById(R.id.galleryQrPopup);
//
            surfaceView = (SurfaceView) view.findViewById(R.id.cameraView);
            surfaceView.setZOrderOnTop(true);    // necessary
            SurfaceHolder sfhTrackHolder = surfaceView.getHolder();
            sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
            surfaceView.setZOrderMediaOverlay(true);
            surfaceView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_show));

            TextView tvCancel = (TextView) view.findViewById(R.id.tvMsgCancel);
            LinearLayout layoutBlinkingQRcode = (LinearLayout) view.findViewById(R.id.layoutBlinkingQRcode);
            layoutBlinkingQRcode.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));

            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.AlertTheme);
            dialogBuilder.setView(view);
            alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
//            alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            alert.setCanceledOnTouchOutside(true);
            alert.setCancelable(false);
            alert.show();


            initialiseDetectorsAndSources(cameraType);
            torchQrPopup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    flashOnButton();
                }
            });
            galleryQrPopup.setVisibility(View.GONE);
            galleryQrPopup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    loadImagefromGallery();

                    detector = new BarcodeDetector.Builder(getApplicationContext())
                            .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                            .build();

                    if (!detector.isOperational()) {
//                        tvQRResult.setText("Detector initialisation failed");
                        Toast.makeText(mContext, "Detector initialisation failed", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            });
            switchCameraQrPopup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    if (cameraType == 0)
                        showQRcodeLayout(1);
                    else if (cameraType == 1)
                        showQRcodeLayout(0);
                }
            });
            close_pop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

            tvCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ReceivePayment.this, "Exception ee>" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadImagefromGallery() {
        // Create intent to Open Image applications like_primary Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    private void flashOnButton() {
        mCamera = getCamera(cameraSource);
        if (mCamera != null) {
            try {
                Camera.Parameters param = mCamera.getParameters();
                param.setFlashMode(!flashmode ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(param);
                flashmode = !flashmode;
                if (flashmode) {
                    System.out.println("Flash Switched ON");
                } else {
                    System.out.println("Flash Switched Off");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static Camera getCamera(@NonNull CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();


        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        return camera;
                    }
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);



        if (requestCode == RESULT_LOAD_IMG) {
            try {
                if (resultCode == RESULT_OK && null != data) {
                    launchMediaScanIntent();
                    imageUri = data.getData();
                    Bitmap bitmap = decodeBitmapUri(this, imageUri);

                    if (detector.isOperational() && bitmap != null) {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<Barcode> barcodes = detector.detect(frame);
                        for (int index = 0; index < barcodes.size(); index++) {
                            Barcode code = barcodes.valueAt(index);
                            String barcodeValue = code.displayValue;
                            tvQRResult.setText(barcodeValue);
                            if (sQRDetails.isEmpty())
                                sQRDetails = barcodeValue;
                            else
                                sQRDetails = sQRDetails + ", " + barcodeValue;

                            if (barcodeValue.length() > 25) {
                                try {

                                    JSONObject js = new JSONObject(barcodeValue);
                                    //////////////////         //hcl no  qr text insert in textview
                                    if (intentResult != null) {
                                        String str = intentResult.getContents();
                                        Log.e(TAG, "STRING-->" + str);
                                        if (str != null) {
                                            Log.e(TAG, "inside-->" + intentResult.getContents());
                                            hci_no_txt.setText(intentResult.getContents());
                                        } else {
                                            Log.e(TAG, "else-->" + intentResult.getContents());
                                            Toast.makeText(ReceivePayment.this, "Invalid QR", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.e(TAG, "outside-->" + intentResult.getContents());

//            super.onActivityResult(requestCode, resultCode, data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (rec_pos.equals("sealNo"))
                                    seal_no_txt.setText(barcodeValue);
                                if (rec_pos.equals("hciNo"))
                                    hci_no_txt.setText(barcodeValue);
                            }

                        }
                        if (barcodes.size() == 0) {
//                            tvQRResult.setText("No barcode could be detected. Please try again.");
                            Toast.makeText(mContext, "No barcode could be detected. Please try again.", Toast.LENGTH_LONG).show();
                            alert.dismiss();
                        }
                    } else {
//                        tvQRResult.setText("Detector initialisation failed");
                        Toast.makeText(mContext, "Detector initialisation failed", Toast.LENGTH_LONG).show();
                        alert.dismiss();
                    }
                } else {
                    Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                        .show();
            }
        }


    }



///////////***************************** CPIN *******************************************
    private void Otpdialog(final String otppin) {
        try {
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutView = inflater.inflate(R.layout.dialog_entercpin, null);
            final Button img_close = (Button) layoutView.findViewById(R.id.img_close);
            final TextView tvTitle = (TextView) layoutView.findViewById(R.id.tv_submit);
            TextView tv_resend = (TextView) layoutView.findViewById(R.id.tv_resend);
            final EditText ed_cpin = (EditText) layoutView.findViewById(R.id.ed_cpin);
            img_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // startActivity(new Intent(ReceivePayment.this, Transaction.class).putExtra("ce_id", ce_id));
                    startActivity(new Intent(ReceivePayment.this, Transaction.class));
                    finish();

                }
            });
            LinearLayout ln_submit = (LinearLayout) layoutView.findViewById(R.id.ln_submit);
            tv_resend.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(ReceivePayment.this, "Update you Soon!", Toast.LENGTH_SHORT).show();

                }
            });
            ln_submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ed_cpin.equals("") || ed_cpin.getText().toString().isEmpty()) {
                        Toast.makeText(ReceivePayment.this, "Otp is Empty", Toast.LENGTH_SHORT).show();
                    } else if (!otppin.equals(ed_cpin.getText().toString().trim())) {
                        Toast.makeText(ReceivePayment.this, "Otp is not correct", Toast.LENGTH_SHORT).show();
                    } else {
                        sendValueToServer();
                    }
                }
            });
            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.AlertTheme);
            dialogBuilder.setView(layoutView);
            final androidx.appcompat.app.AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
            alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


///////////***************************** CPIN ABOVE *******************************************

    public void OtpService(String otpstatus, String ce_id, String trans_id, String shop_id, ApiInterface apiInterfaces) {
        try {
            JSONObject jsonObjectresponse = new JSONObject();
            jsonObjectresponse.put("request_type", otpstatus);
            jsonObjectresponse.put("ce_id", ce_id);
            jsonObjectresponse.put("trans_id", trans_id);
            jsonObjectresponse.put("shop_id", shop_id);
            JsonObject jsonObject = null;
            jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
            Call<OtpResponse> otpResponseCall = apiInterfaces.doOtpResponse(jsonObject);
            Log.e(TAG, "CPIN OTP REQUEST>>>>>>>>>>>>>>>>>>--." + jsonObject);
            otpResponseCall.enqueue(new Callback<OtpResponse>() {
                @Override
                public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                    if (response.code() == 200) {
                        Log.e(TAG, "response.code" + response.code());
                        if (response.body().getStatus() == true) {
                            String otp_pin = response.body().getOtpPin();
                            String otptransaction = response.body().getTransId();
                            Otpdialog(otp_pin);
                            Log.e(TAG, "otp_pin dialog-->" + otp_pin);
                        } else {
                            Toast.makeText(ReceivePayment.this, "sorry!! Try again later", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "false");
                        Toast.makeText(ReceivePayment.this, "sorry!! Try again later", Toast.LENGTH_SHORT).show();
                        showEnteredData();
                    }
                }

                @Override
                public void onFailure(Call<OtpResponse> call, Throwable t) {
                    Log.e(TAG, "faliure ->" + t.getMessage());
                    Log.e(TAG, "request ->" + otp_flag);
                    showEnteredData();
                    Toast.makeText(ReceivePayment.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            showEnteredData();
            Toast.makeText(ReceivePayment.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "exception e ->" + e.getMessage());
        }
    }

///////////////************ RESEND OTP AXIS********************

    private class ResendOtpTask extends AsyncTask<String, String, Void> {
        private String otp_day, ceid, otp_transid, otp_mobile, otp_shopid;
        private ProgressDialog progressDialog;
        private Activity mActivity;

        public ResendOtpTask(Activity mActivity, String otp_day, String ceid, String otp_transid, String otp_mobile, String otp_shopid, ApiInterface apiInterface) {
            apiInterface = Constants.getClient().create(ApiInterface.class);
            this.otp_day = otp_day;
            this.ceid = ceid;
            this.otp_transid = otp_transid;
            this.otp_mobile = otp_mobile;
            this.otp_shopid = otp_shopid;
            this.mActivity = mActivity;
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
//

                JSONObject jsonObjectresponse = new JSONObject();
                jsonObjectresponse.put("request_type", otp_day);
                jsonObjectresponse.put("ce_id", ceid);
                jsonObjectresponse.put("trans_id", otp_transid);
                jsonObjectresponse.put("mobile_no",otp_mobile);
                jsonObjectresponse.put("shop_id",otp_shopid);
                JsonObject jsonObject = null;
                jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
                Call<OtpResponse> otpResponseCall = apiInterface.doOtpResponse(jsonObject);
                Log.e(TAG, "MOBILE  RESEND OTP REQUEST>>>>>>>>>>>>>>>>>>--." + jsonObject);
                otpResponseCall.enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                        if (response.code() == 200) {
                            Log.e(TAG, "response.code" + response.code());
                            if (response.body().getStatus() == true) {
                                String otp_pin = response.body().getOtpPin();
                                String otptransaction = response.body().getTransId();

                                OtpDialogVerify(ReceivePayment.this,otp_pin,otp_day, ceid, otp_transid, otp_mobile,otp_shopid);
                                Log.e(TAG,"RESEND OTP IS >>>>>>>>>>-->"+otp_pin);
                            } else {
                                Toast.makeText(ReceivePayment.this, "sorry!! Try again later", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "false");
                            Toast.makeText(ReceivePayment.this, "sorry!! Try again later", Toast.LENGTH_SHORT).show();
                            showEnteredData();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "faliure ->" + t.getMessage());
                        Log.e(TAG, "request mobile otp process>>>>>>>>>>>>>>>> ->" + otp_flag);
                        showEnteredData();
                        Toast.makeText(ReceivePayment.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();

            }


            return null;
        }
    }
   ////////////////// ************ RESEND OTP AXIS*****************

    public class OtpveriftTask extends AsyncTask<String, String, Void> {
        //  private ApiInterface apiInterface;
        private String otp_day, ceid, otp_transid, otp_mobile, otp_shopid;
        private ProgressDialog progressDialog;
        private Activity mActivity;
        public OtpveriftTask(Activity mActivity, String otp_day, String ceid, String otp_transid, String otp_mobile, String otp_shopid, ApiInterface apiInterface) {
            apiInterface = Constants.getClient().create(ApiInterface.class);
            this.otp_day = otp_day;
            this.ceid = ceid;
            this.otp_transid = otp_transid;
            this.otp_mobile = otp_mobile;
            this.otp_shopid = otp_shopid;
            this.mActivity = mActivity;
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... strings) {
            try {
                JSONObject jsonObjectresponse = new JSONObject();
                jsonObjectresponse.put("request_type", otp_day);
                jsonObjectresponse.put("ce_id", ceid);
                jsonObjectresponse.put("trans_id", otp_transid);
                jsonObjectresponse.put("mobile_no",otp_mobile);
                jsonObjectresponse.put("shop_id",otp_shopid);
                JsonObject jsonObject = null;
                jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
                Call<OtpResponse> otpResponseCall = apiInterface.doOtpResponse(jsonObject);
                Log.e(TAG, "MOBILE  OTP REQUEST>>>>>>>>>>>>>>>>>>" + jsonObject);
                otpResponseCall.enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                        if (response.code() == 200) {
                            Log.e(TAG, "response.code" + response.code());
                            if (response.body().getStatus() == true) {
                                String otp_pin = response.body().getOtpPin();
                                String otptransaction = response.body().getTransId();

                                OtpDialogVerify(mActivity,otp_pin,otp_day,ceid,otp_transid,otp_mobile,otp_shopid);
                                Log.e(TAG,"otp_pin dialog-->"+otp_pin);
                            } else {
                                Toast.makeText(ReceivePayment.this, "sorry!! Try again later", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "false");
                            Toast.makeText(ReceivePayment.this, "sorry!! Try again later", Toast.LENGTH_SHORT).show();
                            showEnteredData();
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {
                        Log.e(TAG, "faliure ->" + t.getMessage());
                        Log.e(TAG, "request mobile otp process>>>>>>>>>>>>>>>> ->" + otp_flag);
                        showEnteredData();
                        Toast.makeText(ReceivePayment.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();

            }


            return null;
        }
    }






    /////////////////  **************   MOBILE OTP CLASS  ******************

    private void mobileno_OTP_diaglog(final String otp_day, final String otp_ceid, final String transid, final String shopid,final ApiInterface apiInterface) {

        try {

            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutView = inflater.inflate(R.layout.dialog_mobilenumber_verify, null);
            img_close_OTP = layoutView.findViewById(R.id.img_close_OTP);
            CardView verify_mobile_no = (CardView) layoutView.findViewById(R.id.verify_mobile_no);
            final EditText et_mobile_no = (EditText) layoutView.findViewById(R.id.et_mobile_no);
            img_close_OTP.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ReceivePayment.this, Transaction.class));
                    finish();

                }
            });

            verify_mobile_no.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (et_mobile_no.equals("") || et_mobile_no.getText().toString().isEmpty()) {
                        Toast.makeText(ReceivePayment.this, "Mobile Number is Empty", Toast.LENGTH_SHORT).show();
                    } else if (et_mobile_no.getText().toString().length() < 10) {
                        Toast.makeText(ReceivePayment.this, "Mobile Number Must be 10 Digit", Toast.LENGTH_SHORT).show();
                    } else {
                        new OtpveriftTask(ReceivePayment.this, otp_day, otp_ceid, transid,  et_mobile_no.getText().toString(),shopid, apiInterface).execute();
                    }
                }
            });
            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.AlertTheme);
            dialogBuilder.setView(layoutView);
            final androidx.appcompat.app.AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
            alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
////////////////////////////////////////////////////////////////
    private void OtpDialogVerify( final Activity activity,final String otppin,final String otp_day, final String ceid,final String otp_transid, final String otp_mobile,final String otp_shopid) {

        try {
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             final   View layoutView = inflater.inflate(R.layout.dialog_loginotp_verify, null);
            final ImageView img_close = (ImageView) layoutView.findViewById(R.id.otpimg_close);
            final LinearLayout otp_resendotp = (LinearLayout) layoutView.findViewById(R.id.otp_resendotp);
            final EditText ed_cpin = (EditText) layoutView.findViewById(R.id.otp_enter);
            img_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ReceivePayment.this, Transaction.class));
                    finish();

                }
            });
            LinearLayout ln_submit = (LinearLayout) layoutView.findViewById(R.id.otp_submit);
            otp_resendotp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                   new ResendOtpTask(ReceivePayment.this, ReceivePayment.this.otp_day, ce_id, otp_transid,otp_mobile,otp_shopid, apiInterface).execute();
                    Toast.makeText(ReceivePayment.this, "Update you Soon!", Toast.LENGTH_SHORT).show();

                }
            });
            ln_submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ed_cpin.equals("") || ed_cpin.getText().toString().isEmpty()) {
                        Toast.makeText(ReceivePayment.this, "Otp is Empty", Toast.LENGTH_SHORT).show();
                    }
                    else if (!otppin.equals(ed_cpin.getText().toString().trim())) {
                        Toast.makeText(ReceivePayment.this, "Otp is not correct", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        sendValueToServer();
                    }
                }
            });
            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.AlertTheme);
            dialogBuilder.setView(layoutView);
            final androidx.appcompat.app.AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
            alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void HclNumberQR(final int cameraType) {

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View view = inflater.inflate(R.layout.pop_up_qr_scanner_layout, null);

            LinearLayout close_pop = (LinearLayout) view.findViewById(R.id.close_pop);
            LinearLayout switchCameraQrPopup = (LinearLayout) view.findViewById(R.id.switchCameraQrPopup);
            LinearLayout torchQrPopup = (LinearLayout) view.findViewById(R.id.torchQrPopup);
            LinearLayout galleryQrPopup = (LinearLayout) view.findViewById(R.id.galleryQrPopup);
//
            surfaceView = (SurfaceView) view.findViewById(R.id.cameraView);
            surfaceView.setZOrderOnTop(true);    // necessary
            SurfaceHolder sfhTrackHolder = surfaceView.getHolder();
            sfhTrackHolder.setFormat(PixelFormat.TRANSPARENT);
            surfaceView.setZOrderMediaOverlay(true);
            surfaceView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_show));

            TextView tvCancel = (TextView) view.findViewById(R.id.tvMsgCancel);
            LinearLayout layoutBlinkingQRcode = (LinearLayout) view.findViewById(R.id.layoutBlinkingQRcode);
            layoutBlinkingQRcode.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));

            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(mContext, R.style.AlertTheme);
            dialogBuilder.setView(view);
            alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
//            alert.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            alert.setCanceledOnTouchOutside(true);
            alert.setCancelable(false);
            alert.show();


            DetectorsAndSources(cameraType);
            torchQrPopup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    flashOnButton();
                }
            });
            galleryQrPopup.setVisibility(View.GONE);
            galleryQrPopup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    loadImagefromGallery();

                    detector = new BarcodeDetector.Builder(getApplicationContext())
                            .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                            .build();

                    if (!detector.isOperational()) {
//                        tvQRResult.setText("Detector initialisation failed");
                        Toast.makeText(mContext, "Detector initialisation failed", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            });
            switchCameraQrPopup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    if (cameraType == 0)
                        showQRcodeLayout(1);
                    else if (cameraType == 1)
                        showQRcodeLayout(0);
                }
            });
            close_pop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

            tvCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ReceivePayment.this, "Exception ee>" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//////////////

    private void DetectorsAndSources(int cameraType) {
        BarcodeDetector detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, detector)
//                .setRequestedPreviewSize(640, 480)
                .setRequestedPreviewSize(1920, 1080)
                .setFacing(cameraType)
                .setAutoFocusEnabled(true) //you should add this 4
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ReceivePayment.this, new
                                String[]{android.Manifest.permission.CAMERA}, 201);
                    }
//                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    Log.e("CAMERA SOURCE", e.getMessage());
                    Toast.makeText(ReceivePayment.this, "CAMERA SOURCE", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraSource != null) {
                    try {
                        cameraSource.release();
                    } catch (NullPointerException ignored) {
                    }
                    cameraSource = null;
                }

            }
        });

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    tvQRResult.post(new Runnable() {
                        @Override
                        public void run() {
                            ExeceptionalQRScan(true);
                            expectionalqrscan = true;
                            if (getQrValue ==0) {
                                String barcodeValue = barcodes.valueAt(0).displayValue;
                                hci_no_txt.setText(barcodeValue);
                                hci_no_txt.setEnabled(true);
                                alert.dismiss();
                            } else {
                                Log.e(TAG, "6");
                                ExeceptionalQRScan(true);
                                expectionalqrscan = true;
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "7");
                    ExeceptionalQRScan(true);
                    expectionalqrscan = true;
                    Toast.makeText(ReceivePayment.this, "getQrValue == 0 else", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}

