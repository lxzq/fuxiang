package com.qcsh.fuxiang.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aliyun.mbaas.oss.model.OSSException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.OssHandler;
import com.qcsh.fuxiang.common.OssUtils;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.CityPicker_wheelView.MWheelView;
import com.qcsh.fuxiang.widget.CityPicker_wheelView.adapter.CityPicker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * Created by wo on 15/9/14.
 */
public class HomePersonActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ScrollView scrollView;
    private LinearLayout mainLayout;

    private TextView sexBtn;
    private ImageView faceImage;
    private LinearLayout nichengLayout;
    private TextView nichengText;

    private LinearLayout addressLayout;
    private TextView addressText;

    private LinearLayout sexLayout;
    //private Spinner spiner;

    private LinearLayout codeLayout;

    private ArrayList<String> picPath;
    File mfile;

    private static String[] sexs = new String[]{"男", "女"};
    private ArrayAdapter<String> adapter;

    private View dateView;
    private TextView titleText;
    private CityPicker cityPicker;
    private MWheelView mCityPicker;
    private MWheelView mProvincePicker;
    private String city;
    private String province;

    private Drawable boyImg;
    private Drawable girlImg;
    private TextView sexText;
    private int width;
    private int height;
    private TextView address;
    private TextView face;
    private TextView nicheng;
    private TextView sex;
    private TextView ewm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_person);
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
        title.setText("个人资料");
    }

    private void initView() {
/*
        boyImg = getResources().getDrawable(R.mipmap.ic_wojia_nan2);
        boyImg.setBounds(5, 0, boyImg.getMinimumWidth(), boyImg.getMinimumHeight());

        girlImg = getResources().getDrawable(R.mipmap.ic_wojia_nv2);
        girlImg.setBounds(5, 0, girlImg.getMinimumWidth(), girlImg.getMinimumHeight());*/
//        if (!StringUtils.isEmpty(getCurrentUser().getSex())) {
//            if ("1".equals(getCurrentUser().getSex())) {
//
//            } else if ("0".equals(getCurrentUser().getSex())) {
//                sexs = new String[]{"女", "男"};
//            }
//        }


        //Dip -> Int
        width = Utils.dip2int(HomePersonActivity.this, 37);
        height = Utils.dip2int(HomePersonActivity.this, 23);

        Drawable addImg = getResources().getDrawable(R.mipmap.ic_wojia_add);
        addImg.setBounds(0, 0, width, height);
        address = (TextView) findViewById(R.id.ic_address2);
        address.setCompoundDrawables(addImg, null, null, null);

        Drawable faceImg = getResources().getDrawable(R.mipmap.ic_wojia_txpic);
        faceImg.setBounds(0, 0, width, height);
        face = (TextView) findViewById(R.id.ic_face2);
        face.setCompoundDrawables(faceImg, null, null, null);

        Drawable nichengImg = getResources().getDrawable(R.mipmap.ic_wojia_name);
        nichengImg.setBounds(0, 0, width, height);
        nicheng = (TextView) findViewById(R.id.ic_nicheng2);
        nicheng.setCompoundDrawables(nichengImg, null, null, null);

        Drawable sexImg = getResources().getDrawable(R.mipmap.ic_wojia_xb);
        sexImg.setBounds(0, 0, width, height);
        sex = (TextView) findViewById(R.id.ic_sex2);
        sex.setCompoundDrawables(sexImg, null, null, null);



        Drawable ewmImg = getResources().getDrawable(R.mipmap.ic_wojia_ewm);
        ewmImg.setBounds(0, 0, width, height);
        sex = (TextView) findViewById(R.id.ic_code2);
        sex.setCompoundDrawables(ewmImg, null, null, null);

        sexBtn = (TextView) findViewById(R.id.btn_sex);
        sexBtn.getBackground().setAlpha(30);

        boyImg = getResources().getDrawable(R.mipmap.ic_wojia_nan2);
        boyImg.setBounds(0, 0, boyImg.getIntrinsicWidth(), boyImg.getIntrinsicHeight());

        girlImg = getResources().getDrawable(R.mipmap.ic_wojia_nv2);
        girlImg.setBounds(0, 0, girlImg.getIntrinsicWidth(), girlImg.getIntrinsicHeight());

        faceImage = (ImageView) findViewById(R.id.iv_face);

        nichengText = (TextView) findViewById(R.id.ic_nicheng);

        addressLayout = (LinearLayout) findViewById(R.id.layout_address);
        addressText = (TextView) findViewById(R.id.ic_address);

        sexText = (TextView) findViewById(R.id.ic_sex1);

        sexLayout = (LinearLayout) findViewById(R.id.layout_sex1);
        //spiner = (Spinner) findViewById(R.id.ic_sex);

        faceImage.setOnClickListener(this);

        nichengLayout = (LinearLayout) findViewById(R.id.layout_nicheng);
        nichengLayout.setOnClickListener(this);

        addressLayout.setOnClickListener(this);
        sexLayout.setOnClickListener(this);

        //spiner.setPrompt("选择性别");
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexs);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spiner.setAdapter(adapter);

        /*spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                //修改spinner字体
                TextView tv = (TextView) view;
                tv.setTextSize(14.0f);
                tv.setTextColor(getResources().getColor(R.color.text_medium));
                tv.setGravity(Gravity.CENTER);
                sexBtn.setText(sexs[position]);
                switch (sexs[position]) {
                    case "男": {
//                        sexBtn.setCompoundDrawables(boyImg, null, null, null);
//                        user.setSex("1");
//                        getAppContext().cacheUserInfo(user);
                        editUser(null, null, "1", null);
                    }
                    break;

                    case "女": {
//                        sexBtn.setCompoundDrawables(girlImg, null, null, null);
//                        user.setSex("0");
//                        getAppContext().cacheUserInfo(user);
                        editUser(null, null, "0", null);
                    }
                    break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        codeLayout = (LinearLayout) findViewById(R.id.layout_code);
        codeLayout.setOnClickListener(this);

        mainLayout = (LinearLayout) findViewById(R.id.mainlayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(mainLayout.getTop(), mainLayout.getBottom());
        setView();
    }

    private void setView() {
        String sex = getCurrentUser().getSex();
        if ("0".equals(sex)) {
            sexBtn.setCompoundDrawables(girlImg, null, null, null);
            sexBtn.setText("女");
            sexText.setText("女");
        } else if ("1".equals(sex)) {
            sexBtn.setCompoundDrawables(boyImg, null, null, null);
            sexBtn.setText("男");
            sexText.setText("男");
        } else {
            sexBtn.setCompoundDrawables(null, null, null, null);
            sexBtn.setText("");
            sexText.setText("");
        }

        if (!StringUtils.isEmpty(getCurrentUser().getUserface())) {
            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(getCurrentUser().getUserface()), faceImage);
        } else {
            faceImage.setImageDrawable(getResources().getDrawable(R.mipmap.defalut_user_face));
        }

        if (!StringUtils.isEmpty(getCurrentUser().getNickname())) {
            nichengText.setText(getCurrentUser().getNickname());
        } else {
            nichengText.setText("");
        }
        if (!StringUtils.isEmpty(getCurrentUser().getLocation())) {
            addressText.setText(getCurrentUser().getLocation());
        } else {
            addressText.setText("");
        }
    }

    //回调传值修改昵称
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                picPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
//                resetImage();
                upLoadFace();
            }
        } else if (resultCode == 1001 && requestCode == 1000) {

            String name = data.getStringExtra("Name");
            if (name.length() > 0)
//                nichengText.setText(name);
//            user.setNickname(name);
//            getAppContext().cacheUserInfo(user);
                editUser(name, null, null, null);

        }
    }
/*
    private void resetImage() {
        if (null != picPath && picPath.size() > 0) {
            final String path = picPath.get(0);
            Bitmap picBitmap = ImageUtils.getBitmapByPath(path);
            Bitmap bitmap = ImageUtils.ratioBitmap(picBitmap, AppConfig.IMAGE_WIDTH, AppConfig.IMAGE_HEIGHT);
            CuttingBitmap.toRoundBitmap(bitmap);
            faceImage.setImageBitmap(bitmap);
            upLoadFace();

        }
    }*/

    private void upLoadFace() {
        showProgress();
        OssUtils.upload(picPath.get(0), AppConfig.OSS_UPLOAD.images.toString(), new OssHandler() {
            @Override
            public void onSuccess(String strPath) {
                closeProgress();
                Message msg = new Message();
                msg.obj = strPath;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(String strPath, OSSException ossException) {
                closeProgress();
            }
        });
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String strPath = (String) msg.obj;
            editUser(null, strPath, null, null);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_face:

                AppIntentManager.startImageSelectorForResult(HomePersonActivity.this, picPath, 1, MultiImageSelectorActivity.IMAGE_FILE);
                break;

            case R.id.layout_nicheng:
                Intent intent = new Intent(HomePersonActivity.this, HomeEditNameActivity.class);
                startActivityForResult(intent, 1000);
                break;

            case R.id.layout_address:
                //地址选择器
                LayoutInflater inflater = LayoutInflater.from(HomePersonActivity.this);
                dateView = inflater.inflate(R.layout.citypicker_view, null);
                cityPicker = (CityPicker) dateView.findViewById(R.id.citypicker);
                mProvincePicker = (MWheelView) dateView.findViewById(R.id.province);
                mCityPicker = (MWheelView) cityPicker.findViewById(R.id.city);
                titleText = (TextView) dateView.findViewById(R.id.ic_address);
                titleText.setText(addressText.getText().toString());
                new AlertDialog.Builder(HomePersonActivity.this)
                        .setTitle("选择地点")
                        .setView(dateView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                city = mCityPicker.getSelectedText().toString();
                                province = mProvincePicker.getSelectedText().toString();
                                //addressText.setText(province + "." + city);
                                titleText.setText(province + "." + city);
                                editUser(null, null, null, province + "." + city);

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;

            case R.id.layout_sex1:
                String s = getCurrentUser().getSex();
                int sex = 1;
                if (!StringUtils.isEmpty(s)) {
                    sex = Integer.valueOf(s);
                }
                new AlertDialog.Builder(HomePersonActivity.this).setTitle("选择性别")
                        .setSingleChoiceItems(sexs, 1 - sex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editUser(null, null, 1 - which + "", null);
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.layout_code:
                AppIntentManager.startHomeCodeActivity(HomePersonActivity.this);
                break;
        }

    }

    private void editUser(String nickname, String userface, String sex, String location) {
        showProgress();
        ApiClient.post(HomePersonActivity.this, ApiConfig.EDIT_USER, new ApiResponseHandler<User>() {
            @Override
            public void onSuccess(DataEntity entity) {
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
                getAppContext().cacheUserInfo(user);
                setView();
                sendUserInfoChangeBroadcast();
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                UIHelper.ToastMessage(HomePersonActivity.this, errorInfo.getMessage());
            }
        }, "userId", getCurrentUser().getId(), "nickname", nickname, "userface", userface, "sex", sex, "location", location);
    }

    private void sendUserInfoChangeBroadcast() {
        Intent intent = new Intent();
        intent.setAction(HomeMainFragment.USER_INFO_CHANGE);
        sendBroadcast(intent);
    }
}
