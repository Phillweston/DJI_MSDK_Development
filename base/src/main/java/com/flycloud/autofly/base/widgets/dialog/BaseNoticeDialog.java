package com.flycloud.autofly.base.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;



public class BaseNoticeDialog extends Dialog{

    public BaseNoticeDialog(@NonNull Context context) {
        super(context);
    }

    public BaseNoticeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

}
