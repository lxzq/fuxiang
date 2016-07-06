package com.qcsh.fuxiang.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.AppManager;
import com.qcsh.fuxiang.AppStart;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.FragmentTabAdapter;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.bang.BangFragment;
import com.qcsh.fuxiang.ui.home.HomeFamilyManagerActivity;
import com.qcsh.fuxiang.ui.home.HomeMainFragment;
import com.qcsh.fuxiang.ui.leyuan.LeyuanFragment;
import com.qcsh.fuxiang.ui.look.LookMapFragment;
import com.qcsh.fuxiang.ui.share.ShareFragment;
import com.qcsh.fuxiang.ui.umengmessage.MyPushIntentService;


import java.util.ArrayList;

/**
 * 系统主界面
 * Created by LXZ on 2015/8/11.
 */
public class HomeActivity extends BaseActivity {

    private LookMapFragment tab1;
    private BangFragment tab2;
    private ShareFragment tab3;
    private HomeMainFragment tab4;

    private LeyuanFragment tab5;
    private RadioGroup tabHost;
    /**
     * 我的tab圆点提示
     */
    private ImageView host4Tips;
    private ImageView host1Tips;
    /**
     * 主页我的提示圆点广播
     */
    public static final String HOME_TABS_4_TIPS_VISIBILITY = "com.qcsh.fuxiang.ui.home.tab4Tips";
    /**
     * 主页 看着圆点提示
     */
    public static final String HOME_TABS_1_TIPS_VISIBILITY = "com.qcsh.fuxiang.ui.home.tab1Tips";
    private TipBroadcastReceiver tipBroadcastReceiver;

    private int exitApp = 0;
    private int curIndex;

    private ArrayList<Fragment> fragments;
    private SharedPreferences sharedPreferences;
    private FragmentTabAdapter tabAdapter;
    private AppContext appContext;
    private RadioButton host1;
    private RadioButton host1Add;
    private int length40;
    private int length25;
    private int length22;
    private RadioButton host2;
    private RadioButton host3;
    private RadioButton host4;
    private Drawable addImg;
    private Drawable img2;
    private Drawable img1;
    private Drawable img2ed;
    private Drawable img3;
    private Drawable img3ed;
    private Drawable img4;
    private Drawable img4ed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        appContext = (AppContext) getApplication();

        isLogin();

        sharedPreferences = getSharedPreferences(AppStart.SYS_VERSION_PREF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(AppStart.IS_LOGIN, true).commit();
        fragments = new ArrayList<Fragment>();

        tab1 = new LookMapFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MyPushIntentService.PUSH_MESSAGE, getIntent().getStringExtra(MyPushIntentService.PUSH_MESSAGE));
        tab1.setArguments(bundle);

        tab2 = new BangFragment();
        tab3 = new ShareFragment();
        tab4 = new HomeMainFragment();

        tab5 = new LeyuanFragment();


        fragments.add(tab1);
        fragments.add(tab2);
        fragments.add(tab3);
        fragments.add(tab4);
        fragments.add(tab5);

        tipBroadcastReceiver = new TipBroadcastReceiver();
        //注册提示广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(HOME_TABS_4_TIPS_VISIBILITY);
        filter.addAction(HOME_TABS_1_TIPS_VISIBILITY);
        registerReceiver(tipBroadcastReceiver, filter);

        initView();
        //启动本地定位服务
        AppIntentManager.startMyLocationService(this);

    }

    @Override
    protected void onResume() {
        exitApp = 0;
        sharedPreferences.edit().putBoolean(MyPushIntentService.IS_EXIT, false).commit();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消提示广播
        unregisterReceiver(tipBroadcastReceiver);
        sharedPreferences.edit().putBoolean(MyPushIntentService.IS_EXIT, true).commit();
    }

    private void initView() {
        length40 = Utils.dip2int(HomeActivity.this, 40);
        length25 = Utils.dip2int(HomeActivity.this, 25);
        length22 = Utils.dip2int(HomeActivity.this, 22);

        tabHost = (RadioGroup) findViewById(R.id.tab_host);
        host4Tips = (ImageView) findViewById(R.id.host4_tips);
        host1Tips = (ImageView) findViewById(R.id.host1_tips);
        host1 = (RadioButton) findViewById(R.id.host1);
        host2 = (RadioButton) findViewById(R.id.host2);
        host3 = (RadioButton) findViewById(R.id.host3);
        host4 = (RadioButton) findViewById(R.id.host4);
        host1Add = (RadioButton) findViewById(R.id.host1_add);

        addImg = getResources().getDrawable(R.mipmap.btn_kanzhe_fabu);
        addImg.setBounds(0, 0, length40, length40);
        host1Add.setCompoundDrawables(addImg, null, null, null);
        host1Add.setPadding(Utils.dip2px(HomeActivity.this, 16), 0, 0, 0);

        img1 = getResources().getDrawable(R.mipmap.tabbar_btn_kanzhe_normal);
        img1.setBounds(0, 0, length25, length22);
        host1.setCompoundDrawables(null, img1, null, null);

        img2 = getResources().getDrawable(R.mipmap.tabbar_btn_bang_normal);
        img2.setBounds(0, 0, length25, length22);
        img2ed = getResources().getDrawable(R.mipmap.tabbar_btn_bang_selected);
        img2ed.setBounds(0, 0, length25, length22);
        host2.setCompoundDrawables(null, img2, null, null);

        img3 = getResources().getDrawable(R.mipmap.tabbar_btn_shai_normal);
        img3.setBounds(0, 0, length25, length22);
        img3ed = getResources().getDrawable(R.mipmap.tabbar_btn_shai_selected);
        img3ed.setBounds(0, 0, length25, length22);
        host3.setCompoundDrawables(null, img3, null, null);

        img4 = getResources().getDrawable(R.mipmap.tabbar_btn_wojia_normal);
        img4.setBounds(0, 0, length25, length22);
        img4ed = getResources().getDrawable(R.mipmap.tabbar_btn_wojia_selected);
        img4ed.setBounds(0, 0, length25, length22);
        host4.setCompoundDrawables(null, img4, null, null);

        tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, tabHost);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                curIndex = index;
                initButtonTab(checkedId);
            }
        });
    }

    private void initButtonTab(int id) {
        if (id == R.id.host1 || id == R.id.host1_add) {
            host1.setVisibility(View.GONE);
            host1Add.setVisibility(View.VISIBLE);
            host1Tips.setVisibility(View.GONE);
            host2.setCompoundDrawables(null, img2, null, null);
            host3.setCompoundDrawables(null, img3, null, null);
            host4.setCompoundDrawables(null, img4, null, null);
        } else {
            host1.setVisibility(View.VISIBLE);
            host1Add.setVisibility(View.GONE);
        }
        host1Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IHomeTabListener iHomeTabListener = (IHomeTabListener) tab1;
                iHomeTabListener.onAction();
            }
        });
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (exitApp == 0) {
                    exitApp = 1;
                    UIHelper.ToastMessage(this, getString(R.string.exit_app));
                    return true;
                }
                AppManager.getAppManager().finishAllActivity();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    private class TipBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(HOME_TABS_4_TIPS_VISIBILITY)) {
                host4Tips.setVisibility(View.VISIBLE);
            } else if (action.equals(HOME_TABS_1_TIPS_VISIBILITY)) {
                if (curIndex != 0) {
                    host1Tips.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 主页tab切换卡监听器
     */
    public interface IHomeTabListener {
        void onAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (0 == curIndex)
            tab1.onActivityResult(requestCode, resultCode, data);
        else if(2 == curIndex){
            tab3.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void isLogin() {
        if (!appContext.isLogin()) {
            User user = appContext.getCacheLoginInfo();
            if (null != user) {
                appContext.cacheUserInfo(user);
            }
        }
    }

}
