package com.ew.autofly.module.setting.fragment.common;

import android.view.View;
import android.widget.CompoundButton;

import com.ew.autofly.R;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.flycloud.autofly.design.view.setting.SettingCheckView;
import com.flycloud.autofly.design.view.setting.SettingEnterView;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;
import com.kyleduo.switchbutton.SwitchButton;


public class CommonSettingFragment extends BaseSettingFragment implements View.OnClickListener {

    private SettingCheckView mScGrid;
    private SettingEnterView mSsMapType;

    private SettingEnterView mLiveSe;

    private FlySettingConfigKey mVideoGridKey = FlySettingConfigKey.create(FlySettingConfigKey.VIDEO_GRID);
    private FlySettingConfigKey mMapServiceKey = FlySettingConfigKey.create(FlySettingConfigKey.MAP_SERVICE_PROVIDER);
    private FlySettingConfigKey mMapRegionKey = FlySettingConfigKey.create(FlySettingConfigKey.MAP_REGION);

    private String[] mapSourceArray;
    private String[] mapRegionArray;


    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_common;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        mScGrid = rootView.findViewById(R.id.sc_grid);
        mSsMapType = rootView.findViewById(R.id.ss_map_setting);
        mLiveSe = (SettingEnterView) rootView.findViewById(R.id.se_live);

        initData();
        initVideoGrid();
        initMapType();
        initLive();
    }

    private void initData() {
        mapSourceArray = getResources().getStringArray(R.array.map_source);
        mapRegionArray = getResources().getStringArray(R.array.map_region);
    }

    private void initVideoGrid() {
        Object isOpenGrid = FlyKeyManager.getInstance().getValue(mVideoGridKey);
        mScGrid.setCheck(isOpenGrid == null ? false : (Boolean) isOpenGrid);
        mScGrid.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FlyKeyManager.getInstance().setValue(mVideoGridKey, isChecked);
            }

        });
    }

    private void initMapType() {

        String mapTypeName = "";
        int mapType = (int) FlyKeyManager.getInstance().getValue(mMapServiceKey);
        MapServiceProvider mapServiceProvider = MapServiceProvider.find(mapType);
        switch (mapServiceProvider) {
            case GOOGLE_MAP:
                mapTypeName = mapSourceArray[0];
                break;
            case AMAP:
                mapTypeName = mapSourceArray[1];
                break;
            case OPENCYCLE_MAP:
                mapTypeName = mapSourceArray[2];
                break;
        }

        String mapRegionName = "";
        int mapRegion = (int) FlyKeyManager.getInstance().getValue(mMapRegionKey);
        MapRegion mp = MapRegion.find(mapRegion);
        switch (mp) {
            case CHINA:
                mapRegionName = mapRegionArray[0];
                break;
            case CHINA_HONGKONG:
                mapRegionName = mapRegionArray[1];
                break;
            case OTHER:
                mapRegionName = mapRegionArray[2];
                break;
        }

        mSsMapType.setEnterResult(mapTypeName + "(" + mapRegionName + ")");
        mSsMapType.setOnClickListener(this);
    }

    private void initLive() {
        mLiveSe.setVisibility(View.VISIBLE);
        mLiveSe.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.se_live:
                enterToSecondMenu(new LiveSettingFragment(), getString(R.string.video_live));
                break;
            case R.id.ss_map_setting:
                enterToSecondMenu(new MapSettingFragment(), getString(R.string.setting_map));
                break;
        }
    }
}
