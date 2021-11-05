package com.ew.autofly.widgets.topbar;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ew.autofly.R;
import com.ew.autofly.event.ui.topbar.DeviceEnableEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class DeviceView extends ImageView{

    public DeviceView(Context context) {
        this(context,null);
    }

    public DeviceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DeviceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceEnableEvent(DeviceEnableEvent event) {
        if (event.isEnable()) {
            setImageResource(R.drawable.icon_wifi_enable);
        } else {
            setImageResource(R.drawable.icon_wifi_disable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }
}
