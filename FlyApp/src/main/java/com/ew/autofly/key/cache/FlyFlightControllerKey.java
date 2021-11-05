package com.ew.autofly.key.cache;

import androidx.annotation.NonNull;

import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.internal.key.base.FlyKey;
import com.ew.autofly.internal.key.base.FlyKeyManager;


public class FlyFlightControllerKey extends FlyKey {

    private static final String KEY_NAME = "YFFlightControllerKey";

    public static final String AIRCRAFT_LOCATION = "AIRCRAFT_LOCATION";


    public static FlyFlightControllerKey create(@NonNull String paramString) {
        String keyStr = KEY_NAME + paramString;
        FlyKey mKey = FlyKeyManager.getInstance().getKey(keyStr);
        if (mKey != null) {
            return (FlyFlightControllerKey) mKey;
        } else {
            FlyFlightControllerKey key;
            switch (paramString) {
                case AIRCRAFT_LOCATION:
                    key = new FlyFlightControllerKey(keyStr, null, LocationCoordinate.class);
                    break;
                default:
                    key = new FlyFlightControllerKey(keyStr);
                    break;
            }
            FlyKeyManager.getInstance().addKey(key);
            return key;
        }
    }

    public FlyFlightControllerKey(String key) {
        super(key);
    }


    public FlyFlightControllerKey(String key, Object defaultCache, Class clazz) {
        super(key, defaultCache, clazz);
    }

    @Override
    protected String initCacheFile() {
        return KEY_NAME;
    }

    @Override
    protected boolean initSave() {
        return false;
    }

    @Override
    protected boolean initSecure() {
        return false;
    }

}
