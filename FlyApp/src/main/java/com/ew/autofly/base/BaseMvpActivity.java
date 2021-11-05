package com.ew.autofly.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.WindowManager;

import com.flycloud.autofly.base.base.BaseFragmentActivity;




public abstract class BaseMvpActivity <V, P extends BaseMvpPresenter<V>> extends BaseFragmentActivity{

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract P createPresenter();
}
