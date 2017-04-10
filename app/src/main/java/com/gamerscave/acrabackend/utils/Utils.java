package com.gamerscave.acrabackend.utils;

import android.support.annotation.NonNull;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    @NonNull
    public static String md5(String unhashed) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(unhashed.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    public static String getDate(){
        Calendar c = Calendar.getInstance();
        int d = c.get(Calendar.DATE);
        int m = c.get(Calendar.MONTH) + 1;
        int y = c.get(Calendar.YEAR);

        return String.format(Locale.ENGLISH, "%d.%d.%d", d, m, y);
    }
}
