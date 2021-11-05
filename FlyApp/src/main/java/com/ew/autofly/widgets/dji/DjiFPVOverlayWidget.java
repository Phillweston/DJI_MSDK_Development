package com.ew.autofly.widgets.dji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import dji.ux.widget.FPVOverlayWidget;




public class DjiFPVOverlayWidget extends FPVOverlayWidget {

    private boolean isTouchable = true;

    public DjiFPVOverlayWidget(Context context) {
        super(context);
    }

    public DjiFPVOverlayWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DjiFPVOverlayWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setTouchable(boolean touchable) {
        isTouchable = touchable;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (isTouchable) {
            return super.onTouch(view, motionEvent);
        }
        return false;
    }
}
