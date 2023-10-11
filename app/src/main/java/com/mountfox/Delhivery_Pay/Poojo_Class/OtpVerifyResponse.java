package com.mountfox.Delhivery_Pay.Poojo_Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpVerifyResponse {


    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private OtpVerifyResponseList status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OtpVerifyResponseList getStatus() {
        return status;
    }

    public void setStatus(OtpVerifyResponseList status) {
        this.status = status;
    }

}