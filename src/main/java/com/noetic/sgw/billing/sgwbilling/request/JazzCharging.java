package com.noetic.sgw.billing.sgwbilling.request;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import java.security.acl.LastOwnerException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class JazzCharging {

    @Autowired
    private Environment env;
    private static final double CHARGABLE_AMOUNT = 5;
    private static final double CHARGABLE_AMOUNT_WITH_TAX = 598;
    private String methodName ="UpdateBalanceAndDate";
    private String transactionCurrency="PKR";
    private String originNodeType="EXT";
    private String originHostName="GNcasual";
    private	String transactionType="GNcasual";
    private String transactionCode="GNcasual";
    private String externalData1="GNcasual_VAS";
    private String externalData2="GNcasual_VAS";
    private String originTimeStamp = "";
    private ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(JazzCharging.class);

    public void jazzChargeRequest(HttpServletRequest request) throws JsonProcessingException {

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        TimeZone PKT = TimeZone.getTimeZone("Asia/Karachi");
        dateFormat.setTimeZone(PKT);
        this.originTimeStamp = dateFormat.format(date);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);

        String subscriberNumber = "";
        if(request.getHeader("msisdn").startsWith("92")) {
            subscriberNumber = request.getHeader("msisdn").toString().substring(2);
        }else if(request.getHeader("msisdn").startsWith("0")){
            subscriberNumber = request.getHeader("msisdn").substring(1);
        }else if(request.getHeader("msisdn").startsWith("920")){
            subscriberNumber = request.getHeader("msisdn").substring(3);
        }else {
            subscriberNumber = request.getHeader("msisdn");
        }

        String inputXML = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
                "<methodCall>\n" +
                "<methodName>" + this.methodName + "</methodName>\n" +
                "<params>\n" +
                "<param>\n" +
                "<value>\n" +
                "<struct>\n" +
                "<member>\n" +
                "<name>originNodeType</name>\n" +
                "<value><string>" + this.originNodeType + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>originHostName</name>\n" +
                "<value><string>" + this.originHostName + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>originTransactionID</name>\n" +
                "<value><string>" + transactionID + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>transactionType</name>\n" +
                "<value><string>" + this.transactionType + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>transactionCode</name>\n" +
                "<value><string>" + this.transactionCode + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>externalData1</name>\n" +
                "<value><string>" + this.externalData1 + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>externalData2</name>\n" +
                "<value><string>" + this.externalData2 + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>originTimeStamp</name>\n" +
                "<value><dateTime.iso8601>" + this.originTimeStamp + "+0500</dateTime.iso8601></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>transactionCurrency</name>\n" +
                "<value><string>" + this.transactionCurrency + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>subscriberNumber</name>\n" +
                //"<value><string>3015166666</string></value>\n" +
                "<value><string>" + subscriberNumber + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>adjustmentAmountRelative</name>\n" +
                "<value><string>-" + CHARGABLE_AMOUNT_WITH_TAX + "</string></value>\n" +
                "</member>\n" +
                "</struct>\n" +
                "</value>\n" +
                "</param>\n" +
                "</params>\n" +
                "</methodCall>";
        try {
            HttpResponse<String> response = Unirest.post(env.getProperty("jazz.api.url"))
                    .header("Authorization", env.getProperty("jazz.api.authorization"))
                    .header("Content-Type", "text/xml")
                    .header("User-Agent", "UGw Server/4.3/1.0")
                    .header("Cache-Control", "no-cache")
                    .header("Pragma", "no-cache")
                    .header("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
                    .header("Connection", "keep-alive")
                    .body(inputXML).asString();
            Map map = objectMapper.readValue(response.getBody().toString(), Map.class);
        }catch (UnirestException e){
            logger.error("Error while sending request "+e.getCause());
        }
    }
}
