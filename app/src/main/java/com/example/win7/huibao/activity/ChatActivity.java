package com.example.win7.huibao.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.win7.huibao.R;
import com.example.win7.huibao.adapter.MyChatAdapter;
import com.example.win7.huibao.entity.Item;
import com.example.win7.huibao.listener.CallBackListener;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.util.Utils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends BaseActivity implements View.OnClickListener {
    EditText et_msg;
    //    TextView        tv_send;
    Button   bt_send;
    String   nickname, username;
    RecyclerView    rcv_msg;
    //    ChatAdapter     adapter;
    ArrayList<Item> list;
    String          content;
    Toolbar         toolbar;
    LinearLayout    ll_chat, ll_bottom;
    public static final int             SHOW_RESPONSE  = 0;
    private             List<EMMessage> mEMMessageList = new ArrayList<>();
    private MyChatAdapter mChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EventBus.getDefault().register(this);
        initView();
        setTool();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
        et_msg = (EditText) findViewById(R.id.et_msg);
        //        tv_send = (TextView)findViewById(R.id.tv_send);
        bt_send = (Button) findViewById(R.id.bt_send);
        rcv_msg = (RecyclerView) findViewById(R.id.rcv_msg);
    }

    protected void setTool() {
        nickname = getIntent().getStringExtra("nickname");
        username = getIntent().getStringExtra("name");
        if (TextUtils.isEmpty(username)) {
            ToastUtils.showToast(this, "stub,请携带username参数！");
            finish();
            return;
        }
        username = username.toLowerCase();
        toolbar.setTitle(nickname);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //根据name获取聊天数据
        getTalkData();
        setViews();
        setListeners();
    }

    private void getTalkData() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation != null) {
            //需要将所有的未读消息标记为已读
            conversation.markAllMessagesAsRead();
            //            //获取此会话的所有消息
            //            List<EMMessage> messages = conversation.getAllMessages();
            //曾经有聊天过,那么获取最多最近的20条聊天记录，然后展示到View层
            //获取最后一条消息
            EMMessage lastMessage = conversation.getLastMessage();
            //获取最后一条消息之前的19条（最多）
            int count = 19;
            if (mEMMessageList.size() >= 19) {
                count = mEMMessageList.size();
            }
            List<EMMessage> messageList = conversation.loadMoreMsgFromDB(lastMessage.getMsgId(), count);
            Collections.reverse(messageList);
            mEMMessageList.clear();

            EMTextMessageBody body = (EMTextMessageBody) lastMessage.getBody();
            String message = body.getMessage();
            if (!message.startsWith("{goodsId}")){
                mEMMessageList.add(lastMessage);
            }
            for (int i =0;i<messageList.size();i++){
                EMMessage emMessage = messageList.get(i);
                EMTextMessageBody body1 = (EMTextMessageBody) emMessage.getBody();
                String message1 = body1.getMessage();
                if (!message1.startsWith("{goodsId}")){
                    mEMMessageList.add(messageList.get(i));
                }
            }
//            mEMMessageList.addAll(messageList);
            Collections.reverse(mEMMessageList);
        } else {
            mEMMessageList.clear();
        }
    }

    protected void setViews() {
        //        list = new ArrayList<>();
        //        adapter = new ChatAdapter();
        //        rcv_msg.setHasFixedSize(true);
        //        rcv_msg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //        rcv_msg.setAdapter(adapter);
        ////        adapter.replaceAll(list);
        Utils.autoScrollView(ll_chat, ll_bottom);//弹出软键盘时滚动视图
        rcv_msg.setLayoutManager(new LinearLayoutManager(this));
        mChatAdapter = new MyChatAdapter(mEMMessageList);
        rcv_msg.setAdapter(mChatAdapter);
        if (mEMMessageList.size() != 0) {
            rcv_msg.scrollToPosition(mEMMessageList.size() - 1);
        }
    }

    protected void setListeners() {
        //        et_msg.addTextChangedListener(new TextWatcher() {
        //            @Override
        //            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
        //            }
        //
        //            @Override
        //            public void onTextChanged(CharSequence s, int start, int before, int count) {
        //
        //            }
        //
        //            @Override
        //            public void afterTextChanged(Editable s) {
        //                content = s.toString().trim();
        //            }
        //        });

        //        tv_send.setOnClickListener(new View.OnClickListener() {
        //            @TargetApi(Build.VERSION_CODES.M)
        //            @Override
        //            public void onClick(View view) {
        //                content = et_msg.getText().toString();
        //                if(content.equals("")){
        //                    Toast.makeText(ChatActivity.this,"不能发送空消息",Toast.LENGTH_SHORT).show();
        //                }else {
        //                    ArrayList<Item> data = new ArrayList<>();
        //                    //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        //                    EMMessage message = EMMessage.createTxtSendMessage(content, nickname);
        //                    //发送消息
        //                    EMClient.getInstance().chatManager().sendMessage(message);
        //                    //将消息放到右边
        //                    Chat chat = new Chat();
        //                    chat.setIcon("http://wx.qlogo.cn/mmopen/V1RSN8IUDAyVjF42G3G1lTBAEiaiam5hmhmnm0MmFRmf61oicTJhVTRAOS9tqjhMc5hmRcGNFQR2VACicSic6xq0m34lpPAKIcOdJ/0");
        //                    chat.setContent(content);
        //                    data.add(new Item(Item.CHAT_B,chat));
        //                    adapter.addAll(data);
        //                    et_msg.setText("");
        //                    hideKeyBorad(et_msg);
        //                }
        //            }
        //        });
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    bt_send.setEnabled(false);
                } else {
                    bt_send.setEnabled(true);
                }
            }
        });
        bt_send.setOnClickListener(this);
        String msg = et_msg.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            bt_send.setEnabled(false);
        } else {
            bt_send.setEnabled(true);
        }
    }

    //    EMMessageListener msgListener = new EMMessageListener() {
    //
    //        @Override
    //        public void onMessageReceived(final List<EMMessage> messages) {
    //            //收到消息
    //            //将消息放到左边
    //            runOnUiThread(new Runnable() {
    //                @Override
    //                public void run() {
    //                    ArrayList<Item> data1 = new ArrayList<>();
    //                    Chat chat = new Chat();
    //                    chat.setIcon("http://wx.qlogo.cn/mmopen/V1RSN8IUDAyVjF42G3G1lTBAEiaiam5hmhmnm0MmFRmf61oicTJhVTRAOS9tqjhMc5hmRcGNFQR2VACicSic6xq0m34lpPAKIcOdJ/0");
    //                    chat.setContent(messages.get(0).getBody().toString().replace("txt:\"","").replace("\"",""));
    //                    data1.add(new Item(Item.CHAT_A,chat));
    //                    adapter.addAll(data1);
    //                }
    //            });
    //            hideKeyBorad(et_msg);
    //            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    //            Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
    //            rt.play();
    //        }
    //
    //        @Override
    //        public void onCmdMessageReceived(List<EMMessage> messages) {
    //            //收到透传消息
    //
    //        }
    //
    //        @Override
    //        public void onMessageRead(List<EMMessage> messages) {
    //            //收到已读回执
    //
    //        }
    //
    //        @Override
    //        public void onMessageDelivered(List<EMMessage> message) {
    //            //收到已送达回执
    //
    //        }
    //        @Override
    //        public void onMessageRecalled(List<EMMessage> messages) {
    //            //消息被撤回
    //
    //        }
    //        @Override
    //        public void onMessageChanged(EMMessage message, Object change) {
    //            //消息状态变动
    //
    //        }
    //    };


    private void hideKeyBorad(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        //记得在不需要的时候移除listener，如在activity的onDestroy()时
        //        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send:
                String msg = et_msg.getText().toString();
                EMMessage emMessage = EMMessage.createTxtSendMessage(msg, username);
                emMessage.setStatus(EMMessage.Status.INPROGRESS);
                mEMMessageList.add(emMessage);
                onUpdata(mEMMessageList.size());
                emMessage.setMessageStatusCallback(new CallBackListener() {
                    @Override
                    public void onMainSuccess() {
                        onUpdata(mEMMessageList.size());
                    }

                    @Override
                    public void onMainError(int i, String s) {
                        onUpdata(mEMMessageList.size());
                    }
                });
                EMClient.getInstance().chatManager().sendMessage(emMessage);
                et_msg.getText().clear();
                break;
        }
    }

    private void onUpdata(int size) {
        mChatAdapter.notifyDataSetChanged();
        if (size != 0) {
            rcv_msg.smoothScrollToPosition(size - 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage) {
        //当收到信消息的时候
        /*
         *  判断当前这个消息是不是正在聊天的用户给我发的
         *  如果是，让ChatPresenter 更新数据
         *
         */
        String from = emMessage.getFrom();
        if (from.equals(username)) {
            getTalkData();
            onUpdata(mEMMessageList.size());
        }
    }
}
