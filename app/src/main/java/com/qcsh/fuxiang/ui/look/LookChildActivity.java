package com.qcsh.fuxiang.ui.look;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.look.LookChildEntity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 带孩子
 * Created by Administrator on 2015/9/17.
 */
public class LookChildActivity extends BaseActivity {

    private ImageButton backB;
    private Button actionB;
    private TextView titleView;

    private TextView hostText,text1,text2,text3;
    private ImageView hostFace,face1,face2,face3;
    private String userId;
    private String child = "3";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_child_view);
        User user = getCurrentUser();
        if(null != user){
            userId = user.id;
            child = user.childId;
        }

        initBar();
        initView();
        lookChildData();

    }

    private void initBar(){
        backB = (ImageButton)findViewById(R.id.action_bar_back);
        actionB = (Button)findViewById(R.id.action_bar_action);
        titleView = (TextView)findViewById(R.id.action_bar_title);
        backB.setVisibility(View.VISIBLE);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleView.setText("带孩子");
        actionB.setVisibility(View.GONE);
    }

    private void initView(){
        hostText = (TextView)findViewById(R.id.text_host);
        text1 = (TextView)findViewById(R.id.text_1);
        text2 = (TextView)findViewById(R.id.text_2);
        text3 = (TextView)findViewById(R.id.text_3);

        hostFace = (ImageView)findViewById(R.id.face_host);
        face1 = (ImageView)findViewById(R.id.face_1);
        face2 = (ImageView)findViewById(R.id.face_2);
        face3 = (ImageView)findViewById(R.id.face_3);
    }

    private void lookChildData(){
        showProgress();
        ApiClient.post(this, ApiConfig.LOOK_CHILD, new ApiResponseHandler<LookChildEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List<Object> datas = entity.data;
                setData(datas);
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(LookChildActivity.this, errorInfo.getMessage());
            }
        }, "chiId", child);
    }

    private void setData(List<Object> datas){
        int family = 0;//爸爸妈妈
        if(null != datas){

            text3.setVisibility(View.INVISIBLE);
            face3.setVisibility(View.INVISIBLE);
            text2.setVisibility(View.INVISIBLE);
            face2.setVisibility(View.INVISIBLE);
            text1.setVisibility(View.INVISIBLE);
            face1.setVisibility(View.INVISIBLE);

            for(int i = 0 ; i < datas.size() ; i++){
                final LookChildEntity lookChildEntity = (LookChildEntity) datas.get(i);
                Bitmap yeyeMap = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_kanzhe_yeye);
                Bitmap nainaiMap = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_kanzhe_nainai);

                String relationName = lookChildEntity.getRelation();
                if("1".equals(relationName)){
                    lookChildEntity.setNickname("爸爸");
                }else if("2".equals(relationName)){
                    lookChildEntity.setNickname("妈妈");
                }else if("3".equals(relationName)){
                    lookChildEntity.setNickname("爷爷");
                    family = 1;//爷爷
                }else if("4".equals(relationName)){
                    lookChildEntity.setNickname("奶奶");
                    family = 2;//奶奶
                }else if("5".equals(relationName) || "6".equals(relationName)){//外公 外婆
                    continue;
                }
                String isNurse = lookChildEntity.getIsNurse();
                if("0".equals(isNurse)){//看护人
                    hostText.setText(lookChildEntity.getNickname());
                    if(!TextUtils.isEmpty(lookChildEntity.getFace()) && lookChildEntity.getFace().trim().length() > 5){
                        ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_xx(lookChildEntity.getFace()),hostFace);
                    }else{
                        if(family == 1){
                            hostFace.setImageBitmap(yeyeMap);
                        }else if(family == 2){
                            hostFace.setImageBitmap(nainaiMap);
                        }
                    }

                }else{

                   int vis1 =  text1.getVisibility();
                   int vis2 =  text2.getVisibility();

                   if(vis1 == View.INVISIBLE){
                        text1.setVisibility(View.VISIBLE);
                        face1.setVisibility(View.VISIBLE);
                        text1.setText(lookChildEntity.getNickname());
                        if(!TextUtils.isEmpty(lookChildEntity.getFace()) && lookChildEntity.getFace().trim().length() > 5) {
                            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(lookChildEntity.getFace()), face1);
                        }else{
                            if(family == 1){
                                face1.setImageBitmap(yeyeMap);
                            }else if(family == 2){
                                face1.setImageBitmap(nainaiMap);
                            }
                        }

                        face1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(lookChildEntity.getUserId());
                            }
                        });
                    }else if(vis2 == View.INVISIBLE){
                        text2.setVisibility(View.VISIBLE);
                        face2.setVisibility(View.VISIBLE);
                        text2.setText(lookChildEntity.getNickname());
                        if(!TextUtils.isEmpty(lookChildEntity.getFace()) && lookChildEntity.getFace().trim().length() > 5) {
                            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(lookChildEntity.getFace()), face2);
                        }else{
                            if(family == 1){
                                face2.setImageBitmap(yeyeMap);
                            }else if(family == 2){
                                face2.setImageBitmap(nainaiMap);
                            }
                        }

                        face2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(lookChildEntity.getUserId());
                            }
                        });
                    }else {
                        text3.setVisibility(View.VISIBLE);
                        face3.setVisibility(View.VISIBLE);
                        text3.setText(lookChildEntity.getNickname());
                        if(!TextUtils.isEmpty(lookChildEntity.getFace()) && lookChildEntity.getFace().trim().length() > 5) {
                            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(lookChildEntity.getFace()), face3);
                        }else{
                            if(family == 1){
                                face3.setImageBitmap(yeyeMap);
                            }else if(family == 2){
                                face3.setImageBitmap(nainaiMap);
                            }
                        }
                        face3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeChild(lookChildEntity.getUserId());
                            }
                        });
                    }

                }

            }
        }
    }

    private void changeChild(String id){
        showProgress();
        ApiClient.post(this, ApiConfig.CHANGE_CHILD, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                UIHelper.ToastMessage(LookChildActivity.this, "切换看护人完成");
                lookChildData();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(LookChildActivity.this, errorInfo.getMessage());

            }
        }, "userId", id, "chiId", child);
    }

}
