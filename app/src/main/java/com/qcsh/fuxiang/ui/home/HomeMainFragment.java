package com.qcsh.fuxiang.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.BaseFragment;

/**
 * 我的
 * Created by wo on 15/9/14.
 */
public class HomeMainFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;


    private ImageView faceImage;
    private TextView nameText;
    private TextView sexText1,sexText0;
    private Button editButton;

    private LinearLayout myBangBi;
    private LinearLayout myBangBang;
    private LinearLayout myShaiShai;
    private LinearLayout mySave;


    private LinearLayout familyManager;
    private LinearLayout partenerManager;
    private LinearLayout set;

    private TextView myBangBiText;
    private TextView myBangBangText;
    private TextView myHuoDongText;
    private TextView mySaveText;
    private TextView familyText;
    private TextView partenerText;
    private TextView setText;


    private View mBaseView;
    private User user;
    private int width;
    private int height;

    public static final String USER_INFO_CHANGE = "com.qcsh.fuxiang.ui.home.change_user";

    private ChangeUserInfoReceiver changeUserInfoReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeUserInfoReceiver = new ChangeUserInfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(USER_INFO_CHANGE);
        getActivity().registerReceiver(changeUserInfoReceiver,filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.home_main, container, false);
        initToolBar();
        initView();
        setViewData();
        return mBaseView;
    }

    private void initToolBar() {
        leftBtn = (ImageButton) mBaseView.findViewById(R.id.action_bar_back);
        title = (TextView) mBaseView.findViewById(R.id.action_bar_title);
        rightBtn = (Button) mBaseView.findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.INVISIBLE);
        rightBtn.setVisibility(View.INVISIBLE);
        title.setText("我家");
    }

    private void initView() {

        faceImage = (ImageView) mBaseView.findViewById(R.id.iv_face);
        faceImage.setOnClickListener(this);


        //Dip -> Int
        width = Utils.dip2int(getActivity(), 37);
        height = Utils.dip2int(getActivity(), 23);

        Drawable bangbiImg = getResources().getDrawable(R.mipmap.ic_wojia_bangbi);
        bangbiImg.setBounds(0, 0, width, height);
        myBangBiText = (TextView)mBaseView.findViewById(R.id.ic_bangbi);
        myBangBiText.setCompoundDrawables(bangbiImg,null,null,null);

        Drawable bangbbangImg = getResources().getDrawable(R.mipmap.ic_wojia_bangbang);
        bangbbangImg.setBounds(0,0,width,height);
        myBangBangText = (TextView)mBaseView.findViewById(R.id.ic_bangbang);
        myBangBangText.setCompoundDrawables(bangbbangImg, null, null, null);

        Drawable huoDongImg = getResources().getDrawable(R.mipmap.ic_wojia_shaishai);
        huoDongImg.setBounds(0,0,width,height);
        myHuoDongText = (TextView)mBaseView.findViewById(R.id.ic_huodong);
        myHuoDongText.setCompoundDrawables(huoDongImg, null, null, null);

        Drawable saveImg = getResources().getDrawable(R.mipmap.ic_wojia_shoucang);
        saveImg.setBounds(0,0,width,height);
        mySaveText = (TextView)mBaseView.findViewById(R.id.ic_save);
        mySaveText.setCompoundDrawables(saveImg, null, null, null);

        Drawable familyImg = getResources().getDrawable(R.mipmap.ic_wojia_jiaren);
        familyImg.setBounds(0,0,width,height);
        familyText = (TextView)mBaseView.findViewById(R.id.ic_familymanager);
        familyText.setCompoundDrawables(familyImg, null, null, null);

        Drawable partnerImg = getResources().getDrawable(R.mipmap.ic_wojia_huoban);
        partnerImg.setBounds(0,0,width,height);
        partenerText = (TextView)mBaseView.findViewById(R.id.ic_partenermanager);
        partenerText.setCompoundDrawables(partnerImg, null, null, null);

        Drawable setImg = getResources().getDrawable(R.mipmap.ic_wojia_shezhi);
        setImg.setBounds(0,0,width,height);
        setText = (TextView)mBaseView.findViewById(R.id.ic_set);
        setText.setCompoundDrawables(setImg, null, null, null);

        nameText = (TextView) mBaseView.findViewById(R.id.ic_name);
        sexText1 = (TextView) mBaseView.findViewById(R.id.ic_sex_1);
        sexText0 = (TextView) mBaseView.findViewById(R.id.ic_sex_0);
        editButton = (Button) mBaseView.findViewById(R.id.btn_edit);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppIntentManager.startHomePersonActivity(getActivity());
            }
        });

        myBangBi = (LinearLayout) mBaseView.findViewById(R.id.layout_bangbi);
        myBangBi.setOnClickListener(this);


        myBangBang = (LinearLayout) mBaseView.findViewById(R.id.layout_bangbbang);
        myBangBang.setOnClickListener(this);


        myShaiShai = (LinearLayout) mBaseView.findViewById(R.id.layout_huodong);
        myShaiShai.setOnClickListener(this);


        mySave = (LinearLayout) mBaseView.findViewById(R.id.layout_save);
        mySave.setOnClickListener(this);


        familyManager = (LinearLayout) mBaseView.findViewById(R.id.layout_familymanager);
        familyManager.setOnClickListener(this);

        partenerManager = (LinearLayout) mBaseView.findViewById(R.id.layout_partenermanager);
        partenerManager.setOnClickListener(this);

        set = (LinearLayout) mBaseView.findViewById(R.id.layout_set);
        set.setOnClickListener(this);

        mBaseView.findViewById(R.id.ewm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppIntentManager.startHomeCodeActivity(getActivity());
            }
        });
    }

    private void setViewData(){
        user = getCurrentUser();
        String face = user.getUserface();
        String nickname = user.getNickname();
        String sex = user.getSex();
        nameText.setText(nickname);
        if (!StringUtils.isEmpty(face)) {
            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), faceImage);
        }

        sexText1.setVisibility(View.GONE);
        sexText0.setVisibility(View.GONE);
        if("1".equals(sex)){
            sexText1.setVisibility(View.VISIBLE);
        }else if("0".equals(sex)) {
            sexText0.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_home:

                AppIntentManager.startHomePersonActivity(getActivity());
                break;

            case R.id.layout_bangbi:

                AppIntentManager.startHomeMyBangBiActivity(getActivity());
                break;

            case R.id.layout_bangbbang:

                AppIntentManager.startHomeMyBangBangActivity(getActivity());
                break;

            case R.id.layout_huodong:

//                AppIntentManager.startHomeMyHuoDongActivity(getActivity());
                AppIntentManager.startHomeMyShaiShaiActivity(getActivity());
                break;

            case R.id.iv_face:
               // AppIntentManager.startHomeMyShaiShaiActivity(getActivity());
                break;

            case R.id.layout_save:
                AppIntentManager.startHomeMySaveActivity(getActivity());
                break;

            case R.id.layout_familymanager:

                AppIntentManager.startHomeFamilyMangerActivity(getActivity());
                break;

            case R.id.layout_partenermanager:

                AppIntentManager.startHomePartnerMangerActivity(getActivity());
//                AppIntentManager.startHomeBabyDetailActivity(getActivity(),childId);
                break;

            case R.id.layout_set:

                AppIntentManager.startHomeSettingActivity(getActivity());
                break;

        }
    }

    private class ChangeUserInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(changeUserInfoReceiver);
    }
}
