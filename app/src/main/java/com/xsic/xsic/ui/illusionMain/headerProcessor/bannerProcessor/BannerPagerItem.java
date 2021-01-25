package com.xsic.xsic.ui.illusionMain.headerProcessor.bannerProcessor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.xsic.xsic.R;
import com.xsic.xsic.utils.ImageLoader;

public class BannerPagerItem extends LinearLayout {
    private ImageView img;

    public BannerPagerItem(Context context, String url) {
        this(context,null,0,url);
    }

    public BannerPagerItem(Context context, @Nullable AttributeSet attrs, String url) {
        this(context, attrs,0,url);
    }

    public BannerPagerItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr, String url) {
        super(context, attrs, defStyleAttr);
        init(context,url);
    }

    private void init(Context context,String url){
        LayoutInflater.from(context).inflate(R.layout.banner_item,this,true);
        img = findViewById(R.id.img);
//        ImageLoader.getInstance().displayUrl(url,img);
        ImageLoader.getInstance().displayLocalDrawable(context.getResources().getDrawable(R.drawable.test),img,1);
    }

}
