package com.flycloud.autofly.ux.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.flycloud.autofly.base.util.StringUtils;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;
import com.flycloud.autofly.ux.R;

/**
 * ux基础dialog
 * Created by admin on 2016/12/8.
 */

public class BaseUxDialog extends DialogFragment {

    private String tag = getClass().getSimpleName();

    protected BaseProgressDialog mLoadProgressDlg;

    protected Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TransparentDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        setWindowParams();
    }

    protected void setWindowParams() {
        onCreateSize();
        onCreateBackground();
    }

    
    protected void onCreateSize() {
        setSize(0.7f, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    
    protected void setSize(float scaleWidth, float scaleHeight) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            int width;
            int height;
            if (dm.widthPixels > dm.heightPixels) {
                width = scaleWidth > ViewGroup.LayoutParams.MATCH_PARENT ? (int) (dm.widthPixels * scaleWidth) : (int) scaleWidth;
                height = scaleHeight > ViewGroup.LayoutParams.MATCH_PARENT ? (int) (dm.heightPixels * scaleHeight) : (int) scaleHeight;
            } else {
                width = scaleWidth > ViewGroup.LayoutParams.MATCH_PARENT ? (int) (dm.heightPixels * scaleWidth) : (int) scaleWidth;
                height = scaleHeight > ViewGroup.LayoutParams.MATCH_PARENT ? (int) (dm.widthPixels * scaleHeight) : (int) scaleHeight;
            }

            dialog.getWindow().setLayout(width, height);
        }
    }

    
    protected void onCreateBackground() {
        Dialog dialog = getDialog();
        if (dialog != null) {
         /*   dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);*/
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public void showLoadProgressDialog(@Nullable String message) {
        showLoadProgressDialog(message, true);
    }

    public void showLoadProgressDialog(@Nullable String message, boolean isCancelable) {
        if (mLoadProgressDlg == null) {
            mLoadProgressDlg = new BaseProgressDialog(getContext());
            mLoadProgressDlg.setCancelable(isCancelable);
        }
        mLoadProgressDlg.setMessage(message);
        mLoadProgressDlg.show();
    }

    public void dismissLoadProgressDialog() {
        if (mLoadProgressDlg != null && mLoadProgressDlg.isShowing()) {
            mLoadProgressDlg.dismiss();
        }
    }

    protected void showToast(String msg) {
        showToast(getContext(), msg);
    }

    protected void showToast(final Context context, final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(context, msg);
            }
        });
    }

    public void show(FragmentManager manager) {
        super.show(manager, tag + StringUtils.newGUID());
    }

    private DialogInterface.OnDismissListener mOnClickListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mOnClickListener = listener;
    }

  

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnClickListener != null) {
            mOnClickListener.onDismiss(dialog);
        }
    }
}