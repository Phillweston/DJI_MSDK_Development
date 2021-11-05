package com.ew.autofly.module.media.view;

import android.os.Bundle;

import com.ew.autofly.R;
import com.ew.autofly.activities.BaseActivity;
import com.ew.autofly.module.media.player.DJIVideoPlayer;
import com.flycloud.autofly.video.controller.VideoController;


public class PlayDjiVideoActivity extends BaseActivity {

    private DJIVideoPlayer mVideoPlayer;
    private VideoController mVideoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_dji_video);
        initView();
    }

    private void initView() {
        mVideoPlayer = (DJIVideoPlayer) findViewById(R.id.dji_video_player);

        mVideoController=new VideoController(this);
        mVideoPlayer.setController(mVideoController);
    }
}
