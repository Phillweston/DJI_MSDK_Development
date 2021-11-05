package com.ew.autofly.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.ew.autofly.R;

import java.util.ArrayList;

/**
 * 简介:
 * <p/>
 * Created by ChenGuang on 2014/6/23 0023
 */
public class DebugWindow {
    private static DebugWindow mInstance = null;
    private WindowManager windowManager = null;
    private RelativeLayout mLayout = null;
    private ListView mListView = null;
    private ArrayAdapter mAdapter = null;
    private ArrayList<String> debugData = new ArrayList<String>();
    private Handler mHandler = null;

    private final int Handler_NotifyDataSetChanged = 0x001;

    public synchronized void addDebugData(String log) {
        if (mAdapter == null || log == null) return;
        mHandler.sendMessage(mHandler.obtainMessage(Handler_NotifyDataSetChanged, log));
    }

    public void clearDebugData() {
        if (mAdapter == null) return;
        mHandler.removeCallbacksAndMessages(null);
        debugData.clear();
        mAdapter.notifyDataSetChanged();
    }

    private DebugWindow() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case Handler_NotifyDataSetChanged:
                        debugData.add(0, msg.obj.toString());
                        mAdapter.notifyDataSetChanged();
                        mListView.setSelection(0);
                        break;
                }
                return false;
            }
        });
    }

    public static DebugWindow getInstance() {
        if (mInstance == null) {
            mInstance = new DebugWindow();
        }
        return mInstance;
    }

    public boolean isShow() {
        return windowManager != null;
    }

    public void showDebugWindow(Context ctx) {
        Context context = ctx.getApplicationContext();
        dismissDebugWindow();

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        windowParams.gravity = Gravity.LEFT;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayout = new RelativeLayout(context);
        mLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        mLayout.setBackgroundColor(Color.parseColor("#40000000"));

        mListView = new ListView(context);
        mAdapter = new ArrayAdapter(context, R.layout.debug_window_item, debugData);
        mListView.setAdapter(mAdapter);

        mLayout.addView(mListView);

        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(mLayout, windowParams);
    }

    public void dismissDebugWindow() {
        if (windowManager != null) {
            windowManager.removeViewImmediate(mLayout);
            mListView = null;
            mAdapter.clear();
            mAdapter = null;
            mLayout = null;
            windowManager = null;
        }
    }
}
