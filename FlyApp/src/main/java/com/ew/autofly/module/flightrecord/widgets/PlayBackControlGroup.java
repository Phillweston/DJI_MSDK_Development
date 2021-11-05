package com.ew.autofly.module.flightrecord.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.flycloud.autofly.base.util.DensityUtils;


public class PlayBackControlGroup extends ViewGroup{

    private PlayBackControlView mPlayBackControlView;

    private int mWidth;

    private int mHeight;

    private float smallWidth;

    private float smallHeight;

    private TextView mLeftTv;

    private TextView mRightTv;

    private TextView mTopTv;

    private TextView mBottomTv;

    private TextView mCenterTv;

    private float mDefaultPadding;

    private ImageView mCenterPointIv;

    private ImageButton a;

    private Context mContext;

    private int mVerticalNum = 0;

    private int mHorizontalNum = 0;



    public PlayBackControlGroup(Context context) {
        this(context,null);
    }

    public PlayBackControlGroup(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayBackControlGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {

        smallWidth = DensityUtils.dip2px(mContext,120);
        smallHeight = DensityUtils.dip2px(mContext,120);
        mDefaultPadding = DensityUtils.dip2px(mContext,6);

        LayoutParams params;
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mPlayBackControlView = new PlayBackControlView(getContext());
        addView(mPlayBackControlView,params);


        mTopTv = new TextView(getContext());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTopTv.setText("0%");
        mTopTv.setTag("top");
        mTopTv.setGravity(Gravity.CENTER);
        mTopTv.setTextColor(getResources().getColor(R.color.white));
        addView(mTopTv,params);


        mBottomTv = new TextView(getContext());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mBottomTv.setText("0%");
        mBottomTv.setTag("bottom");
        mBottomTv.setTextColor(getResources().getColor(R.color.white));
        addView(mBottomTv,params);


        mLeftTv = new TextView(getContext());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLeftTv.setText("0%");
        mLeftTv.setTag("left");
        mLeftTv.setTextColor(getResources().getColor(R.color.white));
        addView(mLeftTv,params);


        mRightTv = new TextView(getContext());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mRightTv.setText("0%");
        mRightTv.setTag("right");
        mRightTv.setTextColor(getResources().getColor(R.color.white));
        addView(mRightTv,params);


        mCenterPointIv = new ImageView(getContext());
        params = new LayoutParams((int) mPlayBackControlView.getRectWidth()*2, (int) mPlayBackControlView.getRectWidth()*2);
        mCenterPointIv.setTag("center");
        mCenterPointIv.setBackgroundResource(R.drawable.my_flight_playerrunner);
        addView(mCenterPointIv,params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int w = measureWidth(widthMeasureSpec);
        int h = measureHeight(heightMeasureSpec);
        setMeasuredDimension(w, h);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        int childCout = getChildCount();
        for (int i = 0; i < childCout; i++) {
            View view = getChildAt(i);
            if (view instanceof PlayBackControlView){
                view.measure(mWidth/3*2,mHeight/3*2);
            }else {
                view.measure(w, h);
            }
        }

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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int ChildCout = getChildCount();
        for (int i = 0; i < ChildCout; i++) {
            View child = getChildAt(i);
            if (child instanceof PlayBackControlView) {
                child.layout(mWidth / 2 - child.getMeasuredWidth() / 2, mHeight / 2 - child.getMeasuredHeight()/2, mWidth / 2 + child.getMeasuredWidth() / 2, mHeight / 2 + child.getMeasuredHeight() / 2);
            }else {
                if (child.getTag().equals("top")) {
                    child.layout(mWidth/2-child.getMeasuredWidth()/2,(int)mDefaultPadding,mWidth/2+child.getMeasuredWidth()/2,(int)mDefaultPadding+child.getMeasuredHeight());
                } else if (child.getTag().equals("bottom")) {
                    child.layout(mWidth/2-child.getMeasuredWidth()/2,mHeight-(int)mDefaultPadding-child.getMeasuredHeight(),mWidth/2+child.getMeasuredWidth()/2,mHeight-(int)mDefaultPadding);
                } else if (child.getTag().equals("left")) {
                    child.layout((int)mDefaultPadding,mHeight/2-child.getMeasuredHeight()/2,(int)mDefaultPadding+child.getMeasuredWidth(),mHeight/2+child.getMeasuredHeight()/2);
                } else if (child.getTag().equals("right")) {
                    child.layout(mWidth - (int) mDefaultPadding - child.getMeasuredWidth(), mHeight / 2 - child.getMeasuredHeight() / 2, mWidth - (int) mDefaultPadding + child.getMeasuredWidth() / 2, mHeight / 2 + child.getMeasuredHeight() / 2);
                } else if (child.getTag().equals("center")) {
                    LayoutParams params = child.getLayoutParams();
                    child.layout(mWidth/2-params.width/2,mHeight/2-params.height/2,mWidth/2+params.width/2,mHeight/2+params.height/2);
                }
            }
        }
    }


    public void changeControlNum(int vertical , int horizontal) {
        mPlayBackControlView.changeControlNum(vertical,horizontal);
        mPlayBackControlView.invalidate();

        setTopText(0+"");
        setBottomText(0+"");
        setLeftText(0+"");
        setRightText(0+"");

        if (vertical >= 0){

            setTopText(vertical+"");
        } else if (vertical < 0) {

            setBottomText(-vertical+"");
        }

        if (horizontal >= 0) {

            setRightText(horizontal+"");
        } else if (horizontal < 0) {

            setLeftText(-horizontal+"");
        }

    }

    private void setTopText(String topText){
        mTopTv.setText(topText+"%");
    }

    private void setLeftText(String leftText) {
        mLeftTv.setText(leftText+"%");
    }

    private void setRightText(String rightText) {
        mRightTv.setText(rightText+"%");
    }

    private void setBottomText(String bottomText) {
        mBottomTv.setText(bottomText+"%");
    }


}
