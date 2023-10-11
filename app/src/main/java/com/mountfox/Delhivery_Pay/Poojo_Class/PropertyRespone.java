package com.mountfox.Delhivery_Pay.Poojo_Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PropertyRespone {


    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private List<PropertyResponeList> data;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<PropertyResponeList> getData() {
        return data;
    }

    public void setData(List<PropertyResponeList> data) {
        this.data = data;
    }

}