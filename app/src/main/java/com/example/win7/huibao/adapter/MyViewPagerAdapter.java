package com.example.win7.huibao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bm.library.PhotoView;
import com.example.win7.huibao.R;
import com.example.win7.huibao.util.GlideLoaderUtil;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/21 9:14
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MyViewPagerAdapter extends PagerAdapter {
    private List        mArrayList;
    private Context     mContext;
    private PopupWindow mPopupWindow;

    public MyViewPagerAdapter(Context context, List arrayList, PopupWindow popupWindow) {
        this.mContext = context;
        this.mArrayList = arrayList;
        this.mPopupWindow = popupWindow;
    }

    @Override
    public int getCount() {
        return mArrayList == null ? 0 : mArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // 初始化显示的条目对象
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.photoview_only, null);
        view.setLayoutParams(lp);
        //设置viewpager中子view显示的数据
        PhotoView img_show = (PhotoView) view.findViewById(R.id.photoview);
        Object o = mArrayList.get(position);
        if (null != o) {
            if (o instanceof Bitmap) {
                img_show.setImageBitmap((Bitmap) o);
            } else if (o instanceof String) {
                GlideLoaderUtil.showImageView(mContext, (String) o, img_show);
            }
        }
        img_show.enable();
        img_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        // 添加到ViewPager容器
        container.addView(view);
        // 返回填充的View对象
        return view;
    }

    // 销毁条目对象
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
