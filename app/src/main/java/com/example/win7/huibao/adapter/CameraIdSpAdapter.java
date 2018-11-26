package com.example.win7.huibao.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.win7.huibao.R;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/6/15 9:20
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class CameraIdSpAdapter extends BaseAdapter {
    private List<String> mList;
    private Context      mContext;

    public CameraIdSpAdapter(Context context, List list) {
        this.mList = list;
        this.mContext = context;
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
        ViewHolder viewHolder;
        if (null == view) {
            viewHolder = new ViewHolder();
            view = View.inflate(mContext, R.layout.spin_item_id, null);
            viewHolder.tv_title = view.findViewById(R.id.tv_title);
            viewHolder.tv_name = view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_name.setText(mList.get(i));
        return view;
    }

    private class ViewHolder {
        TextView tv_name, tv_title;
    }
}
