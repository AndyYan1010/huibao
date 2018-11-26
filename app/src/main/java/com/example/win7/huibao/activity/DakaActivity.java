package com.example.win7.huibao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.view.CustomProgress;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DakaActivity extends BaseActivity {
    Toolbar toolbar;
    Button btn_shang,btn_xia,btn_out;
    LocationClient mLocationClient;
    MyLocationListener myListener = new MyLocationListener();
    String addr,note;
    CustomProgress dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daka);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar)findViewById(R.id.id_toolbar);
        toolbar.setTitle(getResources().getString(R.string.daka));

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_details:
                        startActivity(new Intent(DakaActivity.this, LocationActivity.class));
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void setViews(){
        btn_shang = (Button)findViewById(R.id.btn_shang);
        btn_xia = (Button)findViewById(R.id.btn_xia);
        btn_out = (Button)findViewById(R.id.btn_out);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
    }

    protected void setListeners(){
        btn_shang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(DakaActivity.this);
                new AlertDialog.Builder(DakaActivity.this).setTitle("打卡备注").setView(
                        et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note = et.getText().toString().trim();
                        new DTask("上班",note).execute();
//                        Toast.makeText(DakaActivity.this,"上班"+addr,Toast.LENGTH_SHORT).show();
                    }
                })
                        .setNegativeButton("取消",null).show();
            }
        });

        btn_xia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(DakaActivity.this);
                new AlertDialog.Builder(DakaActivity.this).setTitle("打卡备注").setView(
                        et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note = et.getText().toString().trim();
                        new DTask("下班",note).execute();
//                        Toast.makeText(DakaActivity.this,"下班"+addr,Toast.LENGTH_SHORT).show();
                    }
                })
                        .setNegativeButton("取消",null).show();
            }
        });

        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText et = new EditText(DakaActivity.this);
                new AlertDialog.Builder(DakaActivity.this).setTitle("外出备注").setView(
                        et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note = et.getText().toString().trim();
                        new DTask("外出",note).execute();
//                        Toast.makeText(DakaActivity.this,"下班"+addr,Toast.LENGTH_SHORT).show();
                    }
                })
                        .setNegativeButton("取消",null).show();
            }
        });
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            dialog = CustomProgress.show(DakaActivity.this,"加载中...",true,null);
            addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            int flag = location.getIndoorLocationSurpport();
            String buildingname = location.getIndoorLocationSurpportBuidlingName();
            Log.i("是否支持室内",flag+"=======================");
            Log.i("大楼名称",buildingname+"=======================");
            Log.i("打卡地址",addr+"");
            dialog.dismiss();
        }
    }

    class DTask extends AsyncTask<Void,String,String>{
        String type;
        String note;

        public DTask(String type,String note){
            this.type = type;
            this.note = note;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "Attendance";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/Attendance";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("FAddress", addr);
            rpc.addProperty("FType", type);
            rpc.addProperty("UserName", YApplication.fname);
            rpc.addProperty("Note", note);

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
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
            String result = object.getProperty(0).toString();
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("1")){
                Toast.makeText(DakaActivity.this,"上班打卡成功!打卡地点:"+addr,Toast.LENGTH_LONG).show();
            }else if(s.equals("11")){
                Toast.makeText(DakaActivity.this,"请勿重复打卡!",Toast.LENGTH_LONG).show();
            }else if(s.equals("2")){
                Toast.makeText(DakaActivity.this,"下班打卡成功!打卡地点:"+addr,Toast.LENGTH_LONG).show();
            }else if(s.equals("22")){
                Toast.makeText(DakaActivity.this,"请勿重复打卡!",Toast.LENGTH_LONG).show();
            }else if(s.equals("3")){
                Toast.makeText(DakaActivity.this,"外出打卡成功!",Toast.LENGTH_LONG).show();
            }else if(s.equals("33")){
                Toast.makeText(DakaActivity.this,"请勿重复打卡!",Toast.LENGTH_LONG).show();
            }
        }
    }
}
