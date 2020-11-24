package com.xsic.xsic.ui.pictureSelector;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.xsic.xsic.R;
import com.xsic.xsic.app.BaseApplication;
import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class dataMgr {
    // TODO: 2020/11/24
    /* 修改了样式需要修改的值Start */
    private static final int LOAD_SCREEN_COUNT = 3;      //每次加载3屏数据
    private static final int ROW_COUNT = 3;              //每行3个
    private static float itemHeight = BaseApplication.getBaseApplication().getResources().getDimension(R.dimen.dp_116);  //每个item的高度 = 104 + 12
    /* 修改了样式需要修改的值End */

    private static dataMgr mInstance = null;
    private int mCursor = 0;         //每次截取源数据后的游标位置
    private List<String> mAllData = new ArrayList<>();       //持有的所有源数据
    private List<String> mLastShowData = new ArrayList<>();  //上次返回给适配器的数据
    private List<String> mIncreShowData = new ArrayList<>(); //本次的【增量】数据

    /**
     * 饿汉 + 双重检测锁提高性能
     * @return
     */
    public static dataMgr getInstance(){
        if (mInstance == null){
            synchronized (dataMgr.class){
                if (mInstance == null){
                    mInstance = new dataMgr();
                }
            }
        }
        return mInstance;
    }

    /**
     * 私有构造函数，在构造时获取所有图片
     */
    private dataMgr() {
        getSystemPhotoList();

    }

    /**
     * 返回从系统中获取的所有数据
     * @return allData
     */
    public List<String> getAllData(){
        return mAllData;
    }

    /**
     * 上次返回的数据 + 增量数据 = 本次外部适配器需要setData的数据
     * @return data
     */
    public List<String> getShowData(){
        getIncreShowDataBySplitSource();
        mLastShowData.addAll(mIncreShowData);
        return mLastShowData;
    }

    /**
     * 获取[增量]数据的数量.
     * 根据 LOAD_SCREEN_COUNT 等来决定每一次返回给适配器的 [增量] 数据数量
     * @return 返回增量的数量
     */
    private int getIncreShowDataCount(){
        if (ScreenUtil.getScreenHeight() % itemHeight > 0){
            return (int) ((((ScreenUtil.getScreenHeight() % itemHeight) + 1) * ROW_COUNT) * LOAD_SCREEN_COUNT);
        }
        return (int) Math.ceil(((ScreenUtil.getScreenHeight() % itemHeight) * ROW_COUNT) * LOAD_SCREEN_COUNT);
    }

    /**
     * 通过截取的方式获取增量数据，然后赋值给 mIncreShowData
     */
    private void getIncreShowDataBySplitSource(){
        mIncreShowData.clear();
        mIncreShowData.addAll(mAllData.subList(mCursor,getIncreShowDataCount()));
        //移动游标
        mCursor = getIncreShowDataCount() + 1;
    }

    /**
     * 从系统中获取图片
     */
    private void getSystemPhotoList(){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = BaseApplication.getBaseApplication().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) return ; // 没有图片
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists()) {
                mAllData.add(path);
                LogUtil.i("SystemPicture", path);
            }
        }
    }

}
