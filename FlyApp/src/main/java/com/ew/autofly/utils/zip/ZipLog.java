package com.ew.autofly.utils.zip;

import android.util.Log;

import com.ew.autofly.BuildConfig;

/**
 *
 *
 */
final class ZipLog {
    private static final String TAG = "ZipLog";

    private static boolean DEBUG = false;

    static void config(boolean debug) {
        DEBUG = debug;
    }

    static void debug(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }
}
