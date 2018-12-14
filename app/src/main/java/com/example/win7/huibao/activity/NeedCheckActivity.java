package com.example.win7.huibao.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.adapter.NeedCheckAdapter;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.view.CustomProgress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NeedCheckActivity extends BaseActivity {
    Toolbar toolbar;
    ListView lv;
    TextView tv_zanwu;
    List<HashMap<String,String>> list;
    CustomProgress progress;
    SwipeRefreshLayout srl_need_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_check);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.ncheck);
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
        tv_zanwu = (TextView)findViewById(R.id.tv_zanwu);
        lv = (ListView)findViewById(R.id.lv_need_check);
        srl_need_check = (SwipeRefreshLayout)findViewById(R.id.srl_need_check);
        list = new ArrayList<>();
        new NeedTask().execute();
    }

    protected void setListeners(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NeedCheckActivity.this,CheckActivity.class);
                intent.putExtra("goodsId",list.get(i).get("id"));
                startActivity(intent);
            }
        });

        srl_need_check.setColorSchemeResources(new int[]{R.color.bottom_button_text_green, R.color.colorPrimary});
        srl_need_check.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NeedTask().execute();
                srl_need_check.setRefreshing(false);
            }
        });
    }

    class NeedTask extends AsyncTask<Void,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.clear();
            progress = CustomProgress.show(NeedCheckActivity.this,"加载中...",true,null);
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
//            String name = YApplication.fname;
            String name = YApplication.username;
            String sql = "select a.id,b.fname item,a.fdecimal number,a.fdecimal1 price,a.famount2 amount from t_BOS200000000Entry2 a left join t_icitem b on b.fitemid=a.fbase1 where (a.FBase5=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox1=0) or " +
                    "(a.FBase6=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox2=0) or (a.FBase7=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox3=0) " +
                    "or (a.FBase8=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox4=0) or (a.FBase9=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox5=0)";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_BOS200000000Entry2");

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
                Log.i("NeedCheckActivity", e.toString() + "==================================");
            }

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
                    HashMap<String,String> map = new HashMap<>();
                    map.put("id",recordEle.elementTextTrim("id"));
                    map.put("item",recordEle.elementTextTrim("item"));
                    map.put("number",recordEle.elementTextTrim("number"));
                    map.put("price",recordEle.elementTextTrim("price"));
                    map.put("amount",recordEle.elementTextTrim("amount"));
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if(list.size()==0){
                tv_zanwu.setVisibility(View.VISIBLE);
            }else {
                tv_zanwu.setVisibility(View.GONE);
            }
            NeedCheckAdapter adapter = new NeedCheckAdapter(NeedCheckActivity.this, list);
            lv.setAdapter(adapter);
        }
    }
}
