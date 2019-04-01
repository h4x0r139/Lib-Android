package cn.yinxm.lib.view.webview;

/**
 * <p>
 *
 * @author yinxuming
 * @date 2018/12/28
 */
public class WebViewCallbackManager {
    private Callback mCallback;

    private WebViewCallbackManager() {
    }

    public static WebViewCallbackManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static WebViewCallbackManager INSTANCE = new WebViewCallbackManager();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void notifyWebViewClosed(String url) {
        if (mCallback != null) {
            mCallback.onWebViewClosed(url);
        }
    }


    public void notifyPageStarted(String url) {
        if (mCallback != null) {
            mCallback.onPageStarted(url);
        }
    }

    public void notifyPageFinished(String url) {
        if (mCallback != null) {
            mCallback.onPageFinished(url);
        }
    }


    public static class Callback {
        /**
         * 打开新网页
         *
         * @param url
         */
        public void onPageStarted(String url) {

        }

        /**
         * 关闭网页
         *
         * @param url
         */
        public void onPageFinished(String url) {

        }

        /**
         * webview 关闭
         *
         * @param url
         */
        public void onWebViewClosed(String url) {

        }

    }

}
