package com.example.win7.huibao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.win7.huibao.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class NeedCheckAdapter extends BaseAdapter {
    Context mContext;
    List<HashMap<String,String>> list;
    DecimalFormat df  = new DecimalFormat("#0.00");

    public NeedCheckAdapter(Context context,List<HashMap<String,String>> list){
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
        Holder holder;
        if(view==null){
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_need_check,null);
            holder.tv_items = (TextView)view.
                    findViewById(R.id.tv_items);
            holder.tv_number = (TextView)view.
                    findViewById(R.id.tv_number);
            holder.tv_price = (TextView)view.
                    findViewById(R.id.tv_price);
            holder.tv_amount = (TextView)view.
                    findViewById(R.id.tv_amount);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        HashMap<String,String> map = list.get(i);
        holder.tv_items.setText(map.get("item"));
        holder.tv_number.setText("数量"+df.format(Double.parseDouble(map.get("number"))));
        holder.tv_price.setText("单价"+df.format(Double.parseDouble(map.get("price"))));
        holder.tv_amount.setText("金额含税"+df.format(Double.parseDouble(map.get("amount"))));
        return view;
    }

    class Holder{
        TextView tv_items,tv_number,tv_price,tv_amount;
    }
}
