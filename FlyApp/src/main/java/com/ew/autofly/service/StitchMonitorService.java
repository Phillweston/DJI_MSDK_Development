package com.ew.autofly.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.net.api.BoxApi;
import com.ew.autofly.utils.LogUtilsOld;



public class StitchMonitorService extends Service {

    private boolean monitorFlag = false;

    private Mission2 mMission;

    private final IBinder mBinder = new MyBinder();

    private String mosaicStep="";

    public class MyBinder extends Binder {
        public StitchMonitorService getService() {
            return StitchMonitorService.this;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        monitorFlag=true;
        startStitchMonitor();
        return super.onStartCommand(intent,
                Service.START_FLAG_REDELIVERY, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            mMission = (Mission2) intent.getSerializableExtra("mission");
        }
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        sendMonitorBroadcast();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        monitorFlag = false;
        super.onDestroy();
    }

    public Mission2 getMission() {
        return mMission;
    }

    public void stopStitchMonitor() {
        monitorFlag = false;
    }

    public boolean isStopStitch(){

        return mosaicStep.contains("智能快拼软件已经开启");
    }


    public void startStitchMonitor() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mMission != null) {
                    while (monitorFlag) {
                        try {

                            Thread.sleep(1000);

                            mosaicStep = BoxApi.getMosaicStep(mMission.getId());

                            if (!mosaicStep.isEmpty() && !mosaicStep.equals("0")) {

                                LogUtilsOld.getInstance(StitchMonitorService.this).e("StitchMonitorService:"+mosaicStep);

                                if (mosaicStep.contains("拼接完成")
                                        || mosaicStep.contains("失败")
                                        || mosaicStep.contains("找不到照片文件夹")) {
                                    monitorFlag = false;
                                }else if(mosaicStep.contains("终止成功")){
                                    continue;
                                }else if(isStopStitch()){
                                    continue;
                                }

                                sendMonitorBroadcast();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            LogUtilsOld.getInstance(StitchMonitorService.this).e("StitchMonitorService:"+e.toString());
                        }
                    }
                }

                stopSelf();

            }
        }).start();
    }

    private void sendMonitorBroadcast(){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(StitchMonitorService.this);
        Intent intent = new Intent(AppConstant.BROADCAST_STITCH_MONITOR);
        intent.putExtra("stepMsg", mosaicStep);
        localBroadcastManager.sendBroadcast(intent);
    }

}
