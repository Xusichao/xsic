package com.xsic.xsic.ui.illusionMain;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.base.BaseViewHolder;
import com.xsic.xsic.databinding.ActivityMainIllusionBinding;
import com.xsic.xsic.ui.illusionMain.adapter.ListAdapter;
import com.xsic.xsic.ui.illusionMain.customResponseBody.CustomResponseBody;
import com.xsic.xsic.ui.illusionMain.headerProcessor.bannerProcessor.BannerPagerAdapter;
import com.xsic.xsic.ui.illusionMain.itemDecoration.ListItemDecoration;
import com.xsic.xsic.ui.illusionMain.localTestData.TestData;

import java.lang.ref.WeakReference;
import java.util.List;

import static android.view.View.OVER_SCROLL_NEVER;
import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

public class MainActivity extends BaseDataBindingActivity<ActivityMainIllusionBinding,MainViewmodel > {

    private CarouselHandler carouselHandler;
    private static final int CAROUSEL_MSG = 0;
    private static final int CAROUSEL_DELAY = 6000;

    private CustomResponseBody mCustomResponseBody = new CustomResponseBody();

    @Override
    protected void setUpUi() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main_illusion;
    }

    @Override
    protected void initView() {
        createTestData();
        bannerProcessor(mCustomResponseBody.getBanner());
        waterFallProcessor(mCustomResponseBody.getList());
    }

    private void createTestData(){
        mCustomResponseBody = TestData.createTestData();
    }

    /**
     * banner位处理器
     * @param bannerBean
     *             type类型
     *             1：图片轮播
     *             2：视频
     */
    private void bannerProcessor(CustomResponseBody.BannerBean bannerBean){
        if (bannerBean.getType() == 1){
            viewBinding.header.viewpager.setVisibility(View.VISIBLE);
            viewBinding.header.mediaPlayer.setVisibility(View.GONE);
            BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(mContext);
            bannerPagerAdapter.setCallBack(mIBannerCallBack);
            bannerPagerAdapter.setDatas(bannerBean.getContentList());
            viewBinding.header.viewpager.setAdapter(bannerPagerAdapter);
            viewBinding.header.viewpager.addOnPageChangeListener(mOnPageChangeListener);
            viewBinding.header.viewpager.setCurrentItem(BannerPagerAdapter.MAXCOUNT/2);
            carouselHandler = new CarouselHandler(mContext);
            carouselHandler.sendEmptyMessage(CAROUSEL_MSG);
        }else {
            viewBinding.header.viewpager.setVisibility(View.GONE);
            viewBinding.header.mediaPlayer.setVisibility(View.VISIBLE);
            // TODO: 2020/12/21
        }
    }

    /**
     * 瀑布流处理器
     * @param listBeans
     */
    private void waterFallProcessor(List<CustomResponseBody.ListBean> listBeans){
        ListAdapter listAdapter = new ListAdapter(mContext);
        listAdapter.setCallBack(mListCallBack);
        viewBinding.list.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,VERTICAL));
        viewBinding.list.recyclerView.addItemDecoration(new ListItemDecoration(mContext));
        viewBinding.list.recyclerView.setAdapter(listAdapter);
        viewBinding.list.recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);

        listAdapter.setData(listBeans);
        listAdapter.notifyDataSetChanged();
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {}
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE){
                //静止状态，等待后开始轮播
                carouselHandler.sendEmptyMessageDelayed(CAROUSEL_MSG,CAROUSEL_DELAY);
            }else {
                //滑动中禁止轮播
                carouselHandler.removeMessages(CAROUSEL_MSG);
            }
        }
    };

    private ListAdapter.ListCallBack mListCallBack = new ListAdapter.ListCallBack() {
        @Override
        public void onApply(String downloadUrl) {

        }

        @Override
        public void onUse(int jumpType, String jumpUrl) {

        }
    };

    private BannerPagerAdapter.IBannerCallBack mIBannerCallBack = new BannerPagerAdapter.IBannerCallBack() {
        @Override
        public void onClick(int jumpType, String jumpUrl) {

        }
    };


    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(MainViewmodel viewModel) {

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

    class CarouselHandler extends Handler {
        private WeakReference<Context> context;

        public CarouselHandler(Context pContext) {
            context = new WeakReference<>(pContext);
        }

        @Override
        public void handleMessage(Message msg) {
            viewBinding.header.viewpager.setCurrentItem(viewBinding.header.viewpager.getCurrentItem() + 1);
            sendEmptyMessageDelayed(CAROUSEL_MSG,CAROUSEL_DELAY);
        }
    }
}
