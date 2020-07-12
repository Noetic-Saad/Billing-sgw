package com.noetic.sgw.billing.sgwbilling.service;

import com.noetic.sgw.billing.sgwbilling.config.StartConfiguration;
import com.noetic.sgw.billing.sgwbilling.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DcbChargingService {

    @Autowired private StartConfiguration startConfiguration;
    @Autowired private ChargingService chargingService;
    private long operator_id;

    public DcbApiResponse processRequest(DcbChargeRequestProperties properties) throws Exception {

        operator_id = properties.getOperator_id();
        ChargeRequestProperties requestProperties = new ChargeRequestProperties();

        requestProperties.setAttempts(0);

        if (operator_id == startConfiguration.getZong()) {
            requestProperties.setChargingAmount(Double.valueOf(properties.getPricePoint()));
        }else {
            requestProperties.setTaxAmount(Double.parseDouble(properties.getTaxAmount()));
            requestProperties.setChargingAmount(Double.parseDouble(properties.getPricePoint()));
        }

        requestProperties.setCorrelationId("");
        requestProperties.setDailyAttempts(0);
        requestProperties.setIsRenewal(0);
        requestProperties.setOperatorId(properties.getOperator_id());
        requestProperties.setMsisdn(Long.valueOf(properties.getMsisdn()));
        requestProperties.setShortcode("");
        requestProperties.setSubCycleId(0);
        requestProperties.setOriginDateTime(new Date());
        requestProperties.setDcb(true);

        Response response = chargingService.processRequest(requestProperties);

        if (operator_id == startConfiguration.getZong()) {
            return processZongResponse(response);
        }else if (operator_id == startConfiguration.getJazz()) {
            return processJazzResponse(response);
        }else if(operator_id == startConfiguration.getTelenor()){
            return processTelenorResponse(response);
        }else {
            return null;
        }

    }

    public DcbApiResponse processJazzResponse(Response response){

        DcbApiResponse res = new DcbApiResponse();

        if (response == null) {
            res.setCode(Integer.parseInt(ResponseTypeConstants.REMOTE_SERVER_CONNECTION_ERROR));
            res.setMsg(startConfiguration.getResultStatusDescription(ResponseTypeConstants.REMOTE_SERVER_CONNECTION_ERROR));
        } else if (response.getCode() == 0) {
            res.setOperatorResponse(String.valueOf(response.getCode()));
            res.setCode(ResponseTypeConstants.CHARGED_SUCCESSFUL);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.CHARGED_SUCCESSFUL)));
        } else if (response.getCode() == 102) {
            res.setOperatorResponse(String.valueOf(response.getCode()));
            res.setCode(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND)));
        } else if (response.getCode() == 124) {
            res.setOperatorResponse(String.valueOf(response.getCode()));
            res.setCode(ResponseTypeConstants.INSUFFICIENT_BALANCE);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.INSUFFICIENT_BALANCE)));
        } else {
            res.setOperatorResponse(String.valueOf(response.getCode()));
            res.setCode(ResponseTypeConstants.OTHER_ERROR);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.OTHER_ERROR)));
        }

        return res;

    }

    public DcbApiResponse processZongResponse(Response response){
        DcbApiResponse res = new DcbApiResponse();
        if(response.getCode()==0000){
            res.setCode(ResponseTypeConstants.CHARGED_SUCCESSFUL);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.CHARGED_SUCCESSFUL)));
            res.setOperatorResponse(String.valueOf(response.getCode()));
        }else {
            res.setOperatorResponse(String.valueOf(response.getCode()));
            res.setCode(ResponseTypeConstants.INSUFFICIENT_BALANCE);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.INSUFFICIENT_BALANCE)));
        }
        return res;
    }

    public DcbApiResponse processTelenorResponse(Response response){
        DcbApiResponse res = new DcbApiResponse();
        return res;
    }
}
