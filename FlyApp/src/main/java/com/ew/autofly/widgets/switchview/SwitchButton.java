package com.ew.autofly.widgets.switchview;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

import com.ew.autofly.R;



public class SwitchButton extends AppCompatImageButton implements View.OnClickListener {

  
    private boolean autoSwitchEnable=true;

    private int offResId = 0;

    private int onResId = 0;

    private String offText;

    private String onText;

  
    private boolean isOn = false;

    private OnSwitchClickListener mOnSwitchListener;

    public SwitchButton(Context context) {
        this(context,null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);

        try {
            offResId =array.getResourceId(R.styleable.SwitchButton_off_icon,0);
            onResId =array.getResourceId(R.styleable.SwitchButton_on_icon,0);
            offText =array.getString(R.styleable.SwitchButton_off_text);
            onText =array.getString(R.styleable.SwitchButton_on_text);
            autoSwitchEnable=array.getBoolean(R.styleable.SwitchButton_auto_switch_enable,true);
        }finally {
            array.recycle();
        }

        switchOff();
        setOnClickListener(this);
    }

    public void setSwitchIcon(@DrawableRes int uncheckResId, @DrawableRes int checkedResId) {
        this.offResId = uncheckResId;
        this.onResId = checkedResId;
        setImageResource(uncheckResId);
    }

    public void setAutoSwitchEnable(boolean autoSwitchEnable) {
        this.autoSwitchEnable = autoSwitchEnable;
    }

  
    public void switchOff(){
        setImageResource(offResId);

        isOn=false;
    }

    public void switchOn(){
        setImageResource(onResId);

        isOn=true;
    }

    @Override
    public void onClick(View v) {

        if (mOnSwitchListener != null) {
            mOnSwitchListener.onSwitchState(isOn);
        }

        if(autoSwitchEnable){
            if(isOn){
                switchOff();
            }else {
                switchOn();
            }
        }
    }

    public void setOnSwitchListener(OnSwitchClickListener onSwitchListener) {
        mOnSwitchListener = onSwitchListener;
    }

}
