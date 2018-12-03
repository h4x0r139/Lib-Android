package cn.yinxm.lib.utils;

import android.content.Context;
import android.os.Looper;

import java.net.URL;
import java.net.URLConnection;

import cn.yinxm.lib.api.manager.AppManager;
import cn.yinxm.lib.utils.log.LogUtil;

/**
 * Created by yinxm on 2017/3/27.
 * 功能: 时间工具
 */

public class SystemTimeUtil {
    public static final long ALLOW_ERROR_MILLISECOND = 40000;//允许误差40s，超过这个误差，取网络时间
    public static final long TIME_SYNC_INTERVAL_MILLISECOND = 10 * 60 * 1000;//同步时间最小间隔10分钟

    public static final String SP_CONFIG = "config_time";//上一次同步时间的时间
    public static final String SP_TIME_LAST_SYNC = "time_last_sync";//上一次同步时间的时间
    public static final String SP_TIME_NET_DIFF_SYS = "time_diff";//时间相差

    /**
     * 获取校正后的系统时间
     *
     * @return
     */
    public static long getCorrectSystemTime() {
        long currentTime = 0;
        try {
            Context context = AppManager.getInstance().getApplicationContext();
            if (context == null) {
                currentTime = System.currentTimeMillis();
            } else {
                long netDifSys = 0;
                String netDifSysStr = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_NET_DIFF_SYS);
                if (StringUtil.isNotBlank(netDifSysStr)) {
                    netDifSys = Long.parseLong(netDifSysStr);
                }
                //有误差，间隔一定时间去同步网络时间
                String lastSync = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_LAST_SYNC);
                long lastSyncLong = 0;
                if (StringUtil.isNotBlank(lastSync)) {
                    lastSyncLong = Long.parseLong(lastSync);
                }
//                LogUtil.d("lastSync=" + lastSync+", netDifSys="+netDifSys);
                if (lastSyncLong == 0 || System.currentTimeMillis() > (lastSyncLong + TIME_SYNC_INTERVAL_MILLISECOND)) {
                    if (Looper.getMainLooper() == Looper.myLooper()) {
//                        LogUtil.d("Main UI Thead");
                        syncNetTime();
                    } else {
//                        LogUtil.d("Child Thead");
                        netDifSys = getNetTimeDifSys();
                    }
                }
                long localTime = System.currentTimeMillis();
                currentTime = localTime;
                currentTime = currentTime + netDifSys;
                if (netDifSys > 0) {
                    LogUtil.d("校正前时间：" + localTime + ", 校正后时间：" + currentTime + ", 误差：" + netDifSys);
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
//        LogUtil.d("syncNetTime ");
        long netDifSys = 0;

        Context context = AppManager.getInstance().getApplicationContext();
        if (context == null) {
            return netDifSys;
        }
        String lastSync = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_LAST_SYNC);
        long lastSyncLong = 0;
        if (StringUtil.isNotBlank(lastSync)) {
            lastSyncLong = Long.parseLong(lastSync);
        }
//        LogUtil.d("lastSync=" + lastSync);

        try {
            long netTime = getNetTime();
            if (netTime > 0) {
                long sysTime = System.currentTimeMillis();//ms
                //允许误差40s=40 000ms
                netDifSys = netTime - sysTime;
                LogUtil.d("sysTime=" + sysTime + ", netTime=" + netTime + ", netDifSys=" + netDifSys);

                if (Math.abs(netDifSys) > ALLOW_ERROR_MILLISECOND) {
                    LogUtil.d("当前系统时间不正确 netDifSys=" + netDifSys);
                    SpUtil.spWriteStr(AppManager.getInstance().getApplicationContext(), SP_CONFIG, SP_TIME_NET_DIFF_SYS, String.valueOf(netDifSys));
                } else {
                    LogUtil.d("当前系统时间正确 netDifSys=" + netDifSys);
                    SpUtil.spWriteStr(AppManager.getInstance().getApplicationContext(), SP_CONFIG, SP_TIME_NET_DIFF_SYS, "");
                }
                SpUtil.spWriteStr(AppManager.getInstance().getApplicationContext(), SP_CONFIG, SP_TIME_LAST_SYNC, String.valueOf(sysTime));
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
        LogUtil.d("syncNetTime ");

        Context context = AppManager.getInstance().getApplicationContext();
        if (context == null) {
            return;
        }
        String lastSync = SpUtil.spReadStr(context, SP_CONFIG, SP_TIME_LAST_SYNC);
        long lastSyncLong = 0;
        if (StringUtil.isNotBlank(lastSync)) {
            lastSyncLong = Long.parseLong(lastSync);
        }
//        LogUtil.d("lastSync=" + lastSync);
//        if (lastSyncLong == 0 || System.currentTimeMillis() > (lastSyncLong + TIME_SYNC_INTERVAL_MILLISECOND)) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long netTime = getNetTime();
                    if (netTime > 0) {
                        long sysTime = System.currentTimeMillis();//ms
                        //允许误差40s=40 000ms
                        long netDifSys = netTime - sysTime;
                        LogUtil.d("sysTime=" + sysTime + ", netTime=" + netTime + ", netDifSys=" + netDifSys);

                        if (Math.abs(netDifSys) > ALLOW_ERROR_MILLISECOND) {
//                            LogUtil.d("当前系统时间不正确 netDifSys=" + netDifSys);
                            SpUtil.spWriteStr(AppManager.getInstance().getApplicationContext(), SP_CONFIG, SP_TIME_NET_DIFF_SYS, String.valueOf(netDifSys));
                        } else {
//                            LogUtil.d("当前系统时间正确 netDifSys=" + netDifSys);
                            SpUtil.spWriteStr(AppManager.getInstance().getApplicationContext(), SP_CONFIG, SP_TIME_NET_DIFF_SYS, "");
                        }
                        SpUtil.spWriteStr(AppManager.getInstance().getApplicationContext(), SP_CONFIG, SP_TIME_LAST_SYNC, String.valueOf(sysTime));
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
        if (NetworkUtil.isNetworkConnected(AppManager.getInstance().getApplicationContext())) {
            try {
                URL url = new URL("http://www.beijing-time.org");
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                time = urlConnection.getDate();
            } catch (Exception e) {
                LogUtil.e(e);
            }
            LogUtil.d("getNetTime time=" + time);
        }
        return time;
    }
}
