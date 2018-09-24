package cn.yinxm.lib.media.player.constant;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static cn.yinxm.lib.media.player.constant.MediaFormat.FLAC;
import static cn.yinxm.lib.media.player.constant.MediaFormat.M4A;
import static cn.yinxm.lib.media.player.constant.MediaFormat.MP3;
import static cn.yinxm.lib.media.player.constant.MediaFormat.MP4;
import static cn.yinxm.lib.media.player.constant.MediaFormat.PCM;
import static cn.yinxm.lib.media.player.constant.MediaFormat.WAV;
import static cn.yinxm.lib.media.player.constant.MediaFormat.WMA;


/**
 * 常用多媒体编码格式
 * <p>
 * Created by yinxuming on 2018/8/7.
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({MP3, MP4, M4A, WMA, FLAC, WAV, PCM})
public @interface MediaFormat {
    String MP3 = "mp3";
    String MP4 = "mp4";

    String M4A = "m4a";
    String WMA = "wma";
    String FLAC = "flac";

    String WAV = "wav";
    String PCM = "pcm";
}
