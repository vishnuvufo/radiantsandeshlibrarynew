package com.mountfox;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sathish canine on 05-07-2016.
 */
public class TransactionSingleItemDataCenter {
    public static String ce_id_ = "";
    public static String trans_ids = "";
    public static String pin_statuss = "";
    public static String pin_nos = "";
    public static String types = "";
    public static String client_code = "";
    public static String deno_status = "";
    public static String client_amt = "";
    public static String captions = "";
    public static String amts = "";
    public static String cust_names = "";
    public static String dep_type = "";
    public static String otp_flags = "";
    public static String otp_days = "";

    public static int canceledEntry=0;


    TransactionSingleItemDataCenter(String ce_id, String trans_ids, String pin_statuss, String pin_nos, String types, String client_code, String deno_status, String client_amt, String captions, String amts, String cust_names,String dep_typ,String otp_flags,String otp_days) {
        Log.d("Data_inserted","Data inserted :");
        this.ce_id_ = ce_id;
        Log.d("ce_id_","ce_id_"+ce_id_);
        this.trans_ids = trans_ids;
        Log.d("trans_ids","trans_ids"+trans_ids);
        this.pin_statuss = pin_statuss;
        Log.d("pin_statuss","pin_statuss"+pin_statuss);
        this.pin_nos = pin_nos;
        Log.d("pin_nos","pin_nos"+pin_nos);
        this.types = types;
        Log.d("types","types"+types);
        this.client_code = client_code;
        Log.d("client_code","client_code"+client_code);
        this.deno_status = deno_status;
        Log.d("deno_status","deno_status"+deno_status);
        this.client_amt = client_amt;
        Log.d("client_amt","client_amt"+client_amt);
        this.amts = amts;
        Log.d("amts","amts"+amts);
        this.cust_names = cust_names;
        Log.d("cust_names","cust_names"+cust_names);
        this.captions=captions;
        Log.d("captions","captions"+captions);
        this.dep_type=dep_typ;
        Log.d("dep_type","dep_type_dataCntr"+dep_type);
        this.otp_flags=otp_flags;
        Log.d("otp_flags","otp_flags_dataCntr"+otp_flags);
        this.otp_days=otp_days;
        Log.d("otp_days","otp_days_dataCntr"+otp_days);
    }
}