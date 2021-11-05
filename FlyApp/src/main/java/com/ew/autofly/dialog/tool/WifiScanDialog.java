package com.ew.autofly.dialog.tool;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;



public class WifiScanDialog extends BaseDialogFragment implements View.OnClickListener {

    private static final int WIFICIPHER_NOPASS = 1;
    private static final int WIFICIPHER_WEP = 2;
    private static final int WIFICIPHER_WPA = 3;

    public static final int DEVICE_CONNECTING = 1;
    public static final int DEVICE_CONNECTED = 2;
    public static final int SEND_MSG_SUCCSEE = 3;
    public static final int SEND_MSG_ERROR = 4;
    public static final int GET_MSG = 6;

    private Context mContext;

    private WifiManager wifiManager;
    private WifiConfiguration config;
    private int wcgID;

    private ListView mLvWifiList;
    private WifiAdapter wifiListAdapter;

    private String mFilterSSID;
    private String mTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_scan_list, container, false);
        initView(view);
        initBroadcastReceiver();
        wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void onCreateSize() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                DisplayMetrics dm = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                if (dm.widthPixels > dm.heightPixels)
                    window.setLayout((int) (dm.widthPixels * 0.6), (int) (dm.heightPixels * 0.8));
                else
                    window.setLayout((int) (dm.heightPixels * 0.8), (int) (dm.widthPixels * 0.6));
            }
        }
    }

    private void initView(View view) {
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tv_title)).setText(mTitle);
        mLvWifiList = (ListView) view.findViewById(R.id.lv_result_list);

        initWifiAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }


    public void setTitle(String title) {
        this.mTitle = title;
    }


    public void setFilterSSID(String filterSSID) {
        this.mFilterSSID = filterSSID;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {


                refreshWifiList(NetworkInfo.DetailedState.CONNECTED);

            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {

                int wifiState = intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:

                        wifiManager.startScan();
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:

                        wifiListAdapter.clear();
                        break;
                }
            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {


                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

                    refreshWifiList(NetworkInfo.DetailedState.CONNECTED);

                } else {
                    NetworkInfo.DetailedState state = info.getDetailedState();
                    refreshWifiList(state);
                }

            }
           /* else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    text_state.setText("连接已断开");
                    wifiManager.removeNetwork(wcgID);
                } else {
                    WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    text_state.setText("已连接到网络:" + wifiInfo.getSSID());
                }
            }*/
        }
    };

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);


        getContext().registerReceiver(receiver, intentFilter);
    }

    private void initWifiAdapter() {
        wifiListAdapter = new WifiAdapter(getContext());
        mLvWifiList.setAdapter(wifiListAdapter);

        mLvWifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wifiManager.disconnect();
                final ScanResult scanResult = wifiListAdapter.getItem(position).getScanResult();
                String capabilities = scanResult.capabilities;
                int type = WIFICIPHER_WPA;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        type = WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        type = WIFICIPHER_WEP;
                    } else {
                        type = WIFICIPHER_NOPASS;
                    }
                }
                config = isExsits(scanResult.SSID);
                if (config == null) {
                    if (type != WIFICIPHER_NOPASS) {//需要密码
                        final EditText editText = new EditText(getContext());
                        final int finalType = type;
                        new AlertDialog.Builder(getContext()).setTitle("请输入Wifi密码").setIcon(
                                android.R.drawable.ic_dialog_info).setView(
                                editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.w("AAA", "editText.getText():" + editText.getText());
                                config = createWifiInfo(scanResult.SSID, editText.getText().toString(), finalType);
                                connect(config);
                            }
                        })
                                .setNegativeButton("取消", null).show();
                        return;
                    } else {
                        config = createWifiInfo(scanResult.SSID, "", type);
                        connect(config);
                    }
                } else {
                    connect(config);
                }
            }
        });
    }

    private void refreshWifiList(NetworkInfo.DetailedState state) {
        wifiListAdapter.clear();
        List<WifiResult> wifiResults = getConnectedWifiResultList(state);
        if (wifiResults != null) {
            wifiListAdapter.addAll(wifiResults);
        }
    }

    private List<WifiResult> getWifiResultList() {
        List<ScanResult> scanResults = wifiManager.getScanResults();
        List<WifiResult> wifiResults = new ArrayList<>();
        if (scanResults != null) {
            for (ScanResult scanResult : scanResults) {

                if (!TextUtils.isEmpty(mFilterSSID)) {

                    if (scanResult != null && scanResult.SSID != null
                            && scanResult.SSID.contains(mFilterSSID)) {

                        WifiResult wifiResult = new WifiResult();
                        wifiResult.setScanResult(scanResult);
                        wifiResults.add(wifiResult);
                    }

                } else {
                    WifiResult wifiResult = new WifiResult();
                    wifiResult.setScanResult(scanResult);
                    wifiResults.add(wifiResult);
                }

            }
        }

        return wifiResults;
    }

    private List<WifiResult> getConnectedWifiResultList(NetworkInfo.DetailedState state) {
        return getConnectedWifiResultList(null, state);
    }

    private List<WifiResult> getConnectedWifiResultList(String SSID, NetworkInfo.DetailedState state) {

        String wifiSSID = "";

        if (TextUtils.isEmpty(SSID)) {
            WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.getConnectionInfo() != null) {
                wifiSSID = wifiManager.getConnectionInfo().getSSID();
                if (wifiSSID != null) {
                    wifiSSID = wifiSSID.replace("\"", "");
                }
            }
        } else {
            wifiSSID = SSID;
        }

        List<WifiResult> wifiResults = getWifiResultList();
        if (wifiResults != null) {
            for (WifiResult wifiResult : wifiResults) {
                ScanResult scanResult = wifiResult.getScanResult();
                if (scanResult.SSID.equals(wifiSSID)) {
                    wifiResult.setState(state);
                } else {
                    wifiResult.setState(NetworkInfo.DetailedState.DISCONNECTED);
                }
            }
        }
        return wifiResults;
    }

    private void connect(WifiConfiguration config) {

        refreshWifiList(NetworkInfo.DetailedState.CONNECTING);
        wcgID = wifiManager.addNetwork(config);
        wifiManager.enableNetwork(wcgID, true);
    }

    /**
     * 判断当前wifi是否有保存
     *
     * @param SSID
     * @return
     */
    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public WifiConfiguration createWifiInfo(String SSID, String password,
                                            int type) {

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);

            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            return null;
        }
        return config;
    }

    private void requestLocationPermission(){

        if (Build.VERSION.SDK_INT>=23&&getActivity()!=null) {
            int hasPermission=getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasPermission!=PackageManager.PERMISSION_GRANTED){
                getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    private class WifiAdapter extends ArrayAdapter<WifiResult> {

        private final LayoutInflater mInflater;

        public WifiAdapter(Context context) {
            super(context, 0);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_wifi_list, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.wifi_name);
            TextView signl = (TextView) convertView.findViewById(R.id.wifi_signal);
            TextView state = (TextView) convertView.findViewById(R.id.wifi_state);

            WifiResult wifiResult = getItem(position);
            ScanResult scanResult = wifiResult.getScanResult();
            name.setText(scanResult.SSID);

            int level = scanResult.level;
            if (level <= 0 && level >= -50) {
                signl.setText("信号很好");
            } else if (level < -50 && level >= -70) {
                signl.setText("信号较好");
            } else if (level < -70 && level >= -80) {
                signl.setText("信号一般");
            } else if (level < -80 && level >= -100) {
                signl.setText("信号较差");
            } else {
                signl.setText("信号很差");
            }

            switch (wifiResult.getState()) {
                case CONNECTED:
                    state.setText("已连接");
                    break;
                case CONNECTING:
                    state.setText("连接中...");
                    break;
                case AUTHENTICATING:
                    state.setText("正在验证身份信息...");
                    break;
                case OBTAINING_IPADDR:
                    state.setText("正在获取IP地址...");
                    break;
                case FAILED:
                    state.setText("连接失败");
                    break;
                default:
                    state.setText("");
                    break;
            }

            return convertView;
        }

    }

    private class WifiResult {

        private ScanResult scanResult;

        private NetworkInfo.DetailedState state = NetworkInfo.DetailedState.DISCONNECTED;

        public ScanResult getScanResult() {
            return scanResult;
        }

        public void setScanResult(ScanResult scanResult) {
            this.scanResult = scanResult;
        }

        public NetworkInfo.DetailedState getState() {
            return state;
        }

        public void setState(NetworkInfo.DetailedState state) {
            this.state = state;
        }
    }
}
