package com.example.win7.huibao.entity;

import java.io.Serializable;

/**
 * Created by WIN7 on 2018/3/16.
 */

public class Chat implements Serializable{
    private String icon;
    private String content;
    private String type;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
