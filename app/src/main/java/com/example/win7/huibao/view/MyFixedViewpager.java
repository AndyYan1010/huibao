package com.example.win7.huibao.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @创建者 AndyYan
 * @创建时间 2018/1/25 8:44
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class MyFixedViewpager extends ViewPager {//重写了viewpager
    private boolean isCanScroll = true;//是否能滑动

    public MyFixedViewpager(Context context) {
        super(context);
    }

    public MyFixedViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {//重写了触摸事件
        return isCanScroll && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {//重写了触摸事件
        return isCanScroll && super.onInterceptTouchEvent(ev);
    }

    /**
     * 设置其是否能滑动换页
     *
     * @param isCanScroll false 不能换页， true 可以滑动换页
     */
    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }
}
