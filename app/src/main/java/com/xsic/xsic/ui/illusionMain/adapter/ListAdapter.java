package com.xsic.xsic.ui.illusionMain.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xsic.xsic.R;
import com.xsic.xsic.base.BaseViewHolder;
import com.xsic.xsic.ui.illusionMain.customResponseBody.CustomResponseBody;
import com.xsic.xsic.utils.ImageLoader;
import com.xsic.xsic.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context mContext;
    private List<CustomResponseBody.ListBean> mDatas = new ArrayList<>();
    private ListCallBack mListCallBack;

    public ListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<CustomResponseBody.ListBean> datas){
        mDatas = datas;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.create(mContext, R.layout.list_item,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (mDatas.get(position)!=null){
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.getView(R.id.img).getLayoutParams();//new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = mDatas.get(position).getHeight();
//            layoutParams.width = mDatas.get(position).getWidth();
            holder.getView(R.id.img).setLayoutParams(layoutParams);

//            ImageLoader.getInstance().displayUrl(mDatas.get(position).getImgUrl(),holder.getView(R.id.img));
            ImageLoader.getInstance().displayLocalDrawable(mContext.getResources().getDrawable(R.drawable.test),holder.getView(R.id.img),1);
            ((TextView)holder.getView(R.id.title)).setText(mDatas.get(position).getTitle());
            ((TextView)holder.getView(R.id.btn)).setEnabled(mDatas.get(position).getStatus()==1);
            ((TextView)holder.getView(R.id.btn)).setText(mDatas.get(position).getStatus()==1?"Use":"Apply");
            ((TextView)holder.getView(R.id.btn)).setOnClickListener(v->{
                if (mDatas.get(position).getStatus()==1){
                    mListCallBack.onUse(mDatas.get(position).getJumpType(), mDatas.get(position).getJumpUrl());
                }else {
                    mListCallBack.onApply(mDatas.get(position).getDownloadUrl());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mDatas.size() > 0){
            return mDatas.size();
        }
        return 0;
    }

    public void setCallBack(ListCallBack listCallBack){
        mListCallBack = listCallBack;
    }
    public interface ListCallBack{
        void onApply(String downloadUrl);
        void onUse(int jumpType, String jumpUrl);
    }
}
