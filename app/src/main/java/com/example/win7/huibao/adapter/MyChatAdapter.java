package com.example.win7.huibao.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/12 19:43
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MyChatAdapter extends RecyclerView.Adapter<MyChatAdapter.ChatViewHolder> {
    private List<EMMessage> mEMMessageList;

    public MyChatAdapter(List<EMMessage> EMMessageList) {
        mEMMessageList = EMMessageList;
    }

    @Override
    public int getItemCount() {
        return mEMMessageList == null ? 0 : mEMMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage emMessage = mEMMessageList.get(position);
        return emMessage.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_receiver, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_send, parent, false);
        }
        ChatViewHolder chatViewHolder = new ChatViewHolder(view);
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        EMMessage emMessage = mEMMessageList.get(position);
        long msgTime = emMessage.getMsgTime();
        //需要将消息body转换为EMTextMessageBody
        EMTextMessageBody body = (EMTextMessageBody) emMessage.getBody();
        String message = body.getMessage();
        holder.mTvMsg.setText(message);

        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
        if (position==0){
            holder.mTvTime.setVisibility(View.VISIBLE);
        }else{
            EMMessage preMessage = mEMMessageList.get(position - 1);
            long preMsgTime = preMessage.getMsgTime();
            if (DateUtils.isCloseEnough(msgTime,preMsgTime)){
                holder.mTvTime.setVisibility(View.GONE);
            }else{
                holder.mTvTime.setVisibility(View.VISIBLE);
            }
        }
        if (emMessage.direct()== EMMessage.Direct.SEND){
            switch (emMessage.status()) {
                case INPROGRESS:
                    holder.mIvState.setVisibility(View.VISIBLE);
                    holder.mIvState.setImageResource(R.drawable.msg_state_animation);
                    AnimationDrawable drawable = (AnimationDrawable) holder.mIvState.getDrawable();
                    if (drawable.isRunning()){
                        drawable.stop();
                    }
                    drawable.start();
                    break;
                case SUCCESS:
                    holder.mIvState.setVisibility(View.GONE);
                    break;
                case FAIL:
                    holder.mIvState.setVisibility(View.VISIBLE);
                    holder.mIvState.setImageResource(R.mipmap.msg_error);
                    break;
            }
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView  mTvTime;
        TextView  mTvMsg;
        ImageView mIvState;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mIvState = (ImageView) itemView.findViewById(R.id.iv_state);
        }
    }
}
