package com.qcsh.fuxiang.ui.bang;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.mbaas.oss.model.OSSException;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.OssHandler;
import com.qcsh.fuxiang.common.OssUtils;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 发布帮问题
 * Created by Administrator on 2015/9/6.
 */
public class PublishQuestionActivity extends BaseActivity {

    private ImageButton backButton;
    private TextView titleV;
    private Button action;
    private EditText title, content;
    private FrameLayout imageF1, imageF2, imageF3;
    private ImageView imageView1, imageView2, imageView3;
    private ImageButton delIma1, delIma2, delIma3, addImage;

    private ArrayList<String> mSelectPath;
    //  private String type;
    private StringBuffer imageBuffer;

    private String userId;
    private String titleStr;
    private String contentStr;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bang_question);
        // type = getIntent().getStringExtra("type");
        User u = getCurrentUser();
        userId = u.id;
        initView();
        initListener();
    }

    private void initView() {

        backButton = (ImageButton) findViewById(R.id.action_bar_back);
        titleV = (TextView) findViewById(R.id.action_bar_title);
        action = (Button) findViewById(R.id.action_bar_action);
        action.setText("提交");
        titleV.setText("描述你的问题");
        backButton.setVisibility(View.VISIBLE);

        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);

        imageF1 = (FrameLayout) findViewById(R.id.select_image_1);
        imageF2 = (FrameLayout) findViewById(R.id.select_image_2);
        imageF3 = (FrameLayout) findViewById(R.id.select_image_3);

        imageView1 = (ImageView) findViewById(R.id.select_image1);
        imageView2 = (ImageView) findViewById(R.id.select_image2);
        imageView3 = (ImageView) findViewById(R.id.select_image3);

        delIma1 = (ImageButton) findViewById(R.id.img_delete_1);
        delIma2 = (ImageButton) findViewById(R.id.img_delete_2);
        delIma3 = (ImageButton) findViewById(R.id.img_delete_3);

        addImage = (ImageButton) findViewById(R.id.img_add);

        imageF1.setVisibility(View.GONE);
        imageF2.setVisibility(View.GONE);
        imageF3.setVisibility(View.GONE);
    }

    private void initListener() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoadImage();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startImageSelectorForResult(PublishQuestionActivity.this, mSelectPath, 3, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                reSetImage();
            }
        }

    }

    private void reSetImage() {
        addImage.setVisibility(View.VISIBLE);
        imageF1.setVisibility(View.GONE);
        imageF2.setVisibility(View.GONE);
        imageF3.setVisibility(View.GONE);

        for (int i = 0; i < mSelectPath.size(); i++) {
            final String path = mSelectPath.get(i);
            Bitmap bitmap = ImageUtils.getSmallBitmap(path);
            if (i == 0) {
                imageView1.setImageBitmap(bitmap);
                delIma1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectPath.remove(path);
                        reSetImage();
                    }
                });
                imageF1.setVisibility(View.VISIBLE);
            } else if (i == 1) {
                imageView2.setImageBitmap(bitmap);
                delIma2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectPath.remove(path);
                        reSetImage();
                    }
                });
                imageF2.setVisibility(View.VISIBLE);
            } else {
                imageView3.setImageBitmap(bitmap);
                delIma3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectPath.remove(path);
                        reSetImage();
                    }
                });
                imageF3.setVisibility(View.VISIBLE);
                addImage.setVisibility(View.GONE);
            }
        }
    }

    private void upLoadImage() {

        titleStr = title.getText().toString();
        contentStr = content.getText().toString();

        if (TextUtils.isEmpty(titleStr)) {
            UIHelper.ToastMessage(getApplication(), "请输入标题");
            return;
        }

        if (TextUtils.isEmpty(contentStr)) {
            UIHelper.ToastMessage(getApplication(), "请描述您的问题");
            return;
        }
        showProgress();
        imageBuffer = new StringBuffer();
        if (null != mSelectPath && !mSelectPath.isEmpty()) {

            for (int i = 0; i < mSelectPath.size(); i++) {
                final int index = i;
                String path = mSelectPath.get(i);
                OssUtils.upload(path, AppConfig.OSS_UPLOAD.images.toString(), new OssHandler() {
                    @Override
                    public void onSuccess(String strPath) {
                        imageBuffer.append(strPath + "|" + index + ",");
                        String[] s = imageBuffer.toString().split(",");
                        if (s.length == mSelectPath.size()) {
                            handler.sendEmptyMessage(0);
                        }
                    }

                    @Override
                    public void onFailure(String strPath, OSSException ossException) {
                        super.onFailure(strPath, ossException);
                        UIHelper.ToastMessage(getApplication(), ossException.getMessage());
                    }
                });
            }
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            submitData();
        }
    };

    private void submitData() {
        long t = Calendar.getInstance().getTimeInMillis();
        String time = Utils.jsonTimeToString(t);
        ApiClient.post(this, ApiConfig.PUBLISH_BANG_QUESTION, new ApiResponseHandler<BaseEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
//                UIHelper.ToastMessage(PublishQuestionActivity.this, "发布成功");
//                imageBuffer = null;
//                mSelectPath.clear();
//                reSetImage();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                if ("10000".equals(errorInfo.getCode())) finish();
                UIHelper.ToastMessage(PublishQuestionActivity.this, errorInfo.getMessage());
            }
        }, "userId", userId, "imageUrls", imageBuffer.substring(0, imageBuffer.length() - 1).toString(), "title", titleStr, "content", contentStr, "time", time);

    }
}
