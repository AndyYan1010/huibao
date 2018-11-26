package com.example.win7.huibao.adapter;


import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/12 19:43
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MessageListenerAdapter implements EMMessageListener {

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        //收到消息
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //收到透传消息
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        //收到已读回执
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        //收到已送达回执
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        //消息被撤回
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {
        //消息状态变动
    }
}
