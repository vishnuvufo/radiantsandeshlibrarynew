package com.mountfox;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

import android.widget.Toast;
//import android.widget.Toast;

public class PendingTransactionProgress implements GetJson.CallbackInterface {

	//ProgressDialog progressDialog;
	GetJson getJson;
	DbHandler dbHandler;
	Context context;
	int serviceCompleted = 0;
	TelephonyManager telephonyManager;
	List<BasicNameValuePair> params;
	int numberOfPendingTrasaction = 0;

	private static final String TAG = "PendingTransactionProgress";
    private ProgressDialog  progressDialog;
	String ce_id = "", trans_id = "", type = "", pickup_amount = "",
			deposit_slip = "", pis_no = "", hci_no = "", sealtag_no = "",
			dep_amount = "", rec_status = "", remarks = "", device_id = "",
			Strtrans_param, strdenoParam, strNoTrans,dep_type="",bank_name="",branch_name="",account_no="",vault_name="",dep_slip_no="";
    int     typ=0;

	public PendingTransactionProgress(Context context) {
		this.context    = context;
		dbHandler       = new DbHandler(context);
        progressDialog  =   new ProgressDialog(context);
        progressDialog.setMessage("Uploading Previous Transactions, Please wait...");
        progressDialog.setCancelable(false);
	//	progressDialog = new ProgressDialog(context);
	//	progressDialog.setTitle("Pending Transactions in Progress");
	//	progressDialog.setMessage("Please Wait...");
	//	progressDialog.setCancelable(false);
	//	progressDialog.setIndeterminateDrawable(context.getResources()
	//			.getDrawable(R.drawable.progressbar));
	}

	public boolean isPendingTransactionAvailable(String ce_id) {

       	ContentValues contentValues[] = dbHandler
				.select("select * from ptransactions where sent='no' and ce_id='"
						+ ce_id + "'");
     //   //Log.v("PendingTransactionProgress","PendingTransactionProgress"+ce_id+":"+contentValues.length);

        if (contentValues.length > 0)
        {

            return true;
        }
        else
        {
            return false;
        }
	}

	@SuppressWarnings("unchecked")
	public void doPendingTransaction(String ce_id,int typ) {
        this.typ    =   typ;
        //Log.v("Doing","Doing");
        if(!progressDialog.isShowing()&&progressDialog!=null&typ==0)
            progressDialog.show();
		this.ce_id = ce_id;
		serviceCompleted = 0;
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		device_id = telephonyManager.getDeviceId();

		ContentValues contentValues[] = dbHandler
				.select("select * from ptransactions where sent='no' and ce_id='"
						+ ce_id + "'");
		numberOfPendingTrasaction = contentValues.length;

	    ////Log.i(TAG, "numberOfPendingTrasaction" + numberOfPendingTrasaction);
		for (int i = 0; i < contentValues.length; i++) {

			type = contentValues[i].getAsString("type");
			ce_id = contentValues[i].getAsString("ce_id");
			trans_id = contentValues[i].getAsString("trans_id");
			strNoTrans = contentValues[i].getAsString("no_recs");
			Strtrans_param = contentValues[i].getAsString("trans_param");
			strdenoParam = contentValues[i].getAsString("deno");
			dep_amount = contentValues[i].getAsString("dep_amount");
			rec_status = contentValues[i].getAsString("rec_status");
			remarks = contentValues[i].getAsString("remarks");
			device_id = contentValues[i].getAsString("device_id");
            dep_type    =   contentValues[i].getAsString("dep_type");
            bank_name   =   contentValues[i].getAsString("bank_name");
            branch_name =   contentValues[i].getAsString("branch_name");
            account_no  =   contentValues[i].getAsString("account_no");
            vault_name  =   contentValues[i].getAsString("vault_name");
            dep_slip_no =   contentValues[i].getAsString("bank_dep_slip");
			if (Config.DEBUG) {
				//Log.i("Received Status: ", rec_status);
			}
			params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("opt", "rec_info"));
			params.add(new BasicNameValuePair("type", type));
			params.add(new BasicNameValuePair("ce_id", ce_id));
			params.add(new BasicNameValuePair("trans_id", trans_id));
			params.add(new BasicNameValuePair("no_recs", String
					.valueOf(strNoTrans)));
			params.add(new BasicNameValuePair("trans_param", Strtrans_param));
			//Log.e("test2", strdenoParam);
			params.add(new BasicNameValuePair("deno", strdenoParam));
			params.add(new BasicNameValuePair("dep_amount", dep_amount));
			params.add(new BasicNameValuePair("rec_status", rec_status));
			params.add(new BasicNameValuePair("remarks", remarks));
			params.add(new BasicNameValuePair("device_id", device_id));
            params.add(new BasicNameValuePair("dep_type",dep_type));
            params.add(new BasicNameValuePair("bank_name",bank_name));
            params.add(new BasicNameValuePair("branch_name",branch_name));
            params.add(new BasicNameValuePair("account_no",account_no));
            //Log.v("PendingTransactionProgress","depslip"+dep_slip_no);
            params.add(new BasicNameValuePair("deposit_slip_no",dep_slip_no));
            params.add(new BasicNameValuePair("vault_name",vault_name));
			params.add(new BasicNameValuePair("final", "1"));
		//	if (!progressDialog.isShowing())
//				progressDialog.show();/*
			getJson = new GetJson(context,this);
			getJson.execute(params);
		}
	}

	@Override
	public void onRequestCompleted(JSONObject object) {
		++serviceCompleted;
		if (serviceCompleted == numberOfPendingTrasaction) {
		//	progressDialog.dismiss();
            if(progressDialog.isShowing()||progressDialog!=null)
                progressDialog.dismiss();
		}
		if (object != null) {
			if (Config.DEBUG) {
			//	//Log.d(TAG, "Result Json: " + object.toString());
			}
			try {
				String status = object.getString("status");
				String trans_id = object.getString("trans_id");
				if (Config.DEBUG) {
				//	//Log.d(TAG, "Trans_id: " + trans_id + " Status: " + status);
				}
				if (status.equals("success")) {

					//Toast.makeText(
						//	context,
						//	"Pending transaction Completed with Transaction Id: "
						//			+ trans_id, Toast.LENGTH_LONG).show();

                    Intent intent1 = new Intent(context, SplashNew.class);
                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1, 0);
                    long[] vibrate = { 0, 100, 200, 300 };
                    Notification n  = new Notification.Builder(context)
                            .setContentTitle("Update Success")
                            .setContentText("Pending transaction Completed with Transaction Id: "+trans_id )
                            .setContentIntent(pIntent)
                            .setAutoCancel(true).setContentIntent(pIntent).setSmallIcon(R.drawable.icon)
                            .setSound(Uri.parse("android.resource://"
                                    + context.getPackageName() + "/" + R.raw.notify)).setVibrate(vibrate).getNotification();
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, n);

                    //context.startActivity(new Intent(context,Home.class).putExtra("ce_id",ce_id).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
					String transrec_id = object.getString("transrec_id");

				/*	dbHandler.execute("update ptransactions set transrec_id='"
							+ transrec_id + "', sent='yes' where trans_id='"
							+ trans_id + "'");
					dbHandler
							.execute("DELETE FROM ptransactions WHERE trans_id='"
									+ trans_id + "' and sent ='yes' ");*/

					if (Config.DEBUG) {
					//	//Log.i(TAG,
						//		"Transaction Completed with Transaction Receipt Id: "
						//				+ transrec_id + " and Transaction Id: "
						//				+ trans_id);
					}
                    if(Utils.isInternetAvailable(context)) {
                        dbHandler.execute("DELETE FROM ptransactions");
                        dbHandler.execute("DELETE FROM transactions");
                        dbHandler.execute("DELETE FROM receipt");
                    //    dbHandler.execute("DELETE FROM transactions_Entry");
                    }

                    //dbHandler.execute("DELETE FROM transactions");
                  /*  if(!progressDialog.isShowing()||progressDialog!=null)
                        progressDialog.show();*/
				} else if (Config.DEBUG) {
					////Log.i(TAG, "Transaction Failed with trans_id: " + trans_id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Config.DEBUG) {
			////Log.e(TAG, "Communication Failure");
            if(progressDialog.isShowing()||progressDialog!=null)
                progressDialog.dismiss();
		}

	}


	class Reg_Async extends AsyncTask<Void,Void,Void>
	{
		String                      test    =   "";
		StringBuilder       stringBuilder   =   new StringBuilder();
		ProgressDialog      progressDialog;
		@Override
		protected void onPreExecute() {
			test= "false";
			progressDialog      =   new ProgressDialog(context);
			progressDialog.setMessage("Loading...");
			progressDialog.show();

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void aVoid) {

			super.onPostExecute(aVoid);
		}

		@Override
		protected Void doInBackground(Void... param) {
			try{
				HttpClient httpClient      =   new DefaultHttpClient();
				HttpPost httpPost        =   new HttpPost(Config.url1);

				MultipartEntity multipartEntity =   new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				//multipartEntity.addPart("userid",new StringBody(ReceivePayment.this.getSharedPreferences(Config.shared_name, Context.MODE_PRIVATE).getString("userid","")));
//                multipartEntity.addPart("comments",new StringBody(str_comments));
				//              multipartEntity.addPart("prescription",new FileBody(new File(selectedImagePath/*"https://www.google.co.in/images/srpr/logo11w.png"*/),"image/png"));
//                //Log.v("Login","Selectedpath"+selectedImagePath);


				for(int i=0;i<params.size();i++)
					multipartEntity.addPart(params.get(i).getName(),new StringBody(params.get(i).getValue()));
				//params.add(new BasicNameValuePair("sign_image",img_path));
				multipartEntity.addPart("sign_image",new FileBody(new File(context.getFilesDir().getPath()+File.separator+trans_id)));
				//  multipartEntity.addPart("prescription",new FileBody(new File(selectedImagePath/*"https://www.google.co.in/images/srpr/logo11w.png"*/),"image/png"));
				httpPost.setEntity(multipartEntity);
				HttpResponse httpResponse    =   httpClient.execute(httpPost);

                /*list.add(new BasicNameValuePair("studentname",str_name));
                list.add(new BasicNameValuePair("studentclass",str_class));
                list.add(new BasicNameValuePair("rollno",str_roll));
                list.add(new BasicNameValuePair("department",str_dept));
                list.add(new BasicNameValuePair("password",str_pass));
                list.add(new BasicNameValuePair("profileimage", Base64.encodeToString(bytes,0)));

*/
				BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(),"UTF8"));
				while ((test=bufferedInputStream.readLine())!=null)
				{
					stringBuilder.append(test);
				}
				//Log.v("Login", stringBuilder.toString() + "asd" + httpResponse.getStatusLine().getStatusCode());

			} catch (Exception e){e.printStackTrace();
				progressDialog.dismiss();
			}
			return null;
		}
	}
}
