package com.mountfox.Services.dataRequest;


import com.mountfox.Services.RequestData;

import org.json.JSONException;
import org.json.JSONObject;

public class SubmitChequePickupDetailsRequestData implements RequestData {

  private String opt;
  private String ce_Id;
  private String transactionId;
  private String deviceId;
  private String IMEI_No;
  private String latitude;
  private String longitude;
  private String noOfCheque;
  private String chequeNo;
  private String receiptNo;
  private String hciSlipNo;
  private String chequeAmt;
  private String depositBank;
  private String accNo;
  private String sendTime;
  private String destination;
  private String distance;
  private String chargers;
  private String courierName;
  private String PODno;
  private String courierStatus;
  private String scanCopy;
  private String remarks;
  private String noOfTransaction;

  @Override
  public String constructRequestData() {

    JSONObject requestJSON = new JSONObject();

    try {
      requestJSON.put("opt", getOpt());
      requestJSON.put("ce_id", getCe_Id());
      requestJSON.put("trans_id", getTransactionId());
      requestJSON.put("noOfTransaction", getNoOfTransaction());
      requestJSON.put("noOfCheque", getNoOfCheque());
//      requestJSON.put("clientCode", getTransactionId());
//      requestJSON.put("pickupPointCode", getTransactionId());
      requestJSON.put("chequeNo", getChequeNo());
      requestJSON.put("chequeAmount", getChequeAmt());
      requestJSON.put("receiptNo", getReceiptNo());
      requestJSON.put("hclSlipNO", getHciSlipNo());
      requestJSON.put("depositBank", getDepositBank());
      requestJSON.put("accountNo", getAccNo());
      requestJSON.put("sendTime", getSendTime());
      requestJSON.put("destination", getDestination());
      requestJSON.put("distancectoBank", getDistance());
      requestJSON.put("charges", getChargers());
      requestJSON.put("courierName", getCourierName());
      requestJSON.put("podNo", getPODno());
      requestJSON.put("courierStatus", getCourierStatus());
      requestJSON.put("scanCopy", getScanCopy());
      requestJSON.put("remarks", getRemarks());

      requestJSON.put("device_id", getDeviceId());
      requestJSON.put("IMEI_no", getIMEI_No());
      requestJSON.put("Latitude", getLatitude());
      requestJSON.put("Longitude", getLongitude());

    } catch (JSONException e) {
      e.printStackTrace();
    }

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


  public void setNoOfCheque(String noOfCheque) {
    this.noOfCheque = noOfCheque;
  }

  public String getNoOfCheque() {
    return noOfCheque;
  }

  public void setChequeNo(String chequeNo) {
    this.chequeNo = chequeNo;
  }

  public String getChequeNo() {
    return chequeNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setHciSlipNo(String hciSlipNo) {
    this.hciSlipNo = hciSlipNo;
  }

  public String getHciSlipNo() {
    return hciSlipNo;
  }

  public void setChequeAmt(String chequeAmt) {
    this.chequeAmt = chequeAmt;
  }

  public String getChequeAmt() {
    return chequeAmt;
  }

  public void setDepositBank(String depositBank) {
    this.depositBank = depositBank;
  }

  public String getDepositBank() {
    return depositBank;
  }

  public void setAccNo(String accNo) {
    this.accNo = accNo;
  }

  public String getAccNo() {
    return accNo;
  }

  public void setSendTime(String sendTime) {
    this.sendTime = sendTime;
  }

  public String getSendTime() {
    return sendTime;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public String getDestination() {
    return destination;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

  public String getDistance() {
    return distance;
  }

  public void setChargers(String chargers) {
    this.chargers = chargers;
  }

  public String getChargers() {
    return chargers;
  }

  public void setCourierName(String courierName) {
    this.courierName = courierName;
  }

  public String getCourierName() {
    return courierName;
  }

  public void setPODno(String PODno) {
    this.PODno = PODno;
  }

  public String getPODno() {
    return PODno;
  }

  public void setCourierStatus(String courierStatus) {
    this.courierStatus = courierStatus;
  }

  public String getCourierStatus() {
    return courierStatus;
  }

  public void setScanCopy(String scanCopy) {
    this.scanCopy = scanCopy;
  }

  public String getScanCopy() {
    return scanCopy;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setNoOfTransaction(String noOfTransaction) {
    this.noOfTransaction = noOfTransaction;
  }

  public String getNoOfTransaction() {
    return noOfTransaction;
  }
}
