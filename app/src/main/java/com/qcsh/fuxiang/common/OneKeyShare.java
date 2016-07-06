package com.qcsh.fuxiang.common;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.widget.CustomShareBoard;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by WWW on 2015/8/18.
 */
public class OneKeyShare {
    private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private Context context;


    public OneKeyShare(Context context) {
        this.context = context;
        // 添加新浪sso授权
        //mController.getConfig().setSsoHandler(new SinaSsoHandler());

        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }

    public UMSocialService getUMSocialService() {
        return mController;
    }

    /**
     * 添加需要的平台</br>
     */
    public void addSharePlatforms() {
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA/*, SHARE_MEDIA.TENCENT*/);
        mController.openShare((Activity) context, false);
    }

    public void addCustomShareBoard(String contentId){
        CustomShareBoard shareBoard = new CustomShareBoard(context,mController,contentId);
        shareBoard.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public void setShareContent(String title, String content, String url, String imgurl, String audiourl, String videourl) {
        mController.setShareContent(content);
        UMImage localImage = new UMImage(context, R.mipmap.logo);
        UMImage urlImage = null;

        // 设置微信分享的内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        // 设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        // 设置新浪微博分享内容
        SinaShareContent sinaContent = new SinaShareContent();


        if (!StringUtils.isEmpty(title)) {
            weixinContent.setTitle(title);
            circleMedia.setTitle(title);
            qzone.setTitle(title);
            qqShareContent.setTitle(title);
            sinaContent.setTitle(title);
        } else {

        }


        if (!StringUtils.isEmpty(content)) {
            weixinContent.setShareContent(content);
            circleMedia.setShareContent(content);
            qzone.setShareContent(content);
            qqShareContent.setShareContent(content);
            sinaContent.setShareContent(content);
        } else {

        }

        if (!StringUtils.isEmpty(url)) {
            weixinContent.setTargetUrl(url);
            circleMedia.setTargetUrl(url);
            qzone.setTargetUrl(url);
            qqShareContent.setTargetUrl(url);
            sinaContent.setTargetUrl(url);
        } else {

        }

        if (!StringUtils.isEmpty(imgurl)) {
            urlImage = new UMImage(context, AppConfig.getUserPhoto_x(imgurl));
            weixinContent.setShareMedia(urlImage);
            circleMedia.setShareImage(urlImage);
            qzone.setShareImage(urlImage);
            qqShareContent.setShareImage(urlImage);
            sinaContent.setShareImage(urlImage);
        } else {
            weixinContent.setShareMedia(localImage);
            circleMedia.setShareImage(localImage);
            qzone.setShareImage(localImage);
            qqShareContent.setShareImage(localImage);
            sinaContent.setShareImage(localImage);
        }

        if (!StringUtils.isEmpty(audiourl)) {
            UMusic uMusic = new UMusic(AppConfig.FILE_SERVER + audiourl);
//          uMusic.setAuthor("umeng");
            if (!StringUtils.isEmpty(title)) {
                uMusic.setTitle(title);
            } else {
                uMusic.setTitle("");
            }
            if (!StringUtils.isEmpty(imgurl)) {
                uMusic.setThumb(urlImage);
            } else {
                uMusic.setThumb(localImage);
            }

            circleMedia.setShareMedia(uMusic);
            qqShareContent.setShareMedia(uMusic);
            weixinContent.setShareMedia(uMusic);
            qzone.setShareMedia(uMusic);
            sinaContent.setShareMedia(uMusic);
        }

        if (!StringUtils.isEmpty(videourl)) {
            // 视频分享
            UMVideo video = new UMVideo(AppConfig.FILE_SERVER + videourl);
            if (!StringUtils.isEmpty(title)) {
                video.setTitle(title);
            } else {
                video.setTitle("");
            }
            if (!StringUtils.isEmpty(imgurl)) {
                video.setThumb(urlImage);
            } else {
                video.setThumb(localImage);
            }
            circleMedia.setShareMedia(video);
            qqShareContent.setShareVideo(video);
            weixinContent.setShareVideo(video);
            qzone.setShareVideo(video);
            sinaContent.setShareVideo(video);
        }

        mController.setShareMedia(weixinContent);
        mController.setShareMedia(circleMedia);
        mController.setShareMedia(qzone);
        mController.setShareMedia(qqShareContent);
        mController.setShareMedia(sinaContent);
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = AppConfig.WEIXIN_APPID;
        String appSecret = AppConfig.WEXIN_APPSECRET;
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context, appId,
                appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context,
                appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     * image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     * 要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     * : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    private void addQQQZonePlatform() {
        String appId = AppConfig.QQ_APPID;
        String appKey = AppConfig.QQ_APPKEY;
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) context,
                appId, appKey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
                (Activity) context, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * 注销本次登录</br>
     */
    private void logout(final SHARE_MEDIA platform) {
        mController.deleteOauth(context, platform, new SocializeListeners.SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                String showText = "解除" + platform.toString() + "平台授权成功";
                if (status != StatusCode.ST_CODE_SUCCESSED) {
                    showText = "解除" + platform.toString() + "平台授权失败[" + status + "]";
                }
                Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
