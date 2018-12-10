package com.example.win7.huibao.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.win7.huibao.R;
import com.example.win7.huibao.adapter.ContactAdapter;
import com.example.win7.huibao.eventMessege.OnContactUpdateEvent;
import com.example.win7.huibao.util.DBUtils;
import com.example.win7.huibao.util.ThreadUtils;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.view.ContactLayout;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/4/15 9:49
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MailListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ContactAdapter.OnItemClickListener {
    private View           view;
    private ContactLayout  mContactLayout;
    private ContactAdapter mContactAdapter;
    private List<String> contactList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mail_list, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        mContactLayout = (ContactLayout) view.findViewById(R.id.contactLayout);
    }

    private void initData() {
        mContactLayout.setOnRefreshListener(this);
        /**
         * 初始化联系人界面
         * 1. 首先访问本地的缓存联系人
         * 2. 然后开辟子线程去环信后台获取当前用户的联系人
         * 3. 更新本地的缓存，刷新UI
         */
        getLocalAndIntInfo();
        EventBus.getDefault().register(this);
    }

    private void getLocalAndIntInfo() {
        final String currentUser = EMClient.getInstance().getCurrentUser();
        List<String> contacts = DBUtils.getContacts(currentUser);
        contactList.clear();
        contactList.addAll(contacts);
        mContactAdapter = new ContactAdapter(contacts);
        mContactLayout.setAdapter(mContactAdapter);
        mContactAdapter.setOnItemClickListener(this);

        //然后开辟子线程去环信后台获取当前用户的联系人
        updateContactsFromServer(currentUser);
    }

    private void updateContactsFromServer(final String currentUser) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> contactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //排序
                    Collections.sort(contactsFromServer, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    //更新本地的缓存，
                    DBUtils.updateContacts(currentUser, contactsFromServer);
                    contactList.clear();
                    contactList.addAll(contactsFromServer);
                    //通知View刷新UI
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            updateContacts(true, null);
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            updateContacts(false, e.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        /**
         * 1. 访问网络，获取联系人
         * 2. 如果拿到数据了，更新数据库
         * 3. 隐藏下拉刷新
         */
        getMailListInfo();
    }

    public void updateContacts(boolean success, String msg) {
        mContactAdapter.notifyDataSetChanged();
        //隐藏下拉刷新
        mContactLayout.setRefreshing(false);
    }

    private void getMailListInfo() {
        updateContactsFromServer(EMClient.getInstance().getCurrentUser());
    }

    @Override
    public void onItemLongClick(final String contact, int position) {
        Snackbar.make(mContactLayout, "您确定删除" + contact + "联系人吗？", Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteContact(contact);
                    }
                }).show();
    }

    @Override
    public void onItemClick(String contact, int position) {
        //跳转聊天界面//携带contact

    }

    public void deleteContact(final String contact) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contact);
                    afterDelete(contact, true, null);
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    afterDelete(contact, false, e.toString());
                }

            }
        });
    }

    private void afterDelete(final String contact, final boolean success, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onDelete(contact, success, msg);
            }
        });
    }

    public void onDelete(String contact, boolean success, String msg) {
        if (success) {
            ToastUtils.showToast(getActivity(), "已删除");
        } else {
            ToastUtils.showToast(getActivity(), "删除失败，请重试");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OnContactUpdateEvent onContactUpdateEvent) {
        updateContactsFromServer(EMClient.getInstance().getCurrentUser());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
