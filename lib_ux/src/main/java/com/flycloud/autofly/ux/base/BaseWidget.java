package com.flycloud.autofly.ux.base;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;


public abstract class BaseWidget extends FrameLayout implements IBaseWidget {

    public BaseWidget(Context context) {
        this(context, null);
    }

    public BaseWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(setRootView(), this);
        initView(context,attrs,defStyleAttr);
    }
}
