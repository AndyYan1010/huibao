package com.example.win7.huibao.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.win7.huibao.R;
import com.example.win7.huibao.fragment.ZhuFragment;
import com.example.win7.huibao.fragment.ZiFragment;

import java.util.Map;

public class TaskDetailActivity extends BaseActivity {
    Toolbar     toolbar;
    ZhuFragment zhu;
    ZiFragment  zi;
    Button[]   btns            = new Button[2];
    Fragment[] frags           = null;
    String     taskno, interid = "";
    /**
     * 当前显示的fragment
     */
    int currentIndex = 0;
    /**
     * 选中的button,显示下一个fragment
     */
    int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.taskdetail);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

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

    protected void setViews() {
        taskno = getIntent().getStringExtra("taskno");
        interid = getIntent().getStringExtra("interid");
        btns[0] = (Button) findViewById(R.id.btn_zhu);
        btns[1] = (Button) findViewById(R.id.btn_zi);
        btns[0].setSelected(true);
        zhu = new ZhuFragment();
        zi = new ZiFragment();
        frags = new Fragment[]{zhu, zi};
        // 一开始，显示第一个fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.task_fragment, zhu);
        transaction.show(zhu);
        transaction.commit();
        Bundle bundle = new Bundle();
        bundle.putString("taskno", taskno);
        zhu.setArguments(bundle);
        zi.setArguments(bundle);
    }

    protected void setListeners() {
        for (int i = 0; i < btns.length; i++) {
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        switch (view.getId()) {
                            case R.id.btn_zhu:
                                selectedIndex = 0;
                                break;
                            case R.id.btn_zi:
                                selectedIndex = 1;
                                break;
                        }

                        // 判断单击是不是当前的
                        if (selectedIndex != currentIndex) {
                            // 不是当前的
                            FragmentTransaction transaction = getSupportFragmentManager()
                                    .beginTransaction();
                            // 当前hide
                            transaction.hide(frags[currentIndex]);

                            // show你选中
                            if (!frags[selectedIndex].isAdded()) {
                                // 以前没添加过
                                transaction.add(R.id.task_fragment,
                                        frags[selectedIndex]);
                            }
                            if (selectedIndex == 1) {
                                ZhuFragment frag1 = (ZhuFragment) frags[0];
                                Map<String, String> info = frag1.getInfo();
                                ZiFragment frag = (ZiFragment) frags[1];
                                frag.setInfo(info);
//                                ToastUtils.showToast(getBaseContext(),"接收到了");
                            }
                            // 事务
                            transaction.show(frags[selectedIndex]);
                            transaction.commit();

                            btns[currentIndex].setSelected(false);
                            btns[selectedIndex].setSelected(true);
                            currentIndex = selectedIndex;

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
