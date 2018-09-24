/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package cn.yinxm.lib.screen;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * 通过更新配置，改变屏幕的密度配置
 * <p>
 * Created by yinxuming on 2018/6/20.
 */
public class ScreenAdapter {

    /**
     * @param mContext
     * @param baseDensityDpi 适配的基准densityDpi，720p xhdpi的手机为320
     */
    public static void screenAdapterByUpdate(Context mContext, int baseDensityDpi) {
        // baseDensityDpi 基准密度，UI稿对应适配的设备像素密度，按手机1280*720 xhdpi density=2 densityDpi=320，即为320
        Configuration configuration = mContext.getResources().getConfiguration();
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float density = baseDensityDpi / 160.0f;
        configuration.densityDpi = baseDensityDpi;
        configuration.fontScale = 1.0f;
        displayMetrics.densityDpi = baseDensityDpi;
        displayMetrics.density = density;
        displayMetrics.scaledDensity = density;
        mContext.getResources().updateConfiguration(configuration, displayMetrics);
    }
}
