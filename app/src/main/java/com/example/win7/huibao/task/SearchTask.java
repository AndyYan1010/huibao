package com.example.win7.huibao.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win7.huibao.entity.Condition;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.SoapUtil;
import com.example.win7.huibao.util.Utils;
import com.example.win7.huibao.view.CustomProgress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 查询组织机构
 */
public class SearchTask extends AsyncTask<Void,String,String>{
        Context mContext;
        List<HashMap<String,String>> list = new ArrayList<>();
        Condition con;
        TextView tv;
        String idName;
        String sql;
        int type;//是否首字母拼音排序,是_0,否_其他
        CustomProgress dialog;

        public SearchTask(Context context,TextView tv,Condition con,
                          String sql,String idName,int type){
            this.mContext = context;
            this.con = con;
            this.tv = tv;
            this.idName = idName;
            this.sql = sql;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            list.clear();
            dialog = CustomProgress.show(mContext,"加载中",true,null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select,map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(s); // 将字符串转为XML
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
                List<String> list1 = new ArrayList<>();
                if(type==0) {
                    Utils.sortByInitial(list);
                }
                dialog.dismiss();
                for (HashMap<String, String> map : list) {
                    String name = map.get("fname");
                    list1.add(name);
                }
                final ListView lv = new ListView(mContext);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, list1);
                lv.setAdapter(adapter);
                final AlertDialog dialog = new AlertDialog.Builder(mContext).setView(lv)
                        .setTitle("请选择:").show();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tv.setText(list.get(i).get("fname"));//显示组织机构名称
                        con.setName(list.get(i).get("fname"));
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
