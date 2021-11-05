package com.ew.autofly.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.event.DJIRegistrationEvent;
import com.ew.autofly.mode.linepatrol.point.ui.db.LocalDataSource;
import com.ew.autofly.mode.linepatrol.point.ui.db.LocalDataSourceImpl;
import com.ew.autofly.mode.linepatrol.point.ui.db.Repository;
import com.ew.autofly.mode.linepatrol.point.ui.db.RepositoryImpl;
import com.ew.autofly.model.ConnectManager;
import com.ew.autofly.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;

import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;



public class DJIApplication extends Application {

    private AtomicBoolean isRegistrationInProgress = new AtomicBoolean(false);

    private DJISDKManager.SDKManagerCallback mDJISDKManagerCallback;

    private BaseComponent.ComponentListener mDJIComponentListener;
    private static BaseProduct mProduct;
    private BaseComponent mComponent;
    private BaseProduct.ComponentKey mComponentKey;
    public Handler mHandler;

    private Application instance;


    public void setContext(Application application) {
        instance = application;
    }

    @Override
    public Context getApplicationContext() {
        return instance;
    }

    public DJIApplication() {

    }

    /**
     * This function is used to get the instance of DJIBaseProduct.
     * If no product is connected, it returns null.
     */
    public static synchronized BaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
    }

    public static synchronized Camera getCameraInstance() {

        if (getProductInstance() == null) return null;

        Camera camera = null;

        if (getProductInstance() instanceof Aircraft) {
            camera = ((Aircraft) getProductInstance()).getCamera();

        } else if (getProductInstance() instanceof HandHeld) {
            camera = ((HandHeld) getProductInstance()).getCamera();
        }

        return camera;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        mDJIComponentListener = new BaseComponent.ComponentListener() {

            @Override
            public void onConnectivityChange(boolean isConnected) {

                ConnectManager.getInstance().onComponentChange(mComponentKey, mComponent, isConnected);

                notifyStatusChange();
            }

        };

        mDJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError djiError) {

                isRegistrationInProgress.set(false);
                if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                    EventBus.getDefault().post(new DJIRegistrationEvent(true));
                } else {
                    EventBus.getDefault().post(new DJIRegistrationEvent(false));
                }
            }

            @Override
            public void onProductDisconnect() {

                ConnectManager.getInstance().onProductDisconnect();

                notifyStatusChange();
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {

                ConnectManager.getInstance().onProductConnect(baseProduct);

                notifyStatusChange();
            }

            @Override
            public void onProductChanged(BaseProduct baseProduct) {

            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent, BaseComponent newComponent) {

                if (newComponent != null) {
                    mComponent = newComponent;
                    mComponentKey = componentKey;
                    newComponent.setComponentListener(mDJIComponentListener);
                }

                ConnectManager.getInstance().onComponentChange(mComponentKey, mComponent, true);

                notifyStatusChange();
            }

            @Override
            public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

            }

            @Override
            public void onDatabaseDownloadProgress(long l, long l1) {

            }

         /*   @Override
            public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

            }*/
        };

    }

  
    public void startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    DJISDKManager.getInstance().registerApp(getApplicationContext(), mDJISDKManagerCallback);
                }
            });
        }
    }

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(AppConstant.BROADCAST_DJI_PRODUCT_CONNECTION_CHANGE);
            getApplicationContext().sendBroadcast(intent);
        }
    };
}
