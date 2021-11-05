package com.ew.autofly.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;



public class LocationCoordinate implements Parcelable, Serializable {

    public double latitude = 0.0D;
    public double longitude = 0.0D;
    public float altitude = 0.0F;

    public LocationCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationCoordinate(double latitude, double longitude, float altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeFloat(this.altitude);
    }

    protected LocationCoordinate(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.altitude = in.readFloat();
    }

    public static final Creator<LocationCoordinate> CREATOR = new Creator<LocationCoordinate>() {
        @Override
        public LocationCoordinate createFromParcel(Parcel source) {
            return new LocationCoordinate(source);
        }

        @Override
        public LocationCoordinate[] newArray(int size) {
            return new LocationCoordinate[size];
        }
    };
}
