package com.lunatk.alisa.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.lunatk.alisa.util.Utils;

/**
 * Created by LunaTK on 2018. 1. 18..
 */

public class NetworkThread extends HandlerThread {

    private static NetworkThread instance;

    private static final String TAG = "NetworkThread";
    private Socket socket;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private Handler mHandler, activityHandler;


//    private static final String IP = "192.168.0.24";
    private static final String IP = "115.145.177.64";
    private static final int PORT = 9949;

    private NetworkThread(){
        super("NetworkThread");
    }

    public static NetworkThread getInstance(){
        if(instance == null) instance = new NetworkThread();
        return instance;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mHandler = new Handler(getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                activityHandler = (Handler)msg.obj;
                switch(msg.what){
                    case OPCode.REQ_LOGIN:
                        handleLoginRequest(msg.getData());
                        break;
                    case OPCode.REQ_REGISTER:
                        handleRegisterRequest(msg.getData());
                        break;
                    case OPCode.SENSOR_DATA:
                        handleSendSensorData(msg.getData());
                        break;
                }
            }
        };

        Log.d(TAG,"mainHandler : " + mHandler);
        try {
            socket = new Socket(IP,PORT);
            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    private void handleLoginRequest(Bundle bundle){
        String id,pw;
        byte result;
        byte[] buffer;
        id = bundle.getString("id");
        pw = bundle.getString("pw");
        buffer = new byte[1024];
        buffer[0] = OPCode.REQ_LOGIN;
        System.arraycopy(id.getBytes(),0, buffer, 1, id.getBytes().length);
        byte[] enc = Utils.getSHA512(pw);
        System.arraycopy(enc, 0, buffer, 256, enc.length);
        Log.d(TAG,"enc len : " + enc.length);
        try {
            bos.write(buffer);
            bos.flush();
            Log.d(TAG,"Login Request Sent");
            result = (byte)bis.read();
            Log.d(TAG,"Login Request Result : " + result);
            Message msg = Message.obtain();
            msg.what=result;
            activityHandler.sendMessage(msg);
        } catch (IOException e) {
            Log.d(TAG," Write failed : " +e.getMessage() );
        }
    }

    private void handleRegisterRequest(Bundle bundle){
        String id,pw;
        byte result;
        byte[] buffer;
        id = bundle.getString("id");
        pw = bundle.getString("pw");
        buffer = new byte[1024];
        buffer[0] = OPCode.REQ_REGISTER;
        System.arraycopy(id.getBytes(),0, buffer, 1, id.getBytes().length);
        byte[] enc = Utils.getSHA512(pw);
        System.arraycopy(enc, 0, buffer, 256, enc.length);
        Log.d(TAG,"enc len : " + enc.length);
        try {
            bos.write(buffer);
            bos.flush();
            Log.d(TAG,"Register Request Sent");
            result = (byte)bis.read();
            Log.d(TAG,"Register Request result : " + result);
            Message msg = Message.obtain();
            msg.what=result;
            activityHandler.sendMessage(msg);
        } catch (IOException e) {
            Log.d(TAG," Write failed : " +e.getMessage() );
        }
    }

    private void handleSendSensorData(Bundle bundle){
        String data;
        byte result;
        byte[] buffer;
        data = bundle.getString("data");
        buffer = new byte[1024];
        buffer[0] = OPCode.SENSOR_DATA;
        System.arraycopy(data.getBytes(),0, buffer, 1, data.getBytes().length);
        try {
            bos.write(buffer);
            bos.flush();
            Log.d(TAG,"Data Sent");
        } catch (IOException e) {
            Log.d(TAG," Write failed : " +e.getMessage() );
        }
    }

    public static void requestRegister(Handler handler, String id, String pw){
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("pw", pw);
        msg.what = OPCode.REQ_REGISTER;
        msg.setData(bundle);
        msg.obj = handler;
        instance.getHandler().sendMessage(msg);
    }

    public static void requestLogin(Handler handler, String id, String pw){
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        msg.what = OPCode.REQ_LOGIN;
        bundle.putString("id", id);
        bundle.putString("pw", pw);
        msg.setData(bundle);
        msg.obj = handler;
        instance.getHandler().sendMessage(msg);
    }

    public static void sendSensorData(Handler handler, String data){
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        msg.what = OPCode.SENSOR_DATA;
        bundle.putString("data", data);
        msg.setData(bundle);
        msg.obj = handler;
        instance.getHandler().sendMessage(msg);

    }
}
