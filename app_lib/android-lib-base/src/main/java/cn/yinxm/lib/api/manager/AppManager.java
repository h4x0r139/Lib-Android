package cn.yinxm.lib.api.manager;

import android.content.Context;

import cn.yinxm.lib.api.IAppAudioManager;
import cn.yinxm.lib.api.IAppConfig;
import cn.yinxm.lib.api.IAudioPlayController;
import cn.yinxm.lib.api.ICallManager;
import cn.yinxm.lib.api.ILocationApi;
import cn.yinxm.lib.api.ILoginInfo;
import cn.yinxm.lib.api.iml.DefaultAppConfigIml;
import cn.yinxm.lib.api.iml.DefaultAudioManagerIml;
import cn.yinxm.lib.api.iml.DefaultAudioPlayControllerIml;
import cn.yinxm.lib.api.iml.DefaultCallManagerIml;
import cn.yinxm.lib.api.iml.DefaultLoginInfoIml;
import cn.yinxm.lib.api.iml.OsNativeLocationApi;

/**
 * Created by yinxm on 2016/8/11.
 */
public  class AppManager {
    private AppManager(){}
    private Context applicationContext;
    private IAppConfig appConfig;
    private IAppAudioManager appAudioManager;
    private IAudioPlayController audioPlayController;
    private ICallManager callManager;
    private ILoginInfo loginInfo;
    private ILocationApi location;


    public static AppManager getInstance() {
        return AppManagerFactory.instance;
    }
    private static class AppManagerFactory{
        private static AppManager instance = new AppManager();
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public IAppConfig getAppConfig() {
        if (appConfig == null) {
            appConfig = new DefaultAppConfigIml();
        }
        return appConfig;
    }

    public  void setAppConfig(IAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public IAppAudioManager getAppAudioManager() {
        if (appAudioManager == null) {
            appAudioManager = new DefaultAudioManagerIml();
        }
        return appAudioManager;
    }
    public void setAppAudioManager(IAppAudioManager appAudioManager) {
        this.appAudioManager = appAudioManager;
    }

    public IAudioPlayController getAudioPlayController() {
        if (audioPlayController == null) {
            audioPlayController = new DefaultAudioPlayControllerIml();
        }
        return audioPlayController;
    }

    public void setAudioPlayController(IAudioPlayController audioPlayController) {
        this.audioPlayController = audioPlayController;
    }

    public ICallManager getCallManager() {
        if (callManager == null) {
            callManager = new DefaultCallManagerIml();
        }
        return callManager;
    }

    public void setCallManager(ICallManager callManager) {
        this.callManager = callManager;
    }

    public ILoginInfo getLoginInfo() {
        if (loginInfo == null) {
            loginInfo = new DefaultLoginInfoIml();
        }
        return loginInfo;
    }
    public void setLoginInfo(ILoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public void setLocation(ILocationApi location) {
        this.location = location;
    }

    public ILocationApi getLocation() {
        if (location == null) {
            location = new OsNativeLocationApi(getApplicationContext());
        }
        return location;
    }
}
