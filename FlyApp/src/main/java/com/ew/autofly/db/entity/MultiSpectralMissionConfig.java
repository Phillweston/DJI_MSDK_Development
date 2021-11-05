package com.ew.autofly.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;



@Entity(nameInDb = "multispectral_mission_config")
public class MultiSpectralMissionConfig implements Serializable {

    @Transient
    private static final long serialVersionUID = 1L;

    @Transient
    public static final int CAMERA_RGB = 1 << 0;
    @Transient
    public static final int CAMERA_GREEN = 1 << 1;
    @Transient
    public static final int CAMERA_RED = 1 << 2;
    @Transient
    public static final int CAMERA_RED_EDGE = 1 << 3;
    @Transient
    public static final int CAMERA_NIR = 1 << 4;

    @Transient
    public static final String CAPTURE_MODE_TIMELAPSE = "timelapse";
    @Transient
    public static final String CAPTURE_MODE_GPS = "gps_position";

    @Transient
    public static final String CAPTURE_TYPE_VEGETATION = "vegetation";
    @Transient
    public static final String CAPTURE_TYPE_RIVER = "river";

    
    @Id
    @Property(nameInDb = "mission_id")
    private String missionId;

    
    private String capture_mode = CAPTURE_MODE_TIMELAPSE;

    
    private String capture_type = CAPTURE_TYPE_RIVER;

    
    private double timelapse_param = 2.0D;

    
    private double gps_param = 32.2D;

    
    private double overlap_param = 70.0D;

    
    private int sensors_mask = CAMERA_GREEN | CAMERA_RED | CAMERA_RED_EDGE | CAMERA_NIR | CAMERA_RGB;

    public int getSensors_mask() {
        return this.sensors_mask;
    }

    public void setSensors_mask(int sensors_mask) {
        this.sensors_mask = sensors_mask;
    }

    public double getOverlap_param() {
        return this.overlap_param;
    }

    public void setOverlap_param(double overlap_param) {
        this.overlap_param = overlap_param;
    }

    public double getGps_param() {
        return this.gps_param;
    }

    public void setGps_param(double gps_param) {
        this.gps_param = gps_param;
    }

    public double getTimelapse_param() {
        return this.timelapse_param;
    }

    public void setTimelapse_param(double timelapse_param) {
        this.timelapse_param = timelapse_param;
    }

    public String getCapture_type() {
        return this.capture_type;
    }

    public void setCapture_type(String capture_type) {
        this.capture_type = capture_type;
    }

    public String getCapture_mode() {
        return this.capture_mode;
    }

    public void setCapture_mode(String capture_mode) {
        this.capture_mode = capture_mode;
    }

    public String getMissionId() {
        return this.missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    @Generated(hash = 631054419)
    public MultiSpectralMissionConfig(String missionId, String capture_mode,
                                      String capture_type, double timelapse_param, double gps_param,
                                      double overlap_param, int sensors_mask) {
        this.missionId = missionId;
        this.capture_mode = capture_mode;
        this.capture_type = capture_type;
        this.timelapse_param = timelapse_param;
        this.gps_param = gps_param;
        this.overlap_param = overlap_param;
        this.sensors_mask = sensors_mask;
    }

    @Generated(hash = 1244170695)
    public MultiSpectralMissionConfig() {
    }
}
