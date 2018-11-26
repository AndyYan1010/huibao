package com.example.win7.huibao.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.win7.huibao.R;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/19 14:29
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class LvAccAdapter extends BaseAdapter {
    private Context mContext;
    private List    mList;
    private int     mOpenItem;

    public LvAccAdapter(Context context, List list, int openItem) {
        this.mContext = context;
        this.mList = list;
        this.mOpenItem = openItem;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final MyViewHolder viewHolder;
        if (null == view) {
            viewHolder = new MyViewHolder();
            view = View.inflate(mContext, R.layout.lv_item_acc, null);
            viewHolder.linear_hide = view.findViewById(R.id.linear_hide);
            viewHolder.tv_more = view.findViewById(R.id.tv_more);
            viewHolder.tv_jfkm = view.findViewById(R.id.tv_jfkm);//借方科目
            viewHolder.tv_jfkmje = view.findViewById(R.id.tv_jfkmje);//借方科目金额
            viewHolder.tv_jfjxkm = view.findViewById(R.id.tv_jfjxkm);//借方进项科目
            viewHolder.tv_jfjxje = view.findViewById(R.id.tv_jfjxje);//借方进项金额
            viewHolder.tv_wy = view.findViewById(R.id.tv_wy);//网银
            viewHolder.tv_dfkm = view.findViewById(R.id.tv_dfkm);//贷方科目
            viewHolder.tv_dfkmje = view.findViewById(R.id.tv_dfkmje);//贷方科目金额
            viewHolder.tv_dfxxkm = view.findViewById(R.id.tv_dfxxkm);//贷方销项科目
            viewHolder.tv_dfxxje = view.findViewById(R.id.tv_dfxxje);//贷方销项金额
            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }
        viewHolder.tv_more.setTag(i);
        viewHolder.linear_hide.setVisibility(View.GONE);
        if (i == mOpenItem) {
            viewHolder.linear_hide.setVisibility(View.VISIBLE);
        } else {
            viewHolder.linear_hide.setVisibility(View.GONE);
        }
        viewHolder.tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //用 currentItem 记录点击位置s
                int tag = (Integer) view.getTag();
                if (tag == mOpenItem) { //再次点击
                    mOpenItem = -1; //给 currentItem 一个无效值
                } else {
                    mOpenItem = tag;
                }
                //通知adapter数据改变需要重新加载
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private class MyViewHolder {
        LinearLayout linear_hide;
        TextView     tv_more, tv_jfkm, tv_jfkmje, tv_jfjxkm, tv_jfjxje, tv_wy, tv_dfkm, tv_dfkmje, tv_dfxxkm, tv_dfxxje;
    }
}
