package com.ew.autofly.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;



public class Tower implements Parcelable, Serializable {

  
    public final static int ALTITUDE_NO_VALUE = -9999999;
    public final static int ALTITUDE_INPUT_ERROR = -9999998;
    public final static int DEFAULT_SAFE_ALTITUDE = 100;

    private long id;
    private float altitude = ALTITUDE_NO_VALUE;
    private float topAltitude = ALTITUDE_NO_VALUE;
    private int flyAltitude = ALTITUDE_NO_VALUE;
    private int towerAltitude = ALTITUDE_NO_VALUE;

    private long gridLineId;
    private String gridLineName;
    private double latitude;
    private double longitude;
    private int status = 1;
    private String towerNo;
    private boolean checked = false;
    private String voltage;
    private String manageGroup;
    private String fileId;
    private String towerId;
    private String description = "";
    private boolean hasPointCloud;
    private boolean isLocationUpdated;

    public String toString() {
        return String.format("\nTowerNo:%s\nTower latitude:%f\nTower longitude:%f", towerNo, latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        while ((o instanceof Tower) && (((Tower) o).getId()) == getId())
            return true;
        return false;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public float getTopAltitude() {
        return topAltitude;
    }

    public void setTopAltitude(float topAltitude) {
        this.topAltitude = topAltitude;
    }

    public int getFlyAltitude() {
        return flyAltitude;
    }

    public boolean isNoSafeFlyAltitude() {
        return flyAltitude == ALTITUDE_NO_VALUE;
    }

    public void setFlyAltitude(int flyAltitude) {
        this.flyAltitude = flyAltitude;
    }

    public long getGridLineId() {
        return gridLineId;
    }

    public void setGridLineId(long gridLineId) {
        this.gridLineId = gridLineId;
    }

    public String getGridLineName() {
      
      
        return getGridLineNameNoSuffix();
    }


    /**
     * 获取没有后缀名的线路名称
     *
     * @return
     */
    private String getGridLineNameNoSuffix() {
        return gridLineName != null ? gridLineName.replace(".xls", "")
                .replace(".kml", "") : "";
    }

    public void setGridLineName(String gridLineName) {
        this.gridLineName = gridLineName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTowerAltitude() {
        return towerAltitude;
    }

    public void setTowerAltitude(int towerAltitude) {
        this.towerAltitude = towerAltitude;
    }

    public String getTowerNo() {
        return towerNo;
    }

    public void setTowerNo(String towerNo) {
        this.towerNo = towerNo;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getManageGroup() {
        return manageGroup;
    }

    public void setManageGroup(String manageGroup) {
        this.manageGroup = manageGroup;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getTowerId() {
        return towerId;
    }

    public void setTowerId(String towerId) {
        this.towerId = towerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasPointCloud() {
        return hasPointCloud;
    }

    public void setHasPointCloud(boolean hasPointCloud) {
        this.hasPointCloud = hasPointCloud;
    }

    public boolean isLocationUpdated() {
        return isLocationUpdated;
    }

    public void setLocationUpdated(boolean locationUpdated) {
        isLocationUpdated = locationUpdated;
    }

    public boolean isEqualPosition(Tower tower) {
        return this.latitude == tower.getLatitude() && this.longitude == tower.getLongitude();
    }

    public Tower() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeFloat(this.altitude);
        dest.writeFloat(this.topAltitude);
        dest.writeInt(this.flyAltitude);
        dest.writeLong(this.gridLineId);
        dest.writeString(this.gridLineName);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.status);
        dest.writeInt(this.towerAltitude);
        dest.writeString(this.towerNo);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        dest.writeString(this.voltage);
        dest.writeString(this.manageGroup);
        dest.writeString(this.fileId);
        dest.writeString(this.towerId);
        dest.writeString(this.description);
        dest.writeByte(this.hasPointCloud ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocationUpdated ? (byte) 1 : (byte) 0);
    }

    protected Tower(Parcel in) {
        this.id = in.readLong();
        this.altitude = in.readFloat();
        this.topAltitude = in.readFloat();
        this.flyAltitude = in.readInt();

        this.gridLineId = in.readLong();
        this.gridLineName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.status = in.readInt();
        this.towerAltitude = in.readInt();
        this.towerNo = in.readString();
        this.checked = in.readByte() != 0;
        this.voltage = in.readString();
        this.manageGroup = in.readString();
        this.fileId = in.readString();
        this.towerId = in.readString();
        this.description = in.readString();
        this.hasPointCloud = in.readByte() != 0;
        this.isLocationUpdated = in.readByte() != 0;
    }

    public static final Creator<Tower> CREATOR = new Creator<Tower>() {
        @Override
        public Tower createFromParcel(Parcel source) {
            return new Tower(source);
        }

        @Override
        public Tower[] newArray(int size) {
            return new Tower[size];
        }
    };
}
