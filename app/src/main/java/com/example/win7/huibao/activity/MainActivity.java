package com.example.win7.huibao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.fragment.MeFragment;
import com.example.win7.huibao.fragment.MsgFragment;
import com.example.win7.huibao.fragment.OrderFragment;
import com.example.win7.huibao.fragment.TaskFragment;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

public class MainActivity extends BaseActivity {
    MsgFragment   msg;
    OrderFragment order;
    TaskFragment  task;
    MeFragment    me;
    Button[]   btns      = new Button[4];
    Fragment[] fragments = null;
    private long exitTime = 0;
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
        setContentView(R.layout.activity_main);
        YApplication.mBaseActivityList.add(this);
        setViews();
        setListeners();
    }

    protected void setViews() {
        btns[0] = (Button) findViewById(R.id.btn_msg);//消息
        //        btns[1] = (Button) findViewById(R.id.btn_msg);//通讯录
        btns[1] = (Button) findViewById(R.id.btn_task);//任务
        btns[2] = (Button) findViewById(R.id.btn_order);//订单
        btns[3] = (Button) findViewById(R.id.btn_me);//我的
        btns[0].setSelected(true);

        msg = new MsgFragment();
        task = new TaskFragment();
        order = new OrderFragment();
        me = new MeFragment();
        fragments = new Fragment[]{msg, task, order, me};

        // 一开始，显示第一个fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, msg);
        transaction.show(msg);
        transaction.commit();
    }

    protected void setListeners() {
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());

        for (int i = 0; i < btns.length; i++) {
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        switch (view.getId()) {
                            case R.id.btn_msg:
                                selectedIndex = 0;
                                break;
                            case R.id.btn_task:
                                selectedIndex = 1;
                                break;
                            case R.id.btn_order:
                                selectedIndex = 2;
                                break;
                            case R.id.btn_me:
                                selectedIndex = 3;
                                break;
                        }

                        // 判断单击是不是当前的
                        if (selectedIndex != currentIndex) {
                            // 不是当前的
                            FragmentTransaction transaction = getSupportFragmentManager()
                                    .beginTransaction();
                            // 当前hide
                            transaction.hide(fragments[currentIndex]);
                            // show你选中
                            if (!fragments[selectedIndex].isAdded()) {
                                // 以前没添加过
                                transaction.add(R.id.fragment_container,
                                        fragments[selectedIndex]);
                            }
                            // 事务
                            transaction.show(fragments[selectedIndex]);
                            transaction.commit();
                            if (fragments[selectedIndex] == me) {
                                me.searchPersonInfo();
                            }
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


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除

                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录

                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器

                        } else {
                            //当前网络不可用，请检查网络设置

                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出应用",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            YApplication.exit();
        }
    }

    @Override
    protected void protectApp() {
        super.protectApp();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
