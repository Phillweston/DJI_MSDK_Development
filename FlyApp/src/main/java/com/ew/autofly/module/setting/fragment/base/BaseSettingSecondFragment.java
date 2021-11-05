package com.ew.autofly.module.setting.fragment.base;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public abstract class BaseSettingSecondFragment extends BaseSettingUiFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setRootView(), container, false);
        initView(view);
        return view;
    }

    /**
     * 设置布局
     *
     * @return @resId
     */
    protected abstract int setRootView();

    protected void initView(View rootView){}
}
