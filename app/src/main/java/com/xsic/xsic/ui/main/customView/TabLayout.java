package com.xsic.xsic.ui.main.customView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xsic.xsic.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @deprecated
 */
public class TabLayout extends LinearLayout {
    private Context mContext;

    private View rootView;

    private View tab1;
    private View tab2;
    private View tab3;
    private View tab4;

    private ImageView tabImg1;
    private ImageView tabImg2;
    private ImageView tabImg3;
    private ImageView tabImg4;

    private TextView tabText1;
    private TextView tabText2;
    private TextView tabText3;
    private TextView tabText4;

    private ArrayList<View> tabList;
    private ArrayList<ImageView> imgList;
    private ArrayList<TextView> textList;

    public TabLayout(Context context) {
        super(context,null,0);

    }

    public TabLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
        mContext = context;
        initView();
    }

    public TabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }


    private void initView(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.main_tablayout,this,true);
        tab1 = findViewById(R.id.tab_item_1);
        tab2 = findViewById(R.id.tab_item_2);
        tab3 = findViewById(R.id.tab_item_3);
        tab4 = findViewById(R.id.tab_item_4);
        tabImg1 = tab1.findViewById(R.id.tab_item_img);
        tabImg2 = tab2.findViewById(R.id.tab_item_img);
        tabImg3 = tab3.findViewById(R.id.tab_item_img);
        tabImg4 = tab4.findViewById(R.id.tab_item_img);
        tabText1 = tab1.findViewById(R.id.tab_item_text);
        tabText2 = tab2.findViewById(R.id.tab_item_text);
        tabText3 = tab3.findViewById(R.id.tab_item_text);
        tabText4 = tab4.findViewById(R.id.tab_item_text);

        tabList = new ArrayList<>(Arrays.asList(tab1,tab2,tab3,tab4));
        imgList = new ArrayList<>(Arrays.asList(tabImg1,tabImg2,tabImg3,tabImg4));
        textList = new ArrayList<>(Arrays.asList(tabText1,tabText2,tabText3,tabText4));
        initClickListener();
        initTabLayout();
    }

    public void initTabLayout(){
        ArrayList<Drawable> img = new ArrayList<>(Arrays.asList(null,null,null,null));
        ArrayList<String> text = new ArrayList<>(Arrays.asList("一","二","三","四"));
        for (int i=0;i<img.size();i++){
            imgList.get(i).setImageDrawable(img.get(i));
        }
        for (int n=0;n<text.size();n++){
            textList.get(n).setText(text.get(n));
        }
    }

    private void initClickListener(){
        for (int i=0;i<tabList.size();i++){
            final int n = i;
            tabList.get(i).setOnClickListener(v ->{
                mCallBack.onClick(n);
            });
        }
    }

    public void setCurrentItemByClick(int position){
        for (View tab:tabList){
            tab.setActivated(false);
        }
        tabList.get(position).setActivated(true);
    }

    public void setCurrentItemByScroll(int position){

    }


    public void clearViewRAM(){

    }

    public CallBack mCallBack;
    public void setmCallBack(CallBack callBack){
        mCallBack = callBack;
    }
    public interface CallBack{
        void onClick(int position);
    }


}
