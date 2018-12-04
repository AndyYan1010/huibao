package com.example.win7.huibao.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.activity.ChatActivity;
import com.example.win7.huibao.adapter.MsgAdapter;
import com.example.win7.huibao.entity.Msg;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.view.CustomProgress;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MsgFragment extends Fragment {
    Context            mContext;
    View               view;
    ListView           lv_msg;
    SwipeRefreshLayout srl_msg;
    MsgAdapter         adapter;
    List<Msg>          list;
    CustomProgress     dialog;
    Toolbar            toolbar;
    private List<Msg> mEMConversationList = new ArrayList<>();
    private static int REQUEST_CODE =10086;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_msg, container, false);
        setTool();
        setViews();
        setListeners();
        return view;
    }

    protected void setTool() {
        toolbar = (Toolbar) view.findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.msg);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    protected void setViews() {
        lv_msg = (ListView) view.findViewById(R.id.lv_msg);
        srl_msg = (SwipeRefreshLayout) view.findViewById(R.id.srl_msg);
        list = new ArrayList<>();
        new NTask().execute();
    }

    protected void setListeners() {
        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                //                intent.putExtra("nickname", list.get(i).getNickname());
                //                intent.putExtra("name", list.get(i).getUsername());
                intent.putExtra("nickname", mEMConversationList.get(i).getNickname());
                intent.putExtra("name", mEMConversationList.get(i).getUsername());
//                startActivity(intent);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        srl_msg.setColorSchemeResources(new int[]{R.color.bottom_button_text_green, R.color.colorPrimary});
        srl_msg.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NTask().execute();
            }
        });
        EventBus.getDefault().register(this);
    }

    class NTask extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            list.clear();
            dialog = CustomProgress.show(mContext, "加载中...", true, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                String s = "(";
                for (String username : usernames) {
                    s = s + "'" + username + "',";
                }
                s = s.substring(0, s.length() - 1);
                s = s + ")";
                Log.i("拼接的数据集", s + "=================================");

                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "JA_select";
                // EndPoint
                String endPoint = Consts.ENDPOINT;
                // SOAP Action
                String soapAction = "http://tempuri.org/JA_select";

                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
                Log.i("昵称查询语句", "select a.fname from t_emp a inner join t_user d on a.fitemid=b.fempid where d.fname in" + s + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                rpc.addProperty("FSql", "select a.fname,b.fname name from t_emp a inner join t_user b on a.fitemid=b.fempid where b.fname in" + s);
                rpc.addProperty("FTable", "t_user");

                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.bodyOut = rpc;
                // 设置是否调用的是dotNet开发的WebService
                envelope.dotNet = true;
                // 等价于envelope.bodyOut = rpc;
                envelope.setOutputSoapObject(rpc);

                HttpTransportSE transport = new HttpTransportSE(endPoint);
                // 调用WebService
                transport.call(soapAction, envelope);
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;

                // 获取返回的结果
                Log.i("返回结果", object.getProperty(0).toString() + "=========================");
                String result = object.getProperty(0).toString();
                Document doc = null;

                try {
                    doc = DocumentHelper.parseText(result); // 将字符串转为XML

                    Element rootElt = doc.getRootElement(); // 获取根节点

                    System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称


                    Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head

                    // 遍历head节点
                    while (iter.hasNext()) {
                        Element recordEle = (Element) iter.next();
                        Msg msg = new Msg();
                        msg.setNickname(recordEle.elementTextTrim("fname"));
                        msg.setUsername(recordEle.elementTextTrim("name"));
                        list.add(msg);
                    }
                    //获取对应的会话列表,放入mEMConversationList集合
                    getAllChatInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            srl_msg.setRefreshing(false);
            if (null == adapter) {
                adapter = new MsgAdapter(mContext, mEMConversationList);
                lv_msg.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            super.onPostExecute(s);
        }
    }

    private void getAllChatInfo() {
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        mEMConversationList.clear();
        Collection<EMConversation> values = allConversations.values();
        for (int i = 0; i < list.size(); i++) {
            Msg msg = list.get(i);
            String userid = msg.getUsername().toLowerCase();
            String nickname = msg.getNickname();
            for (EMConversation emConversation : values) {
                String conversationId = emConversation.conversationId();
                if (userid.equals(conversationId)) {
                    EMTextMessageBody emMessageBody = (EMTextMessageBody) emConversation.getLastMessage().getBody();
                    String message = emMessageBody.getMessage();
                    long msgTime = emConversation.getLastMessage().getMsgTime();
                    int unreadMsgCount = emConversation.getUnreadMsgCount();
                    msg.setLastmsg(message);
                    msg.setLastMsgTime(msgTime);
                    msg.setUnreadMsgCount(unreadMsgCount);
//                    if (!message.startsWith("{goodsId}")){
//                    }
                }
            }
            mEMConversationList.add(msg);
        }
        /**
         * 排序，最近的时间在最上面(时间的倒序)
         */
        Collections.sort(mEMConversationList, new Comparator<Msg>() {
            @Override
            public int compare(Msg o1, Msg o2) {
                if (o1.getLastMsgTime() == 0 || o2.getLastMsgTime() == 0) {
                    return (int) Long.MAX_VALUE;
                } else {
                    return (int) (o2.getLastMsgTime() - o1.getLastMsgTime());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage) {
        ToastUtils.showToast(getActivity(), "收到新消息");
        getAllChatInfo();
        if (null == adapter) {
            adapter = new MsgAdapter(mContext, mEMConversationList);
            lv_msg.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE==requestCode){
            new NTask().execute();
        }
    }
}
