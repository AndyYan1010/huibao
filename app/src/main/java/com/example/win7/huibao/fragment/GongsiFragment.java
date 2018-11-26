package com.example.win7.huibao.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.win7.huibao.R;

/**
 * 公司新增
 */
public class GongsiFragment extends Fragment {
    Context context;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        view = inflater.inflate(R.layout.fragment_gongsi, container, false);
        return view;
    }

}
