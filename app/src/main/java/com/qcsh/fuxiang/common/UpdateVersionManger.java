package com.qcsh.fuxiang.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.qcsh.fuxiang.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;



public class UpdateVersionManger {
	private static UpdateVersionManger updateVersionManger;
	 //终止标记
    private boolean interceptFlag;
    private Context mContext;
    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int versionCode;
	//下载包保存路径
    private String savePath = "";
	//apk保存完整路径
	private String apkFilePath = "";
	//临时下载文件路径
	private String tmpFilePath = "";
	//下载文件大小
	private String apkFileSize;
	//已下载文件大小
	private String tmpFileSize;
	//进度条
    private ProgressBar mProgress;
    //显示下载数值
    private TextView mProgressText;
    private TextView mProgressText2;
  
    //进度值
    private int progress;
    //下载对话框
  	private Dialog downloadDialog;
	public static UpdateVersionManger getUpdateVersionManager() {
		if(updateVersionManger == null){
			updateVersionManger = new UpdateVersionManger();
		}
		updateVersionManger.interceptFlag = false;
		return updateVersionManger;
	}
    
    
    /**
	 * 显示下载对话框
	 */
	public void showDownloadDialog(final Context context,final String url){
		this.mContext = context;
		//需要显示更新提示
		final LayoutInflater tvinflater = LayoutInflater.from(context);
        View view = tvinflater.inflate(R.layout.alert_dialog_text, null);
        assert view != null;
        TextView textView = (TextView) view.findViewById(R.id.dialog_text);
        textView.setText("版本更新");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCustomTitle(textView);
        alertDialog.setMessage("正在下载更新。。。");
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.update_progress);
		mProgressText = (TextView) v.findViewById(R.id.update_progress_text);
		mProgressText2 = (TextView) v.findViewById(R.id.update_progress_text2);
		alertDialog.setView(v);
		alertDialog.setNegativeButton("后台更新", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				((Activity) context).finish();
				//interceptFlag = true;
			}
		});
		alertDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				
				interceptFlag = true;
			}
		});
		downloadDialog = alertDialog.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();
		
		
		
		downloadApkFile(url);
	}
	
	/**
	 * 下载apk
	 */
	private void downloadApkFile(final String apkurl) {
        new Thread() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
			@Override
            public void run() {
    			try {
    				String apkName = "Lvgutou"+getVersionCode()+".apk";
    				String tmpApk = "Lvgutou"+getVersionCode()+".tmp";
    				//判断是否挂载了SD卡
    				String storageState = Environment.getExternalStorageState();		
    				if(storageState.equals(Environment.MEDIA_MOUNTED)){
    					savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JJB/Update/";
    					File file = new File(savePath);
    					if(!file.exists()){
    						file.mkdirs();
    					}
    					apkFilePath = savePath + apkName;
    					tmpFilePath = savePath + tmpApk;
    				}
    				
    				//没有挂载SD卡，无法下载文件
    				if(apkFilePath == null || apkFilePath.equals("")){
    					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
    					return;
    				}
    				
    				File ApkFile = new File(apkFilePath);
    				
    				//是否已下载更新文件
    				if(ApkFile.exists()){
    					downloadDialog.dismiss();
    					installApk();
    					return;
    				}
    				
    				//输出临时下载文件
    				File tmpFile = new File(tmpFilePath);
    				FileOutputStream fos = new FileOutputStream(tmpFile);
    				
    				URL url = new URL(apkurl);
    				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    				conn.connect();
    				int length = conn.getContentLength();
    				InputStream is = conn.getInputStream();
    				
    				//显示文件大小格式：2个小数点显示
    		    	DecimalFormat df = new DecimalFormat("0.00");
    		    	//进度条下面显示的总文件大小
    		    	apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
    				
    				int count = 0;
    				byte buf[] = new byte[1024];
    				
    				do{   		   		
    		    		int numread = is.read(buf);
    		    		count += numread;
    		    		//进度条下面显示的当前下载文件大小
    		    		tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
    		    		//当前进度值
    		    	    progress =(int)(((float)count / length) * 100);
    		    	    //更新进度
    		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
    		    		if(numread <= 0){	
    		    			//下载完成 - 将临时下载文件转成APK文件
    						if(tmpFile.renameTo(ApkFile)){
    							//通知安装
    							mHandler.sendEmptyMessage(DOWN_OVER);
    						}
    		    			break;
    		    		}
    		    		fos.write(buf,0,numread);
    		    	}while(!interceptFlag);//点击取消就停止下载
    				
    				fos.close();
    				is.close();
    			} catch(IOException e){
    				e.printStackTrace();
    			}
    			
    		
            }
        }.start();

    }
	
	 /**
     * 安装apk
     */
 	private void installApk(){
 		File apkfile = new File(apkFilePath);
         if (!apkfile.exists()) {
             return;
         }    
         Intent i = new Intent(Intent.ACTION_VIEW);
         i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
         mContext.startActivity(i);
 	}
 	
 	
 	
    public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				mProgressText2.setText("进度"+ progress + "%");
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				UIHelper.ToastMessage(mContext,"无法下载安装文件，请检查SD卡是否挂载");
				break;
			}
    	}
    };
}
