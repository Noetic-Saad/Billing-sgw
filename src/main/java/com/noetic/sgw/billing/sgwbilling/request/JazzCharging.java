package com.noetic.sgw.billing.sgwbilling.request;


import com.noetic.sgw.billing.sgwbilling.config.StartConfiguration;
import com.noetic.sgw.billing.sgwbilling.entities.GamesBillingRecordEntity;
import com.noetic.sgw.billing.sgwbilling.entities.TodaysChargedMsisdnsEntity;
import com.noetic.sgw.billing.sgwbilling.repository.FailedRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.GamesBillingRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.SuccessBilledRecordsRepository;
import com.noetic.sgw.billing.sgwbilling.repository.TodaysChargedMsisdnsRepository;
import com.noetic.sgw.billing.sgwbilling.util.ChargeRequestProperties;
import com.noetic.sgw.billing.sgwbilling.util.Response;
import com.noetic.sgw.billing.sgwbilling.util.ResponseTypeConstants;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.query.InvalidJpaQueryMethodException;
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
import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    @Autowired TodaysChargedMsisdnsRepository chargedMsisdnsRepository;
    private boolean notTesting = true;
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
    int  response = -1;
    @Autowired
    private GamesBillingRecordsRepository gamesBillingRecordsRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Response jazzChargeRequest(ChargeRequestProperties request) throws InterruptedException, ExecutionException, JSONException, IOException {
        String transID ="";
        Response res = new Response();
        if(notTesting) {
            if(request.getIsRenewal()==0) {
                if (checkPostPaid(String.valueOf(request.getMsisdn()))) {
                    Response response = new Response();
                    response.setCode(ResponseTypeConstants.IS_POSTPAID);
                    response.setMsg(ResponseTypeConstants.IS_POSTPAID_MSG);
                    response.setCorrelationId(request.getCorrelationId());
                    saveChargingRecords(response, request, "postPaid-transId");
                    return response;
                }
            }
            Date date = new Date(System.currentTimeMillis());
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
            TimeZone PKT = TimeZone.getTimeZone("Asia/Karachi");
            dateFormat.setTimeZone(PKT);
            this.originTimeStamp = dateFormat.format(date);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);
            String chargeAmount = "";
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
            DecimalFormat decimalFormatter1 = new DecimalFormat("0.##");
            DecimalFormat decimalFormatter = new DecimalFormat("#");
            Double adjustmentAmount = Double.valueOf(decimalFormatter.format(request.getChargingAmount()))+Double.valueOf(decimalFormatter1.format(request.getTaxAmount()));
            chargeAmount = decimalFormatter.format(adjustmentAmount*100);
            logger.info("BILLING SERVICE || JAZZ CHARGING || TOTAL AMOUNT || "+ chargeAmount);
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
                    "<value><string>-"+chargeAmount+"</string></value>\n" +
                    "</member>\n" +
                    "</struct>\n" +
                    "</value>\n" +
                    "</param>\n" +
                    "</params>\n" +
                    "</methodCall>";

            if (!isAlreadyCharged) {
                try {
//                    response = Unirest.post(env.getProperty("jazz.api"))
//                            .header("Authorization", env.getProperty("jazz.api.authorization"))
//                            .header("Content-Type", "text/xml")
//                            .header("User-Agent", "UGw Server/4.3/1.0")
//                            .header("Cache-Control", "no-cache")
//                            .header("Pragma", "no-cache")
//                            .header("Host", "10.13.32.156:10010")
//                            .header("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
//                            .header("Connection", "keep-alive")
//                            .body(inputXML).asString();
//                    recArray = xmlConversion(response.getBody());
//                    System.out.println(response.getStatus());
                } catch (UnirestException e) {
                    logger.info("Response +" + response);
                    logger.error("Error while sending request " + e.getStackTrace());
                }
                transID = recArray[0];
                if (recArray[1] != null) {
                    responseCode = Integer.valueOf(recArray[1]);
                    logger.info("BILLING SERVICE || JAZZ CHARGING || JAZZ RESPONSE FOR || "+request.getMsisdn()+" || "+responseCode);
                }
                responseCode = (int) (Math.random() * 4 + 1);
                System.out.println(responseCode);
                if (response == 1) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(Integer.parseInt(ResponseTypeConstants.REMOTE_SERVER_CONNECTION_ERROR));
                    res.setMsg(startConfiguration.getResultStatusDescription(ResponseTypeConstants.REMOTE_SERVER_CONNECTION_ERROR));
                    logger.info(String.format("RESPONSE CODE FORBIDDEN-%s", subscriberNumber));
                } else if (responseCode == 2) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)));
                } else if (responseCode == 3) {
                    res.setCorrelationId(request.getCorrelationId());
                    res.setCode(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND);
                    res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND)));
                } else if (responseCode == 4) {
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
            responseCode = 0;
            if(!request.isDcb()) {
                saveChargingRecords(res, request, transID);
            }
            res.setCorrelationId(request.getCorrelationId());
            res.setCode(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL);
            res.setMsg(startConfiguration.getResultStatusDescription(Integer.toString(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)));
        }
        if(request.isDcb()){
            res.setCode(responseCode);
        }else {
            saveChargingRecords(res, request,transID);
        }
        return res;
    }

    /**
     * @desc save Charging Records for Games
     * @param res
     * @param req
     * @param transactionId
     */
    private void saveChargingRecords(Response res, ChargeRequestProperties req,String transactionId) {
        GamesBillingRecordEntity entity = new GamesBillingRecordEntity();
        entity.setAmount(req.getChargingAmount());
        entity.setCdate(new Timestamp(req.getOriginDateTime().getTime()));
        if(res.getCode()==ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL){
            entity.setIsCharged(1);
            entity.setNoAttemptsMonthly(0);
            entity.setNoOfDailyAttempts(0);
        }else {
            entity.setIsCharged(0);
        }
        if(res.getCode()==ResponseTypeConstants.IS_POSTPAID) {
            entity.setIsPostpaid(1);
        }else {
            entity.setIsPostpaid(0);
        }
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
        entity.setTransactionId(transactionId);
        try {
            gamesBillingRecordsRepository.save(entity);
            logger.info("BILLING SERVICE || JAZZ CHARGING || RECORDS INSERTED FOR MSISDN "+req.getMsisdn());
        } catch (InvalidJpaQueryMethodException e) {
            logger.info("BILLING SERVICE || JAZZ CHARGING || EXCEPTION CAUGHT WHILE INSERTING RECORDS "+e.getCause());
        }
        if(req.getIsRenewal()==1){
            logger.info("BILLING SERVICE || JAZZ CHARGING || UPDATING TODAYS CHARGED TABLE");
            updateTodaysChargedTable(res,req);
        }
    }

    /**
     * @desc check if number is post-Paid
     * @param msisdn
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws JSONException
     * @throws IOException
     */
    public boolean checkPostPaid(String msisdn) throws InterruptedException, ExecutionException, JSONException, IOException {
        LocalDateTime now = LocalDateTime.now();
        String transactionID = new Random().nextInt(9999 - 1000) + now.format(formatter);
        PostPaidOrPrePaidCheckService postPaidOrPrePaidCheckService = new PostPaidOrPrePaidCheckService(msisdn,transactionID);
        return postPaidOrPrePaidCheckService.isPostPaid();
    }

    /**
     * @desc update todays Charged Table
     * @param res
     * @param req
     */
    private void updateTodaysChargedTable(Response res, ChargeRequestProperties req) {
        TodaysChargedMsisdnsEntity chargedMsisdnsEntity = chargedMsisdnsRepository.findTopByMsisdn(req.getMsisdn());
        TodaysChargedMsisdnsEntity todaysChargedMsisdnsEntity = new TodaysChargedMsisdnsEntity();
        if (chargedMsisdnsEntity == null) {
            todaysChargedMsisdnsEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
            if (res.getCode()==ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL) {
                logger.info("BILLING SERVICE || JAZZ CHARGING || " + req.getMsisdn() + " | RENEWED SUCCESSFULLY");
                todaysChargedMsisdnsEntity.setIsCharged(1);
            } else {
                logger.info("BILLING SERVICE || JAZZ CHARGING || " + req.getMsisdn() + " | NOT RENEWED SUCCESSFULLY");
                todaysChargedMsisdnsEntity.setIsCharged(0);
            }
            todaysChargedMsisdnsEntity.setNumberOfTries(1);
            todaysChargedMsisdnsEntity.setExpirydatetime(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
            todaysChargedMsisdnsEntity.setSubCycle(req.getSubCycleId());
            todaysChargedMsisdnsEntity.setMsisdn(req.getMsisdn());
            todaysChargedMsisdnsEntity.setVendorPlanId(req.getVendorPlanId().longValue());
            todaysChargedMsisdnsEntity.setOperatorId(req.getOperatorId());
            chargedMsisdnsRepository.save(todaysChargedMsisdnsEntity);
        } else {
            chargedMsisdnsEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
            chargedMsisdnsEntity.setNumberOfTries(chargedMsisdnsEntity.getNumberOfTries() + 1);
            if (res.getCode()==ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL) {
                chargedMsisdnsEntity.setIsCharged(1);
            } else {
                chargedMsisdnsEntity.setIsCharged(0);
            }
            chargedMsisdnsRepository.save(chargedMsisdnsEntity);
        }

    }

    /**
     * @desc parse XML and get response
     * @param xml
     * @return
     */
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
            logger.info("BILLING SERVICE || JAZZ CHARGING || ** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            logger.info("BILLING SERVICE || JAZZ CHARGING || SAXEXCEPTION | " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return retArray;
    }


}