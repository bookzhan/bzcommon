package com.bzcommon.utils;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Util {

    /**
     * 编码
     *
     * @param message 需编码的信息
     */
    public static String encodeWord(String message) {
        try {
            return Base64.encodeToString(message.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        } catch (Exception e) {
            BZLogUtil.e(e);
        }
        return null;
    }

    /**
     * 解码
     * @param encodeWord 编码后的内容
     */
    public static String decodeWord(String encodeWord) {
        try {
            return new String(Base64.decode(encodeWord, Base64.NO_WRAP), StandardCharsets.UTF_8);
        } catch (Exception e) {
            BZLogUtil.e(e);
        }
        return null;
    }

}