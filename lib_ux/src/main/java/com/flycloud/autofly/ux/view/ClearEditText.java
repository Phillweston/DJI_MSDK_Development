package com.flycloud.autofly.ux.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.flycloud.autofly.ux.R;


public class ClearEditText extends AppCompatEditText {
    private static final float DEFAUT_SCALE = 1.0f;
    private Bitmap mClearBitmap;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mBitWidth;
    private int mBitHeight;
    private boolean showClose;
    private float scale;
    private float paddingVertical;
    private float paddingHorizontal;
    private float mDrawWidth;

    public ClearEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int clearIcon = 0;
        if ( attrs != null ) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClearEditText);
            try {

                clearIcon = a.getResourceId(R.styleable.ClearEditText_clearIcon, 0);
                scale = a.getFloat(R.styleable.ClearEditText_scaleSize, 0);
                mDrawWidth = a.getDimension(R.styleable.ClearEditText_drawableWidth, 0.0f);
                paddingHorizontal = a.getDimension(R.styleable.ClearEditText_drawablePaddingHorizontal, 0.0f);
            } finally { //回收这个对象
                a.recycle();
            }
        }

        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        if ( clearIcon != 0 ) {
            mClearBitmap = BitmapFactory.decodeResource(getResources(), clearIcon, bfoOptions);
        } else
            mClearBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ux_ic_clear, bfoOptions);

        if ( scale == 0 ) {
            scale = DEFAUT_SCALE;
        }

        mPaint = new Paint();

        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showClose = !TextUtils.isEmpty(s);
                invalidate();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( hasFocus ) {
                    if ( !TextUtils.isEmpty(getText().toString()) ) {
                        showClose = true;
                    }else{
                        showClose = false;
                    }
                }else{
                    showClose = false;
                }
                invalidate();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ( event.getAction() == MotionEvent.ACTION_UP ) {

            if ( showClose && event.getX() > (getWidth() - getHeight() + paddingVertical)
                    && event.getX() < (getWidth() - paddingHorizontal)
                    && event.getY() > paddingHorizontal
                    && event.getY() < (getHeight() - paddingVertical) ) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ( showClose ) {
            Rect mSrcRect = new Rect(0, 0, mBitWidth, mBitWidth);
            RectF mDestRect = new RectF(mWidth - mBitWidth - paddingHorizontal,
                    paddingVertical, mWidth - paddingHorizontal, mHeight - paddingVertical);
            canvas.drawBitmap(mClearBitmap, mSrcRect, mDestRect, mPaint);
        }
    }

    private boolean hasScale;

    private void deal() {

            int width = mClearBitmap.getWidth();
            int height = mClearBitmap.getHeight();
            Log.e("HHHHH", "width height " + width + " " + height);

            if ( mDrawWidth == 0 ) {
                paddingVertical = (( float ) mHeight) * (1 - scale) / 2;
                mBitWidth = ( int ) (mHeight - 2 * paddingVertical);
                mBitHeight = mBitWidth;
            } else {
                if ( mDrawWidth > mHeight ) {
                    mDrawWidth = mHeight;
                }
                paddingVertical = (( float ) (mHeight - mDrawWidth)) / 2;
                mBitWidth = ( int ) mDrawWidth;
                mBitHeight = ( int ) mDrawWidth;
            }

            float scaleWidth = (( float ) mBitWidth) / width;
            float scaleHeight = (( float ) mBitHeight) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            mClearBitmap = Bitmap.createBitmap(mClearBitmap, 0, 0, width, height, matrix, true);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;


        deal();

    }
}
