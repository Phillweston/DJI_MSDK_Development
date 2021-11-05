package com.ew.autofly.model.mission;

import android.content.Context;
import android.content.DialogInterface;

import com.ew.autofly.R;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.internal.common.CheckError;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;


public class CheckConditionManager {

    private Context mContext;

    private List<Observable<Boolean>> mCheckConditionList = new ArrayList<>();
    private Disposable mCheckConditionDisposable;

    public CheckConditionManager(Context context) {
        mContext = context;
    }

    public void startCheck(Observer<Boolean> observer){

        Observable.fromIterable(mCheckConditionList).subscribeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Observable<Boolean>, ObservableSource<Boolean>>() {

                    @Override
                    public ObservableSource<Boolean> apply(Observable<Boolean> observable) throws Exception {
                        return observable;
                    }
                }).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCheckConditionDisposable = d;
              
                if (observer != null) {
                    observer.onSubscribe(d);
                }
            }

            @Override
            public void onNext(Boolean aBoolean) {
              
                cancel();
                if (observer != null) {
                    observer.onNext(aBoolean);
                }
            }

            @Override
            public void onError(Throwable e) {
              
                cancel();
                if (observer != null) {
                    observer.onError(e);
                }
            }

            @Override
            public void onComplete() {
              
                if (observer != null) {
                    observer.onComplete();
                }
            }
        });
    }

    public void cancel() {
        if (mCheckConditionDisposable != null && !mCheckConditionDisposable.isDisposed()) {
            mCheckConditionDisposable.dispose();
        }
        mCheckConditionList.clear();
    }

    protected void addCheckConditionSubscribe(ObservableOnSubscribe<Boolean> subscribe, int index) {
        int size = mCheckConditionList.size();
        int insertIndex = size;
        if (index < size) {
            insertIndex = index;
        }
        mCheckConditionList.add(insertIndex, Observable.create(subscribe));
    }

    public void addCheckConditionSubscribe(ObservableOnSubscribe<Boolean> subscribe) {
        mCheckConditionList.add(Observable.create(subscribe));
    }

    public void addCheckConditionObservable(Observable<Boolean> observable, int index) {
        int size = mCheckConditionList.size();
        int insertIndex = size;
        if (index < size) {
            insertIndex = index;
        }
        mCheckConditionList.add(insertIndex, observable);
    }

    public void addCheckConditionObservable(Observable<Boolean> observable) {
        mCheckConditionList.add(observable);
    }

    /**
     * @param result
     * @param emitter
     */
    public void showCheckResult(CheckError result, ObservableEmitter<Boolean> emitter) {
        showCheckResult(result, emitter, null);
    }

    public void showCheckResult(CheckError result, ObservableEmitter<Boolean> emitter, DialogInterface.OnClickListener clickListener) {
      
        if (result == null) {
            emitter.onComplete();
        } else {

            if (clickListener != null || !result.isIgnoreError()) {
                emitter.onNext(true);
                showAbortDialog(result.getDescription(), clickListener);
            } else {
                showResumeDialog(result.getDescription(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            emitter.onComplete();
                        } else {
                            emitter.onNext(true);
                        }
                    }
                });
            }
        }
    }

    private void showAbortDialog(String toast, DialogInterface.OnClickListener clickListener){
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(mContext);
        deleteDialog.setTitle(mContext.getString(R.string.notice))
                .setMessage(toast)
                .setPositiveButton(mContext.getString(R.string.sure), clickListener)
                .setNegativeButton(mContext.getString(R.string.cancle), clickListener)
                .create()
                .show();
    }

    private void showResumeDialog(String toast, DialogInterface.OnClickListener clickListener) {
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(mContext);
        deleteDialog.setTitle(mContext.getString(R.string.notice))
                .setMessage(toast + " ，是否继续？")
                .setPositiveButton(mContext.getString(R.string.resume), clickListener)
                .setNegativeButton(mContext.getString(R.string.cancle), clickListener)
                .create()
                .show();
    }
}
