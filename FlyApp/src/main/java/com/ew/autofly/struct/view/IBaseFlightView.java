package com.ew.autofly.struct.view;

import android.content.DialogInterface;
import android.view.View;

import com.ew.autofly.base.IBaseMvpView;


public interface IBaseFlightView extends IBaseMvpView {

    /**
     * 弹出提示对话框
     *
     * @param toast
     */
    void showToastDialog(String toast);

    /**
     * 弹出提示对话框
     *
     * @param toast
     */
    void showToastDialog(String toast, DialogInterface.OnClickListener clickListener);

    
    boolean isTaskCanTerminate();

    /**
     * 登录大疆回调
     *
     * @param isSuccess
     */
    void onLoginDjiAccountCallback(boolean isSuccess);

    void setUpKeys();

    void tearDownKeys();

    /**
     * 控制View的显示
     * @param view
     */
    void toggleViewVisible(View view);
}
