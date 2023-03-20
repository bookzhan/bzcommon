package com.bzcommon.utils;

import android.text.TextUtils;

import java.security.MessageDigest;

/**
 * Created by zhandalin on 2018-10-13 13:52.
 * 说明:
 */
public class BZMD5Util {
    public static String md5(String input) {
        if (TextUtils.isEmpty(input)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            BZLogUtil.e(e);
        }
        return null;
    }
}
