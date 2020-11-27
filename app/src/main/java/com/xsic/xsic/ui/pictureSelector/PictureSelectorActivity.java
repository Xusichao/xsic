package com.xsic.xsic.ui.pictureSelector;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseDataBindingActivity;
import com.xsic.xsic.databinding.ActivityPictureSelectorBinding;
import com.xsic.xsic.ui.pictureSelector.DataMgr.DataMgr;
import com.xsic.xsic.ui.pictureSelector.DataMgr.IDataMgr;
import com.xsic.xsic.ui.pictureSelector.animation.PictureSelectorItemAnimator;
import com.xsic.xsic.ui.pictureSelector.itemDecorator.PictureSelectorItemDecorator;

import java.util.ArrayList;
import java.util.List;


public class PictureSelectorActivity extends BaseDataBindingActivity<ActivityPictureSelectorBinding,PictureSelectorViewmodel> {
    private PictureSelectorAdapter mPictureSelectorAdapter;

    //是否正在加载更多系统图片
    private boolean mIsLoadingNext = false;

    @Override
    protected int getContentViewId(){
        return R.layout.activity_picture_selector;
    }

    private static final int PERMISSION_CODES = 100;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission(){
        List<String> p = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                p.add(permission);
            }
        }
        if (p.size() > 0) {
            requestPermissions(p.toArray(new String[p.size()]), PERMISSION_CODES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPictureSelectorAdapter.setData(DataMgr.getInstance().getmData());
        mPictureSelectorAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setUpUi() {
        mPictureSelectorAdapter = new PictureSelectorAdapter(mContext);
        mPictureSelectorAdapter.setHasStableIds(true);
        viewBinding.recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
        viewBinding.recyclerView.addItemDecoration(new PictureSelectorItemDecorator());
        viewBinding.recyclerView.addOnScrollListener(mOnScrollListener);
        viewBinding.recyclerView.setAdapter(mPictureSelectorAdapter);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
            if (recyclerView.getLayoutManager()!= null){
                if (mIsLoadingNext) return;
                float curPosition = ((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (curPosition / mPictureSelectorAdapter.getItemCount() >= DataMgr.loadLine){
                    //当前可见的最后一个position位于
                    mIsLoadingNext = true;
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            DataMgr.getInstance().loadImageFromSystem(new IDataMgr() {
                                @Override
                                public void onLoadDone(int startPos, int endPos) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mIsLoadingNext = false;
                                            mPictureSelectorAdapter.notifyItemRangeChanged(startPos,startPos+endPos);
                                        }
                                    });
                                }
                            });

                        }
                    }.start();
                }
            }
        }
    };

    @Override
    protected void initView() {
        requestPermission();
    }

    @Override
    protected void registerClick() {

    }

    @Override
    protected void subscribeUi(PictureSelectorViewmodel viewModel) {

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
