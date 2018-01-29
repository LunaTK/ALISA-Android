package com.lunatk.alisa.util;

import android.util.Base64;

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
}
