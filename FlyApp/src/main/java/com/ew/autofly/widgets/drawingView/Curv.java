package com.ew.autofly.widgets.drawingView;

import android.graphics.Canvas;
import android.graphics.Paint;


public class Curv extends ShapeAbstract {


    public Curv(Shapable paintTool) {
        super(paintTool);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);
        canvas.drawPath(mPath, paint);
    }

}
