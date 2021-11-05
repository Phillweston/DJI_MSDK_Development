package com.flycloud.autofly.design.view.setting;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flycloud.autofly.R;


public class SettingTextView extends BaseSettingView {

    private TextView mTvSet;
    private String mText;

    public SettingTextView(Context context) {
        super(context);
    }

    public SettingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initXmlConfigAttr(AttributeSet attrs) {
        super.initXmlConfigAttr(attrs);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SettingTextView);

        mText = ta.getString(R.styleable.SettingTextView_design_stv_text);

        ta.recycle();
    }

    @Override
    public int setRootView() {
        return R.layout.view_setting_text;
    }

    @Override
    public void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        mTvSet = findViewById(R.id.tv_set);
        if (!TextUtils.isEmpty(mText)) {
            setText(mText);
        }
    }

    public void setText(String text) {
        mTvSet.setText(text);
    }

    public String getText() {
        return mTvSet.getText().toString();
    }

    public TextView getTextView(){
        return mTvSet;
    }
}
