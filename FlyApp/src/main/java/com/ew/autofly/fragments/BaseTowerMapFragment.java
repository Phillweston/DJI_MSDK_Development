package com.ew.autofly.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;
import com.ew.autofly.BuildConfig;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.TowerDetailDlgFragment;
import com.ew.autofly.dialog.common.ImportTowerLineDialog;
import com.ew.autofly.dialog.tower.AddNewTowerDialogFragment;
import com.ew.autofly.dialog.tower.ChooseNewTowerPositionDlgFragment;
import com.ew.autofly.dialog.tower.ChooseTowerDlgFragment;
import com.ew.autofly.dialog.tower.SaveTowerGroupDialogFragment;
import com.ew.autofly.dialog.tower.UpdateTowerNoDialogFragment;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.entity.geometry.GeoPoint;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.interfaces.OnChooseTowerDialogClickListener;
import com.ew.autofly.utils.FontUtils;
import com.ew.autofly.utils.IOUtils;
import com.flycloud.autofly.base.util.SysUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.arcgis.ArcgisMapUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.utils.io.excel.ExcelHelper;
import com.ew.autofly.utils.io.excel.JxlExcelHelper;
import com.ew.autofly.utils.io.file.FileInfo;
import com.ew.autofly.utils.io.file.FileUtils;
import com.ew.autofly.utils.io.kml.KmlHelper;
import com.ew.autofly.widgets.controls.ImageMarkerSymbol;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dji.thirdparty.rx.Observable;
import dji.thirdparty.rx.Subscriber;
import dji.thirdparty.rx.Subscription;
import dji.thirdparty.rx.android.schedulers.AndroidSchedulers;
import dji.thirdparty.rx.functions.Func1;
import dji.thirdparty.rx.schedulers.Schedulers;

import static com.ew.autofly.dialog.common.ImportTowerLineDialog.PARAMS_SELECTED_FILES;
import static com.ew.autofly.dialog.common.ImportTowerLineDialog.PARAMS_SELECTED_TYPE;
import static com.ew.autofly.dialog.common.ImportTowerLineDialog.TAG_APPEND;
import static com.ew.autofly.dialog.common.ImportTowerLineDialog.TAG_IMPORT;
import static com.ew.autofly.dialog.common.ImportTowerLineDialog.TAG_NEW;
import static com.ew.autofly.dialog.common.ImportTowerLineDialog.TYPE_EXCEL;
import static com.ew.autofly.dialog.common.ImportTowerLineDialog.TYPE_KML;
import static com.ew.autofly.dialog.tower.ChooseNewTowerPositionDlgFragment.PARAMS_APPEND_TOWER;
import static com.ew.autofly.dialog.tower.ChooseNewTowerPositionDlgFragment.PARAMS_TOWER_LIST;
import static com.ew.autofly.dialog.tower.ChooseTowerDlgFragment.ARG_PARAM_CHOOSE_TOWER_AIRCRAFT_LOCATION;
import static com.ew.autofly.dialog.tower.ChooseTowerDlgFragment.ARG_PARAM_CHOOSE_TOWER_LIST;
import static com.ew.autofly.dialog.tower.ChooseTowerDlgFragment.TAG_CHOOSE_TOWER;

/**
 * 注：准备弃用，请不要继承此类
 * 杆塔路线基类（需要导入杆塔进行操作继承此基类）
 */
@Deprecated
public class BaseTowerMapFragment extends BaseMapFragment implements IBaseTowerView, ImportTowerLineDialog.OnImportTowerLineListener,
        OnChooseTowerDialogClickListener, View.OnClickListener {

    protected ImageView ivNew, ivSave, ivCancel;


    protected GraphicsLayer mTowerLayer;


    protected GraphicsLayer mTowerNoLayer;


    protected GraphicsLayer mTowerWireLayer;


    protected GraphicsLayer mAddTowerLayer;

    protected GraphicsLayer mChangeTowerLayer = null;


    private GraphicsLayer mTowerAltitudeLayer = null;



    protected ArrayList<Tower> mTowerList = new ArrayList<>();

    public ArrayList<Tower> getTowerList() {
        return mTowerList;
    }


    protected List<Tower> mAddTowerList = new ArrayList<>();


    protected ArrayList<Tower> mSelectedTowerList = new ArrayList<>();


    protected boolean bDrawTower;

    public boolean isDrawingTower() {
        return bDrawTower;
    }


    private boolean isUpdatingTower = false;

    public boolean isUpdatingTower() {
        return isUpdatingTower;
    }

    private String mUpdateTowerFileName = "";


    protected List<LatLngInfo> wayPointMercatorList = new ArrayList<>();

    public ArrayList<Tower> getSelectedTowerList() {
        return mSelectedTowerList;
    }


    protected ArrayList<FileInfo> mSelectTowerLineFiles = new ArrayList<>();
    protected int mSelectTowerLineFilesType = TYPE_KML;


    @Override
    protected void initMap(View view) {
        super.initMap(view);

        llAddNewTower = (LinearLayout) view.findViewById(R.id.ll_add_new_tower);
        ivNew = (ImageView) view.findViewById(R.id.iv_new_tower);
        ivSave = (ImageView) view.findViewById(R.id.iv_save_tower);
        ivCancel = (ImageView) view.findViewById(R.id.iv_cancel_save_tower);

        ivNew.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        ivCancel.setOnClickListener(this);

        mapView.setOnTouchListener(new MapOnTouchListener(getContext(), mapView) {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                refreshAltitude();
                return super.onTouch(v, event);
            }

            @Override
            public boolean onSingleTap(MotionEvent event) {
                onMapSingleTap(event);
                return super.onSingleTap(event);
            }

            @Override
            public void onLongPress(MotionEvent event) {
                super.onLongPress(event);
                onMapLongPress(event);
            }
        });
    }

    @Override
    protected void initMapGraphicsLayer() {


        mTowerWireLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mTowerWireLayer);

        mTowerLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mTowerLayer);

        mTowerNoLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mTowerNoLayer);

        mTowerAltitudeLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mTowerAltitudeLayer);

        mChangeTowerLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mChangeTowerLayer);

        mAddTowerLayer = new GraphicsLayer(GraphicsLayer.RenderingMode.STATIC);
        mapView.addLayer(mAddTowerLayer);

        super.initMapGraphicsLayer();
    }

    protected void onMapSingleTap(MotionEvent event) {

        if (!flyCollectFragment.checkTaskFree()) {
            return;
        }

        Tower tower = getTouchSelectedTower(event);
        if (tower != null) {
            showUpdaterTowerNoDialog(tower);
        }
    }

    protected void onMapLongPress(MotionEvent event) {

        if (!flyCollectFragment.checkTaskFree()) {
            return;
        }

        Tower tower = getTouchSelectedTower(event);
        if (tower != null) {
            showTowerDetailDialog(tower);
        }
    }

    /**
     * 获取点击选中的杆塔
     *
     * @param event
     * @return
     */
    private Tower getTouchSelectedTower(MotionEvent event) {

        if (flyCollectFragment == null)
            return null;

        if (checkIfModifyingTower() || mTowerList == null || mTowerList.isEmpty())
            return null;

        try {

            int[] vPoint = mTowerLayer.getGraphicIDs(event.getX(), event.getY(), 10);

            if (vPoint.length > 0) {
                if (flyCollectFragment.checkTaskFree()) {

                    Graphic g = mTowerLayer.getGraphic(vPoint[0]);
                    Object object = g.getAttributeValue("index");

                    if (object != null) {
                        return mTowerList.get(Integer.parseInt(object.toString()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void onLongClickDrawCircle(){
        if (flyCollectFragment == null || !flyCollectFragment.checkTaskFree())
            return;
        if (!bDrawTower) {
            showImportTowerDialog();
        }
    }

    public void onClickDrawCircle(){
        if (flyCollectFragment == null || !flyCollectFragment.checkTaskFree())
            return;
        if (checkModifyTowerView())
            return;
        if (bDrawTower) {
            clearTowerGridLine();
        }
    }

    @Override
    public void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.iv_new_tower:
                if (EWApplication.getAircraftInstance() != null
                        && LocationCoordinateUtils.checkGpsCoordinate(((BaseTowerCollectFragment) flyCollectFragment).getPlaneLatitude(),
                        ((BaseTowerCollectFragment) flyCollectFragment).getPlaneLongitude())) {
                    AddNewTowerDialogFragment dlg = new AddNewTowerDialogFragment();
                    Bundle args1 = new Bundle();
                    args1.putSerializable("tower_list", (Serializable) mAddTowerList);
                    args1.putDouble("latitude", ((BaseTowerCollectFragment) flyCollectFragment).getPlaneLatitude());
                    args1.putDouble("longitude", ((BaseTowerCollectFragment) flyCollectFragment).getPlaneLongitude());
                    dlg.setArguments(args1);
                    dlg.setListener(new AddNewTowerDialogFragment.AddNewTowerListener() {
                        @Override
                        public void OnAddNewTowerConfirm(boolean replace, final Tower tower, int currentTowerIndex) {
                            if (!replace) {
                                if (isUpdatingTower()) {
                                    ChooseNewTowerPositionDlgFragment cnpDlg = new ChooseNewTowerPositionDlgFragment();
                                    Bundle cnpArgs = new Bundle();
                                    cnpArgs.putSerializable(PARAMS_TOWER_LIST, (Serializable) mAddTowerList);
                                    cnpArgs.putParcelable(PARAMS_APPEND_TOWER, tower);
                                    cnpDlg.setArguments(cnpArgs);
                                    cnpDlg.setOnSelectedPositionListener(new ChooseNewTowerPositionDlgFragment.OnSelectedPositionListener() {
                                        @Override
                                        public void onSelected(int position) {
                                            mAddTowerList.add(position, tower);
                                            refreshAddTowerGridLine(false);
                                        }
                                    });
                                    cnpDlg.show(getFragmentManager(), "choose_new_tower_position");
                                } else {
                                    mAddTowerList.add(tower);
                                    refreshAddTowerGridLine(false);
                                }
                            } else {
                                mAddTowerList.set(currentTowerIndex, tower);
                                refreshAddTowerGridLine(false);
                            }
                        }
                    });
                    dlg.show(getFragmentManager(), "add_new_tower");
                } else {
                    ToastUtil.show(EWApplication.getInstance(), "请检查飞机连接状态");
                }
                break;
            case R.id.ib_choose_tower:
                if (flyCollectFragment == null || !flyCollectFragment.checkTaskFree())
                    return;
                showChoseTowerDialog();
                break;
            case R.id.iv_cancel_save_tower:
                if (mAddTowerList != null && !mAddTowerList.isEmpty()) {
                    showDropSaveTowerDialog();
                } else {
                    showEditTowerView(false);
                }
                break;
            case R.id.iv_save_tower:
                if (mAddTowerList.size() == 0) {
                    ToastUtil.show(EWApplication.getInstance(), "请先添加杆塔！");
                    return;
                }
                if (isUpdatingTower) {
                    saveUpdateTowerGridLine(mUpdateTowerFileName);
                } else {
                    SaveTowerGroupDialogFragment dlg2 = new SaveTowerGroupDialogFragment();
                    dlg2.setListener(new SaveTowerGroupDialogFragment.SaveTowerGroupListener() {
                        @Override
                        public void onSaveTowerGroupConfirm(String lineName, String voltage, String groupName) {
                            saveNewTowerGridLine(lineName, voltage, groupName);
                        }
                    });
                    dlg2.show(getFragmentManager(), "save_line");
                }
                break;
        }
    }

    @Override
    public void onImportTowerLine(String tag, int type, Object object) {
        mSelectTowerLineFilesType = type;
        switch (tag) {
            case TAG_IMPORT:
                mSelectTowerLineFiles = (ArrayList<FileInfo>) object;
                importTowerGridLine();
                break;
            case TAG_NEW:
                showEditTowerView(true);
                break;
            case TAG_APPEND:
                List<FileInfo> fileInfos = (ArrayList<FileInfo>) object;
                showEditTowerView(true);
                FileInfo fileInfo = fileInfos.get(0);
                mAddTowerList.clear();
                mAddTowerList.addAll(loadTowerListByFile(fileInfo));
                mUpdateTowerFileName = fileInfo.getFileNameWithoutSuffix();
                isUpdatingTower = true;
                refreshAddTowerGridLine(true);
                break;

        }
    }

    @Override
    public void showSettingDialog() {

    }

    @Override
    public void showImportTowerDialog() {

        ImportTowerLineDialog importDlg = new ImportTowerLineDialog();
        importDlg.setImportTowerLineListener(this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAMS_SELECTED_FILES, mSelectTowerLineFiles);
        bundle.putInt(PARAMS_SELECTED_TYPE, TYPE_KML);
        importDlg.setArguments(bundle);
        importDlg.show(getFragmentManager(), "ImportTowerLineDialog");
    }

    @Override
    public void showChoseTowerDialog() {
        ChooseTowerDlgFragment towerDlg = new ChooseTowerDlgFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_PARAM_CHOOSE_TOWER_LIST, mTowerList);
        bundle.putParcelable(ARG_PARAM_CHOOSE_TOWER_AIRCRAFT_LOCATION,
                new LocationCoordinate(((BaseTowerCollectFragment) flyCollectFragment).getPlaneLatitude(),
                        ((BaseTowerCollectFragment) flyCollectFragment).getPlaneLongitude()));
        towerDlg.setArguments(bundle);
        towerDlg.setOnChooseTowerListener(this);
        towerDlg.show(getFragmentManager(), TAG_CHOOSE_TOWER);
    }

    @Override
    public void showTowerDetailDialog(final Tower tower) {

        try {

            final TowerDetailDlgFragment towerDetailDlg = new TowerDetailDlgFragment();
            Bundle args = new Bundle();
            args.putParcelable("tower", tower);
            args.putDouble("latitude", ((BaseTowerCollectFragment) flyCollectFragment).getPlaneLatitude());
            args.putDouble("longitude", ((BaseTowerCollectFragment) flyCollectFragment).getPlaneLongitude());
            towerDetailDlg.setArguments(args);
            towerDetailDlg.setClickListener(new TowerDetailDlgFragment.onTowerDetailClickListener() {
                @Override
                public void onUpdateLocation(Tower t) {

                    updateTowerLocation(t);
                }
            });
            towerDetailDlg.show(getFragmentManager(), "tower_detail");

        } catch (Exception e) {
            ToastUtil.show(getContext(), "杆塔出错");
        }
    }

    @Override
    public void showUpdaterTowerNoDialog(final Tower tower) {

        UpdateTowerNoDialogFragment dlg = new UpdateTowerNoDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("tower_list", mTowerList);
        dlg.setArguments(bundle);
        dlg.setOnConfirmListener(new IConfirmListener() {
            @Override
            public void onConfirm(String tag, Object object) {

                String towerNoStr = (String) object;
                updateTowerNo(tower, towerNoStr);
            }
        });
        dlg.show(getFragmentManager(), "update_tower_no");
    }


    @Override
    public void importTowerGridLine() {
        ibDrawCircle.setImageResource(R.drawable.icon_clear_selector);
        bDrawTower = true;
        ibChooseTower.setVisibility(View.VISIBLE);
        drawTowerGridLines();
    }

    @Override
    public void clearTowerGridLine() {
        bDrawTower = false;
        this.mSelectTowerLineFiles.clear();
        ibChooseTower.setVisibility(View.GONE);
        mSelectedTowerList.clear();
        mTowerList.clear();

        clearPaintPoint(true);
        tvBottomInfo.setVisibility(View.GONE);
        SetFlightButton(false);
    }

    @Override
    public void refreshDrawTowerGriLines() {

        showLoadProgressDialog("正在更新杆塔……");

        clearPaintPoint(true);

        compareSelectedFiles(new ICompareListener() {
            @Override
            public void onCompare(List<Tower> compareList) {
                if (compareList != null && !compareList.isEmpty()) {
                    try {
                        drawTowerGridLine(compareList, false);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("绘制杆塔错误");
                            }
                        });
                    }
                }
            }
        });

        dismissLoadProgressDialog();
    }

    @Override
    public void refreshDrawTowerGriLinesAltitude() {

        mTowerAltitudeLayer.removeAll();

        if (mTowerList != null && !mTowerList.isEmpty()) {
            drawTowerGridLineAltitude(mTowerList);
        }
    }

    @Override
    public void refreshAddTowerGridLine(boolean isMoveToCenter) {

        showLoadProgressDialog("正在添加杆塔……");

        mAddTowerLayer.removeAll();
        ImageMarkerSymbol symbol = ArcgisMapUtil.getImageMarkerSymbol(getContext(), R.drawable.icon_tower);


        boolean isFirst = true;
        Polyline polyline = new Polyline();
        for (Tower t : mAddTowerList) {

            LatLngInfo mercator = CoordinateUtils.gps84ToMapMercator(t.getLongitude(), t.getLatitude());

            if (isFirst) {
                isFirst = false;
                polyline.startPath(mercator.longitude, mercator.latitude);
            } else {
                polyline.lineTo(mercator.longitude, mercator.latitude);
            }
        }
        SimpleLineSymbol lineSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), Color.parseColor("#ffa800"), 2.0f);
        mAddTowerLayer.addGraphic(new Graphic(polyline, lineSymbol));


        for (int i = 0; i < mAddTowerList.size(); i++) {
            Tower tower1 = mAddTowerList.get(i);
            Map<String, Object> attr = new HashMap<>();
            attr.put("index", i);

            final LatLngInfo mercator1 = CoordinateUtils.gps84ToMapMercator(tower1.getLongitude(), tower1.getLatitude());

            mAddTowerLayer.addGraphic(new Graphic(new Point(mercator1.longitude, mercator1.latitude), symbol, attr));
            if (i == 0 && isMoveToCenter) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapView.centerAt(new Point(mercator1.longitude, mercator1.latitude), true);

                    }
                });
            }

            Graphic textGraphic = getTextGraphic(tower1.getTowerNo(), mercator1);

            mAddTowerLayer.addGraphic(textGraphic);
        }

        dismissLoadProgressDialog();
    }

    @Override
    public void repaintTaskPathByTower() {

    }

    public Graphic getTextGraphic(String towerNo, LatLngInfo mercator) {

        Graphic graphicAnno;
        if (BuildConfig.ARCGIS_TEXT_ENABLE) {
            TextSymbol towerText = ArcgisMapUtil.getTextSymbol(getContext(), (int) getResources().getDimension(R.dimen.map_app_text_size),
                    towerNo, Color.argb(255, 255, 165, 0));
            towerText.setOffsetX(ArcgisMapUtil.getEqualRatioFactor(getContext()) * 20);
            towerText.setOffsetY(ArcgisMapUtil.getEqualRatioFactor(getContext()) * 20);
            graphicAnno = new Graphic(new Point(mercator.longitude, mercator.latitude), towerText);
        } else {
            PictureMarkerSymbol towerText = ArcgisMapUtil.getImageMarkerSymbol(getContext(), FontUtils.getImage(getActivity(), 300, 36, towerNo, 24));
            towerText.setOffsetX(ArcgisMapUtil.getEqualRatioFactor(getContext()) * 20);
            towerText.setOffsetY(ArcgisMapUtil.getEqualRatioFactor(getContext()) * 48);
            graphicAnno = new Graphic(new Point(mercator.longitude, mercator.latitude), towerText);
        }

        return graphicAnno;
    }

    /**
     * 绘制单条杆塔路线
     *
     * @param towerList
     */
    @Override
    public synchronized void drawTowerGridLine(List<Tower> towerList, boolean isMoveCenter) throws Throwable {

        int kk = 0;
        SimpleLineSymbol wireSymbol = ArcgisMapUtil.getSimpleLineSymbol(getContext(), Color.argb(255, 255, 165, 0), DEFAULT_LINE_WIDTH);
        Polyline polyline = new Polyline();


        Graphic[] wireGraphics = new Graphic[towerList.size()];


        Graphic[] towerGraphics = new Graphic[towerList.size()];


        Graphic[] towerNoGraphics = new Graphic[towerList.size()];

        Point startPoint = null;

        for (Tower tower : towerList) {

            final LatLngInfo mercator = CoordinateUtils.gps84ToMapMercator(tower.getLongitude(), tower.getLatitude());

            if (kk == 0) {
                startPoint = new Point(mercator.longitude, mercator.latitude);
                polyline.startPath(mercator.longitude, mercator.latitude);
            } else
                polyline.lineTo(mercator.longitude, mercator.latitude);

            wireGraphics[kk] = new Graphic(polyline, wireSymbol);

            Graphic textGraphic = getTextGraphic(tower.getTowerNo(), mercator);

            towerNoGraphics[kk] = textGraphic;


            int towerMakerResId = R.drawable.icon_tower;
            ImageMarkerSymbol towerMaker = ArcgisMapUtil.getImageMarkerSymbol(getContext(), towerMakerResId);

            Map<String, Object> map = new HashMap<>();
            map.put("index", kk);

            Graphic towerGraphic = new Graphic(new Point(mercator.longitude, mercator.latitude), towerMaker, map);

            towerGraphics[kk] = towerGraphic;

            kk++;
        }

        mTowerWireLayer.addGraphics(wireGraphics);
        mTowerNoLayer.addGraphics(towerNoGraphics);
        mTowerLayer.addGraphics(towerGraphics);


        final Point centerPoint = startPoint;

        if (isMoveCenter && getActivity() != null && centerPoint != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mapView.centerAt(centerPoint, true);
                }
            });
        }

        drawTowerGridLineAltitude(towerList);

    }

    @Override
    public void refreshLayerVisibility() {
        super.refreshLayerVisibility();
        if (getActivity() != null) {
            if (mapView.getScale() > 20000 / ArcgisMapUtil.getEqualFactor(getActivity())) {
                mTowerNoLayer.setVisible(false);
                mTowerAltitudeLayer.setVisible(false);
            } else {
                mTowerNoLayer.setVisible(true);
                mTowerAltitudeLayer.setVisible(true);
            }
        }
    }

    @Override
    public void drawTowerGridLineAltitude(List<Tower> towerList) {


        Graphic[] altitudeGraphics = new Graphic[towerList.size()];

        int kk = 0;
        for (Tower tower : towerList) {


            double altitude = demReaderUtils.getZValue(new LatLngInfo(tower.getLatitude(), tower.getLongitude()));
            String altitude_txt = demReaderUtils.checkZValue(altitude) ? String.format("h:%.0fm", altitude) : "h:n/a";

            if (!flyCollectFragment.airRoutePara.isFixedAltitude()) {
                for (Tower selectedTower : mSelectedTowerList) {
                    if (tower.getTowerId().equals(selectedTower.getTowerId())) {
                        altitude_txt = altitude_txt + String.format("(%d)", tower.getFlyAltitude() == 0 ? flyCollectFragment.airRoutePara.getAltitude() : tower.getFlyAltitude());
                    }
                }
            }

            TextSymbol symbol = ArcgisMapUtil.getTextSymbol(getContext(), (int) getResources().getDimension(R.dimen.map_app_text_size), altitude_txt, Color.RED);
            symbol.setOffsetX(-1 * ArcgisMapUtil.getEqualFactor(getContext()) * SysUtils.dip2px(getActivity(), altitude_txt.length() * 2));
            symbol.setOffsetY(ArcgisMapUtil.getEqualFactor(getContext()) * -50);
            final LatLngInfo mercator = CoordinateUtils.gps84ToMapMercator(tower.getLongitude(), tower.getLatitude());
            Graphic altitude_graphic = new Graphic(new Point(mercator.longitude, mercator.latitude), symbol);
            altitudeGraphics[kk] = altitude_graphic;
            kk++;
        }

        mTowerAltitudeLayer.addGraphics(altitudeGraphics);
    }

    @Override
    public void saveNewTowerGridLine(String lineName, String voltage, String groupName) {

        showLoadProgressDialog("正在保存……");

        for (Tower tower : mAddTowerList) {
            tower.setGridLineName(lineName);
            tower.setVoltage(voltage);
            tower.setManageGroup(groupName);
        }
        saveTowerGridLine(lineName, true);

     /*   try {
            TowerGridLineDao gridLineDao = EWApplication.getInstance().getDaoSession().getTowerGridLineDao();
            TowerGridLine gridLine = gridLineDao.queryBuilder().where(TowerGridLineDao.Properties.LineName.eq(lineName)).unique();
            if (gridLine != null) {
                gridLine.setVoltage(voltage);
                gridLine.setGroupName(groupName);
                gridLineDao.update(gridLine);
            } else {
                TowerGridLine newGridLine = new TowerGridLine();
                newGridLine.setLineName(lineName);
                newGridLine.setVoltage(voltage);
                newGridLine.setGroupName(groupName);
                gridLineDao.insert(newGridLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
        dismissLoadProgressDialog();
    }

    @Override
    public void saveUpdateTowerGridLine(final String lineName) {

        showLoadProgressDialog("正在更新杆塔……");

        saveTowerGridLine(lineName, false);

        refreshDrawTowerGriLines();

        dismissLoadProgressDialog();

    }

    
    public void drawTowerGridLines() {


        Subscription subscription = Observable.from(this.mSelectTowerLineFiles).concatMap(new Func1<FileInfo, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(final FileInfo fileInfo) {

                return Observable.create(new Observable.OnSubscribe<Boolean>() {

                    @Override
                    public void call(final Subscriber<? super Boolean> subscriber) {

                        try {
                            List<Tower> towers = loadTowerListByFile(fileInfo);
                            mTowerList.addAll(towers);
                            drawTowerGridLine(towers, true);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }

                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io());

            }
        }).observeOn(AndroidSchedulers.mainThread())
                .takeLast(1)
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onStart() {
                        showLoadProgressDialog("正在绘制杆塔……");
                        mTowerList.clear();
                    }

                    @Override
                    public void onCompleted() {
                        dismissLoadProgressDialog();
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

    /**
     * 显示新增/追加杆塔
     *
     * @param isShow
     */
    protected void showEditTowerView(boolean isShow) {
        if (!isShow) {
            mSelectTowerLineFiles.clear();
            mAddTowerList.clear();
            mAddTowerLayer.removeAll();
            isUpdatingTower = false;
            mUpdateTowerFileName = "";
        }


        rlRightButton.setVisibility(isShow ? View.GONE : View.VISIBLE);
        llAddNewTower.setVisibility(isShow ? View.VISIBLE : View.GONE);

        if (CAMERA_MAP_SWITCH) {
            rlRightButton.setVisibility(View.GONE);
        }
    }

    /**
     * 检查是否正在新增/追加杆塔
     *
     * @return
     */
    public boolean checkIfModifyingTower() {
        return llAddNewTower.getVisibility() == View.VISIBLE;
    }

    /**
     * 检查是否关闭新增/追加杆塔View
     *
     * @return
     */
    protected boolean checkModifyTowerView() {
        boolean flag = checkIfModifyingTower();
        if (flag)
            showDropSaveTowerDialog();
        return flag;
    }

    public void showDropSaveTowerDialog() {
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getContext());
        deleteDialog.setTitle("提醒")
                .setMessage("是否放弃保存杆塔文件?")
                .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showEditTowerView(false);
                    }
                })
                .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onChooseTowerConfirm(String tag, Object object) {

    }

    @Override
    protected void clearPaintPoint(boolean bRemoveAll) {
        super.clearPaintPoint(bRemoveAll);

        if (this.mTowerLayer != null)
            this.mTowerLayer.removeAll();

        if (this.mTowerNoLayer != null) {
            this.mTowerNoLayer.removeAll();
        }

        if (mChangeTowerLayer != null)
            mChangeTowerLayer.removeAll();

        if (this.mTowerWireLayer != null)
            this.mTowerWireLayer.removeAll();

        if (this.mAddTowerLayer != null)
            this.mAddTowerLayer.removeAll();

        if (this.mTowerAltitudeLayer != null) {
            this.mTowerAltitudeLayer.removeAll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mTowerLayer = null;
        this.mTowerNoLayer = null;
        this.mTowerWireLayer = null;
        this.mAddTowerLayer = null;
        this.mChangeTowerLayer = null;
        this.mTowerAltitudeLayer = null;
    }




    private List<Tower> loadTowerListByFile(FileInfo fileInfo) {
        List<Tower> towers;

        if (mSelectTowerLineFilesType == TYPE_KML) {
            towers = loadTowerListByKml(fileInfo);
        } else {
            towers = loadTowerListByExcel(fileInfo);
        }

        return towers;
    }

    public List<Tower> loadTowerListByKml(FileInfo fileInfo) {
        List<Tower> towers = new ArrayList<>();
        KmlHelper loader = new KmlHelper();
        loader.loadKml(fileInfo.getFilePath());
        List<GeoPoint> geoPoints = loader.getGeoPoints();
        for (GeoPoint geoPoint : geoPoints) {
            Tower tower = new Tower();
            tower.setLongitude(geoPoint.getPoint().getX());
            tower.setLatitude(geoPoint.getPoint().getY());
            tower.setAltitude((float) geoPoint.getPoint().getZ());
            tower.setTowerId(geoPoint.getGid());
            tower.setTowerNo(geoPoint.getName());
            tower.setGridLineName(fileInfo.getFileNameWithoutSuffix());
            tower.setDescription(geoPoint.getDescription());
            towers.add(tower);
        }
        return towers;
    }


    public List<Tower> loadTowerListByExcel(FileInfo fileInfo) {
        return ExcelHelper.loadTowers(fileInfo.getFilePath());
    }

    private void saveTowerGridLine(String lineName, boolean isNewFile) {

        boolean saveResult = false;

        if (mSelectTowerLineFilesType == TYPE_KML) {
            saveResult = saveTowerListToKml(getContext(), mAddTowerList, lineName, isNewFile);
        } else if (mSelectTowerLineFilesType == TYPE_EXCEL) {
            saveResult = saveTowerListToExcel(getContext(), mAddTowerList, lineName, isNewFile);
        }

        if (saveResult) {
            if (checkIfModifyingTower()) {
                showEditTowerView(false);
            } else {
                mAddTowerList.clear();
            }
        }

        showToast(saveResult ? "保存成功" : "保存失败");
    }

    public synchronized boolean saveTowerListToKml(Context context, List<Tower> towerList, String lineName, boolean isNewFile) {

        String path = IOUtils.getRootStoragePath(context) + AppConstant.DIR_TOWER_KML +
                File.separator + lineName + ".kml";

        if (!checkCreateNewFile(path, isNewFile)) {
            return false;
        }


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < towerList.size(); i++) {
            sb.append("<Placemark>");
            sb.append("<name>").append(towerList.get(i).getTowerNo()).append("</name>");
            sb.append("<description>").append(towerList.get(i).getDescription()).append("</description>");
            sb.append("<styleUrl>#m_ylw-pushpin</styleUrl>");
            sb.append("<Point>");
            sb.append("<gx:drawOrder>1</gx:drawOrder>");
            sb.append("<coordinates>").append(towerList.get(i).getLongitude()).append(",").append(towerList.get(i).getLatitude()).append(",0</coordinates>");
            sb.append("</Point>");
            sb.append("</Placemark>");
        }
        String template = IOUtils.getFromAssets(context, "point_template.kml");
        template = String.format(template, sb.toString());

        return IOUtils.writeTxtFile(path, template);
    }

    public synchronized boolean saveTowerListToExcel(Context context, List<Tower> towerList, String lineName, boolean isNewFile) {

        String path = IOUtils.getRootStoragePath(context) + AppConstant.DIR_TOWER_EXCEL +
                File.separator + lineName + ".xls";

        if (!checkCreateNewFile(path, isNewFile)) {
            return false;
        }

        String[] fieldName = new String[]{"gridLineName", "voltage", "manageGroup", "towerNo", "longitude", "latitude", "towerAltitude", "altitude"};
        String[] title = new String[]{"线路名称", "电压等级", "管理班组", "杆塔编号", "经度", "纬度", "杆塔高度", "海拔高度"};

        try {
            JxlExcelHelper excelHelper = JxlExcelHelper.getInstance(path);
            excelHelper.writeExcel(Tower.class, towerList, fieldName, title);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected void updateTowerNo(Tower tower, String towerNoStr) {


        tower.setTowerNo(towerNoStr);
        String gridLineNameNoSuffix = tower.getGridLineName();
        mAddTowerList.clear();
        mAddTowerList.addAll(getCompareTowerListWithSelectedFile(gridLineNameNoSuffix));

        saveUpdateTowerGridLine(gridLineNameNoSuffix);

    }

    protected void updateTowerLocation(Tower tower) {

        String gridLineNameNoSuffix = tower.getGridLineName();


        mAddTowerList.clear();
        mAddTowerList.addAll(getCompareTowerListWithSelectedFile(gridLineNameNoSuffix));

        saveUpdateTowerGridLine(gridLineNameNoSuffix);

    }

    private boolean checkCreateNewFile(String path, boolean isNewFile) {
        if (isNewFile && FileUtils.checkFileExist(path)) {
            showToast("已存在相同命名的文件，请重新命名");
            return false;
        }

        if (FileUtils.checkFileExist(path)) {
            boolean isDeleteSuccess = IOUtils.delete(path);
            if (!isDeleteSuccess) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较杆塔集是否在选中的文件
     *
     * @param compareListener
     */
    private void compareSelectedFiles(ICompareListener compareListener) {
        if (mSelectTowerLineFiles != null && !mSelectTowerLineFiles.isEmpty()) {
            for (FileInfo selectTowerLineFile : mSelectTowerLineFiles) {
                if (compareListener != null) {
                    compareListener.onCompare(getCompareTowerListWithSelectedFile(
                            selectTowerLineFile.getFileNameWithoutSuffix()));
                }
            }
        }
    }

    private List<Tower> getCompareTowerListWithSelectedFile(String fileName) {
        List<Tower> compareList = new ArrayList<>();
        for (Tower tower : mTowerList) {
            if (tower.getGridLineName().equals(fileName)) {
                compareList.add(tower);
            }
        }
        return compareList;
    }

    private interface ICompareListener {

        void onCompare(List<Tower> compareList);
    }


}
