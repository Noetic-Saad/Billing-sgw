package com.noetic.sgw.billing.sgwbilling.request;

import com.noetic.sgw.billing.sgwbilling.config.StartConfiguration;
import com.noetic.sgw.billing.sgwbilling.entities.MoBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.repository.GamesBillingRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.MoBilledRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.WeeklyChargedMsisdnsRepository;
import com.noetic.sgw.billing.sgwbilling.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ZongMoCharging {

    private static final Logger log = LoggerFactory.getLogger(ZongCharging.class);

    private String serviceId = "";
    private String[] zongRes;

    private ZongMMLRequest zongMMLRequest = new ZongMMLRequest();
    private MoResponse res = new MoResponse();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private boolean testing = false;
    @Autowired
    private StartConfiguration startConfiguration;
    @Autowired
    private MoBilledRecordsRepository moBilledRecordsRepository;

    public MoResponse sendChargingRequest(MoRequestProperties request) throws Exception {
        String charginAmount = "";

        Integer shortcode = Integer.valueOf(request.getShortcode());
        if (shortcode == 3441) {
            serviceId = "Noet01";
        } else if (shortcode == 3443) {
            serviceId = "Noet05";
        } else if (shortcode == 3444) {
            serviceId = "Noet10";
        } else if (shortcode == 3445) {
            serviceId = "Noet25";
        }
        if(!testing) {
                zongMMLRequest.logIn();
                charginAmount = String.valueOf((int) request.getChargingAmount() * 100);
                String response = zongMMLRequest.deductBalance(String.valueOf(request.getMsisdn()), charginAmount, serviceId);

                log.info("CHARGING | ZONGCHARGING CLASS | ZONG RESPONSE | " + response);
                zongRes = response.split("RETN=");
                String[] codeArr = zongRes[1].split(",");
                String code = codeArr[0];
                log.info("CHARGING | ZONGCHARGING CLASS | ZONG MML RESPONSE CODE | " + code);

                if (code.equalsIgnoreCase("0000")) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)));
                } else {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.INSUFFICIENT_BALANCE);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.INSUFFICIENT_BALANCE)));
                }
        }else {
            Thread.sleep(100l);
            log.info("BILLING SERVICE || ZONG CHARGING || MOCK REQUEST FOR || "+request.getMsisdn());
            res.setCorrelationId(request.getCorrelationId());
            res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)));
        }
        saveChargingRecords(res,request,zongRes[1]);
        return res;
    }

    private void saveChargingRecords(MoResponse moResponse, MoRequestProperties requestProperties, String transactionId){

        MoBilledRecordsEntity moBilledRecordsEntity = new MoBilledRecordsEntity();
        moBilledRecordsEntity.setMsisdn(requestProperties.getMsisdn());
        moBilledRecordsEntity.setOperatorId((int) requestProperties.getOperatorId());
        moBilledRecordsEntity.setChargingMechanism(requestProperties.getChargingMechanism());
        moBilledRecordsEntity.setOriginalSmsId(333l);
        moBilledRecordsEntity.setPartnerPlanId((int) requestProperties.getPartnerPlanId());
        moBilledRecordsEntity.setChargedAmount(requestProperties.getChargingAmount());
        moBilledRecordsEntity.setTaxAmount(requestProperties.getTaxAmount());
        moBilledRecordsEntity.setChargeTime(Timestamp.valueOf(LocalDateTime.now()));
        moBilledRecordsEntity.setTransactionId(transactionId);
        if(moResponse.getCode() == ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL){
            moBilledRecordsEntity.setIsCharged(1);
            moBilledRecordsEntity.setChargingResponse("CHARGED SUCCESSFULLY");
        }else if(moResponse.getCode() == ResponseTypeConstants.INSUFFICIENT_BALANCE){
            moBilledRecordsEntity.setIsCharged(0);
            moBilledRecordsEntity.setChargingResponse("INSUFFICIENT BALANCE");
        }else if(moResponse.getCode() == ResponseTypeConstants.SUBSCRIBER_NOT_FOUND){
            moBilledRecordsEntity.setIsCharged(0);
            moBilledRecordsEntity.setChargingResponse("SUBSCRIBER NOT FOUNT");
        }else {
            moBilledRecordsEntity.setIsCharged(0);
            moBilledRecordsEntity.setChargingResponse("OTHER ERROR");
        }
        moBilledRecordsRepository.save(moBilledRecordsEntity);

    }
}
