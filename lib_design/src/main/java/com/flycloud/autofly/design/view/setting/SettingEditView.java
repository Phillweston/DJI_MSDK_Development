package com.flycloud.autofly.design.view.setting;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.flycloud.autofly.R;


public class SettingEditView extends BaseSettingView {

    private final int DEFAULT_NO_VALUE = Integer.MIN_VALUE;

    private EditText mEtEdit;
    private TextView mTvValueScale;

    private int maxValue = DEFAULT_NO_VALUE;
    private int minValue = DEFAULT_NO_VALUE;


    public SettingEditView(Context context) {
        super(context);
    }

    public SettingEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int setRootView() {
        return R.layout.view_setting_edit;
    }

    @Override
    public void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        mEtEdit = findViewById(R.id.et_edit);
        mTvValueScale = findViewById(R.id.tv_value_scale);
        initEdit();
    }

    /**
     * 设置范围值
     *
     * @return
     */
    public void setValueScale(int minValue, int maxValue, String unit) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        mTvValueScale.setText(minValue + "~" + maxValue + (unit == null ? "" : unit));
    }

    
    public void setValue(int value) {
        mEtEdit.setText(String.valueOf(value));
    }

    private void initEdit() {

        mEtEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        int value = Integer.valueOf(mEtEdit.getText().toString().trim());
                        if (maxValue != DEFAULT_NO_VALUE && value > maxValue) {
                            value = maxValue;
                        }
                        if (minValue != DEFAULT_NO_VALUE && value < minValue) {
                            value = minValue;
                        }
                        mEtEdit.setText(String.valueOf(value));
                        if (mOnEditListener != null) {
                            mOnEditListener.onDone(value);
                        }
                    } catch (Exception e) {

                    }

                }
                return false;
            }
        });

    }

    private OnEditListener mOnEditListener;

    public void setOnEditListener(OnEditListener onEditListener) {
        mOnEditListener = onEditListener;
    }

    public interface OnEditListener {

        void onDone(int value);
    }
}
