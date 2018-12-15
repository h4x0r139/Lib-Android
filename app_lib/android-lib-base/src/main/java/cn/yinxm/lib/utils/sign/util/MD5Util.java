package cn.yinxm.lib.utils.sign.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.yinxm.lib.utils.log.LogUtil;


public class MD5Util {
    public static String getMD5(String val) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return getMD5(val, "UTF-8");
    }

    public static String getMD5(String val, String charset) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes(charset));
        byte[] m = md5.digest();
        return getBytesToHexString(m);
    }

    public static byte[] getMD5(byte[] plainBytes) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(plainBytes);
        return md5.digest();
    }

    public static String getMD5Str(byte[] plainBytes) throws NoSuchAlgorithmException {
        return getBytesToHexString(getMD5(plainBytes));
    }

    public static String getBytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * bd radio
     *
     * @param s
     * @return
     */
    public static final String getMD5String(String s) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            LogUtil.e(e);
            return null;
        }
    }
}
