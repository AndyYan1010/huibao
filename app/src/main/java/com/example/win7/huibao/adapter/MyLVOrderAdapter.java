package com.example.win7.huibao.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.win7.huibao.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @创建者 AndyYan
 * @创建时间 2018/6/26 13:05
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class MyLVOrderAdapter extends BaseAdapter {
    private Context                   mContext;
    //    private List<OrderDataInfo.ListsonBean> mList;
    private List<Map<String, String>> mList;
    private int                       mOpenItem;

    public MyLVOrderAdapter(Context context, List list, int openItem) {
        this.mContext = context;
        this.mList = list;
        this.mOpenItem = openItem;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolder viewHolder;
        if (null == view) {
            viewHolder = new MyViewHolder();
            view = View.inflate(mContext, R.layout.lv_item_open_detail, null);
            viewHolder.view_line = view.findViewById(R.id.view_line);
            viewHolder.lin_open = view.findViewById(R.id.lin_open);
            viewHolder.lin_sorh = view.findViewById(R.id.lin_sorh);
            //需填充数据的view
//            viewHolder.tv_con = view.findViewById(R.id.tv_con);//内 容
            viewHolder.tv_date = view.findViewById(R.id.tv_date);//日期
            viewHolder.tv_zdr = view.findViewById(R.id.tv_zdr);//制单人
            viewHolder.tv_sqbm = view.findViewById(R.id.tv_sqbm);//申请部门
            viewHolder.tv_zrbm = view.findViewById(R.id.tv_zrbm);//责任部门/考核
            viewHolder.tv_wl = view.findViewById(R.id.tv_wl);//表体往来
            viewHolder.tv_wlyhzh = view.findViewById(R.id.tv_wlyhzh);//往来-银行及帐号
            viewHolder.tv_jhys = view.findViewById(R.id.tv_jhys);//计划预算进度
            viewHolder.tv_yskm = view.findViewById(R.id.tv_yskm);//预算科目
            viewHolder.tv_ysye = view.findViewById(R.id.tv_ysye);//预算余额
            viewHolder.tv_jl = view.findViewById(R.id.tv_jl);//计量
            viewHolder.tv_sl = view.findViewById(R.id.tv_sl);//数量
            viewHolder.tv_djhs = view.findViewById(R.id.tv_djhs);//单价含税
            viewHolder.tv_jehs = view.findViewById(R.id.tv_jehs);//金额含税
            viewHolder.tv_se = view.findViewById(R.id.tv_se);//税额
            viewHolder.tv_rmbbhse = view.findViewById(R.id.tv_rmbbhse);//人民币不含税额
            viewHolder.tv_slpercent = view.findViewById(R.id.tv_slpercent);//税率%
            viewHolder.tv_fz = view.findViewById(R.id.tv_fz);//辅助
            viewHolder.tv_fl = view.findViewById(R.id.tv_fl);//辅量
            viewHolder.tv_bz = view.findViewById(R.id.tv_bz);//备注-发票号码/税票说明
            viewHolder.tv_fpswkm = view.findViewById(R.id.tv_fpswkm);//发票税务科目
            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolder) view.getTag();
        }
        //填充数据
        //        OrderDataInfo.ListsonBean bean = mList.get(i);
        Map<String, String> dataMap = mList.get(i);

//        viewHolder.tv_con.setText(dataMap.get("FName8"));
        viewHolder.tv_date.setText(dataMap.get("FTime2"));
        viewHolder.tv_zdr.setText(dataMap.get("FName9"));
        viewHolder.tv_sqbm.setText(dataMap.get("FName10"));
        viewHolder.tv_zrbm.setText(dataMap.get("FName11"));
        viewHolder.tv_wl.setText(dataMap.get("FName12"));
        viewHolder.tv_wlyhzh.setText(dataMap.get("FBankAccount"));
        viewHolder.tv_jhys.setText(dataMap.get("FName13"));
        viewHolder.tv_yskm.setText(dataMap.get("FName14"));
        viewHolder.tv_ysye.setText(dataMap.get("F_109"));
        viewHolder.tv_jl.setText(dataMap.get("FName15"));
        viewHolder.tv_sl.setText(getTowPoint(dataMap.get("FDecimal")));//..
        viewHolder.tv_djhs.setText(getTowPoint(dataMap.get("FDecimal1")));//..
        viewHolder.tv_jehs.setText(getTowPoint(dataMap.get("FAmount2")));//..
        viewHolder.tv_se.setText(getTowPoint(dataMap.get("FAmount")));//..
        viewHolder.tv_rmbbhse.setText(getTowPoint(dataMap.get("FAmount3")));//..
        viewHolder.tv_slpercent.setText(getTowPoint(dataMap.get("FAmount10")));//..
        viewHolder.tv_fz.setText(dataMap.get("FName16"));
        viewHolder.tv_fl.setText(getTowPoint(dataMap.get("FDecimal2")));//..
        viewHolder.tv_bz.setText(dataMap.get("FText2"));
        viewHolder.tv_fpswkm.setText(dataMap.get("FName17"));
        //隐藏展示
        viewHolder.lin_open.setTag(i);
        if (i == mOpenItem) {
            viewHolder.view_line.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            viewHolder.lin_sorh.setVisibility(View.VISIBLE);
        } else {
            viewHolder.view_line.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            viewHolder.lin_sorh.setVisibility(View.GONE);
        }
        viewHolder.lin_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //用 currentItem 记录点击位置s
                int tag = (Integer) view.getTag();
                if (tag == mOpenItem) { //再次点击
                    mOpenItem = -1; //给 currentItem 一个无效值
                } else {
                    mOpenItem = tag;
                }
                //通知adapter数据改变需要重新加载
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private class MyViewHolder {
        private View view_line;
        TextView tv_con, tv_date, tv_zdr, tv_sqbm, tv_zrbm, tv_wl, tv_wlyhzh, tv_jhys, tv_yskm, tv_ysye, tv_jl, tv_sl, tv_djhs, tv_jehs, tv_se, tv_rmbbhse, tv_slpercent, tv_fz, tv_fl, tv_bz, tv_fpswkm;
        LinearLayout lin_sorh, lin_open;
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
}
