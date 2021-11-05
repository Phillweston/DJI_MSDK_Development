package com.ew.autofly.widgets.dji;

import android.content.Context;
import android.util.AttributeSet;

import com.ew.autofly.R;

import dji.ux.widget.controls.CameraCaptureWidget;
import dji.ux.widget.controls.CameraControlsWidget;
import dji.ux.widget.controls.PictureVideoSwitch;


public class DjiCameraControlsWidget extends CameraControlsWidget {

    private PictureVideoSwitch pictureVideoSwitch;
    private CameraCaptureWidget cameraCaptureWidget;

    public DjiCameraControlsWidget(Context context) {
        this(context, null);
    }

    public DjiCameraControlsWidget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DjiCameraControlsWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);

        this.pictureVideoSwitch = (PictureVideoSwitch) this.findViewById(R.id.widget_camera_capture_switch);
        this.cameraCaptureWidget = (CameraCaptureWidget) this.findViewById(R.id.widget_camera_capture);
    }

    public void enablePictureVideoSwitch(boolean enable) {
        this.pictureVideoSwitch.setVisibility(enable ? VISIBLE : INVISIBLE);
    }

    public void enableCameraCaptureWidget(boolean enable) {
        this.cameraCaptureWidget.setVisibility(enable ? VISIBLE : INVISIBLE);
    }
}
