package com.example.win7.huibao;

import android.util.Log;
import com.example.win7.huibao.util.Consts;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.Iterator;

public class Test {
    public static void main(String[] args) {
//        String name = "z001";
//        String sql = "select a.id,b.fname item,a.fdecimal shuliang,a.fdecimal1 danjia,a.famount2 hanshui from t_BOS200000000Entry2 a left join t_icitem b on b.fitemid=a.fbase1 where (a.FBase5=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox1=0) or " +
//                "(a.FBase6=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox2=0) or (a.FBase7=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox3=0) " +
//                "or (a.FBase8=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox4=0) or (a.FBase9=(select FEmpID from t_User where FName='"+name+"') and a.FCheckBox5=0)";
//        String id = "171ceb56e6824bc092e5496c4cf60c08";
//        String sql = "select b.fname item,a.fdecimal shuliang,a.fdecimal1 danjia,a.famount2 hanshui,a.ftime qi,a.ftime1 zhi,c.fname progress," +
//                "c.f_111 plans,d.fname budget,c.f_107 pbudget,a.fnote,a.famount3 buhan,e.fname fuzhu,a.fdecimal2 fuliang,a.ftext fasong,a.ftext1 huikui,f.fname pf" +
//                " from t_BOS200000000Entry2 a left join t_icitem b on b.fitemid=a.fbase1 " +
//                "left join t_item_3007 c on c.fitemid=a.fbase left join t_item d on d.fitemid=c.f_105 " +
//                "left join t_measureunit e on e.fmeasureunitid=b.fitemid left join t_item f on f.fitemid=a.fbase14 where a.id='"+id+"'";
//        NeedCheck(sql);
        Exit("z019");
//        UserGroup();
    }

    protected static void Exit(String fname){
        // 命名空间
        String nameSpace = "http://tempuri.org/";
        // 调用的方法名称
        String methodName = "Z_Exit";
        // EndPoint
        String endPoint = Consts.ENDPOINT;
        // SOAP Action
        String soapAction = "http://tempuri.org/Z_Exit";

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("UserName", fname);

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
            Log.i("LoginActivity", e.toString() + "==================================");
        }
    }

    protected static void NeedCheck(String sql){
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
        rpc.addProperty("FSql", sql);
        rpc.addProperty("FTable", "t_BOS200000000Entry2");

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
            Log.i("NeedCheckActivity", e.toString() + "==================================");
        }

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;

        // 获取返回的结果
        System.out.println("返回结果"+object.getProperty(0).toString() + "=========================");
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

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void UserGroup(){
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
        String sql = "select a.*,b.fname groupname,c.FName realname,c.fitemid,d.FName username,e.FName departname from t_Group a inner join t_User b on a.FGroupID=b.FUserID " +
                "left join t_User d on d.FUserID=a.FUserID left join t_Emp c on d.FEmpID=c.fitemid " +
                "left join t_Department e on e.FItemID=c.FDepartmentID where FGroupID>0";
        String sql1 = "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%集团%'";
        String sql2 = "select distinct a.fid,a.fbillno from t_BOS200000000 a left join t_BOS200000000Entry2 b on a.fid=b.fid where b.fbase15 in (" +
                "select c.fitemid from t_group a inner join t_user b on a.fgroupid=b.fuserid left join t_user d on d.fuserid = a.fuserid left join t_emp c on d.fempid=c.fitemid where a.fgroupid>0 and b.fname like '%仓储%') order by a.fid desc";
        String sql3 = "select a.fitemid from t_Emp a left join t_user b on a.fitemid=b.fempid where b.fname='z001'";
        String sql4 = "select t.fname comp,u.fname part,b.fname item,a.fdecimal shuliang,a.fdecimal1 danjia,a.famount2 hanshui,a.ftime qi,a.ftime1 zhi,c.fname progress," +
                "c.f_111 plans,d.fname budget,c.f_107 pbudget,a.fnote,a.famount3 buhan,e.fname fuzhu,a.fdecimal2 fuliang,a.ftext fasong,a.ftext1 huikui,f.fname pf,i.fname zhidan" +
                " from t_BOS200000000Entry2 a left join t_BOS200000000 s on s.fid=a.fid left join t_Item_3001 t on t.fitemid=s.fbase11 left join t_Department u on u.FItemID=s.FBase12 " +
                " left join t_icitem b on b.fitemid=a.fbase1 " +
                "left join t_item_3007 c on c.fitemid=a.fbase left join t_item d on d.fitemid=c.f_105 " +
                "left join t_measureunit e on e.fmeasureunitid=b.fitemid left join t_item f on f.fitemid=a.fbase14 left join t_emp i on i.fitemid=a.fbase15 where a.id='b68efec3789b4036a46820cacf0ca818'";
        rpc.addProperty("FSql", sql4);
        rpc.addProperty("FTable", "t_BOS200000000Entry2");

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
            Log.i("NeedCheckActivity", e.toString() + "==================================");
        }

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;

        // 获取返回的结果
        System.out.println("返回结果"+object.getProperty(0).toString() + "=========================");
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

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
