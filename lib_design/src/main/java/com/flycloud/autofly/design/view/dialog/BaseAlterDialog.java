package com.flycloud.autofly.design.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flycloud.autofly.R;
import com.flycloud.autofly.ux.base.BaseUxDialog;
import com.flycloud.autofly.ux.interfaces.OnDialogClickListener;


public abstract class BaseAlterDialog extends BaseUxDialog implements View.OnClickListener {

    public final int THEME_BLACK = 0;
    public final int THEME_BLACK_TRANSPARENT = 1;
    public final int THEME_DARK = 2;

    private TextView mTvCancel;
    private View mLineBottom;
    private TextView mTvOk;
    private TextView mTvTitle;
    private FrameLayout mLayoutContent;
    private RelativeLayout mLayoutTitle;
    private View mBottomLy;

    private View mPanel;


    private int theme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.design_dialog_alter, container, false);
        initView(view, inflater);
        initData();
        return view;
    }

    private void initData() {

    }

    protected void initView(View view, LayoutInflater inflater) {
        mTvCancel = view.findViewById(R.id.tv_cancel);
        mLineBottom = view.findViewById(R.id.line_bottom);
        mTvOk = view.findViewById(R.id.tv_ok);
        mTvTitle = view.findViewById(R.id.tv_title);
        mLayoutContent = view.findViewById(R.id.layout_content);
        mLayoutTitle = view.findViewById(R.id.layout_title);
        mBottomLy = view.findViewById(R.id.layout_bottom);

        mTvCancel.setOnClickListener(this);
        mTvOk.setOnClickListener(this);

        View content = inflater.inflate(setContentViewId(), mLayoutContent, false);
        if (content!=null){
            mLayoutContent.addView(content);
        }
        mPanel = view.findViewById(R.id.layout_panel);

        this.theme = setTheme();
        initTheme();
    }

    protected abstract int setContentViewId();


    protected int setTheme() {
        return THEME_BLACK_TRANSPARENT;
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void showNegativeButton(boolean visible) {
        mTvCancel.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible) {
            setBottomLineGone();
        }
        checkButtonVisibility();
    }

    public void showPositiveButton(boolean visible) {
        mTvOk.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible) {
            setBottomLineGone();
        }
        checkButtonVisibility();
    }

    private void setBottomLineGone() {
        mLineBottom.setVisibility(View.GONE);
    }

    private void checkButtonVisibility(){
        boolean hide=mTvCancel.getVisibility()== View.GONE&&mTvOk.getVisibility()==View.GONE;
        mBottomLy.setVisibility(hide ? View.GONE : View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cancel) {
            if (mClickListener != null) {
                mClickListener.onClick(OnDialogClickListener.BUTTON_NEGATIVE, onClickNegative());
            }
            dismiss();
        } else if (id == R.id.tv_ok) {
            if (mClickListener != null) {
                mClickListener.onClick(OnDialogClickListener.BUTTON_POSITIVE, onClickPositive());
            }
            dismiss();
        }
    }

    /**
     * 点击取消
     *
     * @return 返回值
     */
    protected Object onClickNegative() {
        return null;
    }

    /**
     * 点击确定
     *
     * @return 返回值
     */
    protected Object onClickPositive() {
        return null;
    }

    protected OnDialogClickListener mClickListener;

    public void setBottomClickListener(OnDialogClickListener clickListener) {
        mClickListener = clickListener;
    }

    private void initTheme() {
        if (this.theme == THEME_BLACK) {
            mPanel.setBackground(getResources().getDrawable(R.drawable.design_bg_radius_corner_black));
        } else if (this.theme == THEME_BLACK_TRANSPARENT) {
            mPanel.setBackground(getResources().getDrawable(R.drawable.design_bg_radius_corner_black_transparent));
        } else if (this.theme == THEME_DARK) {
            mPanel.setBackground(getResources().getDrawable(R.drawable.design_bg_radius_corner_light));
        }
    }

}
