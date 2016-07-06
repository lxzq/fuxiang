package com.qcsh.fuxiang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amap.api.maps2d.model.LatLng;
import com.qcsh.fuxiang.bean.bang.BangInfo;
import com.qcsh.fuxiang.bean.leyuan.LeyuanTab2Entity;
import com.qcsh.fuxiang.bean.look.GrowthTreeEntity;
import com.qcsh.fuxiang.service.MyLocationService;
import com.qcsh.fuxiang.ui.EditActivity;
import com.qcsh.fuxiang.ui.HomeActivity;
import com.qcsh.fuxiang.ui.PhotoViewActivity;
import com.qcsh.fuxiang.ui.UserGuideActivity;
import com.qcsh.fuxiang.ui.bang.BangDetailActivity;
import com.qcsh.fuxiang.ui.bang.PublishQuestionActivity;


import com.qcsh.fuxiang.ui.home.BindNumberActivity;
import com.qcsh.fuxiang.ui.home.HomeBabyDetailActivity;
import com.qcsh.fuxiang.ui.home.HomeBabyInfoActivity;
import com.qcsh.fuxiang.ui.home.HomeBangBiMoreActivity;
import com.qcsh.fuxiang.ui.home.HomeChangePasswordActivity;
import com.qcsh.fuxiang.ui.home.HomeCodeActivity;
import com.qcsh.fuxiang.ui.home.HomeFamilyManagerActivity;
import com.qcsh.fuxiang.ui.home.HomeMyBangBangActivity;
import com.qcsh.fuxiang.ui.home.HomeMyBangBiActivity;
import com.qcsh.fuxiang.ui.home.HomeMyHuoDongActivity;
import com.qcsh.fuxiang.ui.home.HomeMySaveActivity;
import com.qcsh.fuxiang.ui.home.HomeMyshaishaiActivity;
import com.qcsh.fuxiang.ui.home.HomePartnerActivity;
import com.qcsh.fuxiang.ui.home.HomePersonActivity;
import com.qcsh.fuxiang.ui.home.HomeRemindActivity;
import com.qcsh.fuxiang.ui.home.HomeSettingActivity;

import com.qcsh.fuxiang.ui.home.HomejjbIntroduceActivity;

import com.qcsh.fuxiang.ui.leyuan.LeyuanDetailActivity;
import com.qcsh.fuxiang.ui.leyuan.LiveActivity;
import com.qcsh.fuxiang.ui.login.RegisterBabyActivity;

import com.qcsh.fuxiang.ui.look.ContactsActivity;

import com.qcsh.fuxiang.ui.login.LoginNewActivity;
import com.qcsh.fuxiang.ui.login.QuickLoginActivity;
import com.qcsh.fuxiang.ui.login.RegisterActivity;
import com.qcsh.fuxiang.ui.login.RegisterByPhoneActivity;
import com.qcsh.fuxiang.ui.login.ResetByIdentyActivity;
import com.qcsh.fuxiang.ui.login.ResetByPhoneActivity;
import com.qcsh.fuxiang.ui.login.ResettingActivity;

import com.qcsh.fuxiang.ui.look.FriendListActivity;
import com.qcsh.fuxiang.ui.look.GrowthTreeActivity;
import com.qcsh.fuxiang.ui.look.GrowthtreeDetailActivity;
import com.qcsh.fuxiang.ui.look.LookChildActivity;
import com.qcsh.fuxiang.ui.look.LookInviteFamilyActivity;
import com.qcsh.fuxiang.ui.look.LookInviteFriendsActivity;
import com.qcsh.fuxiang.ui.look.LookMapFragment;
import com.qcsh.fuxiang.ui.look.MessageActivity;
import com.qcsh.fuxiang.ui.look.ShareFriendActivity;
import com.qcsh.fuxiang.ui.look.SubmitActivity;
import com.qcsh.fuxiang.ui.media.VRecordActivity;
import com.qcsh.fuxiang.ui.share.SquareDetailActivity;
import com.qcsh.fuxiang.ui.umengmessage.MyPushIntentService;
import com.qcsh.fuxiang.widget.JJBFullLiveView;
import com.umeng.message.PushAgent;
import com.zbar.lib.CaptureActivity;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 系统界面跳转管理
 * Created by Lxz on 2015/8/12.
 */
public class AppIntentManager {


    /**
     * 跳转到主界面
     *
     * @param activity
     */
    public static void startHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 跳转到快速登录界面
     *
     * @param activity
     */
    public static void startLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, QuickLoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 跳转到用户引导界面
     *
     * @param activity
     */
    public static void startUserGuideActivity(Activity activity) {
        Intent intent = new Intent(activity, UserGuideActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 跳转到扫描二维码
     *
     * @param activity
     * @param relation 家人的关系 1爸爸  2妈妈  3爷爷 4奶奶 5外公 6外婆 7朋友
     */
    public static void startCaptureActivity(Activity activity, String relation) {
        Intent intent = new Intent(activity, CaptureActivity.class);
        intent.putExtra("relation", relation);
        activity.startActivity(intent);
    }

    /**
     * 发送广播 显示主页 我的 的红色圆点
     *
     * @param activity
     */
    public static void sendHomeTab4TipsBroadcast(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(HomeActivity.HOME_TABS_4_TIPS_VISIBILITY);
        activity.sendBroadcast(intent);
    }

    /**
     * 发送广播 显示主页 看着 的红色圆点
     *
     * @param context
     */
    public static void sendHomeTab1TipsBroadcast(AppContext context) {
        Intent intent = new Intent();
        intent.setAction(HomeActivity.HOME_TABS_1_TIPS_VISIBILITY);
        context.sendBroadcast(intent);
    }


    /**
     * 跳转到图片 或者 视频 选择器界面
     *
     * @param activity
     * @param mSelectPath 存储已经选择的图片路径
     * @param maxPicNum   最大可选择图片数量
     * @param fileType    文件类型  0 图片文件   1 视频文件
     */
    public static void startImageSelectorForResult(Activity activity, ArrayList<String> mSelectPath, int maxPicNum, int fileType) {

        Intent intent = new Intent(activity, MultiImageSelectorActivity.class);
        if (fileType == MultiImageSelectorActivity.IMAGE_FILE)
            // 是否显示拍摄图片
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        else
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxPicNum);
        // 选择模式 多选模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        intent.putExtra(MultiImageSelectorActivity.FILE_TYPE, fileType);
        // 默认选择
        if (mSelectPath != null && mSelectPath.size() > 0) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        activity.startActivityForResult(intent, AppConfig.REQUEST_IMAGE);
    }

    /**
     * 跳转到图片查看界面
     */
    public static void startPhotoViewActivity(Activity activity, String currentPath, ArrayList<String> imagesList, int imgtype) {
        Intent intent = new Intent(activity, PhotoViewActivity.class);
        intent.putExtra(PhotoViewActivity.IMAGE_PATG, currentPath);
        intent.putExtra(PhotoViewActivity.IMAGES_LIST, imagesList);
        intent.putExtra(PhotoViewActivity.IMAGE_TYPE, imgtype);
        activity.startActivity(intent);
    }

    /**
     * 启动系统自带视频播放器 播放视频
     *
     * @param context
     * @param videoFile 视频文件
     * @param video     视频配置 LOCAL_VIDEO , HTTP_VIDEO
     */
    public static void startSysVideoPlay(Context context, String videoFile, AppConfig.VIDEO video) {

        String filePath = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (video == AppConfig.VIDEO.HTTP_VIDEO) {
            filePath = AppConfig.FILE_SERVER + videoFile;
        } else if (video == AppConfig.VIDEO.LOCAL_VIDEO) {
            if (videoFile.contains("storage")) {
                filePath = videoFile;
            } else {
                String path = Environment.getExternalStorageDirectory().getPath();
                filePath = path + File.separator + videoFile;
            }
        }
        Uri uri = Uri.parse(filePath);
        String[] videoFiles = videoFile.split("\\.");
        int index = videoFiles.length - 1;
        String type = videoFiles[index];
        Log.d("startSysVideoPlay===", filePath);
        intent.setDataAndType(uri, "video/" + type);
        context.startActivity(intent);
    }

    /**
     * 跳转到录制视频界面
     */
    public static void startRecordVideoActivity(Activity activity, GrowthTreeEntity gtEntity, String flag) {
        Intent intent = new Intent(activity, VRecordActivity.class);
        intent.putExtra("gtEntity", gtEntity);
        intent.putExtra("flag", flag);
        activity.startActivity(intent);
    }

    /**
     * 发送地理位置广播
     *
     * @param context
     * @param pushMessage 地理标记信息
     */
    public static void sendMapLocationBroadcast(Context context, String pushMessage) {
        Intent intent = new Intent();
        intent.setAction(LookMapFragment.LOOKMAP_LOCATION_INFO);
        intent.putExtra(MyPushIntentService.PUSH_MESSAGE, pushMessage);
        context.sendBroadcast(intent);
    }

    /**
     * 发送家人聊天消息广播
     *
     * @param context
     * @param pushMessage
     */
    public static void sendMessageBroadcast(Context context, String pushMessage) {
        Intent intent = new Intent();
        intent.setAction(MessageActivity.MESSAGE);
        intent.putExtra(MyPushIntentService.PUSH_MESSAGE, pushMessage);
        context.sendBroadcast(intent);
    }

    /**
     * 跳转到成长树列表界面
     */
    public static void startGrowthTreeActivity(Activity activity, String childId) {
        Intent intent = new Intent(activity, GrowthTreeActivity.class);
        intent.putExtra("childId", childId);
        activity.startActivity(intent);
    }

    /**
     * 跳转到成长树列表详情
     */
    public static void startGrowthTreeDetailActivity(Context activity, GrowthTreeEntity gtEntity, String flag) {
        Intent intent = new Intent(activity, GrowthtreeDetailActivity.class);
        intent.putExtra("gtEntity", gtEntity);
        intent.putExtra("flag", flag);
        activity.startActivity(intent);
    }

    /**
     * 跳转到邀请家人页面
     */
    public static void startLookInviteFamilyActivity(Context context) {
        Intent intent = new Intent(context, LookInviteFamilyActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到邀请好友页面
     */
    public static void startLookFriendsActivity(Context context) {
        Intent intent = new Intent(context, LookInviteFriendsActivity.class);
        context.startActivity(intent);
    }

    /**
     * 启动定位服务
     *
     * @param context
     */
    public static void startMyLocationService(Context context) {
        Intent intent = new Intent(context, MyLocationService.class);
        intent.setAction(MyLocationService.JJB_LOCATION_SERVICE);
        context.startService(intent);
    }

    /**
     * 启动友盟推送
     *
     * @param context
     */
    public static void startPushAgent(Context context) {
        PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.enable();
        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
    }

    /**
     * 跳转到发布界面
     *
     * @param activity
     * @param videoPath //视频路径
     * @param picPath   //图片路径
     * @param type      //发布的类型  0：心情 1：视频 2：语音 3：图片
     * @param to        //发布到 1：成长树  2：晒晒
     */
    public static void startSubmitActivity(Activity activity, String videoPath, ArrayList<String> picPath, int type, GrowthTreeEntity entity, String to) {
        Intent intent = new Intent(activity, SubmitActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putStringArrayListExtra("picPath", picPath);
        intent.putExtra("submitType", type);
        intent.putExtra("gtEntity", entity);
        intent.putExtra("submitTo", to);
        activity.startActivity(intent);
    }

    /**
     * 跳转到帮发布问题界面
     *
     * @param activity
     */
    public static void startBangPublishQuestionActivity(Activity activity) {
        Intent intent = new Intent(activity, PublishQuestionActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到帮帮详情界面
     *
     * @param activity
     * @param bangDetailInfo
     */
    public static void startBangDetailActivity(Activity activity, BangInfo bangDetailInfo) {
        Intent intent = new Intent(activity, BangDetailActivity.class);
        intent.putExtra("bangDetailInfo", bangDetailInfo);
        activity.startActivity(intent);
    }

    /**
     * 跳转到广场详情列表
     */
    public static void startSquareDetailActivity(Context context) {
        Intent intent = new Intent(context, SquareDetailActivity.class);
        context.startActivity(intent);
    }


    /**
     * 跳转通讯录界面
     *
     * @param activity
     */
    public static void startContactsActivity(Activity activity) {
        Intent intent = new Intent(activity, ContactsActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 分享好友圈界面 和 短信邀请
     *
     * @param activity
     */
    public static void startShareFriendActivity(Activity activity) {
        Intent intent = new Intent(activity, ShareFriendActivity.class);
        activity.startActivity(intent);
    }


    /**
     * 跳转到登陆页面
     */
    public static void startLoginNewActivity(Context context) {
        Intent intent = new Intent(context, LoginNewActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到3/3注册页面
     */
    public static void startRegisterActivity(Context context, String tel, String psw, String key) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra("phone", tel);
        intent.putExtra("password", psw);
        intent.putExtra("code", key);
        context.startActivity(intent);
    }

    /**
     * 跳转到注册1/3页面
     */
    public static void startRegisterByPhoneActivity(Context context) {
        Intent intent = new Intent(context, RegisterByPhoneActivity.class);
        context.startActivity(intent);
    }

   /* *//**
     * 跳转到注册2/3页面
     *//*
    public static void startRegisterByIdentyActivity(Context context) {
        Intent intent = new Intent(context, RegisterByIdentyActivity.class);
        context.startActivity(intent);
    }*/

    /**
     * 跳转到重置密码1/3页面
     */
    public static void startResetByPhoneActivity(Context context) {
        Intent intent = new Intent(context, ResetByPhoneActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到重置密码2/3页面
     */
    public static void startResetByIdentyActivity(Context context) {
        Intent intent = new Intent(context, ResetByIdentyActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到重置密码3/3页面
     */
    public static void startResetActivity(Context context) {
        Intent intent = new Intent(context, ResettingActivity.class);
        context.startActivity(intent);
    }


    /**
     * 跳转到个人资料页面
     */
    public static void startHomePersonActivity(Context context) {
        Intent intent = new Intent(context, HomePersonActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到设置页面
     */
    public static void startHomeSettingActivity(Context context) {
        Intent intent = new Intent(context, HomeSettingActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到编辑文字页面
     */
    public static void startEditActivity(Activity context, String title, String content, int limit, int requestCode) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("limitCount", limit);
        context.startActivityForResult(intent, requestCode);

    }

    /**
     * 跳转到消息提醒页面
     */
    public static void startHomeRemindActivity(Context context) {
        Intent intent = new Intent(context, HomeRemindActivity.class);
        context.startActivity(intent);
    }


    /**
     * 跳转到我的二维码页面
     */
    public static void startHomeCodeActivity(Context context) {
        Intent intent = new Intent(context, HomeCodeActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到宝贝详细信息页面
     */
    public static void startHomeBabyInfoActivity(Context context) {
        Intent intent = new Intent(context, HomeBabyInfoActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转到我的帮帮界面
     *
     * @param activity
     */
    public static void startHomeMyBangBangActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeMyBangBangActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到修改密码界面
     *
     * @param activity
     */
    public static void startHomeChangeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeChangePasswordActivity.class);
        activity.startActivity(intent);
    }


    /**
     * 跳转到带孩子界面
     *
     * @param activity
     */
    public static void startLookChildActivity(Activity activity) {
        Intent intent = new Intent(activity, LookChildActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到我的帮币界面
     *
     * @param activity
     */
    public static void startHomeMyBangBiActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeMyBangBiActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到我的晒晒界面
     *
     * @param activity
     */
    public static void startHomeMyShaiShaiActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeMyshaishaiActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到我的活动界面
     *
     * @param activity
     */
    public static void startHomeMyHuoDongActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeMyHuoDongActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到我的收藏界面
     *
     * @param activity
     */
    public static void startHomeMySaveActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeMySaveActivity.class);
        activity.startActivity(intent);
    }


    /**
     * 跳转到帮币明细界面
     *
     * @param activity
     */
    public static void startHomeBangBiMoreActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeBangBiMoreActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到关于家家帮界面
     *
     * @param activity
     */
    public static void startHomejjbIntroduceActivity(Activity activity) {
        Intent intent = new Intent(activity, HomejjbIntroduceActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到家人管理界面
     *
     * @param activity
     */
    public static void startHomeFamilyMangerActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeFamilyManagerActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到伙伴管理界面
     *
     * @param activity
     */
    public static void startHomePartnerMangerActivity(Activity activity) {
        Intent intent = new Intent(activity, HomePartnerActivity.class);
        activity.startActivity(intent);
    }


    /**
     * 跳转到添加宝宝信息
     *
     * @param activity
     * @param flag     0 表示登录注册孩子  1表示已经登录后添加孩子
     */
    public static void startRegisterBabyActivity(Activity activity, int flag) {
        Intent intent = new Intent(activity, RegisterBabyActivity.class);
        intent.putExtra("flag", flag);
        activity.startActivity(intent);
    }

    /**
     * 跳转到宝宝详细信息页面
     *
     * @param activity
     */
    public static void startHomeBabyDetailActivity(Activity activity, String childId) {
        Intent intent = new Intent(activity, HomeBabyDetailActivity.class);
        intent.putExtra("childId", childId);
        activity.startActivity(intent);
    }

    /**
     * 跳转到绑定账号页面
     *
     * @param activity
     */
    public static void startBindNumberActivity(Activity activity) {
        Intent intent = new Intent(activity, BindNumberActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到伙伴列表页面
     *
     * @param activity
     */
    public static void startFriendListActivity(Activity activity, String childIds) {
        Intent intent = new Intent(activity, FriendListActivity.class);
        intent.putExtra("childIds", childIds);
        activity.startActivity(intent);
    }

    /**
     * 跳转到家人聊天界面
     *
     * @param activity
     */
    public static void startMessageActivity(Activity activity) {
        Intent intent = new Intent(activity, MessageActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 跳转到全屏直播界面
     * @param appContext
     * @param path
     */
    public static void startJJBFullLive(Context appContext,String path){
        Intent intent = new Intent(appContext, JJBFullLiveView.class);
        intent.putExtra("path",path);
        appContext.startActivity(intent);
    }

    /**
     * 跳转到乐园直播界面
     * @param activity
     * @param path 直播地址
     * @param title 直播标题
     * @param id 摄像头id
     */
    public static void startLeyuanLive(Activity activity,String path,String title,String id){
        Intent intent = new Intent(activity, LiveActivity.class);
        intent.putExtra("path",path);
        intent.putExtra("title",title);
        intent.putExtra("id",id);
        activity.startActivity(intent);
    }

    /**
     * 跳转到乐园项目详情
     * @param activity
     * @param entity
     */
    public static void startLeyuanDetailActivity(Activity activity,LeyuanTab2Entity entity){
        Intent intent = new Intent(activity, LeyuanDetailActivity.class);
        intent.putExtra("leyuan",entity);
        activity.startActivity(intent);
    }
}
