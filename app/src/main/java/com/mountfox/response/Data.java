package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Data {

    @SerializedName("remark")
    @Expose
    private List<List<String>> remark = null;

    public List<List<String>> getRemark() {
        return remark;
    }

    public void setRemark(List<List<String>> remark) {
        this.remark = remark;
    }

}
