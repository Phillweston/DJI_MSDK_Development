package com.ew.autofly.activities;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.WindowManager;

import com.flycloud.autofly.base.base.BaseFragmentActivity;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;

public class BaseActivity extends BaseFragmentActivity {

    protected BaseProgressDialog mLoadProgressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public void showLoadProgressDialog(@Nullable String message) {
        if (mLoadProgressDlg == null) {
            mLoadProgressDlg = new BaseProgressDialog(this);
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
}