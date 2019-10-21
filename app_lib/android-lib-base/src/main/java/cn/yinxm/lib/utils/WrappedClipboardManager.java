package cn.yinxm.lib.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * 剪切板
 * ClipboardManager在API 11前后接口发生改变且无法兼容，WrappedClipboardManager类
 * 用于屏蔽此差异性
 */
public abstract class WrappedClipboardManager {

    /**
     * 程序上下文，用于获取系统service
     */
    protected static Context sTheApp;

    /**
     * 将文本拷贝至剪贴板
     *
     * @param text 文本
     */
    public abstract void setText(CharSequence text);

    /**
     * 剪切板中是否有文本内容
     *
     * @return true-是
     */
    public abstract boolean hasText();

    /**
     * 获取剪切板中的文本
     *
     * @return 文本
     */
    public abstract CharSequence getText();

    /**
     * 根据平台版本获取可用的ClipboardManager
     *
     * @param context 程序实例
     * @return 当前平台的剪贴板实例
     */
    public static WrappedClipboardManager newInstance(Context context) {
        sTheApp = context.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new HoneycombClipboardManager();
        } else {
            return new OldClipboardManager();
        }
    }

    /**
     * Android 3.0(API 11)以下平台的剪贴板
     */
    private static class OldClipboardManager extends WrappedClipboardManager {

        /**
         * 单实例引用
         */
        @SuppressWarnings("deprecation")
        private static android.text.ClipboardManager sInstance = null;

        /**
         * 构造方法
         */
        @SuppressWarnings("deprecation")
        public OldClipboardManager() {
            sInstance = (android.text.ClipboardManager) sTheApp.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void setText(CharSequence text) {
            sInstance.setText(text);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean hasText() {
            return sInstance.hasText();
        }

        @SuppressWarnings("deprecation")
        @Override
        public CharSequence getText() {
            return sInstance.getText();
        }

    }

    /**
     * Android 3.0(API 11)以上平台的剪贴板
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class HoneycombClipboardManager extends WrappedClipboardManager {
        /**
         * 单实例引用
         */
        private static android.content.ClipboardManager sInstance = null;
        /**
         * 单实例引用数据
         */
        private static android.content.ClipData sClipData = null;

        /**
         * 构造方法
         */
        @SuppressLint("ServiceCast")
        public HoneycombClipboardManager() {
            sInstance = (android.content.ClipboardManager) sTheApp.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        @Override
        public void setText(CharSequence text) {
            try {
                sClipData = android.content.ClipData.newPlainText(android.content.ClipDescription.MIMETYPE_TEXT_PLAIN, text);
                sInstance.setPrimaryClip(sClipData);
            } catch (Exception e) {
                // 4.2 mtj crash
                // Caused by: java.lang.IllegalArgumentException: Unknown package com.baidu.haokan
            }
        }

        @Override
        public boolean hasText() {
            try {
                return sInstance.hasPrimaryClip();
            } catch (Exception e) {
                // 4.2 mtj crash
                // Caused by: java.lang.IllegalArgumentException: Unknown package com.baidu.haokan
                return false;
            }
        }

        @Override
        public CharSequence getText() {
            try {
                sClipData = sInstance.getPrimaryClip();
                if (sClipData != null && sClipData.getItemCount() > 0) {
                    return sClipData.getItemAt(0).getText();
                }
            } catch (Exception e) {
                // 4.2 mtj crash
                // Caused by: java.lang.IllegalArgumentException: Unknown package com.baidu.haokan
            }
            return "";
        }

    }
}
