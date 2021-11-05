package com.ew.autofly.module.flightrecord.presenter;

import android.os.Handler;
import android.os.Message;

import com.esri.core.geometry.Point;
import com.ew.autofly.base.BaseMvpPresenter;
import com.ew.autofly.db.entity.FlightRecord;
import com.ew.autofly.db.entity.FlightRecordDetail;
import com.ew.autofly.db.helper.FlightRecordDbHelper;
import com.ew.autofly.db.helper.FlightRecordDetailDbHelper;
import com.ew.autofly.entity.BaiduAddress;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.module.flightrecord.view.IPlaybackView;
import com.ew.autofly.net.OkHttpUtil;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ew.autofly.constant.AppConstant.BAIDU_CODE;
import static com.ew.autofly.constant.AppConstant.BAIDU_KEY;
import static com.ew.autofly.constant.AppConstant.BAIDU_URL;



public class PlayBackPresenter extends BaseMvpPresenter<IPlaybackView> implements IPlayBackPresenter {

    private static final int MSG_CHANGE_SEEKBAR = 1;

    private List<FlightRecordDetail> mRecordDetailList = new ArrayList<>();

    private PlaybackHandler mHandler = new PlaybackHandler();

    private boolean isLoadFinish = false;

    private int mProgress = 0;

    private int mBlock = 1;

    private final long sleepTime = 1000;

    private long mSpeed = sleepTime;

    @Override
    public void loadDetailListByRecordId(String recordId) {
        FlightRecordDetailDbHelper.getInstance().getDetailListByRecordId(recordId, new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation != null && operation.isCompletedSucessfully()) {
                    mRecordDetailList = (List<FlightRecordDetail>) operation.getResult();
                    if (mRecordDetailList == null || mRecordDetailList.isEmpty()) {
                        return;
                    }
                    isLoadFinish = true;
                    loadLocation();
                    showAriRouteInMap();
                }
            }
        });
    }

    @Override
    public void loadDetailListByMissionId(String missionId) {
        FlightRecordDetailDbHelper.getInstance().getDetailListByMissionId(missionId, new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation != null && operation.isCompletedSucessfully()) {

                    mRecordDetailList = (List<FlightRecordDetail>) operation.getResult();
                    if (mRecordDetailList == null || mRecordDetailList.isEmpty()) {
                        return;
                    }
                    isLoadFinish = true;
                    initFlightInfo(mRecordDetailList.get(0).getRecordId());
                    loadLocation();
                    showAriRouteInMap();
                }
            }
        });
    }

    @Override
    public void play() {
        if (mProgress + 1 >= mRecordDetailList.size()) {
            mProgress = 0;
            mHandler.removeMessages(MSG_CHANGE_SEEKBAR);
        }
        mHandler.sendEmptyMessage(MSG_CHANGE_SEEKBAR);
    }

    @Override
    public void pause() {
        mHandler.removeMessages(MSG_CHANGE_SEEKBAR);
    }

    @Override
    public void stop() {
        mHandler.removeMessages(MSG_CHANGE_SEEKBAR);
    }

    @Override
    public void changeProgress(int progress) {
        mProgress = progress;
        updatePlane();
    }

    @Override
    public void changeSpeed(int speed) {
        mSpeed = sleepTime / speed;
    }

    private void showAriRouteInMap() {

        List<Point> pointList = new ArrayList<>();
        for (FlightRecordDetail detail : mRecordDetailList) {
            LatLngInfo latLngInfo = CoordinateUtils.gps84_To_Gcj02(detail.getLatitude(), detail.getLongitude());
            LatLngInfo lGPS = CoordinateUtils.lonLatToMercator(latLngInfo.longitude, latLngInfo.latitude);
            Point pt = new Point(lGPS.longitude, lGPS.latitude);
            pointList.add(pt);
        }

        if (isViewAttached()) {
            getView().showAirRouteInMap(pointList);
            getView().addPlane(pointList.get(0), mRecordDetailList.get(0).getAngle());
            getView().setProgressMax(pointList.size() - 1);
        }

    }

    class PlaybackHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHANGE_SEEKBAR:
                    changeSeekBar();
                    break;
            }
        }

    }

    private void changeSeekBar() {

        if (mRecordDetailList.size() > mProgress && isLoadFinish) {
            FlightRecordDetail detail = mRecordDetailList.get(mProgress);
            LatLngInfo mer = CoordinateUtils.gps84ToMapMercator(detail.getLongitude(), detail.getLatitude());
            if (isViewAttached()) {
                getView().updatePlane(new Point(mer.longitude, mer.latitude), detail.getAngle());
                getView().updateRealTimeFlightInfo("高度:" + String.format("%.2f", detail.getAltitude()) + "M",
                        "距离:" + String.format("%.2f", detail.getDistance()) + "M",
                        "垂直速度:" + detail.getVerticalSpeed() + "M/S",
                        "水平速度:" + detail.getHorizontalSpeed() + "M/S",
                        (int) detail.getBatteryLevel() + "%",
                        detail.getGPSMode());
                getView().updateRealTimeDroneLocation(new LocationCoordinate(detail.getLatitude(), detail.getLongitude(), (float) detail.getAltitude()));
                getView().updateControllerState((int) (detail.getLeftStickHorizontalPosition() / 660f * 100),
                        (int) (detail.getLeftStickVerticalPosition() / 660f * 100),
                        (int) (detail.getRightStickHorizontalPosition() / 660f * 100),
                        (int) (detail.getRightStickVerticalPosition() / 660f * 100));

                getView().updateProgress(mProgress);
                getView().setCreatedTime(detail.getCreatedTime());
            }
        }

        mHandler.sendEmptyMessageDelayed(MSG_CHANGE_SEEKBAR, mSpeed);

        mProgress += mBlock;
    }

    private void updatePlane() {
        if (mRecordDetailList.size() > mProgress && isLoadFinish) {

            FlightRecordDetail detail = mRecordDetailList.get(mProgress);
            LatLngInfo latLngInfo = CoordinateUtils.gps84_To_Gcj02(detail.getLatitude(), detail.getLongitude());
            LatLngInfo lGPS = CoordinateUtils.lonLatToMercator(latLngInfo.longitude, latLngInfo.latitude);

            Point point = new Point(lGPS.longitude, lGPS.latitude);

            if (isViewAttached())
                getView().updatePlane(point, detail.getAngle());
        }
    }

    
    public void loadLocation() {

        String locationStr = mRecordDetailList.get(0).getLatitude() + "," + mRecordDetailList.get(0).getLongitude();

        Map<String, String> params = new HashMap<>();
        params.put("ak", BAIDU_KEY);
        params.put("location", locationStr);
        params.put("coordtype", "wgs84ll");
        params.put("output", "json");
        params.put("pois", "0");
        params.put("mcode", BAIDU_CODE);

        OkHttpUtil.doPostAsyncUI(BAIDU_URL, params, new OkHttpUtil.HttpCallBack<BaiduAddress>() {
            @Override
            public void onSuccess(BaiduAddress baiduAddress) {
                if (baiduAddress != null && baiduAddress.getStatus() == 0) {
                    String address = baiduAddress.getResult().getFormatted_address();
                    if (isViewAttached())
                        getView().setLocationCity(address);
                }
            }

            @Override
            public void onError(Exception e) {
                if (isViewAttached())
                    getView().setLocationCity("定位失败");
            }
        });
    }

    
    private void initFlightInfo(String recordId) {

        FlightRecordDbHelper.getInstance().getRecordById(recordId, new AsyncOperationListener() {
            @Override
            public void onAsyncOperationCompleted(AsyncOperation operation) {
                if (operation != null && operation.isCompletedSucessfully()) {
                    FlightRecord flightRecord = (FlightRecord) operation.getResult();
                    if (flightRecord != null) {

                        if (isViewAttached()) {
                            getView().setProductInfo(flightRecord.getProductName());
                            getView().setCreatedTime(flightRecord.getCreatedTime());
                            getView().setTotalTime(flightRecord.getStartTime(), flightRecord.getEndTime());
                            getView().setTotalDistance(flightRecord.getDistance());
                            getView().setMaxHeight(flightRecord.getMaxHeight());
                        }
                    }
                }
            }
        });
    }

}
