package com.ew.autofly.widgets.drawingView;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class Eraser implements ToolInterface {
    private static final float TOUCH_TOLERANCE = 4.0f;

    private float mCurrentX = 0.0f;
    private float mCurrentY = 0.0f;
    private Path mPath = new Path();
    private Paint mEraserPaint = new Paint();
    private boolean mHasDraw = false;
    private int eraserSize = 0;

    public Eraser(int eraserSize) {
        mEraserPaint.setStrokeWidth(eraserSize);
        this.eraserSize = eraserSize;
        setUp();
    }

    private void setUp() {
        mEraserPaint.setColor(Color.BLACK);
        mEraserPaint.setDither(true);
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.SQUARE);
        mEraserPaint
                .setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    @Override
    public void draw(Canvas canvas) {
        if (null != canvas) {
            canvas.drawPath(mPath, mEraserPaint);
        }
    }

    @Override
    public void touchDown(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mCurrentX = x;
        mCurrentY = y;
    }

    @Override
    public void touchMove(float x, float y) {
        if (isMoved(x, y)) {
            drawBeziercurve(x, y);
            mCurrentX = x;
            mCurrentY = y;
            mHasDraw = true;
        }

    }

    @Override
    public void touchUp(float x, float y) {
        mPath.lineTo(x, y);
    }

    @Override
    public boolean hasDraw() {
        return mHasDraw;
    }

    private boolean isMoved(float x, float y) {
        float dx = Math.abs(x - mCurrentX);
        float dy = Math.abs(y - mCurrentX);
        boolean isMoved = ((dx >= TOUCH_TOLERANCE) || (dy >= TOUCH_TOLERANCE));
        return isMoved;
    }

    private void drawBeziercurve(float x, float y) {
        mPath.quadTo(mCurrentX, mCurrentY, (x + mCurrentX) / 2,
                (y + mCurrentY) / 2);
    }


    @Override
    public String toString() {
        return "eraser:" + " size is" + eraserSize;
    }
}
