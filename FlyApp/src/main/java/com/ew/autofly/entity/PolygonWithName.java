package com.ew.autofly.entity;

import com.esri.core.geometry.Polygon;

public class PolygonWithName {
	private String gid = "";
	private Polygon polygon;
	private String name = "";
	private int fillColor = -256;
	private int lineColor = -256;

	public String getGid() {
		return this.gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public Polygon getPolygon() {
		return this.polygon;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFillColor() {
		return this.fillColor;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public int getLineColor() {
		return this.lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}
}