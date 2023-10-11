package com.mountfox.Delhivery_Pay.Poojo_Class;

import com.google.android.gms.common.api.Status;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DelhiveryOtpResponse {


    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private DelhiveryOtpResponseStatus status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DelhiveryOtpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(DelhiveryOtpResponseStatus status) {
        this.status = status;
    }

}