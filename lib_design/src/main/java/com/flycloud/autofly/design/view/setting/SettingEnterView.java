package com.flycloud.autofly.design.view.setting;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flycloud.autofly.R;


public class SettingEnterView extends BaseSettingView {


    private TextView mEnterResultTv;

    public SettingEnterView(Context context) {
        this(context, null);
    }

    public SettingEnterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingEnterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public int setRootView() {
        return R.layout.view_setting_enter;
    }

    @Override
    public void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        mEnterResultTv = findViewById(R.id.tv_enter_result);
    }

    /**
     * 设置右边结果显示小文字
     * @param result
     */
    public void setEnterResult(String result){
        if (mEnterResultTv != null) {
            mEnterResultTv.setText(result);
        }
    }
}
