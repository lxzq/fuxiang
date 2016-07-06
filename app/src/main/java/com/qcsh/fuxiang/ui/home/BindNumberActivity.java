package com.qcsh.fuxiang.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.login.QuickLoginActivity;
import com.qcsh.fuxiang.ui.login.RegisterByPhoneActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.yixia.camera.demo.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class BindNumberActivity extends BaseActivity implements View.OnClickListener {

    private String tag = BindNumberActivity.class.getName();
    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private TextView bindPhone;
    private TextView bindQQ;
    private TextView bindWeixin;
    private TextView bindSinaWeibo;
    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
    private User user;
    private boolean isPhoneBind = false;
    private boolean isQQBind = false;
    private boolean isWeixinBind = false;
    private boolean isSinaweiboBind = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_number);
        initToolBar();
        initView();
        addPlatform();
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
        title.setText("账号绑定");
    }

    private void initView() {
        bindPhone = (TextView) findViewById(R.id.bind_phone);
        bindQQ = (TextView) findViewById(R.id.bind_qq);
        bindWeixin = (TextView) findViewById(R.id.bind_weixin);
        bindSinaWeibo = (TextView) findViewById(R.id.bind_sinaweibo);

        bindPhone.setOnClickListener(this);
        bindQQ.setOnClickListener(this);
        bindWeixin.setOnClickListener(this);
        bindSinaWeibo.setOnClickListener(this);
        refreshView();
    }

    private void refreshView() {
        user = getCurrentUser();
        if (!StringUtils.isEmpty(user.getPhone())) {
            isPhoneBind = true;
            bindPhone.setText("解除绑定");
        } else {
            isPhoneBind = false;
            bindPhone.setText("去绑定");
        }
        if (!StringUtils.isEmpty(user.getQq_uid())) {
            isQQBind = true;
            bindQQ.setText("解除绑定");
        } else {
            isQQBind = false;
            bindQQ.setText("去绑定");
        }
        if (!StringUtils.isEmpty(user.getOpenid())) {
            isWeixinBind = true;
            bindWeixin.setText("解除绑定");
        } else {
            isWeixinBind = false;
            bindWeixin.setText("去绑定");
        }
        if (!StringUtils.isEmpty(user.getWeibo_uid())) {
            isSinaweiboBind = true;
            bindSinaWeibo.setText("解除绑定");
        } else {
            isSinaweiboBind = false;
            bindSinaWeibo.setText("去绑定");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_phone:
                if (isPhoneBind) {
                    showProgress();
                    ApiClient.post(BindNumberActivity.this, ApiConfig.BIND_NUMBER, new ApiResponseHandler<User>() {
                        @Override
                        public void onSuccess(DataEntity entity) {
                            closeProgress();
                            User user = (User) entity.data.get(0);
                            getAppContext().cacheUserInfo(user);
                            refreshView();
                        }

                        @Override
                        public void onFailure(ErrorEntity errorInfo) {
                            closeProgress();
                            UIHelper.ToastMessage(BindNumberActivity.this, errorInfo.getMessage());
                        }
                    }, "userid", user.getId(), "bindkey", "unbindphone");
                } else {
                    Intent intent = new Intent(BindNumberActivity.this, RegisterByPhoneActivity.class);
                    intent.putExtra("from", 1);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.bind_qq:
                if (isQQBind) {
                    bind("qq", null);
                } else {
                    login(SHARE_MEDIA.QQ);
                }
                break;
            case R.id.bind_weixin:
                if (isWeixinBind) {
                    bind("wechat", null);
                } else {
                    login(SHARE_MEDIA.WEIXIN);
                }
                break;
            case R.id.bind_sinaweibo:
                if (isSinaweiboBind) {
                    bind("weibo", null);
                } else {
                    login(SHARE_MEDIA.SINA);
                }
                break;
        }
    }

    private void addPlatform() {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        String appId = AppConfig.QQ_APPID;
        String appKey = AppConfig.QQ_APPKEY;
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(BindNumberActivity.this, appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(BindNumberActivity.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(BindNumberActivity.this, AppConfig.WEIXIN_APPID, AppConfig.WEXIN_APPSECRET);
        wxHandler.addToSocialSDK();
        // 设置新浪SSO handler,调用客户端
        //mController.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    // 如果有使用任一平台的SSO授权, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 根据requestCode获取对应的SsoHandler
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(resultCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                refreshView();
            }
        }
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {

        mController.doOauthVerify(BindNumberActivity.this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                Log.i(tag, "授权开始..."); showProgress("正在授权..");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Log.e(tag, "授权失败:" + e.getMessage());
                closeProgress();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(platform, uid);
                } else {
                    closeProgress();
                    UIHelper.ToastMessage(BindNumberActivity.this, "账户登录失败,请检查账户是否正确");
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                closeProgress();
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform, final String userid) {

        mController.getPlatformInfo(BindNumberActivity.this, platform, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {

                if (status == 200 && null != info) {
                    if (platform == SHARE_MEDIA.SINA) {
                        String uid = info.get("uid").toString();
                        bind("weibo", uid);
                    } else if (platform == SHARE_MEDIA.QQ) {
                        bind("qq", userid);
                    } else if (platform == SHARE_MEDIA.WEIXIN) {
                        String openid = info.get("openid").toString();
                        bind("wechat", openid);
                    }
                } else {
                    closeProgress();
                    UIHelper.ToastMessage(BindNumberActivity.this, "获取登录信息失败");
                }
            }
        });
    }

    private void bind(String bindkey, String bindvalue) {
        showProgress();
        ApiClient.post(BindNumberActivity.this, ApiConfig.BIND_NUMBER, new ApiResponseHandler<User>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                User user = (User) entity.data.get(0);
                getAppContext().cacheUserInfo(user);
                refreshView();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(BindNumberActivity.this, errorInfo.getMessage());
            }
        }, "userid", user.getId(), "bindkey", bindkey, "bindvalue", bindvalue);
    }

}
