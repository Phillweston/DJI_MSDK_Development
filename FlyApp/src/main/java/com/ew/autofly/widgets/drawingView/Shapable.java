package com.ew.autofly.widgets.drawingView;

import android.graphics.Path;


public interface Shapable {
    public Path getPath();

    public FirstCurrentPosition getFirstLastPoint();

    void setShap(ShapesInterface shape);
}
