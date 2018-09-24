package cn.yinxm.lib.utils.net;

import android.content.Context;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.yinxm.lib.api.manager.AppManager;
import cn.yinxm.lib.utils.NetworkUtil;
import cn.yinxm.lib.utils.StringUtil;
import cn.yinxm.lib.utils.log.LogUtil;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yinxm on 2016/4/1.
 */
public class OkHttpUtil {


    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 原生okhttp3 get 请求 【适用于get返回数据量比较大】
     * @param url
     * @param paramHeaders 传null会自动添加公共请求头部，传空不会添加公共请求头
     * @param params
     * @return response
     * @throws IOException
     */
    public static Response getFormResponse(String url, Map<String, String> paramHeaders, Map<String, String> params) throws IOException {
        if (StringUtil.isBlank(url)) {
            return null;
        }
        Context context =  AppManager.getInstance().getApplicationContext();
        if (context != null &&
                !NetworkUtil.isNetworkConnected(context)) {//无网络
            LogUtil.d("[OkHttpUtil.getFormResponse]未连接网络");
            return null;
        }

//        if (paramHeaders == null) {
//            paramHeaders = AppSign.getPublicHeaders(null);
//        }
//        Headers.Builder headers = new Headers.Builder();
//        if (paramHeaders != null && !paramHeaders.isEmpty()) {
//            Set<Map.Entry<String, String>> set = paramHeaders.entrySet();
//            for (Map.Entry<String, String> entry : set) {
//                headers.add(entry.getKey(), entry.getValue());
//            }
//        }

        StringBuilder sb = new StringBuilder(url);
        if (params != null && !params.isEmpty()) {
            if (url.contains("?")) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            Set<Map.Entry<String, String>> set = params.entrySet();
            for (Map.Entry<String, String> entry : set) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        url = sb.toString();
        LogUtil.i("get url="+url);


        Request request = new Request.Builder()
//                .headers(headers.build())
                .url(url)
                .build();
        //设置超时时间
        OkHttpClient clientCopy = client.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Response execute = clientCopy.newCall(request).execute();
        LogUtil.d(execute+"");
        return execute;

    }

    /**
     *
     * @param url
     * @param params
     * @return
     */
    public static String getForm(String url, Map<String, String> params) {
        return getForm(url, null, params);
    }


        /**
         * 原生okhttp3 get 请求
         * @param url
         * @param paramHeaders  传null会自动添加公共请求头部，传空不会添加公共请求头
         * @param params
         * @return
         */
    public static String getForm(String url, Map<String, String> paramHeaders, Map<String, String> params) {

        try {
            Response response = getFormResponse(url, paramHeaders, params);
            if (response != null && response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }


    /**
     * 原生okhttp post form 表单 同步提交
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String postForm(String url, Map<String, String> params) throws IOException {
        return postForm(url, null, params);
    }

    /**
     * 原生okhttp post form 表单 同步提交
     * @param url 请求地址
     * @param paramHeaders  请求头, 传null会自动添加公共请求头部，传空不会添加公共请求头
     * @param params 请求参数
     * @return
     * @throws IOException
     */
    public static String postForm(String url, Map<String, String> paramHeaders, Map<String, String> params) throws IOException {
        LogUtil.d("url="+url+", paramHeaders="+paramHeaders+", params="+params);
        if (StringUtil.isBlank(url)) {
            return null;
        }
        Context context =  AppManager.getInstance().getApplicationContext();
        if (context != null &&
                !NetworkUtil.isNetworkConnected(context)) {//无网络
            LogUtil.d("[OkHttpUtil.postForm]未连接网络");
            return null;
        }

//        if (paramHeaders == null) {
//            paramHeaders = AppSign.getPublicHeaders(null);
//        }
//        Headers.Builder headers = new Headers.Builder();
//        if (paramHeaders != null && !paramHeaders.isEmpty()) {
//            Set<Map.Entry<String, String>> set = paramHeaders.entrySet();
//            for (Map.Entry<String, String> entry : set) {
//                headers.add(entry.getKey(), entry.getValue());
//            }
//        }

        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            Set<Map.Entry<String, String>> set = params.entrySet();
            for (Map.Entry<String, String> entry : set) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
//                .headers(headers.build())
                .post(formBody)
                .build();
        //设置超时时间
        OkHttpClient clientCopy = client.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Response response = clientCopy.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        // TODO: 2017/12/4 失败重连
        
        String str = response.body().string();
        LogUtil.d("postForm="+str);
        return  str;
    }

    /**
     * 字符串
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String postJson(String url, String params) throws IOException {
        if (StringUtil.isBlank(url)) {
            return null;
        }
        Context context =  AppManager.getInstance().getApplicationContext();
        if (context != null &&
                !NetworkUtil.isNetworkConnected(context)) {//无网络
            LogUtil.d("[OkHttpUtil.postForm]未连接网络");
            return null;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/x-markdown; charset=ISO8859-1"), params);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        String str = response.body().string();
        return str;
    }
}
