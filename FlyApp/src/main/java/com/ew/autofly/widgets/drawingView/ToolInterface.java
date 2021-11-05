package com.ew.autofly.widgets.drawingView;


import android.graphics.Canvas;

public interface ToolInterface {
    public void draw(Canvas canvas);

    public void touchDown(float x, float y);

    public void touchMove(float x, float y);

    public void touchUp(float x, float y);

    public boolean hasDraw();
}
