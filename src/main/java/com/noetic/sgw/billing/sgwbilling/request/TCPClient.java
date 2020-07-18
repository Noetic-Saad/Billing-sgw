package com.noetic.sgw.billing.sgwbilling.request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

    private Socket clientSocket;

    public void Connect(String ServerIP, int ServerPort) {
        //create client socket, connect to server
        try {

            clientSocket = new Socket(ServerIP,ServerPort);
            clientSocket.setTcpNoDelay(true);
            clientSocket.setKeepAlive(true);

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Input Stream
    protected InputStream Read() {
        InputStream MyInputStream=null;
        try {
            MyInputStream =  clientSocket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return MyInputStream;
    }

    public void closeConnection(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get Stream
    public OutputStream GetStream() {
        OutputStream OutputStream=null;
        try {
            OutputStream =  clientSocket.getOutputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return OutputStream;
    }



}
