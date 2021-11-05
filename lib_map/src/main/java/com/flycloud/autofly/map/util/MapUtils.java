package com.flycloud.autofly.map.util;

import com.flycloud.autofly.map.MapCoordinateSystem;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;


public class MapUtils {

    /**
     * 获取地图坐标系统
     *
     * @param mapServiceProvider
     */
    public static MapCoordinateSystem getMapCoordinateSystem(MapServiceProvider mapServiceProvider, MapRegion region) {

        MapCoordinateSystem mapCoordinateSystem = MapCoordinateSystem.GCJ02;
        if (mapServiceProvider == MapServiceProvider.GOOGLE_MAP) {
            if (region == MapRegion.CHINA) {//不包含港澳台
                mapCoordinateSystem = MapCoordinateSystem.GCJ02;
            } else {
                mapCoordinateSystem = MapCoordinateSystem.WGS84;
            }
        } else if (mapServiceProvider == MapServiceProvider.AMAP
                || mapServiceProvider == MapServiceProvider.TENCENT_MAP) {
            if (region == MapRegion.CHINA || region == MapRegion.CHINA_HONGKONG
                    || region == MapRegion.CHINA_MACAO) {
                mapCoordinateSystem = MapCoordinateSystem.GCJ02;
            } else {
                mapCoordinateSystem = MapCoordinateSystem.WGS84;
            }
        } else if (mapServiceProvider == MapServiceProvider.OPENCYCLE_MAP) {
            mapCoordinateSystem = MapCoordinateSystem.WGS84;
        } else if (mapServiceProvider == MapServiceProvider.BAIDU_MAP) {
          
            if (region != MapRegion.OTHER) {
                mapCoordinateSystem = MapCoordinateSystem.BD09;
            } else {
                mapCoordinateSystem = MapCoordinateSystem.WGS84;
            }
        }
        return mapCoordinateSystem;
    }
}
