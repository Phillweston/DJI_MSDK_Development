package com.ew.autofly.widgets.business;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.ew.autofly.db.entity.FinePatrolWayPointDetail;
import com.ew.autofly.db.entity.PhotoPosition;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.mission.MissionPointType;
import com.ew.autofly.event.flight.AircraftAttitudeEvent;
import com.ew.autofly.event.flight.LocationStateEvent;
import com.ew.autofly.widgets.CloudPointView2.util.LatLngCloudPoint;
import com.ew.autofly.widgets.CloudPointView2.widget.CloudPointView2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class AirlineModelWidget extends CloudPointView2 {

    protected Handler mUIHandler = new Handler(Looper.getMainLooper());

    private boolean isLoadCloudPointSuccess = false;

    private boolean isShowAircraft = false;


    private LocationCoordinate mBaseLoc;

    private LocationCoordinate mAircraftLoc;

    private double mAircraftYaw;

    private List<FinePatrolWayPointDetail> mWayPointDetails;

    private ArrayList<LatLngCloudPoint> mWaypointList = new ArrayList<>();
    private ArrayList<LatLngCloudPoint> mHighLightWaypointList = new ArrayList<>();
    private ArrayList<LatLngCloudPoint> mPhotoPointList = new ArrayList<>();

    private String mModelPointCloudPath;

    public AirlineModelWidget(Context context) {
        super(context);
    }

    public AirlineModelWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AirlineModelWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void show(Param param) {

        mWayPointDetails = param.wayPointDetails;
        mBaseLoc = param.baseLoc;
        mAircraftYaw = param.aircraftYaw;
        mModelPointCloudPath = param.modelPointCloudPath;
        isShowAircraft = param.isShowAircraft;

        convertToWayPointLine();
        convertToHighLightPoint(0);
        convertToPhotoPoint(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                CloudPointView2 cloudPointView2 = initAirLineInfo(mWaypointList)
                        .initHighPointInfo(mHighLightWaypointList)
                        .initPhotoPointInfo(mPhotoPointList)
                        .initTowerLocationInfo((float) mBaseLoc.getLatitude(), (float) mBaseLoc.getLongitude(), mBaseLoc.getAltitude())
                        .initCloudPointInfo(mModelPointCloudPath);
                if (isShowAircraft) {
                    cloudPointView2.initAircraftLocation();
                }
                cloudPointView2.build();
                isLoadCloudPointSuccess = true;
            }
        }, 50);
    }

    public boolean isLoaded() {
        return isLoadCloudPointSuccess;
    }

    /**
     * 选中航点
     *
     * @param checkPosition
     */
    public void checkWaypoint(int checkPosition) {
        convertToHighLightPoint(checkPosition);
        convertToPhotoPoint(checkPosition);
        initHighPointInfo(mHighLightWaypointList).initPhotoPointInfo(mPhotoPointList).update();
    }

    /**
     * 添加航点
     *
     * @param checkPosition
     */
    public void addWaypoint(int checkPosition) {
        convertToWayPointLine();
        convertToHighLightPoint(checkPosition);
        convertToPhotoPoint(checkPosition);
        initAirLineInfo(mWaypointList).initHighPointInfo(mHighLightWaypointList).initPhotoPointInfo(mPhotoPointList).update();
    }


    public void deleteWaypoint(int checkPosition) {
        mWaypointList.remove(checkPosition);
        checkWaypoint(0);
    }


    private void convertToWayPointLine() {
        mWaypointList.clear();
        for (FinePatrolWayPointDetail studyDetail : mWayPointDetails) {
            LatLngCloudPoint cloudPoint = new LatLngCloudPoint(studyDetail.getAircraftLocationLatitude(),
                    studyDetail.getAircraftLocationLongitude(), studyDetail.getAircraftLocationAltitude(), 0, 255, 0);
            mWaypointList.add(cloudPoint);
        }

    }


    private void convertToHighLightPoint(int checkPosition) {

        mHighLightWaypointList.clear();

        for (int i = 0, mStudyListSize = mWayPointDetails.size(); i < mStudyListSize; i++) {
            FinePatrolWayPointDetail studyDetail = mWayPointDetails.get(i);

            int red = 0;
            int green = 0;
            int blue = 0;
            int pointSize = 10;
            if (studyDetail.getWaypointType() == MissionPointType.SHOT_PHOTO.value()) {
                red = 255;
                green = 0;
                blue = 0;
                pointSize = 10;
            } else {
                red = 0;
                green = 0;
                blue = 255;
                pointSize = 10;
            }

            if (checkPosition == i) {
                red = 225;
                green = 255;
                blue = 0;
                pointSize = 20;
            }

            LatLngCloudPoint highLightPoint = new LatLngCloudPoint(studyDetail.getAircraftLocationLatitude(),
                    studyDetail.getAircraftLocationLongitude(), studyDetail.getAircraftLocationAltitude(), pointSize, red, green, blue);

            mHighLightWaypointList.add(highLightPoint);
        }


    }


    private void convertToPhotoPoint(int checkPosition) {

        mPhotoPointList.clear();
        for (int i = 0, mStudyListSize = mWayPointDetails.size(); i < mStudyListSize; i++) {
            FinePatrolWayPointDetail studyDetail = mWayPointDetails.get(i);
            List<PhotoPosition> photoPositions = studyDetail.getPhotoPositionList();
            if (photoPositions != null && !photoPositions.isEmpty()) {
                int red = 255;
                int green = 0;
                int blue = 255;
                int pointSize = 10;
                if (checkPosition == i) {
                    red = 225;
                    green = 255;
                    blue = 0;
                    pointSize = 10;
                }
                for (PhotoPosition photoPosition : photoPositions) {
                    LatLngCloudPoint photoPoint = new LatLngCloudPoint(photoPosition.getLatitude(),
                            photoPosition.getLongitude(), photoPosition.getAltitude(), pointSize, red, green, blue);

                    mPhotoPointList.add(photoPoint);
                }
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationStateEvent(LocationStateEvent event) {
        if (isShowAircraft) {
            mAircraftLoc = event.getAircraftCoordinate();
            if (mAircraftLoc != null) {
                updateAircraft();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAircraftAttitudeEvent(AircraftAttitudeEvent event) {
        if (isShowAircraft) {
            mAircraftYaw = event.getYaw();
            updateAircraft();
        }
    }

    private void updateAircraft() {
        mUIHandler.post(mUpdateAircraftRunnable);
    }

    private Runnable mUpdateAircraftRunnable = new Runnable() {
        @Override
        public void run() {
            updateAircraftLocation(mAircraftLoc.getLatitude(), mAircraftLoc.getLongitude(),
                    mAircraftLoc.getAltitude(), mAircraftYaw);
        }
    };

    public static final class Param {

        private boolean isShowAircraft;
        private double aircraftYaw;
        private List<FinePatrolWayPointDetail> wayPointDetails;
        private String modelPointCloudPath;
        private LocationCoordinate baseLoc;

        public Param() {
        }

        public Param isShowAircraft(boolean val) {
            isShowAircraft = val;
            return this;
        }

        public Param aircraftYaw(double val) {
            aircraftYaw = val;
            return this;
        }

        public Param wayPointDetails(List<FinePatrolWayPointDetail> val) {
            wayPointDetails = val;
            return this;
        }

        public Param modelPointCloudPath(String val) {
            modelPointCloudPath = val;
            return this;
        }

        public Param modelBaseLocationCoordinate(LocationCoordinate val) {
            baseLoc = val;
            return this;
        }

        public Param build() {
            return this;
        }
    }
}
