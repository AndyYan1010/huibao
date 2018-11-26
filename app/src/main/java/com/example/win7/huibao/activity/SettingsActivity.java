package com.example.win7.huibao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;

public class SettingsActivity extends BaseActivity {
    Toolbar toolbar;
    Button btn_logout,btn_notify_pwd,btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar)findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.settings);

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setViews(){
        btn_logout = (Button)findViewById(R.id.btn_logout);
        btn_notify_pwd = (Button)findViewById(R.id.btn_notify_pwd);
        btn_update = (Button)findViewById(R.id.btn_update_apk);
    }

    protected void setListeners(){
        btn_notify_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this,PwdNotifyActivity.class));
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YApplication.exit();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });
//        btn_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SettingsActivity.this,UpdateActivity.class));
//            }
//        });
    }
}
