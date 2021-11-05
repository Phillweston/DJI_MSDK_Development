package com.flycloud.autofly.map.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TileImageDownloadUtils {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    public static OkHttpClient getInstance() {
        return OK_HTTP_CLIENT;
    }

    public static byte[] getImageFromURL2(String urlPath) {

        OkHttpClient client = getInstance();

        Request request = new Request.Builder()
                .url(urlPath)
                .build();
        try {

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {

                ResponseBody body = response.body();

                if (body != null) {

                    return body.bytes();
                }

            }
        } catch (Exception e) {
         
        }
        return null;
    }
}
