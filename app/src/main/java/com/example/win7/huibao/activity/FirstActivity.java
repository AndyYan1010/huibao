package com.example.win7.huibao.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;

public class FirstActivity extends BaseActivity {
    Handler handler = new Handler();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first);
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        sp = getSharedPreferences("token",MODE_PRIVATE);
                        String fname = sp.getString("fname","");
                        String fgroup = sp.getString("fgroup","");
                        String status = sp.getString("status","");
                        //判断是否已登录
                        if(status.equals("")){
                            startActivity(new Intent(FirstActivity.this, LoginActivity.class));
                        }else {
                            YApplication.fname = fname;
                            YApplication.fgroup = fgroup;
                            Intent intent = new Intent(FirstActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
