package com.example.win7.huibao.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.view.HorizontalselectedView;

import java.util.ArrayList;
import java.util.List;

public class BasicDataActivity extends BaseActivity {
    Toolbar toolbar;
    HorizontalselectedView hsv_main;
    List<String> strings = new ArrayList<>();
    TextView tv_left,tv_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_data);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar)findViewById(R.id.id_toolbar);
        toolbar.setTitle(getResources().getString(R.string.basicdata));

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
        hsv_main = (HorizontalselectedView)findViewById(R.id.hsv_main);
        strings.add("公司");
        strings.add("网银");
        strings.add("部门");
        strings.add("预算");
        strings.add("职员");
        strings.add("物料");
        strings.add("仓库");
        strings.add("计划");
        hsv_main.setData(strings);
        tv_left = (TextView)findViewById(R.id.tv_left);
        tv_right = (TextView)findViewById(R.id.tv_right);
        Toast.makeText(BasicDataActivity.this,hsv_main.getSelectedString(),Toast.LENGTH_SHORT).show();
    }

    protected void setListeners(){
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hsv_main.setAnRightOffset();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hsv_main.setAnLeftOffset();
            }
        });
    }


}
