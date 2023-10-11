package com.mountfox;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.util.Log;

import static java.security.AccessController.getContext;


public class DbHandler {

    private String TAG = "DBHandler";
    SQLiteDatabase db;
    Context mContext;

    public DbHandler(Context context) {
        mContext = context;
    }

    public boolean createables() {
        db = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE,
                null);
        db.execSQL("CREATE TABLE IF NOT EXISTS login (pin_no integer PRIMARY KEY,ce_name text,ce_id text,location text,email_id text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ptransactions (trans_id text PRIMARY KEY,type text,no_recs text,trans_param text,deno text,dep_amount integer,rec_status text,remarks text,device_id text,ce_id text,transrec_id text DEFAULT 0,sent text DEFAULT no,dep_type text, bank_name text,branch_name text, account_no text, vault_name text,bank_dep_slip text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS receipt (trans_id text,point_name text,cust_name text,type text,req_amount integer,pickup_amount integer,ce_id text, show text DEFAULT yes, printed text DEFAULT no,trans_date text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS transactions (trans_id text PRIMARY KEY,point_name text,cust_name text,pickup_session text,type text,amount integer,pin_status text,pin_no text,ce_id text, day integer, month integer, year integer, hour integer, minute integer, show text DEFAULT yes,client_code text,trans_date text,deno_status text,captions text,client_amt text,shop_id text,dep_typeee text,otp_flag text,otp_day text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS transactions_Entry (type text,ce_id text,trans_id text,no_recs text,trans_param text,deno text,dep_amount text, rec_status text, remarks text, device_id text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS bank_list(id integer PRIMARY KEY,  bank_name text,acc_id text,branch_name text,acc_no text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS vault_list(id integer PRIMARY KEY,vault_name text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ipaddress (ip_id integer PRIMARY KEY,address text, status text)");
        db.close();
        if (Config.DEBUG) {
            //Log.d(TAG, "Table Created");
        }
        return true;
    }

    public void clear()
    {
        db.execSQL("delete from ipaddress ");
    }

    public boolean insert(String tableName, ContentValues contentValues) {

        try {
            db = mContext.openOrCreateDatabase("mountfox",
                    Context.MODE_PRIVATE, null);
            //Log.v(TAG,"insert"+contentValues.getAsString("pin_no"));
            db.insertOrThrow(tableName, null, contentValues);
            db.close();
            if (Config.DEBUG) {
                //Log.d(TAG, "Value Inserted");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return false;
    }

    public boolean update(String tableName, ContentValues contentValues) {
        try {
            db = mContext.openOrCreateDatabase("mountfox",
                    Context.MODE_PRIVATE, null);
            db.replaceOrThrow(tableName, null, contentValues);
            db.close();
            if (Config.DEBUG) {
                //Log.d(TAG, "Value Updated");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return false;
    }

    public boolean delete(String sql) {
        try {
            db = mContext.openOrCreateDatabase("mountfox",
                    Context.MODE_PRIVATE, null);
            db.execSQL(sql);
            db.close();
            if (Config.DEBUG) {
                //Log.d(TAG, "Value Deleted");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return false;
    }

    public ContentValues[] select(String sql) {
        //Log.v(TAG,"select"+sql);
        db = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE,
                null);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int n = cursor.getCount();
        ContentValues contentValues[] = new ContentValues[n];
        for (int i = 0; i < n; i++, cursor.moveToNext()) {
            contentValues[i] = new ContentValues();
            for (int j = 0; j < cursor.getColumnCount(); j++) {
                contentValues[i].put(cursor.getColumnName(j),
                        cursor.getString(j));
            }
        }
        cursor.close();
        db.close();
        if (Config.DEBUG) {
            //Log.d(TAG, "Value Seleted");
        }
        return contentValues;
    }

    public void execute(String sql) {
        try {
            db = mContext.openOrCreateDatabase("mountfox",
                    Context.MODE_PRIVATE, null);
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAlreadyPrinted(String trans_id) {
        db = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE,
                null);
        Cursor cursor = db.rawQuery("select *from receipt where trans_id='"
                + trans_id + "'", null);
        cursor.moveToFirst();
        String printed = cursor.getString(cursor.getColumnIndex("printed"));
        cursor.close();
        db.close();
        if (printed.equals("yes"))
            return true;
        return false;
    }

    public boolean isExistRow(String table, String trans_id) {
        try {
            db = mContext.openOrCreateDatabase("mountfox",
                    Context.MODE_PRIVATE, null);
            Cursor cursor = db.rawQuery("select *from " + table
                    + " where trans_id='" + trans_id + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                db.close();
                cursor.close();
                if (Config.DEBUG) {
                    //Log.d(TAG, "The trans_id: " + trans_id + " exist");
                }
                return true;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return false;
    }

    public void deleteTimeoutTransaction() {
        ContentValues contentValues[] = select("select * from transactions where show='yes'");
        for (int i = 0; i < contentValues.length; i++) {
            Calendar a = Calendar.getInstance();
            a.set(Calendar.DAY_OF_MONTH, contentValues[i].getAsInteger("day"));
            a.set(Calendar.MONTH, contentValues[i].getAsInteger("month"));
            a.set(Calendar.YEAR, contentValues[i].getAsInteger("year"));
            a.set(Calendar.HOUR_OF_DAY, contentValues[i].getAsInteger("hour"));
            a.set(Calendar.MINUTE, contentValues[i].getAsInteger("minute"));

            Calendar b = Calendar.getInstance();
            double dif = (double) (b.getTimeInMillis() - a.getTimeInMillis())
                    / (1000 * 60 * 60);
            if (dif >= 48) {
                String trans_id = contentValues[i].getAsString("trans_id");
                db = mContext.openOrCreateDatabase("mountfox",
                        Context.MODE_PRIVATE, null);
                db.execSQL("update transactions set show='no' where trans_id='"
                        + trans_id + "'");
                db.close();
//                if (Config.DEBUG) {
//                    //Log.d(TAG, "Trans_id: " + trans_id
//                    //      + " was removed from the list");
//                }
            }
        }
    }

    //code to insert bank_details
    public void insert_bankName(ArrayList<String> bank_name, ArrayList<String> acc_ids, ArrayList<String> branch, ArrayList<String> acc_no) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE, null);

        for (int i = 0; i < bank_name.size(); i++) {
            contentValues.put("bank_name", bank_name.get(i));
            contentValues.put("branch_name", branch.get(i));
            contentValues.put("acc_id", acc_ids.get(i));
            contentValues.put("acc_no", acc_no.get(i));
            sqLiteDatabase.insert("bank_list", null, contentValues);
        }
        if (sqLiteDatabase.isOpen())
            sqLiteDatabase.close();
    }

    //code to insert vault_details
    public void insert_vaultName(ArrayList<String> vault_name) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE, null);

        for (int i = 0; i < vault_name.size(); i++) {
            contentValues.put("vault_name", vault_name.get(i));
            sqLiteDatabase.insert("vault_list", null, contentValues);
        }
        if (sqLiteDatabase.isOpen())
            sqLiteDatabase.close();
    }
    //code to read vault or bank

    public ArrayList<String> get_list(String table) {
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE, null);
        Cursor cursor = sqLiteDatabase.query(table, null, null, null, null, null, null);

        if (cursor.moveToFirst())
            while (true) {
                if (cursor.isAfterLast())
                    break;
                if (!list.contains(cursor.getString(1)))
                    list.add(cursor.getString(1));
                cursor.moveToNext();
            }

        return list;
    }

    public String getAccount_Id(String table, String account_number_auto) {
        String string = "0";
        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE, null);
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + table + " WHERE acc_no='" + account_number_auto + "'", null);
        if (c.moveToFirst()) {
            string = c.getString(2);
            Log.d("","string::"+string);
        }
        return string;
    }

    //code to get entire bank based on where condition
    public ArrayList<Bank_Pojo> get_list_where(String table, String bank, String branch, String type) {
        ArrayList<Bank_Pojo> list = new ArrayList<Bank_Pojo>();
        SQLiteDatabase sqLiteDatabase = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE, null);
        Cursor cursor = null;
        /*if(type.equalsIgnoreCase("1"))
            cursor          =   sqLiteDatabase.query(table,null,"bank_name='"+ bank+"' and branch_name='"+branch+"'",null,null,null,null);
        else*/
        cursor = sqLiteDatabase.query(table, null, "bank_name='" + bank + "'", null, null, null, null);
        ////Log.v("DbHandler","Sqlite:"+"bank_name='"+ bank+"' and branch_name='"+branch+"'");
        if (cursor.moveToFirst())
            while (true) {
                Bank_Pojo bank_pojo = new Bank_Pojo();
                if (cursor.isAfterLast())
                    break;
                bank_pojo.setId(cursor.getString(cursor.getColumnIndex("acc_id")));
                ////Log.v("DbHandler","id in banks"+cursor.getString(cursor.getColumnIndex("id")));
                bank_pojo.setAcc_no(cursor.getString(cursor.getColumnIndex("acc_no")));
                bank_pojo.setBank_name(cursor.getString(cursor.getColumnIndex("bank_name")));
                //Log.v("DbHandler","acc_no:"+cursor.getString(cursor.getColumnIndex("acc_no")));
                bank_pojo.setBranch_name(cursor.getString(cursor.getColumnIndex("branch_name")));
                list.add(bank_pojo);
                cursor.moveToNext();
            }

        return list;
    }

    public String getIP() {
        String ip = "";
        String qry = "SELECT * FROM ipaddress WHERE status='1' ";

        db = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE,
                null);
        Cursor cursor = db.rawQuery(qry, null);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            //cursor is empty
        } else {
            cursor.moveToFirst();
            ip = cursor.getString(1);
            cursor.close();
        }
        db.close();
        return ip;
    }

    public Boolean checkAvailableOrNot(String ip) {
        String qry = "SELECT * FROM ipaddress WHERE address='" + ip + "' ";

        db = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE,
                null);
        Cursor cursor = db.rawQuery(qry, null);
        if (cursor.getCount() != 0) {
            return true;

        } else {
            return false;
        }

    }

    public void updateIp(String ip, String stats) {
        ContentValues values = new ContentValues();
        values.put("status", stats);
        String qry = "SELECT * FROM ipaddress WHERE address='" + ip + "' ";
        db = mContext.openOrCreateDatabase("mountfox", Context.MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        String RequestID = cursor.getString(0);
        db.update("ipaddress", values, "ip_id" + "=" + RequestID, null);

    }

}
