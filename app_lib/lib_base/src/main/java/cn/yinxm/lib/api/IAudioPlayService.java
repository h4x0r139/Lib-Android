package cn.yinxm.lib.api;

import java.util.List;

/**
 * Created by yinxm on 2016/12/14.
 * 功能: Service 对外暴露接口
 */

public interface IAudioPlayService<T> {

    /**
     * 是否获取到焦点
     * @return
     */
    boolean isHaveAudioFocus();

    /**
     * 是否正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 是否暂停
     * @return
     */
    boolean isPaused();

    /**
     * 播放模式
     */
    int getPlayMode();

    /**
     * 获取播放positon
     * @return
     */
    int getPositionPlaying();

    /**
     * 获取播放列表大小
     * @return
     */
    int getPlayListSize();

    /**
     * 获取播放列表信息
     * @return
     */
    List<T> getPlayList();

    /**
     * 获取正在播放的实体信息
     * @return
     */
    T getPlayingAudioBean();

    /**
     * 获取正在播放url
     * @return
     */
    String getPlayUrl();

    /**
     * 切换播放器
     * @param currentPlayerType 当前播放器类型
     */
    void changeAudioPlayer(PlayerType currentPlayerType);

    /**
     * 获取最后一次播放的开始位置 ms
     * @param playUrl 将要播放的地址
     * @return 返回非负数为有效值
     */
    long getLastPlayHistorySeek(String playUrl);

    /**
     * 播放器类型
     */
    enum PlayerType {
        EXO(1),
        IJK(2),
        Media(3),
        AiTing(4);

        int type;

        PlayerType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
