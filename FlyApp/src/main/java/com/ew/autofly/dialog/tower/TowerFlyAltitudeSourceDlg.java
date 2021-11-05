package com.ew.autofly.dialog.tower;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ew.autofly.R;
import com.flycloud.autofly.design.view.dialog.BaseAlterDialog;


public class TowerFlyAltitudeSourceDlg extends BaseAlterDialog {

    private RadioGroup mAltitudeSourceRg;
    private TextView mAltitudeSourceDescTv;

    private boolean mIsDemSource = true;

    @Override
    protected void onCreateSize() {
        setSize(0.5f, 0.6f);
    }

    @Override
    protected int setContentViewId() {
        return R.layout.dialog_tower_fly_altitude_source;
    }

    @Override
    protected int setTheme() {
        return THEME_DARK;
    }

    @Override
    protected void initView(View view, LayoutInflater inflater) {
        super.initView(view, inflater);
        setTitle(getString(R.string.select_altitude_data_source));

        mAltitudeSourceRg = (RadioGroup) view.findViewById(R.id.rg_altitude_source);
        mAltitudeSourceDescTv = (TextView) view.findViewById(R.id.tv_altitude_source_desc);

        initSourceType();
    }

    private void initSourceType() {
        mAltitudeSourceDescTv.setText(getResources().getString(R.string.tower_fly_altitude_source_dem_desc));
        mAltitudeSourceRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_altitude_source_dem:
                        mIsDemSource = true;
                        mAltitudeSourceDescTv.setText(getResources().getString(R.string.tower_fly_altitude_source_dem_desc));
                        break;
                    case R.id.rb_altitude_source_kml:
                        mIsDemSource = false;
                        mAltitudeSourceDescTv.setText(getResources().getString(R.string.tower_fly_altitude_source_kml_desc));
                        break;
                }
            }
        });
    }

    @Override
    protected Object onClickPositive() {
        return mIsDemSource;
    }


}
