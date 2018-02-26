package com.lunatk.alisa.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import com.lunatk.alisa.bluetooth.AlisaService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by LunaTK on 2018. 1. 19..
 */

public class Utils {

    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static Bitmap resizeBitmap(Context context, int id, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), id);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public static String getRegisteredDevice(SharedPreferences sharedPreferences){
        String address = sharedPreferences.getString("device_address","");
        if(address.length()>0) return address;
        else return null;
    }

    public static void setRegisteredDevice(SharedPreferences.Editor editor, String address, String name){
        editor.putString("device_address", address);
        editor.putString("device_name", name);
        editor.commit();
    }

    public static void removeRegisteredDevice(SharedPreferences.Editor editor){
        editor.remove("device_address");
        editor.remove("device_name");
        editor.commit();
    }

    public static void setSessionId(SharedPreferences.Editor editor, int sessionId){
        editor.putInt("session_id", sessionId);
        editor.commit();
    }

    public static void setLoginInfo(SharedPreferences.Editor editor, String id, String pw){
        editor.putString("user_id", id);
        editor.putString("user_pass", pw);
        editor.commit();
    }

    public static void removeLoginInfo(SharedPreferences.Editor editor){
        editor.remove("user_id");
        editor.remove("user_pass");
        editor.remove("session_id");
        editor.commit();
    }

    public static void startAlisaService(Context context){
        if(isServiceRunning(context)) return;
        Log.d("Utils","start alisa service");
        Intent service = new Intent(context, AlisaService.class);
        context.startService(service);
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

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

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
