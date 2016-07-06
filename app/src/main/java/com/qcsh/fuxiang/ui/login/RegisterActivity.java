package com.qcsh.fuxiang.ui.login;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aliyun.mbaas.oss.model.OSSException;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.BaseEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.CuttingBitmap;
import com.qcsh.fuxiang.common.ImageUtils;
import com.qcsh.fuxiang.common.OssHandler;
import com.qcsh.fuxiang.common.OssUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by wo on 15/9/8.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ImageButton faceImage;
    private EditText nameText;
    //private EditText addressText;
    //private EditText blessText;
    //private ImageView agreeBtn;

    private RadioGroup raGroup;
    private RadioButton boyBtn;
    private RadioButton girlBtn;

    // private Boolean isAgree = false;

    private ArrayList<String> picPath;
    File mfile;
    //    private TextView sureBtn;
    private String tel;
    private String psw;
    private String key;
    private int sex = 0;//0:女   1：男

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_register);
        tel = getIntent().getStringExtra("phone");
        psw = getIntent().getStringExtra("password");
        key = getIntent().getStringExtra("code");
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
        rightBtn.setText("继续");
        rightBtn.setOnClickListener(this);
        title.setText("完善资料");
    }

    private void initView() {

        faceImage = (ImageButton) findViewById(R.id.btn_face);
        faceImage.setOnClickListener(this);


        nameText = (EditText) findViewById(R.id.ic_name);
//        addressText = (EditText) findViewById(R.id.ic_address);
//        blessText = (EditText)findViewById(R.id.ic_bless);
//        sureBtn = (TextView) findViewById(R.id.sure_btn);
//        sureBtn.setOnClickListener(this);
//        agreeBtn = (ImageView) findViewById(R.id.btn_select);

        boyBtn = (RadioButton) findViewById(R.id.btn_boy);
        girlBtn = (RadioButton) findViewById(R.id.btn_girl);
        raGroup = (RadioGroup) findViewById(R.id.layout_select);
        raGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_boy:
                        sex = 1;
                        break;
                    case R.id.btn_girl:
                        sex = 0;
                        break;
                }
            }
        });


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
            final String path = picPath.get(0);
            Bitmap picBitmap = ImageUtils.getBitmapByPath(path);
            Bitmap bitmap = ImageUtils.ratioBitmap(picBitmap, AppConfig.IMAGE_WIDTH, AppConfig.IMAGE_HEIGHT);
            Bitmap roundBitmap = CuttingBitmap.toRoundBitmap(bitmap);
            faceImage.setImageBitmap(roundBitmap);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
//            case R.id.sure_btn:
//                upLoadFace();
//                break;
            case R.id.action_bar_action:
                upLoadFace();
                break;
            case R.id.btn_face:
                AppIntentManager.startImageSelectorForResult(RegisterActivity.this, picPath, 1, MultiImageSelectorActivity.IMAGE_FILE);
                break;

        }

    }

    private void upLoadFace() {
        showProgress();
        OssUtils.upload(picPath.get(0), AppConfig.OSS_UPLOAD.images.toString(), new OssHandler() {
            @Override
            public void onSuccess(String strPath) {
                doRegister(strPath, nameText.getText().toString());
            }

            @Override
            public void onFailure(String strPath, OSSException ossException) {

            }
        });
    }

    private void doRegister(String userface, String nickname) {
        showProgress();
        ApiClient.post(RegisterActivity.this, ApiConfig.SYS_REGISTER, new ApiResponseHandler<User>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                List data = entity.data;
                User user = (User) data.get(0);
                getAppContext().cacheUserInfo(user);
                AppIntentManager.startRegisterBabyActivity(RegisterActivity.this,0);
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(RegisterActivity.this, errorInfo.getMessage());
            }
        }, "phone", tel, "password", psw, "nickname", nickname, "code", key, "userface", userface, "sex", sex + "");
    }
}
