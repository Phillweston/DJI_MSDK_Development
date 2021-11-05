
package com.flycloud.autofly.base.net.callback;

import android.os.Handler;
import android.os.Looper;





public abstract class HttpCallbackInUI<T> extends HttpCallback<T> {

    private Handler mUIThreadHandler = null;

    public HttpCallbackInUI() {
        mUIThreadHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void sendSuccessResult(final T result) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                onSuccess(result);
                onComplete();
            }
        });
    }

    @Override
    protected void sendFailedResult(final Exception e) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                onError(e);
                onComplete();
            }
        });
    }

}

