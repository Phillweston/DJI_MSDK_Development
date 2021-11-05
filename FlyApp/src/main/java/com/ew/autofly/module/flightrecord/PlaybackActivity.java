package com.ew.autofly.module.flightrecord;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.ew.autofly.R;
import com.ew.autofly.base.BaseMvpActivity;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.widgets.controls.ImageMarkerSymbol;
import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.module.flightrecord.presenter.PlayBackPresenter;
import com.ew.autofly.module.flightrecord.view.IPlaybackView;
import com.ew.autofly.module.flightrecord.widgets.PlayBackControlGroup;
import com.ew.autofly.utils.arcgis.ArcgisMapUtil;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.CommonConstants;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.ew.autofly.xflyer.utils.GoogleMapLayer;

import java.text.DecimalFormat;
import java.util.List;


public class PlaybackActivity extends BaseMvpActivity<IPlaybackView, PlayBackPresenter>
        implements IPlaybackView, View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    public static String ARG_FLIGHT_RECORD = "flight_record";

    public static String ARG_MISSION_EXECUTE = "missionExecute";

    private boolean isLoadedMap = false;

    private MapView mMapView;

    private TextView mPlaySpeed;

    private ImageView mPlaybackControllerIv;

    private SeekBar mPlaySeekbar;

    private RelativeLayout mControllerLayout;

    private CheckBox mPlayCb;

    private PlayBackControlGroup mControlGroupLeft;

    private PlayBackControlGroup mControlGroupRight;

    private TextView mCityTv;

    private TextView mDateTv;

    private TextView mTotalTimeTv;

    private TextView mTotalDistanceTv;

    private TextView mMaxHeightTv;

    private TextView mCurrentHeightTv;

    private TextView mCurrentDistanceTv;

    private TextView mCurrentVSpeedTv;

    private TextView mCurrentHSpeedTv;

    private TextView mCurrentBatteryTv;

    private TextView mCurrentGPSModeTv;

    private ImageView mProductIv;

    private TextView mProductTv;

    private ImageButton mBackIb;


    private GoogleMapLayer mGoogleImageMapLayer;

    private GraphicsLayer mDrawLayer = new GraphicsLayer();

    private Graphic mPlaneGraphic;

    private ImageMarkerSymbol mPlaneSymbol;

    private int mGraphicId;


    private FlightRecord mFlightRecord;

    private Mission2 mMission;

  
    private int mSpeedRate = 1;
    private TextView mTvLatitude;
    private TextView mTvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordplayback);
        initData();
        initView();
    }

    @Override
    protected PlayBackPresenter createPresenter() {
        return new PlayBackPresenter();
    }

    private void initData() {
        if (getIntent().hasExtra(PlaybackActivity.ARG_MISSION_EXECUTE)) {
            mMission = (Mission2) getIntent().getSerializableExtra(PlaybackActivity.ARG_MISSION_EXECUTE);
        } else if (getIntent().hasExtra(PlaybackActivity.ARG_FLIGHT_RECORD)) {
            mFlightRecord = (FlightRecord) getIntent().getSerializableExtra(PlaybackActivity.ARG_FLIGHT_RECORD);
        }
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mapview);
        mPlaySpeed = (TextView) findViewById(R.id.tv_playspeed);
        mPlaybackControllerIv = (ImageView) findViewById(R.id.iv_play_back_controller);
        mPlaySeekbar = (SeekBar) findViewById(R.id.sbar_play);
        mControllerLayout = (RelativeLayout) findViewById(R.id.rlayout_controller);
        mPlayCb = (CheckBox) findViewById(R.id.cb_play);
        mControlGroupLeft = (PlayBackControlGroup) findViewById(R.id.control_group_left);
        mControlGroupRight = (PlayBackControlGroup) findViewById(R.id.control_group_right);
        mCityTv = (TextView) findViewById(R.id.tv_city);
        mDateTv = (TextView) findViewById(R.id.tv_date);
        mTotalTimeTv = (TextView) findViewById(R.id.tv_flight_total_time);
        mTotalDistanceTv = (TextView) findViewById(R.id.tv_flight_total_distance);
        mMaxHeightTv = (TextView) findViewById(R.id.tv_flight_max_height);
        mCurrentHeightTv = (TextView) findViewById(R.id.tv_current_height);
        mCurrentDistanceTv = (TextView) findViewById(R.id.tv_current_distance);
        mCurrentVSpeedTv = (TextView) findViewById(R.id.tv_current_vspeed);
        mCurrentHSpeedTv = (TextView) findViewById(R.id.tv_current_hspeed);
        mCurrentBatteryTv = (TextView) findViewById(R.id.tv_current_battery);
        mCurrentGPSModeTv = (TextView) findViewById(R.id.tv_current_mode);
        mProductIv = (ImageView) findViewById(R.id.iv_product);
        mProductTv = (TextView) findViewById(R.id.tv_product);
        mBackIb = (ImageButton) findViewById(R.id.imgBtnBack);
        mTvLatitude = findViewById(R.id.tv_latitude);
        mTvLongitude = findViewById(R.id.tv_longitude);

        mBackIb.setOnClickListener(this);

        mPlaySeekbar.setOnSeekBarChangeListener(this);
        mPlayCb.setOnCheckedChangeListener(this);
        mPlaySpeed.setOnClickListener(this);
        mPlaybackControllerIv.setOnClickListener(this);

        initMapView();

        initFlightInfo();

    }

    private void loadData() {

        if (mFlightRecord != null && !TextUtils.isEmpty(mFlightRecord.getId())) {
            mPresenter.loadDetailListByRecordId(mFlightRecord.getId());
        } else if (mMission != null && !TextUtils.isEmpty(mMission.getId())) {
            mPresenter.loadDetailListByMissionId(mMission.getId());
        }
    }

    private void initMapView() {
        ArcGISRuntime.setClientId(AppConstant.ARCGIS_CLIENT_ID);

      
        mGoogleImageMapLayer = new GoogleMapLayer(CommonConstants.GoogleMapType.VectorMap, IOUtils.getRootStoragePath(this) + AppConstant.DIR_GOOGLE_CACHE);
        mMapView.addLayer(mGoogleImageMapLayer);

        mDrawLayer = new GraphicsLayer();
        mMapView.addLayer(mDrawLayer);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (status.equals(STATUS.LAYER_LOADED) && !isLoadedMap) {
                    isLoadedMap = true;
                    ArcgisMapUtil.initialize(mMapView.getMinScale());
                    mMapView.setScale(ArcgisMapUtil.getZoomScale());
                    loadData();
                }
            }
        });
    }

    
    private void initFlightInfo() {

        if (mFlightRecord != null) {
            String productName = mFlightRecord.getProductName();
            setProductInfo(productName);
            String createdTime = mFlightRecord.getCreatedTime();
            setCreatedTime(createdTime);
            String startTimeStr = mFlightRecord.getStartTime();
            String endTimeStr = mFlightRecord.getEndTime();

            setTotalTime(startTimeStr, endTimeStr);

            setTotalDistance(mFlightRecord.getDistance());
            setMaxHeight(mFlightRecord.getMaxHeight());

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_playspeed:
                mSpeedRate = mSpeedRate * 2;
                if (mSpeedRate > 4) {
                    mSpeedRate = 1;
                }
                mPlaySpeed.setText("X " + mSpeedRate);
                mPresenter.changeSpeed(mSpeedRate);
                break;
            case R.id.iv_play_back_controller:
                toggleController();
                break;
            case R.id.imgBtnBack:
                finish();
                break;
            default:
                break;
        }
    }

    private void toggleController() {
        if (mControllerLayout.getVisibility() == View.VISIBLE) {
            mControllerLayout.setVisibility(View.GONE);
        } else if (mControllerLayout.getVisibility() == View.GONE) {
            mControllerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mPresenter.play();
        } else {
            mPresenter.pause();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mPresenter.changeProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mPresenter.pause();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mPlayCb.isChecked()) {
            mPresenter.play();
        }
    }

    @Override
    public void showToast(String toast) {
        ToastUtil.show(this, toast);
    }

    @Override
    public void showLoading(boolean isShow, String loadingMsg) {

    }

    @Override
    public void showError(boolean isShow, String errorMsg) {

    }

    @Override
    public void showEmpty(boolean isShow, String emptyMsg) {

    }

    @Override
    public void setProductInfo(String productName) {
        if (productName != null) {
            int drawable = R.drawable.ic_launcher;
            if (productName.contains("PHANTOM_3")) {
                drawable = R.drawable.ic_aircaft_p3;
            } else if (productName.contains("PHANTOM_4")) {
                drawable = R.drawable.ic_aircaft_p4;
            } else if (productName.contains("MATRICE_600")) {
                drawable = R.drawable.ic_aircaft_m600;
            } else if (productName.contains("MATRICE_100")) {
                drawable = R.drawable.ic_aircaft_m100;
            } else if (productName.contains("INSPIRE")) {
                drawable = R.drawable.ic_aircaft_wu;
            }

            mProductIv.setImageResource(drawable);
            mProductTv.setText(productName);

        } else {

            mProductIv.setImageResource(R.drawable.ic_launcher);
            mProductTv.setText("N/A");
        }
    }

    @Override
    public void setLocationCity(String city) {
        mCityTv.setText(city);
    }

    @Override
    public void setCreatedTime(String createdTime) {
        mDateTv.setText(createdTime);
    }

    @Override
    public void setTotalTime(String startTime, String endTime) {
        if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime)) {
            int second = (int) (DateHelperUtils.string2DateTime(endTime).getTime()
                    - DateHelperUtils.string2DateTime(startTime).getTime()) / 1000;
            String totalTime = DateHelperUtils.formatTimeByHMS(second);
            mTotalTimeTv.setText(totalTime);
        }
    }

    @Override
    public void setTotalDistance(double distance) {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0");
        mTotalDistanceTv.setText(decimalFormat.format((int) distance) + "m");
    }

    @Override
    public void setMaxHeight(double maxHeight) {
        DecimalFormat decimalFormat = new DecimalFormat("###,##0");
        mMaxHeightTv.setText(decimalFormat.format((int) maxHeight) + "m");
    }

    @Override
    public void showAirRouteInMap(List<Point> pointList) {
        if (pointList != null && pointList.size() > 1) {

            mMapView.centerAt(pointList.get(0), true);

            ImageMarkerSymbol homeSymbol = ArcgisMapUtil.getImageMarkerSymbol(this, R.drawable.ic_map_homepoint);
            Graphic homeGraphic = new Graphic(pointList.get(0), homeSymbol);
            mDrawLayer.addGraphic(homeGraphic);

            ImageMarkerSymbol endSymbol = ArcgisMapUtil.getImageMarkerSymbol(this, R.drawable.ic_target_point);
            Graphic endGraphic = new Graphic(pointList.get(pointList.size() - 1), endSymbol);
            mDrawLayer.addGraphic(endGraphic);

            Polyline line = new Polyline();
            SimpleLineSymbol lineSymbol = ArcgisMapUtil.getSimpleLineSymbol(this, Color.BLUE, 2.0f);

            for (int i = 0; i < pointList.size(); i++) {
                if (i == 0) {
                    line.startPath(pointList.get(i));
                } else {
                    line.lineTo(pointList.get(i));
                }
            }

            mDrawLayer.addGraphic(new Graphic(line, lineSymbol));
        }
    }

    @Override
    public void addPlane(Point point, double angle) {

        mPlaneSymbol = ArcgisMapUtil.getImageMarkerSymbol(this, R.drawable.aircraft);
        mPlaneSymbol.setAngle((float) angle);
        mPlaneGraphic = new Graphic(point, mPlaneSymbol, null);
        mGraphicId = mDrawLayer.addGraphic(mPlaneGraphic);
    }

    @Override
    public void updatePlane(Point point, double angle) {
        mPlaneSymbol.setAngle((float) angle);
        Graphic graphic = new Graphic(point, mPlaneSymbol, null);
        mDrawLayer.updateGraphic(mGraphicId, graphic);
    }

    @Override
    public void updateRealTimeFlightInfo(String height, String distance, String vSpeed, String hSpeed, String battery, String GPSMode) {
      
        mCurrentDistanceTv.setText(distance);
        mCurrentVSpeedTv.setText(vSpeed);
        mCurrentHSpeedTv.setText(hSpeed);
        mCurrentBatteryTv.setText(battery);
        mCurrentGPSModeTv.setText(GPSMode);
    }

    @Override
    public void updateRealTimeDroneLocation(LocationCoordinate locationCoordinate) {
        if (locationCoordinate != null) {
            mCurrentHeightTv.setText("高度:" + String.format("%.2f", locationCoordinate.getAltitude()) + "M");
            mTvLongitude.setText("经度:" + String.format("%.7f", locationCoordinate.getLongitude()));
            mTvLatitude.setText("纬度:" + String.format("%.7f", locationCoordinate.getLatitude()));
        }
    }

    @Override
    public void updateControllerState(int leftStickHorizontalPos, int leftStickVerticalPos, int rightStickHorizontalPos, int rightStickVerticalPos) {
        mControlGroupLeft.changeControlNum(leftStickVerticalPos, leftStickHorizontalPos);
        mControlGroupRight.changeControlNum(rightStickVerticalPos, rightStickHorizontalPos);
    }

    @Override
    public void updateProgress(int progress) {
        mPlaySeekbar.setProgress(progress);
        if (progress >= mPlaySeekbar.getMax()) {
            mPlayCb.setChecked(false);
        }
    }

    @Override
    public void setProgressMax(int max) {
        mPlaySeekbar.setMax(max);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
    }

}

