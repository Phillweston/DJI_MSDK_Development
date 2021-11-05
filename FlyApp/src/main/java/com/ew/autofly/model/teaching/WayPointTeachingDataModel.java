package com.ew.autofly.model.teaching;

import android.text.TextUtils;

import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.FinePatrolWayPointDetail;
import com.ew.autofly.db.entity.PhotoPosition;
import com.ew.autofly.entity.airroute.AirRoute;
import com.ew.autofly.entity.airroute.AirRouteResponse;
import com.ew.autofly.entity.airroute.MultiAirRouteResponse;
import com.ew.autofly.entity.mission.MissionPointType;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.io.file.FileUtils;
import com.ew.autofly.utils.tool.DataUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;


public class WayPointTeachingDataModel {

    private final static String DATA_PATH = AppConstant.ROOT_PATH + File.separator;

    
    public final static String DATA_SUFFIX = ".data";

    
    private String path;

    public WayPointTeachingDataModel(String path) {
        this.path = path;
    }

    /**
     * 保存学习记录
     *
     * @param airRoute
     * @return
     */
    public boolean saveStudyRecords(AirRoute airRoute, String saveName) {
        boolean isWriteSuccess = false;
        try {

            AirRouteResponse response = new AirRouteResponse();
            response.setCode(200);
            response.setData(airRoute);
            response.setMessage("success");
            Gson gson = new GsonBuilder().create();
            isWriteSuccess = DataUtils.saveData(DATA_PATH + path + File.separator + saveName
                    + DATA_SUFFIX, gson.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return isWriteSuccess;
    }

    /**
     * 获取学习记录文件
     *
     * @param fileName 文件名称
     * @return
     */
    public AirRoute getAirRoute(String fileName) {
        try {

            DataUtils.Data data = DataUtils.readData(DATA_PATH + path + File.separator + fileName);
            if (data == null) {
                return null;
            }
            String json = data.getData();
            if (json == null) {
                return null;
            }

            Gson gson = new Gson();

            AirRoute airRoute = null;
            if (data.isMultiMission()) {
                MultiAirRouteResponse httpResponse = gson.fromJson(json, MultiAirRouteResponse.class);
                if (httpResponse != null && httpResponse.getData() != null) {
                    airRoute = httpResponse.getData();
                }
            } else {
                AirRouteResponse httpResponse = gson.fromJson(json, AirRouteResponse.class);
                if (httpResponse != null && httpResponse.getData() != null) {
                    airRoute = httpResponse.getData();
                }
            }
            return airRoute;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有航线文件
     *
     * @return
     */
    public LinkedHashMap<String, AirRoute> getAllAirRoute() {
        LinkedHashMap<String, AirRoute> map = new LinkedHashMap<>();
        File dir = new File(DATA_PATH + path);
        File[] files = dir.listFiles();
        FileUtils.SortFile(files, FileUtils.SortCondition.TIME, false);
        for (File file : files) {
            try {
                String fileName = file.getName();
                int index = fileName.lastIndexOf(".");
                if (index != -1) {
                    String suffix = fileName.substring(index);
                    if (DATA_SUFFIX.equals(suffix)) {
                        AirRoute airRoute = getAirRoute(file.getName());
                        if (airRoute != null) {
                            map.put(fileName, airRoute);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return map;
    }

    public void deleteAirRoute(String missionName) {
        IOUtils.delete(DATA_PATH + path + File.separator + missionName);
    }

    /**
     * 检查是否存在相同文件
     *
     * @param name 文件名称
     * @return
     */
    public boolean checkIfExistData(String name) {
        File dir = new File(DATA_PATH + path);
        File[] files = dir.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    
    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            return file1.getName().compareTo(file2.getName());
        }
    }

    public static int getAppropriateGimbalPitch(float pitch) {
        int waypointPicth;
        if (AircraftManager.isP4R()) {
            if (pitch > 30) {
                waypointPicth = 30;
            } else if (pitch < -90) {
                waypointPicth = -90;
            } else {
                waypointPicth = (int) pitch;
            }
        } else {
            if (pitch > 0) {
                waypointPicth = 0;
            } else if (pitch < -90) {
                waypointPicth = -90;
            } else {
                waypointPicth = (int) pitch;
            }
        }
        return waypointPicth;
    }

    /**
     * 转换成正数的云台角度(因为兼容之前版本的角度都是0-90度范围，大疆为-90-0度，需要做一下转换)
     *
     * @param pitch
     * @return
     */
    public static float changeToPositiveGimbalPitch(float pitch) {

        float positivePitch = pitch;
        if (positivePitch < -90f) {
            positivePitch = (-90.0f);
        } else if (positivePitch > 30f) {
            positivePitch = 30f;
        }
        positivePitch += 90;
        return positivePitch;
    }

    /**
     * 获取航点详情信息
     * @param data
     * @param isAbsoluteAltitude
     * @return
     */
    public static String getWaypointDetailInfo(FinePatrolWayPointDetail data,boolean isAbsoluteAltitude){
        StringBuilder info = new StringBuilder();
        info.append("纬度：").append(String.format("%.9f", data.getAircraftLocationLatitude())).append("\n");
        info.append("经度：").append(String.format("%.9f", data.getAircraftLocationLongitude())).append("\n");
        info.append(isAbsoluteAltitude ? "海拔：" : "高度：").append(String.format("%.3f", data.getAircraftLocationAltitude()));
        if (data.getWaypointType() != MissionPointType.ASSIST_WITHOUT_ACTION.value()) {
            info.append("\n").append("飞机朝向：").append(data.getAircraftYaw()).append("\n");
            info.append("云台俯仰：").append(data.getGimbalPitch());
            int focalLength = data.getFocalLength();
            if (focalLength != 0) {
                info.append("\n").append("相机焦距：").append(focalLength);
            }
        }

        List<PhotoPosition> photoPositionList = data.getPhotoPositionList();
        if (photoPositionList != null && !photoPositionList.isEmpty()) {
            PhotoPosition photoPosition = photoPositionList.get(0);
            String displayName = photoPosition.getDisplayName();
            if (!TextUtils.isEmpty(displayName)) {
                info.append("\n").append("航点信息：").append(displayName);
            }
        }
        return info.toString();
    }
}
