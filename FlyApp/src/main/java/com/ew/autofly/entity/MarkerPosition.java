package com.ew.autofly.entity;

public class MarkerPosition {
	private double latitude;
	private double longitude;
	private int imageId;
	private int imageWidth;
	private int imageHeight;
	private double angle;

	public MarkerPosition() {
	}

	public MarkerPosition(double latitude, double longitude, int imageId,
			double angle) {
		this.imageId = imageId;
		this.angle = angle;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public MarkerPosition(double latitude, double longitude, int imageId,
			int width, int height, double angle) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.imageId = imageId;
		this.angle = angle;
		this.imageWidth = width;
		this.imageHeight = height;
	}

	public int getImageId() {
		return this.imageId;
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

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public double getAngle() {
		return this.angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public int getImageWidth() {
		return this.imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return this.imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
}