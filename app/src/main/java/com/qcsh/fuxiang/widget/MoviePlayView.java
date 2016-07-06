package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.look.GrowthTreeActivity;
import com.qcsh.fuxiang.ui.look.GrowthtreeDetailActivity;


/**
 * Created by WWW on 2015/8/28.
 */

public class MoviePlayView extends LinearLayout implements MediaPlayer.OnErrorListener,
                            MediaPlayer.OnCompletionListener,GrowthTreeActivity.GrowthTreeListener
    {
    private VideoView videoView;
    private TextView btnPlay;
    private ImageView loadingView ;
    private String path;
    private Context context;
    private Handler handler ;
    private PlayStatus playStatus;

    private LinearLayout progressbarLiner;
    private ProgressBar progressBar;
    private TextView times;
    private VideoPlayStopListener videoPlayStopListener;

    public void setPath(String path) {
        this.path = path;
    }

    public MoviePlayView(Context context) {
        super(context);
        init();
    }

    public MoviePlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        init();
    }

    public MoviePlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        handler = new Handler();
        playStatus = new PlayStatus();
        LayoutInflater.from(context).inflate(R.layout.video_view, this);
        btnPlay = (TextView) findViewById(R.id.palybtn);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        videoView = (VideoView) findViewById(R.id.video_view);

        loadingView = (ImageView) findViewById(R.id.loading);
        progressbarLiner = (LinearLayout)findViewById(R.id.progressbar);
        progressBar = (ProgressBar)findViewById(R.id.play_progressBar);
        times = (TextView) findViewById(R.id.times);

        videoView.setOnCompletionListener(this);
        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay();
            }
        });
        //缓冲完成就隐藏
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                onStopLoading();
            }
        });
    }

    public void startPlay() {
        if (!StringUtils.isEmpty(path)) {
            videoView.requestFocus();
            videoView.setVideoURI(Uri.parse(path));
            videoView.start();

            progressbarLiner.setVisibility(GONE);
            btnPlay.setVisibility(View.GONE);
            onLoading();
        }
    }

    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }
    /**
     * 加载视频中
     */
    private void onLoading(){
        btnPlay.setVisibility(View.GONE);
        loadingView.setVisibility(VISIBLE);
        AnimationDrawable spinner = (AnimationDrawable) loadingView.getBackground();
        assert spinner != null;
        spinner.start();
    }

    /**
     * 完成缓冲加载
     */
    private void onStopLoading(){
        loadingView.setVisibility(GONE);
        progressbarLiner.setVisibility(VISIBLE);
        handler.postDelayed(playStatus, 50);
        times.setText(Utils.secondToMinute(getPlayTotalTime()));
        int max = getPlayTotalTime();
        progressBar.setMax(max);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        btnPlay.setVisibility(View.VISIBLE);
        progressbarLiner.setVisibility(GONE);
        handler.removeCallbacks(playStatus);
        if(null != videoPlayStopListener){
            videoPlayStopListener.playStop();
        }
    }

    @Override
    public void stop() {
        stopPlay();
    }
    /**
     * 停止播放视频
     */
    public void stopPlay(){
        videoView.stopPlayback();
        btnPlay.setVisibility(VISIBLE);
        progressbarLiner.setVisibility(GONE);
        handler.removeCallbacks(playStatus);
        if(null != videoPlayStopListener){
            videoPlayStopListener.playStop();
        }
    }
    /**
     * 获取视频时间
     * @return
     */
    public int getPlayTotalTime(){
        return videoView.getDuration() / 1000 ;
    }

    /**
     * 获取当前播放时间
     * @return
     */
    public int getPlayCurrTime(){
        return videoView.getCurrentPosition() / 1000;
    }

    private class PlayStatus implements Runnable {
        @Override
        public void run() {
            if(videoView.isPlaying()){
                int curr = getPlayCurrTime();
                times.setText(Utils.secondToMinute(curr));
                progressBar.setProgress(curr);
            }
            handler.postDelayed(playStatus,50);
        }
    }

    public void setVideoPlayStopListener(VideoPlayStopListener listener){
        this.videoPlayStopListener = listener;
    }

    public interface VideoPlayStopListener {
         void playStop();
    }
}
