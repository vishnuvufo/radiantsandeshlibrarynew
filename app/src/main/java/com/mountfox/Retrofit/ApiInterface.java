package com.mountfox.Retrofit;

import com.google.gson.JsonObject;
import com.mountfox.Delhivery_Pay.Poojo_Class.DelhiveryOtpResponse;
import com.mountfox.Delhivery_Pay.Poojo_Class.Delivery_EntryResponse;
import com.mountfox.Delhivery_Pay.Poojo_Class.OtpVerifyResponse;
import com.mountfox.Delhivery_Pay.Poojo_Class.PropertyRespone;
import com.mountfox.UploadImageListpojo;
import com.mountfox.UploadPojo;
import com.mountfox.otpresponse.OtpResponse;
import com.mountfox.response.AddDepositResponse;
import com.mountfox.response.BankDepositAccountResponse;
import com.mountfox.response.BankDepositBankname;
import com.mountfox.response.BankDepositResponse;
import com.mountfox.response.BankResponse;
import com.mountfox.response.PickupStandardRemarks;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {

////otp pin recive payment
    @POST("otp_request_from_mob.php")
    @Headers("Accept: application/json")
    Call<OtpResponse> doOtpResponse(@Body JsonObject jsonObject);

//deposit
    @GET("MobileAppBankDeposit.php")
    Call<BankDepositResponse> doBankDepositResponse(@Query("opt") String opt,
                                        @Query("ce_id") String ceid,
                                        @Query("trans_date") String trans_date);

    @GET("MobileAppBankDepositGetData.php")
    Call<BankDepositBankname> doBankNameResponse(@Query("opt") String opt);



    @GET("MobileAppBankDepositGetData.php")
    Call<BankDepositAccountResponse> doBankDepositAccountResponse(@Query("opt") String opt,
                                                           @Query("bank_name") String bank_name);

    @POST("MobileAppBankDeposit.php?opt=AddDeposit")
    Call<AddDepositResponse> doAdddepositWithremarks(@Query("DepositeDate") String DepositeDate,
                                        @Query("Ce_ID") String Ce_ID,
                                        @Query("DepositType") String DepositType,
                                        @Query("AccountID") String AccountID,
                                        @Query("DepositAmount") String DepositAmount,
                                        @Query("TransactionID") String TransactionID,
                                        @Query("CountOfTransactions") String CountOfTransactions,
                                        @Query("BankName") String BankName,
                                        @Query("DepositBranch") String DepositBranch,
                                        @Query("Remarks") String Remarks);
//// deposit
    @POST("MobileAppBankDeposit.php?opt=AddDeposit")
    @Headers("Accept: application/json")
    Call<AddDepositResponse> doAdddepositWithremarks(@Body JsonObject jsonObject);


    @GET("mfservices_test_2000.php")
    Call<BankResponse> doBankResponse(@Query("opt") String opt);

    ////  master remarks
    @GET("App-remark/ListMasterRemark.php")
    Call<PickupStandardRemarks> doStandardRemarksResponse();


    // CHILD REMARKS
    @POST("App-remark/ListChildRemark.php")
    @Headers("Accept: application/json")
    Call<PickupStandardRemarks> doPostStandardRemarks(@Body JsonObject jsonObject);




    /////  dELIVERY EMPLOYEE iD ////
    @POST("ce_validate.php")
    @Headers("Accept: application/json")
    Call<OtpVerifyResponse> getemployeeid(@Body JsonObject jsonObject);

    /////  dELIVERY getPropertyRespone ////
    @POST("get_property.php")
    @Headers("Accept: application/json")
    Call<PropertyRespone> getPropertyRespone(@Body JsonObject jsonObject);

    /////  dELIVERY deliveryEntryCash ////
    @POST("delhivery_api_rcms.php")
    @Headers("Accept: application/json")
    Call<Delivery_EntryResponse> getdeliveryEntryCash(@Body JsonObject jsonObject);


    @POST("delhivery_otp_api_rcms.php")
    @Headers("Accept: application/json")
    Call<DelhiveryOtpResponse> getdeliveryotp(@Body JsonObject jsonObject);

    @POST("delhivery_otp_api_rcms.php")
    @Headers("Accept: application/json")
    Call<OtpVerifyResponse> getotpverify(@Body JsonObject jsonObject);




    // image upload list

    @Headers("Content-Type: application/json")
    @POST("list_slip_txn.php")
    Call<UploadImageListpojo> getuploadimageList(@Body JsonObject body);

    // image upload

    @Headers("Content-Type: application/json")
    @POST("upload_image_slip.php")
    Call<UploadPojo> getuploadimage(@Body JsonObject body);

}
