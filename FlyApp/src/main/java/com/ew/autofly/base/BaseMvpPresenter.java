package com.ew.autofly.base;

import android.content.Context;
import android.content.res.Resources;

import com.ew.autofly.application.EWApplication;

import java.lang.ref.WeakReference;



public class BaseMvpPresenter<V> {

    /**
     * 判断是否与View建立了关联,view是否存在（已销毁返回null）
     * @return
     */
    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    public Context getAppContext() {
        return EWApplication.getInstance();
    }

    public String getString(int resId) {
        return getAppContext().getString(resId);
    }

    public Resources getResources() {
        return getAppContext().getResources();
    }


    private WeakReference<V> viewRef;


    public void attachView(V view) {
        viewRef = new WeakReference<V>(view);
    }

    public void onViewCreated(){}


    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }


    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }
}
