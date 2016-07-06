package com.qcsh.fuxiang.ui.look;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.OneKeyShare;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 分享到朋友圈
 * Created by Administrator on 2015/9/10.
 */
public class ShareFriendActivity extends BaseActivity{

    private ImageButton backB;
    private TextView titleV;
    private Button actionB,smsB,moreB,ecodeB,copyB;

    private OneKeyShare oneKeyShare;
    private String shareTitle = "友盟社会化分享组件-微信" ,
            shareContent = "友盟社会化分享组件-微信" ,
            targetUrl = "http://www.umeng.com";
    private UMSocialService umSocialService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_friend);
        oneKeyShare = new OneKeyShare(this);
        initView();
    }

    private void initView(){
        backB = (ImageButton)findViewById(R.id.action_bar_back);
        titleV = (TextView)findViewById(R.id.action_bar_title);
        actionB = (Button)findViewById(R.id.action_bar_action);
        smsB = (Button)findViewById(R.id.sms);
        moreB = (Button)findViewById(R.id.more);

        ecodeB = (Button)findViewById(R.id.ecode);
        copyB = (Button)findViewById(R.id.copy);

        titleV.setText("邀请好友");
        actionB.setVisibility(View.GONE);
        backB.setVisibility(View.VISIBLE);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        smsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startContactsActivity(ShareFriendActivity.this);
            }
        });

        moreB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShareContent();
                addSharePlatforms();
            }
        });

        ecodeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // AppIntentManager.startCaptureActivity(ShareFriendActivity.this);
            }
        });

        copyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT > 11) {
                    android.content.ClipboardManager c = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    c.setText(targetUrl);
                    UIHelper.ToastMessage(ShareFriendActivity.this, "复制成功,去邀请好友吧");
                 } else {
                    android.text.ClipboardManager c = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    c.setText(targetUrl);
                    UIHelper.ToastMessage(ShareFriendActivity.this, "复制成功,去邀请好友吧");
                }
            }
        });
    }

    private void setShareContent(){
        umSocialService = oneKeyShare.getUMSocialService();
        // 设置微信分享的内容
        WeiXinShareContent weiXinContent = new WeiXinShareContent();
        weiXinContent.setShareContent(shareContent);
        weiXinContent.setTitle(shareTitle);
        weiXinContent.setTargetUrl(targetUrl);
        umSocialService.setShareMedia(weiXinContent);


        // 设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(shareContent);
        qqShareContent.setTitle(shareTitle);
        qqShareContent.setTargetUrl(targetUrl);
        umSocialService.setShareMedia(qqShareContent);

        // 设置腾讯微博分享内容
        TencentWbShareContent tencent = new TencentWbShareContent();
        tencent.setShareContent(shareContent + targetUrl);
        umSocialService.setShareMedia(tencent);

    }

    private void addSharePlatforms() {
        umSocialService.getConfig().setPlatforms(
                 SHARE_MEDIA.WEIXIN,
                 SHARE_MEDIA.QQ,
                 SHARE_MEDIA.TENCENT);
        umSocialService.openShare(this, false);
    }
}


