package com.flycloud.autofly.video.rtc;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.flycloud.autofly.video.R;
import com.tencent.trtc.TRTCCloudDef;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Module:   TRTCSettingDialog
 * <p>
 * Function: 用于对视频通话的分辨率、帧率和流畅模式进行调整，并支持记录下这些设置项
 */
public class RTCSettingDialog extends Dialog {
    private final static String TAG = RTCSettingDialog.class.getSimpleName();
    private Spinner spSolution, spFps;
    private SeekBar sbVideoBitrate;
    private CheckBox cbSave, cbPriorSmall, cbEnableSmall;
    private RadioButton rbSmooth, rbClear, rbHorizontal, rbVertical, rbServer, rbClient, rbVoiceCall, rbLive;
    private TextView tvVideoBitrate, tvSubmit;
    private int curRes = 2;

    static class TRTCSettingBitrateTable {
        public int resolution;
        public int defaultBitrate;
        public int minBitrate;
        public int maxBitrate;
        public int step;

        public TRTCSettingBitrateTable(int resolution, int defaultBitrate, int minBitrate, int maxBitrate, int step) {
            this.resolution = resolution;
            this.defaultBitrate = defaultBitrate;
            this.minBitrate = minBitrate;
            this.maxBitrate = maxBitrate;
            this.step = step;
        }
    }

    private ArrayList<TRTCSettingBitrateTable> paramArray;


    private final static String PER_DATA = "per_data";

    private final static String PER_RESOLUTION = "per_resolution";
    private final static String PER_VIDEOFPS = "per_videofps";
    private final static String PER_VIDEOBITRATE = "per_videobitrate";
    private final static String PER_HIGHQUALITY = "per_highquality";
    private final static String PER_VIDEO_ORIENTATION = "per_video_orientation";
    private final static String PER_QOSTYPE = "per_qos_type";
    private final static String PER_CON_TYPE = "per_control_type";
    private final static String PER_APP_SCENCE = "per_app_scence";
    private final static String PER_SAVEFLAG = "per_save_flag";
    private final static String PER_ENABLE_SMALL = "per_enable_small";
    private final static String PER_PRIOR_SMALL = "per_prior_small";

    final static int DEFAULT_BITRATE = 600;
    final static int DEFAULT_FPS = 15;

    private int videoResolution = TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
    private int videoFps = DEFAULT_FPS;
    private int videoBitrate = DEFAULT_BITRATE;
    private boolean highQuality = true;
    private boolean videoVertical = true;
    private boolean saveFlag = true;
    public boolean enableSmall = false;
    public boolean priorSmall = false;
    private int qosPreference = TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_CLEAR;
    private int qosMode = TRTCCloudDef.VIDEO_QOS_CONTROL_SERVER;
    private int appScene = TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL;

    public interface ISettingListener {
        void onComplete();
    }

    private WeakReference<ISettingListener> settingListener;

    public RTCSettingDialog(Context context, ISettingListener listener) {
        super(context, R.style.room_setting_dlg);
        settingListener = new WeakReference<>(listener);

        loadCache(context);
    }


    public int getResolution() {
        return videoResolution;
    }

    public int getQosPreference() {
        return qosPreference;
    }

    public boolean isVideoVertical() {
        return videoVertical;
    }

    public int getQosMode() {
        return qosMode;
    }

    public int getVideoFps() {
        return videoFps;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public int getAppScene() {
        return appScene;
    }

    public void show() {
        super.show();
        updateDialogValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_rtc_dialog_setting);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(true);
        paramArray = new ArrayList<>();
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_160_160, 250, 40, 300, 10));
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_320_180, 350, 80, 350, 10));
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_320_240, 400, 100, 400, 10));
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_480_480, 500, 200, 1000, 10));
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360, 600, 200, 1000, 10));
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_480, 700, 250, 1000, 50));
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_960_540, 900, 400, 1600, 50));
        paramArray.add(new TRTCSettingBitrateTable(TRTCCloudDef.TRTC_VIDEO_RESOLUTION_1280_720, 1250, 500, 2000, 50));

        initView();
    }

    private void initView() {
        spSolution = (Spinner) findViewById(R.id.sp_solution);
        spFps = (Spinner) findViewById(R.id.sp_video_fps);
        sbVideoBitrate = (SeekBar) findViewById(R.id.sk_video_bitrate);
        cbEnableSmall = (CheckBox) findViewById(R.id.cb_enable_small);
        cbPriorSmall = (CheckBox) findViewById(R.id.cb_prior_small);
        cbSave = (CheckBox) findViewById(R.id.cb_save);
        tvVideoBitrate = (TextView) findViewById(R.id.tv_video_bitrate);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        rbSmooth = (RadioButton) findViewById(R.id.rb_smooth);
        rbClear = (RadioButton) findViewById(R.id.rb_clear);
        rbHorizontal = (RadioButton) findViewById(R.id.rb_horizontal);
        rbVertical = (RadioButton) findViewById(R.id.rb_vertical);
        rbClient = (RadioButton) findViewById(R.id.rb_client);
        rbServer = (RadioButton) findViewById(R.id.rb_server);
        rbLive = (RadioButton) findViewById(R.id.rb_live);
        rbVoiceCall = (RadioButton) findViewById(R.id.rb_voicecall);

        ArrayAdapter<String> spinnerAadapter = new ArrayAdapter<String>(getContext(),
                R.layout.video_textview_spinner, getContext().getResources().getStringArray(R.array.video_rtc_solution));
        spSolution.setAdapter(spinnerAadapter);
        spSolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curRes = position;
                updateSolution(curRes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> spinnerFpsAadapter = new ArrayAdapter<String>(getContext(),
                R.layout.video_textview_spinner, getContext().getResources().getStringArray(R.array.video_rtc_video_fps));
        spFps.setAdapter(spinnerFpsAadapter);
        spFps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvVideoBitrate.setText("" + getBitrate(sbVideoBitrate.getProgress()) + "kbps");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sbVideoBitrate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvVideoBitrate.setText("" + getBitrate(progress) + "kbps");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tvSubmit.setClickable(true);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoResolution = getResolution(spSolution.getSelectedItemPosition());
                videoFps = getFps(spFps.getSelectedItemPosition());
                videoBitrate = getBitrate(sbVideoBitrate.getProgress());
               /* qosPreference = rbSmooth.isChecked() ? TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_SMOOTH : TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_CLEAR;
                videoVertical = rbVertical.isChecked();
                qosMode = rbClient.isChecked() ? TRTCCloudDef.VIDEO_QOS_CONTROL_CLIENT : TRTCCloudDef.VIDEO_QOS_CONTROL_SERVER;
                appScene = rbLive.isChecked() ? TRTCCloudDef.TRTC_APP_SCENE_LIVE : TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL;
                enableSmall = cbEnableSmall.isChecked();
                priorSmall = cbPriorSmall.isChecked();
                saveFlag = cbSave.isChecked();

                if (saveFlag) {
                    saveCache(getContext());
                } else {
                    try {
                        SharedPreferences shareInfo = getContext().getSharedPreferences(PER_DATA, 0);
                        SharedPreferences.Editor editor = shareInfo.edit();
                        editor.putBoolean(PER_SAVEFLAG, saveFlag);
                        editor.commit();
                    } catch (Exception e) {

                    }
                }*/

                saveCache(getContext());

                ISettingListener api = settingListener.get();
                if (api != null) {
                    api.onComplete();
                }

                dismiss();
            }
        });
    }

    private void updateDialogValue() {
        curRes = getResolutionPos(videoResolution);
        spSolution.setSelection(curRes);
        cbSave.setChecked(saveFlag);
        cbPriorSmall.setChecked(priorSmall);
        cbEnableSmall.setChecked(enableSmall);
        updateSolution(curRes);
        spFps.setSelection(getFpsPos(videoFps));
        sbVideoBitrate.setProgress(getBitrateProgress(videoBitrate));
        tvVideoBitrate.setText("" + getBitrate(sbVideoBitrate.getProgress()) + "kbps");

        rbVertical.setChecked(videoVertical);
        rbHorizontal.setChecked(!videoVertical);

        if (qosPreference == TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_SMOOTH) {
            rbSmooth.setChecked(true);
        } else {
            rbClear.setChecked(true);
        }
        if (qosMode == TRTCCloudDef.VIDEO_QOS_CONTROL_CLIENT) {
            rbClient.setChecked(true);
        } else {
            rbServer.setChecked(true);
        }
        if (appScene == TRTCCloudDef.TRTC_APP_SCENE_LIVE) {
            rbLive.setChecked(true);
        } else {
            rbVoiceCall.setChecked(true);
        }
    }

    private int getResolutionPos(int resolution) {
        for (int i = 0; i < paramArray.size(); i++) {
            if (resolution == (paramArray.get(i).resolution)) {
                return i;
            }
        }
        return 4;
    }

    private int getResolution(int pos) {
        if (pos >= 0 && pos < paramArray.size()) {
            return paramArray.get(pos).resolution;
        }
        return TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360;
    }

    private int getFpsPos(int fps) {
        switch (fps) {
            case 15:
                return 0;
            case 20:
                return 1;
            default:
                return 0;
        }
    }

    private int getFps(int pos) {
        switch (pos) {
            case 0:
                return 15;
            case 1:
                return 20;
            default:
                return 15;
        }
    }

    private int getMinBitrate(int pos) {
        if (pos >= 0 && pos < paramArray.size()) {
            return paramArray.get(pos).minBitrate;
        }
        return 300;
    }

    private int getMaxBitrate(int pos) {
        if (pos >= 0 && pos < paramArray.size()) {
            return paramArray.get(pos).maxBitrate;
        }
        return 1000;
    }

    private int getDefBitrate(int pos) {
        if (pos >= 0 && pos < paramArray.size()) {
            return paramArray.get(pos).defaultBitrate;
        }
        return 400;
    }

    
    private int getStepBitrate(int pos) {
        if (pos >= 0 && pos < paramArray.size()) {
            return paramArray.get(pos).step;
        }
        return 10;
    }

    private int getBitrateProgress(int bitrate) {
        int minBitrate = getMinBitrate(curRes);
        int stepBitrate = getStepBitrate(curRes);

        int progress = (bitrate - minBitrate) / stepBitrate;
        Log.e(TAG, "getBitrateProgress->progress: " + progress + ", min: " + minBitrate + ", stepBitrate: " + stepBitrate + "/" + bitrate);
        return progress;
    }

    private int getBitrate(int progress) {
        int minBitrate = getMinBitrate(curRes);
        int maxBitrate = getMaxBitrate(curRes);
        int stepBitrate = getStepBitrate(curRes);
        int bit = (progress * stepBitrate) + minBitrate;
        Log.e(TAG, "getBitrate->bit: " + bit + ", min: " + minBitrate + ", max: " + maxBitrate);
        return bit;
    }

    private void updateSolution(int pos) {
        int minBitrate = getMinBitrate(curRes);
        int maxBitrate = getMaxBitrate(curRes);

        int stepBitrate = getStepBitrate(curRes);
        int max = (maxBitrate - minBitrate) / stepBitrate;
        if (sbVideoBitrate.getMax() != max) {
            sbVideoBitrate.setMax(max);
            int defBitrate = getDefBitrate(curRes);
            sbVideoBitrate.setProgress(getBitrateProgress(defBitrate));
        } else {
            sbVideoBitrate.setMax(max);
        }
    }

    private void saveCache(Context context) {
        try {
            SharedPreferences shareInfo = context.getSharedPreferences(PER_DATA, 0);
            SharedPreferences.Editor editor = shareInfo.edit();
            editor.putInt(PER_RESOLUTION, videoResolution);
            editor.putInt(PER_VIDEOFPS, videoFps);
            editor.putInt(PER_VIDEOBITRATE, videoBitrate);
            editor.putBoolean(PER_HIGHQUALITY, highQuality);
            editor.putBoolean(PER_VIDEO_ORIENTATION, videoVertical);
            editor.putInt(PER_QOSTYPE, qosPreference);
            editor.putInt(PER_CON_TYPE, qosMode);
            editor.putInt(PER_APP_SCENCE, appScene);
            editor.putBoolean(PER_SAVEFLAG, saveFlag);
            editor.putBoolean(PER_ENABLE_SMALL, enableSmall);
            editor.putBoolean(PER_PRIOR_SMALL, priorSmall);
            editor.commit();
        } catch (Exception e) {

        }

    }

    private void loadCache(Context context) {
        try {
            SharedPreferences shareInfo = context.getSharedPreferences(PER_DATA, 0);
            videoResolution = shareInfo.getInt(PER_RESOLUTION, TRTCCloudDef.TRTC_VIDEO_RESOLUTION_640_360);
            videoFps = shareInfo.getInt(PER_VIDEOFPS, DEFAULT_FPS);
            videoBitrate = shareInfo.getInt(PER_VIDEOBITRATE, DEFAULT_BITRATE);
            highQuality = shareInfo.getBoolean(PER_HIGHQUALITY, true);
            videoVertical = shareInfo.getBoolean(PER_VIDEO_ORIENTATION, videoVertical);
            qosPreference = shareInfo.getInt(PER_QOSTYPE, TRTCCloudDef.TRTC_VIDEO_QOS_PREFERENCE_CLEAR);
            saveFlag = shareInfo.getBoolean(PER_SAVEFLAG, true);
            enableSmall = shareInfo.getBoolean(PER_ENABLE_SMALL, false);
            priorSmall = shareInfo.getBoolean(PER_PRIOR_SMALL, false);
            qosMode = shareInfo.getInt(PER_CON_TYPE, TRTCCloudDef.VIDEO_QOS_CONTROL_SERVER);
            appScene = shareInfo.getInt(PER_APP_SCENCE, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
        } catch (Exception e) {

        }


    }
}
