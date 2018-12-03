package cn.yinxm.lib.api.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private List<WeakReference<Activity>> allActivityList = new ArrayList<>();
    /**
     * Activity显示计数器
     */
    private int count = 0;

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
        if (count > 0) {
            return true;
        } else {
            return false;
        }
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
        count += 1;
    }

    public void onActivityStopped(Activity activity) {
        count -= 1;
    }


    /**
     * Activity onCreate时添加Activity管理
     *
     * @param activity
     */
    public void onActivityCreated(Activity activity) {
        if (allActivityList != null && activity != null && !activity.isFinishing()) {
            WeakReference<Activity> reference = new WeakReference<Activity>(activity);
            allActivityList.add(reference);
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
}
