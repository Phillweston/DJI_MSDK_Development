package com.flycloud.autofly.base.widgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flycloud.autofly.base.R;




public class BaseProgressDialog extends Dialog {

    private TextView textView;

    public BaseProgressDialog(Context context) {
        this(context, null);
    }

    public BaseProgressDialog(Context context, String message) {
        this(context, R.style.BaseProgressDialogTheme, message);
    }

    public BaseProgressDialog(Context context, int style, String message) {
        super(context, style);
        setContentView(R.layout.base_dialog_progress);
        textView = (TextView) findViewById(R.id.loading_view_textview);
        setMessage(message);
    }

    public void setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            textView.setText(message);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

}