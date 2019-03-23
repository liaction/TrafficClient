package com.mad.trafficclient.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mad.trafficclient.R;
import com.mad.trafficclient.util.LoadingDialog;
import com.mad.trafficclient.util.UrlBean;
import com.mad.trafficclient.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseFragment extends Fragment {
    protected View mLayoutView;
    protected UrlBean mUrlBean;
    protected String mUserName;
    protected String mUrlBase;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != getView()) {
            mLayoutView = getView().findViewById(R.id.mLayoutError);
        }
        mUrlBean = Util.loadSetting(getActivity());
        mUserName = Util.getUser(getActivity());
        mUrlBase = "http://" + mUrlBean.getUrl() + ":" + mUrlBean.getPort() + "/api/v2/";
        initViews();
    }

    protected void initViews() {

    }

    protected void showErrorLayout() {
        if (null != mLayoutView) {
            mLayoutView.setVisibility(View.VISIBLE);
        }
    }

    protected void doRequestData(String lastUrl, JSONObject params,
                                 boolean canShowDialog,
                                 final boolean canHideDialog,
                                 final RequestCallback callback) {
        doRequestData(lastUrl, params, canShowDialog, canHideDialog, true, callback);
    }

    protected void doRequestData(String lastUrl, JSONObject params,
                                 boolean canShowDialog,
                                 final boolean canHideDialog,
                                 final boolean showErrorLayout,
                                 final RequestCallback callback) {
        try {
            params.put("UserName", mUserName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String strUrl = mUrlBase + lastUrl;
        if (canShowDialog) {
            LoadingDialog.showDialog(getActivity());
        }
        RequestQueue mQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, strUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG", response.toString());
                if (canHideDialog) {
                    LoadingDialog.disDialog();
                }
                if (response.optString("RESULT").equals("F")) {
                    if (showErrorLayout) {
                        showErrorLayout();
                        return;
                    }
                    showErrorTip();
                    return;
                }
                if (response.optString("RESULT").equals("S")) {
                    if (null != callback) {
                        callback.success(response);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingDialog.disDialog();
                if (showErrorLayout) {
                    showErrorLayout();
                    return;
                }
                showErrorTip();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    protected void hideDialog() {
        hideDialog(true);
    }

    protected void hideDialog(boolean showErrorLayout) {
        LoadingDialog.disDialog();
        if (showErrorLayout) {
            showErrorLayout();
        }
    }

    protected void showErrorTip() {
        showErrorTip("请求失败,请稍后重试");
    }

    protected void showErrorTip(String tip) {
        if (null == tip || TextUtils.isEmpty(tip = tip.trim()) || tip.equals(" ")) {
            return;
        }
        Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
    }

    protected interface RequestCallback {
        void success(JSONObject response);
    }
}
