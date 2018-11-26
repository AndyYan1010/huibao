package com.example.win7.huibao.entity;

/**
 * Created by WIN7 on 2018/3/14.
 */

public class Msg {
    String username;
    String nickname;
    String lastmsg;
    long   lastMsgTime;
    int    unreadMsgCount;
    String time;
    String imagepath;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLastmsg() {
        return lastmsg;
    }

    public void setLastmsg(String lastmsg) {
        this.lastmsg = lastmsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
