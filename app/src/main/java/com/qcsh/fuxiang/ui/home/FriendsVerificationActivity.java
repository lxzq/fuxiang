package com.qcsh.fuxiang.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.common.CuttingBitmap;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.umengmessage.MyPushIntentService;

import org.json.JSONObject;

/**
 * 好小伙伴验证
 * Created by Administrator on 2015/9/24.
 */
public class FriendsVerificationActivity extends BaseActivity {

    private ImageButton action_bar_back;
    private TextView action_bar_title;
    private Button action_bar_action;

    private ImageView faceV;
    private TextView nicknameV, notesV;
    private Button yesB, noB;

    private String push_message, msgType;
    private String userId, friendId, childId, nurseId, nickname, face, childName, relation;

    public static final String YES = "0";
    public static final String NO = "1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_verication_view);
        push_message = getIntent().getStringExtra(MyPushIntentService.PUSH_MESSAGE);
        msgType = getIntent().getStringExtra("msgType");//1 加伙伴验证  6 加家人验证

        initBar();
        initView();
        setData();
        setNotes();
    }

    private void initBar() {
        action_bar_back = (ImageButton) findViewById(R.id.action_bar_back);
        action_bar_title = (TextView) findViewById(R.id.action_bar_title);
        action_bar_action = (Button) findViewById(R.id.action_bar_action);

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

    private void initView() {
        faceV = (ImageView) findViewById(R.id.face);
        nicknameV = (TextView) findViewById(R.id.nickname);
        notesV = (TextView) findViewById(R.id.notes);
        yesB = (Button) findViewById(R.id.yes);
        noB = (Button) findViewById(R.id.no);

        yesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(msgType))
                    postData(YES);
                else if ("6".equals(msgType)) {
                    postFamilyData(YES);
                }
            }
        });

        noB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("1".equals(msgType))
                    postData(NO);
                else if ("6".equals(msgType)) {
                    postFamilyData(NO);
                }
            }
        });
    }

    private void setData() {
        try {
            JSONObject jsonObject = new JSONObject(push_message);
            userId = jsonObject.getString("userId");
            childId = jsonObject.getString("childId");
            nurseId = jsonObject.getString("nuserId");
            face = jsonObject.getString("face");

            if ("1".equals(msgType)) {
                friendId = jsonObject.getString("friendId");
                nickname = jsonObject.getString("nickname");
            } else if ("6".equals(msgType)) {
                childName = jsonObject.getString("childName");
                nickname = jsonObject.getString("userName");
                relation = jsonObject.getString("relation");
            }

            nicknameV.setText(nickname);
            if (!TextUtils.isEmpty(face) && face.trim().length() > 5)
                ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), faceV, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if (null != loadedImage) {
                            Bitmap roundBitmap = CuttingBitmap
                                    .toRoundBitmap(loadedImage);
                            ((ImageView) view).setImageBitmap(roundBitmap);
                        }
                    }
                });

        } catch (Exception e) {

        }
    }

    private void setNotes() {
        if ("1".equals(msgType)) {
            notesV.setText("请求加我成为小伙伴");
        } else if ("6".equals(msgType)) {
            if ("1".equals(relation)) {
                notesV.setText("请求加我成为"+childName+"的爸爸");
            } else if ("2".equals(relation)) {
                notesV.setText("请求加我成为"+childName+"的妈妈");
            } else if ("3".equals(relation)) {
                notesV.setText("请求加我成为"+childName+"的爷爷");
            } else if ("4".equals(relation)) {
                notesV.setText("请求加我成为"+childName+"的奶奶");
            } else if ("5".equals(relation)) {
                notesV.setText("请求加我成为"+childName+"的外公");
            } else if ("6".equals(relation)) {
                notesV.setText("请求加我成为"+childName+"的外婆");
            }
        }
    }

    private void postData(final String msg) {
        showProgress("正在处理..");
        ApiClient.post(this, ApiConfig.FRIENDS_VERICATION, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                if (YES.equals(msg))
                    UIHelper.ToastMessage(FriendsVerificationActivity.this, "已成功加为小伙伴");
                else
                    UIHelper.ToastMessage(FriendsVerificationActivity.this, "已拒绝加为小伙伴");
                finish();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(FriendsVerificationActivity.this, errorInfo.getMessage());

            }
        }, "nuserId", userId, "friendId", childId, "childId", friendId, "userId", nurseId, "result", msg);
    }

    private void postFamilyData(final String msg) {
        showProgress("正在处理..");
        ApiClient.post(this, ApiConfig.ADD_FAMILY_VERICATION, new ApiResponseHandler<BaseEntity>() {
                    @Override
                    public void onSuccess(DataEntity entity) {
                        closeProgress();
                        if (YES.equals(msg))
                            UIHelper.ToastMessage(FriendsVerificationActivity.this, "已成功加为家人");
                        else
                            UIHelper.ToastMessage(FriendsVerificationActivity.this, "已拒绝加为家人");
                        finish();
                    }

                    @Override
                    public void onFailure(ErrorEntity errorInfo) {
                        closeProgress();
                        UIHelper.ToastMessage(FriendsVerificationActivity.this, errorInfo.getMessage());

                    }
                }, "userId", userId, "childId", childId, "nuserId", nurseId, "relation", relation,
                "childName", childName, "userName", nickname, "result", msg);
    }
}
