package com.flycloud.autofly.base.util;

import android.content.Context;

import android.widget.Toast;


public class ToastUtil {


    public static void show(Context context, int info) {
        show(context, context.getString(info));
    }

    public synchronized static void show(Context context, String info) {
        if(context==null){
            return;
        }
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    public synchronized static void showLongTime(Context context, String info) {
        if(context==null){
            return;
        }
        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
    }

}

