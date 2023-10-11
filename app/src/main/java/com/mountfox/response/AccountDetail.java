package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountDetail {

    @SerializedName("AccountID")
    @Expose
    private String accountID;
    @SerializedName("BankName")
    @Expose
    private String bankName;
    @SerializedName("BranchName")
    @Expose
    private String branchName;
    @SerializedName("AccountNo")
    @Expose
    private String accountNo;
    @SerializedName("Type")
    @Expose
    private String type;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
