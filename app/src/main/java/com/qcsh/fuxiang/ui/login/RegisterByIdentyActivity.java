package com.qcsh.fuxiang.ui.login;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.BaseActivity;

/**
 * Created by wo on 15/9/7.
 */
public class RegisterByIdentyActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText;
    private EditText passwordText;
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register_byidenty);
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
        rightBtn.setOnClickListener(this);
        title.setText("注册3/3");
    }

    private void initView() {

        editText = (EditText)findViewById(R.id.ic_identy);
        passwordText = (EditText)findViewById(R.id.ic_password);
    }


    @Override
    public void onClick(View view) {

    }
}
