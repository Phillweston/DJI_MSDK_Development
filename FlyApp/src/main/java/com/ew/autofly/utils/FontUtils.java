
package com.ew.autofly.utils;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class FontUtils {
    /*
      * 默认采用白色字体，宋体文字加粗
      */
    public static Drawable getImage(Context context, int width, int height, String mString, int size) {
        return getImage(context, width, height, mString, size, Color.BLACK, Typeface.create("宋体", Typeface.NORMAL));
    }

    public static Drawable getImage(Context context, int width, int height, String mString, int size, int color) {
        return getImage(context, width, height, mString, size, color, Typeface.create("宋体", Typeface.NORMAL));
    }

    public static Drawable getImage(Context context, int width, int height, String mString, int size, int color, String familyName) {
        return getImage(context, width, height, mString, size, color, Typeface.create(familyName, Typeface.NORMAL));
    }

    public static Drawable getImage(Context context, int width, int height, String mString, int size, int color, Typeface font) {
        int padding = (int) scalaFonts(context, 3);
        Paint pText = new Paint();
        pText.setColor(color);
        pText.setTypeface(font);
        pText.setAntiAlias(true);
        pText.setFilterBitmap(true);
        pText.setTextSize(scalaFonts(context, size));

        Paint pBackground = new Paint();
        pBackground.setAntiAlias(true);
        pBackground.setColor(Color.parseColor("#70ffffff"));

        float tX = getFontlength(pText, mString) + padding * 2;
        float tY = getFontHeight(pText) + getFontLeading(pText) / 2;
        Bitmap bmp = Bitmap.createBitmap((int) tX, (int) tY, Bitmap.Config.ARGB_8888);

        Canvas canvasTemp = new Canvas(bmp);
        final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 50;
        canvasTemp.drawRoundRect(rectF, roundPx, roundPx, pBackground);
        canvasTemp.drawColor(Color.TRANSPARENT);
        canvasTemp.drawText(mString, padding, tY - getFontLeading(pText) / 2, pText);

        return new BitmapDrawable(bmp);
    }

    /**
     * 根据屏幕系数比例获取文字大小
     *
     * @return
     */
    private static float scalaFonts(Context context, int size) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (size * fontScale + 0.5f);
    }

    
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    
    public static float getFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    
    public static float getFontLeading(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }
}
