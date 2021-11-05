package com.ew.autofly.widgets.ai;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;
import java.util.List;


public class FPVAIOverlayWidget extends View {

    private final float cornerSize = 4;

    private List<RectF> recognitions = new LinkedList<>();

    private final Paint boxPaint = new Paint();

    public FPVAIOverlayWidget(Context context) {
        this(context, null);
    }

    public FPVAIOverlayWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(4.0f);
    }

    public void draw(List<RectF> rectFList) {
        this.recognitions.clear();
        for (RectF rectF : rectFList) {
            this.recognitions.add(new RectF(getWidth() * rectF.left, getHeight() * rectF.top,
                    getWidth() * rectF.right, getHeight() * rectF.bottom));
        }
        postInvalidate();
    }

    public void clear() {
        if (this.recognitions.size() > 0) {
            this.recognitions.clear();
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (RectF recognition : recognitions) {
            canvas.drawRoundRect(recognition, cornerSize, cornerSize, boxPaint);
        }

    }
}
