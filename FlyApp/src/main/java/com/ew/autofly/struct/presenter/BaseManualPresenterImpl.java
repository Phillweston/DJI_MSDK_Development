package com.ew.autofly.struct.presenter;

import androidx.annotation.NonNull;

import com.ew.autofly.model.VideoRecordManager;
import com.ew.autofly.struct.view.IBaseFlightView;

import dji.common.camera.SystemState;
import dji.sdk.media.MediaFile;


public class BaseManualPresenterImpl<V extends IBaseFlightView> extends BaseFlightPresenterImpl<V> {

    private VideoRecordManager mVideoRecordManager;

    @Override
    public void attachView(V view) {
        super.attachView(view);
        mVideoRecordManager = new VideoRecordManager();
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    @Override
    public void onCameraSystemStateCallback(@NonNull SystemState systemState) {

        if (systemState.isRecording()) {
            mVideoRecordManager.startRecordVideoPoint();
        } else if (!systemState.isRecording()) {
            mVideoRecordManager.finishRecordVideoPoint();
        }
    }

    @Override
    public void onCameraMediaFileCallback(@NonNull MediaFile mediaFile) {

        if (mediaFile.getMediaType() == MediaFile.MediaType.MP4
                || mediaFile.getMediaType() == MediaFile.MediaType.MOV) {

            String name = mediaFile.getFileName();
            mVideoRecordManager.writeVideoDataIntoFile(name.substring(0, name.indexOf(".")));
        }
    }

    @Override
    public void onDestroy() {
        mVideoRecordManager.destroy();
    }
}
