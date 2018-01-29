package com.lunatk.alisa.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lunatk.mybluetooth.R;

/**
 * Created by LunaTK on 2018. 1. 15..
 */

public class BluetoothServiceReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(R.string.string_filter_action_send_to_service)){

        }
    }
}
