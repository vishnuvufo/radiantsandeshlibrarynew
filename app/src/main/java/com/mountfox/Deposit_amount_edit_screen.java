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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class Deposit_amount_edit_screen extends Activity {

    private EditText edt_dep_branch,bank_dep_slip_edt,dep_amount_txt,bank_dep_slip_txt,branch_name_edt;
    AutoCompleteTextView accounts_autocomplete;
    public Button submit_btn;

    public int lat = 1, lon = 2;

    private Spinner bank_spin, bvType_spin,vault_spin,dep_type_spin;

    private AutoCompleteTextView account_autocomplete;

    private ArrayList<String> type_arls = new ArrayList<String>();

    private LinearLayout lin_vault,new_deposite_block;

    private ArrayAdapter<String> bankSpin_adap,branch_adap,vaultSpin_adap;

    private ArrayList<String> acc_ids = new ArrayList<String>();

    private ArrayList<String> bank_arls = new ArrayList<String>();

    private DbHandler dbHandler;

    private ArrayList<String> vault_arls = new ArrayList<String>();

    private ArrayList<String> accounts = new ArrayList<String>();

    private ArrayList<Bank_Pojo> bank_acc_arls = new ArrayList<Bank_Pojo>();


    public String acc_no="";


    private Dialog dialog;

    public String  Strtrans_param="";


    public  String client_code="",strdenoParam="",strQrParam="";

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

    private ArrayAdapter<String> cspin_adap,  cc_adap;


    private ArrayList<String> cspin_arls = new ArrayList<String>();

    private LinearLayout lin_deno, lin_req;

    public ListView dialoglist;

    public String captions = "";

    private LinearLayout layout1, layout2, layout3,  lin_bank,  lin_dep, lin_pis, lin_hci, lin_receipt, lin_dep_slip, deposite_line;

    private String[] caps_arr = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_deposit_amount_edit_screen);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Transaction in Progress");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        dbHandler = new DbHandler(this);

        bank_arls = dbHandler.get_list("bank_list");
        vault_arls = dbHandler.get_list("vault_list");

        Log.d("Vaults","Vaults::"+vault_arls);

        vault_arls.add(0, "Select Vault");


        bvType_spin = (Spinner) findViewById(R.id.dep_type_spin);
        bank_spin = (Spinner) findViewById(R.id.bank_name_spin);
        account_autocomplete = (AutoCompleteTextView) findViewById(R.id.accounts_autocomplete);
        edt_dep_branch = (EditText) findViewById(R.id.branch_name_edt);
        bank_dep_slip_edt = (EditText) findViewById(R.id.bank_dep_slip_txt);
        dep_amount_txt = (EditText) findViewById(R.id.dep_amount_txt);
        vault_spin = (Spinner) findViewById(R.id.vault_name_spin);

        submit_btn=(Button)findViewById(R.id.submit_btn);

        dep_amount_txt.setText(DepositAmountDataCenter.dep_amount_data);

        bank_dep_slip_edt.setText(DepositAmountDataCenter.bank_dep_slip_str);

        edt_dep_branch.setText(DepositAmountDataCenter.branch_name_data);

//        accounts_autocomplete.setSelection(2);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DepositAmountDataCenter.dep_amount_data=dep_amount_txt.getText().toString();

                DepositAmountDataCenter.bank_dep_slip_str=bank_dep_slip_edt.getText().toString();

                DepositAmountDataCenter.branch_name_data=edt_dep_branch.getText().toString();

                DepositAmountDataCenter.typ_data=type_arls.get(bvType_spin.getSelectedItemPosition());

                DepositAmountDataCenter.account_autocomplete_data=account_autocomplete.getText().toString();


                if (bvType_spin.getSelectedItemPosition() == 0) {
                    Helper.showLongToast(getApplicationContext(),
                            "Select Deposit Type.");
                }
                else if (TextUtils.isEmpty(dep_amount_txt.getText().toString())) {
                    Helper.showLongToast(getApplicationContext(),
                            "Deposit amount is required.");
                }else
                {
                showEnteredData();
                }
            }
        });

        vaultSpin_adap = new ArrayAdapter<String>(Deposit_amount_edit_screen.this, R.layout.spinner_items, vault_arls) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(Deposit_amount_edit_screen.this, R.layout.spinner_items, null);
                TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(vault_arls.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(Deposit_amount_edit_screen.this, R.layout.spinner_items, null);

                // if(position!=0)
                {
                    TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(vault_arls.get(position));
                }
                return convertView;
            }
        };

        vault_spin.setAdapter(vaultSpin_adap);
        lin_vault = (LinearLayout) findViewById(R.id.lin_vault);

        new_deposite_block = (LinearLayout) findViewById(R.id.new_deposite_block);

        type_arls.add(0, "Select Deposit Type");
        type_arls.add("Burial");
        type_arls.add("Partner Bank");
        type_arls.add("Client Bank");
        type_arls.add("Vault");

        Log.d("Selected_type","Selected_type::"+TransactionSingleItemDataCenter.dep_type);

//        if(TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Burial"))
//            bvType_spin.setSelection(1);
//        if(TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Partner Bank"))
//            bvType_spin.setSelection(2);
//        if(TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Client Bank"))
//            bvType_spin.setSelection(3);
//        if(TransactionSingleItemDataCenter.dep_type.equalsIgnoreCase("Vault"))
//            bvType_spin.setSelection(4);


        Log.d("deposite_type_slctd","deposite_type_slctd:"+ReceivePayment.deposite_type_selected_item);

        if (ReceivePayment.deposite_type_selected_item != 4) {

//            deposite_type_selected_item = bvType_spin.getSelectedItemPosition();
            Log.d("deposite_type_slctd","in_condtion:"+ReceivePayment.deposite_type_selected_item);
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
            Log.d("deposite_type_slctd","in_condtion2:"+ReceivePayment.deposite_type_selected_item);
            lin_vault.setVisibility(View.VISIBLE);
            vault_spin.setSelection(ReceivePayment.Vault_spinner_selected_position);
            new_deposite_block.setVisibility(View.GONE);
        }


        bvType_spin.setAdapter(new ArrayAdapter<String>(Deposit_amount_edit_screen.this, android.R.layout.simple_list_item_1, android.R.id.text1, type_arls) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(Deposit_amount_edit_screen.this, android.R.layout.simple_list_item_1, null);

                // if(position!=0)
                {
                    TextView _txt = (TextView) convertView.findViewById(android.R.id.text1);
                    _txt.setTextColor(Color.BLACK);
                    _txt.setText(type_arls.get(position));
                }
                return convertView;
            }
        });





        bvType_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ReceivePayment.deposite_type_selected_item=i;
                if (i != 4) {
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


        bankSpin_adap = new ArrayAdapter<String>(Deposit_amount_edit_screen.this, R.layout.spinner_items, bank_arls) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(Deposit_amount_edit_screen.this, R.layout.spinner_items, null);
                TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                txt.setTextColor(Color.WHITE);
                txt.setText(bank_arls.get(position));
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                convertView = View.inflate(Deposit_amount_edit_screen.this, R.layout.spinner_items, null);
                {
                    TextView txt = (TextView) convertView.findViewById(R.id.spinner_item_id);
                    txt.setTextColor(Color.BLACK);
                    txt.setText(bank_arls.get(position));
                }
                return convertView;
            }
        };

        bvType_spin.setSelection(ReceivePayment.deposite_type_selected_item);



        branch_adap = new ArrayAdapter<String>(this, R.layout.spinner_items, R.id.spinner_item_id, accounts);

        account_autocomplete.setAdapter(branch_adap);
        account_autocomplete.setText(DepositAmountDataCenter.account_autocomplete_data);
        account_autocomplete.setThreshold(1);
        account_autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view = View.inflate(Deposit_amount_edit_screen.this, R.layout.spinner_items, null);
                TextView txt = (TextView) view.findViewById(R.id.spinner_item_id);
                {
                    if (!TextUtils.isEmpty(accounts.get(position)))
                        txt.setText(accounts.get(position));
                    int index = accounts.indexOf(account_autocomplete.getText().toString());
                    Log.d("index", "bbb" + index);
                    acc_no = "" + index;
                }
                Log.d("Autocmp ID", "Auto cmp" + accounts.get(position));
            }
        });

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

        final AlertDialog alertDialog = new AlertDialog.Builder(Deposit_amount_edit_screen.this).create();
        alertDialog.setTitle("");
        View view = View.inflate(Deposit_amount_edit_screen.this, R.layout.confirm_screen, null);
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
            dialog = new Dialog(Deposit_amount_edit_screen.this);

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
                                getJson = new GetJson(Deposit_amount_edit_screen.this,new GetJson.CallbackInterface() {

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
            final AlertDialog alertDialog = new AlertDialog.Builder(Deposit_amount_edit_screen.this).create();
            final View signView_layout = View.inflate(Deposit_amount_edit_screen.this, R.layout.sign_inc, null);

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
            params.add(new BasicNameValuePair("dep_amount", DepositAmountDataCenter.dep_amount_data));
            params.add(new BasicNameValuePair("rec_status", DepositAmountDataCenter.rec_status));

            params.add(new BasicNameValuePair("remarks", DepositAmountDataCenter.remarks));
            params.add(new BasicNameValuePair("device_id", ""));
            params.add(new BasicNameValuePair("client_code", client_code));
            //TODO
            params.add(new BasicNameValuePair("deposit_slip_no", DepositAmountDataCenter.bank_dep_slip_str));

            //Log.d("ReceivePayment",img_path);
            params.add(new BasicNameValuePair("final", "1"));
            params.add(new BasicNameValuePair("qr_details", strQrParam));
            getJson = new GetJson(Deposit_amount_edit_screen.this,new GetJson.CallbackInterface() {

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



                        if (!transrec_id.equals("")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Transaction Completed with Transaction Receipt Id: "
                                            + transrec_id, Toast.LENGTH_LONG)
                                    .show();
                        }
                        Deposit_amount_edit_screen.this.setResult(RESULT_OK);
                        final Intent inte = new Intent(Deposit_amount_edit_screen.this, Home.class);
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
        strQrParam = "";
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
            }
            else
            {

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
            progressDialog = new ProgressDialog(Deposit_amount_edit_screen.this);
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
                Deposit_amount_edit_screen.this.setResult(RESULT_OK);
                final Intent inte = new Intent(Deposit_amount_edit_screen.this, Home.class);
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
}