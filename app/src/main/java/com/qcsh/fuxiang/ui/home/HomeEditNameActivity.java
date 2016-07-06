package com.qcsh.fuxiang.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.yixia.camera.demo.utils.ToastUtils;

/**
 * Created by wo on 15/9/15.
 */
public class HomeEditNameActivity extends BaseActivity {
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private EditText editText;
    private Button clickBtn;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_editname);
        user = getCurrentUser();
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
        rightBtn.setText("保存");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintKbTwo();
                Intent intent = new Intent();
                intent.putExtra("Name", editText.getText().toString().trim());
                setResult(1001, intent);
                finish();
            }
        });

        title.setText("修改昵称");
    }



    private void initView() {


        editText = (EditText)findViewById(R.id.ic_edit);
        clickBtn = (Button)findViewById(R.id.btn_click);
        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editText.getText().clear();
            }
        });

        editText.setText(user.getNickname());
    }

    //此方法只是关闭软键盘
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
