package com.noetic.sgw.billing.sgwbilling.request;


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
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JazzCharging {

    Logger logger = LoggerFactory.getLogger(JazzCharging.class);
    @Autowired
    private Environment env;
    @Autowired
    SuccessBilledRecordsRepository successBilledRecordsRepository;
    @Autowired
    FailedRecordsRepository failedRecordsRepository;
    @Autowired StartConfiguration startConfiguration;
    private boolean isTestingFlagOff = false;
    private String methodName = "UpdateBalanceAndDate";
    private String transactionCurrency = "PKR";
    private String originNodeType = "EXT";
    private String originHostName = "GNcasual";
    private String transactionType = "GNcasual";
    private String transactionCode = "GNcasual";
    private String externalData1 = "GNcasual_VAS";
    private String externalData2 = "GNcasual_VAS";
    private String originTimeStamp = "";
    private int responseCode = -1;
    private String status="Fail";
    private String[] recArray = new String[2];
    HttpResponse<String> response;
    @Autowired
    private GamesBillingRecordsRepository gamesBillingRecordsRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Response jazzChargeRequest(ChargeRequestProperties request) {
        Response res = new Response();
        if(isTestingFlagOff) {
            Date date = new Date(System.currentTimeMillis());
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
            TimeZone PKT = TimeZone.getTimeZone("Asia/Karachi");
            dateFormat.setTimeZone(PKT);
            this.originTimeStamp = dateFormat.format(date);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);
            int chargeAmount = 0;
            String subscriberNumber = "";
            boolean isAlreadyCharged = false;
            if (Long.toString(request.getMsisdn()).startsWith("92")) {
                subscriberNumber = Long.toString(request.getMsisdn()).substring(2);
            } else if (Long.toString(request.getMsisdn()).startsWith("0")) {
                subscriberNumber = Long.toString(request.getMsisdn()).substring(1);
            } else if (Long.toString(request.getMsisdn()).startsWith("920")) {
                subscriberNumber = Long.toString(request.getMsisdn()).substring(3);
            } else {
                subscriberNumber = Long.toString(request.getMsisdn());
            }
            chargeAmount = (int) (request.getChargingAmount() + request.getTaxAmount());
            System.out.println("Amount->" + request.getChargingAmount() + request.getTaxAmount());
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
                    "<value><string>" + subscriberNumber + "</string></value>\n" +
                    "</member>\n" +
                    "<member>\n" +
                    "<name>adjustmentAmountRelative</name>\n" +
                    "<value><string>-" + chargeAmount + "</string></value>\n" +
                    "</member>\n" +
                    "</struct>\n" +
                    "</value>\n" +
                    "</param>\n" +
                    "</params>\n" +
                    "</methodCall>";
            Date date1 = new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(date1.toInstant(), ZoneId.systemDefault());
            Date toDate = Date.from(localDateTime.minusHours(12).atZone(ZoneId.systemDefault()).toInstant());
            GamesBillingRecordEntity successEntity = gamesBillingRecordsRepository.isAlreadyCharged(request.getMsisdn(), date1, toDate);
            if (successEntity != null) {
                isAlreadyCharged = true;
            }
            if (!isAlreadyCharged) {
                try {
                    response = Unirest.post(env.getProperty("jazz.api"))
                            .header("Authorization", env.getProperty("jazz.api.authorization"))
                            .header("Content-Type", "text/xml")
                            .header("User-Agent", "UGw Server/4.3/1.0")
                            .header("Cache-Control", "no-cache")
                            .header("Pragma", "no-cache")
                            .header("Host", "10.13.32.156:10010")
                            .header("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
                            .header("Connection", "keep-alive")
                            .body(inputXML).asString();
                    System.out.println("Raw Response-->" + response);
                    recArray = xmlConversion(response.getBody());
                    System.out.println(response.getStatus());
                } catch (UnirestException e) {
                    logger.info("Response +" + response);
                    logger.error("Error while sending request " + e.getStackTrace());
                }
                String transID = recArray[0]; // TransactionID
                System.out.println("Transaction Id-->" + transID);
                if (recArray[1] != null) {
                    responseCode = Integer.valueOf(recArray[1]);
                    // ResponseCode
                }
                if (response == null) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(Integer.parseInt(ResponseTypeConstants.REMOTE_SERVER_CONNECTION_ERROR));
                    res.setMsg(startConfiguration.getResultStatusDescription(ResponseTypeConstants.REMOTE_SERVER_CONNECTION_ERROR));
                    logger.info(String.format("RESPONSE CODE FORBIDDEN-%s", subscriberNumber));
                } else if (responseCode == 0) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)));
                } else if (responseCode == 102) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND)));
                } else if (responseCode == 124) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.INSUFFICIENT_BALANCE);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.INSUFFICIENT_BALANCE)));
                } else {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.OTHER_ERROR);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.OTHER_ERROR)));
                }
                if (status != null)
                    logger.info("RESPONSE MESSAGE: " + res.getMsg());
            } else {
                res.setCorrelationId(request.getCorrelationId());
                res.setCode(ResponseTypeConstants.ALREADY_CHARGED);
                res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.ALREADY_CHARGED)));
            }
        }else {
            logger.info("BILLING SERVICE || JAZZ CHARGING || MOCK REQUEST FOR || "+request.getMsisdn());
            saveChargingRecords(res, request);
            res.setCorrelationId(request.getCorrelationId());
            res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)));
        }
        saveChargingRecords(res, request);
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
            logger.info("CHARGING | JAZZCHARGING CLASS | RECORDS INSERTED FOR MSISDN "+req.getMsisdn());
        } catch (InvalidJpaQueryMethodException e) {
            logger.info("CHARGING | JAZZCHARGING CLASS | EXCEPTION CAUGHT WHILE INSERTING RECORDS "+e.getCause());
        }
    }

    protected
    String[] xmlConversion(String xml) {
        String[] retArray = new String[2];
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xml));

            Document doc = docBuilder.parse(src);

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList listOfPersons = doc.getElementsByTagName("member");

            System.out.println(listOfPersons.getLength());

            if (listOfPersons.getLength() == 2) {

                Node firstPersonNode11 = listOfPersons.item(0);

                if (firstPersonNode11.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) firstPersonNode11;

                    retArray[0] = eElement.getElementsByTagName("value").item(0).getTextContent();
                }    //end of if clause

                // Return Response Code
                Node firstPersonNode22 = listOfPersons.item(1);
                if (firstPersonNode22.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPersonElement22 = (Element) firstPersonNode22;

                    retArray[1] = firstPersonElement22.getElementsByTagName("value").item(0).getTextContent();
                } //end of if clause

            } else {
                //Return Transaction ID
                Node firstPersonNode = listOfPersons.item(16);
                if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPersonElement = (Element) firstPersonNode;

                    //-------
                    NodeList lastNameList = firstPersonElement.getElementsByTagName("string");
                    Element lastNameElement = (Element) lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    System.out.println("Para 1 Value : " + ((Node) textLNList.item(0)).getNodeValue().trim());
                    retArray[0] = ((Node) textLNList.item(0)).getNodeValue().trim();

                } //End Transaction IF
                //Return Response Code
                Node firstPersonNode1 = listOfPersons.item(17);
                if (firstPersonNode1.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPersonElement1 = (Element) firstPersonNode1;

                    NodeList lastNameList = firstPersonElement1.getElementsByTagName("i4");
                    Element lastNameElement = (Element) lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    retArray[1] = ((Node) textLNList.item(0)).getNodeValue().trim();

                } //End Response Code IF
            }
        } catch (SAXParseException err) {
            logger.info("CHARGING | JAZZCHARGING CLASS | ** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            logger.info("CHARGING | JAZZCHARGING CLASS | SAXEXCEPTION | " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return retArray;
    }
}