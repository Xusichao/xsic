package com.xsic.xsic.network;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.xsic.xsic.network.vo.ApiResponse;
import com.xsic.xsic.network.vo.AppResponseBody;
import com.xsic.xsic.network.vo.Resource;
import com.xsic.xsic.utils.ObjectUtil;

public abstract class NetworkdBoundResource<ResultType> {
    private MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkdBoundResource() {
        final LiveData<ApiResponse<ResultType>> apiResource = createCall();
        result.addSource(apiResource, new Observer<ApiResponse<ResultType>>() {
            @Override
            public void onChanged(ApiResponse<ResultType> resultTypeApiResponse) {
                result.removeSource(apiResource);
                //检查数据加载状态
                if (resultTypeApiResponse.isHttpSuccessful()) {
                    ResultType resultType = processResponse(resultTypeApiResponse);
                    //这里判断服务器我们自己定义的字段是否是成功
                    if (processResponseStatus(resultType)) {
                        setValue(Resource.success(resultType));
                        onFetchSuccess(resultType);
                    } else {
                        setValue(Resource.error("", resultTypeApiResponse.body));
                        onFetchFailed();
                    }
                } else {
                    setValue(Resource.error(resultTypeApiResponse.errorMsg, resultTypeApiResponse.body));
                    onFetchFailed();
                }
            }
        });
    }


    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if (!ObjectUtil.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    @WorkerThread
    private ResultType processResponse(ApiResponse<ResultType> response) {
        return response.body;
    }

    /**
     * 检查我们服务器返回的状态码是否是成功
     *
     * @param responseBody 服务器返回
     * @return 们服务器返回的状态码是否是成功
     */
    @WorkerThread
    private boolean processResponseStatus(ResultType responseBody) {
        if(responseBody instanceof AppResponseBody){
            AppResponseBody appResponseBody = (AppResponseBody) responseBody;
            if(appResponseBody.getStatus_code() == 1){
                return true;
            }
            else{
                return false;
            }
        }
        return true;
    }

    /**
     * 调用它来创建API调用。
     *
     * @return 从网络接口获得的数据
     */
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<ResultType>> createCall();

    /**
     * 获取失败时调用。子类可能需要重置组件。
     */
    protected abstract void onFetchFailed();

    /**
     * 请求成功，你可以进行后续的非view相关处理，如保存到SharedPreferences
     *
     * @param item 服务器返回数据
     */
    protected abstract void onFetchSuccess(@NonNull ResultType item);

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }
}
