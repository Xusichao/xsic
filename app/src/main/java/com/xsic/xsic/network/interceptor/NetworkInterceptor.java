package com.xsic.xsic.network.interceptor;

import com.xsic.xsic.constant.GlobalConstant;
import com.xsic.xsic.utils.LogUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class NetworkInterceptor implements Interceptor {

    private final String TAG = "网络请求日志";

    public NetworkInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request enhanceRequest = request.newBuilder()
                .build();
        if (GlobalConstant.IS_SHOW_LOG){
            RequestBody requestBody = enhanceRequest.body();
            String body = null;
            if (requestBody != null) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                Charset charset = Charset.forName("UTF-8");
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(Charset.forName("UTF-8"));
                }
                body = buffer.readString(charset);
            }
            LogUtil.i("网络请求日志","\n请求method:"
                    + String.format(Locale.getDefault(), "%s\n请求url：%s\n请求headers: %s\n请求body：%s",
                    enhanceRequest.method(), enhanceRequest.url(), enhanceRequest.headers(), body));
        }

        return chain.proceed(enhanceRequest);
    }
}
