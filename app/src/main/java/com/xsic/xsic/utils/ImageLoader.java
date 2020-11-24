package com.xsic.xsic.utils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xsic.xsic.app.BaseApplication;

public class ImageLoader {
    public static ImageLoader instance = null;

    public final static int radius_1 = 20;

    /**
     * 1. 线程安全：
     *
     * 　 饿汉式天生线程安全，可以直接用于多线程而不会出现问题。
     *
     * 　 懒汉式本身非线程安全，需要人为实现线程安全。
     *
     * 2. 资源加载和性能：
     *
     * 　 饿汉式在类创建的同时就实例化一个静态对象出来，不管之后会不会使用这个单例，都会占据一定的内存，造成内存泄漏，但相应的，在第一次调用时速度也会更快。
     *
     * 　 懒汉式会延迟加载，加载前不占用内存，在第一次使用该单例的时候才会实例化对象，且第一次调用时要做初始化，如果要做的工作比较多，性能上会有些延迟，之后就和饿汉式一样了。
     * @return
     */
    public static ImageLoader getInstance(){
        if (instance == null){
            synchronized (ImageLoader.class){
                if (instance == null){
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    /**
     * 展示本地图片，传入int型
     * @param res
     * @param view
     * @param radius
     */
    public void displayLocalRes(int res, ImageView view, int radius){
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(BaseApplication.getBaseApplication()).load(res).apply(requestOptions).into(view);
    }

    /**
     * 展示本地图片，传入drawable型
     * @param drawable
     * @param view
     * @param radius
     */
    public void displayLocalDrawable(Drawable drawable, ImageView view, int radius){
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(BaseApplication.getBaseApplication()).load(drawable).apply(requestOptions).into(view);
    }

    /**
     * 加载网络图片
     * @param url
     * @param view
     */
    public void displayUrl(String url, ImageView view){
        Glide.with(BaseApplication.getBaseApplication()).load(url).into(view);
    }

    /**
     * 展示本地gif图片，传入res型
     * @param res
     * @param view
     * @param radius
     */
    public void displayLocalGifRes(int res, ImageView view, int radius){
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(BaseApplication.getBaseApplication()).asGif().load(res).apply(requestOptions).into(view);
    }

    /**
     * 展示本地gif图片，传入drawable型
     * @param drawable
     * @param view
     * @param radius
     */
    public void displayLocalGifDrawable(Drawable drawable, ImageView view, int radius){
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(BaseApplication.getBaseApplication()).asGif().load(drawable).apply(requestOptions).into(view);
    }

    /**
     * 加载网络gif图
     * @param url
     * @param view
     * @param radius
     */
    public void displayGifUrl(String url, ImageView view, int radius){
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(BaseApplication.getBaseApplication()).asGif().load(url).apply(requestOptions).into(view);
    }
}
