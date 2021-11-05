package com.ew.autofly.widgets.drawingView;

import android.graphics.Canvas;
import android.graphics.Paint;


public class Rectangle extends ShapeAbstract {
    public Rectangle(Shapable paintTool) {
        super(paintTool);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);
        canvas.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), paint);
    }

}
