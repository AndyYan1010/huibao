package com.example.win7.huibao.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.win7.huibao.R;
import com.example.win7.huibao.adapter.CameraIdSpAdapter;
import com.example.win7.huibao.util.Consts;
import com.example.win7.huibao.util.ProgressDialogUtil;
import com.example.win7.huibao.util.SoapUtil;
import com.example.win7.huibao.util.ToastUtils;
import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JiankongActivity extends BaseActivity {
    private Toolbar    toolbar;
    private EZUIPlayer mPlayer;
    private Spinner    mSp_camid;
    private String cameraID = "107910292";
    private List<String> mIdData;//存放摄像头ID
    private String token = "at.9bnl7xjt8rpqsd9t0iwgldl48ijivya9-73rejh53j4-0vp0wbc-cen7sk5pi";
    private CameraIdSpAdapter spAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiankong);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTool();
        setViews();
        setData();

    }

    protected void setTool() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle(getResources().getString(R.string.jiankong));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setViews() {
        mSp_camid = (Spinner) findViewById(R.id.sp_camid);
        //获取EZUIPlayer实例
        mPlayer = (EZUIPlayer) findViewById(R.id.player_ui);

        //获取应用在萤石云上的token
        getTokenFromYSY();
    }

    private void getTokenFromYSY() {
        ProgressDialogUtil.startShow(JiankongActivity.this,"正在连接，请稍等...");
        String getTokenUrl = "select token from z_token where id=1";
        ItemTask itemTask = new ItemTask(getTokenUrl);
        itemTask.execute();
    }

    private void setData() {
        mIdData = new ArrayList();
        mIdData.add("107910291");
        mIdData.add("107910292");

        spAdapter = new CameraIdSpAdapter(JiankongActivity.this, mIdData);
        mSp_camid.setAdapter(spAdapter);
        ProgressDialogUtil.startShow(JiankongActivity.this,"正在查询设备ID，请稍等...");
        String searchIDUrl = "select fnumber from t_Item where FItemClassID=3015";
        ItemIDTask itemIDTask = new ItemIDTask(searchIDUrl);
        itemIDTask.execute();
    }

    private void initCamera() {
        //初始化EZUIKit
        EZUIKit.initWithAppKey(getApplication(), "d55fc8eefc784b9db9acbacd1bab29a2");
        //设置授权token
        EZUIKit.setAccessToken(token);
        //设置播放回调callback
        mPlayer.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {

            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {
                String errorString = ezuiError.getErrorString();
                Toast.makeText(JiankongActivity.this, errorString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVideoSizeChange(int i, int i1) {

            }

            @Override
            public void onPrepared() {
                //回放文件列表
                //List<EZRecordFile> playList = mPlayer.getPlayList();
                mPlayer.startPlay();
            }

            @Override
            public void onPlayTime(Calendar calendar) {

            }

            @Override
            public void onPlayFinish() {
                Toast.makeText(JiankongActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
            }
        });
        //设置播放参数
        mPlayer.setUrl("ezopen://open.ys7.com/" + cameraID + "/1.hd.live");

        //加载中显示的UI
        //创建loadingview
        //        ProgressBar mLoadView = new ProgressBar(JiankongActivity.this);
        //        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        //        mLoadView.setLayoutParams(lp);
        //        //设置loadingview
        //        mPlayer.setLoadingView(mLoadView);
        setListeners();
    }

    protected void setListeners() {
        mSp_camid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String camID = mIdData.get(i);
                //设置播放参数
                mPlayer.setUrl("ezopen://open.ys7.com/" + camID + "/1.hd.live");
                mPlayer.startPlay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mPlayer) {
            mPlayer.startPlay();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止播放
        mPlayer.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        mPlayer.releasePlayer();
    }

    class ItemTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Document doc = DocumentHelper.parseText(s);
                Element ele = doc.getRootElement();
                Iterator iter = ele.elementIterator("Cust");
                HashMap<String, String> map = new HashMap<>();
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    map.put("token", recordEle.elementTextTrim("token"));//物料内码(提交订单用)
                }
                //填充数据到页面
                String tokenid = map.get("token");
                if (tokenid.startsWith("at.")) {
                    token = tokenid;
                }
                //初始化摄像头
                initCamera();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(JiankongActivity.this, "未查获取到token");
                finish();
            }
            ProgressDialogUtil.hideDialog();
        }
    }

    /*查询设备序列号
    *select fnumber from t_Item where FItemClassID=3015
    * */
    class ItemIDTask extends AsyncTask<Void, String, String> {
        String sql;

        ItemIDTask(String sql) {
            this.sql = sql;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Map<String, String> map = new HashMap<>();
            map.put("FSql", sql);
            map.put("FTable", "t_icitem");
            return SoapUtil.requestWebService(Consts.JA_select, map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Document doc = DocumentHelper.parseText(s);
                Element ele = doc.getRootElement();
                Iterator iter = ele.elementIterator("Cust");
                HashMap<String, String> map = new HashMap<>();
                mIdData.clear();
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    map.put("fnumber", recordEle.elementTextTrim("fnumber"));//物料内码(提交订单用)
                    mIdData.add(recordEle.elementTextTrim("fnumber"));
                }
                int size = mIdData.size();
                if (size <= 0) {
                    ToastUtils.showToast(JiankongActivity.this, "未查取到摄像头ID");
                    finish();
                }
                spAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showToast(JiankongActivity.this, "查询出错，未查取到摄像头ID");
            }
            ProgressDialogUtil.hideDialog();
        }
    }
}
