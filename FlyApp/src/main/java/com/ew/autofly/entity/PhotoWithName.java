package com.ew.autofly.entity;

import com.esri.core.geometry.Point;

public class PhotoWithName {
	private String id = "";
	private Point point;
	private String filename = "";

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getFileName() {
		return this.filename;
	}

	public void setFileName(String name) {
		this.filename = name;
	}
}