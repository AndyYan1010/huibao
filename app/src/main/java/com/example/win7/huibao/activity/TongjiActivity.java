package com.example.win7.huibao.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.table.TableData;
import com.example.win7.huibao.R;
import com.example.win7.huibao.adapter.CheckBoxAdapter;
import com.example.win7.huibao.adapter.ShaixuanAdapter;
import com.example.win7.huibao.entity.Condition;
import com.example.win7.huibao.entity.Statistics;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.SoapUtil;
import com.example.win7.huibao.util.Utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TongjiActivity extends BaseActivity {
    Toolbar toolbar;
    List<HashMap<String,Object>> list = new ArrayList<>();
    List<Condition> l = new ArrayList<>();
    ListView lv1;
    TextView tv_tj;
    SmartTable mTable;
    List<Column> columnList;
    List<Statistics> dataList;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tongji);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool(){
        toolbar = (Toolbar)findViewById(R.id.id_toolbar);
        toolbar.setTitle(getResources().getString(R.string.tongji));

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
                    case R.id.action_shaixuan:
                        //筛选条件选择
                        list.clear();
                        l.clear();
                        View v = getLayoutInflater().inflate(R.layout.item_shenhe, null);
                        final ListView lv = v.findViewById(R.id.lv_checkbox);
                        final TextView tv_submits = v.findViewById(R.id.tv_check_submit);
                        String[] str = new String[]{"日期","组织机构","责任部门","往来","计划预算进度","内容"};

                        for(int i=0;i<str.length;i++){
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("fname",str[i]);
                            map.put("ischeck",false);
                            list.add(map);
                        }
                        CheckBoxAdapter adapter = new CheckBoxAdapter(TongjiActivity.this,list);
                        lv.setAdapter(adapter);
                        final AlertDialog dialog = new AlertDialog.Builder(TongjiActivity.this).setView(v)
                                .setTitle("条件").show();
                        tv_submits.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                for (int i = 0; i < list.size(); i++) {
                                    if (Boolean.valueOf(list.get(i).get("ischeck").toString())) {
                                        Condition con = new Condition();
                                        con.setType(list.get(i).get("fname").toString());
                                        l.add(con);
                                    }
                                }
                                ShaixuanAdapter a = new ShaixuanAdapter(TongjiActivity.this,l);
                                lv1.setAdapter(a);
                                a.notifyDataSetChanged();
                                tv_tj.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                            }
                        });
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu4, menu);
        return true;
    }

    protected void setViews(){
        lv1 = findViewById(R.id.lv1);
        tv_tj = findViewById(R.id.tv_tj);
        mTable = findViewById(R.id.table);
        Map<String,String> maps = new LinkedHashMap<>();
        maps.put("单据号","fbillno");
        maps.put("启日期","qi");
        maps.put("止日期","zhi");
        maps.put("组织机构","zuzhi");
        maps.put("责任部门","zeren");
        maps.put("往来","wanglai");
        maps.put("计划预算进度","jihua");
        maps.put("内容","neirong");
        maps.put("数量","shuliang");
        maps.put("单价","danjia");
        maps.put("金额含税","hanshui");
        maps.put("人民币不含税额","buhan");
        columnList = new ArrayList<>();
        for(String s : maps.keySet()){
            Column<String> column = new Column<String>(s,maps.get(s));
            columnList.add(column);
        }
        dataList = new ArrayList<>();
    }

    protected void setListeners(){
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv_tj.setVisibility(View.VISIBLE);
            }
        });
        tv_tj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_tj.setVisibility(View.GONE);
                String sql = "select * from z_report_t_BOS200000000 where ";
                for (Condition con: l){
                    if(con.getType().equals("日期")){
                        String[] str = con.getName().split("~");
                        String s = str[0];
                        String e = str[1];
                        sql = sql + "(止日期 between '" + s + "' and '" + e + "' or 止日期 is null) and ";
                    }else {
                        sql = sql + con.getType() + "='" + con.getName() + "' and ";
                    }
                }
                sql = sql.substring(0,sql.length()-4) + "order by 组织机构 desc,责任部门 desc,计划预算进度 desc,内容 desc,nm";
                Log.i("目前的sql语句",sql);
                new StatisticsTask(sql).execute();
            }
        });
    }

    class StatisticsTask extends AsyncTask<Void,String,String>{
        String sql;

        StatisticsTask(String sql){
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            dataList.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,String> map = new HashMap<>();
            map.put("FSql",sql);
            map.put("FTable","t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select,map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("统计结果",s);
            try {
                Document doc = DocumentHelper.parseText(s);
                Element ele = doc.getRootElement();
                Iterator iter = ele.elementIterator("Cust");
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    Statistics sta = new Statistics();
                    sta.setFbillno(recordEle.elementTextTrim("单据号"));
                    sta.setQi(recordEle.elementTextTrim("启日期"));
                    sta.setZhi(recordEle.elementTextTrim("止日期"));
                    sta.setZuzhi(recordEle.elementTextTrim("组织机构"));
                    sta.setZeren(recordEle.elementTextTrim("责任部门"));
                    sta.setWanglai(recordEle.elementTextTrim("往来"));
                    sta.setJihua(recordEle.elementTextTrim("计划预算进度"));
                    sta.setNeirong(recordEle.elementTextTrim("内容"));
                    sta.setShuliang(df.format(Double.parseDouble(Utils.NulltoZero(recordEle.elementTextTrim("数量")))));
                    sta.setDanjia(df.format(Double.parseDouble(Utils.NulltoZero(recordEle.elementTextTrim("单价")))));
                    sta.setHanshui(df.format(Double.parseDouble(Utils.NulltoZero(recordEle.elementTextTrim("金额含税")))));
                    sta.setBuhan(df.format(Double.parseDouble(Utils.NulltoZero(recordEle.elementTextTrim("人民币不含税额")))));
                    dataList.add(sta);
                }
                TableData<Statistics> data = new TableData<Statistics>("统计表", dataList, columnList);
                mTable.setZoom(true);
                mTable.setTableData(data);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
