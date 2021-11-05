package com.ew.autofly.fragments;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.dji.mapkit.core.maps.DJIMap;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.ew.autofly.BuildConfig;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.MobileNavigationDialogFragment;
import com.ew.autofly.dialog.common.ImportExcelKmlDlgFragment;
import com.ew.autofly.dialog.mission.LoadLocalAndCloudTaskDlg;
import com.ew.autofly.dialog.mission.LoadLocalTaskDlg;
import com.ew.autofly.dialog.tool.FastLocationDialog;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.MarkerPosition;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.PointWithName;
import com.ew.autofly.entity.WayPointTask;
import com.ew.autofly.interfaces.KMLFragmentListener;
import com.ew.autofly.interfaces.OnGetNoFlyZoneDataListener;
import com.ew.autofly.internal.key.base.FlyKeyManager;
import com.ew.autofly.internal.key.callback.KeyListener;
import com.ew.autofly.model.AccessoryManager;
import com.ew.autofly.module.setting.cache.FlySettingConfigKey;
import com.ew.autofly.module.setting.cache.FlySettingKey;
import com.ew.autofly.module.weather.WeatherInfoDlgFragment;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.DemReaderUtils;
import com.ew.autofly.utils.FontUtils;
import com.ew.autofly.utils.GpsUtils2;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.LogUtilsOld;
import com.ew.autofly.utils.NoFlyZoneDataUtils;
import com.flycloud.autofly.base.util.SysUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.arcgis.ArcgisMapUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.utils.tool.ImageUtils;
import com.ew.autofly.widgets.CustomSeekbar.CrystalRangeSeekbar;
import com.ew.autofly.widgets.controls.ImageMarkerSymbol;
import com.ew.autofly.widgets.dji.DjiFPVOverlayWidget;
import com.ew.autofly.widgets.dji.DjiFPVWidget;
import com.ew.autofly.xflyer.utils.CommonConstants;
import com.ew.autofly.xflyer.utils.CoordinateUtils;
import com.ew.autofly.xflyer.utils.GaodeMapLayer;
import com.ew.autofly.xflyer.utils.GoogleMapLayer;
import com.ew.autofly.xflyer.utils.LatLngUtils;
import com.ew.autofly.xflyer.utils.OpenCycleMapLayer;
import com.flycloud.autofly.base.framework.aop.annotation.SingleClick;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;
import com.flycloud.autofly.map.MapRegion;
import com.flycloud.autofly.map.MapServiceProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import dji.common.bus.UXSDKEventBus;
import dji.common.flightcontroller.flyzone.SubFlyZoneInformation;
import dji.common.model.LocationCoordinate2D;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.thirdparty.rx.Observable;
import dji.thirdparty.rx.Subscriber;
import dji.thirdparty.rx.Subscription;
import dji.thirdparty.rx.android.schedulers.AndroidSchedulers;
import dji.thirdparty.rx.functions.Func1;
import dji.thirdparty.rx.schedulers.Schedulers;
import dji.thirdparty.rx.subscriptions.CompositeSubscription;
import dji.ux.internal.Events;
import dji.ux.panel.PreFlightCheckListPanel;
import dji.ux.panel.SpeakerPanel;
import dji.ux.panel.SpotlightPanel;
import dji.ux.widget.FPVWidget;
import dji.ux.widget.HistogramWidget;
import dji.ux.widget.RadarWidget;
import dji.ux.widget.dashboard.DashboardWidget;

import static com.ew.autofly.xflyer.utils.CoordinateUtils.gps84ToMapMercator;
import static com.ew.autofly.xflyer.utils.CoordinateUtils.lonLatToMercator;

/**
 * Created by Administrator on 2016/3/2.
 * 代码耦合程度过高，不宜再使用，请使用{@link com.ew.autofly.struct.controller.BaseMapFragment}代替
 */
@Deprecated
public abstract class BaseMapFragment extends BaseFragment implements View.OnClickListener,
        OnGetNoFlyZoneDataListener, View.OnLongClickListener, KMLFragmentListener {

    protected final float DEFAULT_LINE_WIDTH = 4.0F;

    public static class FPVStatus {
        public final static int min = 0;
        public final static int twice = 1;
        public final static int max = 2;
    }

    private int mFPVStatus = FPVStatus.min;

    public int getFPVStatus() {
        return mFPVStatus;
    }

    private MapServiceProvider mMapServiceProvider = MapServiceProvider.GOOGLE_MAP;
    private MapRegion mMapRegion = MapRegion.CHINA;
    public AppConstant.OperationMode currentMode = AppConstant.OperationMode.LinePatrolVideo;
    public CommonConstants.TiltStep currentTiltStep = CommonConstants.TiltStep.Step1;
    public List<WayPointTask> wayPointTaskList = new ArrayList<>();
    public boolean bDrawPath = false;
    public String snapshotName;

    public Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPosition = new Hashtable<>();
    public GraphicsLayer mVertexLayer;
    public GraphicsLayer mIrregularPolygonLayer = null;
    public GraphicsLayer mParallelLayer = null;


    public List<WayPointTask> mStartingLine = new ArrayList<>();

    public ArrayList<LatLngInfo> mPointCache;
    public ArrayList<LatLngInfo> mPointCacheReverse;

    public ArrayList<Point> mHotPointCache;
    public Polygon polygonRect;
    public float rotating;
    public LocationCoordinate2D mHotPointInterest;


    public double fullImageFlyTime = 0;

    public ArrayList<LatLngInfo> mBeltPointCache;
    public ArrayList<LatLngInfo> mBeltWayPointCache;
    public ArrayList<LatLngInfo> mBeltGps84Cache;


    public ArrayList<LatLngInfo> mTiltPointCache;
    public ArrayList<LatLngInfo> mTiltWayPointCache;
    public ArrayList<LatLngInfo> mTiltGps84Cache;


    public ArrayList<LatLngInfo> mStandMerLatLngInfo;
    public List<WayPointTask> mStandWayPointCache;


    protected ViewGroup ly_base_geo_region, ly_base_map_and_fpv, ly_fpv, ly_fpv_camera_setting,
            ly_camera_min_window_operation_layer, ly_base_operation_control_visible;
    protected RelativeLayout ly_base_operation;
    protected RelativeLayout ly_additional_operation;

    protected View view;
    protected MapView mapView;
    protected SharedConfig config;
    protected LogUtilsOld log = null;
    protected DataBaseUtils mDB = null;

    private LatLngInfo currentLntLng;
    protected RelativeLayout mLlBottom;
    protected LinearLayout mLlTilt;
    public ImageButton ibMapMode,
            ibGoHome, ibKML, ibChooseTower, ibLocation, ibLoadTask, ibSaveTask,
            ibSetting, ibDrawCircle, ibUndo, ibRedo, ibLoadTaskClear, ibMore;
    public Button ibFlight, ibPause, ibResume, ibVerify;
    protected LinearLayout llAddNewTower, llKml, llDrawCircle;
    private HistogramWidget histogram;

    protected ImageView ivShowCompass;
    protected DashboardWidget mCompassWidget;
    protected LinearLayout llLeftButton;
    protected LinearLayout rlRightButton;
    protected BaseProduct mProduct = null;
    protected RelativeLayout rlFPV;
    protected RelativeLayout rlBase;

    protected DjiFPVWidget mFPVWidget;


    protected PreFlightCheckListPanel mFlightCheck;
    protected DjiFPVOverlayWidget mFPVOverlayWidget;
    private RadarWidget mRadarWidget;

    private RelativeLayout vsFPVCameraSetting;
    protected ImageView mCameraBorder;
    protected int ratioWidth = 16, ratioHeight = 9;
    protected ImageView ivWeatherLoc, ivNarrowFPV;
    protected LinearLayout llAssistantFunction;
    private ImageButton tvWeatherDetail;
    private ImageButton tvNavigation;
    private ImageButton tvNoFlyZone;
    private TextView tvAltitude;

    protected TiledServiceLayer mGoogleImageMapLayer = null;
    protected TiledServiceLayer mGoogleRoadMapLayer = null;
    protected TiledServiceLayer mGoogleVectorMapLayer = null;
    protected GraphicsLayer rectangle = null;
    protected GraphicsLayer ellipse = null;
    protected GraphicsLayer mGpsLayer = null;
    protected String mapType = "Vector";

    protected boolean bHasTask = false;
    protected TextView tvBottomInfo;
    protected CrystalRangeSeekbar customSeekBar;


    protected GraphicsLayer startFlagLayer;

    protected GraphicsLayer endFlagLayer;

    protected GraphicsLayer flightPointTextLayer;

    protected ArrayList<String> mSelectedKmlFileIds = new ArrayList<>();
    protected GraphicsLayer polyLine = null;
    protected GraphicsLayer runLine = null;

    protected GraphicsLayer homeLayer;

    protected GraphicsLayer lineBetweenHomeAndDroneLayer;

    protected GraphicsLayer droneLayer;

    protected GraphicsLayer runTaskLayer;

    protected GraphicsLayer operateMarker;


    protected GraphicsLayer mReviewMissionLayer = null;

    protected GraphicsLayer mNoFlyZoneLayer = null;

    protected GraphicsLayer mKmlPolygonLayer, mKmlPolylineLayer, mKmlPointLayer, mKmlAnnoLayer, mKmlDotLayer;

    private boolean clickOnCameraWindow = false;

    public BaseCollectFragment flyCollectFragment;


    protected ArrayList<String> selectedMissionBatchIdList = new ArrayList<>();

    private double lastX, lastY;
    protected boolean CAMERA_MAP_SWITCH = false;
    private boolean bFirstLocate = true;
    public boolean bResumeFly = false;

    public double BottomInfoMeters;
    public boolean isCanTouchMap = true;
    private boolean isShowWeather = false;

    private boolean isRadarDisplayEnable;

    private boolean isFpvGirdLineEnable;


    public ArrayList<LatLngInfo> polygonMerList = new ArrayList<>();

    protected BaseProgressDialog mLoadProgressDlg;

    protected CompositeSubscription mKmlRxSubscription;


    public int currentTouchEvent = MotionEvent.ACTION_UP;


    protected DemReaderUtils demReaderUtils = null;

    public ImageMarkerSymbol mPointSymbol = null;
    public ImageMarkerSymbol mEditPointSymbol = null;
    public ImageMarkerSymbol mCenterPointSymbol = null;
    public SimpleLineSymbol mPolylineSymbol = null;
    public SimpleLineSymbol mEditPolylineSymbol = null;

    public SpotlightPanel spotlightPanel;
    public SpeakerPanel speakerPanel;
    public ImageButton imgSpotlight;
    public ImageButton imgSpeaker;
    public ImageButton imgBeacon;
    public TextView mTvPsdkNotify;


    private void addGpsArrowOnMap(Point gps) {
        if (ArcgisMapUtil.isInitialized()) {
            if (null != mGpsLayer && mGpsLayer.getNumberOfGraphics() < 1) {
                ImageMarkerSymbol symbol = ArcgisMapUtil.getImageMarkerSymbol(getContext(), R.drawable.gpspoint);
                Graphic graphic = new Graphic(gps, symbol);
                mGpsLayer.addGraphic(graphic);
            } else if (null != mGpsLayer) {
                int id = mGpsLayer.getGraphicIDs()[0];
                double x = gps.getX();
                double y = gps.getY();
                if (lastX == x && lastY == y)
                    return;
                lastX = x;
                lastY = y;
                mGpsLayer.updateGraphic(id, new Point(x, y));
            }
        }
    }

    public void addHomePointOnMap(double lat, double lon) {
        if (ArcgisMapUtil.isInitialized()) {
            LatLngInfo homePoint = new LatLngInfo(lat, lon);

            ArcgisMapUtil.updateMarkerToMapByGps84(getContext(), homeLayer, homePoint, R.drawable.ic_map_homepoint);
        }
    }

    public void addLineBetweenDroneAndHome(double homeLat, double homeLon, double droneLat, double droneLon) {
        if (ArcgisMapUtil.isInitialized()) {
            if (LocationCoordinateUtils.checkGpsCoordinate(homeLat, homeLon) && LocationCoordinateUtils.checkGpsCoordinate(droneLat, droneLon)) {
                LatLngInfo homeMer = gps84ToMapMercator(homeLon, homeLat);
                LatLngInfo droneMer = gps84ToMapMercator(droneLon, droneLat);
                Polyline line = new Polyline();
                line.startPath(homeMer.longitude, homeMer.latitude);
                line.lineTo(droneMer.longitude, droneMer.latitude);
                if (lineBetweenHomeAndDroneLayer != null && lineBetweenHomeAndDroneLayer.getNumberOfGraphics() > 0) {
                    int id = lineBetweenHomeAndDroneLayer.getGraphicIDs()[0];
                    lineBetweenHomeAndDroneLayer.updateGraphic(id, line);
                } else if (lineBetweenHomeAndDroneLayer != null) {
                    SimpleLineSymbol mLineSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), Color.RED, DEFAULT_LINE_WIDTH);
                    lineBetweenHomeAndDroneLayer.addGraphic(new Graphic(line, mLineSymbol));
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = LogUtilsOld.getInstance(getActivity()).setTag("BaseMapFragment");
        config = new SharedConfig(EWApplication.getInstance());
        try {
            mDB = DataBaseUtils.getInstance(EWApplication.getInstance());
        } catch (Exception e) {
            log.e(e.getMessage());
        }
        ArcGISRuntime.setClientId(AppConstant.ARCGIS_CLIENT_ID);
        mProduct = EWApplication.getProductInstance();

        mKmlRxSubscription = new CompositeSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_map_google, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSaveAndLoadTaskBtn();
    }

    
    private void initSaveAndLoadTaskBtn() {
        ibSaveTask.setVisibility(checkSaveTaskMode() ? View.VISIBLE : View.GONE);
        ibSaveTask.setEnabled(false);
        ibLoadTask.setVisibility(checkLoadTaskMode() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.unpause();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.pause();
    }

    @Override
    public void onDestroy() {

        GpsUtils2.stopLocationClient(false);
        if (null != mapView)
            mapView.removeAll();

        this.mKmlPolygonLayer = null;
        this.mKmlPolylineLayer = null;
        this.mKmlPointLayer = null;
        this.mKmlAnnoLayer = null;
        this.mKmlDotLayer = null;

        this.droneLayer = null;
        this.lineBetweenHomeAndDroneLayer = null;
        this.homeLayer = null;
        this.mGoogleImageMapLayer = null;
        this.mGoogleVectorMapLayer = null;
        this.mGoogleRoadMapLayer = null;
        this.rectangle = null;
        this.ellipse = null;
        this.polyLine = null;

        this.mGpsLayer = null;
        this.startFlagLayer = null;
        this.endFlagLayer = null;
        this.operateMarker = null;
        this.mVertexLayer = null;
        this.mIrregularPolygonLayer = null;
        this.mParallelLayer = null;
        this.flightPointTextLayer = null;
        this.runTaskLayer = null;
        this.runLine = null;
        this.mapView = null;
        this.mReviewMissionLayer = null;
        this.mNoFlyZoneLayer = null;

        if (mKmlRxSubscription != null) {
            mKmlRxSubscription.clear();
        }

        tearDownKeys();

        super.onDestroy();
    }

    protected void initMap(View view) {

        ly_base_geo_region = view.findViewById(R.id.ly_base_geo_region);
        ly_base_map_and_fpv = view.findViewById(R.id.ly_base_map_and_fpv);
        ly_fpv = view.findViewById(R.id.ly_fpv);
        ly_fpv_camera_setting = view.findViewById(R.id.ly_camera_and_setting);
        ly_camera_min_window_operation_layer = view.findViewById(R.id.ly_camera_min_window_operation_layer);

        ly_base_operation = (RelativeLayout) view.findViewById(R.id.ly_base_operation);
        ly_base_operation_control_visible = view.findViewById(R.id.ly_base_operation_control_visible);
        ly_additional_operation = (RelativeLayout) view.findViewById(R.id.ly_additional_operation);


        rlBase = (RelativeLayout) view.findViewById(R.id.rl_base);
        mFlightCheck = (PreFlightCheckListPanel) view.findViewById(R.id.perflight_check);
        mFPVWidget = view.findViewById(R.id.fpv_live);

        mFPVOverlayWidget = (DjiFPVOverlayWidget) view.findViewById(R.id.fpv_overlay);

        mRadarWidget = view.findViewById(R.id.radar);

        mapView = (MapView) view.findViewById(R.id.googleMap);
        histogram = (HistogramWidget) view.findViewById(R.id.histogram);
        mLlBottom = (RelativeLayout) view.findViewById(R.id.ll_bottom);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLlBottom.getLayoutParams();
        layoutParams.setMarginStart(AppConstant.ScreenWidth * 3 / ratioHeight);
        mLlBottom.setLayoutParams(layoutParams);

        mLlTilt = (LinearLayout) view.findViewById(R.id.ll_tilt);


        tvBottomInfo = (TextView) view.findViewById(R.id.tv_bottom_info);
        llLeftButton = (LinearLayout) view.findViewById(R.id.llLeftButton);
        rlRightButton = (LinearLayout) view.findViewById(R.id.ll_right_button);

        llKml = (LinearLayout) view.findViewById(R.id.ll_kml_tower);
        llDrawCircle = (LinearLayout) view.findViewById(R.id.ll_draw_polygon);

        ivShowCompass = (ImageView) view.findViewById(R.id.iv_show_compass);
        ivShowCompass.setVisibility(View.VISIBLE);

        mCompassWidget = view.findViewById(R.id.compass);

        ibKML = (ImageButton) view.findViewById(R.id.imgBtnKML);
        ibChooseTower = (ImageButton) view.findViewById(R.id.ib_choose_tower);
        ibLocation = (ImageButton) view.findViewById(R.id.imgBtnLocation);
        ibMapMode = (ImageButton) view.findViewById(R.id.imgMapMode);
        ibFlight = (Button) view.findViewById(R.id.imgBtnFlight);
        ibPause = (Button) view.findViewById(R.id.imgBtnPause);
        ibResume = (Button) view.findViewById(R.id.imgBtnResume);
        ibGoHome = (ImageButton) view.findViewById(R.id.imgBtnGoHome);
        ibLoadTask = (ImageButton) view.findViewById(R.id.imgBtnLoadTask);
        ibSaveTask = (ImageButton) view.findViewById(R.id.imgBtnSaveTask);
        ibSetting = (ImageButton) view.findViewById(R.id.imgBtnSettings);
        ibDrawCircle = (ImageButton) view.findViewById(R.id.imgBtnCircle);
        ibUndo = (ImageButton) view.findViewById(R.id.ib_undo);
        ibRedo = (ImageButton) view.findViewById(R.id.ib_redo);
        ibLoadTaskClear = (ImageButton) view.findViewById(R.id.imgBtnLoadTaskClear);
        ibMore = (ImageButton) view.findViewById(R.id.ib_more);
        ibVerify = (Button) view.findViewById(R.id.imgBtnCalibration);
        llAssistantFunction = (LinearLayout) view.findViewById(R.id.ll_assistant_function);
        tvWeatherDetail = (ImageButton) view.findViewById(R.id.tv_weather_detail);
        tvNavigation = (ImageButton) view.findViewById(R.id.tv_navigation);
        tvNoFlyZone = (ImageButton) view.findViewById(R.id.tv_noflyzone);
        tvAltitude = (TextView) view.findViewById(R.id.tv_altitude);
        ivWeatherLoc = (ImageView) view.findViewById(R.id.iv_weather_loc);
        ivNarrowFPV = (ImageView) view.findViewById(R.id.iv_narrow_fpv);
        customSeekBar = (CrystalRangeSeekbar) view.findViewById(R.id.sb_starting_point);

        speakerPanel = view.findViewById(R.id.speaker_panel);
        spotlightPanel = view.findViewById(R.id.spotlight_panel);
        imgSpeaker = view.findViewById(R.id.tv_speaker);
        imgSpotlight = view.findViewById(R.id.tv_spotlight);
        imgBeacon = view.findViewById(R.id.tv_beacon);
        imgSpeaker.setOnClickListener(this);
        imgSpotlight.setOnClickListener(this);
        imgBeacon.setOnClickListener(this);

        ibMore.setOnClickListener(this);
        ibUndo.setOnClickListener(this);
        ibRedo.setOnClickListener(this);
        ibFlight.setOnClickListener(this);
        ibMapMode.setOnClickListener(this);
        ibSaveTask.setOnClickListener(this);
        ly_fpv.setOnClickListener(this);
        tvWeatherDetail.setOnClickListener(this);
        tvNavigation.setOnClickListener(this);
        tvNoFlyZone.setOnClickListener(this);
        ibLoadTask.setOnClickListener(this);
        ibLoadTaskClear.setOnClickListener(this);
        if (checkDrawCircleDoubleClickMode()) {
            ibDrawCircle.setOnClickListener(this);
            ibDrawCircle.setOnLongClickListener(this);
        } else {
            ibDrawCircle.setOnClickListener(this);
        }
        ibSetting.setOnClickListener(this);
        ibKML.setOnClickListener(this);
        ibChooseTower.setOnClickListener(this);
        ibLocation.setOnClickListener(this);
        ibLocation.setOnLongClickListener(this);
        ibPause.setOnClickListener(this);
        ibResume.setOnClickListener(this);
        ibGoHome.setOnClickListener(this);
        ivShowCompass.setOnClickListener(this);
        mCompassWidget.setOnClickListener(this);
        ivNarrowFPV.setOnClickListener(this);

        initMyMap();

        initMapGraphicsLayer();

        if (bHasTask)
            SetFlightButton(true);
        else
            SetFlightButton(false);

        mapView.setOnSingleTapListener(new MapSingleTapListener());

        mapView.setOnStatusChangedListener(new MapStatusChangedListener());

        mapView.setOnZoomListener(new MapZoomListener());


        demReaderUtils = DemReaderUtils.getInstance(getContext());


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
        rlBase.addView(mTvPsdkNotify);

        initFPVDefault();

        setUpKeys();

        initViewConfig();
    }

    private void initMyMap() {
        Object mapServiceProvider = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.MAP_SERVICE_PROVIDER));
        mMapServiceProvider = MapServiceProvider.find((int) mapServiceProvider);

        Object mapRegion = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.MAP_REGION));
        mMapRegion = MapRegion.find((int) mapRegion);

        LocationCoordinateUtils.transformMapCoordinateType(mMapServiceProvider, mMapRegion);
        AppConstant.mapCoordinateType = LocationCoordinateUtils.MAP_COORDINATE_SYSTEM;
        changeMap();
    }

    
    public void refreshLayerVisibility() {
        if (getActivity() != null) {
            if (mapView.getScale() < 20000 / ArcgisMapUtil.getEqualFactor(getActivity())) {
                mKmlPointLayer.setVisible(true);
                mKmlAnnoLayer.setVisible(true);
                mKmlDotLayer.setVisible(false);
            } else if (mapView.getScale() < 50000 / ArcgisMapUtil.getEqualFactor(getActivity())) {
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

    
    protected void initMapGraphicsLayer() {

        mGpsLayer = new GraphicsLayer();
        mapView.addLayer(mGpsLayer);

        mKmlPolygonLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mKmlPolygonLayer);

        mKmlPolylineLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mKmlPolylineLayer);

        mKmlPointLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mKmlPointLayer);

        mKmlDotLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mKmlDotLayer);

        mKmlAnnoLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mKmlAnnoLayer);

        rectangle = new GraphicsLayer();
        mapView.addLayer(rectangle);

        ellipse = new GraphicsLayer();
        mapView.addLayer(ellipse);

        polyLine = new GraphicsLayer();
        mapView.addLayer(polyLine);

        runLine = new GraphicsLayer();
        mapView.addLayer(runLine);

        runTaskLayer = new GraphicsLayer();
        mapView.addLayer(runTaskLayer);

        flightPointTextLayer = new GraphicsLayer();
        mapView.addLayer(flightPointTextLayer);

        lineBetweenHomeAndDroneLayer = new GraphicsLayer();
        mapView.addLayer(lineBetweenHomeAndDroneLayer);

        homeLayer = new GraphicsLayer();
        mapView.addLayer(homeLayer);

        mReviewMissionLayer = new GraphicsLayer();
        mapView.addLayer(mReviewMissionLayer);

        mNoFlyZoneLayer = new GraphicsLayer();
        mapView.addLayer(mNoFlyZoneLayer);

        operateMarker = new GraphicsLayer();
        mapView.addLayer(operateMarker);

        mIrregularPolygonLayer = new GraphicsLayer();
        mapView.addLayer(mIrregularPolygonLayer);

        mParallelLayer = new GraphicsLayer();
        mapView.addLayer(mParallelLayer);

        mVertexLayer = new GraphicsLayer();
        mapView.addLayer(mVertexLayer);

        startFlagLayer = new GraphicsLayer();
        mapView.addLayer(startFlagLayer);

        endFlagLayer = new GraphicsLayer();
        mapView.addLayer(endFlagLayer);

        droneLayer = new GraphicsLayer();
        mapView.addLayer(droneLayer);
    }

    public void paintPoint() {

    }

    public void refreshEllipse(int angleTimes) {

    }

    public void repaintTaskPath() {
    }

    public void refreshBottomInfo() {
    }

    public void refreshFlightLine(int indexStartPoint, int indexEndPoint) {

    }

    public void refreshChildInfo() {

    }

    @Override
    public void onSelectKMLComplete(String tag, ArrayList<String> selectIdList) {

        this.mSelectedKmlFileIds = selectIdList;



        if (mKmlPolygonLayer != null) {
            mKmlPolygonLayer.removeAll();
        }

        if (mKmlPolylineLayer != null) {
            mKmlPolylineLayer.removeAll();
        }

        if (mKmlPointLayer != null) {
            mKmlPointLayer.removeAll();
        }

        if (mKmlAnnoLayer != null) {
            mKmlAnnoLayer.removeAll();
        }

        if (mKmlDotLayer != null) {
            mKmlDotLayer.removeAll();
        }

        Subscription subscription = Observable.from(this.mSelectedKmlFileIds).concatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String fileId) {
                return getMercatorPolygonsByFiledId(fileId);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .takeLast(1)
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onStart() {
                        showLoadProgressDialog("正在绘制图像……");
                    }

                    @Override
                    public void onCompleted() {
                        dismissLoadProgressDialog();
                        refreshLayerVisibility();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dismissLoadProgressDialog();
                    }

                    @Override
                    public void onNext(Boolean flag) {
                        dismissLoadProgressDialog();
                    }

                });

        mKmlRxSubscription.add(subscription);

    }

    private Observable<Boolean> getMercatorPolygonsByFiledId(final String fileId) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {

            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {

                mDB.getMercatorPolygonsByFiledId(fileId, new DataBaseUtils.onExecResult() {
                    @Override
                    public void execResult(boolean succ, String errStr) {

                    }

                    @Override
                    public void execResultWithResult(boolean succ, Object result, String errStr) {
                        if (succ) {

                            final List<List<LatLngInfo>> allPolygonsList = (List<List<LatLngInfo>>) result;
                            if (allPolygonsList != null && !allPolygonsList.isEmpty()) {

                                SimpleLineSymbol lineSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), Color.RED, 2.0F);

                                SimpleFillSymbol mPolygonSymbol = new SimpleFillSymbol(0x1000ee00);
                                mPolygonSymbol.setOutline(lineSymbol);

                                Graphic[] graphics;

                                graphics = new Graphic[allPolygonsList.size()];

                                for (int count = 0; count < allPolygonsList.size(); count++) {

                                    List<LatLngInfo> mercatorList = allPolygonsList.get(count);

                                    if (mercatorList != null) {
                                        Polygon polygon = new Polygon();

                                        for (int i = 0; i < mercatorList.size(); i++) {

                                            LatLngInfo mercator = mercatorList.get(i);

                                            if (i == 0) {
                                                polygon.startPath(mercator.longitude, mercator.latitude);
                                            } else {
                                                polygon.lineTo(mercator.longitude, mercator.latitude);
                                            }
                                        }

                                        graphics[count] = new Graphic(polygon, mPolygonSymbol);
                                    }
                                }

                                mKmlPolygonLayer.addGraphics(graphics);


                                if (getActivity() != null) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (allPolygonsList.get(0) != null) {
                                                LatLngInfo firstPoint = allPolygonsList.get(0).get(0);
                                                if (firstPoint != null) {
                                                    mapView.centerAt(new Point(firstPoint.longitude, firstPoint.latitude), true);
                                                }
                                            }

                                        }
                                    });
                                }

                            }



                        } else {

                        }

                        getMercatorPolylinesByFieldId(fileId, subscriber);


                    }

                    @Override
                    public void setExecCount(int i, int count) {

                    }
                });
            }
        }).subscribeOn(Schedulers.io());
    }

    private void getMercatorPolylinesByFieldId(final String fileId, final Subscriber<? super Boolean> subscriber) {


        mDB.getMercatorPolylinesByFieldId(fileId, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(boolean succ, String errStr) {

            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {
                if (succ) {

                    final List<List<LatLngInfo>> allPolylinesList = (List<List<LatLngInfo>>) result;
                    if (allPolylinesList != null && !allPolylinesList.isEmpty()) {

                        SimpleLineSymbol lineSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), Color.RED, 2.0F);

                        Graphic[] graphics;

                        graphics = new Graphic[allPolylinesList.size()];

                        for (int count = 0; count < allPolylinesList.size(); count++) {

                            List<LatLngInfo> mercatorList = allPolylinesList.get(count);

                            if (mercatorList != null) {
                                Polyline polygonLine = new Polyline();

                                for (int i = 0; i < mercatorList.size(); i++) {

                                    LatLngInfo mercator = mercatorList.get(i);

                                    if (i == 0) {
                                        polygonLine.startPath(mercator.longitude, mercator.latitude);
                                    } else {
                                        polygonLine.lineTo(mercator.longitude, mercator.latitude);
                                    }
                                }

                                graphics[count] = new Graphic(polygonLine, lineSymbol);
                            }
                        }

                        mKmlPolylineLayer.addGraphics(graphics);


                        if (getActivity() != null) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (allPolylinesList.get(0) != null) {
                                        LatLngInfo firstPoint = allPolylinesList.get(0).get(0);
                                        if (firstPoint != null) {
                                            mapView.centerAt(new Point(firstPoint.longitude, firstPoint.latitude), true);
                                        }
                                    }

                                }
                            });
                        }

                    }



                } else {

                }

                getPointsByFileId(fileId, subscriber);



            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });

    }

    private void getPointsByFileId(final String fileId, final Subscriber<? super Boolean> subscriber) {

        mDB.getPointsByFileId(fileId, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(boolean succ, String errStr) {

            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {
                if (succ) {
                    final ArrayList<PointWithName> pointList = (ArrayList<PointWithName>) result;
                    int iPosition = 0;
                    LatLngInfo firstPoint = null;

                    ImageMarkerSymbol symbol = ArcgisMapUtil.getImageMarkerSymbol(getContext(), R.drawable.kmlpoint_google);
                    SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.WHITE,
                            (int) (12 * ArcgisMapUtil.getEqualFactor(getActivity())), SimpleMarkerSymbol.STYLE.CIRCLE);

                    for (PointWithName point : pointList) {

                        LatLngInfo position = CoordinateUtils.gps84ToMapMercator(point.getPoint().getX(), point.getPoint().getY());
                        if (iPosition == 0) {
                            firstPoint = new LatLngInfo(position.latitude, position.longitude);
                        }
                        iPosition++;

                        iPosition++;

                        Graphic graphicAnno;
                        if (BuildConfig.ARCGIS_TEXT_ENABLE) {
                            TextSymbol textSymbol = ArcgisMapUtil.getTextSymbol(getContext(), getResources().getDimensionPixelSize(R.dimen.flight_num_text_size), point.getName(), Color.BLUE);
                            textSymbol.setOffsetX(getResources().getDimension(R.dimen.location_name_offsetX));
                            graphicAnno = new Graphic(new Point(position.longitude, position.latitude), textSymbol);
                        } else {
                            PictureMarkerSymbol ps = ArcgisMapUtil.getImageMarkerSymbol(getContext(), FontUtils.getImage(getActivity(), 300, 36, point.getName(), 24));
                            ps.setOffsetX(getResources().getDimension(R.dimen.location_name_offsetX));
                            graphicAnno = new Graphic(new Point(position.longitude, position.latitude), ps);
                        }

                        Graphic graphic = new Graphic(new Point(position.longitude, position.latitude), symbol);
                        mKmlPointLayer.addGraphic(graphic);
                        mKmlAnnoLayer.addGraphic(graphicAnno);
                        mKmlDotLayer.addGraphic(new Graphic(new Point(position.longitude, position.latitude), simpleMarkerSymbol));
                    }

                    final LatLngInfo centerPoint = firstPoint;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (centerPoint != null) {
                                mapView.centerAt(new Point(centerPoint.longitude, centerPoint.latitude), true);
                            }
                        }
                    });

                }



                subscriber.onCompleted();
            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });

    }

    /**
     * 载入任务
     *
     * @param mission
     */
    public void loadMission(Mission2 mission) {
    }

    /**
     * 查看选中的任务
     *
     * @param selectIdList
     */
    public void reviewSelectedMissions(ArrayList<String> selectIdList) {
    }

    public abstract void showSettingDialog();

    protected void addPointListToMap(GraphicsLayer layer, List<Point> pointList, int textSize) {
        int i = 1;
        for (Point point : pointList) {
            TextSymbol symbol = ArcgisMapUtil.getTextSymbol(getContext(), textSize, String.valueOf(i), getResources().getColor(R.color.flight_number));
            symbol.setOffsetX(-ArcgisMapUtil.getEqualRatioFactor(getContext()) * 5.0f);
            Graphic textGraphic = new Graphic(point, symbol);
            layer.addGraphic(textGraphic);
            i++;
        }
    }

    /**
     * 实时更新飞机的位置信息
     *
     * @param droneLocationLat 飞机所在位置的纬度
     * @param droneLocationLng 飞机所在位置的经度
     * @param angle            飞机机头的方位
     */
    public void updateDroneLocation(double droneLocationLat, double droneLocationLng, double angle) {

        if (ArcgisMapUtil.isInitialized()) {

            if (droneLayer == null) {
                return;
            }

            if (LocationCoordinateUtils.checkGpsCoordinate(droneLocationLat, droneLocationLat)) {
                double droneYaw = angle > 0 ? angle : 360 + angle;

                LatLngInfo pCurrent = gps84ToMapMercator(droneLocationLng, droneLocationLat);
                Point point = new Point(pCurrent.longitude, pCurrent.latitude);
                if (droneLayer.getNumberOfGraphics() < 1) {
                    ImageMarkerSymbol symbol = ArcgisMapUtil.getImageMarkerSymbol(getContext(), R.drawable.ic_map_aircraft);
                    Graphic graphic = new Graphic(point, symbol);
                    droneLayer.addGraphic(graphic);
                    return;
                }
                int id = droneLayer.getGraphicIDs()[0];
                Graphic g = droneLayer.getGraphic(id);

                droneLayer.updateGraphic(id, point);
                PictureMarkerSymbol gpsPic = (PictureMarkerSymbol) g.getSymbol();

                gpsPic.setAngle((float) droneYaw);
                droneLayer.updateGraphic(id, gpsPic);
            }
        }
    }


    public void drawTextAnnos(GraphicsLayer layer, ArrayList<LatLngInfo> gps84) {
        double perLen, totalLen = 0;
        for (int i = 1; i < gps84.size(); i++) {
            LatLngInfo p11 = gps84.get(i - 1);
            LatLngInfo p21 = gps84.get(i);

            perLen = LatLngUtils.getDistance(p11.latitude, p11.longitude, p21.latitude, p21.longitude);
            totalLen += perLen;

            String txt = totalLen > 1000 ?
                    String.format("%.2fkm", perLen / 1000) + "/" + String.format("%.2fkm", totalLen / 1000) :
                    String.format("%.2fm", perLen) + "/" + String.format("%.2fm", totalLen);
            TextSymbol symbol = ArcgisMapUtil.getTextSymbol(getContext(), (int) getResources().getDimension(R.dimen.map_app_text_size), txt, Color.RED);
            symbol.setOffsetX(-1 * ArcgisMapUtil.getEqualFactor(getContext()) * SysUtils.dip2px(getActivity(), txt.length() * 2));
            symbol.setOffsetY(ArcgisMapUtil.getEqualFactor(getContext()) * -50);
            LatLngInfo p2 = gps84ToMapMercator(p21.longitude, p21.latitude);
            Graphic graphic = new Graphic(new Point(p2.longitude, p2.latitude), symbol);
            layer.addGraphic(graphic);
        }
    }

    
    public void screenShot() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        snapshotName = sdf.format(new Date()) + ".webp";

        new Thread(new Runnable() {
            @Override
            public void run() {

                mapView.clearFocus();
                mapView.setPressed(false);
                Bitmap cacheBitmap = null;

                try {

                    boolean willNotCache = mapView.willNotCacheDrawing();
                    mapView.setWillNotCacheDrawing(false);
                    int color = mapView.getDrawingCacheBackgroundColor();
                    mapView.setDrawingCacheBackgroundColor(0);
                    if (color != 0) {
                        mapView.destroyDrawingCache();
                    }
                    mapView.buildDrawingCache();

                    while (cacheBitmap == null) {
                        cacheBitmap = mapView.getDrawingMapCache(0, 0, mapView.getWidth(), mapView.getHeight());
                    }

                    ImageUtils.saveScreenShot(cacheBitmap, IOUtils.getRootStoragePath(getActivity())
                            + AppConstant.DIR_MISSION_PHOTO, snapshotName);

                    mapView.destroyDrawingCache();
                    mapView.setWillNotCacheDrawing(willNotCache);
                    mapView.setDrawingCacheBackgroundColor(color);


                    mapView.zoomin();
                    mapView.zoomout();

                } catch (OutOfMemoryError | Exception error) {
                    error.printStackTrace();
                } finally {

                    if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
                        cacheBitmap.recycle();
                        cacheBitmap = null;
                    }
                    System.gc();
                }

            }
        }).start();
    }

    /**
     * 刷新航飞任务的几个按钮
     *
     * @param action
     */
    public void refreshFlightButton(AppConstant.FlightAction action) {
        try {
            switch (action) {
                case StartTask:
                case ResumeTask:
                    setFlightButtonState(false, true, false, true);
                    if (isShowWeather)
                        ibMore.performClick();
                    break;
                case PauseTask:
                    setFlightButtonState(false, false, true, true);
                    break;
                case GoHomeTask:
                case Unknown:
                    setFlightButtonState(true, false, false, false);
                    break;
            }
        } catch (Exception e) {
            log.e(e.getMessage());
        }
    }

    protected void setFlightButtonState(boolean flight, boolean pause, boolean resume, boolean goHome) {

        int mode = config.getMode();

        ibFlight.setVisibility(flight ? View.VISIBLE : View.GONE);
        ibPause.setVisibility(pause ? View.VISIBLE : View.GONE);
        ibResume.setVisibility(resume ? View.VISIBLE : View.GONE);
        ibGoHome.setVisibility(goHome ? View.VISIBLE : View.GONE);
        llDrawCircle.setVisibility(!goHome ? View.VISIBLE : View.GONE);
        llKml.setVisibility(!goHome ? View.VISIBLE : View.GONE);
        ibSetting.setVisibility(!goHome ? View.VISIBLE : View.GONE);
        ibMore.setVisibility(!goHome ? View.VISIBLE : View.GONE);

        if (checkSaveTaskMode()) {
            ibSaveTask.setVisibility(!goHome ? View.VISIBLE : View.GONE);
        }

        if (checkLoadTaskMode()) {
            ibLoadTask.setVisibility(!goHome ? View.VISIBLE : View.GONE);
        }


        ibLoadTaskClear.setVisibility(!flight ? View.VISIBLE : View.GONE);
        if (goHome || mReviewMissionLayer.getNumberOfGraphics() == 0) {
            ibLoadTaskClear.setVisibility(View.GONE);
        } else {
            ibLoadTaskClear.setVisibility(View.VISIBLE);
            if (mReviewMissionLayer.getNumberOfGraphics() == 0) {
                ibLoadTaskClear.setVisibility(View.GONE);
            }
        }

        if (mode == 9) {
            if (flyCollectFragment.RiverMode == AppConstant.RiverMissionMode.RiverPatrolStudy) {
                ibLoadTask.setVisibility(View.GONE);
                ibSaveTask.setVisibility(View.GONE);
                ibFlight.setVisibility(View.GONE);
            } else if (flyCollectFragment.RiverMode == AppConstant.RiverMissionMode.RiverPatrolMode) {
                ibLoadTask.setVisibility(!goHome ? View.VISIBLE : View.GONE);
            } else {
                ibSaveTask.setVisibility(!goHome ? View.VISIBLE : View.GONE);
                ibLoadTask.setVisibility(!goHome ? View.VISIBLE : View.GONE);
            }
        }

        if (mode == 15) {
            if (flyCollectFragment.mStandMode == 0) {
                ibLoadTask.setVisibility(!goHome ? View.VISIBLE : View.GONE);
                ibSaveTask.setVisibility(View.GONE);
            } else {
                ibLoadTask.setVisibility(View.GONE);
                ibSaveTask.setVisibility(View.VISIBLE);
            }
        }

        if (checkResumeFlyMode() && customSeekBar != null) {
            if (flyCollectFragment.operateAction == AppConstant.OperateAction.Unknown && bDrawPath) {
                customSeekBar.setVisibility(View.VISIBLE);
            } else {
                customSeekBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置起飞航纽
     *
     * @param bEnable
     */
    public void SetFlightButton(final boolean bEnable) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ibFlight.setEnabled(bEnable);
               /* if (bEnable) {
                    ibFlight.setBackgroundResource(R.drawable.btn_icon_start_task_selector);
                } else {
                    ibFlight.setBackgroundResource(R.drawable.icon_start_task_ban);
                }*/
            }
        });
    }

    
    protected void clearPaintPoint(boolean bRemoveAll) {
        if (this.runTaskLayer != null)
            this.runTaskLayer.removeAll();

        if (bRemoveAll && this.startFlagLayer != null)
            this.startFlagLayer.removeAll();

        if (bRemoveAll && this.endFlagLayer != null)
            this.endFlagLayer.removeAll();

        if (bRemoveAll && this.mVertexLayer != null)
            this.mVertexLayer.removeAll();

        if (bRemoveAll && this.mIrregularPolygonLayer != null)
            this.mIrregularPolygonLayer.removeAll();

        if (bRemoveAll && this.mParallelLayer != null)
            this.mParallelLayer.removeAll();

        if (this.flightPointTextLayer != null)
            this.flightPointTextLayer.removeAll();

        if (this.polyLine != null)
            this.polyLine.removeAll();

        if (bRemoveAll && this.rectangle != null)
            this.rectangle.removeAll();

        if (bRemoveAll && this.ellipse != null) {
            this.ellipse.removeAll();
        }

        if (runLine != null)
            this.runLine.removeAll();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLocation:
                FastLocationDialog dlg = new FastLocationDialog();
                dlg.setOnFastLocaionConfirm(new FastLocationDialog.OnFastLocationConfirm() {
                    @Override
                    public void onLocationConfirm(LatLngInfo latLngInfo) {
                        mapView.setScale((float) (mapView.getMinScale() / (Math.pow(2, 16))));
                        LatLngInfo mer = gps84ToMapMercator(latLngInfo);
                        mapView.centerAt(new Point(mer.longitude, mer.latitude), true);
                        refreshAltitude(latLngInfo);
                    }
                });
                dlg.show(getFragmentManager(), "fast_location");
                break;
            case R.id.imgBtnCircle:
                onLongClickDrawCircle();
                break;
        }
        return false;
    }


    @Override
    public void onClick(View v) {

        if (!checkActionFree())
            return;

        if (getParentFragment() instanceof CommonConstants.MapFragmentClickListener) {
            ((CommonConstants.MapFragmentClickListener) getParentFragment()).onMapFragmentClick(v);
        }

        onNormalClick(v);

        onSingleClick(v);
    }

    public void onNormalClick(View v) {
        switch (v.getId()) {
            case R.id.ib_more:
                if (clickOnCameraWindow) {
                    ToastUtil.show(getContext(), "请切换到地图模式");
                    return;
                }
                showWeatherDetail(!isShowWeather);
                break;
            case R.id.imgBtnCircle:
                onClickDrawCircle();
                break;
            case R.id.imgMapMode:







//

                ChooseMapTypePopupWindow mapTypePopupWindow = new ChooseMapTypePopupWindow(requireContext());
                mapTypePopupWindow.chooseMapTypeClickListener = map -> {
                    if (DJIMap.MAP_TYPE_NORMAL == map) {
                        mapType = "Vector";
                    } else {
                        mapType = "MixImage";
                    }
                    changeMap();
                };
                mapTypePopupWindow.showAsDropDown(ibMapMode);
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

    protected void onClickPauseMission(){
        flyCollectFragment.pauseMission();
    }

    protected void onClickResumeMission(){
        flyCollectFragment.resumeMission();
    }

    @SingleClick
    public void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnGoHome:
                flyCollectFragment.goHome();
                break;
            case R.id.imgBtnPause:

                onClickPauseMission();
                break;
            case R.id.imgBtnResume:

                onClickResumeMission();
                break;
            case R.id.imgBtnKML:
                if (!flyCollectFragment.checkTaskFree())
                    return;
                ImportExcelKmlDlgFragment importDlg = new ImportExcelKmlDlgFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("fileIds", mSelectedKmlFileIds);
                args.putBoolean("no_excel", true);
                importDlg.setArguments(args);
                importDlg.setKMLListener(this);
                importDlg.show(getFragmentManager(), "kml_excel");
                break;

            case R.id.imgBtnLocation:
                mProduct = EWApplication.getProductInstance();
                if (mProduct != null && mProduct.isConnected() &&
                        flyCollectFragment.aircraftLocationLatitude != -1 &&
                        LocationCoordinateUtils.checkGpsCoordinate(flyCollectFragment.aircraftLocationLatitude, flyCollectFragment.aircraftLocationLongitude)) {
                    centerAtDroneLocation();
                } else {
                    centerAtMobileLocation();
                }
                break;
            case R.id.ly_fpv:
                showWeatherDetail(false);
                if (mFPVStatus == FPVStatus.min) {
                    switchFPVTwice();
                } else if (mFPVStatus == FPVStatus.twice) {
                    switchFPVMax();
                }

                break;
            case R.id.iv_show_compass:

                if (mFPVStatus == FPVStatus.max) {
                    mapView.setVisibility(View.GONE);
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
                mapView.setVisibility(View.VISIBLE);
                ly_camera_min_window_operation_layer.setVisibility(View.VISIBLE);
                ly_fpv.setVisibility(View.VISIBLE);

                break;
            case R.id.tv_weather_detail:
                Point p = mapView.getCenter();
                LatLngInfo pCenter;
                if (mMapServiceProvider == MapServiceProvider.OPENCYCLE_MAP)
                    pCenter = CoordinateUtils.mercatorToGcj(p.getX(), p.getY());
                else
                    pCenter = CoordinateUtils.mercatorToLonLat(p.getX(), p.getY());

                WeatherInfoDlgFragment weatherInfoDlgFragment = new WeatherInfoDlgFragment();
                Bundle weatherArgs = new Bundle();
                weatherArgs.putSerializable("location", pCenter);
                weatherInfoDlgFragment.setArguments(weatherArgs);
                weatherInfoDlgFragment.show(getFragmentManager(), "weather");
                break;
            case R.id.tv_navigation:
                showMobileNavigationDialog();
                break;
            case R.id.imgBtnSettings:
                if (!(flyCollectFragment).checkTaskFree())
                    return;
                showSettingDialog();
                break;
            case R.id.imgBtnLoadTask:
                showLoadTaskDialog();
                break;
            case R.id.imgBtnLoadTaskClear:
                mReviewMissionLayer.removeAll();
                ibLoadTaskClear.setVisibility(View.GONE);
                selectedMissionBatchIdList.clear();
                break;
            case R.id.iv_narrow_fpv:
                switchFPVDefault();
                break;
            case R.id.tv_noflyzone:
                if (mProduct == null || !mProduct.isConnected()) {
                    ToastUtil.show(getActivity(), "请连接飞机");
                    return;
                }
                if (mNoFlyZoneLayer.getGraphicIDs() != null && mNoFlyZoneLayer.getGraphicIDs().length != 0) {
                    mNoFlyZoneLayer.removeAll();
                    return;
                }
                NoFlyZoneDataUtils flyZoneData = NoFlyZoneDataUtils.getInstance();
                flyZoneData.getNoFlyZoneData(getContext(), this);
                break;
        }
    }

    public void onLongClickDrawCircle() {

    }

    public void onClickDrawCircle() {

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
            Point pc = mapView.getCenter();
            double altitude = demReaderUtils.getZValue(CoordinateUtils.mapMercatorToGps84(pc.getX(), pc.getY()));
            tvAltitude.setText(demReaderUtils.checkZValue(altitude) ? String.format("海拔:%.0fm", altitude) : "海拔:N/A");
        }
    }

    protected void refreshAltitude(LatLngInfo info) {
        if (isShowWeather) {
            double altitude = demReaderUtils.getZValue(info);
            tvAltitude.setText(demReaderUtils.checkZValue(altitude) ? String.format("海拔:%.0fm", altitude) : "海拔:N/A");
        }
    }

    private void centerAtMobileLocation() {

        GpsUtils2.getCurrentLocation(new GpsUtils2.LocationListner() {
            @Override
            public void result(AMapLocation location) {
                if (location.getErrorCode() != 0) {
                    ToastUtil.show(getActivity(), "无法定位当前位置,请稍后再试");
                    location = GpsUtils2.getLastKnowLocaation();
                }

                if (location != null) {
                    LatLngInfo locate_gcj = new LatLngInfo(location.getLatitude(), location.getLongitude());
                    currentLntLng = CoordinateUtils.gcj_To_Gps84(locate_gcj.latitude, locate_gcj.longitude);
                    LatLngInfo mer = CoordinateUtils.gps84ToMapMercator(currentLntLng.longitude, currentLntLng.latitude);
                    Point gps = new Point(mer.longitude, mer.latitude);
                    addGpsArrowOnMap(gps);
                    mapView.setScale((float) (mapView.getMinScale() / (Math.pow(2, 16))));
                    mapView.centerAt(gps, true);
                    refreshAltitude(currentLntLng);
                }
            }
        });
    }

    private void centerAtDroneLocation() {
        LatLngInfo droneMercator = CoordinateUtils.gps84ToMapMercator(flyCollectFragment.aircraftLocationLongitude, flyCollectFragment.aircraftLocationLatitude);
        Point droneLocation = new Point(droneMercator.longitude, droneMercator.latitude);
        mapView.centerAt(droneLocation, true);
        mapView.setScale((float) (mapView.getMinScale() / (Math.pow(2, 16))));
        refreshAltitude(new LatLngInfo(flyCollectFragment.aircraftLocationLatitude,
                flyCollectFragment.aircraftLocationLongitude));
    }

    public void updateVideoFeed() {
        mFPVWidget.updateVideoFeed();
    }

    
    protected void switchFPVDefault() {
        initFPVDefault();

     /*   UXSDKEventBus.getInstance().post(new Events.FPVDimensionsEvent(AppConstant.ScreenWidth * 3 / ratioHeight,
                AppConstant.ScreenWidth * 3 / ratioWidth, FPVWidget.VideoSource.PRIMARY));*/

    }

    private void initFPVDefault() {

        ly_base_operation.setVisibility(View.VISIBLE);
        ly_base_operation_control_visible.setVisibility(View.VISIBLE);

        showResumeFlyBar();

        ly_fpv_camera_setting.setVisibility(View.GONE);
        ly_camera_min_window_operation_layer.setLayoutParams(getFpvMinLayoutParams());
        mRadarWidget.setVisibility(View.GONE);

        mFPVOverlayWidget.setTouchable(false);

        ly_base_map_and_fpv.bringChildToFront(ly_fpv);
        ly_fpv.setLayoutParams(getFpvMinLayoutParams());
        ly_base_geo_region.setLayoutParams(getFpvMaxLayoutParams());

        ly_fpv.setOnClickListener(this);

        ivNarrowFPV.setVisibility(View.GONE);

        mFPVStatus = FPVStatus.min;

        isCanTouchMap = true;
    }

    
    protected void switchFPVTwice() {

        ly_base_operation_control_visible.setVisibility(View.GONE);

        customSeekBar.setVisibility(View.INVISIBLE);

        ly_fpv.setLayoutParams(getFpvTwiceLayoutParams());
        ly_camera_min_window_operation_layer.setLayoutParams(getFpvTwiceLayoutParams());

        ivNarrowFPV.setVisibility(View.VISIBLE);

        mFPVStatus = FPVStatus.twice;

        isCanTouchMap = false;
    }

    
    protected void switchFPVMax() {

        ly_base_operation.setVisibility(View.GONE);
        ly_fpv_camera_setting.setVisibility(View.VISIBLE);

        customSeekBar.setVisibility(View.INVISIBLE);

        ly_camera_min_window_operation_layer.setLayoutParams(getFpvMinLayoutParams());

        if (isRadarDisplayEnable) {
            mRadarWidget.setVisibility(View.VISIBLE);
        }

        ly_base_map_and_fpv.bringChildToFront(ly_base_geo_region);
        ly_fpv.setLayoutParams(getFpvMaxLayoutParams());
        ly_base_geo_region.setLayoutParams(getFpvMinLayoutParams());

        ly_fpv.setOnClickListener(null);

        mFPVOverlayWidget.setTouchable(true);

        ivNarrowFPV.setVisibility(View.GONE);

        mFPVStatus = FPVStatus.max;

        isCanTouchMap = false;


        UXSDKEventBus.getInstance().post(new Events.FPVDimensionsEvent(ly_base_map_and_fpv.getWidth(),
                ly_base_map_and_fpv.getHeight(), FPVWidget.VideoSource.PRIMARY));

    }

    private void showResumeFlyBar() {
        if (bDrawPath) {
            if (checkResumeFlyMode())
                if (flyCollectFragment.operateAction != AppConstant.OperateAction.Unknown && flyCollectFragment.operateAction != AppConstant.OperateAction.FinishTask)
                    customSeekBar.setVisibility(View.INVISIBLE);
                else
                    customSeekBar.setVisibility(View.VISIBLE);
        }
    }


    protected void setBottomTextColor(double flyTime, String aircraftModel) {
        if (flyTime > 18 && (aircraftModel.contains("Inspire")
                || aircraftModel.contains("Phantom 4"))) {
            tvBottomInfo.setTextColor(Color.RED);
        } else if (flyTime > 15 && !aircraftModel.contains("Phantom 4")
                && !aircraftModel.contains("Inspire")) {
            tvBottomInfo.setTextColor(Color.RED);
        } else if (flyTime > 10) {
            tvBottomInfo.setTextColor(Color.YELLOW);
        } else {
            tvBottomInfo.setTextColor(Color.WHITE);
        }
    }

    
    protected void showLoadTaskDialog() {


        LoadLocalTaskDlg dlg;
        LoadLocalAndCloudTaskDlg cloudDialog;

        Bundle args = new Bundle();

        if (currentMode == AppConstant.OperationMode.LinePatrolPhoto
                || currentMode == AppConstant.OperationMode.LinePatrolVideo)
            args.putString("mode", "LinePatrol");
        else
            args.putString("mode", currentMode.toString());
        args.putStringArrayList("selectedIdList", selectedMissionBatchIdList);

        dlg = new LoadLocalTaskDlg();
        dlg.setArguments(args);
        dlg.show(getFragmentManager(), "load_task");
        dlg.setOnItemClickListener(new LoadLocalTaskDlg.OnItemLoadClickListener() {
            @Override
            public void onItemLoadClick(Mission2 mission) {
                loadMission(mission);
            }
        });
        dlg.setOnItemReviewClickListener(new LoadLocalTaskDlg.OnItemReviewClickListener() {
            @Override
            public void onItemReviewClick(ArrayList<String> selectIdList) {
                reviewSelectedMissions(selectIdList);
            }
        });
    }

    protected void showMobileNavigationDialog() {
        Point p = mapView.getCenter();
        LatLngInfo pCenter;
        if (mMapServiceProvider == MapServiceProvider.OPENCYCLE_MAP)
            pCenter = CoordinateUtils.mercatorToGcj(p.getX(), p.getY());
        else
            pCenter = CoordinateUtils.mercatorToLonLat(p.getX(), p.getY());

        MobileNavigationDialogFragment dlg = new MobileNavigationDialogFragment();
        Bundle dlgArgs = new Bundle();
        dlgArgs.putSerializable("location", pCenter);
        dlg.setArguments(dlgArgs);
        dlg.show(getFragmentManager(), "mobile_navigation");
    }

    public void showLoadProgressDialog(@Nullable String message) {
        if (mLoadProgressDlg == null) {
            mLoadProgressDlg = new BaseProgressDialog(getContext());
            mLoadProgressDlg.setCancelable(false);
        }
        mLoadProgressDlg.setMessage(message);
        mLoadProgressDlg.show();
    }

    public void dismissLoadProgressDialog() {
        if (mLoadProgressDlg != null && mLoadProgressDlg.isShowing()) {
            mLoadProgressDlg.dismiss();
        }
    }

    /**
     * 检查那些模块需要加载任务和保存任务按钮
     *
     * @return
     */
    private boolean checkSaveTaskMode() {
        int mode = config.getMode();
        return mode == FlyCollectMode.LinePatrol || mode == FlyCollectMode.TiltImage;
    }

    private boolean checkLoadTaskMode() {
        int mode = config.getMode();
        return mode == FlyCollectMode.LinePatrol || mode == FlyCollectMode.TiltImage;
    }

    private boolean checkChangeFpv() {
        if (mProduct != null && mProduct.isConnected() && mProduct.getCamera() != null) {
            int cameraSize = ((Aircraft) mProduct).getCameras().size();
            return cameraSize > 1;
        }
        return false;
    }

    private boolean checkDrawCircleDoubleClickMode() {
        int mode = config.getMode();
        return false;
    }

    private boolean checkResumeFlyMode() {
        int mode = config.getMode();

        return mode == FlyCollectMode.TiltImage;
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
                mapView.setScale((float) (mapView.getMinScale() / (Math.pow(2, 10))));
                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.argb(100, 219, 112, 147));
                for (SubFlyZoneInformation[] subFlyZoneInformations : map) {
                    if (subFlyZoneInformations != null && subFlyZoneInformations.length > 0) {
                        for (SubFlyZoneInformation subFlyZoneInformation : subFlyZoneInformations) {
                            Polygon polygon = new Polygon();
                            for (int i = 0; i < subFlyZoneInformation.getVertices().size(); i++) {
                                LocationCoordinate2D locationCoordinate2D = subFlyZoneInformation.getVertices().get(i);
                                LatLngInfo lngInfo = lonLatToMercator(locationCoordinate2D.getLongitude(), locationCoordinate2D.getLatitude());
                                if (i == 0)
                                    polygon.startPath(lngInfo.longitude, lngInfo.latitude);
                                else
                                    polygon.lineTo(lngInfo.longitude, lngInfo.latitude);
                            }
                            simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.WHITE, 0.0f, SimpleLineSymbol.STYLE.NULL));
                            mNoFlyZoneLayer.addGraphic(new Graphic(polygon, simpleFillSymbol));
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(getActivity(), error);
            }
        });
    }

    /**
     * 获取当前定位位置（gps84）：有连接飞机就返回飞机位置，无连接飞机返回手机定位位置
     *
     * @return
     */
    public LatLngInfo getCurrentLocation() {

        LatLngInfo currentLocation = null;


        LatLngInfo hp = flyCollectFragment.getHomePoint();
        if (hp != null) {
            currentLocation = hp;
        } else if (this.currentLntLng != null) {
            currentLocation = new LatLngInfo(this.currentLntLng.latitude, this.currentLntLng.longitude);
        }
        return currentLocation;
    }

    /**
     * 当前触摸事件是否允许继续操作
     *
     * @return
     */
    public boolean checkActionFree() {
        if (currentTouchEvent != MotionEvent.ACTION_UP)
            return false;
        return true;
    }



    private void initViewConfig() {

        Object isOpenRadar = FlyKeyManager.getInstance().getValue(FlySettingConfigKey.create(FlySettingConfigKey.RADAR_DISPLAY_ENABLE));
        isRadarDisplayEnable = isOpenRadar != null && (boolean) isOpenRadar;

        Object videoSource = FlyKeyManager.getInstance().getValue(FlySettingKey.create(FlySettingKey.VIDEO_SOURCE));
        if (videoSource instanceof DjiFPVWidget.VideoSource) {
            if (mFPVWidget != null) {
                mFPVWidget.setVideoSource((DjiFPVWidget.VideoSource) videoSource);
            }
        }
    }

    private void setUpKeys() {

        FlyKeyManager.getInstance().addListener(FlySettingConfigKey.create(FlySettingConfigKey.RADAR_DISPLAY_ENABLE), mRadarDisplayListener);
        FlyKeyManager.getInstance().addListener(FlySettingKey.create(FlySettingKey.VIDEO_SOURCE), mVideoSourceListener);

    }

    private void tearDownKeys() {
        FlyKeyManager.getInstance().removeListener(mRadarDisplayListener);
        FlyKeyManager.getInstance().removeListener(mVideoSourceListener);

    }


    private KeyListener mRadarDisplayListener = new KeyListener() {
        @Override
        public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
            if (newValue instanceof Boolean) {
                isRadarDisplayEnable = (boolean) newValue;
                if (CAMERA_MAP_SWITCH) {
                    mRadarWidget.setVisibility(isRadarDisplayEnable ? View.VISIBLE : View.GONE);
                }
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



    private void changeMap() {

        if (mGoogleVectorMapLayer != null) {
            mapView.removeLayer(this.mGoogleVectorMapLayer);
            mGoogleVectorMapLayer = null;
        }

        if (mGoogleImageMapLayer != null) {
            mapView.removeLayer(this.mGoogleImageMapLayer);
            mGoogleImageMapLayer = null;
        }

        if (mGoogleRoadMapLayer != null) {
            mapView.removeLayer(this.mGoogleRoadMapLayer);
            mGoogleRoadMapLayer = null;
        }

        if (mMapServiceProvider == MapServiceProvider.GOOGLE_MAP) {
            mGoogleImageMapLayer = new GoogleMapLayer(CommonConstants.GoogleMapType.ImageMap, mMapRegion, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_GOOGLE_CACHE);
            mGoogleRoadMapLayer = new GoogleMapLayer(CommonConstants.GoogleMapType.RoadMap, mMapRegion, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_GOOGLE_CACHE);
            mGoogleVectorMapLayer = new GoogleMapLayer(CommonConstants.GoogleMapType.VectorMap, mMapRegion, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_GOOGLE_CACHE);
            ibMapMode.setVisibility(View.VISIBLE);
        } else if (mMapServiceProvider == MapServiceProvider.AMAP) {
            mGoogleImageMapLayer = new GaodeMapLayer(CommonConstants.GoogleMapType.ImageMap, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_GAODE_CACHE);
            mGoogleRoadMapLayer = new GaodeMapLayer(CommonConstants.GoogleMapType.RoadMap, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_GAODE_CACHE);
            mGoogleVectorMapLayer = new GaodeMapLayer(CommonConstants.GoogleMapType.VectorMap, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_GAODE_CACHE);
            ibMapMode.setVisibility(View.VISIBLE);
        } else {
            mGoogleImageMapLayer = new OpenCycleMapLayer(CommonConstants.GoogleMapType.ImageMap, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_OPENCYCLE_CACHE);
            mGoogleRoadMapLayer = new OpenCycleMapLayer(CommonConstants.GoogleMapType.RoadMap, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_OPENCYCLE_CACHE);
            mGoogleVectorMapLayer = new OpenCycleMapLayer(CommonConstants.GoogleMapType.VectorMap, IOUtils.getRootStoragePath(getContext()) + AppConstant.DIR_OPENCYCLE_CACHE);
            ibMapMode.setVisibility(View.GONE);
        }

        if (this.mapType.equals("Vector")) {
            mapView.addLayer(this.mGoogleVectorMapLayer, 0);
            mGoogleImageMapLayer = null;
            mGoogleRoadMapLayer = null;
        } else {
            mapView.addLayer(this.mGoogleImageMapLayer, 0);
            mapView.addLayer(this.mGoogleRoadMapLayer, 1);
            mGoogleVectorMapLayer = null;
        }
    }

    private class MapSingleTapListener implements OnSingleTapListener {

        @Override
        public void onSingleTap(float longitude, float latitude) {
            if (mFPVStatus == FPVStatus.max) {
                switchFPVDefault();
            }
        }
    }

    private class MapStatusChangedListener implements OnStatusChangedListener {

        @Override
        public void onStatusChanged(Object o, OnStatusChangedListener.STATUS status) {
            if (status.equals(OnStatusChangedListener.STATUS.INITIALIZED)) {

                double minScale = mapView.getMinScale();

                if (minScale != 0) {

                    ArcgisMapUtil.initialize(minScale);

                    centerAtMobileLocation();

                    mPolylineSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), getResources().getColor(R.color.mission_graphic_line), DEFAULT_LINE_WIDTH);
                    mEditPolylineSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), Color.RED, DEFAULT_LINE_WIDTH);

                    mPointSymbol = ArcgisMapUtil.getImageMarkerSymbol(getContext(), R.drawable.google_control);
                    mEditPointSymbol = ArcgisMapUtil.getImageMarkerSymbol(getContext(), R.drawable.google_control);
                    mCenterPointSymbol = ArcgisMapUtil.getImageMarkerSymbol(getContext(), R.drawable.midvetex);

                }
            }
        }
    }

    private class MapZoomListener implements OnZoomListener {
        @Override
        public void preAction(float v, float v1, double v2) {

        }

        @Override
        public void postAction(float v, float v1, double v2) {
            refreshLayerVisibility();
        }
    }

    protected RelativeLayout.LayoutParams getFpvMaxLayoutParams() {
        RelativeLayout.LayoutParams fpvMaxLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        return fpvMaxLayoutParams;
    }

    protected RelativeLayout.LayoutParams getFpvTwiceLayoutParams() {
        RelativeLayout.LayoutParams fpvMinLayoutParams = new RelativeLayout.LayoutParams(
                AppConstant.ScreenWidth * 3 * 2 / ratioHeight,
                AppConstant.ScreenWidth * 3 * 2 / ratioWidth);

        fpvMinLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        return fpvMinLayoutParams;
    }

    protected RelativeLayout.LayoutParams getFpvMinLayoutParams() {
        RelativeLayout.LayoutParams fpvMinLayoutParams = new RelativeLayout.LayoutParams(
                AppConstant.ScreenWidth * 3 / ratioHeight,
                AppConstant.ScreenWidth * 3 / ratioWidth);

        fpvMinLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        return fpvMinLayoutParams;
    }

    public void showToast(String toast) {
        ToastUtil.show(getContext(), toast);
    }

    public void showToastDialog(String toast) {
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
        deleteDialog.setTitle(getActivity().getString(R.string.notice))
                .setMessage(toast)
                .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(getActivity().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    public void toggleViewVisible(View view) {
        if (view != null) {
            view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }
}