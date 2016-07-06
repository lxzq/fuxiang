package com.qcsh.fuxiang.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.AppStart;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.umeng.message.UmengRegistrar;
import com.yixia.camera.demo.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by wo on 15/9/8.
 */
public class LoginNewActivity extends BaseActivity {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private EditText phoneText;
    private EditText passwordText;
    private Button loginBtn;
    private TextView resetText;
    private AppContext context;
    private String device_token;//友盟消息推送 token

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context=(AppContext)getApplication();

        SharedPreferences spf = getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
        device_token = spf.getString(AppStart.UM_TOKEN,"");

        initToolBar();
        initView();
    }

    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        title = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.INVISIBLE);
        title.setText("登录");

    }

    private void initView() {

        phoneText = (EditText)findViewById(R.id.ic_phonenumber);
        passwordText = (EditText)findViewById(R.id.ic_password);
        User user = context.getCacheLoginInfo();

        if(null != user){
            phoneText.setText(user.getPhone());
            passwordText.setText(user.getPassword());
        }

        loginBtn = (Button)findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress("正在登录..");
                ApiClient.post(LoginNewActivity.this, ApiConfig.SYS_LOGIN, new ApiResponseHandler<User>() {
                    @Override
                    public void onSuccess(DataEntity entity) {
                        doSuccess(entity);
                    }

                    @Override
                    public void onFailure(ErrorEntity errorInfo) {
                        closeProgress();
                        ToastUtils.showLongToast(LoginNewActivity.this, errorInfo.getMessage());
                    }
                }, "phone", phoneText.getText().toString(), "password", passwordText.getText().toString(), "umtoken", device_token);
            }
        });

        resetText = (TextView)findViewById(R.id.ic_fogetpassword);
        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppIntentManager.startResetActivity(LoginNewActivity.this);
            }
        });
    }

    private void doSuccess(DataEntity entity){
        closeProgress();
        User user = (User) entity.data.get(0);
        String childInfo = user.getChild_info();
        if (!TextUtils.isEmpty(childInfo)) {
            try {
                JSONArray jsonArray = new JSONArray(childInfo);
                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    user.setChildId(jsonObject.getString("child_id"));
                }
            } catch (Exception e) {
            }
        }
        context.cacheUserInfo(user);
        AppIntentManager.startHomeActivity(this);
    }
}
