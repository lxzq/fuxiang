package com.qcsh.fuxiang.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcsh.fuxiang.AppStart;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.umengmessage.MyPushIntentService;
import com.qcsh.fuxiang.widget.SwitchView;
import com.tencent.open.utils.Util;
import com.umeng.message.PushAgent;
import com.yixia.camera.demo.utils.ToastUtils;

/**
 * 消息提醒设置界面
 * Created by wo on 15/9/16.
 */
public class HomeRemindActivity extends BaseActivity {



    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private SwitchView messageSwitch;

    private SwitchView commentSwitch;
    private SwitchView praiseSwitch;
    private SwitchView haraseSwitch;
    private PushAgent mPushAgent;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_remind);
        mPushAgent = PushAgent.getInstance(HomeRemindActivity.this);
        sharedPreferences = getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
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
        title.setText("消息提醒");
    }

    private void initView() {

        //消息提醒
        messageSwitch = (SwitchView)findViewById(R.id.view_message);

        //如果应用在前台的时候，开发者可以自定义配置是否显示通知，默认情况下，应用在前台是显示通知的。 开发者更改前台通知显示设置后，会根据更改生效。
        messageSwitch.setState(true);
        messageSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                messageSwitch.setState(true);
                ToastUtils.showLongToast(HomeRemindActivity.this, "启用推送消息提醒");
            }

            @Override
            public void toggleToOff(View view) {
                messageSwitch.setState(false);
                ToastUtils.showLongToast(HomeRemindActivity.this, "关闭推送消息提醒");
            }
        });


        //有人评论我
        boolean isComment = sharedPreferences.getBoolean(MyPushIntentService.ADD_COMMENT,true);
        commentSwitch = (SwitchView)findViewById(R.id.view_comment);
        commentSwitch.setState(isComment);
        commentSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                MyPushIntentService.pushMessageComment(getAppContext(),true);
                commentSwitch.setState(true);
            }

            @Override
            public void toggleToOff(View view) {
                MyPushIntentService.pushMessageComment(getAppContext(),false);
                commentSwitch.setState(false);
            }
        });

        //有人赞我
        boolean isZan = sharedPreferences.getBoolean(MyPushIntentService.ADD_ZAN,true);
        praiseSwitch = (SwitchView)findViewById(R.id.view_praise);
        praiseSwitch.setState(isZan);
        praiseSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                MyPushIntentService.pushMessageZan(getAppContext(), true);
                praiseSwitch.setState(true);
            }

            @Override
            public void toggleToOff(View view) {
                MyPushIntentService.pushMessageZan(getAppContext(),false);
                praiseSwitch.setState(false);
            }
        });

        //夜间防骚扰
        boolean isSR = sharedPreferences.getBoolean(MyPushIntentService.SR,false);
        haraseSwitch = (SwitchView)findViewById(R.id.view_noharass);
        //为免过度打扰用户，SDK默认在“23:00”到“7:00”之间收到通知消息时不响铃，不振动，不闪灯。
        haraseSwitch.setState(isSR);
        haraseSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                MyPushIntentService.pushMessageSR(getAppContext(), true);
                mPushAgent.setNoDisturbMode(23, 0, 8, 0);
                haraseSwitch.setState(true);
            }

            @Override
            public void toggleToOff(View view) {
                haraseSwitch.setState(false);
                MyPushIntentService.pushMessageSR(getAppContext(), false);
                mPushAgent.setNoDisturbMode(0,0,0,0);
            }
        });

    }

}
