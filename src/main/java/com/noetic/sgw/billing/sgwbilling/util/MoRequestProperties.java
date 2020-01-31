package com.noetic.sgw.billing.sgwbilling.util;

import java.util.Date;

public class MoRequestProperties {

    private long msisdn;
    private long partnerPlanId;
    private String correlationId;
    private Date originDateTime;
    private double chargingAmount;
    private String shortcode;
    private double taxAmount;
    private double shareAmount;
    private long operatorId;
    private int chargingMechanism;

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public long getPartnerPlanId() {
        return partnerPlanId;
    }

    public void setPartnerPlanId(long partnerPlanId) {
        this.partnerPlanId = partnerPlanId;
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

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public double getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(double shareAmount) {
        this.shareAmount = shareAmount;
    }

    public int getChargingMechanism() {
        return chargingMechanism;
    }

    public void setChargingMechanism(int chargingMechanism) {
        this.chargingMechanism = chargingMechanism;
    }
}
