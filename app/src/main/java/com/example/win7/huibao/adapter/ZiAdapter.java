package com.example.win7.huibao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.YApplication;
import com.example.win7.huibao.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WIN7 on 2018/3/23.
 */

public class ZiAdapter extends BaseAdapter {
    Context                       mContext;
    List<HashMap<String, String>> list;
    List<List>                    mSumBitmapList;
    private int currentItem = -1; //用于记录点击的 Item 的 position，是控制 item 展开的核心
    private int mKind;//1传的是bitmap，2是url；

    public ZiAdapter(Context context, List<HashMap<String, String>> list) {
        this.mContext = context;
        this.list = list;
    }

    public ZiAdapter(Context context, List<HashMap<String, String>> list, List sumBitmapList, int kind) {
        this.mContext = context;
        this.list = list;
        this.mSumBitmapList = sumBitmapList;
        this.mKind = kind;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_zi, null);
            holder = new Holder();
            holder.recview_show = view.findViewById(R.id.recview_show);
            holder.tv_qi = (TextView) view.
                    findViewById(R.id.tv_qi);
            holder.tv_zhi = (TextView) view.
                    findViewById(R.id.tv_zhi);
            holder.tv_progress = (TextView) view.
                    findViewById(R.id.tv_progress);
            holder.tv_progress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.tv_plan = (TextView) view.
                    findViewById(R.id.tv_plan);
            holder.tv_budget = (TextView) view.
                    findViewById(R.id.tv_budget);
            holder.tv_pbudget = (TextView) view.
                    findViewById(R.id.tv_pbudget);
            holder.tv_note = (TextView) view.
                    findViewById(R.id.tv_note);
            holder.tv_shuliang = (TextView) view
                    .findViewById(R.id.tv_shuliang);
            holder.tv_danjia = (TextView) view
                    .findViewById(R.id.tv_danjia);
            holder.tv_hanshui = (TextView) view
                    .findViewById(R.id.tv_hanshui);
            holder.tv_buhan = (TextView) view
                    .findViewById(R.id.tv_buhan);
            holder.tv_fuzhu = (TextView) view
                    .findViewById(R.id.tv_fuzhu);
            holder.tv_fuliang = (TextView) view
                    .findViewById(R.id.tv_fuliang);
            holder.tv_fasong = (TextView) view
                    .findViewById(R.id.tv_fasong);
            holder.tv_huikui = (TextView) view
                    .findViewById(R.id.tv_huikui);
            holder.tv_pingfen = (TextView) view
                    .findViewById(R.id.tv_pingfen);
            holder.ll_buhan = (LinearLayout) view
                    .findViewById(R.id.ll_buhan);
            holder.ll_show = (LinearLayout) view
                    .findViewById(R.id.ll_show);
            holder.ll_hide = (LinearLayout) view
                    .findViewById(R.id.ll_hide);
            holder.ll_note = (LinearLayout) view.
                    findViewById(R.id.ll_note);
            holder.ll_a = (LinearLayout) view.
                    findViewById(R.id.ll_a);
            holder.ll_c = (LinearLayout) view.
                    findViewById(R.id.ll_c);
            holder.ll_b = (LinearLayout) view.
                    findViewById(R.id.ll_b);
            holder.ll_d = (LinearLayout) view.
                    findViewById(R.id.ll_d);
            holder.ll_e = (LinearLayout) view.
                    findViewById(R.id.ll_e);
            holder.tv_a = (TextView) view.
                    findViewById(R.id.tv_a);
            holder.tv_b = (TextView) view.
                    findViewById(R.id.tv_b);
            holder.tv_c = (TextView) view.
                    findViewById(R.id.tv_c);
            holder.tv_d = (TextView) view.
                    findViewById(R.id.tv_d);
            holder.tv_e = (TextView) view.
                    findViewById(R.id.tv_e);
            if (YApplication.fgroup.contains("仓储")) {
                holder.tv_danjia.setVisibility(View.INVISIBLE);
                holder.tv_hanshui.setVisibility(View.INVISIBLE);
                holder.ll_buhan.setVisibility(View.GONE);
            }
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        final HashMap<String, String> item = list.get(i);
        holder.ll_show.setTag(i);
        holder.tv_qi.setText(item.get("qi"));
        holder.tv_zhi.setText(item.get("zhi"));
        holder.tv_progress.setText(item.get("progress"));
        holder.tv_plan.setText(item.get("plan"));
        holder.tv_budget.setText(item.get("budget"));
        holder.tv_pbudget.setText(item.get("pbudget"));
        holder.tv_note.setText(item.get("note"));
        holder.tv_shuliang.setText("数量：" + item.get("shuliang"));
        holder.tv_danjia.setText("单价：" + item.get("danjia"));
        holder.tv_hanshui.setText("金额含税：" + item.get("hanshui"));
        holder.tv_buhan.setText(item.get("buhan"));
        holder.tv_fuzhu.setText(item.get("fuzhu"));
        holder.tv_fuliang.setText(item.get("fuliang"));
        holder.tv_fasong.setText(item.get("fasong"));
        holder.tv_huikui.setText(item.get("huikui"));
        holder.tv_pingfen.setText(item.get("pingfen"));
        String a = item.get("a");
        String b = item.get("b");
        String c = item.get("c");
        String d = item.get("d");
        String e = item.get("e");
//        List<String> l = new ArrayList<>();
//        if (!TextUtils.isEmpty(a)) {
//            l.add(a);
//        }
//        if (!TextUtils.isEmpty(b)) {
//            l.add(b);
//        }
//        if (!TextUtils.isEmpty(c)) {
//            l.add(c);
//        }
//        if (!TextUtils.isEmpty(d)) {
//            l.add(d);
//        }
//        if (!TextUtils.isEmpty(e)) {
//            l.add(e);
//        }
//        switch (l.size()) {
//            case 0:
//                holder.ll_a.setVisibility(View.GONE);
//                holder.ll_b.setVisibility(View.GONE);
//                holder.ll_c.setVisibility(View.GONE);
//                holder.ll_d.setVisibility(View.GONE);
//                holder.ll_e.setVisibility(View.GONE);
//                break;
//            case 1:
//                holder.tv_a.setText(a);
//                if (item.get("qr1").equals("True")) {
//                    holder.tv_a.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.ll_b.setVisibility(View.INVISIBLE);
//                holder.ll_c.setVisibility(View.GONE);
//                holder.ll_d.setVisibility(View.GONE);
//                holder.ll_e.setVisibility(View.GONE);
//                break;
//            case 2:
//                holder.tv_a.setText(a);
//                if (item.get("qr1").equals("True")) {
//                    holder.tv_a.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.tv_b.setText(b);
//                if (item.get("qr2").equals("True")) {
//                    holder.tv_b.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.ll_c.setVisibility(View.GONE);
//                holder.ll_d.setVisibility(View.GONE);
//                holder.ll_e.setVisibility(View.GONE);
//                break;
//            case 3:
//                holder.tv_a.setText(a);
//                if (item.get("qr1").equals("True")) {
//                    holder.tv_a.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.tv_b.setText(b);
//                if (item.get("qr2").equals("True")) {
//                    holder.tv_b.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.tv_c.setText(c);
//                if (item.get("qr3").equals("True")) {
//                    holder.tv_c.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.ll_d.setVisibility(View.INVISIBLE);
//                holder.ll_e.setVisibility(View.GONE);
//                break;
//            case 4:
//                holder.tv_a.setText(a);
//                if (item.get("qr1").equals("True")) {
//                    holder.tv_a.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.tv_b.setText(b);
//                if (item.get("qr2").equals("True")) {
//                    holder.tv_b.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.tv_c.setText(c);
//                if (item.get("qr3").equals("True")) {
//                    holder.tv_c.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.tv_d.setText(d);
//                if (item.get("qr4").equals("True")) {
//                    holder.tv_d.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
//                }
//                holder.ll_e.setVisibility(View.GONE);
//                break;
//            case 5:
                holder.tv_a.setText(a);
                if (Utils.BooleantoNum(item.get("qr1")).equals("1")) {
                    holder.tv_a.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
                }
                holder.tv_b.setText(b);
                if (Utils.BooleantoNum(item.get("qr2")).equals("1")) {
                    holder.tv_b.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
                }
                holder.tv_c.setText(c);
                if (Utils.BooleantoNum(item.get("qr3")).equals("1")) {
                    holder.tv_c.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
                }
                holder.tv_d.setText(d);
                if (Utils.BooleantoNum(item.get("qr4")).equals("1")) {
                    holder.tv_d.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
                }
                holder.tv_e.setText(e);
                if (Utils.BooleantoNum(item.get("qr5")).equals("1")) {
                    holder.tv_e.setTextColor(mContext.getResources().getColor(R.color.bottom_button_text_green));
                }
//                break;
//        }

        if (currentItem == i) {
            holder.ll_hide.setVisibility(View.VISIBLE);
        } else {
            holder.ll_hide.setVisibility(View.GONE);
        }

        holder.ll_show.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //用 currentItem 记录点击位置s
                int tag = (Integer) view.getTag();
                if (tag == currentItem) { //再次点击
                    currentItem = -1; //给 currentItem 一个无效值
                } else {
                    currentItem = tag;
                }
                //通知adapter数据改变需要重新加载
                notifyDataSetChanged(); //必须有的一步
            }
        });


        holder.ll_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = new TextView(mContext);
                tv.setText(item.get("note"));
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setView(tv).show();
            }
        });

        holder.tv_fasong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = new TextView(mContext);
                tv.setText(item.get("fasong"));
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setView(tv).show();
            }
        });

        holder.tv_huikui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = new TextView(mContext);
                tv.setText(item.get("huikui"));
                tv.setTextSize(16);
                tv.setPadding(60, 20, 40, 10);
                new AlertDialog.Builder(mContext).setView(tv).show();
            }
        });
        if (mKind == 1) {//bitmapList放入的是图片
            if (null != mSumBitmapList && mSumBitmapList.size() > 0) {
                //填充图片
                List<Bitmap> bitmapList = (ArrayList) mSumBitmapList.get(i);
                GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
                RecViewShowAdapter showAdapter = new RecViewShowAdapter(mContext, bitmapList);
                holder.recview_show.setLayoutManager(mLayoutManager);
                holder.recview_show.setAdapter(showAdapter);
            }
        } else if (mKind == 2) {//bitmapList放入的是url地址
            if (null != mSumBitmapList && mSumBitmapList.size() > 0) {
                List<String> urlList = (ArrayList) mSumBitmapList.get(i);
                GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
                RecViewShowAdapter showAdapter = new RecViewShowAdapter(mContext, urlList);
                holder.recview_show.setLayoutManager(mLayoutManager);
                holder.recview_show.setAdapter(showAdapter);
            }
        }
        return view;
    }

    class Holder {
        LinearLayout ll_show, ll_hide, ll_note, ll_a, ll_b, ll_c, ll_d, ll_e, ll_buhan;
        TextView tv_progress, tv_plan, tv_budget,
                tv_pbudget, tv_note, tv_shuliang, tv_danjia, tv_hanshui,
                tv_buhan, tv_fuzhu, tv_fuliang, tv_fasong,
                tv_huikui, tv_pingfen, tv_qi, tv_zhi,
                tv_a, tv_b, tv_c, tv_d, tv_e;
        RecyclerView recview_show;
    }
}
