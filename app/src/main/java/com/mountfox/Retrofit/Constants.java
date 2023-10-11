package com.mountfox.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Constants {


////////////////New Server Url
//          public static final String BASEURL = "http://www.radianterp.in/RCMS/";
//          public static final String BASEURLTWO = "http://www.radianterp.in/RCMS/";
//          public static final String LOCALBASEURL = "http://www.radianterp.in/RCMS/";
////        ********************************* TESTING URL ***********************************
//               public static final String BASEURL = "http://210.18.134.54/RCMS/";
//               public static final String BASEURLTWO = "http://210.18.134.54/RCMS/";
//               public static final String LOCALBASEURL = "http://210.18.134.54/RCMS/";
//        ********************************* TESTING URL ***********************************



    //        ********************************* TESTING URL ***********************************
//    public static final String BASEURL = "http://113.193.19.67/RCMS/";
//    public static final String BASEURLTWO = "http://113.193.19.67/RCMS/";
//    public static final String LOCALBASEURL = "http://113.193.19.67/RCMS/";
//        ********************************* TESTING URL ***********************************

    //        ********************************* TESTING URL ***********************************
//    public static final String BASEURL = "http://rcmstest.com/RCMS/";
//    public static final String BASEURLTWO = "http://rcmstest.com/RCMS/";
//    public static final String LOCALBASEURL = "http://rcmstest.com/RCMS/";
//        ********************************* TESTING URL ***********************************

    //        ********************************* TESTING URL ***********************************
//    public static final String BASEURL = "http://49.249.173.254/RCMS/";
//    public static final String BASEURLTWO = "http://49.249.173.254/RCMS/";
//    public static final String LOCALBASEURL = "http://49.249.173.254/RCMS/";
//        ********************************* TESTING URL ***********************************

//         ************************************* LIVE URL ***********************************************
           public static final String BASEURL = "http://sandeshone.scanslips.in/RCMS/";
           public static final String BASEURLTWO = "http://sandeshtwo.scanslips.in/RCMS/";
           public static final String LOCALBASEURL = "http://sandeshone.scanslips.in/RCMS/";
//        ************************************* LIVE URL ***********************************************




    // public static final String BASEURL = "http://sandeshone.scanslips.in/RCMS/";
 // public static final String BASEURL = "http://sandeshone.scanslips.in/RCMS/";
// public static final String BASEURL = "http://210.18.134.54/RCMS/";
  //public static final String BASEURL = "http://r2fs.scanslips.in/RCMS/";
    // public static final String BASEURL = "http://111.93.12.250/RCMS/";
// public static final String BASEURLTWO = "http://210.18.134.54/RCMS/";
  // public static final String BASEURLTWO = "http://rcmstest.scanslips.in/RCMS/";
 // public static final String BASEURLTWO = "http://210.18.134.54/RCMS/";
 //public static final String BASEURLTWO = "http://111.93.12.250/RCMS/";
// public static final String BASEURLTWO = "http://sandeshtwo.scanslips.in/RCMS/";
// public static final String LOCALBASEURL = "http://sandeshone.scanslips.in/RCMS/";
// public static final String LOCALBASEURL = "http://210.18.134.54/RCMS/";

    public static Retrofit getClient() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okBuilder.build())
                .build();
    }

    public static Retrofit getClientTwo() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(BASEURLTWO)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okBuilder.build())
                .build();
    }

    public static Retrofit getClientLocal() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(LOCALBASEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okBuilder.build())
                .build();
    }





    public static Retrofit getdelivery() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // print log req & res
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl("http://49.249.173.254/RCMS/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }



}

