package com.xsic.xsic.ui.pictureSelector;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseViewHolder;
import com.xsic.xsic.utils.ImageLoader;
import com.xsic.xsic.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PictureSelectorAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context mContext;
    private List<String> mDatas = new ArrayList<>();
    private PictureSelectorCallBack mPictureSelectorCallBack;


    public PictureSelectorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<String> datas){
        mDatas = datas;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LogUtil.d("pictts","onCreate");
        return BaseViewHolder.create(mContext, R.layout.picture_selector_item,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position){
        LogUtil.d("pictts","onBind");
        ImageLoader.getInstance().displayUrl(mDatas.get(position),holder.getView(R.id.image));
    }

    @Override
    public int getItemCount() {
        if (mDatas.size() > 0){
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface PictureSelectorCallBack{
        void onSelector(String url);
    }
    public void setCallBack(PictureSelectorCallBack pictureSelectorCallBack){
        mPictureSelectorCallBack = pictureSelectorCallBack;
    }
}
