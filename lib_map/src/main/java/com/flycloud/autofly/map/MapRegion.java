package com.flycloud.autofly.map;


public enum MapRegion {

    CHINA(0),//中国大陆
    CHINA_HONGKONG(1),//香港
    CHINA_MACAO(2),//澳门
    CHINA_TAIWAN(3),//台湾
    OTHER(-1);

    private int value;

    MapRegion(int paramInt) {
        this.value = paramInt;
    }

    public int value() {
        return this.value;
    }

    public boolean _equals(int paramInt) {
        return this.value == paramInt;
    }

    public static MapRegion find(int paramInt) {
        MapRegion mMapRegion = CHINA;
        for (int i = 0; i < values().length; i++) {
            if (values()[i]._equals(paramInt)) {
                mMapRegion = values()[i];
                break;
            }
        }
        return mMapRegion;
    }

}
