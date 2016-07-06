package com.qcsh.fuxiang;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.widget.CheckBox;

import android.widget.TextView;
import android.text.Html;

import android.view.View;

import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.VersionCheckListEntity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.UpdateVersionManger;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.umeng.message.UmengRegistrar;

import java.util.List;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * @author lxz
 * @version 1.0
 * @created 2015-5-6
 */
public class AppStart extends BaseActivity {
    

	private SharedPreferences spf;
	private AppContext context;
	public static final String SYS_VERSION_PREF = "sys_start_config";
	public static final String UM_TOKEN = "um_token";
	public static final String IS_LOGIN = "is_login";
	private final String VERSION_CODE = "version_code";
	private final String IS_FIRST_LOAD = "first_load";
	private final int LOAD_TIME = 1000;

	private Handler handler ;
	private LoadMainTask loadMainTask;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	setContentView(R.layout.start);
		handler = new Handler();
		loadMainTask = new LoadMainTask();
		//开启友盟推送服务
		AppIntentManager.startPushAgent(this);
    	spf = getSharedPreferences(SYS_VERSION_PREF,Context.MODE_PRIVATE);
		context = (AppContext)getApplication();
		//checkNewVersion();
		handler.postDelayed(loadMainTask,LOAD_TIME);
	}
	private class LoadMainTask implements Runnable {
		@Override
		public void run() {
			boolean isFirstLoad = spf.getBoolean(IS_FIRST_LOAD, true);
			boolean isLogin = spf.getBoolean(IS_LOGIN, false);
			String device_token = spf.getString(AppStart.UM_TOKEN, "");
			if (isFirstLoad) {
				if(!hasShortcut()){
					addShortcut();
				}
     		spf.edit().putBoolean(IS_FIRST_LOAD, false).commit();
				//AppIntentManager.startUserGuideActivity(AppStart.this);
			}
			if(!TextUtils.isEmpty(device_token)){
				if(isLogin){
					AppIntentManager.startHomeActivity(AppStart.this);
				}else{
					AppIntentManager.startLoginActivity(AppStart.this);
				}
			}else{
				device_token = UmengRegistrar.getRegistrationId(AppStart.this);
				if(!TextUtils.isEmpty(device_token)){
					spf.edit().putString(UM_TOKEN, device_token).commit();
				}
				handler.postDelayed(loadMainTask,LOAD_TIME);
			}
		}
	}

	/**
	 * 判断是否已经创建快捷方式
	 */
	private boolean hasShortcut(){
		boolean isInstallShortcut = false;
		final ContentResolver cr = getContentResolver();
		final String AUTHORITY ="com.android.launcher.settings";
		final Uri CONTENT_URI = Uri.parse("content://" +AUTHORITY + "/favorites?notify=true");
		Cursor c = cr.query(CONTENT_URI, new String[]{"title", "iconResource"}, "title=?", new String[]{getString(R.string.app_name).trim()}, null);
		if(c!=null && c.getCount()>0){
			isInstallShortcut = true ;
		}
		return isInstallShortcut;
	}

	/**
	 * 创建快捷方式
	 */
	private void addShortcut(){
		Intent shortcutIntent = new Intent();
		shortcutIntent.setClass(this, AppStart.class);
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
		shortcut.putExtra("duplicate", 0);    	 //不允许重复创建
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.mipmap.logo);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		sendBroadcast(shortcut);
	}

   /**
	 * 检测新版本
	 */
	private void checkNewVersion(){
		ApiClient.get(this,ApiConfig.CHECK_NEW_VERSION,new ApiResponseHandler<VersionCheckListEntity>(){
			@Override
			public void onSuccess(DataEntity entity) {
				List<Object> data = entity.data;
				if (null != data&&!data.isEmpty()) {

					int curVersionCode = getCurVersion();
					final VersionCheckListEntity v = (VersionCheckListEntity) data.get(0);
					final int version = Integer.valueOf(v.getVersion());
					if (curVersionCode != 0 && curVersionCode < version) {
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(AppStart.this);
						//需要显示更新提示
						View view = getLayoutInflater().inflate(R.layout.version_update_view, null);
						assert view != null;
						TextView textView = (TextView) view.findViewById(R.id.dialog_text);
						final CheckBox checkbox = (CheckBox)view.findViewById(R.id.dis_vresion);
						textView.setText(Html.fromHtml(v.getContent()));
						alertDialog.setView(view);
						alertDialog.setTitle("发现新版本");
						alertDialog.setPositiveButton("立即升级",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										UpdateVersionManger upVersion = UpdateVersionManger.getUpdateVersionManager();
										upVersion.setVersionCode(version);
										upVersion.showDownloadDialog(AppStart.this, v.getUpload());
									}
								}).setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (checkbox.isChecked()) {
									spf.edit().putInt(VERSION_CODE, version).commit();
								}
								handler.postDelayed(loadMainTask, LOAD_TIME);
							}
						});
						alertDialog.create().show();
					}else{
						handler.postDelayed(loadMainTask, LOAD_TIME);
					}
				}
			}

			@Override
			public void onFailure(ErrorEntity errorInfo) {
				handler.postDelayed(loadMainTask, LOAD_TIME);
			}

		},"");
	}

	// 获取应用的版本号
	private int getCurVersion() {
		PackageManager packageManager = getPackageManager();
		int curVersionCode = 0 ;
		try {
			assert packageManager != null;
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			curVersionCode = spf.getInt(VERSION_CODE,packageInfo.versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return curVersionCode;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(loadMainTask);
	}
}