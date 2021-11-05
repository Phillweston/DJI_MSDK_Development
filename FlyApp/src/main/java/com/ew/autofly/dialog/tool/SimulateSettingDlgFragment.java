package com.ew.autofly.dialog.tool;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.interfaces.OnSimulateSettingDialogClickListener;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.utils.GpsUtils2;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import java.util.HashMap;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;

public class SimulateSettingDlgFragment extends DialogFragment implements OnClickListener {
    private EditText mEtLongitude, mEtLatitude;
    private TextView mTvSerialNo;
    private String simulateLat, simulateLon;
    private BaseProduct mProduct;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProduct = EWApplication.getProductInstance();
            refreshConnectState();
        }
    };
    private String mTag;
    private TextView mTvConfirm;
    private ImageButton imgBtnLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.BROADCAST_DJI_PRODUCT_CONNECTION_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(false);
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_simulate_setting, container, false);
        bindField(view);
        initData();
        return view;
    }

    private void bindField(View view) {
        mTvSerialNo = (TextView) view.findViewById(R.id.tv_serial_no);
        mEtLatitude = (EditText) view.findViewById(R.id.et_latitude);
        mEtLongitude = (EditText) view.findViewById(R.id.et_longitude);
        mTvConfirm = (TextView) view.findViewById(R.id.tv_start_simulate);
        imgBtnLocation = (ImageButton) view.findViewById(R.id.imgbtn_location);
        view.findViewById(R.id.tv_cancel_sinulate).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
    }

    private void initData() {
        mProduct = EWApplication.getProductInstance();
        if (null != mProduct && mProduct.isConnected()) {
            mTvSerialNo.setText(mProduct.getModel().getDisplayName());
            mTvSerialNo.setTextColor(Color.parseColor("#32CD32"));
            mTvConfirm.setVisibility(View.VISIBLE);
        } else {
            mTvSerialNo.setText(getResources().getString(R.string.txt_simulate_notify));
            mTvSerialNo.setTextColor(Color.parseColor("#DC143C"));
            mTvConfirm.setVisibility(View.GONE);
        }
        if (getArguments() != null) {
            mEtLatitude.setText(getArguments().getString("lat"));
            mEtLongitude.setText(getArguments().getString("lon"));
            mTag = getArguments().getString("tag");
        }
        setConfirmTag();
        imgBtnLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsUtils2.getCurrentLocation(new GpsUtils2.LocationListner() {
                    @Override
                    public void result(AMapLocation location) {
                        LatLngInfo latLngInfo;
                        if (location.getErrorCode() == 0) {
                            latLngInfo = CoordinateUtils.gcj_To_Gps84(location.getLatitude(), location.getLongitude());
                        } else {
                            AMapLocation lastKnowLocaation = GpsUtils2.getLastKnowLocaation();
                            latLngInfo = CoordinateUtils.gcj_To_Gps84(lastKnowLocaation.getLatitude(), lastKnowLocaation.getLongitude());
                        }
                        mEtLatitude.setText(String.valueOf(latLngInfo.latitude));
                        mEtLongitude.setText(String.valueOf(latLngInfo.longitude));
                    }
                });
            }
        });
    }

    private void setConfirmTag() {
        Aircraft aircraft = EWApplication.getAircraftInstance();
        try {
            if (aircraft.getFlightController().getSimulator().isSimulatorActive()) {
                mTvConfirm.setText("结束模拟");
            } else if (!((Aircraft) mProduct).getFlightController().getSimulator().isSimulatorActive()) {
                mTvConfirm.setText("开始模拟");
            } else {
                mTvConfirm.setText("状态异常，请重新连接飞行器");
            }
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public void onClick(View v) {
        OnSimulateSettingDialogClickListener act = (OnSimulateSettingDialogClickListener) getActivity().getSupportFragmentManager().findFragmentByTag(mTag);
        switch (v.getId()) {
            case R.id.tv_start_simulate:
                if (mTvConfirm.getText().toString().equals("开始模拟")) {
                    if (getTag().equals("simulate")) {
                        if (!checkStatus())
                            return;
                        HashMap<String, String> map = new HashMap<>();
                        map.put("lat", simulateLat);
                        map.put("lon", simulateLon);
                        if(act!=null){
                            act.onSimulateDialogConfirm(getTag(), map);
                        }
                    }
                } else {
                    EWApplication.getAircraftInstance().getFlightController().getSimulator().stop(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                Logger.i("mmmm-----模拟终止失败" + djiError.toString());
                            }
                        }
                    });
                }
                dismiss();
                break;
            case R.id.tv_cancel_sinulate:
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    private void refreshConnectState() {
        if (null != mProduct && mProduct.isConnected()) {
            mTvSerialNo.setText(mProduct.getModel().getDisplayName());
            mTvSerialNo.setTextColor(Color.parseColor("#32CD32"));
            mTvConfirm.setVisibility(View.VISIBLE);
            setConfirmTag();
        } else {
            mTvSerialNo.setText(getResources().getString(R.string.txt_simulate_notify));
            mTvSerialNo.setTextColor(Color.parseColor("#DC143C"));
            mTvConfirm.setVisibility(View.GONE);
        }
    }

    private boolean checkStatus() {
        if (null == mProduct || !mProduct.isConnected()) {
            ToastUtil.show(getActivity(), "请先连接飞行器");
            return false;
        }
        simulateLon = mEtLongitude.getText().toString().trim();
        if (simulateLon == null || simulateLon.isEmpty()) {
            ToastUtil.show(getActivity(), "请填写经度");
            return false;
        }
        simulateLat = mEtLatitude.getText().toString();
        if (simulateLat == null || simulateLat.isEmpty()) {
            ToastUtil.show(getActivity(), "请填写纬度");
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }
}