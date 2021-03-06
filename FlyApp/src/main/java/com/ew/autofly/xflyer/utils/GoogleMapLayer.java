package com.ew.autofly.xflyer.utils;

import android.util.Log;

import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.internal.io.handler.a;
import com.flycloud.autofly.map.MapRegion;

import java.io.File;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

public class GoogleMapLayer extends TiledServiceLayer {
    private int minLevel = 0;
    private int maxLevel = 19;
    private CommonConstants.GoogleMapType googleMapType;
    private MapRegion mMapRegion;
    private String cacheDirectory;
    private double[] scales = {591657527.591555D, 295828763.79577702D,
            147914381.89788899D, 73957190.948944002D, 36978595.474472001D,
            18489297.737236001D, 9244648.8686180003D, 4622324.4343090001D,
            2311162.2171550002D, 1155581.108577D, 577790.55428899999D,
            288895.27714399999D, 144447.638572D, 72223.819285999998D,
            36111.909642999999D, 18055.954822D, 9027.9774109999998D,
            4513.9887049999998D, 2256.994353D, 1128.4971760000001D};

    private double[] resolutions = {156543.03392800014D, 78271.516963999937D,
            39135.758482000092D, 19567.879240999919D, 9783.9396204999593D,
            4891.9698102499797D, 2445.9849051249898D, 1222.9924525624949D,
            611.49622628138002D, 305.74811314055802D, 152.874056570411D,
            76.437028285073197D, 38.218514142536598D, 19.109257071268299D,
            9.55462853563415D, 4.77731426794937D, 2.388657133974685D,
            1.19432856685505D, 0.5971642835598172D, 0.2985821416476167D};

    private Point origin = new Point(-20037508.342787001D, 20037508.342787001D);

    private int dpi = 96;

    private int tileWidth = 256;
    private int tileHeight = 256;

    public GoogleMapLayer(CommonConstants.GoogleMapType googleMapType,
                          String cacheDirectory) {
        this(googleMapType, null, cacheDirectory);
    }


    public GoogleMapLayer(CommonConstants.GoogleMapType googleMapType, MapRegion mMapRegion,
                          String cacheDirectory) {
        super(true);
        this.googleMapType = googleMapType;
        this.mMapRegion = mMapRegion;
        this.cacheDirectory = cacheDirectory;
        init();
    }

    private void init() {
        try {
            getServiceExecutor().submit(new Runnable() {
                public void run() {
                    GoogleMapLayer.this.initLayer();
                }
            });
        } catch (RejectedExecutionException rejectedexecutionexception) {
            Log.e("Google Map Layer", "initialization of the layer failed.",
                    rejectedexecutionexception);
        }
    }

    protected byte[] getTile(int level, int col, int row) throws Exception {
        if ((level > this.maxLevel) || (level < this.minLevel)) {
            return new byte[0];
        }


        String url = "http://mt" + col % 4 + ".google.cn/vt/lyrs=s@192&v=w2.114&hl=zh-CN&gl=cn&" + "x=" + col + "&" + "y=" + row + "&" + "z=" + level + "&s=Galil";
        if (mMapRegion != null) {

            String layerName = "s";
            if (this.googleMapType == CommonConstants.GoogleMapType.ImageMap) {
                layerName = "s,h";
            } else if (this.googleMapType == CommonConstants.GoogleMapType.VectorMap) {
                layerName = "m";
            } else if (this.googleMapType == CommonConstants.GoogleMapType.RoadMap) {
                layerName = "h";
            }

            if (mMapRegion == MapRegion.CHINA) {
                url = "http://mt" + (col % 4) + ".google.cn/vt/lyrs="
                        + layerName + "&hl=zh-CN&gl=cn&x="
                        + col + "&" + "y=" + row + "&" + "z=" + level
                        + "&scale=2&s=Galileo";
            } else if (mMapRegion == MapRegion.CHINA_HONGKONG || mMapRegion == MapRegion.CHINA_MACAO
                    || mMapRegion == MapRegion.CHINA_TAIWAN) {
                url = "http://mt" + (col % 4) + ".google.cn/vt/lyrs="
                        + layerName + "@218000000&hl=zh-CN&src=app&x="
                        + col + "&y=" + row + "&z=" + level
                        + "&scale=2&s=Galileo";
            } else {
                url = "http://mt" + (col % 4) + ".google.cn/vt/lyrs="
                        + layerName + "@218000000&hl=en&src=app&x="
                        + col + "&y=" + row + "&z=" + level
                        + "&scale=2&s=Galileo";
            }
        } else {


            if (this.googleMapType == CommonConstants.GoogleMapType.ImageMap) {
                url = "http://mt" + col % 4
                        + ".google.cn/vt/lyrs=s&v=w2.114&hl=zh-CN&gl=cn&" + "x="
                        + col + "&" + "y=" + row + "&" + "z=" + level + "&s=Galil";
            } else if (this.googleMapType == CommonConstants.GoogleMapType.VectorMap) {
                url = "http://mt" + col % 4
                        + ".google.cn/vt/lyrs=m@177000000&v=w2.114&hl=zh-CN&gl=cn&"
                        + "x=" + col + "&" + "y=" + row + "&" + "z=" + level
                        + "&s=Galil";
            } else if (this.googleMapType == CommonConstants.GoogleMapType.RoadMap) {
                url = "http://mt"
                        + col
                        % 4
                        + ".google.cn/vt/imgtp=png32&lyrs=h@177000000&v=w2.114&hl=zh-CN&gl=cn&"
                        + "x=" + col + "&" + "y=" + row + "&" + "z=" + level
                        + "&s=Galil";
            }
        }


        String region = "";

        if (mMapRegion != null) {
            if (mMapRegion != MapRegion.CHINA) {//???????????????
                region = "Region_" + mMapRegion.name() + File.separator;
            }
        }

        String filePath = this.cacheDirectory
                + File.separator
                + region
                + this.googleMapType.toString()
                + File.separator;

        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String oldFileName = filePath + String.format(
                "L%dR%dC%d.png",
                new Object[]{Integer.valueOf(level),
                        Integer.valueOf(row), Integer.valueOf(col)});
        String newFileName = filePath + String.format(
                "L%dR%dC%d",
                new Object[]{Integer.valueOf(level),
                        Integer.valueOf(row), Integer.valueOf(col)});
        File oldFileImage = new File(oldFileName);
        File newFileImage = new File(newFileName);
        if (oldFileImage.exists()) {
            return FileUtils.getBytesFromFile(oldFileImage);
        }
        if (newFileImage.exists()) {
            return FileUtils.getBytesFromFile(newFileImage);
        }
        Map map = null;

        byte[] bufferImage = a.a(url, map);

        FileUtils.saveFileFromBytes(bufferImage, newFileName);
        return bufferImage;
    }

    protected void initLayer() {
        if (getID() == 0L) {
            this.nativeHandle = create();
            changeStatus(OnStatusChangedListener.STATUS.fromInt(-1000));
        } else {
            setDefaultSpatialReference(SpatialReference.create(102100));

            setFullExtent(new Envelope(-22041257.773878001D,
                    -32673939.672751699D, 22041257.773878001D,
                    20851350.0432886D));

            setTileInfo(new TiledServiceLayer.TileInfo(this.origin,
                    this.scales, this.resolutions, this.scales.length,
                    this.dpi, this.tileWidth, this.tileHeight));

            super.initLayer();
        }
    }
}