package com.ew.autofly.mode.linepatrol;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esri.core.geometry.Polyline;
import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.constant.AppConstant.OperateAction;
import com.ew.autofly.dialog.EditMissionNameDialogFragment;
import com.ew.autofly.dialog.FlyCheckingDialogFragment;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.MissionBatch2;
import com.ew.autofly.entity.WayPointTask;
import com.ew.autofly.fragments.BaseCollectFragment;
import com.ew.autofly.interfaces.OnSettingDialogClickListener;
import com.ew.autofly.interfaces.TopFragmentClickListener;
import com.ew.autofly.internal.common.CheckError;
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint;
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction;
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.LogUtilsOld;
import com.flycloud.autofly.base.util.SysUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.CommonConstants.MapFragmentClickListener;
import com.ew.autofly.xflyer.utils.CoordinateUtils;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.ew.autofly.xflyer.utils.LatLngUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointTurnMode;
import dji.sdk.media.MediaFile;

import static com.ew.autofly.model.WayPointManager.addBlankWaypoint;

public class LinePatrolCollectFragment extends BaseCollectFragment implements TopFragmentClickListener, MapFragmentClickListener, OnSettingDialogClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        tvTopTitle.setText(getResources().getString(R.string.txt_line_patrol));
        config.setMode(5);
        actionMode = AppConstant.ACTION_MODE_VIDEO;
        fragMap = new LinePatrolGoogleMapFragment();
        ((LinePatrolGoogleMapFragment) fragMap).act = this;
        fragMap.currentMode = AppConstant.OperationMode.LinePatrolVideo;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.id_fragment_map, fragMap);
        ft.commit();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initDroneEvent() {
        super.initDroneEvent();
        initCameraLintener();
    }

    private void initCameraLintener() {
        if (isHasCamera()) {

            mProduct.getCamera().setMediaFileCallback(new MediaFile.Callback() {
                @Override
                public void onNewFile(@NonNull final MediaFile mediaFile) {
                    if (mediaFile.getMediaType() == MediaFile.MediaType.MP4 || mediaFile.getMediaType() == MediaFile.MediaType.MOV) {
                        String name = mediaFile.getFileName();
                        fileName = name.substring(0, name.indexOf("."));
                        writeDataIntoFile(fileName);
                    }

                    if ((mediaFile.getMediaType() == MediaFile.MediaType.JPEG ||
                            mediaFile.getMediaType() == MediaFile.MediaType.RAW_DNG) &&
                            operateAction == OperateAction.ExecuteTask) {

                        int index = mediaFile.getIndex();

                        if (index > 0 && currentPhotoIndex != index) {

                            currentPhotoIndex = index;
                            exposureNum++;

                          
                            updateMissionStart(new DataBaseUtils.onExecResult() {
                                @Override
                                public void execResult(boolean succ, String errStr) {
                                    if (succ) {
                                        currentMission2.setStartPhotoIndex(currentPhotoIndex);
                                        insertMissionPhoto(mediaFile);
                                    }
                                }

                                @Override
                                public void execResultWithResult(boolean succ, Object result, String errStr) {

                                }

                                @Override
                                public void setExecCount(int i, int count) {

                                }
                            });

                            if (exposureNum > 1) {
                                insertMissionPhoto(mediaFile);
                            }
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapFragmentClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnFlight:


                if (!checkTaskFree()) {
                    fragMap.SetFlightButton(true);
                    return;
                }
                CheckError error = checkCommonCondition();
                if (error != null) {
                    showToast(error.getDescription());
                    fragMap.SetFlightButton(true);
                    return;
                }
              /*  if (!checkEnoughFlyTimeAndBattery())
                    return;*/
                collectWayPointTask(((LinePatrolGoogleMapFragment) fragMap).mLinePointReverseMer);
                if (fragMap.bDrawPath) {
                    fragMap.screenShot();
                    prepareWayPoint(fragMap.wayPointTaskList);
                    fragMap.paintPoint();
                }
                if (totalFlyTask == 0) {
                    ToastUtil.show(getActivity(), "请先设置航点");
                    fragMap.SetFlightButton(true);
                    return;
                }

                setCameraParams();

                operateAction = OperateAction.SecurityCheck;

                FlyCheckingDialogFragment dialog = new FlyCheckingDialogFragment();
                dialog.show(getFragmentManager(), "line_check");

                fragMap.SetFlightButton(true);
                break;
            case R.id.imgBtnSaveTask:
                saveTask();
                break;
        }
    }

    private void collectWayPointTask(ArrayList<LatLngInfo> pointCache) {
        ArrayList<LatLngInfo> pointList = new ArrayList<>();
        for (LatLngInfo latLngInfo : pointCache) {
            LatLngInfo lonLat = CoordinateUtils.mercatorToLonLat(latLngInfo.longitude, latLngInfo.latitude);
            pointList.add(lonLat);
        }
        fragMap.wayPointTaskList.clear();
        String[] altitudeList = null;
        altitudeList = airRoutePara.getFixedAltitudeList().split(",");
        for (int i = 0; i < pointList.size(); i++) {
            LatLngInfo info = CoordinateUtils.getOffsetGcj02_To_Gps84(pointList.get(i).latitude, pointList.get(i).longitude);
            WayPointTask task = new WayPointTask();
            task.setPosition(info);
            task.setWayPointFlag(true);
            if (!airRoutePara.isFixedAltitude() && altitudeList.length == pointList.size()) {
                task.setAltitude(Double.valueOf(altitudeList[i]));
            } else {
                task.setAltitude(airRoutePara.getAltitude());
            }
            fragMap.wayPointTaskList.add(task);
        }
    }

    @Override
    public void onAttach(Context context) {
        log = LogUtilsOld.getInstance(getActivity()).setTag("ChannelPatrolCollectFragment");
        super.onAttach(context);
    }

    /**
     * 由规划路径产生的航点信息
     *
     * @param wayPointTaskList
     */
    public void prepareWayPoint(List<WayPointTask> wayPointTaskList) {
        if (wayPointTaskList == null || wayPointTaskList.size() == 0)
            return;

        WaypointMission.Builder builder = new WaypointMission.Builder();
        List<Waypoint> waypointList = new ArrayList<>();

      

        double firstAngle = LatLngUtils.getABAngle(wayPointTaskList.get(0).getPosition().latitude, wayPointTaskList.get(0).getPosition().longitude,
                homeLocationLatitude, homeLocationLongitude);
        int firstRealAngle = (int) firstAngle % 360;
        firstRealAngle = (firstRealAngle >= 180 ? firstRealAngle - 360 : firstRealAngle);

        Waypoint firstAnglePoint = addBlankWaypoint(false, homeLocationLatitude, homeLocationLongitude, (float) airRoutePara.getEntryHeight(), 0.0);
        firstAnglePoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, firstRealAngle));
        firstAnglePoint.actionTimeoutInSeconds = AppConstant.MAX_ACTION_TIMEOUT;
        waypointList.add(firstAnglePoint);

        if (airRoutePara.getEntryHeight() > airRoutePara.getAltitude() || airRoutePara.getEntryHeight() > wayPointTaskList.get(0).getAltitude()) {
            WayPointTask task = wayPointTaskList.get(0);
            waypointList.add(addBlankWaypoint(false, task.getPosition().latitude, task.getPosition().longitude, (float) airRoutePara.getEntryHeight(), 0.0));
        }

        Waypoint djiWaypoint = null;
        int kk = 0;
        int size = wayPointTaskList.size();
        for (WayPointTask wpt : wayPointTaskList) {

            if (kk < size - 1) {
                double iAngle = LatLngUtils.getABAngle(wayPointTaskList.get(kk + 1).getPosition().latitude, wayPointTaskList.get(kk + 1).getPosition().longitude,
                        wpt.getPosition().latitude, wpt.getPosition().longitude);
                iAngle += airRoutePara.getPlaneYaw();
                int realAngle = (int) iAngle % 360;
                realAngle = (realAngle >= 180 ? realAngle - 360 : realAngle);
                wpt.setHeadAngle(realAngle);
            }

            if (wpt.isWayPointFlag()) {
                FlyTask flyTask = wpt.getPosition().flyTask;
                djiWaypoint = new Waypoint(wpt.getPosition().latitude, wpt.getPosition().longitude, (float) wpt.getAltitude());
                djiWaypoint.turnMode = WaypointTurnMode.CLOCKWISE;
                djiWaypoint.gimbalPitch = flyTask.pitch;
                djiWaypoint.speed = flyTask.speed;

                djiWaypoint.altitude = 47;

                int stay = WaypointActionType.STAY.value();
                int startTakePhoto = WaypointActionType.START_TAKE_PHOTO.value();
                int startRecord = WaypointActionType.START_RECORD.value();
                int stopRecord = WaypointActionType.STOP_RECORD.value();
                int rotateAircraft = WaypointActionType.ROTATE_AIRCRAFT.value();
                int gimbalPitch = WaypointActionType.GIMBAL_PITCH.value();
                int cameraZoom = WaypointActionType.CAMERA_ZOOM.value();
                int cameraFocus = WaypointActionType.CAMERA_FOCUS.value();
                int photoGrouping = WaypointActionType.PHOTO_GROUPING.value();
                int fineTuneGimbalPitch = WaypointActionType.FINE_TUNE_GIMBAL_PITCH.value();
                int resetGimbalYaw = WaypointActionType.RESET_GIMBAL_YAW.value();
                ArrayList<FlyAreaPoint> flyAreaPoints = flyTask.flyAreaPoints;
                if (!flyAreaPoints.isEmpty()) {
                    ArrayList<FlyPointAction> flyPointActions = flyAreaPoints.get(0).flyPointActions;
                    for (FlyPointAction flyPointAction : flyPointActions) {
                        int action = flyPointAction.action;
                        if (action == stay) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.STAY, flyPointAction.staySecond * 1000));
                        } else if (action == startTakePhoto) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 0));
                        } else if (action == startRecord) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.START_RECORD, 0));
                        } else if (action == stopRecord) {

                        } else if (action == rotateAircraft) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, flyPointAction.rotateAircraft));
                        } else if (action == gimbalPitch) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, flyPointAction.gimbalPitch));
                        } else if (action == cameraZoom) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.CAMERA_ZOOM, 0));
                        } else if (action == cameraFocus) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.CAMERA_FOCUS, 0));
                        } else if (action == photoGrouping) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.PHOTO_GROUPING, 0));
                        } else if (action == fineTuneGimbalPitch) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.FINE_TUNE_GIMBAL_PITCH, 0));
                        } else if (action == resetGimbalYaw) {
                            djiWaypoint.addAction(new WaypointAction(WaypointActionType.RESET_GIMBAL_YAW, 0));
                        }
                    }
                }

                if (actionMode == AppConstant.ACTION_MODE_VIDEO) {
                    if (kk == 0)
                        djiWaypoint.addAction(new WaypointAction(WaypointActionType.START_RECORD, 0));
                    else if (kk == size - 1) {
                        djiWaypoint.addAction(new WaypointAction(WaypointActionType.STOP_RECORD, 0));
                    }
                } else {
                    if (kk != size - 1) {
                        djiWaypoint.shootPhotoTimeInterval = airRoutePara.getPhotoInterval();
                    }
                }
                if (kk != size - 1) {
                    djiWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, (int) wpt.getHeadAngle()));
                }
                djiWaypoint.actionTimeoutInSeconds = AppConstant.MAX_ACTION_TIMEOUT;
                waypointList.add(djiWaypoint);
            }
            kk++;
        }

        if (returnMode == AppConstant.RETURN_MODE_ORIGIN) {

            int returnSize = wayPointTaskList.size();

            Collections.reverse(wayPointTaskList);

            for (int i = 0; i < returnSize; i++) {

                int returnAngle = 0;

                if (i < returnSize - 1) {
                    returnAngle = (int) wayPointTaskList.get(i + 1).getHeadAngle();
                    returnAngle -= airRoutePara.getPlaneYaw();
                    returnAngle += 360;
                    returnAngle += 180;
                    returnAngle %= 360;
                    returnAngle = (returnAngle >= 180 ? returnAngle - 360 : returnAngle);
                }

              
                if (i == 0 && djiWaypoint != null) {
                    djiWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, returnAngle));
                    continue;
                }

                WayPointTask wpt = wayPointTaskList.get(i);
                if (wpt.isWayPointFlag()) {
                    Waypoint waypoint = new Waypoint(wpt.getPosition().latitude, wpt.getPosition().longitude, (float) wpt.getAltitude());
                    waypoint.turnMode = WaypointTurnMode.CLOCKWISE;
                    if (i != returnSize - 1) {
                        waypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, returnAngle));
                    }
                    waypoint.actionTimeoutInSeconds = AppConstant.MAX_ACTION_TIMEOUT;
                    waypointList.add(waypoint);
                }
            }
        }

      
        WayPointTask task = wayPointTaskList.get(size - 1);
        if (task.getAltitude() < airRoutePara.getEntryHeight())
            waypointList.add(addBlankWaypoint(false, task.getPosition().latitude, task.getPosition().longitude, (float) airRoutePara.getEntryHeight(), 0.0));

        builder.maxFlightSpeed(AppConstant.MAX_FLY_SPEED);
        if (actionMode == AppConstant.ACTION_MODE_VIDEO)
            builder.autoFlightSpeed((float) airRoutePara.getFlySpeed());
        else
            builder.autoFlightSpeed(AppConstant.MAX_FLY_SPEED);
        builder.finishedAction(WaypointMissionFinishedAction.GO_FIRST_WAYPOINT);
        builder.headingMode(WaypointMissionHeadingMode.USING_INITIAL_DIRECTION);
        builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);

        builder.waypointList(waypointList).waypointCount(waypointList.size());
        mDJIWayPointMission = builder.build();
        totalFlyTask = wayPointTaskList.size();
    }

    private void setCameraParams() {

        if (actionMode == AppConstant.ACTION_MODE_PHOTO) {
            setCameraShootPhoto();
        } else {
            setCameraRecordVideo();
        }
    }

    @Override
    protected void onStartMissionSuccess() {
        super.onStartMissionSuccess();
        if (actionMode == AppConstant.ACTION_MODE_VIDEO && recodeMode == AppConstant.START_RECODE_VIDEO) {
            startRecord();
        }
        collectMissionInfo2(DateHelperUtils.getDateSeries());
    }

    
    private void collectMissionInfo2(String name) {

        currentMission2 = new Mission2();
        currentMission2.setId(SysUtils.newGUID());
        currentMission2.setFlightNum(1);
        currentMission2.setRouteOverlap((int) (100 * airRoutePara.getRouteOverlap()));
        currentMission2.setSideOverlap((int) (100 * airRoutePara.getSideOverlap()));
        currentMission2.setAltitude(airRoutePara.getAltitude());
        currentMission2.setFixedAltitude(airRoutePara.isFixedAltitude());
        currentMission2.setCreateDate(new Date());
        currentMission2.setStartTime(null);
        currentMission2.setEndTime(null);
        currentMission2.setFlySpeed((int) airRoutePara.getFlySpeed());
        currentMission2.setGimbalAngle(airRoutePara.getGimbalAngle());
        currentMission2.setResolutionRate(airRoutePara.getResolutionRateByAltitude());
        currentMission2.setName(name);
        currentMission2.setSnapshot(fragMap.snapshotName);
        currentMission2.setWorkMode(fragMap.currentMode.toString());
        currentMission2.setWorkStep(null);
        currentMission2.setReturnMode(returnMode);
        currentMission2.setFixedAltitudeList(airRoutePara.getFixedAltitudeList());
        currentMission2.setEntryHeight(airRoutePara.getEntryHeight());



        Polyline polyline = new Polyline();
        int i = 0;
        for (LatLngInfo mer : ((LinePatrolGoogleMapFragment) fragMap).mLinePointReverseMer) {
            if (i == 0)
                polyline.startPath(mer.longitude, mer.latitude);
            else
                polyline.lineTo(mer.longitude, mer.latitude);
            i++;
        }

        if (AppConstant.mapCoordinateType == AppConstant.WGS84_MAP_TYPE) {
            Polyline p = new Polyline();
            for (int k = 0; k < polyline.getPointCount(); k++) {
                if (k == 0) {
                    p.startPath(CoordinateUtils.getOffsetLoadMissionPoint(false, false, polyline.getPoint(k)));
                } else {
                    p.lineTo(CoordinateUtils.getOffsetLoadMissionPoint(false, false, polyline.getPoint(k)));
                }
            }
            currentMission2.setPolyLine(p);
        } else {
            currentMission2.setPolyLine(polyline);
        }
        MissionBatch2 missionBatch2 = new MissionBatch2();
        missionBatch2.setId(SysUtils.newGUID());
        missionBatch2.setName(name);
        missionBatch2.setCreateDate(DateHelperUtils.string2DateTime(DateHelperUtils.getSystemTime()));
        missionBatch2.setSideOverlap((int) (airRoutePara.getSideOverlap() * 100));
        missionBatch2.setRouteOverlap((int) (airRoutePara.getRouteOverlap() * 100));
        missionBatch2.setResolutionRate(airRoutePara.getResolutionRateByAltitude());
        missionBatch2.setAltitude(airRoutePara.getAltitude());
        missionBatch2.setFixedAltitude(airRoutePara.isFixedAltitude());
        missionBatch2.setSnapShot(fragMap.snapshotName);
        missionBatch2.setWorkMode(fragMap.currentMode.toString());
        this.currentMissionBatch2 = missionBatch2;
        insertMissionToDB2();
    }

    private void insertMissionToDB2() {
        mDB.insertMission2(1, currentMissionBatch2, currentMission2, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(boolean succ, String errStr) {
                if (succ)
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, "任务保存到数据库成功"));
                else {
                    handler.sendMessage(handler.obtainMessage(SHOW_TOAST, "任务保存到数据库失败"));
                    log.e("任务保存到数据库失败:" + errStr);
                }
            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {

            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });
    }

    /**
     * 得到当前的Home点(GPS84)
     *
     * @return
     */
    public LatLngInfo getHomePoint() {
        if (this.getHomePointFlag && homeLocationLatitude != -1 && homeLocationLatitude != 0 && homeLocationLongitude != -1)
            return new LatLngInfo(this.homeLocationLatitude, this.homeLocationLongitude);
        else
            return null;
    }

    @Override
    public void onSettingDialogConfirm(String tag, Object obj, AirRouteParameter params) {
        if (tag.equals("pointInfoBasicFragment")) {
            this.airRoutePara.setAltitude(params.getAltitude());
            this.airRoutePara.setRouteOverlap(params.getRouteOverlap());
            this.airRoutePara.setFixedAltitude(params.isFixedAltitude());
            this.airRoutePara.setGimbalAngle(params.getGimbalAngle());
            HashMap<String, Integer> map = (HashMap<String, Integer>) obj;
            this.actionMode = map.get("actionMode");
            this.returnMode = map.get("returnMode");
            this.recodeMode = map.get("recordMode");
            if (this.actionMode == AppConstant.ACTION_MODE_VIDEO) {
                this.airRoutePara.setFlySpeed(params.getFlySpeed());
                if (fragMap != null)
                    fragMap.currentMode = AppConstant.OperationMode.LinePatrolVideo;
            } else {
                if (fragMap != null)
                    fragMap.currentMode = AppConstant.OperationMode.LinePatrolPhoto;
            }
            if (fragMap != null && ((LinePatrolGoogleMapFragment) fragMap).mLinePointMer != null && ((LinePatrolGoogleMapFragment) fragMap).mLinePointMer.size() > 1)
                fragMap.refreshBottomInfo();
        }
    }

    
    private void saveTask() {
        EditMissionNameDialogFragment editMissionNameDialogFragment = new EditMissionNameDialogFragment();
        editMissionNameDialogFragment.show(getActivity().getFragmentManager(), "编辑任务名称");
        editMissionNameDialogFragment.setOnSureButtonClickListener(new EditMissionNameDialogFragment.onSureButtonClickListener() {
            @Override
            public void sure(final String name) {
                fragMap.screenShot();
                collectMissionInfo2(name);
            }
        });
    }

    private StringBuilder stringBuilder = new StringBuilder();
    private Timer timer;
    private long currentTimeMillis;
    private String fileName = "";

    @Override
    public void startRecordPoint() {
        stringBuilder.setLength(0);
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isRecording) {
                    currentTimeMillis = System.currentTimeMillis();
                    isRecording = true;
                }
                stringBuilder.append(System.currentTimeMillis() - currentTimeMillis).append(" ").append(aircraftLocationLongitude).append(" ").append(aircraftLocationLatitude).append("\n");
            }
        }, 0, 200);
        super.startRecordPoint();
    }

    @Override
    public void finishRecordPoint() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;

        }
        super.finishRecordPoint();
    }

    private void writeDataIntoFile(String name) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        DataOutputStream dos = null;
        try {
            if (name != null)
                dos = new DataOutputStream(new FileOutputStream(AppConstant.LOG_PATH + File.separator + name + ".bin"));
            else
                dos = new DataOutputStream(new FileOutputStream(AppConstant.LOG_PATH + File.separator + format.format(new Date()) + ".bin"));

            dos.writeUTF("时间：" + format.format(new Date()));

            if (mProduct == null || mProduct.getModel() == null)
                dos.writeUTF("机型：" + "Unknow Aircraft");
            else
                dos.writeUTF("机型：" + mProduct.getModel().getDisplayName());
            dos.writeUTF(stringBuilder.toString());
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}