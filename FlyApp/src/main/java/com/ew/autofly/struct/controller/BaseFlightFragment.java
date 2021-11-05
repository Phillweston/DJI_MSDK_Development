package com.ew.autofly.struct.controller;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.bulong.rudeness.RudenessScreenHelper;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.base.BaseMvpFragment;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.model.ConnectManager;
import com.ew.autofly.model.UserManager;
import com.ew.autofly.struct.presenter.BaseFlightPresenterImpl;
import com.ew.autofly.struct.view.IBaseFlightView;
import com.flycloud.autofly.base.framework.rx.RxManager;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

import java.util.concurrent.TimeUnit;

import dji.common.error.DJIError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public abstract class BaseFlightFragment<V extends IBaseFlightView, P extends BaseFlightPresenterImpl<V>> extends BaseMvpFragment<V, P> implements IBaseFlightView, ConnectManager.OnProductConnectListener {

    protected Handler mUIThreadHandler = new Handler(Looper.getMainLooper());

    protected RxManager mRxManager = new RxManager();

    protected BaseProgressDialog mLoadProgressDlg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RudenessScreenHelper.resetDensity(getBaseContext(), AppConstant.DESIGN_RESOLUTION_WIDTH);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RudenessScreenHelper.resetDensity(getContext(), AppConstant.DESIGN_RESOLUTION_WIDTH);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConnectManager.getInstance().register(this);
        setUpKeys();
        try {
            EventBus.getDefault().register(this);
        } catch (EventBusException e) {

        }
        loginDjiAccountDelay();
    }

    @Override
    public void onDestroy() {
        ConnectManager.getInstance().unRegister(this);
        tearDownKeys();
        mPresenter.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (EventBusException e) {

        }

        mRxManager.clear();

        super.onDestroy();
    }

    @Override
    public boolean isTaskCanTerminate() {
        return true;
    }

    @Override
    public void onProductDisconnect() {

    }

    @Override
    public void onProductConnect(BaseProduct baseProduct) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                loginDjiAccount(false);
            }
        });
    }

    @Override
    public void onProductComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent newComponent, boolean isConnected) {

    }

    @Override
    public void onLoginDjiAccountCallback(boolean isSuccess) {

    }

    @Override
    public void showToastDialog(String toast) {
        showToastDialog(toast, null);
    }

    @Override
    public void showToastDialog(String toast, DialogInterface.OnClickListener clickListener) {
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
        deleteDialog.setTitle(getString(R.string.notice))
                .setMessage(toast)
                .setPositiveButton(getString(R.string.sure), clickListener)
                .setNegativeButton(getActivity().getString(R.string.cancle), clickListener)
                .create()
                .show();
    }

    @Override
    public void showToast(String toast) {
        ToastUtil.show(getContext(), toast);
    }

    @Override
    public void showLoading(boolean isShow, String loadingMsg) {
        if (isShow) {
            showLoadProgressDialog(loadingMsg);
        } else {
            dismissLoadProgressDialog();
        }
    }

    @Override
    public void showError(boolean isShow, String errorMsg) {

    }

    @Override
    public void showEmpty(boolean isShow, String emptyMsg) {

    }

    public void showLoadProgressDialog(@Nullable String message) {
        if (mLoadProgressDlg == null) {
            mLoadProgressDlg = new BaseProgressDialog(getContext());
            mLoadProgressDlg.setCancelable(false);
        }
        mLoadProgressDlg.setMessage(message);
        mLoadProgressDlg.show();
    }

    public void dismissLoadProgressDialog() {
        if (mLoadProgressDlg != null && mLoadProgressDlg.isShowing()) {
            mLoadProgressDlg.dismiss();
        }
    }

    public void loginDjiAccountDelay() {
        Disposable disposable = Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        loginDjiAccount(false);
                    }
                });

        mRxManager.add(disposable);
    }

    public void loginDjiAccount(boolean recall) {
        if (getActivity() != null && AircraftManager.isAircraftConnected()) {
            UserManager.getInstance().logIntoDJIUserAccount(getActivity(),
                    new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                        @Override
                        public void onSuccess(final UserAccountState userAccountState) {
                            onLoginDjiAccountCallback(true);
                            showToast(EWApplication.getInstance().getString(R.string.message_login_to_dji_success));
                        }

                        @Override
                        public void onFailure(DJIError error) {
                            onLoginDjiAccountCallback(false);
                            showToast(EWApplication.getInstance().getString(R.string.message_login_to_dji_fail));
                        }
                    }, recall);
        }
    }

    @Override
    public void setUpKeys() {

    }

    @Override
    public void tearDownKeys() {

    }

    @Override
    public void toggleViewVisible(View view) {
        if (view != null) {
            view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }
}
