package com.xsic.xsic.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.xsic.xsic.base.interfaces.SendDataCallBack;
import com.xsic.xsic.utils.ActivityUtil;
import com.xsic.xsic.utils.StatusBarUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseDataBindingActivity <T extends ViewDataBinding, E extends BaseViewModel> extends AppCompatActivity implements View.OnClickListener, SendDataCallBack {
    protected Context mContext;
    protected T viewBinding;
    protected E viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 初始化变量 */
        mContext = this;
        viewBinding = DataBindingUtil.setContentView(this,getContentViewId());
        initViewModel();

        /*--分割线--*/
        StatusBarUtil.setTransparentStatusBar(this);
        setScreenRoate(true);
        initView();
        registerClick();
        subscribeBaseUi();
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

    private void subscribeBaseUi(){
        subscribeUi(viewModel);
    }

    /* 设置Activity屏幕方向 */
    @SuppressLint("SourceLockedOrientationActivity")
    private void setScreenRoate(Boolean screenRoate) {
        if (screenRoate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /* 仿IOS侧滑关闭页面 */
    private void initSlidable(){

    }

    /* 防止快速多次点击而启动多个Activity */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
//            if (ActivityUtil.isFastDoubleClick()){
//                return true;
//            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /* 显示loading */
    protected void showLoading(){

    }

    /* 隐藏loading */
    protected void hideLoading(){

    }

    /* 显示toast */
    protected void showToast(String toastContent){
        Toast.makeText(this,toastContent,Toast.LENGTH_SHORT).show();
    }

    /* 显示网络错误view */
    protected void showErrorView(){

    }

    /* 隐藏网络错误view */
    protected void hideErrorView(){

    }

    /* 页面跳转（startActivity） */
    protected void navigateToActivity(Class<?> clazz, Bundle extra){
        Intent intent = new Intent(this,clazz);
        if (extra!=null) {
            intent.putExtras(extra);
        }
        startActivity(intent);
    }

    /* 页面跳转（startActivityForResult） */
    protected void navigateToActivityForResult(Class<?> clazz, Bundle extra, int resultCode){
        Intent intent = new Intent(this,clazz);
        if (extra!=null) {
            intent.putExtras(extra);
        }
        startActivityForResult(intent,resultCode);
    }

    /**
     * 【111】：Activity ---> Fragment
     * 当Activity要传数据给Fragment时，只需要调用该方法
     */
    protected abstract Object sendDataToFragment();

    /**
     * 【MARK】
     * 实现了fragment中的接口
     * 当需要获取从Fragment传过来的数据时，只需要调用该方法
     */
    @Override
    public void getDataFromFragment(Object data) {

    }
}
