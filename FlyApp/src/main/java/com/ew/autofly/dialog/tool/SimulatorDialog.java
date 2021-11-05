package com.ew.autofly.dialog.tool;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.utils.GpsUtils2;
import com.ew.autofly.xflyer.utils.CoordinateUtils;


public class SimulatorDialog extends BaseDialogFragment implements OnClickListener {

    public final static String PARAM_SIMULATOR_LONGITUDE = "PARAM_SIMULATOR_LONGITUDE";
    public final static String PARAM_SIMULATOR_LATITUDE = "PARAM_SIMULATOR_LATITUDE";

    private TextView mTvCancel;
    private TextView mTvOk;
    private ImageButton mImgbtnLocation;
    private EditText mEtLongitude;
    private EditText mEtLatitude;
    private EditText mEtAltitude;

    private String simulateLat, simulateLon, simulateAlt;

    private LocationCoordinate simluateLoc;

    private IConfirmListener mConfirmListener;

    public void setConfirmListener(IConfirmListener confirmListener) {
        mConfirmListener = confirmListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateSize() {
        setSize(0.5f, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_setting_simulator, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {

        mTvCancel = view.findViewById(R.id.tv_cancel);
        mTvOk = view.findViewById(R.id.tv_ok);
        mImgbtnLocation = view.findViewById(R.id.imgbtn_location);
        mEtLongitude = view.findViewById(R.id.et_longitude);
        mEtLatitude = view.findViewById(R.id.et_latitude);
        mEtAltitude = view.findViewById(R.id.et_altitude);

        mTvCancel.setOnClickListener(this);
        mTvOk.setOnClickListener(this);
        mImgbtnLocation.setOnClickListener(this);
    }

    private void initData() {
        if (getArguments() != null) {
            mEtLongitude.setText(String.valueOf(getArguments().getDouble(PARAM_SIMULATOR_LONGITUDE)));
            mEtLatitude.setText(String.valueOf(getArguments().getDouble(PARAM_SIMULATOR_LATITUDE)));
            mEtLongitude.setSelection(mEtLongitude.getText() != null ? mEtLongitude.getText().length() : 0);
            mEtLatitude.setSelection(mEtLatitude.getText() != null ? mEtLatitude.getText().length() : 0);
        }

        Object value = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.SIMULATE_ALTITUDE));
        mEtAltitude.setText(String.valueOf((float) value));
        mEtAltitude.setSelection(mEtAltitude.getText() != null ? mEtAltitude.getText().length() : 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ok:
                if (checkStatus()) {
                    if (mConfirmListener != null) {
                        FlyKeyManager.getInstance().setValue(FlySettingConfigKey.create(FlySettingConfigKey.SIMULATE_ALTITUDE), simluateLoc.getAltitude());
                        mConfirmListener.onConfirm("ok", simluateLoc);
                    }
                }
                dismiss();
                break;
            case R.id.imgbtn_location:
                GpsUtils2.getCurrentLocation(new GpsUtils2.LocationListner() {
                    @Override
                    public void result(AMapLocation location) {
                        LatLngInfo latLngInfo = null;
                        if (location.getErrorCode() == 0) {
                            latLngInfo = CoordinateUtils.gcj_To_Gps84(location.getLatitude(), location.getLongitude());
                        } else {
                            AMapLocation lastKnowLocaation = GpsUtils2.getLastKnowLocaation();
                            if (lastKnowLocaation != null) {
                                latLngInfo = CoordinateUtils.gcj_To_Gps84(lastKnowLocaation.getLatitude(), lastKnowLocaation.getLongitude());
                            }
                        }
                        if (latLngInfo != null) {
                            mEtLongitude.setText(String.valueOf(latLngInfo.longitude));
                            mEtLatitude.setText(String.valueOf(latLngInfo.latitude));
                            mEtLongitude.setSelection(mEtLongitude.getText() != null ? mEtLongitude.getText().length() : 0);
                            mEtLatitude.setSelection(mEtLatitude.getText() != null ? mEtLatitude.getText().length() : 0);
                        }
                    }
                });
                break;
        }
    }

    private boolean checkStatus() {
        simulateLon = mEtLongitude.getText().toString().trim();
        if (simulateLon.isEmpty()) {
            showToast("请填写经度");
            return false;
        }
        simulateLat = mEtLatitude.getText().toString().trim();
        if (simulateLat.isEmpty()) {
            showToast("请填写纬度");
            return false;
        }

        simulateAlt = mEtAltitude.getText().toString().trim();
        if (simulateAlt.isEmpty()) {
            showToast("请填写高度");
            return false;
        }

        try {
            simluateLoc = new LocationCoordinate(Double.valueOf(simulateLat),
                    Double.valueOf(simulateLon), Float.valueOf(simulateAlt));
        } catch (Exception e) {
            showToast("经纬度坐标不合法");
            return false;
        }

        return true;
    }

}
