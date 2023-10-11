package com.mountfox;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;


/**
 * Created by RITS03 on 15-04-2015.
 */
public class Off_Data_Service extends android.app.Service {

   // static boolean stat        =   false;


    @Override
    public void onCreate() {
        //Log.v("Off_Data_Service","Off_Data_Service Create");


        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
  //          stat=true;
     //   //Log.v("Off_Data_Service","Off_Data_Service"+startId);
        PendingTransactionProgress pendingTransactionProgress = new PendingTransactionProgress(
                this);
        String ce_id    =   getApplicationContext().getSharedPreferences("mountfox", Context.MODE_PRIVATE).getString("ce_id","");
        if(!TextUtils.isEmpty(ce_id))
            if (pendingTransactionProgress.isPendingTransactionAvailable(ce_id)) {
                if (Utils.isInternetAvailable(getApplicationContext())) {
                    pendingTransactionProgress.doPendingTransaction(ce_id,1);
                }
                else
                {
                    Intent intent1 = new Intent(this, SplashNew.class);
                    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);
                    long[] vibrate = { 0, 100, 200, 300 };
                    Notification n  = new Notification.Builder(getApplicationContext())
                            .setContentTitle("Update Pending")
                            .setContentText("Enable Internet to Update Pending Records")
                            .setContentIntent(pIntent)
                            .setAutoCancel(true).setContentIntent(pIntent).setSmallIcon(R.drawable.icon)
                            .setSound(Uri.parse("android.resource://"
                                    + getApplicationContext().getPackageName() + "/" + R.raw.notify)).setVibrate(vibrate).getNotification();
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, n);
                }
            }
        return 0;
    }

    @Override
    public void onDestroy() {
           // stat=   false;
        Intent intent = new Intent("com.mountfox.receive");

        PendingIntent sender = PendingIntent.getBroadcast(this, 1, intent, 0);

        // We want the alarm to go off 3 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 3 * 1000;//start 3 seconds after first register.

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) this
                .getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
                600000, sender);

        super.onDestroy();
    }
}
