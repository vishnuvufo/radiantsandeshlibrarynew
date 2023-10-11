package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankListResponse {

    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("acc_no")
    @Expose
    private String accNo;
    @SerializedName("acc_id")
    @Expose
    private String accId;

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

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

}
