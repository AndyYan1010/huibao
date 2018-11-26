package com.example.win7.huibao.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.win7.huibao.R;
import com.example.win7.huibao.entity.Chat;
import com.example.win7.huibao.entity.Item;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by WIN7 on 2018/3/16.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BaseAdapter>{
    private ArrayList<Item> dataList = new ArrayList<>();

    public void replaceAll(ArrayList<Item> list) {
        dataList.clear();
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Item> list) {
        if (dataList != null && list != null) {
            dataList.addAll(list);
            notifyItemRangeChanged(dataList.size(),list.size());
        }

    }

    @Override
    public BaseAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case Item.CHAT_A:
                return new ChatAViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_a, parent, false));
            case Item.CHAT_B:
                return new ChatBViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_b, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseAdapter holder, int position) {
        holder.setData(dataList.get(position).object);
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public class BaseAdapter extends RecyclerView.ViewHolder {

        public BaseAdapter(View itemView) {
            super(itemView);
        }

        void setData(Object object) {

        }
    }

    private class ChatAViewHolder extends BaseAdapter {
        private ImageView ic_user;
        private TextView tv;

        public ChatAViewHolder(View view) {
            super(view);
            ic_user = (ImageView) itemView.findViewById(R.id.ic_user);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }

        @Override
        void setData(Object object) {
            super.setData(object);
            Chat chat = (Chat) object;
            Picasso.with(itemView.getContext()).load(chat.getIcon()).placeholder(R.mipmap.ic_launcher).into(ic_user);
            tv.setText(chat.getContent());
        }
    }

    private class ChatBViewHolder extends BaseAdapter {
        private ImageView ic_user;
        private TextView tv;

        public ChatBViewHolder(View view) {
            super(view);
            ic_user = (ImageView) itemView.findViewById(R.id.ic_user);
            tv = (TextView) itemView.findViewById(R.id.tv);

        }

        @Override
        void setData(Object object) {
            super.setData(object);
            Chat chat = (Chat) object;
            Picasso.with(itemView.getContext()).load(chat.getIcon()).placeholder(R.mipmap.ic_launcher).into(ic_user);
            tv.setText(chat.getContent());
        }
    }
}
