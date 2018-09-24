package cn.yinxm.cache;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * 缓存管理器
 * <p>
 *
 * @author yinxuming
 * @date 2018/9/17
 */
public class AppCacheManager {
    private static final String TAG = "AppCacheManager";

    private Context mContext;
    private ICache mCache;

    public static AppCacheManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static AppCacheManager INSTANCE = new AppCacheManager();
    }

    public void init(Context context, ICache cache) {
        mContext = context.getApplicationContext();
        mCache = cache;
    }

    private boolean isInit() {
        return mCache != null;
    }

    public ICache getCache() {
        return mCache;
    }

    public void setCache(ICache cache) {
        mCache = cache;
    }

    /**
     * 缓存数据
     *
     * @param key
     * @param value
     * @return
     */
    public ICache saveCache(String key, Serializable value) {
        try {
            if (!TextUtils.isEmpty(key) && isInit()) {
                return mCache.saveCache(key, value);
            }
        } catch (Exception e) {
            LogUtil.e("putCache异常", e);
        }
        return null;
    }

    /**
     * 获取缓存对象
     *
     * @param key
     * @return
     */
    public Serializable loadCache(String key) {
        Serializable obj = null;
        try {
            if (!TextUtils.isEmpty(key) && isInit()) {
                obj = mCache.loadCache(key);
            }
        } catch (Exception e) {
            LogUtil.e("putCache异常", e);
        }
        return obj;
    }


    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            cachePath = context.getExternalCacheDir().getPath();
//        } else {
            cachePath = context.getCacheDir().getPath();
//        }
        return new File(cachePath + File.separator + uniqueName);
    }



}
