package com.noetic.sgw.billing.sgwbilling.request;

import com.noetic.sgw.billing.sgwbilling.config.StartConfiguration;
import com.noetic.sgw.billing.sgwbilling.entities.GamesBillingRecordEntity;
import com.noetic.sgw.billing.sgwbilling.repository.GamesBillingRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.util.ChargeRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.Response;
import com.noetic.sgw.billing.sgwbilling.util.ResponseTypeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class ZongCharging {

    private static final Logger log = LoggerFactory.getLogger(ZongCharging.class);

    private static String SERVICE_ID_20 = "NOE20";

    private ZongMMLRequest zongMMLRequest = new ZongMMLRequest();
    private Response res = new Response();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private boolean testing = false;
    @Autowired
    private StartConfiguration startConfiguration;
    @Autowired
    private GamesBillingRecordsRepository gamesBillingRecordsRepository;

    public Response sendChargingRequest(ChargeRequestProperties request){
        String charginAmount = "";
        boolean isAlreadyCharged = false;
        Date date1 = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date1.toInstant(), ZoneId.systemDefault());
        Date toDate = Date.from(localDateTime.minusHours(12).atZone(ZoneId.systemDefault()).toInstant());
        /*GamesBillingRecordEntity successEntity = gamesBillingRecordsRepository.isAlreadyCharged(request.getMsisdn(), date1, toDate);
        if (successEntity != null) {
            isAlreadyCharged = true;
        }*/
        if(!testing) {
            if (!isAlreadyCharged) {
                zongMMLRequest.logIn();
                charginAmount = String.valueOf((int) request.getChargingAmount() * 100);
                String response = zongMMLRequest.deductBalance(String.valueOf(request.getMsisdn()), charginAmount, SERVICE_ID_20);

                log.info("CHARGING | ZONGCHARGING CLASS | ZONG RESPONSE | " + response);
                String[] zongRes = response.split("RETN=");
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
            } else {
                res.setCorrelationId(request.getCorrelationId());
                res.setCode(ResponseTypeConstants.ALREADY_CHARGED);
                res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.ALREADY_CHARGED)));
            }
        }else {
            log.info("BILLING SERVICE || ZONG CHARGING || MOCK REQUEST FOR || "+request.getMsisdn());
            res.setCorrelationId(request.getCorrelationId());
            res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)));
        }
        saveChargingRecords(res,request);
        return res;
    }

    private void saveChargingRecords(Response res, ChargeRequestProperties req) {
        GamesBillingRecordEntity entity = new GamesBillingRecordEntity();
        entity.setAmount(req.getChargingAmount());
        entity.setCdate(new Timestamp(req.getOriginDateTime().getTime()));
        if(res.getCode()==ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL){
            entity.setIsCharged(1);
        }else {
            entity.setIsCharged(0);
        }
        entity.setIsPostpaid(0);
        entity.setOparatorId(req.getOperatorId().shortValue());
        entity.setShareAmount(req.getShareAmount());
        entity.setShareAmount(req.getShareAmount());
        entity.setMsisdn(req.getMsisdn());
        entity.setChargingMechanism(req.getOperatorId().shortValue());
        entity.setTaxAmount(req.getTaxAmount());
        entity.setVendorPlanId(req.getVendorPlanId().longValue());
        entity.setSubCycleId((short) req.getSubCycleId());
        if(req.getIsRenewal()==1){
            entity.setNoOfDailyAttempts(req.getDailyAttempts());
            entity.setNoAttemptsMonthly(req.getAttempts());
            entity.setIsRenewal(1);
        }else {
            entity.setNoAttemptsMonthly(1);
            entity.setNoOfDailyAttempts(1);
            entity.setIsRenewal(0);
        }
        String transactionId = Base64.getEncoder().encodeToString((LocalDateTime.now().format(formatter) + UUID.randomUUID().toString()).getBytes());
        entity.setTransactionId(transactionId);
        try {
            gamesBillingRecordsRepository.save(entity);
            log.info("CHARGING | ZONGCHARGING CLASS | RECORDS INSERTED FOR MSISDN "+req.getMsisdn());
        } catch (InvalidJpaQueryMethodException e) {
            log.info("CHARGING | ZONGCHARGING CLASS | EXCEPTION CAUGHT WHILE INSERTING RECORDS "+e.getCause());
        }
    }

}
