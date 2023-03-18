package com.bzcommon.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
    private static final String AES_MODE = "AES/ECB/PKCS5Padding";

    /* 创建密钥 */
    public static SecretKeySpec createKey(String password) {
        if (password == null) {
            password = "";
        }
        StringBuilder sb = new StringBuilder(32);
        sb.append(password);
        while (sb.length() < 32) {
            sb.append("0");
        }
        if (sb.length() > 32) {
            sb.setLength(32);
        }
        byte[] data = sb.toString().getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(data, "AES");
    }

    private static byte[] encrypt(byte[] data, String password) {
        try {
            @SuppressLint("GetInstance")
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, createKey(password));
            return cipher.doFinal(data);
        } catch (Throwable throwable) {
            BZLogUtil.e(throwable);
        }
        return null;
    }

    private static byte[] decrypt(byte[] data, String password) {
        try {
            @SuppressLint("GetInstance")
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, createKey(password));
            return cipher.doFinal(data);
        } catch (Throwable throwable) {
            BZLogUtil.e(throwable);
        }
        return null;
    }

    public static String encrypt(String content, String password) {
        if (null == content || null == password) {
            BZLogUtil.e("encrypt null==plainText||null==password");
            return "";
        }
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encrypt(data, password);
        if (null == encrypted) {
            return "";
        }
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String content, String password) {
        if (null == content || null == password) {
            BZLogUtil.e("encrypt null==plainText||null==password");
            return "";
        }
        byte[] data = Base64.decode(content, Base64.DEFAULT);
        byte[] decrypted = decrypt(data, password);
        if (null == decrypted) {
            return "";
        }
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
