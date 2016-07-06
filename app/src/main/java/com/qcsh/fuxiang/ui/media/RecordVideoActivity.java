package com.qcsh.fuxiang.ui.media;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.common.Utils;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.VideoRecordView;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * 拍摄视频的界面（旧）
 */
public class RecordVideoActivity extends BaseActivity {
    //private MovieRecorderView mRecorderView;
    private VideoRecordView mRecorderView;
    private Button mShootBtn;
    private boolean isRecordVideo = true;
    private TextView videoTime;
    private ImageView localVideo;
    private ImageView playVideo;
    private TextView startRecord;
    private ImageView cacelVideo;
    private ArrayList<String> mSelectPath;
    private Toolbar mToolBar;
    private TextView titleText;
    private TextView rightBtn;
    private String path = null;

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("activityCircle","===========onRestart");
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Log.i("activityCircle", "===========onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        initToolBar();
        mRecorderView = (VideoRecordView) findViewById(R.id.movieRecorderView);
//        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.shoot_button);
        videoTime = (TextView) findViewById(R.id.video_time);
        startRecord = (TextView) findViewById(R.id.start_record);
        localVideo = (ImageView) findViewById(R.id.local_video);
        playVideo = (ImageView) findViewById(R.id.video_play);
        cacelVideo = (ImageView) findViewById(R.id.video_cacel);

        mShootBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRecorderView.getIsRecording()) {
                    mRecorderView.startRecord(new VideoRecordView.OnRecordListener() {
                        @Override
                        public void onRecordFinish() {
                            //handler.sendEmptyMessage(1);
                            playVideo.setVisibility(View.VISIBLE);
                            cacelVideo.setVisibility(View.VISIBLE);
                            startRecord.setVisibility(View.VISIBLE);
                            isRecordVideo = true;
                            rightBtn.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onRecording(int time) {
                            Message msg = new Message();
                            msg.arg1 = time;
                            msg.what = 0;
                            handler.sendMessage(msg);
                            //videoTime.setText(Utils.secondToMinute(time));
                        }

                        @Override
                        public void onStart() {
                            videoTime.setVisibility(View.VISIBLE);
                            startRecord.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    mRecorderView.stopRecord();
                    if (mRecorderView.getTimeCount() < 2)
                    //handler.sendEmptyMessage(1);
                    {
                        if (mRecorderView.getmRecordFile() != null) {
                            new File(mRecorderView.getmRecordFile()).delete();
                        }
                        //mRecorderView.stop();
                        Toast.makeText(RecordVideoActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    } else {
                        isRecordVideo = true;
                        rightBtn.setVisibility(View.VISIBLE);
                        playVideo.setVisibility(View.VISIBLE);
                        cacelVideo.setVisibility(View.VISIBLE);
                        startRecord.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        cacelVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorderView.getmRecordFile() != null) {
                    mRecorderView.stopRecord();
                    if (new File(mRecorderView.getmRecordFile()).delete()) {
                        videoTime.setText("00:00");
                        UIHelper.ToastMessage(RecordVideoActivity.this, "视频删除成功！");
                    } else {
                        UIHelper.ToastMessage(RecordVideoActivity.this, "视频删除失败！");
                    }
                }
            }
        });

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecordVideo) {
                    path = mRecorderView.getmRecordFile().toString();
                } else {
                    path = mSelectPath.get(0);
                }
                AppIntentManager.startSysVideoPlay(RecordVideoActivity.this, path, AppConfig.VIDEO.LOCAL_VIDEO);
            }
        });
        localVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppIntentManager.startImageSelectorForResult(RecordVideoActivity.this, mSelectPath, 1, 1);
            }
        });
    }

    private void initToolBar() {
        mToolBar = (Toolbar) findViewById(R.id.layout_toolbar_title);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.mipmap.back_while);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecorderView.backRemove();
                finish();
            }
        });
        titleText = (TextView) mToolBar.findViewById(R.id.text_title);
        titleText.setText("发视频");
        rightBtn = (TextView) mToolBar.findViewById(R.id.rightbtn);
        rightBtn.setText("完成");
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecordVideo) {
                    path = mRecorderView.getmRecordFile().toString();
                } else {
                    path = mSelectPath.get(0);
                }
                //AppIntentManager.startSubmitActivity(RecordVideoActivity.this, path, null, AppConfig.VIDEOTYPE,null);
                finish();

            }
        });
        rightBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();
        mRecorderView.initMediaRecorder();
        Log.i("activityCircle", "===========onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //isFinish = false;
        mRecorderView.stopRecord();
//        mRecorderView.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
        UtilityAdapter.freeFilterParser();
        mRecorderView.releaseRecorder();
        Log.i("activityCircle", "===========onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("activityCircle", "===========onDestroy");
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    videoTime.setText(Utils.secondToMinute(msg.arg1));
                    break;
                default:
                    //finishActivity();
                    break;
            }

        }
    };

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
}

    /*private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            // 返回到播放页面
            Intent intent = new Intent();
            Log.d("TAG", mRecorderView.getmRecordFile().getAbsolutePath());
            intent.putExtra("path", mRecorderView.getmRecordFile().getAbsolutePath());
            setResult(RESULT_OK, intent);
        }
        // isFinish = false;
        finish();
    }*/

/**
 * 录制完成回调
 *
 * @author liuyinjun
 * @date 2015-2-9
 *//*
    public interface OnShootCompletionListener {
        public void OnShootSuccess(String path, int second);

        public void OnShootFailure();
    }*/

