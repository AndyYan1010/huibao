package com.example.win7.huibao.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ThreadUtils;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.util.Utils;
import com.example.win7.huibao.view.CustomProgress;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class LoginActivity extends BaseActivity {
    EditText et_username, et_password;
    Button            btn_login;
    CustomProgress    dialog;
    LinearLayout      ll_login;
    SharedPreferences sp;
    private ImageView mView_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setViews();
        setListeners();
    }

    protected void setViews() {
        sp = getSharedPreferences("token", MODE_PRIVATE);
        mView_logout = findViewById(R.id.view_logout);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password.requestFocus();
        btn_login = (Button) findViewById(R.id.btn_login);
        String fname = sp.getString("fname", "");
        String fgroup = sp.getString("fgroup", "");
        et_username.setText(fname);
        Utils.autoScrollView(ll_login, btn_login);//弹出软键盘时滚动视图
        //登录环信
        loginToHX(fname, fgroup);
    }

    private void loginToHX(String fname, String fgroup) {
        if (null != sp && !"".equals(fname) && null != fgroup && !"".equals(fgroup)) {
            YApplication.fname = fname;
            YApplication.fgroup = fgroup;
            //登录环信聊天服务器
            EMClient.getInstance().login(fname, "123", new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    ToastUtils.showToast(LoginActivity.this, "hx登录成功");
                    Log.d("main_older", "登录聊天服务器成功！");
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, String message) {
                    Log.d("main_older", "登录聊天服务器失败！");
                }
            });
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    protected void setListeners() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_username.getText().toString().equals("") || et_password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "请填写完整用户名和密码", Toast.LENGTH_SHORT).show();
                } else {
                    //登录K3用户
                    new LTask(et_username.getText().toString(), et_password.getText().toString()).execute();
                }
            }
        });
        mView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = et_username.getText().toString();
                if (null != name && !"".equals(name)) {
                    //退出当前账号
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            Exit(name);
                        }
                    });
                }
            }
        });
    }


    class LTask extends AsyncTask<Void, String, String> {
        String name;
        String pass;

        LTask(String name, String pass) {
            this.name = name;
            this.pass = pass;
        }

        @Override
        protected void onPreExecute() {
            dialog = CustomProgress.show(LoginActivity.this, "登录中...", true, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "Login";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/Login";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("UserName", name);
            rpc.addProperty("PassWord", pass);

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
            try {
                if (null != object) {
                    Log.i("返回结果", object.getProperty(0).toString() + "=========================");
                    String result = object.getProperty(0).toString();//0销售-总部/X001   0销售-总部
                    if (result.substring(0, 1).equals("0")) {
                        YApplication.fname = name;
                        YApplication.fgroup = result.substring(2);
                        //                        YApplication.fgroup = result.replace("0", "");
                        return "0";
                    } else if (result.equals("1")) {
                        return "1";
                    } else if (result.equals("2")) {
                        return "2";
                    } else if (result.equals("3")) {
                        return "3";
                    }
                }
            } catch (Exception e) {
                return "4";
            }
            return "4";
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if (s.equals("0")) {
                //登录环信聊天服务器
                EMClient.getInstance().login(YApplication.fname, "123", new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Log.d("main_older", "登录聊天服务器成功！");
                        YApplication.isHuanXinLogin = true;
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.d("main_older", "登录聊天服务器失败！");
                        YApplication.isHuanXinLogin = false;
                    }
                });
                //保存用户名，用户组
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("fname", name);
                editor.putString("fgroup", YApplication.fgroup);
                editor.putString("status", "1");
                editor.commit();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                finish();
            } else if (s.equals("1")) {
                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            } else if (s.equals("2")) {
                Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
            } else if (s.equals("3")) {
                Toast.makeText(LoginActivity.this, "当前账号已在线", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    protected static void Exit(String fname) {
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
    }
}
