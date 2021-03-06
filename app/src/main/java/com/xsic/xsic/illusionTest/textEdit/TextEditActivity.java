package com.xsic.xsic.illusionTest.textEdit;

import android.view.View;
import android.widget.FrameLayout;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.databinding.ActivityEditBinding;
import com.xsic.xsic.databinding.ActivityTextBinding;
import com.xsic.xsic.illusionTest.DataMgr.DataMgr;
import com.xsic.xsic.utils.ScreenUtil;

public class TextEditActivity extends BaseDataBindingActivity<ActivityTextBinding,TextEditViewmodel> {
    @Override
    protected void setUpUi() {
        int imgHeight = (int) (ScreenUtil.getScreenHeight() - getResources().getDimension(R.dimen.dp_96));
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) viewBinding.img.getLayoutParams();
        fl.height = imgHeight;
        viewBinding.img.setLayoutParams(fl);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_text;
    }

    @Override
    protected void initView() {
        viewBinding.img.setImage("/storage/emulated/0/新建文件夹/lADPBGnDao-sGcrNAfTNAfQ_500_500.jpg_720x720q90g - 副本 (1009) - 副本 - 副本 - 副本.jpg");
    }

    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(TextEditViewmodel viewModel) {

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
