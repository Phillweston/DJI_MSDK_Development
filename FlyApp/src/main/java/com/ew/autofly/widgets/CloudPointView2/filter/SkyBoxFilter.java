package com.ew.autofly.widgets.CloudPointView2.filter;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ew.autofly.R;
import com.ew.autofly.widgets.CloudPointView2.util.CustomGestureDetector;
import com.ew.autofly.widgets.CloudPointView2.util.GlUtil;
import com.ew.autofly.widgets.CloudPointView2.util.MatrixHelper;
import com.ew.autofly.widgets.CloudPointView2.util.ResourceUtil;
import com.ew.autofly.widgets.CloudPointView2.util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;



public class SkyBoxFilter {

    private static final int COORDS_PER_VERTEX = 3;

  
    private static final float[] CubeCoords = new float[] {
            -1,  1,  1,   
            1,   1,  1,   
            -1, -1,  1,   
            1,  -1,  1,   
            -1,  1, -1,   
            1,   1, -1,   
            -1, -1, -1,   
            1,  -1, -1    
    };

  
    private static final byte[] CubeIndex = new byte[] {
          
            1, 3, 0,
            0, 3, 2,

          
            4, 6, 5,
            5, 6, 7,

          
            0, 2, 4,
            4, 2, 6,

          
            5, 7, 1,
            1, 7, 3,

          
            5, 1, 4,
            4, 1, 0,

          
            6, 2, 7,
            7, 2, 3
    };

    private Context mContext;

  
    private int mViewWidth;
    private int mViewHeight;

    private FloatBuffer mVertexBuffer;
    private ByteBuffer mIndexBuffer;

    private int mProgramHandle;

    private int muMatrixHandle;
    private int muTextureUnitHandle;
    private int maPositionHandle;

  
    private int mSkyboxTexture;

  
    private float[] mRotationMatrix = new float[16];
    private float[] mViewMatrix = new float[16];  
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public SkyBoxFilter(Context context) {
        mContext = context;
        mVertexBuffer = GlUtil.createFloatBuffer(CubeCoords);
        mIndexBuffer = GlUtil.createByteBuffer(CubeIndex);
        initMatrix();

        createProgram();
    }


    private void initMatrix() {
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }


    private void createProgram() {
        mProgramHandle = GlUtil.createProgram(ResourceUtil
                        .readTextFileFromResource(mContext, R.raw.vertex_skybox),
                ResourceUtil
                        .readTextFileFromResource(mContext, R.raw.fragment_skybox));
        muMatrixHandle = glGetUniformLocation(mProgramHandle, "u_Matrix");
        muTextureUnitHandle = glGetUniformLocation(mProgramHandle, "u_TextureUnit");
        maPositionHandle = glGetAttribLocation(mProgramHandle, "a_Position");
        mSkyboxTexture =  TextureHelper.loadCubeMap(mContext,
                new int[] {
                        R.drawable.bg_left, R.drawable.bg_left,
                        R.drawable.bg_black, R.drawable.bg_black,
                        R.drawable.bg_front, R.drawable.bg_front,
                });
    }


    public void setViewSize(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;

      
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f,
                0f, 1.0f, 0.0f);

      
        float ratio = (float) width / (float) height;
        MatrixHelper.perspectiveM(mProjectionMatrix, 45, ratio, 1f, 300f);
    }

    /**
     * 绘制天空盒
     * @param customGestureDetector
     */
    public void drawSkyBox(CustomGestureDetector customGestureDetector) {

        GLES20.glUseProgram(mProgramHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, mSkyboxTexture);
        calculateMatrix(customGestureDetector);
        GLES20.glUniformMatrix4fv(muMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1i(muTextureUnitHandle, 0);
//
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);
        GLES20.glUseProgram(0);
    }

    /**
     * 计算总变换
     * @param customGestureDetector
     */
    private void calculateMatrix(CustomGestureDetector customGestureDetector) {
      
        Matrix.setIdentityM(mViewMatrix, 0);




        Matrix.multiplyMM(mViewMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0);
        Matrix.rotateM(mViewMatrix, 0, customGestureDetector.getAmountX(), 0, 1f, 0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }
}
