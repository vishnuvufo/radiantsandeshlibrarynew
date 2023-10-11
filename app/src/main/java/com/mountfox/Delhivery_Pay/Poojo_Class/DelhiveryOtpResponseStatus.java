package com.mountfox.Delhivery_Pay.Poojo_Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DelhiveryOtpResponseStatus {



    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("otp")
    @Expose
    private String otp;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}