package com.ew.autofly.mode.linepatrol;

import android.content.Context;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.fragments.BaseMapFragment;
import com.ew.autofly.interfaces.OnSettingDialogClickListener;
import com.ew.autofly.mode.linepatrol.point.ui.fragment.PointInfoContainerFragment;
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint;
import com.ew.autofly.mode.linepatrol.widget.dialog.LinePatrolSettingDlgFragment;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.MyUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.arcgis.ArcgisMapUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.ArcgisPointUtils;
import com.ew.autofly.xflyer.utils.CoordinateUtils;
import com.hwangjr.rxbus.RxBus;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinePatrolGoogleMapFragment extends BaseMapFragment {

    private boolean isCancelTouch = false;

    private long mLastLoadLayer = 0l;

    private boolean isAllowInitGoogleMapExtent = true;


    private GraphicsLayer mDrawingPointLayer = null;


    private GraphicsLayer mDrawingModifyLayer = null;


    private int mCurrentPathIndex = -1;
    private List<ArrayList<LatLngInfo>> mPathSetCache;
    private List<Point> mFlightNumPointListMer;

    private int mSelectIndex = -1;
    private int mModifyPointUID = -1;
    private int mModifyPolylineUID = -1;

    private Vibrator vibrator;
    private boolean reverse = false;

    public ArrayList<LatLngInfo> mLinePointMer;
    public ArrayList<LatLngInfo> mLinePointReverseMer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        if (getActivity() != null && getParentFragment() instanceof LinePatrolCollectFragment)
            flyCollectFragment = (LinePatrolCollectFragment) getParentFragment();
        initMap(view);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        return view;
    }

    /**
     * 初始化地图信息
     *
     * @param view
     */
    @Override
    protected void initMap(View view) {
        super.initMap(view);
        ibDrawCircle.setImageResource(R.drawable.btn_paint_waypoint_selector);

        mapView.setOnTouchListener(new CustomTouchListener(getContext(), mapView));

        mDensity = getResources().getDisplayMetrics().density;
        FlyAreaPoint flyAreaPoint = new FlyAreaPoint();
        onInitLinePointFinish(flyAreaPoint);
    }

    OnSettingDialogClickListener act;

    void onInitLinePointFinish(FlyAreaPoint location) {

        ArrayList<LatLngInfo> routeInfoList = new ArrayList<>();
        if (mLinePointReverseMer != null && mLinePointReverseMer.size() != 0) {
            for (LatLngInfo latLngInfo : mLinePointReverseMer) {
                routeInfoList.add(CoordinateUtils.mercatorToLonLat(latLngInfo.longitude, latLngInfo.latitude));
            }
        } else {
            routeInfoList = null;
        }

        if (!flyCollectFragment.checkTaskFree())
            return;
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", flyCollectFragment.airRoutePara);
        bundle.putSerializable("routeInfoList", routeInfoList);
        bundle.putSerializable("aircraftLocation", new LatLngInfo(flyCollectFragment.aircraftLocationLatitude,
                flyCollectFragment.aircraftLocationLatitude));
        bundle.putInt("actionMode", flyCollectFragment.actionMode);
        bundle.putInt("returnMode", flyCollectFragment.returnMode);
        bundle.putInt("recordMode", flyCollectFragment.recodeMode);


        pointInfoContainerFragment = PointInfoContainerFragment.Companion.newInstance("", location.latitude, location.longitude, bundle, act);
        FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
        beginTransaction.add(R.id.content, pointInfoContainerFragment, "pointInfoContainerFragment");
        beginTransaction.show(pointInfoContainerFragment);
        beginTransaction.commit();












    }

    PointInfoContainerFragment pointInfoContainerFragment;

    
    private void updateStartFlag() {
        int size = mLinePointMer.size();
        if (size < 1) {
            startFlagLayer.removeAll();
            return;
        }

        LatLngInfo homeGps84 = getCurrentLocation();
        LatLngInfo homePointMer = CoordinateUtils.gps84ToMapMercator(homeGps84.longitude, homeGps84.latitude);

        double dis1 = 0;
        double dis2 = 0;

        if (homePointMer != null) {
            dis1 = ArcgisPointUtils.getDistance(new Point(homePointMer.latitude, homePointMer.longitude), new Point(mLinePointMer.get(0).latitude, mLinePointMer.get(0).longitude));
            dis2 = ArcgisPointUtils.getDistance(new Point(homePointMer.latitude, homePointMer.longitude), new Point(mLinePointMer.get(size - 1).latitude, mLinePointMer.get(size - 1).longitude));
        }

        if (dis1 < dis2) {
            reverse = false;
            ArcgisMapUtil.updateMarkerToMapByMercator(getContext(), startFlagLayer, mLinePointMer.get(0), R.drawable.ic_startpoint_flag);
        } else {
            reverse = true;
            ArcgisMapUtil.updateMarkerToMapByMercator(getContext(), startFlagLayer, mLinePointMer.get(size - 1), R.drawable.ic_startpoint_flag);
        }

    }

    
    private void updateFlightNumText() {
        if (flightPointTextLayer != null)
            flightPointTextLayer.removeAll();

        mFlightNumPointListMer.clear();
        mLinePointReverseMer.clear();

        int size = mLinePointMer.size();
        if (size < 1) {
            flightPointTextLayer.removeAll();
            return;
        }

        for (LatLngInfo latLngInfo : mLinePointMer) {

            if (latLngInfo != null) {



                Point point = new Point(latLngInfo.longitude, latLngInfo.latitude);
                mFlightNumPointListMer.add(point);
            }
        }

        mLinePointReverseMer.addAll(mLinePointMer);
        if (reverse) {
            Collections.reverse(mFlightNumPointListMer);
            Collections.reverse(mLinePointReverseMer);
        }

        addPointListToMap(flightPointTextLayer, mFlightNumPointListMer, (int) getResources().getDimension(R.dimen.map_app_text_size));

        pointInfoContainerFragment.onTouchPointChange(mLinePointReverseMer);

    }

    TouchPointChangeListener touchPointChangeListener;


//

//


    private Point mDraggingPoint = null;

    private static final float TOUCH_POINT_CATCH_DISTANCE = 15;

    List<Point> mCropPoints = new ArrayList<>();

    private Point getNearbyPoint(MotionEvent event) {
        if (!checkPoints(mCropPoints)) {
            return null;
        }
        float x = event.getX();
        float y = event.getY();
        for (Point p : mCropPoints) {
            double px = p.getX();
            double py = p.getY();
            double distance = Math.sqrt(Math.pow(x - px, 2) + Math.pow(y - py, 2));
            if (distance < dp2px(TOUCH_POINT_CATCH_DISTANCE)) {
                return p;
            }
        }
        return null;
    }

    private float mDensity;

    private float dp2px(float dp) {
        return dp * mDensity;
    }

    public boolean checkPoints(List<Point> points) {
        return points != null && points.size() > 4
                && points.get(0) != null && points.get(1) != null && points.get(2) != null && points.get(3) != null;
    }


    private class CustomTouchListener extends MapOnTouchListener {
        public CustomTouchListener(Context context, MapView view) {
            super(context, view);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            boolean handle = true;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mDraggingPoint = getNearbyPoint(event);
                if (mDraggingPoint == null) {
                    handle = false;
                }
            }

            currentTouchEvent = event.getAction();

            refreshAltitude();

            if (mModifyPointUID == -1)
                moveEvent(event);

            mapTouch(event);
            if (!isCancelTouch)
                return (mDraggingPoint != null && handle) || super.onTouch(v, event);
            else
                return true;
        }

        @Override
        public boolean onSingleTap(MotionEvent event) {

            if (!CAMERA_MAP_SWITCH)
                if (bDrawPath && event.getAction() == MotionEvent.ACTION_DOWN && flyCollectFragment != null
                        && flyCollectFragment.operateAction == AppConstant.OperateAction.Unknown) {
                    flyCollectFragment.airRoutePara.setFixedAltitude(true);
                    flyCollectFragment.airRoutePara.setFixedAltitudeList("");

                    Point currentPoint = mapView.toMapPoint(event.getX(), event.getY());
                    LatLngInfo latLng = new LatLngInfo(currentPoint.getY(), currentPoint.getX());
                    mLinePointMer.add(latLng);
                    mCropPoints.add(new Point(event.getX(), event.getY()));

                    drawLine(mLinePointMer);
                    if (mCurrentPathIndex < mPathSetCache.size() - 1) {
                        for (int i = mPathSetCache.size() - 1; i > mCurrentPathIndex; i--) {
                            mPathSetCache.remove(i);
                        }
                    }
                    mCurrentPathIndex++;

                    mPathSetCache.add(mCurrentPathIndex, getPathPoints(mLinePointMer));

                    if (ibRedo.getAlpha() == 1.0f) {
                        ibRedo.setAlpha(0.5f);
                    }

                    int size = mLinePointMer.size();
                    if (size == 1)
                        ArcgisMapUtil.updateMarkerToMapByMercator(getContext(), startFlagLayer, latLng, R.drawable.ic_startpoint_flag);

                    else if (size > 1) {

                        ibUndo.setAlpha(1.0f);

                        updateStartFlag();
                        updateFlightNumText();
                        SetFlightButton(true);
                        tvBottomInfo.setVisibility(View.VISIBLE);
                        refreshBottomInfo();
                        refreshChildInfo();
                    }

                    ibSaveTask.setEnabled(true);
                }
            return super.onSingleTap(event);
        }

        @Override
        public void onLongPress(MotionEvent event) {
            super.onLongPress(event);
        }
    }

    @Subscribe
    public void onTaskMessage() {

    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RxBus.get().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    private void moveEvent(MotionEvent event) {
        if (!isCanTouchMap)
            return;

        if (bDrawPath && flyCollectFragment != null
                && flyCollectFragment.operateAction == AppConstant.OperateAction.Unknown) {

            int[] vPoint = mDrawingPointLayer.getGraphicIDs(event.getX(), event.getY(), 20);
            if (vPoint.length > 0) {
                vibrator.vibrate(100);
                isCancelTouch = true;
                if (mLinePointMer.size() > 1) {
                    Graphic g = mDrawingPointLayer.getGraphic(vPoint[0]);
                    Object object = g.getAttributeValue("index");
                    if (object != null)
                        mSelectIndex = Integer.parseInt(object.toString());

                    LatLngInfo ps = mLinePointMer.get(mSelectIndex);
                    LatLngInfo psMe = new LatLngInfo(ps.latitude, ps.longitude);

                    Polyline line = new Polyline();
                    if (mSelectIndex == 0) {//第一个点
                        LatLngInfo p1 = mLinePointMer.get(1);
                        LatLngInfo pMe1 = new LatLngInfo(p1.latitude, p1.longitude);
                        line.startPath(new Point(psMe.longitude, psMe.latitude));
                        line.lineTo(new Point(pMe1.longitude, pMe1.latitude));
                        mModifyPolylineUID = mDrawingModifyLayer.addGraphic(new Graphic(line, mEditPolylineSymbol));
                    } else if (mSelectIndex == (mLinePointMer.size() - 1)) {
                        LatLngInfo p0 = mLinePointMer.get(mSelectIndex - 1);
                        LatLngInfo pMe0 = new LatLngInfo(p0.latitude, p0.longitude);
                        line.startPath(new Point(pMe0.longitude, pMe0.latitude));
                        line.lineTo(new Point(psMe.longitude, psMe.latitude));
                        mModifyPolylineUID = mDrawingModifyLayer.addGraphic(new Graphic(line, mEditPolylineSymbol));
                    } else {
                        LatLngInfo p0 = mLinePointMer.get(mSelectIndex - 1);
                        LatLngInfo p2 = mLinePointMer.get(mSelectIndex + 1);
                        LatLngInfo pMe0 = new LatLngInfo(p0.latitude, p0.longitude);

                        LatLngInfo pMe2 = new LatLngInfo(p2.latitude, p2.longitude);
                        line.startPath(new Point(pMe0.longitude, pMe0.latitude));
                        line.lineTo(new Point(psMe.longitude, psMe.latitude));
                        line.lineTo(new Point(pMe2.longitude, pMe2.latitude));
                        mModifyPolylineUID = mDrawingModifyLayer.addGraphic(new Graphic(line, mEditPolylineSymbol));
                    }
                    mModifyPointUID = mDrawingModifyLayer.addGraphic(new Graphic(new Point(psMe.longitude, psMe.latitude), mEditPointSymbol));
                } else if (mLinePointMer.size() == 1) {//当只有一个点时
                    mModifyPointUID = vPoint[0];
                    mSelectIndex = 0;
                }
            }
        }
    }

    private boolean mapTouch(MotionEvent event) {
        Point touchPoint = mapView.toMapPoint(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mModifyPointUID == -1)
                    break;
                if (mLinePointMer.size() == 1)
                    mDrawingPointLayer.updateGraphic(mModifyPointUID, touchPoint);
                else {
                    mDrawingModifyLayer.updateGraphic(mModifyPointUID, touchPoint);
                    mDrawingModifyLayer.removeGraphic(mModifyPolylineUID);

                    Polyline line = new Polyline();
                    if (mSelectIndex == 0) {//第一个点
                        LatLngInfo selectedPt = mLinePointMer.get(mSelectIndex + 1);
                        LatLngInfo selectedPtMer = new LatLngInfo(selectedPt.latitude, selectedPt.longitude);
                        line.startPath(touchPoint);
                        line.lineTo(new Point(selectedPtMer.longitude, selectedPtMer.latitude));
                        mModifyPolylineUID = mDrawingModifyLayer.addGraphic(new Graphic(line, mEditPolylineSymbol));
                    } else if (mSelectIndex == mLinePointMer.size() - 1) {//最后一个点
                        LatLngInfo selectedPt = mLinePointMer.get(mSelectIndex - 1);
                        LatLngInfo selectedPtMer = new LatLngInfo(selectedPt.latitude, selectedPt.longitude);
                        line.startPath(new Point(selectedPtMer.longitude, selectedPtMer.latitude));
                        line.lineTo(touchPoint);
                        mModifyPolylineUID = mDrawingModifyLayer.addGraphic(new Graphic(line, mEditPolylineSymbol));
                    } else {
                        LatLngInfo selectedPt1 = mLinePointMer.get(mSelectIndex - 1);
                        LatLngInfo selectedPt1Mer = new LatLngInfo(selectedPt1.latitude, selectedPt1.longitude);
                        LatLngInfo selectedPt2 = mLinePointMer.get(mSelectIndex + 1);
                        LatLngInfo selectedPt2Mer = new LatLngInfo(selectedPt2.latitude, selectedPt2.longitude);
                        line.startPath(new Point(selectedPt1Mer.longitude, selectedPt1Mer.latitude));
                        line.lineTo(touchPoint);
                        line.lineTo(new Point(selectedPt2Mer.longitude, selectedPt2Mer.latitude));
                        mModifyPolylineUID = mDrawingModifyLayer.addGraphic(new Graphic(line, mEditPolylineSymbol));
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mModifyPointUID == -1)
                    break;
                if (mLinePointMer.size() == 1) {
                    mDrawingModifyLayer.updateGraphic(mModifyPointUID, touchPoint);
                    mLinePointMer.remove(mSelectIndex);
                    mLinePointMer.add(new LatLngInfo(touchPoint.getY(), touchPoint.getX()));
                    mCropPoints.add(new Point(touchPoint.getX(), touchPoint.getY()));
                    drawLine(mLinePointMer);
                } else {
                    mDrawingModifyLayer.removeAll();
                    mLinePointMer.remove(mSelectIndex);
                    mLinePointMer.add(mSelectIndex, new LatLngInfo(touchPoint.getY(), touchPoint.getX()));
                    mCropPoints.add(new Point(touchPoint.getX(), touchPoint.getY()));
                    drawLine(mLinePointMer);

                    if (mCurrentPathIndex < mPathSetCache.size() - 1) {
                        for (int i = mPathSetCache.size() - 1; i > mCurrentPathIndex; i--) {
                            mPathSetCache.remove(i);
                        }
                    }

                    mCurrentPathIndex++;
                    mPathSetCache.add(mCurrentPathIndex, getPathPoints(mLinePointMer));

                    if (ibRedo.getAlpha() == 1.0f) {
                        ibRedo.setAlpha(0.5f);
                    }
                }

                mSelectIndex = -1;
                mModifyPointUID = -1;
                isCancelTouch = false;
                updateStartFlag();
                updateFlightNumText();
                refreshBottomInfo();

                break;
        }
        return true;
    }

    private void drawLine(ArrayList<LatLngInfo> pointMerList) {
        mDrawingPointLayer.removeAll();
        mDrawingModifyLayer.removeAll();
        Polyline line = new Polyline();
        int size = pointMerList.size();
        for (int i = 0; i < size; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", i);
            map.put("type", 1);


            mDrawingPointLayer.addGraphic(new Graphic(new Point(pointMerList.get(i).longitude, pointMerList.get(i).latitude), mPointSymbol, map));
            if (i == 0)
                line.startPath(new Point(pointMerList.get(i).longitude, pointMerList.get(i).latitude));
            else
                line.lineTo(new Point(pointMerList.get(i).longitude, pointMerList.get(i).latitude));
        }
        if (size > 1)
            mModifyPolylineUID = mDrawingModifyLayer.addGraphic(new Graphic(line, mPolylineSymbol));
        drawTapAnnos(pointMerList);
    }


    /**
     * 获取线的长度画出长度值
     *
     * @param psSrc 墨卡托
     */
    private void drawTapAnnos(ArrayList<LatLngInfo> psSrc) {
        ArrayList<LatLngInfo> ps = new ArrayList<>();
        LatLngInfo homePoint = getCurrentLocation();
        if (homePoint != null && LocationCoordinateUtils.checkGpsCoordinate(homePoint.latitude, homePoint.longitude)) {
            ps.add(homePoint);
        }
        for (LatLngInfo latLngInfo : psSrc) {
            ps.add(CoordinateUtils.mapMercatorToGps84(latLngInfo.longitude, latLngInfo.latitude));
        }

        drawTextAnnos(mDrawingModifyLayer, ps);
    }

    
    @Override
    public void onClick(View v) {

        try {
            super.onClick(v);
            switch (v.getId()) {
                case R.id.imgBtnCircle:
                    if (flyCollectFragment == null || !flyCollectFragment.checkTaskFree())
                        return;
                    if (!bDrawPath) {

                        ToastUtil.show(getContext(), "点击地图添加拍摄位置点");
                        initDrawMode();
                        ibDrawCircle.setImageResource(R.drawable.ic_baseline_clear_24);
                        ibLoadTask.setEnabled(false);

                        ibUndo.setVisibility(View.VISIBLE);
                        ibRedo.setVisibility(View.VISIBLE);

                        ibUndo.setAlpha(0.5f);
                        ibRedo.setAlpha(0.5f);

                    } else {
                        ibDrawCircle.setImageResource(R.drawable.mission_library_type_waypoint);
                        ibLoadTask.setEnabled(true);
                        ibSaveTask.setEnabled(false);
                        clearPaintPoint(true);
                        SetFlightButton(false);
                        tvBottomInfo.setVisibility(View.GONE);

                        ibRedo.setVisibility(View.GONE);
                        ibUndo.setVisibility(View.GONE);
                        mCurrentPathIndex = -1;
                        mPathSetCache.clear();
                    }
                    bDrawPath = !bDrawPath;
                    break;
                case R.id.ib_undo:
                    if (flyCollectFragment == null || !flyCollectFragment.checkTaskFree())
                        return;
                    if (ibUndo.getAlpha() != 1) {
                        return;
                    }

                    if (mPathSetCache.size() == 0) {
                        return;
                    }

                    --mCurrentPathIndex;

                    ibRedo.setAlpha(1.0f);

                    if (mCurrentPathIndex < 0) {

                        clearPaintPoint(true);
                        ibUndo.setAlpha(0.5f);
                        return;

                    } else if (mCurrentPathIndex == 0) {

                        ibUndo.setAlpha(0.5f);

                    } else {
                        ibUndo.setAlpha(1.0f);
                    }

                    drawPathUndoOrRedo(mCurrentPathIndex);

                    break;

                case R.id.ib_redo:
                    if (flyCollectFragment == null || !flyCollectFragment.checkTaskFree())
                        return;
                    if (ibRedo.getAlpha() != 1) {
                        return;
                    }

                    if (mPathSetCache.size() == 0) {
                        return;
                    }

                    ++mCurrentPathIndex;

                    ibUndo.setAlpha(1.0f);

                    if (mCurrentPathIndex >= mPathSetCache.size() - 1) {
                        mCurrentPathIndex = mPathSetCache.size() - 1;
                        ibRedo.setAlpha(0.5f);
                    }

                    drawPathUndoOrRedo(mCurrentPathIndex);

                    break;
            }
        } catch (Exception ex) {
            log.e("public void onClick(View v):" + ex.getMessage());
        }
    }

    
    private void drawPathUndoOrRedo(int currentPathIndex) {
        mLinePointMer = getPathPoints(mPathSetCache.get(currentPathIndex));

        drawLine(mLinePointMer);

        updateStartFlag();
        updateFlightNumText();
        refreshBottomInfo();
        refreshChildInfo();


        flyCollectFragment.airRoutePara.setFixedAltitudeList("");
        flyCollectFragment.airRoutePara.setFixedAltitude(true);

        if (mLinePointMer.size() < 2) {//少于两个点则不能起飞
            SetFlightButton(false);
            tvBottomInfo.setVisibility(View.GONE);
        } else {
            SetFlightButton(true);
            tvBottomInfo.setVisibility(View.VISIBLE);
        }
    }

    
    public ArrayList<LatLngInfo> getPathPoints(ArrayList<LatLngInfo> inputPoints) {
        ArrayList<LatLngInfo> outputPoints = new ArrayList<>();

        for (int i = 0; i < inputPoints.size(); i++) {
            LatLngInfo p = inputPoints.get(i);
            outputPoints.add(p);
        }

        return outputPoints;
    }

    private void initDrawMode() {

        mDrawingPointLayer = new GraphicsLayer();
        mapView.addLayer(mDrawingPointLayer, 3);

        mDrawingModifyLayer = new GraphicsLayer();
        mapView.addLayer(mDrawingModifyLayer, 2);

        mLinePointMer = new ArrayList<>();
        mLinePointReverseMer = new ArrayList<>();

        mFlightNumPointListMer = new ArrayList<>();
        mPathSetCache = new ArrayList<>();
    }

    @Override
    protected void clearPaintPoint(boolean bRemoveAll) {
        super.clearPaintPoint(bRemoveAll);
        mSelectIndex = -1;
        if (mDrawingModifyLayer != null)
            mDrawingModifyLayer.removeAll();

        if (mDrawingPointLayer != null)
            mDrawingPointLayer.removeAll();

        if (mLinePointMer != null)
            mLinePointMer.clear();

        if (mLinePointReverseMer != null)
            mLinePointReverseMer.clear();

        if (mFlightNumPointListMer != null)
            mFlightNumPointListMer.clear();

    }

    @Override
    public void refreshChildInfo() {
        pointInfoContainerFragment.refreshChildInfo(mLinePointMer, flyCollectFragment.airRoutePara);
    }

    @Override
    public void refreshBottomInfo() {

        double flyTime = 0;

        ArrayList<LatLngInfo> latlngGps84 = new ArrayList<>();
        for (Point point : mFlightNumPointListMer) {
            LatLngInfo latLngInfo = CoordinateUtils.mercatorToLonLat(point.getX(), point.getY());
            latlngGps84.add(CoordinateUtils.getOffsetGcj02_To_Gps84(latLngInfo.latitude, latLngInfo.longitude));
        }

        if (currentMode == AppConstant.OperationMode.LinePatrolVideo) {
            flyTime = MyUtils.calculateFlyTime(getCurrentLocation(), latlngGps84, flyCollectFragment.airRoutePara, flyCollectFragment.returnMode, true);
        } else {
            flyTime = MyUtils.calculateFlyTime(getCurrentLocation(), latlngGps84, flyCollectFragment.airRoutePara, flyCollectFragment.returnMode, false);
        }

        this.fullImageFlyTime = flyTime;
        setBottomTextColor(flyTime, config.getAirCraftModel());
        if (flyTime < 0.01D)
            tvBottomInfo.setText(String.format("线路长度：%.0fm", MyUtils.calculateLinePatrolDistance(mFlightNumPointListMer)));
        else
            tvBottomInfo.setText(String.format("线路长度：%.0fm\n预计：%.2fmin", MyUtils.calculateLinePatrolDistance(mFlightNumPointListMer), flyTime));
    }

    @Override
    public void loadMission(Mission2 mission) {
        initDrawMode();
        flyCollectFragment.airRoutePara.setAltitude(mission.getAltitude());
        flyCollectFragment.airRoutePara.setFlySpeed(mission.getFlySpeed());
        flyCollectFragment.airRoutePara.setFixedAltitude(mission.isFixedAltitude());
        flyCollectFragment.airRoutePara.setRouteOverlap(mission.getRouteOverlap() / 100.0);
        flyCollectFragment.airRoutePara.setSideOverlap(mission.getSideOverlap() / 100.0);
        flyCollectFragment.airRoutePara.setGimbalAngle(mission.getGimbalAngle());
        flyCollectFragment.returnMode = mission.getReturnMode();
        flyCollectFragment.airRoutePara.setFixedAltitudeList(mission.getFixedAltitudeList());
        flyCollectFragment.airRoutePara.setEntryHeight(mission.getEntryHeight());
        if (mission.getWorkMode().contains("Video"))
            flyCollectFragment.actionMode = AppConstant.ACTION_MODE_VIDEO;
        else
            flyCollectFragment.actionMode = AppConstant.ACTION_MODE_PHOTO;

        if (mission.isFixedAltitude())
            flyCollectFragment.airRoutePara.setFixedAltitude(true);
        else
            flyCollectFragment.airRoutePara.setFixedAltitude(false);

        try {
            Polyline polyline = mission.getPolyLine();
            if (polyLine != null) {

                int size = polyline.getPointCount();

                for (int i = 0; i < size; i++) {
                    Point point = CoordinateUtils.getOffsetLoadMissionPoint(AppConstant.mapCoordinateType != AppConstant.WGS84_MAP_TYPE,
                            true, polyline.getPoint(i));
                    LatLngInfo realPoint = new LatLngInfo(point.getY(), point.getX());

                    mLinePointMer.add(realPoint);
                    mCropPoints.add(new Point(point.getX(), point.getY()));
                    if (i == 0)
                        mapView.centerAt(new Point(realPoint.longitude, realPoint.latitude), true);

                }
                updateStartFlag();
                updateFlightNumText();
                drawLine(mLinePointMer);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ibDrawCircle.setImageResource(R.drawable.icon_clear_selector);
                        bDrawPath = true;
                        SetFlightButton(true);
                        tvBottomInfo.setVisibility(View.VISIBLE);
                        ibLoadTask.setEnabled(false);
                        refreshBottomInfo();
                    }
                });
            }
        } catch (Exception ex) {
            log.e("loadMission Error:" + ex.getMessage());
            ToastUtil.show(getActivity(), "航飞任务载入失败");
        }
    }

    @Override
    public void reviewSelectedMissions(ArrayList<String> selectedIdList) {

        mReviewMissionLayer.removeAll();

        ibLoadTaskClear.setVisibility(selectedIdList.size() > 0 ? View.VISIBLE : View.GONE);

        for (String batchId : selectedIdList) {

            mDB.getMissionListByBatchId(batchId, new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {

                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {
                    List<Mission2> missionList = (List<Mission2>) result;
                    Mission2 mission = (missionList == null || missionList.size() == 0) ? null : missionList.get(0);

                    if (mission == null || mReviewMissionLayer == null) {
                        return;
                    }

                    Polyline polyline = mission.getPolyLine();

                    SimpleLineSymbol lineSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), Color.CYAN, DEFAULT_LINE_WIDTH);

                    if (polyline != null) {
                        Polyline line = new Polyline();
                        for (int i = 0; i < polyline.getPointCount(); i++) {
                            Point p = CoordinateUtils.getOffsetLoadMissionPoint(AppConstant.mapCoordinateType != AppConstant.WGS84_MAP_TYPE,
                                    true, polyline.getPoint(i));
                            LatLngInfo realPoint = new LatLngInfo(p.getY(), p.getX());
                            if (i == 0) {
                                mapView.centerAt(new Point(realPoint.longitude, realPoint.latitude), true);
                                line.startPath(realPoint.longitude, realPoint.latitude);
                            } else {
                                line.lineTo(realPoint.longitude, realPoint.latitude);
                            }
                        }


                        mReviewMissionLayer.addGraphic(new Graphic(line, lineSymbol));
                    }
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });
        }
    }

    @Override
    public void showSettingDialog() {

        ArrayList<LatLngInfo> routeInfoList = new ArrayList<>();
        if (mLinePointReverseMer != null && mLinePointReverseMer.size() != 0) {
            for (LatLngInfo latLngInfo : mLinePointReverseMer) {
                routeInfoList.add(CoordinateUtils.mercatorToLonLat(latLngInfo.longitude, latLngInfo.latitude));
            }
        } else {
            routeInfoList = null;
        }

        if (!flyCollectFragment.checkTaskFree())
            return;

        LinePatrolSettingDlgFragment channelDlg = new LinePatrolSettingDlgFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", flyCollectFragment.airRoutePara);
        bundle.putSerializable("routeInfoList", routeInfoList);
        bundle.putSerializable("aircraftLocation", new LatLngInfo(flyCollectFragment.aircraftLocationLatitude,
                flyCollectFragment.aircraftLocationLatitude));
        bundle.putInt("actionMode", flyCollectFragment.actionMode);
        bundle.putInt("returnMode", flyCollectFragment.returnMode);
        bundle.putInt("recordMode", flyCollectFragment.recodeMode);
        channelDlg.setArguments(bundle);
        channelDlg.show(getFragmentManager(), "line_patrol");
    }
}