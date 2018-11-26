package com.example.win7.huibao.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.activity.AddOrderActivity;
import com.example.win7.huibao.adapter.LvShowMoreAdapter;
import com.example.win7.huibao.adapter.MyLVOrderAdapter;
import com.example.win7.huibao.entity.OrderDataInfo;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ToastUtils;
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
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/6/26 9:04
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class ApplyFragment extends Fragment implements View.OnClickListener {
    private View     mRootView;
    private TextView tv_add;
    private ListView lv_order;
    private int mOpenItem = -1;
    private MyLVOrderAdapter orderAdapter;
    private AddOrderActivity orderActivity;
    private boolean mEditable = false;//是否可编辑
    private OrderDataInfo             orderInfo;//整个订单表信息
    private List<Map<String, String>> mData;//存放子表详情对象
    private CustomProgress            dialog;
    private List<Map<String, String>> mShowData;//临时存放查询的内码

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_apply_order, null);
        orderActivity = (AddOrderActivity) getActivity();
        mEditable = orderActivity.getEditable();
        initView();
        initData();
        initListener();
        return mRootView;
    }

    private void initView() {
        tv_add = mRootView.findViewById(R.id.tv_add);
        lv_order = mRootView.findViewById(R.id.lv_order);
    }

    private void initData() {
        String kind = orderActivity.getIntent().getStringExtra("kind");
        if (null == kind) {
            ToastUtils.showToast(getContext(), "查询添加失败");
            return;
        }
        mData = new ArrayList();
        orderInfo = orderActivity.getOrderInfo();
        //区分进入类别
        if (kind.equals("add")) {

        }
        if (kind.equals("edit") || kind.equals("check")) {//查看和编辑//是有查询数据的,填入当前fragment中的数据集合
            List<Map<String, String>> mapListson = orderInfo.getMapListson();
            mData.addAll(mapListson);
        }
        orderAdapter = new MyLVOrderAdapter(getContext(), mData, mOpenItem);
        lv_order.setAdapter(orderAdapter);
    }

    private void initListener() {
        tv_add.setOnClickListener(this);
        if (!mEditable) {//不可编辑时，不显示新增按钮
            tv_add.setVisibility(View.GONE);
        } else {
            lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //弹出dailog让用户修改子表
                    showAlogToChange("修改", i);
                }
            });
            lv_order.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //弹出窗口提示删除数据
                    showDeletAlog(i);
                    return true;
                }
            });
        }
    }

    private void showDeletAlog(final int item) {
        new AlertDialog.Builder(getContext())
                .setTitle("确认删除？").setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                orderInfo.getMapListson().remove(item);
                mData.remove(item);
                orderAdapter.notifyDataSetChanged();
            }
        }).setPositiveButton("取消", null).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add:
                //弹出dailog让用户填写子表
                showAlogToChange("新增", -1);
                break;
        }
    }

    private void showAlogToChange(String title, final int item) {//item=-1是新增,>=0是修改
        View view = View.inflate(getContext(), R.layout.view_add_zibiao, null);
        //        final TextView tv_con = view.findViewById(R.id.tv_con);//内容
        View tv_con = view.findViewById(R.id.view_line);//分隔线
        final TextView tv_date = view.findViewById(R.id.tv_date);//日期
        final TextView tv_zy = view.findViewById(R.id.tv_zy);//摘要
        final TextView tv_zdr = view.findViewById(R.id.tv_zdr);//制单人
        final TextView tv_sqbm = view.findViewById(R.id.tv_sqbm);//申请部门
        final TextView tv_zrbm = view.findViewById(R.id.tv_zrbm);//责任部门
        final TextView tv_wl = view.findViewById(R.id.tv_wl);//往来
        final TextView tv_wlyhzh = view.findViewById(R.id.tv_wlyhzh);//往来银行及账号
        final TextView tv_jhys = view.findViewById(R.id.tv_jhys);//计划预算进度
        final TextView tv_yskm = view.findViewById(R.id.tv_yskm);//预算科目
        final TextView tv_ysye = view.findViewById(R.id.tv_ysye);//预算余额
        final TextView tv_jl = view.findViewById(R.id.tv_jl);//计量
        final TextView tv_sl = view.findViewById(R.id.tv_sl);//数量
        final TextView tv_djhs = view.findViewById(R.id.tv_djhs);//单价含税
        final TextView tv_jehs = view.findViewById(R.id.tv_jehs);//金额含税
        final TextView tv_se = view.findViewById(R.id.tv_se);//税额
        final TextView tv_rmbbhse = view.findViewById(R.id.tv_rmbbhse);//人民币不含税额
        final TextView tv_slpercent = view.findViewById(R.id.tv_slpercent);//税率%
        final TextView tv_fz = view.findViewById(R.id.tv_fz);//辅助
        final TextView tv_fl = view.findViewById(R.id.tv_fl);//辅量
        final TextView tv_fpr = view.findViewById(R.id.tv_fpr);//发票日-权责制
        final TextView tv_bz = view.findViewById(R.id.tv_bz);//备注
        final TextView tv_fpswkm = view.findViewById(R.id.tv_fpswkm);//发票税务科目
        tv_con.setBackgroundColor(getResources().getColor(R.color.black));
        final Map<String, String> sBodyMap;
        //填充数据
        if (item >= 0) {
            sBodyMap = mData.get(item);
            //            tv_con.setText(sBodyMap.get("FName8"));
            tv_date.setText(sBodyMap.get("FTime2"));
            tv_zdr.setText(sBodyMap.get("FName9"));
            tv_sqbm.setText(sBodyMap.get("FName10"));
            tv_zrbm.setText(sBodyMap.get("FName11"));
            tv_wl.setText(sBodyMap.get("FName12"));
            tv_wlyhzh.setText(sBodyMap.get("FBankAccount"));
            tv_jhys.setText(sBodyMap.get("FName13"));
            tv_yskm.setText(sBodyMap.get("FName14"));
            tv_ysye.setText(sBodyMap.get("F_109"));
            tv_jl.setText(sBodyMap.get("FName15"));
            tv_sl.setText(sBodyMap.get("FDecimal"));
            tv_djhs.setText(sBodyMap.get("FDecimal1"));
            tv_jehs.setText(sBodyMap.get("FAmount2"));
            tv_se.setText(sBodyMap.get("FAmount"));
            tv_rmbbhse.setText(sBodyMap.get("FAmount3"));
            tv_slpercent.setText(sBodyMap.get("FAmount10"));
            tv_fz.setText(sBodyMap.get("FName16"));
            tv_fl.setText(sBodyMap.get("FDecimal2"));
            tv_fpr.setText(sBodyMap.get("FTime3"));
            tv_bz.setText(sBodyMap.get("FText2"));
            tv_fpswkm.setText(sBodyMap.get("FName17"));
        } else {
            sBodyMap = new HashMap<>();
        }

        new AlertDialog.Builder(getContext()).setView(view).setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (item < 0) {
                            orderInfo.getMapListson().add(sBodyMap);
                            mData.add(sBodyMap);
                        }
                        orderAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();

        //        setChangeListener(tv_con, "内容", "search", "FName8", item, sBodyMap);
        setChangeListener(tv_date, "日期", "", "FTime2", item, sBodyMap);
        setChangeListener(tv_zdr, "制单人", "search", "FName9", item, sBodyMap);
        setChangeListener(tv_sqbm, "申请部门", "search", "FName10", item, sBodyMap);
        setChangeListener(tv_zrbm, "责任部门", "search", "FName11", item, sBodyMap);
        setChangeListener(tv_wl, "往来", "search", "FName12", item, sBodyMap);
        setChangeListener(tv_wlyhzh, "往来银行及账号", "", "FBankAccount", item, sBodyMap);
        setChangeListener(tv_jhys, "计划预算进度", "search", "FName13", item, sBodyMap);
        setChangeListener(tv_yskm, "预算科目", "search", "FName14", item, sBodyMap);
        setChangeListener(tv_ysye, "预算余额", "", "F_109", item, sBodyMap);
        setChangeListener(tv_jl, "计量", "search", "FName15", item, sBodyMap);
        setChangeListener(tv_sl, "数量", "", "FDecimal", item, sBodyMap);
        setChangeListener(tv_djhs, "单价含税", "", "FDecimal1", item, sBodyMap);
        setChangeListener(tv_jehs, "金额含税", "", "FAmount2", item, sBodyMap);
        setChangeListener(tv_se, "税额", "", "FAmount", item, sBodyMap);
        setChangeListener(tv_rmbbhse, "人民币不含税额", "", "FAmount3", item, sBodyMap);
        setChangeListener(tv_slpercent, "税率%", "", "FAmount10", item, sBodyMap);
        setChangeListener(tv_fz, "辅助", "", "FName16", item, sBodyMap);
        setChangeListener(tv_fl, "辅量", "", "FDecimal2", item, sBodyMap);
        setChangeListener(tv_fpr, "发票日-权责制", "", "FTime3", item, sBodyMap);
        setChangeListener(tv_bz, "备注", "", "FText2", item, sBodyMap);
        setChangeListener(tv_fpswkm, "发票税务科目", "search", "FName17", item, sBodyMap);
    }

    private void setChangeListener(final TextView tv, final String title, final String writeKind, final String whichkey, final int item, final Map<String, String> sBodyMap) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cont = String.valueOf(tv.getText());
                final EditText et = new EditText(getContext());
                et.setText(cont);
                new AlertDialog.Builder(getContext()).setView(et).setTitle(title)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //修改tv的数据
                                String et_con = String.valueOf(et.getText());
                                if (null == writeKind || "".equals(writeKind)) {
                                    tv.setText(et_con);
                                    //TODO:填入子表
                                    if (item >= 0) {
                                        Map<String, String> bodyMap = orderInfo.getMapListson().get(item);
                                        bodyMap.put(whichkey, et_con);
                                        mData.get(item).put(whichkey, et_con);
                                    } else {
                                        //                                        List<Map<String, String>> mapListson = orderInfo.getMapListson();
                                        sBodyMap.put(whichkey, et_con);
                                        //                                        mapListson.add(sBodyMap);
                                    }
                                }
                                if ("search".equals(writeKind)) {
                                    //弹出选择菜单列表,填写内码
                                    showMoreWriteInfo(tv, title, et_con, whichkey, item, sBodyMap);
                                }
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
    }

    private LvShowMoreAdapter showMoreAdapter;

    private void showMoreWriteInfo(final TextView tvcontent, String title, final String cont, final String whichkey, final int item, final Map<String, String> bodyMap) {//cont查找内容是否为空
        View view = View.inflate(getContext(), R.layout.view_only_list, null);
        ListView lv_showmore = view.findViewById(R.id.lv_showmore);
        //新建的，存放更多，查询的内码
        if (null == mShowData) {
            mShowData = new ArrayList<>();
        } else {
            mShowData.clear();
        }
        showMoreAdapter = new LvShowMoreAdapter(getContext(), mShowData);
        lv_showmore.setAdapter(showMoreAdapter);
        //TODO:根据确定的内容查找内码
        String sql = "";
        if (whichkey.equals("FName8")) {//内容
            if (null == cont || "".equals(cont)) {
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0 order by a.fnumber";
            } else {
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0 and a.fname like '%" + cont + "%' order by a.fnumber";
            }
        }
        if (whichkey.equals("FName9")) {//制单人
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName10") || whichkey.equals("FName11")) {//申请部门//责任部门
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Department where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Department where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName12")) {//表体往来
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName13")) {//计划预算进度
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Item_3007  where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Item_3007  where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName14")) {//预算科目
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Item  where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Item  where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName15")) {//计量
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_MeasureUnit where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_MeasureUnit where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName17")) {//发票税务科目
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Item  where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Item  where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        dialog = CustomProgress.show(getContext(), "查找中...", true, null);
        SearchMoreTask moreTask = new SearchMoreTask(sql);
        moreTask.execute();
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setTitle(title).show();
        lv_showmore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvcontent.setText(mShowData.get(i).get("fname"));
                //填入子表
                if (item >= 0) {
                    Map<String, String> bodyMap = orderInfo.getMapListson().get(item);
                    bodyMap.put(whichkey, mShowData.get(i).get("fname"));
                    bodyMap.put(whichkey + "id", mShowData.get(i).get("itemid"));
                    mData.get(item).put(whichkey, mShowData.get(i).get("fname"));
                } else {
                    bodyMap.put(whichkey, mShowData.get(i).get("fname"));
                    bodyMap.put(whichkey + "id", mShowData.get(i).get("itemid"));
                }
                dialog.dismiss();
            }
        });
    }

    //查询更多
    class SearchMoreTask extends AsyncTask<Void, String, String> {
        private String mSql;

        public SearchMoreTask(String sql) {
            this.mSql = sql;
        }

        @Override
        protected void onPreExecute() {
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
            //            String sql = "select fitemid,fname from t_MeasureUnit where fitemid>0";
            rpc.addProperty("FSql", mSql);
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
            }
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("itemid", recordEle.elementTextTrim("fitemid"));
                    map.put("fname", recordEle.elementTextTrim("fname"));
                    mShowData.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            showMoreAdapter.notifyDataSetChanged();
        }
    }
}
