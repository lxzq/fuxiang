package com.qcsh.fuxiang.ui.home;


import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.qcsh.fuxiang.R;

import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.umengmessage.MyPushIntentService;

import org.json.JSONObject;

/**
 * 邀请好友验证结果
 * Created by Administrator on 2015/9/24.
 */
public class FriendsVerificationResultActivity extends BaseActivity {


    private ImageButton action_bar_back;
    private TextView action_bar_title;
    private Button action_bar_action;
    private TextView resultV;

    private String push_message;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_verication_result_view);
        push_message = getIntent().getStringExtra(MyPushIntentService.PUSH_MESSAGE);
        initBar();
        initView();
    }

    private void initBar(){
        action_bar_back = (ImageButton)findViewById(R.id.action_bar_back);
        action_bar_title= (TextView)findViewById(R.id.action_bar_title);
        action_bar_action = (Button)findViewById(R.id.action_bar_action);

        action_bar_back.setVisibility(View.VISIBLE);
        action_bar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        action_bar_title.setText("邀友验证");
        action_bar_action.setVisibility(View.GONE);
    }

    private void initView(){
        resultV = (TextView)findViewById(R.id.result);
        setData();
    }

    private void setData(){
        try{
            JSONObject jsonObject = new JSONObject(push_message);
            String nickname = jsonObject.getString("nickname");
            String result = jsonObject.getString("result");
            if(FriendsVerificationActivity.YES.equals(result))
            resultV.setText("【"+nickname+"】同意请求.");
            else{
            resultV.setText("【"+nickname+"】拒绝请求.");
            }
        }catch (Exception e){

        }
    }
}
