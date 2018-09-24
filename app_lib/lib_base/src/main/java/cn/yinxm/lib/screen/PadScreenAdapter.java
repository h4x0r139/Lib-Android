package com.baidu.media.ui.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import cn.yinxm.lib.screen.ScreenUtil;

/**
 * 车机UI稿一般会设计成1:1的，在使用UI稿标注的时候rd也一般用的mdpi（uiScale=1），可以使用此工具适配车机、手机
 * <p>
 * Created by yinxuming on 2018/7/31.
 */
public class PadScreenAdapter {

    private Context mContext;
    private int uiMaxSize;
    private int uiMinSize;
    private float uiScale;

    private int screenMaxSize;
    private int screenMinSize;
    private float screenOriginalDensity;
    private float scaleScreenToUi;

    private PadScreenAdapter() {
    }

    private static class SingleHolder {

        private static final com.baidu.media.ui.util.PadScreenAdapter INSTANCE = new com.baidu.media.ui.util.PadScreenAdapter();
    }

    public static com.baidu.media.ui.util.PadScreenAdapter getInstance() {
        return SingleHolder.INSTANCE;
    }


    /**
     * 全局调用一次
     * 设置UI稿设计尺寸，scale为标注图采用的倍数，例如mdpi图标注的为1倍图，xhdpi图标注的为2倍图...
     *
     * @param uiWidth  UI稿宽
     * @param uiHeight UI稿高
     * @param uiScale  标注或者切图采用的UI稿倍数
     */
    public com.baidu.media.ui.util.PadScreenAdapter setUiDesign(Context context, int uiWidth, int uiHeight, float uiScale) {
        if (context == null || uiWidth <= 0 || uiHeight <= 0 || uiScale <= 0) {
            throw new RuntimeException(
                    new IllegalArgumentException("illegal argument context=" + context + ", "
                            + "uiWidth=" + uiWidth + "， uiHeight="
                            + uiHeight + ", scale=" + uiScale));
        }
        mContext = context.getApplicationContext();
        this.uiScale = uiScale;

        if (uiWidth <= uiHeight) {
            uiMinSize = uiWidth;
            uiMaxSize = uiHeight;
        } else {
            uiMinSize = uiHeight;
            uiMaxSize = uiWidth;
        }
        return SingleHolder.INSTANCE;
    }

    /**
     * Activity每次新建都需要调用
     */
    public void adaptUpdate() {
        genScale(mContext);
        Log.i("ScreenAdapter",
                "scaleScreenToUi=" + scaleScreenToUi + ", old density=" + screenOriginalDensity + ", screen " +
                        "width=" + screenMaxSize + ", height=" + screenMinSize);

        if (scaleScreenToUi > 0) {

            // 计算屏幕相对UI稿的缩放倍数scaleScreenToUi
            Resources resources = mContext.getResources();
            Configuration configuration = resources.getConfiguration();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Log.i("ScreenAdapter",
                    "old density=" + displayMetrics.density + ", " + displayMetrics.densityDpi);

            configuration.densityDpi = (int) (160 * scaleScreenToUi);
            configuration.fontScale = scaleScreenToUi;
            displayMetrics.densityDpi = (int) (160 * scaleScreenToUi);
            displayMetrics.density = scaleScreenToUi;
            displayMetrics.scaledDensity = scaleScreenToUi;
            // 只对本页面有效
            resources.updateConfiguration(configuration, displayMetrics);
            Log.i("ScreenAdapter",
                    "[new] density=" + displayMetrics.density + ", " + displayMetrics.densityDpi);
        }

    }

    /**
     * 生成屏幕与UI稿之间的相对倍数
     *
     * @param context
     */
    private void genScale(Context context) {
        if (scaleScreenToUi <= 0) {
            int width = 0;
            int height = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                width = ScreenUtil.getScreenLogicWidth(context);
                height = ScreenUtil.getScreenLogicHeight(context);
            } else {
                width = ScreenUtil.getScreenWidth(context);
                height = ScreenUtil.getScreenHeight(context);
            }
            if (width <= height) {
                screenMinSize = width;
                screenMaxSize = height;
            } else {
                screenMinSize = height;
                screenMaxSize = width;
            }
            screenOriginalDensity = ScreenUtil.getDensity(context);

            scaleScreenToUi = uiScale * 1.0f * Math.min(
                    screenMaxSize * 1.0f / uiMaxSize
                    , screenMinSize * 1.0f / uiMinSize);
        }
    }
}
