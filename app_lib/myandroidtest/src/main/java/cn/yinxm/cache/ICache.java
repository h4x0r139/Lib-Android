package cn.yinxm.cache;

import java.io.Serializable;

/**
 * <p>
 *
 * @author yinxuming
 * @date 2018/9/20
 */
public interface ICache {
    /**
     * 将缓存写入磁盘，onPause调用
     * @return
     */
    ICache flush();

    /**
     * 写入缓存
     * @param key
     * @param serializable
     * @return
     */
    ICache saveCache(String key, Serializable serializable);

    /**
     * 读取缓存
     * @param key
     * @return
     */
    Serializable loadCache(String key);

    /**
     * 删除缓存
     * @param key
     * @return
     */
    ICache removeCache(String key);

    /**
     * 清空所有缓存
     * @return
     */
    ICache clearAllCache();

}
