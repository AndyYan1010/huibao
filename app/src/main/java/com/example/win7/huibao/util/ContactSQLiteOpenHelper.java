package com.example.win7.huibao.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/15 9:49
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class ContactSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final  String CONTACT   = "contact";
    private static final String DB_NAME   = "contacts.db";
    private static final int    VERSION   = 1;
    public static final  String T_CONTACT = "t_contact";
    public static final  String USERNAME  = "username";

    private ContactSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ContactSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    //初始化表结构
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + T_CONTACT + "(_id integer primary key," + USERNAME + " varchar(20)," + CONTACT + " varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
