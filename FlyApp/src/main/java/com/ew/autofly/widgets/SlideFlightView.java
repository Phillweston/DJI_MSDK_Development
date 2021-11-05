package com.ew.autofly.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ew.autofly.R;



public class SlideFlightView extends View {
    public int currentState;
    public static final int STATE_LOCK = 1;
    public static final int STATE_UNLOCK = 2;
    public static final int STATE_MOVING = 3;
    private Bitmap slideUnlockBackground;
    private Bitmap slideUnlockBlock;
    private int blockBackgoundWidth;
    private int blockWidth;
    private int blockHeight;
    private float x;
    private float y;
    private Canvas canvas;
    private boolean downOnBlock;
    private Paint mPaint ;
    public String mTextColor = "#A9A9A9";
    public boolean isPermitClick = false;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                if (x > 0) {
                    x = x - blockBackgoundWidth * 1.0f / 100;

                    postInvalidate();

                    handler.sendEmptyMessageDelayed(0, 10);
                } else {
                    handler.removeCallbacksAndMessages(null);
                    currentState = STATE_LOCK;
                }
            }
        };
    };
    private OnUnLockListener onUnLockListener;

    /**
     * 自定义View的构造方法
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SlideFlightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        currentState = STATE_LOCK;
        setSlideUnlockBackground(R.drawable.bg_default_slide_flight);
        setSlideUnlockBlock(R.drawable.btn_default_slide_flight);
        postInvalidate();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int screenArg = Math.min(slideUnlockBackground.getWidth(), slideUnlockBackground.getHeight());
        mPaint.setTextSize(screenArg*0.6f);
    }

    public SlideFlightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideFlightView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        canvas.drawBitmap(slideUnlockBackground, 0, 0, null);


            switch (currentState) {
                case STATE_LOCK:
                    drawTextView();
                    canvas.drawBitmap(slideUnlockBlock, 0, 0, null);
                    break;
                case STATE_UNLOCK:
                    if(isPermitClick) {
                        drawTextView();
                        int unlockX = blockBackgoundWidth - blockWidth;
                        canvas.drawBitmap(slideUnlockBlock, unlockX, 0, null);
                    }else{
                        drawTextView();
                        canvas.drawBitmap(slideUnlockBlock, 0, 0, null);
                    }
                    break;
                case STATE_MOVING:
                    if(isPermitClick) {
                        drawTextView();
                        if (x < 0) {
                            x = 0;
                        } else if (x > blockBackgoundWidth - blockWidth) {
                            x = blockBackgoundWidth - blockWidth;
                        }
                        canvas.drawBitmap(slideUnlockBlock, x, 0, null);
                    }else{
                        drawTextView();
                        canvas.drawBitmap(slideUnlockBlock, 0, 0, null);
                    }
                    break;
                default:
                    break;
        }
    }

    private void drawTextView() {
        mPaint.setColor(Color.parseColor(mTextColor));
        float stringWidth = mPaint.measureText("滑 动 起 飞");
        float x = (getWidth() - stringWidth) / 2;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float y = slideUnlockBlock.getHeight() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        canvas.drawText("滑 动 起 飞", x, y, mPaint);
    }

    public void setSlideUnlockBackground(int slideUnlockBackgroundResource) {
        slideUnlockBackground = BitmapFactory.decodeResource(getResources(),
                slideUnlockBackgroundResource);
        blockBackgoundWidth = slideUnlockBackground.getWidth();
    }

    public void setSlideUnlockBlock(int slideUnlockBlockResource) {
        slideUnlockBlock = BitmapFactory.decodeResource(getResources(),
                slideUnlockBlockResource);
        blockWidth = slideUnlockBlock.getWidth();
        blockHeight = slideUnlockBlock.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(slideUnlockBackground.getWidth(),slideUnlockBackground.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (currentState != STATE_MOVING) {
                    x = event.getX();
                    y = event.getY();
                    float blockCenterX = blockWidth * 1.0f / 2;
                    float blockCenterY = blockHeight * 1.0f / 2;
                    downOnBlock = isDownOnBlock(blockCenterX, x, blockCenterY, y);
                    postInvalidate();

                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (downOnBlock && isPermitClick) {
                    x = event.getX();
                    y = event.getY();
                    currentState = STATE_MOVING;
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == STATE_MOVING && isPermitClick) {
                    if (x < blockBackgoundWidth - blockWidth) {
                        handler.sendEmptyMessageDelayed(0, 10);
                        onUnLockListener.setUnLocked(false);
                    } else {
                        currentState = STATE_UNLOCK;
                        onUnLockListener.setUnLocked(true);
                    }
                    downOnBlock = false;
                    postInvalidate();

                }
                break;

            default:
                break;
        }
        return true;
    }

    public boolean isDownOnBlock(float x1, float x2, float y1, float y2) {
        float sqrt = (float) Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
                + Math.abs(y1 - y2) * Math.abs(y1 - y2));
        if (sqrt <= blockWidth / 2) {
            return true;
        }
        return false;
    }

    /**
     * 设置解锁监听
     *
     * @param onUnLockListener
     */
    public void setOnUnLockListener(OnUnLockListener onUnLockListener) {
        this.onUnLockListener = onUnLockListener;
    }

    public interface OnUnLockListener {
        public void setUnLocked(boolean lock);
    }

    
    public void reset() {
        currentState = STATE_LOCK;
        postInvalidate();
    }


    public boolean isOnBackground(int x,int y){
        if(x<=slideUnlockBackground.getWidth()&&y<=slideUnlockBackground.getHeight()){
            return true;
        }
        return false;
    }
}
