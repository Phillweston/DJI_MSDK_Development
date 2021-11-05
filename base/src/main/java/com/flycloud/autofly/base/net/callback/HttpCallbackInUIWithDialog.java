package com.flycloud.autofly.base.net.callback;

import android.content.Context;

import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;



public class HttpCallbackInUIWithDialog<T> extends HttpCallbackInUI<T> {
    private BaseProgressDialog waitingDlg;
    private Context context;
    private String message;

    public Context getContext() {
        return context;
    }

    public HttpCallbackInUIWithDialog(Context context) {
        this(context, null);
    }

    public HttpCallbackInUIWithDialog(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (context != null) {
            waitingDlg = new BaseProgressDialog(context, message);
            waitingDlg.show();
        }
    }

    public void onSuccess(T result) {
        if (waitingDlg != null) {
            waitingDlg.dismiss();
        }
    }

    public void onError(Exception e) {
        if (waitingDlg != null) {
            waitingDlg.dismiss();
        }
    }
}
