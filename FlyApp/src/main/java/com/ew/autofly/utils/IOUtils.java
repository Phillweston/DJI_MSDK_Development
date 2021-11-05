package com.ew.autofly.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;

import com.ew.autofly.constant.AppConstant;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IOUtils {
    /**
     * @param context
     * @return
     */
    public static String getRootStoragePath(Context context) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + AppConstant.APP_STORAGE_PATH + File.separator;
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            return savePath;
        } else {
            String tmp = context.getFilesDir().getAbsolutePath();
            if (tmp.endsWith(File.separator)) {
                return tmp;
            } else {
                return tmp + File.separator;
            }
        }
    }

    /**
     * @param fileFullName
     * @param content
     */
    public static boolean writeTxtFile(String fileFullName, String content) {
        boolean isWriteSuccess = false;

        FileWriter fw = null;
        try {
            File file = new File(fileFullName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file, true);
            fw.write(content);
            fw.close();
            isWriteSuccess = true;
        } catch (Exception e) {
            isWriteSuccess = false;
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isWriteSuccess;
    }

    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readPamFile(File file) {
        if (!file.exists()) {
            return "";
        }

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            String p = EncodingUtils.getString(buffer, "UTF-8");
            p = p.replace("+", "=");
            p = p.replace("!", "1");
            p = p.replace("*", "7");
            p = p.replace("#", "2");
            p = p.replace("~", "0");
            p = p.replace("%", "6");
            p = p.replace("?", "3");
            p = p.replace("/", "O");
            p = p.replace("@", "M");
            p = p.replace("^", "9");
            byte[] tmp = p.getBytes();
            byte[] decodeBuff = Base64.decode(tmp, 0, tmp.length, Base64.DEFAULT);
            if (decodeBuff == null) {
                return "";
            }
            return EncodingUtils.getString(decodeBuff, "UTF-8");
        } catch (Exception e) {
            return "";
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFile(File file) {
        if (!file.exists()) {
            return "";
        }

        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            return EncodingUtils.getString(buffer, "UTF-8");
        } catch (Exception e) {
            return "";
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeFileSdcard(String fileName, String message) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取android当前可用内存大小
     *
     * @return
     */
    public static String getAvailMemory(Activity activity) {// 获取android当前可用内存大小  

        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);


        return Formatter.formatFileSize(activity.getBaseContext(), mi.availMem);
    }

    /**
     * 系统内存总大小
     *
     * @param activity
     * @return
     */
    public static String getTotalMemory(Activity activity) {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return Formatter.formatFileSize(activity.getBaseContext(), initial_memory);
    }

    /**
     * 取得空闲sd卡空间大小
     *
     * @return
     */
    public static double getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory();

        StatFs stat = new StatFs(path.getPath());


        long blockSize = stat.getBlockSize();
        /*空闲的Block的数量*/
        long availableBlocks = stat.getAvailableBlocks();
        /* 返回bit大小值GB*/
        return availableBlocks * blockSize * 1.0 / 1024 / 1024 / 1024;


    }

    /**
     * SD卡大小
     *
     * @return
     */
    public static double getAllSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        /*获取block的SIZE*/
        long blockSize = stat.getBlockSize();
        /*块数量*/
        long availableBlocks = stat.getBlockCount();
        /* 返回bit大小值*/
        return availableBlocks * blockSize * 1.0 / 1024 / 1024;
    }

    /** */

    public static void renameFile(String oldname, String newname) {
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(oldname);
            File newfile = new File(newname);
            if (!oldfile.exists()) {
                return;
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                System.out.println(newname + "已经存在！");
            else {
                oldfile.renameTo(newfile);
            }
        } else {
            System.out.println("新文件名和旧文件名相同...");
        }
    }


    public static int copy(String fromPath, String toPathName) throws FileNotFoundException {
        InputStream from = new FileInputStream(fromPath);
        return copy(from, toPathName);
    }


    public static int copy(InputStream from, String toPathName) {
        try {
            delete(toPathName);
            OutputStream to = new FileOutputStream(toPathName);
            byte buf[] = new byte[1024];
            int c;
            while ((c = from.read(buf)) > 0) {
                to.write(buf, 0, c);
            }
            from.close();
            to.close();
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }


    public static int copyFiles(String sourcePath, String newPath) {
        try {
            File file = new File(sourcePath);
            String[] filePath = file.list();

            if (!(new File(newPath)).exists()) {
                (new File(newPath)).mkdir();
            }

            for (int i = 0; i < filePath.length; i++) {
                if ((new File(sourcePath + File.separator + filePath[i])).isDirectory()) {
                    copyFiles(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
                }

                if (new File(sourcePath + File.separator + filePath[i]).isFile()) {
                    copy(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
                }

            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    
    public static boolean delete(String filePathName) {
        if (TextUtils.isEmpty(filePathName)) return false;
        boolean isDeleteSuccess = false;

        try {
            File file = new File(filePathName);
            if (file.exists() && file.isFile()) {
                isDeleteSuccess = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeleteSuccess;
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {

        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);

        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {

            return false;
        }
        boolean flag = true;

        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {

            if (files[i].isFile()) {
                flag = delete(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }

            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {

            return false;
        }

        if (dirFile.delete()) {

            return true;
        } else {
            return false;
        }
    }

    /**
     * Copy the given file from the app's assets folder to the app's cache directory.
     *
     * @param fileName as String
     */
    public static void copyFileFromAssetsToCache(Context context, String fileName) {
        AssetManager assetManager = context.getApplicationContext().getAssets();
        File file = new File(context.getCacheDir() + File.separator + fileName);
        if (!file.exists()) {
            try {
                InputStream in = assetManager.open(fileName);
                OutputStream out = new FileOutputStream(context.getCacheDir() + File.separator + fileName);
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1) {
                    out.write(buffer, 0, read);
                    read = in.read(buffer);
                }
            } catch (Exception e) {
            }
        } else {
        }
    }


}