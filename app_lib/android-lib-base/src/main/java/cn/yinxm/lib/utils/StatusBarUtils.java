package cn.yinxm.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * 沉浸状态栏工具类
 * <p>
 *
 * @author yinxuming
 * @date 2019-10-16
 */
public class StatusBarUtils {

    /**
     * 沉浸状态栏
     *
     * @param mContext
     */
    public static void fullScreenImmersive(Context mContext) {
        if (mContext != null && mContext instanceof Activity) {
            Window window = ((Activity) mContext).getWindow();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                StatusBarUtils.setStatusBarTranslucent(window, 0);
                StatusBarUtils.setStatusBar(window, false, false, true);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                StatusBarUtils.setActionBarAndNavKeyVisibility(mContext, false);
            }
        }
    }

    public static void fullScreenImmersive(Window window) {
        if (window != null) {
            fullScreenImmersive(window.getDecorView());
        }
    }

    public static void fullScreenImmersive(View view) {
        if (view == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        } else {
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
            );
        }
    }

    private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;

    private static final int STATUS_BAR_BACKGROUND_COLOR_GRAY = 0xFFCECECE;

    public static void setStatusBarLightMode(Context context, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && window != null) {
            setStatusBarTranslucent(window, 0);
            setStatusBar(window, true, true, false);
        }
    }

    public static void setStatusBarTranslucent(Window window, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0及以上
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if ((color & 0xFF000000) == 0) {
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if ((color ^ 0xFFFFFFFF) == 0) {
                if (isSupportWhiteBackground()) {
                    window.setStatusBarColor(color);
                } else {
                    window.setStatusBarColor(STATUS_BAR_BACKGROUND_COLOR_GRAY);
                }
            } else {
                window.setStatusBarColor(color);
            }
        } else {
            if ((color & 0xFF000000) == 0) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    public static void setStatusBar(Window window, boolean show, int color, boolean fullscreen) {
        setStatusBarTranslucent(window, color);
        boolean light = getBright(color) * 2 > 255;
        setStatusBar(window, show, light, fullscreen);
    }

    public static void setStatusBar(Window window, boolean show, int color, boolean light, boolean fullscreen) {
        setStatusBarTranslucent(window, color);
        setStatusBar(window, show, light, fullscreen);
    }

    public static void setStatusBar(Window window, boolean show, boolean light, boolean fullscreen) {
        Context context = window.getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (show) {
                int opt = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                if (fullscreen) {
                    opt |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                } else {
                    opt |= View.SYSTEM_UI_FLAG_VISIBLE;
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

                if (light) {
                    opt |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    opt &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }

                if (DeviceUtils.getDeviceType() == DeviceUtils.DEVICE_XIAOMI) {
                    setMIUIStatusBarLightMode(window, light);
                } else if (DeviceUtils.getDeviceType() == DeviceUtils.DEVICE_MEIZU) {
                    setFlymeStatusBarLightMode(window, light);
                } else if (DeviceUtils.getDeviceType() == DeviceUtils.DEVICE_OPPO) {
                    opt |= getOppoLightStatusBarIcon(window, light, opt);
                }

                window.getDecorView().setSystemUiVisibility(opt);
            } else {
                int opt = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                if (fullscreen) {
                    opt |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                }

                window.getDecorView().setSystemUiVisibility(opt);
            }
        } else {
            if (fullscreen) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            setActionBarAndNavKeyVisibility(context, show);
        }
    }

    public static void setActionBarAndNavKeyVisibility(Context context, boolean show) {
        if (show) {
            showSupportActionBar(context, true, true);
            if (context instanceof Activity) {
                showNavKey((Activity) context, View.SYSTEM_UI_FLAG_VISIBLE);
            }
        } else {
            hideSupportActionBar(context, true, true);
            if (context instanceof Activity) {
                hideNavKey((Activity) context);
            }
        }
    }

    public static void fitSystemWindows(Window window, boolean fitSystemWindows) {
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            mChildView.setFitsSystemWindows(fitSystemWindows);
            mChildView.requestLayout();
        }
    }

    private static boolean setMIUIStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    // 状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    // 清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
                result = true;
            } catch (Exception e) {
                // do nothing
            }
        }
        return result;
    }


    private static boolean setFlymeStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                // do nothing
            }
        }
        return result;
    }

    public static int getBright(int color) {
        return (int) (0.299 * ((color | 0xff00ffff) >> 16 & 0x00ff) + 0.587 * ((color | 0xffff00ff) >> 8 & 0x0000ff) + 0.114 * ((color | 0xffffff00) & 0x0000ff));
    }

    public static void hideSupportActionBar(Context context, boolean actionBar, boolean statusBar) {
        if (actionBar) {
            AppCompatActivity appCompatActivity = getAppCompActivity(context);
            if (appCompatActivity != null) {
                ActionBar ab = appCompatActivity.getSupportActionBar();
                if (ab != null) {
                    ab.setShowHideAnimationEnabled(false);
                    ab.hide();
                }
            }
        }
        //        if (statusBar) {
        //            if (context instanceof FragmentActivity) {
        //                FragmentActivity fragmentActivity = (FragmentActivity) context;
        //                fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //            } else {
        //                CommonUtil.getAppCompActivity(context).getWindow().setFlags(WindowManager.LayoutParams
        // .FLAG_FULLSCREEN,
        //                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //            }
        //        }
    }

    public static void showSupportActionBar(Context context, boolean actionBar, boolean statusBar) {
        if (actionBar) {
            AppCompatActivity appCompatActivity = getAppCompActivity(context);
            if (appCompatActivity != null) {
                ActionBar ab = appCompatActivity.getSupportActionBar();
                if (ab != null) {
                    ab.setShowHideAnimationEnabled(false);
                    ab.show();
                }
            }
        }

        //        if (statusBar) {
        //            if (context instanceof FragmentActivity) {
        //                FragmentActivity fragmentActivity = (FragmentActivity) context;
        //                fragmentActivity.getWindow()
        //                        .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //            } else {
        //                CommonUtil.getAppCompActivity(context).getWindow()
        //                        .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //            }
        //        }
    }

    /**
     * Get AppCompatActivity from context
     *
     * @param context
     * @return AppCompatActivity if it's not null
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static void showNavKey(Activity activity, int systemUiVisibility) {
        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    public static void hideNavKey(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //       设置屏幕始终在前面，不然点击鼠标，重新出现虚拟按键
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
                    // bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
            );
        }
    }

    public static boolean isSupportWhiteBackground() {
        if (((DeviceUtils.getDeviceType() == DeviceUtils.DEVICE_VIVO) || (DeviceUtils.getDeviceType() == DeviceUtils.DEVICE_SAMSUNG)) && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        return true;
    }


    public static int getOppoLightStatusBarIcon(Window window, boolean lightMode, int vis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (lightMode) {
                vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            } else {
                vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            }
        }
        return vis;
    }

}
