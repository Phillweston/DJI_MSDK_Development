package com.ew.autofly.db.entity;

import com.ew.autofly.db.converter.StringConverter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.utils.StringUtils;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import java.util.List;



@Entity(nameInDb = "t_Mission_PointCloud")
public class MissionPointCloud extends MissionBase {

    @Id(autoincrement = true)
    protected Long id;

    @Unique
    protected String missionId;
    protected String missionBatchId;
    private int missionType;
    protected String name;
    protected String snapshot;
  
    protected int status;
  
    protected Date createDate;
  
    protected Date startTime;
  
    protected Date endTime;
  
    private int startPhotoIndex;
  
    private int endPhotoIndex;
  
    protected int geometryType;
  
    @Convert(columnType = String.class, converter = StringConverter.class)
    protected List<String> geometryLatLngList;

  
    private float convergeAltitude;

     private float convergeRadius;
   
     private float convergeSpeed;
   
     private int convergeStraightLineTime;
   
    @Transient
    private LocationCoordinate convergePosition;

    private String convergePositionStr;


  
    private boolean isAltitudeFixed;
  
    private int altitude;
  
    private int flySpeed;

  
    private int riseHeight;

  
    private float sideDistance;
  
    private int layerNumber;
  
    private float layerHeight;
  
    private float endShrink;

  
    private boolean isOtherSide = false;

    @Generated(hash = 220904780)
    public MissionPointCloud(Long id, String missionId, String missionBatchId, int missionType, String name,
            String snapshot, int status, Date createDate, Date startTime, Date endTime, int startPhotoIndex,
            int endPhotoIndex, int geometryType, List<String> geometryLatLngList, float convergeAltitude,
            float convergeRadius, float convergeSpeed, int convergeStraightLineTime, String convergePositionStr,
            boolean isAltitudeFixed, int altitude, int flySpeed, int riseHeight, float sideDistance, int layerNumber,
            float layerHeight, float endShrink, boolean isOtherSide) {
        this.id = id;
        this.missionId = missionId;
        this.missionBatchId = missionBatchId;
        this.missionType = missionType;
        this.name = name;
        this.snapshot = snapshot;
        this.status = status;
        this.createDate = createDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPhotoIndex = startPhotoIndex;
        this.endPhotoIndex = endPhotoIndex;
        this.geometryType = geometryType;
        this.geometryLatLngList = geometryLatLngList;
        this.convergeAltitude = convergeAltitude;
        this.convergeRadius = convergeRadius;
        this.convergeSpeed = convergeSpeed;
        this.convergeStraightLineTime = convergeStraightLineTime;
        this.convergePositionStr = convergePositionStr;
        this.isAltitudeFixed = isAltitudeFixed;
        this.altitude = altitude;
        this.flySpeed = flySpeed;
        this.riseHeight = riseHeight;
        this.sideDistance = sideDistance;
        this.layerNumber = layerNumber;
        this.layerHeight = layerHeight;
        this.endShrink = endShrink;
        this.isOtherSide = isOtherSide;
    }

    @Generated(hash = 719813388)
    public MissionPointCloud() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMissionId() {
        return this.missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getMissionBatchId() {
        return this.missionBatchId;
    }

    public void setMissionBatchId(String missionBatchId) {
        this.missionBatchId = missionBatchId;
    }

    public int getMissionType() {
        return this.missionType;
    }

    public void setMissionType(int missionType) {
        this.missionType = missionType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnapshot() {
        return this.snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getStartPhotoIndex() {
        return this.startPhotoIndex;
    }

    public void setStartPhotoIndex(int startPhotoIndex) {
        this.startPhotoIndex = startPhotoIndex;
    }

    public int getEndPhotoIndex() {
        return this.endPhotoIndex;
    }

    public void setEndPhotoIndex(int endPhotoIndex) {
        this.endPhotoIndex = endPhotoIndex;
    }

    public int getGeometryType() {
        return this.geometryType;
    }

    public void setGeometryType(int geometryType) {
        this.geometryType = geometryType;
    }

    public List<String> getGeometryLatLngList() {
        return this.geometryLatLngList;
    }

    public void setGeometryLatLngList(List<String> geometryLatLngList) {
        this.geometryLatLngList = geometryLatLngList;
    }

    public float getConvergeAltitude() {
        return this.convergeAltitude;
    }

    public void setConvergeAltitude(float convergeAltitude) {
        this.convergeAltitude = convergeAltitude;
    }

    public LocationCoordinate getConvergePosition() {
        if (!StringUtils.isEmptyOrNull(this.convergePositionStr)) {
            String[] latLng = this.convergePositionStr.split(",");
            if (latLng.length > 1) {
                try {
                    this.convergePosition = new LocationCoordinate(Double.valueOf(latLng[0]), Double.valueOf(latLng[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return convergePosition;
    }

    public void setConvergePosition(LocationCoordinate convergePosition) {
        this.convergePosition = convergePosition;
        if (this.convergePosition != null) {
            this.convergePositionStr = convergePosition.latitude + "," + convergePosition.longitude;
        }
    }

    public String getConvergePositionStr() {
        return this.convergePositionStr;
    }

    public void setConvergePositionStr(String convergePositionStr) {
        this.convergePositionStr = convergePositionStr;
    }

    public int getRiseHeight() {
        return this.riseHeight;
    }

    public void setRiseHeight(int riseHeight) {
        this.riseHeight = riseHeight;
    }

    public float getSideDistance() {
        return this.sideDistance;
    }

    public void setSideDistance(float sideDistance) {
        this.sideDistance = sideDistance;
    }

    public int getLayerNumber() {
        return this.layerNumber;
    }

    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    public float getLayerHeight() {
        return this.layerHeight;
    }

    public void setLayerHeight(float layerHeight) {
        this.layerHeight = layerHeight;
    }

    public float getEndShrink() {
        return this.endShrink;
    }

    public void setEndShrink(float endShrink) {
        this.endShrink = endShrink;
    }

    public boolean getIsAltitudeFixed() {
        return this.isAltitudeFixed;
    }

    public void setIsAltitudeFixed(boolean isAltitudeFixed) {
        this.isAltitudeFixed = isAltitudeFixed;
    }

    public int getAltitude() {
        return this.altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(int flySpeed) {
        this.flySpeed = flySpeed;
    }

    public boolean getIsOtherSide() {
        return this.isOtherSide;
    }

    public void setIsOtherSide(boolean isOtherSide) {
        this.isOtherSide = isOtherSide;
    }

    public float getConvergeRadius() {
        return this.convergeRadius;
    }

    public void setConvergeRadius(float convergeRadius) {
        this.convergeRadius = convergeRadius;
    }

    public float getConvergeSpeed() {
        return this.convergeSpeed;
    }

    public void setConvergeSpeed(float convergeSpeed) {
        this.convergeSpeed = convergeSpeed;
    }

    public int getConvergeStraightLineTime() {
        return this.convergeStraightLineTime;
    }

    public void setConvergeStraightLineTime(int convergeStraightLineTime) {
        this.convergeStraightLineTime = convergeStraightLineTime;
    }
}
