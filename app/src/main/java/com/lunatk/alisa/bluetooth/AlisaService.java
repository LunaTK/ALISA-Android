package com.lunatk.alisa.bluetooth;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lunatk.alisa.network.OPCode;
import com.lunatk.alisa.network.RequestManager;
import com.lunatk.alisa.util.Config;
import com.lunatk.alisa.util.Utils;
import com.lunatk.mybluetooth.R;

import java.util.List;

/**
 * Created by LunaTK on 2018. 1. 15..
 */

public class AlisaService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBLEScanner;
    private BluetoothServiceReciever mReceiver;

    private AlisaDevice alisaDevice;

    private ScanningThread scanningThread;

    private RequestManager requestManager = null;
    private Handler sHandler;
    private int startId;
    private boolean loggedIn = false;
    SharedPreferences sharedPreferences;

    private static final String TAG="AlisaService";

    public IBinder mBinder = new AlisaServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreated");
//        unregisterRestartAlarm();
    }

    public Handler getHandler() {
        return sHandler;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        this.startId = startId;
        initBluetoothAdapter();

        requestManager = RequestManager.getInstance();

        if(Utils.getConnectivityStatus(this)!=Utils.TYPE_NOT_CONNECTED) requestManager.setNetworkOnline(true);
        else requestManager.setNetworkOnline(false);

        if(!requestManager.isAlive() && !requestManager.isRunning()) {
            requestManager.start();
        }

        sHandler = new ServiceHandler();
        registerReceiver();
        setImmortal();
        loginCheck();
        return super.onStartCommand(intent,flags,startId);
    }

    private void loginCheck() {
        int sessionId = -1;
//        String id, pw;
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sessionId = sharedPreferences.getInt("session_id", -1);
//        id = sharedPreferences.getString("user_id", null);
//        pw = sharedPreferences.getString("user_pass", null);
//        RequestManager.requestLogin(sHandler, id, pw);
        Log.d(TAG, "Login Info : " +sessionId);


        if(sessionId==-1) { // Login Failed
            Toast.makeText(AlisaService.this, "Login Failed",Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        } else { // Login Success
            Toast.makeText(AlisaService.this, "Login Success",Toast.LENGTH_SHORT).show();
            loggedIn = true;
            startScanning();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
//        mBLEScanner.stopScan(mScanCallback);
        if(alisaDevice != null && alisaDevice.isConnected()){
            alisaDevice.disconnect();
        }
        stopScanning();
        requestManager.stopRunning();
        unregisterReceiver();
//        if(loggedIn) registerRestartAlarm();
//        stopForeground(true);
//        stopSelf();
    }

    public void startScanning(){
        if(scanningThread!=null && scanningThread.isScanning) stopScanning();
        scanningThread = new ScanningThread();
        scanningThread.start();
    }

    public void stopScanning() {
        if(scanningThread!= null && scanningThread.isScanning){
            scanningThread.isScanning = false;
        }
    }

    private void setImmortal(){
        Log.d(TAG,"setImmortal");
        startForeground(1,new Notification());

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("ALISA")
                .setContentText("ALISA 서비스가 실행중입니다")
                .setSmallIcon(R.mipmap.alisa_icon)
                .build();


        nm.notify(startId, notification);
        nm.cancel(startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class AlisaServiceBinder extends Binder {
        AlisaService getService() {
            return AlisaService.this;
        }
    }

    private void initBluetoothAdapter(){
        // BLE adapter 획득
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    private void connectDevice(BluetoothDevice device) {
        Log.d(TAG, "QUALC Connected");
        Toast.makeText(getApplicationContext(), getResources().getText(R.string.string_ble_find_device), Toast.LENGTH_LONG).show();
        scanDevice(false);
//        startService(new Intent(this, BLEService.class));
//        Intent sendIntent = new Intent(getString(R.string.string_filter_action_central_to_peripheral));
//        sendIntent.putExtra(getString(R.string.extra_client_intent_Device), device);
        //  sendBroadcast(sendIntent);

        // TODO TEST용, 끝나면 지울 것
        alisaDevice = new AlisaDevice(this,device);
        alisaDevice.connect();

    }

    private void scanDevice(boolean enable) {
        if(mBLEScanner==null){
            broadcastStatus("Scan unavailable");
            Log.v(TAG,"Scan unavailable");
            return;
        }
        if (enable) {
            mBLEScanner.startScan(mScanCallback);
            broadcastStatus("Scanning...");
            Log.v(TAG,"start scanning period");
        } else{
            mBLEScanner.stopScan(mScanCallback);
            broadcastStatus("Scan stopped");
            Log.v(TAG,"stop scanning period");
        }
    }
    /*
     API 21 이상의 경우의 스캔 콜백
      */
    private ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            //mAdapter.list.clear();
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
        }

        private void processResult(final ScanResult result) {
            Log.d(TAG, String.format("%s, %s, %s", result.getDevice().toString(), result.getRssi() + "", result.getDevice().getName()));
            passScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
        }
    };

    private void passScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if(device.getName()!=null && device.getName().equals("QUALC")){
            connectDevice(device);
            Log.d(TAG,"CONNECTED!");
            stopScanning();
        }
    }

    public void broadcastStatus(String status){
        Intent sendIntent = new Intent(getResources().getString(R.string.string_filter_action_send_to_activity));
        sendIntent.putExtra("status", status);
        sendBroadcast(sendIntent);
    }

    public void broadcastData(int[] data){
        Intent sendIntent = new Intent(getResources().getString(R.string.string_filter_action_send_to_activity));
        sendIntent.putExtra("data", data);
        sendBroadcast(sendIntent);
    }

    private void registerReceiver() {
        if(mReceiver !=null) return;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.string_filter_action_send_to_service));
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new BluetoothServiceReciever();
        registerReceiver(mReceiver,filter);
    }

    private void unregisterReceiver(){
        if(mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    class ServiceHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case OPCode.REQ_LOGIN:
                    if(msg.arg1==OPCode.OK) { // Login success
                        Toast.makeText(AlisaService.this, "Login Success",Toast.LENGTH_SHORT).show();
                        loggedIn = true;
                        startScanning();
                    } else if(msg.arg1==OPCode.NOK){ // Login invalid
                        Toast.makeText(AlisaService.this, "Login Failed",Toast.LENGTH_SHORT).show();
                        loggedIn = false;
                        stopForeground(true);
                        stopSelf();
                    } else if(msg.arg1==OPCode.ERR){ // Server offline
                        Toast.makeText(AlisaService.this, "Server Offline",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    class BluetoothServiceReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(R.string.string_filter_action_send_to_service)){
                //TODO : 센서한테 업데이트 정보 보내주기
            } else if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_ON) {
                    Log.d(TAG, "BLE ON!");
                    if(mBLEScanner==null)initBluetoothAdapter();
                    synchronized (scanningThread) {
                        scanningThread.notify();
                    }
                }
            } else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                if(Utils.getConnectivityStatus(AlisaService.this)!=Utils.TYPE_NOT_CONNECTED){ // Internet Online
                    synchronized (requestManager) {
                        Log.d(TAG,"Network Online");
                        requestManager.setNetworkOnline(true);
                        requestManager.notify();
                    }
                } else { //Internet Offline
                    synchronized (requestManager) {
                        Log.d(TAG, "Network Offline");
                        requestManager.setNetworkOnline(false);
                    }
                }
            }
        }
    }

    class ScanningThread extends Thread{
        public boolean isScanning = true;
        @Override
        public synchronized void run() {
            super.run();
            while(isScanning){
                if(mBLEScanner==null || mBluetoothAdapter.getState()==BluetoothAdapter.STATE_OFF){
                    try {
                        Log.e(TAG,"BLE not enabled. Wait");
                        this.wait();
                    } catch (InterruptedException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Log.e(TAG,"ScanningThread wake up");
                    continue;
                }
                scanDevice(true);
                try {
                    Thread.sleep(Config.BLE_SCAN_TIME);
                } catch (InterruptedException e) {
                    Log.e(TAG,"BLE_SCAN_TIME interrupted : " + e.getMessage());
                }
                scanDevice(false);
                try {
                    Thread.sleep(Config.BLE_SCAN_PERIOD);
                } catch (InterruptedException e) {
                    Log.e(TAG,"BLE_SCAN_PERIOD interrupted : " + e.getMessage());
                }
            }
            Log.d(TAG,"ScanningThread dead");
        }
    }

    /**
     * 알람 매니져에 서비스 등록
     */
    private void registerRestartAlarm(){
        //태근이의 코멘트 : 작동은 하는데 stopService시에만 살아나고 process kill의경우는 살아나지못함.
        Log.i("000 PersistentService" , "registerRestartAlarm" );
        Intent intent = new Intent(AlisaService.this,RestartServiceReceiver.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(AlisaService.this,0,intent,0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1*1000;

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 등록
         */
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime,1*1000,sender);

    }

    /**
     * 알람 매니져에 서비스 해제
     */
    private void unregisterRestartAlarm(){

        Log.i("000 PersistentService" , "unregisterRestartAlarm" );

        Intent intent = new Intent(AlisaService.this,RestartServiceReceiver.class);
        intent.setAction("ACTION.RESTART.PersistentService");
        PendingIntent sender = PendingIntent.getBroadcast(AlisaService.this,0,intent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        /**
         * 알람 취소
         */
        alarmManager.cancel(sender);

    }

}