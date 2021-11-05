package com.ew.autofly.dialog.base;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.interfaces.OnSettingDialogClickListener;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.ux.base.BaseUxDialog;



public class BaseDialogFragment extends BaseUxDialog {

    private ImageView mBackIv;
    private ImageView mCloseIv;
    private TextView mTitleTv;
    private ImageView mIconIv;

    private RelativeLayout mTitleBarLy;

    public void setTitle(String title) {
        if (mTitleTv != null) {
            mTitleTv.setText(title);
        }
    }

    public void setTitleIcon(int resId) {
        if (mIconIv != null) {
            mIconIv.setImageResource(resId);
        }
    }

    /**
     * 显示返回按钮（默认不显示）
     *
     * @param visible
     */
    protected void showBack(boolean visible) {
        if (mBackIv != null) {
            mBackIv.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    protected void showClose(boolean visible) {
        if (mCloseIv != null) {
            mCloseIv.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    protected void onBackPress() {
        dismiss();
    }


    protected void onClosePress() {
        dismiss();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitleBar(view);
    }

    protected void initTitleBar(View rootView) {
        mBackIv = (ImageView) rootView.findViewById(R.id.iv_back);
        mCloseIv = (ImageView) rootView.findViewById(R.id.iv_close);
        mTitleTv = (TextView) rootView.findViewById(R.id.tv_title);
        mIconIv = (ImageView) rootView.findViewById(R.id.iv_icon);
        mTitleBarLy = rootView.findViewById(R.id.rl_title);
        if (mBackIv != null) {
            mBackIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPress();
                }
            });
        }

        if (mCloseIv != null) {
            mCloseIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClosePress();
                }
            });
        }

        showBack(false);

    }

    protected void addTitleView(View view) {
        if (mTitleBarLy != null) {
            mTitleBarLy.addView(view);
        }
    }

    protected OnSettingDialogClickListener mSettingDialogClickListener;

    public void setSettingDialogClickListener(OnSettingDialogClickListener settingDialogClickListener) {
        mSettingDialogClickListener = settingDialogClickListener;
    }

    public IConfirmListener mConfirmListener;

    public void setConfirmListener(IConfirmListener listener) {
        mConfirmListener = listener;
    }

    protected void showToast(final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(EWApplication.getInstance(), msg);
            }
        });
    }
}