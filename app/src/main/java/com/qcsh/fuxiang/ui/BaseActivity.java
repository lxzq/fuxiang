package com.qcsh.fuxiang.ui;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.qcsh.fuxiang.AppContext;
import com.qcsh.fuxiang.AppManager;
import com.qcsh.fuxiang.bean.User;
import com.qcsh.fuxiang.widget.Loading;
import com.qcsh.fuxiang.widget.XListView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class BaseActivity extends ActionBarActivity {

    private Loading loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    protected User getCurrentUser() {
        return getAppContext().getCurUser();
    }

    protected AppContext getAppContext(){
        AppContext appContext = (AppContext) this.getApplication();
        return appContext;
    }

    protected void showProgress() {
        if (loading == null)
            loading = Loading.Show(this, null, true, null);
    }

    protected void showProgress(String str) {
        if (loading == null) {
            loading = Loading.Show(this, str, true, null);
        } else {
            loading.setMessage(str);
            loading.show();
        }
    }

    protected void closeProgress() {
        if (loading != null) {
            loading.dismiss();
            loading = null;
        }
    }

    protected void stopLoading(XListView customListView) {
        if (customListView != null) {
            customListView.stopRefresh();
            customListView.stopLoadMore();
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
                    DateFormat.MEDIUM, Locale.CHINA);
            String strDate = df.format(new Date());
            customListView.setRefreshTime(strDate);// 设置更新时间
        }
    }

    /**
     * 设置为4.4的沉浸式状态栏
     * @param root 当前布局文件中的根view
     * @param res 状态栏颜色
     */
    public void setchenjin(View root,int res) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            BaseActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            mTintManager.setTintColor(res);
            mTintManager.setTintResource(res);
            mTintManager.setStatusBarTintResource(res);
            //透明导航栏
            // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (root != null) {
                root.setPadding(0, getStatusBarHeight(), 0, 0);
                setContentView(root);
            }
        } else {
            setContentView(root);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = BaseActivity.this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = BaseActivity.this.getResources().getDimensionPixelSize(resourceId);
        }
        Log.i("StatusBarHeight", result + "");
        return result;
    }

    /**
     * 设置为4.4的沉浸式状态栏，（有ToolBar时）
     */
    public void applyKitKatTranslucency(int res) {

        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            mTintManager.setTintColor(res);
            mTintManager.setTintResource(res);
            mTintManager.setStatusBarTintResource(res);
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
