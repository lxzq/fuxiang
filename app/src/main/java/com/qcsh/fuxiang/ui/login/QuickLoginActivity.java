package com.qcsh.fuxiang.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.AppStart;
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
import com.umeng.message.UmengRegistrar;
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

/**
 * Created by wo on 15/9/8.
 */
public class QuickLoginActivity extends BaseActivity {


    private Button weixinLogin;
    private Button qqLogin;

    private Button btn_register;
    private Button btn_username;

    private AppContext context;

    private String tag = QuickLoginActivity.class.getName();
    private String device_token;

    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_login);

        SharedPreferences spf = getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
        device_token = spf.getString(AppStart.UM_TOKEN, "");

        context = getAppContext();
        addPlatform();
        initView();
        initListener();
    }

    private void initView() {

        weixinLogin = (Button) findViewById(R.id.weixinLogin);
        qqLogin = (Button) findViewById(R.id.qqLogin);
        //已有账号登陆
        btn_username = (Button) findViewById(R.id.btn_login);
        btn_username.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startLoginNewActivity(QuickLoginActivity.this);
            }
        });

        //手机号注册
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AppIntentManager.startRegisterByPhoneActivity(QuickLoginActivity.this);
            }
        });

    }

    private void initListener() {
        qqLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login(SHARE_MEDIA.QQ);
            }
        });

        weixinLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                login(SHARE_MEDIA.WEIXIN);
            }
        });
    }

    private void addPlatform() {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        String appId = AppConfig.QQ_APPID;
        String appKey = AppConfig.QQ_APPKEY;
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(QuickLoginActivity.this, appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(QuickLoginActivity.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(QuickLoginActivity.this, AppConfig.WEIXIN_APPID, AppConfig.WEXIN_APPSECRET);
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
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void login(final SHARE_MEDIA platform) {
        showProgress("正在登录..");
        mController.doOauthVerify(QuickLoginActivity.this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                Log.i(tag, "授权开始...");
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
                    UIHelper.ToastMessage(QuickLoginActivity.this, "账户登录失败,请检查账户是否正确");
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

        mController.getPlatformInfo(QuickLoginActivity.this, platform, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {

                if (status == 200 && null != info) {
                    if (platform == SHARE_MEDIA.SINA) {

                        String uid = info.get("uid").toString();
                        String gender = info.get("gender").toString();
                        String screen_name = info.get("screen_name").toString();
                        String profile_image_url = info.get("profile_image_url").toString();
                        ApiClient.post(QuickLoginActivity.this, ApiConfig.SYS_WEIBO, new ApiResponseHandler<User>() {
                            @Override
                            public void onSuccess(DataEntity entity) {
                                doSuccess(entity);
                            }

                            @Override
                            public void onFailure(ErrorEntity errorInfo) {
                                closeProgress();
                                ToastUtils.showLongToast(QuickLoginActivity.this, errorInfo.getMessage());
                            }
                        }, "uid", uid, "sex", gender, "nickname", screen_name, "face", profile_image_url, "umtoken", device_token);

                    } else if (platform == SHARE_MEDIA.QQ) {
                        final String nickname = info.get("screen_name").toString();//昵称
                        final String profile_image_url = info.get("profile_image_url").toString();//头像
                        String gender = info.get("gender").toString();//性别 男 女 中文
                        String sex = "";
                        if ("男".equals(gender)) {
                            sex = "1";
                        } else if ("女".equals(gender)) {
                            sex = "0";
                        }
                        ApiClient.post(QuickLoginActivity.this, ApiConfig.SYS_QQ, new ApiResponseHandler<User>() {
                            @Override
                            public void onSuccess(DataEntity entity) {
                                doSuccess(entity);
                            }

                            @Override
                            public void onFailure(ErrorEntity errorInfo) {
                                closeProgress();
                                UIHelper.ToastMessage(QuickLoginActivity.this, errorInfo.getMessage());
                            }
                        }, "userId", userid, "nickname", nickname, "face", profile_image_url, "gender", sex, "umtoken", device_token);

                    } else if (platform == SHARE_MEDIA.WEIXIN) {

                        String sex = info.get("sex").toString();
                        String openid = info.get("openid").toString();
                        String nickname = info.get("nickname").toString();
                        String headimgurl = info.get("headimgurl").toString();
                        ApiClient.post(QuickLoginActivity.this, ApiConfig.SYS_WEIXIN, new ApiResponseHandler<User>() {
                            @Override
                            public void onSuccess(DataEntity entity) {
                                doSuccess(entity);
                            }

                            @Override
                            public void onFailure(ErrorEntity errorInfo) {
                                closeProgress();
                                UIHelper.ToastMessage(QuickLoginActivity.this, errorInfo.getMessage());
                            }
                        }, "openid", openid, "nickname", nickname, "headimgurl", headimgurl, "sex", sex, "umtoken", device_token);
                    }
                } else {
                    closeProgress();
                    UIHelper.ToastMessage(QuickLoginActivity.this, "获取登录信息失败");
                }
            }
        });
    }

    private void doSuccess(DataEntity entity) {
        closeProgress();
        User user = (User) entity.data.get(0);
        String childInfo = user.getChild_info();
        if (!TextUtils.isEmpty(childInfo)) {
            try {
                JSONArray jsonArray = new JSONArray(childInfo);
                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    user.setChildId(jsonObject.getString("child_id"));
                }
            } catch (Exception e) {
            }
        }
        context.cacheUserInfo(user);
        if (!StringUtils.isEmpty(user.getChildId())) {
            AppIntentManager.startHomeActivity(QuickLoginActivity.this);
        } else {
            AppIntentManager.startRegisterBabyActivity(QuickLoginActivity.this,0);
        }
    }

}


