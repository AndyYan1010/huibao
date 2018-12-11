package com.example.win7.huibao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.activity.ChatActivity;
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

public class MailListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ContactAdapter.OnItemClickListener, View.OnClickListener {
    private View           view;
    private ImageView      img_back;
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
        img_back = (ImageView) view.findViewById(R.id.img_back);
    }

    private void initData() {
        mContactLayout.setOnRefreshListener(this);
        mContactLayout.setOnClickListener(this);
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

                    //new NTask(currentUser, contactsFromServer).execute();


                    //更新本地的缓存
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
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("nickname", contact);
        intent.putExtra("name", contact);
        startActivity(intent);
        //        startActivityForResult(intent, REQUEST_CODE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                getActivity().finish();
                break;
        }
    }

    //    private List<Msg>      list;
    //    private CustomProgress dialog;

    //    class NTask extends AsyncTask<Void, String, String> {
    //        private String       currentUser;
    //        private List<String> contactsFromServer;
    //
    //        public NTask(String currentUser, List<String> contactsFromServer) {
    //            this.currentUser = currentUser;
    //            this.contactsFromServer = contactsFromServer;
    //        }
    //
    //        @Override
    //        protected void onPreExecute() {
    //            if (null == list) {
    //                list = new ArrayList<>();
    //            } else {
    //                list.clear();
    //            }
    //            ThreadUtils.runOnMainThread(new Runnable() {
    //                @Override
    //                public void run() {
    //                    dialog = CustomProgress.show(getContext(), "加载中...", true, null);
    //                }
    //            });
    //            super.onPreExecute();
    //        }
    //
    //        @Override
    //        protected String doInBackground(Void... voids) {
    //            try {
    //                List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
    //                String s = "(";
    //                for (String username : usernames) {
    //                    s = s + "'" + username + "',";
    //                }
    //                s = s.substring(0, s.length() - 1);
    //                s = s + ")";
    //                Log.i("拼接的数据集", s + "=================================");
    //
    //                // 命名空间
    //                String nameSpace = "http://tempuri.org/";
    //                // 调用的方法名称
    //                String methodName = "JA_select";
    //                // EndPoint
    //                String endPoint = Consts.ENDPOINT;
    //                // SOAP Action
    //                String soapAction = "http://tempuri.org/JA_select";
    //
    //                // 指定WebService的命名空间和调用的方法名
    //                SoapObject rpc = new SoapObject(nameSpace, methodName);
    //
    //                // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
    //                Log.i("昵称查询语句", "select a.fname from t_emp a inner join t_user d on a.fitemid=b.fempid where d.fname in" + s + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    //                rpc.addProperty("FSql", "select a.fname,b.fname name from t_emp a inner join t_user b on a.fitemid=b.fempid where b.FDescription in" + s);
    //                rpc.addProperty("FTable", "t_user");
    //
    //                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
    //                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
    //
    //                envelope.bodyOut = rpc;
    //                // 设置是否调用的是dotNet开发的WebService
    //                envelope.dotNet = true;
    //                // 等价于envelope.bodyOut = rpc;
    //                envelope.setOutputSoapObject(rpc);
    //
    //                HttpTransportSE transport = new HttpTransportSE(endPoint);
    //                // 调用WebService
    //                transport.call(soapAction, envelope);
    //                // 获取返回的数据
    //                SoapObject object = (SoapObject) envelope.bodyIn;
    //
    //                // 获取返回的结果
    //                Log.i("返回结果", object.getProperty(0).toString() + "=========================");
    //                String result = object.getProperty(0).toString();
    //                Document doc = null;
    //
    //                try {
    //                    doc = DocumentHelper.parseText(result); // 将字符串转为XML
    //                    Element rootElt = doc.getRootElement(); // 获取根节点
    //                    Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
    //
    //                    // 遍历head节点
    //                    while (iter.hasNext()) {
    //                        Element recordEle = (Element) iter.next();
    //                        Msg msg = new Msg();
    //                        msg.setNickname(recordEle.elementTextTrim("fname"));
    //                        msg.setUsername(recordEle.elementTextTrim("name"));
    //                        list.add(msg);
    //                    }
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //            return null;
    //        }
    //
    //        @Override
    //        protected void onPostExecute(String s) {
    //            dialog.dismiss();
    //            contactsFromServer.clear();
    //            for (Msg msg : list) {
    //                contactsFromServer.add(msg.getNickname());
    //            }
    //            getAllChatInfo(contactsFromServer);
    //            //更新本地的缓存
    //            DBUtils.updateContacts(currentUser, contactsFromServer);
    //            contactList.clear();
    //            contactList.addAll(contactsFromServer);
    //            //通知View刷新UI
    //            ThreadUtils.runOnMainThread(new Runnable() {
    //                @Override
    //                public void run() {
    //                    updateContacts(true, null);
    //                }
    //            });
    //            super.onPostExecute(s);
    //        }
    //    }
    //
    //    private final static Comparator<Object> CHINA_COMPARE = Collator.getInstance(java.util.Locale.CHINA);
    //
    //    private void getAllChatInfo(List<String> list) {
    //        /**
    //         * 排序，字母小的在最上面(字母排序)
    //         */
    //        Collections.sort(list, CHINA_COMPARE);
    //    }

    //    public void listSortByName1() {
    //        Collections.sort(list, new Comparator<Msg>() {
    //            @Override
    //            public int compare(Msg o1, Msg o2) {
    //                return Collator.getInstance(Locale.CHINESE).compare(o1.getName(), o2.getName());
    //            }
    //        });
    //
    //        for (String testEntity : list) {
    //            System.out.println(testEntity.toString());
    //        }
    //    }
}
