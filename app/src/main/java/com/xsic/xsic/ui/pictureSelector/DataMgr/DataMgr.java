package com.xsic.xsic.ui.pictureSelector.DataMgr;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.xsic.xsic.R;
import com.xsic.xsic.app.BaseApplication;
import com.xsic.xsic.ui.pictureSelector.customBean.CustomBean;
import com.xsic.xsic.utils.DeviceUtil;
import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataMgr {
    // TODO: 2020/11/24
    /* 修改了样式需要修改的值Start */
    private static int LOAD_SCREEN_COUNT = 3;      //每次加载3屏数据
    private static final int ROW_COUNT = 3;        //每行3个
    private static float itemHeight = BaseApplication.getBaseApplication().getResources().getDimension(R.dimen.dp_116);  //每个item的高度 = 104 + 12
    public static float loadLine = 0.5f;      //当rv滑动到到这个占比时开始加载新数据
    /* 修改了样式需要修改的值End */

    private static DataMgr mInstance = null;
    private int mCursor = 0;         //每次截取源数据后的游标位置
    private List<CustomBean> mData = new ArrayList<>();       //需要给适配器持有的数据

    public static DataMgr getInstance(){
        if (mInstance == null){
            synchronized (DataMgr.class){
                if (mInstance == null){
                    mInstance = new DataMgr();
                }
            }
        }
        return mInstance;
    }

    /**
     * 私有构造函数，在构造时获取图片
     */
    private DataMgr() {
        setLoadCount();
        loadImageFromSystem(null);
    }

    /**
     * 获取每次需要加载的
     * @return
     */
    private int getLoadCount(){
        //余数大于0的话，算多额外一行数据进去。即小数位大于0，即向上取整
        return (int)Math.ceil(ScreenUtil.getScreenHeight() / itemHeight) * ROW_COUNT * LOAD_SCREEN_COUNT;
    }

    /**
     * 设置每次加载的数量
     * 根据手机的RAM值，分为：
     * 1、大于6GB的高端机型
     * 2、大于2GB小于6GB的中端机型
     * 3、小于2GB的低端机型
     */
    private void setLoadCount(){
        if (DeviceUtil.getTotalRam(BaseApplication.getBaseApplication()) >= 6){
            //高端机
            LOAD_SCREEN_COUNT = 10;
            loadLine = 0.7f;
        }else if (DeviceUtil.getTotalRam(BaseApplication.getBaseApplication()) > 2 && DeviceUtil.getTotalRam(BaseApplication.getBaseApplication()) < 6){
            //中端机
            LOAD_SCREEN_COUNT = 6;
            loadLine = 0.5f;
        }else {
            //低端机
            LOAD_SCREEN_COUNT = 3;
            loadLine = 0.3f;
        }
    }

    /**
     * 加载系统图片
     */
    public void loadImageFromSystem(IDataMgr iDataMgr){
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = BaseApplication.getBaseApplication().getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor == null || cursor.getCount() <= 0){
            return ; // 没有图片
        }

        //每次开始取的时候都需要将游标移动到上次取操作结束的位置
        cursor.moveToPosition(mCursor);
        //记录一个保存取操作之前的游标，让mCursor随着取操作而增长
        int tempCursor = mCursor;
        while (cursor.moveToNext()){
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //不存在时返回
            if (columnIndex == -1){
                return;
            }

            //cursor的position相当于 已取 的数量
            int position = cursor.getPosition();
            if (position > tempCursor + getLoadCount()){
                //如果当前位置大于需要取的终点位置时，中断操作
                break;
            }
            //移动游标
            mCursor = position;
            String path = cursor.getString(columnIndex); // 文件地址
            File file = new File(path);
            if (file.exists()) {
                CustomBean customBean = new CustomBean();
                customBean.setUrl(path);
                customBean.setSeleted(false);
                mData.add(customBean);
            }
        }
        if (iDataMgr!=null){
            iDataMgr.onLoadDone(tempCursor, mCursor);
        }
        cursor.close();
    }

    public List<CustomBean> getmData(){
        return mData;
    }



}
