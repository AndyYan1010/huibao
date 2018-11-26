package com.example.win7.huibao.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.win7.huibao.R;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/3 16:09
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class GlideLoaderUtil {
    /**
     * (1)
     * 显示图片Imageview
     *
     * @param context  上下文
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageView(Context context, String url, ImageView imgeview) {
        Glide.with(context)
                .load(url)// 加载图片
                .placeholder(R.mipmap.ic_launcher)//图片加载出来前，显示的图片
                .error(R.mipmap.ic_launcher)// 设置错误图片
                .crossFade()// 设置淡入淡出效果，默认300ms，可以传参
                .into(imgeview);
    }
}
