package cn.yinxm.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

/**
 * Created by yinxuming on 2018/5/26.
 * 应用管理辅助类
 */
public class ApkHelper {

    /**
     * 安装apk
     *
     * @param context
     * @param apkFilePath
     */
    public static void installApk(Context context, String apkFilePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkFilePath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 静默安装apk
     *
     * @param context
     * @param apkFilePath
     */
    public static void installApkSilent(Context context, String apkFilePath) throws IOException {
        // adb shell pm uninstall -k packageName
        Runtime.getRuntime().exec("pm install -t -r "+apkFilePath);
    }

    /**
     * 卸载apk
     *
     * @param context
     * @param packageName
     */
    public static void uninstallApk(Context context, String packageName) {
        Uri packageUri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, packageUri);
        context.startActivity(intent);
    }

    /**
     * 静默卸载apk
     *  <!--应用卸载权限，系统应用-->
     * <uses-permission android:name="android.permission.DELETE_PACKAGES"/>
     *
     * @param context
     * @param packageName
     */
    public static void uninstallApkSilent(Context context, String packageName) throws IOException {
        // adb shell pm uninstall -k packageName
        Runtime.getRuntime().exec("pm uninstall -k "+packageName);
    }

    public static void uninstallApkSilent2(Context context, String packageName) throws IOException {
        // api 21以上 android 5.0
//       PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
    }
}
