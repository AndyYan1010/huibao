package com.example.win7.huibao.listener;

import com.example.win7.huibao.util.ThreadUtils;
import com.hyphenate.EMCallBack;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/12 19:43
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public abstract class CallBackListener implements EMCallBack {

    public  abstract void onMainSuccess();

    public abstract void onMainError(int i, String s);

    @Override
    public void onSuccess() {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onMainSuccess();
            }
        });
    }

    @Override
    public void onError(final int i, final String s) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onMainError(i,s);
            }
        });
    }

    @Override
    public void onProgress(int i, String s) {
    }
}
