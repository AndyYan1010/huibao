package com.example.win7.huibao.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.win7.huibao.entity.TaskEntry;
import com.example.win7.huibao.entity.Tasks;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 新增或编辑任务单
 */
public class SubmitTask extends AsyncTask<Void, String, String> {
    Tasks   tasks;
    Context mContext;

    public SubmitTask(Tasks tasks, Context context) {
        this.tasks = tasks;
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
            String methodName = "t_BOS200000000";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/t_BOS200000000";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的参数
            Log.i("submitTask", tasks.getFinterid());
            Log.i("submitTask", tasks.getFbillno());
            rpc.addProperty("InterID", Integer.parseInt(tasks.getFinterid()));
            rpc.addProperty("BillNO", tasks.getFbillno());

            //表头
            Document document = DocumentHelper.createDocument();
            Element rootElement = document.addElement("NewDataSet");
            Element cust = rootElement.addElement("Cust");
            //币别
            cust.addElement("FBase3").setText(tasks.getFBase3());
            //汇率
            cust.addElement("FAmount4").setText(String.valueOf(tasks.getFAmount4()));
            //组织机构
            cust.addElement("FBase11").setText(tasks.getFBase11());
            //区域部门
            cust.addElement("FBase12").setText(tasks.getFBase12());
            //责任部门
            cust.addElement("FBase16").setText(tasks.getFBase16());
            //制度所属部门
            cust.addElement("FBase13").setText("0");
            //制度操作细则
            cust.addElement("FNOTE1").setText("");

            //表体
            Document document2 = DocumentHelper.createDocument();
            Element rootElement2 = document2.addElement("NewDataSet");
            for (TaskEntry taskEntry : tasks.getEntryList()) {
                Element cust2 = rootElement2.addElement("Cust");
                //启日期
                cust2.addElement("FTime").setText(String.valueOf(taskEntry.getFTime()));
                //止日期
                cust2.addElement("FTime1").setText(String.valueOf(taskEntry.getFTime1()));
                //责任人
                cust2.addElement("FBase4").setText(taskEntry.getFBase4());
                //制单人
                cust2.addElement("FBase15").setText(taskEntry.getFBase15());
                //计划预算进度
                cust2.addElement("FBase").setText(taskEntry.getFBase());
                //备注
                cust2.addElement("FNOTE").setText(taskEntry.getFNOTE());
                //往来
                cust2.addElement("FBase10").setText(taskEntry.getFBase10());
                //内容
                cust2.addElement("FBase1").setText(taskEntry.getFBase1());
                //计量
                cust2.addElement("FBase2").setText(taskEntry.getFBase2());
                //数量
                cust2.addElement("FDecimal").setText(String.valueOf(taskEntry.getFDecimal()));
                //单价
                cust2.addElement("FDecimal1").setText(String.valueOf(taskEntry.getFDecimal1()));
                //金额含税
                cust2.addElement("FAmount2").setText(String.valueOf(taskEntry.getFAmount2()));
                //人民币不含税额
                cust2.addElement("FAmount3").setText(String.valueOf(taskEntry.getFAmount3()));
                //辅量
                cust2.addElement("FDecimal2").setText(String.valueOf(taskEntry.getFDecimal2()));
                //发送消息
                cust2.addElement("FText").setText(taskEntry.getFText());
                //回馈消息
                cust2.addElement("FText1").setText(taskEntry.getFText1());
                //评分
                cust2.addElement("FBase14").setText(taskEntry.getFBase14());
                //消息+接受1
                cust2.addElement("FBase5").setText(taskEntry.getFBase5());
                //消息+确认1
                cust2.addElement("FCheckBox1").setText(String.valueOf(taskEntry.getFCheckBox1()));
                //消息+接受2
                cust2.addElement("FBase6").setText(taskEntry.getFBase6());
                //消息+确认2
                cust2.addElement("FCheckBox2").setText(String.valueOf(taskEntry.getFCheckBox2()));
                //消息+接受3
                cust2.addElement("FBase7").setText(taskEntry.getFBase7());
                //消息+确认3
                cust2.addElement("FCheckBox3").setText(String.valueOf(taskEntry.getFCheckBox3()));
                //消息+接受4
                cust2.addElement("FBase8").setText(taskEntry.getFBase8());
                //消息+确认4
                cust2.addElement("FCheckBox4").setText(String.valueOf(taskEntry.getFCheckBox4()));
                //消息+接受5
                cust2.addElement("FBase9").setText(taskEntry.getFBase9());
                //消息+确认5
                cust2.addElement("FCheckBox5").setText(String.valueOf(taskEntry.getFCheckBox5()));
                //id
                cust2.addElement("ID").setText(taskEntry.getId());
                //pic url
                String bitUrl = taskEntry.getBitUrl();
                String[] split = bitUrl.split(",");
                List urlList = new ArrayList();
                for (int i = 0; i < split.length; i++) {
                    String s = split[i];
                    if (!"".equals(s)) {
                        urlList.add(s);
                    }
                }
                urlList.size();
                int num = 5 - urlList.size();
                for (int n = 0; n < num; n++) {
                    urlList.add("");
                }
                urlList.size();
                for (int n = 0; n < 5; n++) {
                    cust2.addElement("fimage" + (n + 1)).setText((String) urlList.get(n));
                }
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
            Log.i("sss", fbtouxml + "***********");

            Log.i("sss", fbtixml + "***********");
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
                Log.i("新增或编辑", e.toString() + "==================================");
            }

            // 获取返回的数据
            String result = "";
            if (envelope.bodyIn instanceof SoapFault) {
                Log.i("服务器返回", (SoapObject) envelope.getResponse() + "");
            } else {
                SoapObject object = (SoapObject) envelope.bodyIn;

                // 获取返回的结果
                Log.i("返回结果", object.getProperty(0).toString() + "=========================");
                result = object.getProperty(0).toString();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();
        if (s.equals("成功")) {
            Toast.makeText(mContext, "提交成功", Toast.LENGTH_SHORT).show();
            ((AppCompatActivity) mContext).finish();
        } else {
            Toast.makeText(mContext, "提交失败", Toast.LENGTH_SHORT).show();
        }
    }
}
