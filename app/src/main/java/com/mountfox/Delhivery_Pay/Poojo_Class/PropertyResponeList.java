package com.mountfox.Delhivery_Pay.Poojo_Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PropertyResponeList {



    @SerializedName("dcid")
    @Expose
    private String dcid;
    @SerializedName("property_city")
    @Expose
    private String propertyCity;

    public String getDcid() {
        return dcid;
    }

    public void setDcid(String dcid) {
        this.dcid = dcid;
    }

    public String getPropertyCity() {
        return propertyCity;
    }

    public void setPropertyCity(String propertyCity) {
        this.propertyCity = propertyCity;
    }

}
