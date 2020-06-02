package com.xsic.xsic.app;

import androidx.multidex.MultiDexApplication;

public class BaseApplication extends MultiDexApplication {
    public static BaseApplication mBaseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mBaseApplication = this;
    }

    public static BaseApplication getBaseApplication(){
        return mBaseApplication;
    }
}
