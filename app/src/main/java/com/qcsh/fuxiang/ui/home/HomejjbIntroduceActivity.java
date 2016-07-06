package com.qcsh.fuxiang.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.BaseActivity;

/**
 * Created by shenbinbin on 15/9/15.
 */
public class HomejjbIntroduceActivity extends BaseActivity{

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_introduce);
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
        title.setText("关于家家帮");
    }

    private void initView() {

    }
}
