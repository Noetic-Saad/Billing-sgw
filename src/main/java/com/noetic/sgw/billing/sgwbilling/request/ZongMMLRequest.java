package com.noetic.sgw.billing.sgwbilling.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

public class ZongMMLRequest {

    private static final Logger log = LoggerFactory.getLogger(ZongMMLRequest.class);
    public TCPClient client;

    public void serverConnection() {
        String ServerIP = "172.20.51.81";
        int ServerPort = 8799;
        client = new TCPClient();
        client.Connect(ServerIP, ServerPort);
    }

    public String connect(String message, String flag) throws SocketException {
        String output = "";
        try {
            //  	log.debug("IN CONNECT...");
            OutputStream stream;
            //String message = "`SC`005A1.00JS123456PPSPPS  00000000DLGLGN    00000001TXBEG     LOGIN:USER=Noetic,PSWD=Noetic@123;AEBA9EF6";
            byte[] data = message.getBytes("US-ASCII");
            stream = client.GetStream();
            stream.write(data, 0, data.length);
            output = "Sent: " + message;

            data = new byte[10240];
            String responseData = null;

            InputStream stream_in = client.Read();
            int bytes = stream_in.read(data, 0, data.length);
            responseData = new String(data, "US-ASCII");
            output = "Received:  " + responseData;

        } catch (Throwable e) {
            output = "ArgumentNullException" + e;
        }

        return output;
    }

    public String logIn() {
        String userid = "Noetic";
        String password = "Noetic@123";
        String loginbody = "`SC`005A1.00JS123456PPSPPS  00000000DLGLGN    00000001TXBEG     LOGIN:USER="+userid+",PSWD="+password+";";
        String login = loginbody;

        serverConnection();
        String CKsumLogin = chksum(login);

        String logincommand = null;
        try {
            log.info("CHARGING | ZONGMMLREQUEST CLASS | LOGIN REQUEST | "+login + CKsumLogin);
            logincommand = connect(login + CKsumLogin, "N");
        } catch (SocketException e) {
            e.printStackTrace();
        }
        log.info("CHARGING | ZONGMMLREQUEST CLASS | LOGIN RESPONSE | "+logincommand);
        return logincommand;
    }

    public String chksum(String cmd) {
        try {

            byte[] data = cmd.getBytes("US-ASCII");
            byte[] checksum = new byte[8];
            for (int i = 16; i <= data.length - 5; i += 4) {
                checksum[0] = (byte) (checksum[0] ^ data[i]);
                checksum[1] = (byte) (checksum[1] ^ data[i + 1]);
                checksum[2] = (byte) (checksum[2] ^ data[i + 2]);
                checksum[3] = (byte) (checksum[3] ^ data[i + 3]);
            }
            // log.debug("CHECKSUM BYTE CRATED");
            int check = 0;
            for (int i = 0; i <= 3; i++) {
                int r = (int) checksum[i];
                int c = (-(r + (1))) & (0xff);
                c <<= (24 - (i * 8));
                check = (check | c);
            }

            return Integer.toHexString(check).toUpperCase();
        } catch (Exception ex) {
            return "Excepion received: " + ex;

        }
    }

    public String deductBalance(String number, String amt, String serviceId) {
        String header ="`SC`";
        String requestBody = "00761.00JS123456USSD_Pay00000001DLGCON    00000003TXBEG     DEDUCTBALANCE:DN="+number+",AMT="+amt+",SERVICE="+serviceId+",SUBTYPE=P";
        String headerAndBody = header + requestBody;
        String chksum = chksum(headerAndBody);
        String deductBalCommand = null;

        try {
            log.info("CHARGING | ZONGMMLREQUEST CLASS | SENT | "+headerAndBody+chksum);
            deductBalCommand = connect(headerAndBody+chksum, "N");
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return deductBalCommand;
    }

}
