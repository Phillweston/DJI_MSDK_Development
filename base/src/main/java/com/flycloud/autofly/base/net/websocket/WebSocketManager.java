package com.flycloud.autofly.base.net.websocket;

import android.os.Handler;

import com.flycloud.autofly.base.util.ALog;
import com.flycloud.autofly.base.util.StringUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketManager {

    private String url;

    private int reconnectCount;
    private int reconnectIntervalTime;


    private long heartBeatIntervalTime;

    private String heartBeatContent;

    private IWebSocketListener listener;

    private OkHttpClient client;
    private Request request;


    private WebSocket mWebSocket;


    private boolean isConnect = false;
    private int connectNum = 0;

    private long sendTime = 0L;



    private Handler mHandler = new Handler();

    private WebSocketManager(Builder builder) {
        url = builder.url;
        reconnectCount = builder.reconnectCount;
        reconnectIntervalTime = builder.reconnectIntervalTime;
        heartBeatContent = builder.heartBeatContent;
        listener = builder.listener;
        init(builder);
    }

    private void init(Builder builder) {
        buildParams(builder);
        client = new OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        request = new Request.Builder().url(url).build();
    }

    private void buildParams(Builder builder) {
        reconnectCount = builder.reconnectCount;
        reconnectIntervalTime = builder.reconnectIntervalTime;
        heartBeatIntervalTime = builder.heartBeatIntervalTime;
        url = builder.url;
        listener = builder.listener;
    }

    
    public void connect() {
        if (isConnect()) {
            ALog.i("WebSocketManager", "WebSocket 已经连接！");
            return;
        }
        client.newWebSocket(request, createListener());
    }

    
    public void reconnect() {
        if (connectNum <= reconnectCount) {
            try {
                Thread.sleep(reconnectIntervalTime);
                connect();
                connectNum++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            ALog.i("WebSocketManager", "reconnect over " + reconnectCount + ",please check url or network");
        }
    }

    
    public boolean isConnect() {
        return mWebSocket != null && isConnect;
    }

    /**
     * 发送消息
     *
     * @param text 字符串
     * @return boolean
     */
    public boolean sendMessage(String text) {
        if (!isConnect()) return false;
        return mWebSocket.send(text);
    }


    
    public void close() {
        if (isConnect()) {
            mWebSocket.cancel();
            mWebSocket.close(1001, "客户端主动关闭连接");
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private WebSocketListener createListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                ALog.i("WebSocketManager", "WebSocket 打开:" + response.toString());
                mWebSocket = webSocket;
                isConnect = response.code() == 101;
                if (!isConnect) {
                    reconnect();
                } else {
                    ALog.i("WebSocketManager", "WebSocket 连接成功");
                    if (listener != null) {
                        listener.onConnectSuccess();
                    }

                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                if (listener != null) {
                    listener.onMessage(text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                if (listener != null) {
                    listener.onMessage(bytes.base64());
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (listener != null) {
                    listener.onClose();
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                mWebSocket = null;
                isConnect = false;
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (listener != null) {
                    listener.onClose();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                if (response != null) {
                    ALog.i("WebSocketManager", "WebSocket 连接失败：" + response.message());
                }
                ALog.i("WebSocketManager", "WebSocket 连接失败异常原因：" + t.getMessage());
                isConnect = false;
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (listener != null) {
                    listener.onConnectFailed();
                }
                if (!StringUtils.isEmptyOrNull(t.getMessage()) && !t.getMessage().equals("Socket closed")) {
                    reconnect();
                }

            }
        };
    }


    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= heartBeatIntervalTime) {
                sendTime = System.currentTimeMillis();
                boolean isSend = sendMessage(getWSHeart());
                ALog.i("WebSocketManager", "心跳是否发送成功" + isSend);
            }
            mHandler.postDelayed(this, heartBeatIntervalTime);
        }
    };

    private String getWSHeart() {
        return heartBeatContent == null ? "" : heartBeatContent;
    }

    public static final class Builder {
        private String url;
        private int reconnectCount = 5;
        private int reconnectIntervalTime = 5000;
        private String heartBeatContent;
        private long heartBeatIntervalTime = 10 * 1000;
        private IWebSocketListener listener;

        public Builder() {
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder listener(IWebSocketListener val) {
            listener = val;
            return this;
        }

        public Builder reconnectCount(int val) {
            reconnectCount = val;
            return this;
        }

        public Builder reconnectIntervalTime(int val) {
            reconnectIntervalTime = val;
            return this;
        }

        public Builder heartBeatContent(String val) {
            heartBeatContent = val;
            return this;
        }

        public Builder heartBeatIntervalTime(long val) {
            heartBeatIntervalTime = val;
            return this;
        }

        public WebSocketManager build() {
            return new WebSocketManager(this);
        }
    }
}
