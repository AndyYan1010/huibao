package com.example.win7.huibao.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.win7.huibao.R;
import com.example.win7.huibao.util.GlideLoaderUtil;

import java.util.List;

/**
 * @创建者 AndyYan
 * @创建时间 2018/5/16 9:27
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class RecViewShowAdapter extends RecyclerView.Adapter<RecViewShowAdapter.ViewHolder> {
    private Context mContext;
    private List    mData;

    public RecViewShowAdapter(Context context, List data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_recy_item, parent, false);
        // 实例化viewholder
        RecViewShowAdapter.ViewHolder viewHolder = new RecViewShowAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Object o = mData.get(position);
        if (null != o) {
            if (o instanceof Bitmap) {
                holder.img_add_photo.setImageBitmap((Bitmap) o);
            } else if (o instanceof String) {
                GlideLoaderUtil.showImageView(mContext, (String) o, holder.img_add_photo);
            }
        }
        holder.img_delet.setVisibility(View.GONE);
        holder.img_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出popupwindow展示照片
                final PopupWindow popupWindow = new PopupWindow(mContext);
                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.setContentView(LayoutInflater.from(mContext).inflate(R.layout.dialog_photo_view, null));
                popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                popupWindow.setOutsideTouchable(false);
                popupWindow.setFocusable(true);
                //显示popupwindow,并指定位置
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                //找到pic展示条目
                ViewPager viewpager = popupWindow.getContentView().findViewById(R.id.viewpager);
                final TextView tv_title = popupWindow.getContentView().findViewById(R.id.tv_title);
                MyViewPagerAdapter viewPagerAdapter;
                viewPagerAdapter = new MyViewPagerAdapter(mContext, mData, popupWindow);
                viewpager.setAdapter(viewPagerAdapter);
                viewpager.setCurrentItem(position);
                tv_title.setText((position + 1) + "/" + mData.size());
                viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        tv_title.setText((position + 1) + "/" + mData.size());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        //展示条目数
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_add_photo, img_delet;

        public ViewHolder(View itemView) {
            super(itemView);
            img_add_photo = (ImageView) itemView.findViewById(R.id.img_add_photo);
            img_delet = (ImageView) itemView.findViewById(R.id.img_delet);
        }
    }
}
