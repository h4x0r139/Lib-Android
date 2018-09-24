package cn.yinxm.lib.screen;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * Created by yinxm on 2016/3/24.
 * 屏幕工具类
 * px《——》dp
 * Screen width、height
 * 状态栏
 * 截图
 *
 * 最小宽度限定符 swdp = 最小宽度px * 160 / 屏幕密度dpi，一般手机720p的，720*160/320=360dp
 *
 */
public class ScreenUtil {
    /**
     * dp 转化为 px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px 转化为 dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getSmallestScreenWidthDp(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        int smallestScreenWidthDp = configuration.smallestScreenWidthDp;
        Log.d("yinxm", "smallestScreenWidthDp="+smallestScreenWidthDp);
        return smallestScreenWidthDp;
    }

//    /**
//     * 获取屏幕宽度高度
//     * @param activity
//     * @return
//     */
//    @Deprecated
//    public static Point getWidthHeight(Activity activity) {
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        return point;
//    }

    /**
     * 获取设备宽度（px）
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取设备高度（px）
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidthNew(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeightNew(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenLogicWidth(Context context) {
        int size = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        size = outPoint.x;
        return size;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenLogicHeight(Context context) {
        int size = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        size = outPoint.y;
        return size;
    }

    /**
     *
     * @param context
     * @return  屏幕密度（像素比例：0.75/1.0/1.5/2.0）
     */
    public static float getDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     *
     * @param context
     * @return  屏幕密度（每寸像素：120/160/240/320）
     */
    public static int getDensityDPI(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    /**
     *  状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        //沉浸状态栏无效
//        Rect rect = new Rect();
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        return rect.top;//应用区域的上边沿
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    /**
     *  标题栏高度
     * @param activity
     * @return
     */
    public static int getTitleBarHeight(Activity activity) {
        View v = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        return v.getTop() - getStatusBarHeight(activity);//标题栏高度 = View绘制区域的上边沿-状态栏高度
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     * 截图后顶部有白色的状态栏，如果不想要，可以通过snapShotWithoutStatusBar获取截图
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShot(Activity activity)
    {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity)
    {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * sp转px.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static int sp2px(float value, Context context) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }

    /**
     * px转sp.
     *
     * @param value   the value
     * @param context the context
     * @return the int
     */
    public static int px2sp(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }
	
	public static String debugScreenInfo(Activity activity) {
        Context context  = activity.getApplicationContext();
        StringBuilder stringBuilder = new StringBuilder();
        Configuration configuration = context.getResources().getConfiguration();
        stringBuilder.append(configuration.toString()).append("\n");
        //获取屏幕分辨率
        Display display = activity.getWindowManager().getDefaultDisplay();
        stringBuilder.append(display.toString()).append("\n");
        stringBuilder.append("height="+getScreenHeight(context)).append(", width=").append(getScreenWidth(context)).append(", statusBarHeight="+getStatusBarHeight(context)).append("\n");
        stringBuilder.append("density="+getDensity(context)).append(", densityDPI="+getDensityDPI(context)).append("\n");

        LogUtil.e(stringBuilder.toString());
        return stringBuilder.toString();
    }
}

