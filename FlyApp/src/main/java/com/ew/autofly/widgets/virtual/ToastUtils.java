package com.ew.autofly.widgets.virtual;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.ew.autofly.application.EWApplication;



public class ToastUtils {
    private static final int MESSAGE_UPDATE = 1;
    private static final int MESSAGE_TOAST = 2;
    private static Handler mUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
          
            switch ((msg.what)) {
                case MESSAGE_UPDATE:
                    showMessage((Pair<TextView, String>) msg.obj);
                    break;
                case MESSAGE_TOAST:
                    showToast((String) msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private static void showMessage(Pair<TextView, String> msg) {
        if (msg != null) {
            if (msg.first == null) {
                Toast.makeText(EWApplication.getInstance(), "tv is null", Toast.LENGTH_SHORT).show();
            } else {
                msg.first.setText(msg.second);
            }
        }
    }

    public static void showToast(String msg) {
        Toast.makeText(EWApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void setResultToToast(final String string) {
        Message msg = new Message();
        msg.what = MESSAGE_TOAST;
        msg.obj = string;
        mUIHandler.sendMessage(msg);
    }

    public static void setResultToText(final TextView tv, final String s) {
        Message msg = new Message();
        msg.what = MESSAGE_UPDATE;
        msg.obj = new Pair<>(tv, s);
        mUIHandler.sendMessage(msg);
    }
}
