package com.ew.autofly.utils;

import android.graphics.Bitmap;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.mapping.view.GeoView;
import com.esri.core.geometry.Point;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.db.entity.MissionBatch;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.MissionBatch2;
import com.ew.autofly.interfaces.common.CommonCallbackWith;
import com.ew.autofly.utils.tool.ImageUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



public class MissionUtil {

    public static String convertToGeometry(Mission2 mission) {
        String xys = "";
        try {
            StringBuilder sb = new StringBuilder();
            int count;
            switch (mission.getGeomType()) {
                case 0:
                    count = mission.getMultiPoint().getPointCount();
                    for (int i = 0; i < count; i++) {
                        Point p = mission.getMultiPoint().getPoint(i);
                        sb.append(concatCoords(p));
                    }
                    break;
                case 1:
                    count = mission.getPolyLine().getPointCount();
                    for (int i = 0; i < count; i++) {
                        Point p = mission.getPolyLine().getPoint(i);
                        sb.append(concatCoords(p));
                    }
                    break;
                case 2:
                    count = mission.getPolygon().getPointCount();
                    for (int i = 0; i < count; i++) {
                        Point p = mission.getPolygon().getPoint(i);
                        String res = concatCoords(p);
                        sb.append(res);
                    }
                    break;
            }
            xys = sb.substring(0, sb.length() - 1);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }

        return xys;
    }

    public static String concatCoords(Point point) {
        String xStr = String.valueOf(point.getX());
        if (xStr.contains("E") || xStr.contains("e")) {
            BigDecimal bd = new BigDecimal(xStr);
            xStr = bd.toPlainString();
        }
        String yStr = String.valueOf(point.getY());
        if (yStr.contains("E") || yStr.contains("e")) {
            BigDecimal bd = new BigDecimal(yStr);
            yStr = bd.toPlainString();
        }
        return xStr + " " + yStr + ",";
    }

    public static int getMissionType(String mode) {
        int type = -1;
        switch (mode) {
            case "TiltImage":
                type = FlyCollectMode.TiltImage;
                break;
            case "ChannelPhoto":
            case "ManualFullImage":
            case "RiverPatrolPhoto":
            case "LinePatrolPhoto":
            case "LinePatrolVideo":
                type = FlyCollectMode.LinePatrol;
                break;
            case "HotPointPhoto":
        }
        return type;
    }

    public static boolean checkIfNewMissionDB(int mode) {
        return false;
    }

    public static boolean checkIfMissionUploadEnable(int mode) {
        return false;
    }


    /**
     * 兼容以前任务数据
     *
     * @param missionBatch2List
     * @return
     */
    public static List<MissionBatch> convertToMissionBatchList(List<MissionBatch2> missionBatch2List) {
        List<MissionBatch> missionBatchList = new ArrayList<>();
        if (missionBatch2List != null) {
            for (MissionBatch2 missionBatch2 : missionBatch2List) {
                missionBatchList.add(convertToMissionBatch(missionBatch2));
            }
        }
        return missionBatchList;

    }

    /**
     * 兼容以前任务数据
     *
     * @param missionBatch2
     * @return
     */
    public static MissionBatch convertToMissionBatch(MissionBatch2 missionBatch2) {
        MissionBatch missionBatch = new MissionBatch();
        missionBatch.setMissionBatchId(missionBatch2.getId());
        missionBatch.setName(missionBatch2.getName());
        missionBatch.setStatus(missionBatch2.getStatus());
        missionBatch.setSnapShot(missionBatch2.getSnapShot());
        missionBatch.setCreateDate(missionBatch2.getCreateDate());
        return missionBatch;
    }

    /**
     * 对地图进行截屏
     *
     * @param path 图片保存路径
     * @param name 图片名字
     * @return 图片名字（带后缀.webp）
     */
    public static String screenShot(GeoView geoView, final String path, String name) {
        screenShotAsync(geoView, path, name, null);
        return name + ".webp";
    }

    /**
     * 对地图进行截屏(异步)
     *
     * @param geoView
     * @param path     图片保存路径
     * @param name     图片名字
     * @param callback
     */
    public static void screenShotAsync(GeoView geoView, final String path, String name, CommonCallbackWith<String> callback) {
        final String snapshotName = name + ".webp";

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    final ListenableFuture<Bitmap> export = geoView.exportImageAsync();
                    export.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap currentMapImage = null;
                            try {
                                currentMapImage = export.get();

                                ImageUtils.saveScreenShot(currentMapImage, path, snapshotName);

                            } catch (OutOfMemoryError | Exception error) {
                                error.printStackTrace();
                            } finally {
                                if (currentMapImage != null && !currentMapImage.isRecycled()) {
                                    currentMapImage.recycle();
                                    currentMapImage = null;
                                }
                                System.gc();
                                if (callback != null) {
                                    callback.onSuccess(snapshotName);
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    if (callback != null) {
                        callback.onSuccess(snapshotName);
                    }
                }
            }
        }).start();
    }
}
