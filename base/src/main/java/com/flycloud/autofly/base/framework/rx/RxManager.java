package com.flycloud.autofly.base.framework.rx;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class RxManager {

    private CompositeDisposable mRxDisposable = new CompositeDisposable();

    public void add(Disposable disposable) {

        try {
            if (disposable != null) {
                mRxDisposable.add(disposable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(Disposable disposable) {
        try {
            if (disposable != null) {
                mRxDisposable.remove(disposable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        mRxDisposable.clear();
    }
}
