package com.ew.autofly.module.setting.cache;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.key.base.FlyKey;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.widgets.dji.DjiFPVWidget;

import dji.common.gimbal.GimbalMode;


public class FlySettingKey extends FlyKey {

    private static final String KEY_NAME = "YFSettingKey";

    

    public static final String VIDEO_SOURCE = "VIDEO_SOURCE";

    public static final String GIMBAL_MODE = "GIMBAL_MODE";

    public static FlySettingKey create(@NonNull String paramString) {
        String keyStr = KEY_NAME + paramString;
        FlyKey mKey = FlyKeyManager.getInstance().getKey(keyStr);
        if (mKey != null) {
            return (FlySettingKey) mKey;
        } else {
            FlySettingKey key;
            switch (paramString) {
                case VIDEO_SOURCE:
                    key = new FlySettingKey(keyStr, DjiFPVWidget.VideoSource.AUTO, DjiFPVWidget.VideoSource.class);
                    break;
                case GIMBAL_MODE:
                    key = new FlySettingKey(keyStr, GimbalMode.UNKNOWN, GimbalMode.class);
                    break;
                default:
                    key = new FlySettingKey(keyStr);
                    break;
            }
            FlyKeyManager.getInstance().addKey(key);
            return key;
        }
    }

    public FlySettingKey(String key) {
        super(key);
    }


    public FlySettingKey(String key, Object defaultCache, Class clazz) {
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
