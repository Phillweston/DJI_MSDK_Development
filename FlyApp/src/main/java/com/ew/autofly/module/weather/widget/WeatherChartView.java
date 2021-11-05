package com.ew.autofly.module.weather.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;



public class WeatherChartView extends View {

    Paint chartLinePaint;
    Paint chartGradientPaint;

    Paint yLinePaint;

    float gridX, gridY, xSpace = 0, ySpace = 0, spaceYT = 0.005f;
    int xCount=120, yCount=100;

    List<Float> dataY = null;

    private int mLineColor=Color.BLUE;

    private int mYLineColor=Color.GRAY;

    public WeatherChartView(Context context) {
        this(context, null);
    }

    public WeatherChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    
    public void refreshView(List<Float> dataY){
        this.dataY = dataY;
        invalidate();
    }

    public void setLineColor(int lineColor) {
        this.mLineColor = lineColor;
        invalidate();
    }

    public void setYLineColor(int yLineColor) {
        this.mYLineColor = yLineColor;
        invalidate();
    }

    private void initPaint() {
        chartLinePaint = new Paint();
        chartGradientPaint = new Paint();


        chartLinePaint.setStyle(Paint.Style.STROKE);

        CornerPathEffect cornerPathEffect = new CornerPathEffect(200);
        chartLinePaint.setPathEffect(cornerPathEffect);
        chartLinePaint.setAntiAlias(true);


        chartGradientPaint.setStyle(Paint.Style.FILL);
        chartGradientPaint.setAntiAlias(true);


        yLinePaint = new Paint();
        yLinePaint.setStyle(Paint.Style.STROKE);
        yLinePaint.setStrokeWidth(1);
    }

    private void setValue(){
        chartLinePaint.setStrokeWidth(5);
        chartLinePaint.setColor(mLineColor);

        yLinePaint.setColor(mYLineColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setValue();


        gridX = 0;
        gridY = getHeight();

        xSpace = (getWidth() - gridX) / xCount;
        ySpace = gridY / yCount;

        canvas.drawLine(gridX,0,getWidth(),0,yLinePaint);
        canvas.drawLine(gridX,gridY/3,getWidth(),gridY/3,yLinePaint);
        canvas.drawLine(gridX,2*gridY/3,getWidth(),2*gridY/3,yLinePaint);
        canvas.drawLine(gridX,gridY,getWidth(),gridY,yLinePaint);

        if (dataY != null && dataY.size() > 0) {
            float currentPointX;
            float currentPointY;

            
            Path curvePath = new Path();
            
            Path gradientPath = new Path();

            for (int m = 0; m < dataY.size(); m++) {
                currentPointX = m * xSpace + gridX;

                currentPointY = gridY- (dataY.get(m)/spaceYT)*ySpace;

                if(m==0){
                    curvePath.moveTo(currentPointX,currentPointY);
                    gradientPath.moveTo(currentPointX,gridY);
                }else {
                    curvePath.lineTo(currentPointX,currentPointY);
                }

                gradientPath.lineTo(currentPointX,currentPointY);

                if(m==dataY.size()-1){
                    gradientPath.lineTo(currentPointX,gridY);
                }
            }


            canvas.drawPath(curvePath, chartLinePaint);


            Shader mShader = new LinearGradient(0, 0, 0, gridY, new int[]{mLineColor,
                    Color.argb(100, 255, 255, 255)}, null, Shader.TileMode.REPEAT);
            chartGradientPaint.setShader(mShader);
            canvas.drawPath(gradientPath,chartGradientPaint);

        }
    }
}
