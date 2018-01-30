package com.lunatk.alisa.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lunatk.alisa.network.OPCode;
import com.lunatk.alisa.network.RequestManager;
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

    private RequestManager requestManager;
    private Handler sHandler;

    private static final String TAG="AlisaService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreated");
    }

    public Handler getHandler() {
        return sHandler;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");

        initBluetoothAdapter();

        if(!RequestManager.getInstance().isAlive()) {
            requestManager = RequestManager.getInstance();
            requestManager.start();
        }

        registerReceiver();
        sHandler = new ServiceHandler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        mBLEScanner.stopScan(mScanCallback);
        if(alisaDevice != null && alisaDevice.isConnected()){
            alisaDevice.disconnect();
        }
        unregisterReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initBluetoothAdapter(){
        // BLE adapter 획득
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Checks if Bluetooth LE Scanner is available.
        if (mBLEScanner == null) {
            Toast.makeText(this, "Can not find BLE Scanner", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            // Bluetooth 활성화 안됨.
            Log.e(TAG,"Bluetooth Not Enabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            scanDevice(true);
        }
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
        if (enable) {
            mBLEScanner.startScan(mScanCallback);
            broadcastStatus("Scanning...");
        } else{
            mBLEScanner.stopScan(mScanCallback);
            broadcastStatus("Scan finished");
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
//        mAdapter.addDevice(device);
        if(device.getName()!=null && device.getName().equals("QUALC")){
            connectDevice(device);
            Log.d(TAG,"CONNECTED!");
            scanDevice(false);
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
                case OPCode.SENSOR_DATA:
                    break;
            }
        }
    }

    class BluetoothServiceReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(R.string.string_filter_action_send_to_service)){
                //TODO : 센서한테 업데이트 정보 보내주기
            }
        }
    }


}
