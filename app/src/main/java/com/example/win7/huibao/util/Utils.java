package com.example.win7.huibao.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by WIN7 on 2018/3/20.
 */

public class Utils {
    //弹出软键盘时滚动视图
    private static int scrollToPosition=0;
    public static void autoScrollView(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect rect = new Rect();

                        //获取root在窗体的可视区域
                        root.getWindowVisibleDisplayFrame(rect);

                        //获取root在窗体的不可视区域高度(被遮挡的高度)
                        int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;

                        //若不可视区域高度大于150，则键盘显示
                        if (rootInvisibleHeight > 150) {

                            //获取scrollToView在窗体的坐标,location[0]为x坐标，location[1]为y坐标
                            int[] location = new int[2];
                            scrollToView.getLocationInWindow(location);

                            //计算root滚动高度，使scrollToView在可见区域的底部
                            int scrollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;

                            //注意，scrollHeight是一个相对移动距离，而scrollToPosition是一个绝对移动距离
                            scrollToPosition += scrollHeight;

                        } else {
                            //键盘隐藏
                            scrollToPosition = 0;
                        }
                        root.scrollTo(0, scrollToPosition);

                    }
                });
    }

    //null值转换成空字符串
    public static String NulltoString(Object object){
        if(object==null){
            return "";
        }else {
            return object.toString();
        }
    }

    public static String NulltoZero(Object object){
        if(object.equals("")){
            return "0";
        }else{
            return object.toString();
        }
    }

    //boolean转化成0或1
    public static String BooleantoNum(String bool){
        if(bool!=null){
            if(bool.equals("False")){
                return "0";
            }
            else if(bool.equals("True")){
                return "1";
            }else {
                return "0";
            }
        }else{
            return "0";
        }

    }

    //生成32位UUID
    public static String UUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //按拼音首字母排序
    public static void sortByInitial(List<HashMap<String,String>> list){
        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(list, comparator);
    }
}
