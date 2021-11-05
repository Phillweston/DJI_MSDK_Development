package com.flycloud.autofly.design.view.setting;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.flycloud.autofly.R;
import com.kyleduo.switchbutton.SwitchButton;;



public class SettingCheckView extends BaseSettingView {

    private SwitchButton mSbCheck;

    public SettingCheckView(Context context) {
        this(context, null);
    }

    public SettingCheckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int setRootView() {
        return R.layout.view_setting_check;
    }

    @Override
    public void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        mSbCheck = findViewById(R.id.sb_check);
        mSbCheck.setAnimationDuration(0);
    }

    public void setCheck(boolean isCheck) {
        mSbCheck.setChecked(isCheck);
    }

    public boolean isChecked(){
       return mSbCheck.isChecked();
    }

    public void setCheckedNoEvent(boolean isCheck) {
        mSbCheck.setCheckedImmediatelyNoEvent(isCheck);
    }

    public void setOnCheckedChangeListener(SwitchButton.OnCheckedChangeListener l) {
        mSbCheck.setOnCheckedChangeListener(l);
    }

}
