package com.xsic.xsic.network;

import com.xsic.xsic.network.helper.OkhttpHelper;
import com.xsic.xsic.network.helper.RetrofitHelper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class RetrofitManager {
    private static final RetrofitManager mRetrofitManager = new RetrofitManager();

    private OkHttpClient mOkHttpClient;

    private Retrofit mRetrofit;
    private RetrofitManager(){
        if (mOkHttpClient == null){
            synchronized (RetrofitManager.class){
                if (mOkHttpClient == null){
                    mOkHttpClient = OkhttpHelper.getOkHttpClient();
                }
            }
        }
        mRetrofit = RetrofitHelper.getRetrofit(mOkHttpClient);
    }
}
