package cn.yinxm.lib.view.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yinxm.lib.R;
import cn.yinxm.lib.activity.BaseActivity;
import cn.yinxm.lib.utils.log.LogUtil;


public class WebViewActivity extends BaseActivity {
    private static final String TAG = "WebViewActivity";

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String KEY_URL = "url";
    public static final String KEY_TITLE = "title";
    public static final String KEY_INJECTION_URL = "injection_url";
    public static final String KEY_INJECTION_JS_FUNC_BODY = "injection_js_func_body";
    /**
     * 通过判断url中的参数，来关闭页面
     */
    public static final String KEY_CLOSE_URL = "close_url";
    private TextView mTvTitle;
    private WebView mWebView;
    private ImageView mIvBack;

    private String mCloseUrl = "";
    private String injectionUrl;
    private String injectionJsFuncBody;
    private boolean isProgressInjectionJs = false;
    private boolean isFinish = false;

    public static void start(Context context, String url) {
        start(context, url, null, null, null, null);
    }


    public static void start(Context context, String url, String closeUrl, String title) {
        start(context, url, closeUrl, title, null, null);
    }

    /**
     * @param context
     * @param url                     加载网页url
     * @param closeUrl                触发关闭webview的url，
     * @param title                   标题
     * @param jnjectionUrl            需要注入js的url
     * @param injectionJsFunctionBody 注入js 函数体
     */
    public static void start(Context context, String url, String closeUrl, String title, String jnjectionUrl, String injectionJsFunctionBody) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_CLOSE_URL, closeUrl);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_INJECTION_URL, jnjectionUrl);
        intent.putExtra(KEY_INJECTION_JS_FUNC_BODY, injectionJsFunctionBody);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    protected void initView() {
        setContentView(R.layout.activity_web_view);
        mWebView = findViewById(R.id.webView);
        mTvTitle = findViewById(R.id.tv_title);
        mIvBack = findViewById(R.id.iv_back);
        initWebViewSetting(mWebView);
    }

    protected void loadData() {
        mWebView.setBackgroundColor(0);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(KEY_URL);
            LogUtil.d(TAG, "url=" + url);
            mCloseUrl = intent.getStringExtra(KEY_CLOSE_URL);
            mTvTitle.setText(intent.getStringExtra(KEY_TITLE));
            injectionUrl = intent.getStringExtra(KEY_INJECTION_URL);
            injectionJsFuncBody = intent.getStringExtra(KEY_INJECTION_JS_FUNC_BODY);
            mWebView.loadUrl(url);
            mTvTitle.setText(intent.getStringExtra(KEY_TITLE));
        }
    }

    private void initWebViewSetting(WebView webView) {

        WebSettings webSettings = webView.getSettings();
        // 5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        // 允许js代码
        webSettings.setJavaScriptEnabled(true);
        // 允许SessionStorage/LocalStorage存储
        webSettings.setDomStorageEnabled(true);
        // 禁用放缩
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        // 禁用文字缩放
        webSettings.setTextZoom(100);
        // 10M缓存，api 18后，系统自动管理。
        webSettings.setAppCacheMaxSize(10 * 1024 * 1024);
        // 允许缓存，设置缓存位置
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getApplication().getDir("appcache", 0).getPath());
        // 允许WebView使用File协议
        webSettings.setAllowFileAccess(true);
        // 不保存密码
        webSettings.setSavePassword(false);
        // 设置UA
//        webSettings.setUserAgentString(webSettings.getUserAgentString() + " kaolaApp/" + AppUtils.getVersionName());
        // 移除部分系统JavaScript接口
//        KaolaWebViewSecurity.removeJavascriptInterfaces(webView);
        // 自动加载图片
        webSettings.setLoadsImagesAutomatically(true);

        // 设置不用系统浏览器打开,直接显示在当前Webview
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.d(TAG, "shouldOverrideUrlLoading ---> " + url);
                if (!(url.startsWith("http") || url.startsWith("https"))) {
                    LogUtil.d(TAG, "shouldOverrideUrlLoading intercepte");
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.d(TAG, "onPageStarted ---> " + url);
//                mTvTitle.setText(url);
                WebViewCallbackManager.getInstance().notifyPageStarted(url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.d(TAG, "onPageFinished ---> " + url);
                if (!TextUtils.isEmpty(injectionUrl) && url.contains(injectionUrl)) {
                    processInjectionJsFunc(view);
                }

                WebViewCallbackManager.getInstance().notifyPageFinished(url);
                if (url != null && mCloseUrl != null && url.startsWith(mCloseUrl)) {
                    isFinish = true;
                    Intent intent = getIntent();
                    intent.putExtra(KEY_CLOSE_URL, url);
                    setResult(RESULT_OK, intent);
                    finish();
                    WebViewCallbackManager.getInstance().notifyWebViewClosed(url);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogUtil.d(TAG, "onReceivedError ---> " + request + "， " + error);
                if (!isFinish) {
                    LogUtil.e(TAG, "加载失败...");
                }
            }
        });
        // 设置WebChromeClient类
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // 设置标题
                LogUtil.d(TAG, "onReceivedTitle " + title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                LogUtil.d(TAG, "onProgressChanged " + newProgress);
//                if (!isProgressInjectionJs) {
//                    isProgressInjectionJs = true;
//                processInjectionJsFunc(view);
//                }

                if (newProgress > 30) {
                    // TODO: 2019/4/1 hide loading
                }
            }
        });

        if (Build.VERSION.SDK_INT <= 16) {
            try {
                webView.removeJavascriptInterface("searchBoxJavaBridge_");
                webView.removeJavascriptInterface("accessibility");
                webView.removeJavascriptInterface("accessibilityTraversal");
            } catch (Exception e) {
                LogUtil.e(e);
            }
        }
    }

    /**
     * 返回上一页面而不是退出浏览器
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 销毁WebView
     */
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", DEFAULT_ENCODING, null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private void processInjectionJsFunc(WebView view) {
        if (TextUtils.isEmpty(injectionJsFuncBody)) {
            return;
        }
        String functionName = "bdMediaJsInjection";
        String jsFunction = "javascript:function " + functionName + "() {" + injectionJsFuncBody + "}";
        LogUtil.d(TAG, "jsFunction=" + jsFunction);
        view.loadUrl(jsFunction);
        view.loadUrl("javascript:" + functionName + "();");
    }

}
