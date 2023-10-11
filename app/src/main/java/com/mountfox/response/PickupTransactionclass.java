package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickupTransactionclass {

    @SerializedName("PickupTransactions")
    @Expose
    private Map<String, List<PickupTransaction>> elemDetails = new HashMap<>();

    public Map<String, List<PickupTransaction>> getElemDetails() {
        return elemDetails;
    }

    public void setElemDetails(Map<String, List<PickupTransaction>> elemDetails) {
        this.elemDetails = elemDetails;
    }

}
