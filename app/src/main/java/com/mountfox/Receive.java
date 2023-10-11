package com.mountfox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by RITS03 on 15-04-2015.
 */
public class Receive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
     //   if(!Off_Data_Service.stat)
            context.startService(new Intent(context,Off_Data_Service.class));
        //Log.v("Receive","Event Received");

    }
}
