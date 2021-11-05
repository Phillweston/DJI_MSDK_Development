package com.travijuu.numberpicker.library.listener;

import android.view.View;
import android.widget.EditText;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.NumberPicker;


public class ActionListener implements View.OnClickListener {

    NumberPicker layout;
    ActionEnum action;
    EditText display;

    public ActionListener(NumberPicker layout, EditText display, ActionEnum action) {
        this.layout = layout;
        this.action = action;
        this.display = display;
    }

    @Override
    public void onClick(View v) {
        try {

            String symbol = layout.getSymbol();

            String text = display.getText().toString();
            int endIndex = text.lastIndexOf(symbol);
            if (endIndex > -1) {
                text = text.substring(0, endIndex);
            }

            float newValue = Float.parseFloat(text);

            if (!this.layout.valueIsAllowed(newValue)) {
                return;
            }

            this.layout.setValue(newValue);
        } catch (NumberFormatException e) {
            this.layout.refresh(false);
        }

        switch (this.action) {
            case INCREMENT:
                this.layout.increment();
                break;
            case DECREMENT:
                this.layout.decrement();
                break;
        }
    }
}