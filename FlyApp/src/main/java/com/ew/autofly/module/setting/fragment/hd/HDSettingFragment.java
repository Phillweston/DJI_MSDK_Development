package com.ew.autofly.module.setting.fragment.hd;

import androidx.annotation.NonNull;

import android.view.View;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.model.CameraManager;
import com.ew.autofly.module.setting.cache.FlySettingKey;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.ew.autofly.widgets.dji.DjiFPVWidget;
import com.flycloud.autofly.design.view.setting.SettingSpinnerView;
import com.flycloud.autofly.ux.view.PopSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dji.common.airlink.LightbridgeFrequencyBand;
import dji.common.airlink.OcuSyncFrequencyBand;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.keysdk.CameraKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.SetCallback;
import dji.sdk.airlink.AirLink;
import dji.sdk.airlink.LightbridgeLink;
import dji.sdk.airlink.OcuSyncLink;
import dji.sdk.camera.Camera;
import dji.sdk.camera.Lens;
import dji.sdk.products.Aircraft;

import static dji.common.camera.SettingsDefinitions.DisplayMode.MSX;
import static dji.common.camera.SettingsDefinitions.DisplayMode.PIP;
import static dji.common.camera.SettingsDefinitions.DisplayMode.THERMAL_ONLY;
import static dji.common.camera.SettingsDefinitions.DisplayMode.VISUAL_ONLY;


public class HDSettingFragment extends BaseSettingFragment {

    private SettingSpinnerView mSsFrequencyBand;
    private SettingSpinnerView mSsVideoSource;
    private SettingSpinnerView mSsDisplayDual;

    private String[] videoSourceArray;
    private String[] displayDualArray;
    private String[] frequencyBandArray;

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_hd;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mSsFrequencyBand = rootView.findViewById(R.id.ss_frequency_band);
        mSsDisplayDual = rootView.findViewById(R.id.ss_display_dual);
        mSsVideoSource = rootView.findViewById(R.id.ss_video_source);

        initData();
        initVideoSource();
        initDisplayDual();
        initFrequencyBand();

    }

    private void initData() {
        videoSourceArray = getResources().getStringArray(R.array.video_source);
    }

    protected AirLink getAirLink() {
        Aircraft aircraft = EWApplication.getAircraftInstance();
        AirLink airLink = null;

        if (aircraft != null && aircraft.isConnected()) {
            airLink = aircraft.getAirLink();
        }
        return airLink;
    }

    
    private void initVideoSource() {
        List<String> videoSourceList = new ArrayList<>(Arrays.asList(videoSourceArray));

        PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
            @Override
            public void onSelect(int position) {
                FlyKeyManager.getInstance().setValue(FlySettingKey.create(FlySettingKey.VIDEO_SOURCE), DjiFPVWidget.VideoSource.find(position));
            }
        };

        Object object = FlyKeyManager.getInstance().getValue(FlySettingKey.create(FlySettingKey.VIDEO_SOURCE));
        int position = object != null ? getVideoSource((DjiFPVWidget.VideoSource) object) : 0;
        mSsVideoSource.setSelectIndex(position);
        mSsVideoSource.init(videoSourceList.get(position), videoSourceList, callback);
    }


    private int getVideoSource(DjiFPVWidget.VideoSource videoSource) {
        return videoSource.value();
    }

    private void initDisplayDual() {
        Model model = AircraftManager.getModel();
        if (model == Model.MAVIC_2_ENTERPRISE_ADVANCED) {
            mSsDisplayDual.setVisibility(View.VISIBLE);
            PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
                @Override
                public void onSelect(int position) {
                    SettingsDefinitions.DisplayMode displayMode;
                    switch (position) {
                        case 0:
                            displayMode = VISUAL_ONLY;
                            break;
                        case 1:
                            displayMode = THERMAL_ONLY;
                            break;
                        case 2:
                            displayMode = MSX;
                            break;
                        default:
                            displayMode = VISUAL_ONLY;
                            break;
                    }

                    Camera camera = CameraManager.getMavic2EnterpriseDualCamera();
                    if (camera != null) {
                        List<Lens> lenses = camera.getLenses();
                        if (lenses != null && !lenses.isEmpty()) {
                            Lens lens;
                            if (lenses.size() > 1) {
                                lens = lenses.get(1);
                            } else {
                                lens = lenses.get(0);
                            }
                            lens.setDisplayMode(displayMode, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        showToast("设置失败" + djiError.toString());

                                    } else {
                                        showToast("设置成功");
                                    }
                                }
                            });
                        }
                    } else {
                        showToast("获取双光镜头失败");
                    }
                }
            };

            displayDualArray = getResources().getStringArray(R.array.display_M2EA);
            ArrayList<String> strings = new ArrayList<>(Arrays.asList(displayDualArray));

            mSsDisplayDual.init("", strings, callback);

            Camera camera = CameraManager.getMavic2EnterpriseDualCamera();
            if (camera != null) {
                List<Lens> lenses = camera.getLenses();
                if (lenses != null && !lenses.isEmpty()) {
                    Lens lens;
                    if (lenses.size() > 1) {
                        lens = lenses.get(1);
                    } else {
                        lens = lenses.get(0);
                    }
                    lens.getDisplayMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.DisplayMode>() {
                        @Override
                        public void onSuccess(SettingsDefinitions.DisplayMode displayMode) {
                            switch (displayMode) {
                                case VISUAL_ONLY:
                                    mSsDisplayDual.setSelect(0);
                                    break;
                                case THERMAL_ONLY:
                                    mSsDisplayDual.setSelect(1);
                                    break;
                                case MSX:
                                    mSsDisplayDual.setSelect(2);
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                }
            }

        } else if (model == Model.MAVIC_2_ENTERPRISE_DUAL) {
            mSsDisplayDual.setVisibility(View.VISIBLE);
            PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
                @Override
                public void onSelect(int position) {
                    SettingsDefinitions.DisplayMode displayMode;
                    switch (position) {
                        case 0:
                            displayMode = VISUAL_ONLY;
                            break;
                        case 1:
                            displayMode = THERMAL_ONLY;
                            break;
                        case 2:
                            displayMode = MSX;
                            break;
                        default:
                            displayMode = VISUAL_ONLY;
                            break;
                    }

                    Camera camera = CameraManager.getMavic2EnterpriseDualCamera();
                    if (camera != null) {
                        camera.setDisplayMode(displayMode, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    showToast("设置失败" + djiError.toString());

                                } else {
                                    showToast("设置成功");
                                }
                            }
                        });
                    } else {
                        showToast("获取双光镜头失败");
                    }
                }
            };

            displayDualArray = getResources().getStringArray(R.array.display_dual);
            ArrayList<String> strings = new ArrayList<>(Arrays.asList(displayDualArray));

            mSsDisplayDual.init("", strings, callback);

            Camera camera = CameraManager.getMavic2EnterpriseDualCamera();
            if (camera != null) {
                camera.getDisplayMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.DisplayMode>() {
                    @Override
                    public void onSuccess(SettingsDefinitions.DisplayMode displayMode) {
                        switch (displayMode) {
                            case VISUAL_ONLY:
                                mSsDisplayDual.setSelect(0);
                                break;
                            case THERMAL_ONLY:
                                mSsDisplayDual.setSelect(1);
                                break;
                            case MSX:
                                mSsDisplayDual.setSelect(2);
                                break;
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }

        } else if (CameraManager.isXT2CameraConnected()) {

            mSsDisplayDual.setVisibility(View.VISIBLE);

            PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
                @Override
                public void onSelect(int position) {
                    SettingsDefinitions.DisplayMode displayMode;
                    switch (position) {
                        case 0:
                            displayMode = PIP;
                            break;
                        case 1:
                            displayMode = VISUAL_ONLY;
                            break;
                        case 2:
                            displayMode = MSX;
                            break;
                        default:
                            displayMode = VISUAL_ONLY;
                            break;
                    }

                    KeyManager.getInstance().setValue(CameraKey.create(CameraKey.DISPLAY_MODE, 2), displayMode, new SetCallback() {
                        @Override
                        public void onSuccess() {
                            showToast("设置成功");
                        }

                        @Override
                        public void onFailure(@NonNull DJIError error) {
                            showToast("设置失败" + error.toString());
                        }
                    });
                }
            };

            displayDualArray = getResources().getStringArray(R.array.display_xt2_dual);
            ArrayList<String> strings = new ArrayList<>(Arrays.asList(displayDualArray));

            mSsDisplayDual.init("", strings, callback);


            KeyManager.getInstance().getValue(CameraKey.create(CameraKey.DISPLAY_MODE, 2), new GetCallback() {
                @Override
                public void onSuccess(@NonNull Object displayMode) {
                    if (displayMode instanceof SettingsDefinitions.DisplayMode) {
                        switch ((SettingsDefinitions.DisplayMode) displayMode) {
                            case PIP:
                                mSsDisplayDual.setSelect(0);
                                break;
                            case VISUAL_ONLY:
                                mSsDisplayDual.setSelect(1);
                                break;
                            case MSX:
                                mSsDisplayDual.setSelect(2);
                                break;
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull DJIError djiError) {

                }
            });

        } else {
            mSsDisplayDual.setVisibility(View.GONE);
        }
    }

    private void initFrequencyBand() {

        getFrequencyBrand();
    }

    private PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
        @Override
        public void onSelect(int position) {
            setFrequencyBrand(position);
        }
    };

    private void getFrequencyBrand() {
        AirLink airLink = getAirLink();
        if (airLink != null) {
            OcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
            if (ocuSyncLink != null) {

                frequencyBandArray = getResources().getStringArray(R.array.ocuSync_frequency_band);
                List<String> frequencyBrandList = new ArrayList<>(Arrays.asList(frequencyBandArray));
                mSsFrequencyBand.init("", frequencyBrandList, callback);

                ocuSyncLink.getFrequencyBand(new CommonCallbacks.CompletionCallbackWith<OcuSyncFrequencyBand>() {
                    @Override
                    public void onSuccess(final OcuSyncFrequencyBand ocuSyncFrequencyBand) {
                        mUIThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                switch (ocuSyncFrequencyBand) {
                                    case FREQUENCY_BAND_DUAL:
                                        mSsFrequencyBand.setSelect(0);
                                        break;
                                    case FREQUENCY_BAND_2_DOT_4_GHZ:
                                        mSsFrequencyBand.setSelect(1);
                                        break;
                                    case FREQUENCY_BAND_5_DOT_8_GHZ:
                                        mSsFrequencyBand.setSelect(2);
                                        break;
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            } else {

                frequencyBandArray = getResources().getStringArray(R.array.lightbridge_frequency_band);
                List<String> frequencyBrandList = new ArrayList<>(Arrays.asList(frequencyBandArray));
                mSsFrequencyBand.init("", frequencyBrandList, callback);

                LightbridgeLink lightbridgeLink = airLink.getLightbridgeLink();
                if (lightbridgeLink != null) {
                    lightbridgeLink.getFrequencyBand(new CommonCallbacks.CompletionCallbackWith<LightbridgeFrequencyBand>() {
                        @Override
                        public void onSuccess(final LightbridgeFrequencyBand lightbridgeFrequencyBand) {

                            mUIThreadHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    switch (lightbridgeFrequencyBand) {
                                        case FREQUENCY_BAND_2_DOT_4_GHZ:
                                            mSsFrequencyBand.setSelect(0);
                                            break;
                                        case FREQUENCY_BAND_5_DOT_8_GHZ:
                                            mSsFrequencyBand.setSelect(1);
                                            break;
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                }
            }
        }
    }

    private void setFrequencyBrand(int position) {
        AirLink airLink = getAirLink();
        if (airLink != null) {
            OcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
            if (ocuSyncLink != null) {
                OcuSyncFrequencyBand ocuSyncFrequencyBand;
                switch (position) {
                    case 0:
                        ocuSyncFrequencyBand = OcuSyncFrequencyBand.FREQUENCY_BAND_DUAL;
                        break;
                    case 1:
                        ocuSyncFrequencyBand = OcuSyncFrequencyBand.FREQUENCY_BAND_2_DOT_4_GHZ;
                        break;
                    case 2:
                        ocuSyncFrequencyBand = OcuSyncFrequencyBand.FREQUENCY_BAND_5_DOT_8_GHZ;
                        break;
                    default:
                        ocuSyncFrequencyBand = OcuSyncFrequencyBand.FREQUENCY_BAND_DUAL;
                        break;
                }

                ocuSyncLink.setFrequencyBand(ocuSyncFrequencyBand, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            showToast("设置成功");
                        } else {
                            showToast("设置失败");
                        }
                    }
                });
            } else {
                LightbridgeLink lightbridgeLink = airLink.getLightbridgeLink();
                if (lightbridgeLink != null) {

                    LightbridgeFrequencyBand lightbridgeFrequencyBand;
                    if (position == 0) {
                        lightbridgeFrequencyBand = LightbridgeFrequencyBand.FREQUENCY_BAND_2_DOT_4_GHZ;
                    } else {
                        lightbridgeFrequencyBand = LightbridgeFrequencyBand.FREQUENCY_BAND_5_DOT_8_GHZ;
                    }

                    lightbridgeLink.setFrequencyBand(lightbridgeFrequencyBand, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                showToast("设置成功");
                            } else {
                                showToast("设置失败");
                            }
                        }
                    });
                }
            }
        }
    }
}
