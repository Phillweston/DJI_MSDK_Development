package com.flycloud.autofly.map;


public enum MapServiceProvider {
    GOOGLE_MAP(0),//谷歌地图
    AMAP(1),//高德地图
    TENCENT_MAP(2),//腾讯地图
    BAIDU_MAP(3),//百度地图
    OPENCYCLE_MAP(4);

    private int value;

    private String name;

    private MapServiceProvider(
            int paramInt) {
        this.value = paramInt;
    }

    public int value() {
        return this.value;
    }

    public boolean _equals(int paramInt) {
        return this.value == paramInt;
    }

    public static MapServiceProvider find(int paramInt) {
        MapServiceProvider mMapServiceProvider = GOOGLE_MAP;
        for (int i = 0; i < values().length; i++) {
            if (values()[i]._equals(paramInt)) {
                mMapServiceProvider = values()[i];
                break;
            }
        }
        return mMapServiceProvider;
    }
}
