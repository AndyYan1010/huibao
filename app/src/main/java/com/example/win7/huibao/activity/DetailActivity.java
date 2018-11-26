package com.example.win7.huibao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.win7.huibao.R;

public class DetailActivity extends BaseActivity {
    String title,content;
    Toolbar toolbar;
    TextView tv_de;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        Log.i("DetailActivity",content+"========================");
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar)findViewById(R.id.id_toolbar);
        toolbar.setTitle(title);

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
        tv_de = (TextView)findViewById(R.id.tv_de);
        tv_de.setText(content);
    }

    protected void setListeners(){

    }
}
