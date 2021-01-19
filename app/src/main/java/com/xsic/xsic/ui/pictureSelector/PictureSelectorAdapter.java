package com.xsic.xsic.ui.pictureSelector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseViewHolder;
import com.xsic.xsic.ui.pictureSelector.customBean.CustomBean;
import com.xsic.xsic.utils.ImageLoader;
import com.xsic.xsic.utils.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PictureSelectorAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context mContext;
    private List<CustomBean> mDatas = new ArrayList<>();
    private PictureSelectorCallBack mPictureSelectorCallBack;
    private List<String> mSelectedUrl = new LinkedList<>();
    private int mMode = PICTURE_SELECTOR;
    private int mMaxCount = 1;      //最大可选数，1~9
    private boolean mHasLocked = false; //是否锁住不能再选
    /**
     * 表现模式
     * 目前有：
     *        1、图库选择模式
     *        2、拼图模式
     */
    public static final int PICTURE_SELECTOR = 1;
    public static final int PUZZLE = 2;
    /**
     * item的样式
     * 目前有：
     *        1、相机
     *        2、图片
     */
    public static final int CAMERA_ICON = 1;
    public static final int PHOTO = 2;

    public PictureSelectorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<CustomBean> datas){
        mDatas = datas;
    }

    public void setMode(int mode){
        mMode = mode;
    }

    public void setmMaxCount(int count){
        mMaxCount = count;
    }

    @Override
    public int getItemViewType(int position){
        if (mMode == PICTURE_SELECTOR){
            if (position == 0){
                return CAMERA_ICON;
            }else {
                return PHOTO;
            }
        }else {
            return PHOTO;
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if (viewType == CAMERA_ICON){
            return BaseViewHolder.create(mContext, R.layout.picture_selector_camera_item,parent);
        }
        if (viewType == PHOTO){
            return BaseViewHolder.create(mContext, R.layout.picture_selector_item,parent);
        }
        return BaseViewHolder.create(mContext, R.layout.common_empty_view,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position){
        if (getItemViewType(position) == CAMERA_ICON){
            holder.getConvertView().setOnClickListener(v->{
                mPictureSelectorCallBack.onCamera();
            });
        }
        if (getItemViewType(position) == PHOTO){
            initSelected(holder,mDatas.get(position).isSeleted());
            ImageLoader.getInstance().displayUrl(mDatas.get(position).getUrl(),holder.getView(R.id.image));
            holder.getConvertView().setOnClickListener(v->{
                setSelectedWhileClick(holder,mDatas.get(position));
            });
        }
    }

    private void initSelected(BaseViewHolder holder, boolean isSeleted){
        if (isSeleted){
            holder.getView(R.id.mask).setVisibility(View.VISIBLE);
            holder.getView(R.id.locked_mask).setVisibility(View.INVISIBLE);
        }else {
            if (mHasLocked){
                holder.getView(R.id.locked_mask).setVisibility(View.VISIBLE);
            }else {
                holder.getView(R.id.locked_mask).setVisibility(View.INVISIBLE);
            }
            holder.getView(R.id.mask).setVisibility(View.INVISIBLE);
        }
    }

    private void setSelectedWhileClick(BaseViewHolder holder, CustomBean data){
        if (data.isSeleted()){
            data.setSeleted(false);
            holder.getView(R.id.mask).setVisibility(View.INVISIBLE);
            if (mSelectedUrl.contains(data.getUrl()) && data.getUrl() != null){
                mSelectedUrl.remove(data.getUrl());
            }
        }else {
            //已锁住，不允许再选更多
            if (mHasLocked) return;
            data.setSeleted(true);
            holder.getView(R.id.mask).setVisibility(View.VISIBLE);
            if (!mSelectedUrl.contains(data.getUrl()) && data.getUrl() != null){
                mSelectedUrl.add(data.getUrl());
            }
        }
        if (mPictureSelectorCallBack!=null){
            mPictureSelectorCallBack.onSelected(mSelectedUrl);
        }
        if (mSelectedUrl.size() >= mMaxCount){
            mHasLocked = true;
            notifyDataSetChanged();
        }else {
            mHasLocked = false;
            notifyDataSetChanged();
        }
        LogUtil.d("sdadafff",mSelectedUrl+"");
    }


    @Override
    public int getItemCount(){
        if (mDatas.size() > 0){
            if (mMode == PICTURE_SELECTOR){
                return mDatas.size() + 1;
            }else {
                return mDatas.size();
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface PictureSelectorCallBack{
        void onSelected(List<String> urls);
        void onCamera();
    }
    public void setCallBack(PictureSelectorCallBack pictureSelectorCallBack){
        mPictureSelectorCallBack = pictureSelectorCallBack;
    }
}
