package com.flycloud.autofly.map.layer;

import com.esri.arcgisruntime.arcgisservices.LevelOfDetail;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.flycloud.autofly.map.MapLayerType;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;

import java.util.ArrayList;
import java.util.List;


public class MapLayerInfo {

    private String url;
    private String layerName;

    private int minZoomLevel = 0;
    private int maxZoomLevel = 17;

    private double xMin;
    private double yMin;
    private double xMax;
    private double yMax;

    private int tileWidth = 256;
    private int tileHeight = 256;

    private double[] scales;
    private double[] resolutions;

    private int dpi = 96;

    private int srid;

    private Point origin;

    private String tileMatrixSet;

    private MapServiceProvider serviceProvider;
    private MapRegion mapRegion;
    private MapLayerType layerType;


    public TileInfo getTileInfo() {
        List<LevelOfDetail> levelOfDetails = new ArrayList<>();
        for (int i = 0; i < resolutions.length; i++) {
            LevelOfDetail levelOfDetail = new LevelOfDetail(i, resolutions[i], scales[i]);
            levelOfDetails.add(levelOfDetail);
        }
        return new TileInfo(96, TileInfo.ImageFormat.UNKNOWN, levelOfDetails, origin, SpatialReference.create(srid), 256, 256);
    }

    public Envelope getEnvelope() {
        return new Envelope(xMin, yMin, xMax, yMax, SpatialReference.create(srid));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public int getMinZoomLevel() {
        return minZoomLevel;
    }

    public void setMinZoomLevel(int minZoomLevel) {
        this.minZoomLevel = minZoomLevel;
    }

    public int getMaxZoomLevel() {
        return maxZoomLevel;
    }

    public void setMaxZoomLevel(int maxZoomLevel) {
        this.maxZoomLevel = maxZoomLevel;
    }

    public double getxMin() {
        return xMin;
    }

    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public double getxMax() {
        return xMax;
    }

    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public double[] getScales() {
        return scales;
    }

    public void setScales(double[] scales) {
        this.scales = scales;
    }

    public double[] getResolutions() {
        return resolutions;
    }

    public void setResolutions(double[] resolutions) {
        this.resolutions = resolutions;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public int getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public String getTileMatrixSet() {
        return tileMatrixSet;
    }

    public void setTileMatrixSet(String tileMatrixSet) {
        this.tileMatrixSet = tileMatrixSet;
    }

    public MapLayerType getLayerType() {
        return layerType;
    }

    public void setLayerType(MapLayerType layerType) {
        this.layerType = layerType;
    }


    public MapServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(MapServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public MapRegion getMapRegion() {
        return mapRegion;
    }

    public void setMapRegion(MapRegion mapRegion) {
        this.mapRegion = mapRegion;
    }
}
