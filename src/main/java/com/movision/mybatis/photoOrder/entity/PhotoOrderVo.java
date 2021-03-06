package com.movision.mybatis.photoOrder.entity;

import java.util.Date;

/**
 * @Author zhanglei
 * @Date 2018/2/1 15:41
 */
public class PhotoOrderVo {

    private Integer id;

    private Integer userid;

    private Double money;

    private Integer photoid;

    private String nickname;

    private String photo;
    private Integer paytype;//1 支付宝  2微信
    private Integer paycode;
    private Date paytime;
    private Double paymoney;
    private Integer ismian;//1 免单 2不免单

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    public Integer getPaycode() {
        return paycode;
    }

    public void setPaycode(Integer paycode) {
        this.paycode = paycode;
    }

    public Date getPaytime() {
        return paytime;
    }

    public void setPaytime(Date paytime) {
        this.paytime = paytime;
    }

    public Double getPaymoney() {
        return paymoney;
    }

    public void setPaymoney(Double paymoney) {
        this.paymoney = paymoney;
    }

    public Integer getIsmian() {
        return ismian;
    }

    public void setIsmian(Integer ismian) {
        this.ismian = ismian;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Integer getPhotoid() {
        return photoid;
    }

    public void setPhotoid(Integer photoid) {
        this.photoid = photoid;
    }

}
