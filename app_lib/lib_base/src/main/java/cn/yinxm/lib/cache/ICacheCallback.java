package cn.yinxm.lib.cache;

import java.io.Serializable;

/**
 * Created by yinxm on 2017/3/3.
 * 功能:
 */

public interface ICacheCallback {
    int CODE_SUCCESS = 1;// 操作成功
    int CODE_FAIL = 2;// 操作失败

    /**
     * 更新缓存完毕回调
     * @param code 更新状态
     * @param object 更新后的缓存结果
     */
    void cacheFinish(int code, Serializable object);
}
