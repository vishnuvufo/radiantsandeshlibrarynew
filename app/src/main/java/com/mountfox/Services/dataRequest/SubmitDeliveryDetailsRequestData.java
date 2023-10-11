package com.mountfox.Services.dataRequest;


import com.mountfox.Services.RequestData;

import org.json.JSONException;
import org.json.JSONObject;

public class SubmitDeliveryDetailsRequestData implements RequestData {

  private String opt;
  private String ce_Id;
  private String transactionId;
  private String withdrawStatus;
  private String reqAmt;
  private String deliveryAmt;
  private String differenceAmt;
  private String deliveryTo;
  private String receiptNo;
  private String CE_Name;
  private String refNo;
  private String remarkType;
  private String otherRemark;
  private String accType;
  private String accNo;
  private String bankName;
  private String branchName;
  private String chequeNo;
  private String chequeAmt;
  private String withdrawTime;
  private String denominations;
  private String pickupDiffNo;
  private String currentDate;
  private String currentTime;
  private String deviceId;
  private String IMEI_No;
  private String latitude;
  private String longitude;
  private String clientCode;
  private String receiptStatus;
  private String accId;
  private String deliveryTime;

  @Override
  public String constructRequestData() {

    JSONObject requestJSON = new JSONObject();

    try {
      requestJSON.put("opt", getOpt());
      requestJSON.put("ce_id", getCe_Id());
      requestJSON.put("trans_id", getTransactionId());
      requestJSON.put("client_code", getClientCode());
      requestJSON.put("Withdraw_Status", getWithdrawStatus());
      requestJSON.put("Request_Amount", getReqAmt());
      requestJSON.put("Delivery_Amount", getDeliveryAmt());
      requestJSON.put("Difference_Amount", getDifferenceAmt());
      requestJSON.put("Delivery_To", getDeliveryTo());
      requestJSON.put("Cash_Receipt_No", getReceiptNo());
      requestJSON.put("RecStatus", getReceiptStatus());
      requestJSON.put("CE_Name", getCE_Name());
      requestJSON.put("Delivery_Time", getDeliveryTime());
      requestJSON.put("Ref_No", getRefNo());
      requestJSON.put("RemarkType", getRemarkType());
      requestJSON.put("Other_Remarks", getOtherRemark());
      requestJSON.put("Acc_Type", getAccType());
      requestJSON.put("Account_No", getAccNo());
      requestJSON.put("Bank_Name", getBankName());
      requestJSON.put("Branch_Name", getBranchName());
      requestJSON.put("AccId", getAccId());
      requestJSON.put("Cheque_No", getChequeNo());
      requestJSON.put("Cheque_Amount", getChequeAmt());
      requestJSON.put("Withdraw_Time", getWithdrawTime());
      requestJSON.put("Denominations", getDenominations());
      requestJSON.put("Pick_diff_no", getPickupDiffNo());
      requestJSON.put("Current_Date", getCurrentDate());
      requestJSON.put("Current_Time", getCurrentTime());
      requestJSON.put("device_id", getDeviceId());
      requestJSON.put("IMEI_no", getIMEI_No());
      requestJSON.put("Latitude", getLatitude());
      requestJSON.put("Longitude", getLongitude());

    } catch (JSONException e) {
      e.printStackTrace();
    }

//    return loginDetails.toString();
    return requestJSON.toString();
  }

  public void setOpt(String opt) {
    this.opt = opt;
  }

  public String getOpt() {
    return opt;
  }

  public void setCe_Id(String ce_Id) {
    this.ce_Id = ce_Id;
  }

  public String getCe_Id() {
    return ce_Id;
  }


  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setWithdrawStatus(String withdrawStatus) {
    this.withdrawStatus = withdrawStatus;
  }

  public String getWithdrawStatus() {
    return withdrawStatus;
  }

  public void setReqAmt(String reqAmt) {
    this.reqAmt = reqAmt;
  }

  public String getReqAmt() {
    return reqAmt;
  }

  public void setDeliveryAmt(String deliveryAmt) {
    this.deliveryAmt = deliveryAmt;
  }

  public String getDeliveryAmt() {
    return deliveryAmt;
  }

  public void setDifferenceAmt(String differenceAmt) {
    this.differenceAmt = differenceAmt;
  }


  public String getDifferenceAmt() {
    return differenceAmt;
  }

  public void setDeliveryTo(String deliveryTo) {
    this.deliveryTo = deliveryTo;
  }

  public String getDeliveryTo() {
    return deliveryTo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setCE_Name(String CE_Name) {
    this.CE_Name = CE_Name;
  }

  public String getCE_Name() {
    return CE_Name;
  }

  public void setRefNo(String refNo) {
    this.refNo = refNo;
  }

  public String getRefNo() {
    return refNo;
  }

  public void setRemarkType(String remarkType) {
    this.remarkType = remarkType;
  }

  public String getRemarkType() {
    return remarkType;
  }

  public void setOtherRemark(String otherRemark) {
    this.otherRemark = otherRemark;
  }

  public String getOtherRemark() {
    return otherRemark;
  }

  public void setAccType(String accType) {
    this.accType = accType;
  }

  public String getAccType() {
    return accType;
  }

  public void setAccNo(String accNo) {
    this.accNo = accNo;
  }

  public String getAccNo() {
    return accNo;
  }

  public void setBankName(String bankName) {
    this.bankName = bankName;
  }

  public String getBankName() {
    return bankName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setChequeNo(String chequeNo) {
    this.chequeNo = chequeNo;
  }

  public String getChequeNo() {
    return chequeNo;
  }

  public void setChequeAmt(String chequeAmt) {
    this.chequeAmt = chequeAmt;
  }

  public String getChequeAmt() {
    return chequeAmt;
  }

  public void setWithdrawTime(String withdrawTime) {
    this.withdrawTime = withdrawTime;
  }

  public String getWithdrawTime() {
    return withdrawTime;
  }

  public void setDenominations(String denominations) {
    this.denominations = denominations;
  }

  public String getDenominations() {
    return denominations;
  }

  public void setPickupDiffNo(String pickupDiffNo) {
    this.pickupDiffNo = pickupDiffNo;
  }

  public String getPickupDiffNo() {
    return pickupDiffNo;
  }

  public void setCurrentDate(String currentDate) {
    this.currentDate = currentDate;
  }

  public String getCurrentDate() {
    return currentDate;
  }

  public void setCurrentTime(String currentTime) {
    this.currentTime = currentTime;
  }

  public String getCurrentTime() {
    return currentTime;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setIMEI_No(String IMEI_No) {
    this.IMEI_No = IMEI_No;
  }

  public String getIMEI_No() {
    return IMEI_No;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getLongitude() {
    return longitude;
  }


  public void setClientCode(String clientCode) {
    this.clientCode = clientCode;
  }

  public String getClientCode() {
    return clientCode;
  }

  public void setReceiptStatus(String receiptStatus) {
    this.receiptStatus = receiptStatus;
  }

  public String getReceiptStatus() {
    return receiptStatus;
  }

  public void setAccId(String accId) {
    this.accId = accId;
  }

  public String getAccId() {
    return accId;
  }

  public void setDeliveryTime(String deliveryTime) {
    this.deliveryTime = deliveryTime;
  }

  public String getDeliveryTime() {
    return deliveryTime;
  }
}
