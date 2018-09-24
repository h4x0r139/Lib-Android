package cn.yinxm.lib.cache;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yinxm on 2017/3/3.
 * 功能:Cache任务
 */

public interface ICacheTask {
    /**
     * 是否有缓存
     * @return
     */
    boolean isHaveCache();

    /**
     * 取出缓存值
     * @return
     */
    Serializable getCache();


    /**
     * 取出缓存值，并删除原有缓存，更新缓存
     * @param isAutoUpdate  取出数据后，是否自动更新缓存
     * @param params 原接口请求参数
     * @param callback  缓存更新完毕回调
     * @return
     */
    Serializable takeCache(boolean isAutoUpdate, Map<String, Object> params, ICacheCallback callback);

    /**
     * 异步更新缓存
     * @param params  调用接口需要请求的参数
     * @param callback  缓存更新完毕回调
     * @return 更新请求是否提交成功：如果现在正在更新，再重复提交更新请求，将会返回false；没有网络时也会返回false
     */
    boolean updateCacheAsync(Map<String, Object> params, ICacheCallback callback);

}
