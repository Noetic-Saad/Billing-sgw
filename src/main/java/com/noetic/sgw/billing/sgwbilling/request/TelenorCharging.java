package com.noetic.sgw.billing.sgwbilling.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noetic.sgw.billing.sgwbilling.entities.FailedBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.entities.SuccessBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.repository.FailedRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.SuccessBilledRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.util.ChargeRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.Response;
import com.noetic.sgw.billing.sgwbilling.util.ResponseTypeConstants;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Service
public class TelenorCharging {

    Logger logger = LoggerFactory.getLogger(TelenorCharging.class);

    @Autowired
    private Environment env;
    @Autowired
    SuccessBilledRecordsRepository successBilledRecordsRepository;
    @Autowired
    FailedRecordsRepository failedRecordsRepository;
    private String accessToken = "";

    public TelenorCharging(Environment env) {
        this.env = env;
        accessToken = getNewAccessToken();
        if(accessToken == null) {
            System.exit(1);
        }
    }
    private String partnerID = "TP-Noetic";
    private String productID = "Noetic-Weekly-Sub-charge";
    private String res = null;

    public Response chargeRequest(ChargeRequestProperties req) {
        LocalDateTime now = LocalDateTime.now();
        Response res = new Response();
        int chargeAmount = 0;
        boolean isAlreadyCharged = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);
        String subscriberNumber = "";
        if(Long.toString(req.getMsisdn()).startsWith("92")) {
            subscriberNumber = Long.toString(req.getMsisdn()).replaceFirst("92", "0");
        }else {
            subscriberNumber = Long.toString(req.getMsisdn());
        }
        chargeAmount = (int)(req.getChargingAmount()+req.getTaxAmount());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(req.getOriginDateTime().toInstant(), ZoneId.systemDefault());
        Date toDate = Date.from(localDateTime.minusHours(12).atZone(ZoneId.systemDefault()).toInstant());
        SuccessBilledRecordsEntity scuccessRecords = successBilledRecordsRepository.isAlreadyCharged(req.getMsisdn(),req.getOriginDateTime(),toDate);
        if(scuccessRecords != null){
            isAlreadyCharged =true;
        }
        if(!isAlreadyCharged) {
            HttpResponse<JsonNode> response = Unirest.post(env.getProperty("tp.api.url"))
                    .header("authorization", "Bearer " + accessToken)
                    .header("content-type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\n\t\"msisdn\":\"" + subscriberNumber + "\",\n\t\"chargableAmount\":\"" + chargeAmount + "\",\n\t\"PartnerID\":\"" + partnerID + "\",\n\t\"ProductID\":\"" + productID + "\",\n\t\"TransactionID\":\"" + transactionID + "\",\n\t\"correlationID\":\"" + req.getCorrelationId() + "\"\n}")
                    .asJson();
            logger.info("Charging Api Response " + response.getBody().toPrettyString());
            if (response.getStatus() == 200) {
                saveSuccessRecords(res, req);
                res.setCorrelationId(req.getCorrelationId());
                res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
                res.setMsg("Subscribed SuccessFully");
            } else if (response.getStatus() == 403) {
                res.setCorrelationId(req.getCorrelationId());
                res.setCode(ResponseTypeConstants.UNAUTHORIZED_REQUEST);
                res.setMsg("UnAuthorized Request");
                saveFailedRecords(res, req);
            } else if (response.getStatus() == 500) {
                res.setCorrelationId(req.getCorrelationId());
                res.setCode(ResponseTypeConstants.INSUFFICIENT_BALANCE);
                res.setMsg("Insufficient Balance");
                saveFailedRecords(res, req);
            }
        }else {
            res.setCorrelationId(req.getCorrelationId());
            res.setCode(ResponseTypeConstants.ALREADY_CHARGED);
            res.setMsg("Already Charged");
        }
        return res;
    }

    public String getNewAccessToken() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String base64Key = Base64.getEncoder().encodeToString((env.getProperty("tp.api.username") + ":" + env.getProperty("tp.api.password")).getBytes());
            HttpResponse<JsonNode> token = Unirest.post(env.getProperty("tp.api.tokenurl"))
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip")
                    .header("Accept-Language", "en-US")
                    .header("Authorization", "Basic " + base64Key)
                    .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                    .header("Host", "apis.telenor.com.pk")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML")
                    .header("X-Forwarded-For", "10.7.118.131")
                    .header("X-Forwarded-For", "59001")
                    .header("X-Forwarded-For", "http")
                    .asJson();
            System.out.println(token.getBody().toString());
            Map map = objectMapper.readValue(token.getBody().toPrettyString(), Map.class);
            System.out.println(map.get("access_token").toString());
            return map.get("access_token").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String saveSuccessRecords(Response res,ChargeRequestProperties req){

        SuccessBilledRecordsEntity entity = new SuccessBilledRecordsEntity();
        entity.setVpAccountId(req.getVendorPlanId());
        entity.setOperatorId(req.getOperatorId());
        entity.setChargingMechanism(3);
        entity.setShareAmount(req.getShareAmount());
        entity.setChargedAmount(req.getChargingAmount());
        entity.setMsisdn(req.getMsisdn());
        entity.setChargeTime(new Timestamp(req.getOriginDateTime().getTime()));
        try {
            successBilledRecordsRepository.save(entity);
            logger.info("Records For Success Billing Inserted Successfull");
        }catch (InvalidJpaQueryMethodException e){
            logger.error("Jpa Exception Caught Here: "+e.getCause());

        }
        return "";
    }
    private String saveFailedRecords(Response res,ChargeRequestProperties req){

        FailedBilledRecordsEntity entity = new FailedBilledRecordsEntity();
        entity.setVpAccountId(req.getVendorPlanId());
        entity.setOperatorId(req.getOperatorId());
        entity.setChargingMechanism(3);
        entity.setShareAmount(req.getShareAmount());
        entity.setChargeAmount(req.getChargingAmount());
        entity.setMsisdn(req.getMsisdn());
        entity.setDateTime(new Timestamp(req.getOriginDateTime().getTime()));
        entity.setReason(res.getMsg());
        entity.setStatusCode(res.getCode());
        try {
            failedRecordsRepository.save(entity);
            logger.info("Records for failed Billing Inserted Successfull");
        }catch (InvalidJpaQueryMethodException e){
            logger.error("Jpa Exception Caught Here: "+e.getCause());

        }
        return "";
    }
}