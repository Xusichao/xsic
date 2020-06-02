package com.xsic.xsic.network.baseUrl;

import com.xsic.xsic.constant.GlobalConstant;

/**
 * 获取域名
 */
public class BaseUrl {
    public static String getBaseUrl(){
        if (GlobalConstant.IS_DEBUG){
            return "";
        }
        return "";
    }
}
