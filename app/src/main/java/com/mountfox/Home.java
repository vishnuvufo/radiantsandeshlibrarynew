package com.mountfox;

import static com.mountfox.sharedPref.ConstantValues.TAG_CEID;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mountfox.sharedPref.SharedPreference;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Home extends Activity implements OnClickListener {

    Button trans_btn, print_btn, cancel_btn, eod_btn;
    ImageView btn_editreceipt, img_changepin;
    Intent transactionIntent, receiptPrintIntent, cancelReceiptIntent, eodReceiptIntent;
    String ce_id = "";
    ListView listView;
    private ArrayList<ChangePinPojo> changePinPojos = new ArrayList<ChangePinPojo>();
    private ArrayAdapter<ChangePinPojo> changePinPojoArrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.home);
        initializeComponents();

    }

    public void initializeComponents() {
        //dbHandler   =   new DbHandler(Home.this);
        trans_btn = (Button) findViewById(R.id.trans_btn);
        print_btn = (Button) findViewById(R.id.print_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        eod_btn = (Button) findViewById(R.id.eod_btn);
        btn_editreceipt = (ImageView) findViewById(R.id.btn_editreceipt);
        img_changepin = (ImageView) findViewById(R.id.img_changepin);
        trans_btn.setOnClickListener(this);
        print_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        eod_btn.setOnClickListener(this);
        btn_editreceipt.setOnClickListener(this);
        img_changepin.setOnClickListener(this);
        ce_id = getIntent().getStringExtra("ce_id");
        ce_id = Login.ce_id_main;
        Log.d("ce_id", "ce_id_Home:::" + ce_id);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(DeviceStatusReceiver, filter);
        getSharedPreferences("mountfox", Context.MODE_PRIVATE).edit().putString("ce_id", ce_id).commit();
        SharedPreference.setDefaults(Home.this, TAG_CEID,ce_id);

    }

    private final BroadcastReceiver DeviceStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (PrinterSelection.device != null) {
                    if (device.getAddress().equals(
                            PrinterSelection.device.getAddress())) {
                        Toast.makeText(getApplicationContext(), "Printer disconnected.", Toast.LENGTH_SHORT).show();
                        if (PrinterSelection.isPrinterConnected)
                            PrinterSelection.unpairDevice();
                    }
                }
            }
        }
    };

    public void onClick(View v) {
        if (v == trans_btn) {
            transactionIntent = new Intent(this, ModeOfTransactionActivity.class);
            transactionIntent.putExtra("ce_id", ce_id);
            System.out.println("Home ce_id >>> " + ce_id);
            transactionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(transactionIntent);
            finish();
        } else if (v == print_btn) {
            receiptPrintIntent = new Intent(this, ReceiptPrint.class);
            receiptPrintIntent.putExtra("ce_id", ce_id);
            receiptPrintIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(receiptPrintIntent);
            finish();
        } else if (v == cancel_btn) {
            cancelReceiptIntent = new Intent(this, CancelReceipt.class);
            cancelReceiptIntent.putExtra("ce_id", ce_id);
            cancelReceiptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cancelReceiptIntent);
            finish();
        } else if (v == eod_btn) {
//            eodReceiptIntent = new Intent(this, EODReceipt.class);
//            eodReceiptIntent.putExtra("ce_id", ce_id);
//            eodReceiptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(eodReceiptIntent);
//            finish();
        } else if (v == btn_editreceipt) {
//            if (Utils.isInternetAvailable(Home.this)) {
//                Intent intent = new Intent(this, EditReceipt.class);
//                intent.putExtra("ce_id", ce_id);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//            } else
//                Toast.makeText(Home.this, "Internet Connection Required to Edit Receipt", Toast.LENGTH_SHORT).show();
        } else if (v == img_changepin) {
            if (Utils.isInternetAvailable(Home.this)) {
                getData();
            } else
                Toast.makeText(Home.this, "Internet Connection Required to Change Pin", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        final Dialog exitDialog = new Dialog(this);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.setContentView(R.layout.exit_alert_dialog);
        exitDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        Button yes_btn, no_btn;
        yes_btn = (Button) exitDialog.findViewById(R.id.yes_btn);
        no_btn = (Button) exitDialog.findViewById(R.id.no_btn);
        yes_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                deleteCache(getApplicationContext());
                exitDialog.dismiss();
//                finish();
                finishAffinity();

            }
        });
        no_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });
        exitDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (PrinterSelection.isPrinterConnected)
            PrinterSelection.unpairDevice();
        unregisterReceiver(DeviceStatusReceiver);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
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
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }





    public AlertDialog showAlert() {
        final AlertDialog alertDialog = new AlertDialog.Builder(Home.this).create();
        alertDialog.setTitle("Customer Pin Change");
        View view = View.inflate(Home.this, R.layout.changepindlg, null);
        listView = (ListView) view.findViewById(R.id.lst_shops);
        listView.setAdapter(changePinPojoArrayAdapter);
        alertDialog.setView(view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialog.dismiss();
                startActivity(new Intent(Home.this, ChangePinActivity.class).putExtra("ce_id", ce_id).putExtra("shop_id", changePinPojos.get(position).getShop_id()).putExtra("pin_no", changePinPojos.get(position).getPin_no()).putExtra("point_name", changePinPojos.get(position).getPoint_name()));
            }
        });
        return alertDialog;
    }

    public void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(Home.this);
        progressDialog.setTitle("Loading.....");
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();

        GetJson getJson = new GetJson(Home.this,new GetJson.CallbackInterface() {
            @Override
            public void onRequestCompleted(JSONObject object) {
                try {
                    JSONArray jsonArray = object.getJSONArray("transactions");
                    changePinPojos.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        ChangePinPojo changePinPojo = new ChangePinPojo(jsonObject1.getString("shop_id"), jsonObject1.getString("point_name"), jsonObject1.getString("cust_name"), jsonObject1.getString("type"), jsonObject1.getString("amount"), jsonObject1.getString("pin_status"), jsonObject1.getString("pin_no"));
                        changePinPojos.add(changePinPojo);
                    }

                    changePinPojoArrayAdapter = new ArrayAdapter<ChangePinPojo>(Home.this, R.layout.changepin_lst, R.id.shopid_txt, changePinPojos) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = convertView;
                            TextView txt_shop_id, txt_point_name, txt_cust_name, txt_type, txt_amount;
                            if (view == null) {
                                view = View.inflate(Home.this, R.layout.changepin_lst, null);
                                view.setTag(R.id.shopid_txt, view.findViewById(R.id.shopid_txt));
                                view.setTag(R.id.pointname_txt, view.findViewById(R.id.pointname_txt));
                                view.setTag(R.id.customername_txt, view.findViewById(R.id.customername_txt));
                                view.setTag(R.id.type_txt, view.findViewById(R.id.type_txt));
                                view.setTag(R.id.amount_txt, view.findViewById(R.id.amount_txt));
                            }
                            txt_shop_id = (TextView) view.getTag(R.id.shopid_txt);
                            txt_point_name = (TextView) view.getTag(R.id.pointname_txt);
                            txt_cust_name = (TextView) view.getTag(R.id.customername_txt);
                            txt_type = (TextView) view.getTag(R.id.type_txt);
                            txt_amount = (TextView) view.getTag(R.id.amount_txt);
                            ChangePinPojo changePinPojo = changePinPojos.get(position);
                            try {
                                txt_amount.setText(changePinPojo.getAmount());
                                txt_type.setText(changePinPojo.getType());
                                txt_cust_name.setText(changePinPojo.getCust_name());
                                txt_point_name.setText(changePinPojo.getPoint_name());
                                txt_shop_id.setText(changePinPojo.getShop_id());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return view;
                        }
                    };
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    showAlert().show();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
        ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
        basicNameValuePairs.add(new BasicNameValuePair("opt", "view_shop"));
        basicNameValuePairs.add(new BasicNameValuePair("ce_id", ce_id));
        getJson.execute(basicNameValuePairs);
    }

    public boolean ValidatePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkCameraPermission(Home.this)) {
                requestPermission(Home.this);
                return false;
            } else {
                // Move to main act

                return true;
            }
        } else {
            // Move to main act
            return true;
        }
    }

    public static boolean checkCameraPermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE},
                108);
    }




}
