package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;

/**
 * 家家帮直播播放器视图
 * Created by Administrator on 2015/10/29.
 */
public class JJBLiveView extends LinearLayout implements View.OnClickListener {

    private ImageView spinner;
    private JJBVideoView videoView;
    private LinearLayout control;

    private Button startButton;
    private Button stopButton;
    private Button fullButton;
    private Button audioButton;
    private VerticalSeekBar verticalSeekBar;
    private ProgressBar seekBar;
    private TextView timeView;


    private Context context;
    private Handler handler ;
    private PlayTime playTime;

    private String path;
    private int currentVolume;
    private int maxVolume;
    private AudioManager audioManager;
    private boolean isPlay = false;


    public JJBLiveView(Context context){
        super(context);
        this.context = context;
        initLiveView();
    }

    public JJBLiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initLiveView();
    }

    public JJBLiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initLiveView();
    }



    private void initLiveView(){
        LayoutInflater.from(context).inflate(R.layout.jjb_live_view, this);
        handler = new Handler();
        playTime = new PlayTime();
        spinner = (ImageView)findViewById(R.id.spinner);
        videoView = (JJBVideoView)findViewById(R.id.live_video);
        control = (LinearLayout)findViewById(R.id.control);
        startButton = (Button)findViewById(R.id.start);
        stopButton = (Button)findViewById(R.id.stop);
        fullButton = (Button)findViewById(R.id.full);
        timeView = (TextView)findViewById(R.id.live_time);
        audioButton = (Button)findViewById(R.id.audio);
        verticalSeekBar = (VerticalSeekBar)findViewById(R.id.seek_bar);
        seekBar = (ProgressBar)findViewById(R.id.play_progressBar);

        initAudioManager();
        initListener();
    }

    private void initListener(){

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                spinner.setVisibility(GONE);
                control.setVisibility(VISIBLE);
                startButton.setVisibility(GONE);
                stopButton.setVisibility(VISIBLE);
                seekBar.setMax(videoView.getDuration());
                handler.postDelayed(playTime, 100);
                isPlay = true;
            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    spinner.setVisibility(VISIBLE);
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                    if (mp.isPlaying()) {
                        spinner.setVisibility(GONE);
                    }
                }
                return true;
            }
        });


        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                UIHelper.ToastMessage(context, "视频不存在或者网络错误");
                return true;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startButton.setVisibility(VISIBLE);
                stopButton.setVisibility(GONE);
                timeView.setText("00:00");
            }
        });

        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(VISIBLE);
                stopButton.setVisibility(GONE);
                pausePlay();
            }
        });

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(GONE);
                stopButton.setVisibility(VISIBLE);
                resumePlay();
            }
        });

        fullButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // AppIntentManager.startSysVideoPlay(context, path, AppConfig.VIDEO.HTTP_VIDEO);
                AppIntentManager.startJJBFullLive(context, path);
            }
        });

        audioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verticalSeekBar.getVisibility() == View.VISIBLE) {
                    verticalSeekBar.setVisibility(GONE);
                } else {
                    verticalSeekBar.setVisibility(VISIBLE);
                }
            }
        });

        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setOnClickListener(this);
    }

    /**
     * 暂停播放
     */
    public void pausePlay(){
        if(videoView.isPlaying()){
            videoView.pause();
        }
    }
    /**
     * 重新播放
     */
    public void resumePlay(){
        if(!videoView.isPlaying()){
            videoView.start();
        }
    }
    /**
     * 加载视频中
     */
    private void onLoading(){
        AnimationDrawable animationDrawable = (AnimationDrawable) spinner.getBackground();
        assert animationDrawable != null;
        animationDrawable.start();
    }

    private void initAudioManager(){
         audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
         currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
         maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
         verticalSeekBar.setMax(maxVolume);
         verticalSeekBar.setProgress(currentVolume);
    }

    public void startPlay(String path){
        this.path = path;
        videoView.requestFocus();
        videoView.setVideoPath(path);
        videoView.start();
        spinner.setVisibility(VISIBLE);
        control.setVisibility(GONE);
        onLoading();
    }

    public Button getFullButton(){
        return fullButton;
    }


    public void stopPlay(){
        spinner.setVisibility(GONE);
        videoView.stopPlayback();
        handler.removeCallbacks(playTime);
    }

    private class PlayTime implements Runnable{
        @Override
        public void run() {
            if(videoView.isPlaying()){
                int play = videoView.getCurrentPosition() / 1000;
                String timeStr =  Utils.secondToMinute(play);
                timeView.setText(timeStr);
                seekBar.setProgress(videoView.getCurrentPosition());
            }
            handler.postDelayed(playTime, 100);
        }
    }

    @Override
    public void onClick(View v) {
        if(isPlay){
        if(control.getVisibility() == VISIBLE){
            control.setVisibility(GONE);
            verticalSeekBar.setVisibility(GONE);
        }else{
            control.setVisibility(VISIBLE);
        }
        }
    }
}
