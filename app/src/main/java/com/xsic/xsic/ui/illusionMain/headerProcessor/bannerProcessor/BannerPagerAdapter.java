package com.xsic.xsic.ui.illusionMain.headerProcessor.bannerProcessor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.xsic.xsic.ui.illusionMain.customResponseBody.CustomResponseBody;
import com.xsic.xsic.ui.illusionMain.headerProcessor.bannerProcessor.BannerPagerItem;

import java.util.ArrayList;
import java.util.List;

public class BannerPagerAdapter extends androidx.viewpager.widget.PagerAdapter {
    public static final int MAXCOUNT = 1000;
    private List<CustomResponseBody.BannerBean.ContentListBean> mDatas = new ArrayList<>();
    private Context mContext;

    public BannerPagerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (mDatas.size() > 0){
            return MAXCOUNT;
        }
        return 0;
    }

    public void setDatas(List<CustomResponseBody.BannerBean.ContentListBean> datas){
        mDatas = datas;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int realPosition = position % mDatas.size();
        BannerPagerItem bannerImage = new BannerPagerItem(mContext,mDatas.get(realPosition).getImgUrl());
        bannerImage.setOnClickListener(v->{
            mIBannerCallBack.onClick(mDatas.get(position).getJumpType(), mDatas.get(position).getJumpUrl());
        });
        container.addView(bannerImage);
        return bannerImage;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private IBannerCallBack mIBannerCallBack;
    public void setCallBack(IBannerCallBack iBannerCallBack){
        mIBannerCallBack = iBannerCallBack;
    }
    public interface IBannerCallBack{
        void onClick(int jumpType, String jumpUrl);
    }
}
