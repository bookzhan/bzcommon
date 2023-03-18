package com.bzcommon.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

/**
 * Created by bookzhan on 2023−01-25 17:49.
 * description:
 */
public class BZCommonUtil {
    public static boolean floatIsEqual(float a, float b) {
        return Math.abs(a - b) < 0.00001;
    }

    /**
     * @return apk签名信息, 并MD5
     */
    public static String getSignatureInfo(Context context) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            byte[] signatureBytes = signatures[0].toByteArray();
            String encodeToString = Base64.encodeToString(signatureBytes, Base64.DEFAULT);
            return BZMD5Util.md5(encodeToString);
        } catch (Exception e) {
            BZLogUtil.e(e);
        }
        return null;
    }

}
