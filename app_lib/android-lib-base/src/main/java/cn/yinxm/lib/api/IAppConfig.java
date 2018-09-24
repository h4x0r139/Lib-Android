package cn.yinxm.lib.api;

/**
 * app全局配置
 */
public interface IAppConfig {
    /**
     * 用户id
     * @return
     */
    String getUserId();

    /**
     * 是否开启log
     * @return
     */
    boolean isLogEnabled();

    /**
     * 设备唯一标识
     * @return
     */
    String getDeviceId();

    /**
     * 获取应用渠道标识
     * @return
     */
    String getAppChannelId();
}
