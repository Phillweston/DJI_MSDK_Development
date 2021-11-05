package com.ew.autofly.module.setting.fragment.flight;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.event.flight.LocationStateEvent;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.key.config.NTRIPConfigKey;
import com.ew.autofly.model.DataManager;
import com.ew.autofly.model.RTKManager;
import com.ew.autofly.module.setting.fragment.base.BaseSettingSecondFragment;
import com.flycloud.autofly.design.view.setting.SettingCheckView;
import com.flycloud.autofly.design.view.setting.SettingSpinnerView;
import com.flycloud.autofly.design.view.setting.SettingTextView;
import com.flycloud.autofly.ux.view.PopSpinnerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.error.DJIRTKNetworkServiceError;
import dji.common.flightcontroller.rtk.CoordinateSystem;
import dji.common.flightcontroller.rtk.NetworkServiceAccountState;
import dji.common.flightcontroller.rtk.NetworkServiceChannelState;
import dji.common.flightcontroller.rtk.NetworkServicePlan;
import dji.common.flightcontroller.rtk.NetworkServicePlansState;
import dji.common.flightcontroller.rtk.NetworkServiceSettings;
import dji.common.flightcontroller.rtk.NetworkServiceState;
import dji.common.flightcontroller.rtk.ReferenceStationSource;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.RTK;
import dji.sdk.network.RTKNetworkServiceProvider;

import static dji.common.flightcontroller.rtk.ReferenceStationSource.CUSTOM_NETWORK_SERVICE;
import static dji.common.flightcontroller.rtk.ReferenceStationSource.NETWORK_RTK;


public class RtkSettingFragment extends BaseSettingSecondFragment implements View.OnClickListener {

    private SettingCheckView mOpenRtkSc;

    private NetworkServiceSettings mNetworkServiceSettings;
    private TextView mRtkStatusTv;
    private TextView mRtkMessageTv;

    private TableLayout mTbStatusLatLong;

    private TextView planeLatitudeTv;
    private TextView baseLatitudeTV;
    private TextView planeLongitudeTv;
    private TextView baseLongitudeTv;
    private TextView planeAltitudeTv;
    private TextView baseAltitudeTV;

    private double planeLatitude;
    private double planeLongitude;
    private float planeAltitude;

    private double baseLatitude;
    private double baseLongitude;
    private float baseAltitude;
    private EditText mAddressEt;
    private EditText mPortEt;
    private EditText mMountpointEt;
    private EditText mAccountEt;
    private EditText mPasswordEt;
    private SettingTextView mSetRtkSt;
    private LinearLayout mRtkSettingLl;
    private TextView mNoRtkTv;
    private View mRtkPanelSl;
    private SettingCheckView mOpenAircraftRtkSc;
    private SettingSpinnerView mRtkServiceTypeSs;
    private TextView mRtkActivateInfoTv;
    private SettingSpinnerView mRtkCoordinateSystemSs;
    private LinearLayout mRtkServiceTypeNetworkLl;
    private LinearLayout mRtkServiceTypeCustomNetworkLl;
    private LinearLayout mNetworkRtkConnectStatusLl;
    private TextView mReconnectBtn;

    private List<String> mRtkServiceTypeList;
    private List<String> mRtkCoordinateSystemList;

    private ReferenceStationSource.Callback mReferenceStationSourceCallback;

    private boolean hasRequestGetCoordinateSystem = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    protected int setRootView() {
        return R.layout.fragment_setting_flight_rtk;
    }


    private void initData() {
        mRtkServiceTypeList = Arrays.asList(getAppResources().getStringArray(R.array.rtk_service_type));
        mRtkCoordinateSystemList = Arrays.asList(getAppResources().getStringArray(R.array.rtk_coordinate_system));
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mOpenRtkSc = (SettingCheckView) rootView.findViewById(R.id.sc_open_network_rtk);
        mRtkStatusTv = (TextView) rootView.findViewById(R.id.tv_rtk_status);
        mRtkMessageTv = (TextView) rootView.findViewById(R.id.tv_rtk_message);
        mTbStatusLatLong = rootView.findViewById(R.id.tb_status_lat_long);
        mAddressEt = (EditText) rootView.findViewById(R.id.et_address);
        mPortEt = (EditText) rootView.findViewById(R.id.et_port);
        mMountpointEt = (EditText) rootView.findViewById(R.id.et_mountpoint);
        mAccountEt = (EditText) rootView.findViewById(R.id.et_account);
        mPasswordEt = (EditText) rootView.findViewById(R.id.et_password);
        mSetRtkSt = (SettingTextView) rootView.findViewById(R.id.st_set_rtk);
        mSetRtkSt.setOnClickListener(this);
        mRtkSettingLl = (LinearLayout) rootView.findViewById(R.id.ll_rtk_setting);
        mNoRtkTv = (TextView) rootView.findViewById(R.id.tv_no_rtk);
        mRtkPanelSl = rootView.findViewById(R.id.sl_rtk_panel);
        mOpenAircraftRtkSc = (SettingCheckView) rootView.findViewById(R.id.sc_open_aircraft_rtk);

        mSetRtkSt.setText("设置");

        mRtkServiceTypeSs = (SettingSpinnerView) rootView.findViewById(R.id.ss_rtk_service_type);
        mRtkActivateInfoTv = (TextView) rootView.findViewById(R.id.tv_rtk_activate_info);
        mRtkCoordinateSystemSs = (SettingSpinnerView) rootView.findViewById(R.id.ss_rtk_coordinate_system);
        mRtkServiceTypeNetworkLl = (LinearLayout) rootView.findViewById(R.id.ll_rtk_service_type_network);
        mRtkServiceTypeCustomNetworkLl = (LinearLayout) rootView.findViewById(R.id.ll_rtk_service_type_custom_network);
        mNetworkRtkConnectStatusLl = (LinearLayout) rootView.findViewById(R.id.ll_network_rtk_connect_status);
        mReconnectBtn = rootView.findViewById(R.id.btn_reconnect);
        mReconnectBtn.setOnClickListener(this);

        initRTK();
        initRTKServiceType();
        getBuildInRTKPlan();
        initBuildInRTKCoordinateSystem();
        initTableLatLong();
        initNetworkRTKParams();

        addNetworkServiceStateListener();
      

    }

    private void initRTK() {

        RTK rtk = RTKManager.getRTK();
        if (rtk == null) {
            showToast(getAppResources().getString(R.string.network_rtk_no_rtk));
            mRtkPanelSl.setVisibility(View.GONE);
            mNoRtkTv.setVisibility(View.VISIBLE);
            return;
        } else {
            mRtkPanelSl.setVisibility(View.VISIBLE);
            mNoRtkTv.setVisibility(View.GONE);
        }

        rtk.getRtkEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
            @Override
            public void onSuccess(Boolean enable) {
                setCheckedNoEvent(mOpenAircraftRtkSc, enable);
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

        mOpenAircraftRtkSc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                RTK rtk = RTKManager.getRTK();
                if (rtk != null) {
                    rtk.setRtkEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                setCheckedNoEvent(mOpenAircraftRtkSc, !isChecked);
                                showToast("错误：" + djiError.getDescription());
                            } else {
                                if (!isChecked) {
                                    RTKNetworkServiceProvider.getInstance().stopNetworkService(null);
                                }
                            }
                        }
                    });
                } else {
                    setCheckedNoEvent(mOpenAircraftRtkSc, false);
                }
            }
        });
    }

    private void initNetworkRTKParams() {

        String address = getKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_ADDRESS);
        if (!TextUtils.isEmpty(address)) {
            mAddressEt.setText(address);
        }
        String port = getKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_PORT);
        if (!TextUtils.isEmpty(port)) {
            mPortEt.setText(port);
        }

        String mountPoint = getKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_MOUNTPOINT);
        if (!TextUtils.isEmpty(mountPoint)) {
            mMountpointEt.setText(mountPoint);
        }

        String account = getKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_ACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            mAccountEt.setText(account);
        }

        String password = getKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_PASSWORD);
        if (!TextUtils.isEmpty(password)) {
            mPasswordEt.setText(password);
        }
    }

    
    private void initRTKServiceType() {

        PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
            @Override
            public void onSelect(int position) {

                ReferenceStationSource stationSource = NETWORK_RTK;
                switch (position) {
                    case 0:
                        stationSource = NETWORK_RTK;
                        break;
                    case 1:
                        stationSource = CUSTOM_NETWORK_SERVICE;
                        break;
                    case 2:
                        stationSource = ReferenceStationSource.NONE;
                        break;
                }

                final ReferenceStationSource setStationSource = stationSource;
                RTK rtk = RTKManager.getRTK();
                if (rtk != null) {
                    rtk.setReferenceStationSource(setStationSource, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                showToast(getAppResources().getString(R.string.error_setting_fail));
                                mUIThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setRtkServiceType(DataManager.getRtkReferenceStationSource());
                                    }
                                });
                            } else {
                              
                                DataManager.setRtkReferenceStationSource(setStationSource);
                                mUIThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        setRtkServiceType(setStationSource);
                                    }
                                });

                                if (setStationSource == NETWORK_RTK) {
                                    RTKNetworkServiceProvider.getInstance().setNetworkServiceCoordinateSystem(DataManager.getRtkCoordinateSystem(), null);
                                    if (!mOpenAircraftRtkSc.isChecked()) {
                                        showToast(getAppResources().getString(R.string.please_open_aircraft_rtk));
                                        return;
                                    }
                                    RTKNetworkServiceProvider.getInstance().startNetworkService(null);
                                }
                            }
                        }
                    });
                } else {
                    showToast(getAppResources().getString(R.string.rtk_get_fail));
                }

            }
        };

        mRtkServiceTypeSs.init("", mRtkServiceTypeList, callback);

        setRtkServiceType(DataManager.getRtkReferenceStationSource());

    }

    private void setRtkServiceType(ReferenceStationSource referenceStationSource) {

        switch (referenceStationSource) {
            case NETWORK_RTK:
                mRtkServiceTypeNetworkLl.setVisibility(View.VISIBLE);
                mRtkServiceTypeCustomNetworkLl.setVisibility(View.GONE);
                mNetworkRtkConnectStatusLl.setVisibility(View.VISIBLE);
                mRtkServiceTypeSs.setSelect(0);
                break;
            case CUSTOM_NETWORK_SERVICE:
                mRtkServiceTypeNetworkLl.setVisibility(View.GONE);
                mRtkServiceTypeCustomNetworkLl.setVisibility(View.VISIBLE);
                mNetworkRtkConnectStatusLl.setVisibility(View.VISIBLE);
                mRtkServiceTypeSs.setSelect(1);
                break;
            default:
            case NONE:
                mRtkServiceTypeNetworkLl.setVisibility(View.GONE);
                mRtkServiceTypeCustomNetworkLl.setVisibility(View.GONE);
                mNetworkRtkConnectStatusLl.setVisibility(View.GONE);
                mRtkServiceTypeSs.setSelect(2);
                break;
        }
    }

    private void addNetworkServiceStateListener() {

        RTKNetworkServiceProvider.getInstance().addNetworkServiceStateCallback(mNetworkServiceStateCallback);

    }

    private void addReferenceStationSourceListener() {
        removeReferenceStationSourceListener();
        RTK rtk = RTKManager.getRTK();
        if (rtk != null) {
            mReferenceStationSourceCallback = new ReferenceStationSource.Callback() {
                @Override
                public void onReferenceStationSourceUpdate(ReferenceStationSource referenceStationSource) {
                    mUIThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setRtkServiceType(referenceStationSource);
                        }
                    });

                  
                }
            };
            rtk.addReferenceStationSourceCallback(mReferenceStationSourceCallback);
        }
    }

    private void removeReferenceStationSourceListener() {
        RTK rtk = RTKManager.getRTK();
        if (rtk != null) {
            if (mReferenceStationSourceCallback != null) {
                rtk.removeReferenceStationSourceCallback(mReferenceStationSourceCallback);
            }
            mReferenceStationSourceCallback = null;
        }
    }

    private void getBuildInRTKPlan() {
        RTKNetworkServiceProvider.getInstance().getNetworkServiceOrderPlans(new CommonCallbacks.CompletionCallbackWith<NetworkServicePlansState>() {
            @Override
            public void onSuccess(NetworkServicePlansState networkServicePlansState) {
                mUIThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initBuildInRTKPlanState(networkServicePlansState);
                    }
                });
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });
    }

    private void initBuildInRTKPlanState(NetworkServicePlansState networkServicePlansState) {
        if (networkServicePlansState != null) {
            NetworkServiceAccountState state = networkServicePlansState.getState();
            switch (state) {
                case BOUND:
                    List<NetworkServicePlan> plans = networkServicePlansState.getPlans();
                    if (plans == null || plans.isEmpty()) {
                        mRtkActivateInfoTv.setText(getAppResources().getString(R.string.rtk_build_in_plan_state_no_plan));
                    } else {

                        StringBuilder info = new StringBuilder();
                        info.append("已购买/激活内置RTK服务信息:").append("\n");
                        for (int i = 0; i < plans.size(); i++) {
                            info.append(getBuildInRTKPlanInfo(plans.get(i)));
                            if (i != plans.size() - 1) {
                                info.append("\n");
                            }
                        }
                        mRtkActivateInfoTv.setText(info.toString());
                    }
                    break;
                case NOT_PURCHASED:
                    mRtkActivateInfoTv.setText(getAppResources().getString(R.string.rtk_build_in_plan_state_not_purchased));
                    break;
                case UNBOUND:
                    mRtkActivateInfoTv.setText(getAppResources().getString(R.string.rtk_build_in_plan_state_unbound));
                    break;
                case UNKNOWN:
                    mRtkActivateInfoTv.setText(getAppResources().getString(R.string.rtk_build_in_plan_state_unknown));
                    break;
            }
        } else {
            mRtkActivateInfoTv.setVisibility(View.GONE);
        }
    }

    private String getBuildInRTKPlanInfo(NetworkServicePlan plan) {
        StringBuilder info = new StringBuilder();
        info.append("(套餐").append(plan.getPlanName().toString())
                .append("有效期").append(plan.getActivationDate())
                .append("至").append(plan.getExpirationDate()).append(")");
        return info.toString();
    }

    private void initBuildInRTKCoordinateSystem() {

        PopSpinnerView.OnSelectCallback callback = new PopSpinnerView.OnSelectCallback() {
            @Override
            public void onSelect(int position) {
                CoordinateSystem coordinateSystem = CoordinateSystem.WGS84;
                switch (position) {
                    case 0:
                        coordinateSystem = CoordinateSystem.WGS84;
                        break;
                    case 1:
                        coordinateSystem = CoordinateSystem.CGCS2000;
                        break;
                }
                final CoordinateSystem setCoordinateSystem = coordinateSystem;
                RTKNetworkServiceProvider.getInstance()
                        .setNetworkServiceCoordinateSystem(setCoordinateSystem, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    showToast(getAppResources().getString(R.string.error_setting_fail));
                                    setRtkCoordinateSystem(DataManager.getRtkCoordinateSystem());
                                } else {
                                    DataManager.setRtkCoordinateSystem(setCoordinateSystem);
                                    showToast(getAppResources().getString(R.string.message_setting_success));
                                }
                            }
                        });
            }
        };

        mRtkCoordinateSystemSs.init("", mRtkCoordinateSystemList, callback);

        CoordinateSystem rtkCoordinateSystem = DataManager.getRtkCoordinateSystem();
        setRtkCoordinateSystem(rtkCoordinateSystem);

      

    }

    private void getNetworkServiceCoordinateSystem() {
        RTKNetworkServiceProvider.getInstance().getNetworkServiceCoordinateSystem(new CommonCallbacks.CompletionCallbackWith<CoordinateSystem>() {
            @Override
            public void onSuccess(CoordinateSystem coordinateSystem) {
                setRtkCoordinateSystem(coordinateSystem);
                DataManager.setRtkCoordinateSystem(coordinateSystem);
                hasRequestGetCoordinateSystem = true;
              
            }

            @Override
            public void onFailure(DJIError djiError) {
                hasRequestGetCoordinateSystem = false;
              
            }
        });
    }

    private void setRtkCoordinateSystem(CoordinateSystem coordinateSystem) {
        switch (coordinateSystem) {
            case WGS84:
                mRtkCoordinateSystemSs.setSelect(0);
                break;
            case CGCS2000:
                mRtkCoordinateSystemSs.setSelect(1);
                break;
        }
    }

    private boolean createNetworkServiceSettings() {
        String cannot_empty = getAppResources().getString(R.string.cannot_empty);

        String address = mAddressEt.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            showToast(getAppResources().getString(R.string.network_rtk_address) + cannot_empty);
            return false;
        }

        String port = mPortEt.getText().toString().trim();
        if (TextUtils.isEmpty(port)) {
            showToast(getAppResources().getString(R.string.network_rtk_port) + cannot_empty);
            return false;
        }
        String mountPoint = mMountpointEt.getText().toString().trim();
        if (TextUtils.isEmpty(mountPoint)) {
            showToast(getAppResources().getString(R.string.network_rtk_mountpoint) + cannot_empty);
            return false;
        }
        String account = mAccountEt.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            showToast(getAppResources().getString(R.string.network_rtk_account) + cannot_empty);
            return false;
        }
        String password = mPasswordEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            showToast(getAppResources().getString(R.string.network_rtk_passwork) + cannot_empty);
            return false;
        }

        setKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_ADDRESS, address);
        setKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_PORT, port);
        setKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_MOUNTPOINT, mountPoint);
        setKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_ACCOUNT, account);
        setKeyValue(NTRIPConfigKey.RTK_NETWORK_SERVICE_PASSWORD, password);

        mNetworkServiceSettings = new NetworkServiceSettings.Builder()
                .ip(address)
                .port(Integer.valueOf(port))
                .mountPoint(mountPoint)
                .userName(account)
                .password(password)
                .build();
        return true;
    }


    private NetworkServiceState.Callback mNetworkServiceStateCallback = new NetworkServiceState.Callback() {
        @Override
        public void onNetworkServiceStateUpdate(final NetworkServiceState networkServiceState) {
            mUIThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    getNetWorkServiceState(networkServiceState);
                }
            });
        }
    };

    private void getNetWorkServiceState(NetworkServiceState networkServiceState) {
        NetworkServiceChannelState channelState = networkServiceState.getChannelState();

        if (channelState == NetworkServiceChannelState.TRANSMITTING) {
            mRtkStatusTv.setText("连接成功");
            mRtkStatusTv.setTextColor(getAppResources().getColor(R.color.green_dark));
            mRtkMessageTv.setText("");
            if (DataManager.getRtkReferenceStationSource() == NETWORK_RTK) {
                if (!hasRequestGetCoordinateSystem) {
                    getNetworkServiceCoordinateSystem();
                }
            }
        } else {
            mRtkStatusTv.setText("未连接成功");
            mRtkStatusTv.setTextColor(getAppResources().getColor(R.color.red));

            DJIRTKNetworkServiceError error = networkServiceState.getError();
            String errorMessage = "";
            if (error != null) {
                errorMessage = error.toString();
            }
            String message = "";
            switch (channelState) {
                case DISABLED:
                    message = "网络服务未启动";
                    break;
                case AIRCRAFT_DISCONNECTED:
                    message = "飞机没有连接";
                    break;
                case CONNECTING:
                    message = "正在连接服务器";
                    break;
                case LOGIN_FAILURE:
                    message = "网络RTK账号登录失败" + errorMessage;
                    break;
                case INVALID_REQUEST:
                    message = "服务器拒绝了无效请求";
                    break;
                case ACCOUNT_ERROR:
                    message = "网络RTK账号错误" + errorMessage;
                    break;
                case NETWORK_NOT_REACHABLE:
                    message = "网络错误";
                    break;
                case SERVER_NOT_REACHABLE:
                    message = "设备无法访问网络";
                    break;
                case SERVICE_SUSPENSION:
                    message = "当前网络RTK账号购买已过期";
                    break;
                case DISCONNECTED:
                    message = "通道已断开连接" + errorMessage;
                    break;
                default:
                case UNKNOWN:
                    message = "未知错误";
                    break;
            }

            mRtkMessageTv.setText("  (" + message + ")");
        }
    }

    private void restartRtkNetworkService() {

        if (!mOpenAircraftRtkSc.isChecked()) {
            showToast(getAppResources().getString(R.string.please_open_aircraft_rtk));
            return;
        }

        ReferenceStationSource rtkReferenceStationSource = DataManager.getRtkReferenceStationSource();
        switch (rtkReferenceStationSource) {
            case NETWORK_RTK:
                hasRequestGetCoordinateSystem = false;
                startRtkBuildInNetworkService();
                break;
            case CUSTOM_NETWORK_SERVICE:
                boolean isCreated = createNetworkServiceSettings();
                if (isCreated) {
                    startRtkCustomNetworkService();
                }
                break;
        }
    }

    private void startRtkBuildInNetworkService() {
        RTK rtk = RTKManager.getRTK();
        if (rtk != null) {
            rtk.setReferenceStationSource(NETWORK_RTK, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    CoordinateSystem rtkCoordinateSystem = DataManager.getRtkCoordinateSystem();
                    if (djiError == null) {
                        RTKNetworkServiceProvider.getInstance().setNetworkServiceCoordinateSystem(rtkCoordinateSystem, null);
                        RTKNetworkServiceProvider.getInstance().startNetworkService(null);
                    }
                }
            });
        } else {
            showToast(getAppResources().getString(R.string.rtk_get_fail));
        }
    }

    private void startRtkCustomNetworkService() {

        RTK rtk = RTKManager.getRTK();
        if (rtk != null) {
            rtk.setReferenceStationSource(CUSTOM_NETWORK_SERVICE, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        showToast("设置自定义网RTK错误：" + djiError.getDescription());
                    } else {
                        RTKNetworkServiceProvider.getInstance().setCustomNetworkSettings(mNetworkServiceSettings);
                        RTKNetworkServiceProvider.getInstance().startNetworkService(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    showToast(getAppResources().getString(R.string.error_setting_fail) + djiError.getDescription());
                                } else {
                                    showToast(getAppResources().getString(R.string.message_setting_success));
                                }
                            }
                        });
                    }
                }
            });

        } else {
            showToast(getAppResources().getString(R.string.rtk_get_fail));
        }
    }

    private void initTableLatLong() {
        TableRow tableRow1 = new TableRow(getContext());
        TextView row1_1 = createTableType(tableRow1);
        TextView row1_2 = createTableContent(tableRow1);
        row1_2.setText("飞机坐标");
        row1_2.setTextColor(getAppResources().getColor(R.color.white_8));
        TextView row1_3 = createTableContent(tableRow1);
        row1_3.setText("基站坐标");
        row1_3.setTextColor(getAppResources().getColor(R.color.white_8));

        tableRow1.addView(row1_1);
        tableRow1.addView(row1_2);
        tableRow1.addView(row1_3);

        TableRow tableRow2 = new TableRow(getContext());
        TextView row2_1 = createTableType(tableRow2);
        row2_1.setText("纬度");
        planeLatitudeTv = createTableContent(tableRow2);
        planeLatitudeTv.setText("0.00000000");
        baseLatitudeTV = createTableContent(tableRow2);
        baseLatitudeTV.setText("0.00000000");
        tableRow2.addView(row2_1);
        tableRow2.addView(planeLatitudeTv);
        tableRow2.addView(baseLatitudeTV);

        TableRow tableRow3 = new TableRow(getContext());
        TextView row3_1 = createTableType(tableRow3);
        row3_1.setText("经度");
        planeLongitudeTv = createTableContent(tableRow3);
        planeLongitudeTv.setText("0.00000000");
        baseLongitudeTv = createTableContent(tableRow3);
        baseLongitudeTv.setText("0.00000000");
        tableRow3.addView(row3_1);
        tableRow3.addView(planeLongitudeTv);
        tableRow3.addView(baseLongitudeTv);

        TableRow tableRow4 = new TableRow(getContext());
        TextView row4_1 = createTableType(tableRow4);
        row4_1.setText("海拔");
        planeAltitudeTv = createTableContent(tableRow4);
        planeAltitudeTv.setText("0.00");
        baseAltitudeTV = createTableContent(tableRow4);
        baseAltitudeTV.setText("0.00");
        tableRow4.addView(row4_1);
        tableRow4.addView(planeAltitudeTv);
        tableRow4.addView(baseAltitudeTV);

      /*  TableRow tableRow5 = new TableRow(getContext());
        TextView row5_1 = createTableType(tableRow5);
        row5_1.setText("定位");
        planeLocSolutionTv = createTableContent(tableRow5);
        planeLocSolutionTv.setText("N/A");
        baseLocSolutionTV = createTableContent(tableRow5);
        baseLocSolutionTV.setText("N/A");
        tableRow5.addView(row5_1);
        tableRow5.addView(planeLocSolutionTv);
        tableRow5.addView(baseLocSolutionTV);*/

        mTbStatusLatLong.addView(tableRow1);
        mTbStatusLatLong.addView(tableRow2);
        mTbStatusLatLong.addView(tableRow3);
        mTbStatusLatLong.addView(tableRow4);
      
    }

    private TextView createTableContent(TableRow tableRow) {
        return (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.item_dialog_rtk_status_table_row, tableRow, false);
    }

    private TextView createTableType(TableRow tableRow) {
        return (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.item_dialog_rtk_status_table_column, tableRow, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationStateEvent(LocationStateEvent locationStateEvent) {

        LocationCoordinate planeLoc = locationStateEvent.getAircraftCoordinate();
        LocationCoordinate baseLoc = locationStateEvent.getBaseCoordinate();

        if (planeLoc != null) {

            planeLatitude = planeLoc.getLatitude();
            planeLongitude = planeLoc.getLongitude();
            planeAltitude = planeLoc.getAltitude();

            planeLatitudeTv.setText(String.format("%.8f", planeLatitude));
            planeLongitudeTv.setText(String.format("%.8f", planeLongitude));
            planeAltitudeTv.setText(String.format("%.2fm", planeAltitude));
        }

        if (baseLoc != null) {

            baseLatitude = baseLoc.getLatitude();
            baseLongitude = baseLoc.getLongitude();
            baseAltitude = baseLoc.getAltitude();

            baseLatitudeTV.setText(String.format("%.6f", baseLatitude));
            baseLongitudeTv.setText(String.format("%.6f", baseLongitude));
            baseAltitudeTV.setText(String.format("%.2fm", baseAltitude));
        }
    }

    private String getKeyValue(String key) {
        Object object = FlyKeyManager.getInstance().getValue(NTRIPConfigKey
                .create(key));
        if (object instanceof String) {
            return (String) object;
        }
        return null;
    }

    private void setKeyValue(String key, String value) {
        FlyKeyManager.getInstance().setValue(NTRIPConfigKey
                .create(key), value);
    }


    @Override
    public void onDestroy() {
        RTKNetworkServiceProvider.getInstance().removeNetworkServiceStateCallback(mNetworkServiceStateCallback);
      
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.st_set_rtk:
                DataManager.setRtkReferenceStationSource(CUSTOM_NETWORK_SERVICE);
                restartRtkNetworkService();

                break;
            case R.id.btn_reconnect:
                restartRtkNetworkService();
                break;
            default:
                break;
        }
    }
    
   
}
