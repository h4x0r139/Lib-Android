package cn.yinxm.lib.screen;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;


import java.lang.reflect.Field;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * 车机UI稿一般会设计成1:1的，在使用UI稿标注的时候rd也一般用的mdpi（uiScale=1），可以使用此工具适配车机、手机
 *  注意ScreenAdapter调用时机
 *  1、Application中：
 *  attachBaseContext
 *  setUiDesign
 *  <p>
 *  2、Activity onCreate方法中调用，可以在全局注册Activity监听器，去调用
 *  adaptUpdate
 *  <p>
 *  3、Dialog、PopupWindow中需要调用
 * <p>
 * Created by yinxuming on 2018/7/31.
 */
public class ScreenAdapter {
    private static final String TAG = "ScreenAdapter";

    private Application mApplication;

    private int uiMaxSize;
    private int uiMinSize;
    private float uiScale;

    private int screenMaxSize;
    private int screenMinSize;

    private float originalDensity;
    private float originalScaledDensity;

    private float scaleScreenToUi;
    private float scaledChangeDensity;

    private ScreenAdapter() {
    }
    private static class SingleHolder {

        private static final ScreenAdapter INSTANCE = new ScreenAdapter();
    }

    public static ScreenAdapter getInstance() {
        return SingleHolder.INSTANCE;
    }


    public void attachBaseContext(Context base) {
        try {

            DisplayMetrics displayMetrics = base.getResources().getDisplayMetrics();
            originalDensity = displayMetrics.density;
            originalScaledDensity = scaledChangeDensity = displayMetrics.scaledDensity;
            Log.d(TAG, "attachBaseContext=" + base + "， res=" + base.getResources() + ", dm=" + displayMetrics);

            Resources resources = base.getResources();
//            private ResourcesImpl mResourcesImpl;
            Field fieldResourcesImpl = resources.getClass().getDeclaredField("mResourcesImpl");
            fieldResourcesImpl.setAccessible(true);
            Object objResourcesImpl = fieldResourcesImpl.get(resources);

//            android.content.res.ResourcesImpl
//            private final DisplayMetrics mMetrics = new DisplayMetrics();
            Field fieldDisplayMetrics = objResourcesImpl.getClass().getDeclaredField("mMetrics");
            fieldDisplayMetrics.setAccessible(true);

            displayMetrics = (DisplayMetrics) fieldDisplayMetrics.get(objResourcesImpl);
            CustomDisplayMetrics customDisplayMetrics = new CustomDisplayMetrics();
            customDisplayMetrics.setTo(displayMetrics);
            fieldDisplayMetrics.set(objResourcesImpl, customDisplayMetrics);

            Log.d(TAG, "after attachBaseContext getDisplayMetrics=" + base.getResources().getDisplayMetrics());
        } catch (Exception e) {
            LogUtil.e(e);
        }

    }

    /**
     * 全局调用一次
     * 设置UI稿设计尺寸，scale为标注图采用的倍数，例如mdpi图标注的为1倍图，xhdpi图标注的为2倍图...
     *
     * @param uiWidth  UI稿宽
     * @param uiHeight UI稿高
     * @param uiScale  标注或者切图采用的UI稿倍数
     */
    public ScreenAdapter setUiDesign(Application application, int uiWidth, int uiHeight, float uiScale) {
        if (uiWidth <= 0 || uiHeight <= 0 || uiScale <= 0) {
            throw new RuntimeException(
                    new IllegalArgumentException("illegal argument  "
                            + "uiWidth=" + uiWidth + "， uiHeight="
                            + uiHeight + ", scale=" + uiScale));
        }
        mApplication = application;
        this.uiScale = uiScale;

        if (uiWidth <= uiHeight) {
            uiMinSize = uiWidth;
            uiMaxSize = uiHeight;
        } else {
            uiMinSize = uiHeight;
            uiMaxSize = uiWidth;
        }

        mApplication.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                if (newConfig != null && newConfig.fontScale > 0) {
                    scaledChangeDensity = mApplication.getResources().getDisplayMetrics().scaledDensity;
                    Log.i(TAG, "onConfigurationChanged scaledChangeDensity=" + scaledChangeDensity + ", " + scaleScreenToUi + ", " + originalScaledDensity + "， " + newConfig.fontScale + ", newConfig.densityDpi=" + newConfig.densityDpi + ", res=" + mApplication.getResources() + ", dm=" + mApplication.getResources().getDisplayMetrics() + "， mApplication=" + mApplication.getResources().getDisplayMetrics().density);

                    float targetScaledDensity = scaleScreenToUi * scaledChangeDensity / originalDensity;
                    updateApplicationDensity(mApplication, scaleScreenToUi, targetScaledDensity);
                }
            }

            @Override
            public void onLowMemory() {

            }
        });

        // application update
        genScale(mApplication.getApplicationContext());
        if (scaleScreenToUi > 0) {
            float targetScaledDensity = scaleScreenToUi * scaledChangeDensity / originalDensity;
            updateApplicationDensity(application, scaleScreenToUi, targetScaledDensity);
        }

        return SingleHolder.INSTANCE;
    }

    /**
     * Activity每次新建都需要调用
     *
     * @param context 传Activity或其他组件的this，不要传getApplicationContext
     */
    public void adaptUpdate(Context context) {
//        genScale(activity);
        if (scaleScreenToUi > 0) {

            float targetScaledDensity = scaleScreenToUi * scaledChangeDensity / originalDensity;

            // this context update
            DisplayMetrics contextDisplayMetrics = context.getResources().getDisplayMetrics();
            contextDisplayMetrics.density = scaleScreenToUi;
            contextDisplayMetrics.densityDpi = (int) (160 * scaleScreenToUi);
            contextDisplayMetrics.scaledDensity = targetScaledDensity;

            // configuration update
            Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.densityDpi = 0;
            configuration.fontScale = scaleScreenToUi * scaledChangeDensity / originalDensity;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context.createConfigurationContext(configuration);
            } else {
                resources.updateConfiguration(configuration, contextDisplayMetrics);
            }

            // application update
            updateApplicationDensity(mApplication, scaleScreenToUi, targetScaledDensity);
        }

    }

    private void updateApplicationDensity(Application application, float targetDensity, float targetScaledDensity) {
        try {
//            Application中resource是同一个对象
            Resources resources = application.getResources();
//            private ResourcesImpl mResourcesImpl;
            Field fieldResourcesImpl = resources.getClass().getDeclaredField("mResourcesImpl");
            fieldResourcesImpl.setAccessible(true);
            Object objResourcesImpl = fieldResourcesImpl.get(resources);

//            android.content.res.ResourcesImpl
//            private final DisplayMetrics mMetrics = new DisplayMetrics();
            Field fieldDisplayMetrics = objResourcesImpl.getClass().getDeclaredField("mMetrics");
            fieldDisplayMetrics.setAccessible(true);

            DisplayMetrics displayMetrics = (DisplayMetrics) fieldDisplayMetrics.get(objResourcesImpl);
            if (!(displayMetrics instanceof CustomDisplayMetrics)) {
                CustomDisplayMetrics customDisplayMetrics = new CustomDisplayMetrics();
                customDisplayMetrics.setAdaptDensity(targetDensity);
                customDisplayMetrics.setAdaptScaledDensity(targetScaledDensity);
                customDisplayMetrics.setTo(displayMetrics);
                fieldDisplayMetrics.set(objResourcesImpl, customDisplayMetrics);
            } else {
                CustomDisplayMetrics metrics = (CustomDisplayMetrics) displayMetrics;
                metrics.setAdaptDensity(targetDensity);
                metrics.setAdaptScaledDensity(targetScaledDensity);
                metrics.updateDensity();
            }
            LogUtil.d(TAG, "updateApplicationDensity=" + mApplication.getResources().getDisplayMetrics());
        } catch (Exception e) {
            LogUtil.e(TAG, e);
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

            scaleScreenToUi = uiScale * 1.0f * Math.min(
                    screenMaxSize * 1.0f / uiMaxSize,
                    screenMinSize * 1.0f / uiMinSize);
        }
        Log.i(TAG, "scaleScreenToUi=" + scaleScreenToUi + ", old density=" + originalDensity + ", screen width=" + screenMaxSize + ", height=" + screenMinSize + ", scaledChangeDensity=" + scaledChangeDensity);
    }

    public float getScaleScreenToUi() {
        return scaleScreenToUi;
    }

    public float getScaledChangeDensity() {
        return scaledChangeDensity;
    }


    /**
     * 主要用于解决ford车机屏幕放大问题
     */
    class CustomDisplayMetrics extends DisplayMetrics {
        private static final String TAG = "CustomDisplayMetrics";

        private float adaptDensity;
        private float adaptScaledDensity;

        public CustomDisplayMetrics() {
            super();
            density = ScreenAdapter.getInstance().getScaleScreenToUi();
            densityDpi = (int) (160 * density);
            scaledDensity = ScreenAdapter.getInstance().getScaledChangeDensity();
        }

        public void setAdaptDensity(float adaptDensity) {
            if (adaptDensity > 0) {
                this.adaptDensity = adaptDensity;
            }
        }


        public void setAdaptScaledDensity(float adaptScaledDensity) {
            if (adaptScaledDensity > 0) {
                this.adaptScaledDensity = adaptScaledDensity;
            }
        }

        public float getAdaptDensity() {
            return adaptDensity;
        }

        public void updateDensity() {
            if (adaptDensity > 0) {
                density = adaptDensity;
                densityDpi = (int) (160 * density);
                scaledDensity = adaptScaledDensity;
            }
        }

        @Override
        public void setTo(DisplayMetrics o) {
            super.setTo(o);
            updateDensity();
            LogUtil.d(TAG, "after setTo density=" + density + "，" + this);
        }

        @Override
        public void setToDefaults() {
            super.setToDefaults();
            updateDensity();
            LogUtil.d(TAG, "after setToDefaults density=" + density + "，" + this);
        }

        @Override
        public String toString() {
            return super.toString() + " CustomDisplayMetrics " + densityDpi;
        }
    }

}
