package com.qcsh.fuxiang.ui.look;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.InviteFriendAdapter;
import com.qcsh.fuxiang.common.OneKeyShare;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;


/**
 * Created by wo on 15/9/1.
 */
 public class LookInviteFriendsActivity extends BaseActivity {


    private ListView listView;
    private InviteFriendAdapter adapter;

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private OneKeyShare oneKeyShare;
    private String shareTitle = "友盟社会化分享组件-微信" ,
            shareContent = "友盟社会化分享组件-微信" ,
            targetUrl = "http://www.umeng.com";
    private UMSocialService umSocialService;

    public static LookInviteFriendsActivity newInstance() {
        return new LookInviteFriendsActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //调用其父类Activity的onCreate方法来实现对界面的图画绘制工作
        super.onCreate(savedInstanceState);
        setContentView(R.layout.look_invitefriends);
        oneKeyShare = new OneKeyShare(this);
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
        title.setText("邀请好友");

    }

    private void initView(){

        adapter = new InviteFriendAdapter(LookInviteFriendsActivity.this);
        listView=(ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:

                        setShareContent();
                        addSharePlatforms();

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:



                        break;
                    case 4:

                       // AppIntentManager.startCaptureActivity(LookInviteFriendsActivity.this);
                        break;

                    case 5:
                        break;
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
