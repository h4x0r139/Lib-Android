/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package cn.yinxm.lib.media.player.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *
 * @author yinxuming
 * @date 2018/8/7
 */
public class MediaUtil {

    public static String getNetUrlFormat(String playUrl) {
        String type = null;
        BufferedInputStream bis = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(playUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            bis = new BufferedInputStream(urlConnection.getInputStream());
            type = HttpURLConnection.guessContentTypeFromStream(bis);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bis = null;
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
                urlConnection = null;
            }
            return type;
        }
    }

    /**
     * 获取url中的文件类型，
     *
     * @param playUrl 示例：http://music.com/music/3288012.mp3?qcode=werd38sdsd19dkeK
     * @return mp3
     */
    public static String getUrlFormat(String playUrl) {
        String fileType = null;
        if (playUrl == null) {
            return fileType;
        }

        String regex = null;
        if (playUrl.contains("?")) {
            regex = "\\.(\\w+)\\?";
        } else {
            regex = "\\.(\\w+)$";
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(playUrl.trim());
        if (matcher.find()) {
            fileType = matcher.group(1);
        }
        return fileType;
    }
}
