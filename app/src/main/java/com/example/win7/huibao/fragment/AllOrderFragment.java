package com.example.win7.huibao.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.activity.AddOrderActivity;
import com.example.win7.huibao.adapter.LvShowMoreAdapter;
import com.example.win7.huibao.entity.OrderDataInfo;
import com.example.win7.huibao.task.SubmitOrder;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/6/26 9:02
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class AllOrderFragment extends Fragment implements View.OnClickListener {
    private View   mRootView;
    private Button bt_submit;
    private boolean mEditable = false;//是否可编辑
    private TextView tv_hsje, tv_fkwl, tv_orid, tv_RMB, tv_rate, tv_zzjg, tv_nr1, tv_fkze, tv_ljqjx, tv_bdqjx, tv_yfzq, tv_fkl, tv_fkcb, tv_fkhs, tv_jxp, tv_jxfpl, tv_jxfphs, tv_yfk, tv_rckwl, tv_rkcb, tv_ckcb;
    private TextView tv_bdkccb, tv_ljykp, tv_bdykp, tv_yszq, tv_skwl, tv_ysk, tv_xxpwl, tv_xxfpl, tv_xxkphs;
    private String orderID = "";//订单表id
    private OrderDataInfo             mOrderDataInfo;//整个订单表信息
    private List<Map<String, String>> mShowData;//临时存放内码
    private LvShowMoreAdapter         showMoreAdapter;
    private CustomProgress            dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_all_order, null);
        AddOrderActivity activity = (AddOrderActivity) getActivity();
        mEditable = activity.getEditable();
        orderID = activity.getIntent().getStringExtra("orderID");
        mOrderDataInfo = activity.getOrderInfo();
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        tv_orid = mRootView.findViewById(R.id.tv_orid);//单据号
        tv_RMB = mRootView.findViewById(R.id.tv_RMB);//币别
        tv_rate = mRootView.findViewById(R.id.tv_rate);//汇率
        tv_zzjg = mRootView.findViewById(R.id.tv_zzjg);//组织机构
        tv_nr1 = mRootView.findViewById(R.id.tv_nr1);//内容
        tv_hsje = mRootView.findViewById(R.id.tv_hsje);//含税金额合计
        tv_fkze = mRootView.findViewById(R.id.tv_fkze);//付款总额合计
        tv_ljqjx = mRootView.findViewById(R.id.tv_ljqjx);//累计：欠进项发票额
        tv_bdqjx = mRootView.findViewById(R.id.tv_bdqjx);//本单：欠进项发票额
        tv_yfzq = mRootView.findViewById(R.id.tv_yfzq);//应付账期天数
        tv_fkwl = mRootView.findViewById(R.id.tv_fkwl);//付款往来
        tv_fkl = mRootView.findViewById(R.id.tv_fkl);//付款量合计
        tv_fkcb = mRootView.findViewById(R.id.tv_fkcb);//付款成本不含税合计
        tv_fkhs = mRootView.findViewById(R.id.tv_fkhs);//付款含税合计
        tv_jxp = mRootView.findViewById(R.id.tv_jxp);//进项票往来
        tv_jxfpl = mRootView.findViewById(R.id.tv_jxfpl);//进项发票量合计
        tv_jxfphs = mRootView.findViewById(R.id.tv_jxfphs);//进项发票含税总额合计
        tv_yfk = mRootView.findViewById(R.id.tv_yfk);//应付款合计
        tv_rckwl = mRootView.findViewById(R.id.tv_rckwl);//入出库往来
        tv_rkcb = mRootView.findViewById(R.id.tv_rkcb);//入库成本合计
        tv_ckcb = mRootView.findViewById(R.id.tv_ckcb);//出库成本合计
        tv_bdkccb = mRootView.findViewById(R.id.tv_bdkccb);//本单库存成本
        tv_ljykp = mRootView.findViewById(R.id.tv_ljykp);//累计：已开票未收款额
        tv_bdykp = mRootView.findViewById(R.id.tv_bdykp);//本单：已开票未收款额
        tv_yszq = mRootView.findViewById(R.id.tv_yszq);//应收账期天数
        tv_skwl = mRootView.findViewById(R.id.tv_skwl);//收款往来
        tv_ysk = mRootView.findViewById(R.id.tv_ysk);//应收款合计
        tv_xxpwl = mRootView.findViewById(R.id.tv_xxpwl);//销项票往来
        tv_xxfpl = mRootView.findViewById(R.id.tv_xxfpl);//销项发票量合计
        tv_xxkphs = mRootView.findViewById(R.id.tv_xxkphs);//销项开票含税总额合计
        bt_submit = mRootView.findViewById(R.id.bt_submit);//提交
    }

    private void initData() {
        //先行判断，进入条件
        Intent intent = getActivity().getIntent();
        String kind = intent.getStringExtra("kind");
        if (null == kind) {
            ToastUtils.showToast(getContext(), "error:未传递种类");
            getActivity().finish();
            return;
        }
        if (kind.equals("check") || kind.equals("edit")) {//查看//编辑
            if (kind.equals("check"))
                bt_submit.setVisibility(View.GONE);
            //填入数据
            writeDataIn();
        }
        //给tv设置点击修改事件
        setListener();
        bt_submit.setOnClickListener(this);
    }

    private void setListener() {
        if (mEditable) {
//            setTvChangeListener(tv_orid, "单据号", "", "FBillNo");
            setTvChangeListener(tv_RMB, "币别", "search", "FName");
            setTvChangeListener(tv_rate, "汇率", "", "FAmount4");
            setTvChangeListener(tv_zzjg, "组织机构", "search", "FName1");
            setTvChangeListener(tv_nr1, "内容", "search", "FName5");
            setTvChangeListener(tv_hsje, "含税金额合计", "", "");
            setTvChangeListener(tv_fkze, "付款总额合计", "", "");
            setTvChangeListener(tv_ljqjx, "累计：欠进项发票额", "", "FAmount36");
            setTvChangeListener(tv_bdqjx, "本单：欠进项发票额", "", "FAmount29");
            setTvChangeListener(tv_yfzq, "应付账期天数", "", "FInteger");
            setTvChangeListener(tv_fkwl, "付款往来", "search", "fname2");
            setTvChangeListener(tv_fkl, "付款量合计", "", "FDecimal8");
            setTvChangeListener(tv_fkcb, "付款成本不含税合计", "", "FAmount9");
            setTvChangeListener(tv_fkhs, "付款含税合计", "", "FAmount17");
            setTvChangeListener(tv_jxp, "进项票往来", "search", "FName3");
            setTvChangeListener(tv_jxfpl, "进项发票量合计", "", "FDecimal12");
            setTvChangeListener(tv_jxfphs, "进项发票含税总额合计", "", "FAmount16");
            setTvChangeListener(tv_yfk, "应付款合计", "", "FAmount27");
            setTvChangeListener(tv_rckwl, "入出库往来", "search", "FName4");
            setTvChangeListener(tv_rkcb, "入库成本合计", "", "FAmount12");
            setTvChangeListener(tv_ckcb, "出库成本合计", "", "FAmount13");
            //            setTvChangeListener(tv_bdkccb, "本单库存成本", "", "");
            setTvChangeListener(tv_ljykp, "累计：已开票未收款额", "", "FAmount37");
            setTvChangeListener(tv_bdykp, "本单：已开票未收款额", "", "FAmount30");
            setTvChangeListener(tv_yszq, "应收账期天数", "", "FInteger1");
            setTvChangeListener(tv_skwl, "收款往来", "search", "FName6");
            setTvChangeListener(tv_ysk, "应收款合计", "", "FAmount28");
            setTvChangeListener(tv_xxpwl, "销项票往来", "search", "FName7");
            setTvChangeListener(tv_xxfpl, "销项发票量合计", "", "FDecimal13");
            setTvChangeListener(tv_xxkphs, "销项开票含税总额合计", "", "FAmount19");
        }
    }

    private void setTvChangeListener(final TextView tv, final String title, final String writekind, final String which) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeViewContent(tv, title, writekind, which);
            }
        });
    }

    private void writeDataIn() {
        tv_orid.setText(mOrderDataInfo.getHeadData().get("FBillNo"));
        tv_RMB.setText(mOrderDataInfo.getHeadData().get("FName"));
        tv_zzjg.setText(mOrderDataInfo.getHeadData().get("FName1"));
        tv_nr1.setText(mOrderDataInfo.getHeadData().get("FName5"));
        //        tv_hsje.setText(orderInfo.getMoneyTax());
        //        tv_fkze.setText(orderInfo.get);
        tv_ljqjx.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount36")));
        tv_bdqjx.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount29")));
        tv_yfzq.setText(mOrderDataInfo.getHeadData().get("FInteger"));
        tv_fkwl.setText(mOrderDataInfo.getHeadData().get("fname2"));
        tv_fkl.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FDecimal8")));
        tv_fkcb.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount9")));
        tv_fkhs.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount17")));
        tv_jxp.setText(mOrderDataInfo.getHeadData().get("FName3"));
        tv_jxfpl.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FDecimal12")));
        tv_jxfphs.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount16")));
        tv_yfk.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount27")));
        tv_rckwl.setText(mOrderDataInfo.getHeadData().get("FName4"));
        tv_rkcb.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount12")));
        tv_ckcb.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount13")));
        //        tv_bdkccb.setText(orderInfo.get);
        tv_ljykp.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount37")));
        tv_bdykp.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount30")));
        tv_yszq.setText(mOrderDataInfo.getHeadData().get("FInteger1"));
        tv_skwl.setText(mOrderDataInfo.getHeadData().get("FName6"));
        tv_ysk.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount28")));
        tv_xxpwl.setText(mOrderDataInfo.getHeadData().get("FName7"));
        tv_xxfpl.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FDecimal13")));
        tv_xxkphs.setText(getTowPoint(mOrderDataInfo.getHeadData().get("FAmount19")));
    }

    private String getTowPoint(String numString) {
        if (null == numString || "".equals(numString)) {
            return "0.00";
        }
        double price = Double.parseDouble(numString);
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String format = df.format(price);
        return format;
    }

    private boolean canSub = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_submit:

                //提交前先判断，需提交内码的不能为空
                Map<String, String> headData = mOrderDataInfo.getHeadData();
                //判断是否为空
                isNullStr(headData.get("FNameid"), "币别");
                isNullStr(headData.get("FName1id"), "组织机构");
                isNullStr(headData.get("fname2id"), "付款往来");
                isNullStr(headData.get("FName3id"), "进项票往来");
                isNullStr(headData.get("FName4id"), "入出库往来");
                isNullStr(headData.get("FName5id"), "内容");
                isNullStr(headData.get("FName6id"), "收款往来");
                isNullStr(headData.get("FName7id"), "销项票往来");
                List<Map<String, String>> mapListson = mOrderDataInfo.getMapListson();
                for (int i = 0; i < mapListson.size(); i++) {
                    Map<String, String> dataMap = mapListson.get(i);
                    isNullStr(dataMap.get("FName8id"), "第" + (i + 1) + "子表的" + "内容");
                    isNullStr(dataMap.get("FTime2"),"第" + (i + 1) + "子表的" + "日期");
                    isNullStr(dataMap.get("FName9id"), "第" + (i + 1) + "子表的" + "制单人");
                    isNullStr(dataMap.get("FName10id"), "第" + (i + 1) + "子表的" + "申请部门");
                    isNullStr(dataMap.get("FName12id"), "第" + (i + 1) + "子表的" + "往来");
                    //isNullStr(dataMap.get("FBankAccountid"), "第" + (i + 1) + "子表的" + "往来-银行及帐号");
                    isNullStr(dataMap.get("FName15id"), "第" + (i + 1) + "子表的" + "计量");
                    isNullStr(dataMap.get("FName17id"), "第" + (i + 1) + "子表的" + "发票税务科目");
                }
                if (canSub) {
                    //提交
                    sendInfo();
                }
                break;
            default:
                break;
        }
    }

    private void isNullStr(String str, String name) {
        if (null == str || "".equals(str)) {
            ToastUtils.showToast(getContext(), name + "不能为空");
            canSub = false;
            return;
        } else {
            canSub = true;
        }
    }

    private void sendInfo() {
        SubmitOrder submitOrder = new SubmitOrder(mOrderDataInfo, getContext());
        submitOrder.execute();
    }

    private void changeViewContent(final TextView tvcontent, final String title, final String writekind, final String whichkey) {
        //弹出dailog展示修改内容
        final EditText et = new EditText(getContext());
        //写入数据
        String oldContent = String.valueOf(tvcontent.getText());
        et.setText(oldContent);
        new AlertDialog.Builder(getContext()).setView(et).setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //修改改变内容的textview
                        String content = String.valueOf(et.getText()).trim();
                        if (null == writekind || "".equals(writekind)) {
                            tvcontent.setText(content);
                            mOrderDataInfo.getHeadData().put(whichkey, content);
                        }
                        if ("search".equals(writekind)) {
                            //为空，弹出选择菜单列表
                            showMoreWriteInfo(tvcontent, title, content, whichkey);
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }

    private boolean isSearchBibie = false;

    private void showMoreWriteInfo(final TextView tvcontent, String title, final String cont, final String whichkey) {//cont查找内容是否为空
        View view = View.inflate(getContext(), R.layout.view_only_list, null);
        ListView lv_showmore = view.findViewById(R.id.lv_showmore);
        if (null == mShowData) {
            mShowData = new ArrayList();
        } else {
            mShowData.clear();
        }
        showMoreAdapter = new LvShowMoreAdapter(getContext(), mShowData);
        lv_showmore.setAdapter(showMoreAdapter);
        //TODO:根据确定的内容查找内码
        String sql = "";
        if (whichkey.equals("FName")) {//币别
            isSearchBibie = true;
            if (null == cont || "".equals(cont)) {
                sql = "select fcurrencyid,FName,FExchangeRate from t_Currency where fcurrencyid>0";
            } else {
                sql = "select fcurrencyid,FName,FExchangeRate from t_Currency where fcurrencyid>0 and FName like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName1")) {//组织机构
            isSearchBibie = false;
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Item_3001 where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Item_3001 where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("fname2")) {//付款往来
            isSearchBibie = false;
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName3")) {//进项票往来
            isSearchBibie = false;
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName4")) {//入出库往来
            isSearchBibie = false;
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName5")) {//内容
            isSearchBibie = false;
            if (null == cont || "".equals(cont)) {
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0 order by a.fnumber";
            } else {
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0 and a.fname like '%" + cont + "%' order by a.fnumber";
            }
        }
        if (whichkey.equals("FName6")) {//收款往来
            isSearchBibie = false;
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        if (whichkey.equals("FName7")) {//销项票往来
            isSearchBibie = false;
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        SearchMoreTask moreTask = new SearchMoreTask(sql);
        moreTask.execute();
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setTitle(title).show();
        lv_showmore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvcontent.setText(mShowData.get(i).get("fname"));
                mOrderDataInfo.getHeadData().put(whichkey, mShowData.get(i).get("fname"));
                mOrderDataInfo.getHeadData().put(whichkey + "id", mShowData.get(i).get("itemid"));
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
            dialog = CustomProgress.show(getContext(), "查找中...", true, null);
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
                    if (isSearchBibie) {
                        map.put("itemid", recordEle.elementTextTrim("fcurrencyid"));
                        map.put("fname", recordEle.elementTextTrim("FName"));
                    } else {
                        map.put("itemid", recordEle.elementTextTrim("fitemid"));
                        map.put("fname", recordEle.elementTextTrim("fname"));
                    }
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
