package com.mountfox;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String PICKUP_REMARK_LIST_1= "pickupRemarkList_1";
    public static final String PICKUP_REMARK_LIST_2= "pickupRemarkList_2";
    public static final String PICKUP_REMARK_LIST_3= "pickupRemarkList_3";
    public static final String PICKUP_REMARK_LIST_4= "pickupRemarkList_4";
    public static final String PICKUP_REMARK_LIST_5= "pickupRemarkList_5";

    public static final String BURIAL_LIST = "burialList";
    public static final String PB_LIST = "partnerBankList";
    public static final String CB_LIST = "clientBankList";
    SharedPreferences sharedpreferences;

    public void make(Context context, String remark1, String remark2, String remark3, String remark4, String remark5) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(PICKUP_REMARK_LIST_1, remark1);
        editor.putString(PICKUP_REMARK_LIST_2, remark2);
        editor.putString(PICKUP_REMARK_LIST_3, remark3);
        editor.putString(PICKUP_REMARK_LIST_4, remark4);
        editor.putString(PICKUP_REMARK_LIST_5, remark5);
        editor.commit();
    }

    public void clearAllData(Context context){
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PICKUP_REMARK_LIST_1, "");
        editor.putString(PICKUP_REMARK_LIST_2, "");
        editor.putString(PICKUP_REMARK_LIST_3, "");
        editor.putString(PICKUP_REMARK_LIST_4, "");
        editor.putString(PICKUP_REMARK_LIST_5, "");
        editor.apply();
    }

    public void setPickupRemark1(Context mContext, String data) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PICKUP_REMARK_LIST_1, data);
        editor.apply();
    }
    public String getPickupRemark1(Context context) {
        try{
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(PICKUP_REMARK_LIST_1, "");
        }catch (Exception ex){
            return "";
        }
    }

    public void setPickupRemark2(Context mContext, String remark2) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PICKUP_REMARK_LIST_2, remark2);
        editor.apply();
    }
    public String getPickupRemark2(Context context) {
        try{
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(PICKUP_REMARK_LIST_2, "");
        }catch (Exception ex){
            return "";
        }
    }

    public void setPickupRemark3(Context mContext, String remark3) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PICKUP_REMARK_LIST_3, remark3);
        editor.apply();
    }
    public String getPickupRemark3(Context context) {
        try{
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(PICKUP_REMARK_LIST_3, "");
        }catch (Exception ex){
            return "";
        }
    }

    public void setPickupRemark4(Context mContext, String remark4) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PICKUP_REMARK_LIST_4, remark4);
        editor.apply();
    }
    public String getPickupRemark4(Context context) {
        try{
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(PICKUP_REMARK_LIST_4, "");
        }catch (Exception ex){
            return "";
        }
    }

    public void setPickupRemark5(Context mContext, String remark5) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PICKUP_REMARK_LIST_5, remark5);
        editor.apply();
    }
    public String getPickupRemark5(Context context) {
        try{
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(PICKUP_REMARK_LIST_5, "");
        }catch (Exception ex){
            return "";
        }
    }

    public void setBurialList(Context mContext, String burial) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(BURIAL_LIST, burial);
        editor.apply();
    }

    public String getBurialList(Context context) {
        try {
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(BURIAL_LIST, "");
        } catch (Exception ex) {
            return "";
        }
    }

    public void setPbList(Context mContext, String partnerBank) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PB_LIST, partnerBank);
        editor.apply();
    }

    public String getPbList(Context context) {
        try {
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(PB_LIST, "");
        } catch (Exception ex) {
            return "";
        }
    }

    public void setCbList(Context mContext, String clientBank) {
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(CB_LIST, clientBank);
        editor.apply();
    }

    public String getCbList(Context context) {
        try {
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(CB_LIST, "");
        } catch (Exception ex) {
            return "";
        }
    }
}
