package com.mountfox;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListSumitTrans {
    @SerializedName("trans_id")
    @Expose
    private String transId;
    @SerializedName("coll_id")
    @Expose
    private String collId;
    @SerializedName("cust_name")
    @Expose
    private String custName;
    @SerializedName("mul_status")
    @Expose
    private String mulStatus;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getCollId() {
        return collId;
    }

    public void setCollId(String collId) {
        this.collId = collId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getMulStatus() {
        return mulStatus;
    }

    public void setMulStatus(String mulStatus) {
        this.mulStatus = mulStatus;
    }

}