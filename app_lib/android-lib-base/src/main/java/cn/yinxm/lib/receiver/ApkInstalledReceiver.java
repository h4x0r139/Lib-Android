package cn.yinxm.lib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * Created by yinxuming on 2018/5/26.
 * 1、动态注册、静态xml注册均可以
 *
 */
public class ApkInstalledReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        String packageName = intent.getDataString();
        String packageName = null;
        if (intent.getData() != null) {
            packageName = intent.getData().getSchemeSpecificPart();
        }
        LogUtil.d("ApkInstalledReceiver action="+action+", "+packageName);
        if (Intent.ACTION_PACKAGE_ADDED == action) { // install   android.intent.action.PACKAGE_ADDED
            onApkInstalled(packageName);
        } else if (Intent.ACTION_PACKAGE_REPLACED == action
                || Intent.ACTION_MY_PACKAGE_REPLACED == action) { // install replace
            onApkInstalled(packageName);
        } else if (Intent.ACTION_PACKAGE_REMOVED == action) { // uninstall android.intent.action.PACKAGE_REMOVED
            onApkUninstalled(packageName);
        }
    }

    public IntentFilter getIntentFileter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);//监听应用安装
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);//监听应用卸载
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);//监听应用升级
        intentFilter.addAction(Intent.ACTION_MY_PACKAGE_REPLACED);//自己的应用升级
        intentFilter.addDataScheme("package");  // 必须要
        return intentFilter;
    }

    public void  onApkInstalled(String packageName) {

    }

    public void onApkUninstalled(String packageName) {

    }
}

/*
    <intent-filter>
    <action android:name="android.intent.action.PACKAGE_ADDED"/>
    <action android:name="android.intent.action.PACKAGE_REMOVED"/>
    <action android:name="android.intent.action.PACKAGE_REPLACED"/>
    <action android:name="android.intent.action.MY_PACKAGE_REPLACED"/>
    <data android:scheme="package"/>
    </intent-filter>
*/

