package com.ew.autofly.model;

import androidx.annotation.Nullable;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.model.phone.SoundManager;

import java.util.concurrent.TimeUnit;

import dji.common.battery.BatteryCellVoltageLevel;
import dji.common.battery.BatteryState;
import dji.common.product.Model;
import dji.keysdk.BatteryKey;
import dji.keysdk.DJIKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.KeyListener;
import dji.sdk.battery.Battery;
import dji.sdk.products.Aircraft;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


public class BatteryManager extends BaseInitManager {

    public final static class VOICE_TIPS {


        public final static int LOW_VOLTAGE_LEVEL_1 = 1;
        public final static int LOW_VOLTAGE_LEVEL_2 = 2;
        public final static int LOW_VOLTAGE_LEVEL_3 = 3;
    }


    public final static int MIN_MISSION_POWER = 20;
    public final static float DEFAULT_BATTERY_LIFE_TIME = 23;
    public final static float UNLIMITED_BATTERY_LIFE_TIME = 120;

    private DJIKey CELL_VOLTAGE_LEVEL_KEY = BatteryKey.create(BatteryKey.CELL_VOLTAGE_LEVEL);

    private SoundManager mSoundManager;

    private static BatteryManager INSTANCE;

    public BatteryManager() {

    }

    public static BatteryManager getInstance() {
        if (INSTANCE == null) {
            synchronized (BatteryManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BatteryManager();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void init() {

        try {
            setUpKeys();
            mSoundManager = new SoundManager(EWApplication.getInstance());
            mSoundManager.init();
            mSoundManager.setPlayIntervalTime(5000);
            mSoundManager.putSound(VOICE_TIPS.LOW_VOLTAGE_LEVEL_1, R.raw.voice_tips_low_voltage_level1);
            mSoundManager.putSound(VOICE_TIPS.LOW_VOLTAGE_LEVEL_2, R.raw.voice_tips_low_voltage_level2);
            mSoundManager.putSound(VOICE_TIPS.LOW_VOLTAGE_LEVEL_3, R.raw.voice_tips_low_voltage_level3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        try {
            tearDownKeys();
            if (mSoundManager != null) {
                mSoundManager.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 获取机型的续航时间
     *
     * @return
     */
    public static float getBatteryLifeTime() {

        float batteryLifeTime = DEFAULT_BATTERY_LIFE_TIME;

        Model model = AircraftManager.getModel();
        if (model != null) {
            switch (model) {
                case MAVIC_2_ENTERPRISE:
                case MAVIC_2_ENTERPRISE_DUAL:
                case MAVIC_2:
                case MAVIC_2_ZOOM:
                case MAVIC_2_PRO:
                    batteryLifeTime = 31;
                    break;
                case MAVIC_AIR:
                    batteryLifeTime = 21;
                    break;
                case MAVIC_PRO:
                    batteryLifeTime = 27;
                    break;
                case PHANTOM_4_RTK:
                case PHANTOM_4_PRO_V2:
                case PHANTOM_4_PRO:
                case PHANTOM_4_ADVANCED:
                    batteryLifeTime = 30;
                    break;
                case PHANTOM_4:
                    batteryLifeTime = 28;
                    break;
                case PHANTOM_3_PROFESSIONAL:
                case PHANTOM_3_ADVANCED:
                    batteryLifeTime = 23;
                    break;
                case PHANTOM_3_STANDARD:
                case Phantom_3_4K:
                    batteryLifeTime = 25;
                    break;
                case INSPIRE_2:
                    batteryLifeTime = 23;
                    break;
                case INSPIRE_1_PRO:
                    batteryLifeTime = 15;
                    break;
                case INSPIRE_1:
                    batteryLifeTime = 18;
                    break;
                case MATRICE_200:
                case MATRICE_210:
                case MATRICE_210_RTK:
                    batteryLifeTime = 27;
                    break;
                case MATRICE_200_V2:
                case MATRICE_210_V2:
                case MATRICE_210_RTK_V2:
                    batteryLifeTime = 24;
                    break;
                case MATRICE_600:
                case MATRICE_600_PRO:
                    batteryLifeTime = 30;
                    break;
                case MATRICE_100:
                    batteryLifeTime = 16;
                    break;
                case A3:
                case N3:
                    batteryLifeTime = UNLIMITED_BATTERY_LIFE_TIME;
                    break;
            }
        }


        return batteryLifeTime - batteryLifeTime * MIN_MISSION_POWER / 100.00f;
    }



    public static boolean isEnoughPower(double remainPower, double missionTime) {
        if (remainPower < MIN_MISSION_POWER) {
            return false;
        }
        return missionTime <= getBatteryLifeTime() * remainPower / 100.00f;
    }


    public static Disposable getM200SeriesBattery(BatteryState.Callback callback) {
        Disposable disposable = null;
        if (AircraftManager.isM200Series() || AircraftManager.isM200V2Series()) {
            disposable = Observable
                    .interval(2, TimeUnit.SECONDS)
                    .flatMap(new Function<Long, ObservableSource<Boolean>>() {
                        @Override
                        public ObservableSource<Boolean> apply(Long aLong) throws Exception {
                            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                                @Override
                                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                                    emitter.onNext(setBatteryStateCallback(callback));
                                }
                            });
                        }
                    }).takeUntil(new Predicate<Boolean>() {
                        @Override
                        public boolean test(Boolean aBoolean) throws Exception {
                            return aBoolean;
                        }
                    }).subscribe();
        }
        return disposable;
    }

    public static boolean setBatteryStateCallback(BatteryState.Callback callback) {
        Aircraft aircraft = AircraftManager.getAircraft();
        if (aircraft != null) {
            Battery battery = aircraft.getBattery();
            if (battery != null) {
                battery.setStateCallback(callback);
                return true;
            }
        }
        return false;
    }

    private void setUpKeys() {
        KeyManager.getInstance().addListener(CELL_VOLTAGE_LEVEL_KEY, mCellVoltageLevelListener);
    }

    private void tearDownKeys() {
        KeyManager.getInstance().removeListener(mCellVoltageLevelListener);
    }

    private KeyListener mCellVoltageLevelListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof BatteryCellVoltageLevel) {
                BatteryCellVoltageLevel level = (BatteryCellVoltageLevel) newValue;
                if (level == BatteryCellVoltageLevel.LEVEL_1) {
                    playVoiceTips(VOICE_TIPS.LOW_VOLTAGE_LEVEL_1);
                } else if (level == BatteryCellVoltageLevel.LEVEL_2) {
                    playVoiceTips(VOICE_TIPS.LOW_VOLTAGE_LEVEL_2);
                } else if (level == BatteryCellVoltageLevel.LEVEL_3) {
                    playVoiceTips(VOICE_TIPS.LOW_VOLTAGE_LEVEL_3);
                }
            }
        }
    };
}
