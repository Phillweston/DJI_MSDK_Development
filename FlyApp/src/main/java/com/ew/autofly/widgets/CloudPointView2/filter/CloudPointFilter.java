package com.ew.autofly.widgets.CloudPointView2.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ew.autofly.R;
import com.ew.autofly.widgets.CloudPointView2.util.CoordinateConversion;
import com.ew.autofly.widgets.CloudPointView2.util.CustomGestureDetector;
import com.ew.autofly.widgets.CloudPointView2.util.GlUtil;
import com.ew.autofly.widgets.CloudPointView2.util.ResourceUtil;
import com.ew.autofly.widgets.CloudPointView2.widget.CloudPointView2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;



public class CloudPointFilter {
    private final Float scale = CloudPointView2.SCALING;

    public static float[] mProjMatrix = new float[16];
    public static float[] mVMatrix = new float[16];
    public static float[] mMVPMatrix;

    int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    int maColorHandle;
    String mVertexShader;
    String mFragmentShader;
    public static float[] mMMatrix = new float[16];

    FloatBuffer mVertexBuffer;
    FloatBuffer mColorBuffer;
    private Context mContext;
    int vCount = 0;

    public CloudPointFilter(Context context, ArrayList<Float> mPointsData, Float towerUTMLat, Float towerUTMLng) {
        this.mContext = context;

        initVertexData(mPointsData, towerUTMLat, towerUTMLng);

        initShader();
    }

    public void updateFilter(ArrayList<Float> mPointsData, Float towerUTMLat, Float towerUTMLng) {

        initVertexData(mPointsData, towerUTMLat, towerUTMLng);
    }

    public void initVertexData(ArrayList<Float> mPointsData, Float towerUTMLat, Float towerUTMLng) {

        vCount = mPointsData.size() / 3;

        float vertices[] = new float[mPointsData.size()];

        for (int i = 0; i < mPointsData.size() / 3; i++) {
            vertices[i * 3] = (float) ((mPointsData.get(i * 3) - towerUTMLat) * scale);
            vertices[i * 3 + 1] = (float) ((mPointsData.get(i * 3 + 1) - towerUTMLng) * scale);
            vertices[i * 3 + 2] = (float) ((mPointsData.get(i * 3 + 2)) * scale);
        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float colors[] = new float[mPointsData.size() / 3 * 4];
        for (int i = 1; i < colors.length + 1; i++) {
            if (i % 4 == 0) {
                colors[i - 1] = 0;
            } else {
                colors[i - 1] = 1;
            }
        }

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }


    public void initShader() {

        mVertexShader = ResourceUtil.readTextFileFromResource(mContext, R.raw.vertex_cloudpoint);

        mFragmentShader = ResourceUtil.readTextFileFromResource(mContext, R.raw.fragment_cloudpoint);

        mProgram = GlUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");

        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
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

        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount);
    }

    public static float[] getFinalMatrix(float[] spec) {
        mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    /**
     * 构建航线
     *
     * @return
     */
    private ArrayList<Float> initLineData() {
        ArrayList<Float> mLines = new ArrayList<>();

        CoordinateConversion conversion = new CoordinateConversion();
        float[] a = conversion.latLon2UTM(24.43356859063449, 112.6356853905475);
        float[] b = conversion.latLon2UTM(24.43356861987367, 112.6356859169274);
        float[] c = conversion.latLon2UTM(24.43355591697454, 112.635437270932);
        float[] d = conversion.latLon2UTM(24.43355594916077, 112.6354372080751);

        mLines.add((float) (d[0] - 665806.1));
        mLines.add((float) (d[1] - 2703209.0));
        mLines.add(8.4000015258789f);
        mLines.add((float) (c[0] - 665806.1));
        mLines.add((float) (c[1] - 2703209.0));
        mLines.add(20.0999984741211f);
        mLines.add((float) (c[0] - 665806.1));
        mLines.add((float) (c[1] - 2703209.0));
        mLines.add(25f);
        mLines.add((float) (a[0] - 665806.1));
        mLines.add((float) (a[1] - 2703209.0));
        mLines.add(25f);
        mLines.add((float) (a[0] - 665806.1));
        mLines.add((float) (a[1] - 2703209.0));
        mLines.add(14.4000015258789f);
        mLines.add((float) (b[0] - 665806.1));
        mLines.add((float) (b[1] - 2703209.0));
        mLines.add(6.1999969482422f);

        return mLines;
    }
}