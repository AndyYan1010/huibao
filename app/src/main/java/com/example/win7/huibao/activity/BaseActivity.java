package com.example.win7.huibao.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.win7.huibao.YApplication;

/**
 * Created by WIN7 on 2018/3/19.
 */

public class BaseActivity extends AppCompatActivity {
    private YApplication      mApplication;
    private ProgressDialog    mProgressDialog;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (YApplication.flag == -1) {//flag为-1说明程序被杀掉
            protectApp();
        }
        YApplication.mBaseActivityList.add(this);

        mApplication = (YApplication) getApplication();
        mApplication.addActivity(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mSharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
    }

    public void showDialog(String msg) {
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void hideDialog() {
        mProgressDialog.hide();
    }

    protected void protectApp() {
        Intent intent = new Intent(this, FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//清空栈里MainActivity之上的所有activty
        startActivity(intent);
        finish();
        YApplication.flag = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
