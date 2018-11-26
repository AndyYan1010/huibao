package com.example.win7.huibao.presenter;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/12 19:43
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public interface ChatPresenter {

    void initChat(String contact);

    void updateData(String username);

    void sendMessage(String username, String msg);
}
