package com.ew.autofly.module.flightrecord;

import android.text.TextUtils;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.entity.flightRecord.FlightInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.flycloud.autofly.base.net.websocket.IWebSocketListener;
import com.flycloud.autofly.base.net.websocket.WebSocketManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class FlightPlatformManager {

    private String url;



    private final long checkConnectTime = 5000;


    private final long sendDataInterval = 200;

    private long tempSendDataTime = 0;

    private Gson mGson;

    private WebSocketManager mWebSocketManager;

    private Disposable mDaemonDisposable;


    private List<IWebSocketListener> mWebSocketListenerList = new ArrayList<>();

    public void addWebSocketListener(IWebSocketListener webSocketListener) {
        if (webSocketListener != null) {
            mWebSocketListenerList.add(webSocketListener);
        }
    }

    public void removeWebSocketListener(IWebSocketListener webSocketListener) {
        if (webSocketListener != null) {
            mWebSocketListenerList.remove(webSocketListener);
        }
    }

    private FlightPlatformManager() {
    }

    public static FlightPlatformManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final FlightPlatformManager INSTANCE = new FlightPlatformManager();

        private LazyHolder() {
        }
    }

    public void init() {

        SharedConfig config = new SharedConfig(EWApplication.getInstance());
        url = config.getUploadImgUrl();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mWebSocketManager = new WebSocketManager.Builder().url(url).listener(new IWebSocketListener() {
            @Override
            public void onConnectSuccess() {
                for (IWebSocketListener iWebSocketListener : mWebSocketListenerList) {
                    iWebSocketListener.onConnectSuccess();
                }
            }

            @Override
            public void onConnectFailed() {
                for (IWebSocketListener iWebSocketListener : mWebSocketListenerList) {
                    iWebSocketListener.onConnectFailed();
                }
            }

            @Override
            public void onClose() {
                for (IWebSocketListener iWebSocketListener : mWebSocketListenerList) {
                    iWebSocketListener.onClose();
                }
            }

            @Override
            public void onMessage(String text) {
                for (IWebSocketListener iWebSocketListener : mWebSocketListenerList) {
                    iWebSocketListener.onMessage(text);
                }


            }
        }).build();
    }

    public void destroy() {
        stopService();
    }

    public void startService() {
        if (mWebSocketManager != null) {
            mWebSocketManager.connect();
            startDaemonProcess();

        }
    }

    public void stopService() {
        if (mWebSocketManager != null) {
            mWebSocketManager.close();
            stopDaemonProcess();
        }
    }

    public void disConnect() {
        if (mWebSocketManager != null) {
            mWebSocketManager.close();
        }
    }

    public void senData(FlightInfo data) {

        if (mWebSocketManager == null) {
            return;
        }

        if (data == null) {
            return;

        }

        long currentTimeMillis = System.currentTimeMillis();
        if (tempSendDataTime == 0) {
            tempSendDataTime = currentTimeMillis;
        }

        if (currentTimeMillis - tempSendDataTime > sendDataInterval) {
            tempSendDataTime = currentTimeMillis;
            if (mGson == null) {
                GsonBuilder builder = new GsonBuilder();
                builder.serializeSpecialFloatingPointValues();
                mGson = builder.create();
            }

            String sendData = mGson.toJson(data);
            mWebSocketManager.sendMessage(sendData);



        }
    }

    
    private void startDaemonProcess() {
        mDaemonDisposable = Observable.interval(0, checkConnectTime, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mWebSocketManager != null) {
                            mWebSocketManager.connect();
                        }
                    }
                });
    }

    private void stopDaemonProcess() {
        if (mDaemonDisposable != null && !mDaemonDisposable.isDisposed()) {
            mDaemonDisposable.dispose();
        }
    }

   /* private void startTestProcess() {
        Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                senData(getTestData());
            }
        });
    }*/

}
