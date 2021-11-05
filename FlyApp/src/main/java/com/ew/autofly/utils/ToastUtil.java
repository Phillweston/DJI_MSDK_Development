package com.ew.autofly.utils;

import android.content.Context;
import android.widget.Toast;


@Deprecated
public class ToastUtil {


    public static void show(Context context, int info) {
        show(context, context.getString(info));
    }

    public synchronized static void show(Context context, String info) {
        if(context==null){
            return;
        }

        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();

       /* if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        mToast.show();
*/
      
    }

   /* public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = null;
    }*/
}
