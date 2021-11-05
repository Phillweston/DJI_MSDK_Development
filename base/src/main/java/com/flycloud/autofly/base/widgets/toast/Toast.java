package com.flycloud.autofly.base.widgets.toast;



import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Toast {

    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;

    private Context mcontext;
    private WindowManager windowManager;
    private View mNextView;
    private int mDuration;
    private WindowManager.LayoutParams params;
  
    private static BlockingQueue<Toast> mQueue = new LinkedBlockingQueue<>();
  
    protected static AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private static Handler mHandler = new Handler();

    public Toast(Context context) {
        mcontext = context.getApplicationContext();
        windowManager = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Toast;
        params.y = dip2px(mcontext,64);
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;


    }
    public static Toast makeText(Context context,CharSequence text,int duration){
        Toast result = new Toast(context);
        View view = android.widget.Toast.makeText(context,text, android.widget.Toast.LENGTH_SHORT).getView();
        if (view != null){
            TextView tv = (TextView) view.findViewById(android.R.id.message);
            tv.setText(text);
            Log.e("text",tv.getText().toString());
        }
        result.mNextView = view;
        result.setDuration(duration);
        return result;
    }
    public static Toast makeText(Context context,int resId,int duration) throws Resources.NotFoundException{
        return makeText(context,context.getResources().getText(resId),duration);
    }

    public void show(){
      
        mQueue.offer(this);
      
        if (mAtomicInteger.get() == 0){
            mAtomicInteger.incrementAndGet();
            mHandler.post(mActive);
        }
    }

    /**
     * dip 与px的转换
     * @param context
     * @param dipValue
     * @return
     */
    private int dip2px(Context context,float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dipValue*scale + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Toast setGravity(int gravity, int xOffset, int yOffset) {
        final int finalGravity;
        if (Build.VERSION.SDK_INT >= 14){
            final Configuration configuration = mNextView.getContext().getResources().getConfiguration();
            finalGravity = Gravity.getAbsoluteGravity(gravity,configuration.getLayoutDirection());

        }else {
            finalGravity = gravity;
        }
        params.gravity = finalGravity;
        if ((finalGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL){
            params.horizontalWeight = 1.0f;
        }
        if ((finalGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL){
            params.verticalWeight = 1.0f;
        }
        params.y = yOffset;
        params.x = xOffset;
        return this;
    }

    public Toast setDuration(int duration) {
        if (duration<0){
            this.mDuration = 0;
        }
        if (duration == Toast.LENGTH_SHORT){
            this.mDuration = 1000;
        } else if (duration == Toast.LENGTH_LONG){
            this.mDuration = 2000;
        }else {
            this.mDuration = duration;
        }
        return this;
    }

    public Toast setView(View view) {
        this.mNextView = view;
        return this;
    }

    public Toast setMargin(float horizontalMargin,float verticalMargin){
        params.horizontalMargin = horizontalMargin;
        params.verticalMargin = verticalMargin;
        return this;
    }

    private static void activeQueue(){
        final Toast toast = mQueue.peek();
        if (toast == null){
          
            mAtomicInteger.decrementAndGet();
        }else {
            mHandler.post(toast.mShow);
        }
    }

    private void handleShow(){
        if (mNextView !=null){
            if (mNextView.getParent() != null){
                windowManager.removeView(mNextView);
            }
            windowManager.addView(mNextView,params);
        }
    }

    private  void handleHide(){
        if (mNextView != null){
            if (mNextView.getParent() != null){
                windowManager.removeView(mNextView);
                mQueue.poll();
            }
            mNextView = null;
        }
    }

    private final static Runnable mActive  = new Runnable() {
        @Override
        public void run() {
            activeQueue();
        }
    };
    private final Runnable mShow = new Runnable() {
        @Override
        public void run() {
            handleShow();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    handleHide();
                }
            },mDuration);
            new Handler().postDelayed(mActive,mDuration);
        }
    };
}


