package com.xsic.xsic.ui.main.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.xsic.xsic.R;

public class ActionBar extends LinearLayout implements View.OnClickListener{
    private Context mContext;
    private View rootView;
    private View findBtn;
    private View moreBtn;

    public ActionBar(Context context) {
        this(context,null,0,0);
    }

    public ActionBar(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0,0);
    }

    public ActionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context,attrs,defStyleAttr,0);
    }

    public ActionBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initUi();
    }

    private void initUi(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.common_action_bar,this,true);
        findBtn = rootView.findViewById(R.id.actionbar_find);
        moreBtn = rootView.findViewById(R.id.actionbar_more);

    }

    @Override
    public void onClick(View v) {

    }
}
