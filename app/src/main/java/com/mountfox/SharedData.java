package com.mountfox;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by STS on 12/7/2016.
 */

public class SharedData {
    private static SharedData pref;
    private SharedPreferences sharedPreferences;

    public static SharedData getInstance(Context context) {
        if (pref == null) {
            pref = new SharedData(context);
        }
        return pref;
    }

    private SharedData(Context context) {
        sharedPreferences = context.getSharedPreferences("Shared_RadiantSandesh",Context.MODE_PRIVATE);
    }

    public void saveData(String key,String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor .putString(key, value);
        prefsEditor.commit();
    }

    public String getData(String key) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }
}
        /* // must use both lines..
        // used to add value in sharedPrefeence..
        SharedData sharedData = SharedData.getInstance(this);
        sharedData.saveData("key","Sujtih");

        // get value from SharedPreference..
        SharedData sharedData = SharedData.getInstance(this);
        String value = sharedData.getData("key");
        */