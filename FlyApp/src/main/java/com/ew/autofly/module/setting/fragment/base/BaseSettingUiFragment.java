package com.ew.autofly.module.setting.fragment.base;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.module.setting.event.ExitMainSettingEvent;
import com.flycloud.autofly.base.base.BaseFragment;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;
import com.flycloud.autofly.design.view.setting.SettingCheckView;

import org.greenrobot.eventbus.EventBus;


public class BaseSettingUiFragment extends BaseFragment {

    protected BaseProgressDialog mLoadProgressDlg;

    protected Handler mUIThreadHandler = new Handler(Looper.getMainLooper());

    protected void showToast(final String toast) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(EWApplication.getInstance(), toast);
            }
        });

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

    protected void setCheckedNoEvent(final SettingCheckView settingCheckView, final boolean check) {
        if (settingCheckView != null) {
            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    settingCheckView.setCheckedNoEvent(check);
                }
            });
        }
    }

    
    protected void exitMainSetting(){
        EventBus.getDefault().post(new ExitMainSettingEvent());
    }

    protected Resources getAppResources(){
        return EWApplication.getInstance().getResources();
    }

}
