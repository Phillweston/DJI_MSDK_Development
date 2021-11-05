package com.ew.autofly.key.config;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.key.base.FlyKey;
import com.ew.autofly.internal.key.base.FlyKeyManager;


public class NTRIPConfigKey extends FlyKey {

    private static final String KEY_NAME = "NTRIPConfigKey";

    public static final String RTK_NETWORK_SERVICE_ADDRESS = "RTK_NETWORK_SERVICE_ADDRESS";
    public static final String RTK_NETWORK_SERVICE_PORT = "RTK_NETWORK_SERVICE_PORT";
    public static final String RTK_NETWORK_SERVICE_MOUNTPOINT = "RTK_NETWORK_SERVICE_MOUNTPOINT";
    public static final String RTK_NETWORK_SERVICE_ACCOUNT = "RTK_NETWORK_SERVICE_ACCOUNT";
    public static final String RTK_NETWORK_SERVICE_PASSWORD = "RTK_NETWORK_SERVICE_PASSWORD";

    public NTRIPConfigKey(String key) {
        super(key);
    }

    public NTRIPConfigKey(String key, Object defaultCache, Class clazz) {
        super(key, defaultCache, clazz);
    }

    public static NTRIPConfigKey create(@NonNull String paramString) {
        String keyStr = KEY_NAME + paramString;
        FlyKey mKey = FlyKeyManager.getInstance().getKey(keyStr);
        if (mKey != null) {
            return (NTRIPConfigKey) mKey;
        } else {
            NTRIPConfigKey key;
            switch (paramString) {
                case RTK_NETWORK_SERVICE_ADDRESS:
                    key = new NTRIPConfigKey(keyStr, "", String.class);
                    break;
                case RTK_NETWORK_SERVICE_PORT:
                    key = new NTRIPConfigKey(keyStr, "", String.class);
                    break;
                case RTK_NETWORK_SERVICE_MOUNTPOINT:
                    key = new NTRIPConfigKey(keyStr, "", String.class);
                    break;
                case RTK_NETWORK_SERVICE_ACCOUNT:
                    key = new NTRIPConfigKey(keyStr, "", String.class);
                    break;
                case RTK_NETWORK_SERVICE_PASSWORD:
                    key = new NTRIPConfigKey(keyStr, "", String.class);
                    break;
                default:
                    key = new NTRIPConfigKey(keyStr);
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
        return true;
    }
}
