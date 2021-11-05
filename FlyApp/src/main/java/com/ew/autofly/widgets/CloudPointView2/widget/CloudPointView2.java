package com.ew.autofly.widgets.CloudPointView2.widget;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.almeros.android.multitouch.MoveGestureDetector;
import com.ew.autofly.R;
import com.ew.autofly.widgets.CloudPointView2.ThreeDimensionEntity;
import com.ew.autofly.widgets.CloudPointView2.util.CoordinateConversion;
import com.ew.autofly.widgets.CloudPointView2.util.CustomGestureDetector;
import com.ew.autofly.widgets.CloudPointView2.util.LatLngCloudPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;



public class CloudPointView2 extends LinearLayout {

    private GLSurfaceView glSurfaceView;

    private ScaleGestureDetector mScaleDetector;
    private MoveGestureDetector mMoveDetector;
    private CustomGestureDetector mCustomGestureDetector;

    private Float mAltitudeAvg;
    private Float mAltitudeAvgTemp;
    private boolean isShowAircraft = false;
    private boolean supportsEs2;

    private ThreeDimensionEntity threeDimensionEntity;
    private LatLngCloudPoint towerInfo;
    private SkyBoxRenderer mSkyBoxRenderer;

    private boolean xScope = false;
    private boolean yScope = false;

    public static float SCALING = 0.04f;

    public CloudPointView2(Context context) {
        super(context);
        init();
    }

    public CloudPointView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CloudPointView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        threeDimensionEntity = new ThreeDimensionEntity();
        ActivityManager activityManager =
                (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")));

        this.mCustomGestureDetector = new CustomGestureDetector();

        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

        mMoveDetector = new MoveGestureDetector(getContext(), new MoveListener());

    }

    public CloudPointView2 initCloudPointInfo(String path) {
        if (path != null && new File(path).exists()) {
            threeDimensionEntity.setCloudPointLists(initPointData(path));
        } else {
            threeDimensionEntity.setCloudPointLists(null);
        }
        return this;
    }

    public CloudPointView2 initHighPointInfo(ArrayList<LatLngCloudPoint> latLngCloudPoints) {
        threeDimensionEntity.setHighLightPoint(latLngCloudPoints);


//




//


        return this;
    }

    public CloudPointView2 initPhotoPointInfo(ArrayList<LatLngCloudPoint> latLngCloudPoints) {
        threeDimensionEntity.setPhotoPoint(latLngCloudPoints);
        return this;
    }

    public CloudPointView2 initAirLineInfo(ArrayList<LatLngCloudPoint> latLngCloudPoints) {
        threeDimensionEntity.setAirLinePoint(latLngCloudPoints);

        mAltitudeAvg = (float)threeDimensionEntity.getAirLinePoint().get(0).getAltitude();


//




//


        return this;
    }

    public CloudPointView2 initTowerLocationInfo(Float lat, Float lng, Float alt) {
        towerInfo = new LatLngCloudPoint(lat, lng, 0);
        return this;
    }

    public CloudPointView2 initAircraftLocation() {

        isShowAircraft = true;

        return this;
    }

    public void update() {
        if (mSkyBoxRenderer != null) {
            mSkyBoxRenderer.updateData(threeDimensionEntity, towerInfo,
                    mAltitudeAvg, isShowAircraft);
        }
    }

    public void updateAircraftLocation(double x, double y, double z, double yaw) {
        if (mSkyBoxRenderer != null) {
            mSkyBoxRenderer.setAircraftLocation(x, y, z, yaw);
        }
    }

    /**
     * TO CHANGE SCENE SCALES BEFORE INIT
     *
     * REMEMBER DON'T CALL THIS METHOD AT WILL
     * YOU MUST VERIFY IN REAL3D SCENE
     *
     * @param scales
     */
    public void setCameraScales(float scales) {
        SCALING = scales;
    }

    public boolean build() {

        if(mAltitudeAvg==null){
            mAltitudeAvg=0f;
        }

        towerInfo.setAltitude(mAltitudeAvg);

        mSkyBoxRenderer = new SkyBoxRenderer(getContext(),
                threeDimensionEntity, towerInfo,
                mAltitudeAvg, isShowAircraft,
                mCustomGestureDetector);

        if (supportsEs2) {

            View view = inflate(getContext(), R.layout.dialog_cloud_point, this);

            glSurfaceView = (GLSurfaceView) view.findViewById(R.id.surface);

            glSurfaceView.setEGLContextClientVersion(2);

            glSurfaceView.setRenderer(mSkyBoxRenderer);

            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

            glSurfaceView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {

                    mScaleDetector.onTouchEvent(e);
                    mMoveDetector.onTouchEvent(e);

                    return true;
                }
            });

            return xScope && yScope;

        } else {
            Toast.makeText(getContext(), "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG).show();

            return false;
        }
    }

    public void resetCloudPointInfo(String path, Float lat, Float lng, Float alt) {
        threeDimensionEntity.setCloudPointLists(initPointData(path));
        LatLngCloudPoint towerInfo = new LatLngCloudPoint(lat, lng, alt);
        mSkyBoxRenderer.resetCloudPoint(threeDimensionEntity, towerInfo);
    }

    public void resetHighLightPoint(ArrayList<LatLngCloudPoint> latLngCloudPoints) {
        threeDimensionEntity.setHighLightPoint(latLngCloudPoints);
        mSkyBoxRenderer.resetHighLightPoint(threeDimensionEntity);
    }

    public void resetAirLinePoint(ArrayList<LatLngCloudPoint> latLngCloudPoints) {
        threeDimensionEntity.setAirLinePoint(latLngCloudPoints);
        mSkyBoxRenderer.resetAirLinePoint(threeDimensionEntity);
    }

    private ArrayList<LatLngCloudPoint> initHighPointData() {
        ArrayList<LatLngCloudPoint> mHighLightPoint = new ArrayList<>();
        mHighLightPoint.add(new LatLngCloudPoint(23, 113, 319, 10, 0, 255, 0));
        mHighLightPoint.add(new LatLngCloudPoint(23.00004581558642, 112.9999999999992, 321, 10, 255, 0, 0));
        mHighLightPoint.add(new LatLngCloudPoint(22.99998610679517, 113.0000004058916, 320, 20 ,0, 0, 255));
        mHighLightPoint.add(new LatLngCloudPoint(22.99999911940922, 113.0000000940253, 322, 10 ,255, 255, 0));

        return mHighLightPoint;
    }

    private ArrayList<LatLngCloudPoint> initAirLineData() {
        ArrayList<LatLngCloudPoint> mHighLightPoint = new ArrayList<>();
        mHighLightPoint.add(new LatLngCloudPoint(23, 113, 319, 0, 255, 0));
        mHighLightPoint.add(new LatLngCloudPoint(23.00004581558642, 112.9999999999992, 361, 255, 0, 0));
        mHighLightPoint.add(new LatLngCloudPoint(22.99998610679517, 113.0000004058916, 320,0, 0, 255));
        mHighLightPoint.add(new LatLngCloudPoint(22.99999911940922, 113.0000000940253, 322, 255, 0, 0));

        return mHighLightPoint;
    }

    private ArrayList<Float> initPointData(String fileName) {
        ArrayList<Float> mData1 = new ArrayList<>();

        ArrayList<String> rowLists = readAssetsTxt(fileName);

        if (rowLists.size() == 0)
            return mData1;

        ArrayList<Float> mList1 = new ArrayList<>();
        ArrayList<Float> mList2 = new ArrayList<>();
        ArrayList<Float> mList3 = new ArrayList<>();

        for (int j = 0; j < rowLists.size(); j++) {

            String[] line = rowLists.get(j).split(" ");

            for (int i = line.length - 1; i >= 0; i--) {
                if (i % line.length == 0) {
                    mList1.add(Float.parseFloat(line[i]));
                } else if (i % line.length == 1) {
                    mList2.add(Float.parseFloat(line[i]));
                } else if (i % line.length == 2) {
                    mList3.add(Float.parseFloat(line[i]));
                }
            }
        }

        CoordinateConversion coordinateConversion = new CoordinateConversion();
        float[] latUTM = coordinateConversion.latLon2UTM(towerInfo.getLatitude(), towerInfo.getLongitude());

        mAltitudeAvgTemp = getAverage(mList3);
        ArrayList<Float> average1 = mList1;
        ArrayList<Float> average2 = mList2;

        if (mAltitudeAvg == null) {
            mAltitudeAvg = mAltitudeAvgTemp;
        }

        ArrayList<Float> average3 = moveAverage(mList3, mAltitudeAvg);


        if ((getAverage(mList1) - latUTM[0]) * 0.04 < 1)
            xScope = true;
        if ((getAverage(mList2) - latUTM[1]) * 0.04 < 1)
            yScope = true;

        int len = average1.size();

        for (int i = 0; i < len; i++) {
            mData1.add(average1.get(i));
            mData1.add(average2.get(i));
            mData1.add(average3.get(i));
        }

        return mData1;
    }

    /**
     * 读取Assets文本行
     *
     * @param fileName
     * @return
     */
    private ArrayList<String> readAssetsTxt(String fileName) {

        ArrayList<String> rowLists = new ArrayList<>();

        try {
            InputStream is = new FileInputStream(fileName);
            InputStreamReader inputStream = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                rowLists.add(line + "\n");
            }
            inputStream.close();
            return rowLists;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowLists;
    }

    private Float getAverage(ArrayList<Float> list) {
        Float max = Collections.max(list);
        Float min = Collections.min(list);

        return (max + min) / 2;
    }

    /**
     * 平移数组
     *
     * @param list
     * @return
     */
    private ArrayList<Float> moveAverage(ArrayList<Float> list, Float average) {

        ArrayList<Float> arrayList = new ArrayList<>();

        for (Float aFloat : list) {
            arrayList.add((aFloat - average));
        }

        return arrayList;
    }

    public CloudPointView2 setLockScale(boolean isLock) {
        isLockScale = isLock;
        return this;
    }

    private boolean isLockScale = true;
    private boolean isScale = false;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            isScale = true;

            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mCustomGestureDetector.setScale(Math.max(0.1f,
                    Math.min(detector.getScaleFactor(), 10.0f)), isLockScale);

            glSurfaceView.requestRender();

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

            isScale = false;

            super.onScaleEnd(detector);
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {

        @Override
        public boolean onMove(MoveGestureDetector detector) {

            if (isScale)
                return true;

            PointF d = detector.getFocusDelta();

            mCustomGestureDetector.setAmountX(d.x);

            mCustomGestureDetector.setAmountY(d.y);

            glSurfaceView.requestRender();

            return true;
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {

            isScale = false;

            super.onMoveEnd(detector);
        }
    }
}
