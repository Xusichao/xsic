package com.xsic.xsic.base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    public static BaseViewHolder create(Context context, int layoutId, ViewGroup viewGroup){
        View itemView = LayoutInflater.from(context).inflate(layoutId,viewGroup,false);
        return new BaseViewHolder(itemView);
    }

    public static BaseViewHolder create(View itemView){
        return new BaseViewHolder(itemView);
    }

    /**
     * 通过id获得控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }
}
