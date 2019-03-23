package com.mad.trafficclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.mad.trafficclient.login.LoginActivity;

public class GuideActivity extends Activity {


    // 本地sp标识
    private final static String SP_FIRST = "sp_first";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIfFirst()) {
            goToLogin();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        RelativeLayout guide_RL = (RelativeLayout) findViewById(R.id.guide_RL);
        guide_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPreferences(Context.MODE_PRIVATE).edit().putBoolean(SP_FIRST, false).apply();
                goToLogin();
            }
        });
    }

    /**
     * 是否第一次
     * @return true or false
     */
    private boolean checkIfFirst() {
        return getPreferences(Context.MODE_PRIVATE).getBoolean(SP_FIRST, true);
    }

    /**
     * 进入登录界面
     */
    private void goToLogin() {
        Intent intent = new Intent(GuideActivity.this,
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
