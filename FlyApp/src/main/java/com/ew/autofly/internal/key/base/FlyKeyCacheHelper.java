package com.ew.autofly.internal.key.base;

import com.ew.autofly.application.EWApplication;
import com.flycloud.autofly.base.util.SharedPreferencesUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FlyKeyCacheHelper {

    private final Map<String, SharedPreferencesUtil> configMap;

    private static class LazyHolder {
        private static final FlyKeyCacheHelper INSTANCE = new FlyKeyCacheHelper();
    }

    public static FlyKeyCacheHelper getInstance() {
        return LazyHolder.INSTANCE;
    }

    private FlyKeyCacheHelper() {
        configMap = new ConcurrentHashMap<>();
    }

    public void createConfig(String fileName) {
        createConfig(fileName, false);
    }

    public void createConfig(String fileName, boolean isSecure) {
        if (!configMap.containsKey(fileName)) {
            configMap.put(fileName, new SharedPreferencesUtil(EWApplication.getInstance(), fileName, isSecure));
        }
    }

    public SharedPreferencesUtil getConfig(String fileName) {
        return configMap.get(fileName);
    }

    public Object get(String fileName, String key, Object defaultObject, Class clazz) {
        try {
            SharedPreferencesUtil sp = getConfig(fileName);
            if (String.class.equals(clazz)) {
                return sp.getString(key, (String) defaultObject);
            } else if (Integer.class.equals(clazz)) {
                return sp.getInt(key, (Integer) defaultObject);
            } else if (Boolean.class.equals(clazz)) {
                return sp.getBoolean(key, (Boolean) defaultObject);
            } else if (Float.class.equals(clazz)) {
                return sp.getFloat(key, (Float) defaultObject);
            } else if (Long.class.equals(clazz)) {
                return sp.getLong(key, (Long) defaultObject);
            } else if (Double.class.equals(clazz)) {
                return sp.getDouble(key, (Double) defaultObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean set(String fileName, String key, Object value) {
        try {
            SharedPreferencesUtil sp = getConfig(fileName);
            if (value instanceof String) {
                sp.putString(key, (String) value);
            } else if (value instanceof Integer) {
                sp.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                sp.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                sp.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                sp.putLong(key, (Long) value);
            } else if (value instanceof Double) {
                sp.putDouble(key, (Double) value);
            } else {

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

