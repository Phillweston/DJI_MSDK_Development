package com.ew.autofly.model;

import androidx.annotation.Nullable;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.event.flight.VisionDetectionEvent;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.model.phone.SoundManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dji.common.flightcontroller.ObstacleDetectionSector;
import dji.common.flightcontroller.VisionDetectionState;
import dji.keysdk.DJIKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.KeyListener;


public class VisionManager extends BaseInitManager {

    private DJIKey VISION_DETECTION_STATE_KEY = FlightControllerKey.createFlightAssistantKey(FlightControllerKey.VISION_DETECTION_STATE);
    private DJIKey IS_FLYING_KEY = FlightControllerKey.create(FlightControllerKey.IS_FLYING);

    private FlySettingConfigKey RADAR_SOUND_ENABLE_KEY = FlySettingConfigKey.create(FlySettingConfigKey.RADAR_SOUND_ENABLE);

    private RadarLevel mRadarLevel = RadarLevel.LEVEL_MAX;

    private boolean isRadarSoundEnable;

    private boolean isFlying;

    private SoundManager mSoundManager;

    private static VisionManager INSTANCE;

    public VisionManager() {

    }

    public static VisionManager getInstance() {
        if (INSTANCE == null) {
            synchronized (VisionManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VisionManager();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void init() {
        try {
            EventBus.getDefault().register(this);
            setUpKeys();
            initSP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        try {
            EventBus.getDefault().unregister(this);
            tearDownKeys();
            if (mSoundManager != null) {
                mSoundManager.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playWarningSound(RadarLevel radarLevel) {
        if (radarLevel.value() >= RadarLevel.LEVEL_100.value()
                && radarLevel.value() <= RadarLevel.LEVEL_500.value()) {
            if (mSoundManager != null) {
                mSoundManager.playSound(radarLevel.value());
            }
        }
    }

    private void initSP() {
        mSoundManager = new SoundManager(EWApplication.getInstance());
        mSoundManager.init();
        mSoundManager.setPlayIntervalTime(1000);
        mSoundManager.putSound(RadarLevel.LEVEL_100.value(), R.raw.radar_100);
        mSoundManager.putSound(RadarLevel.LEVEL_250.value(), R.raw.radar_250);
        mSoundManager.putSound(RadarLevel.LEVEL_500.value(), R.raw.radar_500);
        mSoundManager.putSound(RadarLevel.LEVEL_1000.value(), R.raw.radar_1000);
    }

    private void setUpKeys() {

        KeyManager.getInstance().addListener(VISION_DETECTION_STATE_KEY, mDetectionStateListener);
        KeyManager.getInstance().addListener(IS_FLYING_KEY, mIsFlyingListener);

        Object isOpenRadar = FlyKeyManager.getInstance().getValue(RADAR_SOUND_ENABLE_KEY);
        isRadarSoundEnable = isOpenRadar != null && (boolean) isOpenRadar;
        FlyKeyManager.getInstance().addListener(RADAR_SOUND_ENABLE_KEY, mRadarSoundOpenListener);
    }

    private void tearDownKeys() {
        KeyManager.getInstance().removeListener(mDetectionStateListener);
        KeyManager.getInstance().removeListener(mIsFlyingListener);

        FlyKeyManager.getInstance().removeListener(mRadarSoundOpenListener);
    }

    private KeyListener mDetectionStateListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {

            if (newValue instanceof VisionDetectionState) {
                VisionDetectionState visionDetectionState = (VisionDetectionState) newValue;
                onVisionDetectionUpdate(visionDetectionState);
            }
        }
    };


    private KeyListener mIsFlyingListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof Boolean) {
                isFlying = (Boolean) newValue;
            }
        }
    };

    private com.ew.autofly.internal.key.callback.KeyListener mRadarSoundOpenListener = new com.ew.autofly.internal.key.callback.KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof Boolean) {
                isRadarSoundEnable = (boolean) newValue;
            }
        }
    };

    private void onVisionDetectionUpdate(VisionDetectionState visionDetectionState) {
        ObstacleDetectionSector[] detectionSectors = visionDetectionState.getDetectionSectors();

        if (detectionSectors == null) {
            return;
        }

        RadarLevel minWarningLevel = RadarLevel.LEVEL_MAX;


        for (ObstacleDetectionSector detectionSector : detectionSectors) {


            float obstacleDistanceInMeters = detectionSector.getObstacleDistanceInMeters();

            RadarLevel warningLevel = RadarLevel.getRadarLevel(obstacleDistanceInMeters);

            int value = warningLevel.value();
            if (value < minWarningLevel.value()) {
                minWarningLevel = warningLevel;
            }
        }

        mRadarLevel = minWarningLevel;

        if (isRadarSoundEnable && isFlying) {
            playWarningSound(mRadarLevel);
        }
    }

    public enum RadarLevel {

        LEVEL_100(0, 1.0f),//1米
        LEVEL_250(1, 2.5f),//2.5米
        LEVEL_500(2, 5.0f),//5米
        LEVEL_1000(3, 10.0f),//10米
        LEVEL_MAX(255, Float.MAX_VALUE);

        private int value;

        private float distance;

        RadarLevel(int paramInt, float distance) {
            this.value = paramInt;
            this.distance = distance;
        }

        public static RadarLevel getRadarLevel(float obstacleDistanceInMeters) {
            RadarLevel level = RadarLevel.LEVEL_MAX;
            if (obstacleDistanceInMeters > 0 && obstacleDistanceInMeters <= RadarLevel.LEVEL_100.getDistance()) {
                level = RadarLevel.LEVEL_100;
            } else if (obstacleDistanceInMeters > RadarLevel.LEVEL_100.getDistance()
                    && obstacleDistanceInMeters < RadarLevel.LEVEL_250.getDistance()) {
                level = RadarLevel.LEVEL_250;
            } else if (obstacleDistanceInMeters > RadarLevel.LEVEL_250.getDistance()
                    && obstacleDistanceInMeters < RadarLevel.LEVEL_500.getDistance()) {
                level = RadarLevel.LEVEL_500;
            } else if (obstacleDistanceInMeters > RadarLevel.LEVEL_500.getDistance()
                    && obstacleDistanceInMeters < RadarLevel.LEVEL_1000.getDistance()) {
                level = RadarLevel.LEVEL_1000;
            }
            return level;
        }

        public float getDistance() {
            return distance;
        }

        public int value() {
            return this.value;
        }

        public boolean _equals(int paramInt) {
            return this.value == paramInt;
        }

        public static RadarLevel find(int paramInt) {
            RadarLevel mRadarLevel = LEVEL_MAX;
            for (int i = 0; i < values().length; i++) {
                if (values()[i]._equals(paramInt)) {
                    mRadarLevel = values()[i];
                    break;
                }
            }
            return mRadarLevel;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVisionDetectionState(VisionDetectionEvent event) {
        VisionDetectionState visionDetectionState = event.getVisionDetectionState();
        onVisionDetectionUpdate(visionDetectionState);
    }
}
