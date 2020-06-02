package com.xsic.xsic.network;

import retrofit2.Retrofit;

public class ApiManager {
    private static Retrofit mRetrofit;

    private static Api mApi;

    public static Api getApi(){
        if (mApi == null){
            synchronized (ApiManager.class){
                if (mApi == null){
                    mApi = mRetrofit.create(Api.class);
                }
            }
        }
        return mApi;
    }

}
