package com.ew.autofly.utils.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ew.autofly.logger.Logger;
import com.flycloud.autofly.base.util.SysUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImageUtils {

    public static void saveBitmap(File saveFile, Bitmap bitmap) {
        if (saveFile == null || bitmap == null) {
            return;
        }
        if (saveFile.exists()) {
            saveFile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Bitmap getWholeListViewItemsToBitmap(ListView listview, int fixHeigth, boolean needLine) {
        Paint paintLine = new Paint();
        paintLine.setColor(Color.parseColor("#6d6d72"));
        paintLine.setStrokeWidth(1);

        BaseAdapter adapter = (BaseAdapter) listview.getAdapter();
        int itemscount = adapter.getCount();
        int allitemsheight = 0;
        int itemWidth = 0, itemHeigth = 0;
        List<Bitmap> bmps = new ArrayList<Bitmap>();
        int h = SysUtils.dip2px(listview.getContext(), fixHeigth);
        try {
            for (int i = 0; i < itemscount; i++) {
                View childView = adapter.getView(i, null, listview);
                childView.measure(View.MeasureSpec.makeMeasureSpec(listview.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY));
                itemWidth = childView.getMeasuredWidth();
                itemHeigth = childView.getMeasuredHeight();
                childView.layout(0, 0, itemWidth, itemHeigth);
                childView.setDrawingCacheEnabled(true);
                childView.buildDrawingCache();
                if (i == 0 && needLine) {
                    Bitmap bLine = Bitmap.createBitmap(itemWidth, 1, Bitmap.Config.ARGB_8888);
                    bmps.add(bLine);
                    allitemsheight = 1;
                }
                bmps.add(childView.getDrawingCache());
                if (needLine) {
                    Bitmap bLine = Bitmap.createBitmap(itemWidth, 1, Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(bLine);
                    c.drawLine(0, 0, bLine.getWidth(), 0, paintLine);
                    bmps.add(bLine);
                    allitemsheight += itemHeigth + 1;
                } else {
                    allitemsheight += itemHeigth;
                }

            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        if (allitemsheight == 0) {
            return null;
        }
        Bitmap bigbitmap = Bitmap.createBitmap(itemWidth, allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap bmp = bmps.get(i);
            if (bmp == null) continue;
            bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
            iHeight += bmp.getHeight();
            bmp.recycle();
            bmp = null;
        }

        return bigbitmap;
    }

    public static Bitmap concactBitmap(Bitmap bitmap1, Bitmap bitmap2) {
        if (bitmap1 == null || bitmap2 == null) {

            return (bitmap1 == null) ? bitmap2 : bitmap1;
        }
        int itemWidth = Math.max(bitmap1.getWidth(), bitmap2.getWidth());
        int allitemsheight = bitmap1.getHeight() + bitmap2.getHeight();
        Bitmap bigbitmap = Bitmap.createBitmap(itemWidth, allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);
        Paint paint = new Paint();
        int iHeight = 0;
        bigcanvas.drawBitmap(bitmap1, 0, iHeight, paint);
        bigcanvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), paint);
        return bigbitmap;
    }


    public static String saveScreenShot(Bitmap bitmap, String filePath) {
        FileOutputStream fos = null;
        String snapshotName = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            snapshotName = sdf.format(new Date()) + ".png";

            fos = new FileOutputStream(filePath + File.separator + snapshotName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            snapshotName = "";
        } catch (Exception e) {
            e.printStackTrace();
            snapshotName = "";
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return snapshotName;
    }


    public static void saveScreenShot(Bitmap bitmap, String filePath, String snapshotName) {
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(filePath + File.separator + snapshotName);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, fos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过降低图片的质量来压缩图片
     *
     * @param bitmap  要压缩的图片位图对象
     * @param maxSize 压缩后图片大小的最大值,单位KB
     * @return 压缩后的图片位图对象
     */
    public static Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        Logger.e("图片压缩前大小：" + baos.toByteArray().length + "byte");
        boolean isCompressed = false;
        while (baos.toByteArray().length / 1024 > maxSize && quality > 0) {
            quality -= 10;
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            Logger.e("质量压缩到原来的" + quality + "%时大小为："
                    + baos.toByteArray().length + "byte");
            isCompressed = true;
        }
        Logger.e("图片压缩后大小：" + baos.toByteArray().length + "byte");
        if (isCompressed) {
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                    baos.toByteArray(), 0, baos.toByteArray().length);
            recycleBitmap(bitmap);
            return compressedBitmap;
        } else {
            return bitmap;
        }
    }

    /**
     * 回收位图对象
     *
     * @param bitmap
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
            bitmap = null;
        }
    }
}
