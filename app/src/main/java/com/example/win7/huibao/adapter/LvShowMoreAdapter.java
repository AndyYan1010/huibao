package com.example.win7.huibao.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.win7.huibao.R;

import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/4 14:30
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LvShowMoreAdapter extends BaseAdapter {
    private Context                   mContext;
    private List<Map<String, String>> mList;

    public LvShowMoreAdapter(Context context, List list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewholder viewholder;
        if (null == view) {
            viewholder = new MyViewholder();
            view = View.inflate(mContext, R.layout.view_only_textview, null);
            viewholder.tv_cont = view.findViewById(R.id.tv_cont);
            view.setTag(viewholder);
        } else {
            viewholder = (MyViewholder) view.getTag();
        }
        viewholder.tv_cont.setText(mList.get(i).get("fname"));
        return view;
    }

    private class MyViewholder {
        TextView tv_cont;
    }
}
