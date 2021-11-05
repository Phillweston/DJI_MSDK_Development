package com.ew.autofly.widgets.dji;

import android.content.Context;
import android.util.AttributeSet;

import dji.ux.widget.FPVWidget;


public class DjiVideoWidget extends FPVWidget {

    public DjiVideoWidget(Context context) {
        super(context);
    }

    public DjiVideoWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DjiVideoWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public void initView(Context context, AttributeSet attributeSet, int i) {
        super.initView(context, attributeSet, i);
        if (!this.isInEditMode()) {
            
            this.calculator.setListener(null);
        }
    }
}
