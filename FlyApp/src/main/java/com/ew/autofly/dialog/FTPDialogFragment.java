package com.ew.autofly.dialog;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.utils.IOUtils;
import com.flycloud.autofly.ux.base.BaseUxDialog;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



public class FTPDialogFragment extends BaseUxDialog {
    static {
        System.setProperty("java.net.preferIPv6Addresses", "false");
    }

    int port = 7909;
    private FtpServer mFtpServer;
    private String ftpConfigDir = IOUtils.getRootStoragePath(getActivity()) + AppConstant.DIR_FTP_CONFIG + File.separator;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_ftp, container, false);
        String addr = "ftp://" + getLocalIpAddress() + ":" + port;
        ((TextView) view.findViewById(R.id.txt_ftp_address_1)).setText(addr);
        ((TextView) view.findViewById(R.id.txt_ftp_address_2)).setText(addr);
        ((TextView) view.findViewById(R.id.txt_net_name)).setText(getWiFiName());
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        initConfigFile();
        startFtpServer();
        return view;
    }

    private void startFtpServer() {
        if (mFtpServer != null && !mFtpServer.isStopped()) {
            return;
        }
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(port);
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        String filename = ftpConfigDir + "users.properties";
        File files = new File(filename);
        userManagerFactory.setFile(files);
        serverFactory.setUserManager(userManagerFactory.createUserManager());
        serverFactory.addListener("default", factory.createListener());
        FtpServer server = serverFactory.createServer();
        this.mFtpServer = server;
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreateSize() {
        setSize(0.5f, 0.5f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mFtpServer) {
            mFtpServer.stop();
            mFtpServer = null;
        }
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public String getLocalIpAddress() {
        WifiManager wm = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wm.isWifiEnabled()) { // 没开启wifi时,ip地址为0.0.0.0
            WifiInfo wifiinfo = wm.getConnectionInfo();
            return intToIp(wifiinfo.getIpAddress());
        } else {
            return "0.0.0.0";
        }
    }

    public String getWiFiName() {
        WifiManager wm = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            WifiInfo wifiInfo = wm.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getSSID();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    private void copyResourceFile(int rid, String targetFile) {
        InputStream fin = getActivity().getResources().openRawResource(rid);
        FileOutputStream fos = null;
        int length;
        try {
            fos = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            while ((length = fin.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initConfigFile() {
        File f = new File(ftpConfigDir);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public void shutDownFTP() {
        if (null != mFtpServer) {
            mFtpServer.stop();
            mFtpServer = null;
        }
    }
}