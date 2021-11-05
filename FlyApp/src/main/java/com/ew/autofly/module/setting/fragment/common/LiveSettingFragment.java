package com.ew.autofly.module.setting.fragment.common;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.fragment.base.BaseSettingSecondFragment;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.design.view.setting.SettingTextView;

import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;


public class LiveSettingFragment extends BaseSettingSecondFragment implements View.OnClickListener {

    private EditText mLiveUrlEt;
    private SettingTextView mLiveOperationSt;

    private String txtStartLive;
    private String txtStopLive;

    private LiveStreamManager mLiveStreamManager;

    private boolean isLiveStarted = false;

    private TextView mTvLiveSpeed;
    private SeekBar mLiveSpeedSeek;
    private Button mBtnRefresh;

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_common_live;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mLiveUrlEt = (EditText) rootView.findViewById(R.id.et_live_url);
        mLiveOperationSt = (SettingTextView) rootView.findViewById(R.id.st_live_operation);
        mTvLiveSpeed = rootView.findViewById(R.id.tv_live_speed);
        mLiveSpeedSeek = rootView.findViewById(R.id.live_speed_seek);
        mBtnRefresh = rootView.findViewById(R.id.btn_refresh);
        initData();
        initLiveStreamManager();
        initLiveUrl();
        initLiveOperation();
        initLiveSpeed();
    }

    private void initData() {
        txtStartLive = getString(R.string.start_live);
        txtStopLive = getString(R.string.stop_live);
    }

    private LiveStreamManager.OnLiveChangeListener mLiveChangeListener = new LiveStreamManager.OnLiveChangeListener() {
        @Override
        public void onStatusChanged(final int status) {
            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (status == LiveStreamManager.STATUS_STREAMING) {


                    } else if (status == LiveStreamManager.STATUS_STOP) {
                        setLiveStop();
                        ToastUtil.show(getContext(), "直播已停止");
                        dismissLoadProgressDialog();
                    } else if (status == LiveStreamManager.STATUS_ERROR) {
                        setLiveError();
                        ToastUtil.show(getContext(), "直播出错");
                        dismissLoadProgressDialog();
                    }
                }
            });
        }
    };

    
    private boolean initLiveStreamManager() {
        if (mLiveStreamManager == null) {
            mLiveStreamManager = DJISDKManager.getInstance().getLiveStreamManager();
            if (mLiveStreamManager != null) {
                mLiveStreamManager.registerListener(mLiveChangeListener);
            }
        }

        return mLiveStreamManager != null;
    }


    private void initLiveUrl() {
        Object object1 = FlyKeyManager.getInstance().getValue(FlySettingConfigKey
                .create(FlySettingConfigKey.LIVE_RTMP_URL));
        if (object1 instanceof String) {
            String url = (String) object1;
            if (!TextUtils.isEmpty(url)) {
                mLiveUrlEt.setText(url);
            }
        }
        mLiveUrlEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String url = mLiveUrlEt.getText().toString();
                    if (!TextUtils.isEmpty(url)) {
                        FlyKeyManager.getInstance().setValue(FlySettingConfigKey.create(FlySettingConfigKey.LIVE_RTMP_URL), url);

                    }
                }
                return false;
            }
        });
    }

    private void initLiveOperation() {

        if (initLiveStreamManager()) {
            if (mLiveStreamManager.isStreaming()) {
                setLiveStart();
            } else {
                setLiveStop();
            }
        }

        mLiveOperationSt.setOnClickListener(this);
    }

    private int liveProgress;

    
    private void updateLiveSpeed() {
        boolean videoStreamSpeedConfigurable = mLiveStreamManager.isVideoStreamSpeedConfigurable();
        int configuredVideoStreamSpeed = LiveStreamManager.getConfiguredVideoStreamSpeed();

        StringBuilder info = new StringBuilder();
        info.append("能否设置直播速度").append(videoStreamSpeedConfigurable).append("\n");
        info.append("直播速度:").append(configuredVideoStreamSpeed).append("\n");
        info.append("直播速度:").append(liveProgress);
        mTvLiveSpeed.setText(info.toString());

    }

    private void initLiveSpeed() {
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLiveSpeed();
            }
        });

        mLiveSpeedSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                liveProgress = progress;
                LiveStreamManager.configVideoStreamSpeed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.st_live_operation:
                if (AircraftManager.isAircraftConnected()) {
                    if (initLiveStreamManager()) {
                        showLoadProgressDialog(null);
                        if (isLiveStarted) {
                            stopLive();
                        } else {
                            new Thread() {
                                @Override
                                public void run() {
                                    startLive();
                                }
                            }.start();
                        }
                    } else {
                        showToast("error:No live stream manager");
                    }
                } else {
                    ToastUtil.show(getContext(), "飞行器未连接");
                }

                break;
            default:
                break;
        }
    }

    
    private void startLive() {
        String url = mLiveUrlEt.getText().toString();
        if (TextUtils.isEmpty(url)) {
            ToastUtil.show(getContext(), "推流地址不能为空");
            return;
        }
        FlyKeyManager.getInstance().setValue(FlySettingConfigKey.create(FlySettingConfigKey.LIVE_RTMP_URL), url);
        String liveUrl = "rtmp://" + url;
        mLiveStreamManager.setLiveUrl(liveUrl);
        final int i = mLiveStreamManager.startStream();
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (i == 0) {
                    setLiveStart();
                    showToast("直播已开始");
                } else {
                    setLiveStop();
                    showToast("直播开始失败，请重新开始");
                }
                dismissLoadProgressDialog();
            }
        });
    }

    
    private void stopLive() {
        mLiveStreamManager.stopStream();
    }

    private void setLiveStart() {
        if (mLiveOperationSt != null) {
            mLiveOperationSt.setText(txtStopLive);
            mLiveOperationSt.getTextView().setTextColor(getResources().getColor(R.color.selector_setting_text_error_color));
        }
        isLiveStarted = true;
    }

    private void setLiveStop() {
        if (mLiveOperationSt != null) {
            mLiveOperationSt.setText(txtStartLive);
            mLiveOperationSt.getTextView().setTextColor(getResources().getColor(R.color.selector_setting_text_color));
        }
        isLiveStarted = false;
    }

    private void setLiveError() {
        if (mLiveOperationSt != null) {
            mLiveOperationSt.setText(txtStartLive);
            mLiveOperationSt.getTextView().setTextColor(getResources().getColor(R.color.selector_setting_text_color));
        }
        isLiveStarted = false;
    }

    @Override
    public void onDestroy() {
        mLiveStreamManager.unregisterListener(mLiveChangeListener);
        super.onDestroy();
    }
}

