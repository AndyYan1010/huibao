package com.example.win7.huibao.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.activity.AddOrderActivity;
import com.example.win7.huibao.adapter.LvAccAdapter;
import com.example.win7.huibao.adapter.LvShowMoreAdapter;
import com.example.win7.huibao.entity.OrderDataInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/7/12 9:42
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class SubBillFragment extends Fragment {
    private View mRootView;
    private boolean mEditable = false;//是否可编辑
    private OrderDataInfo             orderInfo;//整个订单表信息
    private List<Map<String, String>> mShowData;//临时存放内码
    private LvShowMoreAdapter         showMoreAdapter;
    private ListView                  lv_acc;
    private List                      mData;
    private LvAccAdapter              accAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sub_bill, null);
        AddOrderActivity activity = (AddOrderActivity) getActivity();
        mEditable = activity.getEditable();
        orderInfo = activity.getOrderInfo();
        initView();
        initData();
        return mRootView;
    }

    private void initView() {
        lv_acc = mRootView.findViewById(R.id.lv_acc);
        mData = new ArrayList();
        mData.add("");
        mData.add("");
        mData.add("");
        mData.add("");
        accAdapter = new LvAccAdapter(getContext(), mData, -1);
        lv_acc.setAdapter(accAdapter);
    }

    private void initData() {
        //先行判断，进入的条件
        Intent intent = getActivity().getIntent();
        String kind = intent.getStringExtra("kind");
        if (kind.equals("check") || kind.equals("edit")) {//查看//编辑
            //先填入数据
            writeDataIn();
        }
        //给tv设置点击修改事件
        setListener();
    }

    private void writeDataIn() {

    }

    private void setListener() {
        if (mEditable) {
            lv_acc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //弹出dailog让用户修改子表
                    showAlogToChange("修改", i);
                }
            });
        }
    }

    private void showAlogToChange(String title, final int item) {//item=-1是新增,>=0是修改
        View view = View.inflate(getContext(), R.layout.lv_item_acc, null);
        TextView tv_more = view.findViewById(R.id.tv_more);//展示更多
        TextView tv_jfkm = view.findViewById(R.id.tv_jfkm);//借方科目
        TextView tv_jfkmje = view.findViewById(R.id.tv_jfkmje);//借方科目金额
        TextView tv_jfjxkm = view.findViewById(R.id.tv_jfjxkm);//借方进项科目
        TextView tv_jfjxje = view.findViewById(R.id.tv_jfjxje);//借方进项金额
        TextView tv_wy = view.findViewById(R.id.tv_wy);//网银
        TextView tv_dfkm = view.findViewById(R.id.tv_dfkm);//贷方科目
        TextView tv_dfkmje = view.findViewById(R.id.tv_dfkmje);//贷方科目金额
        TextView tv_dfxxkm = view.findViewById(R.id.tv_dfxxkm);//贷方销项科目
        TextView tv_dfxxje = view.findViewById(R.id.tv_dfxxje);//贷方销项金额
        tv_more.setVisibility(View.GONE);
        final Map<String, String> sBodyMap;
        //填充数据
        if (item >= 0) {
        } else {
            sBodyMap = new HashMap<>();
        }

        new AlertDialog.Builder(getContext()).setView(view).setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (item < 0) {
                            //orderInfo.getMapListson().add(sBodyMap);
                            mData.add("");
                        }
                        accAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();

        setTvChangeListener(tv_jfkm, "借方科目", "search", "???");
        setTvChangeListener(tv_jfkmje, "借方科目金额", "", "???");
        setTvChangeListener(tv_jfjxkm, "借方进项科目", "", "???");
        setTvChangeListener(tv_jfjxje, "借方进项金额", "", "???");
        setTvChangeListener(tv_wy, "网银", "", "???");
        setTvChangeListener(tv_dfkm, "贷方科目", "", "???");
        setTvChangeListener(tv_dfkmje, "贷方科目金额", "", "???");
        setTvChangeListener(tv_dfxxkm, "贷方销项科目", "", "???");
        setTvChangeListener(tv_dfxxje, "贷方销项金额", "", "???");
    }

    private void setTvChangeListener(final TextView tv, final String title, final String writekind, final String which) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeViewContent(tv, title, writekind, which);
            }
        });
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
                        }
                        if ("search".equals(writekind)) {
                            //为空，弹出选择菜单列表
                            showMoreWriteInfo(tvcontent, title, content, whichkey);
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void showMoreWriteInfo(final TextView tvcontent, String title, final String cont, final String whichkey) {//cont查找内容是否为空
        View view = View.inflate(getContext(), R.layout.view_only_list, null);
        ListView lv_showmore = view.findViewById(R.id.lv_showmore);
        if (null == mShowData) {
            mShowData = new ArrayList();
        } else {
            mShowData.clear();
        }
        Map exMap = new HashMap();
        exMap.put("fname", "测试1");
        Map exMap2 = new HashMap();
        exMap2.put("fname", "测试2");
        mShowData.add(exMap);
        mShowData.add(exMap2);
        showMoreAdapter = new LvShowMoreAdapter(getContext(), mShowData);
        lv_showmore.setAdapter(showMoreAdapter);
        //TODO:根据确定的内容查找内码
        String sql = "";
        if (whichkey.equals("FName1")) {//组织机构
            if (null == cont || "".equals(cont)) {
                sql = "select fitemid,fname from t_Item_3001 where fitemid>0";
            } else {
                sql = "select fitemid,fname from t_Item_3001 where fitemid>0 and fname like '%" + cont + "%'";
            }
        }
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setTitle(title).show();
        lv_showmore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tvcontent.setText(mShowData.get(i).get("fname"));
                dialog.dismiss();
            }
        });
    }
}
