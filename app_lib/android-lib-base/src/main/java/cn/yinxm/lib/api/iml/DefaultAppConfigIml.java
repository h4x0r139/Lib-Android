package cn.yinxm.lib.api.iml;

import cn.yinxm.lib.api.IAppConfig;
import cn.yinxm.lib.utils.log.LogFileUtil;
import cn.yinxm.lib.utils.log.LogUtil;

/**
 * Created by yinxm on 2016/8/11.
 */
public class DefaultAppConfigIml implements IAppConfig {
    String userId;
    boolean isLogEnabled = false;
    String deviceId = "";
    String appChannelId;

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean isLogEnabled() {
        return isLogEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        isLogEnabled = logEnabled;
        LogUtil.setLogEnabled(logEnabled);
        LogFileUtil.setLog2File(logEnabled);
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String getAppChannelId() {
        return appChannelId;
    }

    public void setAppChannelId(String appChannelId) {
        this.appChannelId = appChannelId;
    }
}
