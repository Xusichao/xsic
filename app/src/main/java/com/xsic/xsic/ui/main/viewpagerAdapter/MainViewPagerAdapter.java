package com.xsic.xsic.ui.main.viewpagerAdapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();

    public MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return  mFragmentList.get(position);
    }

    public void setDatas(List<Fragment> fragmentList){
        mFragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        if (mFragmentList.size() != 0){
            return mFragmentList.size();
        }
        return 0;
    }
}
