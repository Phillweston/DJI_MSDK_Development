package com.ew.autofly.net;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import dji.thirdparty.okhttp3.Call;
import dji.thirdparty.okhttp3.Callback;
import dji.thirdparty.okhttp3.FormBody;
import dji.thirdparty.okhttp3.MediaType;
import dji.thirdparty.okhttp3.OkHttpClient;
import dji.thirdparty.okhttp3.Request;
import dji.thirdparty.okhttp3.RequestBody;
import dji.thirdparty.okhttp3.Response;



public class OkHttpUtil {

    private static OkHttpUtil sSingleton;

    private OkHttpClient mOkHttpClient;
    protected Request.Builder mRequestBuilder;
    private Handler mUIThreadHandler;
    private Gson mGson;

    public OkHttpUtil(OkHttpClient.Builder builder) {
        if (builder == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = builder.build();
        }
        mUIThreadHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public OkHttpUtil(OkHttpClient.Builder ob, Request.Builder rb) {
        if (ob == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = ob.build();
        }
        mRequestBuilder = rb;
        mUIThreadHandler = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public static OkHttpUtil getInstance() {
        if (sSingleton == null) {
            synchronized (OkHttpUtil.class) {
                if (sSingleton == null) {
                    sSingleton = new OkHttpUtil(null);
                }
            }
        }
        return sSingleton;
    }

    public static OkHttpUtil newInstance(OkHttpClient.Builder builder) {
        return new OkHttpUtil(builder, null);
    }

    public static OkHttpUtil newInstance(OkHttpClient.Builder ob, Request.Builder rb) {
        return new OkHttpUtil(ob, rb);
    }


    public static void doGet(String url, Callback callback) {
        getInstance()._doGetAsync(url, callback);
    }


    public static void doGetAsyncUI(String url, HttpCallBack httpCallBack) {
        getInstance()._doGetAsyncUI(url, httpCallBack);
    }

    /**
     * Post请求发送键值对数据（子线程）
     *
     * @param url
     * @param mapParams
     * @param callback
     */
    public static void doPost(String url, Map<String, String> mapParams, Callback callback) {
        getInstance()._doPostAsync(url, mapParams, callback);
    }

    /**
     * Post请求发送Json数据（子线程）
     *
     * @param url
     * @param json
     * @param callback
     */
    public static void doPostJson(String url, String json, Callback callback) {
        getInstance()._doPostAsync(url, json, callback);
    }

    /**
     * Post异步请求发送键值对数据（UI主线程）
     *
     * @param url
     * @param mapParams
     * @param httpCallBack
     */
    public static void doPostAsyncUI(String url, Map<String, String> mapParams, HttpCallBack httpCallBack) {
        getInstance()._doPostAsyncUI(url, mapParams, httpCallBack);
    }

    /**
     * Post异步请求发送Json数据（UI主线程)
     *
     * @param url
     * @param json
     * @param httpCallBack
     */
    public static void doPostJsonAsyncUI(String url, String json, HttpCallBack httpCallBack) {
        getInstance()._doPostAsyncUI(url, json, httpCallBack);
    }


    public void _doGetAsync(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        callAsync(callback, request);
    }


    public void _doGetAsyncUI(String url, HttpCallBack httpCallBack) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        callAsyncUI(httpCallBack, request);
    }


    public void _doPostAsync(String url, Map<String, String> mapParams, Callback callback) {
        Request request = buildPostRequest(url, mapParams);
        callAsync(callback, request);
    }

    public void _doPostAsync(String url, String json, Callback callback) {
        Request request = buildPostRequestJson(url, json);
        callAsync(callback, request);
    }


    public void _doPostAsyncUI(String url, Map<String, String> mapParams, HttpCallBack httpCallBack) {
        Request request = buildPostRequest(url, mapParams);
        callAsyncUI(httpCallBack, request);
    }

    public void _doPostAsyncUI(String url, String json, HttpCallBack httpCallBack) {
        Request request = buildPostRequestJson(url, json);
        callAsyncUI(httpCallBack, request);
    }


    private Request buildPostRequest(String url, Map<String, String> mapParams) {
        FormBody.Builder builder = new FormBody.Builder();
        if (mapParams != null)
            for (String key : mapParams.keySet()) {
                builder.add(key, mapParams.get(key));
            }

        if (mRequestBuilder == null) {
            mRequestBuilder = new Request.Builder();
        }

        return mRequestBuilder.url(url)
                .post(builder.build())
                .build();
    }

    private Request buildPostRequestJson(String url, String json) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        if (mRequestBuilder == null) {
            mRequestBuilder = new Request.Builder();
        }

        return mRequestBuilder.url(url)
                .post(body)
                .build();
    }

    private void callAsync(Callback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    private void callAsyncUI(final HttpCallBack httpCallBack, Request request) {

        sendStartResult(httpCallBack);

        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedResult(e, httpCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        final String string = response.body().string();
                        if (httpCallBack.mType == null || httpCallBack.mType == String.class) {
                            sendSuccessResult(string, httpCallBack);
                        } else {
                            Object o = mGson.fromJson(string, httpCallBack.mType);
                            sendSuccessResult(o, httpCallBack);
                        }
                    } else {
                        sendFailedResult(new NetworkErrorException("错误码" + response.code()), httpCallBack);
                    }

                } catch (Exception e) {
                    sendFailedResult(e, httpCallBack);
                }

            }
        });
    }

    private void sendStartResult(final HttpCallBack httpCallBack) {
        if (httpCallBack != null) {
            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onStart();
                }
            });
        }
    }

    private void sendSuccessResult(final Object object, final HttpCallBack httpCallBack) {
        if (httpCallBack != null) {
            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onSuccess(object);
                }
            });
        }
    }

    private void sendFailedResult(final Exception e, final HttpCallBack httpCallBack) {
        if (httpCallBack != null) {
            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    httpCallBack.onError(e);
                }
            });
        }
    }

    public static abstract class HttpCallBack<T> {

        Type mType;

        public HttpCallBack() {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class) {
                return null;

            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public void onStart() {
        }

        public abstract void onSuccess(T result);

        public abstract void onError(Exception e);
    }

    public static abstract class HttpCallBackWithDialog<T> extends HttpCallBack<T> {

        private BaseProgressDialog waitingDlg;
        private Context context;
        private String message;

        public HttpCallBackWithDialog() {
            this(null, null);
        }

        public HttpCallBackWithDialog(Context context) {
            this(context, null);
        }

        public HttpCallBackWithDialog(Context context, String message) {
            this.context = context;
            this.message = message;
        }

        @Override
        public void onStart() {
            super.onStart();
            if (context != null) {
                waitingDlg = new BaseProgressDialog(context, message);
                waitingDlg.show();
            }
        }

        public void onSuccess(T result) {
            if (waitingDlg != null) {
                waitingDlg.dismiss();
            }
        }

        public void onError(Exception e) {
            if (waitingDlg != null) {
                waitingDlg.dismiss();
            }
        }
    }

}
