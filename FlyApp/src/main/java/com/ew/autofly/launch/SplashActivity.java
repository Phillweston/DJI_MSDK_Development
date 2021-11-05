package com.ew.autofly.launch;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.event.DJIRegistrationEvent;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.flycloud.autofly.base.base.BaseActivity;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.control.event.DevRegEvent;
import com.flycloud.autofly.control.logic.RouteController;
import com.flycloud.autofly.control.service.RouteService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

import de.mrapp.android.dialog.MaterialDialog;
import dji.sdk.sdkmanager.DJISDKManager;


public class SplashActivity extends BaseActivity {

    protected Dialog mCheckDjiRegistrationDlg = null;

    private static final int SPLASH_DISPLAY = 2000;

    private MyHandler handler;

    private PendingIntent mPermissionIntent;

    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE, // Gimbal rotation
            Manifest.permission.INTERNET, // API requests
            Manifest.permission.ACCESS_WIFI_STATE, // WIFI connected products
            Manifest.permission.ACCESS_COARSE_LOCATION, // Maps
            Manifest.permission.ACCESS_NETWORK_STATE, // WIFI connected products
            Manifest.permission.ACCESS_FINE_LOCATION, // Maps
            Manifest.permission.CHANGE_WIFI_STATE, // Changing between WIFI and USB connection
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // Log files
            Manifest.permission.BLUETOOTH, // Bluetooth connected products
            Manifest.permission.BLUETOOTH_ADMIN, // Bluetooth connected products
            Manifest.permission.READ_EXTERNAL_STORAGE, // Log files
            Manifest.permission.READ_PHONE_STATE, // Device UUID accessed upon registration
            Manifest.permission.RECORD_AUDIO // Speaker accessory
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_splash);
        initVars();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {




    }

    private void checkDeviceRegistrationState() {

        gotoMainActivity();
    }

    private void initVars() {

        handler = new MyHandler(this);


        sendUSBAttachBroadcast();

        checkPermissions();
    }

    private void sendUSBAttachBroadcast() {

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        Intent intent = this.getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_ACCESSORY_ATTACHED".equals(action)) {
                Intent i = new Intent();
                i.setAction(DJISDKManager.USB_ACCESSORY_ATTACHED);
                this.sendBroadcast(i);

                UsbAccessory accessory = (UsbAccessory) getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                if (accessory != null) {
                    UsbManager mUsbManager = (UsbManager) getSystemService(USB_SERVICE);

                    if (mUsbManager != null) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                            ToastUtil.show(SplashActivity.this, "遥控器连接成功");
                        } else {
                            mUsbManager.requestPermission(accessory, mPermissionIntent);

                        }
                    }
                }
            }
        }
    }

    private static final String ACTION_USB_PERMISSION = "com.flycloud.autofly.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        ToastUtil.show(SplashActivity.this, "遥控器连接成功");
                    } else {
                        ToastUtil.show(SplashActivity.this, "遥控器连接失败");
                    }
                }
            }
        }
    };

    private void gotoMainActivity() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RouteController.gotoAppMainActivity();
                finish();
            }
        }, SPLASH_DISPLAY);
    }

    static class MyHandler extends Handler {
        WeakReference<Activity> mActivityRef;

        MyHandler(Activity activity) {
            mActivityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivityRef.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    
    private void showCheckDjiRegistrationDlg() {

        if (mCheckDjiRegistrationDlg == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.sdk_registration_message))
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mCheckDjiRegistrationDlg != null && mCheckDjiRegistrationDlg.isShowing()) {
                                mCheckDjiRegistrationDlg.dismiss();
                            }
                            EWApplication.getInstance().startSDKRegistration();
                        }
                    }).setCancelable(false);
            mCheckDjiRegistrationDlg = builder.create();
        }
        mCheckDjiRegistrationDlg.show();
    }

    
    private void checkPermissions() {
        XXPermissions.with(this)



                .permission(REQUIRED_PERMISSION_LIST)
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        if (isAll) {
                            EWApplication.getInstance().startSDKRegistration();
                            EWApplication.getInstance().startInitProcess();
                        } else {
                            showOnDenyPermission();
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
    }

    private void showOnDenyPermission() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(SplashActivity.this, R.style.TransparentDialogTheme);
        builder.setTitle(getString(R.string.system_permission_request));
        builder.setTitleColor(getResources().getColor(R.color.black));
        builder.setMessage(getString(R.string.system_permission_request_explain));
        builder.setMessageColor(getResources().getColor(R.color.black));
        builder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exitAPP();
            }
        });
        builder.setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                XXPermissions.gotoPermissionSettings(SplashActivity.this);
            }
        });
        builder.create().show();

    }

    private void exitAPP() {
        ActivityManager activityManager = (ActivityManager) SplashActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDJIRegistrationEvent(DJIRegistrationEvent event) {
        if (event.isSuccess()) {
            checkDeviceRegistrationState();
        } else {
            showCheckDjiRegistrationDlg();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mUsbReceiver != null) {
            unregisterReceiver(mUsbReceiver);
        }
    }
}