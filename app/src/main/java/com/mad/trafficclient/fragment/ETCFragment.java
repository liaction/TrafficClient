package com.mad.trafficclient.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mad.trafficclient.R;


public class ETCFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View mEtcCZ;
    private View mEtcYuE;
    private View mEtcHistory;


    public ETCFragment() {
    }

    @Override
    protected void initViews() {
        super.initViews();
        if (null != getView()) {
            mEtcCZ = getView().findViewById(R.id.mETCCZ);
            mEtcYuE = getView().findViewById(R.id.mETCYuE);
            mEtcHistory = getView().findViewById(R.id.mETCHistory);
            mEtcCZ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doCZ();
                }
            });
            mEtcYuE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doYuE();
                }
            });

            mEtcHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doHistory();
                }
            });
        }
    }

    private void doCZ() {

    }

    private void doYuE() {

    }

    private void doHistory() {

    }

    public static ETCFragment newInstance(String param1, String param2) {
        ETCFragment fragment = new ETCFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_etc, container, false);
    }

}
