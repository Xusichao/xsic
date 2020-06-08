package com.xsic.xsic.ui.s;

import android.view.View;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.databinding.TestBinding;

public class SActivity extends BaseDataBindingActivity<TestBinding, SViewModel> {
    @Override
    protected int getContentViewId() {
        return R.layout.test;
    }

    @Override
    protected void initView() {
        ImageViewer imageViewer2 = new ImageViewer(mContext);
        viewBinding.container.addView(imageViewer2);
    }

    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(SViewModel viewModel) {

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
