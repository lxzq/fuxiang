package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.common.UIHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;

/**
 * Created by WWW on 2015/10/19.
 */
public class CustomShareBoard extends PopupWindow implements View.OnClickListener {


    private Context context;
    private UMSocialService mController;
    private String contentId;

    public CustomShareBoard(Context context, UMSocialService controller,String contentId) {
        super(context);
        this.context = context;
        this.mController = controller;
        this.contentId = contentId;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_cudtom_share_board, null);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        rootView.findViewById(R.id.qq).setOnClickListener(this);
        rootView.findViewById(R.id.qzone).setOnClickListener(this);
        rootView.findViewById(R.id.sina).setOnClickListener(this);
        rootView.findViewById(R.id.shaishai).setOnClickListener(this);
        setContentView(rootView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.wechat:
                performShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.wechat_circle:
                performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.qq:
                performShare(SHARE_MEDIA.QQ);
                break;
            case R.id.qzone:
                performShare(SHARE_MEDIA.QZONE);
                break;
            case R.id.sina:
                performShare(SHARE_MEDIA.SINA);
                break;
            case R.id.shaishai:
                shareToSS();
                break;
            default:
                break;
        }
    }


    private void performShare(SHARE_MEDIA platform) {
        mController.postShare(context, platform, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
            }
            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                String showText = platform.toString();
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showText += "平台分享成功";
                } else {
                    showText += "平台分享失败";
                }
                Toast.makeText(context, showText, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void shareToSS(){
        ApiClient.post(context, ApiConfig.SHARE_TO_SS, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                dismiss();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                UIHelper.ToastMessage(context,errorInfo.getMessage());
                dismiss();
            }
        },"contentId",contentId);
    }
}
