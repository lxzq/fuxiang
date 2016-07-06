package com.qcsh.fuxiang.ui.look;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.CuttingBitmap;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.XListView;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by wo on 15/8/31.
 */
public class LookInviteFamilyActivity extends BaseActivity  implements View.OnClickListener{


    private LinearLayout iv_Aunt;
    private LinearLayout iv_Others;
    private ImageView faceView;
    private TextView nicknameView;

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_invitefamily);
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
        rightBtn.setVisibility(View.INVISIBLE);
        title.setText("邀请家人");

    }

    private void initView() {
        findViewById(R.id.inviteMother).setOnClickListener(this);
        findViewById(R.id.inviteFather).setOnClickListener(this);
        iv_Aunt = (LinearLayout)findViewById(R.id.inviteAunt);
        iv_Aunt.setOnClickListener(this);
        iv_Others = (LinearLayout)findViewById(R.id.inviteOther);
        iv_Others.setOnClickListener(this);

        faceView = (ImageView)findViewById(R.id.face);
        nicknameView = (TextView)findViewById(R.id.nickname);

        if(null != user){

            if(!TextUtils.isEmpty(user.userface)){
                ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(user.userface),faceView,
                        new SimpleImageLoadingListener(){
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                if(null != loadedImage){
                                    Bitmap roundBitmap = CuttingBitmap.toRoundBitmap(loadedImage);
                                    ((ImageView) view).setImageBitmap(roundBitmap);

                                }
                            }
                        });
            }

            if(!TextUtils.isEmpty(user.nickname)){
                nicknameView.setText(user.nickname);
            }


        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.inviteMother:
                AppIntentManager.startShareFriendActivity(LookInviteFamilyActivity.this);
                break;
            case R.id.inviteFather:
                AppIntentManager.startShareFriendActivity(LookInviteFamilyActivity.this);
                break;
            case R.id.inviteAunt:
               // AppIntentManager.startShareFriendActivity(LookInviteFamilyActivity.this);
                break;
            case R.id.inviteOther:
                AppIntentManager.startShareFriendActivity(LookInviteFamilyActivity.this);
                break;
        }

    }
}
