package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.UIHelper;
import com.yixia.camera.demo.log.Logger;
import com.yixia.camera.demo.utils.ConvertToUtils;
import com.yixia.weibo.sdk.MediaRecorderBase;
import com.yixia.weibo.sdk.MediaRecorderNative;
import com.yixia.weibo.sdk.VCamera;
import com.yixia.weibo.sdk.model.MediaObject;
import com.yixia.weibo.sdk.util.DeviceUtils;
import com.yixia.weibo.sdk.util.FileUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频录制控件（使用第三方VCamera）（没用上）
 * Created by WWW on 2015/9/9.
 */
public class VideoRecordView extends LinearLayout implements MediaRecorderBase.OnErrorListener, MediaRecorderBase.OnEncodeListener {

    /**
     * 录制最长时间
     */
    public final static int RECORD_TIME_MAX = 60 * 1000 * 10;
    /**
     * 录制最小时间
     */
    public final static int RECORD_TIME_MIN = 3 * 1000;
    /**
     * 刷新进度条
     */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /**
     * 延迟拍摄停止
     */
    private static final int HANDLE_STOP_RECORD = 1;
    /**
     * 对焦
     */
    private static final int HANDLE_HIDE_RECORD_FOCUS = 2;
    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    /**
     * SDK视频录制对象
     */
    private MediaRecorderBase mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;
    /**
     * 屏幕宽度
     */
    private int mWindowWidth;
    /**
     * 对焦图片宽度
     */
    private int mFocusWidth;
    /**
     * 底部背景色
     */
    private int mBackgroundColorNormal, mBackgroundColorPress;
    /**
     * 需要重新编译（拍摄新的或者回删）
     */
    private boolean mRebuild;
    /**
     * 对焦图标-带动画效果
     */
    private ImageView mFocusImage;
    /**
     * 对焦动画
     */
    private Animation mFocusAnimation;
    /**
     * on
     */
    private boolean mCreated;
    /**
     * 录制完成回调接口
     */
    private OnRecordListener mOnRecordListener;

    private Context context;
    private int mTimeCount;
    private Timer mTimer;
    // private ProgressBar mProgressBar;

    public VideoRecordView(Context context) {
        this(context, null);
        this.context = context;
    }

    public VideoRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public VideoRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCreated = false;
        this.context = context;
        loadIntent();
        loadViews();
        mCreated = true;
    }


    /**
     * 加载传入的参数
     */
    private void loadIntent() {
        mWindowWidth = DeviceUtils.getScreenWidth(context);
        mFocusWidth = ConvertToUtils.dipToPX(context, 64);
        mBackgroundColorNormal = getResources().getColor(R.color.black);//camera_bottom_bg
        mBackgroundColorPress = getResources().getColor(R.color.camera_bottom_press_bg);
        mRebuild = false;
    }

    /**
     * 加载视图
     */
    private void loadViews() {
        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mFocusImage = (ImageView) findViewById(R.id.record_focusing);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        mProgressBar.setMax(RECORD_TIME_MAX);// 设置进度条最大量

        if (DeviceUtils.hasICS()) {
            mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);
        }
        initSurfaceView();
    }

    /**
     * 初始化画布
     */
    private void initSurfaceView() {
        int width = mWindowWidth;
        int height = mWindowWidth/* * 4 / 3*/;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mSurfaceView.setLayoutParams(lp);
    }

    /**
     * 初始化拍摄SDK
     */
    public void initMediaRecorder() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorderNative();
//        mRebuild = true;
            mMediaRecorder.setOnErrorListener(this);
            mMediaRecorder.setOnEncodeListener(this);
            File f = new File(VCamera.getVideoCachePath());
            if (!FileUtils.checkFile(f)) {
                f.mkdirs();
            }
            String key = String.valueOf(System.currentTimeMillis());
            mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key);
        }
        mMediaRecorder.setOnSurfaveViewTouchListener(mSurfaceView);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }

    /**
     * 开始录制
     */
    public void startRecord(final OnRecordListener onRecordListener) {
        this.mOnRecordListener = onRecordListener;
        //initMediaRecorder();
        if (mMediaRecorder != null) {
            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (mOnRecordListener != null)
                mOnRecordListener.onStart();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
//                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    if (mOnRecordListener != null) {
                        mOnRecordListener.onRecording(mTimeCount);
                    }
                    mTimeCount++;
                    if (mTimeCount == RECORD_TIME_MAX) {// 达到指定时间，停止拍摄
                        stopRecord();
                    }
                }
            }, 0, 1000);
            if (part == null) {
                return;
            }
            //如果使用MediaRecorderSystem，不能在中途切换前后摄像头，否则有问题
//            if (mMediaRecorder instanceof MediaRecorderSystem) {
//                mCameraSwitch.setVisibility(View.GONE);
//            }
//            mProgressView.setData(mMediaObject);
        }
        mRebuild = true;
        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD, RECORD_TIME_MAX - mMediaObject.getDuration());
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        //mPressedStatus = false;
//        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
            if (mOnRecordListener != null)
                mOnRecordListener.onRecordFinish();
            mRebuild = false;
            mMediaRecorder.startEncoding();
        }
        mHandler.removeMessages(HANDLE_STOP_RECORD);
        checkStatus();
    }

    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnSurfaveViewTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null || !mCreated) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //检测是否手动对焦
                    showFocusImage(event);
                    if (!mMediaRecorder.onTouch(event, new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            mFocusImage.setVisibility(View.GONE);

                        }
                    })) {
                        return true;
                    } else {
                        mFocusImage.setVisibility(View.GONE);
                    }
                    mMediaRecorder.setAutoFocus();
                    break;
            }
            return true;
        }

    };

    /**
     * 手动对焦
     */

    private void showFocusImage(MotionEvent e) {

        int x = Math.round(e.getX());
        int y = Math.round(e.getY());
        int focusWidth = 100;
        int focusHeight = 100;
        int previewWidth = mSurfaceView.getWidth();
        Rect touchRect = new Rect();

        mMediaRecorder.calculateTapArea(focusWidth, focusHeight, 1f, x, y, previewWidth, previewWidth, touchRect);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFocusImage.getLayoutParams();
        int left = touchRect.left - (mFocusWidth / 2);//(int) x - (focusingImage.getWidth() / 2);
        int top = touchRect.top - (mFocusWidth / 2);//(int) y - (focusingImage.getHeight() / 2);
        if (left < 0)
            left = 0;
        else if (left + mFocusWidth > mWindowWidth)
            left = mWindowWidth - mFocusWidth;
        if (top + mFocusWidth > mWindowWidth)
            top = mWindowWidth - mFocusWidth;

        lp.leftMargin = left;
        lp.topMargin = top;

        Logger.e("left =  " + left);
        Logger.e("top =  " + top);

        mFocusImage.setLayoutParams(lp);
        mFocusImage.setVisibility(View.VISIBLE);

        if (mFocusAnimation == null)
            mFocusAnimation = AnimationUtils.loadAnimation(context, R.anim.record_focus);

        mFocusImage.startAnimation(mFocusAnimation);
        mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);//最多3.5秒也要消失
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_STOP_RECORD:
                    stopRecord();
                    break;
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null) {
                        /*if (mProgressView != null)
                            mProgressView.invalidate();
                        if (mPressedStatus)
                            sendEmptyMessageDelayed(0, 30);*/
                    }
                    break;
            }
        }
    };

    /**
     * 回删
     */
    public boolean backRemove() {
        if (mMediaObject != null && mMediaObject.mediaList != null) {
            File cacheDir = new File(mMediaObject.getOutputDirectory());
            int size = mMediaObject.mediaList.size();
            if (size > 0) {
                MediaObject.MediaPart part = mMediaObject.mediaList.get(size - 1);
                mMediaObject.removePart(part, true);

                if (mMediaObject.mediaList.size() > 0)
                    mMediaObject.mCurrentPart = mMediaObject.mediaList.get(mMediaObject.mediaList.size() - 1);
                else
                    mMediaObject.mCurrentPart = null;
                return true;
            }
            if (cacheDir.delete()) return true;
        }
        return false;
    }

    /**
     * 检查录制时间，显示/隐藏下一步按钮
     */
    private int checkStatus() {
        int duration = 0;
        if (mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < RECORD_TIME_MIN) {
                if (duration == 0) {

                }
            } else {

            }
        }
        return duration;
    }

    public int getTimeCount() {
        return mMediaObject.getDuration();
    }

    public String getmRecordFile() {
        return mMediaObject.getOutputTempVideoPath();
    }

    public boolean getIsRecording() {
        return mRebuild;
    }

    public void releaseRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 录制完成回调接口
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public interface OnRecordListener {
        void onRecordFinish();

        void onRecording(int time);

        void onStart();
    }

    @Override
    public void onVideoError(int i, int i1) {

    }

    @Override
    public void onAudioError(int i, String s) {

    }

    @Override
    public void onEncodeStart() {

    }

    @Override
    public void onEncodeProgress(int i) {

    }

    @Override
    public void onEncodeComplete() {
        UIHelper.ToastMessage(context, "转码完成");
        if (mMediaObject!=null)
            mMediaObject.getCurrentPart().delete();
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEncodeError() {

    }
}
