package com.noetic.sgw.billing.sgwbilling.util;

public class DcbApiResponse {

    private int code;
    private String msg;
    private String operatorResponse;

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

    public String getOperatorResponse() {
        return operatorResponse;
    }

    public void setOperatorResponse(String operatorResponse) {
        this.operatorResponse = operatorResponse;
    }
}
