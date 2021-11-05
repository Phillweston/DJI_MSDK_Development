package com.flycloud.autofly.base.net;

import com.flycloud.autofly.base.net.callback.HttpCallback;
import com.flycloud.autofly.base.net.callback.IHttpCallback;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



public class HttpHelper {


    public static final String METHOD_POST_FROM = "POST_FROM";
    public static final String METHOD_POST_JSON = "POST_JSON";
    public static final String METHOD_POST_LIST_FILE = "POST_LIST_FILE";

    private String url;
    private String method;

    private Map<String, String> formParams;
    Map<String, File> formFileParams;
    private String json;

    private OkHttpClient.Builder httpClientBuilder;
    private Request.Builder requestBuilder;

    private HttpHelper() {
    }

    private HttpHelper(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.json = builder.json;
        this.formParams = builder.formParams;
        this.formFileParams = builder.formFileParams;
        this.httpClientBuilder = builder.httpClientBuilder;
        this.requestBuilder = builder.requestBuilder;
    }

    public void call(IHttpCallback callback) {

        try{

            Request request = null;

            if (this.httpClientBuilder == null) {
                this.httpClientBuilder = new OkHttpClient.Builder();
            }

            switch (this.method) {
                case METHOD_POST_FROM:
                    if (this.formParams != null) {
                        request = buildPostRequest(this.url, this.formParams);
                    } else {
                        throw new IllegalStateException("post formParams == null");
                    }
                    break;
                case METHOD_POST_JSON:
                    if (this.json != null) {
                        request = buildPostRequestJson(this.url, this.json);
                    } else {
                        throw new IllegalStateException("post json == null");
                    }
                    break;
                case METHOD_POST_LIST_FILE:
                    if (this.formFileParams != null) {
                        request = buildPostRequestFile(this.url, this.formParams, this.formFileParams);
                    } else {
                        throw new IllegalStateException("post formFileParams == null");
                    }
                    break;
            }

            if (request != null) {
                if (callback == null) {
                    callback = new HttpCallback();
                }
                callback.onStart();
                OkHttpClient httpClient = this.httpClientBuilder.build();
                httpClient.newCall(request).enqueue(callback);
            } else {
                throw new IllegalStateException("post request == null");
            }
        }catch (Exception e){
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    private Request buildPostRequest(String url, Map<String, String> mapParams) {
        FormBody.Builder builder = new FormBody.Builder();
        if (mapParams != null) {
            for (String key : mapParams.keySet()) {
                builder.add(key, mapParams.get(key));
            }
        }
        return buildPostRequest(url, builder.build());
    }

    private Request buildPostRequestJson(String url, String json) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        return buildPostRequest(url, body);
    }

    private Request buildPostRequest(String url, RequestBody body) {
        if (this.requestBuilder == null) {
            this.requestBuilder = new Request.Builder();
        }
        return requestBuilder.url(url)
                .post(body)
                .build();
    }

    /**
     * @param url
     * @param formParams 表单数据
     * @param fileParams 文件数据
     * @return
     */
    private Request buildPostRequestFile(String url, Map<String, String> formParams, Map<String, File> fileParams) {

        MultipartBody.Builder builder = new MultipartBody.Builder();

        if (fileParams != null) {
            for (String key : fileParams.keySet()) {
                File file = fileParams.get(key);
                RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                builder.addFormDataPart(key, file.getName(), fileBody);
            }
        }
        if (formParams != null) {
            for (String key : formParams.keySet()) {
                String param = formParams.get(key);
                builder.addFormDataPart(key, param);
            }
        }

        MultipartBody body = builder.setType(MultipartBody.FORM).build();

        return buildPostRequest(url, body);
    }

    public static class Builder {

        String url;
        String method;
        Map<String, String> formParams;
        Map<String, File> formFileParams;
        String json;

        OkHttpClient.Builder httpClientBuilder;
        Request.Builder requestBuilder;

        public Builder() {
            method = METHOD_POST_FROM;
        }

        public HttpHelper build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new HttpHelper(this);
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setFormParams(Map<String, String> formParams) {
            this.formParams = formParams;
            return this;
        }

        public Builder setFormFileParams(Map<String, File> formFileParams) {
            this.formFileParams = formFileParams;
            return this;
        }

        public Builder setJson(String json) {
            this.json = json;
            return this;
        }

        public Builder setHttpClientBuilder(OkHttpClient.Builder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
            return this;
        }

        public Builder setRequestBuilder(Request.Builder requestBuilder) {
            this.requestBuilder = requestBuilder;
            return this;
        }
    }
}
