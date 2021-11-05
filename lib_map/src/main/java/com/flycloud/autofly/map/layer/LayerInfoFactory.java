package com.flycloud.autofly.map.layer;

import com.esri.arcgisruntime.data.TileKey;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.flycloud.autofly.map.MapLayerType;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;

import java.util.Calendar;
import java.util.TimeZone;


public class LayerInfoFactory {

    private static final String TAG = "LayerInfoFactory";

    private static class AMAP {
        private static final String VECTOR_NAME = "7";
        private static final String IMAGE_NAME = "6";
        private static final String ROAD_NAME = "8";
    }

    private static class BAIDU_MAP {
        private static final String VECTOR_NAME = "pl";
        private static final String ROAD_NAME = "sl";
    }

    private static class TENCENT_MAP {
        private static final String VECTOR_NAME = "0";
        private static final String VECTOR_NIGHT_NAME = "1";
        private static final String ROAD_NAME = "3";
        private static final String IMAGE_NAME = "sateTiles";
        private static final String TERRAIN_NAME = "demTiles";
    }

    private static class GOOGLE_MAP {
        private static final String VECTOR_NAME = "m";
        private static final String IMAGE_IMAGE = "s,h";
        private static final String TERRAIN_NAME = "t,r";
    }

    private static final Point ORIGIN = new Point(-20037508.342787, 20037508.342787, SpatialReferences.getWebMercator());

    private static final int SRID = SpatialReferences.getWebMercator().getWkid();

    private static final double X_MIN = -22041257.773878;
    private static final double Y_MIN = -32673939.6727517;
    private static final double X_MAX = 22041257.773878;
    private static final double Y_MAX = 20851350.0432886;


    private static final double[] SCALES = {591657527.591555,
            295828763.79577702, 147914381.89788899, 73957190.948944002,
            36978595.474472001, 18489297.737236001, 9244648.8686180003,
            4622324.4343090001, 2311162.217155, 1155581.108577, 577790.554289,
            288895.277144, 144447.638572, 72223.819286, 36111.909643,
            18055.954822, 9027.9774109999998, 4513.9887049999998, 2256.994353,
            1128.4971760000001};
    private static final double[] RESOLUTIONS = {156543.03392800014,
            78271.516963999937, 39135.758482000092, 19567.879240999919,
            9783.9396204999593, 4891.9698102499797, 2445.9849051249898,
            1222.9924525624949, 611.49622628138, 305.748113140558,
            152.874056570411, 76.4370282850732, 38.2185141425366,
            19.1092570712683, 9.55462853563415, 4.7773142679493699,
            2.3886571339746849, 1.1943285668550503, 0.59716428355981721,
            0.29858214164761665};


    public static MapLayerInfo createLayerInfo(MapServiceProvider serviceProvider, MapRegion mapRegion, MapLayerType layerType) {
        MapLayerInfo layerInfo = new MapLayerInfo();
        layerInfo.setLayerType(layerType);
        layerInfo.setServiceProvider(serviceProvider);
        layerInfo.setMapRegion(mapRegion);
        handleLayerInfo(layerInfo);

        switch (serviceProvider) {
            case AMAP:
                switch (layerType) {
                    case VECTOR:
                        layerInfo.setLayerName(AMAP.VECTOR_NAME);
                        break;
                    case IMAGE:
                        layerInfo.setLayerName(AMAP.IMAGE_NAME);
                        break;
                    case ROAD:
                        layerInfo.setLayerName(AMAP.ROAD_NAME);
                        break;
                }
                break;
            case BAIDU_MAP:
                switch (layerType) {
                    case VECTOR:
                        layerInfo.setLayerName(BAIDU_MAP.VECTOR_NAME);
                        layerInfo.setMinZoomLevel(3);
                        layerInfo.setMaxZoomLevel(19);
                        break;
                    case IMAGE:
                        layerInfo.setMinZoomLevel(3);
                        layerInfo.setMaxZoomLevel(19);
                        break;
                    case ROAD:
                        layerInfo.setLayerName(BAIDU_MAP.ROAD_NAME);
                        layerInfo.setMinZoomLevel(3);
                        layerInfo.setMaxZoomLevel(19);
                        break;
                    case TRAFFIC:
                        layerInfo.setMinZoomLevel(3);
                        layerInfo.setMaxZoomLevel(19);
                        break;
                }
                break;
            case TENCENT_MAP:
                switch (layerType) {
                    case VECTOR:
                        layerInfo.setLayerName(TENCENT_MAP.VECTOR_NAME);
                        break;
                    case IMAGE:
                        layerInfo.setLayerName(TENCENT_MAP.IMAGE_NAME);
                        break;
                    case ROAD:
                        layerInfo.setLayerName(TENCENT_MAP.ROAD_NAME);
                    case TRAFFIC:
                        layerInfo.setLayerName(TENCENT_MAP.TERRAIN_NAME);
                        break;
                }
                break;
            case GOOGLE_MAP:
                switch (layerType) {
                    case VECTOR:
                        layerInfo.setLayerName(GOOGLE_MAP.VECTOR_NAME);
                        layerInfo.setMaxZoomLevel(21);
                        break;
                    case IMAGE:
                        layerInfo.setLayerName(GOOGLE_MAP.IMAGE_IMAGE);
                        layerInfo.setMaxZoomLevel(21);
                        break;
                    case TRAFFIC:
                        layerInfo.setLayerName(GOOGLE_MAP.TERRAIN_NAME);
                        layerInfo.setMaxZoomLevel(21);
                        break;
                }
                break;
        }
        return layerInfo;
    }

    public static String getLayerUrl(MapLayerInfo layerInfo, TileKey tileKey) {
        String layerUrl = null;
        MapServiceProvider serviceProvider = layerInfo.getServiceProvider();
        MapLayerType layerType = layerInfo.getLayerType();
        MapRegion mapRegion = layerInfo.getMapRegion();

        int col = tileKey.getColumn();
        int row = tileKey.getRow();
        int level = tileKey.getLevel();

        switch (serviceProvider) {
            case AMAP:
                switch (layerType) {
                    case VECTOR:
                    case IMAGE:
                    case ROAD:
                        layerUrl = "http://webst0" + ((col + row) % 4 + 1) + ".is.autonavi.com/appmaptile?style="
                                + layerInfo.getLayerName()
                                + "&x=" + col + "&y=" + row + "&z=" + level;
                        break;
                    case TRAFFIC:
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                        int hh = calendar.get(Calendar.HOUR_OF_DAY);
                        int mm = calendar.get(Calendar.MINUTE);
                        layerUrl = "http://history.traffic.amap.com/traffic?type=2"
                                + "&day=" + day + "&hh=" + hh + "&mm=" + mm
                                + "&x=" + col + "&y=" + row + "&z=" + level;
                        break;
                }
                break;
            case BAIDU_MAP:
                switch (layerType) {
                    case VECTOR:
                    case ROAD:
                        int offsetV = (int) (Math.pow(2, level - 1));
                        layerUrl = "http://online" + ((col + row) % 8 + 1) + ".map.bdimg.com/onlinelabel/?qt=tile"
                                + "&x=" + (col - offsetV) + "&y=" + (offsetV - row - 1) + "&z=" + level
                                + "&styles=" + layerInfo.getLayerName();
                        break;
                    case IMAGE:
                        int offsetI = (int) (Math.pow(2, level - 1));
                        layerUrl = "http://shangetu" + ((col + row) % 8 + 1) + ".map.bdimg.com/it/u="
                                + "x=" + (col - offsetI) + ";y=" + (offsetI - row - 1) + ";z=" + level
                                + ";v=009;type=sate&fm=46";
                        break;
                    case TRAFFIC:
                        int offsetT = (int) (Math.pow(2, level - 1));
                        layerUrl = "http://its.map.baidu.com:8002/traffic/TrafficTileService?"
                                + "level=" + level + "&x=" + (col - offsetT) + "&y=" + (offsetT - row - 1)
                                + "&time=" + System.currentTimeMillis();
                        break;
                }
                break;
            case TENCENT_MAP:
                switch (layerType) {
                    case VECTOR:
                    case ROAD:
                        row = (int) Math.pow(2, level) - 1 - row;

                        layerUrl = "http://rt" + (col % 4) + ".map.gtimg.com/tile?"
                                + "z=" + level + "&x=" + col + "&y=" + row
                                + "&type=vector&styleid=" + layerInfo.getLayerName();
                        break;
                    case IMAGE:
                    case TRAFFIC:
                        row = (int) Math.pow(2, level) - 1 - row;
                        layerUrl = "http://p" + (col % 4) + ".map.gtimg.com/"
                                + layerInfo.getLayerName()
                                + "/" + level + "/" + (int) Math.floor(col / 16) + "/" + (int) Math.floor(row / 16)
                                + "/" + col + "_" + row + ".jpg";
                        break;
                }
                break;
            case GOOGLE_MAP:
                switch (layerType) {
                    case VECTOR:
                    case IMAGE:
                    case TRAFFIC:
                        if (mapRegion == MapRegion.CHINA) {
                            layerUrl = "http://mt" + (col % 4) + ".google.cn/vt/lyrs="
                                    + layerInfo.getLayerName() + "&hl=zh-CN&gl=cn&x="
                                    + col + "&" + "y=" + row + "&" + "z=" + level
                                    + "&scale=2&s=Galileo";
                        } else if (mapRegion == MapRegion.CHINA_HONGKONG || mapRegion == MapRegion.CHINA_MACAO
                                || mapRegion == MapRegion.CHINA_TAIWAN) {
                            layerUrl = "http://mt" + (col % 4) + ".google.cn/vt/lyrs="
                                    + layerInfo.getLayerName() + "@218000000&hl=zh-CN&src=app&x="
                                    + col + "&y=" + row + "&z=" + level
                                    + "&scale=2&s=Galileo";
                        } else {
                            layerUrl = "http://mt" + (col % 4) + ".google.cn/vt/lyrs="
                                    + layerInfo.getLayerName() + "@218000000&hl=en&src=app&x="
                                    + col + "&y=" + row + "&z=" + level
                                    + "&scale=2&s=Galileo";
                        }

                        break;
                }
                break;
            case OPENCYCLE_MAP:
                layerUrl = "https://c.tile.thunderforest.com/landscape/" + level + "/" + col + "/" + row + ".png?apikey=0206f1118140419fb7afbf96b264880d";
                break;
        }
        return layerUrl;
    }

    private static void handleLayerInfo(MapLayerInfo layerInfo) {

        layerInfo.setOrigin(LayerInfoFactory.ORIGIN);
        layerInfo.setSrid(LayerInfoFactory.SRID);
        layerInfo.setxMin(LayerInfoFactory.X_MIN);
        layerInfo.setyMin(LayerInfoFactory.Y_MIN);
        layerInfo.setxMax(LayerInfoFactory.X_MAX);
        layerInfo.setyMax(LayerInfoFactory.Y_MAX);
        layerInfo.setScales(LayerInfoFactory.SCALES);
        layerInfo.setResolutions(LayerInfoFactory.RESOLUTIONS);

    }


}
