package com.mountfox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.mountfox.Cheque_Pickup.ChequePickupListActivity;
import com.mountfox.Delhivery_Pay.Delivery_EntryCash;
import com.mountfox.Delivery.DeliveryListActivity;
import com.mountfox.Deposits.DepositList;

import java.io.File;

public class ModeOfTransactionActivity extends Activity {
    String ce_id = "";
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
        setContentView(R.layout.activity_mode_of_transaction);
            Clearcache(ModeOfTransactionActivity.this);

        LinearLayout tvPickUp = (LinearLayout) findViewById(R.id.tvPickUp);
        LinearLayout tvDelivery = (LinearLayout) findViewById(R.id.tvDelivery);
        LinearLayout tvChequePickUp = (LinearLayout) findViewById(R.id.tvChequePickUp);
        LinearLayout tvdeposit = (LinearLayout) findViewById(R.id.tvdeposit);
            LinearLayout tvslipupload = (LinearLayout) findViewById(R.id.tvslipupload);


            // delivery
            LinearLayout ll_delhiverypay = (LinearLayout) findViewById(R.id.ll_delhiverypay);





            Log.e("ce_id", "ce_id_Home:::" + Login.ce_id_main);
        ce_id = getIntent().getStringExtra("ce_id");
        tvPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeOfTransactionActivity.this, Transaction.class);
                intent.putExtra("ce_id", ce_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
              //  finish();
            }
        });

        tvDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeOfTransactionActivity.this, DeliveryListActivity.class);
                intent.putExtra("ce_id", ce_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        tvChequePickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeOfTransactionActivity.this, ChequePickupListActivity.class);
                intent.putExtra("ce_id", ce_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

            tvdeposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ModeOfTransactionActivity.this, DepositList.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });


            tvslipupload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ModeOfTransactionActivity.this, UploadPhotoList.class);
                    intent.putExtra("ce_id", ce_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });


            ll_delhiverypay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ModeOfTransactionActivity.this, Delivery_EntryCash.class);
                    startActivity(intent);
                    finish();
                }
            });


        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        long megAvailable = bytesAvailable / (1024 * 1024);
        Log.e("","Available MB : "+megAvailable);
        System.out.println("Available space >>> " + megAvailable);
    }

    @Override
    public void onBackPressed() {
        final Intent inte = new Intent(this, Home.class);
        inte.putExtra("ce_id", ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(inte);
        finish();
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
