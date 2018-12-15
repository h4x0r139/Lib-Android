package cn.yinxm.lib.api.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 应用Activity管理
 * <p>
 *
 * @author yinxuming
 * @date 2018/8/14
 */
public class AppActivityManager {

    /**
     * 当前正在显示的Activity
     */
    private WeakReference<Activity> currentActivityWeakRef;
    /**
     * 所有活动的Activity
     */
    private List<WeakReference<Activity>> allActivityList = new LinkedList<>();
    /**
     * Activity显示计数器
     */
    private int count = 0;

    private List<OnAppStateCallback> appStateCallbackList = new
            CopyOnWriteArrayList<>();


    private AppActivityManager() {
    }

    public static AppActivityManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static AppActivityManager INSTANCE = new AppActivityManager();
    }


    /**
     * 判断app是否在前台执行
     *
     * @return
     */
    public boolean isAppRunForeground() {
        return count > 0;
    }

    /**
     * 退出应用，销毁所有界面
     *
     * @param isKill 是否杀掉进程
     */
    public void exitApp(boolean isKill) {
        for (WeakReference<Activity> reference : allActivityList) {
            if (reference != null && reference.get() != null && !reference.get().isFinishing()) {
                reference.get().finish();
            }
        }
        synchronized (this) {
            allActivityList.clear();
        }
        if (isKill) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void onActivityStart(Activity activity) {
        boolean isRunForegroundBefore = isAppRunForeground();
        count += 1;
        if (!isRunForegroundBefore && isAppRunForeground()) {
            notifyAppOnStart();
        }
    }

    public void onActivityStopped(Activity activity) {
        boolean isRunForegroundBefore = isAppRunForeground();
        count -= 1;
        if (isRunForegroundBefore && !isAppRunForeground()) {
            notifyAppOnStop();
        }
    }


    /**
     * Activity onCreate时添加Activity管理
     *
     * @param activity
     */
    public void onActivityCreated(Activity activity) {
        if (allActivityList != null && activity != null && !activity.isFinishing()) {
            WeakReference<Activity> reference = new WeakReference<Activity>(activity);
            allActivityList.add(0, reference);
        }
    }

    /**
     * Activity销毁时，移除Activity
     *
     * @param activity
     */
    public void onActivityDestroyed(Activity activity) {
        if (allActivityList != null && activity != null) {
            Iterator<WeakReference<Activity>> iterator = allActivityList.iterator();
            while (iterator != null && iterator.hasNext()) {
                WeakReference<Activity> reference = iterator.next();
                if (reference != null && reference.get() != null && !reference.get().isFinishing()) {
                    Activity activityTemp = reference.get();
                    if (activityTemp == activity) {
                        iterator.remove();
                        break;
                    }
                } else {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 获取所有已创建的Activity
     *
     * @return
     */
    public List<WeakReference<Activity>> getAllActivity() {
        return allActivityList;
    }

    /**
     * Activity显示时调用
     *
     * @param activity
     */
    public void onActivityResume(Activity activity) {
        if (activity != null) {
            currentActivityWeakRef = new WeakReference<Activity>(activity);
        }
    }

    /**
     * 获取当前正在前台显示的Activity
     *
     * @return
     */
    public Activity getCurrentShowingActivity() {
        Activity currentActivity = null;
        if (currentActivityWeakRef != null) {
            currentActivity = currentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void registerAppStateCallback(OnAppStateCallback appStateCallback) {
        if (appStateCallback != null) {
            appStateCallbackList.add(appStateCallback);
        }
    }

    public void unregisterAppStateCallback(OnAppStateCallback appStateCallback) {
        if (appStateCallback != null) {
            Iterator<OnAppStateCallback> iterator = appStateCallbackList.iterator();
            while (iterator.hasNext()) {
                OnAppStateCallback callback = iterator.next();
                if (appStateCallback == callback) {
                    appStateCallbackList.remove(appStateCallback);
                    break;
                }
            }
        }
    }


    public void notifyAppOnStart() {
        Iterator<OnAppStateCallback> iterator = appStateCallbackList.iterator();
        while (iterator != null && iterator.hasNext()) {
            OnAppStateCallback listener = iterator.next();
            if (listener != null) {
                listener.onStart();
            } else {
                iterator.remove();
            }
        }
    }

    public void notifyAppOnStop() {
        Iterator<OnAppStateCallback> iterator = appStateCallbackList.iterator();
        while (iterator != null && iterator.hasNext()) {
            OnAppStateCallback listener = iterator.next();
            if (listener != null) {
                listener.onStop();
            } else {
                iterator.remove();
            }
        }
    }

    public static class OnAppStateCallback {
        /**
         * app开始出现
         */
        public void onStart() {
        }

        /**
         * app退到后台
         */
        public void onStop() {
        }
    }
}
