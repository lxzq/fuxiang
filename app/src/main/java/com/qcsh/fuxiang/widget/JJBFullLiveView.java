package com.qcsh.fuxiang.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;



import com.qcsh.fuxiang.R;


/**
 * 全屏播放
 * Created by Administrator on 2015/10/29.
 */
public class JJBFullLiveView extends Activity{

    private JJBLiveView jjbLiveView;
    private String path;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jjb_full_live);
        path = getIntent().getStringExtra("path");
        initView();
    }

    private void initView(){
        jjbLiveView = (JJBLiveView)findViewById(R.id.video_view);
        jjbLiveView.getFullButton().setText("退出");
        jjbLiveView.getFullButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        jjbLiveView.stopPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jjbLiveView.startPlay(path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
