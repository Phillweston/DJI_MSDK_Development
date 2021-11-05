package com.ew.autofly.module.setting.cache;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.key.base.FlyKey;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;


public class FlySettingConfigKey extends FlyKey {

    private static final String KEY_NAME = "YFSettingConfigKey";


    public static final String VIDEO_GRID = "VIDEO_GRID";


    public static final String SIMULATE_ALTITUDE = "SIMULATE_ALTITUDE";


    public static final String MAP_SERVICE_PROVIDER = "MAP_SERVICE_PROVIDER";


    public static final String MAP_REGION = "MAP_REGION";


    public static final String RADAR_DISPLAY_ENABLE = "RADAR_DISPLAY_ENABLE";


    public static final String RADAR_SOUND_ENABLE = "RADAR_SOUND_ENABLE";


    public static final String AI_ENABLE = "AI_ENABLE";


    public static final String LIVE_RTMP_URL = "LIVE_RTMP_URL";


    public static FlySettingConfigKey create(@NonNull String paramString) {
        String keyStr = KEY_NAME + paramString;
        FlyKey mKey = FlyKeyManager.getInstance().getKey(keyStr);
        if (mKey != null) {
            return (FlySettingConfigKey) mKey;
        } else {
            FlySettingConfigKey key;
            switch (paramString) {
                case VIDEO_GRID:
                    key = new FlySettingConfigKey(keyStr, false, Boolean.class);
                    break;
                case SIMULATE_ALTITUDE:
                    key = new FlySettingConfigKey(keyStr, 0.0f, Float.class);
                    break;
                case MAP_SERVICE_PROVIDER:
                    key = new FlySettingConfigKey(keyStr, MapServiceProvider.GOOGLE_MAP.value(), Integer.class);
                    break;
                case MAP_REGION:
                    key = new FlySettingConfigKey(keyStr, MapRegion.CHINA.value(), Integer.class);
                    break;
                case RADAR_DISPLAY_ENABLE:
                    key = new FlySettingConfigKey(keyStr, false, Boolean.class);
                    break;
                case RADAR_SOUND_ENABLE:
                    key = new FlySettingConfigKey(keyStr, true, Boolean.class);
                    break;
                case AI_ENABLE:
                    key = new FlySettingConfigKey(keyStr, false, Boolean.class);
                    break;
                case LIVE_RTMP_URL:
                    key = new FlySettingConfigKey(keyStr, "", String.class);
                    break;
                default:
                    key = new FlySettingConfigKey(keyStr);
                    break;
            }
            FlyKeyManager.getInstance().addKey(key);
            return key;
        }
    }

    public FlySettingConfigKey(String key) {
        super(key);
    }

    
    public FlySettingConfigKey(String key, @NonNull Object defaultCache, Class clazz) {
        super(key, defaultCache, clazz);
    }

    @Override
    protected String initCacheFile() {
        return KEY_NAME;
    }

    @Override
    protected boolean initSave() {
        return true;
    }

    @Override
    protected boolean initSecure() {
        return false;
    }

}
