package com.ew.autofly.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.ew.autofly.constant.AppConstant;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;



public class SequoiaMonitorService extends Service {

    private final String TAG = "SequoiaMonitorService";

    private WebSocketClient mClient;

    private long mCurrentTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startWebSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initSocketClient() throws URISyntaxException {

        close();

        mClient = new WebSocketClient(new URI("ws://192.168.47.1/websocket")) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {

            }

            @Override
            public void onMessage(String message) {
                Log.e(TAG, message);
                try {
                    JSONObject object = new JSONObject(message);
                    String status = (String) object.get("status");
                    if ("ping".equals(status)) {
                        mClient.send("pong");
                    } else if ("change".equals(status)) {
                        if (object.has("snapshot") && "yes".equals(object.get("snapshot"))) {
                            sendMonitorBroadcast();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                close();
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, ex.toString());
            }
        };

        connect();
    }

    public synchronized void startWebSocket() {

        try {
            initSocketClient();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        if (mClient != null) {
            try {
                mClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (mClient == null) {
            return;
        }
        try {
            mClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mClient = null;
        }
    }

    private void sendMonitorBroadcast() {

        long tempTime = System.currentTimeMillis();
        if (tempTime - mCurrentTime > 500) {
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(SequoiaMonitorService.this);
            Intent intent = new Intent(AppConstant.BROADCAST_SEQUOIA_MONITOR);
            localBroadcastManager.sendBroadcast(intent);
        }
        mCurrentTime = tempTime;
    }

    @Override
    public void onDestroy() {
        close();
        super.onDestroy();
    }
}
