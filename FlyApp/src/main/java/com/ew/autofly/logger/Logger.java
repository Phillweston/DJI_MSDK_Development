package com.ew.autofly.logger;

import android.content.Context;
import android.util.Log;

import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.config.PropConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The class <code>Logger</code>日志记录工具类
 *
 * @version 1.0
 */
public class Logger {
    private static String TAG = "";
    private static Logger logger = new Logger();

    public static final int VERBOSE = 1;

    public static final int DEBUG = 2;

    public static final int INFO = 3;

    public static final int WARN = 4;

    public static final int ERROR = 5;

    public static final int NONE = 6;

    private static Context context = null;

    private static boolean append = false;

    private static PlaceMode placeMode = PlaceMode.MODE_SDCARD;

    private static String path = null;

    private static File logFile = null;

    private static long max = 0;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    private boolean timeSwitch = true;

    private static FileOutputStream fos = null;

    private static OutputStreamWriter writer = null;
    /** 日志级别VERBOSE,设置此值将打印全部日志 */

    private static int level = 1;
    public static boolean logCat = true;

    /**
     * The class <code>PlaceMode</code>文件的存储介质
     *
     * @author likai
     * @version 1.0
     */
    public enum PlaceMode {

        MODE_SDCARD,

        MODE_MEMORY
    }

    public Logger() {

    }

    /**
     * 获取日志打印工具
     *
     * @param context   上下文
     * @param path      文件的存储地址（包含文件名），如果在placeMode为MODE_MEMORY时,则此值只需要名称即可
     * @param placeMode 文件存储位置
     * @param append    是否追加
     * @param max       一个文件的最大值，单位byte；当为0的时候表示无限制,
     * @param logLevel  日志输出级别
     * @return 实例
     */
    public static void initLogger(Context context) {
        try {
            PropConfig.getInstance().init(context);
            boolean logCat = PropConfig.getInstance().getLogcat();
            String path = AppConstant.LOG_PATH + File.separator
                    + context.getPackageName() + ".txt";
            logCat = true;
            if (!logCat) {
                return;
            }
            Logger.context = context;
            TAG = context.getPackageName();
            Logger.placeMode = PlaceMode.MODE_SDCARD;
            Logger.path = path;
            Logger.append = true;
            Logger.max = 2 * 1024 * 1024;
            Logger.level = Logger.VERBOSE;
            Logger.logCat = logCat;
            try {
                init();
            } catch (IOException e) {
                throw e;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private static void init() throws IOException {
        if (Logger.placeMode != PlaceMode.MODE_MEMORY) {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                logFile = new File(Logger.path);
                if (!logFile.exists()) {
                    if (logFile.getParentFile().exists()) {
                        logFile.getParentFile().delete();
                    }
                    logFile.getParentFile().mkdirs();
                    logFile.createNewFile();
                    fos = new FileOutputStream(logFile, Logger.append);
                    writer = new OutputStreamWriter(fos, "UTF-8");
                } else {
                    judgeSize();
                }
            } else {
                Log.v(TAG, "sdcard is unavailable");
            }
        } else {
            logFile = new File(context.getFilesDir() + File.separator
                    + Logger.path);
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
                fos = new FileOutputStream(logFile, Logger.append);
                writer = new OutputStreamWriter(fos, "UTF-8");
            } else {
                judgeSize();
            }
        }
    }


    private static void judgeSize() throws IOException {
        if (max != 0 && logFile.length() > Logger.max) {
            int index = 0;
            File back = null;
          
            back = new File(logFile.getAbsolutePath() + "." + index);
            if (!back.exists()) {
                logFile.renameTo(back);
                logFile.delete();
                logFile.createNewFile();
            } else {
                back.delete();
                logFile.renameTo(back);
                logFile.delete();
                logFile.createNewFile();
            }
          
          
          
          
          
          
          
        }
        fos = new FileOutputStream(logFile, Logger.append);
        writer = new OutputStreamWriter(fos, "UTF-8");
    }

    public static void v(String msg) {
        if (logger != null)
            logger.v(msg, Logger.logCat, Logger.logCat);
    }

    /**
     * 打印verbos日志
     *
     * @param msg     具体的内容
     * @param logcat  true：在logcat中也输出，false，在logcat中不输出
     * @param fileLog 是否在文件中输出
     */
    public synchronized void v(String msg, boolean logcat, boolean fileLog) {
        if (writer == null) {
            return;
        }
        if (msg != null && Logger.level <= Logger.VERBOSE) {
            StringBuilder builder = new StringBuilder();
            if (timeSwitch)
                builder.append(dateFormat.format(new Date()) + " ");
            builder.append("[V]:");
            builder.append(msg);
            if (logcat)
                Log.v(TAG, builder.toString());
            if (fileLog)
                write(builder);
        }
    }

    public static void d(String msg) {
        if (logger != null)
            logger.d(msg, Logger.logCat, Logger.logCat);
    }

    /**
     * 打印debug日志
     *
     * @param msg     具体的内容
     * @param logcat  true：在logcat中也输出，false，在logcat中不输出
     * @param fileLog 是否在文件中输出
     */
    public synchronized void d(String msg, boolean logcat, boolean fileLog) {
        if (writer == null) {
            return;
        }
        if (msg != null && Logger.level <= Logger.DEBUG) {
            StringBuilder builder = new StringBuilder();
            if (timeSwitch)
                builder.append(dateFormat.format(new Date()) + " ");
            builder.append("[D]:");
            builder.append(msg);
            if (logcat)
                Log.d(TAG, builder.toString());
            if (fileLog)
                write(builder);
        }
    }

    public static void i(String msg) {
        if (logger != null)
            logger.i(msg, Logger.logCat, Logger.logCat);
    }

    /**
     * 打印info日志
     *
     * @param msg     具体的内容
     * @param logcat  true：在logcat中也输出，false，在logcat中不输出
     * @param fileLog 是否在文件中输出
     */
    public synchronized void i(String msg, boolean logcat, boolean fileLog) {
        if (writer == null) {
            return;
        }
        if (msg != null && Logger.level <= Logger.INFO) {
            StringBuilder builder = new StringBuilder();
            if (timeSwitch)
                builder.append(dateFormat.format(new Date()) + " ");
            builder.append("[I]:");
            builder.append(msg);
            if (logcat)
                Log.i(TAG, builder.toString());
            if (fileLog)
                write(builder);
        }
    }

    public static void w(String msg) {
        if (logger != null)
            logger.w(msg, Logger.logCat, Logger.logCat);
    }

    /**
     * 打印warn日志
     *
     * @param msg     具体的内容
     * @param logcat  true：在logcat中也输出，false，在logcat中不输出
     * @param fileLog 是否在文件中输出
     */
    public synchronized void w(String msg, boolean logcat, boolean fileLog) {
        if (writer == null) {
            return;
        }
        if (msg != null && Logger.level <= Logger.WARN) {
            StringBuilder builder = new StringBuilder();
            if (timeSwitch)
                builder.append(dateFormat.format(new Date()) + " ");
            builder.append("[W]:");
            builder.append(msg);
            if (logcat)
                Log.w(TAG, builder.toString());
            if (fileLog)
                write(builder);
        }
    }

    public static void e(String msg) {
        if (logger != null)
            logger.e(msg, Logger.logCat, Logger.logCat);
    }

    /**
     * 打印error日志
     *
     * @param msg     具体的内容
     * @param logcat  true：在logcat中也输出，false，在logcat中不输出
     * @param fileLog 是否在文件中输出
     */
    public synchronized void e(String msg, boolean logcat, boolean fileLog) {
        if (writer == null) {
            return;
        }
        if (msg != null && Logger.level <= Logger.ERROR) {
            StringBuilder builder = new StringBuilder();
            if (timeSwitch)
                builder.append(dateFormat.format(new Date()) + " ");
            builder.append("[E]:");
            builder.append(msg);
            if (logcat)
                Log.e(TAG, builder.toString());
            if (fileLog)
                write(builder);
        }
    }

    /**
     * 写日志到文件中
     *
     * @param builder 日志内容
     */
    private synchronized void write(StringBuilder builder) {
        if (writer != null) {
            try {
                judgeSize();
            } catch (IOException e) {
                Log.e(TAG,
                        "judge file size error, errmessage is "
                                + e.getMessage());
            }
            try {
                writer.write(builder.toString() + "\n");
                writer.flush();
            } catch (IOException e) {
                Log.e(TAG, "Log msg error,errmessage is " + e.getMessage());
            }
        }
    }


    public void close() {
        try {
            if (writer != null)
                writer.close();
            if (fos != null)
                fos.close();
        } catch (IOException e) {
            Log.e(TAG, "close logger error, errmessage is " + e.getMessage());
        }
    }

    /**
     * Setter of level
     *
     * @param level the level to set
     */
    public void setLevel(int level) {
        Logger.level = level;
    }
}
