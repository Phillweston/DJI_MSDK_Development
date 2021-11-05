package com.ew.autofly.internal.key.base;

import androidx.annotation.NonNull;

import com.ew.autofly.internal.common.error.FlyError;
import com.ew.autofly.internal.key.callback.KeyListener;
import com.ew.autofly.internal.key.callback.SetCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class FlyKeyManager {

    private Set<FlyKey> keyList;

    private final Map<FlyKey, List<KeyListener>> keyToListenerMap;

    private static class LazyHolder {
        private static final FlyKeyManager INSTANCE = new FlyKeyManager();
    }

    private FlyKeyManager() {
        keyList = new HashSet<>();
        keyToListenerMap = new ConcurrentHashMap<>();
    }

    public static FlyKeyManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Object getValue(FlyKey key) {
        return key.getCache();
    }

    public void setValue(FlyKey key, Object value) {
        setValue(key, value, null);
    }

    public void setValue(FlyKey key, Object value, SetCallback callback) {
        try {
            Object oldValue = key.getCache();
            if (key.setCache(value)) {
                handleKeyEvent(key, oldValue, value);
                if (callback != null) {
                    callback.onSuccess();
                }
            } else {
                if (callback != null) {
                    callback.onFailure(FlyError.COMMON_SET_VALUE);
                }
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(FlyError.COMMON_SET_VALUE);
            }
        }

    }

    /**
     * 注册监听器
     * <p>
     * 添加后需要主动调用 {@link #removeListener(KeyListener)} ()} 方法销毁
     *
     * @param paramKey
     * @param paramKeyListener
     */
    public void addListener(@NonNull FlyKey paramKey, KeyListener paramKeyListener) {
        if (paramKeyListener != null) {
            if (keyToListenerMap.containsKey(paramKey)) {
                List<KeyListener> keyListeners = keyToListenerMap.get(paramKey);
                if (keyListeners != null) {
                    keyListeners.add(paramKeyListener);
                }
            } else {
                List<KeyListener> keyListeners = new ArrayList<>();
                keyListeners.add(paramKeyListener);
                keyToListenerMap.put(paramKey, keyListeners);
            }
        }
    }

    /**
     * 撤销注册
     * {@link #addListener(FlyKey, KeyListener)}} ()} 注册方法
     *
     * @param paramKeyListener
     */
    public void removeListener(KeyListener paramKeyListener) {
        if (paramKeyListener != null) {
            for (List<KeyListener> keyListeners : keyToListenerMap.values()) {
                if (keyListeners.remove(paramKeyListener)) {
                    break;
                }
            }
        }
    }

    private void handleKeyEvent(FlyKey paramKey, Object oldValue, Object newValue) {
        if (keyToListenerMap.containsKey(paramKey)) {
            List<KeyListener> keyListeners = keyToListenerMap.get(paramKey);
            if (keyListeners != null && !keyListeners.isEmpty()) {
                for (KeyListener keyListener : keyListeners) {
                    keyListener.onValueChange(oldValue, newValue);
                }
            }
        }
    }

    public void addKey(FlyKey key) {
        keyList.add(key);
    }

    public FlyKey getKey(String keyStr) {

        for (FlyKey key : keyList) {
            if (key.getKey().equals(keyStr)) {
                return key;
            }
        }
        return null;
    }

    
    public void clearKeyCaches(){
        for (FlyKey flyKey : keyList) {
            flyKey.clearCache();
        }
    }
}
