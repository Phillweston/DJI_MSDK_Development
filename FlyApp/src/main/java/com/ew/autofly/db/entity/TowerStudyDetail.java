package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;



@Entity(nameInDb = "t_Tower_StudyDetail")
public class TowerStudyDetail implements Serializable{

    @Transient
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    @Property(nameInDb = "Id")
    private Long id;
    @Property(nameInDb = "towerId")
    private String towerId;
    @Property(nameInDb = "startAltitude")
    private double startAltitude;
    @Property(nameInDb = "header")
    private double header;
    @Property(nameInDb = "pitch")
    private double pitch;
    @Property(nameInDb = "roll")
    private double roll;
    @Property(nameInDb = "angle")
    private double angle;
    @Property(nameInDb = "latitude")
    private double latitude;
    @Property(nameInDb = "longitude")
    private double longitude;
    @Property(nameInDb = "altitude")
    private double altitude;
    @Property(nameInDb="CreatedTime")
    private Date createdTime;
    @Property(nameInDb = "home_latitude")
    private double homeLatitude;
    @Property(nameInDb = "home_longitude")
    private double homeLongitude;
    @Property(nameInDb = "home_altitude")
    private double homeAltitude;
    @Property(nameInDb = "is_rtk")
    private boolean isRTK;


    @Generated(hash = 493420957)
    public TowerStudyDetail(Long id, String towerId, double startAltitude,
            double header, double pitch, double roll, double angle,
            double latitude, double longitude, double altitude, Date createdTime,
            double homeLatitude, double homeLongitude, double homeAltitude,
            boolean isRTK) {
        this.id = id;
        this.towerId = towerId;
        this.startAltitude = startAltitude;
        this.header = header;
        this.pitch = pitch;
        this.roll = roll;
        this.angle = angle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.createdTime = createdTime;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
        this.homeAltitude = homeAltitude;
        this.isRTK = isRTK;
    }
    @Generated(hash = 1981415260)
    public TowerStudyDetail() {
    }

    
    public Date getCreatedTime() {
        return this.createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
    public double getAltitude() {
        return this.altitude;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getAngle() {
        return this.angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public double getRoll() {
        return this.roll;
    }
    public void setRoll(double roll) {
        this.roll = roll;
    }
    public double getPitch() {
        return this.pitch;
    }
    public void setPitch(double pitch) {
        this.pitch = pitch;
    }
    public double getHeader() {
        return this.header;
    }
    public void setHeader(double header) {
        this.header = header;
    }
    public double getStartAltitude() {
        return this.startAltitude;
    }
    public void setStartAltitude(double startAltitude) {
        this.startAltitude = startAltitude;
    }
    public String getTowerId() {
        return this.towerId;
    }
    public void setTowerId(String towerId) {
        this.towerId = towerId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public double getHomeLatitude() {
        return homeLatitude;
    }

    public void setHomeLatitude(double homeLatitude) {
        this.homeLatitude = homeLatitude;
    }

    public double getHomeLongitude() {
        return homeLongitude;
    }

    public void setHomeLongitude(double homeLongitude) {
        this.homeLongitude = homeLongitude;
    }

    public double getHomeAltitude() {
        return homeAltitude;
    }

    public void setHomeAltitude(double homeAltitude) {
        this.homeAltitude = homeAltitude;
    }

    public boolean isRTK() {
        return isRTK;
    }

    public void setRTK(boolean RTK) {
        isRTK = RTK;
    }
    public boolean getIsRTK() {
        return this.isRTK;
    }
    public void setIsRTK(boolean isRTK) {
        this.isRTK = isRTK;
    }
}
