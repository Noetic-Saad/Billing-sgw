package com.noetic.sgw.billing.sgwbilling.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noetic.sgw.billing.sgwbilling.config.StartConfiguration;
import com.noetic.sgw.billing.sgwbilling.entities.FailedBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.entities.GamesBillingRecordEntity;
import com.noetic.sgw.billing.sgwbilling.entities.SuccessBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.repository.FailedRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.GamesBillingRecordsRepository;
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
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class TelenorCharging {

    Logger logger = LoggerFactory.getLogger(TelenorCharging.class);

    public static  int n = 2;

    @Autowired
    private Environment env;
    @Autowired
    SuccessBilledRecordsRepository successBilledRecordsRepository;
    @Autowired
    FailedRecordsRepository failedRecordsRepository;
    private String accessToken = "";
    private boolean testing = true;
    @Autowired
    private GamesBillingRecordsRepository gamesBillingRecordsRepository;
    @Autowired
    private StartConfiguration startConfiguration;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public TelenorCharging(Environment env) {
        this.env = env;
        accessToken = getNewAccessToken();
        if (accessToken == null) {
            System.exit(1);
        }
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> getNewAccessToken(), 0, 50, TimeUnit.MINUTES);
    }

    private String partnerID = "TP-Noetic";
    private String productID = "Noetic-Weekly-Sub-charge";
    private String res = null;


    public Response chargeRequest(ChargeRequestProperties req) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Response res = new Response();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);
        String subscriberNumber = "";
        if (Long.toString(req.getMsisdn()).startsWith("92")) {
            subscriberNumber = Long.toString(req.getMsisdn()).replaceFirst("92", "0");
        } else {
            subscriberNumber = Long.toString(req.getMsisdn());
        }
        DecimalFormat decimalFormatter1 = new DecimalFormat("0.##");
        DecimalFormat decimalFormatter = new DecimalFormat("#");
        Double adjustmentAmount = Double.valueOf(decimalFormatter.format(req.getChargingAmount()))+Double.valueOf(decimalFormatter1.format(req.getTaxAmount()));
        String chargeAmount = decimalFormatter1.format(adjustmentAmount);
        if (!testing) {
            HttpResponse<JsonNode> response = Unirest.post(env.getProperty("tp.api.url"))
                    .header("authorization", "Bearer " + accessToken)
                    .header("content-type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\n\t\"msisdn\":\"" + subscriberNumber + "\",\n\t\"chargableAmount\":\"" + chargeAmount + "\",\n\t\"PartnerID\":\"" + partnerID + "\",\n\t\"ProductID\":\"" + productID + "\",\n\t\"TransactionID\":\"" + transactionID + "\",\n\t\"correlationID\":\"" + req.getCorrelationId() + "\"\n}")
                    .asJson();
            logger.info("Charging Api Response " + response.getBody().toPrettyString());
            if (response.getStatus() == 200) {
                res.setCorrelationId(req.getCorrelationId());
                res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
                res.setMsg("Subscribed SuccessFully");
            } else if (response.getStatus() == 403) {
                res.setCorrelationId(req.getCorrelationId());
                res.setCode(ResponseTypeConstants.UNAUTHORIZED_REQUEST);
                res.setMsg("UnAuthorized Request");
            } else if (response.getStatus() == 500) {
                res.setCorrelationId(req.getCorrelationId());
                res.setCode(ResponseTypeConstants.INSUFFICIENT_BALANCE);
                res.setMsg("Insufficient Balance");
            }else {
                getNewAccessToken();
                chargeRequest(req);
            }
        } else {
            logger.info("BILLING SERVICE || TELENOR CHARGING || MOCK REQUEST FOR || "+req.getMsisdn());
            res.setCorrelationId(req.getCorrelationId());
            res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
            res.setMsg("Subscribed SuccessFully");
        }
        saveChargingRecords(res,req);
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
            accessToken = map.get("access_token").toString();
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveChargingRecords(Response res, ChargeRequestProperties req) {
        GamesBillingRecordEntity entity = new GamesBillingRecordEntity();
       // entity.setAmount(req.getChargingAmount());
        entity.setCdate(new Timestamp(req.getOriginDateTime().getTime()));
        if (res.getCode() == ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL) {
            entity.setIsCharged(1);
        } else {
            entity.setIsCharged(0);
        }
        entity.setIsPostpaid(0);
        entity.setOparatorId(req.getOperatorId().shortValue());
        entity.setShareAmount(req.getShareAmount());
        entity.setShareAmount(req.getShareAmount());
        entity.setMsisdn(req.getMsisdn());
        entity.setChargingMechanism(req.getOperatorId().shortValue());
        //entity.setTaxAmount(req.getTaxAmount());
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
            logger.info("CHARGING | TELENORCHARGING CLASS | RECORDS INSERTED FOR MSISDN " + req.getMsisdn());
        } catch (InvalidJpaQueryMethodException e) {
            logger.info("CHARGING | TELENORCHARGING CLASS | EXCEPTION CAUGHT WHILE INSERTING RECORDS " + e.getCause());
        }

    }
}