package com.example.win7.huibao.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.view.CustomProgress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ZhuFragment extends Fragment {
    Context mContext;
    View view;
    TextView tv_no,tv_currency,tv_rate,tv_departs,tv_area,tv_content,tv_respon,tv_zhidan,tv_contacts,tv_jiliang,tv_zeren;
    CustomProgress dialog;
    String taskno,currency,rate,departs,area,zeren,content,respon,zhidan,contacts,jiliang="";
    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_zhu, container, false);
        setViews();
        setListeners();
        return view;
    }

    protected void setViews(){
        tv_no = (TextView)view.findViewById(R.id.tv_no);
        tv_currency = (TextView)view.findViewById(R.id.tv_currency);
        tv_rate = (TextView)view.findViewById(R.id.tv_rate);
        tv_departs = (TextView)view.findViewById(R.id.tv_departs);
        tv_area = (TextView)view.findViewById(R.id.tv_area);
        tv_content = (TextView)view.findViewById(R.id.tv_content);
        tv_respon = (TextView)view.findViewById(R.id.tv_respon);
        tv_zhidan = (TextView)view.findViewById(R.id.tv_zhidan);
        tv_contacts = (TextView)view.findViewById(R.id.tv_contacts);
        tv_jiliang = (TextView)view.findViewById(R.id.tv_jiliang);
        tv_zeren = view.findViewById(R.id.tv_zeren);
        taskno = getArguments().getString("taskno");
        new ZHUTask(taskno).execute();
    }

    protected void setListeners(){

    }

    public Map<String,String> getInfo(){
        Map<String,String> map = new HashMap<>();
        map.put("fbillno",taskno);
        map.put("zuzhi",departs);
        map.put("shenqing",area);
        map.put("zeren",zeren);
        map.put("respon",respon);
        map.put("zhidan",zhidan);
        map.put("contacts",contacts);
        return map;
    }

    class ZHUTask extends AsyncTask<Void,String,String>{
        String Taskno;

        ZHUTask(String Taskno){
            this.Taskno = Taskno;
        }

        @Override
        protected void onPreExecute() {
            dialog = CustomProgress.show(mContext,"加载中...",true,null);
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
            String sql = "select top 1 a.FAmount4 rate,c.fname departs,d.fname area,e.fname currency," +
                    "f.fname respon,g.fname wanglai,h.fname neirong,i.fname zhidan,j.fname jiliang,k.fname zeren from t_BOS200000000 a " +
                    "left join t_BOS200000000Entry2 b on b.FID=a.FID left join t_Item_3001 c " +
                    "on c.FItemID=a.FBase11 left join t_Department d on d.FItemID=a.FBase12 left join" +
                    " t_Currency e on e.FCurrencyID=a.FBase3 left join t_emp f on f.fitemid=b.fbase4 left join" +
                    " t_emp g on g.fitemid=b.fbase10 left join t_ICItem h on h.FItemID=b.FBase1 left join t_emp i on i.fitemid=b.fbase15 " +
                    "left join t_measureunit j on j.fitemid=h.funitid left join t_Department k on k.fitemid=a.FBase16 where a.FBillNo ='"+Taskno+"'";
            Log.i("主表查询语句",sql);
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_Currency");

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
                    currency = recordEle.elementTextTrim("currency");
                    rate = recordEle.elementTextTrim("rate");
                    departs = recordEle.elementTextTrim("departs");
                    area = recordEle.elementTextTrim("area");
                    content = recordEle.elementTextTrim("neirong");
                    respon = recordEle.elementTextTrim("respon");
                    zhidan = recordEle.elementTextTrim("zhidan");
                    contacts = recordEle.elementTextTrim("wanglai");
                    jiliang = recordEle.elementTextTrim("jiliang");
                    zeren = recordEle.elementTextTrim("zeren");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            tv_no.setText(taskno);
            tv_currency.setText(currency);
            tv_rate.setText(df.format(Double.parseDouble(rate)));
            tv_departs.setText(departs);
            tv_area.setText(area);
            tv_content.setText(content);
            tv_respon.setText(respon);
            tv_zhidan.setText(zhidan);
            tv_contacts.setText(contacts);
            tv_jiliang.setText(jiliang);
            tv_zeren.setText(zeren);
            super.onPostExecute(s);
        }
    }

}
