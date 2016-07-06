package com.qcsh.fuxiang.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.MyBangBiAdapter;
import com.qcsh.fuxiang.ui.BaseActivity;

/**
 * Created by wo on 15/9/22.
 */
public class HomeBangBiMoreActivity extends BaseActivity {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ListView listView;
    private MyBangBiAdapter adapter;

    public static HomeMyBangBiActivity newInstance() {
        return new HomeMyBangBiActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_bangbidetail);
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
        title.setText("帮币明细");
    }

    private void initView() {


        adapter = new MyBangBiAdapter(HomeBangBiMoreActivity.this);
        listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}
