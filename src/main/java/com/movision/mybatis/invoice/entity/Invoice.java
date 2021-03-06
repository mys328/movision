package com.movision.mybatis.invoice.entity;

import java.io.Serializable;

public class Invoice implements Serializable {
    private Integer id;

    private Integer orderid;

    private String head;

    private Integer addressid;

    private Integer onlystatue;

    private String phone;

    private Double money;

    private String name;

    private String code;

    private String bank;

    private String banknum;

    private String content;

    private Integer kind;

    private String companyname;

    private String rigaddress;

    private String rigphone;

    private String logisticsnumber;//快递单号

    private Integer logisticsway;//快递方式

    public Integer getOnlystatue() {
        return onlystatue;
    }

    public void setOnlystatue(Integer onlystatue) {
        this.onlystatue = onlystatue;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getRigaddress() {
        return rigaddress;
    }

    public void setRigaddress(String rigaddress) {
        this.rigaddress = rigaddress;
    }

    public String getRigphone() {
        return rigphone;
    }

    public void setRigphone(String rigphone) {
        this.rigphone = rigphone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBanknum() {
        return banknum;
    }

    public void setBanknum(String banknum) {
        this.banknum = banknum;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head == null ? null : head.trim();
    }

    public Integer getAddressid() {
        return addressid;
    }

    public void setAddressid(Integer addressid) {
        this.addressid = addressid;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getKind() {
        return kind;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }

    public String getLogisticsnumber() {
        return logisticsnumber;
    }

    public void setLogisticsnumber(String logisticsnumber) {
        this.logisticsnumber = logisticsnumber;
    }

    public Integer getLogisticsway() {
        return logisticsway;
    }

    public void setLogisticsway(Integer logisticsway) {
        this.logisticsway = logisticsway;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}