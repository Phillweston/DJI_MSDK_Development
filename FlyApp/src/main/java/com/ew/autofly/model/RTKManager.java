package com.ew.autofly.model;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.model.phone.SoundManager;

import dji.common.error.DJIError;
import dji.common.flightcontroller.rtk.NetworkServiceSettings;
import dji.common.flightcontroller.rtk.ReferenceStationSource;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.RTK;
import dji.sdk.network.RTKNetworkServiceProvider;


public class RTKManager extends BaseInitManager {

    public final static class VOICE_TIPS {

        public final static int RTK_SIGNAL_WEAK = 0;
    }

    private SoundManager mSoundManager;


    private static RTKManager INSTANCE;

    public RTKManager() {

    }

    public static RTKManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RTKManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RTKManager();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void init() {
        mSoundManager = new SoundManager(EWApplication.getInstance());
        mSoundManager.init();
        mSoundManager.setPlayIntervalTime(5000);
        mSoundManager.putSound(VOICE_TIPS.RTK_SIGNAL_WEAK, R.raw.voice_tips_rtk_singal_weak);
    }

    @Override
    public void release() {
        if (mSoundManager != null) {
            mSoundManager.release();
        }
    }

    /**
     * 播放语音提示
     *
     * @param voiceTips {@link VOICE_TIPS}
     * @throws
     */
    public void playVoiceTips(int voiceTips) {
        if (mSoundManager != null) {
            mSoundManager.playSound(voiceTips);
        }
    }

    public static RTK getRTK() {
        RTK rtk = null;
        FlightController flightController = AircraftManager.getFlightController();
        if (flightController != null) {
            rtk = flightController.getRTK();
        }
        return rtk;
    }

    
    public static void initNetWorkRtkService() {
        if (AircraftManager.isSupportNetWorkRtkSetting()) {
            RTK rtk = getRTK();
            if (rtk != null) {

                ReferenceStationSource rtkReferenceStationSource = DataManager.getRtkReferenceStationSource();
                rtk.setReferenceStationSource(rtkReferenceStationSource, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        try {
                            if (djiError == null) {
                                switch (rtkReferenceStationSource) {
                                    case NETWORK_RTK:
                                        RTKNetworkServiceProvider.getInstance().setNetworkServiceCoordinateSystem(DataManager.getRtkCoordinateSystem(), null);
                                        RTKNetworkServiceProvider.getInstance().startNetworkService(null);
                                        break;
                                    case CUSTOM_NETWORK_SERVICE:
                                        NetworkServiceSettings networkServiceSettings = DataManager.getNetworkServiceSettings();
                                        if (networkServiceSettings != null) {
                                            RTKNetworkServiceProvider.getInstance().setCustomNetworkSettings(DataManager.getNetworkServiceSettings());
                                            RTKNetworkServiceProvider.getInstance().startNetworkService(null);
                                        }
                                        break;
                                    case NONE:
                                        RTKNetworkServiceProvider.getInstance().stopNetworkService(null);
                                        break;
                                }
                            }
                        } catch (Exception e) {
                        }

                    }
                });
            }
        }
    }
}
