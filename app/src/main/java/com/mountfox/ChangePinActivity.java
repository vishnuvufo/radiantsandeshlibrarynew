package com.mountfox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RITS03 on 02-02-2016.
 */
public class ChangePinActivity extends Activity {
    String  shop_id =   "";
    String  pin_no  =   "";
    String  ce_id   =   "";
    String  point_name = "";
    String  otp         =   "";
    EditText    edt_old_pin,edt_otp,edt_new_pin,edt_cnewPin;
    Button      btn_otp,btn_update;

    TextView        txt_point_name;
    LinearLayout    linPin,linOTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepin_rl);

        edt_old_pin    = (EditText) findViewById(R.id.edt_old_pin);
        btn_otp        = (Button)   findViewById(R.id.btn_otp);
        txt_point_name = (TextView) findViewById(R.id.txt_point);
        btn_update     = (Button)   findViewById(R.id.btn_update);
        edt_new_pin    = (EditText) findViewById(R.id.edt_newpin);
        edt_cnewPin    = (EditText) findViewById(R.id.edtcnewpin);
        linOTP         = (LinearLayout) findViewById(R.id.lin_otp);
        linPin         = (LinearLayout) findViewById(R.id.lin_updatepass);
        edt_otp        = (EditText)   findViewById(R.id.edt_otp);
        enableLinOTP(1);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edt_otp.getText().toString()))
                    Toast.makeText(ChangePinActivity.this,"Please Enter OTP",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(edt_new_pin.getText().toString()))
                    Toast.makeText(ChangePinActivity.this,"Please Enter new Pin",Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(edt_new_pin.getText().toString()))
                    Toast.makeText(ChangePinActivity.this,"Please Enter Confirm Pin",Toast.LENGTH_SHORT).show();
                else if(edt_new_pin.getText().toString().equals(edt_cnewPin.getText().toString()))
                {
                    if(edt_otp.getText().toString().equals(otp))
                    {
                    final ProgressDialog  progressDialog  =   new ProgressDialog(ChangePinActivity.this);
                    progressDialog.setTitle("Loading.....");
                    if(progressDialog!=null&&!progressDialog.isShowing())
                        progressDialog.show();
                    final GetJson getJson =   new GetJson(ChangePinActivity.this,new GetJson.CallbackInterface() {
                        @Override
                        public void onRequestCompleted(JSONObject object) {
                            try {
                                if(progressDialog!=null&&progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if(object.getString("status").equals("1"))
                                {
                                    finish();
                                   startActivity(new Intent(ChangePinActivity.this,Home.class).putExtra("ce_id",ce_id));
                                    Toast.makeText(ChangePinActivity.this,"Pin No. Changed Successfully",Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(ChangePinActivity.this,"Contact Admin to change pin",Toast.LENGTH_SHORT).show();


                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ChangePinActivity.this,"Contact Admin to change pin",Toast.LENGTH_SHORT).show();
                                if(progressDialog!=null&&progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }

                        }
                    });
                    List<BasicNameValuePair>    basicNameValuePairs =   new ArrayList<BasicNameValuePair>();
                    basicNameValuePairs.add(new BasicNameValuePair("opt","up_cpin"));
                    basicNameValuePairs.add(new BasicNameValuePair("ce_id",ce_id));
                    basicNameValuePairs.add(new BasicNameValuePair("shop_id",shop_id));
                    basicNameValuePairs.add(new BasicNameValuePair("new_pin",edt_cnewPin.getText().toString()));
                    getJson.execute(basicNameValuePairs);
                    }
                    else
                        Toast.makeText(ChangePinActivity.this,"OTP Mismatch",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ChangePinActivity.this,"Pin Mismatch",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edt_old_pin.getText().toString()))
                    Toast.makeText(ChangePinActivity.this,"Please Enter Old Pin No.",Toast.LENGTH_SHORT).show();
                else if(!edt_old_pin.getText().toString().equals(pin_no))
                    Toast.makeText(ChangePinActivity.this,"Please Enter Valid Pin No.",Toast.LENGTH_SHORT).show();
                else
                {
                    getData();

                }
            }
        });
        if(getIntent().hasExtra("shop_id")&&getIntent().hasExtra("pin_no")&&getIntent().hasExtra("ce_id"))
        {
            shop_id =   getIntent().getStringExtra("shop_id");
            pin_no  =   getIntent().getStringExtra("pin_no");
            ce_id   =   getIntent().getStringExtra("ce_id");
            point_name      =   getIntent().getStringExtra("point_name");
            txt_point_name.setText(point_name);
        }
        else
        {
            Toast.makeText(ChangePinActivity.this,"Please Contact Admin",Toast.LENGTH_SHORT).show();
            finish();
        }





    }
    public void getData()
    {
        final ProgressDialog  progressDialog  =   new ProgressDialog(ChangePinActivity.this);
        progressDialog.setTitle("Loading.....");
        if(progressDialog!=null&&!progressDialog.isShowing())
            progressDialog.show();

        final GetJson getJson =   new GetJson(ChangePinActivity.this,new GetJson.CallbackInterface() {
            @Override
            public void onRequestCompleted(JSONObject object) {

                try {
                    if(progressDialog!=null&&progressDialog.isShowing())
                    progressDialog.dismiss();
                    otp =  object.getString("otp");
                    enableLinOTP(0);
                    Toast.makeText(ChangePinActivity.this,object.getString("msg"),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(progressDialog!=null&&progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
        ArrayList<BasicNameValuePair>   basicNameValuePairs =   new ArrayList<BasicNameValuePair>();
        basicNameValuePairs.add(new BasicNameValuePair("opt","pin_check"));
        basicNameValuePairs.add(new BasicNameValuePair("ce_id",ce_id));
        basicNameValuePairs.add(new BasicNameValuePair("shop_id",shop_id));
        getJson.execute(basicNameValuePairs);
    }
    public void enableLinOTP(int stat){
        if(stat==1)
        {
            linPin.setVisibility(View.GONE);
            linOTP.setVisibility(View.VISIBLE);
        }
        else
        {
            linPin.setVisibility(View.VISIBLE);
            linOTP.setVisibility(View.GONE);
        }
    }
}
