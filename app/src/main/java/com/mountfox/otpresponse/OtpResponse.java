package com.mountfox.otpresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpResponse {

    @SerializedName("Status")
    @Expose
    private Boolean status;
    @SerializedName("otp_pin")
    @Expose
    private String otpPin;
    @SerializedName("trans_id")
    @Expose
    private String transId;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getOtpPin() {
        return otpPin;
    }

    public void setOtpPin(String otpPin) {
        this.otpPin = otpPin;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }


}
