package com.ew.autofly.fragments.dem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.widgets.controls.ImageMarkerSymbol;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.utils.arcgis.ArcgisMapUtil;
import com.ew.autofly.utils.coordinate.CoordConvertManager;
import com.ew.autofly.utils.DemReaderUtils;
import com.ew.autofly.utils.GpsUtils2;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.MapUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ew.autofly.xflyer.utils.ArcgisPointUtils.getCenterPoint;



public class GetDemDataActivity extends AppCompatActivity implements View.OnClickListener {

    public MapUtils map = null;
    private ImageView download;
    private GisBottomNewFragment bottomFragment;
    private long mLastLoadLayer = 0L;
    private DemReaderUtils demReaderUtils = null;
    private boolean isAllowInitGoogleMapExtent = true;

  
    private GraphicsLayer mDrawingPointLayer = null;
  
    private GraphicsLayer mDrawingModifyLayer = null;
    private SimpleFillSymbol mPolygonSymbol = null;
    private static final int BING_INIT_SCALE = 12000;

    private ImageMarkerSymbol mPointSymbol = null;
    private ImageMarkerSymbol mCenterPointSymbol = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dem_data);
        ArcGISRuntime.setClientId(AppConstant.ARCGIS_CLIENT_ID);
        download = (ImageView) findViewById(R.id.btn_download_dem);
        map = MapUtils.getInstance((RelativeLayout) findViewById(R.id.map_content));
        map.addMapOnTouchListener(onMapTouchListener);

        initView();
        initMap();
        initFigure();
    }

    private void initMap() {
        map.setOnLayerLoadedListener(new MapUtils.OnLayerLoaded() {
            @Override
            public void onLoaded(Object object) {
                long now = System.currentTimeMillis();
                if (now - mLastLoadLayer < 2000) {
                    return;
                }
                mLastLoadLayer = now;
                demReaderUtils = DemReaderUtils.getInstance(GetDemDataActivity.this);
                refreshDemData();
              
                refreshCoord();
                if (isAllowInitGoogleMapExtent && map.isGoogleMapOnShow()) {
                    isAllowInitGoogleMapExtent = false;
                    Intent intent = getIntent();
                    if (intent != null && intent.getStringExtra("lat") != null && intent.getStringExtra("lng") != null) {//查看数据
                        int lat = Integer.valueOf(intent.getStringExtra("lat"));
                        int lng = Integer.valueOf(intent.getStringExtra("lng"));
                        bottomFragment.setValues(CoordConvertManager.convertToSexagesimal(lng),
                                CoordConvertManager.convertToSexagesimal(lat),
                                "0",
                                2000000 + "");
                        LatLngInfo moveTo = CoordinateUtils.lonLatToMercator(lng, lat);
                        showPolygon(lat, lng);
                        map.moveCamera(new Point(moveTo.longitude, moveTo.latitude),
                                2000000);
                        download.setVisibility(View.GONE);
                    } else {//下载数据初始化当前位置的坐标
                        download.setVisibility(View.VISIBLE);
                        GpsUtils2.getCurrentLocation(new GpsUtils2.LocationListner() {
                            @Override
                            public void result(AMapLocation location) {
                                LatLngInfo latLngInfo;
                                LatLngInfo moveTo;
                                if (location.getErrorCode() == 0) {
                                    LatLngInfo gpsLocation = new LatLngInfo(location.getLatitude(), location.getLongitude());
                                    moveTo = CoordinateUtils.lonLatToMercator(gpsLocation.longitude, gpsLocation.latitude);
                                    bottomFragment.setValues(CoordConvertManager.convertToSexagesimal(AppConstant.BING_INIT_LON),
                                            CoordConvertManager.convertToSexagesimal(AppConstant.BING_INIT_LAT),
                                            "0",
                                            BING_INIT_SCALE + "");
                                } else {
                                    ToastUtil.show(GetDemDataActivity.this, "无法定位到当前位置");
                                    AMapLocation lastKnowLocaation = GpsUtils2.getLastKnowLocaation();
                                    latLngInfo = new LatLngInfo(lastKnowLocaation.getLatitude(), lastKnowLocaation.getLongitude());
                                    moveTo = CoordinateUtils.lonLatToMercator(latLngInfo.longitude, latLngInfo.latitude);
                                    bottomFragment.setValues(CoordConvertManager.convertToSexagesimal(AppConstant.BING_INIT_LON),
                                            CoordConvertManager.convertToSexagesimal(AppConstant.BING_INIT_LAT),
                                            "0",
                                            BING_INIT_SCALE + "");
                                }
                                map.moveCamera(new Point(moveTo.longitude, moveTo.latitude), BING_INIT_SCALE);
                            }
                        });
                    }
                }
            }
        });
    }

    private void initFigure() {
        if (map != null) {
            mDrawingModifyLayer = new GraphicsLayer();
            map.getMap().addLayer(mDrawingModifyLayer);
            mDrawingPointLayer = new GraphicsLayer();
            map.getMap().addLayer(mDrawingPointLayer);

            mPointSymbol = ArcgisMapUtil.getImageMarkerSymbol(this, R.drawable.vetex,
                    getResources().getDimension(R.dimen.map_app_vetex_size), getResources().getDimension(R.dimen.map_app_vetex_size));
            mCenterPointSymbol = ArcgisMapUtil.getImageMarkerSymbol(this, R.drawable.midvetex,
                    getResources().getDimension(R.dimen.map_app_vetex_size), getResources().getDimension(R.dimen.map_app_vetex_size));
            mPolygonSymbol = new SimpleFillSymbol(Color.parseColor("#5000ff00"));
            mPolygonSymbol.setOutline(new SimpleLineSymbol(Color.YELLOW, 2.0f));
        }
    }

    private void initView() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.imgbtn_location).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);

        download = (ImageView) findViewById(R.id.btn_download_dem);

        if (bottomFragment == null) {
            attachStateBar();
        }
    }

    /**
     * 画出中心范围图形
     *
     * @param points
     */
    private void drawPolygon(ArrayList<Point> points) {
        mDrawingPointLayer.removeAll();
        mDrawingModifyLayer.removeAll();
        Polygon polygon = new Polygon();
        for (int i = 0; i < points.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", i);
            map.put("type", 1);
            mDrawingPointLayer.addGraphic(new Graphic(points.get(i), mPointSymbol, map));
            if (i == 0) {
                polygon.startPath(points.get(i));
            } else {
                polygon.lineTo(points.get(i));
                Point cp = getCenterPoint(points.get(i - 1), points.get(i));
                Map<String, Object> mapC = new HashMap<>();
                mapC.put("index", i);
                mapC.put("type", 2);
                mDrawingPointLayer.addGraphic(new Graphic(cp, mCenterPointSymbol, mapC));
            }
        }
        mDrawingModifyLayer.addGraphic(new Graphic(polygon, mPolygonSymbol));
    }

    private long mLastClickTime = 0L;
    private MapUtils.OnMapTouchListener onMapTouchListener = new MapUtils.OnMapTouchListener() {
        @Override
        public void onExtentChanged() {
            refreshCoord();
        }

        @Override
        public void onClick(Point point, android.graphics.Point screenPoint) {//第一个参数墨卡托坐标，这二个屏幕坐标
            long time = System.currentTimeMillis();
            if (time - mLastClickTime < 500) {
                return;
            }
            mLastClickTime = time;
          
            map.moveCamera(point, 2000000);
            LatLngInfo p;
            if (map.isGoogleMapOnShow()) {
                p = CoordinateUtils.mercatorToLonLat(point.getX(), point.getY());
            } else {
                p = new LatLngInfo(point.getY(), point.getX());
            }
            LatLngInfo gps = CoordinateUtils.gcj_To_Gps84(p.latitude, p.longitude);
          
            showPolygon(gps.latitude, gps.longitude);
            refreshCoord();
        }

        @Override
        public void onLongClick(android.graphics.Point point) {
        }

        @Override
        public void onTouch(MotionEvent event) {



        }
    };

    private boolean isLoaded(String urlname) {
        return getDemFiles().contains(urlname.substring(urlname.indexOf("_") + 1, urlname.lastIndexOf("_")));
    }

    /**
     * @param lat gps lat
     * @param lng gps lng
     */
    public void showPolygon(double lat, double lng) {
        int lat1 = (int) Math.floor(Math.abs(lat));
        int lat2 = lat1 + 1;
        int lgt1 = (int) Math.floor(Math.abs(lng));
        int lgt2 = lgt1 + 1;
        final String urlname = generateFilename(lat, lng);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog alertDialog = new AlertDialog.Builder(GetDemDataActivity.this, R.style.Dialog).
                        setTitle("提示").setMessage("是否立刻下载？").
                        setPositiveButton("立刻下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!isLoaded(urlname)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("url", urlname);
                                    setResult(1001, intent);
                                    finish();
                                } else {
                                    ToastUtil.show(GetDemDataActivity.this, "该数据已经下载过了哦！");
                                }
                            }
                        }).setNegativeButton("稍后", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                alertDialog.show();
            }
        });
      
      
        Point ptStart = getPointfromgps(lat1, lgt1);
        Point pt2 = getPointfromgps(lat1, lgt2);
        Point pt3 = getPointfromgps(lat2, lgt2);
        Point pt4 = getPointfromgps(lat2, lgt1);
        ArrayList<Point> points = new ArrayList<>();
        points.add(ptStart);
        points.add(pt2);
        points.add(pt3);
        points.add(pt4);
        drawPolygon(points);
    }

  
    private Point getPointfromgps(double lat, double lgt) {
        LatLngInfo info1 = CoordinateUtils.gps84_To_Gcj02(lat, lgt);
        LatLngInfo lgps = CoordinateUtils.lonLatToMercator(info1.longitude, info1.latitude);
        return new Point(lgps.longitude, lgps.latitude);
    }

    
    private String generateFilename(double lat, double lng) {
        StringBuilder sb = new StringBuilder();
        sb.append("astgtm2_");
        if (lat >= 0) {//表示为北纬
            sb.append("n").append((int) Math.floor(Math.abs(lat)));
        } else {
            sb.append("s").append((int) Math.floor(Math.abs(lat)));
        }

        if (lng >= 0) {
            sb.append("e").append((int) Math.floor(Math.abs(lng)));
        } else {
            sb.append("w").append((int) Math.floor(Math.abs(lng)));
        }
        sb.append("_dem.TRA");
        return sb.toString();
    }

    
    private List<String> getDemFiles() {
        List<String> list = new ArrayList<>();
        String dirPath = IOUtils.getRootStoragePath(this) + File.separator + AppConstant.DIR_MAP;
        File file = new File(dirPath);
        File[] files = file.listFiles();
        if (files.length > 0) {
            for (File subfile : files) {
                if (subfile.getName().endsWith(".TRA") || subfile.getName().endsWith(".TEMP")) {
                    String filename = subfile.getName();
                    list.add(filename.substring(filename.indexOf("_") + 1, filename.lastIndexOf("_")));
                }
            }
        }
        return list;
    }

    
    private void attachStateBar() {
        bottomFragment = new GisBottomNewFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft = ft.add(bottomFragment, "gis_bottom");
        ft.replace(R.id.statebar, bottomFragment);
        ft.commit();
    }

    ;

    
    public void refreshCoord() {
        if (bottomFragment == null) {
            return;
        }
        LatLngInfo p = map.getCenter();
        if (p == null) {
            return;
        }

        LatLngInfo pCenter = CoordinateUtils.gcj_To_Gps84(p.latitude, p.longitude);
        if (demReaderUtils != null)
            bottomFragment.setValues(CoordConvertManager.convertToSexagesimal(pCenter.longitude),
                    CoordConvertManager.convertToSexagesimal(pCenter.latitude),
                    demReaderUtils.getZValue(pCenter) + "",
                    (int) map.getMap().getScale() + "");
    }

    public void refreshDemData() {
        demReaderUtils.addDemFilePath(IOUtils.getRootStoragePath(GetDemDataActivity.this)
                + "Maps/");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download_dem:
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.imgbtn_location:
                centerAtMobileLocation();
                break;
        }
    }

    private void centerAtMobileLocation() {
        GpsUtils2.getCurrentLocation(new GpsUtils2.LocationListner() {
            @Override
            public void result(AMapLocation location) {
                LatLngInfo latLngInfo;
                if (location.getErrorCode() == 0) {
                    latLngInfo = new LatLngInfo(location.getLatitude(), location.getLongitude());
                } else {
                    ToastUtil.show(GetDemDataActivity.this, "无法定位当前位置,请稍后再试");
                    AMapLocation lastKnowLocaation = GpsUtils2.getLastKnowLocaation();
                    latLngInfo = new LatLngInfo(lastKnowLocaation.getLatitude(), lastKnowLocaation.getLongitude());
                }
                LatLngInfo mer = CoordinateUtils.lonLatToMercator(latLngInfo.longitude, latLngInfo.latitude);
                Point gps = new Point(mer.longitude, mer.latitude);
                map.moveCamera(gps, 2000000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.removeMapOnClickListener(onMapTouchListener);
        if (map != null) {
            map.onDestroy();
            map = null;
        }
    }
}
