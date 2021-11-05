package com.flycloud.autofly.video.rtc;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flycloud.autofly.video.R;
import com.tencent.liteav.TXLiteAVCode;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;


public class RtcControlPanel extends FrameLayout implements RTCSettingDialog.ISettingListener, View.OnClickListener {

    private Context mContext;

    private RTCSettingDialog settingDlg;
    private RTCVideoView mGuestVideo;
    private RTCVideoView mMasterVideo;
    private TextView mMasterNameTv;
    private TextView mGuestNameTv;
    private ImageView mControlCloseIv;
    private ImageView mControlSettingIv;
    private ImageView mControlExtendIv;
    private ImageView mMasterCameraIv;
    private ImageView mMasterAudioIv;
    private ImageView mMasterCloseIv;
    private ImageView mGuestSpeakerIv;
    private ImageView mGuestCloseIv;
    private RelativeLayout mMasterControlRl;
    private RelativeLayout mGuestControlRl;
    private RelativeLayout mVideoPanelMaster;
    private RelativeLayout mVideoPanelGuest;


    private boolean isFrontCamera = true;
    private boolean isLocalAudioEnable = true;
    private boolean isRemoteAudioEnable = true;
    private boolean isLocalVideoEnable = true;
    private boolean isRemoteVideoEnable = true;

    private boolean isShowMasterVideoPanel = true;
    private boolean isShowGuestVideoPanel = true;

    private String mGuestUserId;

    private RtcControlParams mControlParams;

    private TRTCCloud trtcCloud;
    private TRTCCloudListenerImpl trtcListener;


    public RtcControlPanel(Context context) {
        this(context, null);
    }

    public RtcControlPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RtcControlPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.video_view_rtc_control_panel, this);
        initData();
        initView();
    }

    private void initData() {


        trtcListener = new TRTCCloudListenerImpl();
        trtcCloud = TRTCCloud.sharedInstance(mContext);
        trtcCloud.setListener(trtcListener);
    }

    private void initView() {
        settingDlg = new RTCSettingDialog(mContext, this);

        mGuestVideo = findViewById(R.id.video_guest);
        mMasterVideo = findViewById(R.id.video_master);
        mMasterNameTv = (TextView) findViewById(R.id.tv_master_name);
        mGuestNameTv = (TextView) findViewById(R.id.tv_guest_name);
        mControlCloseIv = (ImageView) findViewById(R.id.iv_control_close);
        mControlCloseIv.setOnClickListener(this);
        mControlSettingIv = (ImageView) findViewById(R.id.iv_control_setting);
        mControlSettingIv.setOnClickListener(this);
        mControlExtendIv = (ImageView) findViewById(R.id.iv_control_extend);
        mControlExtendIv.setOnClickListener(this);
        mMasterCameraIv = (ImageView) findViewById(R.id.iv_master_camera);
        mMasterCameraIv.setOnClickListener(this);
        mMasterAudioIv = (ImageView) findViewById(R.id.iv_master_audio);
        mMasterAudioIv.setOnClickListener(this);
        mMasterCloseIv = (ImageView) findViewById(R.id.iv_master_close);
        mMasterCloseIv.setOnClickListener(this);
        mGuestSpeakerIv = (ImageView) findViewById(R.id.iv_guest_speaker);
        mGuestSpeakerIv.setOnClickListener(this);
        mGuestCloseIv = (ImageView) findViewById(R.id.iv_guest_close);
        mGuestCloseIv.setOnClickListener(this);
        mMasterControlRl = (RelativeLayout) findViewById(R.id.rl_master_control);
        mGuestControlRl = (RelativeLayout) findViewById(R.id.rl_guest_control);
        mVideoPanelMaster = (RelativeLayout) findViewById(R.id.master_video_panel);
        mVideoPanelGuest = (RelativeLayout) findViewById(R.id.guest_video_panel);
    }


    public void setMasterUserName(String name) {
        mMasterNameTv.setText(name);
    }


    public void setGuestUserName(String name) {
        mGuestNameTv.setText(name);
    }


    public void start(RtcControlParams controlParams) {

        setRTCControlParams(controlParams);


        setTRTCCloudParam();



        enableLocalVideo(isLocalVideoEnable);





        setVideoFillMode(true);


        setVideoRotation(false);


        enableAudioHandFree(true);


        enableGSensor(false);


        enableAudioVolumeEvaluation(true);


        enableVideoEncMirror(true);


        setLocalViewMirrorMode(TRTCCloudDef.TRTC_VIDEO_MIRROR_TYPE_ENABLE);

        enableLocalAudio(isLocalAudioEnable);

        trtcCloud.enterRoom(controlParams.rtcParams, settingDlg.getAppScene());

    }

    private void setRTCControlParams(RtcControlParams controlParams) {
        mControlParams = controlParams;
        isLocalAudioEnable = mControlParams.isLocalAudioEnable;
        isRemoteAudioEnable = mControlParams.isRemoteAudioEnable;
        isLocalVideoEnable = mControlParams.isLocalVideoEnable;
        isRemoteVideoEnable = mControlParams.isRemoteVideoEnable;
    }


    public void end() {
        setVisibility(GONE);
        exitRoom();
    }


    private void exitRoom() {
        if (trtcCloud != null) {
            trtcCloud.exitRoom();
        }
        if (mRtcStatusListener != null) {
            mRtcStatusListener.onLocalUserExit();
        }
    }


    private void setTRTCCloudParam() {





        TRTCCloudDef.TRTCVideoEncParam encParam = new TRTCCloudDef.TRTCVideoEncParam();
        encParam.videoResolution = settingDlg.getResolution();
        encParam.videoFps = settingDlg.getVideoFps();
        encParam.videoBitrate = settingDlg.getVideoBitrate();

        trtcCloud.setVideoEncoderParam(encParam);

       /* TRTCCloudDef.TRTCNetworkQosParam qosParam = new TRTCCloudDef.TRTCNetworkQosParam();
        qosParam.controlMode = settingDlg.getQosMode();
        qosParam.preference = settingDlg.getQosPreference();
        trtcCloud.setNetworkQosParam(qosParam);*/



      /*
        TRTCCloudDef.TRTCVideoEncParam smallParam = new TRTCCloudDef.TRTCVideoEncParam();
        smallParam.videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_160_90;
        smallParam.videoFps = settingDlg.getVideoFps();
        smallParam.videoBitrate = 100;
        smallParam.videoResolutionMode = settingDlg.isVideoVertical() ? TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_PORTRAIT : TRTCCloudDef.TRTC_VIDEO_RESOLUTION_MODE_LANDSCAPE;
        trtcCloud.enableEncSmallVideoStream(settingDlg.enableSmall, smallParam);

        trtcCloud.setPriorRemoteVideoStreamType(settingDlg.priorSmall ? TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_SMALL : TRTCCloudDef.TRTC_VIDEO_STREAM_TYPE_BIG);
*/
    }

    private void showMasterVideoPanel(boolean isShow) {
        isShowMasterVideoPanel = isShow;
        mVideoPanelMaster.setVisibility(isShow ? VISIBLE : INVISIBLE);
        mMasterControlRl.setVisibility(isShow ? VISIBLE : INVISIBLE);
        enableLocalVideo(isShow);
    }

    private void showGuestVideoPanel(boolean isShow) {
        isShowGuestVideoPanel = isShow;
        mVideoPanelGuest.setVisibility(isShow ? VISIBLE : INVISIBLE);
        mGuestControlRl.setVisibility(isShow ? VISIBLE : INVISIBLE);
        enableRemoteVideo(isShow);
    }


    private void enableLocalVideo(boolean enable) {
        startLocalVideo(enable);
    }


    private void enableLocalAudio(boolean enable) {

        enableLocalAudioCapture(enable);

        mMasterAudioIv.setImageResource(enable ?
                R.drawable.video_ic_audio_record_enable : R.drawable.video_ic_audio_record_disable);
    }

    /**
     * 开启/关闭本地声音采集
     *
     * @param enable
     */
    private void enableLocalAudioCapture(boolean enable) {
        if (enable) {
            trtcCloud.startLocalAudio();
        } else {
            trtcCloud.stopLocalAudio();
        }
    }

    private void enableRemoteVideo(boolean enable) {
        if (enable) {
            trtcCloud.startRemoteView(mGuestUserId, mGuestVideo);
        } else {
            trtcCloud.stopAllRemoteView();
        }
    }

    private void enableRemoteAudio(boolean enable) {
        trtcCloud.muteAllRemoteAudio(!enable);
        mGuestSpeakerIv.setImageResource(enable ?
                R.drawable.video_ic_speaker_enable : R.drawable.video_ic_speaker_disable);
    }


    private void onShowSettingDlg() {
        settingDlg.show();
    }


    @Override
    public void onComplete() {
        setTRTCCloudParam();
        setVideoFillMode(settingDlg.isVideoVertical());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_control_close) {
            end();
        } else if (i == R.id.iv_control_setting) {// TODO 19/05/14
            onShowSettingDlg();
        } else if (i == R.id.iv_control_extend) {// TODO 19/05/14
            if (!isShowMasterVideoPanel) {
                showMasterVideoPanel(true);
            }
            if (!isShowGuestVideoPanel) {
                showGuestVideoPanel(true);
            }
        } else if (i == R.id.iv_master_camera) {
            trtcCloud.switchCamera();
        } else if (i == R.id.iv_master_audio) {
            isLocalAudioEnable = !isLocalAudioEnable;
            enableLocalAudio(isLocalAudioEnable);
        } else if (i == R.id.iv_master_close) {
            showMasterVideoPanel(false);
        } else if (i == R.id.iv_guest_speaker) {
            isRemoteAudioEnable = !isRemoteAudioEnable;
            enableRemoteAudio(isRemoteAudioEnable);
        } else if (i == R.id.iv_guest_close) {// TODO 19/05/14
            showGuestVideoPanel(false);
        } else {
        }
    }


    public class TRTCCloudListenerImpl extends TRTCCloudListener implements TRTCCloudListener.TRTCVideoRenderListener {

        @Override
        public void onRenderVideoFrame(String s, int i, TRTCCloudDef.TRTCVideoFrame trtcVideoFrame) {

        }

        @Override
        public void onEnterRoom(long elapsed) {
            super.onEnterRoom(elapsed);

        }

        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            Toast.makeText(mContext, "视频通讯出错：" + errCode + "-" + errMsg, Toast.LENGTH_SHORT).show();
            if (errCode == TXLiteAVCode.ERR_ROOM_ENTER_FAIL) {
                exitRoom();
            }
        }

        @Override
        public void onUserEnter(String userId) {
            super.onUserEnter(userId);
            mGuestUserId = userId;

            trtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);
            if (isRemoteVideoEnable) {
                trtcCloud.startRemoteView(userId, mGuestVideo);
            }
        }

        @Override
        public void onUserExit(String userId, int reason) {

            trtcCloud.stopRemoteView(userId);
            if (mRtcStatusListener != null) {
                mRtcStatusListener.onRemoteUserExit();
            }
        }
    }

    private void setVideoFillMode(boolean bFillMode) {
        if (bFillMode) {
            trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
        } else {
            trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);
        }
    }

    private void setVideoRotation(boolean bVertical) {
        if (bVertical) {
            trtcCloud.setLocalViewRotation(TRTCCloudDef.TRTC_VIDEO_ROTATION_0);
        } else {
            trtcCloud.setLocalViewRotation(TRTCCloudDef.TRTC_VIDEO_ROTATION_90);
        }
    }

    private void enableAudioHandFree(boolean bEnable) {
        if (bEnable) {
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_SPEAKER);
        } else {
            trtcCloud.setAudioRoute(TRTCCloudDef.TRTC_AUDIO_ROUTE_EARPIECE);
        }
    }

    private void enableVideoEncMirror(boolean bMirror) {
        trtcCloud.setVideoEncoderMirror(bMirror);
    }

    private void setLocalViewMirrorMode(int mirrorMode) {
        trtcCloud.setLocalViewMirror(mirrorMode);
    }

    private void enableGSensor(boolean bEnable) {
        if (bEnable) {
            trtcCloud.setGSensorMode(TRTCCloudDef.TRTC_GSENSOR_MODE_UIFIXLAYOUT);
        } else {
            trtcCloud.setGSensorMode(TRTCCloudDef.TRTC_GSENSOR_MODE_DISABLE);
        }
    }

    private void enableAudioVolumeEvaluation(boolean bEnable) {
        if (bEnable) {
            trtcCloud.enableAudioVolumeEvaluation(200);
        } else {
            trtcCloud.enableAudioVolumeEvaluation(0);
        }
    }

    private void startLocalVideo(boolean enable) {
        mMasterVideo.setUserId(mControlParams.rtcParams.userId);
        if (enable) {

            trtcCloud.startLocalPreview(isFrontCamera, mMasterVideo);
        } else {
            trtcCloud.stopLocalPreview();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        exitRoom();

        TRTCCloud.destroySharedInstance();
        trtcCloud = null;
    }

    private OnRtcStatusListener mRtcStatusListener;

    public void setRtcStatusListener(OnRtcStatusListener rtcStatusListener) {
        mRtcStatusListener = rtcStatusListener;
    }

    public interface OnRtcStatusListener {


        void onLocalUserExit();


        void onRemoteUserExit();
    }


    public static class RtcControlParams {

        private TRTCCloudDef.TRTCParams rtcParams;
        private boolean isFrontCamera;
        private boolean isLocalAudioEnable = true;
        private boolean isRemoteAudioEnable = true;
        private boolean isLocalVideoEnable = true;
        private boolean isRemoteVideoEnable = true;

        private RtcControlParams(Builder builder) {
            rtcParams = builder.rtcParams;
            isFrontCamera = builder.isFrontCamera;
            isLocalAudioEnable = builder.isLocalAudioEnable;
            isRemoteAudioEnable = builder.isRemoteAudioEnable;
            isLocalVideoEnable = builder.isLocalVideoEnable;
            isRemoteVideoEnable = builder.isRemoteVideoEnable;
        }

        public static final class Builder {
            private TRTCCloudDef.TRTCParams rtcParams;
            private boolean isFrontCamera = true;
            private boolean isLocalAudioEnable = true;
            private boolean isRemoteAudioEnable = true;
            private boolean isLocalVideoEnable = true;
            private boolean isRemoteVideoEnable = true;

            public Builder() {
            }

            public Builder rtcParams(TRTCCloudDef.TRTCParams val) {
                rtcParams = val;
                return this;
            }

            public Builder isFrontCamera(boolean val) {
                isFrontCamera = val;
                return this;
            }

            public Builder isLocalAudioEnable(boolean val) {
                isLocalAudioEnable = val;
                return this;
            }

            public Builder isRemoteAudioEnable(boolean val) {
                isRemoteAudioEnable = val;
                return this;
            }

            public Builder isLocalVideoEnable(boolean val) {
                isLocalVideoEnable = val;
                return this;
            }

            public Builder isRemoteVideoEnable(boolean val) {
                isRemoteVideoEnable = val;
                return this;
            }

            public RtcControlParams build() {
                return new RtcControlParams(this);
            }
        }
    }
}
