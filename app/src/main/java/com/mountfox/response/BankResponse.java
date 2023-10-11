package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BankResponse {

    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("banks")
    @Expose
    private List<BankListResponse> banks = null;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<BankListResponse> getBanks() {
        return banks;
    }

    public void setBanks(List<BankListResponse> banks) {
        this.banks = banks;
    }



}
