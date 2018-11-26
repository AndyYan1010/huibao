package com.example.win7.huibao.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.adapter.MyPagerAdapter;
import com.example.win7.huibao.entity.OrderDataInfo;
import com.example.win7.huibao.fragment.AllOrderFragment;
import com.example.win7.huibao.fragment.ApplyFragment;
import com.example.win7.huibao.fragment.SubBillFragment;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.view.CustomProgress;
import com.example.win7.huibao.view.MyFixedViewpager;

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
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/6/28 15:19
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class AddOrderActivity extends BaseActivity implements View.OnClickListener {
    private ImageView        img_back;
    private TextView         tv_title;
    private TabLayout        mTablayout;//导航标签
    private MyFixedViewpager mView_pager;//自我viewpager可实现禁止滑动
    private String[] mStrings  = {"主表", "子表","账表"};
    private boolean  mEditable = false;
    private CustomProgress dialog;
    private String orderID = "";//订单表id
    private OrderDataInfo orderInfo;//订单表详情封装
    private String        kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        Intent intent = getIntent();
        kind = intent.getStringExtra("kind");
        if (null == kind) {
            ToastUtils.showToast(AddOrderActivity.this, "error:未传递种类");
            finish();
            return;
        }
        if (kind.equals("check")) {//查看
            mEditable = false;
        }
        if (kind.equals("edit")) {//修改
            mEditable = true;
        }
        if (kind.equals("add")) {//新增
            mEditable = true;
        }
        orderID = intent.getStringExtra("orderID");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        img_back = findViewById(R.id.img_back);
        tv_title = findViewById(R.id.tv_title);
        mTablayout = findViewById(R.id.tablayout);
        mView_pager = findViewById(R.id.view_pager);
    }

    private void initData() {
        String title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        orderInfo = new OrderDataInfo();
        if (kind.equals("check") || kind.equals("edit")) {
            //获取订单表详情
            new ZHUTask(orderID).execute();
        }
        if (kind.equals("add")) {
            orderInfo.getHeadData().put("innerid", "0");
            orderInfo.getHeadData().put("FBillNo", "a");
            //初始设置maplist
            //            List<Map<String, String>> dataMap = new ArrayList<>();
            //            orderInfo.setMapListson(dataMap);
            //初始化导航页
            initTabFragment();
        }
    }

    private void initListener() {
        img_back.setOnClickListener(this);
    }

    private void initTabFragment() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        //主表界面
        AllOrderFragment allOrderFragment = new AllOrderFragment();
        fragments.add(allOrderFragment);
        //子表界面
        ApplyFragment applyFragment = new ApplyFragment();
        fragments.add(applyFragment);
        //账表界面
        SubBillFragment subBillFragment = new SubBillFragment();
        fragments.add(subBillFragment);

        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        // 给ViewPager设置适配器
        mView_pager.setAdapter(myPagerAdapter);
        //设置viewpager不可滑动
        mView_pager.setCanScroll(false);
        //tablayout关联tablayout和viewpager实现联动
        mTablayout.setupWithViewPager(mView_pager);
        for (int i = 0; i < mStrings.length; i++) {
            mTablayout.getTabAt(i).setText(mStrings[i]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (mEditable) {
                    new AlertDialog.Builder(AddOrderActivity.this)
                            .setTitle("确认退出？").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).setPositiveButton("取消", null).show();
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEditable) {
                new AlertDialog.Builder(AddOrderActivity.this)
                        .setTitle("确认退出？").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setPositiveButton("取消", null).show();
            } else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean getEditable() {
        return mEditable;
    }

    public OrderDataInfo getOrderInfo() {
        return orderInfo;
    }

    class ZHUTask extends AsyncTask<Void, String, String> {
        String mOrderID;

        ZHUTask(String orderID) {
            this.mOrderID = orderID;
        }

        @Override
        protected void onPreExecute() {
            dialog = CustomProgress.show(AddOrderActivity.this, "加载中...", true, null);
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
            String sql = "select a.FBillNo,c.FName,c.fcurrencyid FNameid,a.FAmount4,d.FName,d.fitemid FName1id,FAmount36,FAmount29," +
                    " a.FInteger,e.fname,e.fitemid fname2id,a.FDecimal8,a.FAmount9,FAmount17,f.FName,f.fitemid FName3id,FDecimal12,FAmount16,FAmount27,g.FName,g.fitemid FName4id,h.FName,h.fitemid FName5id," +
                    " FAmount12,FAmount13,FAmount37,FAmount30,FInteger1,i.FName,i.fitemid FName6id,FAmount28,j.FName,j.fitemid FName7id,FDecimal13,FAmount19," +
                    " k.FName,k.fitemid FName8id,b.FNOTE2,FTime2,l.FName,l.fitemid FName9id,m.FName,m.fitemid FName10id,n.FName,n.fitemid FName11id,o.FName,o.fitemid FName12id,o.FBankAccount,p.FName,p.fitemid FName13id,q.FName,q.fitemid FName14id,p.F_109," +
                    " r.FName,r.fitemid FName15id,b.FDecimal,b.FDecimal1,b.FAmount2,FAmount,b.FAmount3,b.FAmount10,s.FName,s.fitemid FName16id,b.FDecimal," +
                    " b.FTime3,b.FText2,t.FName,t.fitemid FName17id,u.FName,b.FText,b.FText1 " +
                    " from t_BOS200000011 a inner join t_BOS200000011Entry2 b on a.FID=b.FID" +
                    " left join t_Currency c on c.FCurrencyID=a.FBase3 left join t_Item_3001 d on d.FItemID=a.FBase11 left join t_Emp e on e.FItemID=a.FBase24 left join" +
                    " t_Emp f on f.FItemID=a.FBase13 left join t_Emp g on g.FItemID=a.FBase25 left join t_ICItem h on h.FItemID=a.FBase26 left join" +
                    " t_Emp i on i.FItemID=a.FBase27 left join t_Emp j on J.FItemID=a.FBase16 left join t_ICItem k on k.FItemID=b.FBase1 left join" +
                    " t_Emp l on l.FItemID=b.FBase15 left join t_Department m on m.FItemID=b.FBase18 left join t_Department n on n.FItemID=b.FBase14 left join" +
                    " t_Emp o on o.FItemID=b.FBase10 left join t_Item_3007 p on p.FItemID=b.FBase left join t_Item q on q.FItemID=b.FBase21 left join" +
                    " t_MeasureUnit r on r.FItemID=b.FBase2 left join t_MeasureUnit s on s.FItemID=k.FSecUnitID left join t_Item t on t.FItemID=b.FBase17" +
                    " left join t_Item u on u.FItemID=b.FBase14 where a.FBillNo ='" + mOrderID + "'";
            Log.i("主表查询语句", sql);
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
            }
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString() + "=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            //子订单存放列表maplist
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    //表头
                    String fbillNo = recordEle.elementTextTrim("FBillNo");//单据号
                    String currency = recordEle.elementTextTrim("FName");//币种
                    String currencyid = recordEle.elementTextTrim("FNameid");//币种id
                    String rate = recordEle.elementTextTrim("FAmount4");//汇率
                    String orga = recordEle.elementTextTrim("FName1");//组织机构
                    String orgaid = recordEle.elementTextTrim("FName1id");//组织机构id
                    String oweInvTotal = recordEle.elementTextTrim("FAmount36");//累计欠进项发票额
                    String thisOweInv = recordEle.elementTextTrim("FAmount29");//本单欠进项发票额
                    String payDate = recordEle.elementTextTrim("FInteger");//应付账期天数(进项发票日算)
                    String payContact = recordEle.elementTextTrim("fname2");//付款往来
                    String payContactid = recordEle.elementTextTrim("fname2id");//付款往来id
                    String payAmount = recordEle.elementTextTrim("FDecimal8");//付款量合计
                    String paynoTax = recordEle.elementTextTrim("FAmount9");//付款成本不含税合计
                    String paywithTax = recordEle.elementTextTrim("FAmount17");//付款含税合计
                    String incomeCont = recordEle.elementTextTrim("FName3");//进项票往来
                    String incomeContid = recordEle.elementTextTrim("FName3id");//进项票往来id
                    String incomeInv = recordEle.elementTextTrim("FDecimal12");//进项发票量合计
                    String invwithTax = recordEle.elementTextTrim("FAmount16");//进项发票含税总额合计
                    String spayTotal = recordEle.elementTextTrim("FAmount27");//应付款合计
                    String innerIncome = recordEle.elementTextTrim("FName4");//入出库往来
                    String innerIncomeid = recordEle.elementTextTrim("FName4id");//入出库往来id
                    String content = recordEle.elementTextTrim("FName5");//内容
                    String contentid = recordEle.elementTextTrim("FName5id");//内容id
                    String innnerCost = recordEle.elementTextTrim("FAmount12");//入库成本合计
                    String outCost = recordEle.elementTextTrim("FAmount13");//出库成本合计
                    String totalUnrece = recordEle.elementTextTrim("FAmount37");//累计：已开票未收款额！！
                    String thisUnrece = recordEle.elementTextTrim("FAmount30");//本单：已开票未收款额！！
                    String shRecceData = recordEle.elementTextTrim("FInteger1");//应收帐期天数（销项发票日算）！！
                    String recIncome = recordEle.elementTextTrim("FName6");//收款往来
                    String recIncomeid = recordEle.elementTextTrim("FName6id");//收款往来id
                    String shRecTotal = recordEle.elementTextTrim("FAmount28");//应收款合计
                    String outTicIncome = recordEle.elementTextTrim("FName7");//销项票往来
                    String outTicIncomeid = recordEle.elementTextTrim("FName7id");//销项票往来id
                    String outTickTotal = recordEle.elementTextTrim("FDecimal13");//销项发票量合计
                    String outTickWTax = recordEle.elementTextTrim("FAmount19");//销项开票含税总额合计

                    Map<String, String> headMapData = orderInfo.getHeadData();
                    headMapData.put("FBillNo", fbillNo);
                    headMapData.put("FName", currency);
                    headMapData.put("FNameid", currencyid);
                    headMapData.put("FAmount4", rate);
                    headMapData.put("FName1", orga);
                    headMapData.put("FName1id", orgaid);
                    headMapData.put("FAmount36", oweInvTotal);
                    headMapData.put("FAmount29", thisOweInv);
                    headMapData.put("FInteger", payDate);
                    headMapData.put("fname2", payContact);
                    headMapData.put("fname2id", payContactid);
                    headMapData.put("FDecimal8", payAmount);
                    headMapData.put("FAmount9", paynoTax);
                    headMapData.put("FAmount17", paywithTax);
                    headMapData.put("FName3", incomeCont);
                    headMapData.put("FName3id", incomeContid);
                    headMapData.put("FDecimal12", incomeInv);
                    headMapData.put("FAmount16", invwithTax);
                    headMapData.put("FAmount27", spayTotal);
                    headMapData.put("FName4", innerIncome);
                    headMapData.put("FName4id", innerIncomeid);
                    headMapData.put("FName5", content);
                    headMapData.put("FName5id", contentid);
                    headMapData.put("FAmount12", innnerCost);
                    headMapData.put("FAmount13", outCost);
                    headMapData.put("FAmount37", totalUnrece);
                    headMapData.put("FAmount30", thisUnrece);
                    headMapData.put("FInteger1", shRecceData);
                    headMapData.put("FName6", recIncome);
                    headMapData.put("FName6id", recIncomeid);
                    headMapData.put("FAmount28", shRecTotal);
                    headMapData.put("FName7", outTicIncome);
                    headMapData.put("FName7id", outTicIncomeid);
                    headMapData.put("FDecimal13", outTickTotal);
                    headMapData.put("FAmount19", outTickWTax);

                    //表体
                    String content2 = recordEle.elementTextTrim("FName8");//内 容
                    String content2id = recordEle.elementTextTrim("FName8id");//内 容id
                    String remark = recordEle.elementTextTrim("FNOTE2");//摘要
                    String data = recordEle.elementTextTrim("FTime2");//日期
                    String singPerson = recordEle.elementTextTrim("FName9");//制单人
                    String singPersonid = recordEle.elementTextTrim("FName9id");//制单人id
                    String applyPart = recordEle.elementTextTrim("FName10");//申请部门
                    String applyPartid = recordEle.elementTextTrim("FName10id");//申请部门id
                    String responsPart = recordEle.elementTextTrim("FName11");//责任部门/考核
                    String responsPartid = recordEle.elementTextTrim("FName11id");//责任部门/考核
                    String bodyIncome = recordEle.elementTextTrim("FName12");//表体往来
                    String bodyIncomeid = recordEle.elementTextTrim("FName12id");//表体往来id
                    String bankIncome = recordEle.elementTextTrim("FBankAccount");//往来-银行及帐号
                    // String bankIncomeid = recordEle.elementTextTrim("FName12id");//往来-银行及帐号id
                    String planBudget = recordEle.elementTextTrim("FName13");//计划预算进度
                    String planBudgetid = recordEle.elementTextTrim("FName13id");//计划预算进度
                    String budSub = recordEle.elementTextTrim("FName14");//预算科目
                    String budSubid = recordEle.elementTextTrim("FName14id");//预算科目
                    String budBalance = recordEle.elementTextTrim("F_109");//预算余额
                    String unit = recordEle.elementTextTrim("FName15");//计量
                    String unitid = recordEle.elementTextTrim("FName15id");//计量id
                    String number = recordEle.elementTextTrim("FDecimal");//数量
                    String unitPrice = recordEle.elementTextTrim("FDecimal1");//单价含税
                    String moneyTax = recordEle.elementTextTrim("FAmount2");//金额含税
                    String taxAmount = recordEle.elementTextTrim("FAmount");//税额
                    String RMBNoTax = recordEle.elementTextTrim("FAmount3");//人民币不含税额
                    String taxRate = recordEle.elementTextTrim("FAmount10");//税率%
                    String unitOther = recordEle.elementTextTrim("FName16");//辅助
                    String unitOtherid = recordEle.elementTextTrim("FName16id");//辅助
                    String unitNum = recordEle.elementTextTrim("FDecimal2");//辅量
                    String ticDataRespon = recordEle.elementTextTrim("FTime3");//发票日-权责制
                    String remarkTicNO = recordEle.elementTextTrim("FText2");//备注-发票号码/税票说明
                    String ticTaxSub = recordEle.elementTextTrim("FName17");//发票税务科目
                    String ticTaxSubid = recordEle.elementTextTrim("FName17id");//发票税务科目
                    String score = recordEle.elementTextTrim("FName19");//评分
                    String sendMsg = recordEle.elementTextTrim("FText");//发送消息
                    String getMsg = recordEle.elementTextTrim("FText1");//回馈消息

                    Map dataMap = new HashMap();
                    dataMap.put("FName8", content2);
                    dataMap.put("FName8id", content2id);
                    dataMap.put("FNOTE2", remark);
                    dataMap.put("FTime2", data);
                    dataMap.put("FName9", singPerson);
                    dataMap.put("FName9id", singPersonid);
                    dataMap.put("FName10", applyPart);
                    dataMap.put("FName10id", applyPartid);
                    dataMap.put("FName11", responsPart);
                    dataMap.put("FName11id", responsPartid);
                    dataMap.put("FName12", null == bodyIncome ? "" : bodyIncome);
                    dataMap.put("FName12id", null == bodyIncomeid ? "" : bodyIncomeid);
                    dataMap.put("FBankAccount", null == bankIncome ? "" : bankIncome);
                    dataMap.put("FName13", planBudget);
                    dataMap.put("FName13id", planBudgetid);
                    dataMap.put("FName14", budSub);
                    dataMap.put("FName14id", budSubid);
                    dataMap.put("F_109", budBalance);
                    dataMap.put("FName15", unit);
                    dataMap.put("FName15id", unitid);
                    dataMap.put("FDecimal", number);
                    dataMap.put("FDecimal1", unitPrice);
                    dataMap.put("FAmount2", moneyTax);
                    dataMap.put("FAmount", taxAmount);
                    dataMap.put("FAmount3", RMBNoTax);
                    dataMap.put("FAmount10", taxRate);
                    dataMap.put("FName16", unitOther);
                    dataMap.put("FName16id", unitOtherid);
                    dataMap.put("FDecimal2", unitNum);
                    dataMap.put("FTime3", ticDataRespon);
                    dataMap.put("FText2", remarkTicNO);
                    dataMap.put("FName17", ticTaxSub);
                    dataMap.put("FName17id", ticTaxSubid);
                    dataMap.put("FName18", score);
                    dataMap.put("FText", sendMsg);
                    dataMap.put("FText1", getMsg);
                    orderInfo.getMapListson().add(dataMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(AddOrderActivity.this, "查找详情出错");
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            //根据fbillNo查询innerid
            String fbillNo = orderInfo.getHeadData().get("FBillNo");
            searchForInnerID(fbillNo);
        }
    }

    private void searchForInnerID(String fbillNo) {
        InnerIDTask idTask = new InnerIDTask(fbillNo);
        idTask.execute();
    }

    //查询innerid
    class InnerIDTask extends AsyncTask<Void, String, String> {
        String mFbillNo;

        InnerIDTask(String fbillNo) {
            this.mFbillNo = fbillNo;
        }

        @Override
        protected void onPreExecute() {
            dialog = CustomProgress.show(AddOrderActivity.this, "加载中...", true, null);
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
            String sql = "select fid from t_BOS200000011  where fbillno='" + mFbillNo + "'";
            Log.i("主表查询语句", sql);
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
                    String fid = recordEle.elementTextTrim("fid");//InnerID
                    orderInfo.getHeadData().put("innerid", fid);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(AddOrderActivity.this, "未查找到InnerID");
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            //初始化导航页
            initTabFragment();
        }
    }
}
