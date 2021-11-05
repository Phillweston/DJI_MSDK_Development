package com.ew.autofly.entity;

import android.os.Parcel;
import android.os.Parcelable;

import dji.common.battery.BatteryCellVoltageLevel;



public class FlyCheckStatus implements Parcelable {

    private boolean isConnected;

    private int satelliteCount;

    private boolean isCompassOk;

    private String flightModeString = "";

    
    private double handsetStorage;

    
    private BatteryCellVoltageLevel cellVoltageLevel;

    private double remainPower;

    
    private long sdcardRemainSize;

    
    private int totalFlyTaskPhotoNumber;

    
    private boolean isCheckSdCard = true;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public int getSatelliteCount() {
        return satelliteCount;
    }

    public void setSatelliteCount(int satelliteCount) {
        this.satelliteCount = satelliteCount;
    }

    public boolean isCompassOk() {
        return isCompassOk;
    }

    public void setCompassOk(boolean compassOk) {
        isCompassOk = compassOk;
    }

    public String getFlightModeString() {
        return flightModeString;
    }

    public void setFlightModeString(String flightModeString) {
        this.flightModeString = flightModeString;
    }

    public double getHandsetStorage() {
        return handsetStorage;
    }

    public void setHandsetStorage(double handsetStorage) {
        this.handsetStorage = handsetStorage;
    }

    public BatteryCellVoltageLevel getCellVoltageLevel() {
        return cellVoltageLevel;
    }

    public void setCellVoltageLevel(BatteryCellVoltageLevel cellVoltageLevel) {
        this.cellVoltageLevel = cellVoltageLevel;
    }

    public double getRemainPower() {
        return remainPower;
    }

    public void setRemainPower(double remainPower) {
        this.remainPower = remainPower;
    }

    public long getSdcardRemainSize() {
        return sdcardRemainSize;
    }

    public void setSdcardRemainSize(long sdcardRemainSize) {
        this.sdcardRemainSize = sdcardRemainSize;
    }

    public int getTotalFlyTaskPhotoNumber() {
        return totalFlyTaskPhotoNumber;
    }

    public void setTotalFlyTaskPhotoNumber(int totalFlyTaskPhotoNumber) {
        this.totalFlyTaskPhotoNumber = totalFlyTaskPhotoNumber;
    }

    public boolean isCheckSdCard() {
        return isCheckSdCard;
    }

    public void setCheckSdCard(boolean checkSdCard) {
        isCheckSdCard = checkSdCard;
    }

    public FlyCheckStatus() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isConnected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.satelliteCount);
        dest.writeByte(this.isCompassOk ? (byte) 1 : (byte) 0);
        dest.writeString(this.flightModeString);
        dest.writeDouble(this.handsetStorage);
        dest.writeInt(this.cellVoltageLevel == null ? -1 : this.cellVoltageLevel.ordinal());
        dest.writeDouble(this.remainPower);
        dest.writeLong(this.sdcardRemainSize);
        dest.writeInt(this.totalFlyTaskPhotoNumber);
        dest.writeByte(this.isCheckSdCard ? (byte) 1 : (byte) 0);
    }

    protected FlyCheckStatus(Parcel in) {
        this.isConnected = in.readByte() != 0;
        this.satelliteCount = in.readInt();
        this.isCompassOk = in.readByte() != 0;
        this.flightModeString = in.readString();
        this.handsetStorage = in.readDouble();
        int tmpCellVoltageLevel = in.readInt();
        this.cellVoltageLevel = tmpCellVoltageLevel == -1 ? null : BatteryCellVoltageLevel.values()[tmpCellVoltageLevel];
        this.remainPower = in.readDouble();
        this.sdcardRemainSize = in.readLong();
        this.totalFlyTaskPhotoNumber = in.readInt();
        this.isCheckSdCard = in.readByte() != 0;
    }

    public static final Creator<FlyCheckStatus> CREATOR = new Creator<FlyCheckStatus>() {
        @Override
        public FlyCheckStatus createFromParcel(Parcel source) {
            return new FlyCheckStatus(source);
        }

        @Override
        public FlyCheckStatus[] newArray(int size) {
            return new FlyCheckStatus[size];
        }
    };
}
