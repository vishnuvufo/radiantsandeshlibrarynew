package com.mountfox.Delhivery_Pay.Poojo_Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Delivery_EntryResponse {


    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

}