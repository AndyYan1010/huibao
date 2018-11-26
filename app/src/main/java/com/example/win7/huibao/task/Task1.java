package com.example.win7.huibao.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win7.huibao.entity.Tool;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.PinyinComparator;
import com.example.win7.huibao.view.CustomProgress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Task1 extends AsyncTask<Void,String,String>{
        List<HashMap<String,String>> list;
        Context mContext;
        TextView tv;
        Tool tool;
        String sql;
        CustomProgress progress;

        public Task1(List<HashMap<String,String>> list, Context context, TextView tv, Tool tool, String sql){
            this.list = list;
            this.mContext = context;
            this.tv = tv;
            this.tool = tool;
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            progress = CustomProgress.show(mContext,"加载中...",true,null);
            list.clear();
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

            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_user");

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
                    HashMap<String,String> map = new HashMap<>();
                    map.put("fitemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
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
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list,comparator);
            progress.dismiss();
            final List<String> strlist = new ArrayList<>();
            for (HashMap<String,String> map:list){
                strlist.add(map.get("fname"));
            }
            final ListView lv = new ListView(mContext);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1,strlist);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(mContext).setView(lv)
                    .show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tv.setText(strlist.get(i));
                    tool.setId(list.get(i).get("fitemid"));
                    dialog.dismiss();
                }
            });
        }

}
