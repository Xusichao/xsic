package com.xsic.xsic.network.vo;

/**
 * app返回体的基本格式
 * @param <T>
 */
public class AppResponseBody<T> {
    /**
     * status_code : 1
     * status_msg : 请求成功
     * data :
     */

    private int status_code;
    private String status_msg;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }
}
