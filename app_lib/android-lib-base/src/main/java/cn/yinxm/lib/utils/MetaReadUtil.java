package cn.yinxm.lib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * 功能：
 * Created by yinxm on 2017/6/1.
 */

public class MetaReadUtil {
    /**
     * 读取application 节点  meta-data 信息
     */
    public static String readMetaDataFromApplication(Context context, String key) {
       String value = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value =  appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(e);
        }
        return value;

    }

}
