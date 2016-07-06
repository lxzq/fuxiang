package com.qcsh.fuxiang.ui.home;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;

/**
 * 我的二维码
 * Created by wo on 15/9/17.
 */
public class HomeCodeActivity extends BaseActivity {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ImageView faceImage;
    private TextView nameText;
    private ImageView codeImg;
    private TextView sexText_0,sexText_1;
    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_code);
        user = getCurrentUser();
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
        title.setText("我的二维码");
    }

    private void initView() {
        faceImage = (ImageView)findViewById(R.id.iv_face);
        if (!StringUtils.isEmpty(getCurrentUser().getUserface())) {
            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(getCurrentUser().getUserface()), faceImage);
        }
        nameText = (TextView)findViewById(R.id.ic_name);
        nameText.setText(getCurrentUser().getNickname());
        sexText_0 = (TextView)findViewById(R.id.ic_sex_0);
        sexText_1 = (TextView)findViewById(R.id.ic_sex_1);
        String sex = user.getSex();
        if("0".equals(sex)){
            sexText_0.setVisibility(View.VISIBLE);
        }else{
            sexText_1.setVisibility(View.VISIBLE);
        }
        String userId = user.getId();
        String nickName = getCurrentUser().getNickname();
        codeImg = (ImageView)findViewById(R.id.iv_code);
        int w = 300;
        int h = 300;
        Bitmap bitmap = UIHelper.createQRImage("{\"id\":\""+userId+"\",\"name\":\""+nickName+"\"}", w, h);
        codeImg.setImageBitmap(bitmap);
    }
}
