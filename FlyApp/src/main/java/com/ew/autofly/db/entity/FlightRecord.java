package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;



@Entity(nameInDb = "flight_record")
public class FlightRecord implements Serializable{

    @Transient
    private static final long serialVersionUID = 1L;

    @Id
    @Property(nameInDb = "id")
    private String id;

    @Property(nameInDb = "created_time")
    private String createdTime;

    @Property(nameInDb = "start_time")
    private String startTime;

    @Property(nameInDb = "end_time")
    private String endTime;

    @Property(nameInDb = "max_height")
    private int maxHeight;

    @Property(nameInDb = "product_name")
    private String productName;

    @Property(nameInDb = "product_serial_number")
    private String productSerialNumber;

    @Property(nameInDb = "battery_serial_number")
    private String batterySerialNumber;

    @Property(nameInDb = "is_upload")
    private boolean isUpload = false;

    @Property(nameInDb = "distance")
    private double distance;

    @Property(nameInDb = "longitude")
    private double longitude;

    @Property(nameInDb = "latitude")
    private double latitude;

    @Generated(hash = 991405773)
    public FlightRecord(String id, String createdTime, String startTime,
            String endTime, int maxHeight, String productName,
            String productSerialNumber, String batterySerialNumber,
            boolean isUpload, double distance, double longitude, double latitude) {
        this.id = id;
        this.createdTime = createdTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxHeight = maxHeight;
        this.productName = productName;
        this.productSerialNumber = productSerialNumber;
        this.batterySerialNumber = batterySerialNumber;
        this.isUpload = isUpload;
        this.distance = distance;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Generated(hash = 802447759)
    public FlightRecord() {
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean getIsUpload() {
        return this.isUpload;
    }

    public void setIsUpload(boolean isUpload) {
        this.isUpload = isUpload;
    }

    public String getProductSerialNumber() {
        return this.productSerialNumber;
    }

    public void setProductSerialNumber(String productSerialNumber) {
        this.productSerialNumber = productSerialNumber;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public String getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlightRecord that = (FlightRecord) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getBatterySerialNumber() {
        return this.batterySerialNumber;
    }

    public void setBatterySerialNumber(String batterySerialNumber) {
        this.batterySerialNumber = batterySerialNumber;
    }
}
