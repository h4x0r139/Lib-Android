package cn.yinxm.lib.api;

/**
 * Created by yinxm on 2016/11/20.
 * 功能: 音乐播放Service控制
 */

public interface IAudioPlayController {

    public boolean isPlaying();

    public void play();

    public void pausePlay();

    //瞬时中断，程序自动中断操作
    public void pauseEventPlay();

    public void continuePlay();

    public void playPauseContinue();

    public void playNext();

    public void playPrevious();

    public void openAudioPlayingActivity();//打开播放界面

    //播放音频服务
    public IAudioPlayService getAudioPlayService();

    public void startAudioPlayService();

    //通知
    public IMusicNotification getMusicNotification();
}
