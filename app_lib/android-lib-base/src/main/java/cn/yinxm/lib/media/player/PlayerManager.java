
package cn.yinxm.lib.media.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import cn.yinxm.lib.media.player.constant.MediaFormat;
import cn.yinxm.lib.media.player.constant.PlayerType;
import cn.yinxm.lib.media.player.exo.ExoAudioPlayHelper;
import cn.yinxm.lib.media.player.media.MediaPlayHelper;
import cn.yinxm.lib.media.player.util.MediaUtil;

/**
 * 播放器管理
 * <p>
 * Created by yinxuming on 2018/8/7.
 */
public class PlayerManager {

    private Context mContext;
    /**
     * 播放器实例
     */
    private Map<Integer, IPlayHelper> playerList = new HashMap<>();
    /**
     * 播放器切换策略集合，按先后顺序切换
     */
    private LinkedList<Integer> mPlayerTypeList = new LinkedList();
    /**
     * 特殊格式，需要指定播放器集合
     */
    private Map<String, Integer> playerSupportFormat = new HashMap<>();


    private PlayerManager() {
    }

    public static PlayerManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        private static final PlayerManager INSTANCE = new PlayerManager();
    }


    /**
     * 初始化播放器切换策略，返回默认播放器实
     *
     * @param context
     * @return
     */
    // TODO: 2018/8/9 自定义切换策略 
    public IPlayHelper initDefaultStrategy(@Nullable Context context) {
        mContext = context.getApplicationContext();
        // 1、策略1，根据播放格式来选择指定的播放器
        playerSupportFormat.clear();
        addFormatSupport(MediaFormat.FLAC, PlayerType.MEDIA_PLAYER);
        addFormatSupport(MediaFormat.WMA, PlayerType.MEDIA_PLAYER);

        // 2、策略2，根据播放器优先级来选择指定的播放器
        mPlayerTypeList.clear();
        // 第一条数据为默认播放器
        mPlayerTypeList.add(PlayerType.DEFAULT_EXO);
        // TODO: 2018/8/7  添加其他播放器
//        mPlayerList.add(PlayerType.);
        // 最后一条数据为最后备选播放器
        mPlayerTypeList.addLast(PlayerType.MEDIA_PLAYER);

        // 3、添加播放器实例
        playerList.clear();
        playerList.put(PlayerType.DEFAULT_EXO, new ExoAudioPlayHelper(mContext));
        playerList.put(PlayerType.MEDIA_PLAYER, new MediaPlayHelper());

        return getPlayerInstance(mPlayerTypeList.getFirst());
    }

    private void addFormatSupport(@NonNull @MediaFormat String mediaFormat,
                                  @PlayerType int playerType) {
        playerSupportFormat.put(mediaFormat, playerType);
    }


    /**
     * 首次播放，根据URL选择播放器
     *
     * @param currentPlayer
     * @param playUrl
     * @return
     */
    public IPlayHelper getPlayer(IPlayHelper currentPlayer, String playUrl) {
        return getPlayer(currentPlayer, playUrl, null);
    }


    /**
     * 首次播放，根据URL选择播放器
     *
     * @param currentPlayer
     * @param playUrl
     * @param mediaFormat
     * @return
     */
    public IPlayHelper getPlayer(IPlayHelper currentPlayer, String playUrl,
                                 @MediaFormat String mediaFormat) {
        IPlayHelper playerInstance = null;
        int playerType = PlayerType.NONE;
        if (playUrl == null) {
            return playerInstance;
        }
        playUrl = playUrl.toLowerCase();
        int currentPlayerType = getPlayerType(currentPlayer);
        // 1、根据媒体编码格式，选择默认播放器
        if (mediaFormat == null) {
            String fileType = MediaUtil.getUrlFormat(playUrl);
            if (fileType != null && playerSupportFormat.containsKey(fileType)) {
                int type = playerSupportFormat.get(fileType);
                if (currentPlayerType != type) {
                    playerInstance = getPlayerInstance(type);
                } else {
                    playerInstance = currentPlayer;
                }
            }
        }

        // 2、根据优先级，选择默认播放器
        if (playerInstance == null) {
            playerInstance = getPlayerInstance(mPlayerTypeList.getFirst());
        }
        return playerInstance;
    }

    /**
     * 播放出现异常，切换播放器
     *
     * @param currentPlayer
     * @param playUrl
     * @return
     */
    public IPlayHelper getNextPlayer(IPlayHelper currentPlayer,
                                     String playUrl) {
        IPlayHelper playerInstance = null;
        if (playUrl == null) {
            return playerInstance;
        }
        playUrl = playUrl.toLowerCase();
        int currentPlayerType = getPlayerType(currentPlayer);
        // 1、判断是否根据媒体编码格式选择过播放器
        int supportFormatPlayer = PlayerType.NONE;
        String fileType = MediaUtil.getUrlFormat(playUrl);
        if (fileType != null && playerSupportFormat.containsKey(fileType)) {
            supportFormatPlayer = playerSupportFormat.get(fileType);
        }

        // 2、根据优先级，选择默认播放器
        Iterator<Integer> iterator = mPlayerTypeList.iterator();
        while (iterator.hasNext()) {
            int tempType = iterator.next();
            // 如果当前使用过这个播放器，或者已经使用过支持指定格式的播放器，则继续寻找
            if (tempType <= currentPlayerType || tempType == supportFormatPlayer) {
                continue;
            } else {
                // 以上两种情况都不是，说明找到备选播放器
                playerInstance = getPlayerInstance(tempType);
                break;
            }
        }
        return playerInstance;
    }


    private IPlayHelper getPlayerInstance(@PlayerType int playType) {
        return playerList.get(playType);
    }

    private int getPlayerType(IPlayHelper playHelper) {
        int playType = PlayerType.NONE;
        if (playHelper == null) {
            return playType;
        }

        if (playHelper instanceof ExoAudioPlayHelper) {
            playType = PlayerType.DEFAULT_EXO;
        } else if (playHelper instanceof MediaPlayHelper) {
            playType = PlayerType.MEDIA_PLAYER;
        }

        return playType;
    }

}
