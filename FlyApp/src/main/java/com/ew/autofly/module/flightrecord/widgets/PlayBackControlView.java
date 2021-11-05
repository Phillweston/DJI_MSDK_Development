package com.ew.autofly.module.flightrecord.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ew.autofly.R;
import com.flycloud.autofly.base.util.DensityUtils;

public class PlayBackControlView extends View {

    private Context mContext;

    private int mCenterX;

    private int mCenterY;

    private int mOutterRadius;

    private int mInnerRadius;

    private int mWidth;

    private int mHeight;

    private float smallWidth;

    private float smallHeight;

    private Paint mOutterPaint;

    private Paint mInnerPaint;

    private Paint mProgressPaint;

    private Paint mRectPaint;

  
    private Paint mRatePaint;

    private int mInnerWidth = 3;

    private int mOutterWidth = 3;


  
    private int mRectPaintWidth = 2;

    private int mDefaultPadding = 10;

  
    private float mRectWidth;

    private RectF mVerticalRect;

    private RectF mHorizonalRect;

    private RectF mRateVRect;

    private RectF mRateHRect;

    private int mHorizontalNum;

    private int mVerticalNum;


    public PlayBackControlView(Context context) {
        this(context,null);
    }

    public PlayBackControlView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayBackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {






        smallWidth = DensityUtils.dip2px(mContext,70);
        smallHeight = DensityUtils.dip2px(mContext,70);
        mRectWidth = DensityUtils.dip2px(mContext,20);

        mRectPaint = new Paint();
        mRectPaint.setColor(getResources().getColor(R.color.white));
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStrokeWidth(mRectPaintWidth);

        mRatePaint = new Paint();
        mRatePaint.setColor(getResources().getColor(R.color.blue));
        mRatePaint.setStyle(Paint.Style.FILL);
        mRatePaint.setAntiAlias(true);

        mOutterPaint = new Paint();
        mOutterPaint.setColor(getResources().getColor(R.color.white));
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setAntiAlias(true);
        mOutterPaint.setStrokeWidth(mOutterWidth);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            mOutterRadius = mWidth / 2 - mDefaultPadding;

            mCenterX = mWidth / 2;
            mCenterY = mHeight / 2;
            mVerticalRect = new RectF(mCenterX - mRectWidth / 2, mCenterY - mOutterRadius, mCenterX + mRectWidth / 2, mCenterY + mOutterRadius);
            mHorizonalRect = new RectF(mCenterX - mOutterRadius, mCenterY - mRectWidth / 2, mCenterX+mOutterRadius, mCenterY + mRectWidth / 2);
    }



    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        }else{
            result = (int) Math.min(size,smallHeight);
        }

        return result;
    }


    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        }else{
            result = (int) Math.min(size,smallWidth);
        }

        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCenterX,mCenterY,mOutterRadius,mOutterPaint);

        canvas.drawRoundRect(mVerticalRect,mRectWidth/2,mRectWidth/2,mRectPaint);
        canvas.drawRoundRect(mHorizonalRect,mRectWidth/2,mRectWidth/2,mRectPaint);

        if (mRateVRect != null) {
            canvas.drawRoundRect(mRateVRect, mRectWidth / 2, mRectWidth / 2, mRatePaint);
        }

        if (mRateHRect != null) {
            canvas.drawRoundRect(mRateHRect, mRectWidth / 2, mRectWidth / 2, mRatePaint);
        }
    }


    public float getRectWidth(){
        return mRectWidth;
    }

    public void changeControlNum(int vertical , int horizontal) {
        mVerticalNum = vertical;
        int length = (int) (mHeight/2-mRectWidth/2);
        if (mVerticalNum > 0) {
            int bottom = (int) (mHeight/2-mRectWidth/2);
            int top = bottom - (int) ((float)mVerticalNum/100*length);
            mRateVRect = new RectF(mVerticalRect.left, top, mVerticalRect.right, bottom);
        } else if (mVerticalNum < 0) {
            int top = (int) (mHeight / 2 + mRectWidth / 2);
            int bottom = top + (int) ((float) Math.abs(mVerticalNum) / 100 * length);
            mRateVRect = new RectF(mVerticalRect.left, top, mVerticalRect.right, bottom);
        } else {
            mRateVRect = new RectF();
        }


        mHorizontalNum = horizontal;
        if (mHorizontalNum > 0) {
            int left = (int) (mWidth/2+mRectWidth/2);
            int right = left + (int)((float)mHorizontalNum/100*length);
            mRateHRect = new RectF(left, mHorizonalRect.top,right, mHorizonalRect.bottom);
        }else if(mHorizontalNum < 0){
            int right = (int) (mWidth/2-mRectWidth/2);
            int left = right - (int)((float)Math.abs(mHorizontalNum)/100*length);
            mRateHRect = new RectF(left,mHorizonalRect.top,right, mHorizonalRect.bottom);
        }else {
            mRateHRect = new RectF();
        }

    }

}
