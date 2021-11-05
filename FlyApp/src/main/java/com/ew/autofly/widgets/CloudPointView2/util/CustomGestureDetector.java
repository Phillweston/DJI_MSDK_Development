package com.ew.autofly.widgets.CloudPointView2.util;



public class CustomGestureDetector {

    private float amountScale = 1.0f;

    private float amountY = 0.0f;

    private float amountX = 0.0f;

    public float getAmountScale() {
        return amountScale;
    }

    public void setAmountScale(float amountScale) {
        this.amountScale = amountScale;
    }

    public void setScale(float scale, boolean isLockScale) {
        if (getAmountScale() < 1 && isLockScale) {
            setAmountScale(1);
        } else {
            setAmountScale(getAmountScale() - (1 - scale));
        }
        if (getAmountScale() <= 0) {
            setAmountScale(0);
        }
    }

    public float getAmountY() {
        return amountY;
    }

    public void setAmountY(float y) {
        amountY -= y / 200;
    }

    public void setAmountX(float x) {
        amountX += x;
    }

    public float getAmountX() {
        return amountX;
    }
}

