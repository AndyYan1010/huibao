package com.example.win7.huibao.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.entity.Tasks;
import com.example.win7.huibao.util.Consts;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ZhiduTask extends AsyncTask<Void,String,String>{
    Context mContext;
    List<HashMap<String,String>> list;
    List<String> strList;
    Tasks task;
    TextView tv;
    EditText et;

    public ZhiduTask(Context mContext,List<HashMap<String,String>> list,List<String> strList,Tasks task,TextView tv,EditText et){
        this.mContext = mContext;
        this.list = list;
        this.strList= strList;
        this.task = task;
        this.tv = tv;
        this.et=et;
    }

    @Override
    protected void onPreExecute() {
        list.clear();
        strList.clear();
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
            String sql = "select fitemid,fname,f_102 from t_Item_3006 where fitemid>0";
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
                Log.i("AddTaskActivity", e.toString() + "==================================");
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
                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
                    map.put("fname",recordEle.elementTextTrim("fname"));
                    map.put("fnote",recordEle.elementTextTrim("f_102"));
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
            for(HashMap<String,String> map:list){
                String name = map.get("fname");
                strList.add(name);
            }
            final ListView lv = new ListView(mContext);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1,strList);
            lv.setAdapter(adapter);
//            lv.setPadding(60,20,40,10);
            final AlertDialog dialog = new AlertDialog.Builder(mContext).setView(lv)
                    .setTitle(R.string.zhidu1).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    task.setFBase13(list.get(i).get("itemid"));
                    tv.setText(strList.get(i));
                    task.setFNote1(list.get(i).get("fnote"));
                    et.setText(list.get(i).get("fnote"));
                    dialog.dismiss();
                }
            });
        }

}
