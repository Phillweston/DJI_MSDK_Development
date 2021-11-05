package com.ew.autofly.module.media.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.model.CameraManager;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.video.controller.BaseVideoController;
import com.flycloud.autofly.video.player.IVideoPlayer;

import java.util.Map;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;


public class DJIVideoPlayer extends FrameLayout
        implements IVideoPlayer {

    private final String TAG = "DJIVideoPlayer";

    protected Handler mUIHandler = new Handler(Looper.getMainLooper());
    private FrameLayout mContainer;

    private BaseVideoController mController;

    private Context mContext;

    private int mCurrentState = STATE_IDLE;

    private MediaFile mMediaFile;

    
    private long mDuration;

    private int mCachedPercentage;

    private long mCurrentPosition;

    public DJIVideoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public DJIVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DJIVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);

        initMediaManager();
    }

    public void setController(BaseVideoController controller) {
        mContainer.removeView(mController);
        mController = controller;
        mController.reset();
        mController.setVideoPlayer(this);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);
    }

    public void setMediaFile(MediaFile mediaFile) {
        mMediaFile = mediaFile;
        if (mediaFile != null) {

            mController.setTitle(mediaFile.getFileName());
            mDuration = (long) (mediaFile.getDurationInSeconds() * 1000);
            mController.setLength(mDuration);
        }
    }

    @Override
    public void setUp(String url, Map<String, String> headers) {

    }

    @Override
    public void start() {

        if (getMediaManager() != null) {
            if (mMediaFile == null) {
                setCurrentState(STATE_ERROR);
                return;
            }
            getMediaManager().playVideoMediaFile(mMediaFile, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    if (null != error) {
                        setCurrentState(STATE_ERROR);
                        setResultToToast("播放失败：" + error.getDescription());
                    } else {
                        setCurrentState(STATE_PREPARING);
                    }
                }
            });
        }
    }

    @Override
    public void start(long position) {

    }

    @Override
    public void restart() {

    }

    @Override
    public void pause() {
        if (getMediaManager() != null) {
            getMediaManager().pause(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    if (null != error) {
                        setCurrentState(STATE_ERROR);
                        setResultToToast("暂停失败：" + error.getDescription());
                    } else {
                        setCurrentState(STATE_PAUSED);
                    }
                }
            });
        }

    }

    @Override
    public void seekTo(long pos) {
        Log.e(TAG, "-seek:" + pos);
        if (getMediaManager() != null) {
            getMediaManager().moveToPosition(pos,
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError error) {
                            if (null != error) {
                                setResultToToast("播放失败：" + error.getDescription());
                            }
                        }
                    });
        }
    }

    @Override
    public void setVolume(int volume) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {

    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public boolean isTinyWindow() {
        return false;
    }

    @Override
    public boolean isNormal() {
        return true;
    }

    @Override
    public int getMaxVolume() {
        return 0;
    }

    @Override
    public int getVolume() {
        return 0;
    }

    @Override
    public long getDuration() {
        return mDuration;
    }

    @Override
    public long getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public float getSpeed(float speed) {
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        return 0;
    }

    @Override
    public void enterFullScreen() {

    }

    @Override
    public boolean exitFullScreen() {
        return false;
    }

    @Override
    public void enterTinyWindow() {

    }

    @Override
    public boolean exitTinyWindow() {
        return false;
    }

    @Override
    public void releasePlayer() {

    }

    @Override
    public void release() {

    }

    private MediaManager.VideoPlaybackStateListener updatedVideoPlaybackStateListener =
            new MediaManager.VideoPlaybackStateListener() {
                @Override
                public void onUpdate(MediaManager.VideoPlaybackState videoPlaybackState) {
                    MediaFile.VideoPlaybackStatus playbackStatus = videoPlaybackState.getPlaybackStatus();
                    switch (playbackStatus) {
                        case PLAYING:
                            setCurrentState(STATE_PLAYING);
                            break;
                        case PAUSED:
                            setCurrentState(STATE_PAUSED);
                            break;
                    }

                    mCachedPercentage = videoPlaybackState.getCachedPercentage();
                    if (mCachedPercentage >= 100) {
                        setCurrentState(STATE_PREPARED);
                    }
                    mCurrentPosition = (long) (videoPlaybackState.getPlayingPosition() * 1000);
                }
            };

    private void initMediaManager() {
        Camera camera = CameraManager.getCamera();
        if (camera != null && camera.getMediaManager() != null) {
            MediaManager mediaManager = camera.getMediaManager();
            mediaManager.addMediaUpdatedVideoPlaybackStateListener(this.updatedVideoPlaybackStateListener);
            if (!mediaManager.isVideoPlaybackSupported()) {
                setResultToToast("该相机型号不支持视频回放");
                setCurrentState(STATE_IDLE);
            }
        }
    }


    private MediaManager getMediaManager() {
        MediaManager mediaManager = null;
        Camera camera = CameraManager.getCamera();
        if (camera != null) {
            mediaManager = camera.getMediaManager();
        }
        if (mediaManager == null) {
            setCurrentState(STATE_IDLE);
            setResultToToast("未连接媒体管理器");
        }
        return mediaManager;
    }

    public void setCurrentState(int state) {
        mCurrentState = state;
        mController.onPlayStateChanged(mCurrentState);
    }

    private void setResultToToast(final String result) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(EWApplication.getInstance(), result);
            }
        });
    }
}
