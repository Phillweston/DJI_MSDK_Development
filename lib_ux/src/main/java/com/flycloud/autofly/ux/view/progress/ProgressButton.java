package com.flycloud.autofly.ux.view.progress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;


import com.flycloud.autofly.ux.R;

public class ProgressButton extends AppCompatTextView {
    public interface OnDownLoadClickListener {
        void clickDownload();

        void clickPause();

        void clickResume();

        void clickFinish();
    }

    public static class SimpleOnDownLoadClickListener implements OnDownLoadClickListener {
        @Override
        public void clickDownload() {

        }

        @Override
        public void clickPause() {

        }

        @Override
        public void clickResume() {

        }

        @Override
        public void clickFinish() {

        }
    }


    private Paint mBackgroundPaint;

    private Paint mBackgroundBorderPaint;

    private volatile Paint mTextPaint;


    private int mBackgroundColor;

    private int mBackgroundSecondColor;

    private int mTextColor;

    private int mTextCoverColor;


    private float mProgress = -1;
    private float mToProgress;
    private int mMaxProgress;
    private int mMinProgress;
    private float mProgressPercent;

    private float mButtonRadius;

    private RectF mBackgroundBounds;
    private LinearGradient mProgressBgGradient;
    private LinearGradient mProgressTextGradient;

    private ValueAnimator mProgressAnimation;

    private CharSequence mCurrentText;

    public static final int NORMAL = 1;
    public static final int DOWNLOADING = 2;
    public static final int PAUSE = 3;
    public static final int FINISH = 4;

    private int mState = -1;
    private float backgroud_strokeWidth;
    private String mNormalText, mDowningText, mFinishText, mPauseText;
    private long mAnimationDuration;
    private OnDownLoadClickListener mOnDownLoadClickListener;
    private boolean mEnablePause = true;


    public ProgressButton(Context context) {
        this(context, null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initAttrs(context, attrs);
            init();
            setupAnimations();
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Ux_ProgressButton);
        mBackgroundColor = a.getColor(R.styleable.Ux_ProgressButton_pb_backgroud_color, Color.parseColor("#6699ff"));
        mBackgroundSecondColor = a.getColor(R.styleable.Ux_ProgressButton_pb_backgroud_second_color, Color.LTGRAY);
        mButtonRadius = a.getFloat(R.styleable.Ux_ProgressButton_pb_radius, getMeasuredHeight() / 2);
        mTextColor = a.getColor(R.styleable.Ux_ProgressButton_pb_text_color, mBackgroundColor);
        mTextCoverColor = a.getColor(R.styleable.Ux_ProgressButton_pb_text_covercolor, Color.WHITE);
        backgroud_strokeWidth = a.getDimension(R.styleable.Ux_ProgressButton_pb_backgroud_strokeWidth, 3F);
        mNormalText = a.getString(R.styleable.Ux_ProgressButton_pb_text_normal);
        mDowningText = a.getString(R.styleable.Ux_ProgressButton_pb_text_downing);
        mFinishText = a.getString(R.styleable.Ux_ProgressButton_pb_text_finish);
        mPauseText = a.getString(R.styleable.Ux_ProgressButton_pb_text_pause);
        mAnimationDuration = a.getInt(R.styleable.Ux_ProgressButton_pb_animation_duration, 0);
        a.recycle();
    }

    /*@Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mTextPaint.setTextSize(getTextSize());
        invalidate();
    }*/

    private void init() {
        mMaxProgress = 100;
        mMinProgress = 0;
        mProgress = 0;
        if (mNormalText == null) {
            mNormalText = "下载";
        }
        if (mDowningText == null) {
            mDowningText = "";
        }
        if (mFinishText == null) {
            mFinishText = "打开";
        }
        if (mPauseText == null) {
            mPauseText = "继续";
        }

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundBorderPaint = new Paint();
        mBackgroundBorderPaint.setAntiAlias(true);
        mBackgroundBorderPaint.setStyle(Paint.Style.STROKE);
        mBackgroundBorderPaint.setStrokeWidth(backgroud_strokeWidth);
        mBackgroundBorderPaint.setColor(mBackgroundColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            setLayerType(LAYER_TYPE_SOFTWARE, mTextPaint);
        }


        setState(NORMAL);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDownLoadClickListener == null) {
                    return;
                }
                if (getState() == NORMAL) {
                    mOnDownLoadClickListener.clickDownload();
                    setState(DOWNLOADING);
                    setProgressText(0);
                } else if (getState() == DOWNLOADING) {
                    if (mEnablePause) {
                        mOnDownLoadClickListener.clickPause();
                        setState(PAUSE);
                    }
                } else if (getState() == PAUSE) {
                    mOnDownLoadClickListener.clickResume();
                    setState(DOWNLOADING);
                    setProgressText((int) mProgress);
                } else if (getState() == FINISH) {
                    mOnDownLoadClickListener.clickFinish();
                }
            }
        });
    }

    private void setupAnimations() {
        mProgressAnimation = ValueAnimator.ofFloat(0, 1).setDuration(mAnimationDuration);
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float timePercent = (float) animation.getAnimatedValue();
                mProgress = ((mToProgress - mProgress) * timePercent + mProgress);
                setProgressText((int) mProgress);
            }
        });
        mProgressAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (mToProgress < mProgress) {
                    mProgress = mToProgress;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mProgress == mMaxProgress) {
                    setState(FINISH);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            drawing(canvas);
        }
    }

    private void drawing(Canvas canvas) {
        drawBackground(canvas);
        drawTextAbove(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if (mBackgroundBounds == null) {
            mBackgroundBounds = new RectF();
            if (mButtonRadius == 0) {
                mButtonRadius = getMeasuredHeight() / 2;
            }
            mBackgroundBounds.left = backgroud_strokeWidth;
            mBackgroundBounds.top = backgroud_strokeWidth;
            mBackgroundBounds.right = getMeasuredWidth() - backgroud_strokeWidth;
            mBackgroundBounds.bottom = getMeasuredHeight() - backgroud_strokeWidth;
        }
        switch (mState) {
            case NORMAL:
                break;
            case DOWNLOADING:
            case PAUSE:
                mProgressPercent = mProgress / (mMaxProgress + 0f);
                mProgressBgGradient = new LinearGradient(backgroud_strokeWidth,
                        0, getMeasuredWidth() - backgroud_strokeWidth, 0,
                        new int[]{mBackgroundColor, mBackgroundSecondColor},
                        new float[]{mProgressPercent, mProgressPercent + 0.001f},
                        Shader.TileMode.CLAMP
                );
                mBackgroundPaint.setColor(mBackgroundColor);
                mBackgroundPaint.setShader(mProgressBgGradient);
                canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                break;
            case FINISH:
                mBackgroundPaint.setShader(null);
                mBackgroundPaint.setColor(mBackgroundColor);
                canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                break;
        }
        canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundBorderPaint);
    }

    private void drawTextAbove(Canvas canvas) {
        mTextPaint.setTextSize(getTextSize());
        final float y = canvas.getHeight() / 2 - (mTextPaint.descent() / 2 + mTextPaint.ascent() / 2);
        if (mCurrentText == null) {
            mCurrentText = "";
        }
        final float textWidth = mTextPaint.measureText(mCurrentText.toString());
        switch (mState) {
            case NORMAL:
                mTextPaint.setShader(null);
                mTextPaint.setColor(mTextColor);
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                break;
            case DOWNLOADING:
            case PAUSE:
                float w = getMeasuredWidth() - 2 * backgroud_strokeWidth;

                float coverlength = w * mProgressPercent;

                float indicator1 = w / 2 - textWidth / 2;

                float indicator2 = w / 2 + textWidth / 2;

                float coverTextLength = textWidth / 2 - w / 2 + coverlength;
                float textProgress = coverTextLength / textWidth;
                if (coverlength <= indicator1) {
                    mTextPaint.setShader(null);
                    mTextPaint.setColor(mTextColor);
                } else if (indicator1 < coverlength && coverlength <= indicator2) {
                    mProgressTextGradient = new LinearGradient((w - textWidth) / 2 + backgroud_strokeWidth, 0,
                            (w + textWidth) / 2 + backgroud_strokeWidth, 0,
                            new int[]{mTextCoverColor, mTextColor},
                            new float[]{textProgress, textProgress + 0.001f},
                            Shader.TileMode.CLAMP);
                    mTextPaint.setColor(mTextColor);
                    mTextPaint.setShader(mProgressTextGradient);
                } else {
                    mTextPaint.setShader(null);
                    mTextPaint.setColor(mTextCoverColor);
                }
                canvas.drawText(mCurrentText.toString(), (w - textWidth) / 2 + backgroud_strokeWidth, y, mTextPaint);
                break;
            case FINISH:
                mTextPaint.setColor(mTextCoverColor);
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                break;
        }
    }

    public int getState() {
        return mState;
    }

    public void reset() {
        setState(NORMAL);
    }

    public void finish() {
        setState(FINISH);
    }

    private void setState(int state) {
        if (mState != state) {//状态确实有改变
            this.mState = state;
            if (state == FINISH) {
                setCurrentText(mFinishText);
                mProgress = mMaxProgress;
            } else if (state == NORMAL) {
                mProgress = mToProgress = mMinProgress;
                setCurrentText(mNormalText);
            } else if (state == PAUSE) {
                setCurrentText(mPauseText);
            }
            invalidate();
        }
    }

    public void setCurrentText(CharSequence charSequence) {
        mCurrentText = charSequence;
        invalidate();
    }

    public CharSequence getCurrentText() {
        return mCurrentText;
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        if (progress <= mMinProgress
                || progress <= mToProgress
                || getState() == FINISH) {
            return;
        }
        mToProgress = Math.min(progress, mMaxProgress);
        setState(DOWNLOADING);
        if (mAnimationDuration > 0) {
            if (mProgressAnimation.isRunning()) {
                mProgressAnimation.end();
                mProgressAnimation.start();
            } else {
                mProgressAnimation.start();
            }
        } else {
            mProgress = progress;
            setProgressText((int) mProgress);
        }
    }

    private void setProgressText(int progress) {
        if (getState() == DOWNLOADING) {
            setCurrentText(mDowningText + progress + "%");
        }
    }

    public void pause() {
        setState(PAUSE);
    }

    public void pause(float progress) {
        mProgress = progress;
        pause();
    }

    public float getButtonRadius() {
        return mButtonRadius;
    }

    public void setButtonRadius(float buttonRadius) {
        mButtonRadius = buttonRadius;
    }

    public int getTextColor() {
        return mTextColor;
    }

    @Override
    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getTextCoverColor() {
        return mTextCoverColor;
    }

    public void setTextCoverColor(int textCoverColor) {
        mTextCoverColor = textCoverColor;
    }

    public int getMinProgress() {
        return mMinProgress;
    }

    public void setMinProgress(int minProgress) {
        mMinProgress = minProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
        mProgressAnimation.setDuration(animationDuration);
    }

    public OnDownLoadClickListener getOnDownLoadClickListener() {
        return mOnDownLoadClickListener;
    }

    public void setOnDownLoadClickListener(OnDownLoadClickListener onDownLoadClickListener) {
        mOnDownLoadClickListener = onDownLoadClickListener;
    }

    public boolean isEnablePause() {
        return mEnablePause;
    }

    public void setEnablePause(boolean enablePause) {
        mEnablePause = enablePause;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mState = ss.state;
        mProgress = ss.progress;
        mCurrentText = ss.currentText;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, (int) mProgress, mState, mCurrentText.toString());
    }

    public static class SavedState extends BaseSavedState {

        private int progress;
        private int state;
        private String currentText;

        public SavedState(Parcelable parcel, int progress, int state, String currentText) {
            super(parcel);
            this.progress = progress;
            this.state = state;
            this.currentText = currentText;
        }

        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
            state = in.readInt();
            currentText = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
            out.writeInt(state);
            out.writeString(currentText);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}