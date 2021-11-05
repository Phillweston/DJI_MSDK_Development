package com.flycloud.autofly.ux.view.button;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flycloud.autofly.ux.R;


public class ExpandableBreathingButton extends View {

    private String mButtonStr;
    private String[] mItemTextArr = {""};
    private float mButtonTextSize;
    private int mButtonTextColor;
    private float mItemTextSize;
    private int mItemTextColor;
    private int mInnerCircleColor;
    private int mBackgroundColor;
    private int mOuterRadius;
    private int mInnerRadius;
    private Paint mInnerCirclePaint;
    private Paint mmBackgroundRectPaint;
    private Paint mButtonTextPaint;
    private Paint mItemTextPaint;
    private Paint mBreathePaint;
    private RectF mBackgroundRectF;
    private int mInnerCircleCenterX;
    private int mInnerCircleCenterY;
    private int mItemWidth;
    private int mBackgroundRectFLeft;
    private int mArcWidth = 12;
    private float mBreatheRadius;
    private int mItemTextAlpha = 255;
    private boolean isOpen;
    private boolean isAniming;
    private boolean isEnableExpand = true;
    private boolean isDisplayButtonText = true;
    private ValueAnimator mBreatheAnim;
    private OnButtonItemClickListener mOnButtonItemClickListener;

    public ExpandableBreathingButton(Context context) {
        this(context, null);
    }

    public ExpandableBreathingButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableBreathingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Ux_ExpandableBreathingButton, defStyleAttr, 0);
        mButtonStr = ta.getString(R.styleable.Ux_ExpandableBreathingButton_ebbButtonText);
        String itemStr = ta.getString(R.styleable.Ux_ExpandableBreathingButton_ebbItemText);

        if (!TextUtils.isEmpty(itemStr)) {
            mItemTextArr = itemStr.split("\\|");
        }
        mButtonTextSize = ta.getDimension(R.styleable.Ux_ExpandableBreathingButton_ebbButtonTextSize,
                getResources().getDimensionPixelSize(R.dimen.default_text_size_normal));
        mButtonTextColor = ta.getColor(R.styleable.Ux_ExpandableBreathingButton_ebbButtonTextColor, Color.WHITE);
        mItemTextSize = ta.getDimension(R.styleable.Ux_ExpandableBreathingButton_ebbItemTextSize,
                getResources().getDimensionPixelSize(R.dimen.default_text_size_medium));
        mItemTextColor = ta.getColor(R.styleable.Ux_ExpandableBreathingButton_ebbItemTextColor, Color.WHITE);
        mInnerCircleColor = ta.getColor(R.styleable.Ux_ExpandableBreathingButton_ebbInnerCircleColor, Color.parseColor("#0877f4"));
        mBackgroundColor = ta.getColor(R.styleable.Ux_ExpandableBreathingButton_ebbRectBackgroundColor, Color.parseColor("#5db8ff"));
        ta.recycle();
        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setStyle(Paint.Style.FILL);
        mInnerCirclePaint.setColor(mInnerCircleColor);
        mmBackgroundRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mmBackgroundRectPaint.setStyle(Paint.Style.FILL);
        mmBackgroundRectPaint.setColor(mBackgroundColor);
        mButtonTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonTextPaint.setColor(mButtonTextColor);
        mButtonTextPaint.setTextSize(mButtonTextSize);
        mItemTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mItemTextPaint.setColor(mItemTextColor);
        mItemTextPaint.setTextSize(mItemTextSize);
        mBreathePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBreathePaint.setStyle(Paint.Style.STROKE);
        mBreathePaint.setColor(mBackgroundColor);
        mBreathePaint.setStrokeWidth(mArcWidth / 2);
        mBackgroundRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int width = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            if (mode == MeasureSpec.AT_MOST) {
                int maxWidth = 912;
                width = Math.min(maxWidth, size);
            }
        }
        return width;
    }

    private int measureHeight(int heightMeasureSpec) {
        int height = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            if (mode == MeasureSpec.AT_MOST) {
                int maxHeight = 140;
                height = Math.min(maxHeight, size);
            }
        }
        return height;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mOuterRadius = getHeight() / 2 - mArcWidth / 2;
        mInnerRadius = mOuterRadius - mArcWidth / 2;
        mBackgroundRectFLeft = getWidth() - mOuterRadius * 2 - mArcWidth / 2;
        mInnerCircleCenterX = getWidth() - mOuterRadius - mArcWidth / 2;
        mInnerCircleCenterY = getHeight() / 2;
        calculateItemWidth();

        mBreatheRadius = getHeight() / 2 - mArcWidth / 4;
        mBreatheAnim = ValueAnimator.ofFloat(mBreatheRadius, mBreatheRadius - mArcWidth / 2);
        mBreatheAnim.setDuration(1000);
        mBreatheAnim.setRepeatMode(ValueAnimator.REVERSE);
        mBreatheAnim.setRepeatCount(Integer.MAX_VALUE);
        mBreatheAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBreatheRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mBreatheAnim.start();
    }

    private void calculateItemWidth() {
        mItemWidth = (getWidth() - 2 * mOuterRadius - mArcWidth - mArcWidth / 2) / mItemTextArr.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBackgroundRectF.set(mBackgroundRectFLeft, mArcWidth / 2, getWidth() - mArcWidth / 2, getHeight() - mArcWidth / 2);
        canvas.drawRoundRect(mBackgroundRectF, mOuterRadius, mOuterRadius, mmBackgroundRectPaint);
        canvas.drawCircle(mInnerCircleCenterX, mInnerCircleCenterY, mInnerRadius, mInnerCirclePaint);
        canvas.drawCircle(mInnerCircleCenterX, mInnerCircleCenterY, mBreatheRadius, mBreathePaint);
        float buttonTextWidth = mButtonTextPaint.measureText(mButtonStr, 0, mButtonStr.length());
        Paint.FontMetrics publishFontMetrics = mButtonTextPaint.getFontMetrics();
        if (isDisplayButtonText) {
            canvas.drawText(mButtonStr, 0, mButtonStr.length(), getWidth() - mOuterRadius - mArcWidth / 2 - buttonTextWidth / 2,
                    mOuterRadius + mArcWidth / 2 + -(publishFontMetrics.ascent + publishFontMetrics.descent) / 2, mButtonTextPaint);
        } else {
            canvas.drawText("", 0, 0, 0, 0, mButtonTextPaint);
        }
        if (mBackgroundRectFLeft == mArcWidth / 2) {
            mItemTextPaint.setAlpha(mItemTextAlpha);
            for (int i = 0; i < mItemTextArr.length; i++) {
                float itemTextWidth = mButtonTextPaint.measureText(mItemTextArr[i], 0, mItemTextArr[i].length());
                Paint.FontMetrics itemFontMetrics = mButtonTextPaint.getFontMetrics();
                canvas.drawText(mItemTextArr[i], 0, mItemTextArr[i].length(),
                        mItemWidth * i + mItemWidth / 2 - itemTextWidth / 2, mOuterRadius + mArcWidth / 2 -
                                (itemFontMetrics.ascent + itemFontMetrics.descent) / 2, mItemTextPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (!isEnableExpand) {
            return super.onTouchEvent(event);
        }*/
        if (isAniming) {//在动画的时候什么都不做
            return true;
        }
        int x;
        int y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                if (!isOpen && x < getWidth() - 2 * mOuterRadius && y > 0 && y < getHeight()) {

                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                x = (int) event.getX();
                y = (int) event.getY();
                int d = (int) Math.sqrt(Math.pow(x - mInnerCircleCenterX, 2) + Math.pow(y - mInnerCircleCenterY, 2));
                if (d < mInnerRadius) {//点击发布按钮

                    if (isEnableExpand) {
                        if (isOpen) {
                            closeButton();
                        } else {
                            openButton();
                        }
                    }

                    if (mOnButtonItemClickListener != null) {
                        mOnButtonItemClickListener.onExpandClick();
                    }

                } else {
                    if (isOpen && y > 0 && y < getHeight() && isEnableExpand) {
                        for (int i = 0; i < mItemTextArr.length; i++) {//计算点击了哪个item
                            if (x < mItemWidth * (i + 1)) {
                                if (mOnButtonItemClickListener != null) {
                                    mOnButtonItemClickListener.onButtonItemClick(i);
                                }
                                break;
                            }
                        }
                        closeButton();
                    }
                }
                break;
        }
        return true;
    }

    private void openButton() {
        isAniming = true;
        isOpen = true;
        AnimatorSet openAnimSet = new AnimatorSet();
        ValueAnimator rectLeftAnim = ValueAnimator.ofInt(mBackgroundRectFLeft, mArcWidth / 2);
        rectLeftAnim.setDuration(250);
        ValueAnimator textAlphaAnim = ValueAnimator.ofInt(0, mItemTextAlpha);
        textAlphaAnim.setDuration(120);
        rectLeftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundRectFLeft = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        textAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mItemTextAlpha = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        openAnimSet.playSequentially(rectLeftAnim, textAlphaAnim);
        openAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAniming = false;
            }
        });
        openAnimSet.start();
    }

    private void closeButton() {
        isAniming = true;
        isOpen = false;
        mItemTextAlpha = 255;
        ValueAnimator closeAnimSet = ValueAnimator.ofInt(mBackgroundRectFLeft, getWidth() - mOuterRadius * 2 - mArcWidth / 2);
        closeAnimSet.setDuration(250);
        closeAnimSet.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundRectFLeft = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        closeAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAniming = false;
            }
        });
        closeAnimSet.start();
    }

    public boolean isExpandEnable() {
        return isEnableExpand;
    }

    /**
     * 是否支持展开按钮
     *
     * @param enable
     */
    public void enableExpand(boolean enable) {
        this.isEnableExpand = enable;
    }

    public void disPlayButtonText(boolean isDisplay) {
        this.isDisplayButtonText = isDisplay;
        invalidate();
    }

    public void setItemTextArr(String[] itemTextArr) {
        mItemTextArr = itemTextArr;
        calculateItemWidth();
        invalidate();
    }

    public void setOnButtonItemClickListener(OnButtonItemClickListener onButtonItemClickListener) {
        mOnButtonItemClickListener = onButtonItemClickListener;
    }

    public interface OnButtonItemClickListener {
        void onButtonItemClick(int position);

        void onExpandClick();
    }
}
