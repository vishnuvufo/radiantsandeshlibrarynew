package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DepositTransaction {

    @SerializedName("trans_id")
    @Expose
    private String transId;
    @SerializedName("ent_type")
    @Expose
    private String entType;
    @SerializedName("depositType")
    @Expose
    private String depositType;
    @SerializedName("acc_id")
    @Expose
    private String accId;
    @SerializedName("ce_id")
    @Expose
    private String ceId;
    @SerializedName("depositAmount")
    @Expose
    private String depositAmount;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("staffID")
    @Expose
    private String staffID;
    @SerializedName("depositID")
    @Expose
    private String depositID;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getEntType() {
        return entType;
    }

    public void setEntType(String entType) {
        this.entType = entType;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getCeId() {
        return ceId;
    }

    public void setCeId(String ceId) {
        this.ceId = ceId;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getDepositID() {
        return depositID;
    }

    public void setDepositID(String depositID) {
        this.depositID = depositID;
    }






}
