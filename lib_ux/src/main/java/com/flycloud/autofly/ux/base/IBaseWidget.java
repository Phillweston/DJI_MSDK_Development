package com.flycloud.autofly.ux.base;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;


public interface IBaseWidget {

    /**
     * 设置布局
     *
     * @return @resId
     */
    int setRootView();

    void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr);

}
