package com.flycloud.autofly.ux.interfaces;


public interface OnDialogClickListener {

    int BUTTON_POSITIVE = -1;

    int BUTTON_NEGATIVE = -2;

    void onClick(int which, Object data);
}
