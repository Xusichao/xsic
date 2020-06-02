package com.xsic.xsic.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.base.BaseViewModel;
import com.xsic.xsic.base.interfaces.SendDataCallBack;
import com.xsic.xsic.utils.ActivityUtil;
import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.StatusBarUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseDataBindingFragment <T extends ViewDataBinding, E extends BaseViewModel> extends Fragment implements View.OnClickListener  {
    public static final String TAG = "BaseDataBindingFragment";

    protected Context mContext;
    protected T viewBinding;
    protected E viewModel;

    private Object dataFromAct;
    private SendDataCallBack mSendDataCallBack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        /* 将Activity传过来的数据赋值，通过 [getDataFromActivity] 获取 */
        dataFromAct = ((BaseDataBindingActivity)mContext).sendDataToFragment();

        /* 初始化实现了接口的Activity */
        mSendDataCallBack = (BaseDataBindingActivity)mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG,"aaaaa");
        /* 初始化变量 */
        viewBinding = DataBindingUtil.inflate(inflater,getContentViewId(),container,false);
        initViewModel();

        /*--分割线--*/
        StatusBarUtil.setTransparentStatusBar((Activity) mContext);
        viewBinding.getRoot().setOnTouchListener(mOnTouchListener);
        initView();
        registerClick();
        subscribeBaseUi();

        return viewBinding.getRoot();
    }


    protected abstract int getContentViewId();

    protected abstract void initView();

    protected abstract void registerClick();

    protected abstract void subscribeUi(E viewModel);

    protected abstract void touchToRefresh();

    @SuppressWarnings("unchecked")
    private void initViewModel(){
        //这里获得到的是类的泛型的类型
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        if (type != null) {
            Type[] actualTypeArguments = type.getActualTypeArguments();
            Class<E> tClass = (Class<E>) actualTypeArguments[1];
            viewModel = new ViewModelProvider(this).get(tClass);
        }
    }

    /* 防止快速多次点击而启动多个Activity */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
//                if (ActivityUtil.isFastDoubleClick()){
//                    return true;
//                }
            }
            return false;
        }
    };

    private void subscribeBaseUi(){
        subscribeUi(viewModel);
    }


    /* 显示loading */
    protected void showLoading(){

    }

    /* 隐藏loading */
    protected void hideLoading(){

    }

    /* 显示toast */
    protected void showToast(String toastContent){
        Toast.makeText(mContext,toastContent,Toast.LENGTH_SHORT).show();
    }

    /* 显示网络错误view */
    protected void showErrorView(){

    }

    /* 隐藏网络错误view */
    protected void hideErrorView(){

    }

    /* 页面跳转（startActivity） */
    protected void navigateToActivity(Class<?> clazz, Bundle extra){
        Intent intent = new Intent(mContext,clazz);
        if (extra!=null){
            intent.putExtras(extra);
        }
        startActivity(intent);
    }

    /* 页面跳转（startActivityForResult） */
    protected void navigateToActivityForResult(Class<?> clazz, Bundle extra, int resultCode){
        Intent intent = new Intent(mContext,clazz);
        if (extra!=null){
            intent.putExtras(extra);
        }
        startActivityForResult(intent,resultCode);
    }

    /**
     * 【MARK】
     * 供fragment调用
     * 当Fragment要传数据给Activity时，只需要调用该方法
     */
    protected void sendDataToActivity(Object data){
        mSendDataCallBack.getDataFromFragment(data);
    }

    /**
     * 【MARK】
     * 当需要获取从Activity传递过来的数据时，只需要调用该方法
     */
    protected Object getDataFromActivity(){
        return dataFromAct;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isVisible() && isResumed()){
            onFragmentVisible();
        }
    }
    /* 当前fragment可见 */
    public abstract void onFragmentVisible();
}
