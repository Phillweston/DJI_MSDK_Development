package com.flycloud.autofly.base.net.callback;

import okhttp3.Callback;



public interface IHttpCallback<T> extends Callback {

    void onStart();

    void onSuccess(T result);

    void onError(Exception e);

    void onComplete();
}
