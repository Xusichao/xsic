package com.xsic.xsic.network.helper;

import com.xsic.xsic.network.interceptor.NetworkInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkhttpHelper {
    /* 连接超时时间 */
    public static final long CONNECT_TIMEOUT = 10;
    /* 读取超时时间 */
    public static final long READ_TIMEOUT = 10;
    /* 写入超时时间 */
    public static final long WRITE_TIMEOUT = 10;

    public static OkHttpClient getOkHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)
                .addInterceptor(new NetworkInterceptor());

        return builder.build();
    }
}
