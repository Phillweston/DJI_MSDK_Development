package com.flycloud.autofly.base.net.callback;

import com.google.gson.Gson;
import com.flycloud.autofly.base.net.entity.error.HttpNetWorkError;
import com.flycloud.autofly.base.net.entity.error.HttpServerError;
import com.flycloud.autofly.base.util.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;



public class HttpCallback<T> implements IHttpCallback<T> {

    private Gson mGson;
    private Type mType;

    public HttpCallback() {
        mGson = new Gson();
        mType = ClassUtil.getSuperclassTypeParameter(getClass());
    }


    @Override
    public void onStart() {

    }


    @Override
    public void onSuccess(T result) {

    }


    @Override
    public void onError(Exception e) {

    }


    @Override
    public void onComplete() {

    }

    @Override
    public final void onFailure(Call call, IOException e) {
        sendFailedResult(new HttpNetWorkError("网络错误", e));
    }

    @Override
    public final void onResponse(Call call, Response response) throws IOException {
        try {
            if (response.isSuccessful()) {
                final String string = response.body().string();
                if (mType == null || mType == String.class) {
                    sendSuccessResult((T) string);
                } else {
                    Object jsonOb = mGson.fromJson(string, mType);
                    sendSuccessResult((T) jsonOb);
                }
            } else {
                sendFailedResult(new HttpServerError("错误码" + response.code()));
            }

        } catch (Exception e) {
            sendFailedResult(new HttpServerError("服务器错误", e));
        }
    }

    protected void sendSuccessResult(T result) {
        onSuccess(result);
        onComplete();
    }

    protected void sendFailedResult(Exception e) {
        onError(e);
        onComplete();
    }

}
