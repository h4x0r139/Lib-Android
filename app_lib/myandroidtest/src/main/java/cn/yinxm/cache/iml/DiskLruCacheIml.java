package cn.yinxm.cache.iml;

import android.text.TextUtils;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import cn.yinxm.cache.ICache;
import cn.yinxm.lib.utils.log.LogUtil;
import cn.yinxm.lib.utils.sign.util.MD5Util;


/**
 * <p>
 *
 * @author yinxuming
 * @date 2018/9/20
 */
public class DiskLruCacheIml implements ICache {
    DiskLruCache mDiskLruCache;

    public DiskLruCacheIml(File directory, int appVersion, int valueCount, long maxSize) throws IOException {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        mDiskLruCache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);

    }

    @Override
    public ICache flush() {
        try {
            mDiskLruCache.flush();
        } catch (IOException e) {
            LogUtil.e(e);
        }
        return this;
    }

    @Override
    public ICache saveCache(String key, Serializable serializable) {
        try {
            String cacheKey = getCacheKey(key);
            if (TextUtils.isEmpty(cacheKey)) {
                LogUtil.e("cache key can not be null");
                return this;
            }
            DiskLruCache.Editor editor = mDiskLruCache.edit(cacheKey);
            OutputStream os = editor.newOutputStream(0);

            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(serializable);
            outputStream.close();
            editor.commit();
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return this;
    }

    @Override
    public Serializable loadCache(String key) {
        Serializable obj = null;
        try {
            String cacheKey = getCacheKey(key);
            if (TextUtils.isEmpty(cacheKey)) {
                LogUtil.e("cache key can not be null");
                return null;
            }
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(cacheKey);
            if (snapshot != null ){
                InputStream in = snapshot.getInputStream(0);
                ObjectInputStream ois = new ObjectInputStream(in);
                obj = (Serializable) ois.readObject();
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }

        return obj;

    }

    @Override
    public ICache removeCache(String key) {
        try {
            mDiskLruCache.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }


    @Override
    public ICache clearAllCache() {
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            LogUtil.e(e);
        }
        return this;
    }

    private String getCacheKey(String key) {
        String cacheKey = null;
        try {
            cacheKey = MD5Util.getMD5(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(cacheKey)) {
            return cacheKey.toLowerCase();
        }
        return null;
    }
}
