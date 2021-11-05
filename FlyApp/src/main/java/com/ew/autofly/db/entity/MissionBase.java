package com.ew.autofly.db.entity;

import com.ew.autofly.entity.LocationCoordinate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public abstract class MissionBase implements Serializable {


    public final static double NaN = -10000;

    public final static int GEOMETRYTYPE_POINT = 0;
    public final static int GEOMETRYTYPE_LINE = 1;
    public final static int GEOMETRYTYPE_POLYGON = 2;

    public abstract String getMissionId();

    public abstract void setMissionId(String missionId);

    public abstract String getMissionBatchId();

    public abstract void setMissionBatchId(String missionBatchId);

    public abstract int getMissionType();

    public abstract void setMissionType(int missionType);

    public abstract void setName(String name);

    public abstract String getName();

    public abstract Date getCreateDate();

    public abstract void setCreateDate(Date createDate);

    public abstract String getSnapshot();

    public abstract void setSnapshot(String snapshot);

    public abstract int getStatus();

    public abstract void setStatus(int status);

    public abstract Date getStartTime();

    public abstract void setStartTime(Date startTime);

    public abstract Date getEndTime();

    public abstract void setEndTime(Date endTime);

    public abstract int getStartPhotoIndex();

    public abstract void setStartPhotoIndex(int startPhotoIndex);

    public abstract int getEndPhotoIndex();

    public abstract void setEndPhotoIndex(int endPhotoIndex);

    public abstract int getGeometryType();

    public abstract void setGeometryType(int geometryType);

    public abstract List<String> getGeometryLatLngList();

    public abstract void setGeometryLatLngList(List<String> geometryLatLngList);

    public List<LocationCoordinate> getGeometryLatLngInfoList() {
        List<LocationCoordinate> latLngInfoList = new ArrayList<>();
        if (getGeometryLatLngList() != null) {
            try {
                for (String latLng : getGeometryLatLngList()) {
                    String[] latLngArray = latLng.split(",");
                    if (latLngArray.length == 2) {
                        LocationCoordinate LocationCoordinate = new LocationCoordinate(Double.valueOf(latLngArray[0]), Double.valueOf(latLngArray[1]));
                        latLngInfoList.add(LocationCoordinate);
                    } else if (latLngArray.length == 3) {
                        LocationCoordinate LocationCoordinate = new LocationCoordinate(Double.valueOf(latLngArray[0]), Double.valueOf(latLngArray[1]), Float.valueOf(latLngArray[2]));
                        latLngInfoList.add(LocationCoordinate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return latLngInfoList;
    }

    public int getAltitude() {
        return (int) NaN;
    }

    public double getResolutionRate() {
        return NaN;
    }

    public int getRouteOverlap() {
        return (int) NaN;
    }

    public int getSideOverlap() {
        return (int) NaN;
    }
}
