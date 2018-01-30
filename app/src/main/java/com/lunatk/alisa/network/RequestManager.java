package com.lunatk.alisa.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lunatk.alisa.util.Config;

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

    public static RequestManager getInstance(){
        if(instance == null) instance = new RequestManager();
        return instance;
    }

    private RequestManager(){
        queue = new PriorityQueue<>();
    }

    public void addRequest(Request request){
        queue.offer(request);
    }

    @Override
    public void run() {
        super.run();
        isRunning = true;

        while(isRunning){

            try {
                Thread.sleep(Config.FLUSH_REQUEST_PERIOD);
            } catch (InterruptedException e) {
                Log.d(TAG,"Interrupted");
            }

            Request.establishStaticConnection();
            while(queue.size()!=0) queue.poll().staticExecute();
            Request.closeStaticConnection();
        }
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
        instance.addRequest(new Request(handler, OPCode.SENSOR_DATA , bundle, Request.TYPE_DELAYED)); // 큐에 등록
        Log.d(TAG,"offering sensor data to queue");
    }
}
