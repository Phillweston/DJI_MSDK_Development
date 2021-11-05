package com.ew.autofly.widgets;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;
import com.almeros.android.multitouch.MoveGestureDetector;
import com.esri.core.geometry.Point;
import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.db.entity.TowerStudyDetail;
import com.ew.autofly.utils.coordinate.CoordinateConversion;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.ew.autofly.entity.LatLngCloudPoint;
import com.ew.autofly.xflyer.utils.ArcgisPointUtils;

import dji.common.flightcontroller.simulator.SimulatorState;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

import static android.content.Context.ACTIVITY_SERVICE;



public class CloudPointView extends RelativeLayout {

    float[] cubeVertices3 = {

            0.5f,0.5f,0.5f,
            0.5f,0.5f,-0.5f,
            -0.5f,0.5f,0.5f,
            -0.5f,0.5f,-0.5f,


            0,-0.5f,0,
            0.5f,0.5f,0.5f,
            -0.5f,0.5f,0.5f,


            0,-0.5f,0,
            0.5f,0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,


            0,-0.5f,0,
            -0.5f,0.5f,-0.5f,
            -0.5f,0.5f,0.5f,


            0,-0.5f,0,
            0.5f,0.5f,-0.5f,
            0.5f,0.5f,0.5f,
    };

    float [] cubeVerticesYaw = cubeVertices3.clone();

    float []  cubeColors = {
            1f,0f,0f,1f ,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,

            0f,0f,1f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,

            0f,0f,1f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,

            0f,0f,1f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,

            0f,0f,1f,1f,
            0f,1f,0f,1f,
            0f,1f,0f,1f,
    };

    float [] bottomFace = {
            10,10,10,
            10,10,-10,
            -10,-10,-10,
            -10,-10,10,
    };

    float [] bottomColor = {
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
            1f,0f,0f,1f,
    };

    private GLSurfaceView glSurfaceView;

    private ScaleGestureDetector mScaleDetector;
    private MoveGestureDetector mMoveDetector;
    private CustomGestureDetector customGestureDetector;

    public CloudPointView(Context context) {
        super(context);
        initView();
    }

    public CloudPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CloudPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.dialog_cloud_point, this);

    }


    private String mCloudPointPath;
    private float[] mAircraftLocationArray;
    private FloatBuffer mAircraftFloatBuffer;
    private ArrayList<CustomFloatBuffer> mPointFloatBuffers;
    private ArrayList<CustomFloatBuffer> mLineFloatBuffers;

    public CloudPointView initCloudPointPath(String path) {

        mCloudPointPath = path;

        return CloudPointView.this;
    }

    public CloudPointView initPointList(ArrayList<LatLngCloudPoint> ... pointList) {

        mPointFloatBuffers = new ArrayList<>();

        for (ArrayList<LatLngCloudPoint> LatLngCloudPoints : pointList) {

            float[] cloudPoint = new float[LatLngCloudPoints.size() * 3];
            int j = 0;

            for (int k = 0; k < LatLngCloudPoints.size(); k += 3) {
                cloudPoint[k] = (float) LatLngCloudPoints.get(j).latitude;
                cloudPoint[k] = (float) LatLngCloudPoints.get(j).longitude;
                cloudPoint[k] = (float) LatLngCloudPoints.get(j).altitude;
                j++;
            }
            mPointFloatBuffers.add(new CustomFloatBuffer(getFloatBuffer(cloudPoint), LatLngCloudPoints.size()));
        }
        return CloudPointView.this;
    }

    public CloudPointView initLineList(ArrayList<LatLngCloudPoint> ... lineList) {

        mLineFloatBuffers = new ArrayList<>();

        for (ArrayList<LatLngCloudPoint> LatLngCloudPoints : lineList) {

            float[] cloudPoint = new float[LatLngCloudPoints.size() * 3];
            int j = 0;

            for (int k = 0; k < LatLngCloudPoints.size(); k += 3) {
                cloudPoint[k] = (float) LatLngCloudPoints.get(j).latitude;
                cloudPoint[k] = (float) LatLngCloudPoints.get(j).longitude;
                cloudPoint[k] = (float) LatLngCloudPoints.get(j).altitude;
                j++;
            }
            mLineFloatBuffers.add(new CustomFloatBuffer(getFloatBuffer(cloudPoint), LatLngCloudPoints.size()));
        }

        return CloudPointView.this;
    }

    public CloudPointView initAircraftLocationListener() {

        mAircraftLocationArray = new float[3];
        mAircraftLocationArray[0] = 0;
        mAircraftLocationArray[1] = 0;
        mAircraftLocationArray[2] = 0;
        mAircraftFloatBuffer = getFloatBuffer(mAircraftLocationArray);

        buildAircraftYaw(cubeVertices3, mAircraftLocationArray, 0);

        Aircraft aircraft = EWApplication.getAircraftInstance();

        if (aircraft != null) {

            FlightController flightController = aircraft.getFlightController();

            if (flightController != null) {

                flightController.getSimulator().setStateCallback(new SimulatorState.Callback() {
                    @Override
                    public void onUpdate(SimulatorState simulatorState) {

                        CoordinateConversion coordinateConversion = new CoordinateConversion();
                        float[] latLon2Utm = coordinateConversion.latLon2UTM(
                                simulatorState.getLocation().getLatitude(),
                                simulatorState.getLocation().getLongitude());
                        mAircraftLocationArray[0] = (float) (latLon2Utm[0] - 704992.0);
                        mAircraftLocationArray[1] = (float) (latLon2Utm[1] - 2544918.0);
                        mAircraftLocationArray[2] = -simulatorState.getPositionZ();

                        buildAircraftYaw(cubeVertices3, mAircraftLocationArray, -simulatorState.getYaw());

                        mAircraftFloatBuffer = getFloatBuffer(mAircraftLocationArray);

                        glSurfaceView.requestRender();
                    }
                });
            }
        }
        return CloudPointView.this;
    }

    private void buildAircraftYaw(float[] cubeVertices, float[] mAircraftLocationArray, float yaw) {
        for (int i = 0; i < cubeVertices.length; i++) {
            cubeVerticesYaw[i] = cubeVertices[i] + mAircraftLocationArray[i % 3];
        }

        for (int i = 0; i < cubeVertices.length / 3; i++) {
            Point rotatePoint = ArcgisPointUtils.GetBPoint(new Point(cubeVerticesYaw[i * 3], cubeVerticesYaw[i * 3 + 1]),
                    new Point(mAircraftLocationArray[0], mAircraftLocationArray[1]), -yaw);
            cubeVerticesYaw[i * 3] = (float) rotatePoint.getX();
            cubeVerticesYaw[i * 3 + 1] = (float) rotatePoint.getY();
        }
    }


    public void build() {

        this.customGestureDetector = new CustomGestureDetector();

        initOpenGL(mCloudPointPath);
    }

    private void initOpenGL(String fileName) {

        ArrayList<Float> mPointsData = initPointData(fileName);

        if (IsSupported()) {



            CustomFloatBuffer fCloudPoint = buildCloudPoint(mPointsData);

            MyRenderer mRenderer = new MyRenderer(customGestureDetector, fCloudPoint);


            glSurfaceView.setRenderer(mRenderer);

            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

            mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

            mMoveDetector = new MoveGestureDetector(getContext(), new MoveListener());

            glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {

                    mScaleDetector.onTouchEvent(e);
                    mMoveDetector.onTouchEvent(e);

                    return true;
                }
            });
        }
    }

    private boolean isScale = false;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            isScale = true;

            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            customGestureDetector.setScale(Math.max(0.1f, Math.min(detector.getScaleFactor(), 10.0f)));

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

            customGestureDetector.setX(d.x);

            customGestureDetector.setY(d.y);

            glSurfaceView.requestRender();
            return true;
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {

            customGestureDetector.setY(0);

            customGestureDetector.setX(0);

            super.onMoveEnd(detector);
        }
    }

    private class MyRenderer implements GLSurfaceView.Renderer {

        private CustomGestureDetector customGestureDetector;

        private CustomFloatBuffer mCloudFloatBuffer;

        public MyRenderer(CustomGestureDetector customGestureDetector,
                          CustomFloatBuffer mCloudFloatBuffer) {

            this.customGestureDetector = customGestureDetector;

            this.mCloudFloatBuffer = mCloudFloatBuffer;

        }


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            gl.glClearColor(0f, 0f, 0f, 0f);

            gl.glMatrixMode(GL10.GL_PROJECTION);

            gl.glRotatef(-90, 1, 0, 0);

            gl.glScalef(0.04f, 0.04f, 0.04f);


            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

            gl.glClearColorx(0, 0, 0, 0);

            gl.glShadeModel(GL10.GL_SMOOTH);

            gl.glClearDepthf(1.0f);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);
        }


        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            gl.glViewport(0, 0, width, height);
        }


        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glFrustumf(-500, 500, -500, 500, -500, 500);


            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);


            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glLineWidth(2);


            if (mAircraftFloatBuffer != null) {

                drawPoint(gl, 5, mAircraftFloatBuffer, 1);

            }

            if (mCloudFloatBuffer != null) {

                drawPoint(gl, 1, mCloudFloatBuffer.getFloatBuffer(), mCloudFloatBuffer.getLen());

            }

            if (mPointFloatBuffers != null) {

                for (CustomFloatBuffer mPointFloatBuffer : mPointFloatBuffers) {

                    drawPoint(gl, 1, mPointFloatBuffer.getFloatBuffer(), mPointFloatBuffer.getLen());

                }
            }

            if (mLineFloatBuffers != null) {

                for (CustomFloatBuffer mLineFloatBuffer : mLineFloatBuffers) {

                    drawLine(gl, mLineFloatBuffer.getFloatBuffer(), mLineFloatBuffer.getLen());

                }
            }



            gl.glRotatef(customGestureDetector.getRotate(), 0, 0, 1);
            gl.glTranslatef(0, 0, customGestureDetector.getTranslate());

            if (customGestureDetector.isChangedScale()) {

                float amountY = customGestureDetector.getAmountY();
                float scale = customGestureDetector.getScale();

                gl.glTranslatef(0, 0, amountY / 60);
                gl.glScalef(scale, scale, scale);
                gl.glTranslatef(0, 0, -amountY / 60);
            }
            customGestureDetector.setCurrentScale();



            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);


            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, getFloatBuffer(cubeVerticesYaw));


            gl.glColorPointer(4, GL10.GL_FLOAT, 0, getFloatBuffer(cubeColors));





            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP  , 0, 4);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP  , 4, 3);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP  , 7, 3);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP  , 10, 3);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP  , 13, 3);


//

//



            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        }

        private void drawLine(GL10 gl, FloatBuffer floatBuffer, int len) {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuffer);
            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, len);
        }

        private void drawPoint(GL10 gl, int pointSize, FloatBuffer floatBuffer, int len) {
            gl.glPointSize(pointSize);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, floatBuffer);
            gl.glDrawArrays(GL10.GL_POINTS, 0, len);
        }
    }

    private boolean IsSupported() {
        ActivityManager activityManager = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        return configurationInfo.reqGlEsVersion >= 0x2000;
    }

    /**
     * 构建点云数据
     *
     * @param mPointsData
     * @return
     */
    private CustomFloatBuffer buildCloudPoint(ArrayList<Float> mPointsData) {

        float[] mArrayPointData = new float[mPointsData.size() * 3];
        for (int i = 0; i < mArrayPointData.length / 3; i++) {
            mArrayPointData[i] = mPointsData.get(i);
        }

        return new CustomFloatBuffer(getFloatBuffer(mArrayPointData), mPointsData.size());
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

        ArrayList<Float> average1 = moveAverage(mList1);
        ArrayList<Float> average2 = moveAverage(mList2);
        ArrayList<Float> average3 = moveAverage(mList3);

        int len = average1.size();

        for (int i = 0; i < len; i++) {
            mData1.add(average1.get(i));
            mData1.add(average2.get(i));
            mData1.add(average3.get(i));
        }

        return mData1;
    }

    /**
     * 构建航线
     *
     * @param latLngInfos
     * @return
     */
    private ArrayList<Float> initLineData(ArrayList<TowerStudyDetail> latLngInfos) {
        ArrayList<Float> mLines = new ArrayList<>();

        CoordinateConversion conversion = new CoordinateConversion();

        for (TowerStudyDetail latLngInfo : latLngInfos) {
            float[] latLon2UTM = conversion.latLon2UTM(latLngInfo.getLatitude(), latLngInfo.getLongitude());


            mLines.add((float) (latLon2UTM[0] - 704992.0));
            mLines.add((float) (latLon2UTM[1] - 2544918.0));
            mLines.add((float) latLngInfo.getAltitude());

        }

        return mLines;
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

    /**
     * 平移数组
     *
     * @param list
     * @return
     */
    private ArrayList<Float> moveAverage(ArrayList<Float> list) {
        Float max = Collections.max(list);
        Float min = Collections.min(list);

        Float average = (max + min) / 2;
        ArrayList<Float> arrayList = new ArrayList<>();

        for (Float aFloat : list) {
            arrayList.add((aFloat - average));
        }

        return arrayList;
    }

    /**
     * @param vertexs int数组
     * @return 获取整形缓冲数据
     */
    public static IntBuffer getIntBuffer(int[] vertexs) {
        IntBuffer buffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        buffer = qbb.asIntBuffer();
        buffer.put(vertexs);
        buffer.position(0);
        return buffer;
    }

    /**
     * @param vertexs float 数组
     * @return 获取浮点形缓冲数据
     */
    private static FloatBuffer getFloatBuffer(float[] vertexs) {
        FloatBuffer buffer;
        ByteBuffer qbb = ByteBuffer.allocateDirect(vertexs.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        buffer = qbb.asFloatBuffer();
        buffer.put(vertexs);
        buffer.position(0);
        return buffer;
    }

    /**
     * @param vertexs Byte 数组
     * @return 获取字节型缓冲数据
     */
    public static ByteBuffer getByteBuffer(byte[] vertexs) {
        ByteBuffer buffer = null;
        buffer = ByteBuffer.allocateDirect(vertexs.length);
        buffer.put(vertexs);
        buffer.position(0);
        return buffer;
    }

    class CustomGestureDetector {

        private float currentScale = 0;

        private float amountScale = 1.0f;

        private float scale = 1;

        private float amountY = 0.0f;

        private float y = 0;

        private float x = 0;

        public float getCurrentScale() {
            return currentScale;
        }

        public void setCurrentScale(float currentScale) {
            this.currentScale = currentScale;
        }

        public float getAmountScale() {
            return amountScale;
        }

        public void setAmountScale(float amountScale) {
            this.amountScale = amountScale;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public float getAmountY() {
            return amountY;
        }

        public void setAmountY(float amountY) {
            this.amountY = amountY;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public boolean isChangedScale() {
            return getCurrentScale() != getScale();
        }

        public void setCurrentScale() {
            setCurrentScale(getScale());
        }

        public float getRotate() {
            return getX();
        }

        public float getTranslate() {
            return (-getY() / 60) / getAmountScale();
        }
    }

    class CustomFloatBuffer {
        
        public CustomFloatBuffer(FloatBuffer floatBuffer, int len) {
            setFloatBuffer(floatBuffer);
            setLen(len);
        }

        private FloatBuffer mFloatBuffer;

        private int mLen;

        public FloatBuffer getFloatBuffer() {
            return mFloatBuffer;
        }

        public void setFloatBuffer(FloatBuffer mFloatBuffer) {
            this.mFloatBuffer = mFloatBuffer;
        }

        public int getLen() {
            return mLen;
        }

        public void setLen(int mLen) {
            this.mLen = mLen;
        }
    }
}
