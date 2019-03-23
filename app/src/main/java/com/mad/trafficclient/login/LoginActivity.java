package com.mad.trafficclient.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mad.trafficclient.MainActivity;
import com.mad.trafficclient.R;
import com.mad.trafficclient.util.LoadingDialog;
import com.mad.trafficclient.util.UrlBean;
import com.mad.trafficclient.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ApplySharedPref")
public class LoginActivity extends Activity {

    private UrlBean urlBean;
    private String urlHttp;
    private String urlPort = "8080";

    EditText etUserName, etUserPwd;
    Button btLogin, btReg;
    private TextView btNetSetting;

    private CheckBox mAutoLogin;
    private CheckBox mRememberPwd;

    private SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mPreference = getSharedPreferences("tc", Context.MODE_PRIVATE);
        etUserName = (EditText) findViewById(R.id.et_user_name);
        etUserPwd = (EditText) findViewById(R.id.et_user_pwd);
        btLogin = (Button) findViewById(R.id.bt_login);
        btReg = (Button) findViewById(R.id.bt_reg);
        btNetSetting = (TextView) findViewById(R.id.bt_net_setting);
        mAutoLogin = (CheckBox) findViewById(R.id.autologCB);
        mRememberPwd = (CheckBox) findViewById(R.id.jzpwdCB);
        urlBean = Util.loadSetting(LoginActivity.this);

        initLiserter();

        etUserName.setText(new String(Base64.decode(mPreference.getString("u", ""), Base64.DEFAULT)));
        etUserName.setSelection(etUserName.getText().length());
        if (mPreference.getBoolean("r", false)) {
            mRememberPwd.setChecked(true);
            etUserPwd.setText(new String(Base64.decode(mPreference.getString("p", ""), Base64.DEFAULT)));
            etUserPwd.setSelection(etUserPwd.getText().length());
        }
        if (mPreference.getBoolean("auto", false)) {
            mAutoLogin.setChecked(true);
            btLogin.performClick();
        }
    }

    private void initLiserter() {
        btReg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        RegActivity.class);
                startActivity(intent);
            }
        });

        mRememberPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mAutoLogin.setChecked(false);
                }
            }
        });
        mAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRememberPwd.setChecked(true);
                }
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String userName = etUserName.getText().toString().trim();
                String userPwd = etUserPwd.getText().toString().trim();

                if (TextUtils.isEmpty(userName) || userName.equals(" ")) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(userPwd) || userPwd.equals(" ")) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                mPreference.edit().putString("u", Base64.encodeToString(userName.getBytes(), Base64.DEFAULT)).commit();
                doWhenRememberPwdChange(mRememberPwd.isChecked());
                doWhenAutoLoginChange(mAutoLogin.isChecked());

                LoadingDialog.showDialog(LoginActivity.this);
                JSONObject params = new JSONObject();
                try {
                    params.put("UserName", userName);
                    params.put("UserPwd", userPwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", params.toString());


                String strUrl = "http://" + urlBean.getUrl() + ":" + urlBean.getPort() + "/api/v2/user_login";


                Log.d("TAG", strUrl);

                RequestQueue mQueue = Volley.newRequestQueue(LoginActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, strUrl, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // TODO Auto-generated method stu
                        Log.d("TAG", response.toString());
                        LoadingDialog.disDialog();
                        if (response.optString("RESULT").equals("S")) {
                            Toast.makeText(getApplicationContext(), response.optString("ERRMSG"), Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.optString("RESULT").equals("F")) {
                            Toast.makeText(getApplicationContext(), response.optString("ERRMSG"), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        LoadingDialog.disDialog();
                        Log.d("TAG volley error", error.toString());
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

                    }
                });
                mQueue.add(jsonObjectRequest);
            }
        });

        btNetSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog urlSettingDialog = new Dialog(LoginActivity.this);
                urlSettingDialog.show();
                urlSettingDialog.setTitle("Setting");
                urlSettingDialog.getWindow().setContentView(R.layout.login_setting);
                final EditText edit_urlHttp = (EditText) urlSettingDialog.getWindow().findViewById(R.id.edit_setting_url);
                final EditText edit_urlPort = (EditText) urlSettingDialog.getWindow().findViewById(R.id.edit_setting_port);

                edit_urlHttp.setText(urlBean.getUrl());
                edit_urlPort.setText(urlBean.getPort());
                Button save = (Button) urlSettingDialog.getWindow().findViewById(R.id.save);
                Button cancel = (Button) urlSettingDialog.getWindow().findViewById(R.id.cancel);
                save.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        urlHttp = edit_urlHttp.getText().toString();
                        urlPort = edit_urlPort.getText().toString();

                        if (urlHttp == null || urlHttp.equals("")) {
                            Toast.makeText(LoginActivity.this, R.string.error_ip, Toast.LENGTH_LONG).show();
                        } else {
                            Util.saveSetting(urlHttp, urlPort, LoginActivity.this);
                            urlBean = Util.loadSetting(LoginActivity.this);
                            urlSettingDialog.dismiss();
                        }
                    }
                });
                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        urlSettingDialog.dismiss();
                    }
                });
                urlSettingDialog.show();
            }
        });

    }

    private void doWhenRememberPwdChange(boolean isChecked) {
        SharedPreferences.Editor edit = mPreference.edit();
        edit.putBoolean("r", isChecked);
        if (isChecked) {
            String userName = etUserName.getText().toString().trim();
            String userPwd = etUserPwd.getText().toString().trim();
            edit.putString("u", Base64.encodeToString(userName.getBytes(), Base64.DEFAULT));
            edit.putString("p", Base64.encodeToString(userPwd.getBytes(), Base64.DEFAULT));
            edit.commit();
            return;
        }
        edit.remove("p");
        edit.commit();
    }

    private void doWhenAutoLoginChange(boolean isChecked) {
        SharedPreferences.Editor edit = mPreference.edit();
        edit.putBoolean("auto", isChecked);
        if (isChecked) {
            String userName = etUserName.getText().toString().trim();
            String userPwd = etUserPwd.getText().toString().trim();
            edit.putString("u", Base64.encodeToString(userName.getBytes(), Base64.DEFAULT));
            edit.putString("p", Base64.encodeToString(userPwd.getBytes(), Base64.DEFAULT));
            edit.commit();
            return;
        }
        if (!mRememberPwd.isChecked()) {
            edit.remove("p");
        }
        edit.commit();
    }

}
