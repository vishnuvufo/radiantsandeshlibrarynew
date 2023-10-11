package com.mountfox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mountfox.Retrofit.ApiInterface;
import com.mountfox.Retrofit.Constants;
import com.mountfox.cropimage.CropImage;
import com.mountfox.cropimage.CropImageView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPhoto extends AppCompatActivity {

    public static final String TAG = UploadPhoto.class.getSimpleName();

    Context mContext;
    private RelativeLayout rl_slipupload;
    private FrameLayout fr_slipupload;
    private ImageView img_slip ;
    String slipImage="";
    LinearLayout ln_submit;
    private String ceid="",  coll_id="",mul_status="";
    private ApiInterface apiInterface;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        ceid = getIntent().getStringExtra("ce_id");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        coll_id = getIntent().getStringExtra("coll_id");
        mul_status = getIntent().getStringExtra("mul_status");
        Log.e(TAG,"coll_id->"+coll_id);
        Log.e(TAG,"mul_status->"+mul_status);



        apiInterface = Constants.getClient().create(ApiInterface.class);

        rl_slipupload = findViewById(R.id.rl_slipupload);
        fr_slipupload = findViewById(R.id.fr_slipupload);
        img_slip = findViewById(R.id.img_slip);
        ln_submit = findViewById(R.id.ln_submit);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UploadPhoto.this, UploadPhotoList.class);
                startActivity(intent);
            }
        });

        img_slip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(UploadPhoto.this);

            }
        });

        ln_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               if (slipImage.equals("") || slipImage.isEmpty()) {
                        Toast.makeText(UploadPhoto.this, "Please Upload Photo", Toast.LENGTH_SHORT).show();
                    }
                   else if(img_slip.equals("")||img_slip==null){

                        Toast.makeText(UploadPhoto.this, "Please Upload Photo", Toast.LENGTH_SHORT).show();
                    }

                else {
                    new onGetSlipUpload(UploadPhoto.this,coll_id,slipImage,mul_status).execute();
                }

            }
        });

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {

                    Bitmap bitmap = getBitmap(UploadPhoto.this, result.getUri());
                    Log.e(TAG,"uriii->"+result.getUri());
                    Bitmap compressedBitmap = Utils.getResizedBitmap(bitmap, 1000);
                    Log.e(TAG,"compressedBitmap->"+compressedBitmap);
                    slipImage = Utils.BitMapToString(compressedBitmap);
                    Log.e(TAG,"slipImage->"+slipImage);
                    try {
                        bitmap= Utils.StringToBitMap(slipImage);
                        img_slip.setImageBitmap(bitmap);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.e(TAG,"exception ->"+e.getMessage());

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"IOexception ->"+e.getMessage());

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(UploadPhoto.this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                Log.e(TAG,"result ->"+result.getError());
            }
        }


    }


    // api image
    private class onGetSlipUpload extends AsyncTask<String, String, String> {
      private Activity activity;
        private String coll_id,slipImage,mul_status;

        public onGetSlipUpload(Activity activity, String coll_id, String slipImage, String mul_status) {
            this.activity = activity;
            this.coll_id = coll_id;
            this.slipImage = slipImage;
            this.mul_status = mul_status;



        }



        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject jsonObjectresponse = new JSONObject();
                jsonObjectresponse.put("coll_id", coll_id);
                jsonObjectresponse.put("slip_image", slipImage);
                jsonObjectresponse.put("mul_status", mul_status);

                JsonObject jsonObject = null;
                jsonObject = (JsonObject) new JsonParser().parse(String.valueOf(jsonObjectresponse));
                Log.e(TAG,"uploAD image Json >>"+jsonObject);
                Log.e(TAG,"mul_status >>"+mul_status);


                Call<UploadPojo> upload = apiInterface.getuploadimage(jsonObject);
                upload.enqueue(new Callback<UploadPojo>() {
                    @Override
                    public void onResponse(Call<UploadPojo> call, Response<UploadPojo> response) {
                        if (response.code() == 200) {
                            UploadPojo uploadPojo = new UploadPojo();
                            UploadPojo listSumitTrans=response.body();
                            if (response.body().getStatus().equals("000")) {
                                Toast.makeText(UploadPhoto.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UploadPhoto.this, UploadPhotoList.class);
                                intent.putExtra("ce_id", ceid);
                                Log.e(TAG, " RESPOSE >>>> " + response.body().getMessage());
                                startActivity(intent);
                                finish();

                            }
                            else {
                                Toast.makeText(UploadPhoto.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"outside>>>> "+response.body().getMessage());
                            }



                        }


                        else {
                            Log.e(TAG,"outside>>>> "+response);
                            Toast.makeText(UploadPhoto.this,  "Try again Later", Toast.LENGTH_SHORT).show();

                        }


                    }
                    @Override
                    public void onFailure(Call<UploadPojo> call, Throwable t) {
                        Log.e(TAG,"onFailure >>>> "+t.getMessage());
                        Toast.makeText(UploadPhoto.this,  Objects.requireNonNull(t.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG,"Exception >>>> "+e.getMessage());
                Toast.makeText(UploadPhoto.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }



    public static Bitmap getBitmap(Activity mContext, Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor = mContext.getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
        return original;
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


