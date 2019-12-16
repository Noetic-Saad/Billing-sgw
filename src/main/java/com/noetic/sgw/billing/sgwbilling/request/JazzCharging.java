package com.noetic.sgw.billing.sgwbilling.request;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noetic.sgw.billing.sgwbilling.entities.FailedBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.entities.SuccessBilledRecordsEntity;
import com.noetic.sgw.billing.sgwbilling.repository.FailedRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.SuccessBilledRecordsRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

@Service
public class JazzCharging {

    Logger logger = LoggerFactory.getLogger(JazzCharging.class);
    @Autowired
    private Environment env;
    @Autowired
    SuccessBilledRecordsRepository successBilledRecordsRepository;
    @Autowired
    FailedRecordsRepository failedRecordsRepository;
    private static final double CHARGABLE_AMOUNT = 5;
    private static final double CHARGABLE_AMOUNT_WITH_TAX = 598;
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
    private String msg = "";
    private String[] recArray = new String[2];
    HttpResponse<String> response;

    public String jazzChargeRequest(HttpServletRequest request) throws JsonProcessingException {

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        TimeZone PKT = TimeZone.getTimeZone("Asia/Karachi");
        dateFormat.setTimeZone(PKT);
        this.originTimeStamp = dateFormat.format(date);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);

        String subscriberNumber = "";
        if (request.getHeader("msisdn").startsWith("92")) {
            subscriberNumber = request.getHeader("msisdn").toString().substring(2);
        } else if (request.getHeader("msisdn").startsWith("0")) {
            subscriberNumber = request.getHeader("msisdn").substring(1);
        } else if (request.getHeader("msisdn").startsWith("920")) {
            subscriberNumber = request.getHeader("msisdn").substring(3);
        } else {
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
            recArray = xmlConversion(response.toString());

            String transID = recArray[0]; // TransactionID
            System.out.println("Transaction Id-->"+transID);
            if (recArray[1] != null) {
                responseCode = Integer.valueOf(recArray[1]);
                // ResponseCode
            }
            if(responseCode == HttpStatus.FORBIDDEN.value()) {
                logger.info(String.format("RESPONSE CODE FORBIDDEN-%s", subscriberNumber));
                return null;
            }

            if(responseCode==0){
                status = "Success";
                msg = "Succesfull";
            }else if(responseCode==102){
                msg = "Subscriber not found";
            }else if(responseCode==124){
                msg =  "Below minimum balance";
            }else{
                msg = "Other Error";
            }
            if(status != null)
                logger.info("RESPONSE MESSAGE: " + status);
        } catch (UnirestException e) {
            logger.info("Response +"+ response);
            logger.error("Error while sending request " + e.getStackTrace());
        }
        if (responseCode == 200) {
            saveSuccessRecords(msg, request);
            return "success";
        } else {
            logger.info("Tyring to insert In Failed Record Table");
            saveFailedRecords(msg, request);
            return "fail";
        }


    }

    private String saveSuccessRecords(String msg, HttpServletRequest req) {

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
        } catch (InvalidJpaQueryMethodException e) {
            logger.error("Jpa Exception Caught Here: " + e.getCause());

        }
        return "";
    }

    private String saveFailedRecords(String msg, HttpServletRequest req) {

        FailedBilledRecordsEntity entity = new FailedBilledRecordsEntity();
        entity.setVpAccountId(Integer.parseInt(req.getHeader("vp_account_id")));
        entity.setOperatorId(req.getHeader("operator_id"));
        entity.setChargingMechanism(3);
        entity.setShareAmount(Double.parseDouble(req.getHeader("share_amount")));
        entity.setChargeAmount(CHARGABLE_AMOUNT);
        entity.setMsisdn(req.getHeader("msisdn"));
        entity.setDateTime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setReason(msg);
        try {
            failedRecordsRepository.save(entity);
            logger.info("Records for failed Billing Inserted Successfull");
        } catch (InvalidJpaQueryMethodException e) {
            logger.error("Jpa Exception Caught Here: " + e.getCause());

        }
        return "";
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
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return retArray;

    }
}
