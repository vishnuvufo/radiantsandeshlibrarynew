package com.mountfox;

/**
 * Created by RITS03 on 02-02-2016.
 */
public class ChangePinPojo {

    String  shop_id;
    String  point_name;
    String  cust_name;
    String  type;
    String  amount;
    String  pin_status;
    String  pin_no;

    public ChangePinPojo(String shop_id, String point_name, String cust_name, String type, String amount, String pin_status, String pin_no) {
        this.shop_id = shop_id;
        this.point_name = point_name;
        this.cust_name = cust_name;
        this.type = type;
        this.amount = amount;
        this.pin_status = pin_status;
        this.pin_no = pin_no;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getPoint_name() {
        return point_name;
    }

    public void setPoint_name(String point_name) {
        this.point_name = point_name;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPin_status() {
        return pin_status;
    }

    public void setPin_status(String pin_status) {
        this.pin_status = pin_status;
    }

    public String getPin_no() {
        return pin_no;
    }

    public void setPin_no(String pin_no) {
        this.pin_no = pin_no;
    }
}
