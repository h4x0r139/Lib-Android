package cn.yinxm.lib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * Created by yinxm on 2016/3/24.
 * sd卡、ram、rom 存储内工具
 */
public class StorageUtil {
    public static final long ONE_KB = 1024L;
    /**
     * 1024*1024
     */
    public static final long ONE_MB = 1048576L;
    /**
     * 1024*1024*1024
     */
    public static final long ONE_GB = 1073741824L;
    /**
     * 1024*1024*1024*1024,末尾加L
     */
    public static final long ONE_TB = 1099511627776L;
    /**
     * 1024*1024*1024*1024*1024,末尾加L
     */
    public static final long ONE_PB = 1125899906842624L;

    /**
     * 获取缓存路径，默认用外置sd卡，需要先判断权限
     *
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (isSDCardAvailable()) {// || !Environment.isExternalStorageRemovable()
//            <!-- 在SDCard中创建与删除文件权限 -->
//            <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
//            <!-- 往SDCard写入数据权限 -->
//            <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
                    cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static String getInnerCacheDir(Context context) {
        return  context.getCacheDir().getPath();
    }

    public static String getExternalCacheDir(Context context) {
        String cachePath = null;
        if (isSDCardAvailable()) {// || !Environment.isExternalStorageRemovable()
//            <!-- 在SDCard中创建与删除文件权限 -->
//            <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
//            <!-- 往SDCard写入数据权限 -->
//            <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
            cachePath = context.getExternalCacheDir().getPath();
        }
        return cachePath;
    }





    /**
     * SD卡判断
     * 当只存在内部存储，不存在外部扩展sd卡时，返回也为true
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * SD卡路径
     *
     * @return
     */
    public static File getSDCardDirectory() {
        if (isSDCardAvailable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static long getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();//getBlockSizeLong 要求api18
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得SD卡总大小B、KB、MB...
     *
     * @return
     */
    public static String getSDTotalSizeFormat() {
        return formatFileSize(getSDTotalSize());
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }


    /**
     * 获得sd卡剩余容量，即可用大小B、KB、MB...
     *
     * @return
     */
    public static String getSDAvailableSizeFormat() {
        return formatFileSize(getSDAvailableSize());
    }


    /**
     * 获得机身内存总大小 B
     *
     * @return
     */
    public static long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    /**
     * 获得机身内存总大小B、KB、MB...
     *
     * @return
     */
    public static String getRomTotalSizeFormat() {
        return formatFileSize(getRomTotalSize());
    }

    /**
     * B
     * 获得机身可用内存 B
     *
     * @return
     */
    public static long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    /**
     * 获得机身可用内存 B、KB、MB...
     *
     * @return
     */
    public static String getRomAvailableSizeFormat() {
        return formatFileSize(getRomAvailableSize());
    }

    /**
     * 内部存储
     *
     * @return
     */
    public static StorageVolumeUtil.MyStorageVolume getInnerStorage(Context context) {
        //获取所有已挂载设备
        List<StorageVolumeUtil.MyStorageVolume> mountedStorageVolumeList = StorageVolumeUtil.getMountedVolumeList(context);
        //内部存储不可移除
        for (StorageVolumeUtil.MyStorageVolume storageVolume : mountedStorageVolumeList) {
            if (!storageVolume.removable) {
                return storageVolume;
            }
        }
        return null;
    }
    /**
     * 内部存储
     *
     * @return
     */
    public static File getInnerStorageDirectory(Context context) {
        File file = null;
        StorageVolumeUtil.MyStorageVolume storageVolume = getInnerStorage(context);
        if (storageVolume != null) {
            file = new File(storageVolume.path);
        }
        return file;
    }

    /**
     * 内部存储
     *
     * @return
     */
    public static long getInnerStorageTotalSize(Context context) {
        StorageVolumeUtil.MyStorageVolume storageVolume = getInnerStorage(context);
        if (storageVolume != null) {
            return storageVolume.totalSize;
        } else {
            return 0;
        }
    }

    /**
     * 内部存储
     *
     * @return
     */
    public static long getInnerStorageAvailableSize(Context context) {
        StorageVolumeUtil.MyStorageVolume storageVolume = getInnerStorage(context);
        if (storageVolume != null) {
            return storageVolume.availableSize;
        } else {
            return 0;
        }
    }

    /**
     * 获取Volume挂载状态, 例如Environment.MEDIA_MOUNTED
     */
    public static String getVolumeState(Context context, String path) {
        //mountPoint是挂载点名Storage'paths[1]:/mnt/extSdCard不是/mnt/extSdCard/
        //不同手机外接存储卡名字不一样。/mnt/sdcard
        StorageManager mStorageManager = (StorageManager) context
                .getSystemService(Activity.STORAGE_SERVICE);
        String status = null;
        try {
            Method mMethodGetPathsState = mStorageManager.getClass().
                    getMethod("getVolumeState", String.class);
            return (String) mMethodGetPathsState.invoke(mStorageManager, path);

        } catch (Exception e) {
            LogUtil.e(e);
        }
        return status;
    }

    /**
     *  Ram 总大小
     * @param context
     * @return
     */
    public static long getRamTotalSize(Context context){
       String ramPath = "/proc/meminfo";
        long totalSize = 0L;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            String line = null;
            fr = new FileReader(ramPath);
            br = new BufferedReader(fr, 8192);
            line = br.readLine();//MemTotal:       204876 kB
            LogUtil.d(line);
            if (line != null && line.startsWith("MemTotal:")) {
                totalSize = Long.parseLong(line.replaceAll("\\D+", "")) * 1024L;
            }
        } catch (FileNotFoundException e) {
            LogUtil.e(e);
        } catch (IOException e) {
            LogUtil.e(e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    LogUtil.e(e);
                }
                fr = null;
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LogUtil.e(e);
                }
                br = null;
            }
            return  totalSize;
        }
    }

    /**
     *  Ram 总大小 B、KB...
     * @param context
     * @return
     */
    public static String getRamTotalSizeFormat(Context context){
        return formatFileSize(getRamTotalSize(context));
    }

    /**
     *  Ram 可用大小，不精确
     * @param context
     * @return
     */
    public static long getRamAvailableSize(Context context){
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }
    /**
     *  Ram 可用大小
     * @param context
     * @return
     */
    public static String getRamAvailableSizeFormat(Context context){
        return formatFileSize(getRamAvailableSize(context));
    }



    public static String formatFileSize(long size) {
        //系统默认方法Formatter.formatFileSize(size);
        StringBuilder stringBuilder = new StringBuilder();
//        DecimalFormat df  = new DecimalFormat("0.00");
        if (size > ONE_PB) {
            stringBuilder.append(size * 100 / ONE_PB / 100.00).append(" PB");
        } else if (ONE_PB >= size && size > ONE_TB) {
            stringBuilder.append(size * 100 / ONE_TB / 100.00).append(" TB");
        } else if (ONE_TB >= size && size > ONE_GB) {
            stringBuilder.append(size * 100 / ONE_GB / 100.00).append(" GB");
        } else if (ONE_GB >= size && size > ONE_MB) {
            stringBuilder.append(size * 100 / ONE_MB / 100.00).append(" MB");
        } else if (ONE_MB >= size && size > ONE_KB) {
            stringBuilder.append(size * 100 / ONE_KB / 100.00).append(" KB");
        } else if (ONE_KB >= size && size > 0) {
            stringBuilder.append(size).append(" B");
        } else {
            stringBuilder.append("0 B");
        }
        return stringBuilder.toString();
    }

    public static String debugInfo(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getRamTotalSizeFormat=").append(getRamTotalSizeFormat(context)).append(", getRamAvailableSizeFormat=").append(getRamAvailableSizeFormat(context)).append("\n");
        stringBuilder.append("getRomTotalSizeFormat=").append(getRomTotalSizeFormat()).append(", getRomAvailableSizeFormat=").append(getRomAvailableSizeFormat()).append("\n");
        stringBuilder.append("getSDTotalSizeFormat=").append(getSDTotalSizeFormat()).append(", getSDAvailableSizeFormat=").append(getSDAvailableSizeFormat()).append("\n");
        return stringBuilder.toString();
    }

}
