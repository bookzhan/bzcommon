package com.bzcommon.utils;

import android.util.Base64;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSAUtil {

    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"
    private static String sTransform = "RSA/NONE/PKCS1Padding";

    //进行Base64转码时的flag设置，默认为Base64.DEFAULT
    private static int sBase64Mode = Base64.DEFAULT;

    //初始化方法，设置参数
    public static void init(String transform, int base64Mode) {
        sTransform = transform;
        sBase64Mode = base64Mode;
    }

    //产生密钥对
    public static KeyPair generateRSAKeyPair(int keyLength) {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); //设置密钥长度
            keyPairGenerator.initialize(keyLength); //产生密钥对
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    /**
     * 加密或解密数据的通用的方法,srcData:待处理的数据；key:公钥或者私钥，mode指
     * 加密还是解密，值为Cipher.ENCRYPT_MODE或者Cipher.DECRYPT_MODE
     */
    public static byte[] processDAta(byte[] srcData, Key key, int mode) {
        //用来保存处理的结果
        byte[] resultBytes = null;
        //构建Cipher对象，需要传入一个字符串，格式必须为"algorithm/mode/padding"或者"algorithm/",意为"算法/加密模式/填充方式"
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            //初始化Cipher,mode指定是加密还是解密，key为公钥或密钥
            cipher.init(mode, key);
            //处理数据
            resultBytes = cipher.doFinal(srcData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBytes;
    }

    //使用公钥加密数据，结果用Base64转码
    public static String encryptDataByPublicKey(byte[] srcData, PublicKey publicKey) {
        byte[] resultBytes = processDAta(srcData, publicKey, Cipher.ENCRYPT_MODE);
        return Base64.encodeToString(resultBytes, sBase64Mode);
    }

    //使用私钥解密，结果用Base64转码
    public static byte[] decryptDataByPrivate(String encryptedData, PrivateKey privateKey) {
        byte[] bytes = Base64.decode(encryptedData, sBase64Mode);
        return processDAta(bytes, privateKey, Cipher.DECRYPT_MODE);
    }

    //使用私钥解密，返回解码数据
    public static String decryptToStrByPrivate(String encryptedData, PrivateKey privateKey) {
        return new String(decryptDataByPrivate(encryptedData, privateKey));
    }
}

