package com.ew.autofly.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ew.autofly.R;

public class CustomSeekBar extends View {

    private Paint paint;
    private Drawable drawableBg;
    private Bitmap bitmapBg;
    private Drawable drawableCircle , drawableadd , drawablereduct;
    private Bitmap bitmapCircle , bitmapAdd, bitmapReduct;
    private int Max = 100;
    private int progress = 0;
    private float unit = 1;

    private float circleX = 0;

    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        paint.setAntiAlias(true);

        drawableBg = getResources().getDrawable(R.drawable.seekbar_bg);
        drawableCircle = getResources().getDrawable(R.drawable.icon_seekbar_circle);
        drawableadd = getResources().getDrawable(R.drawable.icon_custom_seekbar_add);
        drawablereduct = getResources().getDrawable(R.drawable.icon_custom_seekbar_reduct);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        unit = (getWidth() * 1.0f - getHeight() * 3.0f) / (Max * 1.0f);

        if (isSetProgress) {
            circleX = progress * 1.0f * unit+ getHeight()+unit/2;
            Log.w("AAA", "setProgress:" + progress + " circleX:" + circleX + "  unit:" + unit + "  height:" + getHeight());
        }
        bitmapBg = drawableToBitmap(drawableBg, getWidth(), getHeight() - 20);
        bitmapCircle = drawableToBitmap(drawableCircle, getHeight(),getHeight());
        bitmapAdd = drawableToBitmap(drawableadd,getHeight(),getHeight());
        bitmapReduct = drawableToBitmap(drawablereduct,getHeight(),getHeight());
        canvas.drawBitmap(bitmapBg, 0 , 10, paint);
        if (circleX <= getHeight() + unit) {
            circleX = getHeight() + unit;
        }
        if (circleX >= (getWidth() - getHeight() * 2)) {
            circleX = getWidth() - getHeight() * 2;
        }
        if(!isSetProgress) {
            progress = (int) ((circleX - getHeight()) / unit);
        }else{
            isSetProgress = false;
        }
        paint.setColor(Color.parseColor("#25E0E7"));
        canvas.drawRect(getHeight() / 2, 10, (circleX + getHeight() / 2), getHeight()-10,paint);
        canvas.drawBitmap(bitmapCircle, circleX, 0, paint);
        canvas.drawBitmap(bitmapReduct,0,0,paint);
        canvas.drawBitmap(bitmapAdd,getWidth() - getHeight(),0,paint);




//





        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.seekBarChange(progress);
        }
    }

    private int downX;

    private int state = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                if (downX > circleX && downX < (circleX + getHeight())) {
                    state = 0;
                } else if (downX < getHeight()) {
                    state = 1;
                } else if (downX > (getWidth() - getHeight())) {
                    state = 2;
                } else {
                    state = -1;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (state == 0) {
                    int num = (int) ((event.getX() - getHeight()) / unit) + 1;
                    circleX = num * unit;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (state == 1) {
                    circleX = circleX - unit;
                }
                if (state == 2) {
                    circleX = circleX + unit;
                }
                state = -1;
                break;
        }
        invalidate();
        return true;
    }

    public void setMax(int max) {
        Max = max;
    }

    public int getMax() {
        return Max;
    }

    private boolean isSetProgress = false;

    public void setProgress(int progress) {
        if (progress > Max || progress < 0) {
            return;
        }
        this.progress = progress;
        isSetProgress = true;
        invalidate();
    }

    public int getProgress() {
        Log.w("AAA", "getProgress:" + progress + " circleX:" + circleX + "  unit:" + unit + "  height:" + getHeight());
        return progress;
    }

    private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {

        int w = width;
        int h = height;


        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;

        Bitmap bitmap = Bitmap.createBitmap(w, h, config);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);

        drawable.draw(canvas);
        return bitmap;
    }

    private onSeekBarChangeListener onSeekBarChangeListener;

    public interface onSeekBarChangeListener {
        void seekBarChange(int progress);
    }

    public void setOnSeekBarChangeListener(
            onSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

}
