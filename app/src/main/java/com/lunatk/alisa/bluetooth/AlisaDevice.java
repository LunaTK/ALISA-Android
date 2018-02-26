package com.lunatk.alisa.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.lunatk.alisa.network.RequestManager;
import com.lunatk.alisa.util.UUIDHelper;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by LunaTK on 2018. 1. 12..
 */

class AlisaDevice extends BluetoothGattCallback {
    private static final String TAG = "AlisaDevice";
    private static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUIDHelper.uuidFromString("2902");
    private BluetoothDevice device;
    private byte[] advertisingData = {0};
    private int advertisingRSSI = 0;
    private boolean connected = false;
    private boolean connecting = false;
    BluetoothGatt gatt;
    private AlisaService mService;
    private Context mContext;

    public AlisaDevice(AlisaService service, BluetoothDevice device) {
        this.mService = service;
        this.device = device;
        mContext = service.getApplicationContext();
    }

    public AlisaDevice(BluetoothDevice device, int advertisingRSSI, byte[] scanRecord) {

        this.device = device;
        this.advertisingRSSI = advertisingRSSI;
        this.advertisingData = scanRecord;
    }

    public void connect() {
        BluetoothDevice device = getDevice();
        connecting = true;
        if (Build.VERSION.SDK_INT < 23) {
            gatt = device.connectGatt(mContext, false, this);
        } else {
            gatt = device.connectGatt(mContext, false, this, BluetoothDevice.TRANSPORT_LE);
        }
    }

    public void disconnect() {
        connected = false;
        connecting = false;

        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
            gatt = null;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    // Update rssi and scanRecord.
    public void update(int rssi, byte[] scanRecord) {
        this.advertisingRSSI = rssi;
        this.advertisingData = scanRecord;
    }

    public void updateRssi(int rssi) {
        advertisingRSSI = rssi;
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        Log.e(TAG, "onServicesDiscovered");

        Log.d(TAG,"<<Bluetooth Gatt Service List>>");
        if(status==BluetoothGatt.GATT_SUCCESS){
            for(BluetoothGattService service : gatt.getServices()){
                Log.d(TAG, "- Service : " + service.getUuid().toString());
                for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                    Log.d(TAG,"\t- Characteristic : " + characteristic.getUuid()
                            + " Permissions[" + String.format("%x",characteristic.getPermissions()) + "]"
                            + " Properties[" + String.format("%x",characteristic.getProperties()) + "]" );

                    if(characteristic.getValue()!=null)
                        Log.d(TAG,"\t\t- Values(" + characteristic.getValue().length + ") : " + Arrays.toString(characteristic.getValue()));

                    if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0){
                        gatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID);
                        if (descriptor != null) {
                            Log.e(TAG, "get descriptor, CLIENT_CHARACTERISTIC_CONFIGURATION_UUID");
                            // notify하기 위해, descriptor를 검사.
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            if (gatt.writeDescriptor(descriptor)) {
                                Log.e(TAG, "success to write Descriptor");
                            } else {
                                Log.e(TAG, "failed to write Descriptor");
                            }

                        } else {
                            //nothing
                        }
                    }

                    /*
                    for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()){
                        Log.d(TAG,"\t\t- Descriptor : " + descriptor.getUuid()
                                + " Permissions[" + String.format("%x",descriptor.getPermissions()) + "]"  );

                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        if (gatt.writeDescriptor(descriptor)) {
                            Log.e(TAG, "success to write Descriptor");
                        } else {
                            Log.e(TAG, "failed to write Descriptor");
                        }

                    }
                    */
                }
            }
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        this.gatt = gatt;
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            Log.d(TAG, "STATE_CONNECTED");
            connected = true;
            connecting = false;
            gatt.discoverServices();
            mService.broadcastStatus("Connected!");
        } else {
            Log.d(TAG, "STATE_DISCONNECTED");
            disconnect();
            mService.broadcastStatus("Disconnected!");
            mService.startScanningWithRegisteredDevice();
        }

    }

    int[] data_buffer = new int[22];
    int data_null_index = 0;

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
//        Log.d(TAG, "onCharacteristicChanged " + characteristic.getUuid());
        byte[] data = characteristic.getValue();
        if (data[0] == 0xAA - 256) { // STX
            if (data[1] == 0x11) {  //FLOW TYPE
                if (data[2] == 0x01) { //CMD
                    if (data[3] == 0x01) { // CONTINUE
                        for (int i = 0; i < data[4]; i++) {

                            data_buffer[data_null_index + i] = data[5 + i] >= 0 ? data[5 + i] : data[5 + i] + 128;
                        }
                        data_null_index += data[4]; // DATA_LENGTH
                    } else { // FINISH
                        for (int i = 0; i < data[4]; i++) {
                            data_buffer[data_null_index + i] = data[5 + i];
                        }
                        String datastr = "홀센서 : " + (data_buffer[0] + data_buffer[1] * 256)
                                + "\n온도 : " + ((data_buffer[2] + data_buffer[3] * 256) * 0.0078125)
                                + "\n가속도x : " + (data_buffer[4] + data_buffer[5] * 256)
                                + "\n가속도y : " + (data_buffer[6] + data_buffer[7] * 256)
                                + "\n가속도z : " + (data_buffer[8] + data_buffer[9] * 256)
                                + "\n자이로x : " + (data_buffer[10] + data_buffer[11] * 256)
                                + "\n자이로y : " + (data_buffer[12] + data_buffer[13] * 256)
                                + "\n자이로z : " + (data_buffer[14] + data_buffer[15] * 256)
                                + "\n지자기x : " + (data_buffer[16] + data_buffer[17] * 256)
                                + "\n지자기y : " + (data_buffer[18] + data_buffer[19] * 256)
                                + "\n지자기z : " + (data_buffer[20] + data_buffer[21] * 256);

                        mService.broadcastData(data_buffer);
                        RequestManager.sendSensorData(mService.getHandler(), datastr);

                        data_null_index = 0;
                    }
                }
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        Log.d(TAG, "onCharacteristicRead " + characteristic);

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        Log.d(TAG, "onCharacteristicWrite " + characteristic);

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        Log.d(TAG, "onDescriptorWrite " + descriptor);

    }
}