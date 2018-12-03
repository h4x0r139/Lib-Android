package cn.yinxm.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.android.internal.telephony.uicc.IccUtils.bytesToHexString;

/**
 * <p>
 *
 * @author yinxuming
 * @date 2018/9/20
 */
public class Md5Util {
    public String getMd5(String key) {
        String cacheKey = null;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

}
