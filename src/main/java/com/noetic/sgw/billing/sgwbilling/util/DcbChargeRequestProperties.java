package com.noetic.sgw.billing.sgwbilling.util;

public class DcbChargeRequestProperties {

    private String msisdn;
    private int operator_id;
    private String pricePoint;
    private String dcbServiceId;
    private String taxAmount;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(int operator_id) {
        this.operator_id = operator_id;
    }

    public String getPricePoint() {
        return pricePoint;
    }

    public void setPricePoint(String pricePoint) {
        this.pricePoint = pricePoint;
    }

    public String getDcbServiceId() {
        return dcbServiceId;
    }

    public void setDcbServiceId(String dcbServiceId) {
        this.dcbServiceId = dcbServiceId;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }
}
