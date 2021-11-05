package com.travijuu.numberpicker.library.listener;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.NumberPicker;



public class DefaultOnEditorActionListener implements TextView.OnEditorActionListener {

    NumberPicker layout;

    public DefaultOnEditorActionListener(NumberPicker layout) {
        this.layout = layout;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            try {
                String symbol = layout.getSymbol();

                String text = v.getText().toString();
                int endIndex = text.lastIndexOf(symbol);
                if (endIndex > -1) {
                    text = text.substring(0, endIndex);
                }
                float value = Float.parseFloat(text);

                if (value > layout.getMax()) {
                    value = layout.getMax();
                } else if (value < layout.getMin()) {
                    value = layout.getMin();
                }

                layout.setValue(value);

                if (layout.getValue() == value) {
                    layout.getValueChangedListener().valueChanged(value, ActionEnum.MANUAL);
                    return false;
                }
            } catch (NumberFormatException e) {
                layout.refresh(false);
            }
        }
        return true;
    }
}
