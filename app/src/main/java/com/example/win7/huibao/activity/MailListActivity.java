package com.example.win7.huibao.activity;

import android.os.Bundle;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.fragment.MailListFragment;

/**
 * @创建者 AndyYan
 * @创建时间 2018/12/4 14:04
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MailListActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        YApplication.mBaseActivityList.add(this);
        setViews();
        setData();
    }

    private void setViews() {
        android.support.v4.app.FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
        MailListFragment contListFt = new MailListFragment();
        ftt.add(R.id.frame, contListFt, "contListFt");
//        ftt.addToBackStack(null);
        ftt.commit();
    }

    private void setData() {

    }
}
