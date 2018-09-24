package cn.yinxm.lib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * <p>
 *
 * @author yinxuming
 * @date 2018/8/24
 */
public class FileExists {

    public static boolean isClassExist(String clazz) {
        boolean flag = true;
        try {
            Class.forName(clazz);
        } catch (Exception ex) {
            flag = false;
        }
        return flag;
    }

    /**
     *
     * @param path /system/lib/libtest.so
     * @return
     */
    public static boolean isSoExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void test(Context context, String soPath) {
        try {
            //ApplicationInfo:用于封装应用的信息
            ApplicationInfo info = context.getApplicationInfo();
            String dataDir = info.dataDir;// 存放数据的路径  应用数据目录。
            String nativeLibraryDir = info.nativeLibraryDir;// 本地路径  JNI本地库存放路径。
            String sourceDir = info.sourceDir;// 资源路径  应用APK的全路径
            String publicSourceDir = info.publicSourceDir;// 公共资源路径
            int targetSdkVersion = info.targetSdkVersion;//  // 应用所需的最小sdk版本
            ZipFile file = new ZipFile(info.sourceDir);
            //
            ZipEntry zipEntry = file.getEntry("lib/armeabi/a.so");
            if (zipEntry != null) {
                //a.so已经 加入到项目中
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
