package com.mountfox;

import static com.mountfox.sharedPref.ConstantValues.TAG_CEID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.sharedPref.SharedPreference;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPhotoList extends AppCompatActivity {



    View view;
    public static final String TAG = UploadPhotoList.class.getSimpleName();
    ApiInterface apiInterface;
    private ImageView back;
    private RecyclerView trackingrecycler;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView notranscation;
//    public UploadPhoto.UploadImageAdapter Adapter;
    ProgressDialog progressDialog;
    private Activity mActivity;
    private Toolbar toolbar;
    private  String ceid="",coll_id="",mul_status="";
    private  int positioin;


    public UploadImageAdapter uploadImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo_list);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        ceid= SharedPreference.getDefaults(UploadPhotoList.this,TAG_CEID);
        Log.e("ce_id", "upload image list:::" +ceid);
        apiInterface = Constants.getClient().create(ApiInterface.class);
        notranscation =findViewById(R.id.tv_notransaction);
        back = findViewById(R.id.back);
        trackingrecycler = findViewById(R.id.recyclerview);



        progressDialog = new ProgressDialog(UploadPhotoList.this);
//        progressDialog.onStart();
        GridLayoutManager mGridLayoutManagerCategoriesBrand = new GridLayoutManager(UploadPhotoList.this, 1);
        trackingrecycler.setLayoutManager(mGridLayoutManagerCategoriesBrand);
        trackingrecycler.setHasFixedSize(true);
        trackingrecycler.setNestedScrollingEnabled(true);
//        trackingrecycler.setAdapter(Adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UploadPhotoList.this, ModeOfTransactionActivity.class);
                startActivity(intent);
            }
        });



        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new onGetListSumitTrans(UploadPhotoList.this,ceid).execute();
            }
        });

        new onGetListSumitTrans(UploadPhotoList.this,ceid).execute();

    }



    public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.CategoriesViewHolder> {
        private Activity mactivity;
        private List<ListSumitTrans> listSumitTrans;

        public UploadImageAdapter(Activity mactivity, List<ListSumitTrans> listSumitTrans) {
            this.mactivity = mactivity;
            this.listSumitTrans = listSumitTrans;

        }

        @Override
        public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_image_list_item, parent, false);
            return new  CategoriesViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(final  CategoriesViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.tv_transID.setText("" + listSumitTrans.get(position).getTransId());
            holder.tv_customername.setText("" + listSumitTrans.get(position).getCustName());

            holder.framelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"uploAD image page >>");

                   Intent intent=new Intent(UploadPhotoList.this,UploadPhoto.class);
                   intent.putExtra("coll_id",listSumitTrans.get(position).getCollId());
                   intent.putExtra("mul_status",listSumitTrans.get(position).getMulStatus());
                    intent.putExtra("ce_id", ceid);
                    startActivity(intent);

                }
            });
        }



        @Override
        public int getItemCount() {
            return listSumitTrans.size();
        }

        class CategoriesViewHolder extends RecyclerView.ViewHolder {
            TextView tv_customername,tv_transID,tv_customercode,tv_status;
            CardView framelayout;
            LinearLayout status_linearlayout,date_ll;
            CategoriesViewHolder(View itemView) {
                super(itemView);
                tv_customername = itemView.findViewById(R.id.tv_customername);
                tv_transID = itemView.findViewById(R.id.tv_transID);
                tv_status = itemView.findViewById(R.id.tv_status);
                framelayout = itemView.findViewById(R.id.framelayout);

            }
        }
    }

 // API

    private class onGetListSumitTrans extends AsyncTask<String, String, String> {
        private  Activity activity;
        private String ceid;

        public onGetListSumitTrans(Activity activity ,String ceid) {
            this.activity=activity;
            this.ceid = ceid;


        }



        @Override
        protected String doInBackground(String... strings) {
            try {
                mSwipeRefreshLayout.setRefreshing(false);
                JSONObject jsonObjectresponse = new JSONObject();
                jsonObjectresponse.put("ce_id", ceid);
                JsonObject jsonObject = null;
                jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
                Log.e(TAG,"uploAD image list Json >>"+jsonObject);


                Call<UploadImageListpojo> tracking = apiInterface.getuploadimageList(jsonObject);
                tracking.enqueue(new Callback<UploadImageListpojo>() {
                    @Override
                    public void onResponse(Call<UploadImageListpojo> call, Response<UploadImageListpojo> response) {
                        if (response.code() == 200) {

                            List<ListSumitTrans> uploadImageListpojoList = response.body().getData();

                            if (response.body().getData().size() == 0) {
                                  mSwipeRefreshLayout.setVisibility(View.GONE);
                                notranscation.setVisibility(View.VISIBLE);
                                Toast.makeText(UploadPhotoList.this,"No Transactionfound", Toast.LENGTH_SHORT).show();
                            }else{
                                notranscation.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                uploadImageAdapter = new UploadImageAdapter(UploadPhotoList.this, uploadImageListpojoList);
                                trackingrecycler.setAdapter(uploadImageAdapter);


                            }




//                            if (response.body().getStatus().equals("888")){
//
//                                notranscation.setVisibility(View.GONE);
//                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
//                                uploadImageAdapter = new UploadPhotoList.UploadImageAdapter(UploadPhotoList.this, uploadImageListpojoList);
//                                trackingrecycler.setAdapter(uploadImageAdapter);
//
//                            }
//                            else{
//
//                                mSwipeRefreshLayout.setVisibility(View.GONE);
//                                notranscation.setVisibility(View.VISIBLE);
//                                Toast.makeText(UploadPhotoList.this,"No Transactionfound", Toast.LENGTH_SHORT).show();
//                            }

                        }


                        else {
                            Log.e(TAG,"outside>>>> "+response);
                            Toast.makeText(UploadPhotoList.this,  "Try again Later", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }


                    }
                    @Override
                    public void onFailure(Call<UploadImageListpojo> call, Throwable t) {
                        progressDialog.cancel();
                        Toast.makeText(UploadPhotoList.this,  Objects.requireNonNull(t.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                progressDialog.cancel();
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(UploadPhotoList.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
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