package com.example.win7.huibao.eventMessege;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/15 9:49
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class OnContactUpdateEvent {
    public String contact;
    public boolean isAdded;

    public OnContactUpdateEvent(String contact, boolean isAdded) {
        this.contact = contact;
        this.isAdded = isAdded;
    }
}
