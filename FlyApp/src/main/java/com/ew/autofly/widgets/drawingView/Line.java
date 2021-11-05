package com.ew.autofly.widgets.drawingView;

import android.graphics.Canvas;
import android.graphics.Paint;


public class Line extends ShapeAbstract {
    public Line(Shapable paintTool) {
        super(paintTool);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);
        canvas.drawLine(x1, y1, x2, y2, paint);
    }
}
