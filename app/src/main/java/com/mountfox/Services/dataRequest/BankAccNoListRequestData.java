package com.mountfox.Services.dataRequest;


import com.mountfox.Services.RequestData;

import org.json.JSONException;
import org.json.JSONObject;

public class BankAccNoListRequestData implements RequestData {

  private String opt;
  private Object accNo;
  private Object bankType;

  @Override
  public String constructRequestData() {

    JSONObject requestJSON = new JSONObject();

    try {
      requestJSON.put("opt", getOpt());
      requestJSON.put("ac_type", getBankType());
      requestJSON.put("ac_no", getAccNo());


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

  public Object getAccNo() {
    return accNo;
  }

  public void setAccNo(String accNo) {
    this.accNo = accNo;
  }

  public Object getBankType() {
    return bankType;
  }

  public void setBankType(String bankType) {
    this.bankType = bankType;
  }
}
