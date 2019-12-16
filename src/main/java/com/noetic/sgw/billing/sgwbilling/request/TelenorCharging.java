package com.noetic.sgw.billing.sgwbilling.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noetic.sgw.billing.sgwbilling.entities.FailedBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.entities.SuccessBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.repository.FailedRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.SuccessBilledRecordsRepository;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
    private ObjectMapper objectMapper = new ObjectMapper();

    public TelenorCharging(Environment env) {
        this.env = env;
        accessToken = getNewAccessToken();
        if(accessToken == null) {
            System.exit(1);
        }
    }

    private static final double CHARGABLE_AMOUNT = 3d;
    private static final double CHARGABLE_AMOUNT_WITH_TAX = 3.58d;
    private String partnerID = "TP-Noetic";
    private String productID = "Noetic-Weekly-Sub-charge";
    private String res = null;

    public String chargeRequest(HttpServletRequest req) throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String correlationID = new Random().nextInt((999 - 100) + 1) + now.format(formatter);
        String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);
        String subscriberNumber = "";
        if(req.getHeader("msisdn").startsWith("92")) {
            subscriberNumber = req.getHeader("msisdn").replaceFirst("92", "0");
        }else {
            subscriberNumber = req.getHeader("msisdn");
        }
        HttpResponse<JsonNode> response = Unirest.post(env.getProperty("tp.api.url"))
                .header("authorization", "Bearer "+accessToken)
                .header("content-type", "application/json")
                .header("cache-control", "no-cache")
                .body("{\n\t\"msisdn\":\""+subscriberNumber+"\",\n\t\"chargableAmount\":\""+CHARGABLE_AMOUNT_WITH_TAX+"\",\n\t\"PartnerID\":\""+partnerID+"\",\n\t\"ProductID\":\""+productID+"\",\n\t\"TransactionID\":\""+transactionID+"\",\n\t\"correlationID\":\""+correlationID+"\"\n}")
                .asJson();
        logger.info("Charging Api Response "+response.getBody().toPrettyString());
        Map map = objectMapper.readValue(response.getBody().toString(), Map.class);
        if(response.getStatus()==200){
            saveSuccessRecords(map,req);
            res = map.get("Message").toString();
        }else {
            saveFailedRecords(map,req);
            res = map.get("fault").toString();
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

    private String saveSuccessRecords(Map map,HttpServletRequest req){

        SuccessBilledRecordsEntity entity = new SuccessBilledRecordsEntity();
        entity.setVpAccountId(Integer.parseInt(req.getHeader("vp_account_id")));
        entity.setOperatorId(Integer.parseInt(req.getHeader("operator_id")));
        entity.setChargingMechanism(3);
        entity.setShareAmount(Double.parseDouble(req.getHeader("share_amount")));
        entity.setChargedAmount(CHARGABLE_AMOUNT);
        entity.setMsisdn(req.getHeader("msisdn"));
        entity.setChargeTime(Timestamp.valueOf(LocalDateTime.now()));
        try {
            successBilledRecordsRepository.save(entity);
            logger.info("Records For Success Billing Inserted Successfull");
        }catch (InvalidJpaQueryMethodException e){
            logger.error("Jpa Exception Caught Here: "+e.getCause());

        }
        return "";
    }
    private String saveFailedRecords(Map map,HttpServletRequest req){

        FailedBilledRecordsEntity entity = new FailedBilledRecordsEntity();
        entity.setVpAccountId(Integer.parseInt(req.getHeader("vp_account_id")));
        entity.setOperatorId(req.getHeader("operator_id"));
        entity.setChargingMechanism(3);
        entity.setShareAmount(Double.parseDouble(req.getHeader("share_amount")));
        entity.setChargeAmount(CHARGABLE_AMOUNT);
        entity.setMsisdn(req.getHeader("msisdn"));
        entity.setDateTime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setReason(map.get("fault").toString());
        try {
            failedRecordsRepository.save(entity);
            logger.info("Records for failed Billing Inserted Successfull");
        }catch (InvalidJpaQueryMethodException e){
            logger.error("Jpa Exception Caught Here: "+e.getCause());

        }
        return "";
    }
}
