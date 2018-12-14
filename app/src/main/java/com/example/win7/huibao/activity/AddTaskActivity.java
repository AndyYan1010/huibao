package com.example.win7.huibao.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.adapter.MyRecAdapter;
import com.example.win7.huibao.adapter.ZiAdapter;
import com.example.win7.huibao.entity.TaskEntry;
import com.example.win7.huibao.entity.Tasks;
import com.example.win7.huibao.listener.CallBackListener;
import com.example.win7.huibao.task.SubmitTask;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.PinyinComparator;
import com.example.win7.huibao.util.ProgressDialogUtil;
import com.example.win7.huibao.util.ToastUtils;
import com.example.win7.huibao.util.Utils;
import com.example.win7.huibao.view.CustomDatePicker;
import com.example.win7.huibao.view.CustomProgress;
import com.example.win7.huibao.view.MyListView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends BaseActivity {
    Toolbar  toolbar;
    TextView tv_huilv, tv_zuzhi, tv_quyu, tv_content, tv_wy, tv_respon, tv_zhidan, tv_contacts, tv_bibie, tv_jl, tv_total, tv_amounts, tv_zeren;
    MyListView                    lv_zb;
    List<HashMap<String, String>> list, list1, ziList;
    List<String> strList, strList1, strList2, lists;
    DecimalFormat df  = new DecimalFormat("#0.00");
    DecimalFormat df1 = new DecimalFormat("#0.0000");
    String interid, taskno, respon, zhidan, contacts,
            content, contentid, wangyinname, wyid, planid, sup, jiliang,
            jiliangid, pfid, zuzhi, quyu, zeren,
            zhidu1, zhidu2, username, depart, company,
            aid, a, aa, bid, b, bb, cid, c, cc, did, d, dd, eid, e, ee, qr1, qr2, qr3, qr4, qr5;
    int    currencyid = 1;
    String currency   = "人民币";
    Double huilv      = 1.00;
    ZiAdapter      adapter;
    Button         btn_submit;
    Tasks          tasks;
    CustomProgress progress;
    private String TAG = "AddTaskActivity";
    Double taxrate, seccoefficient;
    Double total  = 0d;
    Double amount = 0d;
    private GridLayoutManager mLayoutManager;
    private List              mBitmapList;//给recyclerview添加的bitmap集合
    private MyRecAdapter      mMyAdapter;
    private List<List>   mSumBitmapList = new ArrayList();//记录总的bitmaplist的集合
    private List<String> mSumBtUrlList  = new ArrayList();//记录总的图片在服务器地址的集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        setTool();
        setViews();
        setListeners();
    }

    protected void setTool() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle(R.string.addtask);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddTaskActivity.this)
                        .setTitle("确认退出？").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        HashMap<String, String> map = new HashMap<>();
                        try {
                            showInfoDialog(map, -1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void showInfoDialog(final HashMap<String, String> map, final int n) throws ParseException {//n>=0是编辑，n<0是新增
        if (tv_content.getText().toString().equals("")) {
            Toast.makeText(AddTaskActivity.this, "请先选择内容", Toast.LENGTH_SHORT).show();
        } else {
            final View v = getLayoutInflater().inflate(R.layout.item_zi_add, null);
            final RecyclerView recview_add = v.findViewById(R.id.recview_add);
            //添加初始展示的图片
            Bitmap mBm = BitmapFactory.decodeResource(getResources(), R.drawable.add_picture);
            mBitmapList = new ArrayList<>();
            mBitmapList.add(mBm);
            mLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
            if (!map.isEmpty()) {
                planid = map.get("planid");
                pfid = map.get("pfid");
                List list = mSumBitmapList.get(n);
                mBitmapList.addAll(list);
                mMyAdapter = new MyRecAdapter(this, mBitmapList, isChecked, mBitmapList.size());
            } else {
                mLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
                mMyAdapter = new MyRecAdapter(this, mBitmapList, isChecked, mBitmapList.size());
            }
            // 设置布局管理器
            recview_add.setLayoutManager(mLayoutManager);
            // 设置adapter
            recview_add.setAdapter(mMyAdapter);

            final TextView tv_fuzhu = (TextView) v.findViewById(R.id.tv_fuzhu_add);
            tv_fuzhu.setText(sup);
            final EditText et_shuliang = (EditText) v.findViewById(R.id.et_shuliang);
            final EditText et_danjia = (EditText) v.findViewById(R.id.et_danjia);
            final EditText et_note = (EditText) v.findViewById(R.id.et_note);
            final EditText et_hanshui = (EditText) v.findViewById(R.id.et_hanshui);
            final EditText et_buhan = (EditText) v.findViewById(R.id.et_buhan);
            et_buhan.setEnabled(false);
            final EditText et_fuliang = (EditText) v.findViewById(R.id.et_fuliang);
            et_fuliang.setEnabled(false);
            final EditText et_fasong = (EditText) v.findViewById(R.id.et_fasong);
            final TextWatcher shuliang = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().equals("-")) {
                        if (!TextUtils.isEmpty(editable.toString())) {
                            if (seccoefficient != 0.0000000000) {
                                Double fuliang = Double.parseDouble(editable.toString()) / seccoefficient;
                                et_shuliang.removeTextChangedListener(this);
                                et_fuliang.setText(df1.format(fuliang));
                                et_shuliang.addTextChangedListener(this);
                            }
                            if (!TextUtils.isEmpty(et_danjia.getText().toString())) {
                                Double hanshui = Double.parseDouble(editable.toString()) * Double.parseDouble(et_danjia.getText().toString());
                                Double buhan = hanshui / (taxrate + 1) * huilv;
                                et_shuliang.removeTextChangedListener(this);
                                et_hanshui.setText(df.format(hanshui));
                                et_buhan.setText(df.format(buhan));
                                et_shuliang.addTextChangedListener(this);
                            }
                        } else {
                            et_shuliang.removeTextChangedListener(this);
                            et_hanshui.setText("");
                            et_fuliang.setText("");
                            et_buhan.setText("");
                            et_shuliang.addTextChangedListener(this);
                        }
                    }
                }
            };
            final TextWatcher hanshui = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(et_danjia.getText().toString()) && !TextUtils.isEmpty(editable.toString())) {
                        Double shuliang = Double.parseDouble(et_hanshui.getText().toString()) / Double.parseDouble(et_danjia.getText().toString());
                        Double fuliang = shuliang / seccoefficient;
                        Double buhan = Double.parseDouble(editable.toString()) / (taxrate + 1) * huilv;
                        et_hanshui.removeTextChangedListener(this);
                        et_shuliang.setText(df1.format(shuliang));
                        if (seccoefficient != 0.0000000000) {
                            et_fuliang.setText(df1.format(fuliang));
                        }
                        et_buhan.setText(df.format(buhan));
                        et_hanshui.addTextChangedListener(this);
                    } else {
                        et_hanshui.removeTextChangedListener(this);
                        et_shuliang.setText("");
                        et_fuliang.setText("");
                        et_buhan.setText("");
                        et_hanshui.addTextChangedListener(this);
                    }
                }
            };
            et_shuliang.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        Log.i(TAG, "数量框获得了焦点");
                        et_shuliang.addTextChangedListener(shuliang);
                        et_hanshui.removeTextChangedListener(hanshui);
                    }
                }
            });

            et_danjia.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(et_shuliang.getText().toString()) && !TextUtils.isEmpty(editable.toString())) {
                        Double hanshui = Double.parseDouble(et_shuliang.getText().toString()) * Double.parseDouble(editable.toString());
                        Double buhan = hanshui / (taxrate + 1) * huilv;
                        et_danjia.removeTextChangedListener(this);
                        et_hanshui.setText(df.format(hanshui));
                        et_buhan.setText(df.format(buhan));
                        et_danjia.addTextChangedListener(this);
                    } else {
                        et_shuliang.removeTextChangedListener(this);
                        et_hanshui.setText("");
                        et_buhan.setText("");
                        et_shuliang.addTextChangedListener(this);
                    }
                }
            });
            et_hanshui.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (b) {
                        Log.i(TAG, "含税金额框获得了焦点");
                        et_hanshui.addTextChangedListener(hanshui);
                        et_shuliang.removeTextChangedListener(shuliang);
                    }
                }
            });

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            final String now = sdf.format(new Date());
            final TextView tv_qi = (TextView) v.findViewById(R.id.tv_qi_add);
            tv_qi.setText(now);
            tv_qi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDatePicker dpk = new CustomDatePicker(v.getContext(), new CustomDatePicker.ResultHandler() {
                        @Override
                        public void handle(String time) { // 回调接口，获得选中的时间
                            tv_qi.setText(time);
                        }
                    }, "2010-01-01 00:00", "2090-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                    dpk.showSpecificTime(true); // 显示时和分
                    dpk.setIsLoop(true); // 允许循环滚动
                    dpk.show(tv_qi.getText().toString());
                }
            });
            final TextView tv_zhi = (TextView) v.findViewById(R.id.tv_zhi_add);
            tv_zhi.setText(now);
            tv_zhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomDatePicker dpk = new CustomDatePicker(v.getContext(), new CustomDatePicker.ResultHandler() {
                        @Override
                        public void handle(String time) { // 回调接口，获得选中的时间
                            tv_zhi.setText(time);
                        }
                    }, "2010-01-01 00:00", "2090-12-31 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
                    dpk.showSpecificTime(true); // 显示时和分
                    dpk.setIsLoop(true); // 允许循环滚动
                    dpk.show(tv_zhi.getText().toString());
                }
            });
            final TextView tv_progress = (TextView) v.findViewById(R.id.tv_progress_add);
            final TextView tv_plan = (TextView) v.findViewById(R.id.tv_plan_add);
            final TextView tv_budget = (TextView) v.findViewById(R.id.tv_budget_add);
            final TextView tv_pbudget = (TextView) v.findViewById(R.id.tv_pbudget_add);
            tv_progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText et = new EditText(v.getContext());
                    new AlertDialog.Builder(v.getContext()).setTitle("计划预算进度").setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new JHTask(tv_progress, tv_plan, tv_pbudget, tv_budget, et.getText().toString()).execute();
                                }
                            }).setNegativeButton("取消", null).show();
                }
            });
            final TextView tv_one = v.findViewById(R.id.tv_one);
            final TextView tv_two = v.findViewById(R.id.tv_two);
            final TextView tv_three = v.findViewById(R.id.tv_three);
            final TextView tv_four = v.findViewById(R.id.tv_four);
            final TextView tv_five = v.findViewById(R.id.tv_five);
            tv_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new HYTask(tv_one, 1).execute();
                }
            });
            tv_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new HYTask(tv_two, 2).execute();
                }
            });
            tv_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new HYTask(tv_three, 3).execute();
                }
            });
            tv_four.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new HYTask(tv_four, 4).execute();
                }
            });
            tv_five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new HYTask(tv_five, 5).execute();
                }
            });
            final TextView tv_submit = (TextView) v.findViewById(R.id.tv_submit);
            if (!map.isEmpty()) {
                et_shuliang.setText(map.get("shuliang"));
                et_danjia.setText(map.get("danjia"));
                if (map.get("qi").contains("/")) {
                    tv_qi.setText(sdf.format(Date.parse(map.get("qi").toString())));
                    tv_zhi.setText(sdf.format(Date.parse(map.get("zhi").toString())));
                } else {
                    tv_qi.setText(map.get("qi").toString());
                    tv_zhi.setText(map.get("zhi").toString());
                }
                tv_progress.setText(map.get("progress"));
                tv_plan.setText(map.get("plan"));
                tv_budget.setText(map.get("budget"));
                tv_pbudget.setText(map.get("pbudget"));
                et_note.setText(map.get("note"));
                et_hanshui.setText(map.get("hanshui"));
                et_buhan.setText(map.get("buhan"));
                et_fuliang.setText(map.get("fuliang"));
                et_fasong.setText(map.get("fasong"));
                tv_one.setText(map.get("a"));
                tv_two.setText(map.get("b"));
                tv_three.setText(map.get("c"));
                tv_four.setText(map.get("d"));
                tv_five.setText(map.get("e"));
            }
            if (map.get("type") != null) {
                et_shuliang.setEnabled(false);
                et_danjia.setEnabled(false);
                et_hanshui.setEnabled(false);
                et_note.setEnabled(false);
                tv_qi.setEnabled(false);
                tv_zhi.setEnabled(false);
                tv_progress.setEnabled(false);
                et_fasong.setEnabled(false);
                if (map.get("qr1").equals("True")) {
                    tv_one.setEnabled(false);
                }
                if (map.get("qr2").equals("True")) {
                    tv_two.setEnabled(false);
                }
                if (map.get("qr3").equals("True")) {
                    tv_three.setEnabled(false);
                }
                if (map.get("qr4").equals("True")) {
                    tv_four.setEnabled(false);
                }
                if (map.get("qr5").equals("True")) {
                    tv_five.setEnabled(false);
                }
            }
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(v)
                    .show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            tv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!map.isEmpty()) {
                        ziList.remove(map);
                    }
                    if (tv_qi.getText().toString().equals("")) {
                        Toast.makeText(AddTaskActivity.this, "请选择启日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tv_zhi.getText().toString().equals("")) {
                        Toast.makeText(AddTaskActivity.this, "请选择止日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tv_progress.getText().toString().equals("")) {
                        Toast.makeText(AddTaskActivity.this, "请选择计划预算进度", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    map.put("neirong", content);
                    Log.i("计量", jiliangid);
                    map.put("jiliangid", jiliangid);
                    if (et_shuliang.getText().toString().equals("")) {
                        map.put("shuliang", "0");
                    } else {
                        map.put("shuliang", et_shuliang.getText().toString());
                    }
                    if (et_danjia.getText().toString().equals("")) {
                        map.put("danjia", "0");
                    } else {
                        map.put("danjia", et_danjia.getText().toString());
                    }
                    map.put("qi", tv_qi.getText().toString());
                    map.put("zhi", tv_zhi.getText().toString());
                    map.put("progress", tv_progress.getText().toString());
                    map.put("planid", planid);
                    map.put("plan", tv_plan.getText().toString());
                    map.put("budget", tv_budget.getText().toString());
                    map.put("pbudget", tv_pbudget.getText().toString());
                    map.put("note", et_note.getText().toString());
                    if (et_hanshui.getText().toString().equals("")) {
                        map.put("hanshui", "0");
                    } else {
                        map.put("hanshui", et_hanshui.getText().toString());
                    }
                    if (et_buhan.getText().toString().equals("")) {
                        map.put("buhan", "0");
                    } else {
                        map.put("buhan", et_buhan.getText().toString());
                    }
                    map.put("fuzhu", tv_fuzhu.getText().toString());
                    if (et_fuliang.getText().toString().equals("")) {
                        map.put("fuliang", "0");
                    } else {
                        map.put("fuliang", et_fuliang.getText().toString());
                    }
                    map.put("fasong", et_fasong.getText().toString());
                    if (pfid == null) {
                        map.put("pfid", "0");
                    } else {
                        map.put("pfid", pfid);
                    }
                    if (!tv_one.getText().toString().equals("")) {
                        map.put("aid", aid);
                        map.put("a", a);
                        map.put("aa", aa);
                        map.put("qr1", qr1);
                    }
                    if (!tv_two.getText().toString().equals("")) {
                        map.put("bid", bid);
                        map.put("b", b);
                        map.put("bb", bb);
                        map.put("qr2", qr2);
                    }
                    if (!tv_three.getText().toString().equals("")) {
                        map.put("cid", cid);
                        map.put("c", c);
                        map.put("c", cc);
                        map.put("qr3", qr3);
                    }
                    if (!tv_four.getText().toString().equals("")) {
                        map.put("did", did);
                        map.put("d", d);
                        map.put("dd", dd);
                        map.put("qr4", qr4);
                    }
                    if (!tv_five.getText().toString().equals("")) {
                        map.put("eid", eid);
                        map.put("e", e);
                        map.put("ee", ee);
                        map.put("qr5", qr5);
                    }
                    if (map.get("id") == null) {
                        map.put("id", Utils.UUID());
                    }
                    ziList.add(map);
                    total = total + Double.parseDouble(map.get("shuliang"));
                    amount = amount + Double.parseDouble(map.get("hanshui"));
                    tv_total.setText(String.valueOf(total));
                    tv_amounts.setText(String.valueOf(amount));
                    //图片总集合，添加选择的bitmap集合
                    mBitmapList.remove(0);
                    if (n >= 0) {
                        List list = mSumBitmapList.get(n);
                        if (null == list) {
                            list = new ArrayList();
                        } else {
                            list.clear();
                        }
                        list.addAll(mBitmapList);
                    } else {
                        mSumBitmapList.add(mBitmapList);
                    }

                    if (mBitmapList.size() > 0) {
                        //                        MySendProcess = 0;
                        //                        // mBitmapList记录的是图片地址
                        //                        //新建list存放bitmap
                        //                        final List<Bitmap> mBtTemList = new ArrayList<>();
                        //                        ProgressDialogUtil.startShow(AddTaskActivity.this,"正在提交，请稍等");
                        //                        for (int i = 0; i < mBitmapList.size(); i++) {//从总bitmaplist中获取地址，用glide获取bitmap
                        //                            String url = String.valueOf(mBitmapList.get(i));
                        //                                                    Glide.with(AddTaskActivity.this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                        //                                                        @Override
                        //                                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //                                                            mBtTemList.add(resource);
                        //                                                            MySendProcess = MySendProcess + 1;
                        //                                                            if (MySendProcess == mBitmapList.size()) {
                        //                                                                sendPic(mBtTemList, n);//n小于0时是新增，大于等于0时是点击编辑
                        //                                                            }
                        //                                                        }
                        //                                                    });
                        //                        }

                        //新建个list，存放bitmap网络地址
                        mBtUrlList = new ArrayList<>();
                        MySendProcess = 0;
                        htUrlnum = 0;
                        for (Object o : mBitmapList) {
                            mBtUrlList.add("");
                            String s = String.valueOf(o);
                            if (s.contains("http")) {
                                htUrlnum++;
                            }
                        }
                        for (int i = 0; i < mBitmapList.size(); i++) {
                            final String url = String.valueOf(mBitmapList.get(i));
                            if (url.contains("http")) {
                                mBtUrlList.set(i, url);
                            } else {
                                //子线程运行
                                final int finalI = i;
                                Glide.with(AddTaskActivity.this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        new SingleTask2(resource, finalI, mBtUrlList, n).execute();
                                    }
                                });
                                //另一种读取本地bitmap方法
                                //                                ThreadUtils.runOnSubThread(new Runnable() {
                                //                                    @Override
                                //                                    public void run() {
                                //                                        Bitmap bitmap = decodeUriAsBitmap(Uri.parse("file://" + url));
                                //                                        if (null == bitmap) {
                                //                                            ToastUtils.showToast(AddTaskActivity.this, "图片" + finalI + "未读取到");
                                //                                        } else {
                                //                                            new SingleTask2(bitmap, finalI, mBtUrlList, n).execute();
                                //                                        }
                                //                                    }
                                //                                });
                            }
                        }
                    } else {//子表中没有图片
                        if (n >= 0) {
                            mSumBtUrlList.set(n, "");
                        } else {
                            mSumBtUrlList.add("");
                        }
                    }
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private List<String> mBtUrlList;
    private int          MySendProcess;
    private int          htUrlnum;
    private static final int IMAGE     = 1;//调用系统相册-选择图片n小于0
    private static final int SHOT_CODE = 20;//调用系统相机-选择图片n小于0

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //相册返回，获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
        if (requestCode == SHOT_CODE && resultCode == Activity.RESULT_OK) {
            showImage(mRote);
        }
    }

    private String mRote;

    public void setPtRote(String rote) {
        this.mRote = rote;
    }

    //加载图片
    private void showImage(String imgPath) {
        //压缩图片
        //        File file = new File(imgPath);
        //        File newFile = new CompressHelper.Builder(this)
        //                .setMaxWidth(1080)  // 默认最大宽度为720
        //                .setMaxHeight(1920) // 默认最大高度为960
        //                .setQuality(100)    // 默认压缩质量为80
        //                .setFileName("sendPic") // 设置你需要修改的文件名
        //                .setCompressFormat(Bitmap.CompressFormat.PNG) // 设置默认压缩为jpg格式
        //                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
        //                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
        //                .build()
        //                .compressToFile(file);
        //        Bitmap bm = BitmapFactory.decodeFile(newFile.getPath());
        //添加到bitmap集合中
        mBitmapList.add(imgPath);
        mMyAdapter.notifyDataSetChanged();
    }

    private void sendPic(List<Bitmap> bitmapList, int n) {
        Task2 task2 = new Task2(bitmapList, n);
        task2.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(AddTaskActivity.this)
                    .setTitle("确认退出？").setNegativeButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void setViews() {
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        strList = new ArrayList<>();
        strList1 = new ArrayList<>();
        strList2 = new ArrayList<>();
        lists = new ArrayList<>();
        tasks = new Tasks();//任务主表对象
        tv_bibie = (TextView) findViewById(R.id.tv_bibie);//币别
        tv_huilv = (TextView) findViewById(R.id.tv_huilv);//汇率
        tv_zuzhi = (TextView) findViewById(R.id.tv_zuzhi);//组织机构
        tv_quyu = (TextView) findViewById(R.id.tv_quyu);//区域部门
        tv_content = (TextView) findViewById(R.id.tv_content_add);//内容
        tv_jl = (TextView) findViewById(R.id.tv_jl);//计量
        tv_respon = (TextView) findViewById(R.id.tv_respon_add);//责任人
        tv_zhidan = (TextView) findViewById(R.id.tv_zhidan_add);//制单人
        tv_contacts = (TextView) findViewById(R.id.tv_contacts_add);//往来
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_amounts = (TextView) findViewById(R.id.tv_amounts);
        tv_wy = (TextView) findViewById(R.id.tv_wy);
        tv_zeren = findViewById(R.id.tv_zeren);
        if (YApplication.fgroup.contains("仓储")) {
            tv_amounts.setVisibility(View.INVISIBLE);
        }
        lv_zb = findViewById(R.id.lv_zb);//子表
        btn_submit = (Button) findViewById(R.id.btn_submit_add);//提交按钮
        interid = getIntent().getStringExtra("interid");//单据内码
        taskno = getIntent().getStringExtra("taskno");//任务单单号
        ziList = new ArrayList<>();//子表集合
        if (interid.equals("0")) {
            //单据内码为0，表示做新增操作
            adapter = new ZiAdapter(AddTaskActivity.this, ziList, mSumBitmapList, 1);//ziList一开始为空
            lv_zb.setAdapter(adapter);
            //查询默认显示的字段
            new MRTask().execute();
        } else {
            //单据内码不为0，表示做修改操作
            tasks.setFbillno(taskno);
            tasks.setFinterid(interid);
            toolbar.setTitle("编辑任务");
            new DeTask(taskno).execute();
            new DeEntryTask(taskno).execute();
        }
    }

    protected void setListeners() {
        //组织机构选择
        tv_zuzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    //                    Toast.makeText(AddTaskActivity.this,"已确认，无法修改",Toast.LENGTH_SHORT).show();
                    return;
                }
                new DepartsTask().execute();
            }
        });
        //申请部门选择
        tv_quyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    //                    Toast.makeText(AddTaskActivity.this,"已确认，无法修改",Toast.LENGTH_SHORT).show();
                    return;
                }
                new AreaTask(0).execute();
            }
        });
        //责任部门选择
        tv_zeren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    return;
                }
                new AreaTask(1).execute();
            }
        });
        //责任人和制单人选择
        tv_respon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    //                    Toast.makeText(AddTaskActivity.this,"已确认，无法修改",Toast.LENGTH_SHORT).show();
                    return;
                }
                final EditText et = new EditText(AddTaskActivity.this);
                new AlertDialog.Builder(AddTaskActivity.this).setTitle("责任人").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new EmpTask(0, et.getText().toString()).execute();
                            }
                        }).setNegativeButton("取消", null).show();

            }
        });
        //往来选择
        tv_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    //                    Toast.makeText(AddTaskActivity.this,"已确认，无法修改",Toast.LENGTH_SHORT).show();
                    return;
                }
                final EditText et = new EditText(AddTaskActivity.this);
                new AlertDialog.Builder(AddTaskActivity.this).setTitle("往来").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new EmpTask(1, et.getText().toString()).execute();
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        //内容选择
        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    //                    Toast.makeText(AddTaskActivity.this,"已确认，无法修改",Toast.LENGTH_SHORT).show();
                    return;
                }
                final EditText et = new EditText(AddTaskActivity.this);
                new AlertDialog.Builder(AddTaskActivity.this).setTitle("内容").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ItemTask(et.getText().toString()).execute();
                            }
                        }).setNegativeButton("取消", null).show();

            }
        });
        tv_wy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    return;
                }
                final EditText et = new EditText(AddTaskActivity.this);
                new AlertDialog.Builder(AddTaskActivity.this).setTitle("网银").setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new WYTask(et.getText().toString()).execute();
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
        //计量单位选择
        tv_jl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lists.contains("True")) {
                    //                    Toast.makeText(AddTaskActivity.this,"已确认，无法修改",Toast.LENGTH_SHORT).show();
                    return;
                }
                new JLTask().execute();
            }
        });
        //长按选择删除
        lv_zb.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
                new AlertDialog.Builder(AddTaskActivity.this).setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                ziList.remove(index);
                                mSumBtUrlList.remove(index);
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }).show();
                return true;
            }
        });
        //单击编辑子表
        lv_zb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> map = ziList.get(i);
                if (map.get("qr1") != null) {
                    if (map.get("qr1").equals("True") || map.get("qr2").equals("True") ||
                            map.get("qr3").equals("True") || map.get("qr4").equals("True") ||
                            map.get("qr5").equals("True")) {
                        map.put("type", "1");
                    }
                }
                jiliang = map.get("jiliang");
                try {
                    showInfoDialog(map, i);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zuzhi == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择组织机构", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (quyu == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择申请部门", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (zeren == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择责任部门", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (contentid == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (wyid == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择银行", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (respon == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择责任人", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (zhidan == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择制单人", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (contacts == null) {
                    Toast.makeText(AddTaskActivity.this, "请选择往来", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ziList.size() == 0) {
                    Toast.makeText(AddTaskActivity.this, "请添加子表信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (interid.equals("0")) {
                    tasks.setFinterid("0");
                    tasks.setFbillno("a");
                }
                tasks.setFBase3(String.valueOf(currencyid));
                tasks.setFAmount4(Double.parseDouble(tv_huilv.getText().toString()));
                tasks.setFBase11(zuzhi);
                tasks.setFBase12(quyu);
                tasks.setFBase16(zeren);
                tasks.setFBase17(wyid);

                List<TaskEntry> list = new ArrayList<>();
                for (int i = 0; i < ziList.size(); i++) {
                    TaskEntry entry = new TaskEntry();
                    if (interid.equals("0")) {
                        entry.setFTime(ziList.get(i).get("qi") + ":00");
                        entry.setFTime1(ziList.get(i).get("zhi") + ":00");
                    } else {
                        entry.setFTime(ziList.get(i).get("qi"));
                        entry.setFTime1(ziList.get(i).get("zhi"));
                    }
                    entry.setFBase4(respon);
                    entry.setFBase15(zhidan);
                    entry.setFBase10(contacts);
                    entry.setFBase1(contentid);
                    entry.setFBase(ziList.get(i).get("planid"));
                    entry.setFNOTE(ziList.get(i).get("note"));
                    entry.setFBase2(jiliangid);
                    entry.setFDecimal(Double.parseDouble(ziList.get(i).get("shuliang")));
                    entry.setFDecimal1(Double.parseDouble(ziList.get(i).get("danjia")));
                    entry.setFDecimal2(Double.parseDouble(ziList.get(i).get("fuliang")));
                    entry.setFAmount2(Double.parseDouble(ziList.get(i).get("hanshui")));
                    entry.setFAmount3(Double.parseDouble(ziList.get(i).get("buhan")));
                    entry.setFText(ziList.get(i).get("fasong"));
                    entry.setFText1("");
                    entry.setFBase14(Utils.NulltoString(ziList.get(i).get("pfid")));
                    entry.setFBase5(Utils.NulltoString(ziList.get(i).get("aid")));
                    entry.setFBase6(Utils.NulltoString(ziList.get(i).get("bid")));
                    entry.setFBase7(Utils.NulltoString(ziList.get(i).get("cid")));
                    entry.setFBase8(Utils.NulltoString(ziList.get(i).get("did")));
                    entry.setFBase9(Utils.NulltoString(ziList.get(i).get("eid")));
                    entry.setFCheckBox1(Integer.parseInt(Utils.BooleantoNum(ziList.get(i).get("qr1"))));
                    entry.setFCheckBox2(Integer.parseInt(Utils.BooleantoNum(ziList.get(i).get("qr2"))));
                    entry.setFCheckBox3(Integer.parseInt(Utils.BooleantoNum(ziList.get(i).get("qr3"))));
                    entry.setFCheckBox4(Integer.parseInt(Utils.BooleantoNum(ziList.get(i).get("qr4"))));
                    entry.setFCheckBox5(Integer.parseInt(Utils.BooleantoNum(ziList.get(i).get("qr5"))));
                    entry.setId(ziList.get(i).get("id"));
                    String s = mSumBtUrlList.get(i);
                    String bitUrl = "";
                    if (null != s) {
                        bitUrl = s;
                    }
                    entry.setBitUrl(bitUrl);
                    list.add(entry);
                    HashMap<String, String> stringStringHashMap = ziList.get(i);
                    String goodsid = "{goodsId}" + stringStringHashMap.get("id");
                    String aa = stringStringHashMap.get("aa");
                    String bb = stringStringHashMap.get("bb");
                    String cc = stringStringHashMap.get("cc");
                    String dd = stringStringHashMap.get("dd");
                    String ee = stringStringHashMap.get("ee");
                    if (null != aa && !aa.equals("")) {
                        //发送消息
                        sendMessegeToShenhe(goodsid, aa.toLowerCase());
                    }
                    if (null != bb && !bb.equals("")) {
                        //发送消息
                        sendMessegeToShenhe(goodsid, bb.toLowerCase());
                    }
                    if (null != cc && !cc.equals("")) {
                        //发送消息
                        sendMessegeToShenhe(goodsid, cc.toLowerCase());
                    }
                    if (null != dd && !dd.equals("")) {
                        //发送消息
                        sendMessegeToShenhe(goodsid, dd.toLowerCase());
                    }
                    if (null != ee && !ee.equals("")) {
                        //发送消息
                        sendMessegeToShenhe(goodsid, ee.toLowerCase());
                    }

                }
                tasks.setEntryList(list);
                new SubmitTask(tasks, AddTaskActivity.this).execute();
            }
        });
    }

    private void sendMessegeToShenhe(String msg, String username) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(msg, username);
        emMessage.setStatus(EMMessage.Status.INPROGRESS);
        //        emMessage.setAttribute("nickName",);
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

    //默认字段填充
    class MRTask extends AsyncTask<Void, String, String> {
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
            rpc.addProperty("FSql", "select a.fname username,a.fitemid responid,b.fname depart,b.fitemid departid,c.FName company,c.fitemid companyid from t_User d inner join  t_Emp a on d.FEmpID=a.fitemid left join t_Department b on a.FDepartmentID=b.FItemID left join t_Item_3001 c on c.FItemID=b.f_102 where d.FName='" + YApplication.username + "'");
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
                        // 拿到head节点下的子节点title值
                        username = recordEle.elementTextTrim("username");//责任人
                        depart = recordEle.elementTextTrim("depart");//区域部门
                        company = recordEle.elementTextTrim("company");//组织机构
                        respon = recordEle.elementTextTrim("responid");//制单人id
                        zhidan = recordEle.elementTextTrim("responid");//制单人和责任人为一人
                        quyu = recordEle.elementTextTrim("departid");//区域部门id
                        zeren = recordEle.elementTextTrim("departid");
                        zuzhi = recordEle.elementTextTrim("companyid");//组织机构id
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "SUCCESS";
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("SUCCESS")) {
                tv_respon.setText(username);//责任人
                tv_zhidan.setText(username);//制单人
                tv_quyu.setText(depart);//区域部门
                tv_zeren.setText(depart);
                tv_zuzhi.setText(company);//组织机构
            }
        }
    }

    //查币别和汇率
    class CTask extends AsyncTask<Void, String, String> {
        Spinner sp;

        public CTask(Spinner sp) {
            this.sp = sp;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
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
            String sql = "select fcurrencyid,FName,FExchangeRate from t_Currency where fcurrencyid>0";
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
                System.out.println("CTask的根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("fitemid", recordEle.elementTextTrim("fcurrencyid"));
                    map.put("bibie", recordEle.elementTextTrim("FName"));
                    map.put("huilv", df.format(Double.parseDouble(recordEle.elementTextTrim("FExchangeRate"))));
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
            for (HashMap<String, String> map : list1) {
                String fname = map.get("bibie");
                strList1.add(fname);
            }
            SpinnerAdapter adapter1 = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_spinner_item, strList1);
            sp.setAdapter(adapter1);
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currencyid = Integer.parseInt(list1.get(i).get("fitemid"));
                    tv_huilv.setText(list1.get(i).get("huilv"));
                    huilv = Double.parseDouble(list1.get(i).get("huilv"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            Log.i("币别", currency + "<<<<<<<<<<<");
            if (currency != null) {
                for (int i = 0; i < adapter1.getCount(); i++) {
                    Log.i("获得的币别", adapter1.getItem(i) + "");
                    if (currency.equals(adapter1.getItem(i).toString())) {
                        sp.setSelection(i);// 默认选中项
                        break;
                    }
                }
            }
        }
    }

    //查组织机构
    class DepartsTask extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
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
            String sql = "select fitemid,fname from t_Item_3001 where fitemid>0";
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
                    map.put("itemid", recordEle.elementTextTrim("fitemid"));//组织机构id
                    map.put("fname", recordEle.elementTextTrim("fname"));//组织机构名称
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
            Utils.sortByInitial(list1);
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.departs).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tv_zuzhi.setText(strList1.get(i));//显示组织机构名称
                    zuzhi = list1.get(i).get("itemid");//保存组织机构id
                    dialog.dismiss();
                }
            });
        }

    }

    //查区域部门
    class AreaTask extends AsyncTask<Void, String, String> {
        int type;

        AreaTask(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
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
            String sql = "select fitemid,fname from t_Department where fitemid>0";
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
            Utils.sortByInitial(list1);
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.bumen).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (type) {
                        case 0:
                            quyu = list1.get(i).get("itemid");//保存区域部门id
                            tv_quyu.setText(strList1.get(i));//显示区域部门名称
                            break;
                        case 1:
                            zeren = list1.get(i).get("itemid");
                            tv_zeren.setText(strList1.get(i));
                            break;
                    }

                    dialog.dismiss();
                }
            });
        }
    }
    //查制度所属部门和制度操作细则
    //    class ZhiduTask extends AsyncTask<Void,String,String>{
    //
    //        @Override
    //        protected void onPreExecute() {
    //            list1.clear();
    //            strList1.clear();
    //            super.onPreExecute();
    //        }
    //
    //        @Override
    //        protected String doInBackground(Void... voids) {
    //            // 命名空间
    //            String nameSpace = "http://tempuri.org/";
    //            // 调用的方法名称
    //            String methodName = "JA_select";
    //            // EndPoint
    //            String endPoint = Consts.ENDPOINT;
    //            // SOAP Action
    //            String soapAction = "http://tempuri.org/JA_select";
    //
    //            // 指定WebService的命名空间和调用的方法名
    //            SoapObject rpc = new SoapObject(nameSpace, methodName);
    //
    //            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
    //            String sql = "select fitemid,fname,f_102 from t_Item_3006 where fitemid>0";
    //            rpc.addProperty("FSql", sql);
    //            rpc.addProperty("FTable", "t_user");
    //
    //            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
    //            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
    //
    //            envelope.bodyOut = rpc;
    //            // 设置是否调用的是dotNet开发的WebService
    //            envelope.dotNet = true;
    //            // 等价于envelope.bodyOut = rpc;
    //            envelope.setOutputSoapObject(rpc);
    //
    //            HttpTransportSE transport = new HttpTransportSE(endPoint);
    //            try {
    //                // 调用WebService
    //                transport.call(soapAction, envelope);
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //                Log.i("AddTaskActivity", e.toString() + "==================================");
    //            }
    //
    //            // 获取返回的数据
    //            SoapObject object = (SoapObject) envelope.bodyIn;
    //
    //            // 获取返回的结果
    //            Log.i("返回结果", object.getProperty(0).toString()+"=========================");
    //            String result = object.getProperty(0).toString();
    //            Document doc = null;
    //            try {
    //                doc = DocumentHelper.parseText(result); // 将字符串转为XML
    //                Element rootElt = doc.getRootElement(); // 获取根节点
    //                System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
    //                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
    //                // 遍历head节点
    //                while (iter.hasNext()) {
    //                    Element recordEle = (Element) iter.next();
    //                    HashMap<String,String> map = new HashMap<>();
    //                    map.put("itemid",recordEle.elementTextTrim("fitemid"));
    //                    map.put("fname",recordEle.elementTextTrim("fname"));
    //                    map.put("fnote",recordEle.elementTextTrim("f_102"));
    //                    list1.add(map);
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //            return "0";
    //        }
    //
    //        @Override
    //        protected void onPostExecute(String s) {
    //            super.onPostExecute(s);
    //            for(HashMap<String,String> map:list1){
    //                String name = map.get("fname");
    //                strList1.add(name);
    //            }
    //            final ListView lv = new ListView(AddTaskActivity.this);
    //            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this,android.R.layout.simple_list_item_1,strList1);
    //            lv.setAdapter(adapter);
    //            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
    //                    .setTitle(R.string.zhidu1).show();
    //            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    //                @Override
    //                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    //                    zhidu1 = list1.get(i).get("itemid");
    //                    tv_zhidu1.setText(strList1.get(i));
    //                    zhidu2 = list1.get(i).get("fnote");
    //                    et_zhidu2.setText(list1.get(i).get("fnote"));
    //                    dialog.dismiss();
    //                }
    //            });
    //        }
    //
    //    }

    //查内容和辅助
    class ItemTask extends AsyncTask<Void, String, String> {
        String name;

        public ItemTask(String name) {
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            progress = CustomProgress.show(AddTaskActivity.this, "加载中...", true, null);
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
            String sql;
            if (TextUtils.isEmpty(name)) {
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0 order by a.fnumber";
            } else {
                sql = "select a.fitemid,a.fname,a.ftaxrate,a.fseccoefficient,a.funitid,b.fname sup,c.fname jiliang from t_icitem a left join t_measureunit b on b.fmeasureunitid=a.fsecunitid left join t_measureunit c on c.fitemid=a.funitid where a.fitemid>0 and a.fname like '%" + name + "%' order by a.fnumber";
            }
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
                    map.put("itemid", recordEle.elementTextTrim("fitemid"));//物料id
                    map.put("fname", recordEle.elementTextTrim("fname"));//物料名称
                    map.put("sup", recordEle.elementTextTrim("sup"));//辅助单位名称
                    map.put("taxrate", recordEle.elementTextTrim("ftaxrate"));//对应税率
                    map.put("seccoefficient", recordEle.elementTextTrim("fseccoefficient"));//对应辅量换算率
                    map.put("unitid", recordEle.elementTextTrim("funitid"));//计量单位id
                    map.put("jiliang", recordEle.elementTextTrim("jiliang"));//计量名称
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
            progress.dismiss();
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle("内容:").show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    contentid = list1.get(i).get("itemid");//内容id
                    content = strList1.get(i);//内容名称
                    sup = list1.get(i).get("sup");//辅助单位名称
                    jiliangid = list1.get(i).get("unitid");//计量单位id
                    jiliang = list1.get(i).get("jiliang");//计量单位名称
                    taxrate = Double.parseDouble(list1.get(i).get("taxrate")) / 100;//对应税率
                    seccoefficient = Double.parseDouble(list1.get(i).get("seccoefficient"));//辅量换算率
                    tv_content.setText(content);//显示内容名称
                    tv_jl.setText(jiliang);//显示计量单位名称
                    dialog.dismiss();
                }
            });
        }
    }

    class WYTask extends AsyncTask<Void, String, String> {
        String name;

        public WYTask(String name) {
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            progress = CustomProgress.show(AddTaskActivity.this, "加载中...", true, null);
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
            String sql = "select FName,FItemID from t_Item_3003";
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
                    map.put("itemid", null == recordEle.elementTextTrim("FItemID") ? "" : recordEle.elementTextTrim("FItemID"));//银行id
                    map.put("fname", recordEle.elementTextTrim("FName"));//银行名称
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
            progress.dismiss();
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle("网银:").show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    wyid = list1.get(i).get("itemid");//网银id
                    wangyinname = strList1.get(i);//网银名称
                    tv_wy.setText(wangyinname);
                    dialog.dismiss();
                }
            });
        }
    }


    //查人员
    class EmpTask extends AsyncTask<Void, String, String> {
        int    type;//0选择制单人,1选择往来
        String name;

        public EmpTask(int type, String name) {
            this.type = type;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            progress = CustomProgress.show(AddTaskActivity.this, "加载中", true, null);
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
            String sql;
            if (TextUtils.isEmpty(name)) {
                sql = "select fitemid,fname from t_Emp where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Emp where fitemid>0 and fname like '%" + name + "%'";
            }
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
            Utils.sortByInitial(list1);
            progress.dismiss();
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.emp).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (type) {
                        case 0:
                            respon = list1.get(i).get("itemid");
                            tv_respon.setText(strList1.get(i));
                            //                            zhidan = list1.get(i).get("itemid");
                            //                            tv_zhidan.setText(strList1.get(i));
                            break;
                        case 1:
                            //选择往来
                            contacts = list1.get(i).get("itemid");
                            tv_contacts.setText(strList1.get(i));
                            break;
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    //查计量
    class JLTask extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
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
            String sql = "select fitemid,fname from t_MeasureUnit where fitemid>0";
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
            Utils.sortByInitial(list1);
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.jiliang).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    jiliangid = list1.get(i).get("itemid");//计量单位id
                    jiliang = strList1.get(i);//显示计量单位
                    tv_jl.setText(jiliang);
                    dialog.dismiss();
                }
            });
        }
    }

    //查计划相关字段
    class JHTask extends AsyncTask<Void, String, String> {
        TextView tv, tv1, tv2, tv3;
        String name;

        public JHTask(TextView tv, TextView tv1, TextView tv2, TextView tv3, String name) {
            this.tv = tv;
            this.tv1 = tv1;
            this.tv2 = tv2;
            this.tv3 = tv3;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            progress = CustomProgress.show(AddTaskActivity.this, "加载中...", true, null);
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
            String sql;
            if (TextUtils.isEmpty(name)) {
                sql = "select a.fitemid,a.fname,a.f_111,a.f_107,b.fname yusuan from t_Item_3007 a left join t_item b on b.fitemid=a.f_105 where a.fitemid>0";
            } else {
                sql = "select a.fitemid,a.fname,a.f_111,a.f_107,b.fname yusuan from t_Item_3007 a left join t_item b on b.fitemid=a.f_105 where a.fitemid>0 and a.fname like '%" + name + "%'";
            }
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
                    map.put("jihua", recordEle.elementTextTrim("f_111"));
                    map.put("jhys", recordEle.elementTextTrim("f_107"));
                    map.put("yusuan", recordEle.elementTextTrim("yusuan"));
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
            PinyinComparator comparator = new PinyinComparator();
            Collections.sort(list1, comparator);
            progress.dismiss();
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.progress).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    planid = list1.get(i).get("itemid");
                    tv.setText(strList1.get(i));
                    tv1.setText(list1.get(i).get("jihua"));
                    tv2.setText(list1.get(i).get("jhys"));
                    tv3.setText(list1.get(i).get("yusuan"));
                    dialog.dismiss();
                }
            });
        }
    }

    //查评分规则
    class PFTask extends AsyncTask<Void, String, String> {
        TextView tv;

        public PFTask(TextView tv) {
            this.tv = tv;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
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
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final GridView gv = new GridView(AddTaskActivity.this);
            gv.setNumColumns(3);
            final String[] fen = new String[strList1.size()];
            for (int i = 0; i < strList1.size(); i++) {
                fen[i] = strList1.get(i);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1,
                    fen);
            gv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(gv).show();
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

    //查单子主表详情
    class DeTask extends AsyncTask<Void, String, String> {
        String Taskno;

        public DeTask(String Taskno) {
            this.Taskno = Taskno;
        }

        @Override
        protected void onPreExecute() {
            list.clear();
            progress = CustomProgress.show(AddTaskActivity.this, "加载中...", true, null);
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
            String sql = "select top 1 a.FAmount4 rate,c.fname departs,c.fitemid departsid,d.fname area,d.fitemid areaid,e.fname currency,e.fcurrencyid," +
                    "f.fname respon,f.fitemid responid,g.fname wanglai,g.fitemid wanglid,h.fname neirong,h.fitemid neirongid,h.ftaxrate,h.fseccoefficient,i.fname zhidan,i.fitemid zhidanid,j.fname zhidu1,j.fitemid zhiduid,a.fnote1,k.fname jiliang,k.fitemid jiliangid,a.fbase16 zerenid,l.fname zeren ,p.fname wangyin,p.fitemid wangyinid from t_BOS200000000 a " +
                    "left join t_BOS200000000Entry2 b on b.FID=a.FID left join t_Item_3001 c " +
                    "on c.FItemID=a.FBase11 left join t_Department d on d.FItemID=a.FBase12 left join" +
                    " t_Currency e on e.FCurrencyID=a.FBase3 left join t_emp f on f.fitemid=b.fbase4 left join" +
                    " t_emp g on g.fitemid=b.fbase10 left join t_ICItem h on h.FItemID=b.FBase1 left join t_emp i on i.fitemid=b.fbase15 left join t_Item_3006 j on j.FItemID=a.FBase13 left join t_measureunit k on k.fitemid=b.fbase2 " +
                    "left join t_Department l on l.fitemid=a.fbase16 left join t_Item_3003 p on p.fitemid=a.fbase17 where a.FBillNo ='" + Taskno + "'";
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
                System.out.println("DeTask的根节点：" + rootElt.getName()); // 拿到根节点的名称
                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head
                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("rate", recordEle.elementTextTrim("rate"));
                    map.put("departs", recordEle.elementTextTrim("departs"));
                    map.put("departsid", recordEle.elementTextTrim("departsid"));
                    map.put("currency", recordEle.elementTextTrim("currency"));
                    map.put("fcurrencyid", recordEle.elementTextTrim("fcurrencyid"));
                    if (recordEle.elementTextTrim("area").equals("")) {
                        map.put("area", "");
                        map.put("areaid", null);
                    } else {
                        map.put("area", recordEle.elementTextTrim("area"));
                        map.put("areaid", recordEle.elementTextTrim("areaid"));
                    }
                    map.put("zerenid", recordEle.elementTextTrim("zerenid"));
                    map.put("zeren", recordEle.elementTextTrim("zeren"));
                    map.put("neirong", recordEle.elementTextTrim("neirong"));
                    map.put("neirongid", recordEle.elementTextTrim("neirongid"));
                    map.put("respon", recordEle.elementTextTrim("respon"));
                    map.put("responid", recordEle.elementTextTrim("responid"));
                    if (recordEle.elementTextTrim("zhidan").equals("")) {
                        map.put("zhidan", "");
                        map.put("zhidanid", null);
                    } else {
                        map.put("zhidan", recordEle.elementTextTrim("zhidan"));
                        map.put("zhidanid", recordEle.elementTextTrim("zhidanid"));
                    }
                    map.put("wanglai", recordEle.elementTextTrim("wanglai"));
                    map.put("wanglid", recordEle.elementTextTrim("wanglid"));
                    map.put("wangyin", recordEle.elementTextTrim("wangyin"));
                    map.put("wangyinid", recordEle.elementTextTrim("wangyinid"));
                    map.put("zhidu1", recordEle.elementTextTrim("zhidu1"));
                    map.put("zhiduid", recordEle.elementTextTrim("zhiduid"));
                    map.put("fnote1", recordEle.elementTextTrim("fnote1"));
                    map.put("taxrate", recordEle.elementTextTrim("ftaxrate"));
                    map.put("seccoefficient", recordEle.elementTextTrim("fseccoefficient"));
                    map.put("jiliang", recordEle.elementTextTrim("jiliang"));
                    map.put("jiliangid", recordEle.elementTextTrim("jiliangid"));
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //            if (list.get(0).get("fcurrencyid").equals("1")) {
            //                sp_bibie.setSelection(0);
            //            } else {
            //                sp_bibie.setSelection(1);
            //            }
            tv_huilv.setText(df.format(Double.parseDouble(list.get(0).get("rate"))));
            tv_zuzhi.setText(list.get(0).get("departs"));
            tv_quyu.setText(list.get(0).get("area"));
            tv_content.setText(list.get(0).get("neirong"));
            tv_wy.setText(list.get(0).get("wangyin"));
            tv_respon.setText(list.get(0).get("respon"));
            tv_zhidan.setText(list.get(0).get("zhidan"));
            tv_contacts.setText(list.get(0).get("wanglai"));
            tv_jl.setText(list.get(0).get("jiliang"));
            tv_zeren.setText(list.get(0).get("zeren"));
            currencyid = Integer.parseInt(list.get(0).get("fcurrencyid"));
            currency = list.get(0).get("currency");
            zuzhi = list.get(0).get("departsid");
            quyu = list.get(0).get("areaid");
            zeren = list.get(0).get("zerenid");
            zhidu1 = list.get(0).get("zhiduid");
            zhidu2 = list.get(0).get("fnote1");
            contentid = list.get(0).get("neirongid");
            wyid=list.get(0).get("wangyinid");
            content = list.get(0).get("neirong");
            respon = list.get(0).get("responid");
            zhidan = list.get(0).get("zhidanid");
            contacts = list.get(0).get("wanglid");
            taxrate = Double.parseDouble(list.get(0).get("taxrate")) / 100;
            seccoefficient = Double.parseDouble(list.get(0).get("seccoefficient"));
            jiliangid = list.get(0).get("jiliangid");
        }

    }

    //查询单子子表详情
    class DeEntryTask extends AsyncTask<Void, String, String> {
        String Taskno;

        DeEntryTask(String Taskno) {
            this.Taskno = Taskno;
        }

        @Override
        protected void onPreExecute() {
            ziList.clear();
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
            String sql = " select b.FTime qi,b.FTime1 zhi,g.FName respon,h.FName progress,h.fitemid planid,h.F_111 plans,i.FName budget,h.f_107 pbudget,b.FNOTE note," +
                    "   j.FName contacts,k.FName neirong,l.FName jiliang,l.fitemid jiliangid,b.FDecimal shuliang,b.FDecimal1 danjia,b.FAmount2 hanshui,b.FAmount3 buhan," +
                    "   b.FText fasong,b.FText1 huikui,o.FName pingfen,o.fitemid pfid,p.FName js1,p.fitemid jsid1,b.FCheckBox1 qr1,q.FName js2,q.fitemid jsid2,b.FCheckBox2 qr2," +
                    "   m.FName js3,m.fitemid jsid3,b.FCheckBox3 qr3,n.FName js4,n.fitemid jsid4,b.FCheckBox4 qr4,r.FName js5,r.fitemid jsid5,b.FCheckBox5 qr5,s.fname fuzhu,b.fdecimal2 fuliang,b.id" +
                    "   ,b.fimage1,b.fimage2,b.fimage3,b.fimage4,b.fimage5 from t_BOS200000000 a inner join t_BOS200000000Entry2 b on a.FID=b.FID" +
                    "   left join t_Currency c on c.FCurrencyID=a.FBase3 left join t_Item_3001 d on d.FItemID=a.FBase11" +
                    "   left join t_Department e on e.FItemID=a.FBase11 left join t_Item_3006 f on f.FItemID=a.FBase13" +
                    "   left join t_Emp g on g.FItemID=b.FBase4 left join t_Item_3007 h on h.FItemID=b.FBase left join" +
                    "   t_item i on i.FItemID=h.F_105 left join t_Emp j on j.FItemID=b.FBase10 left join t_ICItem k on k.FItemID=b.FBase1" +
                    "   left join t_MeasureUnit l on l.FMeasureUnitID=b.FBase2 left join t_Item o on o.FItemID=b.FBase14" +
                    "   left join t_Emp p on p.FItemID=b.FBase5 left join t_Emp q on q.FItemID=b.FBase6" +
                    "   left join t_Emp m on m.FItemID=b.FBase7 left join t_Emp n on n.FItemID=b.fbase8" +
                    "   left join t_Emp r on r.FItemID=b.FBase9 left join t_MeasureUnit s on s.FMeasureUnitID=k.FSecUnitID where a.fbillno='" + Taskno + "'";
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
                Log.i("MeFragment", e.toString() + "==================================");
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

                System.out.println("DeEntryTask的根节点：" + rootElt.getName()); // 拿到根节点的名称

                Iterator iter = rootElt.elementIterator("Cust"); // 获取根节点下的子节点head

                // 遍历head节点
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    planid = recordEle.elementTextTrim("planid");
                    //                    jiliangid = recordEle.elementTextTrim("jiliangid");
                    pfid = recordEle.elementTextTrim("pfid");
                    String qi = recordEle.elementTextTrim("qi");
                    String zhi = recordEle.elementTextTrim("zhi");
                    String neirong = recordEle.elementTextTrim("neirong");
                    String jiliang = recordEle.elementTextTrim("jiliang");
                    String shuliang = recordEle.elementTextTrim("shuliang");
                    String danjia = recordEle.elementTextTrim("danjia");
                    String progress = recordEle.elementTextTrim("progress");
                    String plan = recordEle.elementTextTrim("plans");
                    String budget = recordEle.elementTextTrim("budget");
                    String pbudget = recordEle.elementTextTrim("pbudget");
                    String note = recordEle.elementTextTrim("note");
                    String hanshui = recordEle.elementTextTrim("hanshui");
                    String buhan = recordEle.elementTextTrim("buhan");
                    String fuzhu = recordEle.elementTextTrim("fuzhu");
                    String fuliang = recordEle.elementTextTrim("fuliang");
                    String fasong = recordEle.elementTextTrim("fasong");
                    String huikui = recordEle.elementTextTrim("huikui");
                    String pingfen = recordEle.elementTextTrim("pingfen");
                    mBitmapList = new ArrayList<>();
                    String btpurl = "";
                    for (int i = 0; i < 5; i++) {
                        final String url = recordEle.elementTextTrim("fimage" + (i + 1));
                        if (null != url && !"".equals(url)) {
                            mBitmapList.add(url);
                            btpurl = btpurl + url;
                        }
                    }
                    mSumBtUrlList.add(btpurl);
                    a = recordEle.elementTextTrim("js1");
                    b = recordEle.elementTextTrim("js2");
                    c = recordEle.elementTextTrim("js3");
                    d = recordEle.elementTextTrim("js4");
                    e = recordEle.elementTextTrim("js5");
                    aid = recordEle.elementTextTrim("jsid1");
                    bid = recordEle.elementTextTrim("jsid2");
                    cid = recordEle.elementTextTrim("jsid3");
                    did = recordEle.elementTextTrim("jsid4");
                    eid = recordEle.elementTextTrim("jsid5");
                    qr1 = recordEle.elementTextTrim("qr1");
                    qr2 = recordEle.elementTextTrim("qr2");
                    qr3 = recordEle.elementTextTrim("qr3");
                    qr4 = recordEle.elementTextTrim("qr4");
                    qr5 = recordEle.elementTextTrim("qr5");
                    String id = recordEle.elementTextTrim("id");
                    Log.i("审核标志", qr1 + qr2 + qr3 + qr4 + qr5);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("qi", qi);
                    map.put("zhi", zhi);
                    map.put("neirong", neirong);
                    //                    map.put("jiliang",jiliang);
                    map.put("shuliang", df.format(Double.parseDouble(shuliang)));
                    map.put("danjia", df.format(Double.parseDouble(danjia)));
                    map.put("progress", progress);
                    map.put("plan", plan);
                    map.put("budget", budget);
                    map.put("pbudget", df.format(Double.parseDouble(pbudget)));
                    map.put("note", note);
                    map.put("hanshui", df.format(Double.parseDouble(hanshui)));
                    map.put("buhan", df.format(Double.parseDouble(buhan)));
                    map.put("fuzhu", fuzhu);
                    map.put("fuliang", df.format(Double.parseDouble(fuliang)));
                    map.put("fasong", fasong);
                    map.put("huikui", huikui);
                    map.put("pingfen", pingfen);
                    map.put("planid", planid);
                    //                    map.put("jiliangid",jiliangid);
                    map.put("pfid", pfid);
                    map.put("a", a);
                    map.put("b", b);
                    map.put("c", c);
                    map.put("d", d);
                    map.put("e", e);
                    map.put("aid", aid);
                    map.put("bid", bid);
                    map.put("cid", cid);
                    map.put("did", did);
                    map.put("eid", eid);
                    map.put("qr1", qr1);
                    map.put("qr2", qr2);
                    map.put("qr3", qr3);
                    map.put("qr4", qr4);
                    map.put("qr5", qr5);
                    map.put("id", id);
                    ziList.add(map);
                    mSumBitmapList.add(mBitmapList);
                    //判断是否有人审核过了
                    isLooked(qr1, qr2, qr3, qr4, qr5);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (list.size() == 0) {
                return "0";
            } else {
                //有人确认过就不能修改
                int size = lists.size();
                for (HashMap<String, String> maps : ziList) {
                    lists.add(maps.get("qr1"));
                    lists.add(maps.get("qr2"));
                    lists.add(maps.get("qr3"));
                    lists.add(maps.get("qr4"));
                    lists.add(maps.get("qr5"));
                    if (!maps.get("a").equals("")) {
                        strList2.add(maps.get("a"));
                    }
                    if (!maps.get("b").equals("")) {
                        strList2.add(maps.get("b"));
                    }
                    if (!maps.get("c").equals("")) {
                        strList2.add(maps.get("c"));
                    }
                    if (!maps.get("d").equals("")) {
                        strList2.add(maps.get("d"));
                    }
                    if (!maps.get("e").equals("")) {
                        strList2.add(maps.get("e"));
                    }
                }
                return "1";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            for (int i = 0; i < ziList.size(); i++) {
                total = total + Double.parseDouble(ziList.get(i).get("shuliang"));
                amount = amount + Double.parseDouble(ziList.get(i).get("hanshui"));
            }
            tv_total.setText(String.valueOf(total));
            tv_amounts.setText(String.valueOf(amount));
            adapter = new ZiAdapter(AddTaskActivity.this, ziList, mSumBitmapList, 2);
            lv_zb.setAdapter(adapter);
        }
    }

    private boolean isChecked = false;

    //查阅是否已被审核
    private void isLooked(String qr1, String qr2, String qr3, String qr4, String qr5) {
        if ("True".equals(qr1) || "True".equals(qr2) || "True".equals(qr3) || "True".equals(qr4) || "True".equals(qr5)) {
            isChecked = true;
        }
    }

    //查询好友列表
    class HYTask extends AsyncTask<Void, String, String> {
        TextView tv;
        int      index;

        public HYTask(TextView tv, int index) {
            this.tv = tv;
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            list1.clear();
            strList1.clear();
            progress = CustomProgress.show(AddTaskActivity.this, "加载中...", true, null);
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
                rpc.addProperty("FSql", "select a.fitemid,a.fname,b.FDescription name from t_emp a inner join t_user b on a.fitemid=b.fempid where b.fuserid in (select fuserid from t_group where FGroupID=45) and  b.FDescription in" + s);
                //                rpc.addProperty("FSql", "select a.fitemid,a.fname,b.FDescription name from t_emp a inner join t_user b on a.fitemid=b.fempid where b.FDescription in" + s);
                //                rpc.addProperty("FSql", "select a.fitemid,a.fname,b.fname name from t_emp a inner join t_user b on a.fitemid=b.fempid where b.FDescription in" + s);
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
                        HashMap<String, String> map = new HashMap<>();
                        map.put("fname", recordEle.elementTextTrim("fname"));//名称
                        map.put("name", recordEle.elementTextTrim("name"));//用户名
                        map.put("fitemid", recordEle.elementTextTrim("fitemid"));//代码
                        list1.add(map);
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
            Utils.sortByInitial(list1);
            for (HashMap<String, String> map : list1) {
                String name = map.get("fname");
                strList1.add(name);
            }
            final ListView lv = new ListView(AddTaskActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTaskActivity.this, android.R.layout.simple_list_item_1, strList1);
            lv.setAdapter(adapter);
            final AlertDialog dialog = new AlertDialog.Builder(AddTaskActivity.this).setView(lv)
                    .setTitle(R.string.emp).show();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (index) {
                        case 1:
                            aid = list1.get(i).get("fitemid");//审核人id
                            a = strList1.get(i);//显示审核人名称
                            aa = list1.get(i).get("name");//审核人用户名
                            qr1 = "0";
                            tv.setText(a);
                            break;
                        case 2:
                            bid = list1.get(i).get("fitemid");//审核人id
                            b = strList1.get(i);//显示审核人名称
                            bb = list1.get(i).get("name");//审核人用户名
                            qr2 = "0";
                            tv.setText(b);
                            break;
                        case 3:
                            cid = list1.get(i).get("fitemid");//审核人id
                            c = strList1.get(i);//显示审核人名称
                            cc = list1.get(i).get("name");//审核人用户名
                            qr3 = "0";
                            tv.setText(c);
                            break;
                        case 4:
                            did = list1.get(i).get("fitemid");//审核人id
                            d = strList1.get(i);//显示审核人名称
                            dd = list1.get(i).get("name");//审核人用户名
                            qr4 = "0";
                            tv.setText(d);
                            break;
                        case 5:
                            eid = list1.get(i).get("fitemid");//审核人id
                            e = strList1.get(i);//显示审核人名称
                            ee = list1.get(i).get("name");//审核人用户名
                            qr5 = "0";
                            tv.setText(e);
                            break;
                    }
                    dialog.dismiss();
                }
            });
            super.onPostExecute(s);
        }
    }

    //提交多张图片
    class Task2 extends AsyncTask<Void, Integer, Integer> {
        private List<Bitmap> mBitmapList;
        private int          n;//n小于0时是新增，大于等于0时是点击编辑

        public Task2(List<Bitmap> btList, int n) {
            this.mBitmapList = btList;
            this.n = n;
        }

        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
            //            ProgressDialogUtil.startShow(AddTaskActivity.this, "正在上传，请稍等");
        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "PIC_UPLoad";
                // EndPoint
                String endPoint = Consts.ENDPOINT;
                // SOAP Action
                String soapAction = "http://tempuri.org/PIC_UPLoad";

                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                //图片
                Document document2 = DocumentHelper.createDocument();
                Element rootElement2 = document2.addElement("NewDataSet");
                for (Bitmap bit : mBitmapList) {
                    Element cust = rootElement2.addElement("Cust");
                    cust.addElement("fimage").setText(bitmapToBase64(bit));
                }
                //
                OutputFormat outputFormat = OutputFormat.createPrettyPrint();
                outputFormat.setSuppressDeclaration(false);
                outputFormat.setNewlines(false);
                StringWriter stringWriter2 = new StringWriter();
                // xmlWriter是用来把XML文档写入字符串的(工具)
                XMLWriter xmlWriter2 = new XMLWriter(stringWriter2, outputFormat);
                // 把创建好的XML文档写入字符串
                xmlWriter2.write(document2);

                rpc.addProperty("base64string", stringWriter2.toString().substring(38));//<NewDataSet><Cust><fimage></fimage></Cust><Cust><fimage></fimage></Cust></NewDataSet>
                //
                Log.i("qwe", stringWriter2.toString().substring(38));

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
                    Log.i("sss", e.toString() + "sss");
                }
                // 获取返回的数据
                SoapObject object = (SoapObject) envelope.bodyIn;
                // 获取返回的结果
                String result = object.getProperty(0).toString();
                Log.i("sss", result + "sss");
                //获取返回的图片网络地址，加入集合中
                if (n >= 0) {
                    mSumBtUrlList.set(n, result);
                } else {
                    mSumBtUrlList.add(result);
                }
            } catch (Exception e) {
                Log.i("sss", e.toString() + "sss");
            }
            return 5;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
            ProgressDialogUtil.hideDialog();
            Toast.makeText(AddTaskActivity.this, "修改图片成功", Toast.LENGTH_LONG).show();
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    //提交单个图片
    class SingleTask2 extends AsyncTask<Void, Integer, Integer> {
        private Bitmap       mBitmap;
        private int          n;
        private int          mKind;
        private List<String> mUrlList;

        public SingleTask2(Bitmap bt, int which, List<String> mBtUrlList, int kind) {
            this.mBitmap = bt;
            this.n = which;
            this.mUrlList = mBtUrlList;
            this.mKind = kind;
        }

        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
            ProgressDialogUtil.startShow(AddTaskActivity.this, "正在上传，请稍等");
        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "PIC_UPLoad";
                // EndPoint
                String endPoint = Consts.ENDPOINT;
                // SOAP Action
                String soapAction = "http://tempuri.org/PIC_UPLoad";

                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                //图片
                Document document2 = DocumentHelper.createDocument();
                Element rootElement2 = document2.addElement("NewDataSet");

                Element cust = rootElement2.addElement("Cust");
                cust.addElement("fimage").setText(bitmapToBase64(mBitmap));

                //
                OutputFormat outputFormat = OutputFormat.createPrettyPrint();
                outputFormat.setSuppressDeclaration(false);
                outputFormat.setNewlines(false);
                StringWriter stringWriter2 = new StringWriter();
                // xmlWriter是用来把XML文档写入字符串的(工具)
                XMLWriter xmlWriter2 = new XMLWriter(stringWriter2, outputFormat);
                // 把创建好的XML文档写入字符串
                xmlWriter2.write(document2);

                rpc.addProperty("base64string", stringWriter2.toString().substring(38));//<NewDataSet><Cust><fimage></fimage></Cust><Cust><fimage></fimage></Cust></NewDataSet>
                //
                Log.i("qwe", stringWriter2.toString().substring(38));

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
                //获取返回的图片网络地址
                if (null != result && result.startsWith(",")) {
                    result.substring(1);
                }
                mUrlList.set(n, result);
                return 5;
            } catch (Exception e) {
                Log.i("sss", e.toString() + "sss");
                return 0;
            }
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
            MySendProcess = MySendProcess + 1;
            if (integer == 5) {
                Toast.makeText(AddTaskActivity.this, "图片" + (n + 1) + "提交成功", Toast.LENGTH_LONG).show();
            } else {
                ToastUtils.showToast(AddTaskActivity.this, "图片" + (n + 1) + "提交失败");
            }
            if (MySendProcess == mBitmapList.size() - htUrlnum) {//说明要提交的图片都提交了
                ProgressDialogUtil.hideDialog();
                //将总的图片地址中对应的url，换置。
                String totalUrl = "";
                for (int m = 0; m < mUrlList.size(); m++) {
                    String url = mUrlList.get(m);
                    if (!"".equals(url)) {
                        if (m == mUrlList.size() - 1) {
                            totalUrl = totalUrl + url;
                        } else {
                            totalUrl = totalUrl + url + ",";
                        }
                    }
                }
                if (mKind >= 0) {
                    mSumBtUrlList.set(mKind, totalUrl);
                } else {
                    mSumBtUrlList.add(totalUrl);
                }
            }
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    /**
     * @param uri：图片的本地url地址
     * @return Bitmap；
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /*bitmap转base64*/
    public String bitmapToBase64(Bitmap bitmap) {
        String result = "";
        ByteArrayOutputStream bos = null;
        try {
            if (null != bitmap) {
                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos);//将bitmap放入字节数组流中

                bos.flush();//将bos流缓存在内存中的数据全部输出，清空缓存
                bos.close();

                byte[] bitmapByte = bos.toByteArray();
                result = Base64.encodeToString(bitmapByte, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
