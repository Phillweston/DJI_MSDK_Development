package com.flycloud.autofly.base.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * 检测网络
 *
 * @author Administrator
 */
public class NetworkUtil {


    public static final int NONETWORK = 0;

    public static final int WIFI = 1;

    public static final int NOWIFI = 2;

    /**
     * 检验网络连接 并判断是否是wifi连接
     *
     * @param context
     * @return <li>没有网络：NetworkUtil.NONETWORK;</li> <li>wifi 连接：NetworkUtil.WIFI;</li> <li>mobile 连接：NetworkUtil.NOWIFI</li>
     */
    public static int checkNetWorkType(Context context) {

        if (!checkNetWork(context)) {
            return NetworkUtil.NONETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting())
            return NetworkUtil.WIFI;
        else
            return NetworkUtil.NOWIFI;
    }

    /**
     * 检测网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean checkNetWork(Context context) {
      
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE)) {
            return true;
        }
        return false;
    }

    /**
     * 判断GPS是否已经打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = ((LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = lm.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }


    /**
     * 判断WIFI是否已打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
       /* ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);*/
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 判断当前网络是否是wifi网络
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否是指定SSID wifi网络
     *
     * @param context
     * @return boolean
     */
    public static boolean isWifiSSID(Context context, String SSID) {
        if (isWifi(context)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                String wifiSSID = wifiManager.getConnectionInfo().getSSID();
                if (wifiSSID != null) {
                    wifiSSID = wifiSSID.replace("\"", "");
                }
                if (SSID.equals(wifiSSID)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断3G网络是否已打开
     *
     * @param context
     * @return
     */
    public static boolean is3rd(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * @return --经检测，此方法不可用。总是返回false
     * @author suncat
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @ip 要Ping的IP地址
     */
    public static final boolean ping(String ip) {

        String result = null;
        try {
            Process p = Runtime.getRuntime().exec("/system/bin/ping -c 3 -w 1 " + ip);
          
            /*InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
			}*/
          
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {

        }
        return false;
    }

    public static class WifiConnector {
        Handler mHandler;

        WifiManager wifiManager;

        /**
         * 向UI发送消息
         * @param info 消息
         */
        public void sendMsg(String info) {
            if (mHandler != null) {
                Message msg = new Message();
                msg.obj = info;
                mHandler.sendMessage(msg);
            } else {
                Log.e("wifi", info);
            }
        }

      
        public enum WifiCipherType {
            WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
        }

      
        public WifiConnector(Context context) {
            this(context, null);
        }

      
        public WifiConnector(Context context, Handler.Callback callback) {
            this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (callback != null) {
                this.mHandler = new Handler(callback);
            }

        }

      
        public void connect(String ssid, String password, WifiCipherType type) {
            Thread thread = new Thread(new ConnectRunnable(ssid, password, type));
            thread.start();
        }

      
        public void disConnect(String ssid, String password, WifiCipherType type) {
            WifiConfiguration wifiConfig = createWifiInfo(ssid, password, type);
            int netId = wifiConfig.networkId;
            if (netId == -1) {
                netId = wifiManager.addNetwork(wifiConfig);
            }
            wifiManager.disableNetwork(netId);
        }

      
        private WifiConfiguration isExsits(String SSID) {
            List<WifiConfiguration> existingConfigs = wifiManager
                    .getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
            return null;
        }

        private WifiConfiguration createWifiInfo(String SSID, String Password,
                                                 WifiCipherType Type) {
            WifiConfiguration config = new WifiConfiguration();
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + SSID + "\"";
          
            if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
          
            else if (Type == WifiCipherType.WIFICIPHER_WEP) {
                if (!TextUtils.isEmpty(Password)) {
                    if (isHexWepKey(Password)) {
                        config.wepKeys[0] = Password;
                    } else {
                        config.wepKeys[0] = "\"" + Password + "\"";
                    }
                }
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
            }
          
            else if (Type == WifiCipherType.WIFICIPHER_WPA) {
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
              
              
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
            }
            return config;
        }

      
        private boolean openWifi() {
            boolean bRet = true;
            if (!wifiManager.isWifiEnabled()) {
                bRet = wifiManager.setWifiEnabled(true);
            }
            return bRet;
        }

        class ConnectRunnable implements Runnable {
            private String ssid;

            private String password;

            private WifiCipherType type;

            public ConnectRunnable(String ssid, String password, WifiCipherType type) {
                this.ssid = ssid;
                this.password = password;
                this.type = type;
            }

            @Override
            public void run() {
                try {
                  
                    openWifi();
                    sendMsg("opened");
                    Thread.sleep(200);
                  
                  
                    while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                        try {
                          
                            Thread.sleep(100);
                        } catch (InterruptedException ie) {
                        }
                    }

                    WifiConfiguration wifiConfig = createWifiInfo(ssid, password,
                            type);
                    //
                    if (wifiConfig == null) {
                        sendMsg("wifiConfig is null!");
                        return;
                    }

                    WifiConfiguration tempConfig = isExsits(ssid);

                    if (tempConfig != null) {
                        wifiManager.removeNetwork(tempConfig.networkId);
                    }

                    int netID = wifiManager.addNetwork(wifiConfig);
                    boolean enabled = wifiManager.enableNetwork(netID, true);
                    sendMsg("enableNetwork status enable=" + enabled);
                  
                  
                    sendMsg("连接成功!");
                } catch (Exception e) {
                  
                    sendMsg(e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        private static boolean isHexWepKey(String wepKey) {
            final int len = wepKey.length();

          
            if (len != 10 && len != 26 && len != 58) {
                return false;
            }

            return isHex(wepKey);
        }

        private static boolean isHex(String key) {
            for (int i = key.length() - 1; i >= 0; i--) {
                final char c = key.charAt(i);
                if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                        && c <= 'f')) {
                    return false;
                }
            }

            return true;
        }
    }
}
