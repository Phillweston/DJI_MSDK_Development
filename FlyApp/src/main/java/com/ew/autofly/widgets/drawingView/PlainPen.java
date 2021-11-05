package com.ew.autofly.widgets.drawingView;

import android.graphics.Paint;

public class PlainPen extends PenAbstract implements ToolInterface {
    public PlainPen(int size, int penColor) {
        this(size, penColor, Paint.Style.STROKE);
    }

    public PlainPen(int size, int penColor, Paint.Style style) {
        super(size, penColor, style);
    }

    @Override
    public String toString() {
        return "\tplainPen: " + "\tshap: " + currentShape + "\thasDraw: "
                + hasDraw() + "\tsize: " + penSize + "\tstyle:" + style;
    }
}
