package com.lunatk.alisa.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lunatk.mybluetooth.R;

import java.util.List;

/**
 * Created by LunaTK on 2018. 1. 15..
 */

public class BluetoothService extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBLEScanner;

    private AlisaDevice alisaDevice;

    private static final String TAG="BluetoothService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        initBluetoothAdapter();
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

    public void broadcastData(String data){
        Intent sendIntent = new Intent(getResources().getString(R.string.string_filter_action_send_to_activity));
        sendIntent.putExtra("data", data);
        sendBroadcast(sendIntent);
    }


}
