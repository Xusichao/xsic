package com.xsic.xsic.ui.illusionMain.localTestData;

import com.xsic.xsic.ui.illusionMain.customResponseBody.CustomResponseBody;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    public static CustomResponseBody createTestData(){
        CustomResponseBody result = new CustomResponseBody();
        List<CustomResponseBody.ListBean> listBeans = new ArrayList<>();
        CustomResponseBody.BannerBean bannerBean = new CustomResponseBody.BannerBean();
        List<CustomResponseBody.BannerBean.ContentListBean> tempList1 = new ArrayList<>();
        for (int i=0;i<5;i++){
            CustomResponseBody.BannerBean.ContentListBean contentListBean = new CustomResponseBody.BannerBean.ContentListBean();
            contentListBean.setImgUrl("http://thirdwx.qlogo.cn/mmopen/vi_32/SufAKqR0nqsSRA1FEgty4vZ9uSyz05g5VYBpqLgHN4CmOSUgiayyjf13Km8ibt7E6aDwQQVCuFYeWBUDNElb6NUQ/132");
//            contentListBean.setImgUrl("https://lh5.googleusercontent.com/-pu88v9WofUM/AAAAAAAAAAI/AAAAAAAAAAA/AAKWJJP19uzjyfI5XZ2dfZPmUox2bkUu9g/s96-c/photo.jpg");
            tempList1.add(contentListBean);
        }
        bannerBean.setType(1);
        bannerBean.setContentList(tempList1);
        for (int i=0;i<20;i++){
            CustomResponseBody.ListBean listBean = new CustomResponseBody.ListBean();
            listBean.setImgUrl("http://thirdwx.qlogo.cn/mmopen/vi_32/SufAKqR0nqsSRA1FEgty4vZ9uSyz05g5VYBpqLgHN4CmOSUgiayyjf13Km8ibt7E6aDwQQVCuFYeWBUDNElb6NUQ/132");
//            listBean.setImgUrl("https://lh5.googleusercontent.com/-pu88v9WofUM/AAAAAAAAAAI/AAAAAAAAAAA/AAKWJJP19uzjyfI5XZ2dfZPmUox2bkUu9g/s96-c/photo.jpg");
            listBean.setHeight(i%2==0?200:600);
            listBean.setStatus(i%2==0?1:2);
            listBean.setTitle("测试："+(i+1));
            listBeans.add(listBean);
        }
        result.setBanner(bannerBean);
        result.setList(listBeans);
        return result;
    }
}
