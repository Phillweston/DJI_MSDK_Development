package com.ew.autofly.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CrashLogCatchUtils implements UncaughtExceptionHandler {

    private static final String TAG = "CrashLogCatchUtils";
    private UncaughtExceptionHandler mDefaultHandler;
    private static CrashLogCatchUtils mInstance;
    private Context mContext;
    private Map<String, String> mLogInfo = new HashMap<String, String>();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
    private String logFilePath = "";

    public static CrashLogCatchUtils getInstance() {
        if (null == mInstance) {
            mInstance = new CrashLogCatchUtils();
        }
        return mInstance;
    }

    public void init(Context paramContext, String logFilePath) {
        this.mContext = paramContext;
        this.logFilePath = logFilePath;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
        if ((!handleException(paramThrowable))
                && (this.mDefaultHandler != null)) {
            this.mDefaultHandler.uncaughtException(paramThread, paramThrowable);
        } else {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    public boolean handleException(Throwable paramThrowable) {
        if (paramThrowable == null)
            return false;
        new Thread() {
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();

        getDeviceInfo(this.mContext);
        saveCrashLogToFile(paramThrowable);
        return true;
    }

    public void getDeviceInfo(Context paramContext) {
        try {
            PackageManager mPackageManager = paramContext.getPackageManager();
            PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
                    paramContext.getPackageName(), 1);

            if (mPackageInfo != null) {
                String versionName = mPackageInfo.versionName == null ? "null"
                        : mPackageInfo.versionName;

                String versionCode = mPackageInfo.versionCode + "";
                this.mLogInfo.put("versionName", versionName);
                this.mLogInfo.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] mFields = Build.class.getDeclaredFields();
        for (Field field : mFields)
            try {
                field.setAccessible(true);
                this.mLogInfo.put(field.getName(), field.get("").toString());
                Log.d("CrashLogCatchUtils",
                        field.getName() + ":" + field.get(""));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
    }

    private String saveCrashLogToFile(Throwable paramThrowable) {
        StringBuffer mStringBuffer = new StringBuffer();
        for (Entry entry : this.mLogInfo.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }
        Writer mWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mWriter);
        paramThrowable.printStackTrace(mPrintWriter);
        paramThrowable.printStackTrace();
        Throwable mThrowable = paramThrowable.getCause();
        while (mThrowable != null) {
            mThrowable.printStackTrace(mPrintWriter);
            mPrintWriter.append("\r\n");
            mThrowable = mThrowable.getCause();
        }
        mPrintWriter.close();
        String mResult = mWriter.toString();
        mStringBuffer.append(mResult);
        String mTime = this.mSimpleDateFormat.format(new Date());
        String mFileName = "CrashLog-" + mTime + ".log";
        if (Environment.getExternalStorageState().equals("mounted")) {
            try {
                if (null != this.mContext) {
                    File mDirectory = new File(this.logFilePath);
                    if (!mDirectory.exists())
                        mDirectory.mkdirs();
                    FileOutputStream mFileOutputStream = new FileOutputStream(
                            mDirectory + "/" + mFileName);

                    mFileOutputStream
                            .write(mStringBuffer.toString().getBytes());
                    mFileOutputStream.close();
                    return mFileName;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
