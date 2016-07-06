package com.qcsh.fuxiang.ui.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
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
public class ResettingActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText;
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private EditText editTel;
    private EditText editPSW;
    private EditText editKey;
    private TextView sendKey;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login_reset_byidentity);
        setContentView(R.layout.login_register_byphone);
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
        rightBtn.setText("确定");
        title.setText("重置密码");
        rightBtn.setOnClickListener(this);
    }

    private void initView() {
//        editText = (EditText)findViewById(R.id.ic_identy);

        editTel = (EditText) findViewById(R.id.ic_phone);
        editPSW = (EditText) findViewById(R.id.ic_psw);
        editKey = (EditText) findViewById(R.id.ic_key);
        sendKey = (TextView) findViewById(R.id.send_key);
        sendKey.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                    UIHelper.ToastMessage(ResettingActivity.this, "请输入正确的手机号！");
                }
                break;
        }
    }

    private void gotoNext() {
        final String tel = editTel.getText().toString();
        String psw = editPSW.getText().toString();
        String key = editKey.getText().toString();
        if (StringUtils.isMobileNO(tel)) {
            if (psw.length() < 6 || psw.length() > 16) {
                UIHelper.ToastMessage(ResettingActivity.this, "请输入正确的密码(6–16位)");
            } else {
                if (!StringUtils.isEmpty(key)) {
                    jump(tel, psw, key);
                } else {
                    UIHelper.ToastMessage(ResettingActivity.this, "请输入验证码！");
                }
            }
        } else {
            UIHelper.ToastMessage(ResettingActivity.this, "请输入正确的手机号！");
        }
    }

    private void jump(String tel, String psw, String key) {
        showProgress();
        ApiClient.post(ResettingActivity.this, ApiConfig.SYS_RESET, new ApiResponseHandler<User>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(ResettingActivity.this, errorInfo.getMessage());
                if ("10081".equals(errorInfo.getCode()))
                    AppIntentManager.startLoginNewActivity(ResettingActivity.this);
            }
        }, "phone", tel, "pwd", psw, "code", key);
    }

    private void sendKey() {

        ApiClient.post(ResettingActivity.this, ApiConfig.SYS_SENDKEY, new ApiResponseHandler<BaseEntity>() {
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
                UIHelper.ToastMessage(ResettingActivity.this, errorInfo.getMessage());
            }
        }, "phone", editTel.getText().toString());
    }
}
