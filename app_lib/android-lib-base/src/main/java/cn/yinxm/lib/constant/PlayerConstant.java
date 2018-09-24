package cn.yinxm.lib.constant;

/**
 * 
 * @Description 播放歌曲的常量类
 * @author ganxin
 * @date 2014-12-10
 * @email ganxinvip@163.com
 */
public class PlayerConstant {

	public static int MPOSTION=0;
	public static  boolean isPlaying=false;

	public static final int STATE_WAIT = 0;// 等待状态
	public static final int STATE_PLAY = 1;// 播放状态
	public static final int STATE_PAUSE = 2;// 暂停状态
	public static final int STATE_STOP = 3;// 停止状态
	public static final int STATE_CONTINUE = 4;// 继续播放状态
	public static final int STATE_PAUSE_EVENT = 5;// 被打断暂停播放

	public static final int MODE_RANDOM = 0;// 随机播放
	public static final int MODE_SINGLE = 1;// 单曲循环
	public static final int MODE_ORDER = 2; // 顺序播放
	public static final int MODE_LOOP = 3;  // 循环播放，列表循环

	public static final int MSG_CHANGE_STATE = 0;
	public static final int MSG_CHANGE_PROGRESS = 1;
	public static final int MSG_CHANGE_MODE = 2;
	public static final int MSG_CHANGE_AUDIO = 3;
	public static final int MSG_CHANGE_PLAY_TYPE = 4;
	public static final int MSG_RECOMMEND_AUDIO_LIST_OVER = 5;//智能广播当前播放列表结束
	public static final int MSG_CHANGE_PLAY_LIST = 6;//播放列表改变监听
	public static final int MSG_CHANGE_BUFFER_PROGRESS = 7; // 缓冲进度改变监听
	public static final int MSG_PLAYING_UPDATE = 0xff3212;  // 监听播放状态的变更（列表，位置，状态）
	public static final int MSG_PLAY_ERROR = 21;// 播放失败
	public static final int MSG_PLAY_COMPLETE = 22;//播放完成
	public static final int MSG_VOLUME_DOWN = 31;//降低音量
	public static final int MSG_VOLUME_UP = 32;//增大音量
	public static final int MSG_VOLUME_RESET = 33;//恢复音量

	//传参数
	public static final String IS_SAME_ALBUM = "isSameAlbum";//是否是同一专辑
	public static final String IS_SAME_AUDIO = "isSameAudio";//是否是同一音频
	public static final String IS_OPEN_PLAY_DETAIL = "openPlayDetail";//是否打开播放详情页
	public static final String IS_PLAY_LIST_CHANGE = "isPlayListChange";//判断是否是播放列表改变
	public static final String POSITION_PLAY = "positionPlay";
	public static final  String IS_KUWO = "isKuwo";
	public static final  String KEY_PLAY_TYPE = "playType";
	public static final  String PLAY_ONE_AUDIO_QI = "oneQi";//点击搜索结果中的某一期节目
	public static final  String CHANGE_STATE_BY_WHO = "auto_next";//当前播放完毕自动下一首

	public static final int STATE_CHAGE_DELAY_TIME = 1500;//当播放歌曲时，系统占用语音时，音乐停止，再点击播放时多长时间可置为暂停状态。
	public enum WHO_CHANGE_STATE {//谁改变了播放状态
		PLAYER_COMPLETE_SELF("1"), //播放器自己播放完毕改变
		USER("2"),
		OTHER("3");
		private String who;

		WHO_CHANGE_STATE(String who) {
			this.who = who;
		}

		public String getWho() {
			return who;
		}
	}

	//菜单播放类型：
	public enum PlayType {
		DEFAULT(-1),
		@Deprecated
		SEARCH(0),//搜索
		RECOMMEND(1),//智能推荐
		COLLECT(2),//收藏
		HISTORY(3),//播放历史
		RADAR(4),//雷达
		RADIO(5),//传统广播
		RECOMMEND_CATEGROY(6),//分类推荐
		RECOMMEND_ALBUM(7);//专辑推荐

		int type;
		PlayType(int i) {
			this.type = i;
		}
		public int getType() {
			return type;
		}

		public static PlayType getType(int num) {//由int构造枚举对象
			PlayType playType = DEFAULT;
			switch (num) {
				case 0:
					playType = SEARCH;
					break;
				case 1:
					playType = RECOMMEND;
					break;
				case 2:
					playType = COLLECT;
					break;
				case 3:
					playType = HISTORY;
					break;
				case 4:
					playType = RADAR;
					break;
				case 5:
					playType = RADIO;
					break;
				case 6:
					playType = RECOMMEND_CATEGROY;
					break;
				case 7:
					playType = RECOMMEND_ALBUM;
					break;
				default:
					playType = DEFAULT;

			}
			return playType;
		}
	}

	/**
	 * 校验播放模式数据
	 * @param mode
	 * @return
	 */
	public static boolean isPlayModeOk(int mode ) {
		if (mode == PlayerConstant.MODE_RANDOM
				|| mode == PlayerConstant.MODE_SINGLE
				|| mode == PlayerConstant.MODE_ORDER
				|| mode == PlayerConstant.MODE_LOOP) {
			return true;
		}else {
			return false;
		}
	}

}
