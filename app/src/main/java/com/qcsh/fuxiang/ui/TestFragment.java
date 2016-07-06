package com.qcsh.fuxiang.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.widget.JJBLiveView;

/**
 * Created by Administrator on 2015/8/11.
 */
public class TestFragment extends BaseFragment {


    View view;
    JJBLiveView jjbLiveView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.test,container,false);
        initView();
        return view;
    }

    private void initView(){
        jjbLiveView = (JJBLiveView)view.findViewById(R.id.video_view);

    }

    @Override
    public void onResume() {
        super.onResume();
        String path =AppConfig.FILE_SERVER + "/videos/2015/10/1444701619322.mp4";
        jjbLiveView.startPlay(path);
    }

    @Override
    public void onPause() {
        super.onPause();
        jjbLiveView.stopPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
