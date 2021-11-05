package com.ew.autofly.widgets.component.flight;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ew.autofly.event.ui.topbar.MonitorInfoControlEvent;
import com.ew.autofly.event.ui.topbar.MonitorInfoShowEvent;
import com.ew.autofly.event.ui.topbar.MonitorInfoUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class FlightMonitorInfoWidget extends TextView {


    public FlightMonitorInfoWidget(Context context) {
        this(context,null);
    }

    public FlightMonitorInfoWidget(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlightMonitorInfoWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMonitorInfoUpdateEvent(MonitorInfoUpdateEvent event) {
        setText(event.getInfo());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMonitorInfoShowEvent(MonitorInfoShowEvent event) {
        setVisibility(event.isShow() ? View.VISIBLE : View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMonitorInfoControlEvent(MonitorInfoControlEvent event) {
        boolean isShow = getVisibility() == VISIBLE;
        setVisibility(isShow ? View.GONE : View.VISIBLE);
    }
}
