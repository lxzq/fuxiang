package com.qcsh.fuxiang.ui.media;

import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.bean.look.GrowthTreeEntity;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.yixia.camera.demo.log.Logger;
import com.yixia.camera.demo.utils.ConvertToUtils;
import com.yixia.videoeditor.adapter.UtilityAdapter;
import com.yixia.weibo.sdk.MediaRecorderBase;
import com.yixia.weibo.sdk.MediaRecorderNative;
import com.yixia.weibo.sdk.VCamera;
import com.yixia.weibo.sdk.VideoProcessEngine;
import com.yixia.weibo.sdk.model.MediaObject;
import com.yixia.weibo.sdk.util.DeviceUtils;
import com.yixia.weibo.sdk.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 拍摄视频的界面（新）
 */
public class VRecordActivity extends BaseActivity implements View.OnClickListener, MediaRecorderBase.OnErrorListener, MediaRecorderBase.OnEncodeListener {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;
    private ImageButton mShootBtn;
    private TextView videoTime;
    private TextView startRecord;
    private ImageView localVideo;
    private ImageView playVideo;
    private ImageView cacelVideo;
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
    private boolean isRecording;
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
    private boolean isRecordVideo = true;
    private ArrayList<String> mSelectPath;
    private int mTimeCount;
    private Timer mTimer;
    private String path;
    private GrowthTreeEntity gtEntity;
    private String flag;

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("activityCircle", "===========onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("activityCircle", "===========onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("activityCircle", "===========onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("activityCircle", "===========onDestroy");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCreated = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vrecord);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        gtEntity = (GrowthTreeEntity) getIntent().getSerializableExtra("gtEntity");
        flag = getIntent().getStringExtra("flag");
        initTitleBar();
        loadIntent();
        initView();
        mCreated = true;
        Log.i("activityCircle", "===========onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();
        videoTime.setVisibility(View.VISIBLE);
        startRecord.setVisibility(View.VISIBLE);
        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mMediaRecorder.prepare();
        }
        Log.i("activityCircle", "===========onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stopRecord();
    }

    @Override
    public void onPause() {
        super.onPause();
        UtilityAdapter.freeFilterParser();
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        Log.i("activityCircle", "===========onPause");
    }

    /**
     * 加载传入的参数
     */
    private void loadIntent() {
        mWindowWidth = DeviceUtils.getScreenWidth(VRecordActivity.this);
        mFocusWidth = ConvertToUtils.dipToPX(VRecordActivity.this, 64);
        mBackgroundColorNormal = getResources().getColor(R.color.black);//camera_bottom_bg
        mBackgroundColorPress = getResources().getColor(R.color.camera_bottom_press_bg);
        isRecording = false;
    }

    private void initTitleBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        title = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(this);
        rightBtn.setVisibility(View.INVISIBLE);
        rightBtn.setText("完成");
        rightBtn.setOnClickListener(this);
        title.setText("发视频");
    }

    private void initView() {
        mShootBtn = (ImageButton) findViewById(R.id.shoot_button);
        videoTime = (TextView) findViewById(R.id.video_time);
        startRecord = (TextView) findViewById(R.id.start_record);
        localVideo = (ImageView) findViewById(R.id.local_video);
        playVideo = (ImageView) findViewById(R.id.video_play);
        cacelVideo = (ImageView) findViewById(R.id.video_cacel);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mFocusImage = (ImageView) findViewById(R.id.record_focusing);

        mShootBtn.setOnClickListener(this);
        localVideo.setOnClickListener(this);
        playVideo.setOnClickListener(this);
        cacelVideo.setOnClickListener(this);
        if (DeviceUtils.hasICS()) {
            mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);
        }
        //initSurfaceView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_back:
                finish();
                break;
            case R.id.action_bar_action:
                if (isRecordVideo) {
                    path = mMediaObject.getOutputTempVideoPath();
                } else {
                    path = mSelectPath.get(0);
                }
                AppIntentManager.startSubmitActivity(VRecordActivity.this, path, null, AppConfig.VIDEOTYPE, gtEntity, flag);
                finish();
                break;
            case R.id.shoot_button:
                if (!isRecording) {
                    rightBtn.setVisibility(View.INVISIBLE);
                    playVideo.setVisibility(View.INVISIBLE);
                    cacelVideo.setVisibility(View.INVISIBLE);
                    startRecord.setVisibility(View.INVISIBLE);
                    startRecord();
                } else {
                    stopRecord();
                    if (mMediaObject.getDuration() < RECORD_TIME_MIN) {
                        backRemove();
                        Toast.makeText(VRecordActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    } else {
                        isRecordVideo = true;
                        rightBtn.setVisibility(View.VISIBLE);
                        playVideo.setVisibility(View.VISIBLE);
                        cacelVideo.setVisibility(View.VISIBLE);
                        startRecord.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.local_video:
                AppIntentManager.startImageSelectorForResult(VRecordActivity.this, mSelectPath, 1, 1);
                break;
            case R.id.video_play:
                if (isRecordVideo) {
                    path = mMediaObject.getOutputTempVideoPath();
                } else {
                    path = mSelectPath.get(0);
                }
                AppIntentManager.startSysVideoPlay(VRecordActivity.this, path, AppConfig.VIDEO.LOCAL_VIDEO);
                break;
            case R.id.video_cacel:
                stopRecord();
                if (backRemove()) {
                    videoTime.setText("00:00");
                    playVideo.setVisibility(View.INVISIBLE);
                    rightBtn.setVisibility(View.INVISIBLE);
                    cacelVideo.setVisibility(View.INVISIBLE);
                    UIHelper.ToastMessage(VRecordActivity.this, "删除成功！");
                } else {
                    UIHelper.ToastMessage(VRecordActivity.this, "删除失败！");
                }
                break;

        }
    }

    private void initSurfaceView() {
        int width = mWindowWidth;
        int height = mWindowWidth * 3 / 4;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mSurfaceView.setLayoutParams(lp);
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
            mFocusAnimation = AnimationUtils.loadAnimation(VRecordActivity.this, R.anim.record_focus);

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
                        videoTime.setText(Utils.secondToMinute(msg.arg1));
                    }
                    break;
            }
        }
    };

    /**
     * 初始化拍摄SDK
     */
    public void initMediaRecorder() {

        mMediaRecorder = new MediaRecorderNative();
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        mMediaRecorder.setOnSurfaveViewTouchListener(mSurfaceView);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();

    }

    /**
     * 开始录制
     */
    public void startRecord() {
        if (mMediaRecorder != null) {
            File f = new File(VCamera.getVideoCachePath());
            if (!FileUtils.checkFile(f)) {
                f.mkdirs();
            }
            String key = String.valueOf(System.currentTimeMillis());
            mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key);
            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            isRecording = true;
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.arg1 = mTimeCount;
                    msg.what = HANDLE_INVALIDATE_PROGRESS;
                    mTimeCount++;
                    mHandler.sendMessage(msg);
                    if (mTimeCount == RECORD_TIME_MAX) {// 达到指定时间，停止拍摄
                        stopRecord();
                    }
                }
            }, 0, 1000);
            if (part == null) {
                return;
            }
        }
        if (mHandler != null) {
//            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
//            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);
            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD, RECORD_TIME_MAX - mMediaObject.getDuration());
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
            isRecording = false;
            isRecordVideo = true;
            mMediaRecorder.startEncoding();
        }
        mHandler.removeMessages(HANDLE_STOP_RECORD);
    }

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
            }
            if (cacheDir.delete()) return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择图片后处理结果
        if (requestCode == AppConfig.REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                isRecordVideo = false;
                playVideo.setVisibility(View.VISIBLE);
                rightBtn.setVisibility(View.VISIBLE);
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            }
        }
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
        UIHelper.ToastMessage(VRecordActivity.this, "转码完成");
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
                mMediaRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaObject.getCurrentPart().delete();
    }

    @Override
    public void onEncodeError() {

    }
}
