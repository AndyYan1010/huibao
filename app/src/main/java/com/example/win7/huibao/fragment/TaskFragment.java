package com.example.win7.huibao.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.activity.AddTaskActivity;
import com.example.win7.huibao.activity.TaskDetailActivity;
import com.example.win7.huibao.adapter.TaskAdapter;
import com.example.win7.huibao.entity.Tasks;
import com.example.win7.huibao.entity.Tool;
import com.example.win7.huibao.task.Task1;
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


public class TaskFragment extends Fragment {
    Context mContext;
    View view;
    Toolbar toolbar;
    ListView lv_task;
    List<Tasks> list;
    TaskAdapter adapter;
    CustomProgress dialog;
    Spinner sp_search;
    TextView tv_notask,tv_search;
    ImageView iv_search;
    ArrayAdapter<String> arr_adapter;
    List<String> strList;
    int index=0;
    Tool tool;
    List<HashMap<String,String>> list1;
    String group = YApplication.fgroup;
    String outeruser = "外部客户组";

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_task, container, false);
        setTool();
        setViews();
        setListeners();
        return view;
    }

    protected void setTool(){
//        toolbar = (Toolbar) view.findViewById(R.id.id_toolbar);
//        toolbar.setTitle(R.string.task);
//        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
//
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(group.equals("")){
//                    Toast.makeText(mContext,"您不在任何用户组内，请先申请权限！",Toast.LENGTH_SHORT).show();
//                }else {
//                    switch (item.getItemId()) {
//                        case R.id.action_add:
//                            Intent intent = new Intent(mContext, AddTaskActivity.class);
//                            intent.putExtra("taskno", "a");
//                            intent.putExtra("interid", "0");
//                            startActivity(intent);
//                            break;
//                    }
//                }
//                    return true;
//            }
//        });
        TextView tv_add = view.findViewById(R.id.tv_add);
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(group.equals("")){
                    Toast.makeText(mContext,"您不在任何用户组内，请先申请权限！",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(mContext, AddTaskActivity.class);
                    intent.putExtra("taskno", "a");
                    intent.putExtra("interid", "0");
                    startActivity(intent);
                }
            }
        });
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu2, menu);
//    }

    protected void setViews(){
        sp_search = (Spinner)view.findViewById(R.id.sp_task_search);
        strList = new ArrayList<>();
        strList.add("请选择");
//        strList.add("币别");
        strList.add("公司");
        strList.add("部门");
        strList.add("内容");
//        strList.add("计量");
        strList.add("责任人");
        strList.add("往来");
        strList.add("制单人");
        arr_adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, strList);
        sp_search.setAdapter(arr_adapter);
        tv_search = (TextView)view.findViewById(R.id.tv_search);
        iv_search = (ImageView)view.findViewById(R.id.iv_search);
        lv_task = (ListView) view.findViewById(R.id.lv_task);
        tv_notask = (TextView)view.findViewById(R.id.tv_notask);
        tv_notask.setVisibility(View.GONE);
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        tool = new Tool();
    }

    protected void setListeners(){
        sp_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tv_search.setText(R.string.select);
                tool = new Tool();
                index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sql = "";
                final EditText et = new EditText(mContext);
                switch(index){
                    case 0:
                        Toast.makeText(mContext,"请选择条件",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //公司
                        sql = "select fitemid,fname from t_Item_3001 where fitemid>0";
                        new Task1(list1,mContext,tv_search,tool,sql).execute();
                        break;
                    case 2:
                        //部门
                        new AlertDialog.Builder(mContext).setView(et).setTitle("部门")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String sql = "";
                                        if(TextUtils.isEmpty(et.getText().toString())){
                                            sql = "select fitemid,fname from t_Department where fitemid>0";
                                        }else{
                                            sql = "select fitemid,fname from t_Department where fitemid>0 and fname like '%" + et.getText().toString() + "%'";
                                        }
                                        new Task1(list1,mContext,tv_search,tool,sql).execute();
                                    }
                                }).setNegativeButton("取消",null).show();
                        break;
                    case 3:
                        //内容
                        new AlertDialog.Builder(mContext).setView(et).setTitle("内容")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String sql = "";
                                        if(TextUtils.isEmpty(et.getText().toString())){
                                            sql = "select fitemid,fname from t_ICItem where fitemid>0";
                                        }else{
                                            sql = "select fitemid,fname from t_ICItem where fitemid>0 and fname like '%"+et.getText().toString()+"%'";
                                        }
                                        new Task1(list1,mContext,tv_search,tool,sql).execute();
                                    }
                                }).setNegativeButton("取消",null).show();
                        break;
                    default:
                        //人员
                        new AlertDialog.Builder(mContext).setView(et).setTitle("人员")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                       String sql = "";
                                       if(TextUtils.isEmpty(et.getText().toString())){
                                           sql = "select fitemid,fname from t_Emp where fitemid>0";
                                       }else {
                                           sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%"+et.getText().toString()+"%'";
                                       }
                                        new Task1(list1,mContext,tv_search,tool,sql).execute();
                                    }
                                }).setNegativeButton("取消",null).show();
                }
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(group.equals("")){
                    Toast.makeText(mContext,"您不在任何用户组内，请先申请权限！",Toast.LENGTH_SHORT).show();
                    return;
                }
                String sql = "";
                Log.i("获取的id",tool.getId()+"<<<<<<<<<<<<<<<<<<<<<<<<<<");
                switch (index){
                    case 0:
                        if(group.contains("集团")){
                            sql = "select fid,fbillno from t_BOS200000000 order by fid desc";
                        }
                        if(group.contains("主管")||(group.contains("财务")&&!group.contains("集团"))){
                            sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                    "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='"+group+"') order by a.fid desc";
                        }
                        if(group.contains("员")||group.equals(outeruser)){
                            sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                                    "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='"+YApplication.fname+"') order by a.fid desc";
                        }
                        if(group.contains("总部")) {
                            String grouphead = group.substring(0,2);
                            sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                    "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%"+grouphead+"%') order by a.fid desc";
                        }
                        new ConditionTask(sql).execute();
                        break;
                    case 1:
                        //公司区分
                        if(tool.getId()!=null){
                            if(group.contains("集团")) {
                                sql = "select fid,fbillno from t_BOS200000000 where fbase11 =" + tool.getId() + " order by a.fid desc";
                            }
                            if(group.contains("主管")||(group.contains("财务")&&!group.contains("集团"))){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='"+group+"') and a.fbase11 =" + tool.getId() +" order by a.fid desc";
                            }
                            if(group.contains("员")||group.equals(outeruser)){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                                        "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='"+YApplication.fname+"') and a.fbase11 ="+tool.getId()+" order by a.fid desc";
                            }
                            if(group.contains("总部")){
                                String grouphead = group.substring(0,2);
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%"+grouphead+"%') and  a.fbase11="+tool.getId()+" order by a.fid desc";
                            }
                            new ConditionTask(sql).execute();
                        }else {
                            Toast.makeText(mContext,"请选择公司",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        //部门区分
                        if(tool.getId()!=null){
                            if(group.contains("集团")) {
                                sql = "select fid,fbillno from t_BOS200000000 where fbase12 =" + tool.getId() + " order by a.fid desc";
                            }
                            if(group.contains("主管")||(group.contains("财务")&&!group.contains("集团"))){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='"+group+"') and a.fbase12 =" + tool.getId() +" order by a.fid desc";
                            }
                            if(group.contains("员")||group.equals(outeruser)){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                                        "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='"+YApplication.fname+"') and a.fbase12 ="+tool.getId()+" order by a.fid desc";
                            }
                            if(group.contains("总部")){
                                String grouphead = group.substring(0,2);
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%"+grouphead+"%') and  a.fbase12="+tool.getId()+" order by a.fid desc";
                            }
                            new ConditionTask(sql).execute();
                        }else {
                            Toast.makeText(mContext,"请选择部门",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        //内容区分
                        if(tool.getId()!=null){
                            if(group.contains("集团")) {
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase1 =" + tool.getId() + " order by a.fid desc";
                            }
                            if(group.contains("主管")||(group.contains("财务")&&!group.contains("集团"))){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='"+group+"') and a.fbase1 =" + tool.getId() +" order by a.fid desc";
                            }
                            if(group.contains("员")||group.equals(outeruser)){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                                        "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='"+YApplication.fname+"') and a.fbase1 ="+tool.getId()+" order by a.fid desc";
                            }
                            if(group.contains("总部")){
                                String grouphead = group.substring(0,2);
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                            "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%"+grouphead+"%') and  a.fbase1="+tool.getId()+" order by a.fid desc";
                            }
                            new ConditionTask(sql).execute();
                        }else {
                            Toast.makeText(mContext,"请选择内容",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 4:
                        //责任人区分
                        if(tool.getId()!=null){
                            if(group.contains("集团")) {
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase4 =" + tool.getId() + " order by a.fid desc";
                            }
                            if(group.contains("主管")||(group.contains("财务")&&!group.contains("集团"))){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='"+group+"') and a.fbase4 =" + tool.getId() +" order by a.fid desc";
                            }
                            if(group.contains("员")||group.equals(outeruser)){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                                        "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='"+YApplication.fname+"') and a.fbase4 ="+tool.getId()+" order by a.fid desc";
                            }
                            if(group.contains("总部")){
                                String grouphead = group.substring(0,2);
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%"+grouphead+"%') and  a.fbase4="+tool.getId()+" order by a.fid desc";
                            }
                            new ConditionTask(sql).execute();
                        }else {
                            Toast.makeText(mContext,"请选择责任人",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 5:
                        //往来区分
                        if(tool.getId()!=null){
                            if(group.contains("集团")) {
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase10 =" + tool.getId() + " order by a.fid desc";
                            }
                            if(group.contains("主管")||(group.contains("财务")&&!group.contains("集团"))){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='"+group+"') and a.fbase10 =" + tool.getId() +" order by a.fid desc";
                            }
                            if(group.contains("员")||group.equals(outeruser)){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                                        "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='"+YApplication.fname+"') and a.fbase10 ="+tool.getId()+" order by a.fid desc";
                            }
                            if(group.contains("总部")){
                                String grouphead = group.substring(0,2);
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%"+grouphead+"%') and  a.fbase10 ="+tool.getId()+" order by a.fid desc";
                            }
                            new ConditionTask(sql).execute();
                        }else {
                            Toast.makeText(mContext,"请选择往来",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 6:
                        //制单人区分
                        if(tool.getId()!=null){
                            if(group.contains("集团")) {
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 =" + tool.getId() + " order by a.fid desc";
                            }
                            if(group.contains("主管")||(group.contains("财务")&&!group.contains("集团"))){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='"+group+"') and a.fbase15 =" + tool.getId() +" order by a.fid desc";
                            }
                            if(group.contains("员")||group.equals(outeruser)){
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                                        "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='"+YApplication.fname+"') and a.fbase15 ="+tool.getId()+" order by a.fid desc";
                            }
                            if(group.contains("总部")){
                                String grouphead = group.substring(0,2);
                                sql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                                        "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%"+grouphead+"%') and  a.fbase15 ="+tool.getId()+" order by a.fid desc";
                            }
                            new ConditionTask(sql).execute();
                        }else {
                            Toast.makeText(mContext,"请选择制单人",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra("taskno", list.get(i).getFbillno());
                intent.putExtra("interid","");
                startActivity(intent);
            }
        });

        lv_task.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(mContext).setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Toast.makeText(mContext,"正在开发中...",Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }).show();
                return true;
            }
        });

    }

    class TTask extends AsyncTask<Void,String,String>{
        @Override
        protected void onPreExecute() {
            list.clear();
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
//            String sql = "select FBillNo from t_BOS200000000";
//            String sql = "select a.finterid,a.fbillno,c.FName FBase3,a.FAmount4,d.FName FBase11,e.FName FBase12,f.FName FBase13,a.FNOTE1 FNote1," +
//                    "   b.FTime,b.FTime1,g.FName FBase4,h.FName FBase,h.F_111 jihua,i.FName yusuan,h.f_107 yusuane,b.FNOTE," +
//                    "   j.FName FBase10,k.FName FBase1,l.FName FBase2,b.FDecimal,b.FDecimal1,b.FAmount2,b.FAmount3," +
//                    "   b.FText,b.FText1,o.FName FBase14,p.FName FBase5,b.FCheckBox1,q.FName FBase6,b.FCheckBox2," +
//                    "   m.FName FBase7,b.FCheckBox3,n.FName FBase8,b.FCheckBox4,r.FName FBase9,b.FCheckBox5,s.FName fuzhu,b.FDecimal2" +
//                    "    from t_BOS200000000 a inner join t_BOS200000000Entry2 b on a.FID=b.FID " +
//                    "   left join t_Currency c on c.FCurrencyID=a.FBase3 left join t_Item_3001 d on d.FItemID=a.FBase11" +
//                    "   left join t_Department e on e.FItemID=a.FBase11 left join t_Item_3006 f on f.FItemID=a.FBase13" +
//                    "   left join t_Emp g on g.FItemID=b.FBase4 left join t_Item_3007 h on h.FItemID=b.FBase left join" +
//                    "   t_item i on i.FItemID=h.F_105 left join t_Emp j on j.FItemID=b.FBase10 left join t_ICItem k on k.FItemID=b.FBase1" +
//                    "   left join t_MeasureUnit l on l.FMeasureUnitID=b.FBase2 left join t_Item o on o.FItemID=b.FBase14" +
//                    "   left join t_Emp p on p.FItemID=b.FBase5 left join t_Emp q on q.FItemID=b.FBase6" +
//                    "   left join t_Emp m on m.FItemID=b.FBase7 left join t_Emp n on n.FItemID=b.fbase8" +
//                    "   left join t_Emp r on r.FItemID=b.FBase9 left join t_MeasureUnit s on s.FMeasureUnitID=k.FItemID";
            String sql = "select fid,fbillno from t_BOS200000000 order by fid desc";
            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_BOS200000000");

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
                Log.i("TaskFragment", e.toString() + "==================================");
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
//                List<TaskEntry> entryList = new ArrayList<>();
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    Tasks tasks = new Tasks();
                    String finterid = recordEle.elementTextTrim("fid");
                    String fbillno = recordEle.elementTextTrim("fbillno");
//                    String FBase3 = recordEle.elementTextTrim("FBase3");
//                    String FAmount4 = recordEle.elementTextTrim("FAmount4");
//                    String FBase11 = recordEle.elementTextTrim("FBase11");
//                    String FBase12 = recordEle.elementTextTrim("FBase12");
//                    String FBase13 = recordEle.elementTextTrim("FBase13");
//                    String FNote1 = recordEle.elementTextTrim("FNote1");
//                    TaskEntry entry = new TaskEntry();
//                    String FTime = recordEle.elementTextTrim("FTime");
//                    String FTime1 = recordEle.elementTextTrim("FTime1");
//                    String FBase4 = recordEle.elementTextTrim("FBase4");
//                    String FBase = recordEle.elementTextTrim("FBase");
//                    String FNOTE = recordEle.elementTextTrim("FNOTE");
//                    String FBase10 = recordEle.elementTextTrim("FBase10");
//                    String FBase1 = recordEle.elementTextTrim("FBase1");
//                    String FBase2 = recordEle.elementTextTrim("FBase2");
//                    String FDecimal = recordEle.elementTextTrim("FDecimal");
//                    String FDecimal1 = recordEle.elementTextTrim("FDecimal1");
//                    String FAmount2 = recordEle.elementTextTrim("FAmount2");
//                    String FAmount3 = recordEle.elementTextTrim("FAmount3");
//                    String FText = recordEle.elementTextTrim("FText");
//                    String FText1 = recordEle.elementTextTrim("FText1");
//                    String FBase14 = recordEle.elementTextTrim("FBase14");
//                    String FBase5 = recordEle.elementTextTrim("FBase5");
//                    String FBase6 = recordEle.elementTextTrim("FBase6");
//                    String FBase7 = recordEle.elementTextTrim("FBase7");
//                    String FBase8 = recordEle.elementTextTrim("FBase8");
//                    String FBase9 = recordEle.elementTextTrim("FBase9");
//                    String FCheckBox1 = recordEle.elementTextTrim("FCheckBox1");
//                    String FCheckBox2 = recordEle.elementTextTrim("FCheckBox2");
//                    String FCheckBox3 = recordEle.elementTextTrim("FCheckBox3");
//                    String FCheckBox4 = recordEle.elementTextTrim("FCheckBox4");
//                    String FCheckBox5 = recordEle.elementTextTrim("FCheckBox5");
//                    entry.setFTime(FTime);
//                    entry.setFTime1(FTime1);
//                    entry.setFBase4(FBase4);
//                    entry.setFBase(FBase);
//                    entry.setFNOTE(FNOTE);
//                    entry.setFBase10(FBase10);
//                    entry.setFBase1(FBase1);
//                    entry.setFBase2(FBase2);
//                    entry.setFDecimal(Double.parseDouble(FDecimal));
//                    entry.setFDecimal1(Double.parseDouble(FDecimal1));
//                    entry.setFAmount2(Double.parseDouble(FAmount2));
//                    entry.setFAmount3(Double.parseDouble(FAmount3));
//                    entry.setFText(FText);
//                    entry.setFText1(FText1);
//                    entry.setFBase14(FBase14);
//                    entry.setFBase5(FBase5);
//                    entry.setFBase6(FBase6);
//                    entry.setFBase7(FBase7);
//                    entry.setFBase8(FBase8);
//                    entry.setFBase9(FBase9);
//                    entry.setFCheckBox1(Integer.parseInt(FCheckBox1));
//                    entry.setFCheckBox2(Integer.parseInt(FCheckBox2));
//                    entry.setFCheckBox3(Integer.parseInt(FCheckBox3));
//                    entry.setFCheckBox4(Integer.parseInt(FCheckBox4));
//                    entry.setFCheckBox5(Integer.parseInt(FCheckBox5));
//                    entryList.add(entry);
                    tasks.setFinterid(finterid);
                    tasks.setFbillno(fbillno);
                    list.add(tasks);
                }

//                tasks.setFBase3(FBase3);
//                tasks.setFAmount4(Double.parseDouble(FAmount4));
//                tasks.setFBase11(FBase11);
//                tasks.setFBase12(FBase12);
//                tasks.setFBase13(FBase13);
//                tasks.setFNote1(FNote1);
//                tasks.setEntryList(entryList);
            } catch (Exception e) {
                    e.printStackTrace();
            }
            if(list.size()==0){
                return "0";
            }else{
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.equals("0")){
                tv_notask.setVisibility(View.VISIBLE);
            }else {
                adapter = new TaskAdapter(mContext, list);
                lv_task.setAdapter(adapter);
            }
            super.onPostExecute(s);
        }
    }

    class ConditionTask extends AsyncTask<Void,String,String>{
        String sql;

        public ConditionTask(String sql){
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.clear();
            dialog = CustomProgress.show(mContext,"加载中...",true,null);
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

            rpc.addProperty("FSql", sql);
            rpc.addProperty("FTable", "t_BOS200000000");

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
                Log.i("TaskFragment", e.toString() + "==================================");
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
                    Tasks tasks = new Tasks();
                    String finterid = recordEle.elementTextTrim("fid");
                    String fbillno = recordEle.elementTextTrim("fbillno");
                    tasks.setFinterid(finterid);
                    tasks.setFbillno(fbillno);
                    list.add(tasks);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(list.size()==0){
                return "0";
            }else{
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(s.equals("0")){
                tv_notask.setVisibility(View.VISIBLE);
                adapter = new TaskAdapter(mContext, list);
                lv_task.setAdapter(adapter);
            }else {
                tv_notask.setVisibility(View.GONE);
                adapter = new TaskAdapter(mContext, list);
                lv_task.setAdapter(adapter);
            }
        }
    }
}
