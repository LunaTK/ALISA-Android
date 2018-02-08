package com.lunatk.alisa.network;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lunatk.alisa.util.Config;
import com.lunatk.alisa.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by LunaTK on 2018. 1. 30..
 */

public class Request extends AsyncTask implements Comparable<Request> {

    public static final int PRI_NORMAL = 0, PRI_URGENT = 1;
    public static final int TYPE_IMMEDIATE = 0, TYPE_DELAYED = 1;

    private static Socket staticSocket;
    private static DataInputStream staticDis;
    private static DataOutputStream staticDos;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private static final String TAG = "Request";

    private int priority = PRI_NORMAL; // 0~40
    private int type;
    private byte opcode;
    private Bundle bundle;
    private Handler retHandler;

    public Request(Handler retHandler, byte opcode, Bundle bundle, int type) {
        super();
        this.retHandler = retHandler;
        this.opcode = opcode;
        this.bundle = bundle;
        this.type = type;
    }

    public Request(Handler handler, byte opcode, Bundle bundle){
        this(handler,opcode,bundle,TYPE_IMMEDIATE);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.d(TAG,"Executing : " + this);
        Object ret = null;
        if(type==TYPE_IMMEDIATE){
            try{
                establishConnection();
                ret = handleRequest();
                closeConnection();
            } catch (IOException e) {
                Log.e(TAG,"Server is Offline");
                Log.d(TAG, e.getMessage());
            }
        }
        return ret;
    }
    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        handleResult(result);
    }

    public void staticExecute(){
        socket = staticSocket;
        dis = staticDis;
        dos = staticDos;
        handleResult(handleRequest());
    }

    public static void establishStaticConnection() throws IOException {
        SocketAddress socketAddress = new InetSocketAddress(Config.IP, Config.PORT);
        staticSocket = new Socket();
        staticSocket.setSoTimeout(Config.SOCKET_TIMEOUT);
        staticSocket.connect(socketAddress, Config.SOCKET_TIMEOUT);
        staticDis = new DataInputStream(new BufferedInputStream(staticSocket.getInputStream()));
        staticDos = new DataOutputStream(new BufferedOutputStream(staticSocket.getOutputStream()));
    }

    public static void closeStaticConnection() throws IOException{
        staticDis.close();
        staticDos.close();
        staticSocket.close();
    }


    private void establishConnection() throws IOException{
        SocketAddress socketAddress = new InetSocketAddress(Config.IP, Config.PORT);
        socket = new Socket();
        socket.setSoTimeout(Config.SOCKET_TIMEOUT);
        socket.connect(socketAddress, Config.SOCKET_TIMEOUT);
        dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

    }

    private void closeConnection() throws IOException {
        dis.close();
        dos.close();
        socket.close();
    }

    public int getPriority() {
        return priority;
    }

    public void setWeight(int priority) {
        if(priority<PRI_NORMAL || priority > PRI_URGENT) priority = PRI_NORMAL;
        this.priority = priority;
    }

    @Override
    public int compareTo(@NonNull Request o) {
        return this.priority - o.getPriority();
    }



    public Object handleRequest(){
        Object ret = null;
        switch(opcode){
            case OPCode.REQ_LOGIN:
                ret = handleLoginRequest();
                break;
            case OPCode.REQ_REGISTER:
                ret = handleRegisterRequest();
                break;
            case OPCode.NEW_SENSOR_DATA:
                ret = handleSendSensorData();
                break;
        }
        return ret;
    }

    public void handleResult(Object result){
        if(result==null) result = (byte)OPCode.ERR;

        Message msg = Message.obtain();
        msg.what = opcode;
        msg.arg1 = (byte)result;
        retHandler.sendMessage(msg);
    }

    private byte handleLoginRequest(){
        String id,pw;
        byte result = OPCode.ERR;
        id = bundle.getString("id");
        pw = bundle.getString("pw");
        if(id==null || pw==null) return result;
        byte[] enc = Utils.getSHA512(pw);
        try {
            dos.writeByte(OPCode.REQ_LOGIN);
            dos.writeUTF(id);
            dos.writeUTF(new String(enc));
            dos.flush();
            Log.d(TAG,"Login Request Sent");
            result = dis.readByte();
            Log.d(TAG,"Login Request Result : " + result);
        } catch (IOException e) {
            Log.d(TAG," Write failed : " +e.getMessage() );
        }

        return result;
    }

    private byte handleRegisterRequest(){
        String id,pw;
        byte result = OPCode.ERR;
        id = bundle.getString("id");
        pw = bundle.getString("pw");
        byte[] enc = Utils.getSHA512(pw);
//        Log.d(TAG,"enc len : " + enc.length);
        try {
            dos.writeByte(OPCode.REQ_REGISTER);
            dos.writeUTF(id);
            dos.writeUTF(new String(enc));
            dos.flush();
            Log.d(TAG,"Register Request Sent");
            result = (byte)dis.readByte();
            Log.d(TAG,"Register Request result : " + result);

        } catch (IOException e) {
            Log.d(TAG," Write failed : " +e.getMessage() );
        }

        return result;
    }

    private byte handleSendSensorData(){
        String data;
        byte result = OPCode.ERR;
        data = bundle.getString("data");
        try {
            dos.writeByte(OPCode.NEW_SENSOR_DATA);
            dos.writeUTF(data);
            dos.flush();
            Log.d(TAG,"Data Sent");
            result = dis.readByte();
        } catch (IOException e) {
            Log.d(TAG," Write failed : " +e.getMessage() );
        }
        return result;
    }

    @Override
    public String toString() {
        return ("OPCode : " + opcode + " | type : " + type);
    }
}
