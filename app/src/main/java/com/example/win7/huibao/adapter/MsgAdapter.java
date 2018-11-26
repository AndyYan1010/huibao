package com.example.win7.huibao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.entity.Msg;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by WIN7 on 2018/3/15.
 */

public class MsgAdapter extends BaseAdapter {
    Context        mContext;
    List<Msg>      mEMConversationList;
    LayoutInflater inflater;

    public MsgAdapter(Context context, List<Msg> list) {
        this.mContext = context;
        this.mEMConversationList = list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mEMConversationList.size();
    }

    @Override
    public Object getItem(int i) {
        return mEMConversationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_msg, null);
            holder = new Holder();
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
            holder.tv_lastmsg = (TextView) view.findViewById(R.id.tv_lastmsg);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_unread = (TextView) view.findViewById(R.id.tv_unread);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        //        String path = list.get(i).getImagepath();
        //        if (path == null) {
        //            holder.iv_icon.setImageResource(R.drawable.me_light);
        //        } else {
        //            File file = new File(path);
        //            if (file.exists()) {
        //                Bitmap bm = BitmapFactory.decodeFile(path);
        //                holder.iv_icon.setImageBitmap(bm);
        //            } else {
        //                holder.iv_icon.setImageResource(R.drawable.me_light);
        //            }
        //        }
        //        holder.tv_nickname.setText(list.get(i).getNickname());
        //        if (list.get(i).getLastmsg() == null) {
        //            holder.tv_lastmsg.setText("");
        //        } else {
        //            holder.tv_lastmsg.setText(list.get(i).getLastmsg());
        //        }
        //        if (list.get(i).getTime() == null) {
        //            holder.tv_time.setText("");
        //        } else {
        //            holder.tv_time.setText(list.get(i).getTime());
        //        }
        final Msg emConversation = mEMConversationList.get(i);
        //聊天的对方的名称
        String userName = emConversation.getNickname();
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        String lastMessage = emConversation.getLastmsg();
        long msgTime = emConversation.getLastMsgTime();

        holder.tv_nickname.setText(userName);
        holder.tv_lastmsg.setText(lastMessage);
        if (msgTime == 0) {
            holder.tv_time.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_time.setText(DateUtils.getTimestampString(new Date(msgTime)));
        }
        if (unreadMsgCount > 99) {
            holder.tv_unread.setVisibility(View.VISIBLE);
            holder.tv_unread.setText("99+");
        } else if (unreadMsgCount > 0) {
            holder.tv_unread.setVisibility(View.VISIBLE);
            holder.tv_unread.setText(unreadMsgCount + "");
        } else {
            holder.tv_unread.setVisibility(View.GONE);
        }
        return view;
    }

    class Holder {
        TextView tv_nickname, tv_lastmsg, tv_time, tv_unread;
        ImageView iv_icon;
    }
}
