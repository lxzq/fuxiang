package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.media.AudioUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by WWW on 2015/8/31.
 */
public class AudioView extends LinearLayout {
    private final TextView btnAudio;
    private final TextView time;
    private final TextView text;
    private final UIHandler uiHandler;
    private UIThread uiThread;
    private int mState = -1;    //-1:没在录制，1：正在录制
    private int mResult = -1;
    private boolean isPlay = false;
    private MediaPlayer mPlayer = null;
    private Context context;
    private OnAudioListener onAudioListener;
    private String playPath;

    public AudioView(Context context) {
        this(context, null);
        this.context = context;
    }

    public AudioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public AudioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.audio_view, this);
        btnAudio = (TextView) findViewById(R.id.audio_btn);
        time = (TextView) findViewById(R.id.audio_time);
        text = (TextView) findViewById(R.id.audio_text);
        uiHandler = new UIHandler();
    }

    public void start(final OnAudioListener mOnAudioListener) {
        this.onAudioListener = mOnAudioListener;
        btnAudio.setBackgroundResource(R.mipmap.btn_kanzhe_yuyin_selected);
        onAudioListener.onStartRecord();
        btnAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mState == -1 && mResult != AudioUtils.SUCCESS) {
                    startRecord();
                } else if (mState == 1) {
                    stopRecord();
                } else if (mResult == AudioUtils.SUCCESS && !isPlay) {
                    startPlay();
                } else if (isPlay) {
                    stopPlay();
                }
            }
        });
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        if (mState != -1) {
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", AudioUtils.E_STATE_RECODING);
            msg.setData(b);

            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
            return;
        }
        AudioUtils mRecord_2 = AudioUtils.getInstance();
        mResult = mRecord_2.startRecordAndFile();
        text.setText(R.string.isRecording);
        setIconAnimotion(R.anim.record_audio);
        Log.i("mResult", mResult + "");
        if (mResult == AudioUtils.SUCCESS) {
            uiThread = new UIThread();
            new Thread(uiThread).start();
            mState = 1;
        } else {
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_RECORDFAIL);
            b.putInt("msg", mResult);
            msg.setData(b);

            uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mState != -1) {
            AudioUtils mRecord_2 = AudioUtils.getInstance();
            mRecord_2.stopRecordAndFile();
            if (uiThread != null) {
                uiThread.stopThread();
            }
            if (uiHandler != null)
                uiHandler.removeCallbacks(uiThread);
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd", CMD_STOP);
            b.putInt("msg", mState);
            msg.setData(b);
            uiHandler.sendMessageDelayed(msg, 1000); // 向Handler发送消息,更新UI
            mState = -1;
            //btnAudio.setText("播放");
            text.setText(R.string.isStopped);
        }
    }

    /**
     * 撤销录制的音频
     */
    public void cancelRecord() {
        if (isPlay) {
            stopPlay();
        }
        if (mResult == AudioUtils.SUCCESS) {
            File file = new File(AudioUtils.getInstance().getAMRFilePath());
            if (file.exists()) {
                boolean isdelete = file.delete();
                if (isdelete) {
                    UIHelper.ToastMessage(context, "成功删除录音！");
                    playPath = null;
                }
            }
            mState = -1;
            mResult = -1;
            time.setText("00:00");
            mTimeMill = 0;
            // btnAudio.setText("录音");
            text.setText(R.string.startRecord);
        } else if (mState == 1) {
            UIHelper.ToastMessage(context, "正在录音中！");
        } else if (mState == -1 && mResult == -1) {
            UIHelper.ToastMessage(context, "没有录制的音频！");
        }
    }

    /**
     * 开始播放录音
     */
    public void startPlay() {
        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            }
            mPlayer.setDataSource(playPath);
            mPlayer.prepare();
            mPlayer.start();
            //开始计时
            mTimeMill = 0;
            uiThread = new UIThread();
            new Thread(uiThread).start();
            isPlay = true;
            onAudioListener.onPlaying();
            //btnAudio.setText("正在播放");
            text.setText(R.string.isPlaying);
            setIconBackround(R.mipmap.btn_kanzhe_yuyin_zhantin);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlay();
                }
            });
        } catch (IOException e) {
            Log.e("AudioView", "播放失败");
        }
    }

    /**
     * 停止播放录音
     */
    public void stopPlay() {
        if (isPlay) {
            if (uiThread != null) {
                uiThread.stopThread();
            }
            if (uiHandler != null)
                uiHandler.removeCallbacks(uiThread);
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            isPlay = false;
            setIconBackround(R.mipmap.btn_kanzhe_yuyin_bofang);
            text.setText(R.string.isStopped);
            time.setText("00:00");
        }
    }

    private final static int CMD_RECORDING_TIME = 2000;
    private final static int CMD_RECORDFAIL = 2001;
    private final static int CMD_STOP = 2002;

    class UIHandler extends Handler {
        public UIHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.d("MyHandler", "handleMessage......");
            super.handleMessage(msg);
            Bundle b = msg.getData();
            int vCmd = b.getInt("cmd");
            switch (vCmd) {
                case CMD_RECORDING_TIME:
                    int vTime = b.getInt("msg");
                    //UIHelper.ToastMessage(RecordAudioActivity.this, "正在录音中，已录制：" + vTime + " s");
                    time.setText(Utils.secondToMinute(vTime));
                    onAudioListener.onRecording(vTime);
                    break;
                case CMD_RECORDFAIL:
                    int vErrorCode = b.getInt("msg");
                    String vMsg = AudioUtils.getErrorInfo(context, vErrorCode);
                    UIHelper.ToastMessage(context, "录音失败：" + vMsg);
                    break;
                case CMD_STOP:
                    AudioUtils mRecord_2 = AudioUtils.getInstance();
                    long mSize = mRecord_2.getRecordFileSize();
                    playPath = AudioUtils.getAMRFilePath();
                    onAudioListener.onRecordFinish();
                    setIconBackround(R.mipmap.btn_kanzhe_yuyin_bofang);
                   // UIHelper.ToastMessage(context, "录音已停止.录音文件:" + playPath + "\n文件大小：" + mSize);
                    break;
                default:
                    break;
            }
        }
    }
    int mTimeMill = 0;
    class UIThread implements Runnable {

        boolean vRun = true;

        public void stopThread() {
            vRun = false;
        }

        public void run() {
            while (vRun) {
                Log.d("thread", "mThread........" + mTimeMill);
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putInt("cmd", CMD_RECORDING_TIME);
                b.putInt("msg", mTimeMill);
                msg.setData(b);
                uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI
                try {
                    Thread.sleep(1000);
                    mTimeMill++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 录音播放回调接口
     */
    public interface OnAudioListener {
        void onStartRecord();

        void onRecording(int time);

        void onRecordFinish();

        void onPlaying();

    }

    public String getAudioPath() {
        return playPath;
    }

    /**
     * 获取录音时间长度
     * @return
     */
    public int getDuration(){
        return mTimeMill;
    }
    public void setIsFinishRecord(boolean isFinishRecord, String path) {
        if (isFinishRecord) {
            mResult = AudioUtils.SUCCESS;
            text.setText(R.string.isStopped);
            playPath = path;
        }
    }

    public void setIconBackround(int res){
        btnAudio.setBackgroundResource(res);
    }

    public void setIconAnimotion(int animation){
        btnAudio.setBackgroundResource(animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) btnAudio.getBackground();
        animationDrawable.start();
    }
}
