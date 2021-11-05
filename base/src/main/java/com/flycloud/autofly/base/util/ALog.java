package com.flycloud.autofly.base.util;

import android.util.Log;


public class ALog {

    private static boolean isLogEnable;

    public static void  init(Builder builder) {
        isLogEnable = builder.isLogEnable;
    }


    public static void d(String tag, String msg) {
        if (!isLogEnable) {
            return;
        }
        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (!isLogEnable) {
            return;
        }
        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (!isLogEnable) {
            return;
        }
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!isLogEnable) {
            return;
        }
        Log.e(tag, msg);
    }


    public static final class Builder {
        private boolean isLogEnable;

        public Builder() {
        }

        public Builder isLogEnable(boolean val) {
            isLogEnable = val;
            return this;
        }
    }
}
