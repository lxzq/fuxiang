package com.qcsh.fuxiang.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.Home.HomePartnerEntity;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;

import java.util.List;

/**
 * Created by wo on 15/9/18.
 */
public class HomeChangePasswordActivity extends BaseActivity {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private EditText oldText;
    private EditText newText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_changepassword);
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
        rightBtn.setText("完成");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
        title.setText("修改密码");
    }

    private void changePassword() {

        if (StringUtils.isEmpty(oldText.getText().toString())){

            UIHelper.ToastMessage(HomeChangePasswordActivity.this,"请输入旧密码");
            return;
        }else if (StringUtils.isEmpty(newText.getText().toString())){

            UIHelper.ToastMessage(HomeChangePasswordActivity.this,"请输入新密码");
            return;
        }

        showProgress();
        ApiClient.post(HomeChangePasswordActivity.this, ApiConfig.EDIT_PASSWORD, new ApiResponseHandler<HomePartnerEntity>() {

            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                UIHelper.ToastMessage(HomeChangePasswordActivity.this, "修改密码成功");
                finish();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();

                UIHelper.ToastMessage(HomeChangePasswordActivity.this, errorInfo.getMessage());
            }

        }, "userId", String.valueOf(getCurrentUser().getId()), "beforepassword", oldText.getText().toString(), "password", newText.getText().toString());
    }

    private void initView(){

        oldText = (EditText)findViewById(R.id.ic_oldpassword);
        newText = (EditText)findViewById(R.id.ic_newpassword);

    }
}
