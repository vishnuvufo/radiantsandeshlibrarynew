package com.mountfox.Deposits;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mountfox.Login;
import com.mountfox.R;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.response.AccountDetail;
import com.mountfox.response.AddDepositResponse;
import com.mountfox.response.BankDepositAccountResponse;
import com.mountfox.response.BankDepositBankname;
import com.mountfox.sharedPref.ConstantValues;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Deposit extends Activity implements View.OnClickListener {

    public static final String TAG = Deposit.class.getSimpleName();
    AutoCompleteTextView ed_bank_name, ed_bank_number;
    ApiInterface apiInterface;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> accountadapter;
    EditText ed_pickup_amount, ed_deposit_amount, ed_difference_amount, ed_branch_name, ed_slip_number, ed_remarks;
    LinearLayout ln_submit;
    Integer pickup_amount;
    String collection_amount = "", deptype = "", point_id = "", noofcount = "", trans_id = "", accountids = "", transactiontype = "";
    private ArrayList<String> accountnumber, accountid,accountposition,accountnumberid;
    private ListPopupWindow accountnumberpopup;
    private String[] accountnumberlist;
    float numbers,totaldepbalamount;
    float changing_diff_amount;
    ProgressDialog progressDialog;
    TextView tv_transtype;
    char depamountfirstletter;
    String ce_id="",datetime="",totaldepositedamount="",totalbalanceamount="",multitype="";
    int maxamount,totalmaxamount;
    TextView tv_maxamount;
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_depost);
        String Urlstatus = SharedPreference.getDefaults(Deposit.this, ConstantValues.TAG_URLVALIDATE);

        if(Urlstatus.equals("dontswap")){
            apiInterface = Constants.getClient().create(ApiInterface.class);
        }else if(Urlstatus.equals("swap")) {
            apiInterface = Constants.getClientTwo().create(ApiInterface.class);
        }
        progressDialog = new ProgressDialog(this);
        onBankName();
        accountnumber = new ArrayList<String>();
        accountid = new ArrayList<String>();
        accountposition = new ArrayList<String>();
        accountnumberid = new ArrayList<String>();
        ed_bank_name = findViewById(R.id.ed_bank_name);
        ed_pickup_amount = findViewById(R.id.ed_pickup_amount);
        ed_deposit_amount = findViewById(R.id.ed_deposit_amount);
        ed_difference_amount = findViewById(R.id.ed_difference_amount);
        ed_bank_number = findViewById(R.id.ed_bank_number);
        ed_branch_name = findViewById(R.id.ed_branch_name);
        ed_slip_number = findViewById(R.id.ed_slip_number);
        ed_remarks = findViewById(R.id.ed_remarks);
        ln_submit = findViewById(R.id.ln_submit);
        tv_transtype = findViewById(R.id.tv_transtype);
        tv_maxamount = findViewById(R.id.tv_maxamount);
        back = findViewById(R.id.back);

        ce_id =  Login.ce_id_main;
       // ce_id =  "RAD-TN-1014";
        Log.e(TAG,"ce_id->"+ce_id);

        try{
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            datetime = format.format(today);
            Log.e(TAG,"dateToStr ->"+datetime);
        }catch (Exception e){
            Log.e(TAG,"exception e->"+e.getMessage());
        }
      ed_pickup_amount.setText(getIntent().getStringExtra("pickup_amount"));
      deptype = getIntent().getStringExtra("deptype");
      noofcount = getIntent().getStringExtra("noofcount");
      trans_id = getIntent().getStringExtra("trans_id");
      transactiontype = getIntent().getStringExtra("transactiontype");
      totaldepositedamount = getIntent().getStringExtra("depositedamount");
      totalbalanceamount = getIntent().getStringExtra("balanceamount");
      multitype = getIntent().getStringExtra("type");

      if(multitype.equals("Yes")){
          tv_transtype.setText("Multiple Transaction Deposit");
      }else if(multitype.equals("No")){
          tv_transtype.setText("Single Transaction Deposit");
      }else
      {
          tv_transtype.setText("Single Transaction Deposit");
      }


      Log.e(TAG,"total deposite amount ->"+totaldepositedamount);
      Log.e(TAG,"total balance amount ->"+totalbalanceamount);


        if(ed_pickup_amount.getText().toString().equals("0")){
            ed_deposit_amount.setText("0");
            ed_deposit_amount.setEnabled(false);
            ed_difference_amount.setText("0");
            ed_difference_amount.setEnabled(false);
            ln_submit.setVisibility(View.GONE);
        }

        if(totalbalanceamount.equals("0")){
        }else {
            ed_deposit_amount.setText(totalbalanceamount);
        }

        if(!totalbalanceamount.equals("0")){
            numbers = Float.parseFloat(ed_pickup_amount.getText().toString());
            changing_diff_amount = numbers - (Float.parseFloat(totaldepositedamount)+Float.parseFloat(totalbalanceamount));
            ed_difference_amount.setText("" + changing_diff_amount);
        }

        if(totaldepositedamount.equals("0")){
            tv_maxamount.setVisibility(View.GONE);
       }else {
            tv_maxamount.setVisibility(View.VISIBLE);
           tv_maxamount.setText(" Already Deposited Amount : "+totaldepositedamount);
        }

        ed_deposit_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(TAG, "before");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(totalbalanceamount.equals("0")){
                    if (ed_deposit_amount.equals("") || ed_deposit_amount.getText().toString().equals("")) {
                        ed_difference_amount.setText("");
                    } else {
                        numbers = Float.parseFloat(ed_pickup_amount.getText().toString());
                        changing_diff_amount = numbers - Float.parseFloat(ed_deposit_amount.getText().toString());
                        ed_difference_amount.setText("" + changing_diff_amount);
                    }
                }else {

                    if(ed_deposit_amount.equals("") || ed_deposit_amount.getText().toString().equals("")){
                        numbers = Float.parseFloat(ed_pickup_amount.getText().toString());
                        changing_diff_amount = numbers - Float.parseFloat(totaldepositedamount);
                        ed_difference_amount.setText("" + changing_diff_amount);

                    }else {
                        numbers = Float.parseFloat(ed_pickup_amount.getText().toString());
                        totaldepbalamount = Float.parseFloat(ed_deposit_amount.getText().toString())+ Float.parseFloat(totaldepositedamount);
                        changing_diff_amount = numbers - totaldepbalamount;
                        ed_difference_amount.setText("" + changing_diff_amount);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "after");
            }
        });


        ed_bank_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onAccount(ed_bank_name.getText().toString());
                if(ed_bank_name.getText().toString().equals("")){
                    ed_bank_number.setText("");

                }
            }
        });

        ed_bank_number.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                String mobiletabletitem = accountnumber.get(position);

//                ed_bank_number.setText(mobiletabletitem);
              //  Log.e(TAG, "account id ->" + accountids);
                Log.e(TAG, "account id test->" + ed_bank_number.getText().toString());
//                Log.e(TAG, "account number ->" + mobiletabletitem);
//                Log.e(TAG, "account number position ->" + position);

                HelloTesting(ed_bank_number.getText().toString());



            }
        });
        ln_submit.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void onBankName() {
        try {
            Call<BankDepositBankname> bankDepositBanknameCall = apiInterface.doBankNameResponse("BankDetails");
            bankDepositBanknameCall.enqueue(new Callback<BankDepositBankname>() {
                @Override
                public void onResponse(Call<BankDepositBankname> call, Response<BankDepositBankname> response) {
                    if (response.code() == 200) {
                        if (response.body().getStatus().equals("1")) {
                            List<String> banknamelist = response.body().getBankDetails();
                            Log.e(TAG, "bank name list ->" + banknamelist);
                            if (banknamelist.size() == 0) {
                                Toast.makeText(Deposit.this, "No Bank Deposit Found", Toast.LENGTH_SHORT).show();
                            } else {
                                adapter = new ArrayAdapter<String>(Deposit.this, R.layout.autocomplete_textview,R.id.tv_textview, banknamelist);
                                ed_bank_name.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(Deposit.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BankDepositBankname> call, Throwable t) {
                    Toast.makeText(Deposit.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "throwable->" + t.getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(Deposit.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception->" + e.getMessage());
        }
    }


    public void onAccount(String bankname) {
        try {
            accountposition.clear();
            accountnumber.clear();
            accountid.clear();
            Call<BankDepositAccountResponse> bankDepositAccountResponseCall = apiInterface.doBankDepositAccountResponse("AccountDetails", bankname);
            bankDepositAccountResponseCall.enqueue(new Callback<BankDepositAccountResponse>() {
                @Override
                public void onResponse(Call<BankDepositAccountResponse> call, Response<BankDepositAccountResponse> response) {
                    if (response.code() == 200) {
                        if (response.body().getStatus().equals("1")) {
                            List<AccountDetail> accountDetailList = response.body().getAccountDetails();
                            Log.e(TAG, "accountDetailList ->" + accountDetailList);
                            if (accountDetailList.size() == 0) {
                                Toast.makeText(Deposit.this, "No Bank Deposit Found", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < accountDetailList.size(); i++) {
                                    accountnumberid.add(accountDetailList.get(i).getAccountNo()+"-"+accountDetailList.get(i).getAccountID());
                                    accountnumber.add(accountDetailList.get(i).getAccountNo());
                                    accountid.add(accountDetailList.get(i).getAccountID());;
                                }
                                Log.e(TAG, "account number list ->" + accountnumber);


                                accountadapter = new ArrayAdapter<String>(Deposit.this, R.layout.autocomplete_textview,R.id.tv_textview, accountnumber);
                                ed_bank_number.setAdapter(accountadapter);

                            }
                        } else {
                            Toast.makeText(Deposit.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<BankDepositAccountResponse> call, Throwable t) {
                    Toast.makeText(Deposit.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "throwable->" + t.getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(Deposit.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception->" + e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ln_submit:
                if (ed_deposit_amount.getText().toString().equals("") || ed_deposit_amount.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Deposit Amount is Empty", Toast.LENGTH_SHORT).show();
                } else if (ed_difference_amount.getText().toString().startsWith("-")) {
                    Toast.makeText(this, "Please check Difference Amount", Toast.LENGTH_SHORT).show();
                }else if (ed_bank_name.getText().toString().equals("") || ed_bank_name.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Bank Name is Empty", Toast.LENGTH_SHORT).show();
                } else if (ed_bank_number.getText().toString().equals("") || ed_bank_number.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Bank Account Number is Empty", Toast.LENGTH_SHORT).show();
                } else if (ed_branch_name.getText().toString().equals("") || ed_branch_name.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Bank Deposit Branch is Empty", Toast.LENGTH_SHORT).show();
                } else if (ed_slip_number.getText().toString().equals("") || ed_slip_number.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Deposit Slip number is Empty", Toast.LENGTH_SHORT).show();
                } else if (ed_remarks.getText().toString().equals("") || ed_remarks.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Remarks is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("please Wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                onAddDepositWithremarkssubmit(""+datetime, ""+Login.ce_id_main, "" + deptype, "" + accountids, ed_deposit_amount.getText().toString(), trans_id, noofcount, ed_bank_name.getText().toString(), ed_branch_name.getText().toString(),ed_slip_number.getText().toString(), ed_remarks.getText().toString());
              //   onAddDepositWithremarkssubmit("31-08-2020", "RAD-TN-1014", "" + deptype, "" + accountids, ed_deposit_amount.getText().toString(), trans_id, noofcount, ed_bank_name.getText().toString(), ed_branch_name.getText().toString(),ed_slip_number.getText().toString(), ed_remarks.getText().toString());
                }
                break;
            case R.id.back:
                startActivity(new Intent(Deposit.this,DepositList.class));
                break;
        }
    }


    public void onAddDepositWithremarkssubmit(String date, String ceid, String type, String accountid, String depamount, String transid, String noofcount, String bankname, String branchname,String depslipno, String remarks) {
        try {


            JSONObject jsonObjectresponse = new JSONObject();
            jsonObjectresponse.put("DepositDate",date);
            jsonObjectresponse.put("Ce_ID",ceid);
            jsonObjectresponse.put("DepositType",type);
            jsonObjectresponse.put("AccountID",accountid);
            jsonObjectresponse.put("DepositAmount",depamount);
            jsonObjectresponse.put("TransactionID",transid);
            jsonObjectresponse.put("CountOfTransactions",noofcount);
            jsonObjectresponse.put("BankName",bankname);
            jsonObjectresponse.put("DepositBranch",branchname);
            jsonObjectresponse.put("DepositSlipNO",depslipno);
            jsonObjectresponse.put("Remarks",remarks);
            JsonObject jsonObject = null;
            jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));

            Call<AddDepositResponse> addDepositResponseCall = apiInterface.doAdddepositWithremarks(jsonObject);
            addDepositResponseCall.enqueue(new Callback<AddDepositResponse>() {
                @Override
                public void onResponse(Call<AddDepositResponse> call, Response<AddDepositResponse> response) {
                    if(response.code()==200){
                        progressDialog.dismiss();
                        if(response.body().getStatus().equals("1")){
                            startActivity(new Intent(Deposit.this,DepositList.class));
                            Toast.makeText(Deposit.this, "Successfully Updated!!", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.dismiss();
                            Log.e(TAG,"withremarks remarks -->"+response.body().getMessage());
                            Toast.makeText(Deposit.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override
                public void onFailure(Call<AddDepositResponse> call, Throwable t) {
                    Toast.makeText(Deposit.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"throwable->"+t.getMessage());
                    progressDialog.dismiss();
                }
            });
        }catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(Deposit.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Exception->"+e.getMessage());
        }
    }


    public void HelloTesting(String values){



        Log.e(TAG,"values ->"+values);
        Log.e(TAG,"account ->"+accountnumber);
        Log.e(TAG,"account id ->"+accountid);




        List <String> listClone = new ArrayList<String>();
        for (String string : accountnumber) {
            if(string.matches(values)){
               Log.e(TAG,"matching values ->"+string);
                int retval = accountnumber.indexOf(string);
                System.out.println("The element E is at index " + retval);
                int element = Integer.parseInt(accountid.get(retval));
                System.out.println("the element at index 2 is " + element);
                accountids = String.valueOf(element);
                Log.e(TAG,"account id ->"+accountids);
            }
        }





    }


}
