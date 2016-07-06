package com.qcsh.fuxiang.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.MyConfirmDialog;

import java.util.List;

/**
 * Created by wo on 15/9/7.
 */
public class RegisterByPhoneActivity extends BaseActivity implements View.OnClickListener {

    private EditText editTel;
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private EditText editPSW;
    private EditText editKey;
    private TextView sendKey;
    private int from;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_byphone);
        from = getIntent().getIntExtra("from", 0);
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
        rightBtn.setVisibility(View.VISIBLE);
        if (from == 1) {
            rightBtn.setText("完成");
            title.setText("绑定手机号");
        } else {
            title.setText("注册");
            rightBtn.setText("继续");
        }
        rightBtn.setOnClickListener(this);
    }

    private void initView() {
        editTel = (EditText) findViewById(R.id.ic_phone);
        editPSW = (EditText) findViewById(R.id.ic_psw);
        editKey = (EditText) findViewById(R.id.ic_key);
        sendKey = (TextView) findViewById(R.id.send_key);
        sendKey.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_bar_action:
                gotoNext();
                break;
            case R.id.send_key:
                if (StringUtils.isMobileNO(editTel.getText().toString())) {
                    MyConfirmDialog dialog = new MyConfirmDialog(this, "确认手机号", "我们将发送验证码到该手机：" + editTel.getText().toString(),
                            new MyConfirmDialog.OnCancelDialogListener() {
                                @Override
                                public void onCancel() {
                                }
                            },
                            new MyConfirmDialog.OnConfirmDialogListener() {
                                @Override
                                public void onConfirm() {
                                    sendKey();
                                }
                            }
                    );
                    dialog.show();
                } else {
                    UIHelper.ToastMessage(RegisterByPhoneActivity.this, "请输入正确的手机号！");
                }
                break;
        }

    }

    private void sendKey() {

        ApiClient.post(RegisterByPhoneActivity.this, ApiConfig.SYS_SENDKEY, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {

            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                if (Integer.valueOf(errorInfo.getCode()) == 10000) {
                    new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            sendKey.setBackgroundResource(R.drawable.gray_button_5dp);
                            sendKey.setText(millisUntilFinished / 1000 + "秒");
                            sendKey.setEnabled(false);
                        }

                        public void onFinish() {
                            sendKey.setBackgroundResource(R.drawable.green_button);
                            sendKey.setText("获取验证码");
                            sendKey.setEnabled(true);
                        }
                    }.start();
                }
                UIHelper.ToastMessage(RegisterByPhoneActivity.this, errorInfo.getMessage());
            }
        }, "phone", editTel.getText().toString());
    }

    private void gotoNext() {
        final String tel = editTel.getText().toString();
        String psw = editPSW.getText().toString();
        String key = editKey.getText().toString();
        if (StringUtils.isMobileNO(tel)) {
            if (psw.length() < 6 || psw.length() > 16) {
                UIHelper.ToastMessage(RegisterByPhoneActivity.this, "请输入正确的密码(6–16位)");
            } else {
                if (!StringUtils.isEmpty(key)) {
                    if (from == 1) {
                        showProgress("绑定中...");
                        bind("phone", tel, psw, key);
                    } else {
                        doRegister(tel,psw,key);
                        //AppIntentManager.startRegisterBabyActivity(RegisterByPhoneActivity.this/*, tel, psw, key*/);
                    }
                } else {
                    UIHelper.ToastMessage(RegisterByPhoneActivity.this, "请输入验证码！");
                }
            }
        } else {
            UIHelper.ToastMessage(RegisterByPhoneActivity.this, "请输入正确的手机号！");
        }
    }

    private void bind(String bindkey, String bindvalue, String password, String code) {
        ApiClient.post(RegisterByPhoneActivity.this, ApiConfig.BIND_NUMBER, new ApiResponseHandler<User>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                User user = (User) entity.data.get(0);
                getAppContext().cacheUserInfo(user);
                Intent intent = new Intent(RegisterByPhoneActivity.this, Activity.class);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(RegisterByPhoneActivity.this, errorInfo.getMessage());
            }
        }, "userid", getCurrentUser().getId(), "bindkey", bindkey, "bindvalue", bindvalue, "password", password, "code", code);
    }

    private void doRegister(String tel, String psw, String key) {
        showProgress();
        ApiClient.post(RegisterByPhoneActivity.this, ApiConfig.SYS_REGISTER, new ApiResponseHandler<User>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List data = entity.data;
                User user = (User) data.get(0);
                getAppContext().cacheUserInfo(user);
                AppIntentManager.startRegisterBabyActivity(RegisterByPhoneActivity.this,0);
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(RegisterByPhoneActivity.this, errorInfo.getMessage());
            }
        }, "phone", tel, "password", psw, "code", key);
    }
}
