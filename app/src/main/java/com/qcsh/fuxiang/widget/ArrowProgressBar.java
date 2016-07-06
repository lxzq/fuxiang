package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.qcsh.fuxiang.R;

/**
 * Created by WWW on 2015/9/14.
 */
public class ArrowProgressBar extends RelativeLayout {

    public ArrowProgressBar(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        initArrowProgressBar(context);

    }

    public ArrowProgressBar(Context context, AttributeSet attrs) {

        super(context, attrs);

        initArrowProgressBar(context);

    }

    public ArrowProgressBar(Context context) {

        super(context);

        initArrowProgressBar(context);

    }

    private void initArrowProgressBar(Context context) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.arrow_progress_bar_layout, null);

        mProgressBar = (ProgressBar) view.findViewById(R.id.downloadProgressId);

        // mProgressTxt = ( TextView )view.findViewById( R.id.progressTxtId );

        mArrowImg = (ImageView) view.findViewById(R.id.arrowImgId);

        mArrowImg.setVisibility(ImageView.GONE);

        addView(view);

    }

    public void setProgress(int progress) {

        if (progress <= progressMax) {

            LayoutParams arrowParams = (LayoutParams) mArrowImg.getLayoutParams();

            int progressWidth = mProgressBar.getWidth();
            int progressLeft = mProgressBar.getLeft();
            int progressRight = mProgressBar.getRight();
            float leftPosition = ((progressWidth / progressMax) * progress) - mArrowImg.getWidth();

            arrowParams.leftMargin = (int) Math.ceil(leftPosition);

            mArrowImg.setLayoutParams(arrowParams);
            mArrowImg.setVisibility(ImageView.VISIBLE);

        } else {

             //mArrowImg.setVisibility(ImageView.GONE);

        }

        mProgressBar.setProgress(progress);

        // mProgressTxt.setText(progress + "%");

    }

    public void setMax(int progress) {
        this.progressMax = progress;
        mProgressBar.setMax(progress);
    }

    private ProgressBar mProgressBar = null;

    //private TextView mProgressTxt = null;

    private ImageView mArrowImg = null;

    private int progressMax = 100;

}
