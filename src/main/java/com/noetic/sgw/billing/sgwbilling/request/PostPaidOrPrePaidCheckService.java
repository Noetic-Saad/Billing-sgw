package com.noetic.sgw.billing.sgwbilling.request;


import kong.unirest.Unirest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PostPaidOrPrePaidCheckService {

    Logger log = LoggerFactory.getLogger(PostPaidOrPrePaidCheckService.class.getName());

    @Autowired
    private Environment env;

    private String methodName = "UpdateBalanceAndDate";
    private String originNodeType = "EXT";
    private String originHostName = "GNcasual";
    private String originTimeStamp = "";

    private String subscriberNumber;
    private String originTransactionID;

    private int responseCode = -1;
    private String status="Fail";
    private String[] recArray = new String[2];
    kong.unirest.HttpResponse<String> response;

    public PostPaidOrPrePaidCheckService(String subscriberNumber, String originTransactionID) {
        this.subscriberNumber = subscriberNumber;
        this.originTransactionID = originTransactionID;
    }


    public boolean isPostPaid() throws IOException, JSONException, ExecutionException, InterruptedException {
        boolean isPostPaid = false;
        log.info("Check Post Paid Or PrePaid For Msisdn || " + this.subscriberNumber);
        response = sendPostPaidCheckRequest();
        try {
            log.info("Request is here");
            System.out.println("response.getStatus() = " + response.getStatus());
            return parseResponse(response.getBody());
        } catch (IllegalStateException e) {
            log.error("EXCEPTION " + e.getMessage());
            isPostPaid();
        }
        return isPostPaid;
    }

    public boolean parseResponse(String xmlResponse) throws IOException, JSONException {

        boolean isPostPaid = false;
        HttpEntity entity = null;
        Map<String, List<Integer>> map = null;
        try {
            map = xmlConversion(xmlResponse);
        } catch (Exception e) {
            log.error("Exception Caught at line 94");
        }

        List<Integer> serviceList = map.get("serviceClassCurrent");
        List<Integer> offerList = map.get("offerID");
        List<Integer> responseList = map.get("responseCode");
        if (responseList.get(0) == 0) {
            log.info("Service Class Id For Msisdn " + this.subscriberNumber + " is " + serviceList.get(0));
            if (serviceList.contains(70) || offerList.contains(7000) || offerList.contains(62) || offerList.contains(18)) {
                isPostPaid = true;
            } else {
                isPostPaid = false;
            }
        } else {
            log.info("Response Form operator || Operator Not Found || " + responseList.get(0));
        }
        return isPostPaid;
    }

    public kong.unirest.HttpResponse<String> sendPostPaidCheckRequest() throws UnsupportedEncodingException, JSONException {

        Date date = new Date(System.currentTimeMillis());

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        TimeZone PKT = TimeZone.getTimeZone("Asia/Karachi");
        dateFormat.setTimeZone(PKT);
        this.originTimeStamp = dateFormat.format(date);

        HttpPost httpRequest = null;
        httpRequest = new HttpPost("http://10.13.32.156:10010/Air");

        String Authorization = "Basic SU5UTk9FVElDOk1vYmkjOTEx";

        if (this.subscriberNumber.startsWith("92")) {
            this.subscriberNumber = subscriberNumber.substring(2);
        } else if (this.subscriberNumber.startsWith("920")) {
            this.subscriberNumber = subscriberNumber.substring(3);
        } else if (this.subscriberNumber.startsWith("0")) {
            this.subscriberNumber = subscriberNumber.substring(1);
        } else {
            this.subscriberNumber = this.subscriberNumber;
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
                "<value><string>" + this.originTransactionID + "</string></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>originTimeStamp</name>\n" +
                "<value><dateTime.iso8601>" + this.originTimeStamp + "+0500</dateTime.iso8601></value>\n" +
                "</member>\n" +
                "<member>\n" +
                "<name>subscriberNumber</name>\n" +
                "<value><string>" + subscriberNumber + "</string></value>\n" +
                "</member>\n" +
                "</struct>\n" +
                "</value>\n" +
                "</param>\n" +
                "</params>\n" +
                "</methodCall>";


        System.out.println("inputXML = " + inputXML);

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

            }catch (Exception e){
                System.out.println("e.getStackTrace() = " + e.getStackTrace());
            }

            return response;
    }

            public static Map<String, List<Integer>> xmlConversion (String xml) throws JSONException {


                JSONObject jsonObject = XML.toJSONObject(xml);
                JSONArray array = jsonObject.getJSONObject("methodResponse").getJSONObject("params").getJSONObject("param").getJSONObject("value").getJSONObject("struct").getJSONArray("member");
                Map<String, String> memberMap = new HashMap<>();
                Map<String, List<Integer>> offerIdMap = new HashMap<>();
                List<Integer> offerIdList = new ArrayList<>();
                List<Integer> serviceClass = new ArrayList<>();
                List<Integer> responseCode = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    String strVal = null;
                    Integer intVal = null;
                    boolean boolVal = false;
                    if (array.getJSONObject(i).has("value") && array.getJSONObject(i).getJSONObject("value").has("struct")) {
                        JSONArray arr = array.getJSONObject(i).getJSONObject("value").getJSONObject("struct").getJSONArray("member");
                        for (int j = 0; j < arr.length(); j++) {
                            String stringVal = null;
                            Integer intVal1 = null;
                            Integer boolVal1 = null;
                            if (arr.getJSONObject(j).get("name") != null) {
                                String name = arr.getJSONObject(j).getString("name");
                                JSONObject jValue = arr.getJSONObject(j).getJSONObject("value");
                                try {
                                    boolVal1 = (Integer) jValue.get("boolean");
                                    memberMap.put(name, Integer.toString(boolVal1));
                                } catch (Exception e) {

                                }
                                try {
                                    intVal1 = (Integer) jValue.get("i4");
                                    memberMap.put(name, Integer.toString(intVal));
                                } catch (Exception e) {

                                }
                                try {
                                    stringVal = (String) jValue.get("string");
                                    memberMap.put(name, stringVal);
                                } catch (Exception e) {

                                }

                            }
                        }
                    } else if (array.getJSONObject(i).has("value") && array.getJSONObject(i).getJSONObject("value").has("array")) {
                        if (array.getJSONObject(i).getJSONObject("value").getJSONObject("array").getJSONObject("data").has("value")) {
                            Object object = array.getJSONObject(i).getJSONObject("value").getJSONObject("array").getJSONObject("data").get("value");
                            if (object instanceof JSONObject) {
                                Integer nodeValue = null;
                                String stringValue = null;
                                JSONObject valueObject = array.getJSONObject(i).getJSONObject("value").getJSONObject("array").getJSONObject("data").getJSONObject("value");
                                JSONArray memberarr = valueObject.getJSONObject("struct").getJSONArray("member");
                                for (int k = 0; k < memberarr.length(); k++) {
                                    System.out.println("processing This");
                                    if (memberarr.getJSONObject(k) != null) {
                                        String name = memberarr.getJSONObject(k).getString("name");
                                        JSONObject value = memberarr.getJSONObject(k).getJSONObject("value");
                                        if (value.has("i4")) {
                                            nodeValue = value.getInt("i4");
                                            if (name.equalsIgnoreCase("offerId")) {
                                                offerIdList.add(nodeValue);
                                                offerIdMap.put(name, offerIdList);
                                            } else {
                                                memberMap.put(name, Integer.toString(nodeValue));
                                            }
                                        } else if (value.has("string")) {
                                            stringValue = (String) value.get("string").toString();
                                            memberMap.put(name, stringValue);
                                        } else {
                                        }

                                    }
                                }
                            } else {
                                JSONArray arr = array.getJSONObject(i).getJSONObject("value").getJSONObject("array").getJSONObject("data").getJSONArray("value");
                                for (int j = 0; j < arr.length(); j++) {
                                    Integer nodeValue = null;
                                    String stringValue = null;
                                    JSONArray memberarr = arr.getJSONObject(j).getJSONObject("struct").getJSONArray("member");
                                    for (int k = 0; k < memberarr.length(); k++) {
                                        if (memberarr.getJSONObject(k) != null) {
                                            String name = memberarr.getJSONObject(k).getString("name");
                                            JSONObject value = memberarr.getJSONObject(k).getJSONObject("value");
                                            if (value.has("i4")) {
                                                nodeValue = value.getInt("i4");
                                                if (name.equalsIgnoreCase("offerId")) {
                                                    offerIdList.add(nodeValue);
                                                    offerIdMap.put(name, offerIdList);
                                                } else {
                                                    memberMap.put(name, Integer.toString(nodeValue));
                                                }
                                            } else if (value.has("string")) {
                                                stringValue = (String) value.get("string").toString();
                                                memberMap.put(name, stringValue);
                                            } else {
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    } else {

                        String name = array.getJSONObject(i).getString("name");
                        String stringVal = null;
                        String intVal1 = null;
                        Integer boolVal1 = null;
                        JSONObject jValue = array.getJSONObject(i).getJSONObject("value");
                        if (jValue.has("boolean")) {
                            boolVal1 = (Integer) jValue.get("boolean");
                            memberMap.put(name, Integer.toString(boolVal1));
                        } else if (jValue.has("i4")) {
                            intVal1 = jValue.get("i4").toString();
                            if (name.equalsIgnoreCase("serviceClassCurrent")) {
                                serviceClass.add(Integer.parseInt(intVal1));
                                offerIdMap.put(name, serviceClass);
                            } else if (name.equalsIgnoreCase("responseCode")) {
                                responseCode.add(Integer.parseInt(intVal1));
                                offerIdMap.put(name, responseCode);
                            } else {
                                memberMap.put(name, intVal1);
                            }
                        } else if (jValue.has("string")) {
                            stringVal = jValue.get("string").toString();
                            memberMap.put(name, stringVal);
                        } else {

                        }
                    }
                }

                return offerIdMap;
            }

        }


