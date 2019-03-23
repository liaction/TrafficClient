package com.mad.trafficclient.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.trafficclient.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fragment20 extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageView mLeftImg;
    private TextView mLeftTv;
    private TextView mRightTopTv;
    private LinearLayout mRightBottomLayout;

    @Override
    protected void initViews() {
        super.initViews();
        if (null != getView()) {
            mLeftImg = (ImageView) getView().findViewById(R.id.mLeftImg);
            mLeftTv = (TextView) getView().findViewById(R.id.mLeftTv);
            mRightTopTv = (TextView) getView().findViewById(R.id.mRightTopTv);
            mRightBottomLayout = (LinearLayout) getView().findViewById(R.id.mRightBottomLayout);
        }
    }

    public Fragment20() {
    }

    public static Fragment20 newInstance(String param1, String param2) {
        Fragment20 fragment = new Fragment20();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestUserData();
    }

    private void requestUserData() {
        doRequestData("get_all_user_info", new JSONObject(),
                true,
                false,
                new RequestCallback() {
                    @Override
                    public void success(JSONObject response) {
                        doRequestCarInfo(response);
                    }
                });
    }

    private void doRequestCarInfo(final JSONObject jsonObject) {

        doRequestData("get_car_info", new JSONObject(),
                false,
                false,
                new RequestCallback() {
                    @Override
                    public void success(JSONObject response) {
                        doFillLayout(jsonObject, response);
                    }
                });
    }

    private void doRequestYuE(List<JSONObject> cars) {
        final List<Integer> requestCount = new ArrayList<>();
        requestCount.add(cars.size());
        for (final JSONObject car :
                cars) {
            JSONObject params = new JSONObject();
            try {
                params.put("CarId", car.getString("number"));
                doRequestData("get_car_account_balance", params,
                        false,
                        false,
                        new RequestCallback() {
                            @Override
                            public void success(JSONObject response) {
                                requestCount.add(0, requestCount.get(0) - 1);
                                if (requestCount.get(0) == 0) {
                                    hideDialog(false);
                                }
                                try {
                                    doFillRightBottomLayout(car, response.getString("Balance"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void doFillRightBottomLayout(JSONObject car, String yuE) {
        View itemLayout = LayoutInflater.from(getActivity()).inflate(R.layout.layout_item, null);
        ImageView mItemImg = (ImageView) itemLayout.findViewById(R.id.mItemImg);
        TextView mItemTv = (TextView) itemLayout.findViewById(R.id.mItemTv);

        mItemImg.setImageResource(R.drawable.biaozhi);

        StringBuilder itemString = new StringBuilder();
        try {
            itemString.append(car.getString("carnumber"))
                    .append("\t\t")
                    .append("余额:\t")
                    .append(yuE)
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mItemTv.setText(itemString);
        mRightBottomLayout.addView(itemLayout);
    }


    /**
     * 填充布局
     *
     * @param userInfo 用户信息
     * @param carInfo  车辆信息
     */
    private void doFillLayout(JSONObject userInfo, JSONObject carInfo) {
        StringBuilder userInfoLeft = new StringBuilder();
        JSONObject user = null;
        try {
            JSONArray users = userInfo.getJSONArray("ROWS_DETAIL");
            for (int i = 0; i < users.length(); i++) {
                JSONObject item = users.getJSONObject(i);
                if (TextUtils.equals(item.getString("username"), mUserName)) {
                    user = item;
                    break;
                }
            }
            if (null != user) {
                String pSex = user.getString("psex");
                if (TextUtils.equals("男", pSex)){
                    mLeftImg.setImageResource(R.drawable.touxiang_2);
                }else if (TextUtils.equals("女", pSex)){
                    mLeftImg.setImageResource(R.drawable.touxiang_1);
                }else {
                    // 有可能不止男女两个性别,暂不考虑
                }
                userInfoLeft
                        .append("用户名:\t")
                        .append(user.getString("username"))
                        .append("\n\n")
                        .append("姓名:\t")
                        .append(user.getString("pname"))
                        .append("\n\n")
                        .append("性别:\t")
                        .append(pSex)
                        .append("\n\n")
                        .append("手机:\t")
                        .append(user.getString("ptel"))
                ;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLeftTv.setText(userInfoLeft.toString());

        StringBuilder userInfoRightTop = new StringBuilder();
        if (null != user) {
            try {
                StringBuilder cardId = new StringBuilder(user.getString("pcardid"));
                if (cardId.length() > 6) {
                    for (int i = 0; i < cardId.length(); i++) {
                        if (i >= 6 && i < cardId.length() - 4) {
                            cardId.replace(i, i + 1, "*");
                        }
                    }
                }
                userInfoRightTop.append("身份证号:")
                        .append(cardId)
                        .append("\t\t")
                        .append("注册时间:")
                        .append(user.getString("pregisterdate"))
                ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mRightTopTv.setText(userInfoRightTop);

        // right bottom
        List<JSONObject> cars = new ArrayList<>();
        if (null != user) {
            try {
                JSONArray carsArray = carInfo.getJSONArray("ROWS_DETAIL");
                for (int i = 0; i < carsArray.length(); i++) {
                    JSONObject car = carsArray.getJSONObject(i);
                    if (TextUtils.equals(car.getString("pcardid"), user.getString("pcardid"))) {
                        cars.add(car);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!cars.isEmpty()) {
            doRequestYuE(cars);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment20, container, false);
    }


}
