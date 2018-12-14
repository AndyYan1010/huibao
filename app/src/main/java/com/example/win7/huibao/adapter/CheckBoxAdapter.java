package com.example.win7.huibao.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.win7.huibao.R;

import java.util.HashMap;
import java.util.List;

public class CheckBoxAdapter extends BaseAdapter {
    Context                       mContext;
    List<HashMap<String, Object>> list;

    public CheckBoxAdapter(Context context, List<HashMap<String, Object>> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_checkbox, null);
            holder.checkBox = (CheckBox) view
                    .findViewById(R.id.check_box);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.checkBox.setText(list.get(i).get("fname").toString());
        if (Boolean.valueOf(list.get(i).get("ischeck").toString())) {
            holder.checkBox.setTextColor(Color.GREEN);
        }
        holder.checkBox.setChecked(Boolean.valueOf(list.get(i).get("ischeck").toString()));
        holder.checkBox.setTag(i);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean b) {
                int p = (Integer) btn.getTag();
                HashMap<String, Object> m = list.get(p);

                m.put("ischeck", !Boolean.valueOf(m.get("ischeck").toString()));
                //                notifyDataSetChanged();
            }
        });
        return view;
    }

    class ViewHolder {
        CheckBox checkBox;
    }
}
