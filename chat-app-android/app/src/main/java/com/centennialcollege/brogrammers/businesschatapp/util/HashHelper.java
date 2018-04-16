package com.centennialcollege.brogrammers.businesschatapp.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashHelper {

    private static final String TAG = HashHelper.class.getSimpleName();

    public static String getMd5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException exception) {
            Log.e(TAG, "Could not generate Md5 hash for text : " + text, exception);
            return null;
        }
    }
}
