package com.ew.autofly.struct.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Multipoint;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedEvent;
import com.esri.arcgisruntime.mapping.view.LayerViewStateChangedListener;
import com.esri.arcgisruntime.mapping.view.LayerViewStatus;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.db.helper.MissionHelper;
import com.ew.autofly.dialog.EditMissionNameDialogFragment;
import com.ew.autofly.dialog.MobileNavigationDialogFragment;
import com.ew.autofly.dialog.common.FlyCheckingDialogFragment;
import com.ew.autofly.dialog.common.GoHomeDialog;
import com.ew.autofly.dialog.common.ImportExcelKmlDlgFragment;
import com.ew.autofly.dialog.tool.FastLocationDialog;
import com.ew.autofly.dialog.tool.SimulatorDialog;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.LocationMercator;
import com.ew.autofly.entity.PointWithName;
import com.ew.autofly.entity.WayPointInfo;
import com.ew.autofly.event.SimulatorEvent;
import com.ew.autofly.event.ui.topbar.MonitorInfoShowEvent;
import com.ew.autofly.event.ui.topbar.MonitorInfoUpdateEvent;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.interfaces.KMLFragmentListener;
import com.ew.autofly.interfaces.OnGetNoFlyZoneDataListener;
import com.ew.autofly.interfaces.OnSettingDialogClickListener;
import com.ew.autofly.internal.common.CheckError;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.internal.key.callback.KeyListener;
import com.ew.autofly.model.AccessoryManager;
import com.ew.autofly.model.BatteryManager;
import com.ew.autofly.model.mission.CheckConditionManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.cache.FlySettingKey;
import com.ew.autofly.module.weather.WeatherInfoDlgFragment;
import com.ew.autofly.struct.presenter.BaseMapPresenterImpl;
import com.ew.autofly.struct.view.IBaseMapView;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.DemReaderUtils;
import com.ew.autofly.utils.GpsUtils2;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.LocationUtil;
import com.ew.autofly.utils.LogUtilsOld;
import com.ew.autofly.utils.MissionUtil;
import com.ew.autofly.utils.NoFlyZoneDataUtils;
import com.ew.autofly.utils.arcgis.ArcGisUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.widgets.dji.DjiFPVOverlayWidget;
import com.ew.autofly.widgets.dji.DjiFPVWidget;
import com.ew.autofly.widgets.switchview.OnSwitchClickListener;
import com.ew.autofly.widgets.switchview.SwitchButton;
import com.ew.autofly.xflyer.utils.CommonConstants;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.flycloud.autofly.base.framework.aop.annotation.SingleClick;
import com.flycloud.autofly.base.util.ToastUtil;
import com.flycloud.autofly.map.MapLayerType;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;
import com.flycloud.autofly.map.layer.LayerInfoFactory;
import com.flycloud.autofly.map.layer.MapLayerInfo;
import com.flycloud.autofly.map.layer.TiledMapLayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import dji.common.bus.UXSDKEventBus;
import dji.common.flightcontroller.flyzone.SubFlyZoneInformation;
import dji.common.model.LocationCoordinate2D;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.ux.internal.Events;
import dji.ux.panel.SpeakerPanel;
import dji.ux.panel.SpotlightPanel;
import dji.ux.widget.FPVWidget;
import dji.ux.widget.RadarWidget;
import dji.ux.widget.dashboard.CompassWidget;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.ew.autofly.constant.AppConstant.DIR_GAODE_CACHE;
import static com.ew.autofly.constant.AppConstant.DIR_GOOGLE_CACHE;
import static com.ew.autofly.constant.AppConstant.DIR_OPENCYCLE_CACHE;
import static com.ew.autofly.dialog.common.FlyCheckingDialogFragment.ARG_PARAM_FLYCHECK_STATUS;
import static com.ew.autofly.dialog.tool.SimulatorDialog.PARAM_SIMULATOR_LATITUDE;
import static com.ew.autofly.dialog.tool.SimulatorDialog.PARAM_SIMULATOR_LONGITUDE;
import static com.ew.autofly.utils.arcgis.ArcGisUtil.locationToMercatorPoint;
import static com.ew.autofly.utils.arcgis.ArcGisUtil.locationToMercatorPointCollection;



public abstract class BaseMapFragment<V extends IBaseMapView, P extends BaseMapPresenterImpl<V>>
        extends BaseFlightFragment<V, P> implements IBaseMapView, View.OnClickListener, View.OnLongClickListener,
        OnGetNoFlyZoneDataListener, KMLFragmentListener,
        OnSettingDialogClickListener {


    public final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;
    protected int ratioWidth = 16, ratioHeight = 9;

    public AppConstant.OperationMode currentMode = AppConstant.OperationMode.LinePatrolVideo;

    private boolean isMapLoaded = false;
    private boolean isMobileLocationGet = false;
    private boolean isRadarDisplayEnable = false;

    protected MapView mMapView;
    protected SharedConfig config;
    protected LogUtilsOld log = null;
    protected DataBaseUtils mDB = null;

    public ImageButton mBtnMapSwitch,
            ibGoHome, ibKML, ibLocation, ibLoadTask, ibSaveTask,
            ibSetting, ibLoadTaskClear, ibMore;
    protected SwitchButton ibDrawCircle;
    public Button ibFlight, ibPause, ibResume;
    protected LinearLayout llKml, llDrawCircle;

    protected LinearLayout llLeftButton;
    protected LinearLayout llRightButton;

    protected ViewGroup ly_base_geo_region, ly_base_map_and_fpv, ly_fpv, ly_fpv_camera_setting,
            ly_camera_min_window_operation_layer;
    protected RelativeLayout ly_base_operation;
    protected RelativeLayout ly_additional_operation;

    protected DjiFPVWidget mFPVWidget;

    protected DjiFPVOverlayWidget mFPVOverlayWidget;

    protected ImageView ivShowCompass;

    protected ImageView ivWeatherLoc, ivNarrowFPV;
    protected LinearLayout llAssistantFunction;
    private ImageButton tvWeatherDetail;
    private ImageButton tvNavigation;
    private ImageButton tvNoFlyZone;
    private CompassWidget mCompassWidget;
    private RadarWidget mRadarWidget;
    private SpotlightPanel spotlightPanel;
    private SpeakerPanel speakerPanel;
    public ImageButton imgSpotlight;
    public ImageButton imgSpeaker;
    public ImageButton imgBeacon;
    private TextView tvAltitude;

    private ArcGISMap mArcGISMap = new ArcGISMap(getSpatialReference());
    private MapServiceProvider mMapServiceProvider = MapServiceProvider.GOOGLE_MAP;
    private MapRegion mMapRegion = MapRegion.CHINA;
    private MapLayerType mMapLayerType = MapLayerType.VECTOR;

    protected TextView tvBottomInfo;

  
    protected GraphicsOverlay mFlagLayer;
  
    protected ListenableFuture<PictureMarkerSymbol> mStartFlaySymbol;
  
    protected ListenableFuture<PictureMarkerSymbol> mEndFlaySymbol;

  
    protected GraphicsOverlay flightPointTextLayer;
  
    protected ArrayList<String> mSelectedKmlFileIds = new ArrayList<>();

  
    protected GraphicsOverlay mMissionPointLayer;
    protected GraphicsOverlay mMissionLineLayer;
    protected GraphicsOverlay mMissionFlagLayer;
    protected GraphicsOverlay mMissionAltitudeLayer;

  
    private ListenableFuture<PictureMarkerSymbol> mMissionPointSymbol = null;
    private SimpleLineSymbol mMissionLineSymbol = null;

    
    private GraphicsOverlay mFlightLayer;

    private ListenableFuture<PictureMarkerSymbol> mDroneSymbol = null;
    private ListenableFuture<PictureMarkerSymbol> mHomePointSymbol = null;
    private SimpleLineSymbol mHomeAndDroneLineSymbol = null;
    private Graphic mDroneGraphic = null;
    private Graphic mHomeGraphic = null;
    private Graphic mHomeAndDroneLineGraphic = null;

  
    private GraphicsOverlay mLocationLayer;
    private ListenableFuture<PictureMarkerSymbol> mLocationSymbol = null;
    private Graphic mLocationGraphic = null;

  
    protected GraphicsOverlay mReviewMissionLayer = null;

  
    protected GraphicsOverlay mNoFlyZoneLayer = null;

  
    protected GraphicsOverlay mKmlPolygonLayer, mKmlPolylineLayer, mKmlPointLayer, mKmlAnnoLayer, mKmlDotLayer;

  
    protected ArrayList<String> mSelectedMissionIdList = new ArrayList<>();

    private boolean isFPVMax = false;

    public boolean isFPVMax() {
        return isFPVMax;
    }

    public double BottomInfoMeters;

    private boolean isShowWeather = false;

  
    protected List<LocationMercator> mWayPointMercatorList = new ArrayList<>();

  
    protected List<LocationCoordinate> mWayPointWgs84List = new ArrayList<>();

    protected List<WayPointInfo> mWayPointTaskList = new ArrayList<>();


  
    protected DemReaderUtils demReaderUtils = null;


    private GoHomeDialog mGoHomeDialog;

    public TextView mTvPsdkNotify;

    private LocationCoordinate mMobileLocation;

    protected CheckConditionManager mCheckConditionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        log = LogUtilsOld.getInstance(getBaseContext()).setTag("BaseMapFragment");
        config = new SharedConfig(EWApplication.getInstance());
        try {
            mDB = DataBaseUtils.getInstance(EWApplication.getInstance());
        } catch (Exception e) {
            log.e(e.getMessage());
        }

        mCheckConditionManager = new CheckConditionManager(getBaseContext());

        initData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onProductConnect(BaseProduct baseProduct) {
        super.onProductConnect(baseProduct);
    }

    @Override
    public void onProductComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent newComponent, boolean isConnected) {
        super.onProductComponentChange(componentKey, newComponent, isConnected);
    }

    @Override
    public void onProductDisconnect() {
        super.onProductDisconnect();
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                dismissAccessoryAggregationView();
            }
        });
    }

    protected abstract void initData();

    protected abstract void initView(View view);

    @Override
    protected int setRootViewId() {
        return R.layout.frag_map_base_main;
    }

    @Override
    protected void initRootView(View view) {

        initViewLayoutParams();

        ly_base_geo_region = view.findViewById(R.id.ly_base_geo_region);
        ly_base_map_and_fpv = view.findViewById(R.id.ly_base_map_and_fpv);
        ly_fpv = view.findViewById(R.id.ly_fpv);
        ly_fpv_camera_setting = view.findViewById(R.id.ly_camera_and_setting);
        ly_camera_min_window_operation_layer = view.findViewById(R.id.ly_camera_min_window_operation_layer);
        ly_camera_min_window_operation_layer.setLayoutParams(getFpvMinLayoutParams());

        ly_base_operation = (RelativeLayout) view.findViewById(R.id.ly_base_operation);
        ly_additional_operation = (RelativeLayout) view.findViewById(R.id.ly_additional_operation);

        mFPVWidget = view.findViewById(R.id.fpv_live);
      


        mFPVOverlayWidget = (DjiFPVOverlayWidget) view.findViewById(R.id.fpv_overlay);

        mMapView = (MapView) view.findViewById(R.id.arcgis_mapview);

        ivShowCompass = (ImageView) view.findViewById(R.id.iv_show_compass);
        mCompassWidget = view.findViewById(R.id.compass);
        mCompassWidget.setOnClickListener(this);
        mRadarWidget = view.findViewById(R.id.radar);

        tvBottomInfo = (TextView) view.findViewById(R.id.tv_bottom_info);
        llLeftButton = (LinearLayout) view.findViewById(R.id.llLeftButton);
        llRightButton = (LinearLayout) view.findViewById(R.id.ll_right_button);

        llKml = (LinearLayout) view.findViewById(R.id.ll_kml_tower);
        llDrawCircle = (LinearLayout) view.findViewById(R.id.ll_draw_polygon);

        ibKML = (ImageButton) view.findViewById(R.id.imgBtnKML);
        ibLocation = (ImageButton) view.findViewById(R.id.imgBtnLocation);
        mBtnMapSwitch = (ImageButton) view.findViewById(R.id.imgMapMode);
        ibFlight = (Button) view.findViewById(R.id.imgBtnFlight);
        ibPause = (Button) view.findViewById(R.id.imgBtnPause);
        ibResume = (Button) view.findViewById(R.id.imgBtnResume);
        ibGoHome = (ImageButton) view.findViewById(R.id.imgBtnGoHome);
        ibLoadTask = (ImageButton) view.findViewById(R.id.imgBtnLoadTask);
        ibSaveTask = (ImageButton) view.findViewById(R.id.imgBtnSaveTask);
        ibSaveTask.setEnabled(false);
        ibSetting = (ImageButton) view.findViewById(R.id.imgBtnSettings);
        ibDrawCircle = (SwitchButton) view.findViewById(R.id.imgBtnCircle);

        ibDrawCircle.setOnSwitchListener(new OnSwitchClickListener() {
            @Override
            public void onSwitchState(boolean isStateOn) {
                if (mPresenter != null && mPresenter.checkTaskFree()) {
                    onSwitchDrawCircle(isStateOn);
                }
            }
        });

        ibLoadTaskClear = (ImageButton) view.findViewById(R.id.imgBtnLoadTaskClear);
        ibMore = (ImageButton) view.findViewById(R.id.ib_more);
        llAssistantFunction = (LinearLayout) view.findViewById(R.id.ll_assistant_function);
        tvWeatherDetail = (ImageButton) view.findViewById(R.id.tv_weather_detail);
        tvNavigation = (ImageButton) view.findViewById(R.id.tv_navigation);
        tvNoFlyZone = (ImageButton) view.findViewById(R.id.tv_noflyzone);

        speakerPanel = view.findViewById(R.id.speaker_panel);
        spotlightPanel = view.findViewById(R.id.spotlight_panel);
        imgSpeaker = view.findViewById(R.id.tv_speaker);
        imgSpotlight = view.findViewById(R.id.tv_spotlight);
        imgBeacon = view.findViewById(R.id.tv_beacon);
        imgSpeaker.setOnClickListener(this);
        imgSpotlight.setOnClickListener(this);
        imgBeacon.setOnClickListener(this);


        tvAltitude = (TextView) view.findViewById(R.id.tv_altitude);
        ivWeatherLoc = (ImageView) view.findViewById(R.id.iv_weather_loc);
        ivNarrowFPV = (ImageView) view.findViewById(R.id.iv_narrow_fpv);

        ibMore.setOnClickListener(this);

        ibFlight.setOnClickListener(this);
        mBtnMapSwitch.setOnClickListener(this);
        ibSaveTask.setOnClickListener(this);
        tvWeatherDetail.setOnClickListener(this);
        tvNavigation.setOnClickListener(this);
        tvNoFlyZone.setOnClickListener(this);
        ibLoadTask.setOnClickListener(this);
        ibLoadTaskClear.setOnClickListener(this);

        ibSetting.setOnClickListener(this);
        ibKML.setOnClickListener(this);

        ibLocation.setOnClickListener(this);
        ibLocation.setOnLongClickListener(this);
        ibPause.setOnClickListener(this);
        ibResume.setOnClickListener(this);
        ibGoHome.setOnClickListener(this);

        ivShowCompass.setOnClickListener(this);

        ivNarrowFPV.setOnClickListener(this);

        mMapView.setOnTouchListener(initMapTouchListener(mMapView));
        initMap();
        initMapGraphicsLayer();
        initSymbols();
        initGraphic();

      
        demReaderUtils = DemReaderUtils.getInstance(getContext());

        updateMonitorInfo(mPresenter.getMonitorFlightInfo());

        initFPVDefault();

        initPayload();

        initViewConfig();

        initView(view);

    }

    private RelativeLayout.LayoutParams getFpvMaxLayoutParams() {
        RelativeLayout.LayoutParams fpvMaxLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        return fpvMaxLayoutParams;
    }

    private RelativeLayout.LayoutParams getFpvMinLayoutParams() {
        RelativeLayout.LayoutParams fpvMinLayoutParams = new RelativeLayout.LayoutParams(
                AppConstant.ScreenWidth * 3 / ratioHeight,
                AppConstant.ScreenWidth * 3 / ratioWidth);

        fpvMinLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        return fpvMinLayoutParams;
    }

    private void initViewLayoutParams() {


    }

    protected void initMap() {

        Object mapServiceProvider = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.MAP_SERVICE_PROVIDER));
        mMapServiceProvider = MapServiceProvider.find((int) mapServiceProvider);

        Object mapRegion = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.MAP_REGION));
        mMapRegion = MapRegion.find((int) mapRegion);

        LocationCoordinateUtils.transformMapCoordinateType(mMapServiceProvider, mMapRegion);
        AppConstant.mapCoordinateType = LocationCoordinateUtils.MAP_COORDINATE_SYSTEM;
        mMapView.setAttributionTextVisible(false);

        changeMap();
        initLocation();
    }

    
    protected void initMapGraphicsLayer() {

        mLocationLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mLocationLayer);

        mKmlPolygonLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mKmlPolygonLayer);

        mKmlPolylineLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mKmlPolylineLayer);

        mKmlAnnoLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mKmlAnnoLayer);

        mKmlDotLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mKmlDotLayer);

        mKmlPointLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mKmlPointLayer);

        mMissionLineLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mMissionLineLayer);

        mMissionPointLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mMissionPointLayer);

        mMissionFlagLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mMissionFlagLayer);

        mMissionAltitudeLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mMissionAltitudeLayer);

        flightPointTextLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(flightPointTextLayer);

        mReviewMissionLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mReviewMissionLayer);

        mNoFlyZoneLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mNoFlyZoneLayer);

        mFlagLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mFlagLayer);

        mFlightLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mFlightLayer);
    }

    
    protected void initSymbols() {

        mDroneSymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.ic_map_aircraft));
        mHomePointSymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.ic_map_homepoint));
        mHomeAndDroneLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                getResources().getColor(R.color.red), 2);
        /*mMissionPointSymbol = new PictureMarkerSymbol(ResourceUtil.getUriStrFromDrawableRes(
                mContext, R.drawable.ic_mission_edit_waypoint_normal));*/
        mLocationSymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.icon_location_circle));
        mStartFlaySymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.ic_startpoint_flag));
        mEndFlaySymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.ic_endpoint_flag));

        mMissionLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                getResources().getColor(R.color.mission_graphic_line), 2);
        mMissionLineSymbol.setAntiAlias(true);
        mMissionPointSymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.ic_mission_edit_waypoint_normal));

    }

    protected void initGraphic() {

        mHomeAndDroneLineGraphic = new Graphic();
        mHomeAndDroneLineGraphic.setVisible(false);
        mHomeAndDroneLineGraphic.setSymbol(mHomeAndDroneLineSymbol);
        mHomeAndDroneLineGraphic.setZIndex(0);
        mFlightLayer.getGraphics().add(mHomeAndDroneLineGraphic);

        try {
            mHomeGraphic = new Graphic();
            mHomeGraphic.setVisible(false);
            mHomeGraphic.setSymbol(mHomePointSymbol.get());
            mHomeGraphic.setZIndex(1);
            mFlightLayer.getGraphics().add(mHomeGraphic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            mDroneGraphic = new Graphic();
            mDroneGraphic.setVisible(false);
            mDroneGraphic.setSymbol(mDroneSymbol.get());
            mDroneGraphic.setZIndex(2);
            mFlightLayer.getGraphics().add(mDroneGraphic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            mLocationGraphic = new Graphic();
            mLocationGraphic.setSymbol(mLocationSymbol.get());
            mLocationGraphic.setVisible(false);
            mLocationLayer.getGraphics().add(mLocationGraphic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initPayload() {
      
        RelativeLayout.LayoutParams tvNotifyParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvNotifyParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mTvPsdkNotify = new TextView(getActivity());
        mTvPsdkNotify.setLayoutParams(tvNotifyParams);
        mTvPsdkNotify.setBackgroundResource(R.drawable.tv_moitorinfo_left_radius);
        mTvPsdkNotify.setAlpha(0.7f);
        mTvPsdkNotify.setTextSize(13);
        mTvPsdkNotify.setVisibility(View.GONE);
        mTvPsdkNotify.setTextColor(Color.WHITE);
        ly_base_operation.addView(mTvPsdkNotify);
    }

    /**
     * 初始化触摸事
     *
     * @param mapView
     * @return
     */
    protected BaseMapTouchListener initMapTouchListener(MapView mapView) {
        return new BaseMapTouchListener(getBaseContext(), mapView);
    }

    private void switchMapMode() {
        if (mMapLayerType == MapLayerType.VECTOR) {
            mMapLayerType = MapLayerType.IMAGE;
            mBtnMapSwitch.setImageResource(R.drawable.selector_btn_satellite_map);
        } else {
            mMapLayerType = MapLayerType.VECTOR;
            mBtnMapSwitch.setImageResource(R.drawable.selector_btn_plane_map);
        }
        changeMap();
    }

    private void changeMap() {

        if (mMapServiceProvider == MapServiceProvider.OPENCYCLE_MAP) {
            mBtnMapSwitch.setVisibility(View.GONE);
        } else {
            mBtnMapSwitch.setVisibility(View.VISIBLE);
        }

        MapLayerInfo mapLayerInfo;

      
        String path = "";
        switch (mMapServiceProvider) {
            case GOOGLE_MAP:
                path = DIR_GOOGLE_CACHE;
              
                if (mMapRegion != MapRegion.CHINA) {
                    path += File.separator + "Region_" + mMapRegion.name();
                }
                break;
            case AMAP:
                path = DIR_GAODE_CACHE;
                break;
            case OPENCYCLE_MAP:
                path = DIR_OPENCYCLE_CACHE;
                break;
        }

        switch (mMapLayerType) {
            case VECTOR:
                path += File.separator + CommonConstants.GoogleMapType.VectorMap;
                break;
            case IMAGE:
                path += File.separator + CommonConstants.GoogleMapType.ImageMap;
                break;
        }

        mapLayerInfo = LayerInfoFactory
                .createLayerInfo(mMapServiceProvider, mMapRegion, mMapLayerType);
        TiledMapLayer layer = new TiledMapLayer(mapLayerInfo, AppConstant.ROOT_PATH
                + File.separator + path);
        layer.loadAsync();

        Basemap basemap = new Basemap(layer);
        mArcGISMap.setBasemap(basemap);
        mArcGISMap.setMaxScale(AppConstant.DEFAULT_MAP_MAX_SCALE);
        mMapView.setMap(mArcGISMap);


    }

    public void refreshLayerVisibility() {
        if (getActivity() != null) {
            if (mMapView.getMapScale() < 20000) {
                mKmlPointLayer.setVisible(true);
                mKmlAnnoLayer.setVisible(true);
                mKmlDotLayer.setVisible(false);
            } else if (mMapView.getMapScale() < 50000) {
                mKmlPointLayer.setVisible(true);
                mKmlAnnoLayer.setVisible(false);
                mKmlDotLayer.setVisible(false);
            } else {
                mKmlPointLayer.setVisible(false);
                mKmlAnnoLayer.setVisible(false);
                mKmlDotLayer.setVisible(true);
            }
        }
    }

    @Override
    public void onLoginDjiAccountCallback(boolean isSuccess) {
        super.onLoginDjiAccountCallback(isSuccess);
        if (mFPVWidget != null) {
            mFPVWidget.updateVideoFeed();
        }
    }

    @Override
    public void onPsdkDataReceive(final String s) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mTvPsdkNotify.setText(s);
            }
        });
    }

    @Override
    public void onPsdkConnected() {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mTvPsdkNotify.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showSpotlight(boolean isVisible) {
        imgSpotlight.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showSpeaker(boolean isVisible) {
        imgSpeaker.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showBeacon(boolean isVisible) {
        imgBeacon.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void dismissAccessoryAggregationView() {
        showSpotlight(false);
        showSpeaker(false);
        showBeacon(false);
        spotlightPanel.setVisibility(View.GONE);
        speakerPanel.setVisibility(View.GONE);
    }

    @Override
    public List<String> convertGeometryLatLngList() {
        List<String> LatLngList = new ArrayList<>();
        if (mWayPointTaskList != null) {
            for (WayPointInfo wayPointTask : mWayPointTaskList) {

                String latLng = wayPointTask.getPosition().latitude +
                        "," + wayPointTask.getPosition().longitude +
                        "," + wayPointTask.getPosition().altitude;
                LatLngList.add(latLng);
            }
        }
        return LatLngList;
    }

    /**
     * 绘图按钮
     *
     * @param isOn
     */
    protected abstract void onSwitchDrawCircle(boolean isOn);


    @Override
    public void showMission() {
        showMission(false);
    }

    /**
     * 显示任务
     *
     * @param isLoadMission 是否载入任务
     */
    private void showMission(boolean isLoadMission) {

        drawMissionPath(isLoadMission);
        double flyTime = mPresenter.prepareMission();
        updateBottomMissionInfo(flyTime);
        updateFlightEnable(flyTime > 0);

        ibSaveTask.setEnabled(true);
        ibLoadTask.setEnabled(false);
    }

    @Override
    public void showLoadMission() {

        showMission(true);

        ibDrawCircle.switchOn();
        ibSaveTask.setEnabled(true);
        ibLoadTask.setEnabled(false);
    }

    
    public void clearMission() {

        clearMissionLayer();

        ibSaveTask.setEnabled(false);
        ibLoadTask.setEnabled(true);
        tvBottomInfo.setVisibility(View.GONE);
    }

    @Override
    public void clearMissionLayer() {
        if (this.mFlagLayer != null)
            this.mFlagLayer.getGraphics().clear();

        if (this.flightPointTextLayer != null)
            this.flightPointTextLayer.getGraphics().clear();

        if (this.mMissionLineLayer != null)
            this.mMissionLineLayer.getGraphics().clear();

        if (this.mMissionPointLayer != null)
            this.mMissionPointLayer.getGraphics().clear();

        if (this.mMissionFlagLayer != null)
            this.mMissionFlagLayer.getGraphics().clear();

        if (this.mMissionAltitudeLayer != null)
            this.mMissionAltitudeLayer.getGraphics().clear();
    }

    private Observable<Boolean> getMercatorPolygonsByFiledId(final String fileId) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
              
                mDB.getMercatorPolygonsByFiledId(fileId, new DataBaseUtils.onExecResult() {
                    @Override
                    public void execResult(boolean succ, String errStr) {

                    }

                    @Override
                    public void execResultWithResult(boolean succ, Object result, String errStr) {
                        if (succ) {

                            final List<List<LatLngInfo>> allPolygonsList = (List<List<LatLngInfo>>) result;
                            if (allPolygonsList != null && !allPolygonsList.isEmpty()) {

                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0F);

                                SimpleFillSymbol mPolygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, 0x1000ee00, lineSymbol);

                                for (int count = 0; count < allPolygonsList.size(); count++) {

                                    List<LatLngInfo> mercatorList = allPolygonsList.get(count);
                                    PointCollection collection = new PointCollection(getSpatialReference());
                                    if (mercatorList != null) {

                                        for (int i = 0; i < mercatorList.size(); i++) {

                                            LatLngInfo mercator = mercatorList.get(i);
                                            collection.add(new Point(mercator.longitude, mercator.latitude));
                                        }

                                        Polygon polygon = new Polygon(collection);
                                        Graphic graphics = new Graphic(polygon, mPolygonSymbol);
                                        mKmlPolygonLayer.getGraphics().add(graphics);
                                    }
                                }

                                if (allPolygonsList.get(0) != null) {
                                    LatLngInfo firstPoint = allPolygonsList.get(0).get(0);
                                    if (firstPoint != null) {
                                        centerAtMercatorPoint(new Point(firstPoint.longitude, firstPoint.latitude));
                                    }
                                }
                            }
                        }
                        emitter.onComplete();

                    }

                    @Override
                    public void setExecCount(int i, int count) {

                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> getMercatorPolylinesByFieldId(final String fileId) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
              

                mDB.getMercatorPolylinesByFieldId(fileId, new DataBaseUtils.onExecResult() {
                    @Override
                    public void execResult(boolean succ, String errStr) {

                    }

                    @Override
                    public void execResultWithResult(boolean succ, Object result, String errStr) {
                        if (succ) {

                            final List<List<LatLngInfo>> allPolylinesList = (List<List<LatLngInfo>>) result;
                            if (allPolylinesList != null && !allPolylinesList.isEmpty()) {

                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0F);

                                for (int count = 0; count < allPolylinesList.size(); count++) {

                                    List<LatLngInfo> mercatorList = allPolylinesList.get(count);

                                    PointCollection collection = new PointCollection(getSpatialReference());
                                    if (mercatorList != null) {
                                        for (int i = 0; i < mercatorList.size(); i++) {

                                            LatLngInfo mercator = mercatorList.get(i);
                                            collection.add(mercator.longitude, mercator.latitude);
                                        }

                                        Polyline polyline = new Polyline(collection);
                                        Graphic lineGraphic = new Graphic(polyline, lineSymbol);
                                        mKmlPolylineLayer.getGraphics().add(lineGraphic);
                                    }
                                }

                                if (allPolylinesList.get(0) != null) {
                                    LatLngInfo firstPoint = allPolylinesList.get(0).get(0);
                                    if (firstPoint != null) {
                                        centerAtMercatorPoint(new Point(firstPoint.longitude, firstPoint.latitude));
                                    }
                                }
                            }
                        }

                        emitter.onComplete();

                    }

                    @Override
                    public void setExecCount(int i, int count) {

                    }

                });
            }
        }).subscribeOn(Schedulers.io());
    }

    private Observable<Boolean> getMecatorPointsByFileId(final String fileId) {

        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
              
                mDB.getPointsByFileId(fileId, new DataBaseUtils.onExecResult() {
                    @Override
                    public void execResult(boolean succ, String errStr) {

                    }

                    @Override
                    public void execResultWithResult(boolean succ, Object result, String errStr) {
                        if (succ) {
                            final ArrayList<PointWithName> pointList = (ArrayList<PointWithName>) result;
                            int iPosition = 0;
                            Point firstPoint = null;


                            for (PointWithName pointWithName : pointList) {

                                TextSymbol textSymbol = new TextSymbol();
                                textSymbol.setSize(10);
                                textSymbol.setColor(getResources().getColor(R.color.black));
                                textSymbol.setBackgroundColor(getResources().getColor(R.color.default_text_symbol_background));
                                textSymbol.setHorizontalAlignment(TextSymbol.HorizontalAlignment.RIGHT);
                                textSymbol.setVerticalAlignment(TextSymbol.VerticalAlignment.BOTTOM);
                                textSymbol.setText(pointWithName.getName());

                                LocationMercator position = LocationCoordinateUtils.gps84ToMapMercator(pointWithName.getPoint().getX(), pointWithName.getPoint().getY());

                                Point point = new Point(position.x, position.y);
                                if (iPosition == 0) {
                                    firstPoint = point;
                                }
                                iPosition++;

                                ListenableFuture<PictureMarkerSymbol> symbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                                        R.drawable.kmlpoint_google));

                                try {
                                    Graphic graphicAnno = new Graphic(point, textSymbol);
                                    Graphic graphic = null;
                                    graphic = new Graphic(point, symbol.get());
                                    mKmlPointLayer.getGraphics().add(graphic);
                                    mKmlAnnoLayer.getGraphics().add(graphicAnno);
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (firstPoint != null) {
                                centerAtMercatorPoint(firstPoint);
                            }
                        }

                        emitter.onComplete();
                    }

                    @Override
                    public void setExecCount(int i, int count) {

                    }
                });

            }
        }).subscribeOn(Schedulers.io());


    }

    
    protected void switchFPVDefault() {
        initFPVDefault();

     /*   UXSDKEventBus.getInstance().post(new Events.FPVDimensionsEvent(AppConstant.ScreenWidth * 3 / ratioHeight,
                AppConstant.ScreenWidth * 3 / ratioWidth, FPVWidget.VideoSource.PRIMARY));*/

    }

    private void initFPVDefault() {

        ly_base_operation.setVisibility(View.VISIBLE);
        ly_fpv_camera_setting.setVisibility(View.GONE);
        mRadarWidget.setVisibility(View.GONE);

        mFPVOverlayWidget.setTouchable(false);

        ly_base_map_and_fpv.bringChildToFront(ly_fpv);
        ly_fpv.setLayoutParams(getFpvMinLayoutParams());
        ly_base_geo_region.setLayoutParams(getFpvMaxLayoutParams());

        ly_fpv.setOnClickListener(this);

        isFPVMax = false;
    }

    
    protected void switchFPVMax() {

        ly_base_operation.setVisibility(View.GONE);
        ly_fpv_camera_setting.setVisibility(View.VISIBLE);

        if (isRadarDisplayEnable) {
            mRadarWidget.setVisibility(View.VISIBLE);
        }

        ly_base_map_and_fpv.bringChildToFront(ly_base_geo_region);
        ly_fpv.setLayoutParams(getFpvMaxLayoutParams());
        ly_base_geo_region.setLayoutParams(getFpvMinLayoutParams());

        ly_fpv.setOnClickListener(null);

        mFPVOverlayWidget.setTouchable(true);

        isFPVMax = true;

      
        UXSDKEventBus.getInstance().post(new Events.FPVDimensionsEvent(ly_base_map_and_fpv.getWidth(),
                ly_base_map_and_fpv.getHeight(), FPVWidget.VideoSource.PRIMARY));

    }

    
    private void switchFPVTwice() {

    }

    private void showWeatherDetail(boolean isShow) {
        llAssistantFunction.setVisibility(isShow ? View.VISIBLE : View.GONE);
        ivWeatherLoc.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tvAltitude.setVisibility(isShow ? View.VISIBLE : View.GONE);
        isShowWeather = isShow;
        if (isShow) {
            refreshAltitude();
        }
    }

    
    protected void refreshAltitude() {
        if (isShowWeather) {
            LocationCoordinate coordinate = getMapCenterLocation();
            double altitude = demReaderUtils.getZValue(new LatLngInfo(coordinate.latitude, coordinate.longitude));
            tvAltitude.setText(demReaderUtils.checkZValue(altitude) ? String.format("海拔:%.0fm", altitude) : "海拔:N/A");
        }
    }

    /**
     * 获取地图中心点
     *
     * @return
     */
    protected LocationCoordinate getMapCenterLocation() {
        Point pc = getMapCenterPoint();
        return LocationCoordinateUtils.mapMercatorToGps84(pc.getX(), pc.getY());
    }

    protected Point getMapCenterPoint() {
        int centerX = mMapView.getWidth() / 2;
        int centerY = mMapView.getHeight() / 2;
        return ArcGisUtil.getScreenLocationPoint(mMapView, centerX, centerY);
    }

    @Override
    public void updateMonitorInfo(String info) {
        EventBus.getDefault().post(new MonitorInfoUpdateEvent(info));
    }

    @Override
    public void showMonitorInfo() {
        EventBus.getDefault().post(new MonitorInfoShowEvent(true));
    }

    @Override
    public void updateDroneLocation(LocationCoordinate locationCoordinate, float angle) {

        if (LocationCoordinateUtils.checkGpsCoordinate(locationCoordinate)) {
          
            float droneYaw = angle;

            Point aircraftPoint = locationToMercatorPoint(locationCoordinate);

            try {
                mDroneGraphic.setGeometry(aircraftPoint);
                mDroneGraphic.setVisible(true);
                PictureMarkerSymbol pictureMarkerSymbol = mDroneSymbol.get();
                pictureMarkerSymbol.setAngle(droneYaw);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isFPVMax) {
                mMapView.setViewpointCenterAsync(aircraftPoint);
            }
        }
    }

    @Override
    public void updateHomeLocation(LocationCoordinate locationCoordinate) {
        if (LocationCoordinateUtils.checkGpsCoordinate(locationCoordinate)) {
            mHomeGraphic.setGeometry(locationToMercatorPoint(locationCoordinate));
            mHomeGraphic.setVisible(true);
        }
    }

    @Override
    public void updateLineBetweenDroneAndHome(LocationCoordinate homeLoc, LocationCoordinate droneLoc) {

        if (LocationCoordinateUtils.checkGpsCoordinate(homeLoc) && LocationCoordinateUtils.checkGpsCoordinate(droneLoc)) {
            List<LocationCoordinate> coordinates = new ArrayList<>();
            coordinates.add(homeLoc);
            coordinates.add(droneLoc);

            if (mHomeGraphic.isVisible() && mDroneGraphic.isVisible()) {

                PointCollection points = locationToMercatorPointCollection(coordinates);
                Polyline polyline = new Polyline(points);
                mHomeAndDroneLineGraphic.setVisible(true);
                mHomeAndDroneLineGraphic.setGeometry(polyline);
            } else {
                mHomeAndDroneLineGraphic.setVisible(false);
            }
        }
    }

    @Override
    public void updateBottomMissionInfo(double missionFlyTime) {
        tvBottomInfo.setVisibility(View.VISIBLE);
        setBottomTextColor(missionFlyTime);
    }

    /**
     * 根据预计时长及机型改变文本颜色
     *
     * @param flyTime
     */
    protected void setBottomTextColor(double flyTime) {
        float batteryLifeTime = BatteryManager.getBatteryLifeTime();
        if (flyTime > batteryLifeTime) {
            tvBottomInfo.setTextColor(Color.RED);
        } else if (flyTime > batteryLifeTime * 0.75) {
            tvBottomInfo.setTextColor(Color.YELLOW);
        } else {
            tvBottomInfo.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void saveMission() {
        EditMissionNameDialogFragment editMissionNameDialogFragment = new EditMissionNameDialogFragment();
        editMissionNameDialogFragment.show(getActivity().getFragmentManager(), "编辑任务名称");
        editMissionNameDialogFragment.setOnSureButtonClickListener(new EditMissionNameDialogFragment.onSureButtonClickListener() {
            @Override
            public void sure(final String name) {
                String snapShot = screenShot();
                mPresenter.saveMission(false, name, snapShot);
            }
        });
    }


    
    protected String screenShot() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        final String snapshotName = sdf.format(new Date());

        return MissionUtil.screenShot(mMapView, IOUtils.getRootStoragePath(getActivity())
                + AppConstant.DIR_MISSION_PHOTO, snapshotName);
    }

    /**
     * 双击
     *
     * @return
     */
    protected boolean checkDoubleClick() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            return true;
        }
        return false;
    }


    protected void showWeatherInfoDialog() {
        LocationCoordinate gps = getMapCenterLocation();
        LocationCoordinate gcj = LocationCoordinateUtils.gps84_To_Gcj02(gps);

        LatLngInfo pCenter = new LatLngInfo(gcj.latitude, gcj.longitude);

        WeatherInfoDlgFragment weatherInfoDlgFragment = new WeatherInfoDlgFragment();
        Bundle weatherArgs = new Bundle();
        weatherArgs.putSerializable("location", pCenter);
        weatherInfoDlgFragment.setArguments(weatherArgs);
        weatherInfoDlgFragment.show(getFragmentManager(), "weather");
    }

    protected void showMobileNavigationDialog() {
        LocationCoordinate gps = getMapCenterLocation();
        LocationCoordinate gcj = LocationCoordinateUtils.gps84_To_Gcj02(gps);

        LatLngInfo pCenter = new LatLngInfo(gcj.latitude, gcj.longitude);
        MobileNavigationDialogFragment dlg = new MobileNavigationDialogFragment();
        Bundle dlgArgs = new Bundle();
        dlgArgs.putSerializable("location", pCenter);
        dlg.setArguments(dlgArgs);
        dlg.show(getFragmentManager(), "mobile_navigation");
    }

    @Override
    public void updateFlightEnable(boolean enable) {
        ibFlight.setEnabled(enable);
    }

    @Override
    public void updateFlightButtons(AppConstant.FlightAction action) {

    }

    @Override
    public void updateFlightStart() {
        ibFlight.setVisibility(View.GONE);
        ibPause.setVisibility(View.VISIBLE);
        ibResume.setVisibility(View.GONE);
        ibGoHome.setVisibility(View.VISIBLE);
        llDrawCircle.setVisibility(View.GONE);
        llKml.setVisibility(View.GONE);
        ibSetting.setVisibility(View.GONE);
        ibMore.setVisibility(View.GONE);
        ibLoadTask.setVisibility(View.GONE);
        ibSaveTask.setVisibility(View.GONE);
        if (isShowWeather)
            ibMore.performClick();
    }

    @Override
    public void updateFlightPause() {
        ibFlight.setVisibility(View.GONE);
        ibPause.setVisibility(View.GONE);
        ibResume.setVisibility(View.VISIBLE);
        ibGoHome.setVisibility(View.VISIBLE);
        llDrawCircle.setVisibility(View.GONE);
        llKml.setVisibility(View.GONE);
        ibSetting.setVisibility(View.GONE);
        ibMore.setVisibility(View.GONE);
        ibLoadTask.setVisibility(View.GONE);
        ibSaveTask.setVisibility(View.GONE);
    }

    @Override
    public void updateFlightResume() {
        ibFlight.setVisibility(View.GONE);
        ibPause.setVisibility(View.VISIBLE);
        ibResume.setVisibility(View.GONE);
        ibGoHome.setVisibility(View.VISIBLE);
        llDrawCircle.setVisibility(View.GONE);
        llKml.setVisibility(View.GONE);
        ibSetting.setVisibility(View.GONE);
        ibMore.setVisibility(View.GONE);
        ibLoadTask.setVisibility(View.GONE);
        ibSaveTask.setVisibility(View.GONE);
        if (isShowWeather)
            ibMore.performClick();
    }

    @Override
    public void updateFlightGoHome() {
        ibFlight.setVisibility(View.VISIBLE);
        ibPause.setVisibility(View.GONE);
        ibResume.setVisibility(View.GONE);
        ibGoHome.setVisibility(View.GONE);
        llDrawCircle.setVisibility(View.VISIBLE);
        llKml.setVisibility(View.VISIBLE);
        ibSetting.setVisibility(View.VISIBLE);
        ibMore.setVisibility(View.VISIBLE);
        ibLoadTask.setVisibility(View.VISIBLE);
        ibSaveTask.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateFlightUnknown() {
        ibFlight.setVisibility(View.VISIBLE);
        ibPause.setVisibility(View.GONE);
        ibResume.setVisibility(View.GONE);
        ibGoHome.setVisibility(View.GONE);
        llDrawCircle.setVisibility(View.VISIBLE);
        llKml.setVisibility(View.VISIBLE);
        ibSetting.setVisibility(View.VISIBLE);
        ibMore.setVisibility(View.VISIBLE);
        ibLoadTask.setVisibility(View.VISIBLE);
        ibSaveTask.setVisibility(View.VISIBLE);
    }

    @Override
    public void clickFlightStart() {

        updateFlightEnable(false);

        mCheckConditionManager.cancel();

        startFlyCheck();

        mCheckConditionManager.startCheck(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                updateFlightEnable(true);
            }

            @Override
            public void onError(Throwable e) {
                updateFlightEnable(true);
            }

            @Override
            public void onComplete() {
                updateFlightEnable(true);
                showFlightCheck();
            }
        });
    }

    @Override
    public void showFlightCheck() {
        FlyCheckingDialogFragment dialog = new FlyCheckingDialogFragment();
        Bundle arg = new Bundle();
        arg.putParcelable(ARG_PARAM_FLYCHECK_STATUS, mPresenter.getFlyCheckStatus());
        dialog.setArguments(arg);
        dialog.setFlyCheckingListener(new FlyCheckingDialogFragment.FlyCheckingFragmentListener() {
            @Override
            public void onFlyCheckingComplete(boolean blnResult) {
                if (blnResult) {
                    mPresenter.startMission();
                }
            }

            @Override
            public void onFlyCheckUpload(boolean bResult) {
                if (bResult) {
                    mPresenter.uploadMission();
                } else {
                    mPresenter.cancelUploadMission();
                }
            }
        });
        dialog.show(getFragmentManager(), "FlyCheckingDialogFragment");
    }


    public void startFlyCheck() {

        addCommonCheckCondition();

        mCheckConditionManager.addCheckConditionSubscribe(emitter -> {
            CheckError error = mPresenter.checkMaxFlyHeight();
            mCheckConditionManager.showCheckResult(error, emitter);
        });

        mCheckConditionManager.addCheckConditionSubscribe(emitter -> {
            CheckError error = mPresenter.checkGoHomeFlyHeight();
            mCheckConditionManager.showCheckResult(error, emitter);
        });
    }

    protected void addCommonCheckCondition() {

        mCheckConditionManager.addCheckConditionSubscribe(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                CheckError error = mPresenter.checkGetHomePoint();
                mCheckConditionManager.showCheckResult(error, emitter);
            }
        });

        mCheckConditionManager.addCheckConditionSubscribe(emitter -> {
            CheckError error = mPresenter.checkMission();
            mCheckConditionManager.showCheckResult(error, emitter);
        });


        mCheckConditionManager.addCheckConditionSubscribe(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                CheckError error = mPresenter.checkDJIAccountState();
                mCheckConditionManager.showCheckResult(error, emitter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            loginDjiAccount(true);
                        }
                    }
                });
            }
        });

        mCheckConditionManager.addCheckConditionSubscribe(emitter -> {
            CheckError error = mPresenter.checkEnoughFlyTimeAndBattery();
            mCheckConditionManager.showCheckResult(error, emitter);
        });

        mCheckConditionManager.addCheckConditionSubscribe(emitter -> {
            CheckError error = mPresenter.checkWaypointParams();
            mCheckConditionManager.showCheckResult(error, emitter);
        });

        mPresenter.checkPitchRangeExtension(mCheckConditionManager);
    }


    @Override
    public void showFlightSuccess() {
        showMonitorInfo();
        String snapShot = screenShot();
        mPresenter.saveMission(true, DateHelperUtils.getDateSeries(), snapShot);
    }

    @Override
    public void showSimulateDialog() {

        if (!mPresenter.checkAirCraftConnect())
            return;

      
        LocationCoordinate mapCenterLocation = getMapCenterLocation();
        LatLngInfo mapLatLng = new LatLngInfo(mapCenterLocation.latitude, mapCenterLocation.longitude);
        SimulatorDialog simulatorDialog = new SimulatorDialog();
        Bundle args = new Bundle();
        args.putDouble(PARAM_SIMULATOR_LATITUDE, mapLatLng.latitude);
        args.putDouble(PARAM_SIMULATOR_LONGITUDE, mapLatLng.longitude);
        simulatorDialog.setArguments(args);
        simulatorDialog.setConfirmListener(new IConfirmListener() {
            @Override
            public void onConfirm(String tag, Object object) {
                if (object instanceof LocationCoordinate) {
                    mPresenter.startSimulate((LocationCoordinate) object);
                }
            }
        });
        simulatorDialog.show(getFragmentManager(), "SimulatorDialog");


       /* SimulateSettingDlgFragment simulateDlg = new SimulateSettingDlgFragment();
        Bundle args = new Bundle();
        args.putString("lat", mapLatLng.latitude + "");
        args.putString("lon", mapLatLng.longitude + "");
        args.putString("tag", getTag());
        simulateDlg.setArguments(args);
        simulateDlg.show(getFragmentManager(), "simulate");*/
    }


    @Override
    public void onGetNoFlyZoneSuccess(final ArrayList<SubFlyZoneInformation[]> map) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (map == null) {
                    ToastUtil.show(getActivity(), "获取禁飞区数据失败！");
                    return;
                }
                if (map.size() == 0) {
                    ToastUtil.show(getActivity(), "无禁飞区");
                    return;
                }
                mMapView.setViewpointScaleAsync(AppConstant.DEFAULT_MAP_SCALE);

                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID,
                        Color.argb(100, 219, 112, 147), new SimpleLineSymbol(SimpleLineSymbol.Style.NULL, Color.WHITE, 0.0f));
                for (SubFlyZoneInformation[] subFlyZoneInformations : map) {
                    if (subFlyZoneInformations != null && subFlyZoneInformations.length > 0) {
                        for (SubFlyZoneInformation subFlyZoneInformation : subFlyZoneInformations) {
                            PointCollection collection = new PointCollection(getSpatialReference());
                            for (int i = 0; i < subFlyZoneInformation.getVertices().size(); i++) {
                                LocationCoordinate2D locationCoordinate2D = subFlyZoneInformation.getVertices().get(i);
                                LocationMercator lngInfo = LocationCoordinateUtils.gps84ToMapMercator(locationCoordinate2D.getLongitude(), locationCoordinate2D.getLatitude());
                                collection.add(new Point(lngInfo.x, lngInfo.y));
                            }
                            Polygon polygon = new Polygon(collection);
                            mNoFlyZoneLayer.getGraphics().add(new Graphic(polygon, simpleFillSymbol));
                        }
                    } else {
                        ToastUtil.show(getActivity(), "无禁飞区");
                    }
                }
            }
        });
    }

    @Override
    public void onGetNoFlyZoneFailed(final String error) {

        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(error);
            }
        });
    }

    @Override
    public void onSelectKMLComplete(String tag, ArrayList<String> selectIdList) {

        this.mSelectedKmlFileIds = selectIdList;

        if (mKmlPolygonLayer != null) {
            mKmlPolygonLayer.getGraphics().clear();
        }

        if (mKmlPolylineLayer != null) {
            mKmlPolylineLayer.getGraphics().clear();
        }

        if (mKmlDotLayer != null) {
            mKmlDotLayer.getGraphics().clear();
        }

        if (mKmlAnnoLayer != null) {
            mKmlAnnoLayer.getGraphics().clear();
        }

        if (mKmlPointLayer != null) {
            mKmlPointLayer.getGraphics().clear();
        }

        showLoadProgressDialog("正在绘制图像……");

        Observable.fromIterable(this.mSelectedKmlFileIds).concatMap(new Function<String, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(String fileId) throws Exception {

                return Observable.concat(getMercatorPolygonsByFiledId(fileId), getMercatorPolylinesByFieldId(fileId), getMecatorPointsByFileId(fileId));
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .takeLast(1)
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadProgressDialog();
                        refreshLayerVisibility();
                    }

                    @Override
                    public void onComplete() {
                        dismissLoadProgressDialog();
                        refreshLayerVisibility();
                    }
                });
    }

    @Override
    public void showKmlDialog() {
        if (!mPresenter.checkTaskFree())
            return;
        ImportExcelKmlDlgFragment importDlg = new ImportExcelKmlDlgFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("fileIds", mSelectedKmlFileIds);
        args.putBoolean("no_excel", true);
        importDlg.setArguments(args);
        importDlg.setKMLListener(this);
        importDlg.show(getFragmentManager(), "kml_excel");
    }

    @Override
    public void reviewMission(List<String> selectedIdList, int missionType) {
        mReviewMissionLayer.getGraphics().clear();
        ibLoadTaskClear.setVisibility(selectedIdList != null
                && selectedIdList.size() > 0 ? View.VISIBLE : View.GONE);
        if (selectedIdList != null) {
            for (String missionId : selectedIdList) {
                MissionBase mission = MissionHelper.loadMission(missionType, missionId);
                if (mission != null) {
                    List<LocationCoordinate> gpsInfoList = mission.getGeometryLatLngInfoList();
                    PointCollection collection = new PointCollection(getSpatialReference());
                    int i = 0;
                    for (LocationCoordinate gpsInfo : gpsInfoList) {
                        LocationMercator mercatorInfo = LocationCoordinateUtils.gps84ToMapMercator(gpsInfo.longitude, gpsInfo.latitude);
                        Point point = new Point(mercatorInfo.x, mercatorInfo.y);
                        if (i == 0) {
                            centerAtMercatorPoint(point);
                        }
                        collection.add(point);
                        i++;
                    }
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.CYAN, 2);
                    if (mission.getGeometryType() == MissionBase.GEOMETRYTYPE_POLYGON) {
                        Polygon polygon = new Polygon(collection);
                        mReviewMissionLayer.getGraphics().add(new Graphic(polygon, lineSymbol));
                    } else if (mission.getGeometryType() == MissionBase.GEOMETRYTYPE_LINE) {
                        Polyline polyline = new Polyline(collection);
                        mReviewMissionLayer.getGraphics().add(new Graphic(polyline, lineSymbol));
                    }
                }
            }
        }
    }

    @Override
    public void showLowPowerReturnDialog() {

        if (mGoHomeDialog == null) {
            mGoHomeDialog = new GoHomeDialog(getActivity());
        }
        if (!mGoHomeDialog.isShowing()) {
            mGoHomeDialog.setTitle("电量低，请立即返航！");
            mGoHomeDialog.setMessage("电量低警告！10秒后自动返航");
            mGoHomeDialog.setGoHomeListener(new GoHomeDialog.GoHomeListener() {
                @Override
                public void confirm() {
                    mPresenter.goHome();
                }

                @Override
                public void cancel() {
                   /* mPresenter.stopRecord();
                    mPresenter.stopTakePhoto();*/
                }
            });
            mGoHomeDialog.show();
        }
    }

    @Override
    public void showAutoReturnDialog() {

        if (mGoHomeDialog == null) {
            mGoHomeDialog = new GoHomeDialog(getActivity());
        }
        if (!mGoHomeDialog.isShowing()) {
            mGoHomeDialog.setTitle("任务执行完成");
            mGoHomeDialog.setMessage("任务执行完成，10秒后自动返航");

            mGoHomeDialog.setGoHomeListener(new GoHomeDialog.GoHomeListener() {
                @Override
                public void confirm() {
                    mPresenter.goHome();
                }

                @Override
                public void cancel() {

                }
            });
            mGoHomeDialog.show();
        }
    }

    @Override
    public void showSmartReturnDialog(GoHomeDialog.GoHomeListener goHomeListener) {
        if (mGoHomeDialog == null) {
            mGoHomeDialog = new GoHomeDialog(getActivity());
        }
        if (!mGoHomeDialog.isShowing()) {
            mGoHomeDialog.setTitle("低电量自动返航！");
            mGoHomeDialog.setMessage("当前电量仅足够返航路程，请立即返航，10秒后自动返航");
            mGoHomeDialog.setGoHomeListener(goHomeListener);
            mGoHomeDialog.show(false);
        }
    }

    @Override
    public void showSmartReturnTimeCount(int countdown) {
        if (mGoHomeDialog != null) {
            mGoHomeDialog.updateMessage("当前电量仅足够返航路程，请立即返航，" + countdown + "秒后自动返航");
            if (countdown == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mGoHomeDialog.dismiss();
                    }
                }, 1000);

            }
        }
    }

    @Override
    public boolean isTaskCanTerminate() {
        return mPresenter.checkTaskFree();
    }


    @Override
    public void onClick(View v) {

        onNormalClick(v);

        onSingleClick(v);
    }

    public void onNormalClick(View v) {
        switch (v.getId()) {
            case R.id.ib_more:
                showWeatherDetail(!isShowWeather);
                break;
            case R.id.imgMapMode:
                switchMapMode();
                break;
            case R.id.tv_speaker:
                toggleViewVisible(speakerPanel);
                break;
            case R.id.tv_spotlight:
                toggleViewVisible(spotlightPanel);
                break;
            case R.id.tv_beacon:
                AccessoryManager.toggleBeacon();
                break;
        }
    }

    @SingleClick
    public void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnFlight:
                if (!mPresenter.checkTaskFree())
                    return;
                clickFlightStart();
                break;
            case R.id.imgBtnGoHome:
                mPresenter.goHome();
                break;
            case R.id.imgBtnPause:
                mPresenter.pauseMission();
                break;
            case R.id.imgBtnResume:
                mPresenter.resumeMission();
                break;
            case R.id.imgBtnKML:
                showKmlDialog();
                break;
            case R.id.imgBtnLocation:
                LocationCoordinate aircraftLocation = mPresenter.getAirCraftLocation();
                if (LocationCoordinateUtils.checkGpsCoordinate(aircraftLocation)) {
                    centerAt(aircraftLocation);
                } else if (LocationCoordinateUtils.checkGpsCoordinate(getMobileLocation())) {
                    centerAtMobileLocation();
                } else {
                    showToast("当前无定位");
                }
                break;
            case R.id.ly_fpv:
                showWeatherDetail(false);
                if (!isFPVMax) {
                    switchFPVMax();
                }

                break;
            case R.id.iv_show_compass:
                if (isFPVMax) {
                    mMapView.setVisibility(View.GONE);
                } else {
                    ly_fpv.setVisibility(View.GONE);
                }
                ly_camera_min_window_operation_layer.setVisibility(View.GONE);
                ivShowCompass.setVisibility(View.GONE);
                mCompassWidget.setVisibility(View.VISIBLE);

                break;
            case R.id.compass:
                ivShowCompass.setVisibility(View.VISIBLE);
                mCompassWidget.setVisibility(View.GONE);
                mMapView.setVisibility(View.VISIBLE);
                ly_camera_min_window_operation_layer.setVisibility(View.VISIBLE);
                ly_fpv.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_weather_detail:
                showWeatherInfoDialog();
                break;
            case R.id.tv_navigation:
                showMobileNavigationDialog();
                break;
            case R.id.imgBtnSettings:
                if (!mPresenter.checkTaskFree())
                    return;
                showSettingDialog();
                break;
            case R.id.imgBtnLoadTask:
                if (!mPresenter.checkTaskFree())
                    return;
                showLoadTaskDialog();
                break;
            case R.id.imgBtnLoadTaskClear:
                if (!mPresenter.checkTaskFree())
                    return;
                mReviewMissionLayer.getGraphics().clear();
                ibLoadTaskClear.setVisibility(View.GONE);
                mSelectedMissionIdList.clear();
                break;
            case R.id.imgBtnSaveTask:
                saveMission();
                break;
            case R.id.iv_narrow_fpv:
               /* switchFPVDefault();
                ivNarrowFPV.setVisibility(View.GONE);
                clickOnCameraWindow = false;*/
                break;
            case R.id.tv_noflyzone:
                if (mPresenter.checkAirCraftConnect()) {
                    if (mNoFlyZoneLayer.getGraphics() != null && !mNoFlyZoneLayer.getGraphics().isEmpty()) {
                        mNoFlyZoneLayer.getGraphics().clear();
                        return;
                    }
                    NoFlyZoneDataUtils flyZoneData = NoFlyZoneDataUtils.getInstance();
                    flyZoneData.getNoFlyZoneData(getContext(), this);
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLocation:
                FastLocationDialog dlg = new FastLocationDialog();
                dlg.setOnFastLocaionConfirm(new FastLocationDialog.OnFastLocationConfirm() {
                    @Override
                    public void onLocationConfirm(LatLngInfo latLngInfo) {
                        mMapView.setViewpointScaleAsync(AppConstant.DEFAULT_MAP_SCALE);
                        LocationMercator locMercator = LocationCoordinateUtils.gps84ToMapMercator(latLngInfo.longitude, latLngInfo.latitude);
                        Point locPoint = new Point(locMercator.x, locMercator.y);
                        centerAtMercatorPoint(locPoint);
                    }
                });
                dlg.show(getFragmentManager(), "fast_location");
                break;
        }
        return false;
    }

  

    protected void initViewConfig() {
        Object isOpenRadar = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.RADAR_DISPLAY_ENABLE));
        isRadarDisplayEnable = isOpenRadar != null && (boolean) isOpenRadar;

        Object videoSource = FlyKeyManager.getInstance().getValue(FlySettingKey.create(FlySettingKey.VIDEO_SOURCE));
        if (videoSource instanceof DjiFPVWidget.VideoSource) {
            if (mFPVWidget != null) {
                mFPVWidget.setVideoSource((DjiFPVWidget.VideoSource) videoSource);
            }
        }
    }

    @Override
    public void setUpKeys() {
        FlyKeyManager.getInstance().addListener(FlySettingConfigKey.create(FlySettingConfigKey.RADAR_DISPLAY_ENABLE), mRadarDisplayListener);
        FlyKeyManager.getInstance().addListener(FlySettingKey.create(FlySettingKey.VIDEO_SOURCE), mVideoSourceListener);
    }

    @Override
    public void tearDownKeys() {
        FlyKeyManager.getInstance().removeListener(mRadarDisplayListener);
        FlyKeyManager.getInstance().removeListener(mVideoSourceListener);
    }

    private KeyListener mRadarDisplayListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof Boolean) {
                isRadarDisplayEnable = (boolean) newValue;
                if (isFPVMax) {
                    mRadarWidget.setVisibility(isRadarDisplayEnable ? View.VISIBLE : View.GONE);
                }

               /* mRtcControlPanel.setVisibility(isRadarDisplayEnable?View.VISIBLE:View.GONE);
                mRtcControlPanel.setMasterUserName("我");
                mRtcControlPanel.setGuestUserName("指挥中心");*/
            }
        }
    };

    private KeyListener mVideoSourceListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof DjiFPVWidget.VideoSource) {
                mFPVWidget.switchVideoSource((DjiFPVWidget.VideoSource) newValue);
            }
        }
    };

  

    @Override
    public void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    public void onDestroy() {
        GpsUtils2.stopLocationClient(false);

        mMapView.dispose();
        LocationUtil.getInstance().destroy();

        this.mKmlPolygonLayer = null;
        this.mKmlPolylineLayer = null;
        this.mKmlPointLayer = null;

        mFlightLayer = null;
        mLocationLayer = null;

        this.mMissionLineLayer = null;
        this.mMissionPointLayer = null;
        this.mMissionFlagLayer = null;

        this.mFlagLayer = null;

        this.flightPointTextLayer = null;

        this.mMapView = null;
        this.mReviewMissionLayer = null;
        this.mNoFlyZoneLayer = null;

        if (mCheckConditionManager != null) {
            mCheckConditionManager.cancel();
        }

        super.onDestroy();
    }

  

    /**
     * 获取坐标系统
     *
     * @return
     */
    protected SpatialReference getSpatialReference() {
        return SpatialReferences.getWebMercator();
    }

    protected void initLocation() {

        LocationUtil.getInstance().init(mContext)
                .addLocationListener(new LocationUtil.LocationListener() {
                    @Override
                    public void result(double longitude, double latitude) {
                        LocationCoordinate aircraftLoc = mPresenter.getAirCraftLocation();
                        if (!LocationCoordinateUtils.checkGpsCoordinate(aircraftLoc)) {
                            mMobileLocation = new LocationCoordinate(latitude, longitude);
                            if (!isMobileLocationGet) {
                                isMobileLocationGet = true;
                                centerAtMobileLocation();
                            }
                        } else {
                            if (!isMobileLocationGet) {
                                isMobileLocationGet = true;
                                centerAt(aircraftLoc);
                            }
                        }
                    }

                    @Override
                    public void error(int errorCode) {

                    }

                    @Override
                    public void onRotate(float degree) {
                        if (mLocationSymbol != null) {
                            try {
                                PictureMarkerSymbol pictureMarkerSymbol = mLocationSymbol.get();
                                pictureMarkerSymbol.setAngle(degree + 90);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }).getLocationClient().startLocation();


        mMapView.addLayerViewStateChangedListener(new LayerViewStateChangedListener() {
            @Override
            public void layerViewStateChanged(LayerViewStateChangedEvent layerViewStateChangedEvent) {
                LayerViewStatus status = layerViewStateChangedEvent.getLayerViewStatus().iterator().next();
                if (status == LayerViewStatus.ACTIVE && !isMapLoaded) {
                    isMapLoaded = true;
                    LocationCoordinate aircraftLoc = mPresenter.getAirCraftLocation();
                    if (LocationCoordinateUtils.checkGpsCoordinate(aircraftLoc)) {
                        centerAt(aircraftLoc);
                    } else {
                        startLocation();
                    }
                }

            }
        });
    }

    protected void startLocation() {
        LocationUtil.getInstance().getLocationClient().startLocation();
    }

    protected void centerAt(LocationCoordinate locationCoordinate) {
        centerAt(locationCoordinate, true);
    }

    protected void centerAt(LocationCoordinate locationCoordinate, boolean scaleToDefault) {
        if (LocationCoordinateUtils.checkGpsCoordinate(locationCoordinate)) {
            centerAtMercatorPoint(locationToMercatorPoint(locationCoordinate), scaleToDefault);
        }
    }

    protected void centerAtMercatorPoint(Point point) {
        centerAtMercatorPoint(point, true);
    }

    protected void centerAtMercatorPoint(Point point, boolean scaleToDefault) {
        if (scaleToDefault) {
            mMapView.setViewpointCenterAsync(point, AppConstant.DEFAULT_MAP_SCALE);
        } else {
            mMapView.setViewpointCenterAsync(point);
        }
    }

    private void centerAtMobileLocation() {
        if (LocationCoordinateUtils.checkGpsCoordinate(getMobileLocation())) {
            centerAt(getMobileLocation());

            mLocationGraphic.setGeometry(locationToMercatorPoint(getMobileLocation()));
            mLocationGraphic.setVisible(true);
        }
    }

    @Override
    public LocationCoordinate getMobileLocation() {
        return mMobileLocation;
    }

    @Override
    public void addMissionPoint(Point point) {
        try {
            Graphic graphic = null;
            graphic = new Graphic(point, mMissionPointSymbol.get());
            mMissionPointLayer.getGraphics().add(graphic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMultiMissionPoint(List<Point> pointList) {
        try {
            Multipoint multipoint = new Multipoint(pointList);
            Graphic graphic = null;
            graphic = new Graphic(multipoint, mMissionPointSymbol.get());
            mMissionPointLayer.getGraphics().add(graphic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMissionPointAltitude(Point point, String altitudeTxt) {
        TextSymbol textSymbol = new TextSymbol();
        textSymbol.setSize(10);
        textSymbol.setColor(Color.RED);
        textSymbol.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
        textSymbol.setVerticalAlignment(TextSymbol.VerticalAlignment.TOP);
        textSymbol.setOffsetY(-12);
        textSymbol.setText(altitudeTxt);
        mMissionAltitudeLayer.getGraphics().add(new Graphic(point, textSymbol));
    }

    @Override
    public void addMissionLine(PointCollection points) {
        addMissionLine(points, false);
    }

    @Override
    public void addMissionLine(PointCollection points, boolean showFlag) {
        addMissionLine(points, showFlag, showFlag);
    }

    @Override
    public void addMissionLine(PointCollection points, boolean showStartFlag, boolean showEndFlag) {
        addMissionLine(points, mMissionLineSymbol, showStartFlag, showEndFlag);
    }

    public void addMissionLine(PointCollection points, SimpleLineSymbol lineSymbol, boolean showStartFlag, boolean showEndFlag) {
        Polyline polyline = new Polyline(points);
        Graphic lineGraphic = new Graphic(polyline, lineSymbol);
        lineGraphic.setZIndex(0);
        mMissionLineLayer.getGraphics().add(lineGraphic);
        int size = points.size();
        if (size >= 2) {
            if (showStartFlag) {
                addStartFlag(points.get(0));
            }

            if (showEndFlag) {
                addEndFlag(points.get(size - 1));
            }
        }
    }

    protected void addStartFlag(Point point) {
        PictureMarkerSymbol startFlagSymbol = null;
        try {
            startFlagSymbol = mStartFlaySymbol.get();
            startFlagSymbol.setOffsetY(8);
            Graphic startFlagGraphic = new Graphic(point, startFlagSymbol);
            startFlagGraphic.setZIndex(1);
            mMissionFlagLayer.getGraphics().add(startFlagGraphic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void addEndFlag(Point point) {
        PictureMarkerSymbol endFlagSymbol = null;
        try {
            endFlagSymbol = mEndFlaySymbol.get();
            endFlagSymbol.setOffsetY(8);
            Graphic endFlagGraphic = new Graphic(point, endFlagSymbol);
            endFlagGraphic.setZIndex(2);
            mMissionFlagLayer.getGraphics().add(endFlagGraphic);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建主界面的按钮
     *
     * @param srcDrawableResId
     * @return
     */
    protected ImageButton createOperationButton(ViewGroup viewGroup, int srcDrawableResId) {
        ImageButton imageButton = (ImageButton) LayoutInflater.from(getBaseContext())
                .inflate(R.layout.btn_main_operation, viewGroup, false);
        imageButton.setImageResource(srcDrawableResId);
        return imageButton;
    }


    protected Point getScreenLocationPoint(float screenX, float screenY) {
        android.graphics.Point point = new android.graphics.Point(Math.round(screenX), Math.round(screenY));
        return mMapView.screenToLocation(point);
    }


    protected class BaseMapTouchListener extends DefaultMapViewOnTouchListener {

        public BaseMapTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isFPVMax) {
                switchFPVDefault();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onRotate(MotionEvent event, double rotationAngle) {
          
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            refreshLayerVisibility();
            return super.onScale(detector);
        }

    }

  
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSimulatorEvent(SimulatorEvent event) {
        if (event.isEnable()) {
            showSimulateDialog();
        } else {
            mPresenter.stopSimulate();
        }
    }

}
