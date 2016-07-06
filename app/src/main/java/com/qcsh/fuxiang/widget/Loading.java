package com.qcsh.fuxiang.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qcsh.fuxiang.R;


/**
 * Created by liu on 13-10-8.
 * 自定义进度条
 */
public class Loading extends Dialog {
    public Loading(Context context) {
        super(context);
    }

    public Loading(Context context, int theme) {
        super(context, theme);
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        assert spinner != null;
        spinner.start();
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    public static Loading Show(Activity context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        Loading dialog = null;
    	if(context != null && !context.isFinishing()){
    		     dialog = new Loading(context, R.style.Loading);
    	        dialog.setTitle("");
    	        dialog.setContentView(R.layout.loading);
    	        if (message == null || message.length() == 0) {
    	            dialog.findViewById(R.id.message).setVisibility(View.GONE);
    	        } else {
    	            TextView txt = (TextView) dialog.findViewById(R.id.message);
    	            txt.setText(message);
    	        }
    	        dialog.setCancelable(cancelable);
    	        //dialog.setOnCancelListener(cancelListener);
    	       
    	        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
    	        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
    	        lp.dimAmount = 0.2f;
    	        dialog.getWindow().setAttributes(lp);
    	        dialog.setCanceledOnTouchOutside(cancelable);
    	        dialog.show();
    	}
        return dialog;
    }
    
    
    public static Loading Create(Activity context){
        Loading dialog = null;
    	if(null != context && !context.isFinishing()){
    	dialog = new Loading(context, R.style.Loading);
    	dialog.setTitle("");
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.message).setVisibility(View.GONE);
    	}
    	return dialog;
    	
    }
}
