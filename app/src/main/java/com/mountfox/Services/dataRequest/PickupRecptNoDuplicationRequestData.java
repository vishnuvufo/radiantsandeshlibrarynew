package com.mountfox.Services.dataRequest;


import com.mountfox.Services.RequestData;

import org.json.JSONException;
import org.json.JSONObject;

public class PickupRecptNoDuplicationRequestData implements RequestData {

  private String opt;
  private String ce_Id;
  private String recptNo;
  private String clientId;

  @Override
  public String constructRequestData() {

    JSONObject requestJSON = new JSONObject();

    try {
      requestJSON.put("opt", getOpt());
      requestJSON.put("ce_id", getCe_Id());
      requestJSON.put("recptNo", getRecptNo());
      requestJSON.put("clientId", getClientId());

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

  public void setRecptNo(String recptNo) {
    this.recptNo = recptNo;
  }

  public String getRecptNo() {
    return recptNo;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientId() {
    return clientId;
  }
}
