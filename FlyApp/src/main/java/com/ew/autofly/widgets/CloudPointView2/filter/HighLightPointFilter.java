package com.ew.autofly.widgets.CloudPointView2.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ew.autofly.R;
import com.ew.autofly.widgets.CloudPointView2.util.CoordinateConversion;
import com.ew.autofly.widgets.CloudPointView2.util.CustomGestureDetector;
import com.ew.autofly.widgets.CloudPointView2.util.GlUtil;
import com.ew.autofly.widgets.CloudPointView2.util.LatLngCloudPoint;
import com.ew.autofly.widgets.CloudPointView2.util.ResourceUtil;
import com.ew.autofly.widgets.CloudPointView2.widget.CloudPointView2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;



public class HighLightPointFilter {
    private final Float scale = CloudPointView2.SCALING;

    public static float[] mProjMatrix = new float[16];
    public static float[] mVMatrix = new float[16];
    public static float[] mMVPMatrix;

    int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    int maColorHandle;
    int maPointSizeHandle;
    String mVertexShader;
    String mFragmentShader;
    public static float[] mMMatrix = new float[16];

    FloatBuffer mVertexBuffer;
    FloatBuffer mColorBuffer;
    FloatBuffer mPointSizeBuffer;
    private Context mContext;
    int vCount = 0;

    public HighLightPointFilter(Context context, double towerX, double towerY, double towerA,
                                ArrayList<LatLngCloudPoint> mLatLngCloudPoint) {
        this.mContext = context;

        initVertexData(mLatLngCloudPoint, towerX, towerY, towerA);

        initShader();
    }

    public void updateFilter(double towerX, double towerY, double towerA,
                             ArrayList<LatLngCloudPoint> mLatLngCloudPoint) {

        initVertexData(mLatLngCloudPoint, towerX, towerY, towerA);
    }

    public void initVertexData(ArrayList<LatLngCloudPoint> mLatLngCloudPoint, double towerX, double towerY, double towerA) {
        vCount = mLatLngCloudPoint.size();

        float vertices[] = initPointData(mLatLngCloudPoint, towerX, towerY, towerA);

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float colors[] = new float[vertices.length / 3 * 4];
        for (int i = 0; i < colors.length / 4; i++) {
            colors[i * 4] = mLatLngCloudPoint.get(i).getRed();
            colors[i * 4 + 1] = mLatLngCloudPoint.get(i).getGreen();
            colors[i * 4 + 2] = mLatLngCloudPoint.get(i).getBlue();
            colors[i * 4 + 3] = 1;
        }

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        float size[] = new float[vertices.length / 3];
        for (int i = 0; i < size.length; i++) {
            size[i] = (float) mLatLngCloudPoint.get(i).getPointSize();
        }
        ByteBuffer pbb = ByteBuffer.allocateDirect(size.length * 4);
        pbb.order(ByteOrder.nativeOrder());
        mPointSizeBuffer = pbb.asFloatBuffer();
        mPointSizeBuffer.put(size);
        mPointSizeBuffer.position(0);
    }

    private float[] initPointData(ArrayList<LatLngCloudPoint> mLatLngCloudPoint, double towerX, double towerY, double towerA) {

        CoordinateConversion conversion = new CoordinateConversion();

        float[] mPointData = new float[mLatLngCloudPoint.size() * 3];

        for (int i = 0; i < mLatLngCloudPoint.size(); i++) {
            float[] a = conversion.latLon2UTM(mLatLngCloudPoint.get(i).getLatitude(), mLatLngCloudPoint.get(i).getLongitude());
            mPointData[i * 3] = (float) (a[0] - towerX) * scale;
            mPointData[i * 3 + 1] = (float) (a[1] - towerY) * scale;
            mPointData[i * 3 + 2] = ((float) mLatLngCloudPoint.get(i).getAltitude() - (float) towerA) * scale;
        }

        return mPointData;
    }


  
    public void initShader() {
      
        mVertexShader = ResourceUtil.readTextFileFromResource(mContext, R.raw.vertex_highlightpoint);
      
        mFragmentShader = ResourceUtil.readTextFileFromResource(mContext, R.raw.fragment_cloudpoint);
      
        mProgram = GlUtil.createProgram(mVertexShader, mFragmentShader);
      
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
      
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
      
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
      
        maPointSizeHandle = GLES20.glGetAttribLocation(mProgram, "aPointSize");
    }

    public void drawSelf(CustomGestureDetector customGestureDetector) {
      
        GLES20.glUseProgram(mProgram);
      
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
      
        Matrix.translateM(mMMatrix, 0, 0, 0, 1);
      
        Matrix.rotateM(mMMatrix, 0, customGestureDetector.getAmountX(), 0, 1, 0);
      
        Matrix.translateM(mMMatrix, 0, 0, customGestureDetector.getAmountY(), 0);
      
        Matrix.scaleM(mMMatrix, 0, customGestureDetector.getAmountScale(), customGestureDetector.getAmountScale(), customGestureDetector.getAmountScale());

        Matrix.rotateM(mMMatrix, 0, -90, 1, 0, 0);
        //
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFinalMatrix(mMMatrix), 0);
      
        GLES20.glVertexAttribPointer(
                maPositionHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                3 * 4,
                mVertexBuffer
        );
        GLES20.glVertexAttribPointer
                (
                        maColorHandle,
                        4,
                        GLES20.GL_FLOAT,
                        false,
                        4 * 4,
                        mColorBuffer
                );
        GLES20.glVertexAttribPointer(
                maPointSizeHandle,
                1,
                GLES20.GL_FLOAT,
                false,
                4,
                mPointSizeBuffer
        );
      
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        GLES20.glEnableVertexAttribArray(maPointSizeHandle);
      
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount);

    }

    public static float[] getFinalMatrix(float[] spec) {
        mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }
}
