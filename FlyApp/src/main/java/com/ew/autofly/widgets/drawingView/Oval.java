package com.ew.autofly.widgets.drawingView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;


public class Oval extends ShapeAbstract {


    public Oval(Shapable paintTool) {
        super(paintTool);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);
        RectF rectF = new RectF(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
        canvas.drawOval(rectF, paint);
    }

}
