package com.ew.autofly.struct.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.ew.autofly.R;
import com.ew.autofly.db.entity.TowerLocationUpdate;
import com.ew.autofly.db.helper.TowerLocationUpdateDbHelper;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.common.ImportTowerLineDialog;
import com.ew.autofly.dialog.common.ImportTowerLineDialog.OnImportTowerLineListener;
import com.ew.autofly.dialog.tower.AddNewTowerDialogFragment;
import com.ew.autofly.dialog.tower.ChooseNewTowerPositionDlgFragment;
import com.ew.autofly.dialog.tower.ChooseTowerDlgFragment;
import com.ew.autofly.dialog.tower.SaveTowerGroupDialogFragment;
import com.ew.autofly.dialog.tower.TowerDetailDlgFragment;
import com.ew.autofly.dialog.tower.UpdateTowerNoDialogFragment;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.LocationMercator;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.interfaces.IConfirmListener;
import com.ew.autofly.interfaces.OnChooseTowerDialogClickListener;
import com.ew.autofly.internal.common.error.YFException;
import com.ew.autofly.struct.presenter.BaseTowerMapPresenterImpl;
import com.ew.autofly.struct.view.IBaseTowerMapView;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.utils.io.file.FileInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
import static com.ew.autofly.dialog.tower.TowerDetailDlgFragment.ARG_PARAM_AIRCRAFT_LOCATION;
import static com.ew.autofly.dialog.tower.TowerDetailDlgFragment.ARG_PARAM_TOWER;



public abstract class BaseTowerMapFragment<V extends IBaseTowerMapView, P extends BaseTowerMapPresenterImpl<V>>
        extends BaseMapFragment<V, P> implements IBaseTowerMapView, OnImportTowerLineListener,
        OnChooseTowerDialogClickListener, View.OnClickListener {

    protected ImageView ivNew, ivSave, ivCancel, ibChooseTower;

    protected LinearLayout llAddNewTower;


    protected GraphicsOverlay mTowerLayer;


    protected GraphicsOverlay mTowerNoLayer;


    protected GraphicsOverlay mTowerWireLayer;


    protected GraphicsOverlay mAddTowerLayer;

    protected GraphicsOverlay mChangeTowerLayer = null;


    protected GraphicsOverlay mTowerAltitudeLayer = null;

    protected ListenableFuture<PictureMarkerSymbol> mTowerMarkerSymbol = null;
    protected ListenableFuture<PictureMarkerSymbol> mLocationUpdatedTowerMarkerSymbol = null;
    protected ListenableFuture<PictureMarkerSymbol> mTowerSelectedSymbol = null;
    protected SimpleLineSymbol mTowerLineSymbol = null;


    protected ArrayList<Tower> mTowerList = new ArrayList<>();

    public ArrayList<Tower> getTowerList() {
        return mTowerList;
    }


    protected List<Tower> mAddTowerList = new ArrayList<>();


    protected ArrayList<FileInfo> mSelectTowerLineFiles = new ArrayList<>();
    protected int mSelectTowerLineFilesType = TYPE_KML;


    protected ArrayList<Tower> mSelectedTowerList = new ArrayList<>();


    protected boolean isUpdatingTower = false;
    protected String mUpdateTowerFileName = "";


    @Override
    protected void initRootView(View view) {
        super.initRootView(view);

        llAddNewTower = (LinearLayout) view.findViewById(R.id.ll_add_new_tower);
        ivNew = (ImageView) view.findViewById(R.id.iv_new_tower);
        ivSave = (ImageView) view.findViewById(R.id.iv_save_tower);
        ivCancel = (ImageView) view.findViewById(R.id.iv_cancel_save_tower);
        ibChooseTower = (ImageView) view.findViewById(R.id.ib_choose_tower);
        ibChooseTower.setOnClickListener(this);

        ivNew.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }

    @Override
    protected BaseMapTouchListener initMapTouchListener(MapView mapView) {
        return new BaseTowerMapTouchListener(getBaseContext(), mapView);
    }

    @Override
    protected void initMapGraphicsLayer() {


        mTowerWireLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mTowerWireLayer);

        mTowerLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mTowerLayer);

        mChangeTowerLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mChangeTowerLayer);

        mAddTowerLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mAddTowerLayer);

        mTowerNoLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mTowerNoLayer);

        mTowerAltitudeLayer = new GraphicsOverlay();
        mMapView.getGraphicsOverlays().add(mTowerAltitudeLayer);

        super.initMapGraphicsLayer();

    }

    @Override
    protected void initSymbols() {
        super.initSymbols();
        mTowerMarkerSymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_tower_new));
        mLocationUpdatedTowerMarkerSymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.ic_tower_location_update));
        mTowerSelectedSymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(getBaseContext(),
                R.drawable.ic_tower_selected));
        mTowerLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                getResources().getColor(R.color.tower_line), 2);
        mTowerLineSymbol.setAntiAlias(true);
    }


    protected boolean onMapTouch(MotionEvent event) {
        refreshAltitude();
        return true;
    }

    protected void onMapSingleTap(MotionEvent event) {

        if (!mPresenter.checkTaskFree(false)) {
            return;
        }

        getTouchSelectedTower(event, new OnSelectTower() {
            @Override
            public void onSelect(Tower tower) {
                if (tower != null) {
                    showTowerDetailDialog(tower);
                }
            }
        });
    }

    protected void onMapLongPress(MotionEvent event) {

        if (!mPresenter.checkTaskFree(false)) {
            return;
        }

        getTouchSelectedTower(event, new OnSelectTower() {
            @Override
            public void onSelect(Tower tower) {
                if (tower != null) {
                    showUpdaterTowerNoDialog(tower);
                }
            }
        });
    }

    @Override
    public List<Tower> getSelectedTowers() {
        return mSelectedTowerList;
    }

    /**
     * 获取点击选中的杆塔
     *
     * @param event
     * @return
     */
    private void getTouchSelectedTower(MotionEvent event, final OnSelectTower onSelectTower) {

        if (checkIfModifyingTower() || mTowerList == null || mTowerList.isEmpty())
            return;

        android.graphics.Point screenPoint = new android.graphics.Point((int) event.getX(), (int) event.getY());
        final ListenableFuture<IdentifyGraphicsOverlayResult> listenableFuture = mMapView.identifyGraphicsOverlayAsync(mTowerLayer,
                screenPoint, 20, false);


        listenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    IdentifyGraphicsOverlayResult graphicsOverlayResult = listenableFuture.get();
                    if (graphicsOverlayResult != null) {
                        List<Graphic> graphics = graphicsOverlayResult.getGraphics();
                        if (graphics != null && !graphics.isEmpty()) {
                            Graphic graphic = graphics.get(0);
                            Object object = graphic.getAttributes().get("index");
                            if (object != null) {
                                if (onSelectTower != null) {
                                    onSelectTower.onSelect(mTowerList.get(Integer.parseInt(object.toString())));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private interface OnSelectTower {
        void onSelect(Tower tower);
    }

    @Override
    protected void onSwitchDrawCircle(boolean isOn) {
        if (checkModifyTowerView())
            return;
        if (!isOn) {
            showImportTowerDialog();
        } else {
            removeTowerGridLine();
        }

    }

    @Override
    protected void switchFPVDefault() {
        super.switchFPVDefault();
        if (checkIfModifyingTower()) {
            showEditTowerView(true);
        }
    }

    @Override
    public void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.iv_new_tower:
                if (mPresenter.isAirCraftConnect()
                        && LocationCoordinateUtils.checkGpsCoordinate(mPresenter.getAirCraftLocation())) {
                    AddNewTowerDialogFragment dlg = new AddNewTowerDialogFragment();
                    Bundle args1 = new Bundle();
                    args1.putSerializable("tower_list", (Serializable) mAddTowerList);
                    args1.putDouble("latitude", mPresenter.getAirCraftLocation().getLatitude());
                    args1.putDouble("longitude", mPresenter.getAirCraftLocation().getLongitude());
                    dlg.setArguments(args1);
                    dlg.setListener(new AddNewTowerDialogFragment.AddNewTowerListener() {
                        @Override
                        public void OnAddNewTowerConfirm(boolean replace, final Tower tower, int currentTowerIndex) {
                            if (!replace) {
                                if (isUpdatingTower) {
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
                    showToast("请检查飞机连接状态");
                }
                break;
            case R.id.ib_choose_tower:
                if (mPresenter == null || !mPresenter.checkTaskFree())
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
                    showToast("请先添加杆塔！");
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
    public void showChoseTowerDialog() {
        ChooseTowerDlgFragment towerDlg = new ChooseTowerDlgFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_PARAM_CHOOSE_TOWER_LIST, mTowerList);
        bundle.putParcelable(ARG_PARAM_CHOOSE_TOWER_AIRCRAFT_LOCATION, mPresenter.getAirCraftLocation());
        towerDlg.setArguments(bundle);
        towerDlg.setOnChooseTowerListener(this);
        towerDlg.show(getFragmentManager(), TAG_CHOOSE_TOWER);
    }

    @Override
    public void showTowerDetailDialog(final Tower tower) {

        final TowerDetailDlgFragment towerDetailDlg = new TowerDetailDlgFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_TOWER, tower);
        args.putParcelable(ARG_PARAM_AIRCRAFT_LOCATION, mPresenter.getAirCraftLocation());
        towerDetailDlg.setArguments(args);
        towerDetailDlg.setClickListener(new TowerDetailDlgFragment.onTowerDetailClickListener() {
            @Override
            public void onUpdateLocation(Tower t) {
                updateTowerLocation(t);
            }
        });
        towerDetailDlg.show(getFragmentManager(), "tower_detail");

    }

    protected void updateTowerLocation(Tower tower) {

        String gridLineNameNoSuffix = tower.getGridLineName();

        if (mSelectTowerLineFilesType == TYPE_KML) {
            TowerLocationUpdateDbHelper.getInstance().update(tower);
        }

        mAddTowerList.clear();
        mAddTowerList.addAll(getCompareTowerListWithSelectedFile(gridLineNameNoSuffix));

        saveUpdateTowerGridLine(gridLineNameNoSuffix);

        TowerLocationUpdateDbHelper.getInstance().updateFileCreatedTime(gridLineNameNoSuffix);
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

    protected void updateTowerNo(Tower tower, String towerNoStr) {


        tower.setTowerNo(towerNoStr);
        String gridLineNameNoSuffix = tower.getGridLineName();
        mAddTowerList.clear();
        mAddTowerList.addAll(getCompareTowerListWithSelectedFile(gridLineNameNoSuffix));

        saveUpdateTowerGridLine(gridLineNameNoSuffix);

    }

    @Override
    public void importTowerGridLine() {
        ibDrawCircle.switchOn();
        ibChooseTower.setVisibility(View.VISIBLE);
        drawMultipleTowerGridLine();

    }

    /*protected void setUploadFlightRecordLineName() {
        StringBuilder lineName = new StringBuilder();
        int i = 0;
        for (FileInfo selectTowerLineFile : mSelectTowerLineFiles) {
            if (i == 0) {
                lineName = new StringBuilder(selectTowerLineFile.getFileNameWithoutSuffix());
            } else {
                lineName.append("|").append(selectTowerLineFile.getFileNameWithoutSuffix());
            }
            i++;
        }
        FlightRecordManager.getInstance().setPatrolInfo(lineName.toString());
    }*/

    protected List<Tower> loadTowerListByFile(FileInfo fileInfo) {
        List<Tower> towers;

        if (mSelectTowerLineFilesType == TYPE_KML) {
            towers = mPresenter.loadTowerListByKml(fileInfo);
        } else {
            towers = mPresenter.loadTowerListByExcel(fileInfo);
        }

        return towers;
    }


    protected synchronized void drawMultipleTowerGridLine() {

        Observable.fromIterable(mSelectTowerLineFiles)
                .subscribeOn(Schedulers.io())
                .map(new Function<FileInfo, List<Tower>>() {
                    @Override
                    public List<Tower> apply(FileInfo fileInfo) throws Exception {

                        List<Tower> towers = null;

                        try {

                            towers = loadTowerListByFile(fileInfo);
                            mTowerList.addAll(towers);

                            if (mSelectTowerLineFilesType == TYPE_KML) {
                                TowerLocationUpdateDbHelper.getInstance().deleteIfExistRecord(fileInfo.getFileNameWithoutSuffix(), fileInfo.getTime());
                            }

                        } catch (Exception e) {
                            throw new YFException("导入 " + fileInfo.getFileName() + " 失败:转换错误");
                        }

                        return towers;
                    }
                })
                .map(new Function<List<Tower>, List<Tower>>() {
                    @Override
                    public List<Tower> apply(List<Tower> towerList) throws Exception {
                        Tower tower = towerList.get(0);
                        centerAt(new LocationCoordinate(tower.getLatitude(), tower.getLongitude()));
                        return towerList;
                    }
                })
                .map(new Function<List<Tower>, Boolean>() {
                    @Override
                    public Boolean apply(List<Tower> towerList) throws Exception {
                        try {
                            drawTowerGridLine(towerList);
                        } catch (Exception e) {
                            throw new YFException("绘制杆塔错误");
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (mRxManager != null) {
                            mRxManager.add(d);
                        }

                        showLoadProgressDialog("正在加载……");
                    }

                    @Override
                    public void onNext(Boolean o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                        if (e instanceof YFException) {
                            showToastDialog(e.getMessage());
                        }


                        dismissLoadProgressDialog();
                    }

                    @Override
                    public void onComplete() {

                        refreshLayerVisibility();
                        dismissLoadProgressDialog();
                    }
                });
    }

    @Override
    public void removeTowerGridLine() {
        ibDrawCircle.switchOff();

        clearTower();
        clearMission();

        ibChooseTower.setVisibility(View.GONE);
        tvBottomInfo.setVisibility(View.GONE);


        mPresenter.getAirRouteParameter().setFixedAltitude(true);
        updateFlightEnable(false);
    }

    @Override
    public void clearTower() {
        mSelectTowerLineFiles.clear();
        mSelectedTowerList.clear();
        mTowerList.clear();

        clearTowerLayer();
    }

    @Override
    public void clearTowerLayer() {
        if (this.mTowerLayer != null)
            this.mTowerLayer.getGraphics().clear();

        if (this.mTowerNoLayer != null)
            this.mTowerNoLayer.getGraphics().clear();

        if (mChangeTowerLayer != null)
            mChangeTowerLayer.getGraphics().clear();

        if (this.mTowerWireLayer != null)
            this.mTowerWireLayer.getGraphics().clear();

        if (this.mAddTowerLayer != null)
            this.mAddTowerLayer.getGraphics().clear();

        if (this.mTowerAltitudeLayer != null) {
            this.mTowerAltitudeLayer.getGraphics().clear();
        }

    }

    @Override
    public void refreshDrawTowerGriLines() {

        clearTowerLayer();
        clearMission();

        compareSelectedFiles(new ICompareListener() {
            @Override
            public void onCompare(List<Tower> compareList) {
                if (compareList != null && !compareList.isEmpty()) {
                    try {
                        drawTowerGridLine(compareList);
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

    @Override
    public void refreshAddTowerGridLine(boolean isMoveToCenter) {

        showLoadProgressDialog("正在添加杆塔……");

        mAddTowerLayer.getGraphics().clear();



        PointCollection collection = new PointCollection(getSpatialReference());
        for (Tower t : mAddTowerList) {
            LocationMercator mercator = LocationCoordinateUtils.gps84ToMapMercator(t.getLongitude(), t.getLatitude());
            collection.add(new Point(mercator.x, mercator.y));
        }
        Polyline polyline = new Polyline(collection);

        mAddTowerLayer.getGraphics().add(new Graphic(polyline, mTowerLineSymbol));

        List<Graphic> towerNoGraphics = new ArrayList<>();
        List<Graphic> towerGraphics = new ArrayList<>();


        for (int i = 0; i < mAddTowerList.size(); i++) {
            Tower tower1 = mAddTowerList.get(i);
            Map<String, Object> attr = new HashMap<>();
            attr.put("index", i);

            final LocationMercator mercator1 = LocationCoordinateUtils.gps84ToMapMercator(tower1.getLongitude(), tower1.getLatitude());
            Point point = new Point(mercator1.x, mercator1.y);

            Graphic towerGraphic = null;
            try {
                towerGraphic = new Graphic(point, attr, mTowerMarkerSymbol.get());
                towerGraphics.add(towerGraphic);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i == 0 && isMoveToCenter) {
                centerAtMercatorPoint(point, false);
            }

            Graphic textGraphic = new Graphic(point, createTowerNoTextSymbol(tower1.getTowerNo()));
            towerNoGraphics.add(textGraphic);
        }

        mAddTowerLayer.getGraphics().addAll(towerGraphics);
        mAddTowerLayer.getGraphics().addAll(towerNoGraphics);

        dismissLoadProgressDialog();
    }

    private TextSymbol createTowerNoTextSymbol(String name) {
        TextSymbol textSymbol = new TextSymbol();
        textSymbol.setSize(10);
        textSymbol.setColor(getResources().getColor(R.color.black));
        textSymbol.setBackgroundColor(getResources().getColor(R.color.default_text_symbol_background));
        textSymbol.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
        textSymbol.setVerticalAlignment(TextSymbol.VerticalAlignment.BOTTOM);
        textSymbol.setText(name);
        textSymbol.setOffsetY(12);
        return textSymbol;
    }

    private TextSymbol createTowerAltitudeTextSymbol(String text) {
        TextSymbol textSymbol = new TextSymbol();
        textSymbol.setSize(10);
        textSymbol.setColor(Color.RED);
        textSymbol.setHorizontalAlignment(TextSymbol.HorizontalAlignment.CENTER);
        textSymbol.setVerticalAlignment(TextSymbol.VerticalAlignment.TOP);
        textSymbol.setOffsetY(-12);
        textSymbol.setText(text);
        return textSymbol;
    }

    public synchronized void drawTowerGridLineAnCenterAtFirst(List<Tower> towerList) {
        if (towerList != null && !towerList.isEmpty()) {
            centerAt(new LocationCoordinate(towerList.get(0).getLatitude(),
                    towerList.get(0).getLongitude()));
            drawTowerGridLine(towerList);
        }
    }

    /**
     * 绘制单条杆塔路线
     *
     * @param towerList
     */
    @Override
    public synchronized void drawTowerGridLine(List<Tower> towerList) {

        findTowerLocationUpdated(towerList);

        PointCollection collection = new PointCollection(getSpatialReference());

        List<Graphic> towerNoGraphics = new ArrayList<>();
        List<Graphic> towerGraphics = new ArrayList<>();

        int kk = 0;
        for (Tower tower : towerList) {

            final LocationMercator mercator = LocationCoordinateUtils.gps84ToMapMercator(tower.getLongitude(), tower.getLatitude());
            Point point = new Point(mercator.x, mercator.y);
            collection.add(point);

            Graphic textGraphic = new Graphic(point, createTowerNoTextSymbol(tower.getTowerNo()));
            towerNoGraphics.add(textGraphic);


            Map<String, Object> map = new HashMap<>();
            map.put("index", kk);
            Graphic towerGraphic = null;
            try {
                towerGraphic = new Graphic(point, map, tower.isLocationUpdated() ?
                        mLocationUpdatedTowerMarkerSymbol.get() : mTowerMarkerSymbol.get()
                );
                towerGraphics.add(towerGraphic);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            kk++;
        }

        mTowerNoLayer.getGraphics().addAll(towerNoGraphics);
        mTowerLayer.getGraphics().addAll(towerGraphics);


        Polyline polyline = new Polyline(collection);
        Graphic wireLineGraphic = new Graphic(polyline, mTowerLineSymbol);
        mTowerWireLayer.getGraphics().add(wireLineGraphic);


        drawTowerGridLineAltitude(towerList);
    }



    private void findTowerLocationUpdated(List<Tower> towerList) {
        if (towerList == null || towerList.isEmpty()) {
            return;
        }
        List<TowerLocationUpdate> towerLocationUpdateList = null;
        if (mSelectTowerLineFilesType == TYPE_KML) {
            towerLocationUpdateList = TowerLocationUpdateDbHelper.getInstance()
                    .getRecordListByGridLineName(towerList.get(0).getGridLineName());
        }
        if (towerLocationUpdateList != null && towerList != null) {
            for (TowerLocationUpdate towerLocationUpdate : towerLocationUpdateList) {
                for (Tower tower : towerList) {
                    String tn = towerLocationUpdate.getTowerNo();
                    String towerNo = tower.getTowerNo();
                    if (towerNo != null && towerNo.equals(tn)) {
                        tower.setLocationUpdated(true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void refreshLayerVisibility() {
        super.refreshLayerVisibility();
        if (getActivity() != null) {
            if (mMapView.getMapScale() > 100000) {
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

        List<Graphic> altitudeGraphics = new ArrayList<>();

        for (Tower tower : towerList) {


            double altitude = demReaderUtils.getZValue(new LatLngInfo(tower.getLatitude(), tower.getLongitude()));
            StringBuilder altitude_txt = new StringBuilder();

            altitude_txt.append(demReaderUtils.checkZValue(altitude) ? String.format("h:%.0fm", altitude) : "h:n/a");

            if (!mPresenter.getAirRouteParameter().isFixedAltitude()) {
                for (Tower selectedTower : mSelectedTowerList) {
                    if (tower.getTowerId().equals(selectedTower.getTowerId())) {
                        altitude_txt.append(String.format("(%d)", tower.getFlyAltitude() == 0 ?
                                mPresenter.getAirRouteParameter().getAltitude() : tower.getFlyAltitude()));
                    }
                }
            }
            TextSymbol symbol = createTowerAltitudeTextSymbol(altitude_txt.toString());
            final LocationMercator mercator = LocationCoordinateUtils.gps84ToMapMercator(tower.getLongitude(), tower.getLatitude());

            Graphic graphic = new Graphic(new Point(mercator.x, mercator.y), symbol);
            altitudeGraphics.add(graphic);
        }

        mTowerAltitudeLayer.getGraphics().addAll(altitudeGraphics);
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

       /* try {
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
        }*/

        dismissLoadProgressDialog();
    }

    @Override
    public void saveUpdateTowerGridLine(final String lineName) {

        showLoadProgressDialog("正在更新杆塔……");

        saveTowerGridLine(lineName, false);

        refreshDrawTowerGriLines();

        dismissLoadProgressDialog();
    }

    private void saveTowerGridLine(String lineName, boolean isNewFile) {

        boolean saveResult = false;

        if (mSelectTowerLineFilesType == TYPE_KML) {
            saveResult = mPresenter.saveTowerListToKml(getBaseContext(), mAddTowerList, lineName, isNewFile);
        } else if (mSelectTowerLineFilesType == TYPE_EXCEL) {
            saveResult = mPresenter.saveTowerListToExcel(getBaseContext(), mAddTowerList, lineName, isNewFile);
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

    /**
     * 显示新增/追加杆塔
     *
     * @param isShow
     */
    protected void showEditTowerView(boolean isShow) {
        if (!isShow) {
            mSelectTowerLineFiles.clear();
            mAddTowerList.clear();
            mAddTowerLayer.getGraphics().clear();
            isUpdatingTower = false;
            mUpdateTowerFileName = "";
        }

        llRightButton.setVisibility(!isShow ? View.VISIBLE : View.GONE);
        llAddNewTower.setVisibility(isShow ? View.VISIBLE : View.GONE);
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
    public void onDestroy() {

        this.mTowerLayer = null;
        this.mTowerNoLayer = null;
        this.mTowerWireLayer = null;
        this.mAddTowerLayer = null;
        this.mChangeTowerLayer = null;
        this.mTowerAltitudeLayer = null;
        super.onDestroy();
    }

    protected class BaseTowerMapTouchListener extends BaseMapTouchListener {

        public BaseTowerMapTouchListener(Context context, MapView mapView) {
            super(context, mapView);
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return onMapTouch(event) && super.onTouch(view, event);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!super.onSingleTapConfirmed(e)) {
                onMapSingleTap(e);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onMapLongPress(e);
        }
    }
}
