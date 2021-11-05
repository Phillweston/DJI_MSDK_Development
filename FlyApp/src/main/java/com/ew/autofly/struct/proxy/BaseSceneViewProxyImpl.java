package com.ew.autofly.struct.proxy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.GlobeCameraController;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.LocationToScreenResult;
import com.esri.arcgisruntime.mapping.view.OrbitGeoElementCameraController;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSceneSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.ew.autofly.R;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.mission.MissionPointType;
import com.ew.autofly.utils.arcgis.ArcGisUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ew.autofly.utils.IOUtils.copyFileFromAssetsToCache;
import static com.ew.autofly.utils.arcgis.ArcGisUtil.locationToWgs84Point;


public class BaseSceneViewProxyImpl implements IBaseSceneViewProxy {

  

    private WeakReference<SceneView> mSceneViewRef;

  


  
  
    private GraphicsOverlay mMissionPointLayer;
    private GraphicsOverlay mMissionLineLayer;
    private GraphicsOverlay mMissionFlagLayer;

    private GraphicsOverlay mFlightSceneOverlay;


  

  
    private SimpleMarkerSceneSymbol mAssistPointSymbol;
    private SimpleMarkerSceneSymbol mShotPhotoPointSymbol;

  
    protected ListenableFuture<PictureMarkerSymbol> mStartFlaySymbol;
  
    protected ListenableFuture<PictureMarkerSymbol> mEndFlaySymbol;


    private SimpleLineSymbol mMissionLineSymbol = null;

    private Graphic mDrone3D;

    private OrbitGeoElementCameraController mOrbitCameraController;

    private Context mContext;

    
    private double homeSeaLevel = 0.0f;

    public double getHomeSeaLevel() {
        return homeSeaLevel;
    }

    private boolean isDroneLoaded = false;

    private boolean isRtkMode = false;

    public void enableRtkMode(boolean enable) {
        isRtkMode = enable;
    }

    public BaseSceneViewProxyImpl(Context context) {
        mContext = context;
    }

    /**
     * 绑定SceneView
     *
     * @param sceneView
     */
    public void attach(SceneView sceneView) {
        mSceneViewRef = new WeakReference<>(sceneView);
        if (isAttach()) {
            initGraphicsLayers();
            initSymbols();
            initGraphics();
        }
    }

    
    public void detach() {
        if (mSceneViewRef != null) {
            mSceneViewRef.clear();
            mSceneViewRef = null;
        }
        isDroneLoaded = false;
    }

    
    public boolean isAttach() {
        return getSceneView() != null;
    }

    private SceneView getSceneView() {
        return mSceneViewRef == null ? null : mSceneViewRef.get();
    }

    @Override
    public void initGraphicsLayers() {
        SceneView sceneView = getSceneView();
        mMissionLineLayer = new GraphicsOverlay();
        mMissionLineLayer.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
        if (sceneView != null) {
            sceneView.getGraphicsOverlays().add(mMissionLineLayer);
        }
        mMissionPointLayer = new GraphicsOverlay();
        mMissionPointLayer.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
        if (sceneView != null) {
            sceneView.getGraphicsOverlays().add(mMissionPointLayer);
        }

        mMissionFlagLayer = new GraphicsOverlay();
        mMissionFlagLayer.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
        if (sceneView != null) {
            sceneView.getGraphicsOverlays().add(mMissionFlagLayer);
        }

        mFlightSceneOverlay = new GraphicsOverlay();
        mFlightSceneOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
        SimpleRenderer renderer3D = new SimpleRenderer();
        Renderer.SceneProperties renderProperties = renderer3D.getSceneProperties();
        renderProperties.setHeadingExpression("[HEADING]");
        renderProperties.setPitchExpression("[PITCH]");
        renderProperties.setRollExpression("[ROLL]");
        mFlightSceneOverlay.setRenderer(renderer3D);
        if (sceneView != null) {
            sceneView.getGraphicsOverlays().add(mFlightSceneOverlay);
            ModelSceneSymbol droneSymbol = loadDroneModel();
            droneSymbol.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    isDroneLoaded = true;
                    mOrbitCameraController = new OrbitGeoElementCameraController(mDrone3D,
                            30.0);
                    mOrbitCameraController.setCameraPitchOffset(90.0);
                }
            });
        }
    }

    @Override
    public void initSymbols() {
        mAssistPointSymbol = new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.SPHERE, android.graphics.Color.BLUE, 2,
                2, 2, SceneSymbol.AnchorPosition.CENTER);
        mShotPhotoPointSymbol = new SimpleMarkerSceneSymbol(SimpleMarkerSceneSymbol.Style.SPHERE, Color.RED, 2,
                2, 2, SceneSymbol.AnchorPosition.CENTER);

        mMissionLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,
                mContext.getResources().getColor(R.color.mission_graphic_line), 2);
        mMissionLineSymbol.setAntiAlias(true);

        mStartFlaySymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(mContext,
                R.drawable.ic_startpoint_flag));
        mEndFlaySymbol = PictureMarkerSymbol.createAsync((BitmapDrawable) ContextCompat.getDrawable(mContext,
                R.drawable.ic_endpoint_flag));
    }

    @Override
    public void initGraphics() {

    }

    
    private ModelSceneSymbol loadDroneModel() {
        copyAirCraftModelFile();
        String pathToModel = mContext.getCacheDir() + File.separator + mContext.getString(R.string.bristol_model);

        ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(pathToModel, 1.0);
        plane3DSymbol.loadAsync();
        mDrone3D = new Graphic(new Point(0, 0, 0, SpatialReferences.getWgs84()), plane3DSymbol);
        mFlightSceneOverlay.getGraphics().add(mDrone3D);
        return plane3DSymbol;
    }

    private void copyAirCraftModelFile() {
        copyFileFromAssetsToCache(mContext, mContext.getString(R.string.bristol_model));
        copyFileFromAssetsToCache(mContext, mContext.getString(R.string.bristol_skin));
    }

    @Override
    public void addMissionPoint(LocationCoordinate coordinate, MissionPointType missionPointType) {

        if (!isAttach()) {
            return;
        }

        SimpleMarkerSceneSymbol simpleMarkerSceneSymbol;
        if (missionPointType == MissionPointType.SHOT_PHOTO) {
            simpleMarkerSceneSymbol = mShotPhotoPointSymbol;
        } else {
            simpleMarkerSceneSymbol = mAssistPointSymbol;
        }
        Graphic graphic = new Graphic(locationToWgs84Point(coordinate), simpleMarkerSceneSymbol);
        mMissionPointLayer.getGraphics().add(graphic);
    }

    @Override
    public void addMissionPointAltitude(LocationCoordinate coordinate) {

    }

    @Override
    public void addMissionLine(List<LocationCoordinate> coordinates, boolean showStartFlag, boolean showEndFlag) {
        if (!isAttach()) {
            return;
        }

        Polyline polyline = new Polyline(ArcGisUtil.locationToWgs84PointCollection(coordinates));
        Graphic lineGraphic = new Graphic(polyline, mMissionLineSymbol);
        mMissionLineLayer.getGraphics().add(lineGraphic);
        int size = coordinates.size();
        if (size >= 2) {
            if (showStartFlag) {
                addStartFlag(ArcGisUtil.locationToWgs84Point(coordinates.get(0)));
            }

            if (showEndFlag) {
                addEndFlag(ArcGisUtil.locationToWgs84Point(coordinates.get(size - 1)));
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

    @Override
    public void clearMission() {
        if (this.mMissionLineLayer != null)
            this.mMissionLineLayer.getGraphics().clear();

        if (this.mMissionPointLayer != null)
            this.mMissionPointLayer.getGraphics().clear();

        if (mMissionFlagLayer != null) {
            mMissionFlagLayer.getGraphics().clear();
        }
    }

    public boolean setCameraFollow(boolean follow) {
        if (!isAttach() || !isDroneLoaded) {
            return false;
        }
        if (follow) {
            getSceneView().setCameraController(mOrbitCameraController);
        } else {
            getSceneView().setCameraController(new GlobeCameraController());
        }
        return true;
    }


    @Override
    public void updateDroneLocation(LocationCoordinate locationCoordinate, float angle) {
        if (!isAttach()) {
            return;
        }

        if (mDrone3D != null && isDroneLoaded) {
            Point loc = ArcGisUtil.locationToWgs84Point(transformLocation(locationCoordinate));
            mDrone3D.getAttributes().put("HEADING", angle);
            mDrone3D.setGeometry(loc);
        }
    }

    @Override
    public void updateHomeLocation(LocationCoordinate locationCoordinate) {
        if (!isAttach()) {
            return;
        }
        SceneView sceneView = getSceneView();
        LocationToScreenResult locationToScreenResult = sceneView.locationToScreen(ArcGisUtil.locationToWgs84Point(locationCoordinate));
        Point homPoint = sceneView.screenToBaseSurface(locationToScreenResult.getScreenPoint());
        if (homPoint != null) {
            homeSeaLevel = homPoint.getZ();
        }
    }

    @Override
    public void centerAtLocation(LocationCoordinate locationCoordinate) {
        if (!isAttach()) {
            return;
        }
        SceneView sceneView = getSceneView();
        if (sceneView != null) {
            Camera camera = new Camera(ArcGisUtil.locationToWgs84Point(locationCoordinate), 100,
                    0, 70, 0);
            sceneView.setViewpointCamera(camera);
        }
    }

    public LocationCoordinate getSceneCenterLocation() {

        if (!isAttach()) {
            return null;
        }
        SceneView sceneView = getSceneView();
        int centerX = sceneView.getWidth() / 2;
        int centerY = sceneView.getHeight() / 2;
        android.graphics.Point point = new android.graphics.Point(Math.round(centerX), Math.round(centerY));

        Point centerPoint = sceneView.screenToBaseSurface(point);

        return new LocationCoordinate(centerPoint.getY(), centerPoint.getX(), (float) centerPoint.getZ());
    }

    private LocationCoordinate transformLocation(LocationCoordinate coordinate) {

        return new LocationCoordinate(coordinate.latitude, coordinate.longitude, isRtkMode ? coordinate.altitude : coordinate
                .altitude + (float) homeSeaLevel);
    }

    private List<LocationCoordinate> transformLocationList(List<LocationCoordinate> coordinates) {

        List<LocationCoordinate> ncs = new ArrayList<>();

        for (LocationCoordinate coordinate : coordinates) {
            ncs.add(transformLocation(coordinate));
        }
        return ncs;
    }
}
