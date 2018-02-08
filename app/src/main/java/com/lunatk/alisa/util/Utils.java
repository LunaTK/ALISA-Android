package com.lunatk.alisa.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by LunaTK on 2018. 1. 19..
 */

public class Utils {

    public static byte[] getSHA512(String str){
        byte[] encodedhash = null;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
            encodedhash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            encodedhash = Base64.encode(encodedhash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException ex) {
        }
        return encodedhash;
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(service.service.getClassName().equals("com.lunatk.alisa.bluetooth.AlisaService"))
                return true;
        }
        return false;
    }
}
