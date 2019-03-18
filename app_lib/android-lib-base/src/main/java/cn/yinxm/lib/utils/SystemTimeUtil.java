package cn.yinxm.lib.utils;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.baidu.android.ApplicationManager;

import java.net.URL;
import java.net.URLConnection;


/**
 * Created by yinxm on 2017/3/27.
 * 功能: 时间工具
 */

public class SystemTimeUtil {
    private static final String TAG = "SystemTimeUtil";

    /**
     * 允许误差40s，超过这个误差，取网络时间
     */
    public static final long ALLOW_ERROR_MILLISECOND = 40000;
    /**
     * 同步时间最小间隔10分钟
     */
    public static final long TIME_SYNC_INTERVAL_MILLISECOND = 10 * 60 * 1000;

    /**
     * 上一次同步时间的时间
     */
    public static final String SP_CONFIG = "config_time";
    /**
     * 上一次同步时间的时间
     */
    public static final String SP_TIME_LAST_SYNC = "time_last_sync";
    /**
     * 时间相差
     */
    public static final String SP_TIME_NET_DIFF_SYS = "time_diff";

    /**
     * 获取校正后的系统时间
     *
     * @return
     */
    public static long getCorrectSystemTime() {
        long currentTime = 0;
        try {
            Context context = ApplicationManager.getInstance().getContext();
            if (context == null) {
                currentTime = System.currentTimeMillis();
            } else {
                long netDifSys = 0;
                String netDifSysStr = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_NET_DIFF_SYS);
                if (!TextUtils.isEmpty(netDifSysStr)) {
                    netDifSys = Long.parseLong(netDifSysStr);
                }
                // 有误差，间隔一定时间去同步网络时间
                String lastSync = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_LAST_SYNC);
                long lastSyncLong = 0;
                if (!TextUtils.isEmpty(lastSync)) {
                    lastSyncLong = Long.parseLong(lastSync);
                }
//                LogUtil.d(TAG, "lastSync=" + lastSync+", netDifSys="+netDifSys);
                if (lastSyncLong == 0 || System.currentTimeMillis() > (lastSyncLong + TIME_SYNC_INTERVAL_MILLISECOND)) {
                    if (Looper.getMainLooper() == Looper.myLooper()) {
//                        LogUtil.d(TAG, "Main UI Thead");
                        syncNetTime();
                    } else {
//                        LogUtil.d(TAG, "Child Thead");
                        netDifSys = getNetTimeDifSys();
                    }
                }
                long localTime = System.currentTimeMillis();
                currentTime = localTime;
                currentTime = currentTime + netDifSys;
                if (netDifSys > 0) {
                    LogUtil.d(TAG, "校正前时间：" + localTime + ", 校正后时间：" + currentTime + ", 误差：" + netDifSys);
                }
            }
        } catch (Exception e) {
            LogUtil.e(e);
            currentTime = System.currentTimeMillis();
        }
        return currentTime;
    }

    /**
     * 获取时间差
     */
    public static long getNetTimeDifSys() {
//        LogUtil.d(TAG, "syncNetTime ");
        long netDifSys = 0;

        Context context = ApplicationManager.getInstance().getContext();
        if (context == null) {
            return netDifSys;
        }
        String lastSync = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_LAST_SYNC);
        long lastSyncLong = 0;
        if (!TextUtils.isEmpty(lastSync)) {
            lastSyncLong = Long.parseLong(lastSync);
        }
//        LogUtil.d(TAG, "lastSync=" + lastSync);

        try {
            long netTime = getNetTime();
            if (netTime > 0) {
                long sysTime = System.currentTimeMillis();
                netDifSys = netTime - sysTime;
                LogUtil.d(TAG, "sysTime=" + sysTime + ", netTime=" + netTime + ", netDifSys=" + netDifSys);

                if (Math.abs(netDifSys) > ALLOW_ERROR_MILLISECOND) {
                    LogUtil.d(TAG, "当前系统时间不正确 netDifSys=" + netDifSys);
                    SpUtil.spWriteStr(context, SP_CONFIG, SP_TIME_NET_DIFF_SYS, String.valueOf(netDifSys));
                } else {
                    LogUtil.d(TAG, "当前系统时间正确 netDifSys=" + netDifSys);
                    SpUtil.spWriteStr(context, SP_CONFIG, SP_TIME_NET_DIFF_SYS, "");
                }
                SpUtil.spWriteStr(context, SP_CONFIG, SP_TIME_LAST_SYNC, String.valueOf(sysTime));
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return netDifSys;
    }

    /**
     * 同步网络时间
     */
    public static void syncNetTime() {
        LogUtil.d(TAG, "syncNetTime ");

        final Context context = ApplicationManager.getInstance().getContext();
        if (context == null) {
            return;
        }
        String lastSync = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_LAST_SYNC);
        long lastSyncLong = 0;
        if (!TextUtils.isEmpty(lastSync)) {
            lastSyncLong = Long.parseLong(lastSync);
        }
//        LogUtil.d(TAG, "lastSync=" + lastSync);
//        if (lastSyncLong == 0 || System.currentTimeMillis() > (lastSyncLong + TIME_SYNC_INTERVAL_MILLISECOND)) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long netTime = getNetTime();
                    if (netTime > 0) {
                        long sysTime = System.currentTimeMillis();
                        long netDifSys = netTime - sysTime;
                        LogUtil.d(TAG, "sysTime=" + sysTime + ", netTime=" + netTime + ", netDifSys=" + netDifSys);

                        if (Math.abs(netDifSys) > ALLOW_ERROR_MILLISECOND) {
//                            LogUtil.d(TAG, "当前系统时间不正确 netDifSys=" + netDifSys);
                            SpUtil.spWriteStr(context, SP_CONFIG, SP_TIME_NET_DIFF_SYS, String.valueOf(netDifSys));
                        } else {
//                            LogUtil.d(TAG, "当前系统时间正确 netDifSys=" + netDifSys);
                            SpUtil.spWriteStr(context, SP_CONFIG, SP_TIME_NET_DIFF_SYS, "");
                        }
                        SpUtil.spWriteStr(context, SP_CONFIG, SP_TIME_LAST_SYNC, String.valueOf(sysTime));
                    }
                } catch (Exception e) {
                    LogUtil.e(e);
                }

            }
        }).start();

//        }


    }

    /**
     * 获取网络时间
     *
     * @return
     */
    public static long getNetTime() {
        long time = 0;
        try {
//            URL url = new URL("http://www.beijing-time.org");
            URL url = new URL("http://www.baidu.com");
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            time = urlConnection.getDate();
        } catch (Exception e) {
            LogUtil.e(e);
        }
        LogUtil.d(TAG, "getNetTime time=" + time);
        return time;
    }
}
