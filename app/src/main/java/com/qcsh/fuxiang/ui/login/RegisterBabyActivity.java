package com.qcsh.fuxiang.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aliyun.mbaas.oss.model.OSSException;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.bean.look.LookChildEntity;
import com.qcsh.fuxiang.common.CuttingBitmap;
import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.OssHandler;
import com.qcsh.fuxiang.common.OssUtils;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.ui.home.HomeFamilyManagerActivity;
import com.qcsh.fuxiang.widget.DateTimePicker;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 注册宝宝信息
 * Created by Administrator on 2015/9/22.
 */
public class RegisterBabyActivity extends BaseActivity implements View.OnClickListener {


    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ImageButton faceImage;
    private EditText nameText;
    private TextView dateText;
    private RadioGroup raGroup;

    private RadioGroup familyManager;
    private String family = "1";//1 爸爸  2 妈妈 3 爷爷 4奶奶 5外公 6外婆
    private String sex = "0";
    private String image;
    private String face;
    private String userId;
    private String datetime;


    private ArrayList<String> picPath;
    User user;
    AppContext context;
    private EditText jiyuText;
    private int flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_baby);
        flag = getIntent().getIntExtra("flag",0);
        context = getAppContext();
        user = getCurrentUser();
        if (null != user) userId = user.id;
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
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("完成");
        title.setText("添加宝宝信息");
    }

    private void initView() {

        faceImage = (ImageButton) findViewById(R.id.btn_face);
        faceImage.setOnClickListener(this);

        nameText = (EditText) findViewById(R.id.ic_name);
        jiyuText = (EditText) findViewById(R.id.ic_jiyu);
        dateText = (TextView) findViewById(R.id.ic_date);

        raGroup = (RadioGroup) findViewById(R.id.layout_select);
        raGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_boy:
                        sex = "1";
                        break;

                    case R.id.btn_girl:
                        sex = "0";
                        break;
                }
            }
        });

        familyManager = (RadioGroup) findViewById(R.id.ic_familymanager);

        familyManager.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.baba:
                        family = "1";
                        break;

                    case R.id.mama:
                        family = "2";
                        break;

                    case R.id.yeye:
                        family = "3";
                        break;

                    case R.id.nainai:
                        family = "4";
                        break;
                    case R.id.waigong:
                        family = "5";
                        break;
                    case R.id.waipo:
                        family = "6";
                        break;

                }
            }
        });

        dateText.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.action_bar_action:
                if (null == image) {
                    UIHelper.ToastMessage(this, "请上传宝宝头像");
                    return;
                }
                if (null == datetime) {
                    UIHelper.ToastMessage(this, "请选择出生日期");
                    return;
                }
                upLoad();
                break;
            case R.id.btn_face:
                AppIntentManager.startImageSelectorForResult(RegisterBabyActivity.this, picPath, 1, MultiImageSelectorActivity.IMAGE_FILE);
                break;

            case R.id.ic_date:
                showDateTime();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                picPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                resetImage();
            }
        }
    }

    private void resetImage() {
        if (null != picPath && picPath.size() > 0) {
            image = picPath.get(0);
            Bitmap picBitmap = ImageUtils.getBitmapByPath(image);
            Bitmap bitmap = ImageUtils.ratioBitmap(picBitmap, AppConfig.IMAGE_WIDTH, AppConfig.IMAGE_HEIGHT);
            Bitmap roundBitmap = CuttingBitmap.toRoundBitmap(bitmap);
            faceImage.setImageBitmap(roundBitmap);
        }
    }


    private void upLoad() {
        showProgress();
        OssUtils.upload(image, AppConfig.OSS_UPLOAD.images.toString(), new OssHandler() {
            @Override
            public void onSuccess(String strPath) {
                face = strPath;
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onFailure(String strPath, OSSException ossException) {
                super.onFailure(strPath, ossException);
                closeProgress();
                UIHelper.ToastMessage(getApplication(), ossException.getMessage());
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            submitData();
        }
    };

    private void submitData() {
        final String nameStr = nameText.getText().toString();
        String jiyuStr = jiyuText.getText().toString();
        ApiClient.post(this, ApiConfig.ADD_CHILD, new ApiResponseHandler<LookChildEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                image = null;
                LookChildEntity u = (LookChildEntity) entity.data.get(0);
                user.setChildId(u.getChild_id());
                String oldChildInfo = user.getChild_info();
                if(!TextUtils.isEmpty(oldChildInfo) && oldChildInfo.trim().length() > 5){
                    oldChildInfo= oldChildInfo.substring(0,oldChildInfo.lastIndexOf("]"));
                    String childInfo = ",{\"nick_name\":\""+nameStr+"\",\"face\":\""+face+"\",\"child_id\":\""+u.getChild_id()+"\",\"sex\":\""+sex+"\",\"birthday\":\""+datetime+"\",\"relation\":\""+family+"\"}]";
                    user.setChild_info(oldChildInfo+childInfo);
                }else{
                    String childInfo = "[{\"nick_name\":\""+nameStr+"\",\"face\":\""+face+"\",\"child_id\":\""+u.getChild_id()+"\",\"sex\":\""+sex+"\",\"birthday\":\""+datetime+"\",\"relation\":\""+family+"\"}]";
                    user.setChild_info(childInfo);
                }
                context.cacheUserInfo(user);
                UIHelper.ToastMessage(RegisterBabyActivity.this, "添加成功");
                if(0 == flag)
                AppIntentManager.startHomeActivity(RegisterBabyActivity.this);
                else{
                    //发送广播
                    Intent intent = new Intent();
                    intent.setAction(HomeFamilyManagerActivity.CHILD_INFO);
                    sendBroadcast(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(RegisterBabyActivity.this, errorInfo.getMessage());
            }
        }, "user_id", userId, "face", face, "nick_name", nameStr, "birthday", datetime, "sex", sex, "relation", family, "hope", jiyuStr);

    }

    private void showDateTime() {
        View view = LayoutInflater.from(RegisterBabyActivity.this).inflate(R.layout.layout_datetime, null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        final DateTimePicker mPicker = (DateTimePicker) view.findViewById(R.id.mdatetime_picker);
        Button sureBtn = (Button) view.findViewById(R.id.surebtn);
        Button cacelBtn = (Button) view.findViewById(R.id.cacelbtn);

        mPicker.initDateTime(DateTimePicker.ONLYDATE, null, null, null, null, null);

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datetime = mPicker.getDateTime(DateTimePicker.ONLYDATE);
                dateText.setText(datetime);
                popupWindow.dismiss();
            }
        });
        cacelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;

            }
        });

        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        popupWindow.setAnimationStyle(R.style.PopupAnimationToast);
        popupWindow.showAtLocation(RegisterBabyActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0); // 添加popwindow显示的位置
    }
}
