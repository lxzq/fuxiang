package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2015/9/17.
 */
public class OrderView extends LinearLayout {

    private Scroller scroller;
    private Context mContext;

    private View stayView;
    private StayViewListener stayViewListener;
    private XListView xListView;

    boolean up = true;

    public void setStayView(View stayView,XListView xListView,StayViewListener stayViewListener){

        this.stayView = stayView;
        this.xListView = xListView;
        this.stayViewListener = stayViewListener;

    }

    public OrderView(Context context, AttributeSet attrs){
        super(context,attrs);
        mContext = context;
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        scroller = new Scroller(mContext);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(stayView!=null&&xListView!=null&&stayViewListener!=null){
            int y = xListView.getScrollY();
            if(up){
                int top = stayView.getTop();
                if(y<=top){
                    stayViewListener.onStayViewShow();
                    up = false;
                }
            }
            if(!up){

                int top = stayView.getTop();
                if(y>=top){
                    stayViewListener.onStayViewGone();
                    up = false;
                }
               /*
                int bottom = stayView.getBottom();
                if(y>=bottom-stayView.getHeight()){
                    stayViewListener.onStayViewGone();
                    up = true;
                }*/
            }
        }
     }

    public interface StayViewListener {
        public void onStayViewShow();
        public void onStayViewGone();
    }
}
