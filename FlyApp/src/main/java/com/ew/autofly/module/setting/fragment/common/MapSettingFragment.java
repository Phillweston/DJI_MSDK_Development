package com.ew.autofly.module.setting.fragment.common;

import android.content.DialogInterface;
import android.view.View;

import com.ew.autofly.R;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.event.MapChangeEvent;
import com.ew.autofly.module.setting.fragment.base.BaseSettingSecondFragment;
import com.flycloud.autofly.design.view.setting.SettingSpinnerView;
import com.flycloud.autofly.design.view.setting.SettingTextView;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;
import com.flycloud.autofly.ux.view.PopSpinnerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;


public class MapSettingFragment extends BaseSettingSecondFragment {


    private SettingSpinnerView mMapTypeSs;
    private SettingSpinnerView mMapRegionSs;


    private FlySettingConfigKey mMapServiceKey = FlySettingConfigKey.create(FlySettingConfigKey.MAP_SERVICE_PROVIDER);

    private FlySettingConfigKey mMapRegionKey = FlySettingConfigKey.create(FlySettingConfigKey.MAP_REGION);

    private List<String> mMapSourceList;
    private List<String> mMapRegionList;

    private int mCurrentMapType;
    private int mCurrentMapRegion;
    private int mSelectedMapType;
    private int mSelectedMapRegion;

    private LinkedHashMap<MapServiceProvider, String> mMapServiceProviderStringMap = new LinkedHashMap<>();
    private LinkedHashMap<MapRegion, String> mMapRegionStringMap = new LinkedHashMap<>();
    private SettingTextView mMapSetSt;

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_common_map;
    }

    @Override
    protected void initView(View rootView) {
        mMapTypeSs = (SettingSpinnerView) rootView.findViewById(R.id.ss_map_type);
        mMapRegionSs = (SettingSpinnerView) rootView.findViewById(R.id.ss_map_region);
        mMapSetSt = (SettingTextView) rootView.findViewById(R.id.st_map_set);

        initData();
        initMapType();
        initMapRegion();
        initMapSetting();
    }

    private void initData() {
        mMapSourceList = Arrays.asList(getResources().getStringArray(R.array.map_source));
        mMapServiceProviderStringMap.put(MapServiceProvider.GOOGLE_MAP, mMapSourceList.get(0));
        mMapServiceProviderStringMap.put(MapServiceProvider.AMAP, mMapSourceList.get(1));
        mMapServiceProviderStringMap.put(MapServiceProvider.OPENCYCLE_MAP, mMapSourceList.get(2));

        mMapRegionList = Arrays.asList(getResources().getStringArray(R.array.map_region));
        mMapRegionStringMap.put(MapRegion.CHINA, mMapRegionList.get(0));
        mMapRegionStringMap.put(MapRegion.CHINA_HONGKONG, mMapRegionList.get(1));
        mMapRegionStringMap.put(MapRegion.OTHER, mMapRegionList.get(2));
    }

    
    private void initMapType() {

        mSelectedMapType = mCurrentMapType = (int) FlyKeyManager.getInstance().getValue(mMapServiceKey);
        String initName = mMapServiceProviderStringMap.get(MapServiceProvider.find(mCurrentMapType));

        mMapTypeSs.init(initName, mMapSourceList, new PopSpinnerView.OnSelectCallback() {
            @Override
            public void onSelect(int position) {
                mSelectedMapType = new ArrayList<>(mMapServiceProviderStringMap.keySet()).get(position).value();
            }
        });
    }

    
    private void initMapRegion() {
        mSelectedMapRegion = mCurrentMapRegion = (int) FlyKeyManager.getInstance().getValue(mMapRegionKey);
        String initName = mMapRegionStringMap.get(MapRegion.find(mCurrentMapRegion));
        mMapRegionSs.init(initName, mMapRegionList, new PopSpinnerView.OnSelectCallback() {
            @Override
            public void onSelect(int position) {
                mSelectedMapRegion = new ArrayList<>(mMapRegionStringMap.keySet()).get(position).value();
            }

        });
    }

    private void initMapSetting() {
        mMapSetSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentMapType != mSelectedMapType
                        || mCurrentMapRegion != mSelectedMapRegion) {


                    CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getContext());
                    deleteDialog.setTitle(getResources().getString(R.string.notice))
                            .setMessage(getResources().getString(R.string.change_map_notice))
                            .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FlyKeyManager.getInstance().setValue(mMapServiceKey, mSelectedMapType);
                                    FlyKeyManager.getInstance().setValue(mMapRegionKey, mSelectedMapRegion);
                                    EventBus.getDefault().post(new MapChangeEvent());
                                    exitMainSetting();
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();

                }

            }
        });
    }

}