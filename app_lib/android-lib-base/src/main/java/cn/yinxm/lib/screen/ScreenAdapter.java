package cn.yinxm.lib.screen;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;


/**
 * 车机UI稿一般会设计成1:1的，在使用UI稿标注的时候rd也一般用的mdpi（uiScale=1），可以使用此工具适配车机、手机
 * <p>
 * 注意ScreenAdapter调用时机
 * 1、Application中：
 * setUiDesign
 * adaptUpdate 如果不立即调用，会出现首页放大，8.0以上系统
 * <p>
 * 2、Activity onCreate方法中调用，可以在全局注册Activity监听器，去调用
 * adaptUpdate
 * <p>
 * 3、Dialog、PopupWindow中需要调用
 *
 * <p>
 * Created by yinxuming on 2018/7/31.
 */
public class ScreenAdapter {

    private int uiMaxSize;
    private int uiMinSize;
    private float uiScale;

    private int screenMaxSize;
    private int screenMinSize;
    private float screenOriginalDensity;
    private float scaleScreenToUi;

    private ScreenAdapter() {
    }

    private static class SingleHolder {

        private static final ScreenAdapter INSTANCE = new ScreenAdapter();
    }

    public static ScreenAdapter getInstance() {
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
    public ScreenAdapter setUiDesign(int uiWidth, int uiHeight, float uiScale) {
        if (uiWidth <= 0 || uiHeight <= 0 || uiScale <= 0) {
            throw new RuntimeException(
                    new IllegalArgumentException("illegal argument  "
                            + "uiWidth=" + uiWidth + "， uiHeight="
                            + uiHeight + ", scale=" + uiScale));
        }
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
     * @param context 传Activity或其他组件的this，不要传getApplicationContext
     */
    public void adaptUpdate(Context context) {
        genScale(context);
        Log.i("ScreenAdapter",
                "scaleScreenToUi=" + scaleScreenToUi + ", old density=" + screenOriginalDensity
                        + ", screen width=" + screenMaxSize + ", height=" + screenMinSize);

        if (scaleScreenToUi > 0) {

            // 计算屏幕相对UI稿的缩放倍数scaleScreenToUi
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Log.i("ScreenAdapter",
                    "old density=" + displayMetrics.density + ", " + displayMetrics.densityDpi);

            configuration.densityDpi = (int) (160 * scaleScreenToUi);
//            configuration.fontScale = scaleScreenToUi;
            displayMetrics.densityDpi = (int) (160 * scaleScreenToUi);
            displayMetrics.density = scaleScreenToUi;
            displayMetrics.scaledDensity = scaleScreenToUi;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context.createConfigurationContext(configuration);
            } else {
                resources.updateConfiguration(configuration, displayMetrics);
            }

            // 只对本页面有效
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
                    screenMaxSize * 1.0f / uiMaxSize,
                    screenMinSize * 1.0f / uiMinSize);
        }
    }
}