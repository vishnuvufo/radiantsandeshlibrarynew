package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BankDepositBankname {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("BankDetails")
    @Expose
    private List<String> bankDetails = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(List<String> bankDetails) {
        this.bankDetails = bankDetails;
    }


}
