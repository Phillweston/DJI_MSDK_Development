package com.ew.autofly.widgets.CloudPointView2.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.esri.core.geometry.Point;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.widgets.CloudPointView2.util.CoordinateConversion;
import com.ew.autofly.widgets.CloudPointView2.util.CustomGestureDetector;
import com.ew.autofly.widgets.CloudPointView2.util.v3.Obj2OpenJL;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.OpenGLModelData;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.RawOpenGLModel;
import com.ew.autofly.widgets.CloudPointView2.widget.CloudPointView2;
import com.ew.autofly.xflyer.utils.ArcgisPointUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class AircraftFilter {
	private final float scale = CloudPointView2.SCALING;
	private FloatBuffer mVertexBuffer;
	private int mProgram;
	private int mPositionHandle;
	private int muMVPMatrixHandle;
	private int muMMatrixHandle;
	private int muLightLocationHandle;
	private int mTextureCoordHandle;
	private int textureId;
	private int muTextureHandle;

	private Context mContext;
	private FloatBuffer mTexureBuffer;
	private FloatBuffer mNormalBuffer;
	private int mNormalHandle;

	private int mVertexCount;
	private float vertices[];
	private float texures[];
	private float normals[];

	private int mObjFileName;
	private int mTexrureRes;

	public final float[] mProjectionMatrix = new float[16];
	public final float[] mViewMatrix = new float[16];
	public final float[] mModuleMatrix = new float[16];
	public final float[] mViewProjectionMatrix = new float[16];
	public final float[] mMVPMatrix = new float[16];

	private OpenGLModelData openGLModelData;

	public AircraftFilter(Context context, int objFileName, int texture) {
		this.mContext = context;
		this.mObjFileName = objFileName;
		this.mTexrureRes = texture;

		initVetexData();
	}

	public void initVetexData() {
		InputStream is = mContext.getResources().openRawResource(mObjFileName);

		RawOpenGLModel openGLModel = new Obj2OpenJL().convert(is);
		openGLModelData = openGLModel.normalize().center().getDataForGLDrawArrays();

		float[] vertices = openGLModelData.getVertices();

		mVertexCount = vertices.length / 3;
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		
		texures = openGLModelData.getTextureCoordinates();
		ByteBuffer vbb2 = ByteBuffer.allocateDirect(texures.length * 4);
		vbb2.order(ByteOrder.nativeOrder());
		mTexureBuffer = vbb2.asFloatBuffer();
		mTexureBuffer.put(texures);
		mTexureBuffer.position(0);
		
		normals = openGLModelData.getNormals();
		ByteBuffer vbb3 = ByteBuffer.allocateDirect(normals.length * 4);
		vbb3.order(ByteOrder.nativeOrder());
		mNormalBuffer = vbb3.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);

		int vertexShader = loaderShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loaderShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(mProgram, vertexShader);
		GLES20.glAttachShader(mProgram, fragmentShader);
		GLES20.glLinkProgram(mProgram);

		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
		mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");

		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
		muLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
		muTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
		initTexture(mTexrureRes);
	}

	private float[] initRealPointInfo(double x, double y, double z, double yaw) {

		float[] vertices = openGLModelData.getVertices().clone();

		for (int i = 0; i < vertices.length / 3; i++) {
			Point rotatePoint = ArcgisPointUtils.GetBPoint(new Point(vertices[i * 3 + 2], vertices[i * 3]),
					new Point(y * scale, x * scale), -yaw);

			vertices[i * 3 + 2] = (float) rotatePoint.getX();
			vertices[i * 3] = (float) -rotatePoint.getY();
		}

		for (int i = 1; i < vertices.length + 1; i++) {
			if (i % 3 == 1) {
				vertices[i - 1] += x;
				vertices[i - 1] = vertices[i - 1] * scale;
			} else if (i % 3 == 2) {
				vertices[i - 1] += z;
				vertices[i - 1] = vertices[i - 1] * scale;
			} else if (i % 3 == 0) {
				vertices[i - 1] -= y;
				vertices[i - 1] = vertices[i - 1] * scale;
			}
		}

		return vertices;
	}

	public void initTexture(int res) {
		int [] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		textureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_MIRRORED_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_MIRRORED_REPEAT);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
	
	public void draw(CustomGestureDetector customGestureDetector, double x, double y, double z, double yaw) {

		float[] vertices = initRealPointInfo(x, y, z, yaw);

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		Matrix.setIdentityM(mModuleMatrix, 0);
		Matrix.translateM(mModuleMatrix, 0, 0,  customGestureDetector.getAmountY(), 0);
		Matrix.scaleM(mModuleMatrix, 0, customGestureDetector.getAmountScale(), customGestureDetector.getAmountScale(), customGestureDetector.getAmountScale());
		Matrix.rotateM(mModuleMatrix, 0, customGestureDetector.getAmountX(), 0, 1, 0);
		Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mViewProjectionMatrix, 0, mModuleMatrix, 0);
		setValue(mMVPMatrix, mModuleMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
	}

	public void setValue(float[] mvpMatrix, float[] mMatrix) {
		GLES20.glUseProgram(mProgram);
		mVertexBuffer.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
		mTexureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mTexureBuffer);
        mTexureBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mNormalBuffer);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		
		GLES20.glUniform3f(muLightLocationHandle, 0, 0, 10);
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, mMatrix, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glUniform1i(muTextureHandle, 0);
	}

	private int loaderShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}
	
	private String vertexShaderCode = "uniform mat4 uMVPMatrix;"
			+ "attribute vec2 aTextureCoord;"
			+ "varying vec2 vTextureCoord;"
			+ "uniform mat4 uMMatrix;"
			+ "uniform vec3 uLightLocation;"
			+ "varying vec4 vDiffuse;"
			+ "attribute vec3 aPosition;"
			+ "attribute vec3 aNormal;"
			+ "void main(){"  
			+ "vec3 normalVectorOrigin = aNormal;"
			+ "vec3 normalVector = normalize((uMMatrix*vec4(normalVectorOrigin,1)).xyz);"
			+ "vec3 vectorLight = normalize(uLightLocation - (uMMatrix * vec4(aPosition,1)).xyz);"
			+ "float factor = max(0.0, dot(normalVector, vectorLight));"
			+ "vDiffuse = factor*vec4(1,1,1,1.0);"
			+ "gl_Position = uMVPMatrix * vec4(aPosition,1);"
			+ "vTextureCoord = aTextureCoord;"
			+ "}";

	private String fragmentShaderCode = "precision mediump float;"
			+ "uniform sampler2D uTexture;"
			+ "varying vec2 vTextureCoord;"
			+ "varying  vec4 vColor;"
			+ "varying vec4 vDiffuse;"
			+ "void main(){"
			+ "gl_FragColor = (vDiffuse + vec4(0.6,0.6,0.6,1))*texture2D(uTexture, vec2(vTextureCoord.s,vTextureCoord.t));"
			+ "}";
	
}
