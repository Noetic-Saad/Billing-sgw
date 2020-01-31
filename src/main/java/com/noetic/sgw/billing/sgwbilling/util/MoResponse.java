package com.noetic.sgw.billing.sgwbilling.util;

public class MoResponse {

    private int code;
    private String msg;
    private String correlationId;
    private int chargingMechanism;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public int getChargingMechanism() {
        return chargingMechanism;
    }

    public void setChargingMechanism(int chargingMechanism) {
        this.chargingMechanism = chargingMechanism;
    }
}