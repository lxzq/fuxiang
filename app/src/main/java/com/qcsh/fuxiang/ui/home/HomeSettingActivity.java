package com.qcsh.fuxiang.ui.home;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.AppManager;
import com.qcsh.fuxiang.AppStart;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.VersionCheckListEntity;
import com.qcsh.fuxiang.common.DataCleanManager;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.UpdateVersionManger;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.MyConfirmDialog;
import com.yixia.camera.demo.utils.ToastUtils;

import android.content.Context;

import java.io.File;
import java.util.List;

/**
 * Created by shenbinbin on 15/9/15.
 */
public class HomeSettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private LinearLayout clearLayout;
    private LinearLayout resetLayout;
    private LinearLayout remindLayout;
    private LinearLayout renewLayout;
    private LinearLayout infoLayout;
    private Button quitBtn;
    private TextView renewText;
    String size;

    private SharedPreferences spf;
    public static final String SYS_VERSION_PREF = "sys_start_config";
    private final String VERSION_CODE = "version_code";
    private LinearLayout bindLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_setting);
        spf = getSharedPreferences(SYS_VERSION_PREF, Context.MODE_PRIVATE);
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
        title.setText("设置");
    }

    private void initView() {

        clearLayout = (LinearLayout) findViewById(R.id.layout_clear);
        clearLayout.setOnClickListener(this);
        resetLayout = (LinearLayout) findViewById(R.id.layout_reset);
        resetLayout.setOnClickListener(this);
        remindLayout = (LinearLayout) findViewById(R.id.layout_remind);
        remindLayout.setOnClickListener(this);
        bindLayout = (LinearLayout) findViewById(R.id.layout_bind);
        bindLayout.setOnClickListener(this);
        renewLayout = (LinearLayout) findViewById(R.id.layout_renew);
        renewLayout.setOnClickListener(this);
        infoLayout = (LinearLayout) findViewById(R.id.layout_info);
        infoLayout.setOnClickListener(this);
        quitBtn = (Button) findViewById(R.id.btn_quit);
        quitBtn.setOnClickListener(this);

        renewText = (TextView) findViewById(R.id.ic_renewtext);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.layout_clear:

                /**
                 * 清除缓存
                 */
                try {
                    size = DataCleanManager.getFormatSize(DataCleanManager.getFolderSize(getCacheDir())
                            + DataCleanManager.getFolderSize(getExternalCacheDir()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtils.showLongToast(HomeSettingActivity.this, "清除缓存" + size);
                DataCleanManager.cleanInternalCache(HomeSettingActivity.this);
                DataCleanManager.cleanExternalCache(HomeSettingActivity.this);
                break;

            case R.id.layout_reset:


                if (!StringUtils.isEmpty(getCurrentUser().getPhone())) {

                    AppIntentManager.startHomeChangeActivity(HomeSettingActivity.this);
                } else {

                    UIHelper.ToastMessage(HomeSettingActivity.this, "该用户尚未进行手机号绑定");
                }


                break;

            case R.id.layout_remind:

                AppIntentManager.startHomeRemindActivity(HomeSettingActivity.this);
                break;
            case R.id.layout_bind:

                AppIntentManager.startBindNumberActivity(HomeSettingActivity.this);
                break;

            case R.id.layout_renew:

                /**
                 * 检测版本更新
                 */
                checkNewVersion();
                break;

            case R.id.layout_info:

                /**
                 * 关于家家帮
                 */
                AppIntentManager.startHomejjbIntroduceActivity(HomeSettingActivity.this);
                break;

            case R.id.btn_quit: {


                /**
                 * 退出当前账号
                 */
                MyConfirmDialog dialog = new MyConfirmDialog(this, "退出当前账号", "确定退出账号吗？不再逗留会儿了吗💔",

                        new MyConfirmDialog.OnCancelDialogListener() {
                            @Override
                            public void onCancel() {

                            }
                        },

                        new MyConfirmDialog.OnConfirmDialogListener() {

                            @Override
                            public void onConfirm() {
                                AppManager.getAppManager().AppExit(HomeSettingActivity.this);
                            }
                        }
                );

                dialog.show();
            }

            break;
        }
    }


    /**
     * 检测新版本
     */
    private void checkNewVersion() {
        ApiClient.get(this, ApiConfig.CHECK_NEW_VERSION, new ApiResponseHandler<VersionCheckListEntity>() {
            @Override
            public void onSuccess(DataEntity entity) {
                List<Object> data = entity.data;
                if (null != data && !data.isEmpty()) {

                    int curVersionCode = getCurVersion();
                    final VersionCheckListEntity v = (VersionCheckListEntity) data.get(0);
                    final int version = Integer.valueOf(v.getVersion());
                    if (curVersionCode != 0 && curVersionCode < version) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeSettingActivity.this);
                        //需要显示更新提示
                        View view = getLayoutInflater().inflate(R.layout.version_update_view, null);
                        assert view != null;
                        TextView textView = (TextView) view.findViewById(R.id.dialog_text);
                        final CheckBox checkbox = (CheckBox) view.findViewById(R.id.dis_vresion);
                        textView.setText(Html.fromHtml(v.getContent()));
                        alertDialog.setView(view);
                        alertDialog.setTitle("发现新版本");
                        alertDialog.setPositiveButton("立即升级",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UpdateVersionManger upVersion = UpdateVersionManger.getUpdateVersionManager();
                                        upVersion.setVersionCode(version);
                                        upVersion.showDownloadDialog(HomeSettingActivity.this, v.getUpload());
                                    }
                                }).setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (checkbox.isChecked()) {
                                    spf.edit().putInt(VERSION_CODE, version).commit();
                                }

                            }
                        });
                        alertDialog.create().show();
                    } else {

                        ToastUtils.showLongToast(HomeSettingActivity.this, "已是最新版本!");
                    }
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {

            }

        }, "");
    }

    /**
     * 获取应用的版本号
     */
    private int getCurVersion() {
        PackageManager packageManager = getPackageManager();
        int curVersionCode = 0;
        try {
            assert packageManager != null;
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            curVersionCode = spf.getInt(VERSION_CODE, packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return curVersionCode;
    }


}
