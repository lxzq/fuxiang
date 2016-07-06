package com.qcsh.fuxiang.ui.media;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.qcsh.fuxiang.R;

/**
 * Created by Administrator on 2015/8/24.
 */
public class SurfaceActivity extends Activity implements  MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String TAG = "SurfaceActivity";
    private VideoView mVideoView;
    private Uri mUri;
    private int mPositionWhenPaused = -1;

    public static final String VIDEO_PATH = "video_path";
    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.video_view);


        path = getIntent().getStringExtra(VIDEO_PATH);
        mUri = Uri.parse(path);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //mVideoView = (VideoView)findViewById(R.id.video_view);
        mVideoView.setMediaController(new MediaController(this));
    }

    public void onStart() {
        // Play Video
        mVideoView.setVideoURI(mUri);
        mVideoView.start();
       // mVideoView.requestFocus();
        super.onStart();
    }

    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();

        super.onPause();
    }

    public void onResume() {
        // Resume video player
        if(mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }
        super.onResume();
    }

    public boolean onError(MediaPlayer player, int arg1, int arg2) {
        return false;
    }

    public void onCompletion(MediaPlayer mp) {
        this.finish();
    }
}
