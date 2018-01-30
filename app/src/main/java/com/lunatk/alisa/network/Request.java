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
import java.net.Socket;

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
            establishConnection();
            ret = handleRequest();
            closeConnection();
        }
        return ret;
    }

    public void staticExecute(){
        socket = staticSocket;
        dis = staticDis;
        dos = staticDos;
        handleResult(handleRequest());
    }

    public static void establishStaticConnection(){
        try {
            staticSocket = new Socket(Config.IP, Config.PORT);
            staticDis = new DataInputStream(new BufferedInputStream(staticSocket.getInputStream()));
            staticDos = new DataOutputStream(new BufferedOutputStream(staticSocket.getOutputStream()));
        }catch (IOException e){
            Log.e(TAG,"Server Connect Failed");
        }
    }

    public static void closeStaticConnection(){
        try {
            staticDis.close();
            staticDos.close();
            staticSocket.close();
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        handleResult(result);
    }

    private void establishConnection(){
        try {
            socket = new Socket(Config.IP, Config.PORT);
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }catch (IOException e){
            Log.e(TAG,"Server Connect Failed");
        }
    }

    private void closeConnection(){
        try {
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
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
            case OPCode.SENSOR_DATA:
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
        byte[] enc = Utils.getSHA512(pw);
        Log.d(TAG,"enc len : " + enc.length);
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
            dos.writeByte(OPCode.SENSOR_DATA);
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
