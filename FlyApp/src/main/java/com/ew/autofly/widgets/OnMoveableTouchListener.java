package com.ew.autofly.widgets;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class OnMoveableTouchListener implements View.OnTouchListener {
    private int[] temp = new int[]{0, 0};
    private View mView = null;

    public OnMoveableTouchListener(View view) {
        mView = view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mView == null) {
            return false;
        }
        int action = event.getAction();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                temp[0] = (int) event.getX();
                temp[1] = y - mView.getTop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                break;
            case MotionEvent.ACTION_MOVE:
                int l = x - temp[0];
                int t = y - temp[1];
                int r = x + mView.getWidth() - temp[0];
                int b = y - temp[1] + mView.getHeight();
                mView.layout(l, t, r, b);
                RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                p.leftMargin = l;
                p.topMargin = t - 48;
                mView.setLayoutParams(p);
                break;
        }
        return true;
    }
}
