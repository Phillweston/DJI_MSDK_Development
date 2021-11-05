package com.ew.autofly.entity;

import java.io.Serializable;
import java.util.Date;

import com.esri.core.geometry.Polygon;

public class Mission implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String snapshot;
	private int status;
	private int altitude;
	private int flySpeed;
	private int gimbalAngle;
	private double resolutionRate;
	private Polygon polygon;
	private Date createDate;
	private Date startTime;
	private Date endTime;
	private String workMode;
	private String workStep;

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
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public Polygon getPolygon() {
		return polygon;
	}
	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getSnapshot() {
		return snapshot;
	}
	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}
	public double getResolutionRate() {
		return resolutionRate;
	}
	public void setResolutionRate(double resolutionRate) {
		this.resolutionRate = resolutionRate;
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

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}

