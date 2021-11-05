package com.ew.autofly.entity;

import com.esri.core.geometry.Polyline;

public class PolylineWithName {
	private String gid = "";
	private Polyline polyline;
	private String name = "";
	private int multiGeometry = 0;
	private float lineWidth = 2.0F;
	private int lineColor = -256;

	public String getGid() {
		return this.gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public Polyline getPolyline() {
		return this.polyline;
	}

	public void setPolyline(Polyline polyline) {
		this.polyline = polyline;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMultiGeometry() {
		return multiGeometry;
	}

	public void setMultiGeometry(int multiGeometry) {
		this.multiGeometry = multiGeometry;
	}

	public float getLineWidth() {
		return this.lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public int getLineColor() {
		return this.lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}


}