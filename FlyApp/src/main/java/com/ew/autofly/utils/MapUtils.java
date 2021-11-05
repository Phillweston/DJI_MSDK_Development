package com.ew.autofly.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ew.autofly.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.interfaces.OnMapTouched;
import com.ew.autofly.entity.PhotoWithName;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.utils.arcgis.ArcgisMapUtil;
import com.ew.autofly.xflyer.utils.CommonConstants;
import com.ew.autofly.xflyer.utils.CoordinateUtils;
import com.ew.autofly.xflyer.utils.GoogleMapLayer;
import com.ew.autofly.xflyer.utils.MBTilesHelper;
import com.ew.autofly.xflyer.utils.MBTilesLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapUtils {
    private final String TAG = "MapUtils";
    private final boolean d = AppConstant.DEBUG;
    private static MapUtils mInstance = null;
    private RelativeLayout mMapContent = null;
    private MapView map = null;
    private HashMap<String, Layer> mLoadMapLayers;
    private GoogleMapLayer mGoogleMapLayer = null;
    private GoogleMapLayer mGoogleRoadMapLayer = null;
    private GraphicsLayer mGpsLayer = null;
    private GraphicsLayer mGpsTrackLayer = null;
    private GraphicsLayer mGpsTrackHistoryLayer = null;
    private GraphicsLayer mAnnoLayer = null;
    private GraphicsLayer mPointLayer = null;
    private GraphicsLayer mPolylineLayer = null;
    private GraphicsLayer mPolygonLayer = null;
    private GraphicsLayer mPhotoLayer = null;
    private boolean isCancelTouch = false;
    private Object lock = new Object();

    public GraphicsLayer getGpsTrackLayer() {
        return mGpsTrackLayer;
    }

    public GraphicsLayer getGpsTrackHistoryLayer() {
        return mGpsTrackHistoryLayer;
    }

    public GraphicsLayer getPhotoLayer() {
        return mPhotoLayer;
    }

    public GraphicsLayer getPointLayer() {
        return mPointLayer;
    }

    public GraphicsLayer getPolylineLayer() {
        return mPolylineLayer;
    }

    public GraphicsLayer getPolygonLayer() {
        return mPolygonLayer;
    }

    private OnMapTouched mOnTouchedListener = null;

    public void setOnMapTouchedListener(OnMapTouched listener) {
        mOnTouchedListener = listener;
    }

    public void onPause() {
        if (map != null) {
            map.pause();
        }
    }

    public void onResume() {
        if (map != null) {
            map.unpause();
        }
    }

    public void removeAllLayers() {
        checkMapObject();
        mGoogleMapLayer = null;
        mGoogleRoadMapLayer = null;
        mLoadMapLayers.clear();
        if (mPointLayer != null) {
            mPointLayer.removeAll();
        }
        if (mPolylineLayer != null) {
            mPolylineLayer.removeAll();
        }
        if (mPolygonLayer != null) {
            mPolygonLayer.removeAll();
        }
        map.removeAll();
    }

    public void refreshPhotos() {
        mPhotoLayer.removeAll();
        try {
            DataBaseUtils.getInstance(map.getContext()).getPhotos(new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {

                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {
                    ArrayList<PhotoWithName> points = (ArrayList<PhotoWithName>) result;
                    if (points == null) {
                        return;
                    }
                    PictureMarkerSymbol s = new PictureMarkerSymbol(
                            map.getContext().getResources().getDrawable(R.drawable.photoplace));
                    boolean isGoogleShowMode = isGoogleMapOnShow();
                    for (PhotoWithName p : points) {
                        Map<String, Object> attrs = new HashMap<String, Object>();
                        attrs.put("filename", p.getFileName());
                        attrs.put("id", p.getId());
                        if(isGoogleShowMode) {
                            LatLngInfo pGCJ = CoordinateUtils.gps84_To_Gcj02(p.getPoint().getY(),p.getPoint().getX());
                            LatLngInfo pCurrent = CoordinateUtils.lonLatToMercator(pGCJ.longitude,pGCJ.latitude);
                            Graphic g = new Graphic(new Point(pCurrent.longitude,pCurrent.latitude), s, attrs);
                            mPhotoLayer.addGraphic(g);
                        }else{
                            Graphic g = new Graphic(p.getPoint(), s, attrs);
                            mPhotoLayer.addGraphic(g);
                        }
                    }
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });
        } catch (Exception e) {
            Log.e("MapUtils", e.getMessage());
        }
    }

  
    public interface OnMapTouchListener {
        void onExtentChanged();

        void onClick(Point point, android.graphics.Point screenPoint);

        void onLongClick(android.graphics.Point point);

        void onTouch(MotionEvent event);
    }

    private ArrayList<OnMapTouchListener> mMapOnTouchListeners = null;

    public void addMapOnTouchListener(OnMapTouchListener listener) {
        if (mMapOnTouchListeners == null) {
            mMapOnTouchListeners = new ArrayList<>();
        }
        synchronized (lock) {
            if (listener != null) {
                mMapOnTouchListeners.add(listener);
            }
        }
    }

    public synchronized void removeMapOnClickListener(OnMapTouchListener listener) {
        if (mMapOnTouchListeners == null) {
            mMapOnTouchListeners = new ArrayList<>();
            return;
        }
        synchronized (lock) {
            mMapOnTouchListeners.remove(listener);
        }
    }

    public interface OnLayerLoaded {
        void onLoaded(Object object);
    }

    private OnLayerLoaded onLayerLoaded = null;

    public OnLayerLoaded getOnLayerLoaded() {
        return onLayerLoaded;
    }

    public void setOnLayerLoadedListener(OnLayerLoaded onLayerLoadedListener) {
        this.onLayerLoaded = onLayerLoadedListener;
    }

    public MapView getMap() {
        return map;
    }

    public void setMap(MapView mapView) {
        map = mapView;
    }

    private void log(String log) {
        Log.d(TAG, StringUtils.isEmptyOrNull(log) ? "" : log);
    }

    public void onDestroy() {
        mInstance = null;
        if (mMapOnTouchListeners != null) {
            mMapOnTouchListeners.clear();
            mMapOnTouchListeners = null;
        }
        onLayerLoaded = null;
        if (mPointLayer != null) {
            mPointLayer.removeAll();
        }
        if (mPolylineLayer != null) {
            mPolylineLayer.removeAll();
        }
        if (mPolygonLayer != null) {
            mPolygonLayer.removeAll();
        }

        map.removeAll();
        mPointLayer = null;
        mPolylineLayer = null;
        mPolygonLayer = null;
        mGoogleMapLayer = null;
        mGoogleRoadMapLayer = null;
        mGpsLayer = null;
        mGpsTrackHistoryLayer = null;
        mGpsTrackLayer = null;
        mPhotoLayer = null;
        mAnnoLayer = null;
        map = null;
    }

    public void resetMapView() {
        if (mMapContent == null) {
            Log.e(TAG, "Map content is null");
            return;
        }
        mMapContent.removeAllViews();

        map = new MapView(mMapContent.getContext());
        map.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        map.setLayoutParams(params);

        mMapContent.addView(map);

        map.setBackgroundColor(Color.parseColor("#e0e0e0"));
        mLoadMapLayers = new HashMap<>();
        map.setOnTouchListener(new CustomTouchListener(mMapContent.getContext(), map));
        map.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (status == STATUS.LAYER_LOADED && onLayerLoaded != null) {
                    ArcgisMapUtil.initialize(map.getMinScale());
                    onLayerLoaded.onLoaded(o);
                }
            }
        });
        if (mGpsLayer != null) {
            mGpsLayer.removeAll();
            mGpsLayer = null;
        }
        if (mGpsTrackLayer != null) {
            mGpsTrackLayer.removeAll();
            mGpsTrackLayer = null;
        }
        if (mGpsTrackHistoryLayer != null) {
            mGpsTrackHistoryLayer.removeAll();
            mGpsTrackHistoryLayer = null;
        }
        if (mPhotoLayer != null) {
            mPhotoLayer.removeAll();
            mPhotoLayer = null;
        }
        if (mAnnoLayer != null) {
            mAnnoLayer.removeAll();
            mAnnoLayer = null;
        }

        if (mPointLayer != null) {
            mPointLayer.removeAll();
            mPointLayer = null;
        }
        if (mPolylineLayer != null) {
            mPolylineLayer.removeAll();
            mPolylineLayer = null;
        }
        if (mPolygonLayer != null) {
            mPolygonLayer.removeAll();
            mPolygonLayer = null;
        }

        if(mGoogleMapLayer == null){
            addGoogleMap();
        }
    }

    private MapUtils(RelativeLayout mapContent) {
        mMapContent = mapContent;
        try {
            resetMapView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CancelMapOnTouch(boolean cancel) {
        isCancelTouch = cancel;
    }

    private class CustomTouchListener extends MapOnTouchListener {
        public CustomTouchListener(Context context, MapView view) {
            super(context, view);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mOnTouchedListener != null) {
                mOnTouchedListener.onTouch(event);
            }

            if (mMapOnTouchListeners != null) {
                for (OnMapTouchListener listener : mMapOnTouchListeners) {
                    if (listener == null) continue;
                    listener.onExtentChanged();
                }
            }
            if (mMapOnTouchListeners != null) {
                for (OnMapTouchListener listener : mMapOnTouchListeners) {
                    if (listener == null) continue;
                    listener.onTouch(event);
                }
            }
            if (!isCancelTouch) {
                return super.onTouch(v, event);
            } else {
                return true;
            }
        }

        @Override
        public boolean onSingleTap(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && mMapOnTouchListeners != null) {
                if (mMapOnTouchListeners != null) {
                    for (OnMapTouchListener listener : mMapOnTouchListeners) {
                        if (listener == null) continue;

                        listener.onClick(map.toMapPoint(event.getX(), event.getY()),
                                new android.graphics.Point((int) event.getX(), (int) event.getY()));
                    }
                }
            }
            return super.onSingleTap(event);
        }

        @Override
        public void onLongPress(MotionEvent point) {
            super.onLongPress(point);
            synchronized (lock) {
                if (mMapOnTouchListeners != null) {
                    try {
                        for (OnMapTouchListener listener : mMapOnTouchListeners) {
                            if (listener == null) continue;
                            listener.onLongClick(new android.graphics.Point((int) point.getX(), (int) point.getY()));
                        }
                    } catch (Exception ex) {

                    }

                }
            }
        }
    }

    public static MapUtils getInstance(RelativeLayout mapContent) {
        if (mInstance == null) {
            synchronized (MapUtils.class) {
                if (mInstance == null) {
                    mInstance = new MapUtils(mapContent);
                }
            }
        }
        return mInstance;
    }

    private void checkMapObject() {
        if (map == null) {
            throw new NullPointerException("Map对象为空,请不要手工清除该对象,应调用 MapUtils.destroy()方法清理");
        }
    }

    public HashMap<String, Layer> getLoadLayers() {
        return mLoadMapLayers;
    }

    public synchronized void addLocalTiledLayer(String path) {
        checkMapObject();
        File file = new File(path);
        if (!file.exists()) {
            log("图层文件不存在");
            return;
        }
        map.setVisibility(View.VISIBLE);
        if (mLoadMapLayers.containsKey(path)) {
            return;
        }
        if(path.toLowerCase().endsWith("mbtiles")){
            MBTilesHelper mbTilesHelper=new MBTilesHelper();
            LatLngInfo latLngInfo = mbTilesHelper.readMBTileKml(path);
            MBTilesLayer mbTilesLayer = new MBTilesLayer(file.getAbsolutePath(),latLngInfo);
          
            map.addLayer(mbTilesLayer);
            if(!(latLngInfo!=null && latLngInfo.longitude>0 && latLngInfo.latitude > 0)){
                latLngInfo = mbTilesLayer.latLngInfo;
            }

            Envelope envelope = map.getMaxExtent();
            if (envelope != null) {
                envelope.merge(mbTilesLayer.getFullExtent());
                map.setMaxExtent(envelope);
            }
            if(latLngInfo!=null && latLngInfo.longitude>0 && latLngInfo.latitude > 0) {
                LatLngInfo gcj02 = CoordinateUtils.gps84_To_Gcj02(latLngInfo.latitude,latLngInfo.longitude);
                LatLngInfo moveTo = CoordinateUtils.lonLatToMercator(gcj02.longitude, gcj02.latitude);
                map.setScale(18055.954822);
                map.centerAt(new Point(moveTo.longitude,moveTo.latitude), true);
            }
            mLoadMapLayers.put(path, mbTilesLayer);

        }else {
            ArcGISLocalTiledLayer local =
                    new ArcGISLocalTiledLayer("file://" + file.getAbsolutePath()
                            .replace("file://", ""));
            map.addLayer(local);
            Envelope envelope = map.getMaxExtent();
            if (envelope != null) {
                envelope.merge(local.getFullExtent());
                map.setMaxExtent(envelope);
            }
            mLoadMapLayers.put(path, local);
        }

        if (mPolygonLayer != null) {
            try {
                map.removeLayer(mPolygonLayer);
            } catch (Exception ex) {

            }

        }
        mPolygonLayer = new GraphicsLayer();
        map.addLayer(mPolygonLayer);

        if (mPolylineLayer != null) {
            try {
                map.removeLayer(mPolylineLayer);
            } catch (Exception ex) {

            }

        }
        mPolylineLayer = new GraphicsLayer();
        map.addLayer(mPolylineLayer);

        if (mPointLayer != null) {
            try {
                map.removeLayer(mPointLayer);
            } catch (Exception ex) {

            }

        }
        mPointLayer = new GraphicsLayer();
        map.addLayer(mPointLayer);

        if (mAnnoLayer != null) {
            try {
                map.removeLayer(mAnnoLayer);
            } catch (Exception ex) {

            }
        }
        mAnnoLayer = new GraphicsLayer();
        map.addLayer(mAnnoLayer);

        if (mPhotoLayer != null) {
            try {
                map.removeLayer(mPhotoLayer);
            } catch (Exception ex) {

            }
        }
        mPhotoLayer = new GraphicsLayer();
        map.addLayer(mPhotoLayer);

        if (mGpsTrackHistoryLayer != null) {
            try {
                map.removeLayer(mGpsTrackHistoryLayer);
            } catch (Exception ex) {

            }
        }
        mGpsTrackHistoryLayer = new GraphicsLayer();
        map.addLayer(mGpsTrackHistoryLayer);

        if (mGpsTrackLayer != null) {
            try {
                map.removeLayer(mGpsTrackLayer);
            } catch (Exception ex) {

            }
        }
        mGpsTrackLayer = new GraphicsLayer();
        map.addLayer(mGpsTrackLayer);

        if (mGpsLayer != null) {
            try {
                map.removeLayer(mGpsLayer);
            } catch (Exception ex) {

            }
        }
        mGpsLayer = new GraphicsLayer();
        map.addLayer(mGpsLayer);

        refreshPhotos();
    }

    public synchronized void removeLocalTiledLayer(String path) {
        checkMapObject();
        File file = new File(path);
        if (!file.exists()) {
            log("图层文件不存在");
            return;
        }
        if (!mLoadMapLayers.containsKey(path)) {
            return;
        }
        try {
            map.removeLayer(mLoadMapLayers.get(path));
        } catch (Exception ex) {

        }
        mLoadMapLayers.remove(path);
    }

    public boolean isGoogleMapOnShow() {
        return mGoogleMapLayer != null;
    }

    public synchronized void addGoogleMap() {

        if (mGoogleMapLayer != null) {
            return;
        }
        map.setVisibility(View.VISIBLE);
        mGoogleMapLayer = new GoogleMapLayer(CommonConstants.GoogleMapType.ImageMap,IOUtils.getRootStoragePath(map.getContext()) + AppConstant.DIR_GOOGLE_CACHE);
        map.addLayer(mGoogleMapLayer,0);
        mGoogleRoadMapLayer = new GoogleMapLayer(CommonConstants.GoogleMapType.RoadMap,IOUtils.getRootStoragePath(map.getContext()) + AppConstant.DIR_GOOGLE_CACHE);
        map.addLayer(mGoogleRoadMapLayer,1);
      
      
        if (mPolygonLayer == null) {
            mPolygonLayer = new GraphicsLayer();
            map.addLayer(mPolygonLayer);
        }
        if (mPolylineLayer == null) {
            mPolylineLayer = new GraphicsLayer();
            map.addLayer(mPolylineLayer);
        }

        if (mPointLayer == null) {
            mPointLayer = new GraphicsLayer();
            map.addLayer(mPointLayer);
        }

        if (mPhotoLayer == null) {
            mPhotoLayer = new GraphicsLayer();
            map.addLayer(mPhotoLayer);
        }

        if (mGpsLayer == null) {
            mGpsLayer = new GraphicsLayer();
            map.addLayer(mGpsLayer);
        }
        if (mGpsTrackLayer == null) {
            mGpsTrackLayer = new GraphicsLayer();
            map.addLayer(mGpsTrackLayer);
        }

        if (mGpsTrackHistoryLayer == null) {
            mGpsTrackHistoryLayer = new GraphicsLayer();
            map.addLayer(mGpsTrackHistoryLayer);
        }

        if (mAnnoLayer == null) {
            mAnnoLayer = new GraphicsLayer();
            map.addLayer(mAnnoLayer);
        }

        refreshPhotos();
    }

    public synchronized void removeGoogleMap() {
        if (mGoogleMapLayer == null) {
            return;
        }
        try {
            map.removeLayer(mGoogleMapLayer);
            map.removeLayer(mGoogleRoadMapLayer);
        } catch (Exception ex) {

        }
        mGoogleMapLayer = null;
        mGoogleRoadMapLayer = null;
    }

    public boolean isLayerLoaded(String path) {
        return mLoadMapLayers.containsKey(path);
    }

    public LatLngInfo getCenter() {
        checkMapObject();
        Point p = map.getCenter();
        if (p == null) {
            return null;
        }
        if (isGoogleMapOnShow()) {
            LatLngInfo latLngInfo = CoordinateUtils.mercatorToLonLat(p.getX(),p.getY());
            return latLngInfo;
        } else {
            return new LatLngInfo(p.getY(), p.getX());
        }
    }

    public void setFullExtent() {
        checkMapObject();
        map.setExtent(map.getMaxExtent());
    }

    public void moveCamera(Point point) {
        checkMapObject();
        map.zoomTo(point, 10);
    }

    public void moveCamera(Point point, double scale) {
        checkMapObject();
        map.setScale(scale);
        map.centerAt(point, true);
    }

    private double lastX, lastY;

    public void changeGpsSymbolOnMap(Point newLoc, float header) {
        if (newLoc == null) {
            return;
        }
        if (mGpsLayer.getNumberOfGraphics() < 1) {
            PictureMarkerSymbol symbol = new PictureMarkerSymbol(map.getContext().getResources().getDrawable(R.drawable.gpspoint));
            Graphic graphic = new Graphic(newLoc, symbol);
            mGpsLayer.addGraphic(graphic);
        }
        int id = mGpsLayer.getGraphicIDs()[0];
        Graphic g = mGpsLayer.getGraphic(id);
        double x = newLoc.getX();
        double y = newLoc.getY();
        if (lastX == x && lastY == y) {
            return;
        }
        lastX = x;
        lastY = y;

        mGpsLayer.updateGraphic(id, new Point(x, y));
        PictureMarkerSymbol gps = (PictureMarkerSymbol) g.getSymbol();
        gps.setAngle(header);
        mGpsLayer.updateGraphic(id, gps);
    }

    private int mRouteGraphicId = -1;

    public void changeGpsTrackOnMap(Point p) {
        if (p == null && mGpsTrackLayer != null) {
            mGpsTrackLayer.removeAll();
            return;
        }
      
        if (mGpsTrackLayer.getNumberOfGraphics() < 1) {
            Polyline line = new Polyline();
            line.startPath(p);
            line.lineTo(p);
            mRouteGraphicId = mGpsTrackLayer.addGraphic(new Graphic(line, new SimpleLineSymbol(Color.BLUE, 3.0f)));

            PictureMarkerSymbol symbol = new PictureMarkerSymbol(map.getContext().getResources().getDrawable(R.drawable.start_route));
            Graphic graphic = new Graphic(p, symbol);
            mGpsTrackLayer.addGraphic(graphic);
            return;
        }
        Graphic g = mGpsTrackLayer.getGraphic(mRouteGraphicId);
        if (g == null) {
            return;
        }
        Polyline line = ((Polyline) g.getGeometry());
        line.lineTo(p);
        mGpsTrackLayer.updateGraphic(mRouteGraphicId, line);
    }

    public void setExtent(Envelope extent) {
        if (map == null) {
            return;
        }
        map.setExtent(extent);
    }

    public Envelope getExtent() {
        if (map == null) {
            return null;
        }
        Envelope envelope = new Envelope();
        Polygon env = map.getExtent();
        if (env == null) {
            return null;
        }
        envelope.setCoords(env.getPoint(0).getX(), env.getPoint(0).getY(),
                env.getPoint(2).getX(), env.getPoint(2).getY());

        if (isGoogleMapOnShow()) {
            Point pEnv = envelope.getLowerLeft();
            LatLngInfo p = CoordinateUtils.mercatorToLonLat(pEnv.getX(),pEnv.getY());
            envelope.setXMin(p.longitude);
            envelope.setYMin(p.latitude);
            Point pUR = envelope.getUpperRight();
            LatLngInfo p1 = CoordinateUtils.mercatorToLonLat(pUR.getX(),pUR.getY());
            envelope.setXMax(p1.longitude);
            envelope.setYMax(p1.latitude);
        }
        return envelope;
    }

    public Bitmap getMapBitmap() {
        map.clearFocus();
        map.setPressed(false);

        boolean willNotCache = map.willNotCacheDrawing();
        map.setWillNotCacheDrawing(false);
        int color = map.getDrawingCacheBackgroundColor();
        map.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            map.destroyDrawingCache();
        }
        map.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while (cacheBitmap == null) {
            cacheBitmap = map.getDrawingMapCache(0, 0, map.getWidth(),
                    map.getHeight());
        }
        map.destroyDrawingCache();
        map.setWillNotCacheDrawing(willNotCache);
        map.setDrawingCacheBackgroundColor(color);
        System.gc();

        return cacheBitmap;
    }
}
