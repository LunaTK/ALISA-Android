package com.lunatk.alisa.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lunatk.alisa.bluetooth.AlisaService;
import com.lunatk.alisa.util.Config;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by LunaTK on 2018. 1. 30..
 */

public class RequestManager extends Thread {
    private static RequestManager instance;
    private static final String TAG="RequestManager";
    private PriorityQueue<Request> queue;
    private boolean isRunning = false;
    private int sessionId = -1;
    private boolean isNetworkOnline = false;

    public void setNetworkOnline(boolean networkOnline) {
        isNetworkOnline = networkOnline;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public static RequestManager getInstance(){
        if(instance == null) instance = new RequestManager();
        return instance;
    }

    public static void renewThread(){
        if(instance.isRunning()) instance.stopRunning();
        instance = new RequestManager();
    }

    private RequestManager(){
        sessionId = -1;
        queue = new PriorityQueue<>();
    }

    public void addRequest(Request request){
        queue.offer(request);
    }

    public boolean isRunning(){
        return  isRunning;
    }

    @Override
    public void run() {
        super.run();
        isRunning = true;

        while (isRunning) {
            synchronized(this) {
                while (!isNetworkOnline) {
                    Log.d(TAG, "Network offline, wait");
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.getMessage());
                    }
                    Log.d(TAG,"RequestManager wake up");
                }
            }

            try {
                Thread.sleep(Config.FLUSH_REQUEST_PERIOD);
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupted");
            }

            try {
                if (queue.size() != 0) { // 나중에 세션 유효기간 갱신을 위해 이건 삭제
                    Request.establishStaticConnection();
                    while (queue.size() != 0) queue.poll().staticExecute();
                    Request.closeStaticConnection();
                }
            } catch (IOException e) {
                Log.e(TAG, "Server is Offline");
                Log.d(TAG, e.getMessage());
                try {
                    Thread.sleep(Config.NETWORK_UNREACHABLE_SLEEP_PERIOD);
                } catch (InterruptedException e1) {
                    Log.d(TAG, "interrupted : " + e.getMessage());
                }
            }
        }
        Log.d(TAG, "RequestManager dead");
        instance = new RequestManager();

    }

    public void stopRunning(){
        this.isRunning = false;
    }

    public static void requestRegister(Handler handler, String id, String pw){
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("pw", pw);
        new Request(handler, OPCode.REQ_REGISTER , bundle).execute(); // 즉시 실행
//        instance.addRequest(new Request(handler, OPCode.REQ_REGISTER , bundle));
    }

    public static void requestLogin(Handler handler, String id, String pw){
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("pw", pw);
        new Request(handler, OPCode.REQ_LOGIN , bundle).execute(); // 즉시 실행
//        instance.addRequest(new Request(handler, OPCode.REQ_LOGIN , bundle));
    }

    public static void sendSensorData(Handler handler, String data){
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        instance.addRequest(new Request(handler, OPCode.NEW_SENSOR_DATA , bundle, Request.TYPE_DELAYED)); // 큐에 등록
//        Log.d(TAG,"offering sensor data to queue");
    }

    public static void sendEventData(Handler handler, int eventType, double lat, double lon){
        Bundle bundle = new Bundle();
        bundle.putInt("event_type",eventType);
        bundle.putDouble("lat", lat);
        bundle.putDouble("lon", lon);
        new Request(handler, OPCode.NEW_EVENT_DATA, bundle).execute();
    }
}
