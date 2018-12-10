package com.example.win7.huibao;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.example.win7.huibao.activity.BaseActivity;
import com.example.win7.huibao.activity.ChatActivity;
import com.example.win7.huibao.activity.CheckActivity;
import com.example.win7.huibao.activity.LoginActivity;
import com.example.win7.huibao.activity.MainActivity;
import com.example.win7.huibao.adapter.MessageListenerAdapter;
import com.example.win7.huibao.eventMessege.OnContactUpdateEvent;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.DBUtils;
import com.example.win7.huibao.util.ThreadUtils;
import com.example.win7.huibao.util.ToastUtils;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by WIN7 on 2018/3/15.
 */

public class YApplication extends Application {
    public static ArrayList<BaseActivity> mBaseActivityList = new ArrayList<BaseActivity>();
    public static String                  fname             = "";
    public static String                  fgroup            = "";
    public static int                     flag              = -1;//判断是否被回收
    public static boolean   isHuanXinLogin;
    private       SoundPool mSoundPool;
    private       int       mDuanSound;
    private       int       mYuluSound;
    private int markExamine = 10;
    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        //        sp = getSharedPreferences("token", MODE_PRIVATE);
        //        String fname = sp.getString("fname", "");
        //        YApplication.fname = fname;
        initHuanxin();
        initSoundPool();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initHuanxin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        /**
         * 下面的代码是为了避免环信被初始化2次
         */
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        DBUtils.initDB(this);
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        //添加通讯录监听
        initContactListener();
        //添加消息的监听
        initMessageListener();
        //监听连接状态的改变
        initConnectionListener();
    }

    private void initContactListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactAdded(String username) {
                //好友请求被同意
                //发出通知让ContactFragment更新UI
                EventBus.getDefault().post(new OnContactUpdateEvent(username, true));
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                EventBus.getDefault().post(new OnContactUpdateEvent(username, false));
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                //同意或者拒绝
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFriendRequestAccepted(String username) {
                //增加了联系人时回调此方法
            }

            @Override
            public void onFriendRequestDeclined(String username) {
                //好友请求被拒绝
            }
        });
    }

    private void initConnectionListener() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected(int i) {
                if (i == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    /**
                     *  将当前任务栈中所有的Activity给清空掉
                     *  重新打开登录界面
                     */
                    for (BaseActivity baseActivity : mBaseActivityList) {
                        baseActivity.finish();
                    }

                    Intent intent = new Intent(YApplication.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(YApplication.this, "您已在其他设备上登录了，请重新登录。");
                        }
                    });

                }
            }
        });

    }

    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        mDuanSound = mSoundPool.load(this, R.raw.yulu, 1);
        mYuluSound = mSoundPool.load(this, R.raw.yulu, 1);
    }

    private void initMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new MessageListenerAdapter() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                super.onMessageReceived(list);
                if (list != null && list.size() > 0) {
                    /**
                     * 1. 判断当前应用是否在后台运行
                     * 2. 如果是在后台运行，则发出通知栏
                     * 3. 如果是在后台发出长声音
                     * 4. 如果在前台发出短声音
                     */
                    sendNotification(list.get(0), 1);
                    if (isRuninBackground()) {
                        sendNotification(list.get(0), 0);
                        //发出长声音
                        //参数2/3：左右喇叭声音的大小
                        mSoundPool.play(mYuluSound, 1, 1, 0, 0, 1);
                    } else {
                        //发出短声音
                        mSoundPool.play(mDuanSound, 1, 1, 0, 0, 1);
                    }
                    EMMessage emMessage = list.get(0);
                    EventBus.getDefault().post(list.get(0));
                }
            }
        });
    }


    public static void exit() {
        try {
            for (Activity activity : mBaseActivityList) {
                SharedPreferences sp = activity.getSharedPreferences("token", MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.remove("fgroup");
                ed.remove("status");
                ed.commit();
                EMClient.getInstance().logout(true);
                activity.finish();
                Log.i("退出", activity.toString() + " finish了");
            }
            new ETask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    static class ETask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "Z_Exit";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/Z_Exit";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("UserName", fname);

            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = true;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);

            HttpTransportSE transport = new HttpTransportSE(endPoint);
            try {
                // 调用WebService
                transport.call(soapAction, envelope);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("LoginActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            if (null != object) {
                Log.i("返回结果", object.getProperty(0).toString() + "=========================");
                String result = object.getProperty(0).toString();
                if (result.equals("成功")) {
                    return "1";
                } else {
                    return "2";
                }
            }
            return "3";
        }
    }

    private boolean isRuninBackground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        if (runningTaskInfo.topActivity.getPackageName().equals(getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    private void sendNotification(EMMessage message, int kind) {//kind=0 在后台，kind=1在前台
        EMTextMessageBody messageBody = (EMTextMessageBody) message.getBody();
        String messageContent = messageBody.getMessage();
        if (messageContent.startsWith("{goodsId}")) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            messageContent = messageContent.substring(9, messageContent.length());
            //延时意图
            /**
             * 参数2：请求码 大于1
             */
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent chatIntent = new Intent(this, CheckActivity.class);
            chatIntent.putExtra("goodsId", messageContent);
            Intent[] intents = {mainIntent, chatIntent};
            PendingIntent pendingIntent = PendingIntent.getActivities(this, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this)
                    .setAutoCancel(true) //当点击后自动删除
                    .setSmallIcon(R.mipmap.message) //必须设置
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("您有一条新的待审核消息")
                    .setContentText(messageContent)
                    .setContentInfo(message.getFrom())
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .build();
            notificationManager.notify(markExamine, notification);
            markExamine++;
        } else {
            if (kind == 0) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //延时意图
                /**
                 * 参数2：请求码 大于1
                 */
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent chatIntent = new Intent(this, ChatActivity.class);
                chatIntent.putExtra("nickname", message.getFrom());
                chatIntent.putExtra("name", message.getFrom());

                Intent[] intents = {mainIntent, chatIntent};
                PendingIntent pendingIntent = PendingIntent.getActivities(this, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new Notification.Builder(this)
                        .setAutoCancel(true) //当点击后自动删除
                        .setSmallIcon(R.mipmap.message) //必须设置
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("您有一条新消息")
                        .setContentText(messageContent)
                        .setContentInfo(message.getFrom())
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
                notificationManager.notify(1, notification);
            }
        }
    }

    public void addActivity(BaseActivity activity) {
        if (!mBaseActivityList.contains(activity)) {
            mBaseActivityList.add(activity);
        }
    }

    public void removeActivity(BaseActivity activity) {
        mBaseActivityList.remove(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }
}
