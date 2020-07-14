package com.noetic.sgw.billing.sgwbilling.request;


import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PostPaidOrPrePaidCheckService {

    Logger log = LoggerFactory.getLogger(PostPaidOrPrePaidCheckService.class.getName());

    private String methodName = "GetBalanceAndDate";

    private String originNodeType = "EXT";
    private String originHostName = "Gameinapp";

    private String subscriberNumber;
    private String originTransactionID;
    private String originTimeStamp;
    private String[] recArray = new String[2];
    private static HttpClientConnection conn;
    private static PoolingHttpClientConnectionManager connectionManager;
    private static ConnectionKeepAliveStrategy keepAliveStrategy;
    CloseableHttpClient httpclient;

    public PostPaidOrPrePaidCheckService(String subscriberNumber, String originTransactionID){
        this.subscriberNumber = subscriberNumber;
        this.originTransactionID = originTransactionID;
    }
    public PostPaidOrPrePaidCheckService(PoolingHttpClientConnectionManager connectionManager, HttpClientConnection conn,ConnectionKeepAliveStrategy keepAliveStrategy){
        log.info("APPLICATION STARTED");
        this.connectionManager = connectionManager;
        this.conn = conn;
        this.keepAliveStrategy = keepAliveStrategy;
    }


    public boolean isPostPaid() throws IOException, JSONException, ExecutionException, InterruptedException {
        boolean isPostPaid = false;
        log.info("Check Post Paid Or PrePaid For Msisdn || " + this.subscriberNumber);
        HttpResponse httpResponse = null;
        httpclient = HttpClients.createDefault();
        HttpPost httpRequest = getHttpRequest();
        try {
            log.info("Request is here");
            httpResponse = httpclient.execute(httpRequest);
            return parseResponse(httpResponse);
        }catch (IllegalStateException e){
            log.error("EXCEPTION "+e.getMessage());
            isPostPaid();
        }
        return isPostPaid;
    }

    public boolean parseResponse(HttpResponse httpResponse) throws IOException, JSONException {

        boolean isPostPaid =false;
        HttpEntity entity = null;
        Map<String, List<Integer>> map = null;
        try {
            entity = httpResponse.getEntity();
            String xmlResponse = EntityUtils.toString(entity);
            System.out.println(httpResponse.getStatusLine().getStatusCode());
            map = xmlConversion(xmlResponse);
            EntityUtils.consume(entity);
        }catch (Exception e){
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

    public HttpPost getHttpRequest() throws UnsupportedEncodingException {

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

        httpRequest.addHeader("Content-Type", "text/xml");
        httpRequest.addHeader("User-Agent", "UGw Server/4.3/1.0");
        httpRequest.addHeader("Authorization", Authorization);
        httpRequest.addHeader("Cache-Control", "no-cache");
        httpRequest.addHeader("Pragma", "no-cache");
        httpRequest.addHeader("Host", "10.13.32.156:10010");
        httpRequest.addHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
        httpRequest.addHeader("Connection", "keep-alive");
        httpRequest.addHeader("Keep-Alive", "20");


        httpRequest.setEntity(new StringEntity(inputXML));


        return httpRequest;
    }

    public static Map<String, List<Integer>> xmlConversion(String xml) throws JSONException {


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