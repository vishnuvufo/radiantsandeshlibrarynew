package com.mountfox.Delhivery_Pay;

import static com.mountfox.sharedPref.ConstantValues.TAG_DCID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mountfox.Delhivery_Pay.Poojo_Class.PropertyRespone;
import com.mountfox.Delhivery_Pay.Poojo_Class.PropertyResponeList;
import com.mountfox.R;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Property extends AppCompatActivity {
    public static final String TAG = Property.class.getSimpleName();
    ApiInterface apiInterface;
    private EditText tv_cener ;
    private LinearLayout submit;
    private ImageView back;
    private ProgressDialog progressDialog;
    private String[] property_poupuplist;
    private ListPopupWindow property_popup;
    private String emp_id="",dc_id="",dcid_code="";
    List<PropertyResponeList> propertyLists= new ArrayList<>();
    List<String> propertyListList= new ArrayList<>();
    String[] propertyListListValidation;
    List<String> propertyIDListList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        apiInterface = Constants.getdelivery().create(ApiInterface.class);
        progressDialog = new ProgressDialog(Property.this);
    //    submit=findViewById(R.id.center_submit);
    //    tv_cener=findViewById(R.id.cener);
      //  back=findViewById(R.id.center_back);
        dc_id = SharedPreference.getDefaults(Property.this, TAG_DCID);
        new onPropertyTaskDropDown(Property.this,dc_id).execute();

//        tv_cener.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                property_popup = new ListPopupWindow(Property.this);
//                property_popup.setAdapter(new ArrayAdapter<String>(Property.this, R.layout.to_popupspinner, propertyListList));
//                property_popup.setAnchorView(tv_cener);
//                property_popup.setModal(true);
//                property_popup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        String district = propertyListList.get(position);
//                        tv_cener.setText(district);
//                        dcid_code = propertyLists.get(position).getDcid();
//                        Log.e(TAG, " District Selcet >>>" + district+" ---987654  "+dcid_code);
//                        property_popup.dismiss();
//                    }
//                });
//                property_popup.show();
//            }
//        });
//
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String center=tv_cener.getText().toString();
//                if (center.equals("") || center.isEmpty()) {
//                    Toast.makeText(Property.this, "Please Select Center", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    new onPropertyTask(Property.this,dcid_code).execute();
//                }
//            }
//        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Property.this, DeliveryOtpVerify.class);
//                startActivity(intent);
//                finish();
//            }
//        });

    }


    private class onPropertyTask extends AsyncTask<String, String, String> {
        private Activity activity;
        private String dcid;

        public onPropertyTask(Activity activity ,String dcid) {
            this.activity=activity;
            this.dcid = dcid;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject jsonObjectresponse = new JSONObject();
                jsonObjectresponse.put("dcid", dcid);
                JsonObject jsonObject = null;
                jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
                Log.e(TAG,"dcid ---->>"+jsonObject);
                Call<PropertyRespone> tracking = apiInterface.getPropertyRespone(jsonObject);
                tracking.enqueue(new Callback<PropertyRespone>() {
                    @Override
                    public void onResponse(Call<PropertyRespone> call, Response<PropertyRespone> response) {
                        if (response.code() == 200) {
                            Log.e(TAG,"inside --->>>> "+response);
                            if (response.body().getError().equals("success")){
                                Intent intent = new Intent(Property.this, Delivery_EntryCash.class);
                                startActivity(intent);
                                Log.e(TAG,"secess --->>>> "+response.body().getError());
//                              Toast.makeText(Property.this, response.body().getError(), Toast.LENGTH_SHORT).show();
                            }else {
                                Log.e(TAG,"FAILURE --->>>> "+response.body().getError());
                                Toast.makeText(Property.this, response.body().getError(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Log.e(TAG,"outside>>>> "+response);
                            Toast.makeText(Property.this,  "Try again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                    @Override
                    public void onFailure(Call<PropertyRespone> call, Throwable t) {
                        progressDialog.cancel();
                        Toast.makeText(Property.this,  Objects.requireNonNull(t.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                progressDialog.cancel();
                Toast.makeText(Property.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }



    /////////  onPropertyTaskDropDown
    private class onPropertyTaskDropDown extends AsyncTask<String, String, String> {
        private Activity activity;
        private String dcid;

        public onPropertyTaskDropDown(Activity activity ,String dcid) {
            this.activity=activity;
            this.dcid = dcid;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject jsonObjectresponse = new JSONObject();
                jsonObjectresponse.put("dcid", dcid);
                JsonObject jsonObject = null;
                jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
                Log.e(TAG,"dcid ---->>"+jsonObject);
                Call<PropertyRespone> tracking = apiInterface.getPropertyRespone(jsonObject);
                tracking.enqueue(new Callback<PropertyRespone>() {
                    @Override
                    public void onResponse(Call<PropertyRespone> call, Response<PropertyRespone> response) {
                        if (response.code() == 200) {
                            if(response.body().getError().equals("success")){
                                propertyLists=new ArrayList<PropertyResponeList>();
                                propertyLists=response.body().getData();
                                propertyListList=new ArrayList<>();
                                propertyIDListList=new ArrayList<>();

                                for(int i =0 ; i<propertyLists.size();i++){
                                    propertyListList.add(propertyLists.get(i).getPropertyCity());
                                    propertyIDListList.add(propertyLists.get(i).getDcid());
                                    propertyListListValidation = new String[propertyListList.size()];
                                }
                            }
                        else {
                                Toast.makeText(Property.this, "" + response.body().getError(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "LOG error ------->>>>>> " + response.body().getError());
                            }
                        }
                        else {
                            Log.e(TAG,"outside>>>> "+response);
                            Toast.makeText(Property.this,  "Try again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                    @Override
                    public void onFailure(Call<PropertyRespone> call, Throwable t) {
                        progressDialog.cancel();
                        Toast.makeText(Property.this,  Objects.requireNonNull(t.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                progressDialog.cancel();
                Toast.makeText(Property.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Again To Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}


