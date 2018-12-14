package com.example.win7.huibao.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.adapter.CheckBoxAdapter;
import com.example.win7.huibao.adapter.RecViewShowAdapter;
import com.example.win7.huibao.listener.CallBackListener;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.view.CustomProgress;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CheckActivity extends AppCompatActivity {
    Toolbar  toolbar;
    TextView tv_comp, tv_part, tv_creator,
            tv_item, tv_num, tv_pri, tv_taxpri, tv_start, tv_end, tv_pro,
            tv_plans, tv_budget, tv_pbudget, tv_notes, tv_buhan, tv_sup,
            tv_supl, tv_send, tv_pf, tv_submit, tv_refuse;
    EditText                      et_get;
    List<HashMap<String, String>> list1;
    List<HashMap<String, Object>> list2;
    HashMap<String, String>       map;
    String        pfid = "230";
    DecimalFormat df   = new DecimalFormat("#0.00");
    String goodsid, userid;
    CustomProgress progress;
    private RecyclerView       recview_show;
    private List<String>       mBitmapList;
    private RecViewShowAdapter showAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.check);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_chaosong:
                        new HYTask().execute();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu3, menu);
        return true;
    }

    protected void setViews() {
        tv_comp = (TextView) findViewById(R.id.tv_comp);
        tv_part = (TextView) findViewById(R.id.tv_part);
        tv_creator = (TextView) findViewById(R.id.tv_creator);
        tv_item = (TextView) findViewById(R.id.tv_item);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_pri = (TextView) findViewById(R.id.tv_pri);
        tv_taxpri = (TextView) findViewById(R.id.tv_taxpri);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_end = (TextView) findViewById(R.id.tv_end);
        tv_pro = (TextView) findViewById(R.id.tv_pro);
        tv_pro.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_plans = (TextView) findViewById(R.id.tv_plans);
        tv_budget = (TextView) findViewById(R.id.tv_budget_check);
        tv_pbudget = (TextView) findViewById(R.id.tv_pbudget_check);
        tv_notes = (TextView) findViewById(R.id.tv_notes);
        tv_buhan = (TextView) findViewById(R.id.tv_buhan_check);
        tv_sup = (TextView) findViewById(R.id.tv_sup);
        tv_supl = (TextView) findViewById(R.id.tv_supl);
        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_pf = (TextView) findViewById(R.id.tv_pf);
        recview_show = (RecyclerView) findViewById(R.id.recview_show);
        mBitmapList = new ArrayList<>();
        GridLayoutManager mLayoutManager = new GridLayoutManager(CheckActivity.this, 3, GridLayoutManager.VERTICAL, false);
        showAdapter = new RecViewShowAdapter(CheckActivity.this, mBitmapList);
        recview_show.setLayoutManager(mLayoutManager);
        recview_show.setAdapter(showAdapter);
        tv_pf.setText("10分");
        tv_submit = (TextView) findViewById(R.id.tv_submit_check);
        tv_refuse = (TextView) findViewById(R.id.tv_refuse_check);
        et_get = (EditText) findViewById(R.id.et_get);
        map = new HashMap<>();
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        Intent intent = getIntent();
        goodsid = intent.getStringExtra("goodsId");
        new DetailTask(goodsid).execute();
        new UseridTask().execute();
    }

    protected void setListeners() {
        tv_pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PFTask(tv_pf).execute();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                ToastUtils.showToast(CheckActivity.this,"请抄送后，转下一个人审核");
                new CheckTask(et_get.getText().toString(), pfid, userid, goodsid).execute();
            }
        });
        tv_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_get.getText().toString().equals("")) {
                    Toast.makeText(CheckActivity.this, "请填写回馈消息", Toast.LENGTH_SHORT).show();
                    return;
                }
                new CheckTask(et_get.getText().toString(), pfid, "0", goodsid).execute();
            }
        });
    }

    private void sendMessegeToShenhe(String msg, String username) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(msg, username);
        emMessage.setStatus(EMMessage.Status.INPROGRESS);
        emMessage.setMessageStatusCallback(new CallBackListener() {
            @Override
            public void onMainSuccess() {
                ToastUtils.showToast(getBaseContext(), "发送成功");
            }

            @Override
            public void onMainError(int i, String s) {
                ToastUtils.showToast(getBaseContext(), "发送失败");
            }
        });
        EMClient.getInstance().chatManager().sendMessage(emMessage);
    }

    class CheckTask extends AsyncTask<Void, String, String> {
        String ftext1;
        String pfid;
        String userid;
        String id;

        CheckTask(String ftext1, String pfid, String userid, String id) {
            this.ftext1 = ftext1;
            this.pfid = pfid;
            this.userid = userid;
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = CustomProgress.show(CheckActivity.this, "提交中...", true, null);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "UPDATE_t_BOS200000000";
                // EndPoint
                String endPoint = Consts.ENDPOINT;
                // SOAP Action
                String soapAction = "http://tempuri.org/UPDATE_t_BOS200000000";

                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                // 设置需调用WebService接口需要传入的参数
                Log.i("传入的参数", "回馈消息" + ftext1 + "评分id" + pfid + "审核人id" + userid + "单据id" + id);
                rpc.addProperty("FText1", ftext1);
                rpc.addProperty("FBase14", pfid);
                rpc.addProperty("FCheckerID", userid);
                rpc.addProperty("ID", id);

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
                    Log.i("CheckActivity", e.toString() + "==================================");
                }

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
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("成功")) {
                progress.dismiss();
                Toast.makeText(CheckActivity.this, "确认成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CheckActivity.this, "确认异常", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UseridTask extends AsyncTask<Void, String, String> {

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
            String sql = "select a.fitemid from t_Emp a left join t_user b on a.fitemid=b.fempid where b.fname='" + YApplication.fname + "'";
            rpc.addProperty("FSql", sql);
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
                Log.i("CheckActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
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
                    userid = recordEle.elementTextTrim("fitemid");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    class DetailTask extends AsyncTask<Void, String, String> {
        String id;

        public DetailTask(String id) {
            this.id = id;
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
            String sql = "select t.fname comp,u.fname part,b.fname item,a.fdecimal shuliang,a.fdecimal1 danjia,a.famount2 hanshui,a.ftime qi,a.ftime1 zhi,c.fname progress," +
                    "c.f_111 plans,d.fname budget,c.f_107 pbudget,a.fnote,a.famount3 buhan,e.fname fuzhu,a.fdecimal2 fuliang,a.ftext fasong,a.ftext1 huikui,f.fname pf,i.fname zhidan" +
                    ",a.fimage1,a.fimage2,a.fimage3,a.fimage4,a.fimage5 from t_BOS200000000Entry2 a left join t_BOS200000000 s on s.fid=a.fid left join t_Item_3001 t on t.fitemid=s.fbase11 left join t_Department u on u.FItemID=s.FBase12 " +
                    " left join t_icitem b on b.fitemid=a.fbase1 " +
                    "left join t_item_3007 c on c.fitemid=a.fbase left join t_item d on d.fitemid=c.f_105 " +
                    "left join t_measureunit e on e.fmeasureunitid=b.fitemid left join t_item f on f.fitemid=a.fbase14 left join t_emp i on i.fitemid=a.fbase15  where a.id='" + id + "'";
            Log.i("SQL查询语句", sql);
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
                Log.i("CheckActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString() + "=========================");
            String result = object.getProperty(0).toString();
            Document doc = null;
            try {
                doc = DocumentHelper.parseText(result); // 将字符串转为XML
                Element rootElt = doc.getRootElement(); // 获取根节点
                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                mBitmapList.clear();
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    map.put("comp", recordEle.elementTextTrim("comp"));
                    map.put("part", recordEle.elementTextTrim("part"));
                    map.put("item", recordEle.elementTextTrim("item"));
                    map.put("shuliang", recordEle.elementTextTrim("shuliang"));
                    map.put("danjia", recordEle.elementTextTrim("danjia"));
                    map.put("hanshui", recordEle.elementTextTrim("hanshui"));
                    map.put("qi", recordEle.elementTextTrim("qi"));
                    map.put("zhi", recordEle.elementTextTrim("zhi"));
                    map.put("progress", recordEle.elementTextTrim("progress"));
                    map.put("plan", recordEle.elementTextTrim("plans"));
                    map.put("budget", recordEle.elementTextTrim("budget"));
                    map.put("pbudget", recordEle.elementTextTrim("pbudget"));
                    map.put("note", recordEle.elementTextTrim("fnote"));
                    map.put("buhan", recordEle.elementTextTrim("buhan"));
                    map.put("fuzhu", recordEle.elementTextTrim("fuzhu"));
                    map.put("fuliang", recordEle.elementTextTrim("fuliang"));
                    map.put("fasong", recordEle.elementTextTrim("fasong"));
                    map.put("huikui", recordEle.elementTextTrim("huikui"));
                    for (int i = 0; i < 5; i++) {
                        String url = recordEle.elementTextTrim("fimage" + (i + 1));
                        if (null != url && !"".equals(url)) {
                            mBitmapList.add(url);
                        }
                    }
                    map.put("pf", recordEle.elementTextTrim("pf"));
                    map.put("zhidan", recordEle.elementTextTrim("zhidan"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //刷新图片列表
            showAdapter.notifyDataSetChanged();
            tv_comp.setText(map.get("comp"));
            tv_part.setText(map.get("part"));
            tv_item.setText(map.get("item"));
            tv_num.setText(df.format(Double.parseDouble(null == map.get("shuliang") ? "0" : map.get("shuliang"))));
            tv_pri.setText(df.format(Double.parseDouble(null == map.get("danjia") ? "0" : map.get("danjia"))));
            tv_taxpri.setText(df.format(Double.parseDouble(null == map.get("hanshui") ? "0" : map.get("hanshui"))));
            tv_start.setText(map.get("qi"));
            tv_end.setText(map.get("zhi"));
            tv_pro.setText(map.get("progress"));
            tv_plans.setText(map.get("plan"));
            tv_budget.setText(map.get("budget"));
            tv_pbudget.setText(map.get("pbudget"));
            tv_notes.setText(map.get("note"));
            tv_buhan.setText(df.format(Double.parseDouble(null == map.get("buhan") ? "0" : map.get("buhan"))));
            tv_sup.setText(map.get("fuzhu"));
            tv_supl.setText(df.format(Double.parseDouble(null == map.get("fuliang") ? "0" : map.get("fuliang"))));
            tv_send.setText(map.get("fasong"));
            et_get.setText(map.get("huikui"));
            tv_creator.setText(map.get("zhidan"));
            if (null != map.get("pf") && "".equals(map.get("pf"))) {
                tv_pf.setText(map.get("pf"));
            }
        }
    }

    class PFTask extends AsyncTask<Void, String, String> {
        TextView tv;

        public PFTask(TextView tv) {
            this.tv = tv;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
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
            String sql = "select fitemid,fname from t_Item where FItemClassID=3010";
            rpc.addProperty("FSql", sql);
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
                Log.i("AddTaskActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
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
                    HashMap<String, String> map = new HashMap<>();
                    map.put("itemid", recordEle.elementTextTrim("fitemid"));
                    map.put("fname", recordEle.elementTextTrim("fname"));
                    list1.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            List<String> strList = new ArrayList<>();
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList.add(name);
            }
            final GridView gv = new GridView(CheckActivity.this);
            gv.setNumColumns(3);
            final String[] fen = new String[strList.size()];
            for (int i = 0; i < strList.size(); i++) {
                fen[i] = strList.get(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(CheckActivity.this, android.R.layout.simple_list_item_1,
                    fen);
            gv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(CheckActivity.this).setView(gv).show();
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    pfid = list1.get(i).get("itemid");
                    tv.setText(fen[i]);
                    dialog.dismiss();
                }
            });
        }
    }

    //查询好友列表
    class HYTask extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            list2.clear();
            progress = CustomProgress.show(CheckActivity.this, "加载中...", true, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                String s = "(";
                for (String username : usernames) {
                    s = s + "'" + username + "',";
                }
                s = s.substring(0, s.length() - 1);
                s = s + ")";
                Log.i("拼接的数据集", s + "=================================");

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
                Log.i("昵称查询语句", "select a.fname from t_emp a inner join t_user d on a.fitemid=b.fempid where d.fname in" + s + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                rpc.addProperty("FSql", "select a.fitemid,a.fname,b.FDescription name from t_emp a inner join t_user b on a.fitemid=b.fempid where b.FDescription in" + s);
                //                rpc.addProperty("FSql", "select a.fitemid,a.fname,b.fname name from t_emp a inner join t_user b on a.fitemid=b.fempid where b.fname in" + s);
                rpc.addProperty("FTable", "t_user");

                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

                envelope.bodyOut = rpc;
                // 设置是否调用的是dotNet开发的WebService
                envelope.dotNet = true;
                // 等价于envelope.bodyOut = rpc;
                envelope.setOutputSoapObject(rpc);

                HttpTransportSE transport = new HttpTransportSE(endPoint);
                // 调用WebService
                transport.call(soapAction, envelope);
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;

                // 获取返回的结果
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
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("fname", recordEle.elementTextTrim("fname"));
                        map.put("name", recordEle.elementTextTrim("name"));//for 环信
                        map.put("ischeck", false);
                        map.put("fitemid", recordEle.elementTextTrim("fitemid"));
                        list2.add(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progress.dismiss();
            View v = getLayoutInflater().inflate(R.layout.item_shenhe, null);
            final ListView lv = (ListView) v.findViewById(R.id.lv_checkbox);
            CheckBoxAdapter adapter = new CheckBoxAdapter(CheckActivity.this, list2);
            lv.setAdapter(adapter);
            final TextView tv_submits = (TextView) v.findViewById(R.id.tv_check_submit);
            final AlertDialog dialog = new AlertDialog.Builder(CheckActivity.this).setView(v)
                    .setTitle("请选择").show();
            tv_submits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < list2.size(); i++) {
                        if (Boolean.valueOf(list2.get(i).get("ischeck").toString())) {
                            new ChaoSongTask(list2.get(i).get("fitemid").toString()).execute();
                            sendMessegeToShenhe(goodsid, list2.get(i).get("name").toString());
                        }
                    }
                    dialog.dismiss();
                }
            });
            super.onPostExecute(s);
        }
    }

    class ChaoSongTask extends AsyncTask<Void, String, String> {
        String userid;

        ChaoSongTask(String userid) {
            this.userid = userid;
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
            String methodName = "UPDATE_t_BOS200000000_CheckID";
            // EndPoint
            String endPoint = Consts.ENDPOINT;
            // SOAP Action
            String soapAction = "http://tempuri.org/UPDATE_t_BOS200000000_CheckID";

            // 指定WebService的命名空间和调用的方法名
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的参数
            rpc.addProperty("FUserID", userid);
            rpc.addProperty("ID", goodsid);

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
                Log.i("CheckActivity", e.toString() + "==================================");
            }

            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;

            // 获取返回的结果
            Log.i("返回结果", object.getProperty(0).toString() + "=========================");
            String result = object.getProperty(0).toString();
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("成功")) {

            } else {

            }
        }
    }
}
