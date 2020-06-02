package com.xsic.xsic.network.vo;

import androidx.annotation.Nullable;

import java.io.IOException;

import retrofit2.Response;

/**
 * http层
 * @param <T>   body
 */
public class ApiResponse<T> {

    public int code;
    @Nullable
    public T body;
    @Nullable
    public String errorMsg;

    public ApiResponse(Throwable throwable) {
        code = 500;
        body = null;
        errorMsg = throwable.getMessage();
    }

    public ApiResponse(Response<T> response) {
        code = response.code();
        if (response.isSuccessful()){
            body = response.body();
            errorMsg = null;
        }else {
            String tempMsg = null;
            if (response.errorBody()!=null){
                try {
                    tempMsg = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (tempMsg==null || tempMsg.trim().length() == 0){
                tempMsg = response.message();
            }
            errorMsg = tempMsg;
            body = null;
        }
    }

    public boolean isHttpSuccessful(){
        return code >= 200 && code < 300;
    }
}
