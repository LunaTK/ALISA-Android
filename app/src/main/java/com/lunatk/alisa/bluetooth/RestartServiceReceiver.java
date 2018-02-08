package com.lunatk.alisa.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by LunaTK on 2018. 1. 31..
 */

public class RestartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("000 RestartService" , "RestartService called : " + intent.getAction());

        /**
         * 서비스 죽일때 알람으로 다시 서비스 등록
         */
        if(intent.getAction().equals("ACTION.RESTART.PersistentService")){

            Log.i("000 RestartService" ,"ACTION.RESTART.PersistentService " );

            Intent i = new Intent(context,AlisaService.class);
            context.startService(i);
        }

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            Log.i("RestartService" , "ACTION_BOOT_COMPLETED" );
            Intent i = new Intent(context,AlisaService.class);
            context.startService(i);

        }


    }
}