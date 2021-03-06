package com.xsic.xsic.illusionTest.editPannel;

import android.view.View;
import android.widget.FrameLayout;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.databinding.ActivityEditBinding;
import com.xsic.xsic.illusionTest.DataMgr.DataMgr;
import com.xsic.xsic.utils.LogUtil;
import com.xsic.xsic.utils.ScreenUtil;

public class EditPannelActivity extends BaseDataBindingActivity<ActivityEditBinding,EditPannelViewmodel> {
    @Override
    protected void setUpUi() {
        LogUtil.e("werqrqr",ScreenUtil.getScreenHeight()+"");
        int imgHeight = (int) (ScreenUtil.getScreenHeight() - getResources().getDimension(R.dimen.dp_96));
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) viewBinding.img.getLayoutParams();
        fl.height = imgHeight;
        viewBinding.img.setLayoutParams(fl);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initView() {
        DataMgr dataMgr = new DataMgr(mContext);
        viewBinding.img.setImage("/storage/emulated/0/新建文件夹/lADPBGnDao-sGcrNAfTNAfQ_500_500.jpg_720x720q90g - 副本 (1009) - 副本 - 副本 - 副本.jpg");
        //viewBinding.img.setImage("/storage/emulated/0/DCIM/Camera/IMG_20210121_220934.jpg");
    }

    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(EditPannelViewmodel viewModel) {

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
