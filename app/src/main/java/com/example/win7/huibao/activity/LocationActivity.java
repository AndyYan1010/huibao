package com.example.win7.huibao.activity;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.view.CustomProgress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Calendar;
import java.util.Iterator;

public class LocationActivity extends BaseActivity {
    Toolbar toolbar;
    TextView tv_date,tv_sdkt,tv_sdkl,tv_xdkt,tv_xdkl,tv_wdkt,tv_wdkl,tv_beizhu;
    String shang,xia,wai,slocate,xlocate,wlocate,note="";
    private int year,month,day;
    CustomProgress dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle(getResources().getString(R.string.details));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setViews(){
        tv_date = (TextView)findViewById(R.id.tv_date);
        tv_sdkt = (TextView)findViewById(R.id.tv_sdkt);
        tv_sdkl = (TextView)findViewById(R.id.tv_sdkl);
        tv_xdkt = (TextView)findViewById(R.id.tv_xdkt);
        tv_xdkl = (TextView)findViewById(R.id.tv_xdkl);
        tv_wdkt = (TextView)findViewById(R.id.tv_wdkt);
        tv_wdkl = (TextView)findViewById(R.id.tv_wdkl);
        tv_beizhu = (TextView)findViewById(R.id.tv_beizhu);
        Calendar mycalendar = Calendar.getInstance();
        year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day = mycalendar.get(Calendar.DAY_OF_MONTH);
    }

    protected void setListeners(){
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(LocationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String s;
                        if(monthOfYear+1<10&&dayOfMonth<10){
                            s = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
                        }else if(monthOfYear+1<10){
                            s = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
                        }else if(dayOfMonth<10){
                            s = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                        }else{
                            s = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        }
                        try {
                            tv_date.setText(s);
                            new LTask(s).execute();
                        } catch (Exception e) {
                        }
                    }
                }, year, month, day);
                dpd.show();//显示DatePickerDialog组件
            }
        });
    }

    class LTask extends AsyncTask<Void,String,String>{
        String date;

        LTask(String date){
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
            dialog = CustomProgress.show(LocationActivity.this,"加载中...",true,null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
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
            String sql = "select a.FTime shang,a.FText3 slocate,a.FTime1 xia,a.FText1 xlocate,a.FTime2 wai,a.FText wlocate,a.ftext2 note from t_BOS200000021Entry2 a inner join t_BOS200000021 b on a.FID=b.FID inner join t_Emp c " +
                    "on c.FItemID=a.FBase inner join t_User d on d.FEmpID=c.FItemID where d.FName='"+ YApplication.fname+"' and CONVERT(varchar(100), b.FDate, 23) = '"+date+"'";
            Log.i("考勤详情sql",sql);
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_BOS200000021Entry2");

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
                Log.i("MeFragment", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
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
                    shang = recordEle.elementTextTrim("shang");
                    xia = recordEle.elementTextTrim("xia");
                    wai = recordEle.elementTextTrim("wai");
                    slocate = recordEle.elementTextTrim("slocate");
                    xlocate = recordEle.elementTextTrim("xlocate");
                    wlocate = recordEle.elementTextTrim("wlocate");
                    note = recordEle.elementTextTrim("note");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            tv_sdkt.setText(shang);
            tv_sdkl.setText(slocate);
            tv_xdkt.setText(xia);
            tv_xdkl.setText(xlocate);
            tv_wdkt.setText(wai);
            tv_wdkl.setText(wlocate);
            tv_beizhu.setText(note);
            super.onPostExecute(s);
        }
    }
}
