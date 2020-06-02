package com.xsic.xsic.network.vo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 自定义 封装层
 * @param <T>
 */
public class Resource<T> {
    @NonNull
    public Status status;
    @Nullable
    public String message;
    @Nullable
    public T data;

    public Resource(Status statusCode, @Nullable String statusMessage, @Nullable T data) {
        this.status = statusCode;
        this.message = statusMessage;
        this.data = data;
    }

    public  static <T> Resource<T> success(@Nullable T data){
        return new Resource<>(Status.SUCESS,null,data);
    }

    public  static <T> Resource<T> error(String msg,@Nullable T data){
        return new Resource<>(Status.ERROR,msg,data);
    }

    public  static <T> Resource<T> loading(@Nullable T data){
        return new Resource<>(Status.LOADING,null,data);
    }


}
