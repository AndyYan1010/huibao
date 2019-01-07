package com.example.win7.huibao.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.example.win7.huibao.activity.AddOrderActivity;
import com.example.win7.huibao.adapter.OrderAdapter;
import com.example.win7.huibao.entity.Tool;
import com.example.win7.huibao.task.Task1;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.view.CustomDatePicker;
import com.example.win7.huibao.view.CustomProgress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class OrderFragment extends Fragment implements View.OnClickListener {
    private Context  mContext;
    private View     view;
    private TextView tv_add;
    private String group = YApplication.fgroup;
    private Spinner              sp_search;
    private List<String>         strList;//部门类别
    private ArrayAdapter<String> arr_adapter;//订单列表数据
    private TextView             tv_notask, tv_search;
    private ImageView iv_search;
    private ListView  lv_task;//订单列表
    private Tool      tool;
    private int       index;//记录
    private List      listDlogInfo;
    private String mStartTime = "2018-07-01 10:10";//起始时间yyyy-MM-dd hh:mm
    private String mEndTime   = "2018-07-01 10:10";//终止时间
    private CustomProgress dialog;
    private List<String>   mDataList;//存放查询结果数据
    private OrderAdapter   adapter;//订单表数据设配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_order, container, false);
        setTool();
        setViews();
        setData();
        setListeners();
        return view;
    }

    protected void setTool() {
        tv_add = view.findViewById(R.id.tv_add);//新增
        tv_add.setOnClickListener(this);
    }

    protected void setViews() {
        sp_search = (Spinner) view.findViewById(R.id.sp_task_search);
        strList = new ArrayList<>();
        strList.add("请选择");
        strList.add("日期");
        strList.add("发票日期");
        strList.add("内容");
        strList.add("往来");
        strList.add("制单人");
        strList.add("申请部门");
        strList.add("责任部门");
        strList.add("计划预算进度");
        arr_adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, strList);
        sp_search.setAdapter(arr_adapter);
        tv_search = (TextView) view.findViewById(R.id.tv_search);
        iv_search = (ImageView) view.findViewById(R.id.iv_search);
        lv_task = (ListView) view.findViewById(R.id.lv_task);
        tv_notask = (TextView) view.findViewById(R.id.tv_notask);
        tv_notask.setVisibility(View.GONE);
        //        list = new ArrayList<>();
        listDlogInfo = new ArrayList<>();
        tool = new Tool();
    }

    private void setData() {
        mDataList = new ArrayList();
        adapter = new OrderAdapter(getContext(), mDataList);
        lv_task.setAdapter(adapter);
        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, AddOrderActivity.class);
                intent.putExtra("kind", "check");
                intent.putExtra("title", "查看订单表");
                intent.putExtra("orderID", mDataList.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    protected void setListeners() {
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
        tv_search.setOnClickListener(this);
        iv_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                if (group.equals("")) {
                    Toast.makeText(mContext, "您不在任何用户组内，请先申请权限！", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mContext, AddOrderActivity.class);
                    intent.putExtra("kind", "add");
                    intent.putExtra("title", "新增订单表");
                    startActivity(intent);
                }
                break;
            case R.id.tv_search:
                String sql = "";
                String defaultSql = "";
                final EditText et = new EditText(mContext);
                switch (index) {
                    case 0:
                        ToastUtils.showToast(getContext(), "请选择查找条件");
                        break;
                    case 1://日期
                        //公司
                        // sql = "select fitemid,fname from t_Item_3001 where fitemid>0";
                        // new Task1(listDlogInfo, mContext, tv_search, tool, sql).execute();
                        //先弹出dialog让用户选择起始和终止日期
                        //获取当前日期
                        defaultSql = "";
                        sql = "";
                        searchData("日期", sql, defaultSql);
                        break;
                    case 2://发票日期
                        defaultSql = "";
                        sql = "";
                        searchData("发票日期", sql, defaultSql);
                        break;
                    case 3://内容
                        defaultSql = "select fitemid,fname from t_ICItem where fitemid>0";
                        sql = "select fitemid,fname from t_ICItem where fitemid>0 and fname like '%";
                        searchcontent(et, "内容", defaultSql, sql);
                        break;
                    case 4://往来
                        defaultSql = "select fitemid,fname from t_Emp where fitemid>0";
                        sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%";
                        searchcontent(et, "往来", defaultSql, sql);
                        break;
                    case 5://制单人
                        defaultSql = "select fitemid,fname from t_Emp where fitemid>0";
                        sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%";
                        searchcontent(et, "制单人", defaultSql, sql);
                        break;
                    case 6://申请部门
                        defaultSql = "select fitemid,fname from t_Department where fitemid>0";
                        sql = "select fitemid,fname from t_Department where fitemid>0 and fname like '%";
                        searchcontent(et, "申请部门", defaultSql, sql);
                        break;
                    case 7://责任部门
                        defaultSql = "select fitemid,fname from t_Department where fitemid>0";
                        sql = "select fitemid,fname from t_Department where fitemid>0 and fname like '%";
                        searchcontent(et, "责任部门", defaultSql, sql);
                        break;
                    case 8://计划预算进度

                        break;
                    default:

                        break;
                }
                break;
            case R.id.iv_search:
                if (group.equals("")) {
                    Toast.makeText(mContext, "您不在任何用户组内，请先申请权限！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String searchDataSql = "";
                switch (index) {
                    case 0:
                        //                        if (group.contains("集团")) {
                        //                            searchDataSql = "select fid,fbillno from t_BOS200000000 order by fid desc";
                        //                        }
                        //                        if (group.contains("主管") || (group.contains("财务") && !group.contains("集团"))) {
                        //                            searchDataSql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                        //                                    "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname='" + group + "') order by a.fid desc";
                        //                        }
                        //                        if (group.contains("员") || group.equals(outeruser)) {
                        //                            searchDataSql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 = (" +
                        //                                    "select b.fitemid from t_user a left join t_emp b on a.fempid=b.fitemid where a.fname='" + YApplication.fname + "') order by a.fid desc";
                        //                        }
                        //                        if (group.contains("总部")) {
                        //                            String grouphead = group.substring(0, 2);
                        //                            searchDataSql = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                        //                                    "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%" + grouphead + "%') order by a.fid desc";
                        //                        }
                        searchDataSql = "select fbillno from t_bos200000011";
                        new ConditionTask(searchDataSql).execute();
                        break;
                }
                break;
        }
    }

    private String outeruser = "外部客户组";

    private void searchData(String title, String sql, String defaultSql) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String data = simpleDateFormat.format(new Date());
        View timeView = View.inflate(getContext(), R.layout.select_start_end_time, null);
        ImageView img_st = timeView.findViewById(R.id.img_select_st);//选择开始时间
        ImageView img_end = timeView.findViewById(R.id.img_select_end);//选择结束时间
        final TextView tv_starttime = timeView.findViewById(R.id.tv_start_time);
        final TextView tv_endtime = timeView.findViewById(R.id.tv_end_time);
        tv_starttime.setText(data);
        tv_endtime.setText(data);
        img_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开时间选择器,设置时间
                selectTime(tv_starttime);
            }
        });
        img_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开时间选择器
                selectTime(tv_endtime);
            }
        });
        new AlertDialog.Builder(getContext()).setView(timeView).setTitle(title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mStartTime = String.valueOf(tv_starttime.getText()).trim();
                mEndTime = String.valueOf(tv_endtime.getText()).trim();
                ToastUtils.showToast(getContext(), "*" + mStartTime + "**" + mEndTime);
            }
        }).setNegativeButton("取消", null).show();
    }

    private void selectTime(final TextView tv_select_time) {
        CustomDatePicker dpk1 = new CustomDatePicker(getContext(), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                tv_select_time.setText(time);
            }
        }, "2010-01-01 00:00", "2090-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        dpk1.showSpecificTime(true); // 显示时和分
        dpk1.setIsLoop(true); // 允许循环滚动
        dpk1.show(tv_select_time.getText().toString());
    }

    private void searchcontent(final EditText et, String title, final String defaultSql, final String Searchsql) {
        new AlertDialog.Builder(mContext).setView(et).setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "";
                        if (TextUtils.isEmpty(et.getText().toString())) {
                            sql = defaultSql;
                        } else {
                            sql = Searchsql + et.getText().toString() + "%'";
                        }
                        new Task1(listDlogInfo, mContext, tv_search, tool, sql).execute();
                    }
                }).setNegativeButton("取消", null).show();
    }

    class ConditionTask extends AsyncTask<Void, String, String> {
        String sql;

        public ConditionTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDataList.clear();
            dialog = CustomProgress.show(mContext, "加载中...", true, null);
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

            rpc.addProperty("FSql", sql);//sql:select fbillno from t_bos200000011
            rpc.addProperty("FTable", "t_BOS200000011");

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
            }
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            String result = object.getProperty(0).toString();
            Document doc = null;

            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML

                Element rootElt = doc.getRootElement(); // 获取根节点

                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head

                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String fbillno = recordEle.elementTextTrim("fbillno");
                    mDataList.add(fbillno);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mDataList.size() == 0) {
                return "0";
            } else {
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (s.equals("0")) {
                tv_notask.setVisibility(View.VISIBLE);
            } else {
                tv_notask.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
