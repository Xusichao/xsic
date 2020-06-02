package com.xsic.xsic.network.helper;

import com.google.gson.Gson;
import com.xsic.xsic.network.LiveDataCallAdapterFactory;
import com.xsic.xsic.network.baseUrl.BaseUrl;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    public static Retrofit instance = null;

    public static Retrofit getRetrofit(OkHttpClient okHttpClient){
        if (instance == null){
            synchronized (RetrofitHelper.class){
                if (instance == null){
                    instance = new Retrofit.Builder()
                            .client(okHttpClient)
                            .baseUrl(BaseUrl.getBaseUrl())
                            .addConverterFactory(GsonConverterFactory.create(new Gson()))
                            .addCallAdapterFactory(new LiveDataCallAdapterFactory()).build();
                }
            }
        }

        return instance;
    }
}
