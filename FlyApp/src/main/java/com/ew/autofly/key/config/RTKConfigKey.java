package com.ew.autofly.key.config;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.key.base.FlyKey;
import com.ew.autofly.internal.key.base.FlyKeyManager;


public class RTKConfigKey extends FlyKey {

    private static final String KEY_NAME = "RTKConfigKey";

    public static final String RTK_REFERENCE_STATION_SOURCE = "RTK_REFERENCE_STATION_SOURCE";

    public static final String RTK_COORDINATE_SYSTEM = "RTK_COORDINATE_SYSTEM";

    public RTKConfigKey(String key) {
        super(key);
    }

    public RTKConfigKey(String key, Object defaultCache, Class clazz) {
        super(key, defaultCache, clazz);
    }

    public static RTKConfigKey create(@NonNull String paramString) {
        String keyStr = KEY_NAME + paramString;
        FlyKey mKey = FlyKeyManager.getInstance().getKey(keyStr);
        if (mKey != null) {
            return (RTKConfigKey) mKey;
        } else {
            RTKConfigKey key;
            switch (paramString) {
                case RTK_REFERENCE_STATION_SOURCE:
                    key = new RTKConfigKey(keyStr, 0, Integer.class);
                    break;
                case RTK_COORDINATE_SYSTEM:
                    key = new RTKConfigKey(keyStr, 0, Integer.class);
                    break;
                default:
                    key = new RTKConfigKey(keyStr);
                    break;
            }
            FlyKeyManager.getInstance().addKey(key);
            return key;
        }
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
