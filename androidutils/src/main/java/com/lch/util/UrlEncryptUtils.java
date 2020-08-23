package com.lch.util;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class UrlEncryptUtils {

    private static final String algorithmStr = "AES/ECB/PKCS5Padding";

    /**
     * 加密
     */
    public static String encode(String content, String pwd) {
        try {
            if (TextUtils.isEmpty(content)) {
                return content;
            }
            return parseByte2HexStr(encrypt(content, pwd));
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解密
     */
    public static String decode(String content, String password) {
        try {
            if (TextUtils.isEmpty(content)) {
                return content;
            }

            byte[] b = decrypt(parseHexStr2Byte(content), password);
            return new String(b);
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] encrypt(String content, String pwd) {//should be native.
        try {
            byte[] keyStr = getKey(pwd);
            SecretKeySpec key = new SecretKeySpec(keyStr, "AES");
            Cipher cipher = Cipher.getInstance(algorithmStr);
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return result;

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] decrypt(byte[] content, String password) {
        try {
            byte[] keyStr = getKey(password);
            SecretKeySpec key = new SecretKeySpec(keyStr, "AES");
            Cipher cipher = Cipher.getInstance(algorithmStr);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getKey(String password) {
        byte[] rByte = null;
        if (password != null) {
            rByte = password.getBytes();
        } else {
            rByte = new byte[24];
        }
        return rByte;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }

        return result;
    }

}
