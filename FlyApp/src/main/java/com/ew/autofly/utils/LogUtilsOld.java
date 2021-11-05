package com.ew.autofly.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ew.autofly.constant.AppConstant;


public class LogUtilsOld {
    private static LogUtilsOld mInstance = null;
    private Context mContext = null;
    private String logPath = "";
    private static String mTag = "LIGHT";
    private DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(
            "HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public LogUtilsOld setTag(String tag) {
        mTag = tag;
        return mInstance;
    }

    private LogUtilsOld(Context context) {
        mContext = context;
        logPath = IOUtils.getRootStoragePath(mContext) + AppConstant.DIR_LOG + File.separator;
        String path1 = logPath + "info";
        String path2 = logPath + "error";
        File file1 = new File(path1);
        File file2 = new File(path2);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        if (!file2.exists()) {
            file2.mkdirs();
        }
    }

    public static LogUtilsOld getInstance(Context context) {
        mTag = context.getClass().getName();
        if (mInstance == null) {
            synchronized (LogUtilsOld.class) {
                if (mInstance == null) {
                    mInstance = new LogUtilsOld(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public synchronized void write(byte[] contents, int isError) throws Exception {
        String mDay = DAY_FORMAT.format(new Date());
        String path = logPath + (isError == 1 ? "error" : "info") + File.separator + mDay + ".txt";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fouts = new FileOutputStream(file, true);
        fouts.write(contents);
        fouts.close();
    }

    public synchronized void outDebugWindows(String logStr) {
        if (!AppConstant.DEBUG || StringUtils.isEmptyOrNull(logStr)) {
            return;
        }
        DebugWindow.getInstance().addDebugData(logStr);
    }

    public synchronized void i(String logStr) {
        if (!AppConstant.DEBUG || StringUtils.isEmptyOrNull(logStr)) {
            return;
        }
        Log.i(mTag, logStr);
        try {
            String timeStamp = TIMESTAMP_FORMAT.format(new Date());
            String content = timeStamp + " [" + mTag + "] " + logStr
                    + System.getProperty("line.separator");
            write(content.getBytes("UTF-8"), 0);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public synchronized void e(String logStr) {
        if (StringUtils.isEmptyOrNull(logStr)) {
            logStr = "";
        }
        Log.e(mTag, logStr);
        try {
            String timeStamp = TIMESTAMP_FORMAT.format(new Date());
            String content = timeStamp + logStr
                    + System.getProperty("line.separator");
            write(content.getBytes("UTF-8"), 1);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void destroy() {
        mInstance = null;
    }
}
