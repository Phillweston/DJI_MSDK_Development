package com.ew.autofly.entity;

import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.utils.MissionUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Mission2 extends MissionBase implements Serializable {

	public final static int STATE_EXECUTE_UNFINISHED=0;
	public final static int STATE_EXECUTE_FINISHED=1;
	public final static int STATE_COPYING_PHOTO =2;
	public final static int STATE_COPY_PHOTO_FINISHED =3;
	public final static int STATE_STITCHING_PHOTO =4;
	public final static int STATE_STITCH_PHOTO_FINISHED =5;
	public final static int STATE_DOWNLOAD_FINISHED=6;

	private static final long serialVersionUID = 1L;
	private String id;
	private String batchId;
	private String name;
	private String snapshot;
	private int status;
	private boolean fixedAltitude;
	private int altitude;
	private int flySpeed;
	private int gimbalAngle;
	private double resolutionRate;
	private int geomType;
	private transient Polygon polygon;
	private transient Polyline polyLine;
	private transient MultiPoint multiPoint;
	private int sideOverlap;
	private int routeOverlap;
	private Date createDate;
	private Date startTime;
	private Date endTime;
	private int pointIndex;
	private String workMode;
	private String workStep;
	private String fixedAltitudeList;
	private int flightNum;
	private int startPhotoIndex;
	private int endPhotoIndex;
	private int photoNum;

	private int flyAngle;
	private int minAltitude;
	private int flyingLayer;
	private double rotating;
	private int returnMode;
	private int buffer;
	private int startPoint;
	private int endPoint;
	private int baseLineHeight;
	private int entryHeight;
	private int isPolygon;

	public int getEntryHeight() {
		return entryHeight;
	}

	public void setEntryHeight(int entryHeight) {
		this.entryHeight = entryHeight;
	}

	public int getBaseLineHeight() {
		return baseLineHeight;
	}

	public void setBaseLineHeight(int baseLineHeight) {
		this.baseLineHeight = baseLineHeight;
	}

    public double getRotating() {
        return rotating;
    }

    public void setRotating(double rotating) {
        this.rotating = rotating;
    }

	public int getFlyAngle() {
		return flyAngle;
	}

	public void setFlyAngle(int flyAngle) {
		this.flyAngle = flyAngle;
	}

	public int getMinAltitude() {
		return minAltitude;
	}

	public void setMinAltitude(int minAltitude) {
		this.minAltitude = minAltitude;
	}

	public int getFlyingLayer() {
		return flyingLayer;
	}

	public void setFlyingLayer(int flyingLayer) {
		this.flyingLayer = flyingLayer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	@Override
	public String getMissionId() {
		return getId();
	}

	@Override
	public void setMissionId(String missionId) {
		setId(missionId);
	}

	@Override
	public String getMissionBatchId() {
		return getBatchId();
	}

	@Override
	public void setMissionBatchId(String missionBatchId) {
		setBatchId(missionBatchId);
	}

	@Override
	public int getMissionType() {
		return MissionUtil.getMissionType(getWorkMode());
	}

	@Override
	public void setMissionType(int missionType) {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isFixedAltitude() {
		return fixedAltitude;
	}

	public void setFixedAltitude(boolean fixedAltitude) {
		this.fixedAltitude = fixedAltitude;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	public int getFlySpeed() {
		return flySpeed;
	}

	public void setFlySpeed(int flySpeed) {
		this.flySpeed = flySpeed;
	}

	public int getGimbalAngle() {
		return gimbalAngle;
	}

	public void setGimbalAngle(int gimbalAngle) {
		this.gimbalAngle = gimbalAngle;
	}

	public double getResolutionRate() {
		return resolutionRate;
	}

	public void setResolutionRate(double resolutionRate) {
		this.resolutionRate = resolutionRate;
	}

	public int getGeomType() {
		return geomType;
	}

	public void setGeomType(int geomType) {
		this.geomType = geomType;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	public Polyline getPolyLine() {
		return polyLine;
	}

	public void setPolyLine(Polyline polyLine) {
		this.polyLine = polyLine;
	}

	public MultiPoint getMultiPoint() {
		return multiPoint;
	}

	public void setMultiPoint(MultiPoint multiPoint) {
		this.multiPoint = multiPoint;
	}

	public int getSideOverlap() {
		return sideOverlap;
	}

	public void setSideOverlap(int sideOverlap) {
		this.sideOverlap = sideOverlap;
	}

	public int getRouteOverlap() {
		return routeOverlap;
	}

	public void setRouteOverlap(int routeOverlap) {
		this.routeOverlap = routeOverlap;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	@Override
	public int getGeometryType() {
		return 0;
	}

	@Override
	public void setGeometryType(int geometryType) {

	}

	@Override
	public List<String> getGeometryLatLngList() {
		return null;
	}

	@Override
	public void setGeometryLatLngList(List<String> geometryLatLngList) {

	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getPointIndex() {
		return pointIndex;
	}

	public void setPointIndex(int pointIndex) {
		this.pointIndex = pointIndex;
	}

	public String getWorkMode() {
		return workMode;
	}

	public void setWorkMode(String workMode) {
		this.workMode = workMode;
	}

	public String getWorkStep() {
		return workStep;
	}

	public void setWorkStep(String workStep) {
		this.workStep = workStep;
	}

	public int getFlightNum() {
		return flightNum;
	}

	public void setFlightNum(int flightNum) {
		this.flightNum = flightNum;
	}

	public int getStartPhotoIndex() {
		return startPhotoIndex;
	}

	public void setStartPhotoIndex(int startPhotoIndex) {
		this.startPhotoIndex = startPhotoIndex;
	}

	public int getEndPhotoIndex() {
		return endPhotoIndex;
	}

	public void setEndPhotoIndex(int endPhotoIndex) {
		this.endPhotoIndex = endPhotoIndex;
	}

	public int getPhotoNum() {
		return photoNum;
	}

	public void setPhotoNum(int photoNum) {
		this.photoNum = photoNum;
	}

	public int getReturnMode() {
		return returnMode;
	}

	public void setReturnMode(int returnMode) {
		this.returnMode = returnMode;
	}

	public String getFixedAltitudeList() {
		return fixedAltitudeList;
	}

	public void setFixedAltitudeList(String fixedAltitudeList) {
		this.fixedAltitudeList = fixedAltitudeList;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	public int getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(int startPoint) {
		this.startPoint = startPoint;
	}

	public int getEndPoint(){
		return endPoint;
	}

	public void setEndPoint(int endPoint){
		this.endPoint = endPoint;
	}

	public int isPolygon() {
		return isPolygon;
	}

	public void setIsPolygon(int isPolygon) {
		this.isPolygon = isPolygon;
	}
}