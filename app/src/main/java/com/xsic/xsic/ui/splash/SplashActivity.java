package com.xsic.xsic.ui.splash;

import android.os.Handler;
import android.view.View;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.databinding.ActivitySplashBinding;
import com.xsic.xsic.ui.main.MainActivity;

public class SplashActivity extends BaseDataBindingActivity<ActivitySplashBinding,SplashViewModel> {
    private int skipTime = 2000;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToActivity(MainActivity.class,null);
            }
        },skipTime);
    }

    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(SplashViewModel viewModel) {

    }

    @Override
    protected void touchToRefresh() {

    }

    @Override
    protected Object sendDataToFragment() {
        return null;
    }

    @Override
    public void onClick(View v) {

    }
}
