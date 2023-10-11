package com.mountfox.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PickupTransaction {

    @SerializedName("UniqueID")
    @Expose
    private String UniqueID;
    @SerializedName("trans_id")
    @Expose
    private String transId;
    @SerializedName("clientName")
    @Expose
    private String clientName;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("pickupName")
    @Expose
    private String pickupName;
    @SerializedName("RequestAmount")
    @Expose
    private String requestAmount;
    @SerializedName("TransactionType")
    @Expose
    private String transactionType;
    @SerializedName("PickupType")
    @Expose
    private String pickupType;
    @SerializedName("CollectionAmount")
    @Expose
    private String collectionAmount;
    @SerializedName("depositType")
    @Expose
    private String depositType;
    @SerializedName("PointID")
    @Expose
    private String pointID;
    @SerializedName("checkBoxStatus")
    @Expose
    private String checkBoxStatus;
    @SerializedName("DepositedAmount")
    @Expose
    private String depositedAmount;
    @SerializedName("BalanceAmount")
    @Expose
    private String balanceAmount;
    @SerializedName("Multi")
    @Expose
    private String multi;
    @SerializedName("TableType")
    @Expose
    private String tableType;

    public String getUniqueID() {
        return UniqueID;
    }

    public void setUniqueID(String UniqueID) {
        this.UniqueID = UniqueID;
    }
    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPickupName() {
        return pickupName;
    }

    public void setPickupName(String pickupName) {
        this.pickupName = pickupName;
    }

    public String getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(String requestAmount) {
        this.requestAmount = requestAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getPickupType() {
        return pickupType;
    }

    public void setPickupType(String pickupType) {
        this.pickupType = pickupType;
    }

    public String getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(String collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getPointID() {
        return pointID;
    }

    public void setPointID(String pointID) {
        this.pointID = pointID;
    }

    public String getCheckBoxStatus() {
        return checkBoxStatus;
    }

    public void setCheckBoxStatus(String checkBoxStatus) {
        this.checkBoxStatus = checkBoxStatus;
    }

    public String getDepositedAmount() {
        return depositedAmount;
    }

    public void setDepositedAmount(String depositedAmount) {
        this.depositedAmount = depositedAmount;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getMulti() {
        return multi;
    }

    public void setMulti(String multi) {
        this.multi = multi;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }


}
