package com.ew.autofly.dialog.tower;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.utils.ToastUtil;



public class TowerFlyAltitudeInputDialog extends BaseDialogFragment implements View.OnClickListener {

    public final static String PARAMS_IS_SET_MAX_TOWER_ALTITUDE = "PARAMS_IS_SET_MAX_TOWER_ALTITUDE";


    private IConfirmListener confirmClickListener;
    private EditText mEtTopToFlyAltitude;
    private EditText mEtMaxTowerAltitude;

    private boolean isSetMaxTowerAltitude = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {

        Bundle arguments = getArguments();
        if (arguments != null) {
            isSetMaxTowerAltitude = (boolean) arguments.getBoolean(PARAMS_IS_SET_MAX_TOWER_ALTITUDE, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tower_altitude_input, container, false);
        initView(view);
        return view;

    }

    private void initView(View view) {
        mEtMaxTowerAltitude = view.findViewById(R.id.et_max_tower_altitude);
        mEtTopToFlyAltitude = view.findViewById(R.id.et_top_to_fly_altitude);
        view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        view.findViewById(R.id.ll_max_tower_altitude).setVisibility(isSetMaxTowerAltitude ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                dismiss();
                break;
            case R.id.btn_confirm:
                int towerMaxAltitude = 0;
                if (isSetMaxTowerAltitude) {
                    String towerMaxAltitudeStr = mEtMaxTowerAltitude.getText().toString();
                    if (TextUtils.isEmpty(towerMaxAltitudeStr)) {
                        ToastUtil.show(EWApplication.getInstance(), "请输入最大杆塔高度");
                        return;
                    }
                    towerMaxAltitude = Integer.valueOf(towerMaxAltitudeStr);
                    if (towerMaxAltitude < 0 || towerMaxAltitude > 500) {
                        ToastUtil.show(EWApplication.getInstance(), "最大杆塔高度输入范围为0-500m");
                        return;
                    }
                }

                String topToFlyAltitudeStr = mEtTopToFlyAltitude.getText().toString();
                if (TextUtils.isEmpty(topToFlyAltitudeStr)) {
                    ToastUtil.show(EWApplication.getInstance(), "请输入相对塔顶飞行高度");
                    return;
                }

                int topToFlyAltitude = Integer.valueOf(topToFlyAltitudeStr);
                if (topToFlyAltitude < 0 || topToFlyAltitude > 500) {
                    ToastUtil.show(EWApplication.getInstance(), "相对塔顶飞行高度输入范围为0-500m");
                    return;
                }

                if (confirmClickListener != null) {
                    confirmClickListener.onConfirm("", towerMaxAltitude + topToFlyAltitude);
                }
                dismiss();
                break;
        }
    }

    public void setOnConfirmListener(IConfirmListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
    }

}
