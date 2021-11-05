package com.ew.autofly.module.weather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.flycloud.autofly.base.util.DensityUtils;

import java.util.List;



public class WeatherLineView extends View{

    private Context mContext;

    
    private  int mViewHeight;

    
    private int mViewWidth;

    
    private float mTextSize=12.0f;

    
    private int mTextColor= Color.WHITE;

    
    private int mLineWidth = 4;

    
    private int mHighTempLineColor=Color.RED;

    
    private int mLowTempLineColor=Color.BLUE;

    
    private TextPaint mTextPaint;


    
    private List<Integer> mHighTemperatures;

    
    private List<Integer> mLowTemperatures;

    
    private int mHighestTemperature;

    
    private int mLowestTemperature;

    
    private float mXInterval;

    
    private float mYInterval;

    
    private Path mHighTemperaturePath;

    
    private Path mLowTemperaturePath;

    
    private Paint mHighTemperaturePaint;

    
    private Paint mLowTemperaturePaint;

    private int mTextPadding =4;

    private int mVerticalPadding=8;

    public WeatherLineView(Context context) {
        this(context,null,0);
    }

    public WeatherLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WeatherLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        initPaint();
    }

    private void initPaint(){

        mHighTemperaturePath=new Path();
        mHighTemperaturePaint=new Paint();
        mHighTemperaturePaint.setStyle(Paint.Style.STROKE);
        mHighTemperaturePaint.setAntiAlias(true);

        mLowTemperaturePath=new Path();
        mLowTemperaturePaint=new Paint();
        mLowTemperaturePaint.setStyle(Paint.Style.STROKE);
        mLowTemperaturePaint.setAntiAlias(true);

        mTextPaint=new TextPaint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);

    }

    private void setValue(){
        mHighTemperaturePaint.setStrokeWidth(mLineWidth);
        mHighTemperaturePaint.setColor(mHighTempLineColor);

        mLowTemperaturePaint.setStrokeWidth(mLineWidth);
        mLowTemperaturePaint.setColor(mLowTempLineColor);

        mTextPaint.setTextSize(DensityUtils.sp2px(mContext,mTextSize));
        mTextPaint.setColor(mTextColor);
    }

    
    public void setTemperatureData(List<Integer> highTemperatures,List<Integer> lowTemperatures){

        this.mHighTemperatures=highTemperatures;
        this.mLowTemperatures=lowTemperatures;

        if(mHighTemperatures!=null &&!mHighTemperatures.isEmpty()
                &&mLowTemperatures!=null&&!mLowTemperatures.isEmpty()
                &&mHighTemperatures.size()==mLowTemperatures.size()){

            mHighestTemperature=highTemperatures.get(0);
            mLowestTemperature=lowTemperatures.get(0);

            for(int highTemperature:mHighTemperatures){
                if(highTemperature>mHighestTemperature){
                    mHighestTemperature=highTemperature;
                }
            }

            for(int lowTemperature:mLowTemperatures){
                if(lowTemperature<mLowestTemperature){
                    mLowestTemperature=lowTemperature;
                }
            }
        }

        invalidate();
    }

    /**
     * 设置高温线颜色
     * @param lineColor
     */
    public void setHighTempLineColor(int lineColor) {
        this.mHighTempLineColor = lineColor;
        invalidate();
    }

    /**
     * 设置低温线颜色
     * @param lineColor
     */
    public void setLowTempLineColor(int lineColor) {
        this.mLowTempLineColor = lineColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setValue();

        if(mHighTemperatures!=null &&!mHighTemperatures.isEmpty()
                &&mLowTemperatures!=null&&!mLowTemperatures.isEmpty()
                &&mHighTemperatures.size()==mLowTemperatures.size()){

            mViewHeight=getHeight()-DensityUtils.sp2px(mContext,mTextSize+ mTextPadding+mVerticalPadding)*2;
            mViewWidth=getWidth()-DensityUtils.sp2px(mContext,mTextSize+ mTextPadding)*2;

            mXInterval=mViewWidth/(mHighTemperatures.size()-1);
            mYInterval=mViewHeight/(mHighestTemperature-mLowestTemperature);

            int size=mHighTemperatures.size();

            for(int i=0;i<size;i++){

                int highTemperature=mHighTemperatures.get(i);
                int lowTemperature=mLowTemperatures.get(i);

                float x=i*mXInterval+DensityUtils.sp2px(mContext,mTextSize);
                float highY=mViewHeight-(highTemperature-mLowestTemperature)*mYInterval
                        +DensityUtils.sp2px(mContext,mTextSize);
                float lowY=mViewHeight-(lowTemperature-mLowestTemperature)*mYInterval
                        +DensityUtils.sp2px(mContext,mTextSize);
                if(i==0){
                    mHighTemperaturePath.moveTo(x,highY);
                    mLowTemperaturePath.moveTo(x,lowY);
                }else {
                    mHighTemperaturePath.lineTo(x,highY);
                    mLowTemperaturePath.lineTo(x,lowY);
                }

                canvas.drawText(highTemperature+"°C",x+DensityUtils.sp2px(mContext, mTextPadding),highY-DensityUtils.sp2px(mContext, mTextPadding), mTextPaint);
                canvas.drawText(lowTemperature+"°C",x+DensityUtils.sp2px(mContext, mTextPadding),lowY+DensityUtils.sp2px(mContext,mTextSize+ mTextPadding), mTextPaint);
            }

            canvas.drawPath(mHighTemperaturePath,mHighTemperaturePaint);
            canvas.drawPath(mLowTemperaturePath,mLowTemperaturePaint);
        }
    }
}
