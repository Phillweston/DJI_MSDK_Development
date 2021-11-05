package com.flycloud.autofly.ux.widget.photopick;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;



public class BitmapHelper {
    private static final String TAG = "BitmapHelper";
    public static int max = 0;
    public static boolean act_bool = true;
    public static List<Bitmap> bmp = new ArrayList<>();


    public static List<String> images = new ArrayList<>();













    public static Bitmap revisionImageSize(File file) {
        if (file == null)
            return null;
        long start = System.currentTimeMillis();
        BufferedInputStream in = null;
        Bitmap bitmap = null;

        FileInputStream fis = null;
        FileInputStream fis2 = null;
        try {
            fis = new FileInputStream(file);
            in = new BufferedInputStream(fis);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            IOUtil.close(in);
            int i = 0;
            while (true) {
                if ((options.outWidth >> i <= 2000) && (options.outHeight >> i <= 2000)) {
                    fis2 = new FileInputStream(file);
                    in = new BufferedInputStream(fis2);
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(fis);
            IOUtil.close(fis2);
            IOUtil.close(in);
        }
        long time = System.currentTimeMillis() - start;
        Log.d(TAG, "revisionImageSize: 耗时" + time + "毫秒");
        return bitmap;
    }


    public static boolean saveBitmap2file(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }


    public static File saveBitmap2file(Activity activity, Bitmap bmp) {
        if (bmp == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        File file = new File(activity.getCacheDir(), "uploadFile");
        IOUtil.writeTo(stream, file);
        IOUtil.close(stream);
        return file;
    }


    public static Bitmap decodeUriAsBitmap(Activity activity, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(activity.
                    getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static int getImageSize() {
        return images.size();
    }


    public static File decodeUriAsFile(Activity activity, Uri uri) {
        Bitmap bitmap = decodeUriAsBitmap(activity, uri);
        return saveBitmap2file(activity, bitmap);
    }
}
