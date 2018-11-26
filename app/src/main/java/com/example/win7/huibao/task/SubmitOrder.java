package com.example.win7.huibao.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.win7.huibao.entity.OrderDataInfo;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.view.CustomProgress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.StringWriter;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/6 9:10
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class SubmitOrder extends AsyncTask<Void, String, String> {
    private OrderDataInfo mOrderDataInfo;
    private Context       mContext;

    public SubmitOrder(OrderDataInfo orderDataInfo, Context context) {
        this.mOrderDataInfo = orderDataInfo;
        this.mContext = context;
    }

    CustomProgress dialog;

    @Override
    protected void onPreExecute() {
        dialog = CustomProgress.show(mContext, "提交中...", true, null);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            // 命名空间
            String nameSpace = "http://tempuri.org/";
            // 调用的方法名称
            String methodName = "t_BOS200000011";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/t_BOS200000011";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);
            // 设置需调用WebService接口需要传入的参数
            rpc.addProperty("InterID", mOrderDataInfo.getHeadData().get("innerid"));//mr.0
            rpc.addProperty("BillNO", mOrderDataInfo.getHeadData().get("FBillNo"));//mr.a
            //表头
            Document document = DocumentHelper.createDocument();
            Element rootElement = document.addElement("NewDataSet");
            Element cust = rootElement.addElement("Cust");
            //币别
            cust.addElement("FBase3").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FNameid")));//....
            //汇率
            String fAmount4 = getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount4"));
            if (null == fAmount4 || "".equals(fAmount4)) {
                fAmount4 = "1";
            }
            cust.addElement("FAmount4").setText(fAmount4);
            //组织机构
            cust.addElement("FBase11").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FName1id")));//...
            //累计：欠进项发票额！！
            cust.addElement("FAmount36").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount36")));
            //本单：欠进项发票额！
            cust.addElement("FAmount29").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount29")));
            //应付帐期天数（进项发票日算）！
            cust.addElement("FInteger").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FInteger")));
            //付款往来
            cust.addElement("FBase24").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("fname2id")));//...
            //付款量合计
            cust.addElement("FDecimal8").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FDecimal8")));
            //付款成本不含税合计
            cust.addElement("FAmount9").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount9")));
            //付款含税合计
            cust.addElement("FAmount17").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount17")));
            //进项票往来
            cust.addElement("FBase13").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FName3id")));//..
            //进项发票量合计
            cust.addElement("FDecimal12").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FDecimal12")));
            //进项发票含税总额合计
            cust.addElement("FAmount16").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount16")));
            //应付款合计
            cust.addElement("FAmount27").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount27")));
            //入出库往来
            cust.addElement("FBase25").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FName4id")));//...
            //内容
            cust.addElement("FBase26").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FName5id")));//...
            //入库成本合计
            cust.addElement("FAmount12").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount12")));
            //出库成本合计
            cust.addElement("FAmount13").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount13")));
            //累计：已开票未收款额！！
            cust.addElement("FAmount37").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount37")));
            //本单：已开票未收款额！！
            cust.addElement("FAmount30").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount30")));
            //应收帐期天数（销项发票日算）！！
            cust.addElement("FInteger1").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FInteger1")));
            //收款往来
            cust.addElement("FBase27").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FName6id")));//...
            //应收款合计
            cust.addElement("FAmount28").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount28")));
            //销项票往来
            cust.addElement("FBase16").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FName7id")));//...
            //销项发票量合计
            cust.addElement("FDecimal13").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FDecimal13")));
            //销项开票含税总额合计
            cust.addElement("FAmount19").setText(getSubKeyStr(mOrderDataInfo.getHeadData().get("FAmount19")));
            //表体
            Document document2 = DocumentHelper.createDocument();
            Element rootElement2 = document2.addElement("NewDataSet");
            for (Map<String, String> dataMap : mOrderDataInfo.getMapListson()) {
                Element cust2 = rootElement2.addElement("Cust");
                //内容
                cust2.addElement("FBase1").setText(getSubKeyStr(dataMap.get("FName8id")));//...
                //摘要
                String fnote2 = getSubKeyStr(dataMap.get("FNOTE2"));
                if ("".equals(fnote2)) {
                    fnote2 = "1";
                }
                cust2.addElement("FNOTE2").setText(fnote2);
                //日期
                cust2.addElement("FTime2").setText(getSubKeyStr(dataMap.get("FTime2")));
                //制单人
                cust2.addElement("FBase15").setText(getSubKeyStr(dataMap.get("FName9id")));//...
                //申请部门
                cust2.addElement("FBase18").setText(getSubKeyStr(dataMap.get("FName10id")));//...
                //责任部门/考核
                cust2.addElement("FBase14").setText(getSubKeyStr(dataMap.get("FName11id")));//..
                //表体往来
                cust2.addElement("FBase10").setText(getSubKeyStr(dataMap.get("FName12id")));//...
                //计划预算进度
                cust2.addElement("FBase").setText(getSubKeyStr(dataMap.get("FName13id")));//...
                //预算科目
                cust2.addElement("FBase21").setText(getSubKeyStr(dataMap.get("FName14id")));//...
                //计量
                cust2.addElement("FBase2").setText(getSubKeyStr(dataMap.get("FName15id")));//...
                //数量
                cust2.addElement("FDecimal").setText(getSubKeyStr(dataMap.get("FDecimal")));
                //单价
                cust2.addElement("FDecimal1").setText(getSubKeyStr(dataMap.get("FDecimal1")));
                //金额含税
                cust2.addElement("FAmount2").setText(getSubKeyStr(dataMap.get("FAmount2")));
                //税额
                cust2.addElement("FAmount").setText(getSubKeyStr(dataMap.get("FAmount")));
                //人民币不含税额
                cust2.addElement("FAmount3").setText(getSubKeyStr(dataMap.get("FAmount3")));
                //税率%
                cust2.addElement("FAmount10").setText(getSubKeyStr(dataMap.get("FAmount10")));
                //发票日-权责制
                cust2.addElement("FTime3").setText(getSubKeyStr(dataMap.get("FTime3")));
                //备注-发票号码/税票说明
                cust2.addElement("FText2").setText(getSubKeyStr(dataMap.get("FText2")));
                //发票税务科目
                cust2.addElement("FBase17").setText(getSubKeyStr(dataMap.get("FName17id")));//...
                //发送消息
                cust2.addElement("FText").setText("1");
                //回馈消息
                cust2.addElement("FText1").setText("1");

                //                //计划预算进度
                //                cust2.addElement("FName13").setText(getSubKeyStr(dataMap.get("FName13")));
                //                        //预算科目
                //                cust2.addElement("FName14").setText(getSubKeyStr(dataMap.get("FName14")));
                //                //预算余额
                //                cust2.addElement("F_109").setText(getSubKeyStr(dataMap.get("F_109")));
                //                //辅助
                //                cust2.addElement("FName16").setText(getSubKeyStr(dataMap.get("FName16")));
                //                //辅量
                //                cust2.addElement("FDecimal2").setText(getSubKeyStr(dataMap.get("FDecimal2")));
            }
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setSuppressDeclaration(false);
            outputFormat.setNewlines(false);
            StringWriter stringWriter = new StringWriter();
            StringWriter stringWriter2 = new StringWriter();
            // xmlWriter是用来把XML文档写入字符串的(工具)
            XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
            XMLWriter xmlWriter2 = new XMLWriter(stringWriter2, outputFormat);
            // 把创建好的XML文档写入字符串
            xmlWriter.write(document);
            xmlWriter2.write(document2);
            String fbtouxml = stringWriter.toString().substring(38);
            String fbtixml = stringWriter2.toString().substring(38);
            rpc.addProperty("FBtouXMl", fbtouxml);
            rpc.addProperty("FBtiXML", fbtixml);

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
            String result = "";
            if (envelope.bodyIn instanceof SoapFault) {
                //                Log.i("服务器返回", (SoapObject) envelope.getResponse() + "");
            } else {
                SoapObject object = (SoapObject) envelope.bodyIn;
                // 获取返回的结果
                String s = object.toString();
                result = s;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            String ex = e.toString();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();
        if (s.contains("成功")) {
            Toast.makeText(mContext, "提交成功", Toast.LENGTH_SHORT).show();
            ((AppCompatActivity) mContext).finish();
        } else {
            Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSubKeyStr(String cont) {
        if (null == cont||"".equals(cont)) {
            cont = "0";
        }
        return cont;
    }
}
