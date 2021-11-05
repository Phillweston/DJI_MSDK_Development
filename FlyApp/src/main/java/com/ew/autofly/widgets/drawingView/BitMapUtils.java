package com.ew.autofly.widgets.drawingView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

public class BitMapUtils {

    public static void saveToSdCard(String path, Bitmap bitmap) {
        if (null != bitmap && null != path && !path.equalsIgnoreCase("")) {
            try {
                File file = new File(path);
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
        }
    }

    public static Bitmap duplicateBitmap(Bitmap bmpSrc, int width, int height) {
        if (null == bmpSrc) {
            return null;
        }

        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();

        Bitmap bmpDest = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            Rect viewRect = new Rect();
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            if (bmpSrcWidth <= width && bmpSrcHeight <= height) {
                viewRect.set(rect);
            } else if (bmpSrcHeight > height && bmpSrcWidth <= width) {
                viewRect.set(0, 0, bmpSrcWidth, height);
            } else if (bmpSrcHeight <= height && bmpSrcWidth > width) {
                viewRect.set(0, 0, width, bmpSrcWidth);
            } else if (bmpSrcHeight > height && bmpSrcWidth > width) {
                viewRect.set(0, 0, width, height);
            }
            canvas.drawBitmap(bmpSrc, rect, viewRect, null);
        }

        return bmpDest;
    }

    public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }

        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();

        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight,
                Config.ARGB_8888);
        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);

            canvas.drawBitmap(bmpSrc, rect, rect, null);
        }

        return bmpDest;
    }


    public static byte[] bitampToByteArray(Bitmap bitmap) {
        byte[] array = null;
        try {
            if (null != bitmap) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                array = os.toByteArray();
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;
    }

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
      
      
      
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
      
        cv.drawBitmap(background, 0, 0, null);
      
        cv.drawBitmap(foreground, 0, 0, null);
      
        cv.save();
      
        cv.restore();
        return newbmp;
    }
}
