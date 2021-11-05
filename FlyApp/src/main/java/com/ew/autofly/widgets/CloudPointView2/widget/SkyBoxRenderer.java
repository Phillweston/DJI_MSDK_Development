
package com.ew.autofly.widgets.CloudPointView2.widget;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.widgets.CloudPointView2.ThreeDimensionEntity;
import com.ew.autofly.widgets.CloudPointView2.filter.AirLineFilter;
import com.ew.autofly.widgets.CloudPointView2.filter.AircraftFilter;
import com.ew.autofly.widgets.CloudPointView2.filter.CloudPointFilter;
import com.ew.autofly.widgets.CloudPointView2.filter.HighLightPointFilter;
import com.ew.autofly.widgets.CloudPointView2.filter.SkyBoxFilter;
import com.ew.autofly.widgets.CloudPointView2.util.CoordinateConversion;
import com.ew.autofly.widgets.CloudPointView2.util.CustomGestureDetector;
import com.ew.autofly.widgets.CloudPointView2.util.LatLngCloudPoint;

import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.flightcontroller.simulator.SimulatorState;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

public class SkyBoxRenderer implements GLSurfaceView.Renderer {
    private final Context mContext;

    private Float towerUTMLat;
    private Float towerUTMLng;
    private Float towerAlt;
    private Float towerAverage;


    private ArrayList<Float> mCloudPoint;

    private ArrayList<LatLngCloudPoint> mHighLightPoint;

    private ArrayList<LatLngCloudPoint> mPhotoPoint;


    private ArrayList<LatLngCloudPoint> mAirlinePoint;


    private SkyBoxFilter mSkyBoxFilter;

    private CloudPointFilter mCloudPointFilter;

    private HighLightPointFilter mHighLightPointFilter;

    private HighLightPointFilter mPhotoPointFilter;

    private AirLineFilter mAirLineFilter;

    private AircraftFilter mAircraftFilter;

    private boolean isShowAircraft;

    private double x, y, z, yaw;

    private CustomGestureDetector customGestureDetector;

    public SkyBoxRenderer(Context context, ThreeDimensionEntity entity,
                          LatLngCloudPoint towerInfo,
                          final Float towerAverage, Boolean isShowAircraft,
                          CustomGestureDetector customGestureDetector) {

        CoordinateConversion coordinateConversion = new CoordinateConversion();
        float[] latLon2UTM = coordinateConversion.latLon2UTM(towerInfo.getLatitude(), towerInfo.getLongitude());

        this.mContext = context;
        this.towerUTMLat = latLon2UTM[0];
        this.towerUTMLng = latLon2UTM[1];
        this.towerAlt = (float)towerInfo.getAltitude();
        this.towerAverage = towerAverage;
        this.mCloudPoint = entity.getCloudPointLists();
        this.mHighLightPoint = entity.getHighLightPoint();
        this.mPhotoPoint=entity.getPhotoPoint();
        this.mAirlinePoint = entity.getAirLinePoint();
        this.isShowAircraft = isShowAircraft;
        this.customGestureDetector = customGestureDetector;









//

//







//





    }

    public void updateData(ThreeDimensionEntity entity,
                           LatLngCloudPoint towerInfo,
                           final Float towerAverage, Boolean isShowAircraft) {
        CoordinateConversion coordinateConversion = new CoordinateConversion();
        float[] latLon2UTM = coordinateConversion.latLon2UTM(towerInfo.getLatitude(), towerInfo.getLongitude());

        this.towerUTMLat = latLon2UTM[0];
        this.towerUTMLng = latLon2UTM[1];
        this.towerAlt = (float)towerInfo.getAltitude();
        this.towerAverage = towerAverage;
        this.mCloudPoint = entity.getCloudPointLists();
        this.mHighLightPoint = entity.getHighLightPoint();
        this.mPhotoPoint=entity.getPhotoPoint();
        this.mAirlinePoint = entity.getAirLinePoint();
        this.isShowAircraft = isShowAircraft;

        if (mCloudPoint != null && mCloudPoint.size() != 0) {
            if (mCloudPointFilter != null) {
                mCloudPointFilter.updateFilter(mCloudPoint, towerUTMLat, towerUTMLng);
            } else {
                mCloudPointFilter = new CloudPointFilter(mContext, mCloudPoint, towerUTMLat, towerUTMLng);
            }
        }
        if (mHighLightPoint != null && mHighLightPoint.size() != 0) {
            if (mHighLightPointFilter != null) {
                mHighLightPointFilter.updateFilter(towerUTMLat, towerUTMLng, towerAverage, mHighLightPoint);
            } else {
                mHighLightPointFilter = new HighLightPointFilter(mContext, towerUTMLat, towerUTMLng, towerAverage, mHighLightPoint);
            }
        }
        if (mPhotoPoint != null && mPhotoPoint.size() != 0) {
            if (mPhotoPointFilter != null) {
                mPhotoPointFilter.updateFilter(towerUTMLat, towerUTMLng, towerAverage, mPhotoPoint);
            } else {
                mPhotoPointFilter = new HighLightPointFilter(mContext, towerUTMLat, towerUTMLng, towerAverage, mPhotoPoint);
            }
        }
        if (mAirlinePoint != null && mAirlinePoint.size() != 0) {
            if (mAirLineFilter != null) {
                mAirLineFilter.updateFilter(towerUTMLat, towerUTMLng, towerAverage, mAirlinePoint);
            } else {
                mAirLineFilter = new AirLineFilter(mContext, towerUTMLat, towerUTMLng, towerAverage, mAirlinePoint);
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mSkyBoxFilter = new SkyBoxFilter(mContext);

        if (mCloudPoint != null && mCloudPoint.size() != 0) {
            mCloudPointFilter = new CloudPointFilter(mContext, mCloudPoint, towerUTMLat, towerUTMLng);
        }
        if (mHighLightPoint != null && mHighLightPoint.size() != 0) {
            mHighLightPointFilter = new HighLightPointFilter(mContext, towerUTMLat, towerUTMLng, towerAverage, mHighLightPoint);
        }
        if (mPhotoPoint != null && mPhotoPoint.size() != 0) {
            mPhotoPointFilter = new HighLightPointFilter(mContext, towerUTMLat, towerUTMLng, towerAverage, mPhotoPoint);
        }
        if (mAirlinePoint != null && mAirlinePoint.size() != 0) {
            mAirLineFilter = new AirLineFilter(mContext, towerUTMLat, towerUTMLng, towerAverage, mAirlinePoint);
        }
        if (isShowAircraft) {
            mAircraftFilter = new AircraftFilter(mContext, R.raw.aircraft, R.drawable.white);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);


        float ratio = (float) width / height;
        if (mCloudPointFilter != null) {
            Matrix.frustumM(CloudPointFilter.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            Matrix.setLookAtM(CloudPointFilter.mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }
        if (mHighLightPointFilter != null) {
            Matrix.frustumM(HighLightPointFilter.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            Matrix.setLookAtM(HighLightPointFilter.mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }
        if (mPhotoPointFilter != null) {
            Matrix.frustumM(HighLightPointFilter.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            Matrix.setLookAtM(HighLightPointFilter.mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }
        if (mAirLineFilter != null) {
            Matrix.frustumM(AirLineFilter.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            Matrix.setLookAtM(AirLineFilter.mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }
        if (mAircraftFilter != null && isShowAircraft) {
            Matrix.frustumM(mAircraftFilter.mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            Matrix.setLookAtM(mAircraftFilter.mViewMatrix, 0, 0, 0, 2,  0, 0, 0, 0, 1, 0);
        }

        if (mSkyBoxFilter != null) {
            mSkyBoxFilter.setViewSize(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);


        drawSkyBox();

        drawCloudPoint();

        drawHighLightPoint();

        drawPhotoPoint();

        drawAirLinePoint();

        drawAircraft();
    }


    private void drawSkyBox() {
        if (mSkyBoxFilter != null) {
            mSkyBoxFilter.drawSkyBox(customGestureDetector);
        }
    }

    private void drawCloudPoint() {
        if (mCloudPointFilter != null) {
            mCloudPointFilter.drawSelf(customGestureDetector);
        }
    }

    private void drawHighLightPoint() {
        if (mHighLightPointFilter != null) {
            mHighLightPointFilter.drawSelf(customGestureDetector);
        }
    }

    private void drawPhotoPoint() {
        if (mPhotoPointFilter != null) {
            mPhotoPointFilter.drawSelf(customGestureDetector);
        }
    }

    private void drawAirLinePoint() {
        if (mAirLineFilter != null) {
            mAirLineFilter.drawSelf(customGestureDetector);
        }
    }

    private void drawAircraft() {
        if (mAircraftFilter != null && this.isShowAircraft) {
            mAircraftFilter.draw(customGestureDetector, x, y, z, yaw);
        }
    }

    public void setAircraftLocation(double x, double y, double z, double yaw) {
        CoordinateConversion coordinateConversion = new CoordinateConversion();

        float[] aircraftUtm = coordinateConversion.latLon2UTM(x, y);
        x = aircraftUtm[0] - towerUTMLat;
        y = aircraftUtm[1] - towerUTMLng;

        this.x = x;
        this.y = y;
        this.z = z - towerAverage;
        this.yaw = -yaw;
    }

    public void resetCloudPoint(ThreeDimensionEntity entity, LatLngCloudPoint towerInfo) {

        CoordinateConversion coordinateConversion = new CoordinateConversion();
        float[] latLon2UTM = coordinateConversion.latLon2UTM(towerInfo.getLatitude(), towerInfo.getLongitude());

        this.mCloudPoint = entity.getCloudPointLists();
        this.towerUTMLat = latLon2UTM[0];
        this.towerUTMLng = latLon2UTM[1];
        this.towerAlt = (float)towerInfo.getAltitude();
        mCloudPointFilter.initVertexData(entity.getCloudPointLists(), towerUTMLat, towerUTMLng);
        mHighLightPointFilter.initVertexData(mHighLightPoint, towerUTMLat, towerUTMLng, towerAlt);
        mPhotoPointFilter.initVertexData(mPhotoPoint, towerUTMLat, towerUTMLng, towerAlt);
        mAirLineFilter.initVertexData(mAirlinePoint, towerUTMLat, towerUTMLng, towerAlt);
    }

    public void resetHighLightPoint(ThreeDimensionEntity entity) {
        this.mHighLightPoint = entity.getHighLightPoint();
        mHighLightPointFilter.initVertexData(entity.getHighLightPoint(), towerUTMLat, towerUTMLng, towerAlt);
    }

    public void resetPhotoPoint(ThreeDimensionEntity entity) {
        this.mPhotoPoint = entity.getPhotoPoint();
        mPhotoPointFilter.initVertexData(entity.getPhotoPoint(), towerUTMLat, towerUTMLng, towerAlt);
    }

    public void resetAirLinePoint(ThreeDimensionEntity entity) {
        this.mAirlinePoint = entity.getAirLinePoint();
        mAirLineFilter.initVertexData(entity.getAirLinePoint(), towerUTMLat, towerUTMLng, towerAlt);
    }
}