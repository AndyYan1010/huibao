package com.example.win7.huibao.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.entity.Condition;
import com.example.win7.huibao.task.SearchTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ShaixuanAdapter extends BaseAdapter{
    Context mContext;
    List<Condition> list;
    Calendar mycalendar = Calendar.getInstance();
    int year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
    int month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
    int day = mycalendar.get(Calendar.DAY_OF_MONTH);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ShaixuanAdapter(Context context, List<Condition> list){
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Holder holder;
        if(view==null){
            holder = new Holder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shaixuan,null);
            holder.tv1 = view.findViewById(R.id.tv1);
            holder.tv2 = view.findViewById(R.id.tv2);
            view.setTag(holder);
        }else {
            holder = (Holder)view.getTag();
        }
        final String item = list.get(i).getType();
        final Condition con = list.get(i);
        holder.tv1.setText(item);
        holder.tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.equals("日期")){
                    View v = LayoutInflater.from(mContext).inflate(R.layout.item_datepick,null);
                    final TextView start = v.findViewById(R.id.start);
                    final TextView end = v.findViewById(R.id.end);
                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    String s = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                    try {
                                        java.util.Date date = sdf.parse(s);
                                        String s1 = sdf.format(date);
                                        start.setText(s1);
                                    } catch (Exception e) {
                                    }
                                }
                            }, year, month, day);
                            dpd.show();//显示DatePickerDialog组件
                        }
                    });
                    end.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    String s = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                    try {
                                        java.util.Date date = sdf.parse(s);
                                        String s1 = sdf.format(date);
                                        end.setText(s1);
                                    } catch (Exception e) {
                                    }
                                }
                            }, year, month, day);
                            dpd.show();//显示DatePickerDialog组件
                        }
                    });
                    new AlertDialog.Builder(mContext).setTitle("日期").setView(v)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String s = start.getText().toString()+"~"+end.getText().toString();
                                    holder.tv2.setText(s);
                                    con.setName(s);
                                }
                            }).setNegativeButton("取消",null).show();
                }
                if(item.equals("组织机构")){
                    String sql = "select fitemid,fname from t_Item_3001 where fitemid>0";
                    new SearchTask(mContext,holder.tv2,con,sql,"departid",0).execute();
                }
                if(item.equals("责任部门")){
                    String sql = "select fitemid,fname from t_Department where fitemid>0";
                    new SearchTask(mContext,holder.tv2,con,sql,"areaid",0).execute();
                }
                if(item.equals("往来")){
                    final EditText et = new EditText(mContext);
                    new AlertDialog.Builder(mContext).setTitle("往来").setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String empName = et.getText().toString();
                                    String sql;
                                    if(TextUtils.isEmpty(empName)){
                                        sql = "select fitemid,fname from t_Emp where fitemid>0";
                                    }else{
                                        sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + empName + "%'";
                                    }
                                    new SearchTask(mContext,holder.tv2,con,sql,"empid",0).execute();
                                }
                            }).setNegativeButton("取消", null).show();
                }
                if(item.equals("计划预算进度")){
                    final EditText et = new EditText(mContext);
                    new AlertDialog.Builder(mContext).setTitle("计划预算进度").setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String planName = et.getText().toString();
                                    String sql;
                                    if(TextUtils.isEmpty(planName)){
                                        sql = "select fitemid,fname from t_Item_3007 where fitemid>0";
                                    } else {
                                        sql = "select fitemid,fname from t_Item_3007 where fitemid>0 and fname like '%" + planName + "%'";
                                    }
                                    new SearchTask(mContext,holder.tv2,con,sql,"planid",0).execute();
                                }
                            }).setNegativeButton("取消", null).show();
                }
                if(item.equals("内容")){
                    final EditText et = new EditText(mContext);
                    new AlertDialog.Builder(mContext).setTitle("内容").setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String itemName = et.getText().toString();
                                    String sql;
                                    if(TextUtils.isEmpty(itemName)){
                                        sql = "select fitemid,fname from t_icitem where fitemid>0 order by fnumber";
                                    } else {
                                        sql = "select fitemid,fname from t_icitem where fitemid>0 and fname like '%" + itemName + "%' order by fnumber";
                                    }
                                    new SearchTask(mContext,holder.tv2,con,sql,"itemid",1).execute();
                                }
                            }).setNegativeButton("取消", null).show();
                }
            }
        });
        return view;
    }

    class Holder{
        TextView tv1,tv2;
    }
}
