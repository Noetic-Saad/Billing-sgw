package com.noetic.sgw.billing.sgwbilling.util;

import java.util.Date;

public class ChargeRequestProperties {

    private long msisdn;
    private Integer operatorId;
    private Integer vendorPlanId;
    private String correlationId;
    private Date originDateTime;
    private double chargingAmount;
    private String shortcode;
    private double taxAmount;
    private double shareAmount;
    private int attempts;
    private int isRenewal;
    private int dailyAttempts;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(double shareAmount) {
        this.shareAmount = shareAmount;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public Integer getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(Integer vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Date getOriginDateTime() {
        return originDateTime;
    }

    public void setOriginDateTime(Date originDateTime) {
        this.originDateTime = originDateTime;
    }

    public double getChargingAmount() {
        return chargingAmount;
    }

    public void setChargingAmount(double chargingAmount) {
        this.chargingAmount = chargingAmount;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getIsRenewal() {
        return isRenewal;
    }

    public void setIsRenewal(int isRenewal) {
        this.isRenewal = isRenewal;
    }

    public int getDailyAttempts() {
        return dailyAttempts;
    }

    public void setDailyAttempts(int dailyAttempts) {
        this.dailyAttempts = dailyAttempts;
    }
}
