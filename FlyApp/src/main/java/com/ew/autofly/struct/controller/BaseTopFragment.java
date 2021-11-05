package com.ew.autofly.struct.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.event.BatteryStateEvent;
import com.ew.autofly.event.ui.topbar.DeviceShowEvent;
import com.ew.autofly.event.ui.topbar.MainMenuShowEvent;
import com.ew.autofly.event.ui.topbar.MonitorInfoControlEvent;
import com.ew.autofly.event.ui.topbar.MoreMenuShowEvent;
import com.ew.autofly.event.ui.topbar.TopWidgetShowEvent;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.widgets.topbar.DeviceView;
import com.flycloud.autofly.base.framework.aop.annotation.SingleClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import dji.common.battery.BatteryCellVoltageLevel;
import dji.common.battery.BatteryState;
import dji.ux.widget.BatteryWidget;



public class BaseTopFragment extends Fragment implements View.OnClickListener {


    private TextView mTitle;

    private TextView mTvBattery;

    private BatteryWidget mBattery;

    private ImageButton mBtSimulateSetting;

    private DeviceView mIvDevice;

    private ImageView mIvMore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_topbar, container, false);
        initView(view);
        return view;
    }

    private void initData() {
    }

    private void initView(View view) {
        view.findViewById(R.id.iv_menu).setOnClickListener(this);
        mBtSimulateSetting = (ImageButton) view.findViewById(R.id.imgBtnSimulateSetting);
        mBtSimulateSetting.setOnClickListener(this);
        mBtSimulateSetting.setVisibility(View.GONE);
        mTitle = (TextView) view.findViewById(R.id.tvTopTitle);
        mTitle.setOnClickListener(this);
        mBattery = (BatteryWidget) view.findViewById(R.id.imgBtnBattery);
        mBattery.setOnClickListener(this);
        view.findViewById(R.id.vw_vision_detection).setOnClickListener(this);
        mIvDevice = (DeviceView) view.findViewById(R.id.ivTopDevice);
        mIvDevice.setOnClickListener(this);
      
        view.findViewById(R.id.imgBtnFigure).setOnClickListener(this);
        view.findViewById(R.id.imgBtnSingle).setOnClickListener(this);
        view.findViewById(R.id.tvTopTitle).setOnClickListener(this);
        mTvBattery = (TextView) view.findViewById(R.id.tvBtnBattery);
        mTvBattery.setOnClickListener(this);
        mIvMore = (ImageView) view.findViewById(R.id.iv_more);
        mIvMore.setOnClickListener(this);

        initProductChangeReceiver();
        updateConnectState();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                EventBus.getDefault().post(new MainMenuShowEvent(true));
                break;
            case R.id.imgBtnFigure:
            case R.id.imgBtnSingle:
            case R.id.imgBtnBattery:
            case R.id.tvBtnBattery:
                EventBus.getDefault().post(new MonitorInfoControlEvent());
                break;
            case R.id.ivTopDevice:
                EventBus.getDefault().post(new DeviceShowEvent(true));
                break;
        }

        onSingleClick(v);
    }

    @SingleClick
    public void onSingleClick(View view){
        switch (view.getId()) {
            case R.id.iv_more:
                EventBus.getDefault().post(new MoreMenuShowEvent(true));
                break;
        }
    }

    public void setTitleTex(String title) {
        mTitle.setText(title);
    }

    protected BroadcastReceiver mProductChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnectState();
        }
    };

    private void initProductChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.BROADCAST_DJI_PRODUCT_CONNECTION_CHANGE);
        getActivity().registerReceiver(mProductChangeReceiver, filter);
    }

    private void updateConnectState() {

        if (AircraftManager.isOnlyFlightController()) {
            mBattery.setVisibility(View.GONE);
            mTvBattery.setVisibility(View.VISIBLE);
        } else {
            mBattery.setVisibility(View.VISIBLE);
            mTvBattery.setVisibility(View.GONE);
            mTvBattery.setText("N/A");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopWidgetShowEvent(TopWidgetShowEvent event) {
        switch (event.getShowWitch()) {
            case TopWidgetShowEvent.SHOW_DEVICE:
                mIvDevice.setVisibility(event.isShow()?View.VISIBLE:View.GONE);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatteryStateEvent(BatteryStateEvent batteryStateEvent) {
        BatteryState batteryState = batteryStateEvent.getBatteryState();
        if (batteryState != null) {
            if (AircraftManager.isOnlyFlightController()) {
                mBattery.setVisibility(View.GONE);
                mTvBattery.setVisibility(View.VISIBLE);
                final double amountBattery = batteryState.getVoltage();
                if (amountBattery > 0) {
                    DecimalFormat df = new DecimalFormat("#.00");
                    String f1 = df.format((amountBattery / 1000));
                    mTvBattery.setText(f1 + " v");
                }
                BatteryCellVoltageLevel cellVoltageLevel = batteryState.getCellVoltageLevel();
                if (cellVoltageLevel != null) {
                    switch (cellVoltageLevel) {
                        default:
                        case UNKNOWN:
                        case LEVEL_0:
                            mTvBattery.setTextColor(Color.WHITE);
                            break;
                        case LEVEL_1:
                            mTvBattery.setTextColor(Color.YELLOW);
                            break;
                        case LEVEL_2:
                        case LEVEL_3:
                            mTvBattery.setTextColor(Color.RED);
                            break;
                    }
                }
            } else {
                mBattery.setVisibility(View.VISIBLE);
                mTvBattery.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mProductChangeReceiver);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
