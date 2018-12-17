package com.example.win7.huibao.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.activity.DakaActivity;
import com.example.win7.huibao.activity.DetailActivity;
import com.example.win7.huibao.activity.NeedCheckActivity;
import com.example.win7.huibao.activity.SettingsActivity;
import com.example.win7.huibao.activity.TongjiActivity;
import com.example.win7.huibao.adapter.GridViewAdapter;
import com.example.win7.huibao.entity.MainMenuEntity;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ToastUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeFragment extends Fragment {
    Context  mContext;
    View     view;
    Toolbar  toolbar;
    TextView tv_user, tv_depart, tv_company, tv_detail, tv_lately, tv_zhidu;
    LinearLayout ll_user, ll_depart, ll_company, ll_detail, ll_lately, ll_zhidu;
    Intent intent;
    String username, depart, company, detail, lately, zhidu = "";
    GridView        gv_me;
    GridViewAdapter adapter;
    private int[]                resArr  = new int[]{R.drawable.daka, R.drawable.jiankong, R.drawable.tongji, R.drawable.ic_action_tick, R.drawable.ic_action_barcode_2};
    private String[]             textArr = new String[]{"打卡", "认证", "统计表", "待审核", "收款码"};
    private List<MainMenuEntity> list    = new ArrayList<MainMenuEntity>();
    MainMenuEntity data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_me, container, false);
        for (int i = 0; i < resArr.length; i++) {
            data = new MainMenuEntity();
            data.setResId(resArr[i]);
            data.setText(textArr[i]);
            list.add(data);
        }
        setTool();
        setViews();
        setListeners();
        return view;
    }

    protected void setTool() {
        TextView tv_set = view.findViewById(R.id.tv_set);
        tv_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SettingsActivity.class));
            }
        });
    }

    protected void setViews() {
        gv_me = (GridView) view.findViewById(R.id.gv_me);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        tv_depart = (TextView) view.findViewById(R.id.tv_depart);
        tv_company = (TextView) view.findViewById(R.id.tv_company);
        tv_lately = (TextView) view.findViewById(R.id.tv_lately);
        tv_detail = (TextView) view.findViewById(R.id.tv_detail);
        tv_zhidu = (TextView) view.findViewById(R.id.tv_zhidu);
        ll_user = (LinearLayout) view.findViewById(R.id.ll_user);
        ll_depart = (LinearLayout) view.findViewById(R.id.ll_depart);
        ll_company = (LinearLayout) view.findViewById(R.id.ll_company);
        ll_lately = (LinearLayout) view.findViewById(R.id.ll_lately);
        ll_detail = (LinearLayout) view.findViewById(R.id.ll_companydetail);
        ll_zhidu = (LinearLayout) view.findViewById(R.id.ll_zhidu);
        adapter = new GridViewAdapter(mContext, list);
        gv_me.setAdapter(adapter);
        intent = new Intent(mContext, DetailActivity.class);
//      searchPersonInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchPersonInfo();
    }

    public void searchPersonInfo() {
        new MTask().execute();
    }

    protected void setListeners() {
        ll_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                intent.putExtra("title","用户");
                //                intent.putExtra("content",username);
                //                startActivity(intent);
                TextView tv = new TextView(mContext);
                tv.setText(username);
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setTitle("用户").setView(tv).show();
            }
        });
        ll_depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                intent.putExtra("title","部门");
                //                intent.putExtra("content",depart);
                //                startActivity(intent);
                TextView tv = new TextView(mContext);
                tv.setText(depart);
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setTitle("部门").setView(tv).show();
            }
        });
        ll_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                intent.putExtra("title","公司");
                //                intent.putExtra("content",company);
                //                startActivity(intent);
                TextView tv = new TextView(mContext);
                tv.setText(company);
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setTitle("公司").setView(tv).show();
            }
        });
        ll_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                intent.putExtra("title","公司简介");
                //                intent.putExtra("content",detail);
                //                startActivity(intent);
                TextView tv = new TextView(mContext);
                tv.setText(detail);
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setTitle("公司简介").setView(tv).show();
            }
        });
        ll_lately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                intent.putExtra("title","最近发布");
                //                intent.putExtra("content",lately);
                //                startActivity(intent);
                TextView tv = new TextView(mContext);
                tv.setText(lately);
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setTitle("最近发布").setView(tv).show();
            }
        });
        ll_zhidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                intent.putExtra("title","制度");
                //                intent.putExtra("content",zhidu);
                //                startActivity(intent);
                TextView tv = new TextView(mContext);
                tv.setText(zhidu);
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setTitle("制度").setView(tv).show();
            }
        });
        gv_me.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        //判断是否为android6.0系统版本，如果是，需要动态添加权限
                        if (Build.VERSION.SDK_INT >= 23) {
                            //动态申请定位权限
                            showContacts();
                        } else {
                            startActivity(new Intent(mContext, DakaActivity.class));
                        }
                        break;
                    case 1:
                        String fgroup = YApplication.fgroup;
                        //                        if (null==fgroup){
                        //                            ToastUtils.showToast(getContext(),"获取失败，请退出重新登陆账号。");
                        //                            return;
                        //                        }else if (!fgroup.contains("集团")){
                        //                            ToastUtils.showToast(getContext(),"暂无查看监控权限，请您先申请权限。");
                        //                            return;
                        //                        }
                        ToastUtils.showToast(getContext(), "正在开发...");
                        //                        startActivity(new Intent(mContext, JiankongActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(mContext, TongjiActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(mContext, NeedCheckActivity.class));
                        break;
                    case 4:
                        ToastUtils.showToast(getContext(), "正在开发...");
                        //                        ImageView iv = new ImageView(mContext);
                        //                        iv.setImageResource(R.drawable.receive_barcode);
                        //                        new AlertDialog.Builder(mContext).setView(iv).show();
                        break;
                }
            }
        });
    }

    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BAIDU_READ_PHONE_STATE);
        } else {
            startActivity(new Intent(mContext, DakaActivity.class));
        }
    }

    private static final int BAIDU_READ_PHONE_STATE = 100;

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                startActivity(new Intent(mContext, DakaActivity.class));
                break;
            default:
                break;
        }
    }

    class MTask extends AsyncTask<Void, String, String> {
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
            rpc.addProperty("FSql", "select a.fname username,b.fname depart,c.FName company,c.F_101 detail,c.f_102 lately,e.f_102 zhidu from t_User d inner join  t_Emp a on d.FEmpID=a.fitemid left join t_Department b on a.FDepartmentID=b.FItemID left join t_Item_3001 c on c.FItemID=a.f_102 left join t_Item_3006 e on e.F_101=b.FItemID where FDescription='" + YApplication.fname + "'");
//          rpc.addProperty("FSql", "select a.fname username,b.fname depart,c.FName company,c.F_101 detail,c.f_102 lately,e.f_102 zhidu from t_User d inner join  t_Emp a on d.FEmpID=a.fitemid left join t_Department b on a.FDepartmentID=b.FItemID left join t_Item_3001 c on c.FItemID=b.f_102 left join t_Item_3006 e on e.F_101=b.FItemID where FDescription='" + YApplication.fname + "'");
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
                Log.i("MeFragment", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            if (null != object) {
                Log.i("返回结果", object.getProperty(0).toString() + "=========================");
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
                        username = recordEle.elementTextTrim("username"); // 拿到head节点下的子节点title值
                        YApplication.username = username; // 拿到head节点下的子节点title值
                        depart = recordEle.elementTextTrim("depart");
                        company = recordEle.elementTextTrim("company");
                        detail = recordEle.elementTextTrim("detail");
                        lately = recordEle.elementTextTrim("lately");
                        zhidu = recordEle.elementTextTrim("zhidu");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return username + ";" + depart + ";" + company + ";" + detail + ";" + lately + ";" + zhidu + ";QAQ";
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("")) {
                String[] str = s.split(";");
                tv_user.setText(str[0]);
                tv_depart.setText(str[1]);
                tv_company.setText(str[2]);
                tv_detail.setText(str[3]);
                tv_lately.setText(str[4]);
                tv_zhidu.setText(str[5]);
            }
        }
    }
}
