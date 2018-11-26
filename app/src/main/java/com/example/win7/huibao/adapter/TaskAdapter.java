package com.example.win7.huibao.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.win7.huibao.R;
import com.example.win7.huibao.activity.AddTaskActivity;
import com.example.win7.huibao.entity.Tasks;
import java.util.List;

/**
 * Created by WIN7 on 2018/3/23.
 */

public class TaskAdapter extends BaseAdapter{
    private Context mContext;
    private List<Tasks> list;
    private LayoutInflater inflater;

    public TaskAdapter(Context context,List<Tasks> list){
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(mContext);
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if(view==null){
            view = inflater.inflate(R.layout.item_task,null);
            holder = new Holder();
            holder.tv_taskno = (TextView)view
                    .findViewById(R.id.tv_taskno);
            holder.iv_edit = (ImageView)view
                    .findViewById(R.id.iv_edit);
            view.setTag(holder);
        }else{
            holder = (Holder)view.getTag();
        }
        holder.tv_taskno.setText(list.get(i).getFbillno());
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddTaskActivity.class);
                intent.putExtra("taskno",list.get(i).getFbillno());
                intent.putExtra("interid",list.get(i).getFinterid());
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    class Holder{
        TextView tv_taskno;
        ImageView iv_edit;
    }
}
