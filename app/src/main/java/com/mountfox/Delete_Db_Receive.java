package com.mountfox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;

/**
 * Created by RITS03 on 04-05-2015.
 */
public class Delete_Db_Receive extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.v("Delete_Db_Receive","Delete Db");
        Toast.makeText(context, "8.30'O clock", Toast.LENGTH_SHORT).show();
    }
}
