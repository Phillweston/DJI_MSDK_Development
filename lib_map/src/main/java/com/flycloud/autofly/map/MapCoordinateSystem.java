package com.flycloud.autofly.map;


public enum MapCoordinateSystem {

    WGS84(0),//谷歌(国外)、OSM
    GCJ02(1),//谷歌(国内)、高德、腾讯
    BD09(2);

    private int value;

    MapCoordinateSystem(int paramInt) {
        this.value = paramInt;
    }

    public int value() {
        return this.value;
    }

    public boolean _equals(int paramInt) {
        return this.value == paramInt;
    }

    public static MapCoordinateSystem find(int paramInt) {
        MapCoordinateSystem mMapCoordinateSystem = WGS84;
        for (int i = 0; i < values().length; i++) {
            if (values()[i]._equals(paramInt)) {
                mMapCoordinateSystem = values()[i];
                break;
            }
        }
        return mMapCoordinateSystem;
    }
}
