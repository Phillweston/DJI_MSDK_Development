package com.flycloud.autofly.ux.widget.photopick;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import com.flycloud.autofly.base.util.ToastUtil;

import java.io.File;


/**
 * 拍照或相册选择图片功能封装
 * 使用ActivityResult方法，执行adapter.onActivityResult(requestCode, resultCode, data)监听结果
 */
public class PhotoPickHelper {

    private static final String ROOT_NAME = "UPLOAD_CACHE";
    private final String TAG = "PhotoPickHelper";

    private Activity mActivity;
    private File mTempCameraFile;
    private File mTempCropFile;
    public static final int MODE_HEAD = 0;
    public static final int MODE_IMG = 1;


    public static final int ALBUM_REQUEST_CODE = 1;
    public static final int CAMERA_REQUEST_CODE = 2;
    public static final int CROP_REQUEST_CODE = 4;
    private int mMode = -1;

    public PhotoPickHelper(Activity activity) {
        mActivity = activity;
    }


    public void start(int mode, int request_code) {
        mMode = mode;
        startOption(request_code);

    }

    private void startOption(int request_code) {
        switch (request_code) {
            case CAMERA_REQUEST_CODE:

                try {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getTempCameraFile();
                    if (file == null) {
                        ToastUtil.show(mActivity, "外部储存不可用或外部存储空间不足");
                        return;
                    }
                    Uri photoUri = getUri(file);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                    mActivity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ALBUM_REQUEST_CODE:


                Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                mActivity.startActivityForResult(albumIntent, ALBUM_REQUEST_CODE);
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data, OnPhotoPickedListener listener) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    if (mMode == MODE_IMG) {
                        File file = composBitmap(mTempCameraFile);
                        sendImage(file, listener);
                    } else if (mMode == MODE_HEAD) {

                        Uri photoUri = getUri(mTempCameraFile);

                        startCrop(photoUri, 225, 225);
                    }
                    break;
                case ALBUM_REQUEST_CODE:
                    Uri uri = data.getData();
                    if (mMode == MODE_IMG) {
                        File file = BitmapHelper.decodeUriAsFile(mActivity, uri);
                        file = composBitmap(file);
                        sendImage(file, listener);
                    } else if (mMode == MODE_HEAD) {
                        startCrop(uri, 225, 225);
                    }
                    break;
                case CROP_REQUEST_CODE:
                    sendImage(getTempCropFile(), listener);
                    break;
            }
        }
    }

    /**
     * 开始裁剪
     * 附加选项	   数据类型	    描述
     * crop	        String	    发送裁剪信号
     * aspectX	    int	        X方向上的比例
     * aspectY	    int	        Y方向上的比例
     * outputX	    int	        裁剪区的宽
     * outputY	    int	        裁剪区的高
     * scale	    boolean	    是否保留比例
     * return-data	boolean	    是否将数据保留在Bitmap中返回
     * data	        Parcelable	相应的Bitmap数据
     * circleCrop	String	    圆形裁剪区域？
     * MediaStore.EXTRA_OUTPUT ("output")	URI	将URI指向相应的file:///...
     *
     * @param uri uri
     */
    private void startCrop(Uri uri, int outputX, int outputY) {


//


















        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        if (outputX == outputY) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        } else {
            intent.putExtra("scale", true);
        }

        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(getTempCropFile()));
        mActivity.startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private ContentResolver getContentResolver() {
        return mActivity.getContentResolver();
    }

    private File getTempCropFile() {
        if (mTempCropFile == null) {
            mTempCropFile = getTempMediaFile();
        }
        return mTempCropFile;
    }

    private File getTempCameraFile() {
        if (mTempCameraFile == null)
            mTempCameraFile = getTempMediaFile();
        return mTempCameraFile;
    }

    private File composBitmap(File file) {
        Bitmap bitmap = BitmapHelper.revisionImageSize(file);
        return BitmapHelper.saveBitmap2file(mActivity, bitmap);
    }

    private void sendImage(final File file, OnPhotoPickedListener listener) {
        if (file == null) {
            Toast.makeText(mActivity, "找不到此图片", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listener != null) {
            listener.update(file, mMode);
        }
    }

    /**
     *
     */
    public interface OnPhotoPickedListener {
        void update(File file, int mode);
    }

    
    public File getTempMediaFile() {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String fileName = getTempMediaFileName();
            file = new File(fileName);
        }
        return file;
    }

    public String getTempMediaFileName() {
        return getParentPath() + "image" + System.currentTimeMillis() + ".jpg";
    }

    private String getParentPath() {
        String parent = Environment.getExternalStorageDirectory()
                + File.separator + ROOT_NAME + File.separator;
        mkdirs(parent);
        return parent;
    }

    public boolean mkdirs(String path) {
        File file = new File(path);
        return !file.exists() && file.mkdirs();
    }

    private Uri getUri(File file) {
        return FileProvider.getUriForFile(
                mActivity,
                mActivity.getPackageName() + ".fileProvider",
                file);
    }
}
