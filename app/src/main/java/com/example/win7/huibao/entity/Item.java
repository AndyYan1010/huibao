package com.example.win7.huibao.entity;

import java.io.Serializable;

/**
 * Created by WIN7 on 2018/3/15.
 */

public class Item implements Serializable{
    public static final int CHAT_A = 1001;
    public static final int CHAT_B = 1002;
    public int type;
    public Object object;

    public Item(int type, Object object) {
        this.type = type;
        this.object = object;
    }
}
