package com.qcsh.fuxiang.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.Utils;

/**
 * 自定义确认框
 * Created by Administrator on 2015/8/13.
 */
public class MyConfirmDialog extends Dialog {

    private String title;
    private String content;

    private OnCancelDialogListener cancelDialogListener;
    private OnConfirmDialogListener confirmDialogListener;

    private TextView titleView;
    private TextView contentView;
    private Button cancelButton;
    private Button confirmButton;

    private int width = 200;

    public MyConfirmDialog(Context context,String title,String content,OnCancelDialogListener cancelListener,OnConfirmDialogListener confirmListener){
        super(context, R.style.Loading);
        width = Utils.getScreenWidth(context);
        width = (width * 3 ) / 4;
        this.title = title;
        this.content = content;
        this.cancelDialogListener = cancelListener;
        this.confirmDialogListener = confirmListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter);

        titleView = (TextView) findViewById(R.id.title);
        contentView = (TextView) findViewById(R.id.content);
        cancelButton = (Button)findViewById(R.id.cancel);
        confirmButton = (Button)findViewById(R.id.confirm);

        titleView.setText(title);
        contentView.setText(content);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = this.width;
        lp.dimAmount = 0.2f;
        getWindow().setAttributes(lp);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialogListener.onCancel();
                MyConfirmDialog.this.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialogListener.onConfirm();
                MyConfirmDialog.this.dismiss();
            }
        });

    }

    /**
     * 取消回调接口
     */
    public interface OnCancelDialogListener{
        void onCancel();
    }

    /**
     * 确认回调接口
     */
    public interface OnConfirmDialogListener{
        void onConfirm();
    }
}
