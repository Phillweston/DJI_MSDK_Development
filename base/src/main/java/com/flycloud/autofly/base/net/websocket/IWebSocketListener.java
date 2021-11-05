package com.flycloud.autofly.base.net.websocket;


public interface IWebSocketListener {

    void onConnectSuccess();

    void onConnectFailed();

    void onClose();

    void onMessage(String text);
}
