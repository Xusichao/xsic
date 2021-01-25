package com.xsic.xsic.illusionTest.DataMgr;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;


import com.xsic.xsic.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataMgr {
    // TODO: 2020/11/24
    /* 修改了样式需要修改的值Start */
    private static int LOAD_SCREEN_COUNT = 3;      //每次加载3屏数据
    private static final int ROW_COUNT = 3;        //每行3个
    private static float itemHeight = 0;  //每个item的高度 = 104 + 12
    public static float loadLine = 0.5f;      //当rv滑动到到这个占比时开始加载新数据
    /* 修改了样式需要修改的值End */

    private Context mContext;
    private static DataMgr mInstance = null;
    private int mCursor = 0;         //每次截取源数据后的游标位置
    private List<String> mData = new ArrayList<>();       //需要给适配器持有的数据
    private List<String> mSourceData = new ArrayList<>();     //保存从ContentProvider中取出的所有数据

    /**
     * @deprecated 
     * @return
     */
    public static DataMgr getInstance(){
        if (mInstance == null){
            synchronized (DataMgr.class){
                if (mInstance == null){
                    //mInstance = new DataMgr();
                }
            }
        }
        return mInstance;
    }

    public DataMgr(Context context) {
        mContext = context;
        loadAllImageFromSystem();
    }

    private void loadAllImageFromSystem(){
        mSourceData.clear();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0){
            return ; // 没有图片
        }
        while (cursor.moveToNext()){
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //不存在时返回
            if (columnIndex == -1){
                return;
            }
            LogUtil.d("dadsad",cursor.getString(columnIndex));
            mSourceData.add(cursor.getString(columnIndex));
        }
        cursor.close();
        Collections.reverse(mSourceData);
    }

}
