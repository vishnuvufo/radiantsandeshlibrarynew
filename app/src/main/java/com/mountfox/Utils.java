package com.mountfox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.provider.Settings.Secure;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;

import static android.content.ContentValues.TAG;

public class Utils {

    public static String getIMEI(Context context) {
//
//        String deviceId="";
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return "";
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            deviceId = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
//        }else{
//            deviceId = telephonyManager.getDeviceId();
//        }
//        return deviceId;


        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;


    }

    public static String getSIMnumber(Context context) {

        String simSerialNumber="";

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            simSerialNumber = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                simSerialNumber = mTelephony.getDeviceId();
            } else {
                simSerialNumber = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }
        return simSerialNumber;
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean isInternetConnection(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Bitmap getBitmapFromAsset(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();

        InputStream istream;
        Bitmap bitmap = null;
        try {
            istream = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(istream);
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }

    public static void showSettingsAlert(Context context) {
        final Context mContext = context;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS is settings");
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static String CreateMobileInformationString(Context context) {

        String versionName = "", packageName = "", filePath = "", phoneModel = "", androidVersion = "", board = "", brand = "",
                device = "", display = "", fingerPrint = "", host = "", ID = "", manufacturer = "", model = "", product = "",
                tags = "", type = "", user = "";
        long time = 0;

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;

            // Package name
            packageName = pi.packageName;

            // Files dir for storing the stack traces
            filePath = context.getFilesDir().getAbsolutePath();

            // Device model
            phoneModel = android.os.Build.MODEL;

            // Android version
            androidVersion = android.os.Build.VERSION.RELEASE;

            board = android.os.Build.BOARD;
            brand = android.os.Build.BRAND;

            // CPU_ABI = android.os.Build.;
            device = android.os.Build.DEVICE;
            display = android.os.Build.DISPLAY;
            fingerPrint = android.os.Build.FINGERPRINT;
            host = android.os.Build.HOST;
            ID = android.os.Build.ID;

            // Manufacturer = android.os.Build.;
            model = android.os.Build.MODEL;
            product = android.os.Build.PRODUCT;
            tags = android.os.Build.TAGS;
            time = android.os.Build.TIME;
            type = android.os.Build.TYPE;
            user = android.os.Build.USER;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        String returnVal = "";

        returnVal += "Version : " + versionName;
        returnVal += ", ";
        returnVal += "Package : " + packageName;
        returnVal += ", ";
        returnVal += "FilePath : " + filePath;
        returnVal += ", ";
        returnVal += "Phone Model" + phoneModel;
        returnVal += ", ";
        returnVal += "Android Version : " + androidVersion;
        returnVal += ", ";
        returnVal += "Board : " + board;
        returnVal += ", ";
        returnVal += "Brand : " + brand;
        returnVal += ", ";
        returnVal += "Device : " + device;
        returnVal += ", ";
        returnVal += "Display : " + display;
        returnVal += ", ";
        returnVal += "Finger Print : " + fingerPrint;
        returnVal += ", ";
        returnVal += "Host : " + host;
        returnVal += ", ";
        returnVal += "ID : " + ID;
        returnVal += ", ";
        returnVal += "Model : " + model;
        returnVal += ", ";
        returnVal += "Product : " + product;
        returnVal += ", ";
        returnVal += "Tags : " + tags;
        returnVal += ", ";
        returnVal += "Time : " + time;
        returnVal += ", ";
        returnVal += "Type : " + type;
        returnVal += ", ";
        returnVal += "User : " + user;
        returnVal += ", ";
        returnVal += "Total Internal memory : " + getTotalInternalMemorySize();
        returnVal += ", ";
        returnVal += "Available Internal memory : "
                + getAvailableInternalMemorySize();
        return returnVal;
    }



//    public static void QRScan(final Activity activity){
//        final android.app.AlertDialog maindialog = new android.app.AlertDialog.Builder(activity).create();
//        LayoutInflater inflater = activity.getLayoutInflater();
//        final View convertView = (View) inflater.inflate(R.layout.dialog_qr_scan, null);
//        maindialog.setCanceledOnTouchOutside(false);
//        maindialog.setCancelable(false);
//
//        LinearLayout img_close;
//
//        img_close = convertView.findViewById(R.id.ln_closee);
//
//
//        img_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                maindialog.dismiss();
//            }
//        });
//
//        maindialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        Window window = maindialog.getWindow();
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        lp.width = size.x;
//        lp.height = size.y;
//        window.setAttributes(lp);
//        maindialog.setCancelable(true);
//        maindialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        maindialog.setView(convertView);
//        maindialog.setTitle("");
//        maindialog.show();
//        maindialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//                    maindialog.setCancelable(false);
//                    return true;
//                }
//                return false;
//            }
//        });
//    }


    public static Bitmap byteToBitmap(byte[] image) {
        Bitmap theImage = null;
        try {
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            theImage = BitmapFactory.decodeStream(imageStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return theImage;
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String ImageString = Base64.encodeToString(b, Base64.DEFAULT);
        return ImageString;
    }

    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            patient_profile_pic.setImageBitmap(bitmap);// set the converted bitmap value to ImageView
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


}