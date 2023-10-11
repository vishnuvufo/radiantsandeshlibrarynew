package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class BankDepositResponse {

    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("CashInHand")
    @Expose
    private String CashInHand;
    @SerializedName("PickupTransactions")
    @Expose
    private List<PickupTransaction> pickupTransactions = null;

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

    public String getCashInHand() {
        return CashInHand;
    }

    public void setCashInHand(String CashInHand) {
        this.CashInHand = CashInHand;
    }


    public List<PickupTransaction> getPickupTransactions() {
        return pickupTransactions;
    }

    public void setPickupTransactions(List<PickupTransaction> pickupTransactions) {
        this.pickupTransactions = pickupTransactions;
    }

}
