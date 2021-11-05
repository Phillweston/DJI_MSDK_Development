package com.ew.autofly.net.api;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.entity.sequoia.Calibration;
import com.ew.autofly.entity.sequoia.Capture;
import com.ew.autofly.entity.sequoia.Version;
import com.ew.autofly.entity.sequoia.Wifi;
import com.ew.autofly.entity.sequoia.config.Config;
import com.ew.autofly.entity.sequoia.config.ConfigStatus;
import com.ew.autofly.entity.sequoia.manualmode.ManualMode;
import com.ew.autofly.entity.sequoia.status.Gps;
import com.ew.autofly.entity.sequoia.status.Instruments;
import com.ew.autofly.entity.sequoia.status.Status;
import com.ew.autofly.entity.sequoia.status.Sunshine;
import com.ew.autofly.entity.sequoia.status.Temperature;
import com.ew.autofly.net.OkHttpUtil;
import com.flycloud.autofly.base.util.NetworkUtil;
import com.ew.autofly.utils.ToastUtil;
import com.google.gson.Gson;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import dji.thirdparty.okhttp3.OkHttpClient;

import static com.flycloud.autofly.base.util.NetworkUtil.WifiConnector.WifiCipherType.WIFICIPHER_NOPASS;
import static com.flycloud.autofly.base.util.NetworkUtil.isWifi;

/**
 * 多光谱传感器控制接口
 * http://developer.parrot.com/docs/sequoia/
 */

public class SequoiaApi {

    public final static String sequoia_wifi = "Sequoia";

    public final static String sequoia_ip = "http://192.168.47.1/";

    private OkHttpUtil mOkHttp;

    public OkHttpUtil getOkHttp() {
        return mOkHttp;
    }

    private static final SequoiaApi INSTANCE = new SequoiaApi();

    private SequoiaApi() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(2000, TimeUnit.MILLISECONDS);
        mOkHttp = OkHttpUtil.newInstance(builder);
    }

    public static SequoiaApi getInstance() {
        return INSTANCE;
    }

    public static boolean checkConnect(Context context) {

        if (isWifi(context)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                String wifiSSID = wifiManager.getConnectionInfo().getSSID();
                if (wifiSSID != null) {
                    wifiSSID = wifiSSID.replace("\"", "");
                }
                if (wifiSSID != null && wifiSSID.contains(sequoia_wifi)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void connect(NetworkUtil.WifiConnector wifiConnector) {
        wifiConnector.connect(sequoia_wifi,"",WIFICIPHER_NOPASS);
    }

    public static void disConnect(NetworkUtil.WifiConnector wifiConnector){
        wifiConnector.disConnect(sequoia_wifi,"",WIFICIPHER_NOPASS);
    }

    public static void checkConnectAndGet(Context context, String url, OkHttpUtil.HttpCallBack callBack) {

        if (checkConnect(context)) {
            getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + url, callBack);
        } else {
            callBack.onError(new ConnectException(EWApplication.getInstance().getResources().getString(R.string.multispectral_please_conncet)));
            ToastUtil.show(context, EWApplication.getInstance().getResources().getString(R.string.multispectral_please_conncet));
        }
    }

    public static <T> void checkConnectAndPostJson(Context context, String url, T jsonBean, OkHttpUtil.HttpCallBack callBack) {

        if (checkConnect(context)) {
            getInstance().getOkHttp()._doPostAsyncUI(sequoia_ip + url, new Gson().toJson(jsonBean), callBack);
        } else {
            callBack.onError(new ConnectException(EWApplication.getInstance().getResources().getString(R.string.multispectral_please_conncet)));
            ToastUtil.show(context, EWApplication.getInstance().getResources().getString(R.string.multispectral_please_conncet));
        }

    }


    public static void capture(Context context, OkHttpUtil.HttpCallBack<Capture> callBack) {
        checkConnectAndGet(context, "capture", callBack);
    }

    public static void captureStart(Context context, OkHttpUtil.HttpCallBack<Capture> callBack) {
        checkConnectAndGet(context, "capture/start", callBack);
    }

    public static void captureStop(Context context, OkHttpUtil.HttpCallBack<Capture> callBack) {
        checkConnectAndGet(context, "capture/stop", callBack);
    }

    public static void config(Context context, OkHttpUtil.HttpCallBack<Config> callBack) {
        checkConnectAndGet(context, "config", callBack);
    }

    public static void setConfig(Context context, Config config, OkHttpUtil.HttpCallBack<ConfigStatus> callBack) {
        checkConnectAndPostJson(context, "config", config, callBack);
    }

    public static void status(Context context, OkHttpUtil.HttpCallBack<Status> callBack) {
        checkConnectAndGet(context, "status", callBack);
    }

    public static void statusGps(Context context, OkHttpUtil.HttpCallBack<Gps> callBack) {
        checkConnectAndGet(context, "status/gps", callBack);
    }

    public static void statusInstruments(OkHttpUtil.HttpCallBack<Instruments> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "status/instruments", callBack);
    }

    public static void statusSunshine(OkHttpUtil.HttpCallBack<Sunshine> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "status/sunshine", callBack);
    }

    public static void statusTemperature(OkHttpUtil.HttpCallBack<Temperature> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "status/temperature", callBack);
    }

    public static void calibration(Context context, OkHttpUtil.HttpCallBack<Calibration> callBack) {
        checkConnectAndGet(context, "calibration", callBack);
    }

    public static void calibrationStart(Context context, OkHttpUtil.HttpCallBack<Calibration> callBack) {
        checkConnectAndGet(context, "calibration/start", callBack);
    }

    public static void calibrationStop(Context context, OkHttpUtil.HttpCallBack<Calibration> callBack) {
        checkConnectAndGet(context, "calibration/stop", callBack);
    }

    public static void calibrationRadiometricStart(Context context, OkHttpUtil.HttpCallBack<Capture> callBack) {
        checkConnectAndGet(context, "calibration/radiometric/start", callBack);
    }

    public static void calibrationBodyStart(OkHttpUtil.HttpCallBack<Calibration> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "calibration/body/start", callBack);
    }

    public static void calibrationBodyStop(OkHttpUtil.HttpCallBack<Calibration> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "calibration/body/stop", callBack);
    }

    public static void calibrationSunshineStart(OkHttpUtil.HttpCallBack<Calibration> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "calibration/sunshine/start", callBack);
    }

    public static void calibrationSunshineStop(OkHttpUtil.HttpCallBack<Calibration> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "calibration/sunshine/stop", callBack);
    }

    public static void storage(Context context, OkHttpUtil.HttpCallBack callBack) {
        checkConnectAndGet(context, "storage", callBack);
    }

    public static void delete(String filePath) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "delete" + filePath, null);
    }

    public static void version(OkHttpUtil.HttpCallBack<Version> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "version", callBack);
    }

    public static void wifi(OkHttpUtil.HttpCallBack<Wifi> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "wifi", callBack);
    }

    public static void setWifi(Wifi wifi) {
        getInstance().getOkHttp()._doPostAsyncUI(sequoia_ip + "wifi", new Gson().toJson(wifi), null);
    }

    public static void manualmode(OkHttpUtil.HttpCallBack<Wifi> callBack) {
        getInstance().getOkHttp()._doGetAsyncUI(sequoia_ip + "manualmode", callBack);
    }

    public static void setManualmode(ManualMode manualmode, OkHttpUtil.HttpCallBack<Wifi> callBack) {
        getInstance().getOkHttp()._doPostAsyncUI(sequoia_ip + "manualmode", new Gson().toJson(manualmode), callBack);
    }
}
