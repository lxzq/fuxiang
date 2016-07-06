package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.qcsh.fuxiang.R;

/**
 * Created by Administrator on 2015/10/29.
 */
public class JJBVideoView extends VideoView {

    public JJBVideoView(Context context){
      super(context);
    }

    public JJBVideoView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);

    }
    public JJBVideoView(Context context,AttributeSet attributeSet ,int style){
        super(context,attributeSet,style);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getDefaultSize(0,widthMeasureSpec);
        int h = getDefaultSize(0,heightMeasureSpec);
        setMeasuredDimension(w,h);
    }


}
