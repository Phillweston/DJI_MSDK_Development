package com.ew.autofly.model;

import com.ew.autofly.key.cache.FlyFlightControllerKey;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.internal.key.base.FlyKeyManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import dji.common.product.Model;


public class VideoRecordManager {

    private FlyFlightControllerKey mFlightControllerKey = FlyFlightControllerKey.create(FlyFlightControllerKey.AIRCRAFT_LOCATION);

    private AtomicBoolean isRecordingVideo = new AtomicBoolean(false);

    private StringBuilder stringBuilder = new StringBuilder();
    private Timer timer;
    private long currentTimeMillis;

    public boolean isRecording() {
        return isRecordingVideo.get();
    }

    public void startRecordVideoPoint() {
        if (isRecording()) {
            return;
        }
        if (isRecordingVideo.compareAndSet(false, true)) {
            currentTimeMillis = System.currentTimeMillis();
        }
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis() - currentTimeMillis;
                    Object object = FlyKeyManager.getInstance().getValue(mFlightControllerKey);
                    if (object instanceof LocationCoordinate) {
                        LocationCoordinate locationCoordinate = (LocationCoordinate) object;
                        stringBuilder.append(time).append(" ")
                                .append(locationCoordinate.getLongitude()).append(" ")
                                .append(locationCoordinate.getLatitude()).append("\n");
                    }

                }
            }, 0, 200);
        }
    }

    public void finishRecordVideoPoint() {
        isRecordingVideo.set(false);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void writeVideoDataIntoFile(String name) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        DataOutputStream dos;
        try {
            if (name != null)
                dos = new DataOutputStream(new FileOutputStream(AppConstant.LOG_PATH + File.separator + name + ".bin"));
            else
                dos = new DataOutputStream(new FileOutputStream(AppConstant.LOG_PATH + File.separator + format.format(new Date()) + ".bin"));

            dos.writeUTF("时间：" + format.format(new Date()));

            Model model = AircraftManager.getModel();
            if (model == null)
                dos.writeUTF("机型：" + "Unknow Aircraft");
            else
                dos.writeUTF("机型：" + model.getDisplayName());
            dos.writeUTF(stringBuilder.toString());
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stringBuilder = new StringBuilder();
    }

    public void destroy() {
        finishRecordVideoPoint();
    }
}
