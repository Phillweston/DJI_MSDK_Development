package com.ew.autofly.widgets.switchview;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

import com.ew.autofly.R;



public class SwitchText extends AppCompatTextView implements View.OnClickListener {


    private boolean autoSwitchEnable = true;

    private String offText;

    private String onText;


    private boolean isOn = false;

    private OnSwitchClickListener mOnSwitchListener;

    public SwitchText(Context context) {
        this(context, null);
    }

    public SwitchText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);

        try {
            offText = array.getString(R.styleable.SwitchButton_off_text);
            onText = array.getString(R.styleable.SwitchButton_on_text);
            autoSwitchEnable = array.getBoolean(R.styleable.SwitchButton_auto_switch_enable, true);
        } finally {
            array.recycle();
        }

        switchOff();
        setOnClickListener(this);
    }

    public void setSwitchText(String offText, String onText) {
        this.offText = offText;
        this.onText = onText;
    }

    public void setAutoSwitchEnable(boolean autoSwitchEnable) {
        this.autoSwitchEnable = autoSwitchEnable;
    }


    public void switchOff() {
        setText(offText);
        isOn = false;
    }

    public void switchOn() {
        setText(onText);
        isOn = true;
    }

    @Override
    public void onClick(View v) {

        if (mOnSwitchListener != null) {
            mOnSwitchListener.onSwitchState(isOn);
        }

        if (autoSwitchEnable) {
            if (isOn) {
                switchOff();
            } else {
                switchOn();
            }
        }
    }

    public void setOnSwitchListener(OnSwitchClickListener onSwitchListener) {
        mOnSwitchListener = onSwitchListener;
    }
}
