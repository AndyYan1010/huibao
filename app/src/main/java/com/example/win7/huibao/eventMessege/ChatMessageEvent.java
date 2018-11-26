package com.example.win7.huibao.eventMessege;

import com.hyphenate.chat.EMMessage;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/13 15:39
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class ChatMessageEvent {
    private EMMessage message;

    public ChatMessageEvent(EMMessage message) {
        this.message = message;
    }

    public EMMessage getMessage() {
        return message;
    }

    public void setMessage(EMMessage message) {
        this.message = message;
    }
}
