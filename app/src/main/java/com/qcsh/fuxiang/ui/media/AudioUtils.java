package com.qcsh.fuxiang.ui.media;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.FileUtils;
import com.qcsh.fuxiang.ui.look.GrowthTreeActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by WWW on 2015/8/24.
 */
public class AudioUtils implements GrowthTreeActivity.GrowthTreeAudioListener {
    private boolean isRecord = false;

    private MediaRecorder mMediaRecorder;
    private static String mAudioAMRPath;
    public final static int SUCCESS = 1000;
    public final static int E_NOSDCARD = 1001;
    public final static int E_STATE_RECODING = 1002;
    public final static int E_UNKOWN = 1003;
    private MediaPlayer mPlayer;
    private boolean isPlay = false;
    private AudioStopListener audioStopListener;
    private Handler handler = null;
    private AudioPlayRun audioPlayRun;

    private AudioUtils() {
    }

    private static AudioUtils mInstance = new AudioUtils();

    public synchronized static AudioUtils getInstance() {
        return mInstance;
    }

    public int startRecordAndFile() {
        //判断是否有外部存储设备sdcard
        if (FileUtils.checkSaveLocationExists()) {
            if (isRecord) {
                return AudioUtils.E_STATE_RECODING;
            } else {
                if (mMediaRecorder == null) {
                    createMediaRecord();
                }

                try {
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    // 让录制状态为true
                    isRecord = true;
                    return AudioUtils.SUCCESS;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return AudioUtils.E_UNKOWN;
                }
            }

        } else {
            return AudioUtils.E_NOSDCARD;
        }
    }


    public void stopRecordAndFile() {
        if (mMediaRecorder != null) {
            System.out.println("stopRecord");
            isRecord = false;
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public long getRecordFileSize() {
        return getFileSize(getAMRFilePath());
    }


    private void createMediaRecord() {
         /* ①Initial：实例化MediaRecorder对象 */
        mMediaRecorder = new MediaRecorder();

        /* setAudioSource/setVedioSource*/
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风

        /* 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default
         * THREE_GPP(3gp格式，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
         */
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

         /* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default */
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

         /* 设置输出文件的路径 */
        creatFilePath();
        File file = new File(getAMRFilePath());
        if (file.exists()) {
            file.delete();
        }
        mMediaRecorder.setOutputFile(getAMRFilePath());
    }

    /**
     * 获取文件大小
     *
     * @param path,文件的绝对路径
     * @return
     */
    public static long getFileSize(String path) {
        File mFile = new File(path);
        if (!mFile.exists())
            return -1;
        return mFile.length();
    }

    private void creatFilePath() {
        if (FileUtils.checkSaveLocationExists()) {
            File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + AppConfig.CACHE_AUDIO);
            if (!sampleDir.exists()) {
                sampleDir.mkdirs();
            }
            File vecordDir = sampleDir;
            mAudioAMRPath = "";
            // 创建文件
            try {
                File mRecordFile = File.createTempFile("recording", ".amr", vecordDir);
                mAudioAMRPath = mRecordFile.getAbsolutePath();
                Log.i("TAG", mAudioAMRPath);
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取编码后的AMR格式音频文件路径
     *
     * @return
     */
    public static String getAMRFilePath() {
        return mAudioAMRPath;
    }

    public static String getErrorInfo(Context vContext, int vType) throws Resources.NotFoundException {
        switch (vType) {
            case SUCCESS:
                return "success";
            case E_NOSDCARD:
                return vContext.getResources().getString(R.string.error_no_sdcard);
            case E_STATE_RECODING:
                return vContext.getResources().getString(R.string.error_state_record);
            case E_UNKOWN:
            default:
                return vContext.getResources().getString(R.string.error_unknown);

        }
    }

    /**
     * 开始播放录音
     */
    public void startPlay(final String url) {

        if (null != mPlayer) {
            stopPlay();
        }
        mPlayer = new MediaPlayer();

        if (handler == null) handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                stopPlay();
            }
        };
        audioPlayRun = new AudioPlayRun(url);
        initListener();
        isPlay = true;
        new Thread(audioPlayRun).start();
    }

    private class AudioPlayRun implements Runnable {

        private String url;

        public AudioPlayRun(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                mPlayer.setDataSource(url);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                handler.sendEmptyMessage(0);
            }
        }
    }

    private void initListener() {
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.sendEmptyMessage(0);
            }
        });
        //缓冲完成就隐藏
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });
    }

    /**
     * 停止播放录音
     */
    public void stopPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            isPlay = false;
            handler.removeCallbacks(audioPlayRun);
        }
        if (null != audioStopListener) {
            audioStopListener.stopPlay();
        }
    }

    @Override
    public void stop() {
        stopPlay();
    }

    public boolean isPlaying() {
        return isPlay;
    }


    public void setAudioStopListener(AudioStopListener audioStopListener) {
        this.audioStopListener = audioStopListener;
    }

    /**
     * 音频播放完成监听器
     */
    public interface AudioStopListener {
        void stopPlay();
    }
}
