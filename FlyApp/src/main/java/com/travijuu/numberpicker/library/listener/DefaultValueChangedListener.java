package com.travijuu.numberpicker.library.listener;

import android.util.Log;

import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;



public class DefaultValueChangedListener implements ValueChangedListener {

    public void valueChanged(float value, ActionEnum action) {

        String actionText = action == ActionEnum.MANUAL ? "manually set" : (action == ActionEnum.INCREMENT ? "incremented" : "decremented");


    }
}
