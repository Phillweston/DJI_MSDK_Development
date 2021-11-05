package com.ew.autofly.model;

import java.util.ArrayList;
import java.util.List;

import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;


public class ConnectManager {

    private List<OnProductConnectListener> mConnectListeners = new ArrayList<>();

    public static ConnectManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private ConnectManager() {
    }

    private static class LazyHolder {
        private static final ConnectManager INSTANCE = new ConnectManager();

        private LazyHolder() {
        }
    }

    public void register(OnProductConnectListener listener) {
        if (listener != null && !mConnectListeners.contains(listener)) {
            mConnectListeners.add(listener);
        }
    }

    public void unRegister(OnProductConnectListener listener) {
        try {
            if (listener != null && !mConnectListeners.isEmpty()) {
                mConnectListeners.remove(listener);
            }
        } catch (Exception e) {
        }
    }

    public void onProductDisconnect() {
        for (OnProductConnectListener connectListener : mConnectListeners) {
            connectListener.onProductDisconnect();
        }
    }

    public void onProductConnect(BaseProduct baseProduct) {
        for (OnProductConnectListener connectListener : mConnectListeners) {
            connectListener.onProductConnect(baseProduct);
        }
    }

    public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent newComponent,
                                  boolean isConnected) {
        for (OnProductConnectListener connectListener : mConnectListeners) {
            connectListener.onProductComponentChange(componentKey, newComponent, isConnected);
        }
    }

    public interface OnProductConnectListener {

        void onProductDisconnect();

        void onProductConnect(BaseProduct baseProduct);

        void onProductComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent newComponent,
                                      boolean isConnected);
    }
}
