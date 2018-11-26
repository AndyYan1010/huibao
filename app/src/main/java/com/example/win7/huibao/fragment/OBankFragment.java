package com.example.win7.huibao.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.win7.huibao.R;

/**
 * 网银新增
 */
public class OBankFragment extends Fragment {
    Context context;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        view = inflater.inflate(R.layout.fragment_obank, container, false);
        return view;
    }

}
