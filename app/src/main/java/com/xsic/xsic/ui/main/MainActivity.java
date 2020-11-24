package com.xsic.xsic.ui.main;

import com.google.android.material.tabs.TabLayout;
import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.databinding.ActivityMainBinding;
import com.xsic.xsic.ui.contacts.ContactsFragment;
import com.xsic.xsic.ui.discovery.DiscoveryFragment;
import com.xsic.xsic.ui.main.viewpagerAdapter.MainViewPagerAdapter;
import com.xsic.xsic.ui.mine.MineFragment;
import com.xsic.xsic.ui.session.SessionFragment;
import com.xsic.xsic.utils.LinearGradientUtil;
import com.xsic.xsic.utils.LogUtil;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseDataBindingActivity<ActivityMainBinding,MainViewModel> {

    /* ui */
    private List<String> tabTitleList = new ArrayList<>();
    private List<Drawable> tabImageList = new ArrayList<>();
    private final int mStartColor = 0xFF57BF6A;
    private final int mEndColor = 0xFF000000;

    @Override
    protected void setUpUi() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initMainUi();
    }

    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(MainViewModel viewModel) {

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


    /**
     * 初始化主界面，包含viewpager和tabLayout的初始化
     */
    private void initMainUi(){
        initTabLayout();
        initViewPager();
        setUpTabLayoutWithViewPager();
    }

    /**
     * 初始化fragment 和 viewpager
     */
    private void initViewPager(){
        Fragment session = new SessionFragment();
        Fragment contacts = new ContactsFragment();
        Fragment discovery = new DiscoveryFragment();
        Fragment mine = new MineFragment();
        List<Fragment> fragmentList = new ArrayList<>(Arrays.asList(session,contacts,discovery,mine));
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mainViewPagerAdapter.setDatas(fragmentList);
        viewBinding.mainViewpager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewBinding.mainViewpager.setAdapter(mainViewPagerAdapter);
        viewBinding.mainViewpager.setOffscreenPageLimit(4);

    }

    /**
     * 初始化tabLayout
     */
    private void initTabLayout(){
        tabTitleList = new ArrayList<>(Arrays.asList("1","2","3","4"));
        tabImageList = new ArrayList<>(Arrays.asList(
                ContextCompat.getDrawable(mContext,R.drawable.tab_session_st),
                ContextCompat.getDrawable(mContext,R.drawable.tab_contact_st),
                ContextCompat.getDrawable(mContext,R.drawable.tab_discovery_st),
                ContextCompat.getDrawable(mContext,R.drawable.tab_mine_st)));
        for (int i=0;i<tabImageList.size();i++){
            viewBinding.mainTablayout.addTab(viewBinding.mainTablayout.newTab().setCustomView(createTabItem(tabTitleList,tabImageList,i)));
        }
        /* 设置第一个为激活态 */
        viewBinding.mainTablayout.getTabAt(0).getCustomView().findViewById(R.id.tab_item_text).setActivated(true);
        viewBinding.mainTablayout.getTabAt(0).getCustomView().findViewById(R.id.tab_item_img).setActivated(true);
        viewBinding.mainTablayout.setTabRippleColor(null);
    }

    /**
     * 关联tabLayout和viewPager
     */
    private void setUpTabLayoutWithViewPager(){
        viewBinding.mainTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabCurrentItemActive(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTabCurrentItemInactive(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewBinding.mainViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTabColorWhileScroll(position,positionOffset);
                LogUtil.d("dsadasda",position+" --- "+positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 创建一个tab
     * @param tabTitleList
     * @param tabImageList
     * @param position
     * @return
     */
    private View createTabItem(List<String> tabTitleList,List<Drawable> tabImageList,int position){
        View view = LayoutInflater.from(this).inflate(R.layout.main_tablayout_item,null);
        TextView textView = view.findViewById(R.id.tab_item_text);
        ImageView imageView = view.findViewById(R.id.tab_item_img);
        textView.setText(tabTitleList.get(position));
        imageView.setImageDrawable(tabImageList.get(position));
        return view;
    }

    /**
     * 在viewpager滑动时，逐渐修改tab的颜色
     * @param curPos    当前的位置
     * @param radio     滑动过程中生成的位置值
     */
    private void setTabColorWhileScroll(int curPos, float radio){
        if (radio == 0) return;
        int nextPos = curPos + 1;
        TextView textViewCur = viewBinding.mainTablayout.getTabAt(curPos).getCustomView().findViewById(R.id.tab_item_text);
        ImageView imageViewCur = viewBinding.mainTablayout.getTabAt(curPos).getCustomView().findViewById(R.id.tab_item_img);
        TextView textViewNext = viewBinding.mainTablayout.getTabAt(nextPos).getCustomView().findViewById(R.id.tab_item_text);
        ImageView imageViewNext = viewBinding.mainTablayout.getTabAt(nextPos).getCustomView().findViewById(R.id.tab_item_img);
        textViewCur.setTextColor(LinearGradientUtil.getColor(mStartColor,mEndColor,radio));
        imageViewCur.setColorFilter(LinearGradientUtil.getColor(mStartColor,mEndColor,radio));
        textViewNext.setTextColor(LinearGradientUtil.getColor(mEndColor,mStartColor,radio));
        imageViewNext.setColorFilter(LinearGradientUtil.getColor(mEndColor,mStartColor,radio));
    }

    /**
     * 将位置为position的tab设置成激活态
     * @param position
     */
    private void setTabCurrentItemActive(int position){
        viewBinding.mainTablayout.getTabAt(position).getCustomView().findViewById(R.id.tab_item_text).setActivated(true);
        viewBinding.mainTablayout.getTabAt(position).getCustomView().findViewById(R.id.tab_item_img).setActivated(true);
    }

    private void setTabCurrentItemInactive(int position){
        viewBinding.mainTablayout.getTabAt(position).getCustomView().findViewById(R.id.tab_item_text).setActivated(false);
        viewBinding.mainTablayout.getTabAt(position).getCustomView().findViewById(R.id.tab_item_img).setActivated(false);
    }

}
