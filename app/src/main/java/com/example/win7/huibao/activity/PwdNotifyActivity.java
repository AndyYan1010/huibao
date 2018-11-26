package com.example.win7.huibao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.view.CustomProgress;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class PwdNotifyActivity extends BaseActivity {
    Toolbar toolbar;
    EditText et_newpwd,et_repeatpwd;
    Button btn_submit;
    String newpwd,repeatpwd;
    CustomProgress dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_notify);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar)findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.notifypwd);

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PwdNotifyActivity.this).setTitle("确认放弃修改?").setIcon(
                        android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                        .setNegativeButton("取消", null).show();
            }
        });
    }

    protected void setViews(){
        et_newpwd = (EditText)findViewById(R.id.et_newpwd);
        et_repeatpwd = (EditText)findViewById(R.id.et_repeatpwd);
        btn_submit = (Button)findViewById(R.id.btn_submit);
    }

    protected void setListeners(){
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newpwd = et_newpwd.getText().toString();
                repeatpwd = et_repeatpwd.getText().toString();
                if(newpwd.equals("")||repeatpwd.equals("")){
                    Toast.makeText(PwdNotifyActivity.this,"请填写完整两次密码",Toast.LENGTH_SHORT).show();
                }else if(!newpwd.equals(repeatpwd)){
                    Toast.makeText(PwdNotifyActivity.this,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                }else{
                    new PTask(newpwd).execute();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(PwdNotifyActivity.this).setTitle("确认放弃修改?").setIcon(
                    android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    class PTask extends AsyncTask<Void,String,String>{
        String pass;

        PTask(String pass){
            this.pass = pass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = CustomProgress.show(PwdNotifyActivity.this,"提交中...",true,null);
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "EditPass";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/EditPass";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("UserName", YApplication.fname);
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
            if (null != object) {
                Log.i("返回结果", object.getProperty(0).toString()+"=========================");
                String result = object.getProperty(0).toString();
                if (result.equals("成功")) {
                    return "1";
                } else {
                    return "2";
                }
            }
            return "3";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(s.equals("1")){
                Toast.makeText(PwdNotifyActivity.this,"修改成功，请重新登录",Toast.LENGTH_SHORT).show();
                YApplication.exit();
                startActivity(new Intent(PwdNotifyActivity.this,LoginActivity.class));
            }else{
                Toast.makeText(PwdNotifyActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
